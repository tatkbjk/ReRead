package com.dyingapp_v1.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Event implements Serializable{
    private String id;
    private String eventID;
    private String eventName;
    private String eventType;
    private String eventStart;
    private String eventEnd;
    private List<String> eventDesc;
    private String eventLoc;
    private int eventCpct;
    private String img;

    public Event() {}

    public Event(String id, String eventID, String eventName, String eventType, String eventStart, String eventEnd, List<String> eventDesc, String eventLoc, int eventCpct, String img) {
        this.id = id;
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventType = eventType;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.eventDesc = eventDesc;
        this.eventLoc = eventLoc;
        this.eventCpct = eventCpct;
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventStart() {
        return eventStart;
    }

    public void setEventStart(String eventStart) {
        this.eventStart = eventStart;
    }

    public String getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(String eventEnd) {
        this.eventEnd = eventEnd;
    }

    public List<String> getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(List<String> eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getEventLoc() {
        return eventLoc;
    }

    public void setEventLoc(String eventLoc) {
        this.eventLoc = eventLoc;
    }

    public int getEventCpct() {
        return eventCpct;
    }

    public void setEventCpct(int eventCpct) {
        this.eventCpct = eventCpct;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    // ✅ Định dạng trạng thái sự kiện
    public String getEventStatus() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime endDateTime = LocalDateTime.parse(eventEnd, formatter);
            LocalDateTime now = LocalDateTime.now();

            return endDateTime.isBefore(now) ? "Registration Closed" : "Open For Registration";
        } catch (Exception e) {
            return "Invalid Date Format";
        }
    }

    // ✅ Hiển thị ngày bắt đầu đẹp
    public String getFormattedEventStart() {
        return formatDate(eventStart);
    }

    // ✅ Hiển thị ngày kết thúc đẹp
    public String getFormattedEventEnd() {
        return formatDate(eventEnd);
    }

    // 📌 Format lại ISO date sang dd/MM/yyyy HH:mm
    private String formatDate(String rawDate) {
        try {
            DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime date = LocalDateTime.parse(rawDate, inputFormat);
            return outputFormat.format(date);
        } catch (Exception e) {
            return "Invalid Date";
        }
    }
}
