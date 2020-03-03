package gmedia.net.id.osbond.menuMidtrans;

public class SetGetMasterPaketBaru {
    private String id, icon, paket, keterangan, harga;

    public SetGetMasterPaketBaru(String id, String image, String label, String keterangan, String harga) {
        this.id = id;
        this.icon = image;
        this.paket = label;
        this.keterangan = keterangan;
        this.harga = harga;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPaket() {
        return paket;
    }

    public void setPaket(String paket) {
        this.paket = paket;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }


}
