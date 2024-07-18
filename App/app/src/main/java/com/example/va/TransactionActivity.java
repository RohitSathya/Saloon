package com.example.va;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class TransactionActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTransactions;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        recyclerViewTransactions = findViewById(R.id.recyclerViewTransactions);
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));
        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(transactionList);
        recyclerViewTransactions.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("transactions");

        fetchTransactions();
    }

    private void fetchTransactions() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactionList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Transaction transaction = dataSnapshot.getValue(Transaction.class);
                    transactionList.add(transaction);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    private static class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

        private final List<Transaction> transactionList;

        public TransactionAdapter(List<Transaction> transactionList) {
            this.transactionList = transactionList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Transaction transaction = transactionList.get(position);
            holder.bind(transaction);
        }

        @Override
        public int getItemCount() {
            return transactionList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView textViewTransactionId;
            private final TextView textViewAmount;
            private final TextView textViewStatus;
            private final TextView textViewMessage;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewTransactionId = itemView.findViewById(R.id.textViewTransactionId);
                textViewAmount = itemView.findViewById(R.id.textViewAmount);
                textViewStatus = itemView.findViewById(R.id.textViewStatus);
                textViewMessage = itemView.findViewById(R.id.textViewMessage);
            }

            public void bind(Transaction transaction) {
                textViewTransactionId.setText(transaction.getTransactionId());
                textViewAmount.setText("₹" + transaction.getAmount());
                textViewStatus.setText(transaction.isSuccess() ? "Success" : "Failed");
                textViewMessage.setText(transaction.getMessage());
            }
        }
    }
}
