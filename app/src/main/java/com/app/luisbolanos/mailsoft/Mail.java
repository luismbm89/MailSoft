package com.app.luisbolanos.mailsoft;

/**
 * Created by luis.bolanos on 6/12/2017.
 */

public class Mail {
    @com.google.gson.annotations.SerializedName("id")
    private String id;
    @com.google.gson.annotations.SerializedName("subject")
    private String subject;
    @com.google.gson.annotations.SerializedName("to")
    private String  to;
    @com.google.gson.annotations.SerializedName("from")
    private String from;
    @com.google.gson.annotations.SerializedName("message")
    private String message;
    private Boolean Read;

    public Mail()
    {
        setId("");
        setSubject("");
        setTo("");
        setFrom("");
        setMessage("");
        setRead(false);

    }

    public Mail(String idp, String subjectp, String top, String fromp,String messagep, Boolean readp)
    {
        setId(idp);
        setSubject(subjectp);
        setTo(top);
        setFrom(fromp);
        setMessage(messagep);
        setRead(readp);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getRead() {
        return Read;
    }

    public void setRead(Boolean read) {
        Read = read;
    }
}
