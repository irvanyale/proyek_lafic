package com.proyekta.app.project_lafic.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.SessionManagement;

import java.util.HashMap;

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

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initComponents(view);

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
    }

    public void getUserAccount(){
        try {
            SessionManagement session = new SessionManagement(getActivity());
            HashMap<String, String> user = session.getUserDetails();
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

}
