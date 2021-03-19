package com.ag.twisterpm.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ag.twisterpm.R;
import com.ag.twisterpm.models.Twist;

import java.util.List;

public class TwistRecyclerViewAdapter extends RecyclerView.Adapter<TwistRecyclerViewAdapter.ViewHolder> {

    @SuppressWarnings("FieldMayBeFinal")
    private List<Twist> data;
    private final LayoutInflater inflater;
    private ItemClickListener clickListener;

    public TwistRecyclerViewAdapter(Context context, List<Twist> data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.twist_recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Twist twist = data.get(position);
        holder.postUsername.setText(twist.getUser());
        holder.postContent.setText(twist.getContent());
        holder.postCommentCount.setText(twist.getTotalComments() == 1 ?twist.getTotalComments() + " Comment" : twist.getTotalComments() + " Comments");
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public Twist getItem(int id) {
        return data.get(id);
    }

    public void SetClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView postContent;
        final TextView postUsername;
        final TextView postCommentCount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postContent = itemView.findViewById(R.id.postContent);
            postUsername = itemView.findViewById(R.id.postUsername);
            postCommentCount = itemView.findViewById(R.id.postCommentCount);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i(Constants.LOGTAG, "Adapter OnClick");
            if (clickListener != null) clickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
