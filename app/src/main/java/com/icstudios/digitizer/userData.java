package com.icstudios.digitizer;

import java.util.Calendar;

public class userData {
    public String name;
    public long expireTime;
    public String privateCode;

    public userData(){

    }

    public userData(String name){
        this.name = name;
        Calendar scheduledDate = Calendar.getInstance();
        scheduledDate.add(Calendar.MONTH, 1);
        this.expireTime = scheduledDate.getTime().getTime();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrivateCode(String privateCode) {
        this.privateCode = privateCode;
    }

    public String getPrivateCode() {
        return privateCode;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public Boolean isExpire()
    {
        Calendar scheduledDate = Calendar.getInstance();
        if(scheduledDate.getTime().getTime() > this.expireTime)
            return true;
        return false;
    }
}
