package com.hanibey.smartorder.administration.admin;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorderadapter.UserListAdapter;
import com.hanibey.smartorderbusiness.ActivityService;
import com.hanibey.smartorderbusiness.UserService;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartordermodel.User;
import com.hanibey.smartorderrepository.FirebaseDb;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentUser extends Fragment {
    DatabaseReference userRef;
    UserService userService;
    ActivityService activityService;

    public String selectedUserKey = "";
    public static final FragmentUser instance = new FragmentUser();
    public static FragmentUser getInstance() {
        return instance;
    }

    public FragmentUser() {
        userRef = FirebaseDb.getDatabase().getReference(Constant.Nodes.User);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userService = new UserService(getActivity());
        activityService = new ActivityService(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        ListView userListView = (ListView) rootView.findViewById(R.id.users_listview);
        setUserToListView(userListView);

        FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                userService.showUserDialog();
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setUserToListView(final ListView usersListview){

        try{
            final ProgressDialog progressDialog = ProgressDialog.show(getContext(), getString(R.string.uploading),
                    getString(R.string.uploading_data_message), false, false);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<User> userList = new ArrayList<>();

                    for(DataSnapshot pos : dataSnapshot.getChildren()){
                        User user = pos.getValue(User.class);
                        user.Key = pos.getKey();
                        userList.add(user);
                    }

                    if(getActivity() != null){
                        UserListAdapter adapter = new UserListAdapter(getActivity(), userList);
                        usersListview.setAdapter(adapter);
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Error", "Failed to read value.", error.toException());
                }
            });
        }
        catch (Exception ex){
            Log.e("Hata", ex.toString());
        }


    }

}
