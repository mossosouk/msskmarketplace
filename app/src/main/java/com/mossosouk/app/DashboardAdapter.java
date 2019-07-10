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

/**
 * Created by Hassane on 23/05/2019.
 */

public class DashboardAdapter extends RecyclerView.Adapter<DashboardViewHolder> {

    private List<Commande> listCommandes;
    private Context context;

    public DashboardAdapter(Context context, List<Commande> listCommandes){
        this.context = context;
        this.listCommandes = listCommandes;
    }

    @Override
    public DashboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard, parent, false);
        return new DashboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DashboardViewHolder holder, final int position) {

        Commande commande = listCommandes.get(position);

        Picasso.with(context).load(commande.image).fit().centerCrop().into(holder.image);

        holder.titre.setText(commande.titre);

        if (commande.prix == -1) {
            holder.prix.setText("N/A");
        } else {
            String s = NumberFormat.getInstance(Locale.FRENCH).format(commande.prix);
            holder.prix.setText("Prix: " + s + " FCFA");
        }

        holder.quantite.setText("Quantité: " + String.valueOf(commande.quantite));
        holder.acheteur.setText(commande.acheteur);
        holder.contactAcheteur.setText(commande.contactAcheteur);
        holder.vendeur.setText(commande.vendeur);
        holder.contactVendeur.setText(commande.contactVendeur);
        holder.moyenLivraison.setText(commande.moyenLivraison);

    }

    @Override
    public int getItemCount() {
        return listCommandes.size();
    }
}
