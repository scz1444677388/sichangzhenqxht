package com.qhit.itravel.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * (Favorite)实体类
 *
 * @author makejava
 * @since 2020-04-15 17:59:19
 */
public class Favorite implements Serializable {
    private static final long serialVersionUID = -63384059764615967L;
    
    private Integer rid;
    
    private Date date;
    
    private Integer uid;


    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

}