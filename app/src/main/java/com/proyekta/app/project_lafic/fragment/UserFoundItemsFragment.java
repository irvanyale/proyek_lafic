package com.proyekta.app.project_lafic.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.SessionManagement;
import com.proyekta.app.project_lafic.activity.AddFoundItemActivity;
import com.proyekta.app.project_lafic.activity.BerandaActivity;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.fragment.adapter.UserFoundItemsAdapter;
import com.proyekta.app.project_lafic.helper.BarangPenemuanHelper;
import com.proyekta.app.project_lafic.helper.UserBarangPenemuanHelper;
import com.proyekta.app.project_lafic.model.Barang;
import com.proyekta.app.project_lafic.model.BarangPenemuan;
import com.proyekta.app.project_lafic.model.SuksesResponse;
import com.proyekta.app.project_lafic.util.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFoundItemsFragment extends Fragment {

    private static final String TAG = "UserFoundItemsFragment";

    private RelativeLayout rlly_footer;
    private RecyclerView rv_listItem;
    private UserFoundItemsAdapter foundItemsAdapter;
    private List<BarangPenemuan> barangPenemuan;
    private ApiInterface client;
    private ProgressDialog dialog;

    public UserFoundItemsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_found_items, container, false);

        initComponents(view);

        client = ApiClient.createService(ApiInterface.class, Util.getToken(getActivity()));

        barangPenemuan = UserBarangPenemuanHelper.getUserBarangPenemuan();

        foundItemsAdapter = new UserFoundItemsAdapter(getActivity(), barangPenemuan);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_listItem.setLayoutManager(linearLayoutManager);
        rv_listItem.setAdapter(foundItemsAdapter);

        rlly_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddFoundItemActivity.class));
            }
        });

        foundItemsAdapter.setOnShowEditBarangListener(new UserFoundItemsAdapter.setOnShowEditBarangListener() {
            @Override
            public void OnShowEditBarangListener(String id) {
                showDialogDeleteBarangPenemuan(id);
            }
        });

        loadDataBarangPenemuan();

        return view;
    }

    private void initComponents(View view){
        rv_listItem = (RecyclerView) view.findViewById(R.id.rv_listItem);
        rlly_footer = (RelativeLayout) view.findViewById(R.id.rlly_footer);
    }

    private String statusBarang = "AMAN";
    private EditText edtx_lokasi_hilang;

    private Dialog dialogEditStatus;

    private void showDialogDeleteBarangPenemuan(final String id){
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

        btn_hilang.setVisibility(View.GONE);

        btn_aman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_aman.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_green));
                btn_aman.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));

                btn_hilang.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_grey));
                btn_hilang.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.black));

                edtx_lokasi_hilang.setVisibility(View.GONE);

                statusBarang = "AMAN";
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBarangPenemuan(id);
            }
        });

        dialogEditStatus.setCanceledOnTouchOutside(true);
        dialogEditStatus.show();
    }

    private String getMemberId(){
        SessionManagement session = new SessionManagement(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        return user.get(SessionManagement.KEY_ID_MEMBER);
    }

    private void loadDataBarangPenemuan(){

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.show();

        Call<List<BarangPenemuan>> call = client.getBarangPenemuanMember(getMemberId());
        call.enqueue(new Callback<List<BarangPenemuan>>() {
            @Override
            public void onResponse(Call<List<BarangPenemuan>> call, Response<List<BarangPenemuan>> response) {
                if (response.isSuccessful()){
                    barangPenemuan.clear();
                    for (BarangPenemuan item : response.body()){
                        barangPenemuan.add(item);
                    }
                    foundItemsAdapter.setList(barangPenemuan);
                } else {
                    Toast.makeText(getActivity(), "Data failed to load", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<BarangPenemuan>> call, Throwable t) {
                Toast.makeText(getActivity(), "connection problem", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void deleteBarangPenemuan(String id){
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.show();

        Call<SuksesResponse> call = client.deleteBarangPenemuan(id);
        call.enqueue(new Callback<SuksesResponse>() {
            @Override
            public void onResponse(Call<SuksesResponse> call, Response<SuksesResponse> response) {
                if (response.isSuccessful()){
                    loadDataBarangPenemuan();
                } else {
                    Toast.makeText(getActivity(), "Data gagal dimuat", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                dialogEditStatus.dismiss();
            }

            @Override
            public void onFailure(Call<SuksesResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                dialogEditStatus.dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        barangPenemuan = UserBarangPenemuanHelper.getUserBarangPenemuan();
        foundItemsAdapter.setList(barangPenemuan);
    }
}
