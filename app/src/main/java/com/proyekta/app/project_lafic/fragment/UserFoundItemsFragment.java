package com.proyekta.app.project_lafic.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
import com.proyekta.app.project_lafic.model.BarangPenemuan;
import com.proyekta.app.project_lafic.util.Util;

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

        loadDataBarangPenemuan();

        return view;
    }

    private void initComponents(View view){
        rv_listItem = (RecyclerView) view.findViewById(R.id.rv_listItem);
        rlly_footer = (RelativeLayout) view.findViewById(R.id.rlly_footer);
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

    @Override
    public void onResume() {
        super.onResume();

        barangPenemuan = UserBarangPenemuanHelper.getUserBarangPenemuan();
        foundItemsAdapter.setList(barangPenemuan);
    }
}
