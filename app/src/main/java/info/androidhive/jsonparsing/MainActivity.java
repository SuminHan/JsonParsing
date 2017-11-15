package info.androidhive.jsonparsing;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    private TextView txtView;
    String resultString = "";

    // URL to get contacts JSON
    private static final String url = "https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize";
    private static final String imgurl = "https://firebasestorage.googleapis.com/v0/b/friendlychat-8f9a6.appspot.com/o/image%3A10225?alt=media&token=c0dff97e-61d3-4d08-89d6-ea88dbeb7110";
    private static final String apikey = "b0d59b46a7ef412895c0ceddb8b3ed76";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //lv = (ListView) findViewById(R.id.list);
        txtView = (TextView) findViewById(R.id.txtView);

        new GetResult().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetResult extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, apikey, imgurl);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    // Getting JSON Array node
                    JSONArray faces = new JSONArray(jsonStr);

                    if(faces.length() == 0) resultString = "Nothing to show";

                    // looping through All Contacts
                    for (int i = 0; i < faces.length(); i++) {
                        JSONObject c = faces.getJSONObject(i);

                        JSONObject face = c.getJSONObject("faceRectangle");
                        JSONObject scores = c.getJSONObject("scores");
                        Log.e(TAG, "reading " + i);

                        String height = face.getString("height");
                        String left = face.getString("left");
                        String top = face.getString("top");
                        String width = face.getString("width");

                        String anger = scores.getString("anger");
                        String contempt = scores.getString("contempt");
                        String disgust = scores.getString("disgust");
                        String fear = scores.getString("fear");
                        String happiness = scores.getString("happiness");
                        String neutral = scores.getString("neutral");
                        String sadness = scores.getString("sadness");
                        String surprise = scores.getString("surprise");


                        resultString += "["+i+"] [" + height + ", " + width + ", (" + left + ", " + top + ")]"
                                + "(" + anger + ", " + contempt + ", " + disgust + ", " + fear + ", "
                                + happiness + ", " + neutral + ", " + sadness + ", " + surprise + ")\n";
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }


            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            /*
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, contactList,
                    R.layout.list_item, new String[]{"name", "email",
                    "mobile"}, new int[]{R.id.name,
                    R.id.email, R.id.mobile});

            lv.setAdapter(adapter);
            */
            //Success
            txtView.setText(resultString);
        }

    }
}
