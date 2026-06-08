package com.guardians.domain.wargame.port;

import com.guardians.dto.wargame.res.PodStatusDto;
import io.fabric8.kubernetes.api.model.Pod;

import java.util.List;

public interface KubernetesPodPort {
    void createWargamePod(String podName, Long wargameId, Long userId, String namespace);
    boolean deleteWargamePod(String podName, String namespace);
    PodStatusDto getPodStatus(String podName, String namespace);
    List<Pod> getRunningPodsByWargameId(Long wargameId, String namespace);
    String generateIngressUrl(String podName);
    void createKaliPod(Long userId, String namespace);
    boolean deleteKaliPod(Long userId, String namespace);
    PodStatusDto getKaliPodStatus(Long userId, String namespace);
}
