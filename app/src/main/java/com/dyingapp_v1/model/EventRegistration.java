package com.dyingapp_v1.model;

public class EventRegistration {
    private String _id;
    private String RegistrationID;
    private String EventID;
    private String UserID;
    private String RegDate;
    private String Note;
    private int Joined;

    public EventRegistration() {
    }

    public EventRegistration(String _id, String registrationID, String eventID, String userID, String regDate, String note, int joined) {
        this._id = _id;
        this.RegistrationID = registrationID;
        this.EventID = eventID;
        this.UserID = userID;
        this.RegDate = regDate;
        this.Note = note;
        this.Joined = joined;
    }


    public String getRegistrationID() {
        return RegistrationID;
    }

    public void setRegistrationID(String registrationID) {
        RegistrationID = registrationID;
    }

    public String getRegDate() {
        return RegDate;
    }

    public void setRegDate(String regDate) {
        RegDate = regDate;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEventID() {
        return EventID;
    }

    public void setEventID(String eventID) {
        EventID = eventID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public int getJoined() {
        return Joined;
    }

    public void setJoined(int joined) {
        Joined = joined;
    }
}
