package com.guardians.service.wargame;

public interface KubernetesPodService {
    void createWargamePod(String podName, Long wargameId, Long userId, String namespace);
    boolean deleteWargamePod(String podName, String namespace);
    String getPodStatus(String podName, String namespace);
}

