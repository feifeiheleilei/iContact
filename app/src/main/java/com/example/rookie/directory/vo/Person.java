package com.example.rookie.directory.vo;

import java.io.Serializable;

/**
 * Created by Rookie on 2015/12/13.
 */
public class Person implements Serializable{
    private String disPlayName;
    private String number;
    private long rawContactId;

    public long getRawContactId() {
        return rawContactId;
    }

    public void setRawContactId(long rawContactId) {
        this.rawContactId = rawContactId;
    }

    public void setDisPlayName(String disPlayName) {
        this.disPlayName = disPlayName;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDisPlayName() {
        return disPlayName;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return getDisPlayName()+"\n"+getNumber();
    }
}
