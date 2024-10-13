package com.example.garbagereporting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnAddItems, btnViewItems, btnLogout,btnViewOrderDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddItems = findViewById(R.id.btnAddItems);
        btnViewItems = findViewById(R.id.btnViewItems);
        btnLogout = findViewById(R.id.btnLogout);
        btnViewOrderDetails = findViewById(R.id.btnViewOrderDetails);

        btnAddItems.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddItemsActivity.class);
            startActivity(intent);
        });

        btnViewItems.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewItemsActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            // Logout logic here
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
        });


        btnViewOrderDetails.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderDetailsActivity.class);
            startActivity(intent);
        });
    }
}
