package com.volcanapp.volcanapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.volcanapp.volcanapp.modelos.FirebaseReference;
import com.volcanapp.volcanapp.modelos.LugarMonitoreado;

import java.util.ArrayList;
import java.util.List;

public class RegistroLugar extends FragmentActivity implements OnMapReadyCallback {
    private TextView TV_descripcion;
    private TextView TV_tipoAlarma;
    private TextView TV_ultimaAct;
    private Spinner SpinnerLugaresMonitoreados;
    private MapView mapaLugar;
    private Button btnRegistroLugar;
    private FirebaseFirestore db;
    private Task<DocumentSnapshot> docRef;
    private GoogleMap mMap;

    private Double latitud = 4.00;
    private Double longitud  = 74.0;
    private String nombre = "Default";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_lugar);

        init();
        event();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMaps1);
        mapFragment.getMapAsync(this);




    }

    private void event() {
        btnRegistroLugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHomeUI();
            }
        });
    }

    private void showHomeUI() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void init() {
        TV_descripcion = findViewById(R.id.tv_descripcion);
        TV_tipoAlarma = findViewById(R.id.tv_tipoAlarma);
        TV_ultimaAct = findViewById(R.id.tv_ultimaAct);
        SpinnerLugaresMonitoreados = findViewById(R.id.Spinner_lugaresMonitoreados);
        mapaLugar = findViewById(R.id.nav_view);
        btnRegistroLugar = findViewById(R.id.btn_registrarLugar);
        db = FirebaseFirestore.getInstance();
        loadLuagresMonitoreados();



    }

    private void loadLuagresMonitoreados() {
        List<LugarMonitoreado> lugaresMonitoreados = new ArrayList<>();
        db.collection(FirebaseReference.DB_REFERENCE_LUGARES_MONITOREADOS)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            msn("FireBase error, " + error.getMessage());

                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {


                                String id = dc.getDocument().getId();
                                String nombre = dc.getDocument().getString("nombre").toString();
                                String descripcion = dc.getDocument().getString("descripcion").toString();
                                String tipo_alarma = dc.getDocument().getString("tipo_alarma").toString();
                                String ultima_actualizacion = dc.getDocument().getString("ultima_actualizacion").toString();
                                String latitud = dc.getDocument().getDouble("latitud").toString();
                                String longitud = dc.getDocument().getDouble("longitud").toString();


                                lugaresMonitoreados.add(new LugarMonitoreado(
                                        id, nombre, descripcion, tipo_alarma, ultima_actualizacion, latitud, longitud
                                ));
                                //lugaresMonitoreados.add(dc.getDocument().toObject(LugarMonitoreado.class));

                            }
                        }
                        ArrayAdapter<LugarMonitoreado> arrayAdapter = new ArrayAdapter<>(RegistroLugar.this,
                                android.R.layout.simple_dropdown_item_1line, lugaresMonitoreados);
                        SpinnerLugaresMonitoreados.setAdapter(arrayAdapter);
                        SpinnerLugaresMonitoreados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                String uid = adapterView.getItemAtPosition(position).toString();
                                docRef = db.collection(FirebaseReference.DB_REFERENCE_LUGARES_MONITOREADOS)
                                        .document(uid)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()){
                                                    nombre = documentSnapshot.getString("nombre");
                                                    String descripcion = documentSnapshot.getString("descripcion");
                                                    String tipo_alarma = documentSnapshot.getString("tipo_alarma");
                                                    String ultima_actualizacion = documentSnapshot.getString("ultima_actualizacion");
                                                     latitud = documentSnapshot.getDouble("latitud");
                                                    longitud = documentSnapshot.getDouble("longitud");

                                                    TV_descripcion.setText("Descripción:\n"+ descripcion);
                                                    TV_tipoAlarma.setText("Tipo de alarma:\n"+tipo_alarma);
                                                    TV_ultimaAct.setText("Ultima actualizaci´pon:\n"+ultima_actualizacion);
                                                    showPoint(latitud, longitud, nombre);


                                                }else{
                                                    msn("Lugar ("+uid+") no encontrado");


                                                }
                                            }
                                        });

                                /*
                                String texto = adapterView.getItemAtPosition(position).toString();
                                String tipoAlarma = adapterView.getItemAtPosition(position).
                                        TV_descripcion.setText("Algo:\n" + texto);
                                TV_tipoAlarma.setText("");
                                TV_ultimaAct.setText("");
                                System.out.println(lugaresMonitoreados);

                                 */
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                });
    }

    private void showPoint(Double latitud_, Double longitud_, String nombre_) {
        LatLng sitio = new LatLng(latitud_, longitud_);
        mMap.addMarker(new MarkerOptions().position(sitio).title(nombre_));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sitio));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }


    /*
    @Override
    public void onMapReady(GoogleMap googleMap, Double latitud, Double logitud, String nombre) {

    }

     */

    private void msn(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(latitud, longitud);
        mMap.addMarker(new MarkerOptions().position(sydney).title(nombre));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}