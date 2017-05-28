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
import com.proyekta.app.project_lafic.model.Barang;
import com.proyekta.app.project_lafic.model.BarangHilang;
import com.proyekta.app.project_lafic.model.Member;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by WINDOWS 10 on 13/05/2017.
 */

public class ListLostItemsAdapter extends RecyclerView.Adapter<ListLostItemsAdapter.ViewHolder>{

    private Context context;
    private List<BarangHilang> listBarang;
    private setOnSendMessageListener listener = null;

    public Context getContext() {
        return context;
    }

    public ListLostItemsAdapter(Context context, List<BarangHilang> listBarang) {
        this.context = context;
        this.listBarang = listBarang;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgv_barang;
        private ImageView imgv_pesan;
        private TextView txtv_nama_barang;
        private TextView txtv_warna_barang;
        private TextView txtv_keterangan_barang;
        private TextView txtv_lokasi_hilang;
        private TextView txtv_tgl;
        private TextView txtv_waktu;

        public ViewHolder(View itemView) {
            super(itemView);

            imgv_barang = (ImageView) itemView.findViewById(R.id.imgv_barang);
            imgv_pesan = (ImageView) itemView.findViewById(R.id.imgv_pesan);
            txtv_nama_barang = (TextView) itemView.findViewById(R.id.txtv_nama_barang);
            txtv_warna_barang = (TextView) itemView.findViewById(R.id.txtv_warna_barang);
            txtv_keterangan_barang = (TextView) itemView.findViewById(R.id.txtv_keterangan_barang);
            txtv_lokasi_hilang = (TextView) itemView.findViewById(R.id.txtv_lokasi_hilang);
            txtv_tgl = (TextView) itemView.findViewById(R.id.txtv_tgl);
            txtv_waktu = (TextView) itemView.findViewById(R.id.txtv_waktu);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_list_barang_hilang, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        BarangHilang item = listBarang.get(position);
        String merk = item.getMERK_BARANG().equals("") ? "" : " - " + item.getMERK_BARANG();
        holder.txtv_nama_barang.setText(item.getJENIS_BARANG() + merk);
        holder.txtv_warna_barang.setText(item.getWARNA_BARANG());
        holder.txtv_warna_barang.setVisibility(item.getWARNA_BARANG().equals("") ? View.GONE : View.VISIBLE);
        holder.txtv_keterangan_barang.setText(item.getKETERANGAN());
        holder.txtv_lokasi_hilang.setText(item.getLOKASI_HILANG());

        holder.imgv_pesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){

                    Member member = new Member();
                    member.setMEMBER_ID(listBarang.get(position).getMEMBER_ID());
                    member.setNAMA_MEMBER(listBarang.get(position).getNAMA_MEMBER());
                    member.setTELEPON(listBarang.get(position).getTELEPON());
                    member.setEMAIL_MEMBER(listBarang.get(position).getEMAIL_MEMBER());
                    member.setNOMOR_ID(listBarang.get(position).getNOMOR_ID());

                    listener.OnSendMessageListener(member);
                }
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(item.getTANGGAL_HILANG());
        } catch (Exception e) {
            Log.e("TAG", Log.getStackTraceString(e));
        }
        sdf = new SimpleDateFormat("EEE, d MMM yyyy");
        sdf_time = new SimpleDateFormat("HH:mm");

        String sdate = sdf.format(date);
        String stime = sdf_time.format(date);

        holder.txtv_tgl.setText(sdate);
        holder.txtv_waktu.setText(stime);

        Picasso.with(getContext())
                .load(ApiClient.BASE_URL_FOTO + item.getFOTO())
                .error(R.drawable.ic_image)
                .fit()
                .into(holder.imgv_barang);
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    public void setOnSendMessageListener(setOnSendMessageListener listener){
        this.listener = listener;
    }

    public interface setOnSendMessageListener {
        void OnSendMessageListener(Member member);
    }
}
