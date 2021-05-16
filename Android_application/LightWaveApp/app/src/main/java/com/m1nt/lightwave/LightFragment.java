package com.m1nt.lightwave;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.rtugeek.android.colorseekbar.ColorSeekBar;



public class LightFragment extends Fragment {
    private static final String TAG = "Light frag";
    public interface onLampListener {
        public void changeColor(int red, int green, int blue);
        public void changeBrightness(int bright);
        public void offLed();
        public void changeBreathModeState(boolean state);
        public void changeEffect(int num);
        public void changeSpeed(int speed);
        public void changeParam(int param);
    }
    public String[] effects = { "Nope", "Random", "Rainbow", "Color Cycle", "Running dots", "Twinkle", "Strobe", "Scanner", "Running lights", "Theatre chase" };
    public int pick = 0, curSpeed = 50, curParam = 50;
    onLampListener lampListener;
    private SwitchCompat breathSwitch;
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
        View v = inflater.inflate(R.layout.fragment_light, container, false);
        Log.d(TAG, "START LIGHT FRAGMENT");
        final TextView textView = v.findViewById(R.id.textLamp);
        SeekBar seekBarBright = v.findViewById(R.id.seekBarBright);
        SeekBar seekBarSpeed = v.findViewById(R.id.seekBarSpeed);
        SeekBar seekBarParam = v.findViewById(R.id.seekBarParam);
        TextView editTextBright = v.findViewById(R.id.textBright);
        ColorSeekBar colorSeekBar = v.findViewById(R.id.colorLamp);
        Button butOff = v.findViewById(R.id.butLedOff);
        Button butLamp = v.findViewById(R.id.butLamp);
        breathSwitch = v.findViewById(R.id.breathBtn);
        butOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lampListener.offLed();
            }
        });
        butLamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lampListener.changeEffect(-2);
                lampListener.changeSpeed(curSpeed);
                lampListener.changeParam(curParam);
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
                textView.setText(String.valueOf(R) + ' ' + String.valueOf(G) + ' ' + String.valueOf(B));
            }
        });
        breathSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lampListener.changeBreathModeState(isChecked);
            }
        });
        seekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                curSpeed = progress;
                lampListener.changeSpeed(curSpeed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarParam.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                curParam = progress;
                lampListener.changeParam(curParam);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarBright.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editTextBright.setText(String.valueOf(progress));
                lampListener.changeBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Spinner spinner = v.findViewById(R.id.spinnerEffects);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_spinner_item, effects);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinner.setAdapter(adapter);
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "SELECTED " + position);
                // Получаем выбранный объект
                pick = position - 1;
                lampListener.changeEffect(pick);
                lampListener.changeSpeed(curSpeed);
                lampListener.changeParam(curParam);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);
        return v;
    }
}