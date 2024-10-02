package com.example.construconecta_interdisciplinar_certo;

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

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();

    // Obtém o usuário atualmente logado
    FirebaseUser user = auth.getCurrentUser();
    //Lista de permissões
    private static String[] REQUIRED_PERMISSIONS = {
            android.Manifest.permission.CAMERA
    };
    static{
        List<String> requiredPermissions = new ArrayList<>();
        requiredPermissions.add(android.Manifest.permission.CAMERA);
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
            requiredPermissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        requiredPermissions.add(android.Manifest.permission.RECORD_AUDIO);
        REQUIRED_PERMISSIONS = requiredPermissions.toArray(new String[0]);
    }
    private ExecutorService cameraExecutor;
    private Map<String, String> docData = new HashMap<>();
    private Database databaseE = new Database();
    private androidx.camera.view.PreviewView viewFinder;
    private ImageView foto;
    private ImageCapture imageCapture;
    private CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
    private ImageView lente;
    private Button btFoto;
    //Log
    private static final String TAG = "CameraXGaleria";

    private static final String FILENAME_FORMAT = "yyyy-mm-dd";


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        //obter objetos
        cameraExecutor = Executors.newSingleThreadExecutor();
        viewFinder = findViewById(R.id.viewFinder);
        foto = findViewById(R.id.foto);
        lente = findViewById(R.id.lente);
        btFoto = findViewById(R.id.image_capture_button);

        //request de permissões
        if (!AllPermissionGranted()) {
            requestPermissions();
        }else{
            startCamera();
        }

        lente.setOnClickListener(view -> virarLente(view));
        btFoto.setOnClickListener(view -> {
            tirarFoto();
        });
        foto.setOnClickListener(v -> {
            foto.setVisibility((View.INVISIBLE));
        });
        ImageButton bt4 = findViewById(R.id.gallery);
        bt4.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            resultLauncherGaleria.launch(intent);
        });

    }
    private ActivityResultLauncher<Intent> resultLauncherGaleria =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                Uri imageUri = result.getData().getData();
                if (imageUri != null){
                    foto.setImageURI(imageUri);
                    foto.setVisibility(View.VISIBLE);
                }
            });
    //Bloco de permissão:
    private boolean AllPermissionGranted() {
        for (String permissions : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permissions) != PackageManager.
                    PERMISSION_GRANTED
            ) {
                return false;
            }
        }
        return true;
    }
    //bloco de funções para GERENCIAR PERMISSÕES
    private void requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS);
    }
    private ActivityResultLauncher<String[]> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
// Handle Permission granted/rejected
                boolean permissionGranted = true;
                for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
                    if (Arrays.asList(REQUIRED_PERMISSIONS).contains(entry.getKey()) && !entry.getValue()) {
                        permissionGranted = false;
                        break;
                    }
                }
                if (!permissionGranted) {
                    Toast.makeText(getApplicationContext(),"Permissão NEGADA. Tente novamente.",Toast.LENGTH_SHORT).show();
                } else {
                    //startCamera();
                }
            });
            // fim do bloco de funções para GERENCIAR PERMISSÕES

    // Configura a camera
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                // Used to bind the lifecycle of cameras to the lifecycle owner
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                // Preview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
                // ImageCapture
                imageCapture = new ImageCapture.Builder().build();
                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll();
                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                            this,
                            cameraSelector,
                            preview,
                            imageCapture
                    );
                } catch (Exception exc) {
                    Log.e(TAG, "Camera binding failed", exc);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }
    public void tirarFoto (){
        foto.setVisibility(View.VISIBLE);

        if (imageCapture == null){
            return;
        }

        //definir nome e caminho da imagem
        String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/CameraSalaE");

        //Carregando imagem
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
                getContentResolver(),MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ).build();


        OrientationEventListener orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                int rotation;

                if (orientation>=45 && orientation <135){
                    rotation = Surface.ROTATION_270;
                }else if (orientation>=135 && orientation <=224){
                    rotation = Surface.ROTATION_180;
                }else if (orientation>=225 && orientation <=314){
                    rotation = Surface.ROTATION_90;
                }else{
                    rotation = Surface.ROTATION_0;
                }
                imageCapture.setTargetRotation(rotation);
            }
        };

        imageCapture.takePicture(outputFileOptions,ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback(){
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

    private void mostrarModalConfirmacao(final ImageView fotoTirada) {
        // Cria um novo Handler para adicionar o delay
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Aqui será mostrado o AlertDialog após 6 segundos
                AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
                builder.setTitle("Confirmar foto de perfil");

                // Infla o layout personalizado
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_confirmacao, null);

                // Obtém o ImageView do layout do diálogo
                ImageView imageView = dialogView.findViewById(R.id.imageViewFoto);

                // Define a foto do ImageView recebido como parâmetro
                // Extrai o drawable do ImageView e o transforma em Bitmap
                Bitmap bitmap = ((BitmapDrawable) fotoTirada.getDrawable()).getBitmap();
                imageView.setImageBitmap(bitmap); // Define o bitmap na ImageView do dialog

                // Aplica a transformação circular na foto
                Bitmap fotoCircular = transformarImagemCircular(bitmap);
                imageView.setImageBitmap(fotoCircular);

                // Define o layout personalizado no diálogo
                builder.setView(dialogView);

                // Botão "Sim"
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Chama o método para salvar a foto
                        databaseE.uploadFoto(getBaseContext(), fotoTirada, docData, user.getEmail());
                        Toast.makeText(CameraActivity.this, "Deu green!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                // Botão "Não"
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Fecha o modal
                    }
                });

                // Exibe o AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }, 6000); // Atraso de 6 segundos
    }

    // Método para transformar a imagem em circular
    private Bitmap transformarImagemCircular(Bitmap bitmap) {
        if (bitmap == null) return null;

        int largura = bitmap.getWidth();
        int altura = bitmap.getHeight();
        int menorDimensao = Math.min(largura, altura);

        Bitmap output = Bitmap.createBitmap(menorDimensao, menorDimensao, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Path path = new Path();
        path.addCircle(menorDimensao / 2.0f, menorDimensao / 2.0f, menorDimensao / 2.0f, Path.Direction.CCW);

        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, (menorDimensao - largura) / 2.0f, (menorDimensao - altura) / 2.0f, null);

        return output;
    }



    public void virarLente (View view){
        if(cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA){
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
        }else{
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
        }
        startCamera();
    }};
