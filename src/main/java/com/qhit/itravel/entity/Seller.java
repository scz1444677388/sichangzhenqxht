package com.qhit.itravel.entity;

import java.io.Serializable;

/**
 * (Seller)实体类
 *
 * @author makejava
 * @since 2020-04-14 15:01:44
 */
public class Seller implements Serializable {
    private static final long serialVersionUID = -88296392300347816L;
    
    private Integer sid;
    
    private String sname;
    
    private String consphone;
    
    private String address;


    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getConsphone() {
        return consphone;
    }

    public void setConsphone(String consphone) {
        this.consphone = consphone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}