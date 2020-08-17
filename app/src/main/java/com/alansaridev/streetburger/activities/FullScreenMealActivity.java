package com.alansaridev.streetburger.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alansaridev.streetburger.R;
import com.alansaridev.streetburger.model.VariantModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class FullScreenMealActivity extends AppCompatActivity {

    private static final String TAG = FullScreenMealActivity.class.getName();
    String restaurantName;

    float mainPrice = 0;
    float variantsTotalPrice = 0;
    float totalPrice = 0;
    Button addToCartBtn;
    boolean addToCartBtnEnabled;

    ImageView mealImage;
    TextView mealName, mealDescription, mealPrice;
    String stringMealName, stringMealDescription, stringMealPrice, stringMealImageSrc;

    FirebaseDatabase database;
    DatabaseReference variantsRef;
    DatabaseReference menuRef;

    LinearLayout fsMainNestedLinearLayout;
    ArrayList<VariantModel> variantsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_meal);

        restaurantName = getString(R.string.restaurant_name);


        mealImage = findViewById(R.id.fs_imageView);
        mealName = findViewById(R.id.fs_meal_name);
        mealDescription = findViewById(R.id.fs_meal_description);
        mealPrice = findViewById(R.id.fs_meal_price);
        addToCartBtn = findViewById(R.id.fs_add_to_cart_btn);

        toggleAddToCartBtn(false);

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addToCartBtnEnabled == false) {
                    Toast.makeText(FullScreenMealActivity.this, "Cette fonction est desactivee, pour commander merci de nous contacter :)", Toast.LENGTH_LONG).show();
                }
            }
        });

        Intent intent = getIntent();
        stringMealName = intent.getStringExtra("meal name");
        stringMealDescription = intent.getStringExtra("meal description");
        stringMealPrice = intent.getStringExtra("meal price");
        stringMealImageSrc = intent.getStringExtra("meal image url");

        mealName.setText(stringMealName);
        mealDescription.setText(stringMealDescription);
        mealPrice.setText(stringMealPrice);
        updatePrice(variantsTotalPrice, Float.parseFloat(stringMealPrice.split(" ")[0])); //this has an euro symbol
//        if (stringMealImageSrc.equals("") || stringMealImageSrc.equals(null)) {
//            String defaultArtMealSrc = getString(R.string.default_art_meal_src);
//            Picasso.get().load(defaultArtMealSrc).fit().into(mealImage);
//        } else {
//            Picasso.get().load(stringMealImageSrc).fit().into(mealImage);
//        }
        Log.d(TAG, "onCreate: the stringMealIMageSrc is: " + stringMealImageSrc);

        Picasso.get().load(stringMealImageSrc).fit().centerCrop().into(mealImage);
        database = FirebaseDatabase.getInstance();
        menuRef = database.getReference("restaurants/" + restaurantName + "/menu/" + stringMealName);
        variantsRef = menuRef.child("variants");

        fsMainNestedLinearLayout = findViewById(R.id.fs_main_nested_linear_layout);

        //read variants from the db
        variantsList = new ArrayList<VariantModel>();
        variantsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshotElement : dataSnapshot.getChildren()) {
                    // adding variantName textView
                    String variantNameStr = dataSnapshotElement.child("name").getValue().toString();
                    TextView variantNameTextView = new TextView(getApplicationContext());
                    variantNameTextView.setText(variantNameStr.toUpperCase());
                    variantNameTextView.setTypeface(null, Typeface.BOLD);
                    variantNameTextView.setTextSize(19);
                    variantNameTextView.setPadding(0, 5, 0, 5);
                    variantNameTextView.setTypeface(null, Typeface.BOLD);
                    // Get the primary text color of the theme
                    TypedValue typedValue = new TypedValue();
                    Resources.Theme theme = FullScreenMealActivity.this.getTheme();
                    theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
                    TypedArray arr =
                            FullScreenMealActivity.this.obtainStyledAttributes(typedValue.data, new int[]{
                                    android.R.attr.colorPrimary});
                    int primaryColor = arr.getColor(0, -1);
                    variantNameTextView.setTextColor(primaryColor);
                    //add margins
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(18, 35, 16, 0);
                    variantNameTextView.setLayoutParams(params);
                    fsMainNestedLinearLayout.addView(variantNameTextView);
                    // adding variants
                    // check for type of variant
                    String variantType = dataSnapshotElement.child("type").getValue().toString();
                    Log.d(TAG, "onDataChange: variant type is: " + variantType);
                    if (variantType.equals("single_option")) {
                        Log.d(TAG, "onDataChange: variant type is indeed single_option!");
                        ArrayList<String> variantList = (ArrayList<String>) dataSnapshotElement.child("options").getValue();
                        Log.d(TAG, "onDataChange: variantList is: " + variantList);
                        //populate a list of the variant's options
                        ArrayList<String> variantOptionsList = new ArrayList<String>();
                        for (String variantElement : variantList) {
                            Log.d(TAG, "onDataChange: variantElment is: " + variantElement);
                            String variantOptionStr = variantElement;
//                            TextView variantOptionTextView = new TextView(getApplicationContext());
//                            variantOptionTextView.setText(variantOptionStr);
//                            fsMainNestedLinearLayout.addView(variantOptionTextView);
                            variantOptionsList.add(variantOptionStr);
                        }
                        // add radiogroup
                        final RadioGroup radioGrp = new RadioGroup(getApplicationContext());
                        LinearLayout.LayoutParams radioParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        radioParams.setMargins(28, 5, 26, 45);
                        radioGrp.setLayoutParams(radioParams);
                        //create radio buttons
                        for (int i = 0; i < variantOptionsList.size(); i++) {
                            RadioButton radioButton = new RadioButton(getApplicationContext());
                            radioButton.setText(variantOptionsList.get(i));
                            radioButton.setTextSize(14);
                            radioButton.setId(i);
                            radioGrp.addView(radioButton);
                        }

                        //set listener to radio button group
                        radioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                int checkedRadioButtonId = radioGrp.getCheckedRadioButtonId();
                                RadioButton radioBtn = (RadioButton) findViewById(checkedRadioButtonId);
                                Toast.makeText(FullScreenMealActivity.this, radioBtn.getText(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        fsMainNestedLinearLayout.addView(radioGrp);
                    }

                    // this is the only case where single option is used
                    // if there is no price we'll use 0 instead
                    final ArrayList<CheckBox> checkedList = new ArrayList<>();
                    final HashMap<CheckBox, Float> checkBoxPriceHashMap = new HashMap<>();
                    if (variantType.equals("single_option_price_main")) {
                        Log.d(TAG, "onDataChange: variant type is indeed single_option_price");
                        final int maxOptions = 1;
                        final int minOptions = 1;
                        //populate a list of the variant's objects
                        // we will create a checkbox and a price textview tag for each vatian option
                        // we will add them to the fs activity
                        ArrayList<VariantModel> variantObjectList = new ArrayList<VariantModel>();
                        final ArrayList<CheckBox> checkBoxGroupList = new ArrayList<>();
                        for (DataSnapshot currentVariant : dataSnapshotElement.child("options").getChildren()) {
                            VariantModel variant = currentVariant.getValue(VariantModel.class);
//                            Log.d(TAG, "onDataChange: current Variant name is: " + variant.getName());
                            variantObjectList.add(variant);
                            final CheckBox currentVariantCheckbox = new CheckBox(getApplicationContext());
                            checkBoxGroupList.add(currentVariantCheckbox);
                            Log.d(TAG, "onDataChange: added " + currentVariantCheckbox + " to the checkboxgrouplist; size = " + checkBoxGroupList.size());
                            if ((boolean) currentVariant.child("checked").getValue()) {
                                checkedList.add(currentVariantCheckbox);
                            }
                            currentVariantCheckbox.setChecked((Boolean) currentVariant.child("checked").getValue());
                            checkBoxPriceHashMap.put(currentVariantCheckbox, currentVariant.child("price").getValue(float.class));
                            if (currentVariantCheckbox.isChecked()) {
                                if (currentVariant.child("price").exists()) {
                                    updatePrice(variantsTotalPrice, currentVariant.child("price").getValue(float.class));
                                } else {
                                    updatePrice(variantsTotalPrice, 0);
                                }

                            }
                            LinearLayout checkboxGroupLayout = new LinearLayout(getApplicationContext());
                            checkboxGroupLayout.setOrientation(LinearLayout.HORIZONTAL);
                            // add variant name
                            currentVariantCheckbox.setText(currentVariant.child("name").getValue(String.class));
                            checkboxGroupLayout.addView(currentVariantCheckbox);
                            // add variant price if exists
                            if (currentVariant.child("price").exists()) {
                                checkBoxPriceHashMap.put(currentVariantCheckbox, currentVariant.child("price").getValue(float.class));
                                TextView currentVariantPriceTag = new TextView(getApplicationContext());
                                currentVariantPriceTag.setText(" " + currentVariant.child("price").getValue(float.class).toString() + " €");
                                currentVariantPriceTag.setTextSize(14);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(28, 5, 26, 45);
                                checkboxGroupLayout.addView(currentVariantPriceTag);
                            } else {
                                checkBoxPriceHashMap.put(currentVariantCheckbox, (float) 0);
//                                Toast.makeText(FullScreenMealActivity.this, "no price for main meal error!!", Toast.LENGTH_SHORT).show();

                            }
                            // now trying to inflate the already created view to avoid putting so much detail in this activity
//                            View inflatedLayout = getLayoutInflater().inflate(R.layout.variant_item, null, false);

                            currentVariantCheckbox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Boolean currentCheckState = currentVariantCheckbox.isChecked();
                                    if (currentCheckState) {
                                        checkedList.add(currentVariantCheckbox);
                                        updatePrice(checkBoxPriceHashMap.get(currentVariantCheckbox), mainPrice);
                                        Log.d(TAG, "onClick: added " + currentVariantCheckbox + " ;checkedList.size is now: " + checkedList.size());
                                        if (checkedList.size() >= maxOptions) {
                                            updatePrice(-checkBoxPriceHashMap.get(checkedList.get(0)), mainPrice);
                                            checkedList.get(0).setChecked(false);
                                            checkedList.remove(0);
                                            Log.d(TAG, "onCheckedChanged: checkedList's size is now: " + checkedList.size());
                                        }
                                    } else if (checkedList.size() <= minOptions) {
                                        currentVariantCheckbox.setChecked(true);
                                    } else {
                                        checkedList.remove(currentVariantCheckbox);
                                    }
                                }
                            });
                            fsMainNestedLinearLayout.addView(checkboxGroupLayout);
                        }
                        Log.d(TAG, "onDataChange: the final variantObjectList is: " + variantObjectList.size());
                    }

                    if (variantType.equals("multiple_option_price")) {
                        Log.d(TAG, "onDataChange: variant type is indeed single_option_price");
                        final int maxOptions = Integer.parseInt(dataSnapshotElement.child("max options").getValue().toString());
                        //populate a list of the variant's objects
                        // we will create a checkbox and a price textview tag for each vatian option
                        // we will add them to the fs activity
                        ArrayList<VariantModel> variantObjectList = new ArrayList<VariantModel>();
                        final ArrayList<CheckBox> checkBoxGroupList = new ArrayList<>();
                        for (DataSnapshot currentVariant : dataSnapshotElement.child("options").getChildren()) {
//                            VariantModel variant = currentVariant.getValue(VariantModel.class);
////                            Log.d(TAG, "onDataChange: current Variant name is: " + variant.getName());
//                            variantObjectList.add(variant);
                            final CheckBox currentVariantCheckbox = new CheckBox(getApplicationContext());
                            checkBoxGroupList.add(currentVariantCheckbox);
                            Log.d(TAG, "onDataChange: added " + currentVariantCheckbox + " to the checkboxgrouplist; size = " + checkBoxGroupList.size());
                            currentVariantCheckbox.setChecked((Boolean) currentVariant.child("checked").getValue());
                            LinearLayout checkboxGroupLayout = new LinearLayout(getApplicationContext());
                            checkboxGroupLayout.setOrientation(LinearLayout.HORIZONTAL);

                            currentVariantCheckbox.setText(currentVariant.child("name").getValue(String.class));
                            TextView currentVariantPriceTag = new TextView(getApplicationContext());
                            checkboxGroupLayout.addView(currentVariantCheckbox);
                            if (currentVariant.child("price").exists()) {
                                checkBoxPriceHashMap.put(currentVariantCheckbox, currentVariant.child("price").getValue(float.class));
                                currentVariantPriceTag.setText("+" + currentVariant.child("price").getValue(float.class).toString() + " €");
                                checkboxGroupLayout.addView(currentVariantPriceTag);
                            } else {
                                checkBoxPriceHashMap.put(currentVariantCheckbox, (float) 0);
                            }

                            // now trying to inflate the already created view to avoid putting so much detail in this activity
//                            View inflatedLayout = getLayoutInflater().inflate(R.layout.variant_item, null, false);

                            currentVariantCheckbox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Boolean currentCheckState = currentVariantCheckbox.isChecked();
                                    if (currentCheckState) {
                                        checkedList.add(currentVariantCheckbox);
                                        updatePrice(checkBoxPriceHashMap.get(currentVariantCheckbox), mainPrice);
                                        Log.d(TAG, "onClick: added " + currentVariantCheckbox + " ;checkedList.size is now: " + checkedList.size());
                                        if (checkedList.size() > maxOptions) {
                                            updatePrice(-checkBoxPriceHashMap.get(checkedList.get(0)), mainPrice);
                                            checkedList.get(0).setChecked(false);
                                            checkedList.remove(0);

                                            Log.d(TAG, "onCheckedChanged: checkedList's size is now: " + checkedList.size());
                                        }
                                    } else {
                                        updatePrice(-checkBoxPriceHashMap.get(currentVariantCheckbox), mainPrice);
                                        checkedList.remove(currentVariantCheckbox);
                                        Log.d(TAG, "onCheckedChanged: checkedList's size is now: " + checkedList.size() + "!");
                                    }
                                }
                            });
                            fsMainNestedLinearLayout.addView(checkboxGroupLayout);
                        }
                        Log.d(TAG, "onDataChange: the final variantObjectList is: " + variantObjectList.size());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updatePrice(float variantPrice, @Nullable float mainPrice) {
        this.mainPrice = mainPrice;
        variantsTotalPrice += variantPrice;
        totalPrice = mainPrice + variantsTotalPrice;
        addToCartBtn.setText("ajouter au panier  ‧  " + totalPrice + " €");
    }

    public void toggleAddToCartBtn(boolean toEnable) {
//        addToCartBtn.setClickable(toEnable);
        if (!toEnable) {
            addToCartBtn.setBackgroundColor(getResources().getColor(R.color.colorGrey));
            addToCartBtnEnabled = false;
        } else {
            addToCartBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            addToCartBtnEnabled = true;
        }

    }
}