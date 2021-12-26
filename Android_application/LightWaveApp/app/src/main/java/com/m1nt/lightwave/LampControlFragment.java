package com.m1nt.lightwave;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.flag.BubbleFlag;
import com.skydoves.colorpickerview.flag.FlagMode;

public class LampControlFragment extends Fragment {
    public interface onLampListener {

    }

    onLampListener lampListener;

    private ColorPickerView colorPickerView;
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
        View v = inflater.inflate(R.layout.fragment_lamp_control, container, false);
        colorPickerView = v.findViewById(R.id.colorPickerLamp);
        BubbleFlag bubbleFlag = new BubbleFlag(getActivity());
        bubbleFlag.setFlagMode(FlagMode.FADE);
        colorPickerView.setFlagView(bubbleFlag);
        return v;

    }
}
