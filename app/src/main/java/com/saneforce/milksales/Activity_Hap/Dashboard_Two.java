package com.saneforce.milksales.Activity_Hap;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.saneforce.milksales.Activity.TAClaimActivity;
import com.saneforce.milksales.Common_Class.AlertDialogBox;
import com.saneforce.milksales.Common_Class.Constants;
import com.saneforce.milksales.Common_Class.Shared_Common_Pref;
import com.saneforce.milksales.Interface.AlertBox;
import com.saneforce.milksales.Interface.ApiClient;
import com.saneforce.milksales.Interface.ApiInterface;
import com.saneforce.milksales.Interface.GateEntryQREvents;
import com.saneforce.milksales.Interface.onListItemClick;
import com.saneforce.milksales.R;
import com.saneforce.milksales.Status_Activity.View_All_Status_Activity;
import com.saneforce.milksales.adapters.GateAdapter;
import com.saneforce.milksales.adapters.HomeRptRecyler;
import com.saneforce.milksales.adapters.OffersAdapter;
import com.saneforce.milksales.common.AlmReceiver;
import com.saneforce.milksales.common.DatabaseHandler;
import com.saneforce.milksales.common.SANGPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard_Two extends AppCompatActivity implements View.OnClickListener/*, Main_Model.MasterSyncView*/ {
    private static String Tag = "HAP_Check-In";
    SharedPreferences CheckInDetails;
    SharedPreferences UserDetails;
    public static final String CheckInDetail = "CheckInDetail";
    public static final String UserDetail = "MyPrefs";
    String[] mns = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    Shared_Common_Pref mShared_common_pref;
    GateEntryQREvents GateEvents;
    private RecyclerView recyclerView;
    private HomeRptRecyler mAdapter;
    String viewMode = "", sSFType = "", mPriod = "0";
    int cModMnth = 1;
    Button viewButton;
    Button StActivity, cardview3, cardview4, cardView5, btnCheckout, btnApprovals, btnExit;
    String AllowancePrefernce = "";
    ImageView btMyQR,btnCloseOffer;
    LinearLayout linOffer;
    public static final String mypreference = "mypref";
    public static final String Name = "Allowance";
    public static final String MOT = "ModeOfTravel";
    public static final String SKM = "Started_km";

    String PrivacyScreen = "", ModeOfTravel = "";
    SharedPreferences sharedpreferences;

    public static final String hapLocation = "hpLoc";
    public static final String otherLocation = "othLoc";
    public static final String visitPurpose = "vstPur";
    public static final String modeTravelId = "ShareModesss";
    public static final String modeTypeVale = "SharedModeTypeValesss";
    public static final String modeFromKm = "SharedFromKmsss";
    public static final String modeToKm = "SharedToKmsss";
    public static final String StartedKm = "StartedKMsss";

    RecyclerView mRecyclerView,ryclOffers;
    /*String Mode = "Bus";*/
    Button btnGateIn, btnGateOut;
    ImageView mvPrvMn, mvNxtMn;
    GateAdapter gateAdap;
    CardView cardGateDet;
    String dashMdeCnt = "";
    String datefrmt = "";
    TextView TxtEmpId, txDesgName, txHQName, txDeptName, txRptName,tvapprCnt,lblSlideNo;

    Common_Class DT = new Common_Class();
    private ShimmerFrameLayout mShimmerViewContainer;
    int LoadingCnt = 0;
    String TAG = "Dashboard_Two:LOG ";
    DatabaseHandler db;
    private String key;
    Gson gson;
    private String checkInUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_dashboard__two);
            mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
            mShimmerViewContainer.startShimmerAnimation();
            db = new DatabaseHandler(this);
            gson = new Gson();


            mShared_common_pref = new Shared_Common_Pref(this);
            mShared_common_pref.save("Dashboard", "one");
            sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

            if (sharedpreferences.contains("SharedMode")) {
                dashMdeCnt = sharedpreferences.getString("SharedMode", "");
                Log.e("Privacypolicy_MODE", dashMdeCnt);
            }

            datefrmt = com.saneforce.milksales.Common_Class.Common_Class.GetDateOnly();
            Log.v("DATE_FORMAT_ONLY", datefrmt);

            btMyQR = findViewById(R.id.myQR);
            TextView txtHelp = findViewById(R.id.toolbar_help);
            ImageView imgHome = findViewById(R.id.toolbar_home);
            txtHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), Help_Activity.class));
                }
            });

            CheckInDetails = getSharedPreferences(CheckInDetail, Context.MODE_PRIVATE);
            UserDetails = getSharedPreferences(UserDetail, Context.MODE_PRIVATE);

            TxtEmpId = findViewById(R.id.txt_emp_id);
            TxtEmpId.setText(UserDetails.getString("EmpId", ""));
            txHQName = findViewById(R.id.txHQName);
            txDesgName = findViewById(R.id.txDesgName);
            txDeptName = findViewById(R.id.txDeptName);
            txRptName = findViewById(R.id.txRptName);
            txHQName.setText(UserDetails.getString("DesigNm", ""));

            gson = new Gson();

            linOffer=findViewById(R.id.linOffer);
            linOffer.setVisibility(View.GONE);
            ryclOffers= findViewById(R.id.ryclOffers);
            lblSlideNo =findViewById(R.id.lblSlideNo);
            btnCloseOffer =findViewById(R.id.btnCloseOffer);

//        txHQName.setText(UserDetails.getString("SFHQ",""));
//        txDesgName.setText(UserDetails.getString("SFDesig",""));
//        txDeptName.setText(UserDetails.getString("DepteNm",""));
            //txRptName.setText(UserDetails.getString("SFRptName",""));

            TextView txtErt = findViewById(R.id.toolbar_ert);
            TextView txtPlaySlip = findViewById(R.id.toolbar_play_slip);
            txtErt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), ERT.class));
                }
            });
            txtPlaySlip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), PayslipFtp.class));
                }
            });

            btMyQR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Dashboard_Two.this, CateenToken.class);
                    startActivity(intent);
                }
            });
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dpln = new SimpleDateFormat("yyyy-MM-dd");
            String plantime = dpln.format(c.getTime());

            gatevalue(plantime);
            QRCodeScanner.bindEvents(new GateEntryQREvents() {
                @Override
                public void RefreshGateEntrys() {
                    gatevalue(plantime);
                }
            });
            ObjectAnimator textColorAnim;
            textColorAnim = ObjectAnimator.ofInt(txtErt, "textColor", Color.WHITE, Color.TRANSPARENT);
            textColorAnim.setDuration(500);
            textColorAnim.setEvaluator(new ArgbEvaluator());
            textColorAnim.setRepeatCount(ValueAnimator.INFINITE);
            textColorAnim.setRepeatMode(ValueAnimator.REVERSE);
            textColorAnim.start();

            imgHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!viewMode.equalsIgnoreCase("CIN"))
                        startActivity(new Intent(getApplicationContext(), Dashboard.class));
                }
            });

            TextView txUserName = findViewById(R.id.txUserName);
            String sUName = UserDetails.getString("SfName", "");
            txUserName.setText("HI! " + sUName);
            sSFType = UserDetails.getString("Sf_Type", "");
            Log.d("CINDetails", CheckInDetails.toString());
            cardview3 = findViewById(R.id.cardview3);
            cardview4 = findViewById(R.id.btn_da_exp_entry);
            cardView5 = findViewById(R.id.cardview5);
            btnApprovals = findViewById(R.id.approvals);
            tvapprCnt = findViewById(R.id.approvalcount);
            mPriod = "0";
            mvNxtMn = findViewById(R.id.nxtMn);
            mvPrvMn = findViewById(R.id.prvMn);
            mvNxtMn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPriod == "-1") {
                        mPriod = "0";
                        getMnthReports(0);
                    }
                }
            });

            mvPrvMn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPriod == "0") {
                        mPriod = "-1";
                        getMnthReports(-1);
                    }
                }
            });
            cardGateDet = findViewById(R.id.cardGateDet);
            btnGateIn = findViewById(R.id.btn_gate_in);
            btnGateOut = findViewById(R.id.btn_gate_out);

            mRecyclerView = findViewById(R.id.gate_recycle);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(layoutManager);
            //mRecyclerView.stopScroll();


            StActivity = findViewById(R.id.StActivity);
            btnCheckout = findViewById(R.id.btnCheckout);
            btnExit = findViewById(R.id.btnExit);

            cardview3.setOnClickListener(this);
            cardview4.setOnClickListener(this);
            cardView5.setOnClickListener(this);
            StActivity.setOnClickListener(this);
            btnCheckout.setOnClickListener(this);
            btnExit.setOnClickListener(this);
            btnGateIn.setOnClickListener(this);
            btnGateOut.setOnClickListener(this);
            btnApprovals.setOnClickListener(this);
            btnGateIn.setVisibility(View.GONE);
            btnGateOut.setVisibility(View.GONE);
            cardGateDet.setVisibility(View.GONE);
            StActivity.setVisibility(View.VISIBLE);

            if (UserDetails.getInt("CheckCount", 0) <= 0) {
                btnApprovals.setVisibility(View.GONE);
                tvapprCnt.setVisibility(View.GONE);
            } else {
                btnApprovals.setVisibility(View.VISIBLE);
                tvapprCnt.setVisibility(View.VISIBLE);
            }
            btnExit.setVisibility(View.GONE);
            if (getIntent().getExtras() != null) {
                Bundle params = getIntent().getExtras();
                viewMode = params.getString("Mode");

                if (viewMode.equalsIgnoreCase("CIN") || viewMode.equalsIgnoreCase("extended")) {
                    cardview3.setVisibility(View.VISIBLE);
                    cardview4.setVisibility(View.VISIBLE);
                    //cardView5.setVisibility(View.VISIBLE);
                    btnCheckout.setVisibility(View.VISIBLE);
                    if(viewMode.equalsIgnoreCase("extended")){
                        btnCheckout.setText("Checkout & Sent to Approval");
                    }
                } else {
                    //cardview3.setVisibility(View.GONE);
                    // cardview4.setVisibility(View.GONE);
                    cardView5.setVisibility(View.GONE);
                    //StActivity.setVisibility(View.GONE);
                    btnCheckout.setVisibility(View.GONE);
                    btnExit.setVisibility(View.VISIBLE);
                    //               btnApprovals.setVisibility(View.GONE);
                }
            } else {
                //cardview3.setVisibility(View.GONE);
                //cardview4.setVisibility(View.GONE);
                cardView5.setVisibility(View.GONE);
                //StActivity.setVisibility(View.GONE);
                btnCheckout.setVisibility(View.GONE);
            }
            if (sSFType.equals("0"))
                StActivity.setVisibility(View.GONE);

            Log.v("GATE:", CheckInDetails.getString("On_Duty_Flag", "0") + " :sfType:" + sSFType);

            if (Integer.parseInt(CheckInDetails.getString("On_Duty_Flag", "0")) > 0 || sSFType.equals("1")) {
                btnGateIn.setVisibility(View.VISIBLE);
                btnGateOut.setVisibility(View.VISIBLE);
                cardGateDet.setVisibility(View.VISIBLE);
            }

            String ChkOutTm = CheckInDetails.getString("ShiftEnd", "");
            if (!ChkOutTm.equalsIgnoreCase("")) {
                long AlrmTime = DT.getDate(ChkOutTm).getTime();
                long cTime = DT.GetCurrDateTime(Dashboard_Two.this).getTime();
                if (AlrmTime > cTime) {
                    sendAlarmNotify(1001, AlrmTime, "Check-In", "Check-Out Alert !.");
                }
            }


            viewButton = findViewById(R.id.button3);
            viewButton.setOnClickListener(this);
            ImageView backView = findViewById(R.id.imag_back);
            backView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnBackPressedDispatcher.onBackPressed();
                }
            });

            String currentDate = DT.getDateWithFormat(new Date(), "dd/MM/yyyy");
            String loginDate = mShared_common_pref.getvalue(Constants.LOGIN_DATE);
            if (!loginDate.equalsIgnoreCase(currentDate)) {
                mShared_common_pref.clear_pref(Constants.DB_TWO_GET_NOTIFY);
                mShared_common_pref.clear_pref(Constants.DB_TWO_GET_DYREPORTS);
                Common_Class Dt = new Common_Class();
                String sDt = Dt.GetDateTime(getApplicationContext(), "yyyy-MM-dd HH:mm:ss");
                if (Dt.getDay(sDt) < 23) {
                    sDt = Dt.AddMonths(sDt, -1, "yyyy-MM-dd HH:mm:ss");
                }
                int fmn = Dt.getMonth(sDt);
                mShared_common_pref.clear_pref(Constants.DB_TWO_GET_MREPORTS+"_"+mns[fmn - 1]);
            }
        } catch (Exception e) {
            Log.d("Error Loading:",e.getMessage().toString());
        }
        getNotify();
        getDyReports();
        getMnthReports(0);
        GetMissedPunch();
        getcountdetails();
        btnCloseOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linOffer.setVisibility(View.GONE);
            }
        });
    }

    private void getOfferNotify() {
        if (com.saneforce.milksales.Common_Class.Common_Class.isNullOrEmpty(mShared_common_pref.getvalue(Constants.DB_SFWish_NOTIFY))) {
            Map<String, String> QueryString = new HashMap<>();
            QueryString.put("axn", "get/sfwishnotify");
            QueryString.put("SFCode", UserDetails.getString("Sfcode", ""));
            QueryString.put("divisionCode", UserDetails.getString("Divcode", ""));
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonArray> rptCall = apiInterface.getDataArrayList(QueryString, null);
            rptCall.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    try {
                        JsonArray res = response.body();
                        Log.d("getOfferNotify", String.valueOf(response.body()));
                        //  Log.d("NotifyMsg", response.body().toString());
                        JSONArray sArr=new JSONArray(String.valueOf(response.body()));
                        assignOffGetNotify(sArr);
                        mShared_common_pref.save(Constants.DB_SFWish_NOTIFY, gson.toJson(response.body()));
                    } catch (Exception e) {

                    }

                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {

                    Log.d("Tag", String.valueOf(t));
                }
            });

        } else {
//            try {
//                JSONArray sArr=new JSONArray(String.valueOf(mShared_common_pref.getvalue(Constants.DB_SFWish_NOTIFY)));
//                assignOffGetNotify(sArr);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }
    void assignOffGetNotify(JSONArray res) {
        JSONArray fRes= res;
        if (fRes.length()>0){
            LinearLayoutManager TypgridlayManager = new LinearLayoutManager(this);
            TypgridlayManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            ryclOffers.setLayoutManager(TypgridlayManager);
            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(ryclOffers);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ryclOffers.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                        LinearLayoutManager layoutManager = ((LinearLayoutManager)ryclOffers.getLayoutManager());
                        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                        lblSlideNo.setText((firstVisiblePosition+1)+"/"+fRes.length());
                    }
                });
            }else{
                ryclOffers.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        LinearLayoutManager layoutManager = ((LinearLayoutManager)ryclOffers.getLayoutManager());
                        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                        lblSlideNo.setText((firstVisiblePosition+1)+"/"+fRes.length());
                    }
                });
            }
            OffersAdapter TyplistItems = new OffersAdapter(fRes, this, new onListItemClick() {
                @Override
                public void onItemClick(JSONObject item) {
                    try {
                        //GetJsonData(String.valueOf(db.getMasterData(Constants.Category_List)), "1", item.getString("id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            ryclOffers.setAdapter(TyplistItems);
            linOffer.setVisibility(View.VISIBLE);
            mShared_common_pref.save(Constants.DB_OfferShownOn, com.saneforce.milksales.Common_Class.Common_Class.GetDatewothouttime());
        }
    }
    private void hideShimmer() {
        if (LoadingCnt >= 2) {
            mShimmerViewContainer.stopShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.GONE);
        }
    }

    private void getNotify() {
        if (Common_Class.isNullOrEmpty(mShared_common_pref.getvalue(Constants.DB_TWO_GET_NOTIFY))) {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonArray> rptCall = apiInterface.getDataArrayList("get/notify",
                    UserDetails.getString("Divcode", ""),
                    UserDetails.getString("Sfcode", ""), "", "", null);
            rptCall.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    try {
                        JsonArray res = response.body();
                        Log.d(TAG + "getNotify", String.valueOf(response.body()));

                        //  Log.d("NotifyMsg", response.body().toString());
                        assignGetNotify(res);
                        mShared_common_pref.save(Constants.DB_TWO_GET_NOTIFY, gson.toJson(response.body()));
                    } catch (Exception e) {

                    }

                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {

                    Log.d(Tag, String.valueOf(t));
                }
            });
        } else {
            Type userType = new TypeToken<JsonArray>() {
            }.getType();
            JsonArray arr = (gson.fromJson(mShared_common_pref.getvalue(Constants.DB_TWO_GET_NOTIFY), userType));
            assignGetNotify(arr);
        }
    }


    void assignGetNotify(JsonArray res) {
        TextView txt = findViewById(R.id.MRQtxt);
        txt.setText("");
        txt.setVisibility(View.GONE);
        String sMsg = "";
        txt.setSelected(true);
        for (int il = 0; il < res.size(); il++) {
            JsonObject Itm = res.get(il).getAsJsonObject();
            sMsg += Itm.get("NtfyMsg").getAsString();
        }
        if (!sMsg.equalsIgnoreCase("")) {
            txt.setText(Html.fromHtml(sMsg));
            txt.setVisibility(View.VISIBLE);
        }
    }

    private void getMnthReports(int m) {
        if (cModMnth == m) return;
        Common_Class Dt = new Common_Class();
        String sDt = Dt.GetDateTime(getApplicationContext(), "yyyy-MM-dd HH:mm:ss");
        Date dt = Dt.getDate(sDt);
        if (m == -1) {
            sDt = Dt.AddMonths(sDt, -1, "yyyy-MM-dd HH:mm:ss");
        }
        if (Dt.getDay(sDt) < 23) {
            sDt = Dt.AddMonths(sDt, -1, "yyyy-MM-dd HH:mm:ss");
        }
        int fmn = Dt.getMonth(sDt);
        if (m == -1) {
            mShared_common_pref.clear_pref(Constants.DB_TWO_GET_MREPORTS+"_"+mns[fmn - 1]);
        }
        sDt = Dt.AddMonths(Dt.getYear(sDt) + "-" + Dt.getMonth(sDt) + "-22 00:00:00", 1, "yyyy-MM-dd HH:mm:ss");
        int tmn = Dt.getMonth(sDt);
        Log.d(Tag, sDt + "-" + String.valueOf(fmn) + "-" + String.valueOf(tmn));
        TextView txUserName = findViewById(R.id.txtMnth);
        txUserName.setText("23," + mns[fmn - 1] + " - 22," + mns[tmn - 1]);

        // appendDS = appendDS + "&divisionCode=" + userData.divisionCode + "&sfCode=" + sSF + "&rSF=" + userData.sfCode + "&State_Code=" + userData.State_Code;
        if (Common_Class.isNullOrEmpty(mShared_common_pref.getvalue(Constants.DB_TWO_GET_MREPORTS+"_"+mns[fmn - 1]))) {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonArray> rptMnCall = apiInterface.getDataArrayList("get/AttndMn", m,
                    UserDetails.getString("Divcode", ""),
                    UserDetails.getString("Sfcode", ""), UserDetails.getString("Sfcode", ""), "", "", null);
            rptMnCall.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    assignMnthReports(response.body(), m);
                    mShared_common_pref.save(Constants.DB_TWO_GET_MREPORTS+"_"+mns[fmn - 1], gson.toJson(response.body()));

                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    Log.d(Tag, String.valueOf(t));
                    LoadingCnt++;
                    hideShimmer();
                }
            });
        } else {
            Type userType = new TypeToken<JsonArray>() {
            }.getType();
            JsonArray arr = (gson.fromJson(mShared_common_pref.getvalue(Constants.DB_TWO_GET_MREPORTS+"_"+mns[fmn - 1]), userType));
            assignMnthReports(arr, m);
        }
    }

    private void assignMnthReports(JsonArray res, int m) {
        try {
//            JsonArray res = response.body();
//            Log.d(TAG + "getMnthReports", String.valueOf(response.body()));
            JsonArray dyRpt = new JsonArray();
            for (int il = 0; il < res.size(); il++) {
                JsonObject Itm = res.get(il).getAsJsonObject();
                JsonObject newItem = new JsonObject();
                newItem.addProperty("name", Itm.get("Status").getAsString());
                newItem.addProperty("value", Itm.get("StatusCnt").getAsString());
                newItem.addProperty("Link", true);
                newItem.addProperty("Priod", m);
                newItem.addProperty("color", Itm.get("StusClr").getAsString().replace(" !important", ""));
                dyRpt.add(newItem);
            }

            recyclerView = (RecyclerView) findViewById(R.id.Rv_MnRpt);
            mAdapter = new HomeRptRecyler(dyRpt, Dashboard_Two.this);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
            // Log.d(Tag, String.valueOf(res));
            LoadingCnt++;
            hideShimmer();
        } catch (Exception e) {

        }

    }

    private void getDyReports() {

        // appendDS = appendDS + "&divisionCode=" + userData.divisionCode + "&sfCode=" + sSF + "&rSF=" + userData.sfCode + "&State_Code=" + userData.State_Code;
        if (Common_Class.isNullOrEmpty(mShared_common_pref.getvalue(Constants.DB_TWO_GET_DYREPORTS))) {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonArray> rptCall = apiInterface.getDataArrayList("get/AttnDySty",
                    UserDetails.getString("Divcode", ""),
                    UserDetails.getString("Sfcode", ""), "", "", null);
            Log.v("View_Request", rptCall.request().toString());
            rptCall.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    try {
                        assignDyReports(response.body());
                        mShared_common_pref.save(Constants.DB_TWO_GET_DYREPORTS, gson.toJson(response.body()));
                    } catch (Exception e) {

                        LoadingCnt++;
                        hideShimmer();
                    }

                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    Log.d(Tag, String.valueOf(t));
                    LoadingCnt++;
                    hideShimmer();
                }
            });
        } else {
            Type userType = new TypeToken<JsonArray>() {
            }.getType();
            JsonArray arr = (gson.fromJson(mShared_common_pref.getvalue(Constants.DB_TWO_GET_DYREPORTS), userType));
            assignDyReports(arr);
        }
        ImageView backView = findViewById(R.id.imag_back);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnBackPressedDispatcher.onBackPressed();
            }
        });
    }

    private void assignDyReports(JsonArray res) {
        try {
            // JsonArray res = response.body();
            //  Log.v(TAG + "getDyReports", res.toString());
            if (res.size() < 1) {
                Toast.makeText(getApplicationContext(), "No Records Today", Toast.LENGTH_LONG).show();

                LoadingCnt++;
                hideShimmer();
                return;
            }
            JsonObject fItm = res.get(0).getAsJsonObject();
            TextView txDyDet = findViewById(R.id.lTDyTx);
            //  txDyDet.setText(Html.fromHtml(fItm.get("AttDate").getAsString() + "<br><small>" + fItm.get("AttDtNm").getAsString() + "</small>"));

            txDyDet.setText(fItm.get("AttDtNm").getAsString() + "   " + fItm.get("AttDate").getAsString());

            CircleImageView ivCheckIn = findViewById(R.id.ivCheckIn);
            CircleImageView ivCheckOut = findViewById(R.id.iv_checkout);
            checkInUrl = ApiClient.BASE_URL.replaceAll("server/", "");
            checkInUrl = checkInUrl + fItm.get("ImgName").getAsString();

            if (Common_Class.isNullOrEmpty(fItm.get("ImgName").getAsString()))
                ivCheckIn.setVisibility(View.GONE);
            else {
                ivCheckIn.setVisibility(View.VISIBLE);
                Glide.with(Dashboard_Two.this)
                        .load(checkInUrl)
                        .into(ivCheckIn);
            }

            try {
                Glide.with(Dashboard_Two.this)
                        .load(ApiClient.BASE_URL.replaceAll("server/", "") + fItm.get("EImgName").getAsString())
                        .into(ivCheckOut);
            } catch (Exception e) {

            }


            ivCheckIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ProductImageView.class);
                    intent.putExtra("ImageUrl", checkInUrl);
                    startActivity(intent);

                }
            });

            ivCheckOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Common_Class.isNullOrEmpty(fItm.get("EImgName").getAsString())) {
                        Intent intent = new Intent(getApplicationContext(), ProductImageView.class);
                        intent.putExtra("ImageUrl", ApiClient.BASE_URL.replaceAll("server/", "") + fItm.get("EImgName").getAsString());
                        startActivity(intent);
                    }

                }
            });


            mShared_common_pref.save(Constants.LOGIN_DATE, com.saneforce.milksales.Common_Class.Common_Class.GetDatewothouttime());
            JsonArray dyRpt = new JsonArray();
            JsonObject newItem = new JsonObject();
            newItem.addProperty("name", "Shift");
            newItem.addProperty("value", fItm.get("SFT_Name").getAsString());
            newItem.addProperty("Link", false);
            newItem.addProperty("color", "#333333");
            dyRpt.add(newItem);
            newItem = new JsonObject();
            newItem.addProperty("name", "Status");
            newItem.addProperty("value", fItm.get("DayStatus").getAsString());
            newItem.addProperty("color", fItm.get("StaColor").getAsString());
            dyRpt.add(newItem);

            if (!fItm.get("HQNm").getAsString().equalsIgnoreCase("")) {
                newItem = new JsonObject();
                newItem.addProperty("name", "Location");
                newItem.addProperty("value", fItm.get("HQNm").getAsString());
                newItem.addProperty("color", fItm.get("StaColor").getAsString());
                newItem.addProperty("type", "geo");
                dyRpt.add(newItem);
            }
            newItem = new JsonObject();
            newItem.addProperty("name", "Check-In");
            newItem.addProperty("value", fItm.get("AttTm").getAsString());
            newItem.addProperty("color", "#333333");
            dyRpt.add(newItem);
            if (!fItm.get("ET").isJsonNull()) {
                newItem = new JsonObject();
                newItem.addProperty("name", "Last Check-Out");
                newItem.addProperty("value", fItm.get("ET").getAsString());
                newItem.addProperty("color", "#333333");
                dyRpt.add(newItem);
            }
            newItem = new JsonObject();
            newItem.addProperty("name", "Geo In");
            newItem.addProperty("value", fItm.get("GeoIn").getAsString());
            newItem.addProperty("color", "#333333");
            newItem.addProperty("type", "geo");
            dyRpt.add(newItem);

            newItem = new JsonObject();
            newItem.addProperty("name", "Geo Out");
            newItem.addProperty("value", fItm.get("GeoOut").getAsString());//"<a href=\"https://www.google.com/maps?q="+fItm.get("GeoOut").getAsString()+"\">"+fItm.get("GeoOut").getAsString()+"</a>");
            newItem.addProperty("color", "#333333");
            newItem.addProperty("type", "geo");
            dyRpt.add(newItem);

            Integer OTFlg = UserDetails.getInt("OTFlg", 0);
            if (OTFlg==1 && viewMode.equalsIgnoreCase("extended")) {
                newItem = new JsonObject();
                newItem.addProperty("name", "Extended Start");
                newItem.addProperty("value", fItm.get("ExtStartTtime").getAsString());//"<a href=\"https://www.google.com/maps?q="+fItm.get("GeoOut").getAsString()+"\">"+fItm.get("GeoOut").getAsString()+"</a>");
                newItem.addProperty("color", "#333333");
                /*newItem.addProperty("type", "geo");*/
                dyRpt.add(newItem);

                newItem = new JsonObject();
                newItem.addProperty("name", "Extended End");
                newItem.addProperty("value", fItm.get("ExtEndtime").getAsString());//"<a href=\"https://www.google.com/maps?q="+fItm.get("GeoOut").getAsString()+"\">"+fItm.get("GeoOut").getAsString()+"</a>");
                newItem.addProperty("color", "#333333");
                /*newItem.addProperty("type", "geo");*/
                dyRpt.add(newItem);

                newItem = new JsonObject();
                newItem.addProperty("name", "Ext.Geo In");
                newItem.addProperty("value", fItm.get("Extin").getAsString());
                newItem.addProperty("color", "#333333");
                newItem.addProperty("type", "geo");
                dyRpt.add(newItem);

                newItem = new JsonObject();
                newItem.addProperty("name", "Ext.Geo Out");
                newItem.addProperty("value", fItm.get("Extout").getAsString());//"<a href=\"https://www.google.com/maps?q="+fItm.get("GeoOut").getAsString()+"\">"+fItm.get("GeoOut").getAsString()+"</a>");
                newItem.addProperty("color", "#333333");
                newItem.addProperty("type", "geo");
                dyRpt.add(newItem);
            }
            recyclerView = (RecyclerView) findViewById(R.id.Rv_DyRpt);

            Log.v("Lat_Long", fItm.get("lat_long").getAsString());
            mAdapter = new HomeRptRecyler(dyRpt, Dashboard_Two.this, fItm.get("lat_long").getAsString());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
            LoadingCnt++;
            hideShimmer();

        } catch (Exception e) {
            LoadingCnt++;
            hideShimmer();
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(Dashboard_Two.this, "There is no back action", Toast.LENGTH_LONG).show();
    }

    private final OnBackPressedDispatcher mOnBackPressedDispatcher =
            new OnBackPressedDispatcher(new Runnable() {
                @Override
                public void run() {
                    Boolean CheckIn = CheckInDetails.getBoolean("CheckIn", false);
                    Log.d(Tag, String.valueOf(CheckIn));
                    if (CheckIn != true) {
                        Dashboard_Two.super.onBackPressed();
                    }
                }
            });

    private void GetMissedPunch() {
        // appendDS = appendDS + "&divisionCode=" + userData.divisionCode + "&sfCode=" + sSF + "&rSF=" + userData.sfCode + "&State_Code=" + userData.State_Code;
        try {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonObject> modelCall = apiInterface.getDataList("CheckWeekofandmis",
                    UserDetails.getString("Divcode", ""),
                    UserDetails.getString("Sfcode", ""), "", "", null);
            modelCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {


                        Log.d(TAG + "GetMissedPunch", String.valueOf(response.body()));
                        JsonObject itm = response.body().getAsJsonObject();
                        String mMessage = "";

                        mMessage = itm.get("Msg").getAsString();
                        JsonArray MissedItems = itm.getAsJsonArray("GetMissed");
                        if (MissedItems.size() > 0) {
                            AlertDialog alertDialog = new AlertDialog.Builder(Dashboard_Two.this)
                                    .setTitle("Check-In")
                                    .setMessage(Html.fromHtml(mMessage))
                                    .setCancelable(false)
                                    .setPositiveButton("Missed Punch Request", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            JsonObject mItem = MissedItems.get(0).getAsJsonObject();
                                            Intent mIntent = new Intent(Dashboard_Two.this, Missed_Punch.class);
                                            mIntent.putExtra("EDt", mItem.get("name").getAsString());
                                            mIntent.putExtra("Shift", mItem.get("name1").getAsString());
                                            mIntent.putExtra("CInTm", mItem.get("CInTm").getAsString());
                                            mIntent.putExtra("COutTm", mItem.get("COutTm").getAsString());
                                            mIntent.putExtra("Aflag", mItem.get("Aflag").getAsString());
                                            Dashboard_Two.this.startActivity(mIntent);
                                        }
                                    })
                                    .show();

                        }
                        else {

                            JsonArray WKItems = itm.getAsJsonArray("CheckWK");
                            if (WKItems.size() > 0) {
                                if (itm.get("WKFlg").getAsInt() == 1) {
                                    Log.d("WEEKOFF", String.valueOf(itm.get("WKFlg").getAsInt()));

                                    LayoutInflater inflater = LayoutInflater.from(Dashboard_Two.this);

                                    final View view = inflater.inflate(R.layout.dashboard_deviation_dialog, null);
                                    android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(Dashboard_Two.this).create();
                                    alertDialog.setTitle("Check-In");
                                    alertDialog.setMessage(Html.fromHtml(mMessage));
                                    alertDialog.setCancelable(false);

                                    TextView btnOthers = (TextView) view.findViewById(R.id.tvOthers);
                                    TextView btnWeekOFF = (TextView) view.findViewById(R.id.tvWeekOff);
                                    TextView btnDeviation = (TextView) view.findViewById(R.id.tvDeviation);

                                    btnOthers.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();
                                            JsonObject mItem = WKItems.get(0).getAsJsonObject();
                                            Intent iLeave = new Intent(Dashboard_Two.this, Leave_Request.class);
                                            iLeave.putExtra("EDt", mItem.get("EDt").getAsString());
                                            Dashboard_Two.this.startActivity(iLeave);

                                            ((AppCompatActivity) Dashboard_Two.this).finish();
                                        }
                                    });

                                    btnWeekOFF.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();
                                            JsonObject mItem = WKItems.get(0).getAsJsonObject();
                                            Intent iWeekOff = new Intent(Dashboard_Two.this, Weekly_Off.class);
                                            iWeekOff.putExtra("EDt", mItem.get("EDt").getAsString());
                                            Dashboard_Two.this.startActivity(iWeekOff);
                                            ((AppCompatActivity) Dashboard_Two.this).finish();
                                        }
                                    });

                                    btnDeviation.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();
                                            JsonObject mItem = WKItems.get(0).getAsJsonObject();
                                            Intent iLeave = new Intent(Dashboard_Two.this, DeviationEntry.class);
                                            iLeave.putExtra("EDt", mItem.get("EDt").getAsString());
                                            Dashboard_Two.this.startActivity(iLeave);

                                            ((AppCompatActivity) Dashboard_Two.this).finish();
                                        }
                                    });

                                    alertDialog.setView(view);
                                    alertDialog.show();

                               /* AlertDialog alertDialog = new AlertDialog.Builder(Dashboard_Two.this)
                                        .setTitle("Check-In")
                                        .setMessage(Html.fromHtml(mMessage))
                                        .setCancelable(false)
                                        .setPositiveButton("Weekofffff", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                JsonObject mItem = WKItems.get(0).getAsJsonObject();
                                                Intent iWeekOff = new Intent(Dashboard_Two.this, Weekly_Off.class);
                                                iWeekOff.putExtra("EDt", mItem.get("EDt").getAsString());
                                                Dashboard_Two.this.startActivity(iWeekOff);
                                                ((AppCompatActivity) Dashboard_Two.this).finish();
                                            }
                                        }).setNegativeButton("Others", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                JsonObject mItem = WKItems.get(0).getAsJsonObject();
                                                Intent iLeave = new Intent(Dashboard_Two.this, Leave_Request.class);
                                                iLeave.putExtra("EDt", mItem.get("EDt").getAsString());
                                                Dashboard_Two.this.startActivity(iLeave);
                                                ((AppCompatActivity) Dashboard_Two.this).finish();
                                            }
                                        })
                                        .show();*/
                                } else {
                                    AlertDialog alertDialog = new AlertDialog.Builder(Dashboard_Two.this)
                                            .setTitle("Check-In")
                                            .setMessage(Html.fromHtml(mMessage))
                                            .setCancelable(false)
                                            .setPositiveButton("Others", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {


                                                    JsonObject mItem = WKItems.get(0).getAsJsonObject();
                                                    Intent iLeave = new Intent(Dashboard_Two.this, Leave_Request.class);
                                                    iLeave.putExtra("EDt", mItem.get("EDt").getAsString());
                                                    Dashboard_Two.this.startActivity(iLeave);

                                                    ((AppCompatActivity) Dashboard_Two.this).finish();
                                                }
                                            })
                                            .show();
                                }
                            }

                            getOfferNotify();
                        }
                    } catch (Exception e) {
                        LoadingCnt++;
                        hideShimmer();
                    }


                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    LoadingCnt++;
                    hideShimmer();
                }

            });
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.cardview3:
                intent = new Intent(this, Leave_Dashboard.class);
                break;
            case R.id.btn_da_exp_entry:
                Shared_Common_Pref.TravelAllowance = 0;
                intent = new Intent(this, TAClaimActivity.class);
                //  intent = new Intent(this, TAClaimAwsActivity.class);
                break;
            case R.id.cardview5:
                intent = new Intent(this, Reports.class);
                break;
            case R.id.approvals:
                Shared_Common_Pref.TravelAllowance = 1;
                intent = new Intent(this, Approvals.class);
                break;
            case R.id.btn_gate_in:
                intent = new Intent(this, QRCodeScanner.class);
                intent.putExtra("Name", "GateIn");
                break;
            case R.id.btn_gate_out:
                intent = new Intent(this, QRCodeScanner.class);
                intent.putExtra("Name", "GateOut");
                break;
            case R.id.StActivity:
                new AlertDialog.Builder(Dashboard_Two.this)
                        .setTitle("Check-In")
                        .setMessage(Html.fromHtml("Are you sure to start your Today Activity Now ?"))
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                Intent aIntent;
                                String sDeptType = UserDetails.getString("DeptType", "");
                                Log.d("DeptType", sDeptType);

                                mShared_common_pref.save("ActivityStart", "true");
                                if (sDeptType.equalsIgnoreCase("1")) {
//                                    aIntent = new Intent(getApplicationContext(), ProcurementDashboardActivity.class);
//                                    startActivity(aIntent);

                                    startActivity(new Intent(getApplicationContext(), SFA_Activity.class));


                                } else {

                                    startActivity(new Intent(getApplicationContext(), SFA_Activity.class));

//                                    JSONObject jParam = new JSONObject();
//                                    try {
//                                        jParam.put("SF", UserDetails.getString("Sfcode", ""));
//                                        jParam.put("div", UserDetails.getString("Divcode", ""));
//                                    } catch (JSONException ex) {
//                                        Log.v(Tag, "sfa" + ex.getMessage());
//
//                                    }


//                                    JSONArray jsonArray = db.getMasterData(Distributor_List);
//
//                                    ApiClient.getClient().create(ApiInterface.class)
//                                            .getDataArrayList("get/distributor", jParam.toString())
//                                            .enqueue(new Callback<JsonArray>() {
//                                                @Override
//                                                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
//                                                    try {
//                                                        // new Shared_Common_Pref(Dashboard_Two.this)
//                                                        //         .save(Distributor_List, response.body().toString());
//                                                        db.deleteMasterData(Distributor_List);
//                                                        db.addMasterData(Distributor_List, response.body().toString());
//                                                        if (jsonArray.length() < 1) {
//                                                            startActivity(new Intent(getApplicationContext(), SFA_Activity.class));
//                                                        }
//                                                    } catch (Exception e) {
//                                                        Log.v(Tag, " distri: "+e.getMessage());
//                                                    }
//
//                                                }
//
//                                                @Override
//                                                public void onFailure(Call<JsonArray> call, Throwable t) {
//                                                    Log.v(Tag, " distri:fai: "+String.valueOf(t));
//                                                }
//                                            });
//                                    if (jsonArray.length() > 0) {
                                    // startActivity(new Intent(getApplicationContext(), SFA_Activity.class));
                                    //}
                                    /*Shared_Common_Pref.Sync_Flag = "0";
                                    com.hap.checkinproc.Common_Class.Common_Class common_class = new com.hap.checkinproc.Common_Class.Common_Class(Dashboard_Two.this);
//                                    if (common_class.checkValueStore(Dashboard_Two.this, Retailer_OutletList)) {
//                                        startActivity(new Intent(getApplicationContext(), SFA_Activity.class));
//                                    } else {
                                    common_class.getDataFromApi(Distributor_List, Dashboard_Two.this, false);
                                    // }*/


                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                break;
            case R.id.button3:
                intent = new Intent(this, View_All_Status_Activity.class);
                intent.putExtra("Priod", mPriod);
                intent.putExtra("Status", "");
                intent.putExtra("name", "View All Status");
                break;
            case R.id.btnExit:
                SharedPreferences.Editor editor = UserDetails.edit();
                editor.putBoolean("Login", false);
                editor.apply();
                CheckInDetails.edit().clear().commit();

                mShared_common_pref.clear_pref(Constants.DB_TWO_GET_MREPORTS);
                mShared_common_pref.clear_pref(Constants.DB_TWO_GET_DYREPORTS);
                mShared_common_pref.clear_pref(Constants.DB_TWO_GET_NOTIFY);
                mShared_common_pref.clear_pref(Constants.LOGIN_DATA);

                Intent playIntent = new Intent(this, SANGPSTracker.class);
                stopService(playIntent);
                finishAffinity();
                break;
            case R.id.btnCheckout:
                AlertDialogBox.showDialog(Dashboard_Two.this, "Check-In", "Do you want to Checkout?", "Yes", "No", false, new AlertBox() {
                    @Override
                    public void PositiveMethod(DialogInterface dialog, int id) {

                        Intent takePhoto = new Intent(Dashboard_Two.this, ImageCapture.class);

                        if(viewMode.equalsIgnoreCase("extended")){
                            takePhoto.putExtra("Mode", "EXOUT");
                        }else
                        {
                            takePhoto.putExtra("Mode", "COUT");
                        }
                        startActivity(takePhoto);

                        /*ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                        Call<JsonArray> Callto = apiInterface.getDataArrayList("get/CLSExp",
                                UserDetails.getString("Divcode", ""),
                                UserDetails.getString("Sfcode", ""), datefrmt);
                        Log.v("DATE_REQUEST", Callto.request().toString());
                        Callto.enqueue(new Callback<JsonArray>() {
                            @Override
                            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                                //if (PrivacyScreen.equals("True") && dashMdeCnt.equals("1")) {
                                Log.d("CHECK_OUT_RESPONSE", String.valueOf(response.body()));
                                Log.d(TAG + "btnCheckout", String.valueOf(response.body()));
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.remove(Name);
                                editor.remove(MOT);
                                editor.remove("SharedImage");
                                editor.remove("Sharedallowance");
                                //editor.remove("SharedMode");
                                editor.remove("StartedKM");
                                editor.remove("SharedFromKm");
                                editor.remove("SharedToKm");
                                editor.remove("SharedFare");
                                editor.remove("SharedImages");
                                editor.remove("Closing");
                                editor.remove(hapLocation);
                                editor.remove(otherLocation);
                                editor.remove(visitPurpose);
                                editor.remove(modeTravelId);
                                editor.remove(modeTypeVale);
                                editor.remove(modeFromKm);
                                editor.remove(modeToKm);
                                editor.remove(StartedKm);
                                editor.remove("SharedDailyAllowancess");
                                editor.remove("SharedDriverss");
                                editor.remove("ShareModeIDs");
                                editor.remove("StoreId");
                                editor.commit();
                                //if (dashMdeCnt.equals("1"))
                                if (response.body().size() > 0) {
                                    Intent takePhoto = new Intent(Dashboard_Two.this, AllowanceActivityTwo.class);
                                    takePhoto.putExtra("Mode", "COUT");
                                    startActivity(takePhoto);
                                } else {
                                    Intent takePhoto = new Intent(Dashboard_Two.this, ImageCapture.class);
                                    takePhoto.putExtra("Mode", "COUT");
                                    startActivity(takePhoto);
                                }
                            }
                            @Override
                            public void onFailure(Call<JsonArray> call, Throwable t) {
                            }
                        });*/
                    }

                    @Override
                    public void NegativeMethod(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                break;

            default:
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetMissedPunch();
        Log.v("LOG_IN_LOCATION", "ONRESTART");
    }

    public void getcountdetails() {

        Map<String, String> QueryString = new HashMap<>();
        QueryString.put("axn", "ViewAllCount");
        QueryString.put("sfCode", UserDetails.getString("Sfcode", ""));
        QueryString.put("State_Code", UserDetails.getString("State_Code", ""));
        QueryString.put("divisionCode", UserDetails.getString("Divcode", ""));
        QueryString.put("rSF", UserDetails.getString("Sfcode", ""));
        QueryString.put("desig", "MGR");
        String commonworktype = "{\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> mCall = apiInterface.DCRSave(QueryString, commonworktype);

        mCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // locationList=response.body();
                Log.e("TAG_TP_RESPONSEcount", "response Tp_View: " + new Gson().toJson(response.body()));
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    // int TC=Integer.parseInt(jsonObject.getString("leave")) + Integer.parseInt(jsonObject.getString("Permission")) + Integer.parseInt(jsonObject.getString("vwOnduty")) + Integer.parseInt(jsonObject.getString("vwmissedpunch")) + Integer.parseInt(jsonObject.getString("TountPlanCount")) + Integer.parseInt(jsonObject.getString("vwExtended"));
                    //jsonObject.getString("leave"))
                    Log.e("TOTAl_COUNT", String.valueOf(Integer.parseInt(jsonObject.getString("leave")) + Integer.parseInt(jsonObject.getString("Permission")) + Integer.parseInt(jsonObject.getString("vwOnduty")) + Integer.parseInt(jsonObject.getString("vwmissedpunch")) + Integer.parseInt(jsonObject.getString("TountPlanCount")) + Integer.parseInt(jsonObject.getString("vwExtended"))));
                    //count = count +

                    Shared_Common_Pref.TotalCountApproval = jsonObject.getInt("leave") + jsonObject.getInt("Permission") +
                            jsonObject.getInt("vwOnduty") + jsonObject.getInt("vwmissedpunch") +
                            jsonObject.getInt("vwExtended") + jsonObject.getInt("TountPlanCount") +
                            jsonObject.getInt("FlightAppr") +
                            jsonObject.getInt("HolidayCount") + jsonObject.getInt("DeviationC") +
                            jsonObject.getInt("CancelLeave") + jsonObject.getInt("ExpList");
                    tvapprCnt.setText(String.valueOf(Shared_Common_Pref.TotalCountApproval));
                    tvapprCnt.setVisibility(View.GONE);
                    if(Shared_Common_Pref.TotalCountApproval>0) tvapprCnt.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // common_class.ProgressdialogShow(2, "");
            }
        });

    }

    public void gatevalue(String Date) {
        Log.v("plantimeplantime", Date);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonArray> Callto = apiInterface.gteDta(Shared_Common_Pref.Sf_Code, com.saneforce.milksales.Common_Class.Common_Class.GetDateOnly());
        Callto.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                try {
                    JsonArray jsonArray = response.body();
                    Log.d(TAG + "gatevalue", String.valueOf(response.body()));

                    gateAdap = new GateAdapter(Dashboard_Two.this, jsonArray);
                    mRecyclerView.setAdapter(gateAdap);
               /* for (int l = 0; l < jsonArray.size(); l++) {
                    JsonObject jsonObjectAdd = jsonArray.get(l).getAsJsonObject();
                    Log.v("GATE_DATA", jsonObjectAdd.toString());
                    gateAdap = new GateAdapter(Dashboard_Two.this, jsonArray);
                }
*/
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });


    }

    public void sendAlarmNotify(int AlmID, long AlmTm, String NotifyTitle, String NotifyMsg) {

        /*AlmTm=AlmTm.replaceAll(" ","-").replaceAll("/","-").replaceAll(":","-");
        String[] sDts= AlmTm.split("-");
        Calendar cal = Calendar.getInstance();
        cal.set(sDts[0],sDts[1],sDts[2],sDts[3],sDts[4]);*/

        Intent intent = new Intent(this, AlmReceiver.class);
        intent.putExtra("ID", String.valueOf(AlmID));
        intent.putExtra("Title", NotifyTitle);
        intent.putExtra("Message", NotifyMsg);
        PendingIntent pIntent = null;

        //PendingIntent.getBroadcast(this.getApplicationContext(), AlmID, intent, 0);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pIntent = PendingIntent.getBroadcast
                    (this, 0, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            pIntent = PendingIntent.getBroadcast
                    (this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, AlmTm, pIntent);
    }
}