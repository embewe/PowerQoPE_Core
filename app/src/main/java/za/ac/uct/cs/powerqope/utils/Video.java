package za.ac.uct.cs.powerqope.utils;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Video {
    int id;
    String date, buffer, loadtime,bandwidth;

    public Video(int id, String date, String buffer, String loadtime, String bandwidth) {
        this.id = id;
        this.date = date;
        this.buffer = buffer;
        this.loadtime = loadtime;
        this.bandwidth = bandwidth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBuffer() {
        return buffer;
    }

    public void setBuffer(String buffer) {
        this.buffer = buffer;
    }

    public String getLoadtime() {
        return loadtime;
    }

    public void setLoadtime(String loadtime) {
        this.loadtime = loadtime;
    }

    public String getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(String bandwidth) {
        this.bandwidth = bandwidth;
    }


}
