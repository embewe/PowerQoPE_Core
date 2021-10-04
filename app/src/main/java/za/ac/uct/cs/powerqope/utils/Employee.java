package za.ac.uct.cs.powerqope.utils;

public class Employee {
    int id;
    String upload, download, date;

    public Employee(int id, String download, String upload, String date) {
        this.id = id;
        this.download = download;
        this.upload = upload;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUpload() {
        return upload;
    }

    public void setUpload(String upload) {
        this.upload = upload;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
