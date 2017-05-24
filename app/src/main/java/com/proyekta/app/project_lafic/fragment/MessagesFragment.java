package com.proyekta.app.project_lafic.fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.proyekta.app.project_lafic.model.Member;
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
    private AlertDialog alertDialog;
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

        messagesAdapter.setOnSendMessageListener(new MessagesAdapter.setOnSendMessageListener() {
            @Override
            public void OnSendMessageListener(Member member) {
                showDialogSendMessage(member);
            }
        });

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

    private void showDialogSendMessage(final Member member){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setTitle("Apakah Anda ingin mengirim pesan ke "+member.getNAMA_MEMBER()+"?");

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getActivity(), SendMessageActivity.class);
                        intent.putExtra("member_id", member.getMEMBER_ID());
                        intent.putExtra("nama", member.getNAMA_MEMBER());
                        intent.putExtra("telp", member.getTELEPON());
                        intent.putExtra("email", member.getEMAIL_MEMBER());
                        intent.putExtra("no_id", member.getNOMOR_ID());
                        intent.putExtra("jenis_pesan", "PENEMUAN BARANG");
                        startActivity(intent);
                        alertDialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        alertDialog = alertDialogBuilder.create();

        alertDialog.show();
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
