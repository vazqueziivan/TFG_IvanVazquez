package com.softbankrobotics.maplocalizeandmove.Fragments;

//import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aldebaran.qi.sdk.builder.ApproachHumanBuilder;
import com.aldebaran.qi.sdk.builder.GoToBuilder;
import com.aldebaran.qi.sdk.object.actuation.Frame;
import com.aldebaran.qi.sdk.object.actuation.GoTo;
import com.aldebaran.qi.sdk.object.actuation.OrientationPolicy;
import com.aldebaran.qi.sdk.object.actuation.PathPlanningPolicy;
import com.aldebaran.qi.sdk.object.human.Human;
import com.aldebaran.qi.sdk.object.humanawareness.ApproachHuman;
import com.aldebaran.qi.sdk.object.humanawareness.HumanAwareness;
import com.softbankrobotics.maplocalizeandmove.MainActivity;
import com.softbankrobotics.maplocalizeandmove.R;
import com.softbankrobotics.maplocalizeandmove.Utils.GoToHelper;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SeguirPersonaFragment extends Fragment {
    private static final String TAG = "MSI_MainFragment";
    private MainActivity ma;
    private Future<Void> fgoTo;
    private GoTo goTo;
    private Frame position;
    private boolean seguir = true;
    private Future<Void> seguir_persona;



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        int fragmentId = R.layout.fragment_seguir_persona;
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

        view.findViewById(R.id.button_cancelar).setOnClickListener(
                (v) -> {
                    goTo.removeAllOnStartedListeners();
                    seguir_persona.cancel(true);
                    ma.setFragment(new HumanoEncontradoFragment(), false);
                });

        // Seguir a la persona
        HumanAwareness humanAwareness = ma.qiContext.getHumanAwareness();

        try {
            Log.i("COSITAS", "Buscando");
            humanAwareness.addOnEngagedHumanChangedListener(human -> {
                Log.i("COSITAS", "addOnEngagedHumanChangedListener");
                if(human != null) {
                    Log.i("COSITAS", "addOnEngagedHumanChangedListener human != null");
                    seguir_persona = ma.robotHelper.mirarPersona(human);
                    position = human.getHeadFrame();
                    goTo = GoToBuilder.with(ma.qiContext).withFrame(position).build();
                    goTo.async().run();
                }
            });

//            Future<Human> engagedHuman = humanAwareness.async().getEngagedHuman();
        } catch (Exception e) {
            e.printStackTrace();
            Date dt = new Date();
            Log.d("Cositas", (dt.toString()) + " --- " + e.toString());
        }
    }
}
