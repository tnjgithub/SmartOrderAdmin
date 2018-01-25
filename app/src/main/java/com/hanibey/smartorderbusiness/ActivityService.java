package com.hanibey.smartorderbusiness;

import android.app.Activity;
import android.content.Intent;

import com.hanibey.smartorder.administration.admin.AdminMainActivity;

/**
 * Created by Tanju on 19.12.2017.
 */

public class ActivityService {

    Activity activity;
    public ActivityService(Activity act){
        this.activity = act;
    }

    public void openFrament(int index){
        AdminMainActivity.getInstance().selectedFragmentIndex = index;
        Intent intent = new Intent(activity, AdminMainActivity.class);
        activity.startActivity(intent);
    }
}
