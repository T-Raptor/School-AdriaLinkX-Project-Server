package be.howest.ti.adria.logic.domain.proposals;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ShuttleProposal {
    private final String serial;


    @JsonCreator
    public ShuttleProposal(@JsonProperty("serial") String serial) {
        this.serial = serial;
    }


    public String getSerial() {
        return serial;
    }
}
