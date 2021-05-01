package com.m1nt.lightwave;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class AlarmsFragment extends Fragment {
    private final Button[] setTimeBut = new Button[5];
    private final SwitchCompat[] stateAl = new SwitchCompat[5];



    public interface onAlarmListener {
        public String getTime(int num);
        public boolean getState(int num);
        public void changeTime(int num);
        public void changeState(int num);
    }
    onAlarmListener alarmListener;

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            alarmListener = (onAlarmListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_alarms, container, false);
        setTimeBut[0] = v.findViewById(R.id.butAlFirst);
        setTimeBut[1] = v.findViewById(R.id.butAlSecond);
        setTimeBut[2] = v.findViewById(R.id.butAlThird);
        setTimeBut[3] = v.findViewById(R.id.butAlForth);
        setTimeBut[4] = v.findViewById(R.id.butAlFifth);
        stateAl[0] = v.findViewById(R.id.swAl1);
        stateAl[1] = v.findViewById(R.id.swAl2);
        stateAl[2] = v.findViewById(R.id.swAl3);
        stateAl[3] = v.findViewById(R.id.swAl4);
        stateAl[4] = v.findViewById(R.id.swAl5);
        for (int i = 0; i < 5; i++) {
            setTimeBut[i].setText(alarmListener.getTime(i));
            stateAl[i].setChecked(alarmListener.getState(i));
            final int finalI = i;
            stateAl[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    alarmListener.changeState(finalI);
                }
            });
            setTimeBut[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alarmListener.changeTime(finalI);
                    setTimeBut[finalI].setText(alarmListener.getTime(finalI));
                }
            });
        }
        return v;
    }
}
