package com.example.eventsearch;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.util.LinkifyCompat;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArtistFragment newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtistFragment extends Fragment {

    private TableLayout artistDetailTable;
    private TextView loading, noDetails;
    private String artistDetailString;
    private JsonObject artists;
    private Gson gson;

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gson = new Gson();

        artistDetailString = getArguments().getString("artistDetails");
        artists = gson.fromJson(artistDetailString,JsonObject.class);

        artistDetailTable = view.findViewById(R.id.artistDetailTable);
        loading = view.findViewById(R.id.artistLoading);
        noDetails = view.findViewById(R.id.noDetailsArtist);

        try {
            update(artists);
        } catch (JsonIOException | JSONException e) {
            Toast.makeText(getContext(), "JSON Error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artist, container, false);

        return view;
    }

    public void update(JsonObject artists) throws JSONException {
        loading.setText("");
        String[] keys = {"Name","Followers","Popularity","Check"};
        String[] headings = {"Name","Followers", "Popularity","Check At"};
        String[] artist = artists.getAsJsonObject("nameValuePairs").keySet().toArray(new String[0]);
        if(!artists.toString().equals("{}") && !artist[0].equals("")){
            noDetails.setVisibility(View.INVISIBLE);
            for(int i=0; i<artist.length; i++){
                JsonObject artistJson = artists.getAsJsonObject("nameValuePairs").getAsJsonObject(artist[i]).getAsJsonObject("nameValuePairs");
                if (artistJson.toString().equals("{}")){
                    TableRow tr = new TableRow(getActivity());
                    String value = artist[i] + ": No Details";
                    TextView head = new TextView(getActivity());
                    head.setText(value);
                    head.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                    head.setPaddingRelative(0,0,80,0);
                    head.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    tr.addView(head);
                    tr.setPaddingRelative(0,0,0,50);
                    artistDetailTable.addView(tr,new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));
                }
                else{
                    for (int ii = 0; ii < keys.length ; ii++) {
                        String heading = headings[ii];
                        String value = artistJson.get(keys[ii]).toString();
                        //Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
                        if(!value.equalsIgnoreCase("N/A") && !value.equalsIgnoreCase("")){
                            TableRow tr = new TableRow(getActivity());
                            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                            TextView head = new TextView(getActivity());
                            head.setText(heading);
                            head.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                            head.setPaddingRelative(0,0,80,0);
                            head.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                            tr.addView(head);
                            tr.setPaddingRelative(0,0,0,50);

                            TextView val = new TextView(getActivity());
                            if(keys[ii].equals("Check")){
                                String title = "Spotify";
                                String text = "<a href="+value+">"+ title + "</a>";
                                val.setText(Html.fromHtml(text));
                                val.setClickable(true);
                                val.setMovementMethod(LinkMovementMethod.getInstance());
                            }
                            else{
                                val.setText(heading=="Name"?artist[i]:value);
                            }
                            val.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);

                            tr.addView(val);

                            artistDetailTable.addView(tr,
                                    new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                            TableLayout.LayoutParams.WRAP_CONTENT));
                        }
                    }
                }
            }
        }
        else{
            noDetails.setVisibility(View.VISIBLE);
            //Display No Details Text View
        }
    }
}