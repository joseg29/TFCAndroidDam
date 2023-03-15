package com.example.tarea1firebase;

import static com.example.tarea1firebase.Registro.COLECCION;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Login extends AppCompatActivity {
    private Button btnLogin, btnRegistrar, btnIniciarSesion;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private EditText etEmail, etPassword;
    private Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> {
            //Cambio a activity de registro
            Intent intent = new Intent(Login.this, Registro.class);
            startActivity(intent);
            finish();
        });

        btnIniciarSesion.setOnClickListener(v -> {
            validarLogin();
        });
    }

    public void validarLogin() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);


        String emailUsuario = etEmail.getText().toString();
        String claveUsuario = etPassword.getText().toString();


        if (!emailUsuario.isEmpty() && !claveUsuario.isEmpty()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailUsuario, claveUsuario).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            // El usuario no existe
                            Toast.makeText(Login.this, "Email o contraseña incorrecta", Toast.LENGTH_LONG).show();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            // Credenciales inválidas (correo electrónico incorrecto o contraseña incorrecta)
                            Toast.makeText(Login.this, "Email o contraseña incorrecta", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        iniciarSesion();
                    }
                }
            });
        } else {
            Toast.makeText(Login.this, "Hay algún campo vacío.", Toast.LENGTH_LONG).show();
        }
    }

    public void iniciarSesion() {
        String idDocumento = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Se hace un get de firestore database con el id como referencia de documento, para obtener el objeto Usuario y pasarlo a la siguiente activity
        db.collection(COLECCION).document(idDocumento).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user = document.toObject(Usuario.class);
                        Toast.makeText(Login.this, "Sesión iniciada", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Login.this, PerfilUsuario.class);
                        intent.putExtra("USUARIO", user);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }
}
