package com.qhit.itravel.entity;

import java.io.Serializable;

/**
 * (Category)实体类
 *
 * @author makejava
 * @since 2020-04-08 17:52:55
 */
public class Category implements Serializable {
    private static final long serialVersionUID = 347176194957834551L;
    
    private Integer cid;
    
    private String cname;


    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    @Override
    public String toString() {
        return "Category{" +
                "cid=" + cid +
                ", cname='" + cname + '\'' +
                '}';
    }
}