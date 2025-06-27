package com.dyingapp_v1.model;

public class Product {
    private String id;

    private String bookISBN_n;

    private String bookTitle;
    private int bookPrice;
    private int bookSales;
    private String bookImg1;
    private String bookImg2;
    private String bookCond;
    private int bookInfoId;

    private int currentQty;
    public int getCurrentQty() { return currentQty; }
    public void setCurrentQty(int currentQty) { this.currentQty = currentQty; }


    public Product(int bookInfoId, String bookISBN_n, String id, String bookTitle, int bookPrice, int bookSales,
                   String bookImg1, String bookCond) {
        this.bookInfoId = bookInfoId;
        this.bookISBN_n = bookISBN_n;

        this.id = id;
        this.bookTitle = bookTitle;
        this.bookPrice = bookPrice;
        this.bookSales = bookSales;
        this.bookImg1 = bookImg1;
        this.bookCond = bookCond;
    }

//    public Product(String bookTitle, int bookPrice, int bookSales,
//                   String bookImg1, String bookImg2, String bookCond) {
//        this.bookTitle = bookTitle;
//        this.bookPrice = bookPrice;
//        this.bookSales = bookSales;
//        this.bookImg1 = bookImg1;
//        this.bookImg2 = bookImg2;
//        this.bookCond = bookCond;
//    }

    public String getId() { return id; }
    public String getBookTitle() { return bookTitle; }
    public int getBookPrice() { return bookPrice; }
    public int getBookSales() { return bookSales; }
    public String getBookImg1() { return bookImg1; }
    public String getBookImg2() { return bookImg2; }
    public String getBookCond() { return bookCond; }
    public int getBookInfoId() { return bookInfoId; }

    public String getBookISBN_n() {
        return bookISBN_n;
    }

    public void setBookISBN_n(String bookISBN_n) {
        this.bookISBN_n = bookISBN_n;
    }

    public boolean hasDiscount() {
        return bookSales > 0;
    }

    public int getDiscountedPrice() {
        if (!hasDiscount()) return bookPrice;
        return bookPrice * (100 - bookSales) / 100;
    }


}
