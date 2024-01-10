package com.example.app2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class FlightsActivity extends AppCompatActivity {

    public String message;
    EditText stops, cabin, price;
    Button searchFlights;

    String[] numberOfStops = {"0", "1", "2+"};
    String[] cabinTypes = {"Economy", "Premium Economy", "Business", "First"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flights);

        ArrayAdapter<String> adapterStops = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, numberOfStops);
        AutoCompleteTextView textViewStops = (AutoCompleteTextView)findViewById(R.id.numberOfStopsFlights);
        textViewStops.setThreshold(0);
        textViewStops.setAdapter(adapterStops);

        ArrayAdapter<String> adapterCabin = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, cabinTypes);
        AutoCompleteTextView textViewCabin = (AutoCompleteTextView)findViewById(R.id.cabinFlights);
        textViewCabin.setThreshold(0);
        textViewCabin.setAdapter(adapterCabin);

        stops = findViewById(R.id.numberOfStopsFlights);
        cabin = findViewById(R.id.cabinFlights);
        price = findViewById(R.id.priceFlights);
        searchFlights = findViewById(R.id.continueButtonFlights);

        searchFlights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numberOfStops = stops.getText().toString().trim();
                String cabinFlights = cabin.getText().toString().trim();
                String priceFlights = price.getText().toString().trim();

                if (validateFlights(numberOfStops, cabinFlights)) {
                    Intent intent = getIntent();
                    message = intent.getStringExtra("MESSAGE");
                    message += numberOfStops + " " + cabinFlights + " " + priceFlights + " ";
                    Intent intent_2 = new Intent(FlightsActivity.this, HotelsActivity.class);
                    intent_2.putExtra("MESSAGE2", message);
                    startActivity(intent_2);
                    System.out.println("Yay!");
                }
            }
        });
    }

    public boolean validateFlights(String number_stops, String type_cabin) {

        if (!number_stops.isEmpty()) {
            boolean contains_stops = false;
            for (String stop : numberOfStops) {
                if (stop.equals(number_stops)) {
                    contains_stops = true;
                    break;
                }
            }

            if (!contains_stops) {
                stops.setError("Stops must be chosen from list");
                return false;
            }
        }

        if (!type_cabin.isEmpty()) {
            boolean contains_cabin = false;
            for (String cabin : cabinTypes) {
                if (cabin.equals(type_cabin)) {
                    contains_cabin = true;
                    break;
                }
            }

            if (!contains_cabin) {
                cabin.setError("Cabin must be chosen from list");
                return false;
            }
        }

        return true;
    }
}