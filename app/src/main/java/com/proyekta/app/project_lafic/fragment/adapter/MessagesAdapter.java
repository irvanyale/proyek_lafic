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
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.model.Member;
import com.proyekta.app.project_lafic.model.Pesan;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by WINDOWS 10 on 21/05/2017.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder>{

    private Context context;
    private List<Pesan> listPesan;
    private setOnSendMessageListener listener = null;

    public MessagesAdapter(Context context, List<Pesan> listPesan) {
        this.context = context;
        this.listPesan = listPesan;
    }

    public Context getContext() {
        return context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgv_user;
        private ImageView imgv_pesan;
        private TextView txtv_pengirim;
        private TextView txtv_pesan;
        private TextView txtv_tgl;
        private TextView txtv_waktu;

        public ViewHolder(View itemView) {
            super(itemView);

            imgv_user = (ImageView)itemView.findViewById(R.id.imgv_user);
            imgv_pesan = (ImageView)itemView.findViewById(R.id.imgv_pesan);
            txtv_pengirim = (TextView) itemView.findViewById(R.id.txtv_pengirim);
            txtv_pesan = (TextView) itemView.findViewById(R.id.txtv_pesan);
            txtv_tgl = (TextView) itemView.findViewById(R.id.txtv_tgl);
            txtv_waktu = (TextView) itemView.findViewById(R.id.txtv_waktu);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_list_messages, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Pesan pesan = listPesan.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(pesan.getTANGGAL_PESAN());
        } catch (Exception e) {
            Log.e("TAG", Log.getStackTraceString(e));
        }
        sdf = new SimpleDateFormat("EEE, d MMM yyyy");
        sdf_time = new SimpleDateFormat("HH:mm");

        String sdate = sdf.format(date);
        String stime = sdf_time.format(date);

        holder.txtv_pengirim.setText(pesan.getPENGIRIM());
        holder.txtv_pesan.setText(pesan.getISI_PESAN());
        holder.txtv_tgl.setText(sdate);
        holder.txtv_waktu.setText(stime);

        holder.imgv_pesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){

                    Member member = new Member();
                    member.setMEMBER_ID(listPesan.get(position).getMEMBER_ID());
                    member.setNAMA_MEMBER(listPesan.get(position).getNAMA_MEMBER());
                    member.setTELEPON(listPesan.get(position).getTELEPON());
                    member.setEMAIL_MEMBER(listPesan.get(position).getEMAIL_MEMBER());
                    member.setNOMOR_ID(listPesan.get(position).getNOMOR_ID());

                    listener.OnSendMessageListener(member);
                }
            }
        });

        Picasso.with(getContext())
                .load(ApiClient.BASE_URL_FOTO + pesan.getFOTO_MEMBER())
                .error(R.drawable.ic_image)
                .fit()
                .into(holder.imgv_user);
    }

    @Override
    public int getItemCount() {
        return listPesan.size();
    }

    public void setList(List<Pesan> listPesan){
        this.listPesan = listPesan;
        notifyDataSetChanged();
    }

    public void setOnSendMessageListener(setOnSendMessageListener listener){
        this.listener = listener;
    }

    public interface setOnSendMessageListener {
        void OnSendMessageListener(Member member);
    }
}
