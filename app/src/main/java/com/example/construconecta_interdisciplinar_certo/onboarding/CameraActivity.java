package com.example.construconecta_interdisciplinar_certo.onboarding;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraActivity extends BaseActivity {

    private static final String TAG = "CameraXGaleria";
    private static final String FILENAME_FORMAT = "yyyy-MM-dd";
    private static final int REQUEST_IMAGE_CAPTURE = 1; // Código de solicitação para a câmera
    private static String[] REQUIRED_PERMISSIONS;

    static {
        List<String> requiredPermissions = new ArrayList<>();
        requiredPermissions.add(android.Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            requiredPermissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        REQUIRED_PERMISSIONS = requiredPermissions.toArray(new String[0]);
    }

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private ImageView foto;
    private Map<String, String> docData = new HashMap<>();
    private DatabaseCamera databaseCameraE = new DatabaseCamera();

    private ActivityResultLauncher<String[]> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                boolean permissionGranted = permissions.values().stream().allMatch(granted -> granted);
                if (!permissionGranted) {
                    Toast.makeText(this, "Permissão NEGADA. Tente novamente.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        foto = findViewById(R.id.fotoProduto);

        if (!AllPermissionsGranted()) {
            requestPermissions();
        }

        Intent intent1 = getIntent();
        boolean anuncioProduto = intent1.getBooleanExtra("produtoAnuncio", false);

        // Alterar o clique do botão para abrir a câmera nativa
        dispatchTakePictureIntent();


        foto.setOnClickListener(v -> foto.setVisibility(View.INVISIBLE));

        ImageButton bt4 = findViewById(R.id.gallery);
        bt4.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (anuncioProduto) {
                // Chamada para a galeria do produto (a lógica não foi alterada)
            } else {
                // Chamada para a galeria (a lógica não foi alterada)
            }
        });
    }

    private boolean AllPermissionsGranted() {
        return Arrays.stream(REQUIRED_PERMISSIONS)
                .allMatch(permission -> ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            foto.setImageBitmap(imageBitmap);
            foto.setVisibility(View.VISIBLE);
            mostrarModalConfirmacao(foto);
        }
    }

    private void mostrarModalConfirmacao(final ImageView fotoTirada) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
            builder.setTitle("Confirmar foto de perfil");

            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmacao, null);
            ImageView imageView = dialogView.findViewById(R.id.imageViewFoto);
            Bitmap bitmap = ((BitmapDrawable) fotoTirada.getDrawable()).getBitmap();
            imageView.setImageBitmap(transformarImagemCircular(bitmap));

            builder.setView(dialogView);
            builder.setPositiveButton("Sim", (dialog, which) -> {
                databaseCameraE.uploadFoto(getBaseContext(), fotoTirada, docData, user.getEmail());
                finish();
            });
            builder.setNegativeButton("Não", (dialog, which) -> foto.setVisibility(View.INVISIBLE));

            AlertDialog dialog = builder.create();
            dialog.show();
        }, 1000);
    }

    private Bitmap transformarImagemCircular(Bitmap bitmap) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap circularBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(circularBitmap);
        Path path = new Path();
        path.addCircle(size / 2f, size / 2f, size / 2f, Path.Direction.CCW);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);

        return circularBitmap;
    }
}
