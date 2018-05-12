package com.pashkobohdan.ttsreader.data.model.dto.book;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.pashkobohdan.ttsreader.data.model.dto.common.CommonDTO;

@Entity(tableName = "BookTitleImage")
public class BookTitleImageDTO extends CommonDTO {

    @PrimaryKey(autoGenerate = true)
    private int id;


    private String bookId;
    private boolean isImage;
    private String color;
    private String base64Image;

    public BookTitleImageDTO() {
        //Default constructor for Room
    }

    @Ignore
    public BookTitleImageDTO(String bookId, boolean isImage, String color, String base64Image) {
        this.bookId = bookId;
        this.isImage = isImage;
        this.color = color;
        this.base64Image = base64Image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }
}
