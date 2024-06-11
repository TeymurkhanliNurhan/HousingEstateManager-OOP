package GUI_P1;

import java.util.Calendar;
import java.util.Date;

public class TenantLetter {
    private String content;
    private int ID;
    private static int NextID=1;
    private boolean active;
    private String TenantId;
    private int ApartmentId;
    private Date expiryDate;

    public TenantLetter(String TenantID, int ApartmentId) {
        this.ID=generateNextID();
        this.TenantId=TenantID;
        this.ApartmentId=ApartmentId;
        this.active=false;
        this.content = getContent();

        // expiryDate'i gönderim tarihinden 30 gün sonrası olarak ayarla
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        this.expiryDate = calendar.getTime();
    }

    private synchronized static int generateNextID() {
        return NextID++;
    }


    public String getContent() {
        return "The tenant letter with the ID "+getID()+" is about apartment "+getApartmentId();
    }

    public void setContent(String content) {
        this.content = "The tenant letter with the ID "+getID()+" is about apartment "+getApartmentId();
    }

    // ID
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    // NextID
    public int getNextID() {
        return NextID;
    }

    public void setNextID(int nextID) {
        NextID = nextID;
    }

    // Active


    public void setActive(boolean active) {
        this.active = active;
    }

    // TenantId
    public String getTenantId() {
        return TenantId;
    }

    public void setTenantId(String tenantId) {
        this.TenantId = tenantId;
    }

    // ApartmentId
    public int getApartmentId() {
        return ApartmentId;
    }

    public void setApartmentId(int apartmentId) {
        this.ApartmentId = apartmentId;
    }

    public Date getExpiryDate() {return expiryDate;}

    public void setExpiryDate(Date expiryDate) {this.expiryDate = expiryDate;}

    public boolean isActive() {
        Date currentDate = new Date();
        return expiryDate.after(currentDate);
    }
}