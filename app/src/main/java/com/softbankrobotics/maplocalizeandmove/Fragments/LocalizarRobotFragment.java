package com.softbankrobotics.maplocalizeandmove.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.aldebaran.qi.sdk.object.actuation.OrientationPolicy;
import com.aldebaran.qi.sdk.util.FutureUtils;
import com.softbankrobotics.maplocalizeandmove.MainActivity;
import com.softbankrobotics.maplocalizeandmove.R;
import com.softbankrobotics.maplocalizeandmove.Utils.GoToHelper;
import com.softbankrobotics.maplocalizeandmove.Utils.LocalizeAndMapHelper;
import com.softbankrobotics.maplocalizeandmove.Utils.Popup;

import java.util.concurrent.TimeUnit;

public class LocalizarRobotFragment extends Fragment {

    private static final String TAG = "MSI_LocalizeRobot";
    private MainActivity ma;
    private Popup localizedPopup;

    /**
     * Inflates the layout associated with this fragment
     * If an application theme is set, it will be applied to this fragment.
     */

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        int fragmentId = R.layout.fragment_localize_robot;
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
        LocalizarRobotFragment currentFragment = (LocalizarRobotFragment) ma.getFragment();
        Button back_button = view.findViewById(R.id.back_button);
        back_button.setOnClickListener((v) -> {
            ma.stopLocalizing();
            ma.setFragment(new HumanoEncontradoFragment(), true);
            ma.robotHelper.localizeAndMapHelper.removeOnFinishedLocalizingListeners();

        });
        Button stopLocalize = view.findViewById(R.id.button_stop_save);

        Button retry_Button = view.findViewById(R.id.button_retry);
        LottieAnimationView localizing_at_home = view.findViewById(R.id.localizing_at_home);
        ImageView localizing_error = view.findViewById(R.id.localizing_error);

        stopLocalize.setOnClickListener((v) -> {
            ma.stopLocalizing();
            stopLocalize.setVisibility(View.GONE);
            retry_Button.setVisibility(View.VISIBLE);
            localizing_at_home.setVisibility(View.GONE);
            localizing_error.setVisibility(View.VISIBLE);
        });

        retry_Button.setOnClickListener((v) -> {
            ma.startLocalizing();
            retry_Button.setVisibility(View.GONE);
            localizing_error.setVisibility(View.GONE);
            stopLocalize.setVisibility(View.VISIBLE);
            localizing_at_home.setVisibility(View.VISIBLE);
        });

        localizedPopup = new Popup(R.layout.popup_localized, this, ma);
        Button close_button = localizedPopup.inflator.findViewById(R.id.close_button);
        close_button.setOnClickListener((v) -> {
            localizedPopup.dialog.hide();
            ma.setFragment(new HumanoEncontradoFragment(), false);
        });

        ma.robotHelper.localizeAndMapHelper.addOnFinishedLocalizingListener(result -> {
            ma.robotIsLocalized.set(result == LocalizeAndMapHelper.LocalizationStatus.LOCALIZED);
            ma.robotHelper.releaseAbilities();
            ma.runOnUiThread(() -> {
                if (result == LocalizeAndMapHelper.LocalizationStatus.LOCALIZED) {
                    ma.robotLocalizedOnce = true;
                    localizedPopup.dialog.show();
                    localizedPopup.dialog.getWindow().setAttributes(localizedPopup.lp);
                    FutureUtils.wait(4, TimeUnit.SECONDS)
                            .thenConsume(aUselessFutureB -> ma.runOnUiThread(() -> {
                                if (currentFragment.isVisible()) {
                                    localizedPopup.dialog.hide();
                                    if (ma.destino != null) {
                                        ma.goToLocation(ma.destino, OrientationPolicy.ALIGN_X, this);
                                        ma.robotHelper.goToHelper.addOnFinishedMovingListener((goToStatus) -> {
                                            if (goToStatus == GoToHelper.GoToStatus.FINISHED) {
                                                ma.destino = null;
                                                ma.setFragment(new HumanoEncontradoFragment(), false);
                                                localizedPopup.dialog.hide();
                                            }
                                        });
                                    } else {
                                        ma.setFragment(new HumanoEncontradoFragment(), false);
                                        localizedPopup.dialog.hide();
                                    }
                                }
                            }));
                    ma.robotHelper.localizeAndMapHelper.removeOnFinishedLocalizingListeners();
                } else if (result == LocalizeAndMapHelper.LocalizationStatus.MAP_MISSING) {
                    noMapToLoad();
                    ma.robotHelper.localizeAndMapHelper.removeOnFinishedLocalizingListeners();
                } else if (result == LocalizeAndMapHelper.LocalizationStatus.FAILED) {
                    stopLocalize.setVisibility(View.GONE);
                    localizing_at_home.setVisibility(View.GONE);
                    localizing_error.setVisibility(View.VISIBLE);
                    retry_Button.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "onViewCreated: Unable to localize in Map");
                }
            });
        });
        ma.startLocalizing();
    }


    /**
     * If there is no map to load from memory, display a popup to inform the user.
     */
    private void noMapToLoad() {
        Popup noMapToLoad = new Popup(R.layout.popup_no_map_to_load, this, ma);
        Button close = noMapToLoad.inflator.findViewById(R.id.close_button);
        close.setOnClickListener((v) -> {
            noMapToLoad.dialog.hide();
            ma.setFragment(new ConfiguracionFragment(), true);
        });
        noMapToLoad.dialog.show();
    }

}