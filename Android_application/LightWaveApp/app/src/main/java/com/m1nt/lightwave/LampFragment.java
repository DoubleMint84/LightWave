package com.m1nt.lightwave;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rtugeek.android.colorseekbar.ColorSeekBar;



public class LampFragment extends Fragment {
    public interface onLampListener {
        public void changeColor(int red, int green, int blue);
        public void offLed();
    }
    onLampListener lampListener;

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            lampListener = (onLampListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lamp, container, false);
        final TextView textView = v.findViewById(R.id.textLamp);
        ColorSeekBar colorSeekBar = v.findViewById(R.id.colorLamp);
        Button butOff = v.findViewById(R.id.butLedOff);
        butOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lampListener.offLed();
            }
        });
        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                int A = (color >> 24) & 0xff;
                int R = (color >> 16) & 0xff;
                int G = (color >> 8) & 0xff;
                int B = (color) & 0xff;
                lampListener.changeColor(R, G, B);
                textView.setText(String.valueOf(A) + ' ' + String.valueOf(R) + ' ' + String.valueOf(G) + ' ' + String.valueOf(B));
            }
        });
        return v;
    }
}