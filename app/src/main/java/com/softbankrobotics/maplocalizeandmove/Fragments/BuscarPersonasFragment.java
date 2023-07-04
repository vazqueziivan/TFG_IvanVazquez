package com.softbankrobotics.maplocalizeandmove.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aldebaran.qi.sdk.builder.ApproachHumanBuilder;
import com.aldebaran.qi.sdk.builder.GoToBuilder;
import com.aldebaran.qi.sdk.object.actuation.AttachedFrame;
import com.aldebaran.qi.sdk.object.actuation.Frame;
import com.aldebaran.qi.sdk.object.actuation.GoTo;
import com.aldebaran.qi.sdk.object.actuation.OrientationPolicy;
import com.aldebaran.qi.sdk.object.actuation.PathPlanningPolicy;
import com.aldebaran.qi.sdk.object.geometry.Transform;
import com.aldebaran.qi.sdk.object.human.Human;
import com.aldebaran.qi.sdk.object.humanawareness.ApproachHuman;
import com.aldebaran.qi.sdk.object.humanawareness.HumanAwareness;
import com.softbankrobotics.maplocalizeandmove.MainActivity;
import com.softbankrobotics.maplocalizeandmove.R;
import com.softbankrobotics.maplocalizeandmove.Utils.GoToHelper;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class BuscarPersonasFragment extends Fragment {
    private static final String TAG = "MSI_MainFragment";
    private MainActivity ma;
    private String randomLocation;
    private String randomLocation_ant;
    private Future<Void> seguir = null;



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        int fragmentId = R.layout.fragment_buscar_personas;
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
        // Volver a la pantalla de inicio al pulsar el botón "Cancelar"
        view.findViewById(R.id.button_cancelar).setOnClickListener(
                (v) -> {
                    ma.robotHelper.goToHelper.CancelCurrentGoto();
                    ma.robotHelper.goToHelper.removeOnFinishedMovingListeners();
                    ma.setFragment(new PantallaInicioFragment(), false);
                });

        // Cargar en la memoria los puntos de interés
        if (ma.savedLocations.isEmpty()) {
            try {
                ma.loadLocations().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        if (seguir != null) {
            seguir.cancel(true);
        }

        BuscarPersonas();

        // Ir a una posición aleatoria de los puntos de interés
        randomLocation = ma.pickRandomLocation();
        while (randomLocation == "BaseCarga") {
            randomLocation = ma.pickRandomLocation();
        }
        ma.goToLocation(randomLocation, OrientationPolicy.ALIGN_X, this);
        ma.robotHelper.goToHelper.addOnFinishedMovingListener((goToStatus) -> {
            if (goToStatus == GoToHelper.GoToStatus.FINISHED && ma.currentFragment.equals("BuscarPersonasFragment")) {
                for (long i = 0; i < 99999999; i++) {

                }
                randomLocation_ant = randomLocation;
                while (randomLocation == randomLocation_ant || randomLocation == "BaseCarga") {
                    randomLocation = ma.pickRandomLocation();
                }
                ma.goToLocation(randomLocation, OrientationPolicy.ALIGN_X, this);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getFragmentManager().beginTransaction().remove(ma.getFragment()).commitAllowingStateLoss();
    }

    private void BuscarPersonas() {
        try {
            HumanAwareness humanAwareness = ma.qiContext.getHumanAwareness();
            Future<Human> engagedHuman = humanAwareness.async().getEngagedHuman();

            // Cuando encuentra a una persona se acerca a ella y abre SaludarPersonaFragment() para saludarla
            humanAwareness.addOnEngagedHumanChangedListener(human -> {
                if(human != null) {
                    ma.human = human;
                    ma.robotHelper.goToHelper.CancelCurrentGoto();
                    seguir = ma.robotHelper.mirarPersona(human);
                    Frame position = human.getHeadFrame();
                    GoTo goTo = GoToBuilder.with(ma.qiContext)
                            .withFrame(position)
                            .withPathPlanningPolicy(PathPlanningPolicy.STRAIGHT_LINES_ONLY)
                            .build();
                    goTo.async().run().thenConsume(future -> {
                        if (future.isDone()) {
                            goTo.removeAllOnStartedListeners();
                            engagedHuman.cancel(true);
                            humanAwareness.removeAllOnEngagedHumanChangedListeners();
                            ma.robotHelper.goToHelper.CancelCurrentGoto();
                            ma.robotHelper.goToHelper.removeOnFinishedMovingListeners();
                            seguir.cancel(true);
                            ma.setFragment(new SaludarPersonaFragment(), false);

                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
