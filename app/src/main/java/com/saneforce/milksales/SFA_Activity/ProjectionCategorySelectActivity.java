package com.saneforce.milksales.SFA_Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.saneforce.milksales.Activity_Hap.SFA_Activity;
import com.saneforce.milksales.BuildConfig;
import com.saneforce.milksales.Common_Class.AlertDialogBox;
import com.saneforce.milksales.Common_Class.Common_Class;
import com.saneforce.milksales.Common_Class.Common_Model;
import com.saneforce.milksales.Common_Class.Constants;
import com.saneforce.milksales.Common_Class.Shared_Common_Pref;
import com.saneforce.milksales.Interface.AlertBox;
import com.saneforce.milksales.Interface.ApiClient;
import com.saneforce.milksales.Interface.ApiInterface;
import com.saneforce.milksales.Interface.LocationEvents;
import com.saneforce.milksales.Interface.Master_Interface;
import com.saneforce.milksales.Interface.UpdateResponseUI;
import com.saneforce.milksales.Interface.onListItemClick;
import com.saneforce.milksales.Model_Class.Datum;
import com.saneforce.milksales.R;
import com.saneforce.milksales.SFA_Adapter.RyclBrandListItemAdb;
import com.saneforce.milksales.SFA_Adapter.RyclListItemAdb;
import com.saneforce.milksales.SFA_Model_Class.Category_Universe_Modal;
import com.saneforce.milksales.SFA_Model_Class.Product_Details_Modal;
import com.saneforce.milksales.common.DatabaseHandler;
import com.saneforce.milksales.common.LocationFinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectionCategorySelectActivity extends AppCompatActivity implements View.OnClickListener, UpdateResponseUI, Master_Interface {
    //GridView categorygrid,Grpgrid,Brndgrid;
    List<Category_Universe_Modal> Category_Modal = new ArrayList<>();
    List<Product_Details_Modal> Product_Modal;
    List<Product_Details_Modal> Product_ModalSetAdapter;
    List<Product_Details_Modal> Getorder_Array_List;
    List<Product_Details_Modal> freeQty_Array_List;
    List<Category_Universe_Modal> listt;
    Type userType;
    Gson gson;
    CircularProgressButton takeorder;
    TextView Out_Let_Name, Category_Nametext, tvRetailorPhone, retaileAddress, tvHistory, tvProjectionTimer;
    LinearLayout lin_orderrecyclerview, lin_gridcategory, rlAddProduct, llCalMob;
    Common_Class common_class;
    String Ukey;
    String[] strLoc;
    String Worktype_code = "", Route_Code = "", Dirtributor_Cod = "", Distributor_Name = "";
    Shared_Common_Pref sharedCommonPref;
    Prodct_Adapter mProdct_Adapter;
    String TAG = "Projection_Category_Select";
    DatabaseHandler db;
    RelativeLayout rlCategoryItemSearch;
    ImageView ivClose;
    EditText etCategoryItemSearch;
    int cashDiscount;
    NumberFormat formatter = new DecimalFormat("##0.00");
    private RecyclerView recyclerView, categorygrid, Grpgrid, Brndgrid;
    public int selectedPos = 0;
    private TextView tvTotalAmount, tvPlant;
    private double totalvalues, taxVal;
    private double totalQty;
    private TextView tvBillTotItem;
    final Handler handler = new Handler();
    public static ProjectionCategorySelectActivity order_category_select;
    private List<Product_Details_Modal> orderTotTax;



    ArrayList<Common_Model> plantList = new ArrayList<>();
    private int plantPos;
    LinearLayout llPlant;
    private String plantId = "";
    private ArrayList<Common_Model> uomList;
    private int uomPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_projection_category_select);
            order_category_select = this;
            db = new DatabaseHandler(this);
            sharedCommonPref = new Shared_Common_Pref(ProjectionCategorySelectActivity.this);
            common_class = new Common_Class(this);
            //common_class.getProductDetails(this);

            tvProjectionTimer = findViewById(R.id.tvProjectionTimer);

            Grpgrid = findViewById(R.id.PGroup);
            Brndgrid = findViewById(R.id.PBrnd);
            categorygrid = findViewById(R.id.category);
            takeorder = findViewById(R.id.takeorder);
            common_class.getDataFromApi(Constants.Todaydayplanresult, this, false);
            lin_orderrecyclerview = findViewById(R.id.lin_orderrecyclerview);
            lin_gridcategory = findViewById(R.id.lin_gridcategory);
            Out_Let_Name = findViewById(R.id.outlet_name);
            Category_Nametext = findViewById(R.id.Category_Nametext);
            rlCategoryItemSearch = findViewById(R.id.rlCategoryItemSearch);
            rlAddProduct = findViewById(R.id.rlAddProduct);
            ivClose = findViewById(R.id.ivClose);

            etCategoryItemSearch = findViewById(R.id.searchView);
            retaileAddress = findViewById(R.id.retaileAddress);
            tvRetailorPhone = findViewById(R.id.retailePhoneNum);
            tvHistory = findViewById(R.id.tvHistory);

            llCalMob = findViewById(R.id.btnCallMob);
            llPlant = findViewById(R.id.llPlant);
            tvPlant = findViewById(R.id.tvPlant);
            llCalMob.setOnClickListener(this);
            tvHistory.setOnClickListener(this);
            llPlant.setOnClickListener(this);


            Out_Let_Name.setText(sharedCommonPref.getvalue(Constants.Retailor_Name_ERP_Code));
            Product_ModalSetAdapter = new ArrayList<>();
            gson = new Gson();
            takeorder.setOnClickListener(this);
            rlCategoryItemSearch.setOnClickListener(this);
            ivClose.setOnClickListener(this);
            rlAddProduct.setOnClickListener(this);
            Ukey = Common_Class.GetEkey();
            Out_Let_Name.setText(sharedCommonPref.getvalue(Constants.Distributor_name));
            recyclerView = findViewById(R.id.orderrecyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            categorygrid.setLayoutManager(layoutManager);

            if (Common_Class.isNullOrEmpty(sharedCommonPref.getvalue(Constants.Distributor_phone)))
                llCalMob.setVisibility(View.GONE);
            else
                tvRetailorPhone.setText(sharedCommonPref.getvalue(Constants.Distributor_phone));
            retaileAddress.setText(sharedCommonPref.getvalue(Constants.DistributorAdd));

            //GetJsonData(String.valueOf(db.getMasterData(Constants.Category_List)), "1", "");
            String OrdersTable = String.valueOf(db.getMasterData(Constants.Projection_Product_List));
            userType = new TypeToken<ArrayList<Product_Details_Modal>>() {
            }.getType();

//            if (Common_Class.isNullOrEmpty(sharedCommonPref.getvalue(Constants.LOC_PROJECTION_DATA)))
            Product_Modal = gson.fromJson(OrdersTable, userType);
            // else {
            // Product_Modal = gson.fromJson(sharedCommonPref.getvalue(Constants.LOC_PROJECTION_DATA), userType);

            //  }

            ImageView ivToolbarHome = findViewById(R.id.toolbar_home);
            common_class.gotoHomeScreen(this, ivToolbarHome);

            Log.v(TAG, " order oncreate:h ");
            Log.v(TAG, " order oncreate:i ");
            Category_Nametext.setOnClickListener(this);

            etCategoryItemSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    showOrderItemList(selectedPos, s.toString());

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

//            getOrder();


            Log.d("ssds","nhgjku"+sharedCommonPref.getvalue(Constants.RSM_CUTOFF_TIME));

            handler.postDelayed(new Runnable() {
                public void run() {
                    findNearCutOfftime();
                    tvProjectionTimer.setText(Common_Class.GetTime() + "   /   " + sharedCommonPref.getvalue(Constants.RSM_CUTOFF_TIME));

                    Log.v("cutoffTimeProjection", sharedCommonPref.getvalue(Constants.RSM_CUTOFF_TIME));
                    handler.postDelayed(this, 1000);

                }
            }, 1000);

            /*if(Common_Class.GetTime().equals(sharedCommonPref.getvalue(Constants.RSM_CUTOFF_TIME))){
                Toast.makeText(ProjectionCategorySelectActivity.this,"Times up",Toast.LENGTH_SHORT).show();
                takeorder.setVisibility(View.GONE);
            }*/




            GetJsonData(String.valueOf(db.getMasterData(Constants.Todaydayplanresult)), "6", "");


            JSONArray ProdGroups = db.getMasterData(Constants.ProjectionProdGroups_List);
            LinearLayoutManager GrpgridlayManager = new LinearLayoutManager(this);
            GrpgridlayManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            Grpgrid.setLayoutManager(GrpgridlayManager);

            RyclListItemAdb grplistItems = new RyclListItemAdb(ProdGroups, this, new onListItemClick() {
                @Override
                public void onItemClick(JSONObject item) {

                    try {
                        FilterTypes(item.getString("id"));
                        common_class.brandPos = 0;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            Grpgrid.setAdapter(grplistItems);

            FilterTypes(ProdGroups.getJSONObject(0).getString("id"));

//
//            if (Shared_Common_Pref.Projection_Approval == 1 /*&& !Common_Class.isNullOrEmpty(
//                    sharedCommonPref.getvalue(Constants.LOC_PROJECTION_DATA))*/) {
//                // Product_Modal = gson.fromJson(sharedCommonPref.getvalue(Constants.LOC_PROJECTION_DATA), userType);
//
//                if (Product_Modal != null && Product_Modal.size() > 1) {
//                    findViewById(R.id.llApprovParent).setVisibility(View.VISIBLE);
//                    findViewById(R.id.rlTakeOrder).setVisibility(View.GONE);
//                    Product_Modal.get(0).setQty(5);
//                    Product_Modal.get(1).setQty(3);
//                    showOrderList();
//                }
//            }


            common_class.getDb_310Data(Constants.PLANT_MASTER, this);
        } catch (Exception e) {
            Log.v(TAG, " order oncreate: " + e.getMessage());

        }
    }


    private void findNearCutOfftime() {
        try {
            Type type = new TypeToken<ArrayList<Datum>>() {
            }.getType();
            List<Datum> slotList = new ArrayList<>();
            if (!Common_Class.isNullOrEmpty(sharedCommonPref.getvalue(Constants.SlotTime))) {
                slotList = gson.fromJson(sharedCommonPref.getvalue(Constants.SlotTime), type);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            long val = 0;
            String time = "";
            for (int i = 0; i < slotList.size(); i++) {
                Date d1 = sdf.parse(Common_Class.GetTime());
                Date d2 = sdf.parse(slotList.get(i).getTm());
                long elapsed = d2.getTime() - d1.getTime();
                Log.v(TAG + "time:" + slotList.get(i).getTm(), "Elapse:" + elapsed + ":val" + val);
                if ((val == 0 && elapsed > 0) || (elapsed < val && elapsed > 0)) {
                    val = elapsed;
                    time = slotList.get(i).getTm();
                }
            }

            if (slotList != null && slotList.size() > 0 && time.equals("")) {
                time = slotList.get(0).getTm();
            }

            /*if (!Common_Class.isNullOrEmpty(time))
                sharedCommonPref.save(Constants.RSM_CUTOFF_TIME, time);
            else {
                sharedCommonPref.save(Constants.RSM_CUTOFF_TIME, "16:43:00");
            }*/

        } catch (Exception e) {

        }
    }

    public void sumofTax(List<Product_Details_Modal> Product_Details_Modalitem, int pos) {
        try {
            String taxRes = sharedCommonPref.getvalue(Constants.TAXList);
            if (!Common_Class.isNullOrEmpty(taxRes)) {
                JSONObject jsonObject = new JSONObject(taxRes);
                JSONArray jsonArray = jsonObject.getJSONArray("Data");

                double wholeTax = 0;
                List<Product_Details_Modal> taxList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    if (jsonObject1.getString("Product_Detail_Code").equals(Product_Details_Modalitem.get(pos).getId())) {
                        if (jsonObject1.getDouble("Tax_Val") > 0) {
                            double taxCal = Product_Details_Modalitem.get(pos).getAmount() *
                                    ((jsonObject1.getDouble("Tax_Val") / 100));

                            wholeTax += taxCal;

                            taxList.add(new Product_Details_Modal(jsonObject1.getString("Tax_Id"),
                                    jsonObject1.getString("Tax_Type"), jsonObject1.getDouble("Tax_Val"), taxCal));


                        }
                    }
                }

                Product_Details_Modalitem.get(pos).setProductDetailsModal(taxList);
                Product_Details_Modalitem.get(pos).setAmount(Double.valueOf(formatter.format(Product_Details_Modalitem.get(pos).getAmount()
                        + wholeTax)));
                Product_Details_Modalitem.get(pos).setTax(Double.parseDouble(formatter.format(wholeTax)));
            }
        } catch (Exception e) {

        }
    }

    private void FilterTypes(String GrpID) {
        try {
            JSONArray TypGroups = new JSONArray();
            JSONArray tTypGroups = db.getMasterData(Constants.ProjectionProdTypes_List);
            LinearLayoutManager TypgridlayManager = new LinearLayoutManager(this);
            TypgridlayManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            Brndgrid.setLayoutManager(TypgridlayManager);
            for (int i = 0; i < tTypGroups.length(); i++) {
                JSONObject ritm = tTypGroups.getJSONObject(i);
                if (ritm.getString("GroupId").equalsIgnoreCase(GrpID)) {
                    TypGroups.put(ritm);
                }
            }

            String filterId = "";
            if (TypGroups.length() > 0)
                filterId = TypGroups.getJSONObject(0).getString("id");
            GetJsonData(String.valueOf(db.getMasterData(Constants.Projection_Category_List)), "1", filterId);

            RyclBrandListItemAdb TyplistItems = new RyclBrandListItemAdb(TypGroups, this, new onListItemClick() {
                @Override
                public void onItemClick(JSONObject item) {
                    try {
                        GetJsonData(String.valueOf(db.getMasterData(Constants.Projection_Category_List)), "1", item.getString("id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            Brndgrid.setAdapter(TyplistItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetJsonData(String jsonResponse, String type, String filter) {

        //type =1 product category data values
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);

            Log.v("CatergoryList",jsonArray.toString());

            if (type.equals("1")) Category_Modal.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                if (type.equals("1")) {
                    String id = String.valueOf(jsonObject1.optInt("id"));
                    String name = jsonObject1.optString("name");
                    String typeId = String.valueOf(jsonObject1.optInt("TypID"));
                    String Division_Code = jsonObject1.optString("Division_Code");
                    String Cat_Image = jsonObject1.optString("Cat_Image");
                    String sampleQty = jsonObject1.optString("sampleQty");
                    String colorflag = jsonObject1.optString("colorflag");

                    if (filter.equalsIgnoreCase(typeId))
                        Category_Modal.add(new Category_Universe_Modal(id, name, Division_Code, Cat_Image, sampleQty, colorflag));
                } else {
                    Route_Code = jsonObject1.optString("cluster");
                    Dirtributor_Cod = jsonObject1.optString("stockist");
                    Worktype_code = jsonObject1.optString("wtype");
                    Distributor_Name = jsonObject1.optString("StkName");
                }
            }

            if (type.equals("1")) {

                selectedPos = 0;

                ProjectionCategorySelectActivity.CategoryAdapter customAdapteravail = new ProjectionCategorySelectActivity.CategoryAdapter(getApplicationContext(),
                        Category_Modal);
                categorygrid.setAdapter(customAdapteravail);

                showOrderItemList(selectedPos, "");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    void showOrderList() {
        Getorder_Array_List = new ArrayList<>();
        Getorder_Array_List.clear();

        for (int pm = 0; pm < Product_Modal.size(); pm++) {
            if (Product_Modal.get(pm).getQty() > 0 || Product_Modal.get(pm).getRegularQty() > 0) {
                Getorder_Array_List.add(Product_Modal.get(pm));
            }
        }

        if (Getorder_Array_List.size() == 0)
            Toast.makeText(getApplicationContext(), "Order is empty", Toast.LENGTH_SHORT).show();
        else
            FilterProduct(Getorder_Array_List);

    }

    public void ResetSubmitBtn(int resetMode) {
        common_class.ProgressdialogShow(0, "");
        long dely = 10;
        if (resetMode != 0) dely = 1000;
        if (resetMode == 1) {
            takeorder.doneLoadingAnimation(getResources().getColor(R.color.green), BitmapFactory.decodeResource(getResources(), R.drawable.done));
        } else {
            takeorder.doneLoadingAnimation(getResources().getColor(R.color.color_red), BitmapFactory.decodeResource(getResources(), R.drawable.ic_wrong));
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                takeorder.stopAnimation();
                takeorder.revertAnimation();
            }
        }, dely);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llPlant:
                common_class.showCommonDialog(plantList, 2, this);
                break;
            case R.id.tvHistory:
                startActivity(new Intent(this, ProjectionHistoryActivity.class));
                break;
            case R.id.btnCallMob:
                common_class.showCalDialog(ProjectionCategorySelectActivity.this, "Do you want to Call this Outlet?",
                        tvRetailorPhone.getText().toString().replaceAll(",", ""));

                break;

            case R.id.rlAddProduct:
                moveProductScreen();
                break;

            case R.id.Category_Nametext:
                findViewById(R.id.rlSearchParent).setVisibility(View.GONE);
                findViewById(R.id.rlCategoryItemSearch).setVisibility(View.VISIBLE);
                break;
            case R.id.ivClose:
                findViewById(R.id.rlCategoryItemSearch).setVisibility(View.GONE);
                findViewById(R.id.rlSearchParent).setVisibility(View.VISIBLE);
                etCategoryItemSearch.setText("");
                showOrderItemList(selectedPos, "");
                break;

            case R.id.takeorder:

                try {

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    Date d1 = sdf.parse(Common_Class.GetTime());
                    Date d2 = sdf.parse(sharedCommonPref.getvalue(Constants.RSM_CUTOFF_TIME));
                    long elapsed = d2.getTime() - d1.getTime();
                    if (elapsed < 0 || Common_Class.isNullOrEmpty(sharedCommonPref.getvalue(Constants.RSM_CUTOFF_TIME)) ||
                            sharedCommonPref.getvalue(Constants.RSM_CUTOFF_TIME).equals("--:--:--")) {
                        ResetSubmitBtn(0);
                        common_class.showMsg(this, "Time UP...");
                        takeorder.setVisibility(View.GONE);
                    }

                    if (takeorder.getText().toString().equalsIgnoreCase("SUBMIT")) {

                        if (Common_Class.isNullOrEmpty(tvPlant.getText().toString()))
                            common_class.showMsg(this, "Please Select Plant");
                        else if (Getorder_Array_List != null
                                && Getorder_Array_List.size() > 0) {
                            Log.d("RepeatAni", String.valueOf(takeorder.isAnimating()));
                            if (takeorder.isAnimating()) return;
                            takeorder.startAnimation();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    String sLoc = sharedCommonPref.getvalue("CurrLoc");
                                    if (sLoc.equalsIgnoreCase("")) {
                                        new LocationFinder(getApplication(), new LocationEvents() {
                                            @Override
                                            public void OnLocationRecived(Location location) {
                                                strLoc = (location.getLatitude() + ":" + location.getLongitude()).split(":");
                                                SaveOrder();
                                            }
                                        });
                                    } else {
                                        strLoc = sLoc.split(":");
                                        SaveOrder();
                                    }

                                }
                            }, 500);
                        } else {
                            common_class.showMsg(this, "Your Cart is empty...");
                        }
                    } else {
                        showOrderList();
                    }
                } catch (Exception e) {

                }
                break;


        }
    }

    private void SaveOrder() {

        if (common_class.isNetworkAvailable(this)) {

            AlertDialogBox.showDialog(ProjectionCategorySelectActivity.this, "SFA", "Are You Sure Want to Submit?", "OK", "Cancel", false, new AlertBox() {
                @Override
                public void PositiveMethod(DialogInterface dialog, int id) {
                    common_class.ProgressdialogShow(1, "");
                    JSONArray data = new JSONArray();
                    JSONObject ActivityData = new JSONObject();
                    try {
                        JSONObject HeadItem = new JSONObject();
                        HeadItem.put("SF", Shared_Common_Pref.Sf_Code);
                        HeadItem.put("Worktype_code", Worktype_code);
                        HeadItem.put("Town_code", sharedCommonPref.getvalue(Constants.Route_Id));
                        HeadItem.put("dcr_activity_date", Common_Class.GetDate());
                        HeadItem.put("UKey", Ukey);
                        HeadItem.put("DataSF", Shared_Common_Pref.Sf_Code);
                        HeadItem.put("AppVer", BuildConfig.VERSION_NAME);
                        ActivityData.put("Activity_Report_Head", HeadItem);

                        JSONObject OutletItem = new JSONObject();
                        OutletItem.put("stockist_code", sharedCommonPref.getvalue(Constants.Distributor_Id));
                        OutletItem.put("stockist_name", sharedCommonPref.getvalue(Constants.Distributor_name));
                        OutletItem.put("No_Of_items", tvBillTotItem.getText().toString());
                        OutletItem.put("ordertype", "projection");
                        OutletItem.put("plantName", tvPlant.getText().toString());
                        OutletItem.put("plantId", plantId);
                        OutletItem.put("SFCutoff", sharedCommonPref.getvalue(Constants.RSM_CUTOFF_TIME));

                        Log.v("projectionResponse",OutletItem.toString());

                        if (strLoc.length > 0) {
                            OutletItem.put("Lat", strLoc[0]);
                            OutletItem.put("Long", strLoc[1]);
                        } else {
                            OutletItem.put("Lat", "");
                            OutletItem.put("Long", "");
                        }
                        JSONArray Order_Details = new JSONArray();
                        // JSONArray totTaxArr = new JSONArray();

                        for (int z = 0; z < Getorder_Array_List.size(); z++) {
                            JSONObject ProdItem = new JSONObject();
                            ProdItem.put("product_Name", Getorder_Array_List.get(z).getName());
                            ProdItem.put("product_code", Getorder_Array_List.get(z).getId());
                            ProdItem.put("Product_Qty", Getorder_Array_List.get(z).getQty());

                            ProdItem.put("ConversionFactor", Getorder_Array_List.get(z).getCnvQty());
                            ProdItem.put("UOM_Id", Getorder_Array_List.get(z).getUOM_Id());
                            ProdItem.put("UOM_Nm", Getorder_Array_List.get(z).getUOM_Nm());

                            Order_Details.put(ProdItem);

                        }

                        ActivityData.put("Activity_Doctor_Report", OutletItem);
                        ActivityData.put("Order_Details", Order_Details);
                        data.put(ActivityData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    Call<JsonObject> responseBodyCall = apiInterface.saveProjection(Shared_Common_Pref.Div_Code, Shared_Common_Pref.Sf_Code, data.toString());
                    responseBodyCall.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful()) {
                                try {
                                    common_class.ProgressdialogShow(0, "");
                                    Log.e("JSON_VALUES", response.body().toString());
                                    JSONObject jsonObjects = new JSONObject(response.body().toString());
                                    String san = jsonObjects.getString("success");
                                    Log.e("Success_Message", san);
                                    ResetSubmitBtn(1);
                                    if (san.equals("true")) {
                                        sharedCommonPref.clear_pref(Constants.LOC_PROJECTION_DATA);
                                        common_class.CommonIntentwithFinish(ProjectionHistoryActivity.class);
                                    }
                                    common_class.showMsg(ProjectionCategorySelectActivity.this, jsonObjects.getString("Msg"));

                                } catch (Exception e) {
                                    common_class.ProgressdialogShow(0, "");
                                    ResetSubmitBtn(2);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            common_class.ProgressdialogShow(0, "");
                            Log.e("SUBMIT_VALUE", "ERROR");
                            ResetSubmitBtn(2);
                        }
                    });

                }

                @Override
                public void NegativeMethod(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    ResetSubmitBtn(0);
                }
            });
        } else {
            Toast.makeText(this, "Check your Internet connection", Toast.LENGTH_SHORT).show();
            ResetSubmitBtn(0);
        }
    }

    private void FilterProduct(List<Product_Details_Modal> orderList) {
        findViewById(R.id.rlCategoryItemSearch).setVisibility(View.GONE);
        findViewById(R.id.rlSearchParent).setVisibility(View.GONE);
        findViewById(R.id.llBillHeader).setVisibility(View.VISIBLE);
        findViewById(R.id.llPayNetAmountDetail).setVisibility(View.VISIBLE);
        lin_gridcategory.setVisibility(View.GONE);
        lin_orderrecyclerview.setVisibility(View.VISIBLE);
        takeorder.setText("SUBMIT");

        mProdct_Adapter = new Prodct_Adapter(orderList, R.layout.product_projection_pay_recyclerview, getApplicationContext(), -1);
        recyclerView.setAdapter(mProdct_Adapter);
        showFreeQtyList();
    }

    void showFreeQtyList() {
        freeQty_Array_List = new ArrayList<>();
        freeQty_Array_List.clear();

        for (Product_Details_Modal pm : Product_Modal) {

            if (pm.getRegularQty() != null) {
                if (!Common_Class.isNullOrEmpty(pm.getFree()) && !pm.getFree().equals("0")) {
                    freeQty_Array_List.add(pm);

                }
            }
        }
    }


    public void updateToTALITEMUI() {
        TextView tvTotalItems = findViewById(R.id.tvTotalItems);
        TextView tvTotLabel = findViewById(R.id.tvTotLabel);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        TextView tvTax = findViewById(R.id.tvTaxVal);
        TextView tvTaxLabel = findViewById(R.id.tvTaxLabel);
        TextView tvBillSubTotal = findViewById(R.id.subtotal);
        //  TextView tvSaveAmt = findViewById(R.id.tvSaveAmt);
        tvBillTotItem = findViewById(R.id.totalitem);
        TextView tvBillTotQty = findViewById(R.id.tvtotalqty);
        TextView tvBillToPay = findViewById(R.id.tvnetamount);
        TextView tvCashDiscount = findViewById(R.id.tvcashdiscount);

        Getorder_Array_List = new ArrayList<>();
        Getorder_Array_List.clear();
        totalvalues = 0;
        totalQty = 0;
        cashDiscount = 0;
        taxVal = 0;


        for (int pm = 0; pm < Product_Modal.size(); pm++) {

            if (Product_Modal.get(pm).getRegularQty() != null) {
                if (Product_Modal.get(pm).getQty() > 0 || Product_Modal.get(pm).getRegularQty() > 0) {

                    cashDiscount += (int) Product_Modal.get(pm).getDiscount();

                    totalvalues += Product_Modal.get(pm).getAmount();

                    totalQty += (Product_Modal.get(pm).getQty()  * Product_Modal.get(pm).getCnvQty());//Product_Modal.get(pm).getRegularQty();

                    if (Product_Modal.get(pm).getTax() > 0)
                        taxVal += Product_Modal.get(pm).getTax();


                    Getorder_Array_List.add(Product_Modal.get(pm));


                }
            }
        }

        tvTotalAmount.setText("₹ " + formatter.format(totalvalues));
        tvTotalItems.setText("Items : " + Getorder_Array_List.size() + "   Qty : " + totalQty);

        if (Getorder_Array_List.size() == 1)
            tvTotLabel.setText("Price (1 item)");
        else
            tvTotLabel.setText("Price (" + Getorder_Array_List.size() + " items)");

        tvBillSubTotal.setText("₹ " + formatter.format(totalvalues));
        tvBillTotItem.setText("" + Getorder_Array_List.size());
        tvBillTotQty.setText("" + totalQty);
        tvBillToPay.setText("₹ " + formatter.format(totalvalues));
        tvCashDiscount.setText("₹ " + formatter.format(cashDiscount));
        // tvTax.setText("₹ " + formatter.format(taxVal));


//        if (cashDiscount > 0) {
//            tvSaveAmt.setVisibility(View.VISIBLE);
//            tvSaveAmt.setText("You will save ₹ " + formatter.format(cashDiscount) + " on this order");
//        } else
//            tvSaveAmt.setVisibility(View.GONE);
        orderTotTax = new ArrayList<>();
        orderTotTax.clear();

        for (int l = 0; l < Getorder_Array_List.size(); l++) {
            if (Getorder_Array_List.get(l).getProductDetailsModal() != null) {
                for (int tax = 0; tax < Getorder_Array_List.get(l).getProductDetailsModal().size(); tax++) {
                    String label = Getorder_Array_List.get(l).getProductDetailsModal().get(tax).getTax_Type();
                    Double amt = Getorder_Array_List.get(l).getProductDetailsModal().get(tax).getTax_Amt();
                    if (orderTotTax.size() == 0) {
                        orderTotTax.add(new Product_Details_Modal(label, amt));
                    } else {

                        boolean isDuplicate = false;
                        for (int totTax = 0; totTax < orderTotTax.size(); totTax++) {
                            if (orderTotTax.get(totTax).getTax_Type().equals(label)) {
                                double oldAmt = orderTotTax.get(totTax).getTax_Amt();
                                isDuplicate = true;
                                orderTotTax.set(totTax, new Product_Details_Modal(label, oldAmt + amt));

                            }
                        }

                        if (!isDuplicate) {
                            orderTotTax.add(new Product_Details_Modal(label, amt));

                        }
                    }

                }
            }
        }

        String label = "", amt = "";
        for (int i = 0; i < orderTotTax.size(); i++) {
            label = label + orderTotTax.get(i).getTax_Type() + "\n";
            amt = amt + "₹" + String.valueOf(formatter.format(orderTotTax.get(i).getTax_Amt())) + "\n";
        }
        tvTaxLabel.setText(label);
        tvTax.setText(amt);
        if (orderTotTax.size() == 0) {
            tvTaxLabel.setVisibility(View.INVISIBLE);
            tvTax.setVisibility(View.INVISIBLE);
        } else {
            tvTaxLabel.setVisibility(View.VISIBLE);
            tvTax.setVisibility(View.VISIBLE);
        }

        String data = gson.toJson(Product_Modal);
        sharedCommonPref.save(Constants.LOC_PROJECTION_DATA, data);

    }

    public void showOrderItemList(int categoryPos, String filterString) {
        categoryPos = selectedPos;
        Product_ModalSetAdapter.clear();
        for (Product_Details_Modal personNpi : Product_Modal) {
            if (personNpi.getProductCatCode().toString().equals(listt.get(categoryPos).getId())) {
                if (Common_Class.isNullOrEmpty(filterString))
                    Product_ModalSetAdapter.add(personNpi);
                else if (personNpi.getName().toLowerCase().contains(filterString.toLowerCase()))
                    Product_ModalSetAdapter.add(personNpi);
            }
        }
        lin_orderrecyclerview.setVisibility(View.VISIBLE);
        Category_Nametext.setVisibility(View.VISIBLE);
        Category_Nametext.setText(listt.get(categoryPos).getName());

        mProdct_Adapter = new Prodct_Adapter(Product_ModalSetAdapter, R.layout.product_projection_recyclerview, getApplicationContext(), categoryPos);
        recyclerView.setAdapter(mProdct_Adapter);

    }


    @Override
    public void onLoadDataUpdateUI(String apiDataResponse, String key) {
        try {
            switch (key) {
                case Constants.PreOrderQtyList:
                    // loadData(apiDataResponse);
                    Product_Modal = gson.fromJson(sharedCommonPref.getvalue(Constants.LOC_PROJECTION_DATA), userType);
                    showOrderList();

                    break;

                case Constants.PLANT_MASTER:
                    Log.v(key, apiDataResponse);

                    JSONObject obj = new JSONObject(apiDataResponse);

                    if (obj.getBoolean("success")) {
                        JSONArray arr = obj.getJSONArray("Data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            plantList.add(new Common_Model(data.getString("PlantName"), data.getString("PlantID")));
                        }
                    }

                    break;
            }
        } catch (Exception e) {

        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (Shared_Common_Pref.Projection_Approval == 1) {
                finish();
                Shared_Common_Pref.Projection_Approval = 0;
            }
            if (takeorder.getText().toString().equalsIgnoreCase("SUBMIT")) {
                moveProductScreen();
            } else {
                common_class.commonDialog(this, SFA_Activity.class, "Projection?");
            }
            return true;
        }
        return false;
    }

    void moveProductScreen() {
        lin_gridcategory.setVisibility(View.VISIBLE);
        findViewById(R.id.rlSearchParent).setVisibility(View.VISIBLE);
        findViewById(R.id.rlCategoryItemSearch).setVisibility(View.GONE);
        findViewById(R.id.llBillHeader).setVisibility(View.GONE);
        findViewById(R.id.llPayNetAmountDetail).setVisibility(View.GONE);
        takeorder.setText("PROCEED");
        showOrderItemList(selectedPos, "");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void OnclickMasterType(List<Common_Model> myDataset, int position, int type) {
        try {
            common_class.dismissCommonDialog(type);
            switch (type) {
                case 1:

//                    int qty = (int) (Product_ModalSetAdapter.get(uomPos).getQty() * Double.parseDouble((myDataset.get(position).getPhone())));
//                    if (Product_ModalSetAdapter.get(uomPos).getBalance() == null || Product_ModalSetAdapter.get(uomPos).getBalance() >= qty || Product_ModalSetAdapter.get(uomPos).getCheckStock() == null || Product_ModalSetAdapter.get(uomPos).getCheckStock() == 0) {
                    Product_ModalSetAdapter.get(uomPos).setCnvQty(Double.parseDouble((myDataset.get(position).getPhone())));
                    Product_ModalSetAdapter.get(uomPos).setUOM_Id(myDataset.get(position).getId());
                    Product_ModalSetAdapter.get(uomPos).setUOM_Nm(myDataset.get(position).getName());
                    mProdct_Adapter.notify(Product_ModalSetAdapter, R.layout.product_projection_recyclerview, getApplicationContext(), 1);
//                    } else {
//                        common_class.showMsg(this, "Can't exceed Stock");
//                    }
                    break;
                case 2:
                    tvPlant.setText("" + myDataset.get(position).getName());
                    plantId = myDataset.get(position).getId();
                    break;

            }
        } catch (Exception e) {

        }
    }

    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
        Context context;
        MyViewHolder pholder;

        public CategoryAdapter(Context applicationContext, List<Category_Universe_Modal> list) {
            this.context = applicationContext;
            listt = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.category_order_horizantal_universe_gridview, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            try {
                holder.icon.setText(listt.get(position).getName());
                if (!listt.get(position).getCatImage().equalsIgnoreCase("")) {
                    holder.ivCategoryIcon.clearColorFilter();
                    Glide.with(this.context)
                            .load(listt.get(position).getCatImage())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.ivCategoryIcon);
                } else {
                    holder.ivCategoryIcon.setImageDrawable(getResources().getDrawable(R.drawable.product_logo));
                    holder.ivCategoryIcon.setColorFilter(getResources().getColor(R.color.grey_500));
                }

                holder.gridcolor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (pholder != null) {
                            pholder.gridcolor.setBackground(getResources().getDrawable(R.drawable.cardbutton));
                            pholder.icon.setTextColor(getResources().getColor(R.color.black));
                            pholder.icon.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                            pholder.undrCate.setVisibility(View.GONE);
                        }
                        pholder = holder;
                        selectedPos = position;
                        showOrderItemList(position, "");
                        holder.gridcolor.setBackground(getResources().getDrawable(R.drawable.cardbtnprimary));
                        holder.icon.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        holder.icon.setTypeface(Typeface.DEFAULT_BOLD);
                        holder.undrCate.setVisibility(View.VISIBLE);
                    }
                });


                if (position == selectedPos) {

                    holder.gridcolor.setBackground(getResources().getDrawable(R.drawable.cardbtnprimary));
                    holder.icon.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    holder.icon.setTypeface(Typeface.DEFAULT_BOLD);
                    holder.undrCate.setVisibility(View.VISIBLE);
                    pholder = holder;
                } else {
                    holder.gridcolor.setBackground(getResources().getDrawable(R.drawable.cardbutton));
                    holder.icon.setTextColor(getResources().getColor(R.color.black));
                    holder.icon.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));

                }


            } catch (Exception e) {
                Log.e(TAG, "adapterProduct: " + e.getMessage());
            }


        }

        @Override
        public int getItemCount() {
            return listt.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public LinearLayout gridcolor, undrCate;
            TextView icon;
            ImageView ivCategoryIcon;

            public MyViewHolder(View view) {
                super(view);

                icon = view.findViewById(R.id.textView);
                gridcolor = view.findViewById(R.id.gridcolor);
                ivCategoryIcon = view.findViewById(R.id.ivCategoryIcon);
                undrCate = view.findViewById(R.id.undrCate);

            }
        }


    }

    public class Prodct_Adapter extends RecyclerView.Adapter<Prodct_Adapter.MyViewHolder> {
        Context context;
        int CategoryType;
        private List<Product_Details_Modal> Product_Details_Modalitem;
        private int rowLayout;


        public Prodct_Adapter(List<Product_Details_Modal> Product_Details_Modalitem, int rowLayout, Context context, int categoryType) {
            this.Product_Details_Modalitem = Product_Details_Modalitem;
            this.rowLayout = rowLayout;
            this.context = context;
            this.CategoryType = categoryType;

        }

        public void notify(List<Product_Details_Modal> Product_Details_Modalitem, int rowLayout, Context context, int categoryType) {
            this.Product_Details_Modalitem = Product_Details_Modalitem;
            this.rowLayout = rowLayout;
            this.context = context;
            this.CategoryType = categoryType;
            notifyDataSetChanged();

        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            try {
                Product_Details_Modal Product_Details_Modal = Product_Details_Modalitem.get(holder.getAdapterPosition());

                holder.productname.setText("" + Product_Details_Modal.getName().toUpperCase());
                if (!Common_Class.isNullOrEmpty(Product_Details_Modal.getUOM_Nm()))
                    holder.tvUOM.setText(Product_Details_Modal.getUOM_Nm());
                else {
                    holder.tvUOM.setText(Product_Details_Modal.getDefault_UOM_Name());
                    Product_Details_Modalitem.get(holder.getAdapterPosition()).setUOM_Nm(Product_Details_Modal.getDefault_UOM_Name());
                    Product_Details_Modalitem.get(holder.getAdapterPosition()).setUOM_Id("" + Product_Details_Modal.getDefaultUOM());
                    Product_Details_Modalitem.get(holder.getAdapterPosition()).setCnvQty(Product_Details_Modal.getDefaultUOMQty());


                }

                holder.Rate.setText("₹" + formatter.format(Product_Details_Modal.getRate()));
                holder.Amount.setText("₹" + new DecimalFormat("##0.00").format(Product_Details_Modal.getAmount()));
                holder.RegularQty.setText("" + Product_Details_Modal.getRegularQty());


                if (CategoryType >= 0) {
                    if (Product_Details_Modal.getPlant() == null) {
                        Product_Details_Modal.setPlant("");
                        Product_Details_Modal.setPlantId("");
                    }
                    holder.tvPlant.setText(Product_Details_Modal.getPlant());

                    holder.totalQty.setText("" + ((
                            (Product_Details_Modalitem.get(holder.getAdapterPosition()).getQty()) * Product_Details_Modalitem.get(holder.getAdapterPosition()).getCnvQty())));

                    if (!Product_Details_Modal.getPImage().equalsIgnoreCase("")) {
                        holder.ImgVwProd.clearColorFilter();
                        Glide.with(this.context)
                                .load(Product_Details_Modal.getPImage())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(holder.ImgVwProd);
                    } else {
                        holder.ImgVwProd.setImageDrawable(getResources().getDrawable(R.drawable.product_logo));
                        holder.ImgVwProd.setColorFilter(getResources().getColor(R.color.grey_500));
                    }


                    holder.regularAmt.setText("₹" + new DecimalFormat("##0.00").format(Product_Details_Modal.getRegularQty() * Product_Details_Modalitem.get(holder.getAdapterPosition()).getRate()));

                    holder.QtyAmt.setText("₹" + formatter.format(Product_Details_Modal.getRate() * Product_Details_Modal.getQty()));


                    holder.tvPlant.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            plantPos = position;
                            common_class.showCommonDialog(plantList, 2, ProjectionCategorySelectActivity.this);
                        }
                    });

                    holder.rlUOM.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            uomPos = position;
                            uomList = new ArrayList<>();

                            if (Product_Details_Modal.getUOMList() != null && Product_Details_Modal.getUOMList().size() > 0) {
                                for (int i = 0; i < Product_Details_Modal.getUOMList().size(); i++) {
                                    com.saneforce.milksales.SFA_Model_Class.Product_Details_Modal.UOM uom = Product_Details_Modal.getUOMList().get(i);
                                    uomList.add(new Common_Model(uom.getUOM_Nm(), uom.getUOM_Id(), "", "", String.valueOf(uom.getCnvQty())));

                                }
                                common_class.showCommonDialog(uomList, 1, ProjectionCategorySelectActivity.this);
                            } else {
                                common_class.showMsg(ProjectionCategorySelectActivity.this, "No Records Found.");
                            }
                        }
                    });
                }

                holder.tvTaxLabel.setText("₹" + formatter.format(Product_Details_Modal.getTax()));
                if (Product_Details_Modal.getQty() > 0)
                    holder.Qty.setText("" + Product_Details_Modal.getQty());

                if (Common_Class.isNullOrEmpty(Product_Details_Modal.getFree()))
                    holder.Free.setText("0");
                else
                    holder.Free.setText("" + Product_Details_Modal.getFree());


                holder.Disc.setText("₹" + formatter.format(Product_Details_Modal.getDiscount()));


                holder.QtyPls.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String sVal = holder.Qty.getText().toString();
                        if (sVal.equalsIgnoreCase("")) sVal = "0";
                        holder.Qty.setText(String.valueOf(Integer.parseInt(sVal) + 1));
                    }
                });
                holder.QtyMns.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String sVal = holder.Qty.getText().toString();
                        if (sVal.equalsIgnoreCase("")) sVal = "0";
                        if (Integer.parseInt(sVal) > 0) {
                            holder.Qty.setText(String.valueOf(Integer.parseInt(sVal) - 1));
                        }
                    }
                });


                holder.Qty.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence charSequence, int start,
                                              int before, int count) {
                        try {

                            double enterQty = 0;
                            if (!charSequence.toString().equals(""))
                                enterQty = Double.valueOf(charSequence.toString());

                            double totQty = (enterQty * Product_Details_Modalitem.get(holder.getAdapterPosition()).getCnvQty());


                            Product_Details_Modalitem.get(holder.getAdapterPosition()).setQty((int) enterQty);
                            holder.Amount.setText("₹" + new DecimalFormat("##0.00").format(totQty * Product_Details_Modalitem.get(holder.getAdapterPosition()).getRate()));
                            Product_Details_Modalitem.get(holder.getAdapterPosition()).setAmount(Double.valueOf(formatter.format(totQty *
                                    Product_Details_Modalitem.get(holder.getAdapterPosition()).getRate())));
                            if (CategoryType >= 0) {
                                holder.QtyAmt.setText("₹" + formatter.format(enterQty * Product_Details_Modalitem.get(holder.getAdapterPosition()).getRate()));
                                holder.totalQty.setText("" + (int) totQty);


                            }


                            String strSchemeList = sharedCommonPref.getvalue(Constants.FreeSchemeDiscList);

                            Type type = new TypeToken<ArrayList<Product_Details_Modal>>() {
                            }.getType();
                            List<Product_Details_Modal> product_details_modalArrayList = gson.fromJson(strSchemeList, type);

                            double highestScheme = 0;
                            boolean haveVal = false;
                            if (totQty > 0 && product_details_modalArrayList != null && product_details_modalArrayList.size() > 0) {

                                for (int i = 0; i < product_details_modalArrayList.size(); i++) {

                                    if (Product_Details_Modal.getId().equals(product_details_modalArrayList.get(i).getId())) {

                                        haveVal = true;
                                        double schemeVal = Double.parseDouble(product_details_modalArrayList.get(i).getScheme());

                                        Product_Details_Modalitem.get(holder.getAdapterPosition()).setOff_Pro_code(product_details_modalArrayList.get(i).getOff_Pro_code());
                                        Product_Details_Modalitem.get(holder.getAdapterPosition()).setOff_Pro_name(product_details_modalArrayList.get(i).getOff_Pro_name());
                                        Product_Details_Modalitem.get(holder.getAdapterPosition()).setOff_Pro_Unit(product_details_modalArrayList.get(i).getOff_Pro_Unit());
                                        Product_Details_Modalitem.get(holder.getAdapterPosition()).setFree_val(product_details_modalArrayList.get(i).getFree());

                                        Product_Details_Modalitem.get(holder.getAdapterPosition()).setDiscount_value(String.valueOf(product_details_modalArrayList.get(i).getDiscount()));
                                        Product_Details_Modalitem.get(holder.getAdapterPosition()).setDiscount_type(product_details_modalArrayList.get(i).getDiscount_type());


                                        if (totQty >= schemeVal) {

                                            if (schemeVal > highestScheme) {
                                                highestScheme = schemeVal;


                                                if (!product_details_modalArrayList.get(i).getFree().equals("0")) {
                                                    if (product_details_modalArrayList.get(i).getPackage().equals("N")) {
                                                        double freePer = (totQty / highestScheme);

                                                        double freeVal = freePer * Double.parseDouble(product_details_modalArrayList.
                                                                get(i).getFree());

                                                        Product_Details_Modalitem.get(holder.getAdapterPosition()).setFree(String.valueOf(Math.round(freeVal)));
                                                    } else {
                                                        int val = (int) (totQty / highestScheme);
                                                        int freeVal = val * Integer.parseInt(product_details_modalArrayList.get(i).getFree());
                                                        Product_Details_Modalitem.get(holder.getAdapterPosition()).setFree(String.valueOf(freeVal));
                                                    }
                                                } else {

                                                    holder.Free.setText("0");
                                                    Product_Details_Modalitem.get(holder.getAdapterPosition()).setFree("0");

                                                }


                                                if (product_details_modalArrayList.get(i).getDiscount() != 0) {

                                                    if (product_details_modalArrayList.get(i).getDiscount_type().equals("%")) {
                                                        double discountVal = totQty * (((product_details_modalArrayList.get(i).getDiscount()
                                                        )) / 100);


                                                        Product_Details_Modalitem.get(holder.getAdapterPosition()).setDiscount((Math.round(discountVal)));

                                                    } else {
                                                        //Rs
                                                        if (product_details_modalArrayList.get(i).getPackage().equals("N")) {
                                                            double freePer = (totQty / highestScheme);

                                                            double freeVal = freePer * (product_details_modalArrayList.
                                                                    get(i).getDiscount());

                                                            Product_Details_Modalitem.get(holder.getAdapterPosition()).setDiscount((Math.round(freeVal)));
                                                        } else {
                                                            int val = (int) (totQty / highestScheme);
                                                            double freeVal = (double) (val * (product_details_modalArrayList.get(i).getDiscount()));
                                                            Product_Details_Modalitem.get(holder.getAdapterPosition()).setDiscount((freeVal));
                                                        }
                                                    }


                                                } else {
                                                    holder.Disc.setText("₹0.00");
                                                    Product_Details_Modalitem.get(holder.getAdapterPosition()).setDiscount(0.00);

                                                }


                                            }

                                        } else {
                                            holder.Free.setText("0");
                                            Product_Details_Modalitem.get(holder.getAdapterPosition()).setFree("0");

                                            holder.Disc.setText("₹0.00");
                                            Product_Details_Modalitem.get(holder.getAdapterPosition()).setDiscount(0.00);


                                        }


                                    }

                                }


                            }

                            if (!haveVal) {
                                holder.Free.setText("0");
                                Product_Details_Modalitem.get(holder.getAdapterPosition()).setFree("0");

                                holder.Disc.setText("₹0.00");
                                Product_Details_Modalitem.get(holder.getAdapterPosition()).setDiscount(0.00);

                                Product_Details_Modalitem.get(holder.getAdapterPosition()).setOff_Pro_code("");
                                Product_Details_Modalitem.get(holder.getAdapterPosition()).setOff_Pro_name("");
                                Product_Details_Modalitem.get(holder.getAdapterPosition()).setOff_Pro_Unit("");

                                Product_Details_Modalitem.get(holder.getAdapterPosition()).setDiscount_value("0.00");
                                Product_Details_Modalitem.get(holder.getAdapterPosition()).setDiscount_type("");


                            } else {

                                Product_Details_Modalitem.get(holder.getAdapterPosition()).setAmount((Product_Details_Modalitem.get(holder.getAdapterPosition()).getAmount()) -
                                        (Product_Details_Modalitem.get(holder.getAdapterPosition()).getDiscount()));

                                holder.Free.setText("" + Product_Details_Modalitem.get(holder.getAdapterPosition()).getFree());
                                holder.Disc.setText("₹" + formatter.format(Product_Details_Modalitem.get(holder.getAdapterPosition()).getDiscount()));

                                holder.Amount.setText("₹" + formatter.format(Product_Details_Modalitem.get(holder.getAdapterPosition()).getAmount()));


                            }
                            sumofTax(Product_Details_Modalitem, holder.getAdapterPosition());
                            holder.Amount.setText("₹" + formatter.format(Product_Details_Modalitem.get(holder.getAdapterPosition()).getAmount()));
                            holder.tvTaxLabel.setText("₹" + formatter.format(Product_Details_Modalitem.get(holder.getAdapterPosition()).getTax()));

                            updateToTALITEMUI();

//                            if (CategoryType == -1) {
//                                String amt = holder.Amount.getText().toString();
//                                Log.v(TAG + position, ":OUT:amt:" + amt);
//                                if (amt.equals("₹0.00")) {
//                                    Log.v(TAG + position, ":IN:amt:" + amt);
//                                    Product_Details_Modalitem.remove(position);
//                                    notifyDataSetChanged();
//                                }
//
//                                showFreeQtyList();
//                            }

                        } catch (Exception e) {
                            Log.v(TAG, " orderAdapter:qty " + e.getMessage());
                        }


                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {


                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


                updateToTALITEMUI();
            } catch (Exception e) {
                Log.e(TAG, "adapterProduct: " + e.getMessage());
            }


        }

        @Override
        public int getItemCount() {
            return Product_Details_Modalitem.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView productname, Rate, Amount, Disc, Free, RegularQty, lblRQty, productQty, regularAmt,
                    QtyAmt, totalQty, tvTaxLabel, tvUOM, tvPlant;
            ImageView ImgVwProd, QtyPls, QtyMns;
            EditText Qty;

            LinearLayout llRegular, rlUOM;

            public MyViewHolder(View view) {
                super(view);
                productname = view.findViewById(R.id.productname);
                QtyPls = view.findViewById(R.id.ivQtyPls);
                QtyMns = view.findViewById(R.id.ivQtyMns);
                Rate = view.findViewById(R.id.Rate);
                Qty = view.findViewById(R.id.Qty);
                RegularQty = view.findViewById(R.id.RegularQty);
                Amount = view.findViewById(R.id.Amount);
                Free = view.findViewById(R.id.Free);
                Disc = view.findViewById(R.id.Disc);
                tvTaxLabel = view.findViewById(R.id.tvTaxTotAmt);
                llRegular = view.findViewById(R.id.llRegular);
                tvUOM = view.findViewById(R.id.tvUOM);


                if (CategoryType >= 0) {
                    tvPlant = view.findViewById(R.id.tvPlant);
                    ImgVwProd = view.findViewById(R.id.ivAddShoppingCart);
                    lblRQty = view.findViewById(R.id.status);
                    regularAmt = view.findViewById(R.id.RegularAmt);
                    QtyAmt = view.findViewById(R.id.qtyAmt);
                    totalQty = view.findViewById(R.id.totalqty);
                    rlUOM = view.findViewById(R.id.rlUOM);
                }


            }
        }


    }


}