package com.apps.jlee.coffinder.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apps.jlee.coffinder.Activities.ProfileSliderActivity;
import com.apps.jlee.coffinder.Models.Card;
import com.apps.jlee.coffinder.Interfaces.ProfileInterface;
import com.apps.jlee.coffinder.R;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfilePreviewFragment extends Fragment implements ProfileInterface.ProfileCallback
{
    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.age_city) TextView age_city;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.height_header) TextView height_header;
    @BindView(R.id.height) TextView height;
    @BindView(R.id.imageView1) ImageView job_image;
    @BindView(R.id.job) TextView job;
    @BindView(R.id.imageView2) ImageView school_image;
    @BindView(R.id.school) TextView school;
    @BindView(R.id.description_header) TextView description_header;
    @BindView(R.id.description) TextView description;
    @BindView(R.id.religion_header) TextView religion_header;
    @BindView(R.id.religion) TextView religion;
    @BindView(R.id.ethnicity_header) TextView ethnicity_header;
    @BindView(R.id.ethnicity) TextView ethnicity;
    @BindView(R.id.edit_floatingActionButton) FloatingActionButton editButton;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.no_data) TextView no_data;
    @BindView(R.id.SliderDots) LinearLayout sliderDotspanel;

    private ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    private DatabaseReference databaseReference;
    static ArrayList<String> profileImageUrlArray;
    private float x, y;
    private final int THRESHOLD = 10;
    private int dotscount;
    private ImageView[] dots;

    public ProfilePreviewFragment(Context context)
    {
        profileImageUrlArray = new ArrayList<>();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profile_preview, container, false);
        ButterKnife.bind(this, view);

        toggleUI(true);

        String user_id;
        if(getArguments() == null)
            user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        else
            user_id = getArguments().getString("user_id");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(imageFragmentPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position)
            {
                for(int i = 0; i< dotscount; i++)
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.non_active_dot));

                dots[position].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        viewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        x = motionEvent.getX();
                        y = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        x -= motionEvent.getX();
                        y -= motionEvent.getY();
                        Log.v("Lakers","X:"+Math.abs(x)+", Y:"+Math.abs(y));
                        if(Math.abs(x) <= THRESHOLD && Math.abs(y) <= THRESHOLD)
                        {
                            Intent intent = new Intent(getActivity(),ProfileSliderActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("url_arraylist",profileImageUrlArray);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        break;
                }
                return false;
            }
        });

        editButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setFragment(new AccountFragment());
            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                long childrenSize = dataSnapshot.child("ProfileImageUrl").getChildrenCount();
                if(childrenSize != 0)
                {
                    for (DataSnapshot child : dataSnapshot.child("ProfileImageUrl").getChildren())
                    {
                        profileImageUrlArray.add(child.getValue().toString());
                    }
                }
                else
                {
                    profileImageUrlArray.add(dataSnapshot.child("ProfileImageUrl").getValue().toString());
                }
                imageFragmentPagerAdapter.notifyDataSetChanged();

                Card card = new Card(
                        "",
                        dataSnapshot.child("Name").getValue().toString(),
                        dataSnapshot.child("Age").getValue().toString(),
                        dataSnapshot.child("Height").getValue().toString(),
                        dataSnapshot.child("City").getValue().toString(),
                        dataSnapshot.child("Occupation").exists() ? dataSnapshot.child("Occupation").getValue().toString() : "N/A",
                        dataSnapshot.child("School").exists() ? dataSnapshot.child("School").getValue().toString() : "N/A",
                        dataSnapshot.child("Ethnicity").exists() ? dataSnapshot.child("Ethnicity").getValue().toString() : "N/A",
                        dataSnapshot.child("Religion").exists() ? dataSnapshot.child("Religion").getValue().toString() : "N/A",
                        dataSnapshot.child("Description").exists() ? dataSnapshot.child("Description").getValue().toString() : "N/A",
                        profileImageUrlArray
                        );

                loadUI(card);

                if(getArguments() != null) editButton.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                progressBar.setVisibility(View.GONE);
                no_data.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    @Override
    public void loadUI(Card card)
    {
        name.setText(card.getName());
        age_city.setText(card.getAge() + ", " + card.getCity());
        height.setText(card.getHeight());
        job.setText(card.getJob().length() != 0 ? card.getJob() : "N/A");
        school.setText(card.getSchool().length() != 0 ? card.getSchool() : "N/A");
        description.setText(card.getDescription().length() != 0 ? card.getDescription() : "N/A");
        ethnicity.setText(card.getEthnicity().length() != 0 ? card.getEthnicity() : "N/A");
        religion.setText(card.getReligion().length() != 0 ? card.getReligion() : "N/A");

        dotscount = imageFragmentPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for(int i = 0; i < dotscount; i++)
        {
            dots[i] = new ImageView(getContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.non_active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDotspanel.addView(dots[i], params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));

        toggleUI(false);
    }

    public void toggleUI(boolean toggle)
    {
        if(toggle)
        {
            progressBar.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
            age_city.setVisibility(View.GONE);
            job_image.setVisibility(View.GONE);
            job.setVisibility(View.GONE);
            school_image.setVisibility(View.GONE);
            school.setVisibility(View.GONE);
            height.setVisibility(View.GONE);
            height_header.setVisibility(View.GONE);
            description_header.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
            religion_header.setVisibility(View.GONE);
            religion.setVisibility(View.GONE);
            ethnicity_header.setVisibility(View.GONE);
            ethnicity.setVisibility(View.GONE);
            editButton.hide();
        }
        else
        {
            progressBar.setVisibility(View.INVISIBLE);
            viewPager.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            age_city.setVisibility(View.VISIBLE);
            job_image.setVisibility(View.VISIBLE);
            job.setVisibility(View.VISIBLE);
            school_image.setVisibility(View.VISIBLE);
            school.setVisibility(View.VISIBLE);
            height.setVisibility(View.VISIBLE);
            height_header.setVisibility(View.VISIBLE);
            description_header.setVisibility(View.VISIBLE);
            description.setVisibility(View.VISIBLE);
            religion_header.setVisibility(View.VISIBLE);
            religion.setVisibility(View.VISIBLE);
            ethnicity_header.setVisibility(View.VISIBLE);
            ethnicity.setVisibility(View.VISIBLE);
            editButton.show();
        }
    }

    public void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.profile_preview_main_frame, fragment,"AccountFragment");
        fragmentTransaction.commit();
    }

    class ImageFragmentPagerAdapter extends FragmentPagerAdapter
    {
        public ImageFragmentPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public int getCount()
        {
            for(int i = 0; i < profileImageUrlArray.size(); i++)
            {
                if(profileImageUrlArray.get(i).equals("Default"))
                {
                    return i;
                }
            }
            return profileImageUrlArray.size();
        }

        @Override
        public Fragment getItem(int position)
        {
            return SwipeFragment.newInstance(position);
        }
    }

    public static class SwipeFragment extends Fragment
    {
        public static SwipeFragment newInstance(int position)
        {
            SwipeFragment swipeFragment = new SwipeFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("SwipeFragment_position", position);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View swipeView = inflater.inflate(R.layout.fragment_swipe_small, container, false);
            ImageView imageView = swipeView.findViewById(R.id.imageView);

            Bundle bundle = getArguments();
            int position = bundle.getInt("SwipeFragment_position");
            Glide.with(getContext()).load(profileImageUrlArray.get(position)).into(imageView);

            return swipeView;
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        //Log.v("Lakers","DateFragment started");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //Log.v("Lakers","DateFragment resume");

    }

    @Override
    public void onPause()
    {
        super.onPause();
        //Log.v("Lakers","DateFragment paused");

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //Log.v("Lakers","DateFragment destroyed");
    }
}

