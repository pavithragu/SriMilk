package com.saneforce.milksales.SFA_Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.saneforce.milksales.Common_Class.AlertDialogBox;
import com.saneforce.milksales.Common_Class.Common_Class;
import com.saneforce.milksales.Common_Class.Constants;
import com.saneforce.milksales.Common_Class.Shared_Common_Pref;
import com.saneforce.milksales.Interface.AdapterOnClick;
import com.saneforce.milksales.Interface.AlertBox;
import com.saneforce.milksales.Interface.ApiClient;
import com.saneforce.milksales.Interface.ApiInterface;
import com.saneforce.milksales.R;
import com.saneforce.milksales.SFA_Activity.MapDirectionActivity;
import com.saneforce.milksales.SFA_Activity.Reports_Distributor_Name;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DistributerListAdapter extends RecyclerView.Adapter<DistributerListAdapter.MyViewHolder> {
    JSONArray AryDta;
    private Context context;
    int salRowDetailLayout;
    private double ACBalance = 0.0;
    Shared_Common_Pref shared_common_pref;
    AdapterOnClick mAdapterOnClick;
    Common_Class common_class;

    public DistributerListAdapter(JSONArray jAryDta, int rowLayout, Context mContext) {
        AryDta = jAryDta;
        context = mContext;
        salRowDetailLayout = rowLayout;
        shared_common_pref = new Shared_Common_Pref(context);
        common_class=new Common_Class(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(salRowDetailLayout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            JSONObject itm = AryDta.getJSONObject(position);
            holder.tvDistName.setText(itm.getString("name"));
            holder.tvDistAdd.setText(itm.getString("Addr1"));
            holder.tvLatLng.setText("");


            NumberFormat format1 = NumberFormat.getCurrencyInstance(new Locale("en", "in"));

            holder.rlRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject jParam = new JSONObject();
                        jParam.put("StkERP", AryDta.getJSONObject(position).getString("ERP_Code"));

                        ApiClient.getClient().create(ApiInterface.class)
                                .getDataArrayList("get/custbalance", jParam.toString())
                                .enqueue(new Callback<JsonArray>() {
                                    @Override
                                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                                        try {

                                            JsonArray res = response.body();

                                            JsonObject jItem = res.get(0).getAsJsonObject();
                                            double ActBAL = 0;
                                            try {
                                                ActBAL = jItem.get("LC_BAL").getAsDouble();
                                            } catch (Exception e) {
                                                Log.v("newArr:lccatch:", e.getMessage());

                                            }
                                            ACBalance = jItem.get("Balance").getAsDouble();
                                            if (ACBalance <= 0) ACBalance = Math.abs(ACBalance);
                                            else ACBalance = 0 - ACBalance;
                                            if (ActBAL <= 0) ActBAL = Math.abs(ActBAL);
                                            else ActBAL = 0 - ActBAL;


                                            holder.tvbal.setText("" + format1.format(ACBalance));
                                            holder.tvAvailBal.setText("Available Balance:" + format1.format(ActBAL));
                                            holder.tvAmtBlk.setText("Amount Blocked:" + format1.format(jItem.get("Pending").getAsDouble()));
                                            holder.tvBalUpdTime.setText("Amount Updated On " + Common_Class.GetDatemonthyearTimeformat());

                                            AryDta.getJSONObject(position).put("bal", format1.format(ACBalance));
                                            AryDta.getJSONObject(position).put("avail", format1.format(ActBAL));
                                            AryDta.getJSONObject(position).put("blk", jItem.get("Pending").getAsDouble());
                                            AryDta.getJSONObject(position).put("balUpdatedTime", Common_Class.GetDatemonthyearTimeformat());


                                            // shared_common_pref.save(Constants.Distributor_List,AryDta.toString());
                                            JSONArray distArr = new JSONArray(shared_common_pref.getvalue(Constants.Distributor_List));

                                            for (int i = 0; i < distArr.length(); i++) {

                                                if (AryDta.getJSONObject(position).getInt("id") ==
                                                        distArr.getJSONObject(i).getInt("id")) {
                                                    distArr.put(i, AryDta.getJSONObject(position));
                                                    shared_common_pref.save(Constants.Distributor_List, distArr.toString());

                                                }
                                            }
                                        } catch (Exception e) {
                                            Log.v("newArr:catch", e.getMessage());
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<JsonArray> call, Throwable t) {
                                        Log.v("newArr:fail", t.getMessage());

                                    }
                                });

                    } catch (Exception e) {
                    }
                }

            });
            holder.tvLocUpdTime.setText("");
            try {
                holder.tvLocUpdTime.setText("Location Updated On " + itm.getString("locUpdatedTime"));

            } catch (Exception e) {

            }

            try {
                holder.tvbal.setText("" + (itm.getString("bal")));
                holder.tvAmtBlk.setText("Amount Blocked:" + (itm.getString("blk")));
                holder.tvAvailBal.setText("Previous Order value:" + itm.getString("avail"));
                holder.tvBalUpdTime.setText("Amount Updated On " + itm.getString("balUpdatedTime"));
            } catch (Exception e) {
                // holder.tvBalUpdTime.setText("Last Updated On " + Common_Class.GetDatemonthyearTimeformat());

            }

            holder.llDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Common_Class common_class = new Common_Class(context);
                        JSONObject itm = AryDta.getJSONObject(position);
                        if (Common_Class.isNullOrEmpty(itm.getString("Latlong"))) {
                            common_class.showMsg(Reports_Distributor_Name.reports_distributor_name, "No route is found");
                        } else {

                            String[] latlongs = itm.getString("Latlong").split(":");

                            Intent intent = new Intent(context, MapDirectionActivity.class);
                            intent.putExtra(Constants.DEST_LAT, latlongs[0]);
                            intent.putExtra(Constants.DEST_LNG, latlongs[1]);
                            intent.putExtra(Constants.DEST_NAME, itm.getString("name"));
                            intent.putExtra(Constants.NEW_OUTLET, "new");

                            context.startActivity(intent);
                        }
                    } catch (Exception e) {

                    }

                }
            });

            holder.tvDistName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
//                        JSONObject itm = AryDta.getJSONObject(position);
//
//                        Intent intent = new Intent(context, AddNewRetailer.class);
//                        Shared_Common_Pref.Outlet_Info_Flag = "1";
//                        Shared_Common_Pref.Editoutletflag = "1";
//                        Shared_Common_Pref.Outler_AddFlag = "0";
//                        Shared_Common_Pref.FromActivity = "Outlets";
//                    Shared_Common_Pref.OutletCode = String.valueOf(Retailer_Modal_ListFilter.get(position).getId());
//                    intent.putExtra("OutletCode", String.valueOf(Retailer_Modal_ListFilter.get(position).getId()));
//                    intent.putExtra("OutletName", Retailer_Modal_ListFilter.get(position).getName());
//                    intent.putExtra("OutletAddress", Retailer_Modal_ListFilter.get(position).getListedDrAddress1());
//                    intent.putExtra("OutletMobile", Retailer_Modal_ListFilter.get(position).getPrimary_No());
//                    intent.putExtra("OutletRoute", Retailer_Modal_ListFilter.get(position).getTownName());

                        //  context.startActivity(intent);
                    } catch (Exception e) {

                    }

                }
            });

            holder.llRefreshLoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        AlertDialogBox.showDialog(context, "Check-In", "Do you confirm to Refresh Location?",
                                "Update", "Remove", true, new AlertBox() {
                                    @Override
                                    public void PositiveMethod(DialogInterface dialog, int id) {
                                        try {
                                            Reports_Distributor_Name.reports_distributor_name.updateDistlatLng(AryDta.getJSONObject(position).getInt("id"), position, AryDta, 1);
                                        } catch (Exception e) {
                                        }
                                    }

                                    @Override
                                    public void NegativeMethod(DialogInterface dialog, int id) {
                                        try {
                                            Reports_Distributor_Name.reports_distributor_name.updateDistlatLng(AryDta.getJSONObject(position).getInt("id"), position, AryDta, 0);
                                        } catch (Exception e) {
                                        }
                                    }
                                });

                    } catch (Exception e) {
                    }
                }
            });

            holder.llMobile.setVisibility(View.GONE);
            if (!Common_Class.isNullOrEmpty(itm.getString("Mobile"))) {
                holder.llMobile.setVisibility(View.VISIBLE)
                ;
                holder.tvMobile.setText("" + itm.getString("Mobile"));
            }


            holder.llMobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    common_class.showCalDialog(context, "Do you want to Call this Franchise?",
                            holder.tvMobile.getText().toString().replaceAll(",", ""));

                }
            });

            if (!Common_Class.isNullOrEmpty(itm.getString("Latlong"))) {
                String[] latlongs = itm.getString("Latlong").split(":");

                holder.tvLatLng.setText("Lat:" + latlongs[0] + "   " + "Lng:" + latlongs[1]);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return AryDta.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDistName, tvbal, tvAvailBal, tvAmtBlk, tvBalUpdTime, tvDistAdd, tvLocUpdTime, tvLatLng, tvMobile;
        RelativeLayout rlRefresh;
        LinearLayout llDirection, llParent, llRefreshLoc, llMobile;
        ProgressBar pb;

        public MyViewHolder(View view) {
            super(view);
            tvDistName = view.findViewById(R.id.tvDistName);
            tvbal = view.findViewById(R.id.tvACBal);
            tvAvailBal = view.findViewById(R.id.tvAvailBal);
            tvAmtBlk = view.findViewById(R.id.tvAmtBlk);
            rlRefresh = view.findViewById(R.id.rlRefBal);
            llDirection = view.findViewById(R.id.llDirection);
            llParent = view.findViewById(R.id.layparent);
            tvBalUpdTime = view.findViewById(R.id.tvBalUpdTime);
            tvDistAdd = view.findViewById(R.id.tvDistAdd);
            llRefreshLoc = view.findViewById(R.id.llRefreshLoc);
            tvLocUpdTime = view.findViewById(R.id.tvLocUpdTime);
            pb = view.findViewById(R.id.progressbar);
            tvLatLng = view.findViewById(R.id.tvLatLng);
            llMobile = view.findViewById(R.id.btnCallMob);
            tvMobile = view.findViewById(R.id.tvDistPhone);


        }
    }
}
