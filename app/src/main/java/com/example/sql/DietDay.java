package com.example.sql;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class DietDay extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietday);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        int userId = getIntent().getIntExtra("user_id", -1);
        if (userId < 0) { finish(); return; }
        ImageView backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->onBackPressed());
        ViewPager2 vp = findViewById(R.id.viewPager);
        TabLayout tabs = findViewById(R.id.tabLayout);
        DietPagerAdapter adapter = new DietPagerAdapter(this, userId);

        vp.setAdapter(adapter);
        new TabLayoutMediator(tabs, vp,
                (tab, pos) -> tab.setText(pos == 0 ? "Main Menu" : "Recommended")
        ).attach();

        SearchView sv = findViewById(R.id.searchView);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }
            @Override public boolean onQueryTextChange(String query) {
                Fragment f = adapter.getFragmentAt(vp.getCurrentItem());
                if (f instanceof MainMenuFragment) {
                    ((MainMenuFragment) f).filter(query);
                } else if (f instanceof RecommendedFragment) {
                    ((RecommendedFragment) f).filter(query);
                }
                return true;
            }
        });
    }

//    private ArrayList<Meal> fetchRecommendedMeals() {
//        ArrayList<Meal> list = new ArrayList<>();
//        list.add(new Meal("Oatmeal with Almonds", 250, "Hearty oatmeal topped with almonds",
//                "Recommended Diet", "Energy", "Breakfast", "oatmeal_almonds"));
//        list.add(new Meal("Steamed Salmon & Broccoli", 450, "Omega-rich salmon with greens",
//                "Recommended Diet", "Protein", "Lunch", "salmon_broccoli"));
//        list.add(new Meal("Greek Yogurt & Berries", 200, "Probiotic yogurt with fresh berries",
//                "Recommended Diet", "Digestive", "Snack", "yogurt_berries"));
//        return list;
//    }

    static class DietPagerAdapter extends FragmentStateAdapter {
        private final MainMenuFragment    mainFrag;
        private final RecommendedFragment recFrag;

        public DietPagerAdapter(FragmentActivity fa, int userId) {
            super(fa);
            mainFrag = MainMenuFragment.newInstance(userId);
            recFrag  = RecommendedFragment.newInstance(userId);
        }

        @Override
        public Fragment createFragment(int pos) {
            return (pos == 0) ? mainFrag : recFrag;
        }

        @Override
        public int getItemCount() {
            return 2;
        }

        /** Allows DietDay’s SearchView to grab the currently‐visible fragment. */
        public Fragment getFragmentAt(int pos) {
            return (pos == 0) ? mainFrag : recFrag;
        }
    }
}
