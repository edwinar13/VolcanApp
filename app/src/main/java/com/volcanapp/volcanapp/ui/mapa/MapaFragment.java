package com.volcanapp.volcanapp.ui.mapa;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.volcanapp.volcanapp.R;
import com.volcanapp.volcanapp.RegistroLugar;
import com.volcanapp.volcanapp.modelos.FirebaseReference;
import com.volcanapp.volcanapp.modelos.LugarMonitoreado;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Spinner spinnerLugaresMonitoreados;
    private FloatingActionButton floatingButtonAddLugarMonitoreado;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private View view;

    public MapaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapaFragment newInstance(String param1, String param2) {
        MapaFragment fragment = new MapaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mapa, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        event();
    }
    private void init(){

        spinnerLugaresMonitoreados = view.findViewById(R.id.Spinner_misLugaresMonitoreados);
        floatingButtonAddLugarMonitoreado = view.findViewById(R.id.floatingActionButton_addLugarMonitoreado);
        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        loadLuagresMonitoreados();

    }
    private void loadLuagresMonitoreados(){
        List <LugarMonitoreado> lugaresMonitoreados = new ArrayList<>();
        db.collection(FirebaseReference.DB_REFERENCE_LUGARES_MONITOREADOS)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            Toast.makeText(getContext(), "FireBase error, "+ error.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        /*
                        for (DocumentChange dc : value.getDocumentChanges()){
                            if (dc.getType() == DocumentChange.Type.ADDED){

                                String id = dc.getDocument().getId();
                                String nombre = dc.getDocument().getString("nombre").toString();
                                lugaresMonitoreados.add(new LugarMonitoreado(id, nombre));
                                //lugaresMonitoreados.add(dc.getDocument().toObject(LugarMonitoreado.class));

                            }
                        }
                        ArrayAdapter <LugarMonitoreado> arrayAdapter = new ArrayAdapter<>(getContext(),
                                android.R.layout.simple_dropdown_item_1line, lugaresMonitoreados);
                        spinnerLugaresMonitoreados.setAdapter(arrayAdapter);

                         */
                    }
                });
    }
    private void event(){
        floatingButtonAddLugarMonitoreado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowRegistroLugarIU();
            }
        });

    }
    private void ShowRegistroLugarIU(){

        Intent intent = new Intent(getContext(), RegistroLugar.class);
        startActivity(intent);
    }

}