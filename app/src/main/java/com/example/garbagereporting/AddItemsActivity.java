package com.example.garbagereporting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddItemsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private EditText etName, etPrice, etQuantity;
    private Button btnUpload, btnSave, btnBack;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);

        imageView = findViewById(R.id.imageView);
        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
        btnUpload = findViewById(R.id.btnUpload);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        btnUpload.setOnClickListener(v -> openGallery());
        btnSave.setOnClickListener(v -> saveItem());
        btnBack.setOnClickListener(v -> finish()); // Go back to MainActivity
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri); // Display the selected image
        }
    }

    private void saveItem() {
        String name = etName.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String quantity = etQuantity.getText().toString().trim();

        if (name.isEmpty() || price.isEmpty() || quantity.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save item details and image
        saveImageToInternalStorage(name, price, quantity, imageUri);
    }

    private void saveImageToInternalStorage(String name, String price, String quantity, Uri imageUri) {
        // Save image and item details to internal storage
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            String fileName = name + "_" + price + "_" + quantity + ".jpg"; // Create file name with details
            File file = new File(getFilesDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(this, "Item saved successfully", Toast.LENGTH_SHORT).show();
            clearInputFields(); // Clear input fields after saving
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save item", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputFields() {
        etName.setText("");
        etPrice.setText("");
        etQuantity.setText("");
        imageView.setImageResource(0); // Clear image view
    }
}
