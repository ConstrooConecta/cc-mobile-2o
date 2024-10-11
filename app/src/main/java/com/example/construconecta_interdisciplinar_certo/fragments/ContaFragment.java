package com.example.construconecta_interdisciplinar_certo.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.databinding.FragmentContaBinding;
import com.example.construconecta_interdisciplinar_certo.ui.InternetErrorActivity;
import com.example.construconecta_interdisciplinar_certo.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ContaFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    // Variável para o binding
    private FragmentContaBinding binding;

    public ContaFragment() {
        // Required empty public constructor
    }

    public static ContaFragment newInstance(String param1, String param2) {
        ContaFragment fragment = new ContaFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inicializa o binding
        binding = FragmentContaBinding.inflate(inflater, container, false);

        binding.logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        // Retorna a root view através do binding
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isConnectedToInternet()) {
            Intent intent = new Intent(getActivity(), InternetErrorActivity.class);
            startActivity(intent);
        }
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Limpa o binding quando a view for destruída
        binding = null;
    }
}
