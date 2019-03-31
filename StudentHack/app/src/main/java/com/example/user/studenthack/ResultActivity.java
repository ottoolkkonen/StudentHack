package com.example.user.studenthack;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class ResultActivity extends AppCompatActivity {

    //    private final OkHttpClient client =new OkHttpClient();
    private RequestQueue mRequestQueue;
    ProgressDialog progressDialog;
    private static final String TAG = "ResultActivity";
    private Button stringRequestButton;
    private Button JsonObjectRequestButton;
    private Button JsonArrayRequestButton;
    private Button ImageRequestButton;
    private View showDialogView;
    private TextView outputTextView;
    private ImageView outputImageView;
    RequestQueue queue;
    String filepath;
    Bitmap bitmap;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Log.d("resutl", "Start Result page");
        intent = getIntent();

        queue = Volley.newRequestQueue(this);
        String url = "https://studenthack.wenwen.co.uk/api.compImg";
        url = "https://androidtutorialpoint.com/api/volleyJsonObject";
        progressDialog = new ProgressDialog(this);
        filepath = intent.getStringExtra("photoPath");
        Log.d("result", filepath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8; // down sizing image as it throws OutOfMemory Exception for larger images
        filepath = filepath.replace("file://", ""); // remove to avoid BitmapFactory.decodeFile return null


        // stringRequestButton = (Button)findViewById(R.id.button_get_string);
        JsonObjectRequestButton = (Button) findViewById(R.id.button_get_Json_object);
//        JsonArrayRequestButton = (Button)findViewById(R.id.button_get_Json_array);
//        ImageRequestButton = (Button)findViewById(R.id.button_get_image);

//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Display the first 500 characters of the response string.
//                        Log.d("result", "Response is: "+ response.substring(0,500));
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("result","That didn't work!");
//            }
//        });
//        ActionBar progressDialog;


//        queue.add(stringRequest);

        JsonObjectRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String filepath = intent.getStringExtra("photoPath");
                //Log.d("result", filepath);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8; // down sizing image as it throws OutOfMemory Exception for larger images
                filepath = filepath.replace("file://", ""); // remove to avoid BitmapFactory.decodeFile return null
                Log.d("result", filepath);

                File imgFile = new File(filepath);
                if (imgFile.exists()) {
                    bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                }

                //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                HashMap data = new HashMap();
                data.put("img=@", filepath);
                //data.put("password","password");
                String url = "http://studenthack.wenwen.co.uk/api/compImg/";
                //postData(url, data);
                JSONObject js = new JSONObject();
                try {
                    JSONObject jsonobject_one = new JSONObject();

                    jsonobject_one.put("img", filepath);

                    JSONObject jsonobject = new JSONObject();
                    jsonobject.put("request", jsonobject_one);


                    js.put("data", jsonobject.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonPost(js, url, filepath);
                // post("http://studenthack.wenwen.co.uk/api/compImg", filepath);
                //volleyJsonObjectRequest("http://studenthack.wenwen.co.uk/api/compImg");
            }
        });
    }


    public void volleyJsonObjectRequest(String url) {
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        String filepath = intent.getStringExtra("photoPath");
        Log.d("result", filepath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8; // down sizing image as it throws OutOfMemory Exception for larger images
        filepath = filepath.replace("file://", ""); // remove to avoid BitmapFactory.decodeFile return null
        Log.d("result", filepath);

        File imgFile = new File(filepath);
        if (imgFile.exists()) {
            bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }

        //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("img", filepath);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.dismiss();
                if (s.equals("true")) {
                    Toast.makeText(ResultActivity.this, "Uploaded Successful", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ResultActivity.this, "Some error occurred!", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(ResultActivity.this, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
            }
        }) {
            //adding parameters to send
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                String filepath = intent.getStringExtra("photoPath");
                Log.d("result", filepath);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8; // down sizing image as it throws OutOfMemory Exception for larger images
                filepath = filepath.replace("file://", ""); // remove to avoid BitmapFactory.decodeFile return null
                Log.d("result", imageString);
                parameters.put("img=@", imageString);
                return parameters;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                //headers.put("Content-Type","application/x-www-form-urlencoded");
                return headers;
            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(ResultActivity.this);
        rQueue.add(request);
    }


    public void post(String url, String data) {
        OutputStream out;
        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");


            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(data);
            wr.flush();
            wr.close();

            connection.connect();

        } catch (IOException e) {
            e.printStackTrace();

        }


    }

    public void postData(String url, HashMap data) {
        RequestQueue requstQueue = Volley.newRequestQueue(ResultActivity.this);

        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("result", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("result", error.toString());
                    }
                }
        ) {
            //here I want to post data to sever
        };
        requstQueue.add(jsonobj);

    }


    public void jsonPost(JSONObject js, String url, String image) {
        Log.d("result", "jsonPost start");



        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, url, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("result", "response");
                        Log.d("result", response.toString());

//                        msgResponse.setText(response.toString());
//                        hideProgressDialog();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("result", "Error: " + error.getMessage());
                //hideProgressDialog();
            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }


        };
    }
}
