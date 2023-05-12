package com.saneforce.milksales.SFA_Activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.saneforce.milksales.Activity_Hap.AllowancCapture;
import com.saneforce.milksales.Common_Class.AlertDialogBox;
import com.saneforce.milksales.Common_Class.Common_Class;
import com.saneforce.milksales.Common_Class.Constants;
import com.saneforce.milksales.Common_Class.Shared_Common_Pref;
import com.saneforce.milksales.Interface.AlertBox;
import com.saneforce.milksales.Interface.ApiClient;
import com.saneforce.milksales.Interface.ApiInterface;
import com.saneforce.milksales.Interface.LocationEvents;
import com.saneforce.milksales.Interface.OnImagePickListener;
import com.saneforce.milksales.Interface.UpdateResponseUI;
import com.saneforce.milksales.R;
import com.saneforce.milksales.SFA_Adapter.FilesAdapter;
import com.saneforce.milksales.SFA_Adapter.QPS_Modal;
import com.saneforce.milksales.common.FileUploadService;
import com.saneforce.milksales.common.LocationFinder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoolerInfoActivity extends AppCompatActivity implements View.OnClickListener, UpdateResponseUI {
    TextView tvOrder, tvOtherBrand, tvQPS, tvPOP, tvRetailorName, etTagNo, etMake, etCoolerType, tvReceivedDate;
    EditText etRemarks;
    Common_Class common_class;
    CheckBox cbPurity, cbFrontage, cbNoWrk, cbAvail;
    CircularProgressButton btnSubmit;
    private DatePickerDialog fromDatePickerDialog;
    ImageView ivPurityCapture, ivPurityPreview, ivFTCapture, ivFTPreview, ivNowrkCapture, ivNoWrkPreview, ivToolbarHome;
    Gson gson;
    Shared_Common_Pref shared_common_pref;
    List<QPS_Modal> coolerFileList = new ArrayList<>();

    RecyclerView rvPurity, rvFrontage, rvNotWorking;
    private FilesAdapter filesAdapter;
    private String[] strLoc;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooler_info_layout);

        common_class = new Common_Class(this);
        shared_common_pref = new Shared_Common_Pref(this);
        gson = new Gson();
        init();

        tvRetailorName.setText(shared_common_pref.getvalue(Constants.Retailor_Name_ERP_Code));
        findViewById(R.id.tvCoolerInfo).setVisibility(View.GONE);
        common_class.gotoHomeScreen(this, ivToolbarHome);

        coolerFileList.add(new QPS_Modal("", "", ""));//purity
        coolerFileList.add(new QPS_Modal("", "", ""));//Frontage
        coolerFileList.add(new QPS_Modal("", "", ""));//Not Working

        cbPurity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    findViewById(R.id.purityCaptureOption).setVisibility(View.VISIBLE);
                else {
                    clearFiles(0);
                    findViewById(R.id.purityCaptureOption).setVisibility(View.GONE);
                }
            }
        });

        cbFrontage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    findViewById(R.id.frontageCaptureOption).setVisibility(View.VISIBLE);
                else {
                    clearFiles(1);
                    findViewById(R.id.frontageCaptureOption).setVisibility(View.GONE);
                }
            }
        });

        cbNoWrk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    findViewById(R.id.llNoWrkOpt).setVisibility(View.VISIBLE);
                else {
                    clearFiles(2);
                    findViewById(R.id.llNoWrkOpt).setVisibility(View.GONE);
                }
            }
        });

    }


    void addPic(int pos) {
        if (coolerFileList.get(pos).getFileUrls() == null || coolerFileList.get(pos).getFileUrls().size() < 3) {
            AllowancCapture.setOnImagePickListener(new OnImagePickListener() {
                @Override
                public void OnImageURIPick(Bitmap image, String FileName, String fullPath) {

                    List<String> list = new ArrayList<>();
                    File file = new File(fullPath);
                    Uri contentUri = Uri.fromFile(file);

                    if (coolerFileList.get(pos).getFileUrls() != null && coolerFileList.get(pos).getFileUrls().size() > 0)
                        list = (coolerFileList.get(pos).getFileUrls());
                    list.add(contentUri.toString());
                    coolerFileList.get(pos).setFileUrls(list);

                    filesAdapter = new FilesAdapter(coolerFileList.get(pos).getFileUrls(), R.layout.adapter_local_files_layout, CoolerInfoActivity.this);

                    switch (pos) {
                        case 0:
                            rvPurity.setAdapter(filesAdapter);
                            break;
                        case 1:
                            rvFrontage.setAdapter(filesAdapter);
                            break;
                        case 2:
                            rvNotWorking.setAdapter(filesAdapter);
                            break;
                    }
                }
            });
            Intent intent = new Intent(CoolerInfoActivity.this, AllowancCapture.class);
            intent.putExtra("allowance", "TAClaim");
            startActivity(intent);
        } else {
            common_class.showMsg(CoolerInfoActivity.this, "Limit Exceed...");
        }

    }

    void init() {
        tvOrder = (TextView) findViewById(R.id.tvOrder);
        tvQPS = (TextView) findViewById(R.id.tvQPS);
        tvOtherBrand = (TextView) findViewById(R.id.tvOtherBrand);
        tvPOP = (TextView) findViewById(R.id.tvPOP);
        cbPurity = (CheckBox) findViewById(R.id.cbPurity);
        cbFrontage = (CheckBox) findViewById(R.id.cbFrontage);
        cbNoWrk = (CheckBox) findViewById(R.id.cbNoWorking);
        cbAvail = (CheckBox) findViewById(R.id.cbNoAvail);
        tvReceivedDate = (TextView) findViewById(R.id.tvReceivedDate);
        ivPurityCapture = (ImageView) findViewById(R.id.ivPurityCapture);
        ivPurityPreview = (ImageView) findViewById(R.id.ivPurityPreview);
        ivFTCapture = (ImageView) findViewById(R.id.ivFTCapture);
        ivFTPreview = (ImageView) findViewById(R.id.ivFTPreview);
        ivNowrkCapture = (ImageView) findViewById(R.id.ivNoWrkCapture);
        ivNoWrkPreview = (ImageView) findViewById(R.id.ivNoWrkPreview);
        tvRetailorName = findViewById(R.id.Category_Nametext);
        ivToolbarHome = findViewById(R.id.toolbar_home);
        btnSubmit = findViewById(R.id.btnSubmit);
        etTagNo = findViewById(R.id.etTagNo);
        etMake = findViewById(R.id.etMake);
        etCoolerType = findViewById(R.id.etCoolerType);
        etRemarks = findViewById(R.id.edt_remarks);

        rvPurity = findViewById(R.id.rvPurityFiles);
        rvFrontage = findViewById(R.id.rvFTFiles);
        rvNotWorking = findViewById(R.id.rvNWFiles);

        tvOrder.setOnClickListener(this);
        tvOtherBrand.setOnClickListener(this);
        tvQPS.setOnClickListener(this);
        tvPOP.setOnClickListener(this);
        //  tvReceivedDate.setOnClickListener(this);

        ivPurityCapture.setOnClickListener(this);
        ivPurityPreview.setOnClickListener(this);
        ivFTCapture.setOnClickListener(this);
        ivFTPreview.setOnClickListener(this);
        ivNowrkCapture.setOnClickListener(this);
        ivNoWrkPreview.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        common_class.getDb_310Data(Constants.COOLER_INFO, this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            common_class.CommonIntentwithFinish(Invoice_History.class);
            return true;
        }
        return false;
    }

    void clearFiles(int pos) {
        if (coolerFileList.get(pos).getFileUrls() != null) {
            coolerFileList.get(pos).getFileUrls().clear();
        }

    }

    public void ResetSubmitBtn(int resetMode) {
        common_class.ProgressdialogShow(0, "");
        long dely = 10;
        if (resetMode != 0) dely = 1000;
        if (resetMode == 1) {
            btnSubmit.doneLoadingAnimation(getResources().getColor(R.color.green), BitmapFactory.decodeResource(getResources(), R.drawable.done));
        } else {
            btnSubmit.doneLoadingAnimation(getResources().getColor(R.color.color_red), BitmapFactory.decodeResource(getResources(), R.drawable.ic_wrong));
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btnSubmit.stopAnimation();
                btnSubmit.revertAnimation();
            }
        }, dely);

    }

    @Override
    public void onClick(View v) {
        Common_Class common_class = new Common_Class(this);
        switch (v.getId()) {
            case R.id.tvOrder:
                common_class.commonDialog(this, Order_Category_Select.class, "Cooler Info?");
                break;
            case R.id.tvOtherBrand:
                common_class.commonDialog(this, OtherBrandActivity.class, "Cooler Info?");
                break;
            case R.id.tvQPS:
                common_class.commonDialog(this, QPSActivity.class, "Cooler Info?");
                break;
            case R.id.tvPOP:
                common_class.commonDialog(this, POPActivity.class, "Cooler Info?");
                break;
            case R.id.ivPurityCapture:
                addPic(0);
                break;
            case R.id.ivFTCapture:
                addPic(1);
                break;
            case R.id.ivNoWrkCapture:
                addPic(2);
                break;
            case R.id.tvReceivedDate:
                Calendar newCalendar = Calendar.getInstance();
                fromDatePickerDialog = new DatePickerDialog(CoolerInfoActivity.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        int month = monthOfYear + 1;

                        tvReceivedDate.setText("" + dayOfMonth + "/" + month + "/" + year);
                    }
                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                fromDatePickerDialog.show();
                break;
            case R.id.btnSubmit:

                if (etTagNo.getText().toString().equals(""))
                    common_class.showMsg(this, "Enter Tag No");
//                else if (etMake.getText().toString().equals("")) {
//                    common_class.showMsg(this, "Enter Make");
//                } else if (etCoolerType.getText().toString().equals("")) {
//                    common_class.showMsg(this, "Enter Cooler Type");
//                } else if (tvReceivedDate.getText().toString().equals("")) {
//                    common_class.showMsg(this, "Enter Received Date");
                    //}
                else if (cbPurity.isChecked() && (coolerFileList.get(0).getFileUrls() == null || coolerFileList.get(0).getFileUrls().size() == 0)) {
                    common_class.showMsg(this, "Kindly Attach Purity Files");
                } else if (cbFrontage.isChecked() && (coolerFileList.get(1).getFileUrls() == null || coolerFileList.get(1).getFileUrls().size() == 0)) {
                    common_class.showMsg(this, "Kindly Attach Frontage Files");
                } else if (cbNoWrk.isChecked() && (coolerFileList.get(2).getFileUrls() == null || coolerFileList.get(2).getFileUrls().size() == 0)) {
                    common_class.showMsg(this, "Kindly Attach Not Working Files");
                } else {
                    if (btnSubmit.isAnimating()) return;
                    btnSubmit.startAnimation();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String sLoc = shared_common_pref.getvalue("CurrLoc");
                            if (sLoc.equalsIgnoreCase("")) {
                                new LocationFinder(getApplication(), new LocationEvents() {
                                    @Override
                                    public void OnLocationRecived(Location location) {
                                        strLoc = (location.getLatitude() + ":" + location.getLongitude()).split(":");
                                        submitData();
                                        uploadFile();
                                    }
                                });
                            } else {
                                strLoc = sLoc.split(":");
                                submitData();
                                uploadFile();
                            }
                        }
                    }, 500);

                }
                break;
        }

    }

    void uploadFile() {
        for (int i = 0; i < coolerFileList.size(); i++) {
            if (coolerFileList.get(i).getFileUrls() != null) {
                for (int f = 0; f < coolerFileList.get(i).getFileUrls().size(); f++) {
                    String filePath = coolerFileList.get(i).getFileUrls().get(f).replaceAll("file:/", "");
                    File file = new File(filePath);
                    Intent mIntent = new Intent(this, FileUploadService.class);
                    mIntent.putExtra("mFilePath", filePath);
                    mIntent.putExtra("SF", Shared_Common_Pref.Sf_Code);
                    mIntent.putExtra("FileName", file.getName());
                    mIntent.putExtra("Mode", "cooler");
                    FileUploadService.enqueueWork(this, mIntent);
                }
            }
        }
    }

    void submitData() {
        if (common_class.isNetworkAvailable(this)) {

            AlertDialogBox.showDialog(CoolerInfoActivity.this, "SFA", "Are You Sure Want to Submit?", "OK", "Cancel", false, new AlertBox() {
                @Override
                public void PositiveMethod(DialogInterface dialog, int id) {
                    common_class.ProgressdialogShow(1, "");

                    JSONArray data = new JSONArray();
                    JSONObject ActivityData = new JSONObject();
                    try {
                        JSONObject HeadItem = new JSONObject();
                        HeadItem.put("divisionCode", Shared_Common_Pref.Div_Code);
                        HeadItem.put("sfCode", Shared_Common_Pref.Sf_Code);
                        HeadItem.put("retailorCode", Shared_Common_Pref.OutletCode);

                        HeadItem.put("distributorcode", shared_common_pref.getvalue(Constants.Distributor_Id));

                        HeadItem.put("date", Common_Class.GetDate());

                        HeadItem.put("tagNo", etTagNo.getText().toString());
                        HeadItem.put("make", etMake.getText().toString());
                        HeadItem.put("coolerType", etCoolerType.getText().toString());

                        HeadItem.put("recDate", tvReceivedDate.getText().toString());
                        HeadItem.put("cbPurity", "" + cbPurity.isChecked());

                        HeadItem.put("cbFrontage", "" + cbFrontage.isChecked());
                        HeadItem.put("cbNotAvailable", "" + cbAvail.isChecked());
                        HeadItem.put("cbNotWorking", "" + cbNoWrk.isChecked());
                        HeadItem.put("remarks", etRemarks.getText().toString());
                        HeadItem.put("lat", strLoc[0]);
                        HeadItem.put("lng", strLoc[1]);


                        ActivityData.put("Cooler_Header", HeadItem);

                        for (int z = 0; z < coolerFileList.size(); z++) {

                            String type = "";
                            switch (z) {
                                case 0:
                                    type = "purity";
                                    break;
                                case 1:
                                    type = "frontage";
                                    break;
                                case 2:
                                    type = "notworking";
                                    break;
                            }
                            JSONArray Order_Details = new JSONArray();
                            if (coolerFileList.get(z).getFileUrls() != null) {
                                for (int f = 0; f < coolerFileList.get(z).getFileUrls().size(); f++) {
                                    JSONObject ProdItem = new JSONObject();
                                    String filePath = coolerFileList.get(z).getFileUrls().get(f).replaceAll("file:/", "");
                                    File file = new File(filePath);

                                    ProdItem.put("cooler_filetype", type);
                                    ProdItem.put("cooler_filename", file.getName());
                                    Order_Details.put(ProdItem);
                                }


                            }
                            ActivityData.put(type + "_file_Details", Order_Details);

                        }


                        data.put(ActivityData);
                    } catch (Exception e) {
                        common_class.ProgressdialogShow(0, "");
                        e.printStackTrace();
                    }
                    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    Call<JsonObject> responseBodyCall = apiInterface.approveCIEntry(data.toString());
                    responseBodyCall.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful()) {
                                try {
                                    common_class.ProgressdialogShow(0, "");

                                    JSONObject jsonObjects = new JSONObject(response.body().toString());
                                    String san = jsonObjects.getString("success");
                                    Log.e("Success_Message", san);

                                    common_class.showMsg(CoolerInfoActivity.this, jsonObjects.getString("Msg"));
                                    ResetSubmitBtn(1);

                                    if (jsonObjects.getBoolean("success")) {
                                        finish();
                                    }
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

    @Override
    public void onLoadDataUpdateUI(String apiDataResponse, String key) {
        try {
            Log.v("cooler:", apiDataResponse);
            switch (key) {
                case Constants.COOLER_INFO:
                    JSONObject obj = new JSONObject(apiDataResponse);
                    if (obj.getBoolean("success")) {
                        JSONArray arr = obj.getJSONArray("Data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject arrObj = arr.getJSONObject(i);
                            etTagNo.setText("" + arrObj.getString("TagNo"));
                            etMake.setText("" + arrObj.getString("Make"));
                            etCoolerType.setText("" + arrObj.getString("CoolerType"));
                            tvReceivedDate.setText("" + arrObj.getString("ReceivedDate"));
                        }
                    }
                    else {
                        common_class.showMsg(this, obj.getString("Msg"));
                        common_class.CommonIntentwithFinish(Invoice_History.class);
                    }
                    break;
            }
        } catch (Exception e) {
        }
    }

}