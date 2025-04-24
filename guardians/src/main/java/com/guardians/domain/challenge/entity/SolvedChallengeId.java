package com.guardians.domain.challenge.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolvedChallengeId implements Serializable {

    private Long user;
    private Long challenge;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SolvedChallengeId)) return false;
        SolvedChallengeId that = (SolvedChallengeId) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(challenge, that.challenge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, challenge);
    }
}
