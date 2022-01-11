package com.example.eventsearch;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.max;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private static final int VIEW_TYPE_EVENT = 0;
    private static final int VIEW_TYPE_EMPTY = 1;

    private ArrayList<Event> events;
    private Context context;
    private FavoriteService favoriteService;
    private boolean isFavoriteArray;

    public EventAdapter(ArrayList<Event> events, Context context, boolean isFavoriteArray){
        this.events = events;
        this.context = context;
        this.favoriteService = new FavoriteService(context);
        this.isFavoriteArray = isFavoriteArray;
    }

    @Override
    public int getItemViewType(int position) {
        if(events.size()==0){
            return VIEW_TYPE_EMPTY;
        }
        else{
            return VIEW_TYPE_EVENT;
        }
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view;
        viewType=getItemViewType(0);
        if(viewType==VIEW_TYPE_EVENT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item,parent,false);
        }
        else if (viewType==VIEW_TYPE_EMPTY && isFavoriteArray){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_list_favorite,parent,false);
        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_list_search,parent,false);
        }

        return new ViewHolder(view);
    }

    private String cropText(String s){
        return s.length() > 35 ? s.substring(0,s.substring(0,36).lastIndexOf(' ')!=-1?s.substring(0,36).lastIndexOf(' '):36) + "..." : s;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull EventAdapter.ViewHolder holder, int position) {
        if(getItemViewType(0)==VIEW_TYPE_EVENT){
            Event event = events.get(position);
            holder.eventName.setText(cropText(event.event));
            holder.venueName.setText(event.venue);
            holder.date.setText(event.date);
            holder.categoryIcon.setImageResource(getDrawable(event.category));
            holder.likeButton.setImageResource(getLikeButtonResource(event));//placeholder
            holder.likeButton.setOnClickListener(v -> {
                if(favoriteService.isPresent(event)){
                    holder.likeButton.setImageResource(R.drawable.heart_outline_black);
                    if(isFavoriteArray){
                        favoriteService.remove(event);
                        events.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, events.size());
                    }
                    else{
                        favoriteService.remove(event);
                    }
                }
                else{
                    favoriteService.push(event);
                    holder.likeButton.setImageResource(R.drawable.heart_fill_red);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,EventDetailsActivity.class);
                    intent.putExtra("id",event.id);
                    intent.putExtra("name",event.event);
                    intent.putExtra("isFavorite",favoriteService.isPresent(event));
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return max(events.size(),1);
    }

    public int getLikeButtonResource(Event event){
        if(favoriteService.isPresent(event)){
            return R.drawable.heart_fill_red;
        } //WHEN FAVORITE IS SELECTED
        else{
            return R.drawable.heart_outline_black;
        }
    }

    public int getDrawable(String category){
        ArrayList<String> categoryList = new ArrayList<>(Arrays.asList(category.split("\\s\\|\\s")));
        if(categoryList.contains("Arts & Theatre") || categoryList.contains("Arts") || categoryList.contains("Theatre")){
            return R.drawable.art_icon;
        }
        if(categoryList.contains("Music")){
            return R.drawable.music_icon;
        }
        if(categoryList.contains("Film")){
            return R.drawable.film_icon;
        }
        if(categoryList.contains("Sports")){
            return R.drawable.ic_sport_icon;
        }
        return R.drawable.miscellaneous_icon;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView eventName, venueName, date;
        private ImageButton likeButton;
        private ImageView categoryIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.emptyRecords);
            venueName = itemView.findViewById(R.id.venueName);
            date = itemView.findViewById(R.id.date);
            likeButton = itemView.findViewById(R.id.likeButton);
            categoryIcon = itemView.findViewById(R.id.categoryIcon);
        }
    }
}
