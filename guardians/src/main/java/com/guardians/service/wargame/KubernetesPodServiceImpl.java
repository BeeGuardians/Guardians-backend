package com.guardians.service.wargame;

import com.guardians.domain.wargame.entity.Wargame;
import com.guardians.domain.wargame.repository.WargameRepository;
import com.guardians.dto.wargame.res.PodStatusDto;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.api.model.networking.v1.IngressBackendBuilder;
import io.fabric8.kubernetes.api.model.networking.v1.IngressBuilder;
import io.fabric8.kubernetes.client.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KubernetesPodServiceImpl implements KubernetesPodService {

    private final WargameRepository wargameRepository;

    private KubernetesClient getK8sClient() {
        if ("true".equalsIgnoreCase(System.getenv("IN_CLUSTER"))) {
            Config config = Config.autoConfigure(null);
            config.setNamespace("ns-wargame");
            return new DefaultKubernetesClient(config);
        } else {
            Config config = new ConfigBuilder()
                    .withMasterUrl(System.getenv("K8S_SERVER"))
                    .withClientCertData(System.getenv("K8S_CLIENT_CERT"))
                    .withClientKeyData(System.getenv("K8S_CLIENT_KEY"))
                    .withNamespace(System.getenv("K8S_NAMESPACE"))
                    .withTrustCerts(true)
                    .build();
            return new DefaultKubernetesClient(config);
        }
    }

    @Override
    public void createWargamePod(String podName, Long wargameId, Long userId, String namespace) {
        try (KubernetesClient client = getK8sClient()) {
            Wargame wargame = wargameRepository.findById(wargameId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 워게임이 존재하지 않습니다."));
            String imageName = wargame.getDockerImageUrl();

            if (client.pods().inNamespace(namespace).withName(podName).get() != null) {
                deleteWargamePod(podName, namespace);

                int retry = 0;
                while (retry < 10) {
                    boolean podDeleted = client.pods().inNamespace(namespace).withName(podName).get() == null;
                    boolean svcDeleted = client.services().inNamespace(namespace).withName("svc-" + userId + "-" + wargameId).get() == null;
                    boolean ingDeleted = client.network().v1().ingresses().inNamespace(namespace).withName("ing-" + userId + "-" + wargameId).get() == null;

                    if (podDeleted && svcDeleted && ingDeleted) break;

                    Thread.sleep(1000);
                    retry++;
                }

                if (retry == 10) throw new RuntimeException("기존 인스턴스 삭제 지연");
            }

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

            client.services().inNamespace(namespace).resource(
                    new io.fabric8.kubernetes.api.model.ServiceBuilder()
                            .withNewMetadata().withName("svc-" + userId + "-" + wargameId).endMetadata()
                            .withNewSpec()
                            .addNewPort()
                            .withPort(80)
                            .withTargetPort(new IntOrString(8000))
                            .endPort()
                            .withSelector(Map.of("app", podName))
                            .endSpec()
                            .build()
            ).create();

            Ingress ingress = new IngressBuilder()
                    .withNewMetadata()
                    .withName("ing-" + userId + "-" + wargameId)
                    .withNamespace(namespace)
                    .endMetadata()
                    .withNewSpec()
                    .withIngressClassName("nginx")
                    .addNewRule()
                    .withHost(wargameId + "-" + userId + ".wargames.bee-guardians.com")
                    .withNewHttp()
                    .addNewPath()
                    .withPath("/")
                    .withPathType("Prefix")
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

            Thread.sleep(3000);
        } catch (Exception e) {
            throw new RuntimeException("워게임 인스턴스 생성 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteWargamePod(String podName, String namespace) {
        try (KubernetesClient client = getK8sClient()) {
            String[] parts = podName.split("-");
            String userId = parts[1];
            String wargameId = parts[2];

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
    public List<Pod> getRunningPodsByWargameId(Long wargameId, String namespace) {
        try (KubernetesClient client = getK8sClient()) {
            return client.pods()
                    .inNamespace(namespace)
                    .list()
                    .getItems()
                    .stream()
                    .filter(pod -> {
                        String name = pod.getMetadata().getName();
                        String phase = pod.getStatus().getPhase();
                        return name.contains("-" + wargameId) && "Running".equals(phase);
                    })
                    .collect(Collectors.toList());
        }
    }

    @Override
    public PodStatusDto getPodStatus(String podName, String namespace) {
        try (KubernetesClient client = getK8sClient()) {
            Pod pod = client.pods().inNamespace(namespace).withName(podName).get();

            if (pod == null) {
                return new PodStatusDto("Not Found", null);
            }

            if (pod.getMetadata().getDeletionTimestamp() != null) {
                return new PodStatusDto("Terminating", generateIngressUrl(podName));
            }

            String phase = pod.getStatus().getPhase();
            if ("Succeeded".equals(phase) || "Failed".equals(phase)) {
                return new PodStatusDto("Not Found", null);
            }

            return new PodStatusDto(phase, generateIngressUrl(podName));
        }
    }

    public String generateIngressUrl(String podName) {
        try {
            String[] parts = podName.split("-");
            Long userId = Long.parseLong(parts[1]);
            Long wargameId = Long.parseLong(parts[2]);
            return String.format("https://%d-%d.wargames.bee-guardians.com", wargameId, userId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid pod name format: " + podName);
        }
    }
}
