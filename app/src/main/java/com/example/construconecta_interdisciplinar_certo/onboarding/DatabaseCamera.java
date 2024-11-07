package com.example.construconecta_interdisciplinar_certo.onboarding;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class DatabaseCamera {

    public void uploadFoto(Context c, ImageView foto, Map<String, String> docData, String emailUser) {
        //conversão
        Bitmap bitmap = ((BitmapDrawable) foto.getDrawable()).getBitmap();
        //saída do bitmap convertido (comprimir a imagem)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] databyte = baos.toByteArray();

        //abrir Database
        FirebaseStorage storage = FirebaseStorage.getInstance();
        //criar pasta "galeria"
        storage.getReference("galeria").child(emailUser + ".jpg")
                .putBytes(databyte)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //obter a URL da imagem
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                docData.put("url", uri.toString());
                            }
                        });

                    }
                });
    }

    public void uploadFotoProduto(Context c, ImageView foto, Map<String, String> docData, String emailUser) {
        //conversão
        Bitmap bitmap = ((BitmapDrawable) foto.getDrawable()).getBitmap();
        //saída do bitmap convertido (comprimir a imagem)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] databyte = baos.toByteArray();

        //abrir Database
        FirebaseStorage storage = FirebaseStorage.getInstance();
        //criar pasta "galeria"
        storage.getReference("produtos").child(emailUser + "_" + docData.get("nome") + ".jpg")
                .putBytes(databyte)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //obter a URL da imagem
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                docData.put("url", uri.toString());
                            }
                        });

                    }
                });
    }

    public void downloadFoto(ImageView img, Uri urlFirebase) {
        img.setRotation(0);
        Glide.with(img.getContext()).asBitmap().load(urlFirebase).into(img);
    }
}
