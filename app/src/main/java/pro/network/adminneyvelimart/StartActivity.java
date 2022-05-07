package pro.network.adminneyvelimart;

import static pro.network.adminneyvelimart.app.Appconfig.mypreference;
import static pro.network.adminneyvelimart.app.Appconfig.shopId;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pro.network.adminneyvelimart.app.AppController;
import pro.network.adminneyvelimart.app.Appconfig;
import pro.network.adminneyvelimart.product.MainActivityProduct;

public class StartActivity extends AppCompatActivity {
    ProgressDialog dialog;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        Log.d("TOken ", "" + FirebaseInstanceId.getInstance().getToken());
        FirebaseMessaging.getInstance().subscribeToTopic("allDevices");


        final EditText phone = findViewById(R.id.phone);
        final EditText password = findViewById(R.id.password);
        phone.setText("7200072057");
        password.setText("123456789");
        if ((!("Admin".equalsIgnoreCase(sharedpreferences.getString(Appconfig.shopPhone, ""))))
                && (!("isClient".equalsIgnoreCase(sharedpreferences.getString(Appconfig.shopPhone, ""))))) {
            phone.setText(sharedpreferences.getString(Appconfig.shopPhone, ""));
        }

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((phone.getText().length() <= 0) && (password.getText().length() <= 0)) {
                    Toast.makeText(getApplicationContext(), "Enter valid phone and password", Toast.LENGTH_SHORT).show();
                }else if((phone.getText().toString().equalsIgnoreCase("7200072057"))
                        && (password.getText().toString().equalsIgnoreCase("123456789"))){
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Appconfig.role,"Admin");
                    editor.commit();
                    Intent io = new Intent(StartActivity.this, NaviActivity.class);
                    io.putExtra("role", "Admin");
                    startActivity(io);
                } else {
                    checkLogin(phone.getText().toString(), password.getText().toString());
                }
            }
        });
    }


    private void checkLogin(final String username, final String password) {
        String tag_string_req = "req_register";
        dialog.setMessage("Login ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Appconfig.SHOP_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    String msg = jObj.getString("message");
                    if (success == 1) {
                        String user_id = jObj.getString("shop_id");
                        String name = jObj.getString("name");
                        String phone = jObj.getString("phone");
                        String role = jObj.getString("role");
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(Appconfig.isLogin, true);
                        editor.putString(Appconfig.shopName, name);
                        editor.putString(Appconfig.shopPhone, phone);
                        editor.putString(Appconfig.role, role);
                        editor.putString(shopId, user_id);
                        editor.commit();
                        if (!jObj.isNull("role") && jObj.getString("role").equalsIgnoreCase("isClient")) {
                            Intent io = new Intent(StartActivity.this, NaviActivity.class);
                            io.putExtra("shopId", user_id);
                            io.putExtra("shopName", name);
                            io.putExtra("role", role);
                            startActivity(io);
                            finish();
                        } else {
                            Intent io = new Intent(StartActivity.this, NaviActivity.class);
                            io.putExtra("role", role);
                            startActivity(io);
                            finish();
                        }
                    }


                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Log.e("xxxxxxxxxx", e.toString());
                }
                hideDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        "Slow network found.Try again later", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("phone", username);
                localHashMap.put("password", password);
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!dialog.isShowing())
            dialog.show();
    }

    private void hideDialog() {
        if (dialog.isShowing())
            dialog.dismiss();
    }

}
