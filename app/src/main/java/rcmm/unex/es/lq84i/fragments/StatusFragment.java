package rcmm.unex.es.lq84i.fragments;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import rcmm.unex.es.lq84i.R;
import rcmm.unex.es.lq84i.activities.MainActivity;
import rcmm.unex.es.lq84i.viewmodels.StatusViewModel;
import rcmm.unex.es.lq84i.viewmodels.factories.StatusViewModelFactory;

public class StatusFragment extends Fragment {

    /**
     * ViewModel del fragmento
     */
    private StatusViewModel viewModel;

    /**
     * Vista que contiene los TextViews de los datos
     */
    private View dataHolder;

    /**
     * Actividad anfitriona
     */
    private static Activity mHost = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHost = getActivity();
        PreferenceManager.setDefaultValues(mHost, R.xml.preferences, false);
        String pref = PreferenceManager.getDefaultSharedPreferences(mHost).getString(PreferenceFrag.KEY_LISTENER_PREFERENCE, "time");
        boolean time = pref.equals("time");
        Integer UPDATE_TIME = PreferenceManager.getDefaultSharedPreferences(mHost).getInt("UPDATE_TIME", 1);
        Log.i("datosquequierover", "El valor de la preferencia es " + UPDATE_TIME + " y la otra cosa que hace juanlu es " + pref.equals("UPDATE_TIME"));
        StatusViewModelFactory factory = new StatusViewModelFactory((TelephonyManager)
                Objects.requireNonNull(mHost).getSystemService(Context.TELEPHONY_SERVICE),
                (LocationManager)
                        Objects.requireNonNull(mHost.getSystemService(Context.LOCATION_SERVICE)), time, UPDATE_TIME, mHost);
        viewModel = ViewModelProviders.of(this, factory).get(StatusViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View res = inflater.inflate(R.layout.status_info, container, false);
        dataHolder = res.findViewById(R.id.data);
        Button b = res.findViewById(R.id.button);
        Button s = res.findViewById(R.id.calculate_speed);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI();
            }
        });
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) mHost).changeToMeasuresFragment();
            }
        });
        viewModel.startListeners((TextView) res.findViewById(R.id.call_state),
                (TextView) res.findViewById(R.id.conextion_state),
                (TextView) res.findViewById(R.id.service_state),
                (TextView) res.findViewById(R.id.cell_location),
                (TextView) res.findViewById(R.id.gps_location),
                (TextView) res.findViewById(R.id.signal_level),
                (ImageView) res.findViewById(R.id.signal_level_img),
                (TextView) res.findViewById(R.id.phone_data),
                dataHolder, mHost.getResources());
        return res;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateUI();
    }

    public void newPreferenceTimeValue(String value) {
        viewModel.newPreferenceTimeValue(value);
    }

    public void newPreferenceDistanceValue(String value) {
        viewModel.newPreferenceDistanceValue(value);
    }

    private void updateUI() {
        viewModel.updateView(dataHolder, mHost.getResources());
    }
}
