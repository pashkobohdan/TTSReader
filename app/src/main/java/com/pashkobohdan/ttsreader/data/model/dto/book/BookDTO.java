package com.pashkobohdan.ttsreader.data.model.dto.book;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.pashkobohdan.ttsreader.data.model.dto.common.CommonDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "Book")
public class BookDTO extends CommonDTO {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @PrimaryKey()
    private long id;

    private String name;
    private String author;
    private String text;
    private Integer length;
    private Integer progress;
    private Integer readingSpeed;
    private Integer readingPitch;
    private String createDateString;
    private String lastOpenDateString;

    public BookDTO() {
        //Default constructor for Room
        id = System.currentTimeMillis();
    }

    @Ignore
    public BookDTO(String name, String author, String text, Integer length, Integer progress, Integer readingSpeed, Integer readingPitch, String createDateString, String lastOpenDateString) {
        this.name = name;
        this.author = author;
        this.text = text;
        this.length = length;
        this.progress = progress;
        this.readingSpeed = readingSpeed;
        this.readingPitch = readingPitch;
        this.createDateString = createDateString;
        this.lastOpenDateString = lastOpenDateString;
        id = System.currentTimeMillis();
    }

    @Ignore
    public BookDTO(String name, String author, String text, Integer length, Integer progress, Integer readingSpeed, Integer readingPitch, Date createDate, Date lastOpenDate) {
        this.name = name;
        this.author = author;
        this.text = text;
        this.length = length;
        this.progress = progress;
        this.readingSpeed = readingSpeed;
        this.readingPitch = readingPitch;
        setCreateDate(createDate);
        setLastOpenDate(lastOpenDate);
        id = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getCreateDateString() {
        return createDateString;
    }

    public void setCreateDateString(String createDateString) {
        this.createDateString = createDateString;
    }

    public String getLastOpenDateString() {
        return lastOpenDateString;
    }

    public void setLastOpenDateString(String lastOpenDateString) {
        this.lastOpenDateString = lastOpenDateString;
    }

    public Date getCreateDate() {
        try {
            return DATE_FORMAT.parse(createDateString);
        } catch (ParseException e) {
            e.printStackTrace();//TODO add slf4j
            return null;
        }
    }

    public Date getLastOpenDate() {
        try {
            return DATE_FORMAT.parse(lastOpenDateString);
        } catch (ParseException e) {
            e.printStackTrace();//TODO add slf4j
            return null;
        }
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public void setCreateDate(Date date) {
        createDateString = DATE_FORMAT.format(date);
    }

    public void setLastOpenDate(Date date) {
        lastOpenDateString = DATE_FORMAT.format(date);
    }

    public Integer getReadingSpeed() {
        return readingSpeed;
    }

    public void setReadingSpeed(Integer readingSpeed) {
        this.readingSpeed = readingSpeed;
    }

    public Integer getReadingPitch() {
        return readingPitch;
    }

    public void setReadingPitch(Integer readingPitch) {
        this.readingPitch = readingPitch;
    }
}
