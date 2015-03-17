package com.hexafarm.quizmongo2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements Serializable, Button.OnClickListener {


    public static String TAG = "quiz";
    public static String FOO = "foo";

    public final static String QUESTIONS_JSON = "com.hexafarm.quizmongo2.MESSAGE";


    private String username = "";
    private String password = "";

    private EditText usernameView;
    private EditText passwordView;

    private TextView warning;

    private Button login_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        warning = (TextView) findViewById(R.id.warning);
        warning.setText("");

        login_button = (Button) findViewById(R.id.login_button);

        this.init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        username = usernameView.getText().toString();
        password = passwordView.getText().toString();

        new DownLoadHXFjson().execute(new String[] {getResources().getString(R.string.ws)});

        login_button.setClickable(false);
    }

    private class DownLoadHXFjson extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... wsUrl) {

            String myResponse = "";

            try {
                // http://stackoverflow.com/questions/9767952/how-to-add-parameters-to-httpurlconnection-using-post
                URL url = new URL(wsUrl[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                List<NameValuePair> params = new ArrayList<>();

                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

                int statusCode = conn.getResponseCode();

                Log.i(TAG, "status code : " + statusCode);

                if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {

                    myResponse = "unauthorized";

                } else {

                    InputStream content = conn.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";

                    while((s = buffer.readLine()) != null) {
                        myResponse += s;
                    }

                    Log.i(MainActivity.TAG, "json : " + myResponse);
                }

            } catch (MalformedURLException e) {

                Log.i(MainActivity.TAG, "MalformedURLException");

            } catch (ProtocolException e) {

                Log.i(MainActivity.TAG, "ProtocolException");

            } catch (IOException e) {

                Log.i(MainActivity.TAG, "IOException");

            }

            return myResponse;
        }

        @Override
        protected void onPostExecute(String result) {

            if(result.equals("unauthorized")) {
                warning.setText("Unauthorized");
            } else {
                warning.setText("");

                Intent questionIntent = new Intent(MainActivity.this, QuestionsScreen.class);

                //Bundle bundle = new Bundle();
                //bundle.putSerializable(MainActivity.EXTRA_MESSAGE, new QuestionsService(result));

                questionIntent.putExtra(QUESTIONS_JSON, result);
                //new QuestionsService(result);
                //questionIntent.putExtra(EXTRA_MESSAGE, new QuestionsService(result));

                //questionIntent.putExtras(bundle);

                startActivity(questionIntent);
            }
        }

        private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
        {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (NameValuePair pair : params)
            {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }

            return result.toString();
        }

    }

    private void init() {

        Log.i(TAG, "init()");

        final Button login_button = (Button) findViewById(R.id.login_button);

        usernameView = ((EditText) findViewById(R.id.username));

        passwordView = ((EditText) findViewById(R.id.password));


        usernameView.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        warning.setText("");
                        return false;
                    }
                }
        );

        passwordView.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        warning.setText("");
                        return false;
                    }
                }
        );

        login_button.setOnClickListener(this);
    }


}
