package com.guardians.domain.wargame.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolvedWargameId implements Serializable {

    private Long user;
    private Long wargame;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SolvedWargameId)) return false;
        SolvedWargameId that = (SolvedWargameId) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(wargame, that.wargame);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, wargame);
    }
}
