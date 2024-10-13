package com.example.garbagereporting;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView; // Make sure this is imported
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class ViewItemsActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private LinearLayout itemsContainer;
    private HashMap<String, List<File>> categoryItemsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_items);

        itemsContainer = findViewById(R.id.itemsContainer);
        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // Check and request permissions if necessary
        if (hasStoragePermission()) {
            loadItems();
        } else {
            requestStoragePermission();
        }
    }

    // Check if storage permission is granted
    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Permissions are not required for devices below Android 6.0
    }

    // Request storage permission
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadItems(); // Load items if permission is granted
            } else {
                Toast.makeText(this, "Permission denied to read storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Load items from storage and display them in the container
    private void loadItems() {
        File directory = getFilesDir();  // Internal storage (you don't need permission for this)
        File[] files = directory.listFiles();

        if (files != null && files.length > 0) {
            categoryItemsMap = new HashMap<>();

            // Organize files by category
            for (File file : files) {
                if (file.getName().endsWith(".jpg")) {
                    String[] details = file.getName().replace(".jpg", "").split("_");
                    String itemCategory = details.length > 3 ? details[3] : "Uncategorized"; // Get the category

                    // Add the file to the corresponding category list
                    if (!categoryItemsMap.containsKey(itemCategory)) {
                        categoryItemsMap.put(itemCategory, new ArrayList<>());
                    }
                    categoryItemsMap.get(itemCategory).add(file);
                }
            }

            // Clear previous items
            itemsContainer.removeAllViews();

            // Create views for each category and its items
            for (String category : categoryItemsMap.keySet()) {
                addCategorySection(category, categoryItemsMap.get(category));
            }
        } else {
            Toast.makeText(this, "No items found", Toast.LENGTH_SHORT).show();
        }
    }

    // Add a section for a specific category
    private void addCategorySection(String category, List<File> items) {
        // Add category heading
        TextView categoryHeading = new TextView(this);
        categoryHeading.setText(category);
        categoryHeading.setTextSize(20);
        categoryHeading.setPadding(0, 20, 0, 10);
        itemsContainer.addView(categoryHeading);

        // Add items under this category
        for (File file : items) {
            String[] details = file.getName().replace(".jpg", "").split("_");
            String itemName = details.length > 0 ? details[0] : "Unknown";
            String itemPrice = details.length > 1 ? details[1] : "0";
            String itemQuantity = details.length > 2 ? details[2] : "0";

            LinearLayout itemLayout = createItemLayout(file, itemName, itemPrice, itemQuantity);
            itemsContainer.addView(itemLayout);
        }
    }

    // Create a layout for displaying an item
    private LinearLayout createItemLayout(File file, String name, String price, String quantity) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setPadding(10, 10, 10, 10);

        ImageView itemImageView = new ImageView(this);
        itemImageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        itemImageView.setImageURI(Uri.fromFile(file));
        itemImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        LinearLayout detailsLayout = new LinearLayout(this);
        detailsLayout.setOrientation(LinearLayout.VERTICAL);
        detailsLayout.setPadding(20, 0, 0, 0);

        TextView nameView = new TextView(this);
        nameView.setText("Name: " + name);

        TextView priceView = new TextView(this);
        priceView.setText("Price: $" + price);

        TextView quantityView = new TextView(this);
        quantityView.setText("Quantity: " + quantity);

        detailsLayout.addView(nameView);
        detailsLayout.addView(priceView);
        detailsLayout.addView(quantityView);

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(v -> {
            if (file.delete()) {
                Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
                itemsContainer.removeView(itemLayout);
            } else {
                Toast.makeText(this, "Failed to delete item", Toast.LENGTH_SHORT).show();
            }
        });

        itemLayout.addView(itemImageView);
        itemLayout.addView(detailsLayout);
        itemLayout.addView(deleteButton);

        return itemLayout;
    }
}
