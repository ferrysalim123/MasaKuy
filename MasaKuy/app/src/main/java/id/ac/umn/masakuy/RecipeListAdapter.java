package id.ac.umn.masakuy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ItemRecipeViewHolder> implements Filterable{
    private LinkedList<RecipeSource> mRecipeList;
    private LayoutInflater mInflater;
    private Context mContext;
    ArrayList<RecipeSource> listRecipe;
    String dbusername = "";

    public RecipeListAdapter(Context context, LinkedList<RecipeSource> recipeList){
        this.mContext = context;
        this.mRecipeList = recipeList;
        this.mInflater = LayoutInflater.from(context);
        this.listRecipe = new ArrayList<>(mRecipeList);
    }

    @NonNull
    @Override
    public ItemRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recipe_item_layout, parent, false);
        return new ItemRecipeViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRecipeViewHolder holder, int position) {
        RecipeSource mRecipeSource = mRecipeList.get(position);
        String user_id = String.valueOf(mRecipeSource.getUserId());
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

        String temp = "Posted By " + dbusername;
        holder.tvRecipeName.setText(mRecipeSource.getRecipeName());
        holder.tvDescription.setText(mRecipeSource.getDescription());
        holder.vvUser.setText(temp);

        if(!mRecipeSource.getRecipePict().isEmpty()) {
            Picasso.with(mContext.getApplicationContext()) //The context of your activity
                    .load(mRecipeSource.getRecipePict())
                    .into(holder.ivRecipePict);
        }
    }

    @Override
    public int getItemCount() {
        return mRecipeList.size();
    }

    @Override
    public Filter getFilter() {
        return recipeFilter;
    }

    private final Filter recipeFilter = new Filter(){
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<RecipeSource> filteredRecipeList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredRecipeList.addAll(listRecipe);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (RecipeSource recipe : listRecipe){
                    if(recipe.getRecipeName().toLowerCase().contains(filterPattern)){
                        filteredRecipeList.add(recipe);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredRecipeList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mRecipeList.clear();
            mRecipeList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ItemRecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvRecipeName, tvDescription, vvUser;
        private RecipeListAdapter mAdapter;
        private int mPosition;
        private RecipeSource mRecipeSource;
        private ImageView ivRecipePict;


        public ItemRecipeViewHolder(@NonNull View itemView, RecipeListAdapter adapter) {
            super(itemView);
            mAdapter = adapter;
            tvRecipeName = itemView.findViewById(R.id.tvRecipeName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivRecipePict = itemView.findViewById(R.id.ivRecipePict);
            vvUser = itemView.findViewById(R.id.vvUser);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mPosition = getLayoutPosition();
            mRecipeSource = mRecipeList.get(mPosition);
            Intent detailIntent = new Intent(mContext, RecipeDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("RecipeDetail", mRecipeSource);
            detailIntent.putExtras(bundle);
            mContext.startActivity(detailIntent);
        }
    }
}