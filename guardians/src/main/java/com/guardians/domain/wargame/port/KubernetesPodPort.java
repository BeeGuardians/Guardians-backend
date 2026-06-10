package com.guardians.domain.wargame.port;

import java.util.List;

public interface KubernetesPodPort {
    void createWargamePod(String podName, Long wargameId, Long userId, String namespace);
    boolean deleteWargamePod(String podName, String namespace);
    PodStatus getPodStatus(String podName, String namespace);
    List<RunningPodInfo> getRunningPodsByWargameId(Long wargameId, String namespace);
    String generateIngressUrl(String podName);
    void createKaliPod(Long userId, String namespace);
    boolean deleteKaliPod(Long userId, String namespace);
    PodStatus getKaliPodStatus(Long userId, String namespace);
}
