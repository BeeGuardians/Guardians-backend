package com.guardians.domain.wargame.service;

import org.springframework.stereotype.Component;

@Component
public class WargameDomainService {

    public boolean isCorrectFlag(String storedFlag, String submittedFlag) {
        return storedFlag.equals(submittedFlag);
    }
}
