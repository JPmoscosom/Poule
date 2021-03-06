package com.example.poule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class PerfilUsuario extends AppCompatActivity {
    EditText txtNombre,txtApellidos,txtCedula,txtDireccion,txtFecha, txtEmail;
    ImageButton btnPerfil, btnVehiculos, btnRutas, btnViajes;
    FloatingActionButton btnEditar, btnConductor, bntImagen;
    ImageView ivPerfil;
    String Rol;
    String key, URL;
    Uri uri;
    public static final int PICK_IMAGE = 1;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);
        conectar();
        getDatos();
        conductor();
        editar();
        transiciones();
        getImagen();
    }

    private void conductor() {
        btnConductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(PerfilUsuario.this);
                builder1.setMessage("Quieres registrarte como Conductor/a?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Si, Registrar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                HashMap User = new HashMap();
                                User.put("rol", "conductor");
                                mDatabase = FirebaseDatabase.getInstance().getReference("Usuarios");
                                mDatabase.child(key).updateChildren(User);
                                Intent intent = new Intent(PerfilUsuario.this, RegistroVehiculo.class);
                                startActivity(intent);
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No, Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
    }

    private void conectar() {
        txtNombre = findViewById(R.id.txtNombre);
        txtDireccion = findViewById(R.id.txtDireccion);
        txtApellidos = findViewById(R.id.txtApellidos);
        txtCedula = findViewById(R.id.txtCedula);
        txtFecha = findViewById(R.id.txtFecha);
        txtEmail = findViewById(R.id.txtEmail);
        btnConductor = findViewById(R.id.btnConductor);
        btnEditar = findViewById(R.id.btnEditar);
        btnPerfil = findViewById(R.id.btnPerfil);
        btnViajes = findViewById(R.id.btnViajes);
        btnVehiculos = findViewById(R.id.btnVehiculos);
        btnRutas = findViewById(R.id.btnRutas);
        ivPerfil = findViewById(R.id.ivPerfil);
        bntImagen = findViewById(R.id.btnImagen);
    }

    private void editar(){

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerfilUsuario.this,EdicionDatos.class);
                startActivity(intent);
            }
        });
    }

    private String getEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = user.getEmail();
        if (user != null) {
        } else {
            userEmail = "Email not  Found";
        }
        return userEmail;
    }

    private void getDatos(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("Usuarios");
        StorageReference storageRef = storage.getReference("images/" + getEmail());
        Query q = ref.orderByChild("email").equalTo(getEmail());
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String cedula = dataSnapshot.child("dni").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String direccion = dataSnapshot.child("direccion").getValue(String.class);
                    String nombre = dataSnapshot.child("nombre").getValue(String.class);
                    String apellidos = dataSnapshot.child("apellidos").getValue(String.class);
                    String fecha = dataSnapshot.child("fechaNacimiento").getValue(String.class);
                    Rol = dataSnapshot.child("rol").getValue(String.class);
                    if (Rol.equals("usuario")){
                        btnConductor.setVisibility(View.VISIBLE);
                    }else{
                        btnConductor.setVisibility(View.INVISIBLE);
                    }
                    txtCedula.setText(cedula);
                    txtApellidos.setText(apellidos);
                    txtDireccion.setText(direccion);
                    txtFecha.setText(fecha);
                    txtEmail.setText(email);
                    txtNombre.setText(nombre);
                    key = dataSnapshot.getKey();
                    getImage(email);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void getImage(String email){
        StorageReference storageReference = storage.getReference("images/" + email);

        Glide.with(this /* context */)
                .load(storageReference).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(ivPerfil);
    }

    public void transiciones(){
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerfilUsuario.this,PerfilUsuario.class);
                startActivity(intent);
            }
        });
        btnVehiculos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerfilUsuario.this,VehiculosUsusario.class);
                startActivity(intent);
            }
        });
      /*  btnViajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerfilUsuario.this,MainScreen.class);
                startActivity(intent);
            }
        });
        btnRutas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerfilUsuario.this,MainScreen.class);
                startActivity(intent);
            }
        });*/
    }

    private void getImagen(){
        bntImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccione una foto"), PICK_IMAGE);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE){
            uri = data.getData();
            Upload();


        }
    }

    private void Upload(){
        StorageReference storageRef = storage.getReference("images/" + getEmail());
        storageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                URL = storageRef.getDownloadUrl().toString();
                Toast.makeText(getApplicationContext(),"Imagen de perfil actualizada correctamente",Toast.LENGTH_LONG).show();
                recreate();
            }
        });
    }
}




