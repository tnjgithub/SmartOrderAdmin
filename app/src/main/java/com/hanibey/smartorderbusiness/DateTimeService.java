package com.hanibey.smartorderbusiness;

import android.text.format.DateFormat;

import com.hanibey.smartorderhelper.Constant;

import java.util.Date;

/**
 * Created by Tanju on 30.12.2017.
 */

public class DateTimeService {
    public String getCurrentDate(){
        DateFormat df = new android.text.format.DateFormat();
        String date = df.format("dd.MM.yyyy hh:mm", new Date()).toString();
        return  date;
    }


    public String getDate(int dateType){
        DateFormat df = new android.text.format.DateFormat();
        String date="";

        switch (dateType){
            case Constant.DateTypes.Year:
                date = df.format("yyyy", new Date()).toString();
                break;
            case Constant.DateTypes.Month:
                date = df.format("MM", new Date()).toString();
                break;
            case Constant.DateTypes.Day:
                date = df.format("dd", new Date()).toString();
                break;
        }

        return  date;
    }

}
