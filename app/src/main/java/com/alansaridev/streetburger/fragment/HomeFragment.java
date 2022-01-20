package com.alansaridev.streetburger.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.alansaridev.streetburger.R;
import com.alansaridev.streetburger.adapter.MealAdapter;
import com.alansaridev.streetburger.model.MealModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private String restaurantName;

    private View shimmerMealView, shimmerFeaturedView;
    private final String TAG = HomeFragment.this.getClass().getName();
    private FirebaseDatabase database;
    private DatabaseReference restaurantRef;
    private DatabaseReference menuRef;
    private DatabaseReference categoriesRef;
    private RecyclerView allMealsRecyclerView;
    private ArrayList<MealModel> mealsList;
    private ArrayList<String> categoriesList;
    private MealAdapter adapter;
    private SearchView searchView;
    private LinearLayout mainNestedLinearLayout;
    private NestedScrollView nestedScrollView;
    private RelativeLayout relativeLayout;
    private TextView nosRepas;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        restaurantName = getString(R.string.restaurant_name);

        database = FirebaseDatabase.getInstance();

        // set offline persistence
        database.setPersistenceEnabled(true);

        restaurantRef = database.getReference("restaurants/" + restaurantName);
        menuRef = restaurantRef.child("menu");
        categoriesRef = restaurantRef.child("categories");
        categoriesRef.keepSynced(true);
        menuRef.keepSynced(true);
        allMealsRecyclerView = (RecyclerView) view.findViewById(R.id.all_meals_recycler_view);
        allMealsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        shimmerMealView = view.findViewById(R.id.shimmerMeals);
        shimmerFeaturedView = view.findViewById(R.id.shimmer_view_container2);
        searchView = view.findViewById(R.id.search_view);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
        mainNestedLinearLayout = view.findViewById(R.id.main_nested_linear_layout);
        mealsList = new ArrayList<MealModel>();
        relativeLayout = view.findViewById(R.id.relative_layout_header_view);
        nestedScrollView = view.findViewById(R.id.nested_scroll_view_main);
        nosRepas = view.findViewById(R.id.nos_repas);
        // this code was copied from stackoverflow XD
        nestedScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        ViewCompat.setNestedScrollingEnabled(allMealsRecyclerView, false);

        //get cateogiresList
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoriesList = (ArrayList<String>) snapshot.getValue();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Read meals from the database
        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                shimmerMealView.setVisibility(View.GONE);
                shimmerFeaturedView.setVisibility(View.GONE);
                nosRepas.setVisibility(View.GONE);
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: currently on: " + dataSnapshot1);
                    dataSnapshot.getRef();
                    MealModel meal = dataSnapshot1.getValue(MealModel.class);//this needs to be changed !!
                    mealsList.add(meal);
                }

                ArrayList<MealModel> currentMealCategorieList = new ArrayList<>();
                if (categoriesList != null) {
                    for (String currentCategory : categoriesList) {
                        Log.d(TAG, "onDataChange: currentCategory is: " + currentCategory);

                        //add category title
                        TextView categoryTitle = new TextView(getContext());
                        categoryTitle.setTypeface(null, Typeface.BOLD);
                        categoryTitle.setTextSize(19);
                        categoryTitle.setText(currentCategory.toUpperCase());
                        categoryTitle.setPadding(0, 5, 0, 5);
                        categoryTitle.setTypeface(null, Typeface.BOLD);
                        // Get the primary text color of the theme
                        TypedValue typedValue = new TypedValue();
                        Resources.Theme theme = getActivity().getTheme();
                        theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
                        TypedArray arr =
                                getActivity().obtainStyledAttributes(typedValue.data, new int[]{
                                        android.R.attr.colorPrimary});
                        int primaryColor = arr.getColor(0, -1);
                        categoryTitle.setTextColor(primaryColor);
                        //add margins
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(18, 35, 16, 0);
                        categoryTitle.setLayoutParams(params);

                        mainNestedLinearLayout.addView(categoryTitle);

                        //add category recyclerview
                        RecyclerView categoryRecyclerView = new RecyclerView(getContext());
                        ArrayList<MealModel> queryCategoryList = new ArrayList<>();
                        if (mealsList != null) {
                            for (MealModel currentMealObject : mealsList) {
                                for (String currentMealCategory : currentMealObject.getCategorie()) {
                                    Log.d(TAG, "onDataChange: currentMealCategory is: " + currentMealCategory);
                                    if (currentCategory.toLowerCase().contains(currentMealCategory.toLowerCase())) {
                                        Log.d(TAG, "onDataChange: currentMealObject is: " + currentMealObject);
                                        Log.d(TAG, "onDataChange: added meal " + currentMealObject.getName() + " to category section: " + currentCategory);
                                        queryCategoryList.add(currentMealObject);
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "onDataChange: mealsList == null");
                            Toast.makeText(getContext(), "mealsList == null", Toast.LENGTH_SHORT).show();
                        }

                        MealAdapter categoryAdapter = new MealAdapter(view.getContext(), queryCategoryList);
                        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                        categoryRecyclerView.setAdapter(categoryAdapter);
                        mainNestedLinearLayout.addView(categoryRecyclerView);

                    }
                } else {
                    Log.d(TAG, "onDataChange: categorieslist is null");
                    Toast.makeText(getContext(), "categories list is null", Toast.LENGTH_SHORT).show();
                }

            }

            /** while reading from the database we need to make a list of categories
             * there should be a background function to put all new categories in a seperate path
             * we will read the categories array and put it in a list
             * this list will be used in a loop
             * we could also loop through all the meals and add a category if new to a list
             * but we will do it the first way it's better
             * for every category we need to create a TextView containing the name of the category
             * for every category we need to create a recyclerview which will contain its meal cards
             * then we need to loop through the list of all the meals registered at the beginning
             * and make a new list for every new category this list will contain the meals of the same category
             * similar to the search functionality we will fill the newly created recyclerview
             */


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        //search bar functionality
        //if (searchView != null) {
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    search(s, view.getContext());
                    return true;
                }
            });
        }
        return view;
    }

    private void search(String str, Context context) {
        Log.d(TAG, "search: str is: " + str);
        if (str.equals("")) {
            Log.d(TAG, "search: str is null or empty!");
            ArrayList<MealModel> emptyList;
            emptyList = new ArrayList<MealModel>();
            adapter = new MealAdapter(context, emptyList);
            allMealsRecyclerView.setAdapter(adapter);
        } else {
            Log.d(TAG, "search: str is not empty!");
            ArrayList<MealModel> searchList = new ArrayList<>();
            for (MealModel object : mealsList) {
                if(object.getDescription() != null) {
                    if (object.getDescription().toLowerCase().contains(str.toLowerCase()) || object.getName().toLowerCase().contains(str.toLowerCase())) {
                        searchList.add(object);
                    }
                } else {
                    if (object.getName().toLowerCase().contains(str.toLowerCase())) {
                        searchList.add(object);
                    }

                }

                List<String> categoriesNames = object.getCategorie();
//            Toast.makeText(context, "" + categoriesNames, Toast.LENGTH_SHORT).show();
            }
            MealAdapter mealAdapter = new MealAdapter(context, searchList);
            allMealsRecyclerView.setAdapter(mealAdapter);
        }
    }

    /**
     * loop through meals
     * for each meal get a list of catgories if new category make new list
     * add meal to list
     * if old category (have been created before) just add meal to it
     * we will have a list of all meals in a category
     * loop through a list of categories
     * for each category create a text view and a recyclerview
     * put title in text view (category name)
     * populate recyclerview with meallist
     *
     * @param categorie
     * @param context
     */
    private void queryCategory(String categorie, Context context) {

    }
}