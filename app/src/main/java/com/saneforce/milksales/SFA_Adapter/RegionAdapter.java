package com.saneforce.milksales.SFA_Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saneforce.milksales.R;
import com.saneforce.milksales.SFA_Model_Class.CommonModelForDropDown;
import com.saneforce.milksales.SFA_Model_Class.CommonModelWithThreeString;

import java.util.ArrayList;

public class RegionAdapter extends RecyclerView.Adapter<RegionAdapter.ViewHolder> {
    ArrayList<CommonModelWithThreeString> list;
    Context context;

    SelectItem selectItem;

    public void setSelectItem(SelectItem selectItem) {
        this.selectItem = selectItem;
    }

    public RegionAdapter(ArrayList<CommonModelWithThreeString> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RegionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RegionAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.common_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RegionAdapter.ViewHolder holder, int position) {
        CommonModelWithThreeString model = list.get(position);
        holder.title.setText(model.getTitle());
        holder.itemView.setOnClickListener(v -> {
            if (selectItem != null) {
                selectItem.onItemSelected(model, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.Name);
        }
    }

    public interface SelectItem {
        void onItemSelected(CommonModelWithThreeString model, int position);
    }
}
