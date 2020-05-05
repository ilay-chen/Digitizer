package com.icstudios.digitizer;

import java.util.Calendar;

public class userData {
    public String name;
    public long expireTime;
    public String validationCode;

    public userData(){

    }

    public userData(String name){
        this.name = name;
        Calendar scheduledDate = Calendar.getInstance();
        scheduledDate.add(Calendar.MONTH, 1);
        this.expireTime = scheduledDate.getTime().getTime();
    }

    public Boolean setValidation(String validationCode, int month)
    {
        this.validationCode = validationCode;
        Calendar scheduledDate = Calendar.getInstance();
        scheduledDate.add(Calendar.MONTH, month);
        this.expireTime = scheduledDate.getTime().getTime();
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValidationCode(String validationCode) {
        this.validationCode = validationCode;
    }

    public String getValidationCode() {
        return validationCode;
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
