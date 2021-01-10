package com.applex.icd_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private final String TOKEN_ENPOINT = "https://icdaccessmanagement.who.int/connect/token";
    private final String CLIENT_ID = "293e8de0-8e7b-459d-94f0-2b893a662fad_66d29d64-f45c-4321-b9a8-722a4b9fbf2d";
    private final String CLIENT_SECRET = "5/cImc/L3oJ7Fmi1icmNPGntnM96e9uUA06ILBltivw=";
    private final String SCOPE = "icdapi_access";
    private final String GRANT_TYPE = "client_credentials";
    private final String uri = "https://id.who.int/icd/release/10/2019/A00";
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.text);
        new API_execute().execute();
    }

    // get the OAUTH2 token
    private String getToken() throws Exception {

        System.out.println("Getting token...");

        URL url = new URL(TOKEN_ENPOINT);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("POST");

        // set parameters to post
        String urlParameters =
                "client_id=" + URLEncoder.encode(CLIENT_ID, "UTF-8") +
                "&client_secret=" + URLEncoder.encode(CLIENT_SECRET, "UTF-8") +
                "&scope=" + URLEncoder.encode(SCOPE, "UTF-8") +
                "&grant_type=" + URLEncoder.encode(GRANT_TYPE, "UTF-8");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        // response
        int responseCode = con.getResponseCode();
        System.out.println("Token Response Code : " + responseCode + "\n");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // parse JSON response
        JSONObject jsonObj = new JSONObject(response.toString());
        return jsonObj.getString("access_token");
    }


    // access ICD API
    private String getURI(String token) throws Exception {

        System.out.println("Getting URI...");

        URL url = new URL(uri);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        // HTTP header fields to set
        con.setRequestProperty("Authorization", "Bearer " + token);
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Accept-Language", "en");
        con.setRequestProperty("API-Version", "v2");

        // response
        int responseCode = con.getResponseCode();
        System.out.println("URI Response Code : " + responseCode + "\n");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public class API_execute extends AsyncTask<Void, Void, Void> {

        String text1;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                text1 = "URI Response JSON : \n" + getURI(getToken());
            } catch (Exception e) {
                Log.d("BAMCHIKI", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            text.setText(text1);
        }
    }
}