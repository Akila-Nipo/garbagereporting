package com.example.garbagereporting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class OrderAdapter extends ArrayAdapter<Order> {

    public OrderAdapter(Context context, ArrayList<Order> orders) {
        super(context, 0, orders);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Order order = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        // Lookup view for data population
        TextView titleView = convertView.findViewById(android.R.id.text1);
        TextView detailView = convertView.findViewById(android.R.id.text2);

        // Populate the data into the template view
        titleView.setText("Order No: " + order.getOrderNo());
        detailView.setText("Items: " + order.getItemsWithPricing() + "\nTotal: $" + order.getTotalAmount());

        // Return the completed view to render on screen
        return convertView;
    }

}
