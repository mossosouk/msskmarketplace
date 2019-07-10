package com.mossosouk.app;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.mossosouk.app.R.id.description;

/**
 * Created by Hassane on 23/05/2019.
 */

public class DashboardViewHolder extends RecyclerView.ViewHolder {

    public ImageView image;
    public TextView titre;
    public TextView prix;
    public TextView quantite;
    public TextView vendeur;
    public TextView contactVendeur;
    public TextView acheteur;
    public TextView contactAcheteur;
    public TextView moyenLivraison;


    public DashboardViewHolder(View itemView) {

        super(itemView);

        image = (ImageView) itemView.findViewById(R.id.imageview_id);
        titre = (TextView) itemView.findViewById(R.id.name);
        prix = (TextView) itemView.findViewById(R.id.prix);
        quantite = (TextView) itemView.findViewById(R.id.quantite);
        acheteur = (TextView) itemView.findViewById(R.id.buyerName);
        contactAcheteur = (TextView) itemView.findViewById(R.id.buyerContact);
        vendeur = (TextView) itemView.findViewById(R.id.sellerName);
        contactVendeur = (TextView) itemView.findViewById(R.id.sellerContact);
        moyenLivraison = (TextView) itemView.findViewById(R.id.moyenLivraison);
    }
}
