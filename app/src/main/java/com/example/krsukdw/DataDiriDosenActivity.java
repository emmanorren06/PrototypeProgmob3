package com.example.krsukdw;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.krsukdw.Adapter.DosenAdapter;
import com.example.krsukdw.Model.DefaultResult;
import com.example.krsukdw.Model.Dosen;
import com.example.krsukdw.Network.GetDataService;
import com.example.krsukdw.Network.RetrofitClientInstance;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

public class DataDiriDosenActivity extends AppCompatActivity {

    private boolean isInsert = false;
    ProgressDialog progressDialog;
    EditText nidn, nama, gelar, alamat, email, foto;
    ImageView imgDosen;
    private boolean isUpdate = false;
    private String idDosen = "";
    private String stringImg = "";
    private static final int GALLERY_REQUEST_CODE=58;
    private static final int FILE_ACCESS_REQUEST_CODE=58;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_diri_dosen);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE}, FILE_ACCESS_REQUEST_CODE);
        }
        nama = findViewById(R.id.txtNamaDosen);
        nidn = findViewById(R.id.txtNidnDosen);
        alamat = findViewById(R.id.txtAlamatDosen);
        email = findViewById(R.id.txtEmailDosen);
        gelar = findViewById(R.id.txtGelarDosen);
        foto = (EditText) findViewById(R.id.txtFotoDosen);
        imgDosen = findViewById(R.id.imgDosen);

        Button btnUpload = findViewById(R.id.btnUploadDataDiri);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                String[] mimeTypes = {"image/jpeg"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });
        progressDialog = new ProgressDialog(this);

        checkUpdate();
        Button btnSimpan = (Button) findViewById(R.id.btnSaveDataDiriDosen);
        if (isUpdate) {
            btnSimpan.setText("Update");
        }
        Button btnSaveDataDiri = (Button) findViewById(R.id.btnSaveDataDiriDosen);
        btnSaveDataDiri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Boolean isValid = true;
                //Validation
                if(nama.getText().toString().length() == 0){
                    nama.setError("Silahkan mengisi nama dosen");
                }

                if(nidn.getText().toString().length() == 0){
                    nidn.setError("Silahkan mengisi NIDN dosen");
                }

                if(alamat.getText().toString().length() == 0){
                    alamat.setError("Silahkan mengisi alamat dosen");
                }

                if(email.getText().toString().length() == 0){
                    email.setError("Silahkan mengisi email dosen");
                }

                if(gelar.getText().toString().length() == 0){
                    gelar.setError("Silahkan mengisi gelar dosen");
                }

                if(foto.getText().toString().length() == 0){
                    foto.setError("Silahkan mengisi foto dosen");
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(DataDiriDosenActivity.this);
                nama = (EditText) findViewById(R.id.txtEditNamaDosen);
                nidn = (EditText) findViewById(R.id.txtEditNidnDosen);
                alamat = (EditText) findViewById(R.id.editText2);
                email = (EditText) findViewById(R.id.editText4);
                gelar = (EditText) findViewById(R.id.editText3);
                foto = (EditText) findViewById(R.id.txtFoto);
                imgDosen = findViewById(R.id.imgDosen);
                builder.setMessage("Menyimpan data?").setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(DataDiriDosenActivity.this, "Tidak jadi disimpan", Toast.LENGTH_SHORT).show();
                    }
                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (!isInsert) {
                            progressDialog.setMessage("Send Data");
                            progressDialog.show();


                            GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
                            Call<DefaultResult> call = service.insert_foto_dosen(
                                    nama.getText().toString(),
                                    nidn.getText().toString(),
                                    alamat.getText().toString(),
                                    email.getText().toString(),
                                    gelar.getText().toString(),
                                    /*foto.getText().toString()*/
                                    stringImg,
                                    "72170101");
                            call.enqueue(new Callback<com.example.krsukdw.Model.DefaultResult>() {
                                @Override
                                public void onResponse(Call<DefaultResult> call, Response<DefaultResult> response) {
                                    progressDialog.dismiss();
                                    Toast.makeText(DataDiriDosenActivity.this, "Data Tersimpan",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(DataDiriDosenActivity.this, RecyclerViewDosenActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onFailure(Call<DefaultResult> call, Throwable t) {
                                    Toast.makeText(DataDiriDosenActivity.this, "Gagal Menyimpan",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            progressDialog.setMessage("Send Data");
                            progressDialog.show();

                            GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
                            Call<DefaultResult> call = service.update_foto_dosen(
                                    idDosen,
                                    nama.getText().toString(),
                                    nidn.getText().toString(),
                                    alamat.getText().toString(),
                                    email.getText().toString(),
                                    gelar.getText().toString(),
                                    /*foto.getText().toString()*/
                                    stringImg,
                                    "72170101");
                            call.enqueue(new Callback<com.example.krsukdw.Model.DefaultResult>() {
                                @Override
                                public void onResponse(Call<DefaultResult> call, Response<DefaultResult> response) {
                                    progressDialog.dismiss();
                                    Toast.makeText(DataDiriDosenActivity.this, "Data Terubah",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(DataDiriDosenActivity.this, RecyclerViewDosenActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onFailure(Call<DefaultResult> call, Throwable t) {
                                    Toast.makeText(DataDiriDosenActivity.this, "Gagal Terubah",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });
    }
    void checkUpdate() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        isUpdate = extras.getBoolean("is_update");
        idDosen = extras.getString("id_dosen");
        nama.setText(extras.getString("nama"));
        nidn.setText(extras.getString("nidn"));
        alamat.setText(extras.getString("alamat"));
        email.setText(extras.getString("email"));
        gelar.setText(extras.getString("gelar"));
        foto.setText(extras.getString("foto"));
        stringImg = extras.getString("foto");
        Picasso.with(DataDiriDosenActivity.this).load("https://kpsi.fti.ukdw.ac.id/progmob/%7B%7Bfotoanda%7D%7D%22" + extras.getString("foto"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case FILE_ACCESS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    //PERMISSION_GRANTED
                }
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    Uri selectedImage = data.getData();
                    imgDosen.setImageURI(selectedImage);
                    //proses konversi
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage,
                            /*mendapatkan realpath*/   filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imgDecodeableString = cursor.getString(columnIndex);
                    foto.setText(imgDecodeableString);
                    cursor.close();
                    //convert ke bitmap, lalu array, lalu stringnya pakai base64
                    Bitmap bm = BitmapFactory.decodeFile(imgDecodeableString);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                    byte[] b = baos.toByteArray();

                    stringImg = Base64.encodeToString(b, Base64.DEFAULT);
                    break;
            }
    }
}