package com.dyingapp_v1.model;

public class EventForm {

    private String registrationId;
    private String eventId;
    private String regDate;
    private String note;
    private boolean joined;

    // Bổ sung thông tin chi tiết từ bảng EventList (join tay)
    private String eventName;
    private String eventDate;
    private String eventImg;

    public EventForm(String registrationId, String eventId, String regDate, String note, boolean joined) {
        this.registrationId = registrationId;
        this.eventId = eventId;
        this.regDate = regDate;
        this.note = note;
        this.joined = joined;
    }

    // Getter & Setter
    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventImg() {
        return eventImg;
    }

    public void setEventImg(String eventImg) {
        this.eventImg = eventImg;
    }
}