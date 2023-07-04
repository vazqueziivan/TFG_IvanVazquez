package com.softbankrobotics.maplocalizeandmove.Fragments;

//import android.app.Fragment;
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

public class InformacionFragment extends Fragment {
    private static final String TAG = "MSI_MainFragment";
    private MainActivity ma;
//    private boolean irAleatorio = true;
//    private String randomLocation;
//    private String randomLocation_ant;
//    private Frame position;
//    private Spinner spinner_pastilla;
//    private Spinner spinner_ducha;
//    private Spinner spinner_comida;
//    private String hora_pastilla;
//    private String hora_comida;
//    private String hora_ducha;
    private int aleatorio;
//    private Future<ListenResult> listen;

    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*ImageView tv_fondo = (ImageView) ma.findViewById(R.id.logo_uib);
        TextView tv_info = (TextView) ma.findViewById(R.id.txt_info);
        TextView tv_pastilla = (TextView) ma.findViewById(R.id.txt_pastilla);
        spinner = (Spinner) spinner.findViewById(R.id.spinner_pastilla);

        String[] respuestas = {"16:00 - 17:00", "17:00 - 18:00", "18:00 - 19:00"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ma.qiContext, android.R.layout.simple_spinner_item, respuestas);
        spinner.setAdapter(adapter);*/


        int fragmentId = R.layout.fragment_informacion;
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

        // Te realiza una pregunta aleatoria sobre una acción que no hayas realizado para recordártelo
        if ((ma.comido_b && ma.pastilla_b && ma.duchado_b)) {
            ma.setFragment(new HumanoEncontradoFragment(), false);
        } else {
            ma.construyeListener();

            aleatorio = (int) (Math.random()*3) + 1;
            while ((ma.comido_b && aleatorio == 2) || (ma.pastilla_b && aleatorio == 1) || (ma.duchado_b && aleatorio == 3)) {
                aleatorio = (int) (Math.random()*3) + 1;
            }

            // Te pregunta si te has tomado la pastilla
            if (aleatorio == 1) {
                ma.robotHelper.say("¿Te has tomado la pastilla?").thenConsume( v -> ma.setFragment(new PastillaFragment(), false));
            // Te pregunta si has comido
            } else if (aleatorio == 2) {
                ma.robotHelper.say("¿Has comido?").thenConsume( v -> ma.setFragment(new ComidoFragment(), false));
            // Te pregunta si te has duchado
            } else if (aleatorio == 3) {
                ma.robotHelper.say("¿Te has duchado?").thenConsume( v -> ma.setFragment(new  DuchadoFragment(), false));
            }
        }

//        ma.robotHelper.say("Introuce la siguiente información, por favor");
//        spinner_pastilla = view.findViewById(R.id.spinner_pastilla);
//        spinner_comida = view.findViewById(R.id.spinner_comida);
//        spinner_ducha = view.findViewById(R.id.spinner_ducha);
//        String[] respuestas_pastilla;
//        String[] respuestas_comida;
//        String[] respuestas_ducha;
//        String horas_pastilla[] = new String[24];
//        String horas_comida[] = new String[24];
//        String horas_ducha[] = new String[24];
//        for (int i = 1; i < 24; i++) {
//            if (i < 9) {
//                horas_pastilla[i] = "0" + i + ":00 - 0" + (i+1) + ":00";
//                horas_comida[i] = "0" + i + ":00 - 0" + (i+1) + ":00";
//                horas_ducha[i] = "0" + i + ":00 - 0" + (i+1) + ":00";
//            } else if (i == 9) {
//                horas_pastilla[i] = "0" + i + ":00 - " + (i+1) + ":00";
//                horas_comida[i] = "0" + i + ":00 - " + (i+1) + ":00";
//                horas_ducha[i] = "0" + i + ":00 - " + (i+1) + ":00";
//            } else if (i == 23) {
//                horas_pastilla[i] = i + ":00 - 00:00";
//                horas_comida[i] = i + ":00 - 00:00";
//                horas_ducha[i] = i + ":00 - 00:00";
//            } else {
//                horas_pastilla[i] = i + ":00 - " + (i+1) + ":00";
//                horas_comida[i] = i + ":00 - " + (i+1) + ":00";
//                horas_ducha[i] = i + ":00 - " + (i+1) + ":00";
//            }
//        }
//        horas_pastilla[0] = "Hoy no me he tomado la pastilla";
//        respuestas_pastilla = horas_pastilla;
//        horas_comida[0] = "Aún no he comido nada";
//        respuestas_comida = horas_comida;
//        horas_ducha[0] = "Hoy no me he duchado";
//        respuestas_ducha = horas_ducha;
//
//        ArrayAdapter<String> adapter_pastilla = new ArrayAdapter<String>(ma.qiContext, android.R.layout.simple_spinner_item, respuestas_pastilla);
//        ArrayAdapter<String> adapter_comida = new ArrayAdapter<String>(ma.qiContext, android.R.layout.simple_spinner_item, respuestas_comida);
//        ArrayAdapter<String> adapter_ducha = new ArrayAdapter<String>(ma.qiContext, android.R.layout.simple_spinner_item, respuestas_ducha);
//        spinner_pastilla.setAdapter(adapter_pastilla);
//        spinner_comida.setAdapter(adapter_comida);
//        spinner_ducha.setAdapter(adapter_ducha);
//
//        view.findViewById(R.id.button_aceptar).setOnClickListener(
//                (v) -> {
//                    hora_pastilla = spinner_pastilla.getSelectedItem().toString();
//                    hora_comida = spinner_comida.getSelectedItem().toString();
//                    hora_ducha = spinner_ducha.getSelectedItem().toString();
//                    Log.i("COSITAS", "Te has tomado la pastilla a las " + hora_pastilla);
//                    Log.i("COSITAS", "Has comido a las " + hora_comida);
//                    Log.i("COSITAS", "Te has duchado a las " + hora_ducha);
//                    if(!hora_pastilla.equals(horas_pastilla[0])) {
//                        hora_pastilla = hora_pastilla.substring(0, 2);
//                        ma.pastilla = Integer.parseInt(hora_pastilla);
//                    } else {
//                        ma.pastilla = 25;
//                    }
//                    if(!hora_comida.equals(horas_comida[0])) {
//                        hora_comida = hora_comida.substring(0, 2);
//                        ma.comida = Integer.parseInt(hora_comida);
//                    } else {
//                        ma.comida = 25;
//                    }
//                    if(!hora_ducha.equals(horas_ducha[0])) {
//                        hora_ducha = hora_ducha.substring(0, 2);
//                        ma.ducha = Integer.parseInt(hora_ducha);
//                    } else {
//                        ma.ducha = 25;
//                    }
//                    Log.i("COSITAS", "Te has tomado la pastilla a las " + hora_pastilla);
//                    Log.i("COSITAS", "Has comido a las " + hora_comida);
//                    Log.i("COSITAS", "Te has duchado a las " + hora_ducha);
//                    Log.i("COSITAS", Integer.toString(ma.pastilla));
//                    Log.i("COSITAS", Integer.toString(ma.comida));
//                    Log.i("COSITAS", Integer.toString(ma.ducha));
//
//                    ma.setFragment(new HumanoEncontradoFragment(), false);
//
//                });
//
//        view.findViewById(R.id.button_cancelar).setOnClickListener(
//                (v) -> ma.setFragment(new BuscarPersonasFragment(), false));
    }
}
