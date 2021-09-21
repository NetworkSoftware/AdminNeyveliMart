package pro.network.nanjilmartadmin.shopreg;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import pro.network.nanjilmartadmin.R;
import pro.network.nanjilmartadmin.app.AndroidMultiPartEntity;
import pro.network.nanjilmartadmin.app.AppController;
import pro.network.nanjilmartadmin.app.Appconfig;
import pro.network.nanjilmartadmin.app.GlideApp;
import pro.network.nanjilmartadmin.app.Imageutils;
import pro.network.nanjilmartadmin.product.ImageClick;

import static pro.network.nanjilmartadmin.app.Appconfig.CREATE_SHOP;

/**
 * Created by user_1 on 11-07-2018.
 */

public class ShopRegister extends AppCompatActivity implements Imageutils.ImageAttachmentListener{


    EditText shop_name, phone, latlong;
    MaterialBetterSpinner stock_update;
    private ImageView profiletImage;
    Imageutils imageutils;
    private String imageUrl = "";
    TextView submit;
    private ProgressDialog pDialog;
    private final String[] STOCKUPDATE = new String[]{
            "Available", "Currently Unavailable",
    };
    public Button addSize;
    ArrayList<Time> times = new ArrayList<>();
    TimeAdapter timeAdapter;
    private RecyclerView sizelist;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_reg);

        getSupportActionBar().setTitle("Shop Register");

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        latlong = findViewById(R.id.latlong);
        shop_name = findViewById(R.id.shop_name);
        phone = findViewById(R.id.phone);
        stock_update = findViewById(R.id.stock_update);
        ArrayAdapter<String> stockAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, STOCKUPDATE);
        stock_update.setAdapter(stockAdapter);
        stock_update.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        imageutils = new Imageutils(this);
        profiletImage = (ImageView) findViewById(R.id.profiletImage);
        profiletImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageutils.imagepicker(1);
            }
        });

         addSize = findViewById(R.id.addSize);
        addSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDaysBottom(-1);
            }
        });
        times = new ArrayList<>();
        timeAdapter = new TimeAdapter(this, times, new ImageClick() {
            @Override
            public void onImageClick(int position) {
                showDaysBottom(position);
            }

            @Override
            public void onDeleteClick(int position) {
                times.remove(position);
                timeAdapter.notifyData(times);
            }
        }, true);
        sizelist = findViewById(R.id.sizelist);
        final LinearLayoutManager sizeManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);
        sizelist.setLayoutManager(sizeManager);
        sizelist.setAdapter(timeAdapter);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shop_name.getText().toString().length() <= 0) {
                    shop_name.setError("Select the Shop Name");
                } else if (phone.getText().toString().length() <= 0) {
                    phone.setError("Select the Phone");
                } else if (latlong.getText().toString().length() <= 0) {
                    latlong.setError("Enter the correct Location");
                } else if (stock_update.getText().toString().length() <= 0) {
                    stock_update.setError("Select the Sold or Not");
                } else if (times.size() <= 0) {
                    Toast.makeText(getApplicationContext(), "Upload the Time Schedule!", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser();
                }

            }
        });


    }

    private void showDaysBottom(int position) {
        final RoundedBottomSheetDialog mBottomSheetDialog = new RoundedBottomSheetDialog(ShopRegister.this);
        LayoutInflater inflater = ShopRegister.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.bottom_size_layout, null);
        final String[] TIMEING = new String[]{
                "Sunday", "Monday", "Tuesday", "wednesday", "Thursday", "Friday", "Saturday",
        };

        final MaterialBetterSpinner daySpinner = dialogView.findViewById(R.id.shop_time);
        final RadioButton openRadio = dialogView.findViewById(R.id.in_time);
        final RadioButton closeRadio = dialogView.findViewById(R.id.out_time);
        final EditText openHoursEdit = dialogView.findViewById(R.id.select_inTime);
        final EditText closeHoursEdit = dialogView.findViewById(R.id.select_outTime);
        if (position >= 0) {
            Time timeTemp = times.get(position);
            daySpinner.setText(timeTemp.getDayInWeek());
            openHoursEdit.setText(timeTemp.getOpenHours());
            closeHoursEdit.setText(timeTemp.getCloseHours());
            openRadio.setChecked(timeTemp.isOpen());
            closeRadio.setChecked(!timeTemp.isOpen());
        }

        ArrayAdapter<String> stockAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, TIMEING);
        daySpinner.setAdapter(stockAdapter);
        daySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        openRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openRadio.setChecked(true);
                    closeRadio.setChecked(false);

                } else {
                    openRadio.setChecked(false);
                }
            }
        });

        closeRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openRadio.setChecked(false);
                    closeRadio.setChecked(true);
                } else {
                    closeRadio.setChecked(false);
                }
            }
        });

        openHoursEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(ShopRegister.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                openHoursEdit.setText(hourOfDay + "." + minute);
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });
        closeHoursEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(ShopRegister.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                closeHoursEdit.setText(hourOfDay + "." + minute);
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });
        final Button submit = dialogView.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (daySpinner.getText().toString().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Enter Valid ShopTime", Toast.LENGTH_LONG).show();
                    return;
                } else if (closeHoursEdit.getText().toString().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Enter Valid OutTime", Toast.LENGTH_LONG).show();
                    return;
                }
                if (position >= 0) {
                    times.get(position).setOpen(openRadio.isChecked());
                    times.get(position).setDayInWeek(daySpinner.getText().toString());
                    times.get(position).setOpenHours(openHoursEdit.getText().toString());
                    times.get(position).setCloseHours(closeHoursEdit.getText().toString());
                } else {
                    times.add(new Time(
                            openRadio.isChecked(),
                            daySpinner.getText().toString(),
                            openHoursEdit.getText().toString(),
                            closeHoursEdit.getText().toString()));
                }
                timeAdapter.notifyData(times);
                mBottomSheetDialog.cancel();
            }
        });
        daySpinner.requestFocus();
        openRadio.requestFocus();
        closeRadio.requestFocus();
        openHoursEdit.requestFocus();
        closeHoursEdit.requestFocus();

        mBottomSheetDialog.setContentView(dialogView);
        mBottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mBottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RoundedBottomSheetDialog d = (RoundedBottomSheetDialog) dialog;
                        FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
                        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }, 0);
            }
        });
        mBottomSheetDialog.show();
    }

    private void registerUser() {
        String tag_string_req = "req_register";
        pDialog.setMessage("Uploading ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                CREATE_SHOP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    String msg = jsonObject.getString("message");
                    if (success) {
                        finish();
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();

                localHashMap.put("shop_name", shop_name.getText().toString());
                localHashMap.put("image", imageUrl);
                localHashMap.put("phone", phone.getText().toString());
                localHashMap.put("latlong", latlong.getText().toString());
                localHashMap.put("stock_update", stock_update.getText().toString());
                localHashMap.put("time_schedule", new Gson().toJson(times));
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq);
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    @Override
    protected void onPause() {
        super.onPause();
        hideDialog();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageutils.request_permission_result(requestCode, permissions, grantResults);
    }
    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        String path = getCacheDir().getPath() + File.separator + "ImageAttach" + File.separator;
        String storedPath=imageutils.createImage(file, filename, path, false);
        pDialog.setMessage("Uploading...");
        showDialog();
        new ShopRegister.UploadFileToServer().execute(Appconfig.
                compressImage(storedPath,ShopRegister.this));
    }
    private class UploadFileToServer extends AsyncTask<String, Integer, String> {
        String filepath;
        public long totalSize = 0;

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero

            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            pDialog.setMessage("Uploading..." + (String.valueOf(progress[0])));
        }

        @Override
        protected String doInBackground(String... params) {
            filepath = params[0];
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Appconfig.URL_IMAGE_UPLOAD);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filepath);
                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);

                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;

                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("Response from server: ", result);
            try {
                JSONObject jsonObject = new JSONObject(result.toString());
                if (!jsonObject.getBoolean("error")) {
                    GlideApp.with(getApplicationContext())
                            .load(filepath)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(false)
                            .placeholder(R.drawable.nanjilmart)
                            .into(profiletImage);
                    imageUrl = Appconfig.ip + "/images/" + imageutils.getfilename_from_path(filepath);
                } else {
                    imageUrl = null;
                }
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Image not uploaded", Toast.LENGTH_SHORT).show();
            }
            hideDialog();
            // showing the server response in an alert dialog
            //showAlert(result);


            super.onPostExecute(result);
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageutils.onActivityResult(requestCode, resultCode, data);

    }


}