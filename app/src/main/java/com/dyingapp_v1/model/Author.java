package com.dyingapp_v1.model;

public class Author {
    // Các trường phải khớp với key trong JSON trả về từ API
    private String _id;
    private String AuthorName;
    private String AuthorImage;

    // --- THÊM CÁC TRƯỜNG MỚI VÀO ĐÂY ---
    private String AuthorDesc;
    private String AuthorBorn;
    private String AuthorGenre;
    // ------------------------------------

    // Bắt buộc phải có constructor rỗng
    public Author() {
    }

    // Constructor để dễ dàng tạo đối tượng (cập nhật với các trường mới)
    public Author(String id, String authorName, String authorImage, String authorDesc, String authorBorn, String authorGenre) {
        this._id = id;
        this.AuthorName = authorName;
        this.AuthorImage = authorImage;
        this.AuthorDesc = authorDesc;
        this.AuthorBorn = authorBorn;
        this.AuthorGenre = authorGenre;
    }

    // Getters and Setters
    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getAuthorName() {
        return AuthorName;
    }

    public void setAuthorName(String authorName) {
        this.AuthorName = authorName;
    }

    public String getAuthorImage() {
        return AuthorImage;
    }

    public void setAuthorImage(String authorImage) {
        this.AuthorImage = authorImage;
    }

    // --- GETTERS AND SETTERS CHO CÁC TRƯỜNG MỚI ---
    public String getAuthorDesc() {
        return AuthorDesc;
    }

    public void setAuthorDesc(String authorDesc) {
        this.AuthorDesc = authorDesc;
    }

    public String getAuthorBorn() {
        return AuthorBorn;
    }

    public void setAuthorBorn(String authorBorn) {
        this.AuthorBorn = authorBorn;
    }

    public String getAuthorGenre() {
        return AuthorGenre;
    }

    public void setAuthorGenre(String authorGenre) {
        this.AuthorGenre = authorGenre;
    }
    // ---------------------------------------------
}
