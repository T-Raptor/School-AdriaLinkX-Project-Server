package be.howest.ti.adria.logic.domain;

import java.util.Objects;

public abstract class PureObservable implements Observable {
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        PureObservable that = (PureObservable) o;
        return getId() == that.getId();
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }
}
