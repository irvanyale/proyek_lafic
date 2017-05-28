package com.proyekta.app.project_lafic.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.proyekta.app.project_lafic.R;
import com.proyekta.app.project_lafic.SessionManagement;
import com.proyekta.app.project_lafic.activity.AddItemActivity;
import com.proyekta.app.project_lafic.api.ApiClient;
import com.proyekta.app.project_lafic.api.ApiInterface;
import com.proyekta.app.project_lafic.model.Foto;
import com.proyekta.app.project_lafic.model.Member;
import com.proyekta.app.project_lafic.util.ImageUtil;
import com.proyekta.app.project_lafic.util.Util;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private int SELECT_FILE = 1;

    private TextInputEditText edtx_nomor_id;
    private TextInputEditText edtx_nama;
    private TextInputEditText edtx_email;
    private TextInputEditText edtx_telepon;
    private TextInputEditText edtx_password;
    private TextInputEditText edtx_repassword;
    private RelativeLayout rlly_edit_foto;
    private RelativeLayout rlly_ic_foto;
    private ImageView imgv_user;
    private Button btn_edit_submit;
    private Button btn_submit;
    private ApiInterface client;
    private String idMember;
    private ProgressDialog dialog;
    private String path_gallery = "-1";
    private String fotoPath = "";

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
        rlly_edit_foto.setOnClickListener(_handleClick);

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
        rlly_edit_foto = (RelativeLayout) view.findViewById(R.id.rlly_edit_foto);
        rlly_ic_foto = (RelativeLayout) view.findViewById(R.id.rlly_ic_foto);
        imgv_user = (ImageView) view.findViewById(R.id.imgv_user);
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
        rlly_edit_foto.setEnabled(value);
        if (value){
            rlly_ic_foto.setVisibility(View.VISIBLE);
        }
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
            String foto = user.get(SessionManagement.KEY_FOTO);

            edtx_nomor_id.setText(id);
            edtx_nama.setText(nama);
            edtx_email.setText(email);
            edtx_telepon.setText(telepon);

            Picasso.with(getActivity())
                    .load(ApiClient.BASE_URL_FOTO + foto)
                    .error(R.drawable.ic_profile_circle_grey)
                    .fit()
                    .into(imgv_user);

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
                "",
                member.getFOTO(),
                "");

        edtx_nomor_id.setText(member.getNOMOR_ID());
        edtx_nama.setText(member.getNAMA_MEMBER());
        edtx_email.setText(member.getEMAIL_MEMBER());
        edtx_telepon.setText(member.getTELEPON());
        edtx_password.setText("");
        edtx_repassword.setText("");

        Picasso.with(getActivity())
                .load(ApiClient.BASE_URL_FOTO + member.getFOTO())
                .error(R.drawable.ic_profile_circle_grey)
                .fit()
                .into(imgv_user);
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == SELECT_FILE){
                if (data != null){
                    try {
                        Uri uriImage = data.getData();
                        String[] filePath = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getActivity().getContentResolver().query(uriImage, filePath, null, null, null);
                        if (cursor != null){
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePath[0]);
                            path_gallery = cursor.getString(columnIndex);
                            cursor.close();
                        }

                        Picasso.with(getActivity())
                                .load(uriImage)
                                .error(R.drawable.ic_profile_circle_grey)
                                .fit()
                                .into(imgv_user);

                    } catch (Exception e){
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                }
            }
        }
    }

    private void uploadFoto(final Member member, String filePath){

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.show();

        File file = new File(filePath);
        //reduce image size
        File image = ImageUtil.ImageResizer(file) == null ? file : ImageUtil.ImageResizer(file);

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), image);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), idMember);

        Call<Foto> call = client.uploadFoto(body, id);
        call.enqueue(new Callback<Foto>() {
            @Override
            public void onResponse(Call<Foto> call, Response<Foto> response) {
                if (response.isSuccessful()){
                    Foto fotoPathApi = response.body();
                    fotoPath = fotoPathApi.getFOTO();
                    member.setFOTO(fotoPath);
                    doUpdateProfile(member);
                } else {
                    Toast.makeText(getActivity(), "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Foto> call, Throwable t) {
                Toast.makeText(getActivity(), "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }

    private void doUpdateProfile(Member member){

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
                    if (edtx_nomor_id.getText().toString().equals("") ||
                            edtx_nama.getText().toString().equals("") ||
                            edtx_email.getText().toString().equals("") ||
                            edtx_telepon.getText().toString().equals("") ||
                            edtx_password.getText().toString().equals("")){
                        Toast.makeText(getActivity(), "Silahkan lengkapi data Anda", Toast.LENGTH_SHORT).show();
                    } else if (!edtx_password.getText().toString().equals(edtx_repassword.getText().toString())){
                        Toast.makeText(getActivity(), "Password tidak sama", Toast.LENGTH_SHORT).show();
                    } else {
                        uploadFoto(getEditTextMember(), path_gallery);
                    }

                    break;
                case R.id.rlly_edit_foto:
                    openGallery();
                    break;
            }
        }
    };
}
