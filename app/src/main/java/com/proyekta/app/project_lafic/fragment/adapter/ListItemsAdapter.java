package com.proyekta.app.project_lafic.fragment.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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
import com.proyekta.app.project_lafic.model.Barang;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Ervina Aprilia S on 13/05/2017.
 */

public class ListItemsAdapter extends RecyclerView.Adapter<ListItemsAdapter.ViewHolder>{

    private Context context;
    private List<Barang> listBarang;
    private setOnShowQRCodeListener listener = null;
    private setOnShowEditBarangListener listenerEdit = null;
    private setOnEditBarangListener listenerEditBarang = null;

    public ListItemsAdapter(Context context, List<Barang> listBarang) {
        this.context = context;
        this.listBarang = listBarang;
    }

    private Context getContext() {
        return context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgv_barang;
        ImageView imgv_status;
        TextView txtv_nama_barang;
        TextView txtv_warna_barang;
        TextView txtv_keterangan_barang;
        LinearLayout lnly_qrcode;
        LinearLayout lnly_edit;
        LinearLayout lnly_edit_barang;
        LinearLayout lnly_image;

        public ViewHolder(View itemView) {
            super(itemView);
            imgv_barang = (ImageView)itemView.findViewById(R.id.imgv_barang);
            imgv_status = (ImageView)itemView.findViewById(R.id.imgv_status);
            txtv_nama_barang = (TextView) itemView.findViewById(R.id.txtv_nama_barang);
            txtv_warna_barang = (TextView) itemView.findViewById(R.id.txtv_warna_barang);
            txtv_keterangan_barang = (TextView) itemView.findViewById(R.id.txtv_keterangan_barang);
            lnly_qrcode = (LinearLayout) itemView.findViewById(R.id.lnly_qrcode);
            lnly_edit = (LinearLayout) itemView.findViewById(R.id.lnly_edit);
            lnly_edit_barang = (LinearLayout) itemView.findViewById(R.id.lnly_edit_barang);
            lnly_image = (LinearLayout) itemView.findViewById(R.id.lnly_image);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_list_barang, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Barang barang = listBarang.get(position);
        String merk = barang.getMERK_BARANG().equals("") ? "" : " - " + barang.getMERK_BARANG();
        holder.txtv_nama_barang.setText(barang.getJENIS_BARANG() + merk);
        holder.txtv_warna_barang.setText(barang.getWARNA_BARANG());
        holder.txtv_warna_barang.setVisibility(barang.getWARNA_BARANG().equals("") ? View.GONE : View.VISIBLE);
        holder.txtv_keterangan_barang.setText(barang.getKETERANGAN());
        holder.imgv_status.setImageDrawable(
                barang.getSTATUS().equals("AMAN") ?
                        ContextCompat.getDrawable(getContext(), R.drawable.ic_status_aman) :
                        ContextCompat.getDrawable(getContext(), R.drawable.ic_status_hilang)
        );

        holder.lnly_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    Log.d("a", "onClick: QR");
                    listener.OnShowQRCodeListener(barang.getBARANG_ID()+"_"+barang.getJENIS_BARANG());
                }
            }
        });

        holder.lnly_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listenerEdit != null){

                    Barang item = new Barang();
                    item.setBARANG_ID(barang.getBARANG_ID());
                    item.setMEMBER_ID(barang.getMEMBER_ID());
                    item.setID_KATEGORY(barang.getID_KATEGORY());
                    item.setMERK_BARANG(barang.getMERK_BARANG());
                    item.setJENIS_BARANG(barang.getJENIS_BARANG());
                    item.setWARNA_BARANG(barang.getWARNA_BARANG());
                    item.setKETERANGAN(barang.getKETERANGAN());
                    item.setSTATUS(barang.getSTATUS());
                    item.setFOTO(barang.getFOTO());

                    listenerEdit.OnShowEditBarangListener(item);
                }
            }
        });

        holder.lnly_edit_barang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listenerEditBarang != null){

                    Barang item = new Barang();
                    item.setBARANG_ID(barang.getBARANG_ID());
                    item.setMEMBER_ID(barang.getMEMBER_ID());
                    item.setID_KATEGORY(barang.getID_KATEGORY());
                    item.setMERK_BARANG(barang.getMERK_BARANG());
                    item.setJENIS_BARANG(barang.getJENIS_BARANG());
                    item.setWARNA_BARANG(barang.getWARNA_BARANG());
                    item.setKETERANGAN(barang.getKETERANGAN());
                    item.setSTATUS(barang.getSTATUS());
                    item.setFOTO(barang.getFOTO());

                    listenerEditBarang.OnEditBarangListener(item);
                }
            }
        });

        Picasso.with(getContext())
                .load(ApiClient.BASE_URL_FOTO + barang.getFOTO())
                .error(R.drawable.ic_image)
                .fit()
                .into(holder.imgv_barang);
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    public void setList(List<Barang> listBarang){
        this.listBarang = listBarang;
        notifyDataSetChanged();
    }

    public void setOnShowQRCodeListener(setOnShowQRCodeListener listener){
        this.listener = listener;
    }

    public void setOnShowEditBarangListener(setOnShowEditBarangListener listener){
        this.listenerEdit = listener;
    }

    public void setOnEditBarangListener(setOnEditBarangListener listener){
        this.listenerEditBarang = listener;
    }

    public interface setOnShowQRCodeListener {
        void OnShowQRCodeListener(String url);
    }

    public interface setOnShowEditBarangListener {
        void OnShowEditBarangListener(Barang barang);
    }

    public interface setOnEditBarangListener {
        void OnEditBarangListener(Barang barang);
    }
}
