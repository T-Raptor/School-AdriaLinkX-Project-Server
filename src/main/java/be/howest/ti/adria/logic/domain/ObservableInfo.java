package be.howest.ti.adria.logic.domain;

public class ObservableInfo {
    private final int id;
    private final String subtype;


    public ObservableInfo(int id, String subtype) {
        this.id = id;
        this.subtype = subtype;
    }


    public int getId() {
        return id;
    }

    public String getSubtype() {
        return subtype;
    }
}
