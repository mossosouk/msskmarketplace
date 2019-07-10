package com.mossosouk.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder>{
    private List<Annonce> listAnnonces;
    private Context context;
    private String origine;

    public RecyclerAdapter(Context context, List<Annonce> listAnnonces, String origine) {
        this.listAnnonces = listAnnonces;
        this.context = context;
        this.origine = origine;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Annonce annonce = listAnnonces.get(position);
        holder.name.setText(annonce.titre);
        if (annonce.prix == -1) {
            holder.description.setText("N/A");
        } else {
            String s = NumberFormat.getInstance(Locale.FRENCH).format(annonce.prix);
            holder.description.setText(s + " FCFA");

        }

        Picasso.with(context).load(annonce.image).fit().centerCrop().into(holder.image);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailsActivity.class);
                i.putExtra("position", position);
                i.putExtra("origine", origine);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listAnnonces.size();
    }

}
