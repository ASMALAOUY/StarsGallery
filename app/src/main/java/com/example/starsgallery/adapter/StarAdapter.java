package com.example.starsgallery.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.starsgallery.R;
import com.example.starsgallery.beans.Star;
import com.example.starsgallery.service.StarService;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.List;

public class StarAdapter extends RecyclerView.Adapter<StarAdapter.StarViewHolder>
        implements Filterable {

    private List<Star> stars;          // liste complète
    private List<Star> starsFilter;    // liste filtrée affichée
    private Context context;
    private NewFilter mFilter;

    public StarAdapter(Context context, List<Star> stars) {
        this.context     = context;
        this.stars       = stars;
        this.starsFilter = new ArrayList<>(stars);
        this.mFilter     = new NewFilter(this);
    }

    /* ── ViewHolder ─────────────────────────────────────────── */

    public static class StarViewHolder extends RecyclerView.ViewHolder {
        TextView       idss, name;
        CircleImageView img;
        RatingBar      stars;

        public StarViewHolder(@NonNull View itemView) {
            super(itemView);
            idss  = itemView.findViewById(R.id.ids);
            img   = itemView.findViewById(R.id.img);
            name  = itemView.findViewById(R.id.name);
            stars = itemView.findViewById(R.id.stars);
        }
    }

    /* ── Inflate & click ─────────────────────────────────────── */

    @NonNull
    @Override
    public StarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.star_item, parent, false);
        final StarViewHolder holder = new StarViewHolder(v);

        holder.itemView.setOnClickListener(view -> {
            // Charger la vue du popup
            View popup = LayoutInflater.from(context)
                    .inflate(R.layout.star_edit_item, null, false);

            final ImageView  popupImg  = popup.findViewById(R.id.img);
            final RatingBar  popupBar  = popup.findViewById(R.id.ratingBar);
            final TextView   popupId   = popup.findViewById(R.id.idss);

            // Copier l'image déjà chargée
            Bitmap bmp = ((BitmapDrawable) ((ImageView) view
                    .findViewById(R.id.img)).getDrawable()).getBitmap();
            popupImg.setImageBitmap(bmp);
            popupBar.setRating(((RatingBar) view.findViewById(R.id.stars)).getRating());
            popupId.setText(((TextView) view.findViewById(R.id.ids)).getText().toString());

            new AlertDialog.Builder(context)
                    .setTitle("Notez :")
                    .setMessage("Donnez une note entre 1 et 5 :")
                    .setView(popup)
                    .setPositiveButton("Valider", (dialog, which) -> {
                        float rating = popupBar.getRating();
                        int   id     = Integer.parseInt(popupId.getText().toString());
                        Star  star   = StarService.getInstance().findById(id);
                        star.setStar(rating);
                        StarService.getInstance().update(star);
                        notifyItemChanged(holder.getAdapterPosition());
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });

        return holder;
    }

    /* ── Bind data ───────────────────────────────────────────── */

    @Override
    public void onBindViewHolder(@NonNull StarViewHolder holder, int position) {
        Star s = starsFilter.get(position);

        holder.name.setText(s.getName().toUpperCase());
        holder.stars.setRating(s.getStar());
        holder.idss.setText(String.valueOf(s.getId()));

        // Glide avec placeholder et gestion d'erreur
        Glide.with(context)
                .load(s.getImg())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .centerCrop()
                .into(holder.img);
    }

    @Override public int getItemCount() { return starsFilter.size(); }

    /* ── Filterable ──────────────────────────────────────────── */

    @Override public Filter getFilter() { return mFilter; }

    public class NewFilter extends Filter {
        private final RecyclerView.Adapter<?> mAdapter;

        NewFilter(RecyclerView.Adapter<?> adapter) { this.mAdapter = adapter; }

        @Override
        protected FilterResults performFiltering(CharSequence cs) {
            List<Star> filtered = new ArrayList<>();
            if (cs == null || cs.length() == 0) {
                filtered.addAll(stars);
            } else {
                String pattern = cs.toString().toLowerCase().trim();
                for (Star s : stars) {
                    if (s.getName().toLowerCase().startsWith(pattern))
                        filtered.add(s);
                }
            }
            FilterResults results = new FilterResults();
            results.values = filtered;
            results.count  = filtered.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence cs, FilterResults results) {
            starsFilter = (List<Star>) results.values;
            mAdapter.notifyDataSetChanged();
        }
    }
}