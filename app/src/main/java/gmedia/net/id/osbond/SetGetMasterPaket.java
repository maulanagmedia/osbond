package gmedia.net.id.osbond;

public class SetGetMasterPaket {
    private String id, paket;

    public SetGetMasterPaket(String id, String paket) {
        this.id = id;
        this.paket = paket;
    }
    @Override
    public String toString() {
        return paket;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaket() {
        return paket;
    }

    public void setPaket(String paket) {
        this.paket = paket;
    }
}
