package org.ultimatetoolsil.trackeru;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import java.util.Locale;

/**
 * Created by Mike on 11/10/2018.
 */

public class ShowcaseFragment extends Fragment {
   ImageView window;
   CheckBox skip;
   Button cancel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fabs_layout3,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        window=(ImageView)view.findViewById(R.id.imageView);
        skip=(CheckBox)view.findViewById(R.id.ignore);
        cancel=(Button)view.findViewById(R.id.button4);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });
        if( Locale.getDefault().getLanguage().equals("iw")) {
           window.setImageResource(R.drawable.asd);
        }else{
            window.setImageResource(R.drawable.en2);
        }
        Log.d("locale",Locale.getDefault().getLanguage());
        skip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("intro", "on").apply();
            }
        });
    }
}
