package com.hanibey.smartordermodel;

/**
 * Created by Tanju on 25.12.2017.
 */

public class AppLog {

    public String LogDate;
    public String ClassName;
    public String MetodName;
    public String LogMessage;

    public AppLog() { }

    public AppLog(String logDate, String className, String metodName, String logMessage) {
        super();
        this.LogDate = logDate;
        this.ClassName = className;
        this.MetodName = metodName;
        this.LogMessage = logMessage;
    }
}
