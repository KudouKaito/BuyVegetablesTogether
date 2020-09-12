package com.example.buyvegetablestogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sdx.statusbar.statusbar.StatusBarUtil;

public class MainActivity extends AppCompatActivity {
    private NavController navController = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        StatusBarUtil.setStutatusBar(this, true, true, 1);
        StatusBarUtil.setRootViewFitsSystemWindows(this,true);
        StatusBarUtil.setStatusBarDarkTheme(this, true);
        setupBottomNavigationBar();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setupBottomNavigationBar();
    }

    private void setupBottomNavigationBar() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNav, navController);

    }

    @Override
    public boolean onSupportNavigateUp() {
        if (null != navController)
            return navController.navigateUp();
        else
            return false;
    }


}