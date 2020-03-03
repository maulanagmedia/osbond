package gmedia.net.id.osbond.EKupon;

public class CustomListAdapterEKupon {
    //    private int background;
    private String id, idCabang, kupon, cabang, jenis, jumlah_scan, due_date, event;

    public CustomListAdapterEKupon(String id, String idCabang, String kupon, String cabang, String jenis, String jumlah_scan, String due_date) {
        this.id = id;
        this.idCabang = idCabang;
        this.kupon = kupon;
        this.cabang = cabang;
        this.jenis = jenis;
        this.jumlah_scan = jumlah_scan;
        this.due_date = due_date;
    }

    public CustomListAdapterEKupon(String id, String idCabang, String kupon, String cabang, String jenis, String jumlah_scan, String due_date, String event) {
        this.id = id;
        this.idCabang = idCabang;
        this.kupon = kupon;
        this.cabang = cabang;
        this.jenis = jenis;
        this.jumlah_scan = jumlah_scan;
        this.due_date = due_date;
        this.event = event;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdCabang() {
        return idCabang;
    }

    public void setIdCabang(String idCabang) {
        this.idCabang = idCabang;
    }

    public String getKupon() {
        return kupon;
    }

    public void setKupon(String kupon) {
        this.kupon = kupon;
    }


    public String getCabang() {
        return cabang;
    }

    public void setCabang(String cabang) {
        this.cabang = cabang;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getJumlah_scan() {
        return jumlah_scan;
    }

    public void setJumlah_scan(String jumlah_scan) {
        this.jumlah_scan = jumlah_scan;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
