package com.example.va;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SubscriptionDialogFragment extends DialogFragment {

    private static final String ARG_VIDEO_ITEM = "video_item";

    public static SubscriptionDialogFragment newInstance(VideoItem videoItem) {
        SubscriptionDialogFragment fragment = new SubscriptionDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_VIDEO_ITEM, videoItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_subscription, container, false);

        Button basicPlanButton = view.findViewById(R.id.basic_plan_button);

        Button continueFreeButton = view.findViewById(R.id.continue_free_button);

        basicPlanButton.setOnClickListener(v -> navigateToPayment("Basic", 1800)); // 2900 cents for ₹29

        continueFreeButton.setOnClickListener(v -> dismiss());

        return view;
    }

    private void navigateToPayment(String plan, int amount) {

        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        intent.putExtra("plan", plan);
        intent.putExtra("amount", amount);
        startActivity(intent);
        dismiss();
    }
}
