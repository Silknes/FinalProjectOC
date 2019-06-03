package com.androidapp.finalprojectoc.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.androidapp.finalprojectoc.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private static final int TASK_SIGN_OUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If no user is logged into the app then we start Authentication Activity
        if(isCurrentUserLogged()){
            this.configureToolbar();
            this.configureDrawerLayout();
            this.configureNavigationView();

            TextView usernameText = navigationView.getHeaderView(0).findViewById(R.id.textView_username);
            TextView userEmailText = navigationView.getHeaderView(0).findViewById(R.id.textView_user_email);

            usernameText.setText(getCurrentUser().getDisplayName());
            userEmailText.setText(getCurrentUser().getEmail());
        } else {
            Intent authIntent = new Intent(this, AuthenticationActivity.class);
            startActivity(authIntent);
        }

    }

    /**********************************************
     **** Configure menus and items action ********
     *********************************************/

    private void configureToolbar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void configureDrawerLayout(){
        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView(){
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_toolbar_notification:
                Toast.makeText(this, R.string.toolbar_item_notification, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_drawer_log_out:
                this.signOutUserFromFirebase();
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*************************
     **** Manage Firebase ****
     ************************/

    // Used to Sign Out the user
    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRestRequestsCompleted(TASK_SIGN_OUT));
    }

    // When user is offline starting Authentication Activity
    private OnSuccessListener<Void> updateUIAfterRestRequestsCompleted(final int taskId){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch(taskId){
                    case TASK_SIGN_OUT:
                        Intent signInIntent = new Intent(getBaseContext(), AuthenticationActivity.class);
                        startActivity(signInIntent);
                        break;
                }
            }
        };
    }
}
