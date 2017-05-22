package com.proyekta.app.project_lafic.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.SessionManagement;
import com.proyekta.app.project_lafic.activity.SendMessageActivity;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.fragment.adapter.ListItemsAdapter;
import com.proyekta.app.project_lafic.fragment.adapter.MessagesAdapter;
import com.proyekta.app.project_lafic.helper.PesanHelper;
import com.proyekta.app.project_lafic.model.Pesan;
import com.proyekta.app.project_lafic.util.Util;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {

    private RecyclerView rv_listItem;
    private MessagesAdapter messagesAdapter;
    private List<Pesan> listPesan;
    private ApiInterface client;

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        initComponents(view);

        client = ApiClient.createService(ApiInterface.class, Util.getToken(getActivity()));

        listPesan = PesanHelper.getPesan();

        messagesAdapter = new MessagesAdapter(getActivity(), listPesan);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_listItem.setLayoutManager(linearLayoutManager);
        rv_listItem.setAdapter(messagesAdapter);

        getAllMessages();

        return view;
    }

    private void initComponents(View view){
        rv_listItem = (RecyclerView) view.findViewById(R.id.rv_listItem);
    }

    private String getIdMember(){
        SessionManagement session = new SessionManagement(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        return user.get(SessionManagement.KEY_ID_MEMBER);
    }

    private void getAllMessages(){
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.show();

        Call<List<Pesan>> call = client.getAllMessages(getIdMember());
        call.enqueue(new Callback<List<Pesan>>() {
            @Override
            public void onResponse(Call<List<Pesan>> call, Response<List<Pesan>> response) {
                if (response.isSuccessful()){
                    listPesan.clear();
                    for (Pesan pesan : response.body()){
                        listPesan.add(pesan);
                    }
                    messagesAdapter.setList(listPesan);
                } else {
                    Toast.makeText(getActivity(), "Data gagal dimuat", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Pesan>> call, Throwable t) {
                Toast.makeText(getActivity(), "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

}
