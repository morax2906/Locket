package com.tandev.locket;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.tandev.locket.fragment.home.HomeFragment;
import com.tandev.locket.fragment.login.LoginOrRegisterFragment;
import com.tandev.locket.sharedfreferences.SharedPreferencesUser;
import com.tandev.locket.test.DataSyncWorker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tạo PeriodicWorkRequest để chạy công việc mỗi 15 phút
        PeriodicWorkRequest syncWorkRequest = new PeriodicWorkRequest.Builder(DataSyncWorker.class, 15, TimeUnit.MINUTES)
                .build();

        // Đăng ký công việc với WorkManager
        WorkManager.getInstance(this).enqueue(syncWorkRequest);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, SharedPreferencesUser.getLoginResponse(this) != null ? new HomeFragment() : new LoginOrRegisterFragment())
                    .commit();
        }

    }
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
        );

        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void replaceFragmentWithBundle(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
        );

        transaction.replace(R.id.frame_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}