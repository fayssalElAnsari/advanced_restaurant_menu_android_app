package com.alansaridev.streetburger.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansaridev.streetburger.activities.FullScreenMealActivity;
import com.alansaridev.streetburger.model.MealModel;
import com.alansaridev.streetburger.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    Context context;
    ArrayList<MealModel> meals;
    String imageUrl;

    public MealAdapter(Context context, ArrayList<MealModel> meals) {
        this.context = context;
        this.meals = meals;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MealViewHolder(LayoutInflater.from(context).inflate(R.layout.meal_item, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {

        holder.mealName.setText(meals.get(position).getName());
        holder.mealPrice.setText(meals.get(position).getPrix() + " €");
        holder.mealDescription.setText(meals.get(position).getDescription());
        Picasso.get().load(meals.get(position).getImage()).into(holder.mealPicture);
        holder.mealPicture.setTag(meals.get(position).getImage());


    }


    @Override
    public int getItemCount() {
        return meals.size();
    }

    class MealViewHolder extends RecyclerView.ViewHolder {

        TextView mealName, mealPrice, mealDescription;
        ImageView mealPicture;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);

            mealName = itemView.findViewById(R.id.meal_title);
            mealPrice = itemView.findViewById(R.id.meal_price);
            mealDescription = itemView.findViewById(R.id.meal_description);
            mealPicture = itemView.findViewById(R.id.meal_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
//                    Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show();
                    imageUrl = meals.get(position).getImage();
                    Intent intentToMealFullScreen = new Intent(context, FullScreenMealActivity.class);
                    intentToMealFullScreen.putExtra("meal name", mealName.getText());
                    intentToMealFullScreen.putExtra("meal price", mealPrice.getText());
                    intentToMealFullScreen.putExtra("meal description", mealDescription.getText());
//                    if (imageUrl.equals("") || imageUrl.equals(null)) {
//                        Log.d(TAG, "onClick: imageUrl is empty :(");
////                        imageUrl = Resources.getSystem().getString(R.string.default_art_meal_src);
//                        imageUrl = "https://i.pinimg.com/474x/66/27/c4/6627c45bb789fc0661efa010c53471e0.jpg";
//                    }
                    intentToMealFullScreen.putExtra("meal image url", imageUrl);

                    context.startActivity(intentToMealFullScreen);

                }
            });
        }
    }

}
