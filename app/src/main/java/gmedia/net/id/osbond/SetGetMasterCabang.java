package gmedia.net.id.osbond;

public class SetGetMasterCabang {
    private String id, cabang;

    public SetGetMasterCabang(String id, String cabang) {
        this.id = id;
        this.cabang = cabang;
    }
    @Override
    public String toString() {
        return cabang;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCabang() {
        return cabang;
    }

    public void setCabang(String cabang) {
        this.cabang = cabang;
    }
}
