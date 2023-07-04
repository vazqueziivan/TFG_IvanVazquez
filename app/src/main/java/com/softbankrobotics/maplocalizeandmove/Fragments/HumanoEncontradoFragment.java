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
import com.aldebaran.qi.sdk.object.actuation.OrientationPolicy;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.softbankrobotics.maplocalizeandmove.MainActivity;
import com.softbankrobotics.maplocalizeandmove.R;
import com.softbankrobotics.maplocalizeandmove.Utils.GoToHelper;

import java.util.concurrent.ExecutionException;

public class HumanoEncontradoFragment extends Fragment {
    private static final String TAG = "MSI_MainFragment";
    private MainActivity ma;
    Future<ListenResult> listen = null;



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        int fragmentId = R.layout.fragment_humano_encontrado;
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

        // Abre la opción de chat si se pulsa el botón "Chat"
        view.findViewById(R.id.button_chat).setOnClickListener(
                (v) -> {
                    listen.cancel(true);
                    ma.setFragment(new ChatRobotFragment(), false);
                });

        // Vuelve al fragmento de buscar personas si se pulsa el botón "Cancelar"
        view.findViewById(R.id.button_cancelar).setOnClickListener(
                (v) -> {
                    listen.cancel(true);
                    ma.robotHelper.say("Hasta luego").thenConsume(a -> {
                        if (a.isDone()) {
                            ma.setFragment(new BuscarPersonasFragment(), false);
                        }
                    });
                });

        // Carga los puntos de interés en la memoria
        if (ma.savedLocations.isEmpty()) {
            try {
                ma.loadLocations().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Escucha
        listen = ma.frases.async().run();

        listen.thenConsume(v -> {
            if (v.isDone()) {
                String frase = v.getValue().getHeardPhrase().getText().toString();
                Log.i("FRASE", frase);

                //Se ubica
                if (frase.equals("Donde estamos")) {
                    if (!ma.currentFragment.equalsIgnoreCase("LocalizeRobotFragment")) {
                        //ma.robotHelper.say("Voy a ver dónde estamos");
                        ma.setFragment(new LocalizarRobotFragment(), false);
                    }
                // Te sigue
                } else if (frase.equals("Sigueme")) {
                    listen.cancel(true);
                    ma.human = null;
                    ma.setFragment(new SeguirPersonaFragment(), false);
                // Abre el chat
                } else if (frase.equals("Chat")) {
                    listen.cancel(true);
                    ma.setFragment(new ChatRobotFragment(), false);
                // Te dice si te has tomado o no la pastilla
                } else if (frase.equals("Me he tomado la pastilla")) {
                    if (!ma.pastilla_b) {
                        ma.robotHelper.say("No te la has tomado").thenConsume(a -> {
                            if (v.isDone()) {
                                ma.setFragment(new HumanoEncontradoFragment(), false);
                            }
                        });
                    } else {
                        ma.robotHelper.say("Sí").thenConsume(a -> {
                            if (v.isDone()) {
                                ma.setFragment(new HumanoEncontradoFragment(), false);
                            }
                        });
                    }
                } else if (frase.equals("Tengo hambre")) {
//                    dt = new Date();
//                    date = dt.toString();
//                    date = date.substring(11, 13);
//                    hora = Integer.parseInt(date);
                    if (!ma.comido_b) {
                        ma.robotHelper.say("Normal, aún no has comido nada").thenConsume(a -> {
                            if (a.isDone()) {
                                ma.setFragment(new HumanoEncontradoFragment(), false);
                            }
                        });
                    } //else if (hora - ma.comida <= 3) {
//                        ma.robotHelper.say("No comas más, comilón").thenConsume(a -> {
//                            if (v.isDone()) {
//                                ma.setFragment(new HumanoEncontradoFragment(), false);
//                            }
//                        });
                    //}
                    else {
                        ma.robotHelper.say("No comas más, comilón").thenConsume(a -> {
                            if (a.isDone()) {
                                ma.setFragment(new HumanoEncontradoFragment(), false);
                            }
                        });
                    }

                } else if (frase.equals("Deberia ducharme")) {
//                    dt = new Date();
//                    date = dt.toString();
//                    date = date.substring(11, 13);
//                    hora = Integer.parseInt(date);
                    if(!ma.duchado_b) {
                        ma.robotHelper.say("Sí, hueles un poco mal, marrano").thenConsume(a -> {
                            if (a.isDone()) {
                                ma.setFragment(new HumanoEncontradoFragment(), false);
                            }
                        });
                    } else {
                        ma.robotHelper.say("No es necesario, todavía hueles a rosas").thenConsume(a -> {
                            if (a.isDone()) {
                                ma.setFragment(new HumanoEncontradoFragment(), false);
                            }
                        });
                    }
                // Vuelve a buscar personas si te despides de Pepper
                } else if (frase.equals("Adios")) {
                    listen.cancel(true);
                    ma.robotHelper.say("Hasta luego").thenConsume(a -> {
                        if (a.isDone()) {
                            ma.setFragment(new BuscarPersonasFragment(), false);
                        }
                    });
                // Se mueve hasta donde le indiques al decir "Vamos ..."
                } else {
//                        Log.i("PRUEBA", v.getValue().getMatchedPhraseSet().toString());
//                        Log.i("PRUEBA", v.getValue().toString());
//                        Log.i("PRUEBA", v.getValue().getHeardPhrase().toString());
//                        Log.i("PRUEBA", v.getValue().getHeardPhrase().getText().toString());
                        String lugar = frase.substring(6).toString();
//                        Log.i("PRUEBA", lugar);
//                        Log.i("PRUEBA", "DESPACHO");
                        if (!ma.robotHelper.askToCloseIfFlapIsOpened()) {
                            if (!ma.robotIsLocalized.get()) {
                                if (!ma.currentFragment.equalsIgnoreCase("LocalizeRobotFragment")) {
                                    ma.destino = lugar;
                                    ma.setFragment(new LocalizarRobotFragment(), false);
                                }
                            } else {
                                ma.goToLocation(lugar, OrientationPolicy.ALIGN_X, this);
                                ma.robotHelper.goToHelper.addOnFinishedMovingListener((goToStatus) -> {
                                    if (goToStatus == GoToHelper.GoToStatus.FINISHED) {
                                        ma.destino = null;
                                        ma.robotHelper.goToHelper.removeOnFinishedMovingListeners();
                                        ma.setFragment(new HumanoEncontradoFragment(), false);
                                    }
                                });
                            }
                        }
                    }
            }
        });
    }
}

