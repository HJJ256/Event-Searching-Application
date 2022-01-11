package com.example.eventsearch;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventDetailsFragment newInstance} factory method to
 * create an instance of this fragment.
 */

public class EventDetailsFragment extends Fragment {

    private TextView loading, noDetails;
    private TableLayout eventDetailTable;

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loading = view.findViewById(R.id.loadingEventDetails);
        noDetails = view.findViewById(R.id.noDetailsEvent);
        eventDetailTable = view.findViewById(R.id.eventDetailTable);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_details, container, false);
    }

    public void update(JSONObject event) throws JSONException {
        loading.setText("");
        String[] keys = {"Artist","Venue","Date","Category","PriceRange","Status","BTA","SeatMap"};
        String[] headings = {"Artist/Team(s)","Venue", "Date","Category","Price Range","Ticket Status", "Buy Ticket At","Seat Map"};
        if(!event.toString().equals("{}")){
            noDetails.setVisibility(View.INVISIBLE);
            for (int i = 0; i < keys.length ; i++) {
                String heading = headings[i];
                String value = event.getString(keys[i]);
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
                    if(keys[i].equals("BTA") || keys[i].equals("SeatMap")){
                        String title = keys[i].equals("BTA")?"Ticketmaster":"View Seat Map Here";
                        String text = "<a href='"+value+"' style=\"text-decoration: none; color: blue;\">"+ title + "</a>";
                        //Toast.makeText(getContext(), value, Toast.LENGTH_SHORT).show();
                        val.setText(Html.fromHtml(text));
                        val.setClickable(true);
                        val.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                    else{
                        val.setText(value);
                    }
                    val.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);

                    tr.addView(val);

                    eventDetailTable.addView(tr,
                            new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                    TableLayout.LayoutParams.WRAP_CONTENT));
                }
            }
        }
        else {
            noDetails.setVisibility(View.VISIBLE);
        }
    }
}