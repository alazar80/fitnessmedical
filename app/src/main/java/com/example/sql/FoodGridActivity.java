package com.example.sql;

import static androidx.databinding.DataBindingUtil.setContentView;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodGridActivity extends AppCompatActivity {
    GridView grid;
    FoodAdapter adapter;
    List<Food> foods = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_grid);

        grid = findViewById(R.id.gridFoods);
        adapter = new FoodAdapter(this, foods);
        grid.setAdapter(adapter);

        // fetch “Juices” (or “Foods”) from Open Food Facts
        RetrofitClient.getService()
            .fetchJuices("Juices", "product_name,image_front_url")
            .enqueue(new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call,
                                       Response<SearchResponse> r) {
                    if (r.isSuccessful() && r.body()!=null) {
                        foods.clear();
                        foods.addAll(r.body().products);
                        adapter.notifyDataSetChanged();
                    }
                }
                @Override public void onFailure(Call<SearchResponse> call, Throwable t) {
                    Toast.makeText(FoodGridActivity.this,
                                   "Load failed: "+t.getMessage(),
                                   Toast.LENGTH_SHORT).show();
                }
            });
    }
}
