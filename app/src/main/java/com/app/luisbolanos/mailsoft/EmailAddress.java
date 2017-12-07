package com.app.luisbolanos.mailsoft;

/**
 * Created by luis.bolanos on 6/12/2017.
 */

public class EmailAddress {
    private String id;
    private String email;

    public EmailAddress(String idp, String emailp)
    {
        setId(idp);
        setEmail(emailp);
    }

    public EmailAddress()
    {
        setId("");
        setEmail("");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
