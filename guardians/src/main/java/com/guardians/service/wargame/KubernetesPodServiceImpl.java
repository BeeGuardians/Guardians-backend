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
            String imageName = wargame.getDockerImageUrl(); // 예: ghcr.io/yourorg/wargame:latest

            // 2. Pod 생성
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

            // 3. Service 생성 (⚠ org.springframework.stereotype.Service와 이름 충돌 피하기 위해 풀네임 사용)
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

            // 4. Ingress 생성
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
        }
    }

    @Override
    public boolean deleteWargamePod(String podName, String namespace) {
        try (KubernetesClient client = getK8sClient()) {
            List<StatusDetails> podResult = client.pods().inNamespace(namespace).withName(podName).delete();
            List<StatusDetails> svcResult = client.services().inNamespace(namespace).withName("svc-" + podName.split("-")[1] + "-" + podName.split("-")[2]).delete();
            List<StatusDetails> ingResult = client.network().v1().ingresses().inNamespace(namespace).withName("ing-" + podName.split("-")[1] + "-" + podName.split("-")[2]).delete();

            return (podResult != null && !podResult.isEmpty())
                    || (svcResult != null && !svcResult.isEmpty())
                    || (ingResult != null && !ingResult.isEmpty());
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
