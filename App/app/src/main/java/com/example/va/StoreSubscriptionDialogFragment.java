//package com.example.va;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.DialogFragment;
//
//public class StoreSubscriptionDialogFragment extends DialogFragment {
//
//    private static final String ARG_VIDEO_ITEM = "video_item";
//    private static final int PAYMENT_REQUEST_CODE = 1001;
//
//    private RentedVideoItem videoItem;
//
//    public static StoreSubscriptionDialogFragment newInstance(RentedVideoItem videoItem) {
//        StoreSubscriptionDialogFragment fragment = new StoreSubscriptionDialogFragment();
//        Bundle args = new Bundle();
//        args.putSerializable(ARG_VIDEO_ITEM, videoItem);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.dialog_store_subscription, container, false);
//
//        if (getArguments() != null) {
//            videoItem = (RentedVideoItem) getArguments().getSerializable(ARG_VIDEO_ITEM);
//        }
//
//        Button purchaseButton = view.findViewById(R.id.purchase_button);
//        Button cancelButton = view.findViewById(R.id.cancel_button);
//
//        purchaseButton.setOnClickListener(v -> navigateToPayment(videoItem));
//        cancelButton.setOnClickListener(v -> dismiss());
//
//        return view;
//    }
//
//    private void navigateToPayment(RentedVideoItem videoItem) {
//        if (videoItem == null || videoItem.getCategoryName() == null) {
//            // Handle the null case appropriately, possibly show a message to the user
//            dismiss();
//            return;
//        }
//
////        int amount = 0;
////        switch (videoItem.getCategoryName()) {
////            case "1":
////                amount = 100; // ₹100 for category 1
////                break;
////            case "2":
////                amount = 200; // ₹200 for category 2
////                break;
////            case "3":
////                amount = 300; // ₹300 for category 3
////                break;
////            case "4":
////                amount = 400; // ₹400 for category 4
////                break;
////            default:
////                // Handle default case if necessary
////                break;
////        }
////
////        if (amount == 0) {
////            // Handle the case where the amount is not set
////            dismiss();
////            return;
////        }
//
//        int amountInPaise = 100 * 100;
//        Intent intent = new Intent(getActivity(), StorePaymentActivity.class);
//        intent.putExtra("video_item", videoItem);
//        intent.putExtra("amount", amountInPaise);
//        startActivityForResult(intent, PAYMENT_REQUEST_CODE);
//        dismiss();
//    }
//}
