package com.dyingapp_v1.model;

import java.util.Date;

public class Voucher {
    private String id;              // MongoDB _id (ObjectId dạng String)
    private String discountID;      // DiscountID: "92u8fwm"
    private int discRate;           // DiscRate: phần trăm giảm giá, ví dụ 10%
    private int discReq;            // DiscReq: điều kiện áp dụng (đơn hàng tối thiểu, ví dụ 150000)
    private int discLeft;           // DiscLeft: số lượng còn lại
    private int discUsed;           // DiscUsed: số lần đã dùng
    private Date discValidFrom;     // Thời gian bắt đầu hiệu lực
    private Date discValidTo;       // Thời gian hết hiệu lực

    public Voucher() {}

    // Getter & Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDiscountID() {
        return discountID;
    }

    public void setDiscountID(String discountID) {
        this.discountID = discountID;
    }

    public int getDiscRate() {
        return discRate;
    }

    public void setDiscRate(int discRate) {
        this.discRate = discRate;
    }

    public int getDiscReq() {
        return discReq;
    }

    public void setDiscReq(int discReq) {
        this.discReq = discReq;
    }

    public int getDiscLeft() {
        return discLeft;
    }

    public void setDiscLeft(int discLeft) {
        this.discLeft = discLeft;
    }

    public int getDiscUsed() {
        return discUsed;
    }

    public void setDiscUsed(int discUsed) {
        this.discUsed = discUsed;
    }

    public Date getDiscValidFrom() {
        return discValidFrom;
    }

    public void setDiscValidFrom(Date discValidFrom) {
        this.discValidFrom = discValidFrom;
    }

    public Date getDiscValidTo() {
        return discValidTo;
    }

    public void setDiscValidTo(Date discValidTo) {
        this.discValidTo = discValidTo;
    }

    // Convenience methods
    public boolean isStillValid() {
        Date now = new Date();
        return now.after(discValidFrom) && now.before(discValidTo) && discLeft > 0;
    }

    public double getDiscountRateAsDecimal() {
        return discRate / 100.0;
    }

    public String getFormattedDiscount() {
        return "-" + discRate + "%";
    }
}
