package com.example.garbagereporting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OrderSummaryActivity extends AppCompatActivity {

    private TextView orderDetailsView;
    private Button btnProceedToPay, btnDownload;
    private String orderSummary;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;
    private static int orderCount = 0; // Keeps track of the number of orders

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        // Initialize views
        orderDetailsView = findViewById(R.id.orderDetailsView);
        btnProceedToPay = findViewById(R.id.btnProceedToPay);
        btnDownload = findViewById(R.id.btnDownload);

        // Get the order summary passed from the previous activity
        orderSummary = getIntent().getStringExtra("ORDER_SUMMARY");

        // Initialize order count based on existing orders
        orderCount = getExistingOrderCount(); // Get existing order count

        orderCount++; // Increment the order count for a new order
        String orderNumber = "Order No: " + orderCount; // Generate the order number
        orderSummary = orderNumber + "\n" + orderSummary; // Prepend order number to order summary
        orderDetailsView.setText(orderSummary);

        btnProceedToPay.setOnClickListener(v -> {
            // Redirect to bKash app or payment activity
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.bkash.com/en/business"));
            startActivity(intent);
        });

        btnDownload.setOnClickListener(v -> {
            // Check for write permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
            } else {
                downloadOrderSlip(orderSummary, orderNumber);
            }
        });
    }

    private int getExistingOrderCount() {
        int count = 0;
        try {
            FileInputStream fis = openFileInput("item_details.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            while (reader.readLine() != null) {
                count++;
            }
            reader.close();
        } catch (IOException e) {
            Log.e("OrderSummaryActivity", "Error reading order count", e);
        }
        return count;
    }

    private void downloadOrderSlip(String orderDetails, String orderNumber) {
        // Create a file in the external storage directory with the order number
        String fileName = "OrderSlip_" + orderNumber + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".txt";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(orderDetails.getBytes());
            fos.flush();
            Toast.makeText(this, "Order slip downloaded to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            saveOrderToInternalStorage(orderNumber, orderDetails);
            extractAndSaveOrderDetails(orderNumber, orderDetails);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to download order slip", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveOrderToInternalStorage(String orderNumber, String orderDetails) {
        // Save the order details to internal storage for later access
        try (FileOutputStream fos = openFileOutput("orders.txt", MODE_APPEND)) {
            fos.write((orderNumber + "\n" + orderDetails + "\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractAndSaveOrderDetails(String orderNumber, String orderDetails) {
        // Split the order details based on new lines
        String[] lines = orderDetails.split("\n");
        ArrayList<String> itemDetails = new ArrayList<>();
        double totalPrice = 0.0;

        // Assume the ordered items are listed after the order number
        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (!line.isEmpty()) {
                // Split items by commas
                String[] items = line.split(","); // Assuming each line after the first contains items

                // Loop through each item to extract details
                for (String item : items) {
                    String[] parts = item.split(":");
                    if (parts.length > 1) {
                        String itemName = parts[0].trim();
                        String[] priceAndQuantity = parts[1].split("\\|");

                        // Initialize variables for price and quantity
                        double itemPrice = 0.0;
                        double itemQuantity = 0.0;

                        // Handle price
                        if (priceAndQuantity.length > 0) {
                            try {
                                itemPrice = Double.parseDouble(priceAndQuantity[0].trim());
                            } catch (NumberFormatException e) {
                                Log.d("OrderDetails", "Invalid price format for: " + item);
                            }
                        }

                        // Handle quantity
                        if (priceAndQuantity.length > 1) {
                            try {
                                itemQuantity = Double.parseDouble(priceAndQuantity[1].trim());
                            } catch (NumberFormatException e) {
                                Log.d("OrderDetails", "Invalid quantity format for: " + item);
                            }
                        }

                        // Calculate total price for this item
                        totalPrice += itemPrice * itemQuantity; // Aggregate the total price

                        // Format the item detail
                        itemDetails.add(itemName + " (" + priceAndQuantity[0] + ")");
                    } else {
                        Log.d("OrderDetails", "Item format incorrect for item: " + item);
                    }
                }
            }
        }

        // Create the record in the desired format (with date and time included only once)
        String record = orderNumber + "|" + String.join(", ", itemDetails) + "|" + String.format("%.2f", totalPrice);

        // Save item details to internal storage
        try (FileOutputStream fos = openFileOutput("item_details.txt", MODE_APPEND)) {
            fos.write((record + "\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadOrderSlip(orderSummary, "Order No: " + orderCount); // Pass the order number
            } else {
                Toast.makeText(this, "Permission denied to write to external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String generateOrderSummary(String orderedItems) {
        StringBuilder summary = new StringBuilder();
        summary.append("================================================\n");
        summary.append("                  Order Summary                  \n");
        summary.append("================================================\n");

        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        summary.append("Date: ").append(dateTime).append("\n");
        summary.append("------------------------------------------------\n");
        summary.append(orderedItems);
        summary.append("================================================\n");
        summary.append("              Thank You for Your Order!         \n");
        summary.append("================================================\n");
        return summary.toString();
    }
}
