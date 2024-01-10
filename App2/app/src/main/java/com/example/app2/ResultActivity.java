package com.example.app2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import kotlinx.coroutines.GlobalScope;

public class ResultActivity extends AppCompatActivity {

    Button flightsLink, hotelsLink, attrLink;
    TextView attrText;
    String data = "";
    String linkFlights = "";
    String linkHotels = "";
    String linkAttractions = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        flightsLink = findViewById(R.id.flightsLinkButton);
        hotelsLink = findViewById(R.id.hotelsLinkButton);
        attrLink = findViewById(R.id.attractionsLinkButton);
        attrText = findViewById(R.id.attractionsLinkTextView);

        Get get = new Get();
        get.execute();


        flightsLink.setOnClickListener(new View.OnClickListener() {
            // button clicked - opens flights url
            @Override
            public void onClick(View view) {
                Intent urlIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(linkFlights)
                );
                startActivity(urlIntent);
            }
        });

        hotelsLink.setOnClickListener(new View.OnClickListener() {
            // button clicked - opened hotels url
            @Override
            public void onClick(View view) {
                Intent urlIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(linkHotels)
                );
                startActivity(urlIntent);
            }
        });


        attrLink.setOnClickListener(new View.OnClickListener() {
            // button clicked - opened attractions url
            @Override
            public void onClick(View view) {
                Intent urlIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(linkAttractions)
                );
                startActivity(urlIntent);
            }
        });

        System.out.println("Flights url: " + linkFlights);
        System.out.println("Hotels url: " + linkHotels);
        System.out.println("Attractions url: " + linkAttractions);

    }

    public String readData(String url) {
        String text = "";
        try {
            InputStream is = getAssets().open(url);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return text;
    }


    public class Get extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Create a socket connection to the Python server
                Socket socket = new Socket("172.20.10.2", 1234);

                // Read the incoming data from the socket
                InputStream in = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int bytesRead = in.read(buffer);
                String line = new String(buffer, 0, bytesRead);
                System.out.println("Received data: " + line.trim());
                data = line.trim();
                String[] links = data.split(" ");
                linkFlights = links[0];
                linkHotels = links[1];
                linkAttractions = links[2];

                // Close the socket when finished
                socket.close();
            } catch (IOException e) {
                // Handle any exceptions that may occur
                e.printStackTrace();
            }
            return null;
        }
    }
}