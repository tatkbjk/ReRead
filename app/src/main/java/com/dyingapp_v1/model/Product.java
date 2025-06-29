package com.dyingapp_v1.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
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
    private int BookQuantity;
    private boolean isSelected;

    public Product() {}

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

    // --------- GETTER & SETTER ---------
    public String getId() { return id; }
    public String getBookTitle() { return bookTitle; }
    public int getBookPrice() { return bookPrice; }
    public int getBookSales() { return bookSales; }
    public String getBookImg1() { return bookImg1; }
    public String getBookImg2() { return bookImg2; }
    public String getBookCond() { return bookCond; }
    public int getBookInfoId() { return bookInfoId; }
    public int getCurrentQty() { return currentQty; }
    public int getBookQuantity() { return BookQuantity; }
    public boolean isSelected() { return isSelected; }
    public String getBookISBN_n() { return bookISBN_n; }


    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public void setBookCond(String bookCond) { this.bookCond = bookCond; }
    public void setBookImg1(String bookImg1) { this.bookImg1 = bookImg1; }
    public void setBookPrice(int bookPrice) { this.bookPrice = bookPrice; }
    public void setBookSales(int bookSales) { this.bookSales = bookSales; }
    public void setCurrentQty(int currentQty) { this.currentQty = currentQty; }
    public void setBookQuantity(int bookQuantity) { this.BookQuantity = bookQuantity; }
    public void setSelected(boolean selected) { isSelected = selected; }
    public void setBookISBN_n(String bookISBN_n) { this.bookISBN_n = bookISBN_n; }



    public boolean hasDiscount() {
        return bookSales > 0;
    }

    public int getDiscountedPrice() {
        if (!hasDiscount()) return bookPrice;
        return bookPrice * (100 - bookSales) / 100;
    }

    // --------- PARCELABLE ---------
    protected Product(Parcel in) {
        id = in.readString();
        bookISBN_n = in.readString();
        bookTitle = in.readString();
        bookPrice = in.readInt();
        bookSales = in.readInt();
        bookImg1 = in.readString();
        bookImg2 = in.readString();
        bookCond = in.readString();
        bookInfoId = in.readInt();
        currentQty = in.readInt();
        BookQuantity = in.readInt();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(bookISBN_n);
        dest.writeString(bookTitle);
        dest.writeInt(bookPrice);
        dest.writeInt(bookSales);
        dest.writeString(bookImg1);
        dest.writeString(bookImg2);
        dest.writeString(bookCond);
        dest.writeInt(bookInfoId);
        dest.writeInt(currentQty);
        dest.writeInt(BookQuantity);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };


    public void setBookInfoId(int bookInfoID) {
        this.bookInfoId = bookInfoID;
    }


}
