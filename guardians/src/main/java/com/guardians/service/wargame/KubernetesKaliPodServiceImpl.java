package com.guardians.service.wargame;

import com.guardians.dto.wargame.res.PodStatusDto;
import com.guardians.service.wargame.KubernetesKaliPodService;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.api.model.networking.v1.IngressBackendBuilder;
import io.fabric8.kubernetes.api.model.networking.v1.IngressBuilder;
import io.fabric8.kubernetes.client.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KubernetesKaliPodServiceImpl implements KubernetesKaliPodService {

    private final String IMAGE_NAME = "public.ecr.aws/i7t0x0a1/gaurdians/wargames:kali";

    private KubernetesClient getK8sClient() {
        if ("true".equalsIgnoreCase(System.getenv("IN_CLUSTER"))) {
            return new DefaultKubernetesClient(Config.autoConfigure(null));
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
    public void createKaliPod(Long userId, String namespace) {
        try (KubernetesClient client = getK8sClient()) {
            String podName = "kali-" + userId;

            if (client.pods().inNamespace(namespace).withName(podName).get() != null) {
                deleteKaliPod(userId, namespace);
            }

            Pod pod = new PodBuilder()
                    .withNewMetadata().withName(podName).addToLabels("app", podName).endMetadata()
                    .withNewSpec()
                    .addNewContainer()
                    .withName("main")
                    .withImage(IMAGE_NAME)
                    .addNewPort().withContainerPort(7681).endPort()
                    .withNewSecurityContext()
                            .withNewCapabilities()
                                    .addToAdd("NET_ADMIN")
                                    .addToAdd("NET_RAW")
                            .endCapabilities()
                    .endSecurityContext()
                    .withNewResources()
                            .addToLimits("memory", new Quantity("512Mi"))
                            .addToLimits("cpu", new Quantity("500m"))
                            .addToRequests("memory", new Quantity("256Mi"))
                            .addToRequests("cpu", new Quantity("250m"))
                    .endResources()
                    .endContainer()
                    .endSpec()
                    .build();
            client.pods().inNamespace(namespace).resource(pod).create();

            client.services().inNamespace(namespace).resource(
                    new ServiceBuilder()
                            .withNewMetadata().withName("svc-kali-" + userId).endMetadata()
                            .withNewSpec()
                            .addNewPort().withPort(80).withTargetPort(new IntOrString(7681)).endPort()
                            .withSelector(Map.of("app", podName))
                            .endSpec()
                            .build()
            ).create();

            client.network().v1().ingresses().inNamespace(namespace).resource(
                    new IngressBuilder()
                            .withNewMetadata().withName("ing-kali-" + userId).endMetadata()
                            .withNewSpec()
                            .withIngressClassName("nginx")
                            .addNewRule()
                            .withHost("kali-" + userId + ".wargames.bee-guardians.com")
                            .withNewHttp()
                            .addNewPath()
                            .withPath("/")
                            .withPathType("Prefix")
                            .withBackend(new IngressBackendBuilder()
                                    .withNewService()
                                    .withName("svc-kali-" + userId)
                                    .withNewPort().withNumber(80).endPort()
                                    .endService()
                                    .build())
                            .endPath()
                            .endHttp()
                            .endRule()
                            .endSpec()
                            .build()
            ).create();

            Thread.sleep(3000);
        } catch (Exception e) {
            throw new RuntimeException("칼리 리눅스 인스턴스 생성 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteKaliPod(Long userId, String namespace) {
        try (KubernetesClient client = getK8sClient()) {
            String podName = "kali-" + userId;
            client.pods().inNamespace(namespace).withName(podName).delete();
            client.services().inNamespace(namespace).withName("svc-kali-" + userId).delete();
            client.network().v1().ingresses().inNamespace(namespace).withName("ing-kali-" + userId).delete();

            int retry = 0;
            while (retry < 10) {
                boolean podDeleted = client.pods().inNamespace(namespace).withName(podName).get() == null;
                boolean svcDeleted = client.services().inNamespace(namespace).withName("svc-kali-" + userId).get() == null;
                boolean ingDeleted = client.network().v1().ingresses().inNamespace(namespace).withName("ing-kali-" + userId).get() == null;

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
    public PodStatusDto getKaliPodStatus(Long userId, String namespace) {
        try (KubernetesClient client = getK8sClient()) {
            String podName = "kali-" + userId;
            Pod pod = client.pods().inNamespace(namespace).withName(podName).get();

            if (pod == null) {
                return new PodStatusDto("Not Found", null);
            }

            if (pod.getMetadata().getDeletionTimestamp() != null) {
                return new PodStatusDto("Terminating", getKaliIngressUrl(userId));
            }

            String phase = pod.getStatus().getPhase();
            if ("Succeeded".equals(phase) || "Failed".equals(phase)) {
                return new PodStatusDto("Not Found", null);
            }

            return new PodStatusDto(phase, getKaliIngressUrl(userId));
        }
    }

    private String getKaliIngressUrl(Long userId) {
        return String.format("https://kali-%d.wargames.bee-guardians.com", userId);
    }
}
