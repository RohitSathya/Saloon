package com.example.va;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<OrderItem> orderList;

    public OrderAdapter(Context context, List<OrderItem> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderItem order = orderList.get(position);
        holder.orderName.setText(order.getProductTitle());
        holder.orderPrice.setText("₹" + order.getProductPrice());
        holder.orderQuantity.setText("Quantity: " + order.getQuantity());

        Glide.with(context).load(order.getImageUrl()).into(holder.orderImage);

        holder.cancelOrderButton.setOnClickListener(v -> {
            cancelOrder(order, position);
        });
    }

    private void cancelOrder(OrderItem order, int position) {
        Log.d("orderid",order.getOrderId());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders").child(userId).child(order.getOrderId());
            orderRef.removeValue();
//            orderRef.removeValue().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    // Remove the item from the order list
//                    orderList.remove(position);
//                    Log.d("OrderAction", "Item removed at position: " + position);
//
//                    // Notify the adapter that the item has been removed
//                    notifyItemRemoved(position);
//                    Log.d("OrderAction", "Adapter notified that item was removed at position: " + position);
//
//                    // Notify the adapter that the range of items has changed
//                    notifyItemRangeChanged(position, orderList.size());
//                    Log.d("OrderAction", "Adapter notified that item range changed from position: " + position + " to size: " + orderList.size());
//                }
//                else {
//                    // Handle the error
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderName, orderPrice, orderQuantity;
        ImageView orderImage;
        Button cancelOrderButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderName = itemView.findViewById(R.id.order_name);
            orderPrice = itemView.findViewById(R.id.order_price);
            orderQuantity = itemView.findViewById(R.id.order_quantity);
            orderImage = itemView.findViewById(R.id.order_image);
            cancelOrderButton = itemView.findViewById(R.id.cancel_order_button);
        }
    }
}
