package com.example.haircut_admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private DatabaseReference ordersRef, addressesRef;
    private String userId;
    private Address userAddress;
    private TextView totalPriceTextView;
    private int totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_orders);

        userId = getIntent().getStringExtra("userId");

        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(orderList);
        recyclerView.setAdapter(adapter);

        totalPriceTextView = findViewById(R.id.totalPrice);
        totalPrice = 0;

        ordersRef = FirebaseDatabase.getInstance().getReference("orders").child(userId);
        addressesRef = FirebaseDatabase.getInstance().getReference("addresses").child(userId);

        loadOrders();
        loadAddressDetails();

        Button buttonCustomerDetails = findViewById(R.id.buttonCustomerDetails);
        buttonCustomerDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userAddress != null) {
                    showAddressDialog(userAddress);
                }
            }
        });
    }

    private void loadOrders() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                totalPrice = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                        totalPrice += order.getProductPrice() * order.getQuantity();
                    }
                }
                totalPriceTextView.setText("Total Price: ₹" + totalPrice);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadAddressDetails() {
        addressesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userAddress = snapshot.getValue(Address.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showAddressDialog(Address address) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_address, null);
        builder.setView(dialogView);

        TextView textViewFullName = dialogView.findViewById(R.id.textViewFullName);
        TextView textViewMobileNumber = dialogView.findViewById(R.id.textViewMobileNumber);
        TextView textViewFlatHouse = dialogView.findViewById(R.id.textViewFlatHouse);
        TextView textViewAreaStreet = dialogView.findViewById(R.id.textViewAreaStreet);
        TextView textViewLandmark = dialogView.findViewById(R.id.textViewLandmark);
        TextView textViewTownCity = dialogView.findViewById(R.id.textViewTownCity);
        TextView textViewState = dialogView.findViewById(R.id.textViewState);
        TextView textViewPincode = dialogView.findViewById(R.id.textViewPincode);

        textViewFullName.setText(address.getFullName());
        textViewMobileNumber.setText(address.getMobileNumber());
        textViewFlatHouse.setText(address.getFlatHouse());
        textViewAreaStreet.setText(address.getAreaStreet());
        textViewLandmark.setText(address.getLandmark());
        textViewTownCity.setText(address.getTownCity());
        textViewState.setText(address.getState());
        textViewPincode.setText(address.getPincode());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

        private final List<Order> orderList;

        public OrderAdapter(List<Order> orderList) {
            this.orderList = orderList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Order order = orderList.get(position);
            holder.bind(order);
        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView textViewOrderTitle;
            private final TextView textViewOrderPrice;
            private final TextView qty;
            private final ImageView imageViewOrderThumbnail;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewOrderTitle = itemView.findViewById(R.id.textViewOrderTitle);
                textViewOrderPrice = itemView.findViewById(R.id.textViewOrderPrice);
                qty = itemView.findViewById(R.id.qty);
                imageViewOrderThumbnail = itemView.findViewById(R.id.imageViewOrderThumbnail);
            }

            public void bind(Order order) {
                textViewOrderTitle.setText(order.getProductTitle());
                textViewOrderPrice.setText("₹" + order.getProductPrice());
                qty.setText("Qty: " + order.getQuantity());
                Glide.with(imageViewOrderThumbnail.getContext()).load(order.getImageUrl()).into(imageViewOrderThumbnail);
            }
        }
    }
}
