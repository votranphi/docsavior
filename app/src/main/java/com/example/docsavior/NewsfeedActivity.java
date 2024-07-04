package com.example.docsavior;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;

public class NewsfeedActivity extends AppCompatActivity implements FragmentNavigation {
    ViewPager2 viewPager2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_newsfeed);

        initViewPager2();

        startService(ApplicationInfo.username);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(ApplicationInfo.activityLifecycleManager);
        }
    }

    // move all the code lines that deal with ViewPager2 in onCreate() to this function
    private void initViewPager2() {
        // Khởi tạo ViewPager2 cho 5 fragment, khởi tạo thanh điều hướng bottomNavigation
        viewPager2 = findViewById(R.id.viewPager);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        //Gắn adapter cho ViewPager2
        ViewPager2Adapter viewPager2Adapter = new ViewPager2Adapter(NewsfeedActivity.this, this);
        viewPager2.setAdapter(viewPager2Adapter);

        // Chức năng cho ViewPager2 mỗi khi lướt tới 1 viewpager nào đó sẽ đồng bộ với thanh điều hướng tuương ứng
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.btnHome).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.btnChat).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.btnFriend).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.btnNotification).setChecked(true);
                        break;
                    case 4:
                        bottomNavigationView.getMenu().findItem(R.id.btnSetting).setChecked(true);
                        break;
                }
            }
        });

        // Chức năng cho thanh điều hướng mỗi khi ấn 1 button nào đó sẽ đồng bộ với ViewPager2 tương ứng
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                if (itemId == R.id.btnHome) {
                    viewPager2.setCurrentItem(0);
                } else if (itemId == R.id.btnChat) {
                    viewPager2.setCurrentItem(1);
                } else if (itemId == R.id.btnFriend) {
                    viewPager2.setCurrentItem(2);
                } else if (itemId == R.id.btnNotification) {
                    viewPager2.setCurrentItem(3);
                } else if (itemId == R.id.btnSetting) {
                    viewPager2.setCurrentItem(4);
                }
                return true;
            }
        });
    }

    @Override
    public void goToFriendFragment() {
        viewPager2.setCurrentItem(2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startService(String userID) {
        long appID = 1163686136;
        String appSign = "156dec21df7edd3436628ad0f31184bbc64ec1804ea73bf9fa93c4fa49c5f8ad";
        String userName = userID;

        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();

        ZegoUIKitPrebuiltCallService.init(getApplication(), appID, appSign, userID, userName,callInvitationConfig);
    }
}
