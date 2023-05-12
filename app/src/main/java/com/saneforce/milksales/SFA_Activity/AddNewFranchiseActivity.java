package com.saneforce.milksales.SFA_Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.saneforce.milksales.Activity.AllowanceActivity;
import com.saneforce.milksales.Activity_Hap.Dashboard;
import com.saneforce.milksales.Activity_Hap.Dashboard_Two;
import com.saneforce.milksales.Common_Class.Shared_Common_Pref;
import com.saneforce.milksales.Interface.ApiClient;
import com.saneforce.milksales.Interface.ApiInterface;
import com.saneforce.milksales.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Created by RAGU M

public class AddNewFranchiseActivity extends AppCompatActivity {

    Button saveButton;
    EditText et_distributor_name, et_erp_code, et_gst_number, et_username, et_password, et_contact_person, et_mobile_number, et_email, et_address, et_type, et_norm_value;//et_field_officer;
    TextView tv_headquarters;
    ImageView imgHome;

    String distributor_name, erp_code, gst_number, username, password, contact_person, mobile_number, email, address, type, norm_value, field_officer, headquarters, Sf_Code, Div_Code;

    Context context = this;
    ApiInterface apiInterface;

    SharedPreferences UserDetails, CheckInDetails;

    ProgressDialog progressDialog;

    Shared_Common_Pref shared_common_pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_franchisee);

        saveButton = findViewById(R.id.btn_submit);
        et_distributor_name = findViewById(R.id.et_distributor_name);
        et_erp_code = findViewById(R.id.et_erp_code);
        et_gst_number = findViewById(R.id.et_gst_number);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_contact_person = findViewById(R.id.et_contact_person);
        et_mobile_number = findViewById(R.id.et_mobile_number);
        et_email = findViewById(R.id.et_email);
        et_address = findViewById(R.id.et_address);
        et_type = findViewById(R.id.et_type);
        et_norm_value = findViewById(R.id.et_norm_value);
        tv_headquarters = findViewById(R.id.tv_headquarters);
//        et_field_officer = findViewById(R.id.et_field_officer);

        CheckInDetails = getSharedPreferences("CheckInDetail", Context.MODE_PRIVATE);
        UserDetails = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        imgHome = findViewById(R.id.toolbar_home);
        imgHome.setOnClickListener(v -> openHome());

        progressDialog = new ProgressDialog(context);
        shared_common_pref = new Shared_Common_Pref(this);

        Sf_Code = shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code, "");
        Div_Code = shared_common_pref.getvalue(Shared_Common_Pref.Div_Code, "");

        saveButton.setOnClickListener(v -> {

            distributor_name = et_distributor_name.getText().toString().trim();
            erp_code = et_erp_code.getText().toString().trim();
            gst_number = et_gst_number.getText().toString().trim();
            username = et_username.getText().toString().trim();
            password = et_password.getText().toString().trim();
            contact_person = et_contact_person.getText().toString().trim();
            mobile_number = et_mobile_number.getText().toString().trim();
            email = et_email.getText().toString().trim();
            address = et_address.getText().toString().trim();
            type = et_type.getText().toString().trim();
            norm_value = et_norm_value.getText().toString().trim();
//            field_officer = et_field_officer.getText().toString().trim();

            if (TextUtils.isEmpty(distributor_name)) {
                Toast.makeText(context, "Distributor Name Required", Toast.LENGTH_SHORT).show();
                et_distributor_name.requestFocus();
            } else if (TextUtils.isEmpty(erp_code)) {
                Toast.makeText(context, "ERP Code Required", Toast.LENGTH_SHORT).show();
                et_erp_code.requestFocus();
            } else {
                SaveData();
            }
        });
    }

    public void openHome() {
        boolean CheckIn = CheckInDetails.getBoolean("CheckIn", false);
        Shared_Common_Pref.Sf_Code = UserDetails.getString("Sfcode", "");
        Shared_Common_Pref.Sf_Name = UserDetails.getString("SfName", "");
        Shared_Common_Pref.Div_Code = UserDetails.getString("Divcode", "");
        Shared_Common_Pref.StateCode = UserDetails.getString("State_Code", "");
        if (CheckIn) {
            Intent Dashboard = new Intent(AddNewFranchiseActivity.this, Dashboard_Two.class);
            Dashboard.putExtra("Mode", "CIN");
            startActivity(Dashboard);
        } else
            startActivity(new Intent(getApplicationContext(), Dashboard.class));
    }

    private void SaveData() {

        progressDialog.setMessage("Saving Franchise");
        progressDialog.setCancelable(true);
        progressDialog.show();

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("Sf_Code", Sf_Code);
        queryParams.put("Div_Code", Div_Code.replace(",", ""));
        queryParams.put("distributor_name", distributor_name);
        queryParams.put("erp_code", erp_code);
        queryParams.put("gst_number", gst_number);
        queryParams.put("username", username);
        queryParams.put("password", password);
        queryParams.put("contact_person", contact_person);
        queryParams.put("mobile_number", mobile_number);
        queryParams.put("email", email);
        queryParams.put("address", address);
        queryParams.put("type", type);
        queryParams.put("norm_value", norm_value);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> apiCall = apiInterface.AddFranchise(queryParams);
        apiCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        boolean status = object.getBoolean("success");
                        if (status) {
                            Toast.makeText(context, "New Franchise Added Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Response Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "API Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}