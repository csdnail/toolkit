package com.ray.gg.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ray.gg.BaseActivity;
import com.ray.gg.ui.fragment.MainFragment;

import log.Log;

public class MainActivity extends BaseActivity {

    MainFragment mainFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout content = new FrameLayout(this);
        content.setId(android.R.id.home);
        setContentView(content, new ViewGroup.LayoutParams(-1, -1));

        mainFragment = new MainFragment();
        mainFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.home, mainFragment)
                .commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("ryan", "MainActivity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
