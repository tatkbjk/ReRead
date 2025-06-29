package com.dyingapp_v1.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.dyingapp_v1.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;

public class HeaderNavActivity extends Fragment {
    private DrawerLayout drawerLayout;
    private LinearLayout navMenuLayout;
    private ShapeableImageView imgGroup, imgCart, imgProfile;
    private NavigationView navigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.header_nav, container, false);


        imgGroup = view.findViewById(R.id.imgGroup);
        drawerLayout = getActivity().findViewById(R.id.main); // ID của DrawerLayout ở activity
        navMenuLayout = getActivity().findViewById(R.id.navigation_menu_layout); // phần menu layout chứa NavigationView
        navigationView = getActivity().findViewById(R.id.navigationView); // lấy navigation view từ layout

        imgCart = view.findViewById(R.id.imgCart);
        imgProfile = view.findViewById(R.id.imgProfile);

        SearchView searchBar = view.findViewById(R.id.searchBar);

        // 👉 Bắt sự kiện khi người dùng nhấn nút tìm kiếm (submit)
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.trim().isEmpty()) {
                    Intent intent = new Intent(getActivity(), ProductSearchActivity.class);
                    intent.putExtra("keyword", query.trim());
                    startActivity(intent);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false; // không cần xử lý khi gõ
            }
        });


        // Mở menu khi click icon group
        imgGroup.setOnClickListener(v -> {
            if (drawerLayout != null && navMenuLayout != null) {
                if (drawerLayout.isDrawerOpen(navMenuLayout)) {
                    drawerLayout.closeDrawer(navMenuLayout);
                } else {
                    drawerLayout.openDrawer(navMenuLayout);
                }
            }
        });

        // Gán listener cho NavigationView
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        }

        // Bấm icon giỏ hàng → mở MyCartActivity
        imgCart.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyCartActivity.class);
            startActivity(intent);
        });

        imgProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ManageAccountActivity.class);
            startActivity(intent);
        });

        return view;




    }

    // Xử lý khi người dùng chọn item trong NavigationView
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        SharedPreferences prefs = getActivity().getSharedPreferences("userSession", getContext().MODE_PRIVATE);
        String userId = prefs.getString("userId", null);

        if (id == R.id.profile) {
            Intent intent = new Intent(getActivity(), ManageAccountActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);

        } else if (id == R.id.event) {
            Intent intent = new Intent(getActivity(), EventListActivity.class);
            startActivity(intent);
        } else if (id == R.id.sell) {
            Intent intent = new Intent(getActivity(), QuotationActivity.class);
            startActivity(intent);
        } else if (id == R.id.wishList) {
            Intent intent = new Intent(getActivity(), ListWishlistActivity.class);
            startActivity(intent);
        } else if (id == R.id.author) {
            Intent intent = new Intent(getActivity(), AuthorActivity.class);
            startActivity(intent);
        }

        if (drawerLayout != null && navMenuLayout != null) {
            drawerLayout.closeDrawer(navMenuLayout);
        }

        return true;
    }



}
