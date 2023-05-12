package com.saneforce.milksales.Activity_Hap;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saneforce.milksales.Common_Class.AlertDialogBox;
import com.saneforce.milksales.Interface.AlertBox;
import com.saneforce.milksales.Model_Class.ChildMyOrderModel;
import com.saneforce.milksales.Model_Class.ParentMyOrderModel;
import com.saneforce.milksales.R;
import com.saneforce.milksales.adapters.ParentMyOrderAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {
    private RecyclerView menuRecycler;
    private List<ParentMyOrderModel> cacheMenuRes;
    private ParentMyOrderAdapter mainMenuAdapter;

    TextView toolHeader;
    ImageView imgBack;
    EditText toolSearch;

    String repos = "  {\n" +
            "                            \"data\": [\n" +
            "                            {\n" +
            "                                \"subcategoryid\": 1,\n" +
            "                                    \"categoryname\": \"Karthic\",\n" +
            "                                    \"menuid\": 1,\n" +
            "                                    \"name\": \"1500Ml\",\n" +
            "                                    \"price\": 50,\n" +
            "                                    \"type\": \"2\"\n" +
            "                            },{\n" +
            "                                \"subcategoryid\": 1,\n" +
            "                                    \"categoryname\": \"Karthic\",\n" +
            "                                    \"menuid\": 1,\n" +
            "                                    \"name\": \"1500Ml\",\n" +
            "                                    \"price\": 50,\n" +
            "                                    \"type\": \"2\"\n" +
            "                            },{\n" +
            "                                \"subcategoryid\": 1,\n" +
            "                                    \"categoryname\": \"Karthic\",\n" +
            "                                    \"menuid\": 1,\n" +
            "                                    \"name\": \"1500Ml\",\n" +
            "                                    \"price\": 50,\n" +
            "                                    \"type\": \"2\"\n" +
            "                            },{\n" +
            "                                \"subcategoryid\": 1,\n" +
            "                                    \"categoryname\": \"Karthic\",\n" +
            "                                    \"menuid\": 1,\n" +
            "                                    \"name\": \"1500Ml\",\n" +
            "                                    \"price\": 50,\n" +
            "                                    \"type\": \"2\"\n" +
            "                            },{\n" +
            "                                \"subcategoryid\": 1,\n" +
            "                                    \"categoryname\": \"Karthic\",\n" +
            "                                    \"menuid\": 1,\n" +
            "                                    \"name\": \"1500Ml\",\n" +
            "                                    \"price\": 50,\n" +
            "                                    \"type\": \"2\"\n" +
            "                            },{\n" +
            "                                \"subcategoryid\": 1,\n" +
            "                                    \"categoryname\": \"Karthic\",\n" +
            "                                    \"menuid\": 1,\n" +
            "                                    \"name\": \"1500Ml\",\n" +
            "                                    \"price\": 50,\n" +
            "                                    \"type\": \"2\"\n" +
            "                            },\n" +
            "                            {\n" +
            "                                \"subcategoryid\": 1,\n" +
            "                                    \"categoryname\": \"Karthic\",\n" +
            "                                    \"menuid\": 2,\n" +
            "                                    \"name\": \"1500Ml\",\n" +
            "                                    \"price\": 30,\n" +
            "                                    \"type\": \"2\"\n" +
            "                            },\n" +
            "                            {\n" +
            "                                \"subcategoryid\": 2,\n" +
            "                                    \"categoryname\": \"Anbu\",\n" +
            "                                    \"menuid\": 3,\n" +
            "                                    \"name\": \"Hatsun Curd\",\n" +
            "                                    \"price\": 10,\n" +
            "                                    \"type\": \"11\"\n" +
            "                            },\n" +
            "                            {\n" +
            "                                \"subcategoryid\": 2,\n" +
            "                                    \"categoryname\": \"Anbu\",\n" +
            "                                    \"menuid\": 4,\n" +
            "                                    \"name\": \"Hatsun Curd\",\n" +
            "                                    \"price\": 30,\n" +
            "                                    \"type\": \"12\"\n" +
            "                            },\n" +
            "                            {\n" +
            "                                \"subcategoryid\": 3,\n" +
            "                                    \"categoryname\": \"Anbu\",\n" +
            "                                    \"menuid\": 5,\n" +
            "                                    \"name\": \"Hatsun Milk\",\n" +
            "                                    \"price\": 25,\n" +
            "                                    \"type\": \"5\"\n" +
            "                            },\n" +
            "                            {\n" +
            "                                \"subcategoryid\": 3,\n" +
            "                                    \"categoryname\": \"Anbu\",\n" +
            "                                    \"menuid\": 6,\n" +
            "                                    \"name\": \"Hatsun Milk\",\n" +
            "                                    \"price\": 30,\n" +
            "                                    \"type\": \"6\"\n" +
            "                            },\n" +
            "                            {\n" +
            "                                \"subcategoryid\": 4,\n" +
            "                                    \"categoryname\": \"kumar\",\n" +
            "                                    \"menuid\": 7,\n" +
            "                                    \"name\": \"Hatsun curd\",\n" +
            "                                    \"price\": 10,\n" +
            "                                    \"type\": \"25\"\n" +
            "                            },\n" +
            "                            {\n" +
            "                                \"subcategoryid\": 4,\n" +
            "                                    \"categoryname\": \"kumar\",\n" +
            "                                    \"menuid\": 8,\n" +
            "                                    \"name\": \"Hatsun lassi\",\n" +
            "                                    \"price\": 20,\n" +
            "                                    \"type\": \"22\"\n" +
            "                            }\n" +
            "                            ],\n" +
            "                            \"fullError\": null,\n" +
            "                                \"message\": \"success\"\n" +
            "                        }";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);


        ImageView backView = findViewById(R.id.imag_back);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnBackPressedDispatcher.onBackPressed();
            }
        });

        menuRecycler = findViewById(R.id.my_orders_recyclerview);

        cacheMenuRes = new ArrayList<>();
        mainMenuAdapter = new ParentMyOrderAdapter(cacheMenuRes, MyOrdersActivity.this);
        //Log.e("JDFODSUFJD", String.valueOf(cacheMenuRes));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MyOrdersActivity.this);
        menuRecycler.setLayoutManager(layoutManager);
        menuRecycler.setItemViewCacheSize(20);
        menuRecycler.setAdapter(mainMenuAdapter);
        getMenu();
    }


    public void getMenu() {


        final HashMap<String, List<ChildMyOrderModel>> map = new HashMap<>();
        try {

            JSONObject jsonObjectq = new JSONObject(repos);
            JSONArray jsonArray = jsonObjectq.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (map.containsKey(jsonObject.getString("categoryname"))) {
                    List<ChildMyOrderModel> menu = map.get(jsonObject.getString("categoryname"));
                    menu.add(new ChildMyOrderModel(jsonObject.getInt("menuid"), jsonObject.getInt("subcategoryid"), jsonObject.getString("name"), jsonObject.getDouble("price"), jsonObject.getString("type")));
                    map.put(jsonObject.getString("categoryname"), menu);
                } else {
                    List<ChildMyOrderModel> menus = new ArrayList<>();
                    menus.add(new ChildMyOrderModel(jsonObject.getInt("menuid"), jsonObject.getInt("subcategoryid"), jsonObject.getString("name"), jsonObject.getDouble("price"), jsonObject.getString("type")));
                    map.put(jsonObject.getString("categoryname"), menus);
                }
            }
            for (String catname : map.keySet()) {
                cacheMenuRes.add(new ParentMyOrderModel(catname, map.get(catname)));
            }

            mainMenuAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private final OnBackPressedDispatcher mOnBackPressedDispatcher =
            new OnBackPressedDispatcher(new Runnable() {
                @Override
                public void run() {
                    AlertDialogBox.showDialog(MyOrdersActivity.this, "", "Do you want to exit?", "Yes", "NO", false, new AlertBox() {
                        @Override
                        public void PositiveMethod(DialogInterface dialog, int id) {
                            onSuperBackPressed();
                        }

                        @Override
                        public void NegativeMethod(DialogInterface dialog, int id) {
                        }
                    });
                }
            });

    public void onSuperBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onBackPressed() {

    }
}
