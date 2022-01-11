package com.example.eventsearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.text.Html;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private static final int NUM_PAGES = 2;

    private ViewPager2 viewPager2;
    private TabLayout sfTabLayout;

    private FragmentStateAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\'black\'>"+"Event Search"+"</font>"));

        viewPager2 = findViewById(R.id.viewpager); //Data Binding
        sfTabLayout = findViewById(R.id.sfTabLayout);

        viewPager2.setUserInputEnabled(true);

        pagerAdapter = new SearchFragmentPagerAdapter(this);
        viewPager2.setAdapter(pagerAdapter);

        new TabLayoutMediator(sfTabLayout,viewPager2,(tab, position) -> {
            tab.setText(position==0?"Search":"Favorites");
        }).attach();

        sfTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getText().equals("Search")){
                    viewPager2.setCurrentItem(0,true);
                }
                else if(tab.getText().equals("Favorites")){
                    //Toast.makeText(MainActivity.this,tab.getText(), Toast.LENGTH_SHORT).show();
                    viewPager2.setCurrentItem(1,true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    public class SearchFragmentPagerAdapter extends FragmentStateAdapter{

        public SearchFragmentPagerAdapter(@NonNull @NotNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @NotNull
        @Override
        public Fragment createFragment(int position) {

            switch (position){
                case 0: return new SearchFragment();
                case 1: return new FavoriteFragment();
                default: return new SearchFragment();
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}

