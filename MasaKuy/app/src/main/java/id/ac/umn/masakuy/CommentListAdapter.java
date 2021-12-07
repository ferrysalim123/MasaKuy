package id.ac.umn.masakuy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ItemCommentViewHolder> {
    private LinkedList<CommentSource> mCommentList;
    private LayoutInflater mInflater;
    private Context mContext;
    String dbusername = "";

    public CommentListAdapter(Context context, LinkedList<CommentSource> commentList){


        this.mContext = context;
        this.mCommentList = commentList;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ItemCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.comment_item_layout, parent, false);
        return new ItemCommentViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemCommentViewHolder holder, int position) {
        CommentSource mCommentSource = mCommentList.get(position);

        String user_id = String.valueOf(mCommentSource.getUserId());
        String result = "";

        String[] field = new String[1];
        field[0] = "user_id";
        //Creating array for data
        String[] data = new String[1];
        data[0] = user_id;



        PutData putData = new PutData("http://192.168.0.124/MasaKuy/getUserName.php", "POST", field, data);

        if (putData.startPut()) {
            if (putData.onComplete()) {
                result = putData.getResult();
                JSONObject responseJSON = null;

                try {
                    responseJSON = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray arr = null;
                try {
                    arr = responseJSON.getJSONArray("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < arr.length(); i++) {

                    try {
                        dbusername = arr.getJSONObject(i).getString("name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        holder.tvUserId.setText(dbusername);
        holder.tvComments.setText(mCommentSource.getComments());
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public class ItemCommentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserId;
        private TextView tvComments;
        private CommentListAdapter mAdapter;
        private int mPosition;
        private CommentSource mCommentSource;

        public ItemCommentViewHolder(@NonNull View itemView, CommentListAdapter adapter) {
            super(itemView);
            mAdapter = adapter;
            tvUserId = itemView.findViewById(R.id.tvUserId);
            tvComments = itemView.findViewById(R.id.tvComments);
        }
    }
}
