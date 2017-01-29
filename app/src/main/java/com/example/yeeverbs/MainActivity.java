package com.example.yeeverbs;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;


import org.apache.http.impl.client.DefaultHttpClient;

import java.net.*;
import java.io.*;
import java.util.regex.*;
import java.lang.*;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner langSelector;
    String language = "fr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        langSelector = (Spinner) findViewById(R.id.lang_selector);

        SpinnerAdapter adap = new ArrayAdapter<String>(this, R.layout.langselector_layout,
                new String[]{"French","Spanish","Italian","Portuguese"});

        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        //       R.array.languages, android.R.layout.simple_spinner_item);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSelector.setAdapter(adap);
        langSelector.setOnItemSelectedListener(this);

        Button button = (Button) findViewById(R.id.get_stem);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String verb  = ((EditText) findViewById( R.id.verb)).getText().toString();
                String tense = ((EditText) findViewById(R.id.tense)).getText().toString();
                new getVerb().execute(verb, tense, language);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        String tempLanguage = String.valueOf(adapterView.getItemAtPosition(pos));
        switch (tempLanguage) {
            case "French":
                language = "fr";
                break;
            case "Spanish":
                language = "es";
                break;
            case "Italian":
                language = "it";
                break;
            case "Portuguese":
                language = "pt";
                break;
        }
        System.out.println(language);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class getVerb extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... args) {

            try {
                System.out.println("STARTED");
                URL url = new URL("http://verbmaps.com/en/verb/"+args[2]+"/" + args[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                InputStream is =con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;

                String content="";
                while ((line = br.readLine()) != null) {
                    content+=line;
                }
                br.close();

                content = content.replaceAll("\\s","");
                Pattern p = Pattern.compile("(?<=>)([^><]+)(?=<\\/span><\\/div><divclass=\"transform\">[^>]*>Add"+args[1]+")",Pattern.CASE_INSENSITIVE);
                Matcher matcher = p.matcher(content);
                while (matcher.find()) {
                    System.out.println(matcher.group());
                    return matcher.group()+"-";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error 404";
            }
            return "Error 404";
        }

        @Override
        protected void onPostExecute(String result) {
            TextView stem = (TextView) findViewById(R.id.stem);
            stem.setText(result);
        }
    }
}
