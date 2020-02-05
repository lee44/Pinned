package com.apps.jlee.boginder.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.jlee.boginder.Interfaces.ItemMoveCallback;
import com.apps.jlee.boginder.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;

import androidx.recyclerview.widget.RecyclerView;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.MyViewHolder> implements ItemMoveCallback.ItemTouchHelperContract
{
    private ArrayList<String> photos;
    private Context context;

    public PhotoAdapter(Context context, ArrayList<String> photos)
    {
        this.context = context;
        this.photos = photos;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photos, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        Glide.with(context).load(photos.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount()
    {
        return photos.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition)
    {
        if (fromPosition < toPosition)
        {
            for (int i = fromPosition; i < toPosition; i++)
            {
                Collections.swap(photos, i, i + 1);
            }
        }
        else
        {
            for (int i = fromPosition; i > toPosition; i--)
            {
                Collections.swap(photos, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(MyViewHolder myViewHolder)
    {
        myViewHolder.rowView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(MyViewHolder myViewHolder)
    {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageView;
        View rowView;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            rowView = itemView;
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
