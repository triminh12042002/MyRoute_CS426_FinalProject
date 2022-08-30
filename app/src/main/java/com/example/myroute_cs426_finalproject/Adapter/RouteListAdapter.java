package com.example.myroute_cs426_finalproject.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myroute_cs426_finalproject.Model.RouteModel;
import com.example.myroute_cs426_finalproject.R;

import java.util.List;

public class RouteListAdapter extends RecyclerView.Adapter<RouteListAdapter.MyViewHolder> {
    private List<RouteModel> routeModelList;
    private RouteModelListClickListener clickListener;

    public RouteListAdapter(List<RouteModel> routeModelList, RouteModelListClickListener clickListener){
        this.routeModelList = routeModelList;
        this.clickListener = clickListener;
    }

    public void updateData(List<RouteModel> routeModelList){
        this.routeModelList = routeModelList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RouteListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.routeName.setText(routeModelList.get(position).getName());
        holder.routeInfo.setText(routeModelList.get(position).getInfo());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(routeModelList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return routeModelList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView routeName;
        TextView routeInfo;
        public MyViewHolder(View view){
            super(view);
            routeName = view.findViewById(R.id.routeName);
            routeInfo= view.findViewById(R.id.routeInfo);
        }
    }

    public interface RouteModelListClickListener{
        public void onItemClick(RouteModel routeModel);
    }
}
