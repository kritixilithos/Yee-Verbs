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

/*
1) TODO: add support for future and conditional perfect tenses for french verbs (they are weird)
2) TODO: - add support for other languages' conjugation as well
 */

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner langSelector;
    String language = "fr";
    Button button;

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

        button = (Button) findViewById(R.id.get_stem);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getting the necessary stuff from the EditTexts and then feeding them to our AsyncTask
                String pronoun  = ((EditText) findViewById( R.id.pronoun)).getText().toString();
                String verb     = ((EditText) findViewById(    R.id.verb)).getText().toString();
                String tense    = ((EditText) findViewById(   R.id.tense)).getText().toString();
                new getVerb().execute(pronoun, verb, tense, language);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        String tempLanguage = String.valueOf(adapterView.getItemAtPosition(pos));
        switch (tempLanguage) {
            case "French":
                language = "fr";
                //the two lines for all the languages below determine whether we are in conjugate mode or in stem mode
                button = (Button) findViewById(R.id.get_stem);
                button.setText(getString(R.string.conjugate));
                break;
            case "Spanish":
                language = "es";
                button = (Button) findViewById(R.id.get_stem);
                button.setText(getString(R.string.get_stem));
                break;
            case "Italian":
                language = "it";
                button = (Button) findViewById(R.id.get_stem);
                button.setText(getString(R.string.get_stem));
                break;
            case "Portuguese":
                language = "pt";
                button = (Button) findViewById(R.id.get_stem);
                button.setText(getString(R.string.get_stem));
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
                System.out.println("Conjugating...");

                //easier way of understanding what comes out of args
                String stem="";
                String pronoun = args[0];
                String verb    = args[1];
                String tense   = args[2].replaceAll("\\s",""); //we don't need any spaces
                String language= args[3];

                //get the content of the website
                URL url = new URL("http://verbmaps.com/en/verb/"+language+"/" + verb);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                InputStream is =con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;

                //reading the content
                String content="";
                while ((line = br.readLine()) != null) {
                    content+=line;
                }
                br.close();

                //removing space because why not?
                content = content.replaceAll("\\s","");

                //getting the stem of the verb (if there isn' one we get an empty string)
                Pattern stemPattern = Pattern.compile("(?<=>)([^><]+)(?=<\\/span><\\/div><divclass=\"transform\">[^>]*>Add"+tense+")",Pattern.CASE_INSENSITIVE);
                Matcher stemMatcher = stemPattern.matcher(content);
                while (stemMatcher.find()) {
                    stem = stemMatcher.group() + "";
                }
                System.out.println(stem+"-");

                //give stem if language isn't French since other language are not supported yet
                if(!language.equalsIgnoreCase("fr")) return stem+"-";

                //getting the conjugation of the verb                                                     special cases for je and il/elle (TODO: add special cases for other languages as well)
                Pattern conjugationPattern = Pattern.compile("<strong>"+tense+"<\\/strong>((?!strong).)*"+pronoun.replaceAll("je","(?:je|j')").replaceAll("(il|on|elle)(s?)","il$2/elle$2")+stem+"<spanclass=\"highlight\">([^<]+)<\\/span>([^<]*)<\\/div>",Pattern.CASE_INSENSITIVE);
                Matcher conjugationMatcher = conjugationPattern.matcher(content);
                while (conjugationMatcher.find()) {
                    System.out.println(conjugationMatcher.group());
                    String conjugated = pronoun + " " +  stem + conjugationMatcher.group(2) + " " + conjugationMatcher.group(3);
                    System.out.println(conjugated);
                    //                              je    [vowel] -> j'
                    return (conjugated.replaceAll("^je *([aeiouh])","j' $1"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error 404";
            }
            return "Not found";
        }

        //set the TextView's content to the stem/conjugation
        @Override
        protected void onPostExecute(String result) {
            TextView stem = (TextView) findViewById(R.id.stem);
            stem.setText(result);
        }
    }
}
