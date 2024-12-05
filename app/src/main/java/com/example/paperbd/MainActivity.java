package com.example.paperbd;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private EditText nameText, descriptionText, priceText, imageUrlText;
    private Button addButton, updateButton, deleteButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private String selectedItemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);

        nameText = findViewById(R.id.nameText);
        descriptionText = findViewById(R.id.descriptionText);
        priceText = findViewById(R.id.priceText);
        imageUrlText = findViewById(R.id.imageUrlText);
        addButton = findViewById(R.id.addButton);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);
        listView = findViewById(R.id.listView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getItemNames());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedItemName = adapter.getItem(position);
            ClothingItem item = Paper.book().read(selectedItemName, null);
            if (item != null) {
                nameText.setText(item.getName());
                descriptionText.setText(item.getDescription());
                priceText.setText(String.valueOf(item.getPrice()));
                imageUrlText.setText(item.getImageUrl());
            }
        });

        addButton.setOnClickListener(v -> {
            String name = nameText.getText().toString();
            String description = descriptionText.getText().toString();
            double price = Double.parseDouble(priceText.getText().toString());
            String imageUrl = imageUrlText.getText().toString();
            if (!name.isEmpty() && !description.isEmpty() && !imageUrl.isEmpty()) {
                ClothingItem item = new ClothingItem(name, name, description, price, imageUrl);
                Paper.book().write(name, item);
                updateItemList();
                clearInputs();
            }
        });

        updateButton.setOnClickListener(v -> {
            if (selectedItemName == null) {
                Toast.makeText(MainActivity.this, "Пожалуйста, сначала выберите товар", Toast.LENGTH_SHORT).show();
                return;
            }
            String name = nameText.getText().toString();
            String description = descriptionText.getText().toString();
            double price = Double.parseDouble(priceText.getText().toString());
            String imageUrl = imageUrlText.getText().toString();
            if (!name.isEmpty() && !description.isEmpty() && !imageUrl.isEmpty()) {
                Paper.book().delete(selectedItemName);
                ClothingItem updatedItem = new ClothingItem(name, name, description, price, imageUrl);
                Paper.book().write(name, updatedItem);
                updateItemList();
                clearInputs();
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (selectedItemName == null) {
                Toast.makeText(MainActivity.this, "Пожалуйста, сначала выберите товар", Toast.LENGTH_SHORT).show();
                return;
            }
            Paper.book().delete(selectedItemName);
            updateItemList();
            clearInputs();
        });
    }

    private void updateItemList() {
        adapter.clear();
        adapter.addAll(getItemNames());
        adapter.notifyDataSetChanged();
    }

    private List<String> getItemNames() {
        return new ArrayList<>(Paper.book().getAllKeys());
    }

    private void clearInputs() {
        nameText.setText("");
        descriptionText.setText("");
        priceText.setText("");
        imageUrlText.setText("");
        selectedItemName = null;
    }
}