package com.hanibey.smartorder.administration.admin;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorder.administration.StartActivity;
import com.hanibey.smartorderhelper.Constant;


public class AdminMainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    public int selectedFragmentIndex = Constant.Fragments.Report;
    public static final AdminMainActivity instance = new AdminMainActivity();
    public static AdminMainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminmain);

         mToolbar = (Toolbar) findViewById(R.id.toolbar);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        displayView(AdminMainActivity.getInstance().selectedFragmentIndex);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(Constant.CurrentUser != null){
            String name = Constant.CurrentUser.FirstName +" "+Constant.CurrentUser.LastName;
            menu.findItem(R.id.user_info).setTitle(name);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_exit) {
            Intent intent = new Intent(AdminMainActivity.this, StartActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (position) {
            case Constant.Fragments.Report:
                fragment = new FragmentReport();
                title = getString(R.string.title_report);
                break;
            case Constant.Fragments.User:
                fragment = new FragmentUser();
                title = getString(R.string.title_user);
                break;
            case Constant.Fragments.Category:
                fragment = new FragmentCategory();
                title = getString(R.string.title_category);
                break;
            case Constant.Fragments.Product:
                fragment = new FragmentProduct();
                title = getString(R.string.title_product);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }
}