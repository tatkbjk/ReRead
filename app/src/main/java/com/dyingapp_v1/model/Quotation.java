package com.dyingapp_v1.model;

public class Quotation {
    private String _id, title, author, publisher, condition, note, status, price, imgBook;

    public Quotation(String _id, String title, String author, String publisher, String condition, String note, String status, String price, String imgBook) {
        this._id = _id;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.condition = condition;
        this.note = note;
        this.status = status;
        this.price = price;
        this.imgBook = imgBook;
    }

    public String getId() { return _id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public String getCondition() { return condition; }
    public String getNote() { return note; }
    public String getStatus() { return status; }
    public String getPrice() { return price; }
    public String getImgBook() {
        return imgBook;
    }

    public void setImgBook(String imgBook) {
        this.imgBook = imgBook;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}