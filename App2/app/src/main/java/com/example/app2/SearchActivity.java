package com.example.app2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchActivity extends AppCompatActivity {

    public String message;
    EditText from, to, departureDate, returnDate, numberOfPassengers;
    Button searchFlights;
    private LocationRequest locationRequest;

    String[] cities = {"Amsterdam, Netherlands", "Athens, Greece", "Barcelona, Spain", "Berlin, Germany", "Brussels, Belgium", "Budapest, Hungary", "Copenhagen, Denmark", "Dublin, Ireland", "Edinburgh, England, United Kingdom", "Florence, Italy", "Frankfurt, Germany", "Geneva, Switzerland", "Helsinki, Finland", "Istanbul, Turkey", "Krakow, Poland", "Lisbon, Portugal", "London, England, United Kingdom", "Madrid, Spain", "Manchester, England, United Kingdom", "Milan, Italy", "Moscow, Russia", "Munich, Germany", "Naples, Italy", "Oslo, Norway", "Paris, France", "Prague, Czech Republic", "Reykjavik, Iceland", "Rome, Italy", "Saint Petersburg, Russia", "Stockholm, Sweden", "Vienna, Austria", "Warsaw, Poland", "Zagreb, Croatia", "Amalfi, Italy", "Antwerp, Belgium", "Belfast, England, United Kingdom", "Bergen, Norway", "Bucharest, Romania", "Cologne, Germany", "Dresden, Germany", "Glasgow, England, United Kingdom", "Hamburg, Germany", "Ljubljana, Slovenia", "Luxembourg City, Luxembourg", "Marseille, France", "Oxford, England, United Kingdom", "Rotterdam, Netherlands", "Salzburg, Austria", "Seville, Spain", "Valencia, Spain", "Venice, Italy", "Catania, Sicily ,Italy", "Palermo, Sicily, Italy", "Bangkok, Thailand", "Beijing, China", "Dubai, United Arab Emirates", "Hong Kong, China", "Jakarta, Indonesia", "Kuala Lumpur, Malaysia", "Kyoto, Japan", "Manila, Philippines", "Mumbai, India", "New Delhi, India", "Osaka, Japan", "Phuket, Thailand", "Seoul, South Korea", "Shanghai, China", "Singapore, Singapore", "Sydney, Australia", "Taipei, Taiwan", "Tokyo, Japan", "Abu Dhabi, United Arab Emirates", "Almaty, Kazakhstan", "Amman, Jordan", "Astana, Kazakhstan", "Baghdad, Iraq", "Baku, Azerbaijan", "Bandar Seri Begawan, Brunei", "Colombo, Sri Lanka", "Dhaka, Bangladesh", "Dushanbe, Tajikistan", "Hanoi, Vietnam", "Islamabad, Pakistan", "Eilat, Israel", "Kabul, Afghanistan", "Kathmandu, Nepal", "Kolkata, India", "Kuwait City, Kuwait", "Male, Maldives", "Muscat, Oman", "Nur-Sultan, Kazakhstan", "Pyongyang, North Korea", "Riyadh, Saudi Arabia", "Tashkent, Uzbekistan", "Tel Aviv, Israel", "Thimphu, Bhutan", "Ulaanbaatar, Mongolia", "Vientiane, Laos", "Yangon, Myanmar", "Yerevan, Armenia", "Atlanta, Georgia, United States", "Boston, Massachusetts, United States", "Calgary, Canada", "Chicago, Illinois, United States", "Dallas, Texas, United States", "Denver, Colorado, United States", "Detroit, Michigan, United States", "Edmonton, Canada", "Guadalajara, Mexico", "Halifax, Canada", "Havana, Cuba", "Houston, Texas, United States", "Las Vegas, Nevada, United States", "Los Angeles, California, United States", "Mexico City, Mexico", "Miami, Florida, United States", "Montreal, Canada", "Nashville, Tennessee, United States", "New Orleans, Louisiana, United States", "New York City, New York, United States", "Ottawa, Canada", "Philadelphia, Pennsylvania, United States", "Phoenix, Arizona, United States", "Portland, Oregon, United States", "Quebec City, Canada", "San Diego, California, United States", "San Francisco, California, United States", "San Jose, California, United States", "Santa Fe, New Mexico, United States", "Seattle, Washington, United States", "Toronto, Canada", "Vancouver, Canada", "Washington D.C., United States", "Anchorage, Alaska, United States", "Aspen, Colorado, United States", "Cancun, Mexico", "Charleston, South Carolina, United States", "Honolulu, Hawaii, United States", "Jackson, Wyoming, United States", "Kahului, Hawaii, United States", "Kingston, Jamaica", "Mazatlan, Mexico", "Monterrey, Mexico", "Nassau, Bahamas", "Puerto Vallarta, Mexico", "San Juan, Puerto Rico", "Santo Domingo, Dominican Republic", "St. John's, Canada", "Tijuana, Mexico", "Belo Horizonte, Brazil", "Bogotá, Colombia", "Buenos Aires, Argentina", "Caracas, Venezuela", "Córdoba, Argentina", "Curitiba, Brazil", "Fortaleza, Brazil", "Guayaquil, Ecuador", "La Paz, Bolivia", "Lima, Peru", "Manaus, Brazil", "Medellín, Colombia", "Montevideo, Uruguay", "Porto Alegre, Brazil", "Port-au-Prince, Haiti", "Quito, Ecuador", "Recife, Brazil", "Rio de Janeiro, Brazil", "Salvador, Brazil", "Santiago, Chile", "São Paulo, Brazil", "Asunción, Paraguay", "Brasília, Brazil", "Cali, Colombia", "Campos do Jordão, Brazil", "Cartagena, Colombia", "Cusco, Peru", "Foz do Iguaçu, Brazil", "Goiania, Brazil", "Jujuy, Argentina", "Mendoza, Argentina", "Natal, Brazil", "Pucón, Chile", "Puerto Iguazú, Argentina", "Punta Arenas, Chile", "Punta del Este, Uruguay", "Santa Cruz de la Sierra, Bolivia", "São Luís, Brazil", "Teresina, Brazil", "Ushuaia, Argentina", "Valdivia, Chile", "Valparaíso, Chile", "Vitoria, Brazil", "Volcán Arenal, Costa Rica", "Zapallar, Chile", "Abidjan, Ivory Coast", "Accra, Ghana", "Addis Ababa, Ethiopia", "Algiers, Algeria", "Asmara, Eritrea", "Bamako, Mali", "Cairo, Egypt", "Cape Town, South Africa", "Casablanca, Morocco", "Conakry, Guinea", "Dakar, Senegal", "Dar es Salaam, Tanzania", "Djibouti City, Djibouti", "Douala, Cameroon", "Durban, South Africa", "Entebbe, Uganda", "Harare, Zimbabwe", "Johannesburg, South Africa", "Kampala, Uganda", "Kano, Nigeria", "Khartoum, Sudan", "Kinshasa, Congo", "Lagos, Nigeria", "Libreville, Gabon", "Lilongwe, Malawi", "Luanda, Angola", "Lusaka, Zambia", "Marrakesh, Morocco", "Mogadishu, Somalia", "Nairobi, Kenya", "Ndjamena, Chad", "Ouagadougou, Burkina Faso", "Port Elizabeth, South Africa", "Port Harcourt, Nigeria", "Pretoria, South Africa", "Rabat, Morocco", "Tunis, Tunisia", "Victoria Falls, Zimbabwe", "Windhoek, Namibia", "Yaoundé, Cameroon", "Zanzibar City, Tanzania", "Adelaide, Australia", "Auckland, New Zealand", "Brisbane, Australia", "Cairns, Australia", "Canberra, Australia", "Christchurch, New Zealand", "Darwin, Australia", "Dunedin, New Zealand", "Gold Coast, Australia", "Hobart, Australia", "Melbourne, Australia", "Nadi, Fiji", "Noumea, New Caledonia", "Pago Pago, American Samoa", "Perth, Australia", "Port Moresby, Papua New Guinea", "Port Vila, Vanuatu", "Queenstown, New Zealand", "Rarotonga, Cook Islands", "Tahiti, French Polynesia", "Tauranga, New Zealand", "Townsville, Australia", "Wellington, New Zealand", "Apia, Samoa", "Avarua, Cook Islands", "Bora Bora, French Polynesia", "Broome, Australia", "Coffs Harbour, Australia", "Dili, Timor-Leste", "Fiji, Fiji", "Gisborne, New Zealand", "Hamilton Island, Australia", "Hervey Bay, Australia", "Kiritimati, Kiribati", "Koror, Palau", "Lihue, Hawaii, United States", "Majuro, Marshall Islands", "Nouméa, New Caledonia", "Nuku alofa, Tonga", "Papeete, French Polynesia", "Rabaul, Papua New Guinea", "Rockhampton, Australia", "Rotorua, New Zealand", "Suva, Fiji", "Tonga, Tonga", "Vanuatu, Vanuatu"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ArrayAdapter<String> adapterCities = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, cities);
        AutoCompleteTextView textViewCities = (AutoCompleteTextView)findViewById(R.id.fromLocationSearch);
        textViewCities.setThreshold(0);
        textViewCities.setAdapter(adapterCities);

        ArrayAdapter<String> adapterCities2 = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, cities);
        AutoCompleteTextView textViewCities2 = (AutoCompleteTextView)findViewById(R.id.toLocationSearch);
        textViewCities2.setThreshold(0);
        textViewCities2.setAdapter(adapterCities2);

        from = findViewById(R.id.fromLocationSearch);
        to = findViewById(R.id.toLocationSearch);
        departureDate = findViewById(R.id.departureDateSearch);
        returnDate = findViewById(R.id.returnDateSeach);
        numberOfPassengers = findViewById(R.id.passengersNumberSearch);
        searchFlights = findViewById(R.id.continueButtonSearch);

        departureDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialogDep();
            }
        });

        returnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialogRet();
            }
        });

        searchFlights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fromText = from.getText().toString().trim().replaceAll("\\s", "");
                String toText = to.getText().toString().trim().replaceAll("\\s", "");
                String depDateText = departureDate.getText().toString().trim();
                String retDateText = returnDate.getText().toString().trim();
                String numberPassengersText = numberOfPassengers.getText().toString().trim();

                if (validateSearch(fromText, toText, depDateText, retDateText, numberPassengersText)) {
                    message = fromText + " " + toText + " " + depDateText + " " + retDateText + " " + numberPassengersText + " ";
                    System.out.println("Yay!");
                    Intent intent = new Intent(SearchActivity.this, FlightsActivity.class);
                    intent.putExtra("MESSAGE", message);
                    startActivity(intent);
                }
            }
        });
    }

    public boolean validateSearch(String fromS, String toS, String depS, String retS, String passengersS) {
        if (fromS.isEmpty()) {
            from.setError("Cannot be empty");
            return false;
        }
        if (toS.isEmpty()) {
            to.setError("Cannot be empty");
            return false;
        }
        if (depS.isEmpty()) {
            departureDate.setError("Cannot be empty");
            return false;
        }
        if (retS.isEmpty()) {
            returnDate.setError("Cannot be empty");
            return false;
        }
        if (passengersS.isEmpty()) {
            numberOfPassengers.setError("Cannot be empty");
            return false;
        }
        if (fromS.equals(toS)) {
            to.setError("Return destination must be different than Departure destination");
            return false;
        }

        String regex = "^([0-9]{4})-(1[0-2]|0[1-9])-(3[01]|[12][0-9]|0[1-9])$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher1 = pattern.matcher(depS);

        if (!matcher1.matches()) {
            departureDate.setError("Departure date must be YYYY-MM-DD");
            return false;
        }

        Matcher matcher2 = pattern.matcher(retS);

        if (!matcher2.matches()) {
            returnDate.setError("Return date must be YYYY-MM-DD");
            return false;
        }


        boolean contains_from = false, contains_to = false;
        for (String city : cities) {
            if (city.replaceAll("\\s", "").equals(fromS))
                contains_from = true;
            if (city.replaceAll("\\s", "").equals(toS))
                contains_to = true;
        }

        if (!contains_from) {
            from.setError("City must be chosen from list");
            return false;
        }

        if (!contains_to) {
            to.setError("City must be chosen from list");
            return false;
        }


        // year-month-day
        String[] dep = depS.split("-");
        String[] ret = retS.split("-");
        int[] depInt = new int[dep.length];
        int[] retInt = new int[ret.length];

        for(int i = 0; i < dep.length; i++) {
            depInt[i] = Integer.parseInt(dep[i]);
            retInt[i] = Integer.parseInt(ret[i]);

            System.out.println(depInt[i]);
            System.out.println(retInt[i]);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String d1 = dateFormat.format(date);

        System.out.println("D1: " + d1);
        System.out.println("D2: " + depS);
        System.out.println("D3: " + retS);

//        int year1 = depInt[0];
//        int month1 = depInt[1]; // 5
//        int day1 = depInt[2];
//
//        int year2 = retInt[0];
//        int month2 = retInt[1]; // 4
//        int day2 = retInt[2];
//
//        if (year2 < year1) {
//            returnDate.setError("Invalid date");
//            return false;
//        }
//        if (month2 < month1) {
//            returnDate.setError("Invalid date");
//            return false;
//        }
//        if (day2 < day1) {
//            returnDate.setError("Invalid date");
//            return false;
//        }

        Date date1 = null;
        Date date2 = null;
        Date date3 = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date1 = sdf.parse(d1);  // now
            date2 = sdf.parse(depS);  // departure
            date3 = sdf.parse(retS);  // return
        } catch (ParseException e) {
            System.out.println("exception");
        }

        if (date1.compareTo(date2) > 0) {
            departureDate.setError("Invalid date");
            return false;
        }

        if (date3.compareTo(date2) <= 0) {
            returnDate.setError("Invalid date");
            return false;
        }
        else {
            return true;
        }
    }


    private void showDatePickerDialogDep() {
        DatePickerDialog departureD = new DatePickerDialog(
                this, new InnerFirstDate(),
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        departureD.show();
    }

    private void showDatePickerDialogRet() {
        DatePickerDialog returnD = new DatePickerDialog(
                this, new InnerSecondDate(),
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        returnD.show();
    }


    public class InnerFirstDate implements DatePickerDialog.OnDateSetListener{
        @Override
        public void onDateSet(DatePicker view, int i2, int i1, int i) {
            String i_text = Integer.toString(i);
            String i1_text = Integer.toString(i1+1);
            if (i_text.length() == 1)
                i_text = "0" + i_text;
            if (i1_text.length() == 1)
                i1_text = "0" + i1_text;

            String date = i2 + "-" + i1_text + "-" + i_text;
            departureDate.setText(date);
        }

    }

    public class InnerSecondDate implements DatePickerDialog.OnDateSetListener{
        @Override
        public void onDateSet(DatePicker view, int i2, int i1, int i) {
            String i_text = Integer.toString(i);
            String i1_text = Integer.toString(i1+1);
            if (i_text.length() == 1)
                i_text = "0" + i_text;
            if (i1_text.length() == 1)
                i1_text = "0" + i1_text;

            String date = i2 + "-" + i1_text + "-" + i_text;
            returnDate.setText(date);
        }
    }
}