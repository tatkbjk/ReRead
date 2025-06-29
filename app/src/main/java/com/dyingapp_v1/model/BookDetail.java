package com.dyingapp_v1.model;

public class BookDetail {
    private String id;
    private String bookTitle;
    private int bookPrice;
    private int bookSales;
    private String bookImg1;
    private String bookImg2;
    private String bookCond;

    private String bookAut;
    private String bookDesc;
    private String bookGenre;
    private String bookLang;
    private String bookPub;

    // Add constructor + getters/setters...

    public BookDetail(String id, String bookTitle, int bookPrice, int bookSales, String bookImg1, String bookImg2, String bookCond, String bookAut, String bookDesc, String bookGenre, String bookLang, String bookPub) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.bookPrice = bookPrice;
        this.bookSales = bookSales;
        this.bookImg1 = bookImg1;
        this.bookImg2 = bookImg2;
        this.bookCond = bookCond;
        this.bookAut = bookAut;
        this.bookDesc = bookDesc;
        this.bookGenre = bookGenre;
        this.bookLang = bookLang;
        this.bookPub = bookPub;
    }

    public String getBookGenre() {
        return bookGenre;
    }

    public void setBookGenre(String bookGenre) {
        this.bookGenre = bookGenre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(int bookPrice) {
        this.bookPrice = bookPrice;
    }

    public int getBookSales() {
        return bookSales;
    }

    public void setBookSales(int bookSales) {
        this.bookSales = bookSales;
    }

    public String getBookImg1() {
        return bookImg1;
    }

    public void setBookImg1(String bookImg1) {
        this.bookImg1 = bookImg1;
    }

    public String getBookImg2() {
        return bookImg2;
    }

    public void setBookImg2(String bookImg2) {
        this.bookImg2 = bookImg2;
    }

    public String getBookCond() {
        return bookCond;
    }

    public void setBookCond(String bookCond) {
        this.bookCond = bookCond;
    }

    public String getBookAut() {
        return bookAut;
    }

    public void setBookAut(String bookAut) {
        this.bookAut = bookAut;
    }

    public String getBookDesc() {
        return bookDesc;
    }

    public void setBookDesc(String bookDesc) {
        this.bookDesc = bookDesc;
    }

    public String getBookLang() {
        return bookLang;
    }

    public void setBookLang(String bookLang) {
        this.bookLang = bookLang;
    }

    public String getBookPub() {
        return bookPub;
    }

    public void setBookPub(String bookPub) {
        this.bookPub = bookPub;
    }
}
