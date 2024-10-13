package com.example.garbagereporting;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class OrderDetailsActivity extends AppCompatActivity {
    private ListView ordersListView;
    private ArrayList<String> ordersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        // Initialize the ListView
        ordersListView = findViewById(R.id.ordersListView);
        ordersList = new ArrayList<>();

        // Retrieve orders from internal storage
        retrieveOrders();

        // Set up the adapter for the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ordersList);
        ordersListView.setAdapter(adapter);

        // Handle item click to delete the record
        ordersListView.setOnItemClickListener((parent, view, position, id) -> {
            deleteOrder(position);
            adapter.notifyDataSetChanged(); // Notify adapter of changes
        });
    }

    private void retrieveOrders() {
        try (FileInputStream fis = openFileInput("item_details.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ordersList.add(line); // Add each order to the list
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to retrieve order details", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteOrder(int position) {
        String orderToDelete = ordersList.get(position);
        ordersList.remove(position); // Remove from the list

        // Save the updated list back to the internal storage
        saveUpdatedOrders();

        Toast.makeText(this, "Deleted: " + orderToDelete, Toast.LENGTH_SHORT).show();
    }

    private void saveUpdatedOrders() {
        // Save the updated orders back to internal storage
        try (FileOutputStream fos = openFileOutput("item_details.txt", MODE_PRIVATE)) { // Use MODE_PRIVATE to overwrite
            for (String order : ordersList) {
                fos.write((order + "\n").getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save updated orders", Toast.LENGTH_SHORT).show();
        }
    }
}
