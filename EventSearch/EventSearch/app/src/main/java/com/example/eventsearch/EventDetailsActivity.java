package com.example.eventsearch;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EventDetailsActivity extends AppCompatActivity {

    private static final int NUM_PAGES = 3;

    private String id, eventName;
    private boolean isFavorite;
    private JSONObject event, artist, venue;

    private ViewPager2 detailsPager;
    private TabLayout detailsTabLayout;

    private EventDetailsPagerAdapter eventDetailsPagerAdapter;

    private EventDetailsFragment eventDetailsFragment;
    private ArtistFragment artistFragment;
    private VenueFragment venueFragment;
    private Gson gson;
    private View actionBarView;
    private TextView eventDetailsName;
    private ImageButton tweetButton, likeButton;
    private FavoriteService favoriteService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.Black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.layout_custom_action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarView = getSupportActionBar().getCustomView();

        eventDetailsName = actionBarView.findViewById(R.id.eventDetailsName);
        tweetButton = actionBarView.findViewById(R.id.twitterButton);
        likeButton = actionBarView.findViewById(R.id.detailsLikeButton);

        eventDetailsPagerAdapter = new EventDetailsPagerAdapter(this);
        detailsPager = findViewById(R.id.detailsPager);
        detailsTabLayout = findViewById(R.id.detailsTabLayout);
        detailsPager.setAdapter(eventDetailsPagerAdapter);

        favoriteService = new FavoriteService(this);

        new TabLayoutMediator(detailsTabLayout,detailsPager,(tab, position) -> {
            tab.setIcon(position==0?R.drawable.info_outline:(position==1?R.drawable.artist:R.drawable.venue));
            tab.setText(position==0?"Events":(position==1?"Artist(s)":"Venue"));
        }).attach();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        eventName = intent.getStringExtra("name");
        isFavorite = intent.getBooleanExtra("isFavorite",false);

        eventDetailsName.setText(cropText(eventName,21));
        if(isFavorite){
            likeButton.setBackgroundResource(R.drawable.heart_fill_white);
        }

        gson = new Gson();

        eventDetailsFragment = new EventDetailsFragment();
        artistFragment = new ArtistFragment();
        venueFragment = new VenueFragment();


        getData();


        tweetButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                    String tweetText = "Check%20out%20"+String.join("%20",event.getString("Event").split("\\s"))+
                            "%20at%20"+String.join("%20",event.getString("Venue").split("\\s"));
                    Uri uri = Uri.parse("https://twitter.com/intent/tweet?text="+tweetText);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch (JSONException e) {
                    Toast.makeText(EventDetailsActivity.this, "JSON Error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Event e = new Event(event.getString("id"),event.getString("Event"),
                            event.getString("Date"),event.getString("Category"),
                            event.getString("Venue"));
                    if(favoriteService.isPresent(e)){
                        favoriteService.remove(e);
                        likeButton.setBackgroundResource(R.drawable.heart_outline_white);
                    }
                    else{
                        favoriteService.push(e);
                        likeButton.setBackgroundResource(R.drawable.heart_fill_white);
                    }

                } catch (JSONException jsonException) {
                    Toast.makeText(EventDetailsActivity.this, "JSON Error", Toast.LENGTH_SHORT).show();
                    jsonException.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getData(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://ticketserver2571-new.wl.r.appspot.com/api/details?eventId="+id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                (JSONObject) null,
                response -> {
                    try {
                        event = response.getJSONObject("Event");
                        artist = response.getJSONObject("Artist");
                        venue = response.getJSONObject("Venue");

                        eventDetailsFragment.update(event);

                        Bundle artistBundle = new Bundle();
                        artistBundle.putString("artistDetails",gson.toJson(artist));
                        artistFragment.setArguments(artistBundle);

                        Bundle venueBundle = new Bundle();
                        venueBundle.putString("venueDetails",gson.toJson(venue));
                        venueFragment.setArguments(venueBundle);


                        //Toast.makeText(this, eventList.toString(), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Toast.makeText(this, "JSON Error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(EventDetailsActivity.this, "error calling API", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(jsonObjectRequest);
    }

    private class EventDetailsPagerAdapter extends FragmentStateAdapter{

        public EventDetailsPagerAdapter(@NonNull @NotNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @NotNull
        @Override
        public Fragment createFragment(int position) {
            switch (position){
                case 0: return eventDetailsFragment;
                case 1: return artistFragment;
                case 2: return venueFragment;
                default: return eventDetailsFragment;
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

    private String cropText(String s, int cropLimit){
        return s.length() > cropLimit ? s.substring(0,s.substring(0,cropLimit+1).lastIndexOf(' ')!=-1?s.substring(0,cropLimit+1).lastIndexOf(' '):cropLimit+1) + "..." : s;
    }
}