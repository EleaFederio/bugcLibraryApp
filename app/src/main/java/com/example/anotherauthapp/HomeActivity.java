package com.example.anotherauthapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private TextView name, email;
    private Button logoutBtn;
    SessionManager sessionManager;
    String getId;
    public static final String TAG = HomeActivity.class.getSimpleName();
    private static String URL_READ = "http://192.168.0.222/api/read_detail.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        logoutBtn = findViewById(R.id.btnLogout);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);
//        String sessionName = user.get(sessionManager.NAME);
//        String sessionEmail = user.get(sessionManager.EMAIL);

//        Intent intent = getIntent();
//        String extraName = intent.getStringExtra("name");
//        String extraEmail = intent.getStringExtra("email");

//        name.setText(sessionName);
//        email.setText(sessionEmail);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
            }
        });
    }

    private void getUserDetails(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.i(TAG, response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if(success.equals(1)){
                                for (int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String userName = object.getString("name").trim();
                                    String userEmail = object.getString("email").trim();
                                    name.setText(userName);
                                    email.setText(userEmail);
                                }
                            }

                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            Toast.makeText(HomeActivity.this, "Error Reading Details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(HomeActivity.this, "Error Reading Details: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", getId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserDetails();
    }
}
