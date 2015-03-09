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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements Serializable {


    public static String TAG = "quiz";

    public final static String EXTRA_MESSAGE = "com.hexafarm.quizmongo2.MESSAGE";


    private String username = "";
    private String password = "";

    private EditText usernameView;
    private EditText passwordView;

    private TextView warning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        warning = (TextView) findViewById(R.id.warning);
        warning.setText("");

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

    private class DownLoadHXFjson extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... wsUrl) {

            String myResponse = "";

            HttpClient httpClient = new DefaultHttpClient();

            HttpPost request = new HttpPost(wsUrl[0]);

            List<NameValuePair> params = new ArrayList<>();

            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));


            try {
                request.setEntity(new UrlEncodedFormEntity(params));

            } catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }

            try {
                HttpResponse response = httpClient.execute(request);

                int statusCode = response.getStatusLine().getStatusCode();

                Log.i(TAG, "status code : " + statusCode);

                if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {

                    myResponse = "unauthorized";

                } else {

                    InputStream content = response.getEntity().getContent();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";

                    while((s = buffer.readLine()) != null) {
                        myResponse += s;
                    }
                }

            } catch (ClientProtocolException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();
            }

            return myResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            //TextView tf = (TextView) findViewById(R.id.main_tf);
            //tf.setText(result);

            if(result.equals("unauthorized")) {
                warning.setText("Unauthorized");
            } else {
                warning.setText("");
                //Log.i(TAG, result);

                Intent questionIntent = new Intent(MainActivity.this, QuestionsScreen.class);

                //Bundle bundle = new Bundle();
                //bundle.putSerializable(MainActivity.EXTRA_MESSAGE, new QuestionsService(result));

                questionIntent.putExtra(EXTRA_MESSAGE, result);
                //new QuestionsService(result);
                //questionIntent.putExtra(EXTRA_MESSAGE, new QuestionsService(result));

                //questionIntent.putExtras(bundle);

                startActivity(questionIntent);
            }
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

        login_button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        username = usernameView.getText().toString();
                        password = passwordView.getText().toString();

                        new DownLoadHXFjson().execute(new String[] {getResources().getString(R.string.ws)});

                        login_button.setClickable(false);
                    }
                }
        );
    }
}
