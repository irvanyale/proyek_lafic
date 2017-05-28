package com.proyekta.app.project_lafic.fragment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.model.BarangPenemuan;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by WINDOWS 10 on 21/05/2017.
 */

public class UserFoundItemsAdapter extends RecyclerView.Adapter<UserFoundItemsAdapter.ViewHolder>{

    private Context context;
    private List<BarangPenemuan> listBarangPenemuan;

    public UserFoundItemsAdapter(Context context, List<BarangPenemuan> listBarangPenemuan) {
        this.context = context;
        this.listBarangPenemuan = listBarangPenemuan;
    }

    public Context getContext() {
        return context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgv_barang;
        private ImageView imgv_pesan;
        private TextView txtv_nama_barang;
        private TextView txtv_warna_barang;
        private TextView txtv_keterangan_barang;
        private TextView txtv_lokasi_ketemu;
        private LinearLayout lnly_edit;
        private LinearLayout lnly_delete;
        private TextView txtv_waktu;
        private TextView txtv_tgl;

        public ViewHolder(View itemView) {
            super(itemView);

            imgv_barang = (ImageView) itemView.findViewById(R.id.imgv_barang);
            imgv_pesan = (ImageView) itemView.findViewById(R.id.imgv_pesan);
            txtv_nama_barang = (TextView) itemView.findViewById(R.id.txtv_nama_barang);
            txtv_warna_barang = (TextView) itemView.findViewById(R.id.txtv_warna_barang);
            txtv_keterangan_barang = (TextView) itemView.findViewById(R.id.txtv_keterangan_barang);
            txtv_lokasi_ketemu = (TextView) itemView.findViewById(R.id.txtv_lokasi_ketemu);
            lnly_edit = (LinearLayout) itemView.findViewById(R.id.lnly_edit);
            lnly_delete = (LinearLayout) itemView.findViewById(R.id.lnly_delete);
            txtv_waktu = (TextView) itemView.findViewById(R.id.txtv_waktu);
            txtv_tgl = (TextView) itemView.findViewById(R.id.txtv_tgl);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.user_list_barang_penemuan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BarangPenemuan item = listBarangPenemuan.get(position);
        String merk = item.getMERK_BARANG().equals("") ? "" : " - " + item.getMERK_BARANG();
        holder.txtv_nama_barang.setText(item.getJENIS_BARANG() + merk);
        holder.txtv_warna_barang.setText(item.getWARNA_BARANG());
        holder.txtv_warna_barang.setVisibility(item.getWARNA_BARANG().equals("") ? View.GONE : View.VISIBLE);
        holder.txtv_keterangan_barang.setText(item.getKETERANGAN());
        holder.txtv_lokasi_ketemu.setText(item.getLOKASI_KETEMU());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(item.getTANGGAL_KETEMU());
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
                .load(ApiClient.BASE_URL_FOTO + item.getFOTO_PENEMUAN())
                .error(R.drawable.ic_image)
                .fit()
                .into(holder.imgv_barang);
    }

    @Override
    public int getItemCount() {
        return listBarangPenemuan.size();
    }

    public void setList(List<BarangPenemuan> listBarangPenemuan){
        this.listBarangPenemuan = listBarangPenemuan;
        notifyDataSetChanged();
    }
}
