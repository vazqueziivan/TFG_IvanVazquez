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

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.softbankrobotics.maplocalizeandmove.MainActivity;
import com.softbankrobotics.maplocalizeandmove.R;

public class PastillaFragment extends Fragment {
    private static final String TAG = "MSI_MainFragment";
    private MainActivity ma;
//    private Date dt;
//    private String date;
//    private int hora;
    Future<ListenResult> listen = null;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        int fragmentId = R.layout.fragment_pastilla;
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

        // Si se pulsa el botón "Cancelar" vuelve a buscar personas
        view.findViewById(R.id.button_cancelar).setOnClickListener(
                (v) -> {
                    listen.cancel(true);
                    ma.setFragment(new BuscarPersonasFragment(), false);
                });

        // Escucha
        listen = ma.frases.async().run();

        // Cambia el valor del booleano para saber si te has tomado la pastilla en función de la respuesta
        listen.thenConsume(v -> {
            if (v.isDone()) {
                String frase = v.getValue().getHeardPhrase().getText().toString();
                Log.i("FRASE", frase);
                if (frase.equals("Si")) {
                    ma.robotHelper.say("Así te mantendrás sano").thenConsume(a -> {
                        ma.pastilla_b = true;
                        listen.cancel(true);
                        ma.setFragment(new HumanoEncontradoFragment(), false);
                    });
                } else if (frase.equals("No")) {
                    ma.robotHelper.say("Recuerda tomártela").thenConsume(a -> {
                        ma.pastilla_b = false;
                        listen.cancel(true);
                        ma.setFragment(new HumanoEncontradoFragment(), false);
                    });
                }
            }
        });
    }
}

