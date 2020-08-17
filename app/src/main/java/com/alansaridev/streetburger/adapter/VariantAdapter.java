package com.alansaridev.streetburger.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansaridev.streetburger.R;
import com.alansaridev.streetburger.model.VariantModel;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class VariantAdapter extends RecyclerView.Adapter<VariantAdapter.VariantViewHolder> {

    Context context;
    ArrayList<VariantModel> variants;
    int maxOptions;
    ArrayList<CheckBox> variantCheckBoxesList;

    public VariantAdapter(Context context, ArrayList<VariantModel> variants) {
        this.context = context;
        this.variants = variants;
    }

    @NonNull
    @Override
    public VariantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VariantViewHolder(LayoutInflater.from(context).inflate(R.layout.variant_item, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull VariantViewHolder holder, int position) {

        holder.variantCheckBox.setChecked((variants.get(position).isChecked()));
        holder.variantCheckBox.setText(variants.get(position).getName());
        holder.variantPrice.setText("+" + variants.get(position).getPrice() + " €");
        variantCheckBoxesList.add(holder.variantCheckBox);
        Log.d(TAG, "onBindViewHolder: added: " + holder.variantCheckBox.getText() + " to list of checkboxes");
        Log.d(TAG, "onBindViewHolder: variantCheckBoxesList is now: " + variantCheckBoxesList.size());

    }


    @Override
    public int getItemCount() {
        return variants.size();
    }

    class VariantViewHolder extends RecyclerView.ViewHolder {

        TextView variantPrice;
        CheckBox variantCheckBox;
        int maxOptions;

        public VariantViewHolder(@NonNull View itemView) {
            super(itemView);

            variantPrice = itemView.findViewById(R.id.variant_price);
            variantCheckBox = (CheckBox) itemView.findViewById(R.id.variant_checkbox);

            variantCheckBoxesList = new ArrayList<CheckBox>();
            variantCheckBoxesList.add(variantCheckBox);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    //need to uncheck all checkboxes and then check the pressed one
                    for (CheckBox currentCheckBox : variantCheckBoxesList) {
                        currentCheckBox.setChecked(false);
                    }
                    variantCheckBox.setChecked(!variantCheckBox.isChecked());
                    Log.d(TAG, "onClick: changed " + variantCheckBox.getText() + " from " + !variantCheckBox.isChecked() + " to " + variantCheckBox.isChecked());

                }
            });
        }
    }
}