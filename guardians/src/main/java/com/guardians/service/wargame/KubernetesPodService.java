package com.guardians.service.wargame;

import com.guardians.dto.wargame.res.PodStatusDto;
import io.fabric8.kubernetes.api.model.Pod;

import java.util.List;

public interface KubernetesPodService {
    void createWargamePod(String podName, Long wargameId, Long userId, String namespace);
    boolean deleteWargamePod(String podName, String namespace);
    PodStatusDto getPodStatus(String podName, String namespace); // ✅ 인터페이스도 DTO로 변경
    List<Pod> getRunningPodsByWargameId(Long wargameId, String namespace);

}

