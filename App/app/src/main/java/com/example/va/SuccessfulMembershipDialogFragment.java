package com.example.va;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SuccessfulMembershipDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_successful_membership, container, false);

        Button cancelButton = view.findViewById(R.id.cancel_membership_button);
        cancelButton.setOnClickListener(v -> cancelMembership());

        return view;
    }

    private void cancelMembership() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference subscriptionRef = FirebaseDatabase.getInstance().getReference("subscriptions");

        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            subscriptionRef.child(userId).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    dismiss();
                } else {
                    // Handle failure
                }
            });
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(requireActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                dismiss();
            }
        };
    }
}
