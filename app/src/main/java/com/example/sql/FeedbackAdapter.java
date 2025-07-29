package com.example.sql;

import android.view.LayoutInflater;
import android.view.View;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {

    private Context context;
    private List<Feedback> feedbackList;

    public FeedbackAdapter(Context context, List<Feedback> feedbackList) {
        this.context = context;
        this.feedbackList = feedbackList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subject, message, rating, createdAt;
        Button deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.subjectText);
            message = itemView.findViewById(R.id.messageText);
            rating = itemView.findViewById(R.id.ratingText);
            createdAt = itemView.findViewById(R.id.createdAtText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_feedback, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Feedback f = feedbackList.get(position);
        holder.subject.setText(f.subject);
        holder.message.setText(f.message);
        holder.rating.setText("Rating: " + f.rating);
        holder.createdAt.setText("Date: " + f.created_at);

        holder.deleteButton.setOnClickListener(v -> {
            // send delete request
            StringRequest delReq = new StringRequest(
                    Request.Method.POST,
                    ApiConfig.ADMIN_DELETE_FEEDBACK,
                    response -> {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("success")) {
                                feedbackList.remove(position);
                                notifyItemRemoved(position);
                                ToastUtil.show(context, "Deleted", 1/3);
                            } else {
                                ToastUtil.show(context,
                                        "Server error: " + obj.optString("error"),
                                        1/3);
                            }
                        } catch (JSONException e) {
                            ToastUtil.show(context,
                                    "Parse error", 1/3);
                        }
                    },
                    error -> ToastUtil.show(context,
                            "Network error: " + error.getMessage(),
                            1/3)
            ) {
                @Override
                protected Map<String,String> getParams() {
                    Map<String,String> p = new HashMap<>();
                    p.put("id", String.valueOf(f.id));
                    return p;
                }
            };
            Volley.newRequestQueue(context).add(delReq);
        });
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }
}
