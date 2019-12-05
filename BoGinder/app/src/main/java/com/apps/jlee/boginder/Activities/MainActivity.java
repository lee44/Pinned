package com.apps.jlee.boginder.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.apps.jlee.boginder.Fragments.AccountFragment;
import com.apps.jlee.boginder.Fragments.MatchFragment;
import com.apps.jlee.boginder.Fragments.DateFragment;
import com.apps.jlee.boginder.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity
{
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        final AccountFragment accountFragment = new AccountFragment();
        final MatchFragment matchFragment = new MatchFragment(this);
        final DateFragment dateFragment = new DateFragment(this);

        setFragment(dateFragment);
        bottomNavigationView.setSelectedItemId(R.id.dates);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.account:
                        setFragment(accountFragment);
                        break;
                    case R.id.dates:
                        setFragment(dateFragment);
                        break;
                    case R.id.chat:
                        setFragment(matchFragment);
                        break;
                }
                return true;
            }
        });
    }

    public void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed()
    {
        //Intent intent = new Intent(this,LoginRegisterActivity.class);
        //startActivity(intent);
    }
}
