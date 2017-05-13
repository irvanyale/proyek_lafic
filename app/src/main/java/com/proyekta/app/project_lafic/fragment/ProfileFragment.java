package com.proyekta.app.project_lafic.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.SessionManagement;
import com.proyekta.app.project_lafic.activity.AddItemActivity;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.model.Member;
import com.proyekta.app.project_lafic.util.Util;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private TextInputEditText edtx_nomor_id;
    private TextInputEditText edtx_nama;
    private TextInputEditText edtx_email;
    private TextInputEditText edtx_telepon;
    private TextInputEditText edtx_password;
    private TextInputEditText edtx_repassword;
    private Button btn_edit_submit;
    private Button btn_submit;
    private ApiInterface client;
    private String idMember;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initComponents(view);

        client = ApiClient.createService(ApiInterface.class, Util.getToken(getActivity()));

        btn_edit_submit.setOnClickListener(_handleClick);
        btn_submit.setOnClickListener(_handleClick);

        getUserAccount();

        return view;
    }

    private void initComponents(View view){
        edtx_nomor_id = (TextInputEditText) view.findViewById(R.id.edtx_nomor_id);
        edtx_nama = (TextInputEditText) view.findViewById(R.id.edtx_nama);
        edtx_email = (TextInputEditText) view.findViewById(R.id.edtx_email);
        edtx_telepon = (TextInputEditText) view.findViewById(R.id.edtx_telepon);
        edtx_password = (TextInputEditText) view.findViewById(R.id.edtx_password);
        edtx_repassword = (TextInputEditText) view.findViewById(R.id.edtx_repassword);
        btn_edit_submit = (Button) view.findViewById(R.id.btn_edit_submit);
        btn_submit = (Button) view.findViewById(R.id.btn_submit);

        setEnabled(false);
    }

    private void setEnabled(boolean value){
        edtx_nomor_id.setEnabled(value);
        edtx_nama.setEnabled(value);
        edtx_email.setEnabled(value);
        edtx_telepon.setEnabled(value);
        edtx_password.setEnabled(value);
        edtx_repassword.setEnabled(value);
    }

    private Member getEditTextMember(){
        String id = edtx_nomor_id.getText().toString();
        String nama = edtx_nama.getText().toString();
        String email = edtx_email.getText().toString();
        String telepon = edtx_telepon.getText().toString();
        String password = edtx_password.getText().toString();

        Member member = new Member();
        member.setMEMBER_ID(idMember);
        member.setNAMA_MEMBER(nama);
        member.setEMAIL_MEMBER(email);
        member.setTELEPON(telepon);
        member.setNOMOR_ID(id);
        member.setPASSWORD_MEMBER(password);

        return member;
    }

    public void getUserAccount(){
        try {
            SessionManagement session = new SessionManagement(getActivity());
            HashMap<String, String> user = session.getUserDetails();
            idMember = user.get(SessionManagement.KEY_ID_MEMBER);
            String id = user.get(SessionManagement.KEY_NOMOR_ID);
            String nama = user.get(SessionManagement.KEY_NAMA);
            String email = user.get(SessionManagement.KEY_EMAIL);
            String gender = user.get(SessionManagement.KEY_KELAMIN);
            String telepon = user.get(SessionManagement.KEY_TELEPON);

            edtx_nomor_id.setText(id);
            edtx_nama.setText(nama);
            edtx_email.setText(email);
            edtx_telepon.setText(telepon);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateUserAccount(Member member){
        SessionManagement session = new SessionManagement(getActivity());
        session.createLoginSession(
                member.getMEMBER_ID(),
                member.getNAMA_MEMBER(),
                member.getPASSWORD_MEMBER(),
                member.getEMAIL_MEMBER(),
                member.getTELEPON(),
                "",
                member.getNOMOR_ID(),
                "");

        edtx_nomor_id.setText(member.getNOMOR_ID());
        edtx_nama.setText(member.getNAMA_MEMBER());
        edtx_email.setText(member.getEMAIL_MEMBER());
        edtx_telepon.setText(member.getTELEPON());
        edtx_password.setText("");
        edtx_repassword.setText("");
    }

    private void doUpdateProfile(Member member){

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.show();

        Call<Member> call = client.doUpdateProfile(member);
        call.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                if (response.isSuccessful()){
                    Member member = response.body();
                    updateUserAccount(member);
                    setEnabled(false);
                    btn_submit.setVisibility(View.GONE);
                    btn_edit_submit.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getActivity(), "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                Toast.makeText(getActivity(), "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private View.OnClickListener _handleClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_edit_submit:
                    btn_submit.setVisibility(View.VISIBLE);
                    btn_edit_submit.setVisibility(View.GONE);
                    setEnabled(true);
                    break;
                case R.id.btn_submit:
                    doUpdateProfile(getEditTextMember());
                    break;
            }
        }
    };
}
