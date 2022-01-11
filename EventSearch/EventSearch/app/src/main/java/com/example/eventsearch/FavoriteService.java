package com.example.eventsearch;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class FavoriteService {
    public ArrayList<Event> favorites;
    SharedPreferences sharedPreferences;
    Context context;
    SharedPreferences.Editor editor;
    Gson gson;

    public FavoriteService(Context context){
        this.sharedPreferences = context.getSharedPreferences("myPref",context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
        this.context = context;
        String defaultSet = "[]";
        gson = new Gson();
        this.favorites = gson.fromJson(this.sharedPreferences.getString("favorites",defaultSet),
                new TypeToken<ArrayList<Event>>(){}.getType());
    }

    public void updateSet(){
        String defaultSet = "[]";
        this.favorites = gson.fromJson(this.sharedPreferences.getString("favorites",defaultSet),
                new TypeToken<ArrayList<Event>>(){}.getType());
    }

    public void updateSharedPreferences(){
        editor.putString("favorites",gson.toJson(this.favorites));
        editor.commit();
    }

    public void push(Event event){
        if(!isPresent(event)){
            this.favorites.add(event);
        }
        updateSharedPreferences();
        //Toast.makeText(context, this.favorites.toString(), Toast.LENGTH_SHORT).show();
    }

    public void remove(Event event){
        if(!this.favorites.isEmpty() && isPresent(event)){
            this.favorites.remove(event);
        }
        updateSharedPreferences();
        //Toast.makeText(context, this.favorites.toString(), Toast.LENGTH_SHORT).show();
    }

    public boolean isPresent(Event event){
        if(this.favorites.contains(event)){
            return true;
        }
        else{
            return false;
        }
    }

    public ArrayList<Event> getFavoritesArray(){
        updateSet();
        return this.favorites;
    }
}
