package pro.network.adminneyvelimart.order;

import static org.apache.poi.ss.util.CellUtil.createCell;
import static pro.network.adminneyvelimart.app.Appconfig.DELIVERYBOY;
import static pro.network.adminneyvelimart.app.Appconfig.ORDER;
import static pro.network.adminneyvelimart.app.Appconfig.ORDER_ASSIGN_DBOY;
import static pro.network.adminneyvelimart.app.Appconfig.WALLET;
import static pro.network.adminneyvelimart.app.Appconfig.mypreference;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pro.network.adminneyvelimart.R;
import pro.network.adminneyvelimart.app.AppController;
import pro.network.adminneyvelimart.app.Appconfig;
import pro.network.adminneyvelimart.app.CustomFontEditText;
import pro.network.adminneyvelimart.app.PdfConfig;
import pro.network.adminneyvelimart.product.Product;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MainActivityOrder extends AppCompatActivity
        implements OrderAdapter.ContactsAdapterListener, StatusListener {
    ProgressDialog progressDialog;
    Button loadMore;
    int offset = 0;
    ArrayList<String> dboysName = new ArrayList<>();
    Map<String, String> idNameMap = new HashMap<>();
    String shopID = null, shoName = null, role = null;
    private RecyclerView recyclerView;
    private List<Order> orderList;
    private OrderAdapter mAdapter;
    private SearchView searchView;
    private OrderAdapter deliverAdapter;
    private ArrayList<Order> deliveredList;
    private RecyclerView recycler_view_delivered;
    private String statusVal;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainorder);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        // toolbar fancy stuff
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.order);

        recyclerView = findViewById(R.id.recycler_view);
        orderList = new ArrayList<>();
        mAdapter = new OrderAdapter(this, orderList,
                this, this, sharedPreferences);
        recycler_view_delivered = findViewById(R.id.recycler_view_delivered);
        deliveredList = new ArrayList<>();
        deliverAdapter = new OrderAdapter(this, deliveredList,
                this, this, sharedPreferences);
        loadMore = findViewById(R.id.loadMore);

        shopID = getIntent().getStringExtra("shopId");
        shoName = getIntent().getStringExtra("shopName");
        role = getIntent().getStringExtra("role");
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        final LinearLayoutManager addManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(addManager1);
        recyclerView.setAdapter(mAdapter);

        recycler_view_delivered.setItemAnimator(new DefaultItemAnimator());
        final LinearLayoutManager deliManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler_view_delivered.setLayoutManager(deliManager);
        recycler_view_delivered.setAdapter(deliverAdapter);

        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchContacts("", "", false);
            }
        });

    }

    private void alertMonthReport() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivityOrder.this);
        LayoutInflater inflater = MainActivityOrder.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_report_statement, null);
        final CustomFontEditText fromDate = dialogView.findViewById(R.id.fromDate);
        final CustomFontEditText toDate = dialogView.findViewById(R.id.toDate);
        final TextView submit = dialogView.findViewById(R.id.submit);
        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(true);
        b.show();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fromDate.getText().toString().length() > 0
                        && toDate.getText().toString().length() > 0) {
                    fetchContacts(fromDate.getText().toString(), toDate.getText().toString(), true);
                    b.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), "Select Location", Toast.LENGTH_SHORT).show();

                }

            }
        });


    }


    private void fetchContacts(String fromDate, String toDate, boolean sheet) {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        showDialog();
        if (getIntent().getStringExtra("status") != null) {
            statusVal = getIntent().getStringExtra("status");
        } else {
            statusVal = "";
        }
        String shopId = null;
        if ("Admin".equalsIgnoreCase(role)) {
            shopId = "Admin";
        } else {
            shopId = shopID;
        }
        String url;
        if (sheet) {
            url = ORDER + "?fromDate=" + fromDate.replace("-", "/") + "&toDate=" + toDate.replace("-", "/") + "&report=report";
        } else {
            url = ORDER + "?offset=" + offset * 10 + "" + "&status=" + statusVal
                    + "&shopid=" + shopId;
        }
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        if (offset == 0) {
                            orderList = new ArrayList<>();
                            deliveredList = new ArrayList<>();
                        }
                        offset = offset + 1;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Order order = new Order();
                                order.setId(jsonObject.getString("id"));
                                order.setPrice(jsonObject.getString("price"));
                                order.setQuantity(jsonObject.getString("quantity"));
                                order.setStatus(jsonObject.getString("status"));
                                order.setItems(jsonObject.getString("items"));
                                order.setName(jsonObject.getString("name"));
                                order.setPhone(jsonObject.getString("phone"));
                                order.setAddress(jsonObject.getString("address"));
                                order.setLiveLocation(jsonObject.getString("liveLocation"));
                                order.setCashback(jsonObject.getString("cashback"));
                                order.setCouponAmt(jsonObject.getString("couponCost"));
                                order.setWalletAmt(jsonObject.getString("wallet"));

                                order.setReson(jsonObject.getString("reason"));
                                order.setCreatedOn(jsonObject.getString("createdon"));
                                if (!jsonObject.isNull("shopname")) {
                                    order.setShopname(jsonObject.getString("shopname"));
                                }
                                if (!jsonObject.isNull("paymentId")) {
                                    order.setPaymentId(jsonObject.getString("paymentId"));
                                }
                                order.setTotal(jsonObject.getString("total"));
                                order.setDcharge(jsonObject.getString("dcharge"));
                                order.setPincode(jsonObject.getString("pincode"));
                                order.setUser(jsonObject.getString("user"));
                                order.setDtime(jsonObject.getString("dtime"));
                                ObjectMapper mapper = new ObjectMapper();
                                Object listBeans = new Gson().fromJson(jsonObject.getString("items"),
                                        Object.class);
                                ArrayList<Product> accountList = mapper.convertValue(
                                        listBeans,
                                        new TypeReference<ArrayList<Product>>() {
                                        }
                                );
                                for (int j = 0; j < accountList.size(); j++) {
                                    order.setShopname(accountList.get(j).shopname);
                                }

                                order.setProductBeans(accountList);
                                if (order.getStatus().equalsIgnoreCase("ordered")) {
                                    orderList.add(order);
                                } else {
                                    deliveredList.add(order);
                                }
                            } catch (Exception e) {
                                Log.e("xxxx", e.toString());
                            }
                        }
                        mAdapter.notifyData(orderList);
                        deliverAdapter.notifyData(deliveredList);
                        if (sheet) {
                            printXLSheet(orderList);
                        }
                        int valTotal = orderList.size() + deliveredList.size();
                        getSupportActionBar().setSubtitle("Orders - " + valTotal);

                    } else {
                        Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplication(), "Some Network Error.Try after some time 1", Toast.LENGTH_SHORT).show();

                }
                hideDialog();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getApplication(),
                        "Some Network Error.Try after some time 3", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    String getFilename(ArrayList<Product> accountList) {
        String pro = "";
        for (int j = 0; j < accountList.size(); j++) {
            pro = accountList.get(j).getModel() + " - " + accountList.get(j).getBrand() + ",\n";
        }
        return pro;

    }

    public void printXLSheet(List<Order> user) {

        try {
            String path = getApplication().getExternalCacheDir().getPath() + "/PDF";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File uriFile = new File(dir, System.currentTimeMillis() + ".xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName("report"));

            int columns = 8;
            for (int i = 0; i < columns; i++) {
                sheet.setColumnWidth(i, 15 * 256);
            }

            Row row = sheet.createRow(0);
            createCell(row, 0, "S.NO");
            createCell(row, 1, "Shop");
            createCell(row, 2, "Product");
            createCell(row, 3, "Price");
            createCell(row, 4, "Count");
            createCell(row, 5, "User Name");
            createCell(row, 6, "Phone");
            createCell(row, 7, "Date");

            for (int i = 0; i < user.size(); i++) {
                Row row1 = sheet.createRow(i + 1);
                Order userbean = user.get(i);
                createCell(row1, 0, String.valueOf(i + 1));
                createCell(row1, 1, userbean.getShopname());
                createCell(row1, 2, getFilename(userbean.getProductBeans()));
                createCell(row1, 3, userbean.getPrice());
                createCell(row1, 4, userbean.getQuantity());
                createCell(row1, 5, userbean.getName());
                createCell(row1, 6, userbean.getPhone());
                createCell(row1, 7, userbean.getCreatedOn());
            }
            Uri fileUri = Uri.fromFile(uriFile);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                fileUri = FileProvider.getUriForFile(getApplicationContext(),
                        getApplication().getPackageName() + ".provider",
                        uriFile);
            }

            OutputStream outputStream = getContentResolver().openOutputStream(fileUri);
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            Log.i("TAG", "writeExcel: export successful");
            Toast.makeText(getApplicationContext(), "export successful", Toast.LENGTH_SHORT).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(fileUri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } else {
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(fileUri, "application/xlsx");
                Intent intent = Intent.createChooser(target, "Open File");
                startActivity(intent);
            }

            hideDialog();
        } catch (Exception e) {
            Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            hideDialog();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_sheet);
        item.setVisible(!sharedPreferences.getString(Appconfig.role, "").equalsIgnoreCase("isClient"));
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();

        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setVisibility(View.GONE);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                offset = 0;
                fetchContacts("", "", false);
                return false;
            }
        });
        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                if (query.length() > 3) {
                    offset = 0;
                    fetchContacts("", "", false);
                } else if (query.length() == 0) {
                    offset = 0;
                    fetchContacts("", "", false);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                if (query.length() > 3) {
                    offset = 0;
                    fetchContacts("", "", false);
                } else if (query.length() == 0) {
                    offset = 0;
                    fetchContacts("", "", false);
                }
                return false;
            }
        });
        if (getIntent() != null && getIntent().getBooleanExtra("isSearch", false)) {
            searchView.setIconified(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }
        if (id == R.id.action_sheet) {
            alertMonthReport();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
      /*  // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }*/
        super.onBackPressed();

    }


    @Override
    public void onContactSelected(Order contact) {
       /* Intent intent = new Intent(MainActivityOrder.this, ProductUpdate.class);
        intent.putExtra("data", contact);
        startActivity(intent);*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchDboys();

    }

    private void showBottomDialog(Order order) {
        final RoundedBottomSheetDialog mBottomSheetDialog = new RoundedBottomSheetDialog(MainActivityOrder.this);
        LayoutInflater inflater = MainActivityOrder.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.bottom_assign_dboy, null);

        TextView title = dialogView.findViewById(R.id.title);
        final AutoCompleteTextView dboyName = dialogView.findViewById(R.id.dboyName);
        final TextInputLayout dboylayout = dialogView.findViewById(R.id.dboylayout);
        final MaterialButton assignDboy = dialogView.findViewById(R.id.assignDboy);
        ImageView close = dialogView.findViewById(R.id.close);

        title.setText("Assign Delivery boy");
        dboylayout.setHint("Select Delivery boy");
        ArrayAdapter adapter = new ArrayAdapter(MainActivityOrder.this,
                android.R.layout.simple_list_item_1, dboysName);
        dboyName.setThreshold(1);
        dboyName.setAdapter(adapter);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.cancel();
            }
        });

        assignDboy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dboyName.getText().toString().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Select Valid Entity", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    assignDboyService(order, idNameMap.get(dboyName.getText().toString()), mBottomSheetDialog);
                }

            }

        });
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

    private void fetchDboys() {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                DELIVERYBOY + "?status=active", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        idNameMap = new HashMap<>();
                        dboysName = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            idNameMap.put(jsonObject.getString("name"), jsonObject.getString("id"));
                            dboysName.add(jsonObject.getString("name"));
                        }
                    }
                } catch (JSONException e) {
                }
                fetchContacts("", "", false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void assignDboyService(final Order order, final String dboyid,
                                   final RoundedBottomSheetDialog mBottomSheetDialog) {
        String tag_string_req = "req_register";
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                ORDER_ASSIGN_DBOY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    if (success == 1) {
                        offset = 0;
                        fetchContacts("", "", false);
                        if (mBottomSheetDialog != null) {
                            mBottomSheetDialog.cancel();
                        }
                    }

                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(getApplication(), "Some Network Error.Try after some time 4", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getApplication(),
                        "Some Network Error.Try after some time 5", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("id", order.getId());
                localHashMap.put("dboyid", dboyid);
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public void onDeliveredClick(String id) {
        statusChange(id, "Delivered", "Delivered by admin");
    }

    @Override
    public void onWhatsAppClick(String phone) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=91" + phone
                    + "&text=" + "Hi"));
            intent.setPackage("com.whatsapp.w4b");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=91" + phone
                    + "&text=" + "Hi"));
            intent.setPackage("com.whatsapp");
            startActivity(intent);
        }
    }

    @Override
    public void onCallClick(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    @Override
    public void onCancelClick(final String id) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivityOrder.this);
        LayoutInflater inflater = MainActivityOrder.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialog, null);
        TextView title = dialogView.findViewById(R.id.title);
        final TextInputEditText reason = dialogView.findViewById(R.id.address);


        title.setText("* Do you want to cancel this order? If yes Order will be canceled.");
        dialogBuilder.setTitle("Alert")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (reason.getText().toString().length() > 0) {
                            statusChange(id, "canceled", reason.getText().toString());
                            dialog.cancel();
                        } else {
                            Toast.makeText(getApplicationContext(), "Enter valid reason", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();
    }

    @Override
    public void onAssignDboy(Order order) {
        showBottomDialog(order);
    }

    @Override
    public void onTrackOrder(String id) {
        Intent intent = new Intent(MainActivityOrder.this, Order_TimelineActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onCourier(String id) {

    }

    @Override
    public void InProgress(Order order) {
        statusChange(order.getId(), "InProgress", "InProgress By Admin");

    }

    @Override
    public void bill(Order position) {
        printFunction(MainActivityOrder.this, position);
    }

    @Override
    public void wallet(Order order) {
        showCashBack(order);
    }

//    @Override
//    public void sheet(Order position) {
//        printXLSheet(MainActivityOrder.this, position);
//    }

    private void showCashBack(final Order order) {
        final RoundedBottomSheetDialog mBottomSheetDialog = new RoundedBottomSheetDialog(MainActivityOrder.this);
        LayoutInflater inflater = MainActivityOrder.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.bottom_amount_layout, null);

        TextInputLayout reviewTxt = dialogView.findViewById(R.id.walletTxt);
        final TextInputEditText walletEdit = dialogView.findViewById(R.id.wallet);
        final TextInputEditText description = dialogView.findViewById(R.id.description);


        final RadioButton radioIn = dialogView.findViewById(R.id.radioIn);
        final RadioButton radioOut = dialogView.findViewById(R.id.radioOut);
        radioIn.setChecked(true);

        radioIn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    radioIn.setChecked(true);
                    radioOut.setChecked(false);
                } else {
                    radioIn.setChecked(false);
                    radioOut.setChecked(true);
                }
            }
        });
        radioOut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    radioIn.setChecked(false);
                    radioOut.setChecked(true);
                } else {
                    radioIn.setChecked(true);
                    radioOut.setChecked(false);
                }
            }
        });

        final Button submit = dialogView.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (walletEdit.getText().toString().length() <= 0 ||
                        description.getText().toString().length() <= 0) {
                    Toast.makeText(MainActivityOrder.this, "Enter Valid Cashback", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    updateWallet(order.getUser(), walletEdit.getText().toString(), description.getText().toString(),
                            radioIn.isChecked(), order.id, mBottomSheetDialog);
                }
            }
        });
        mBottomSheetDialog.setContentView(dialogView);
        walletEdit.requestFocus();
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

    private void updateWallet(final String userId, final String wallet, final String description, final boolean isCredit,
                              final String orderId, final RoundedBottomSheetDialog mBottomSheetDialog) {
        String tag_string_req = "req_register";
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.PUT,
                WALLET, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        if (mBottomSheetDialog != null) {
                            mBottomSheetDialog.cancel();
                        }
                        offset = 0;
                        fetchContacts("", "", false);
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(getApplication(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getApplication(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("userId", userId);
                localHashMap.put("amt", wallet);
                localHashMap.put("description", description);
                localHashMap.put("credit", isCredit ? "add" : "minus");
                localHashMap.put("orderId", orderId);
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void statusChange(final String id, final String status, final String reason) {
        String tag_string_req = "req_register";
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.PUT,
                ORDER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        offset = 0;
                        fetchContacts("", "", false);
                    } else {
                        Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(getApplication(), "Some Network Error.Try after some time 6", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getApplication(),
                        "Some Network Error.Try after some time 7", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("id", id);
                localHashMap.put("status", status);
                localHashMap.put("reason", reason);
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public void printFunction(Context context, Order mainbean) {

        try {

            String path = getExternalCacheDir().getPath() + "/PDF";
            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();
            Log.d("PDFCreator", "PDF Path: " + path);
            File file = new File(dir, mainbean.getName().replace(" ", "_") + "_" + mainbean.getId() + ".pdf");
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fOut = new FileOutputStream(file);

            float left = 0;
            float right = 0;
            float top = 0;
            float bottom = 0;
            com.itextpdf.text.Document document = new Document(new Rectangle(288, 512));
            PdfWriter pdfWriter = PdfWriter.getInstance(document, fOut);


            document.open();
            PdfConfig.addMetaData(document);

           /* HeaderFooterPageEvent event = new HeaderFooterPageEvent(Image.getInstance(byteArray), Image.getInstance(byteArray1), isDigital, getConfigBean());
            pdfWriter.setPageEvent(event);*/
            PdfConfig.addContent(document, mainbean, MainActivityOrder.this);

            document.close();

            Uri fileUri = Uri.fromFile(file);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } else {
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                Intent intent = Intent.createChooser(target, "Open File");
                startActivity(intent);
            }
            hideDialog();
            // new UploadInvoiceToServer().execute(imageutils.getPath(fileUri) + "@@" + mainbean.getBuyerphone() + "@@" + mainbean.getDbid());

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            hideDialog();
        }


    }
}
