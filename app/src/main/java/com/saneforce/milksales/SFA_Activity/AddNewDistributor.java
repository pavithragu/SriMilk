package com.saneforce.milksales.SFA_Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.saneforce.milksales.Activity_Hap.AllowancCapture;
import com.saneforce.milksales.Activity_Hap.ProductImageView;
import com.saneforce.milksales.Common_Class.Common_Class;
import com.saneforce.milksales.Common_Class.Shared_Common_Pref;
import com.saneforce.milksales.Interface.ApiClient;
import com.saneforce.milksales.Interface.ApiInterface;
import com.saneforce.milksales.Interface.OnImagePickListener;
import com.saneforce.milksales.R;
import com.saneforce.milksales.common.FileUploadService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewDistributor extends AppCompatActivity {

    TextView select_region, select_sales_office_code, select_sales_office_name, select_route_code, select_route_name, select_channel, select_state_code, date_of_creation,
            select_mode_of_payment, submit, select_bank_details, select_agreement_copy;
    ImageView display_customer_photo, capture_customer_photo, display_shop_photo, capture_shop_photo, display_bank_details, capture_bank_details, display_fssai, capture_fssai,
            display_gst, capture_gst, display_agreement_copy, capture_agreement_copy, display_deposit, capture_deposit, home;
    EditText type_city, type_pincode, type_name_of_the_customer, type_name_of_the_owner, type_address_of_the_shop, type_residence_address, type_mobile_number, type_email_id,
            type_sales_executive_name, type_sales_executive_employee_id, type_aadhaar_number, type_pan_number, type_gst, type_deposit, type_fssai;

    Context context = this;
    Common_Class common_class;
    Shared_Common_Pref pref;
    SharedPreferences UserDetails;

    String customer_photo_url = "", customer_photo_name = "", shop_photo_url = "", shop_photo_name = "", regionStr = "", officeCodeStr = "", officeNameStr = "", routeCodeStr = "",
            routeNameStr = "", channelStr = "", cityStr = "", pincodeStr = "", stateCodeStr = "", customerNameStr = "", ownerNameStr = "", shopAddressStr = "",
            residenceAddressStr = "", mobileNumberStr = "", emailAddressStr = "", executiveNameStr = "", employeeIdStr = "", creationDateStr = "", aadhaarStr = "",
            PANStr = "", bankDetailsStr = "", bankImageName = "", bankImageFullPath = "", FSSAIDetailsStr = "", FSSAIImageName = "", FSSAIImageFullPath = "",
            GSTDetailsStr = "", GSTImageName = "", GSTImageFullPath = "", agreementDetailsStr = "", agreementImageName = "", agreementImageFullPath = "", modeOfPaymentStr = "",
            depositDetailsStr = "", depositImageName = "", depositImageFullPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_distributor);

        select_region = findViewById(R.id.select_region);
        select_sales_office_code = findViewById(R.id.select_sales_office_code);
        select_sales_office_name = findViewById(R.id.select_sales_office_name);
        select_route_code = findViewById(R.id.select_route_code);
        select_route_name = findViewById(R.id.select_route_name);
        select_channel = findViewById(R.id.select_channel);
        select_state_code = findViewById(R.id.select_state_code);
        date_of_creation = findViewById(R.id.date_of_creation);
        select_mode_of_payment = findViewById(R.id.select_mode_of_payment);
        submit = findViewById(R.id.submit);
        display_customer_photo = findViewById(R.id.display_customer_photo);
        capture_customer_photo = findViewById(R.id.capture_customer_photo);
        display_shop_photo = findViewById(R.id.display_shop_photo);
        capture_shop_photo = findViewById(R.id.capture_shop_photo);
        display_bank_details = findViewById(R.id.display_bank_details);
        capture_bank_details = findViewById(R.id.capture_bank_details);
        display_fssai = findViewById(R.id.display_fssai);
        capture_fssai = findViewById(R.id.capture_fssai);
        display_gst = findViewById(R.id.display_gst);
        capture_gst = findViewById(R.id.capture_gst);
        type_gst = findViewById(R.id.type_gst);
        type_deposit = findViewById(R.id.type_deposit);
        display_agreement_copy = findViewById(R.id.display_agreement_copy);
        capture_agreement_copy = findViewById(R.id.capture_agreement_copy);
        display_deposit = findViewById(R.id.display_deposit);
        capture_deposit = findViewById(R.id.capture_deposit);
        home = findViewById(R.id.toolbar_home);
        type_city = findViewById(R.id.type_city);
        type_pincode = findViewById(R.id.type_pincode);
        type_name_of_the_customer = findViewById(R.id.type_name_of_the_customer);
        type_name_of_the_owner = findViewById(R.id.type_name_of_the_owner);
        type_address_of_the_shop = findViewById(R.id.type_address_of_the_shop);
        type_residence_address = findViewById(R.id.type_residence_address);
        type_mobile_number = findViewById(R.id.type_mobile_number);
        type_email_id = findViewById(R.id.type_email_id);
        type_sales_executive_name = findViewById(R.id.type_sales_executive_name);
        type_sales_executive_employee_id = findViewById(R.id.type_sales_executive_employee_id);
        type_aadhaar_number = findViewById(R.id.type_aadhaar_number);
        type_pan_number = findViewById(R.id.type_pan_number);
        select_bank_details = findViewById(R.id.select_bank_details);
        select_agreement_copy = findViewById(R.id.select_agreement_copy);
        type_fssai = findViewById(R.id.type_fssai);

        common_class = new Common_Class(this);
        pref = new Shared_Common_Pref(this);
        UserDetails = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        common_class.gotoHomeScreen(context, home);

        capture_customer_photo.setOnClickListener(v -> {
            AllowancCapture.setOnImagePickListener(new OnImagePickListener() {
                @Override
                public void OnImageURIPick(Bitmap image, String FileName, String fullPath) {
                    customer_photo_name = FileName;
                    customer_photo_url = fullPath;
                    display_customer_photo.setImageBitmap(image);
                    display_customer_photo.setVisibility(View.VISIBLE);
                }
            });
            Intent intent = new Intent(context, AllowancCapture.class);
            startActivity(intent);
        });
        capture_shop_photo.setOnClickListener(v -> {
            AllowancCapture.setOnImagePickListener(new OnImagePickListener() {
                @Override
                public void OnImageURIPick(Bitmap image, String FileName, String fullPath) {
                    shop_photo_name = FileName;
                    shop_photo_url = fullPath;
                    display_shop_photo.setImageBitmap(image);
                    display_shop_photo.setVisibility(View.VISIBLE);
                }
            });
            Intent intent = new Intent(context, AllowancCapture.class);
            startActivity(intent);
        });
        capture_bank_details.setOnClickListener(v -> {
            AllowancCapture.setOnImagePickListener(new OnImagePickListener() {
                @Override
                public void OnImageURIPick(Bitmap image, String FileName, String fullPath) {
                    bankImageName = FileName;
                    bankImageFullPath = fullPath;
                    display_bank_details.setImageBitmap(image);
                    display_bank_details.setVisibility(View.VISIBLE);
                }
            });
            Intent intent = new Intent(context, AllowancCapture.class);
            startActivity(intent);
        });
        capture_fssai.setOnClickListener(v -> {
            AllowancCapture.setOnImagePickListener(new OnImagePickListener() {
                @Override
                public void OnImageURIPick(Bitmap image, String FileName, String fullPath) {
                    FSSAIImageName = FileName;
                    FSSAIImageFullPath = fullPath;
                    display_fssai.setImageBitmap(image);
                    display_fssai.setVisibility(View.VISIBLE);
                }
            });
            Intent intent = new Intent(context, AllowancCapture.class);
            startActivity(intent);
        });
        capture_gst.setOnClickListener(v -> {
            AllowancCapture.setOnImagePickListener(new OnImagePickListener() {
                @Override
                public void OnImageURIPick(Bitmap image, String FileName, String fullPath) {
                    GSTImageName = FileName;
                    GSTImageFullPath = fullPath;
                    display_gst.setImageBitmap(image);
                    display_gst.setVisibility(View.VISIBLE);
                }
            });
            Intent intent = new Intent(context, AllowancCapture.class);
            startActivity(intent);
        });
        capture_agreement_copy.setOnClickListener(v -> {
            AllowancCapture.setOnImagePickListener(new OnImagePickListener() {
                @Override
                public void OnImageURIPick(Bitmap image, String FileName, String fullPath) {
                    agreementImageName = FileName;
                    agreementImageFullPath = fullPath;
                    display_agreement_copy.setImageBitmap(image);
                    display_agreement_copy.setVisibility(View.VISIBLE);
                }
            });
            Intent intent = new Intent(context, AllowancCapture.class);
            startActivity(intent);
        });
        capture_deposit.setOnClickListener(v -> {
            AllowancCapture.setOnImagePickListener(new OnImagePickListener() {
                @Override
                public void OnImageURIPick(Bitmap image, String FileName, String fullPath) {
                    depositImageName = FileName;
                    depositImageFullPath = fullPath;
                    display_deposit.setImageBitmap(image);
                    display_deposit.setVisibility(View.VISIBLE);
                }
            });
            Intent intent = new Intent(context, AllowancCapture.class);
            startActivity(intent);
        });

        display_customer_photo.setOnClickListener(v -> {
            display_customer_photo.setEnabled(false);
            new Handler().postDelayed(() -> display_customer_photo.setEnabled(true), 1500);
            showImage(customer_photo_url);
        });
        display_shop_photo.setOnClickListener(v -> {
            display_shop_photo.setEnabled(false);
            new Handler().postDelayed(() -> display_shop_photo.setEnabled(true), 1500);
            showImage(shop_photo_url);
        });
        display_bank_details.setOnClickListener(v -> {
            display_bank_details.setEnabled(false);
            new Handler().postDelayed(() -> display_bank_details.setEnabled(true), 1500);
            showImage(bankImageFullPath);
        });
        display_fssai.setOnClickListener(v -> {
            display_fssai.setEnabled(false);
            new Handler().postDelayed(() -> display_fssai.setEnabled(true), 1500);
            showImage(FSSAIImageFullPath);
        });
        display_gst.setOnClickListener(v -> {
            display_gst.setEnabled(false);
            new Handler().postDelayed(() -> display_gst.setEnabled(true), 1500);
            showImage(GSTImageFullPath);
        });
        display_agreement_copy.setOnClickListener(v -> {
            display_agreement_copy.setEnabled(false);
            new Handler().postDelayed(() -> display_agreement_copy.setEnabled(true), 1500);
            showImage(agreementImageFullPath);
        });
        display_deposit.setOnClickListener(v -> {
            display_deposit.setEnabled(false);
            new Handler().postDelayed(() -> display_deposit.setEnabled(true), 1500);
            showImage(depositImageFullPath);
        });

        creationDateStr = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        date_of_creation.setText(creationDateStr);

        submit.setOnClickListener(v -> ValidateFields());

    }

    private void showImage(String customer_photo_url) {
        Intent show = new Intent(context, ProductImageView.class);
        show.putExtra("ImageUrl", Uri.fromFile(new File(customer_photo_url)).toString());
        startActivity(show);
    }

    private void uploadImage(String fileName, String fullPath) {
        Intent mIntent = new Intent(context, FileUploadService.class);
        mIntent.putExtra("FileName", fileName);
        mIntent.putExtra("mFilePath", fullPath);
        mIntent.putExtra("SF", UserDetails.getString("Sfcode", ""));
        mIntent.putExtra("Mode", "AddNewDistributor");
        FileUploadService.enqueueWork(this, mIntent);
    }

    private void ValidateFields() {

        regionStr = select_region.getText().toString().trim();
        officeCodeStr = select_sales_office_code.getText().toString().trim();
        officeNameStr = select_sales_office_name.getText().toString().trim();
        routeCodeStr = select_route_code.getText().toString().trim();
        routeNameStr = select_route_name.getText().toString().trim();
        channelStr = select_channel.getText().toString().trim();
        cityStr = type_city.getText().toString().trim();
        pincodeStr = type_pincode.getText().toString().trim();
        stateCodeStr = select_state_code.getText().toString().trim();
        customerNameStr = type_name_of_the_customer.getText().toString().trim();
        ownerNameStr = type_name_of_the_owner.getText().toString().trim();
        shopAddressStr = type_address_of_the_shop.getText().toString().trim();
        residenceAddressStr = type_residence_address.getText().toString().trim();
        mobileNumberStr = type_mobile_number.getText().toString().trim();
        emailAddressStr = type_email_id.getText().toString().trim();
        executiveNameStr = type_sales_executive_name.getText().toString().trim();
        employeeIdStr = type_sales_executive_employee_id.getText().toString().trim();
        creationDateStr = date_of_creation.getText().toString().trim();
        aadhaarStr = type_aadhaar_number.getText().toString().trim();
        PANStr = type_pan_number.getText().toString().trim();
        bankDetailsStr = select_bank_details.getText().toString().trim();
        FSSAIDetailsStr = type_fssai.getText().toString().trim();
        GSTDetailsStr = type_gst.getText().toString().trim();
        agreementDetailsStr = select_agreement_copy.getText().toString().trim();
        modeOfPaymentStr = select_mode_of_payment.getText().toString().trim();
        depositDetailsStr = type_deposit.getText().toString().trim();

        SubmitForm();

        /*if (TextUtils.isEmpty(customer_photo_name) || TextUtils.isEmpty(customer_photo_url)) {
            Toast.makeText(context, "Please Capture Customer Photo", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(shop_photo_name) || TextUtils.isEmpty(shop_photo_url)) {
            Toast.makeText(context, "Please Capture Shop Photo", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(regionStr)) {
            Toast.makeText(context, "Please Select the Region", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(officeCodeStr)) {
            Toast.makeText(context, "Please Select the Sales Office Code", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(officeNameStr)) {
            Toast.makeText(context, "Please Select the Sales Office Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(routeCodeStr)) {
            Toast.makeText(context, "Please Select the Route Code", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(routeNameStr)) {
            Toast.makeText(context, "Please Select the Route Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(channelStr)) {
            Toast.makeText(context, "Please Select the Channel", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cityStr)) {
            Toast.makeText(context, "Please Select the City", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pincodeStr)) {
            Toast.makeText(context, "Please Enter the Pincode", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(stateCodeStr)) {
            Toast.makeText(context, "Please Select the State Code", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(customerNameStr)) {
            Toast.makeText(context, "Please Enter the Customer Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(ownerNameStr)) {
            Toast.makeText(context, "Please Enter the Owner Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(shopAddressStr)) {
            Toast.makeText(context, "Please Enter the Shop Address", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(residenceAddressStr)) {
            Toast.makeText(context, "Please Enter the Residence Address", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mobileNumberStr) || mobileNumberStr.length() != 10) {
            Toast.makeText(context, "Please Enter 10 Digit Mobile Number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(executiveNameStr)) {
            Toast.makeText(context, "Please Enter the Sales Executive Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(employeeIdStr)) {
            Toast.makeText(context, "Please Enter the Sales Executive - Employee ID", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(creationDateStr)) {
            Toast.makeText(context, "Creation Date Can't be Fetched", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(aadhaarStr)) {
            Toast.makeText(context, "Please Enter the Aadhaar Number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(PANStr)) {
            Toast.makeText(context, "Please Enter the PAN Number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(GSTDetailsStr)) {
            Toast.makeText(context, "Please Enter the GST Number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(GSTImageName) || TextUtils.isEmpty(GSTImageFullPath)) {
            Toast.makeText(context, "Please Capture the GST Certificate", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(modeOfPaymentStr)) {
            Toast.makeText(context, "Please Select the Mode of Payment", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(depositDetailsStr)) {
            Toast.makeText(context, "Please Enter the Deposit Details", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(depositImageName) || TextUtils.isEmpty(depositImageFullPath)) {
            Toast.makeText(context, "Please Capture the Deposit Details", Toast.LENGTH_SHORT).show();
        } else {
            SubmitForm();
        }*/
    }

    private void SubmitForm() {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Creating New Distributor");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONArray data = new JSONArray();
        JSONObject object = new JSONObject();

        try {
            object.put("customer_photo_name", customer_photo_name);
            object.put("shop_photo_name", shop_photo_name);
            object.put("regionStr", regionStr);
            object.put("officeCodeStr", officeCodeStr);
            object.put("officeNameStr", officeNameStr);
            object.put("routeCodeStr", routeCodeStr);
            object.put("routeNameStr", routeNameStr);
            object.put("channelStr", channelStr);
            object.put("cityStr", cityStr);
            object.put("pincodeStr", pincodeStr);
            object.put("stateCodeStr", stateCodeStr);
            object.put("customerNameStr", customerNameStr);
            object.put("ownerNameStr", ownerNameStr);
            object.put("shopAddressStr", shopAddressStr);
            object.put("residenceAddressStr", residenceAddressStr);
            object.put("mobileNumberStr", mobileNumberStr);
            object.put("emailAddressStr", emailAddressStr);
            object.put("executiveNameStr", executiveNameStr);
            object.put("employeeIdStr", employeeIdStr);
            object.put("creationDateStr", creationDateStr);
            object.put("aadhaarStr", aadhaarStr);
            object.put("PANStr", PANStr);
            object.put("bankDetailsStr", bankDetailsStr);
            object.put("bankImageName", bankImageName);
            object.put("FSSAIDetailsStr", FSSAIDetailsStr);
            object.put("FSSAIImageName", FSSAIImageName);
            object.put("GSTDetailsStr", GSTDetailsStr);
            object.put("GSTImageName", GSTImageName);
            object.put("agreementDetailsStr", agreementDetailsStr);
            object.put("agreementImageName", agreementImageName);
            object.put("modeOfPaymentStr", modeOfPaymentStr);
            object.put("depositDetailsStr", depositDetailsStr);
            object.put("depositImageName", depositImageName);

        } catch (JSONException e) {
            progressDialog.dismiss();
            Toast.makeText(context, "Json Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        data.put(object);

        Map<String, String> params = new HashMap<>();
        params.put("axn", "save/new_distributor");
        params.put("sfCode", UserDetails.getString("Sfcode", ""));

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.universalAPIRequest(params, data.toString());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        if (response.body() == null) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Response is Null", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getBoolean("success")) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Distributor Added Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Request does not reached the server", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Error while parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Response Not Success", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context, "Response Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}