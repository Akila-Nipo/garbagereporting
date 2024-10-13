package com.example.garbagereporting;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class OrderMealsActivity extends AppCompatActivity {


    private LinearLayout itemsContainer;
    private HashMap<String, List<File>> categoryItemsMap;
    private Button btnConfirm; // Button to confirm order
    private TextView totalPriceView; // TextView to display total price
    private double totalPrice; // Variable to store total price
    private int totalQuantities; // Variable to store total quantities
    private double previousTotalPrice = 0.0;
    private int previousTotalQuantities = 0;
    // Initialize total price for the order
    double orderTotalPrice = 0.0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_meals);


        itemsContainer = findViewById(R.id.itemsContainer);
        btnConfirm = findViewById(R.id.btnConfirm);
        totalPriceView = findViewById(R.id.totalPriceView); // Initialize total price TextView


        // Load items from internal storage
        loadItems();


        // Confirm button click listener
        btnConfirm.setOnClickListener(v -> confirmOrder());
    }


    // Load items from internal storage
    private void loadItems() {
        File directory = getFilesDir();
        File[] files = directory.listFiles();


        if (files != null && files.length > 0) {
            categoryItemsMap = new HashMap<>();


            // Organize files by category
            for (File file : files) {
                if (file.getName().endsWith(".jpg")) {
                    String[] details = file.getName().replace(".jpg", "").split("_");
                    String itemCategory = details.length > 3 ? details[3] : "Uncategorized";


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


    // Add a section for a specific category with NumberPickers
    private void addCategorySection(String category, List<File> items) {
        TextView categoryHeading = new TextView(this);
        categoryHeading.setText(category);
        categoryHeading.setTextSize(20);
        categoryHeading.setPadding(0, 20, 0, 10);
        itemsContainer.addView(categoryHeading);


        for (File file : items) {
            String[] details = file.getName().replace(".jpg", "").split("_");
            String itemName = details.length > 0 ? details[0] : "Unknown";
            String itemPrice = details.length > 1 ? details[1] : "0"; // Get item price
            String itemQuantity = details.length > 2 ? details[2] : "0";


            LinearLayout itemLayout = createItemLayout(file, itemName, itemPrice, itemQuantity);
            itemsContainer.addView(itemLayout);
        }
    }


    private LinearLayout createItemLayout(File file, String name, String price, String quantity) {
        // Create a horizontal LinearLayout to hold the image and NumberPicker side by side
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL); // Horizontal orientation for side-by-side display
        itemLayout.setPadding(20, 20, 20, 20);


        // Create ImageView to display the item image
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(300, 300)); // Set fixed size for the image
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);


        // Load image from the file and set it to the ImageView
        imageView.setImageURI(Uri.fromFile(file));


        // Create a vertical LinearLayout to hold the item name and NumberPicker
        LinearLayout rightLayout = new LinearLayout(this);
        rightLayout.setOrientation(LinearLayout.VERTICAL); // Vertical layout for name and NumberPicker


        // Create TextView for the item name
        TextView nameView = new TextView(this);
        nameView.setText("Name: " + name);
        nameView.setPadding(0, 0, 0, 10); // Padding below the name


        // Create TextView for the item price
        TextView priceView = new TextView(this);
        priceView.setText("Price: $" + price);
        priceView.setPadding(0, 0, 0, 10); // Padding below the price


        // Create NumberPicker for quantity selection
        NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(0);
        int availableQuantity = (int) Double.parseDouble(quantity); // Convert to double first, then cast to int
        numberPicker.setMaxValue(availableQuantity); // Set max to available quantity
        numberPicker.setValue(0); // Default value




        // Set a listener to update the total price and quantities when the NumberPicker value changes
        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> updateTotalPrice());


        // Add the name, price TextView, and NumberPicker to the rightLayout
        rightLayout.addView(nameView); // Name at the top
        rightLayout.addView(priceView); // Price below the name
        rightLayout.addView(numberPicker); // NumberPicker below the price


        // Add the ImageView and the rightLayout to the horizontal itemLayout
        itemLayout.addView(imageView); // Image on the left
        itemLayout.addView(rightLayout); // Name, price, and NumberPicker on the right


        // Store the NumberPicker and price for later use in confirmation
        itemLayout.setTag(new Object[]{numberPicker, price}); // Store both the NumberPicker and price


        return itemLayout;
    }


    private void confirmOrder() {
        int viewIndex = 0;  // Track actual items, skipping category headers
        previousTotalPrice = 0.00;  // Store the previous total price
        previousTotalQuantities = 0;  // Store the previous total quantities

        StringBuilder orderDetails = new StringBuilder("Ordered Items:\n");
        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        orderDetails.append("Date and Time: ").append(dateTime).append("\n");


        // Iterate through each category in the map
        for (String category : categoryItemsMap.keySet()) {
            List<File> itemsInCategory = categoryItemsMap.get(category);
            if (itemsInCategory == null) {
                continue; // Skip if no items in the category
            }


            // Iterate through the items in the category
            for (int j = 0; j < itemsInCategory.size(); j++) {
                // Ensure we're only accessing valid views for items
                while (viewIndex < itemsContainer.getChildCount() &&
                        !(itemsContainer.getChildAt(viewIndex) instanceof LinearLayout)) {
                    viewIndex++;  // Skip non-item views like headers
                }


                if (viewIndex >= itemsContainer.getChildCount()) {
                    break;  // Prevent out-of-bounds access
                }


                // Get the corresponding view for the current item
                LinearLayout itemLayout = (LinearLayout) itemsContainer.getChildAt(viewIndex);
                Object[] tags = (Object[]) itemLayout.getTag();
                NumberPicker numberPicker = (NumberPicker) tags[0];
                String itemPrice = (String) tags[1];
                String itemName = itemsInCategory.get(j).getName().replace(".jpg", "").split("_")[0]; // Extract item name


                if (numberPicker != null) {
                    int quantityToOrder = numberPicker.getValue();
                    File file = itemsInCategory.get(j);
                    String[] details = file.getName().replace(".jpg", "").split("_");
                    int currentQuantity = (int) Double.parseDouble(details[2]); // Assuming quantity is the 3rd element


                    if (quantityToOrder > 0 && currentQuantity >= quantityToOrder) {
                        // Calculate new quantity
                        int newQuantity = currentQuantity - quantityToOrder;


                        // Create a new filename only if the quantity is greater than zero
                        String newFileName = String.format("%s_%s_%d_%s.jpg", details[0], details[1], newQuantity, details[3]);
                        File newFile = new File(file.getParent(), newFileName);


                        // If the new file already exists, delete it first before renaming
                        if (newFile.exists()) {
                            if (!newFile.delete()) {
                                Log.e("OrderMealsActivity", "Failed to delete existing file: " + newFileName);
                            }
                        }


                        // Rename the old file to the new filename or create a new file
                        if (file.renameTo(newFile)) {
                            Log.i("OrderMealsActivity", "Updated quantity for " + file.getName() + " to " + newQuantity);
                        } else {
                            Log.e("OrderMealsActivity", "Failed to rename file " + file.getName());
                        }

                        double itemTotal = quantityToOrder * Double.parseDouble(itemPrice);
                        // Append to order details
                        orderDetails.append(itemName)
                                .append(" - Quantity: ")
                                .append(quantityToOrder)
                                .append(" - Total: $")
                                .append(String.format("%.2f", quantityToOrder * Double.parseDouble(itemPrice)))
                                .append("\n");

                        // Update the order total price
                        orderTotalPrice += itemTotal; // Add to order total
                        // Optionally show a Toast message for confirmation
                        Toast.makeText(this, "Ordered " + quantityToOrder + " of " + itemName, Toast.LENGTH_SHORT).show();
                    }
                }
                viewIndex++;
            }
        }

        // Append total price to the order details
        orderDetails.append("Total Order Price: $").append(String.format("%.2f", orderTotalPrice)).append("\n");

        // Create the order summary string
        String orderSummary = OrderSummaryActivity.generateOrderSummary(orderDetails.toString());

        // Start OrderSummaryActivity with the order summary
        Intent intent = new Intent(this, OrderSummaryActivity.class);
        intent.putExtra("ORDER_SUMMARY", orderSummary);
        startActivity(intent);
    }

    // Restore the previous total price and quantities in onResume
    @Override
    protected void onResume() {
        super.onResume();
        loadItems(); // Reload items to reflect any changes made

        // Restore the previous total price and quantities
        totalPrice = previousTotalPrice;
        totalQuantities = previousTotalQuantities;

        // Update UI accordingly
        totalPriceView.setText("Total Price: $" + String.format("%.2f", totalPrice));
        btnConfirm.setText(String.format("Confirm Order (%d items) - Total: $%.2f", totalQuantities, totalPrice));
    }



    // Update total price and quantity whenever quantities are changed
    private void updateTotalPrice() {
        totalPrice = 0.0;
        totalQuantities = 0; // Reset total quantities

        for (int i = 0; i < itemsContainer.getChildCount(); i++) {
            View view = itemsContainer.getChildAt(i);
            if (view instanceof LinearLayout) {
                Object[] tags = (Object[]) view.getTag();
                NumberPicker numberPicker = (NumberPicker) tags[0];
                String itemPrice = (String) tags[1];

                if (numberPicker != null) {
                    int quantityToOrder = numberPicker.getValue();
                    totalPrice += quantityToOrder * Double.parseDouble(itemPrice);
                    totalQuantities += quantityToOrder; // Update total quantities
                }
            }
        }

        // Update the confirm button text with quantities and total price
        btnConfirm.setText(String.format("Confirm Order (%d items) - Total: $%.2f", totalQuantities, totalPrice));
        totalPriceView.setText("Total Price: $" + String.format("%.2f", totalPrice));
    }
}

