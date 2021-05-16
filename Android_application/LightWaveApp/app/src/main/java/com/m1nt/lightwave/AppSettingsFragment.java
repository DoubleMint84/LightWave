package com.m1nt.lightwave;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

public class AppSettingsFragment extends Fragment {
    private final String TAG = "App settings frag";
    public interface onSettingsListener {
        void sendCommand(String command);
        void showAuthors();
    }
    onSettingsListener settingsListener;

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            settingsListener = (onSettingsListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_app_settings, container, false);
        EditText editCommand = v.findViewById(R.id.editCommand);
        Button butCommand = v.findViewById(R.id.buttonSend);
        Button butSyncTime = v.findViewById(R.id.buttonSyncTime);
        Button butGithub = v.findViewById(R.id.buttonGithub);
        Button butAuthors = v.findViewById(R.id.buttonAuthors);
        butCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = editCommand.getText().toString();
                settingsListener.sendCommand(tmp.trim());
                editCommand.setText("");
            }
        });
        butSyncTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Текущее время
                Date currentDate = new Date();
// Форматирование времени как "день.месяц.год"
                DateFormat dateFormat = new SimpleDateFormat("d M y", Locale.getDefault());
                String dateText = dateFormat.format(currentDate);
                Log.d(TAG, dateText);
                settingsListener.sendCommand(dateText);
// Форматирование времени как "часы:минуты:секунды"
                DateFormat timeFormat = new SimpleDateFormat("H m s", Locale.getDefault());
                String timeText = timeFormat.format(currentDate);
                Log.d(TAG, timeText);
                settingsListener.sendCommand(timeText);
            }
        });
        butGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DoubleMint84/LightWave"));
                startActivity(browserIntent);
            }
        });
        butAuthors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsListener.showAuthors();
            }
        });
        return v;
    }
}