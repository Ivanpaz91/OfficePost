
package com.officewall.pojo.core;

import com.google.gson.annotations.Expose;

public class Contact {
    @Expose
    private String name;
    @Expose
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
