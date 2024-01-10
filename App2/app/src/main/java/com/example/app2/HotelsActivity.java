package com.example.app2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class HotelsActivity extends AppCompatActivity {

    public String message;
    EditText rooms, adults, children, stars, rating, price;
    Button searchHotels;

    String[] minimumStars = {"2", "3", "4", "5"};
    String[] minimumRating = {"6", "7", "8", "9"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotels);

        ArrayAdapter<String> adapterStars = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, minimumStars);
        AutoCompleteTextView textViewStars = (AutoCompleteTextView)findViewById(R.id.numberOfStarsHotels);
        textViewStars.setThreshold(0);
        textViewStars.setAdapter(adapterStars);

        ArrayAdapter<String> adapterRating = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, minimumRating);
        AutoCompleteTextView textViewRating = (AutoCompleteTextView)findViewById(R.id.ratingHotels);
        textViewRating.setThreshold(0);
        textViewRating.setAdapter(adapterRating);

        rooms = findViewById(R.id.numberOfRoomsHotels);
        adults = findViewById(R.id.numberOfAdultsHotels);
        children = findViewById(R.id.numberOfChildrenHotels);
        stars = findViewById(R.id.numberOfStarsHotels);
        rating = findViewById(R.id.ratingHotels);
        price = findViewById(R.id.priceHotels);
        searchHotels = findViewById(R.id.searchButtonHotels);

        searchHotels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Send send = new Send();
                String roomsHotels = rooms.getText().toString().trim();
                String adultsHotels = adults.getText().toString().trim();
                String childrenHotels = children.getText().toString().trim();
                String starsHotels = stars.getText().toString().trim();
                String ratingHotels = rating.getText().toString().trim();
                String priceHotels = price.getText().toString().trim();

                if (validateHotels(starsHotels, ratingHotels)) {
                    Intent intent = getIntent();
                    message = intent.getStringExtra("MESSAGE2");
                    message += roomsHotels + " " + adultsHotels + " " + childrenHotels + " " + starsHotels + " " + ratingHotels + " " + priceHotels;
                    System.out.println("message: " + message);
                    send.execute();
                    Intent intent2 = new Intent(HotelsActivity.this, ResultActivity.class);
                    startActivity(intent2);
                }
            }
        });
    }

    public boolean validateHotels(String number_stars, String number_rating) {
        if (!number_stars.isEmpty()) {
            boolean contains_stars = false;
            for (String star : minimumStars) {
                if (star.equals(number_stars)) {
                    contains_stars = true;
                    break;
                }
            }

            if (!contains_stars) {
                stars.setError("Stars must be chosen from list");
                return false;
            }
        }

        if (!number_rating.isEmpty()) {
            boolean contains_rating = false;
            for (String rating : minimumRating) {
                if (rating.equals(number_rating)) {
                    contains_rating = true;
                    break;
                }
            }

            if (!contains_rating) {
                rating.setError("Rating must be chosen from list");
                return false;
            }
        }

        return true;
    }

    public class Send extends AsyncTask<Void,Void,Void> {
        Socket s;
        PrintWriter pw;
        protected Void doInBackground(Void...params) {
            try {
                s = new Socket("172.20.10.2", 8000);
                pw = new PrintWriter(s.getOutputStream());
                pw.write(message);
                pw.flush();
                pw.close();
                s.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}