package com.lado.fanmobiletest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lado.fanmobiletest.R;
import com.lado.fanmobiletest.databinding.UserItemLayoutBinding;
import com.lado.fanmobiletest.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    public void setUsers(List<User> users) {
        this.users = users;
    }

    private List<User> users;

    private Context context;

    public UserAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }


    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.user_item_layout, parent, false );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        if (user != null) {
            holder.binding.name.setText(user.getName());
            holder.binding.email.setText(user.getEmail());
            holder.binding.isVerified.setText(user.isEmailVerified() ? "Verified" : "Not Verified");
            holder.binding.isVerified.setBackground(user.isEmailVerified() ? ContextCompat.getDrawable(context, R.drawable.verified_bg) : ContextCompat.getDrawable(context, R.drawable.not_verified_bg));
            Glide.with(context)
                    .load(R.drawable.ic_baseline_account_circle_24)
                    .fitCenter()
                    .placeholder(R.drawable.ic_baseline_account_circle_24)
                    .into(holder.binding.userImage);

        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        UserItemLayoutBinding binding;

        public ViewHolder(@NonNull UserItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
