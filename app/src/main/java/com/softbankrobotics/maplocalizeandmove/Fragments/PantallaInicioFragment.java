package com.softbankrobotics.maplocalizeandmove.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.softbankrobotics.maplocalizeandmove.MainActivity;
import com.softbankrobotics.maplocalizeandmove.R;

public class PantallaInicioFragment extends Fragment{
    private static final String TAG = "MSI_MainFragment";
    private MainActivity ma;



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        int fragmentId = R.layout.fragment_pantalla_inicio;
        this.ma = (MainActivity) getActivity();
        if (ma != null) {
            Integer themeId = ma.getThemeId();
            if (themeId != null) {
                final Context contextThemeWrapper = new ContextThemeWrapper(ma, themeId);
                LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
                return localInflater.inflate(fragmentId, container, false);
            } else {
                return inflater.inflate(fragmentId, container, false);
            }
        } else {
            Log.d(TAG, "could not get mainActivity, can't create fragment");
            return null;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Si se pulsa el botón "Comenzar" empezará a funcionar en su modo normal para relacionarse con personas
        view.findViewById(R.id.button_comenzar).setOnClickListener(
                (v) -> {
                    if (!ma.robotHelper.askToCloseIfFlapIsOpened()) {
                        ma.setFragment(new LocalizarRobotInicioFragment(), false);
                    }
                });

        // Si se pulsa el botón "Ajustes" se va al apartado donde se pueden hacer pruebas y escanear el entorno
        view.findViewById(R.id.button_ajustes).setOnClickListener(
                (v) -> ma.setFragment(new AjustesFragment(), false));
    }
}
