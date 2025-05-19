package com.guardians.service.wargame;

import com.guardians.domain.wargame.entity.Wargame;
import com.guardians.domain.wargame.repository.WargameRepository;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.api.model.networking.v1.IngressBackendBuilder;
import io.fabric8.kubernetes.api.model.networking.v1.IngressBuilder;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KubernetesPodServiceImpl implements KubernetesPodService {

    private final WargameRepository wargameRepository;

    private KubernetesClient getK8sClient() {
        Config config = new ConfigBuilder()
                .withMasterUrl(System.getenv("K8S_SERVER"))
                .withClientCertData(System.getenv("K8S_CLIENT_CERT"))
                .withClientKeyData(System.getenv("K8S_CLIENT_KEY"))
                .withNamespace(System.getenv("K8S_NAMESPACE"))
                .withTrustCerts(true)
                .build();

        return new DefaultKubernetesClient(config);
    }

    @Override
    public void createWargamePod(String podName, Long wargameId, Long userId, String namespace) {
        try (KubernetesClient client = getK8sClient()) {
            // 1. 워게임 정보 가져오기
            Wargame wargame = wargameRepository.findById(wargameId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 워게임이 존재하지 않습니다."));
            String imageName = wargame.getDockerImageUrl();

            // 2. 이미 Pod가 존재하면 삭제 후 대기
            if (client.pods().inNamespace(namespace).withName(podName).get() != null) {
                deleteWargamePod(podName, namespace);

                boolean podDeleted = false, svcDeleted = false, ingDeleted = false;
                int retry = 0;

                while (retry < 10) {
                    podDeleted = client.pods().inNamespace(namespace).withName(podName).get() == null;
                    svcDeleted = client.services().inNamespace(namespace).withName("svc-" + userId + "-" + wargameId).get() == null;
                    ingDeleted = client.network().v1().ingresses().inNamespace(namespace).withName("ing-" + userId + "-" + wargameId).get() == null;

                    if (podDeleted && svcDeleted && ingDeleted) break;

                    Thread.sleep(1000);
                    retry++;
                }

                if (!(podDeleted && svcDeleted && ingDeleted)) {
                    throw new RuntimeException("기존 인스턴스 삭제가 지연되어 새 인스턴스를 생성할 수 없습니다.");
                }
            }

            // 3. Pod 생성
            Pod pod = new PodBuilder()
                    .withNewMetadata().withName(podName).addToLabels("app", podName).endMetadata()
                    .withNewSpec()
                    .addNewContainer()
                    .withName("main")
                    .withImage(imageName)
                    .addNewPort().withContainerPort(8000).endPort()
                    .endContainer()
                    .endSpec()
                    .build();
            client.pods().inNamespace(namespace).resource(pod).create();

            // 4. Service 생성
            io.fabric8.kubernetes.api.model.Service svc = new io.fabric8.kubernetes.api.model.ServiceBuilder()
                    .withNewMetadata().withName("svc-" + userId + "-" + wargameId).endMetadata()
                    .withNewSpec()
                    .addNewPort()
                    .withPort(80)
                    .withTargetPort(new IntOrString(8000))
                    .endPort()
                    .withSelector(Map.of("app", podName))
                    .endSpec()
                    .build();
            client.services().inNamespace(namespace).resource(svc).create();

            // 5. Ingress 생성
            Ingress ingress = new IngressBuilder()
                    .withNewMetadata()
                    .withName("ing-" + userId + "-" + wargameId)
                    .withNamespace(namespace)
                    .addToAnnotations("nginx.ingress.kubernetes.io/rewrite-target", "/$2")
                    .addToAnnotations("nginx.ingress.kubernetes.io/use-regex", "true")
                    .endMetadata()
                    .withNewSpec()
                    .withIngressClassName("nginx")
                    .addNewRule()
                    .withHost("wargames.bee-guardians.com")
                    .withNewHttp()
                    .addNewPath()
                    .withPath("/wargame/" + wargameId + "/" + userId + "(/|$)(.*)")
                    .withPathType("ImplementationSpecific")
                    .withBackend(new IngressBackendBuilder()
                            .withNewService()
                            .withName("svc-" + userId + "-" + wargameId)
                            .withNewPort().withNumber(80).endPort()
                            .endService()
                            .build())
                    .endPath()
                    .endHttp()
                    .endRule()
                    .endSpec()
                    .build();
            client.network().v1().ingresses().inNamespace(namespace).resource(ingress).create();

            // 6. 인그레스 등록 후 대기 (URL 접속 가능성 고려)
            Thread.sleep(3000);
        } catch (Exception e) {
            throw new RuntimeException("워게임 인스턴스 생성 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteWargamePod(String podName, String namespace) {
        try (KubernetesClient client = getK8sClient()) {
            String userId = podName.split("-")[1];
            String wargameId = podName.split("-")[2];

            client.pods().inNamespace(namespace).withName(podName).delete();
            client.services().inNamespace(namespace).withName("svc-" + userId + "-" + wargameId).delete();
            client.network().v1().ingresses().inNamespace(namespace).withName("ing-" + userId + "-" + wargameId).delete();

            int retry = 0;
            while (retry < 10) {
                boolean podDeleted = client.pods().inNamespace(namespace).withName(podName).get() == null;
                boolean svcDeleted = client.services().inNamespace(namespace).withName("svc-" + userId + "-" + wargameId).get() == null;
                boolean ingDeleted = client.network().v1().ingresses().inNamespace(namespace).withName("ing-" + userId + "-" + wargameId).get() == null;

                if (podDeleted && svcDeleted && ingDeleted) return true;

                Thread.sleep(1000);
                retry++;
            }

            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public String getPodStatus(String podName, String namespace) {
        try (KubernetesClient client = getK8sClient()) {
            Pod pod = client.pods().inNamespace(namespace).withName(podName).get();
            return pod != null ? pod.getStatus().getPhase() : "Not Found";
        }
    }
}