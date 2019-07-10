package com.mossosouk.app;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewHolder extends RecyclerView.ViewHolder{

    public ImageView image;
    public TextView name;
    public TextView description;
    public LinearLayout linearLayout;


    public ViewHolder(View itemView) {
        super(itemView);

        image = (ImageView) itemView.findViewById(R.id.imageview_id);
        name = (TextView) itemView.findViewById(R.id.name);
        description = (TextView) itemView.findViewById(R.id.description);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.recycler_item);
    }
}
