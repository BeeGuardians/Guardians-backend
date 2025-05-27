package com.guardians.service.wargame;

import com.guardians.dto.wargame.res.PodStatusDto;

public interface KubernetesKaliPodService {
    void createKaliPod(Long userId, String namespace);
    boolean deleteKaliPod(Long userId, String namespace);
    PodStatusDto getKaliPodStatus(Long userId, String namespace);
}
