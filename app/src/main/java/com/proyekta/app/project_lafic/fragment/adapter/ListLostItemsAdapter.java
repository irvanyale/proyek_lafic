package com.proyekta.app.project_lafic.fragment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.model.BarangHilang;

import java.util.List;

/**
 * Created by WINDOWS 10 on 13/05/2017.
 */

public class ListLostItemsAdapter extends RecyclerView.Adapter<ListLostItemsAdapter.ViewHolder>{

    private Context context;
    private List<BarangHilang> listBarang;

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

        public ViewHolder(View itemView) {
            super(itemView);

            imgv_barang = (ImageView) itemView.findViewById(R.id.imgv_barang);
            imgv_pesan = (ImageView) itemView.findViewById(R.id.imgv_pesan);
            txtv_nama_barang = (TextView) itemView.findViewById(R.id.txtv_nama_barang);
            txtv_warna_barang = (TextView) itemView.findViewById(R.id.txtv_warna_barang);
            txtv_keterangan_barang = (TextView) itemView.findViewById(R.id.txtv_keterangan_barang);
            txtv_lokasi_hilang = (TextView) itemView.findViewById(R.id.txtv_lokasi_hilang);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_list_barang_hilang, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BarangHilang item = listBarang.get(position);
        holder.txtv_nama_barang.setText(item.getMERK_BARANG());
        holder.txtv_warna_barang.setText(item.getWARNA_BARANG());
        holder.txtv_keterangan_barang.setText(item.getKETERANGAN());
        holder.txtv_lokasi_hilang.setText(item.getLOKASI_HILANG());
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }
}
