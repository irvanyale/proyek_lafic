package com.proyekta.app.project_lafic.fragment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.model.Pesan;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by WINDOWS 10 on 21/05/2017.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder>{

    private Context context;
    private List<Pesan> listPesan;

    public MessagesAdapter(Context context, List<Pesan> listPesan) {
        this.context = context;
        this.listPesan = listPesan;
    }

    public Context getContext() {
        return context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgv_user;
        private TextView txtv_pengirim;
        private TextView txtv_pesan;
        private TextView txtv_waktu;

        public ViewHolder(View itemView) {
            super(itemView);

            imgv_user = (ImageView)itemView.findViewById(R.id.imgv_user);
            txtv_pengirim = (TextView) itemView.findViewById(R.id.txtv_pengirim);
            txtv_pesan = (TextView) itemView.findViewById(R.id.txtv_pesan);
            txtv_waktu = (TextView) itemView.findViewById(R.id.txtv_waktu);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_list_messages, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Pesan pesan = listPesan.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(pesan.getTANGGAL_PESAN());
        } catch (Exception e) {
            Log.e("TAG", Log.getStackTraceString(e));
        }
        sdf = new SimpleDateFormat("HH:mm");
        String sdate = sdf.format(date);

        holder.txtv_pengirim.setText(pesan.getPENGIRIM());
        holder.txtv_pesan.setText(pesan.getISI_PESAN());
        holder.txtv_waktu.setText(sdate);
    }

    @Override
    public int getItemCount() {
        return listPesan.size();
    }

    public void setList(List<Pesan> listPesan){
        this.listPesan = listPesan;
        notifyDataSetChanged();
    }
}
