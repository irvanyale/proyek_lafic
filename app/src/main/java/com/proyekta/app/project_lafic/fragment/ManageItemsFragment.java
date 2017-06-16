package com.proyekta.app.project_lafic.fragment;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.SessionManagement;
import com.proyekta.app.project_lafic.activity.AddItemActivity;
import com.proyekta.app.project_lafic.activity.EditItemActivity;
import com.proyekta.app.project_lafic.fragment.adapter.ListItemsAdapter;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.helper.BarangHelper;
import com.proyekta.app.project_lafic.helper.BarangStatusAmanHelper;
import com.proyekta.app.project_lafic.model.Barang;
import com.proyekta.app.project_lafic.model.BarangHilang;
import com.proyekta.app.project_lafic.util.StorageUtil;
import com.proyekta.app.project_lafic.util.Util;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageItemsFragment extends Fragment {

    private static final String TAG = "ManageItemsFragment";

    private RelativeLayout rlly_footer;
    private RecyclerView rv_listItem;
    private ListItemsAdapter listItemsAdapter;
    private List<Barang> listBarang;
    private List<Barang> listBarangAman;
    private ApiInterface client;
    private ProgressDialog dialog;

    public ManageItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_items, container, false);

        initComponents(view);

        client = ApiClient.createService(ApiInterface.class, Util.getToken(getActivity()));

        listBarang = BarangHelper.getBarang();
        listBarangAman = BarangStatusAmanHelper.getBarangAman();

        listItemsAdapter = new ListItemsAdapter(getActivity(), listBarangAman);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_listItem.setLayoutManager(linearLayoutManager);
        rv_listItem.setAdapter(listItemsAdapter);

        //tampil QRCode
        listItemsAdapter.setOnShowQRCodeListener(new ListItemsAdapter.setOnShowQRCodeListener() {
            @Override
            public void OnShowQRCodeListener(String url) {
                Log.d(TAG, "OnShowQRCodeListener: onclick");
                showDialogQRCode(url);
            }
        });

        //tampil edit status
        listItemsAdapter.setOnShowEditBarangListener(new ListItemsAdapter.setOnShowEditBarangListener() {
            @Override
            public void OnShowEditBarangListener(Barang barang) {
                showDialogEditBarang(barang);
            }
        });

        listItemsAdapter.setOnEditBarangListener(new ListItemsAdapter.setOnEditBarangListener() {
            @Override
            public void OnEditBarangListener(Barang barang) {
                Intent intent = new Intent(getActivity(), EditItemActivity.class);
                intent.putExtra("barang_id", barang.getBARANG_ID());
                intent.putExtra("member_id", barang.getMEMBER_ID());
                intent.putExtra("id_kategori", barang.getID_KATEGORY());
                intent.putExtra("jenis_barang", barang.getJENIS_BARANG());
                intent.putExtra("merk_barang", barang.getMERK_BARANG());
                intent.putExtra("warna_barang", barang.getWARNA_BARANG());
                intent.putExtra("keterangan", barang.getKETERANGAN());
                intent.putExtra("foto", barang.getFOTO());
                intent.putExtra("status", barang.getSTATUS());
                startActivity(intent);
            }
        });

        rlly_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddItemActivity.class));
            }
        });

        return view;
    }

    private void initComponents(View view){
        rlly_footer = (RelativeLayout) view.findViewById(R.id.rlly_footer);
        rv_listItem = (RecyclerView) view.findViewById(R.id.rv_listItem);
    }

    private String getMemberId(){
        SessionManagement session = new SessionManagement(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        return user.get(SessionManagement.KEY_ID_MEMBER);
    }

    private String getMemberNama(){
        SessionManagement session = new SessionManagement(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        return user.get(SessionManagement.KEY_NAMA);
    }

    private Dialog dialogQrCode;
    //tampil dialog QRCode
    private void showDialogQRCode(final String id){
        dialogQrCode = new Dialog(getActivity(), R.style.Theme_Dialog_Fullscreen_Margin);
        dialogQrCode.setContentView(R.layout.dialog_qrcode);

        Window window = dialogQrCode.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        window.setAttributes(wlp);

        ImageView imgv_share = (ImageView) dialogQrCode.findViewById(R.id.imgv_share);

        //Image share QR code
        imgv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(id);
            }
        });

        dialogQrCode.setCanceledOnTouchOutside(true);

        Uri uri = Uri.fromFile(new File(StorageUtil.getFileDirectoryPath() + "/" +id+".jpg"));
        Log.d(TAG, "showDialogQRCode: "+uri);
        Log.d(TAG, "showDialogQRCode: "+getMemberId());

        ImageView image = (ImageView) dialogQrCode.findViewById(R.id.image);
        Picasso.with(getActivity())
                .load(uri)
                .fit()
                .error(R.drawable.ic_image)
                .into(image);

        dialogQrCode.show();
    }

    private String statusBarang = "AMAN";
    private EditText edtx_lokasi_hilang;
    private TextView edtx_tanggal_hilang;
    private TextView edtx_waktu_hilang;
    private Dialog dialogEditStatus;
    private DatePickerDialog DatePickerDialog;
    private TimePickerDialog timePickerDialog;

    private void showDialogEditBarang(final Barang barang){
        dialogEditStatus = new Dialog(getActivity(), R.style.Theme_Dialog_Fullscreen_Margin);
        dialogEditStatus.setContentView(R.layout.dialog_edit_barang);

        Window window = dialogEditStatus.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);

        final Button btn_aman = (Button) dialogEditStatus.findViewById(R.id.btn_aman);
        final Button btn_hilang = (Button) dialogEditStatus.findViewById(R.id.btn_hilang);
        Button btn_update = (Button) dialogEditStatus.findViewById(R.id.btn_update);
        edtx_lokasi_hilang = (EditText) dialogEditStatus.findViewById(R.id.edtx_lokasi_hilang);
        edtx_tanggal_hilang = (TextView) dialogEditStatus.findViewById(R.id.edtx_tanggal_hilang);
        edtx_waktu_hilang = (TextView) dialogEditStatus.findViewById(R.id.edtx_waktu_hilang);

        edtx_tanggal_hilang.setInputType(InputType.TYPE_NULL);
        edtx_waktu_hilang.setInputType(InputType.TYPE_NULL);
        btn_aman.setVisibility(View.GONE);

        btn_aman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_aman.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_green));
                btn_aman.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));

                btn_hilang.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_grey));
                btn_hilang.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.black));

                edtx_lokasi_hilang.setVisibility(View.GONE);
                edtx_tanggal_hilang.setVisibility(View.GONE);
                edtx_waktu_hilang.setVisibility(View.GONE);

                statusBarang = "AMAN";
            }
        });

        btn_hilang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_hilang.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_red));
                btn_hilang.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));

                btn_aman.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_grey));
                btn_aman.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.black));

                edtx_lokasi_hilang.setVisibility(View.VISIBLE);
                edtx_tanggal_hilang.setVisibility(View.VISIBLE);
                edtx_waktu_hilang.setVisibility(View.VISIBLE);

                statusBarang = "HILANG";
            }
        });

        edtx_tanggal_hilang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.show();
            }
        });

        edtx_waktu_hilang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doUpdateBarang(barang);
            }
        });

        showDatePicker();
        showTimePicker();

        dialogEditStatus.setCanceledOnTouchOutside(true);
        dialogEditStatus.show();
    }

    private void showDatePicker(){

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                edtx_tanggal_hilang.setText(sdf.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void showTimePicker(){

        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                edtx_waktu_hilang.setText(hourOfDay+":"+minute+":00");
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity()));
    }

    private void doUpdateBarang(final Barang barang){

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.show();

        barang.setSTATUS(statusBarang);
        Call<Barang> call = client.doUpdateBarang(barang);
        call.enqueue(new Callback<Barang>() {
            @Override
            public void onResponse(Call<Barang> call, Response<Barang> response) {
                if (response.isSuccessful()){
                    doPostBarangHilang(barang);
                } else {
                    Toast.makeText(getActivity(), "Data failed to load", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    dialogEditStatus.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Barang> call, Throwable t) {
                Toast.makeText(getActivity(), "connection problem", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                dialogEditStatus.dismiss();
            }
        });
    }

    private void doPostBarangHilang(final Barang barang){

        String datelost = edtx_tanggal_hilang.getText().toString();
        String timelost = edtx_waktu_hilang.getText().toString();

        BarangHilang barangHilang = new BarangHilang();
        barangHilang.setBARANG_ID(barang.getBARANG_ID());
        barangHilang.setTANGGAL_HILANG(datelost+" "+timelost);
        //barangHilang.setTANGGAL_KETEMU("-");
        barangHilang.setLOKASI_HILANG(edtx_lokasi_hilang.getText().toString());

        Call<BarangHilang> call = client.postBarangHilang(barangHilang);
        call.enqueue(new Callback<BarangHilang>() {
            @Override
            public void onResponse(Call<BarangHilang> call, Response<BarangHilang> response) {
                if (response.isSuccessful()){
                    loadBarang(barang.getMEMBER_ID());
                } else {
                    Toast.makeText(getActivity(), "Data failed to load", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    dialogEditStatus.dismiss();
                }
            }

            @Override
            public void onFailure(Call<BarangHilang> call, Throwable t) {
                Toast.makeText(getActivity(), "connection problem", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                dialogEditStatus.dismiss();
            }
        });
    }

    private void loadBarang(String idMember){

        Call<List<Barang>> call = client.getAllBarang(idMember);
        call.enqueue(new Callback<List<Barang>>() {
            @Override
            public void onResponse(Call<List<Barang>> call, Response<List<Barang>> response) {
                if (response.isSuccessful()){
                    listBarang.clear();
                    listBarangAman.clear();
                    List<Barang> barang = response.body();
                    for (Barang data : barang){
                        listBarang.add(data);
                        listBarangAman.add(data);
                    }
                    listBarangAman = BarangStatusAmanHelper.getBarangAman();
                    listItemsAdapter.setList(listBarangAman);
                } else {
                    Toast.makeText(getActivity(), "Data failed to load", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                dialogEditStatus.dismiss();
            }

            @Override
            public void onFailure(Call<List<Barang>> call, Throwable t) {
                Toast.makeText(getActivity(), "connection problem", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                dialogEditStatus.dismiss();
            }
        });
    }

    private void share(String id){
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        File file = new File(StorageUtil.getFileDirectoryPath(), id + ".jpg");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        share.setType("image/jpg");
        startActivity(Intent.createChooser(share, "Share"));
    }

    @Override
    public void onResume() {
        super.onResume();
        listBarang = BarangHelper.getBarang();
        listBarangAman = BarangStatusAmanHelper.getBarangAman();
        listItemsAdapter.setList(listBarangAman);

        listItemsAdapter.setOnShowQRCodeListener(new ListItemsAdapter.setOnShowQRCodeListener() {
            @Override
            public void OnShowQRCodeListener(String url) {
                showDialogQRCode(url);
            }
        });

        listItemsAdapter.setOnShowEditBarangListener(new ListItemsAdapter.setOnShowEditBarangListener() {
            @Override
            public void OnShowEditBarangListener(Barang barang) {
                showDialogEditBarang(barang);
            }
        });
    }
}
