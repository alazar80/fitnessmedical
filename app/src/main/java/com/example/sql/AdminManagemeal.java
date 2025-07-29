package com.example.sql;

import static androidx.databinding.adapters.TextViewBindingAdapter.setText;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminManagemeal extends AppCompatActivity {
    private RecyclerView recyclerViewMeals;
    private MealAdapter adapter;
    private List<Meal> mealList;
    private int adminId;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private Button btnAddMeals, btnAssignMeals;
    TextView ManageMealsTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_manage_meal);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        ManageMealsTitle=findViewById(R.id.ManageDoctorMealsTitle);
         ManageMealsTitle.setText("Admin Manage Meals ");
        // grab doctorId
        adminId = getIntent().getIntExtra("admin_id", -1);
        ToastUtil.show(this, "adminId="+adminId, 1/3);
        Log.d("DoctorManageMeal","adminId="+adminId);

        // find views
        recyclerViewMeals = findViewById(R.id.mealRecyclerView);
        progressBar       = findViewById(R.id.progressBar);
        swipeRefresh      = findViewById(R.id.swipeRefresh);
        btnAddMeals       = findViewById(R.id.addMealsBtn);
        btnAssignMeals    = findViewById(R.id.btnAssignMeals);
        ImageView back    = findViewById(R.id.backButton);
        SearchView searchView = findViewById(R.id.searchViewMeals);

        // init list & adapter
        mealList = new ArrayList<>();
        adapter = new MealAdapter(
                this,
                mealList,
                true,  // editable mode
                new MealAdapter.OnMealClickListener() {
                    @Override
                    public void onEditClick(Meal meal) {
                        showEditMealDialog(AdminManagemeal.this, meal, true, () -> adapter.notifyDataSetChanged());
                    }

                    @Override
                    public void onDeleteClick(Meal meal) {
                        deleteMeal(meal.getId());
                    }

                    @Override
                    public void onItemClick(Meal meal) {
                        // your code
                    }
                }
        );
        recyclerViewMeals.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMeals.setAdapter(adapter);

        // pull-to-refresh
        swipeRefresh.setOnRefreshListener(this::fetchMeals);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        // buttons
        btnAddMeals.setOnClickListener(v ->
                startActivity(new Intent(this, AdminAddMealActivity.class)
                        .putExtra("admin_id", adminId))
        );
        btnAssignMeals.setOnClickListener(v ->
                startActivity(new Intent(this, AdminAssignMealActivity.class)
                        .putExtra("admin_id", adminId))
        );
        back.setOnClickListener(v -> onBackPressed());

        // first load
        fetchMeals();
    }

    private void fetchMeals() {
        progressBar.setVisibility(View.VISIBLE);
        String url = ApiConfig.GET_MEALS_BY_ADMIN_FOR_HOME + "?admin_id=" + adminId;
        Log.d("DoctorManageMeal","GET "+url);

        StringRequest req = new StringRequest(
                Request.Method.GET, url,
                this::handleFetchMealsResponse,
                err -> {
                    ToastUtil.show(this, "Failed to load meals", 1/3);
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                }
        );
        Volley.newRequestQueue(this).add(req);
    }
    private void handleFetchMealsResponse(String response) {
        Log.d("DoctorManageMeal","raw resp: "+response);
        try {
            JSONObject jo   = new JSONObject(response);
            JSONArray arr   = jo.getJSONArray("meals");

            // 1) build fresh list
            List<Meal> newMeals = new ArrayList<>(arr.length());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                Meal m = new Meal(
                        o.getInt("id"),
                        o.getString("title"),
                        o.getInt("calories"),
                        o.getString("description"),
                        o.getString("category"),
                        o.getString("fitnessGoal"),
                        o.getString("mealType"),
                        o.getString("image_id")
                );
                newMeals.add(m);
            }

            // 2) update adapter (this resets both full & filtered lists)
            adapter.updateData(newMeals);

        } catch (Exception e) {
            ToastUtil.show(this, "Error parsing meals", 1/3);
            Log.e("DoctorManageMeal","parsing error",e);
        } finally {
            progressBar.setVisibility(View.GONE);
            swipeRefresh.setRefreshing(false);
        }
    }


    private void updateMeal(String url, String id,
                            String title, String desc, String calories,
                            String category, String fitnessGoal,
                            String mealType, String imageId,
                            Runnable onSuccess) {
        StringRequest req = new StringRequest(
                Request.Method.POST, url,
                resp -> {
                    // Optionally parse the response here (e.g., {"success":true,...})
                    onSuccess.run();
                    ToastUtil.show(this, "Meal updated",1/3);
                },
                err -> ToastUtil.show(this, "Update failed", 1/3)
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> p = new HashMap<>();
                p.put("id",               id);
                p.put("title",            title);
                p.put("description",      desc);
                p.put("calories",         calories);
                p.put("category",         category);
                p.put("fitnessGoal",      fitnessGoal);
                p.put("mealType",         mealType);
                p.put("image_id",         imageId);
                return p;
            }
        };
        Volley.newRequestQueue(this).add(req);
    }


    private void showEditMealDialog(Context context,
                                    Meal meal,
                                    boolean isAdmin,
                                    Runnable onEdited) {
        // Inflate the dialog view
        View editView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_edit_meal, null);
        EditText titleInput  = editView.findViewById(R.id.editMealTitle);
        EditText descInput  = editView.findViewById(R.id.editMealDescription);
        EditText caloriesInput = editView.findViewById(R.id.editMealCalories);
        Spinner categorySpinner = editView.findViewById(R.id.editMealCategory);
        Spinner goalSpinner = editView.findViewById(R.id.editMealGoal);
        Spinner typeSpinner = editView.findViewById(R.id.editMealType);
        EditText imageIdInput = editView.findViewById(R.id.editMealImageId);

        // Prefill with current values
        titleInput.setText(meal.getTitle());
        descInput.setText(meal.getDescription());
        caloriesInput.setText(String.valueOf(meal.getCalories()));
        // Assume you’ve populated your spinners and can select current items:
        categorySpinner.setSelection(((ArrayAdapter<String>)categorySpinner.getAdapter())
                .getPosition(meal.getCategory()));
        goalSpinner.setSelection(((ArrayAdapter<String>)goalSpinner.getAdapter())
                .getPosition(meal.getFitnessGoal()));
        typeSpinner.setSelection(((ArrayAdapter<String>)typeSpinner.getAdapter())
                .getPosition(meal.getMealType()));
        imageIdInput.setText(meal.getImageId());

        // Show dialog
        new AlertDialog.Builder(context)
                .setTitle("Edit Meal")
                .setView(editView)
                .setPositiveButton("Save", (d, w) -> {
                    // Collect all fields:
                    String newTitle = titleInput.getText().toString().trim();
                    String newDesc = descInput.getText().toString().trim();
                    String newCalories = caloriesInput.getText().toString().trim();
                    String newCategory = categorySpinner.getSelectedItem().toString();
                    String newGoal = goalSpinner.getSelectedItem().toString();
                    String newType = typeSpinner.getSelectedItem().toString();
                    String newImageId = imageIdInput.getText().toString().trim();
                    String mealId = String.valueOf(meal.getId());

                    // Choose URL based on role
                    String url = isAdmin
                            ? ApiConfig.ADMIN_UPDATE_MEAL_URL
                            : ApiConfig.DOCTOR_UPDATE_MEAL_URL;

                    // Call your helper to POST and on success update UI
                    updateMeal(url, mealId,
                            newTitle, newDesc, newCalories,
                            newCategory, newGoal, newType, newImageId,
                            () -> {
                                // Update local object & notify RecyclerView
                                meal.setTitle(newTitle);
                                meal.setDescription(newDesc);
                                meal.setCalories(Integer.parseInt(newCalories));
                                meal.setCategory(newCategory);
                                meal.setFitnessGoal(newGoal);
                                meal.setMealType(newType);
                                meal.setImageId(newImageId);
                                onEdited.run();
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void deleteMeal(int mealId) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest req = new StringRequest(
                Request.Method.POST, ApiConfig.ADMIN_DELETE_MEAL,
                resp -> {
                    ToastUtil.show(this, "Meal deleted", 1/3);
                    fetchMeals();
                },
                err -> {
                    ToastUtil.show(this, "Failed to delete", 1/3);
                    progressBar.setVisibility(View.GONE);
                }
        ) {
            @Override protected Map<String,String> getParams() {
                Map<String,String> p = new HashMap<>();
                p.put("id", String.valueOf(mealId));
                return p;
            }
        };
        Volley.newRequestQueue(this).add(req);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        startActivity(new Intent(this, AdminActivity.class)
//                .putExtra("admin_id", adminId)
//                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
//        finish();
//    }
}
