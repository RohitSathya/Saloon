package com.example.haircut_admin;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import java.util.List;

public class UserListAdapter extends ArrayAdapter<User> {

    private Context context;
    private List<User> users;

    public UserListAdapter(Context context, List<User> users) {
        super(context, R.layout.user_item, users);
        this.context = context;
        this.users = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        }

        User user = users.get(position);

        TextView textViewEmail = convertView.findViewById(R.id.text_view_email);
        TextView textViewName = convertView.findViewById(R.id.text_view_name);

        textViewEmail.setText(user.getEmail());
        textViewName.setText(user.getName());

        return convertView;
    }
}
