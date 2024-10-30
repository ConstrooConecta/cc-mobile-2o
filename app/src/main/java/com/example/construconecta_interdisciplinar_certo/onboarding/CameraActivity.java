package com.example.construconecta_interdisciplinar_certo.onboarding;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.example.construconecta_interdisciplinar_certo.AnunciarProdutoActivity;
import com.example.construconecta_interdisciplinar_certo.R;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraXGaleria";
    private static final String FILENAME_FORMAT = "yyyy-MM-dd";
    private static String[] REQUIRED_PERMISSIONS;

    static {
        List<String> requiredPermissions = new ArrayList<>();
        requiredPermissions.add(android.Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            requiredPermissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        requiredPermissions.add(android.Manifest.permission.RECORD_AUDIO);
        REQUIRED_PERMISSIONS = requiredPermissions.toArray(new String[0]);
    }

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private ActivityResultLauncher<Intent> resultLauncherGaleriaProduto =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        foto.setImageURI(imageUri);
                        foto.setVisibility(View.VISIBLE);
                        mostrarModalConfirmacaoProduto(foto);
                    }
                }
            });
    private ActivityResultLauncher<Intent> resultLauncherGaleria =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        foto.setImageURI(imageUri);
                        foto.setVisibility(View.VISIBLE);
                        mostrarModalConfirmacao(foto);
                    }
                }
            });
    private Map<String, String> docData = new HashMap<>();
    private DatabaseCamera databaseCameraE = new DatabaseCamera();
    private androidx.camera.view.PreviewView viewFinder;
    private ImageView foto;
    private ImageCapture imageCapture;
    private CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
    private Button btFoto;
    private ActivityResultLauncher<String[]> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                boolean permissionGranted = permissions.values().stream().allMatch(granted -> granted);
                if (!permissionGranted) {
                    Toast.makeText(this, "Permissão NEGADA. Tente novamente.", Toast.LENGTH_SHORT).show();
                } else {
                    startCamera();
                }
            }
    );

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        viewFinder = findViewById(R.id.viewFinder);
        foto = findViewById(R.id.fotoProduto);
        ImageView lente = findViewById(R.id.lente);
        btFoto = findViewById(R.id.image_capture_button);

        if (!AllPermissionsGranted()) {
            requestPermissions();
        } else {
            startCamera();
        }

        lente.setOnClickListener(this::virarLente);

        Intent intent1 = getIntent();
        boolean anuncioProduto = intent1.getBooleanExtra("produtoAnuncio", false);

        if (anuncioProduto) {
            btFoto.setOnClickListener(view -> tirarFotoProduto());
        } else {
            btFoto.setOnClickListener(view -> tirarFoto());
        }

        foto.setOnClickListener(v -> foto.setVisibility(View.INVISIBLE));

        ImageButton bt4 = findViewById(R.id.gallery);
        bt4.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (anuncioProduto) {
                resultLauncherGaleriaProduto.launch(intent);
            } else {
                resultLauncherGaleria.launch(intent);
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

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageCapture
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    public void tirarFoto() {
        if (imageCapture == null) return;

        String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/CameraSalaE");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
                getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ).build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                foto.setImageURI(outputFileResults.getSavedUri());
                mostrarModalConfirmacao(foto);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Toast.makeText(CameraActivity.this, "Erro ao salvar imagem!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void tirarFotoProduto() {
        if (imageCapture == null) return;

        String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/ProdutosAnunciados");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
                getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ).build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                foto.setImageURI(outputFileResults.getSavedUri());
                mostrarModalConfirmacaoProduto(foto);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Toast.makeText(CameraActivity.this, "Erro ao salvar imagem!", Toast.LENGTH_SHORT).show();
            }
        });
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
                Toast.makeText(CameraActivity.this, "Deu green!", Toast.LENGTH_SHORT).show();
                finish();
            });
            builder.setNegativeButton("Não", (dialog, which) -> foto.setVisibility(View.INVISIBLE));

            AlertDialog dialog = builder.create();
            dialog.show();
        }, 1000);
    }

    private void mostrarModalConfirmacaoProduto(final ImageView fotoTirada) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
            builder.setTitle("Confirmar imagem do produto");

            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmacao, null);
            ImageView imageView = dialogView.findViewById(R.id.imageViewFoto);
            Bitmap bitmap = ((BitmapDrawable) fotoTirada.getDrawable()).getBitmap();
            imageView.setImageBitmap(transformarImagemCircular(bitmap));

            builder.setView(dialogView);
            builder.setPositiveButton("Sim", (dialog, which) -> {
                databaseCameraE.uploadFoto(getBaseContext(), fotoTirada, docData, user.getEmail());
                Toast.makeText(CameraActivity.this, "Deu green!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CameraActivity.this, AnunciarProdutoActivity.class);
                startActivity(intent);
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

    private void virarLente(View view) {
        cameraSelector = (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                ? CameraSelector.DEFAULT_FRONT_CAMERA
                : CameraSelector.DEFAULT_BACK_CAMERA;
        startCamera();
    }
}
