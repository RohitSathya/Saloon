package com.example.va;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

public class AddressDialogFragment extends DialogFragment {

    private static final String ARG_ADDRESS = "address";
    private static final String ARG_TOTAL_PRICE = "total_price";
    private static final String ARG_SELECTED_PRODUCTS = "selected_products";
    private static final String ARG_PRODUCT = "product";

    private Address address;
    private int totalPrice;
    private List<ProductItem> selectedProducts;
    private ProductItem product;

    public interface AddressDialogListener {
        void onProceedToPayment(int totalPrice, List<ProductItem> selectedProducts);
        void onProceedToPayment(ProductItem product);
        void onAddNewAddress(List<ProductItem> selectedProducts);
        void onAddNewAddress(ProductItem product);
    }

    public static AddressDialogFragment newInstance(Address address, int totalPrice, List<ProductItem> selectedProducts) {
        AddressDialogFragment fragment = new AddressDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ADDRESS, address);
        args.putInt(ARG_TOTAL_PRICE, totalPrice);
        args.putSerializable(ARG_SELECTED_PRODUCTS, (ArrayList<ProductItem>) selectedProducts);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddressDialogFragment newInstance(Address address, ProductItem product) {
        AddressDialogFragment fragment = new AddressDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ADDRESS, address);
        args.putSerializable(ARG_PRODUCT, product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            address = (Address) getArguments().getSerializable(ARG_ADDRESS);
            totalPrice = getArguments().getInt(ARG_TOTAL_PRICE, 0);
            selectedProducts = (List<ProductItem>) getArguments().getSerializable(ARG_SELECTED_PRODUCTS);
            product = (ProductItem) getArguments().getSerializable(ARG_PRODUCT);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Confirm Address")
                .setMessage("Proceed with this address?\n" +
                        "Address: " + address.getFullName() + "\n" +
                        "Door No: " + address.getPincode() + "\n" +
                        "Mobile No: " + address.getMobileNumber())
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddressDialogListener listener = (AddressDialogListener) getActivity();
                        if (listener != null) {
                            if (selectedProducts != null) {
                                listener.onProceedToPayment(totalPrice, selectedProducts);
                            } else if (product != null) {
                                listener.onProceedToPayment(product);
                            }
                        }
                    }
                })
                .setNegativeButton("Change Address", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddressDialogListener listener = (AddressDialogListener) getActivity();
                        if (listener != null) {
                            if (selectedProducts != null) {
                                listener.onAddNewAddress(selectedProducts);
                            } else if (product != null) {
                                listener.onAddNewAddress(product);
                            }
                        }
                    }
                });
        return builder.create();
    }
}
