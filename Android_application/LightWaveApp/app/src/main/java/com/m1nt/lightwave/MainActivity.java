package com.m1nt.lightwave;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements ConnectFragment.onSomeEventListener, LightFragment.onLampListener, AlarmsFragment.onAlarmListener, AppSettingsFragment.onSettingsListener {

    public static BluetoothAdapter bluetoothAdapter;
    public static ThreadConnectBTdevice myThreadConnectBTDevice;
    public static ThreadConnected myThreadConnected;
    public static UUID myUUID;
    public final String UUID_STRING_WELL_KNOWN_SPP = "00001101-0000-1000-8000-00805F9B34FB";
    private static final int REQUEST_ENABLE_BT = 1;
    private StringBuilder sb = new StringBuilder();
    public int currentAl = 0;
    private static final String TAG = "myLogs";

    public AlarmRecord[] alarmRecord = new AlarmRecord[7];
    public int dawnTime = 30;
    public boolean breathMode = false;

    private ConnectFragment connectFragment = new ConnectFragment();
    private AlarmsFragment alarmsFragment = new AlarmsFragment();
    private LightFragment lightFragment = new LightFragment();
    private AppSettingsFragment appSettingsFragment = new AppSettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //myThreadConnectBTDevice = new ThreadConnectBTdevice();
        for (int i = 0; i < 7; i++) {
            alarmRecord[i] = new AlarmRecord();
            alarmRecord[i].hrs = 0;
            alarmRecord[i].min = 0;
            alarmRecord[i].state = false;
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
            Toast.makeText(this, "BLUETOOTH NOT support", Toast.LENGTH_LONG).show();
            //finish();
            return;
        }

        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this hardware platform", Toast.LENGTH_LONG).show();
            //finish();
            return;
        }
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ConnectFragment()).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) { // ???????? ?????????????????? ???????????????? Bluetooth, ?????????? void setup()
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "BlueTooth ??????????????, ?????????????? ???????????? ?????? ???????????????? ???????????? ????????????????", Toast.LENGTH_SHORT).show();
            } else { // ???????? ???? ??????????????????, ?????????? ?????????????????? ????????????????????
                Toast.makeText(this, "BlueTooth ???? ??????????????", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_connection:
                    selectedFragment = connectFragment;
                    break;
                case R.id.nav_alarms:
                    selectedFragment = alarmsFragment;
                    break;
                case R.id.nav_lamp:
                    selectedFragment = lightFragment;
                    break;
                case R.id.nav_app_settings:
                    selectedFragment = appSettingsFragment;
                    break;
            }

            assert selectedFragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    @Override
    public void someEvent(BluetoothDevice device) {
        myThreadConnectBTDevice = new ThreadConnectBTdevice(device);
        myThreadConnectBTDevice.start();
    }

    @Override
    public void changeColor(int red, int green, int blue) {
        if (myThreadConnected != null) {
            byte[] bytesToSend = ("$2 1 " + String.valueOf(red) + " " + String.valueOf(green) + " " + String.valueOf(blue) + ";").getBytes();
            myThreadConnected.write(bytesToSend );
        }
    }

    @Override
    public void changeBrightness(int bright) {
        if (myThreadConnected != null) {
            byte[] bytesToSend = ("$2 2 0 " + String.valueOf(bright) + ";").getBytes();
            myThreadConnected.write(bytesToSend );
        }
    }

    @Override
    public void offLed() {
        if (myThreadConnected != null) {
            byte[] bytesToSend = ("$2 0;").getBytes();
            myThreadConnected.write(bytesToSend );
        }
    }

    @Override
    public void changeBreathModeState(boolean state) {
        if (myThreadConnected != null) {
            byte[] bytesToSend = ("$2 2 1 " + (state ? "1" : "0") + ";").getBytes();
            myThreadConnected.write(bytesToSend);
        }
    }

    @Override
    public void changeEffect(int num) {
        if (myThreadConnected != null) {
            byte[] bytesToSend = ("$2 3 " + num + ";").getBytes();
            myThreadConnected.write(bytesToSend);
        }
    }

    @Override
    public void changeParam(int param) {
        if (myThreadConnected != null) {
            byte[] bytesToSend = ("$2 5 " + param + ";").getBytes();
            myThreadConnected.write(bytesToSend);
        }
    }

    @Override
    public void changeSpeed(int speed) {
        if (myThreadConnected != null) {
            byte[] bytesToSend = ("$2 4 " + speed + ";").getBytes();
            myThreadConnected.write(bytesToSend);
        }
    }

    @Override
    public String getTime(int num) {
        if (alarmRecord[num].min > 9) {
            return Integer.toString(alarmRecord[num].hrs) + ":" + Integer.toString(alarmRecord[num].min);
        } else {
            return Integer.toString(alarmRecord[num].hrs) + ":0" + Integer.toString(alarmRecord[num].min);
        }

    }

    @Override
    public boolean getState(int num) {
        return alarmRecord[num].state;
    }

    @Override
    public String getDawnTime() {
        return dawnTime + " MIN";
    }

    @Override
    public void showAuthors() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("LightWave Project");
        alert.setMessage("Authors:\n" +
                "Nikita Borodavko\n" +
                "Karim Gataullin\n" +
                "Moscow, 2021\n"
        );
        alert.setPositiveButton("Donate", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    @Override
    public void changeDawnTime() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Change dawn time");
        alert.setMessage("Input dawn time in minutes");

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                try {
                    int timeMin = Integer.parseInt(value);
                    if (timeMin <= 0) {
                        Toast.makeText(MainActivity.this, "Please, type a non zero number", Toast.LENGTH_SHORT).show();
                    } else {
                        dawnTime = timeMin;
                        Button tmp = findViewById(R.id.butDawnTime);
                        tmp.setText(dawnTime + " MIN");
                        if (myThreadConnected != null) {
                            byte[] bytesToSend = ("$1 4 " + dawnTime + ";").getBytes();
                            myThreadConnected.write(bytesToSend);
                        }
                    }
                } catch(NumberFormatException nfe) {
                    Toast.makeText(MainActivity.this, "Please, type a number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    @Override
    public void changeTime(int num) {
        /*DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");*/
        currentAl = num;
        new TimePickerDialog(MainActivity.this, tPick, alarmRecord[num].hrs, alarmRecord[num].min, true).show();

    }

    @Override
    public void changeState(int num) {
        alarmRecord[num].state = !alarmRecord[num].state;
        if (myThreadConnected != null) {
            byte[] bytesToSend = ("$1 2 " + num + " " + (alarmRecord[num].state ? 1 : 0) + ";").getBytes();
            myThreadConnected.write(bytesToSend );
        }

    }

    TimePickerDialog.OnTimeSetListener tPick = new TimePickerDialog.OnTimeSetListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            alarmRecord[currentAl].hrs = hourOfDay;
            alarmRecord[currentAl].min = minute;
            if (myThreadConnected != null) {
                byte[] bytesToSend = ("$1 3 " + currentAl + " " + alarmRecord[currentAl].hrs + " " + alarmRecord[currentAl].min + ";").getBytes();
                myThreadConnected.write(bytesToSend );
            }
            Button but;
            switch (currentAl) {
                case 0:
                    but = findViewById(R.id.butAl1);
                    break;
                case 1:
                    but = findViewById(R.id.butAl2);
                    break;
                case 2:
                    but = findViewById(R.id.butAl3);
                    break;
                case 3:
                    but = findViewById(R.id.butAl4);
                    break;
                case 4:
                    but = findViewById(R.id.butAl5);
                    break;
                case 5:
                    but = findViewById(R.id.butAl6);
                    break;
                case 6:
                    but = findViewById(R.id.butAl7);
                    break;
                default:
                    but = findViewById(R.id.butAl1);
                    break;
            }
            if (alarmRecord[currentAl].min > 9) {
                but.setText(Integer.toString(alarmRecord[currentAl].hrs) + ":" + Integer.toString(alarmRecord[currentAl].min));
            } else {
                but.setText(Integer.toString(alarmRecord[currentAl].hrs) + ":0" + Integer.toString(alarmRecord[currentAl].min));
            }

        }
    };

    @Override
    public void sendCommand(String command) {
        if (myThreadConnected != null) {
            byte[] bytesToSend = ("$" + command + ";").getBytes();
            myThreadConnected.write(bytesToSend );
        }
        Toast.makeText(MainActivity.this, "Command has been sent", Toast.LENGTH_SHORT).show();
    }

    public class ThreadConnectBTdevice extends Thread { // ?????????? ?????? ???????????????? ?? Bluetooth
        private BluetoothSocket bluetoothSocket = null;
        ThreadConnectBTdevice(BluetoothDevice device) {
            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() { // ??????????????
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
            }
            catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Not connected, try again", Toast.LENGTH_LONG).show();
                        Fragment conFrag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        ((ListView) conFrag.getView().findViewById(R.id.listDevices)).setEnabled(true);
                    }
                });

                //ConnectFragment.listViewPairedDevice.setVisibility(View.VISIBLE);
                try {
                    bluetoothSocket.close();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if(success) {  // ???????? ????????????????????????????, ?????????? ?????????????????? ???????????? ?? ???????????????? ?? ?????????????????? ?????????? ???????????? ?? ???????????????? ????????????
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                        //ButPanel.setVisibility(View.VISIBLE); // ?????????????????? ???????????? ?? ????????????????
                    }
                });

                myThreadConnected = new ThreadConnected(bluetoothSocket);
                myThreadConnected.start(); // ???????????? ???????????? ???????????? ?? ???????????????? ????????????
                byte[] bytesToSend = ("$0;").getBytes();
                myThreadConnected.write(bytesToSend);
            }
        }

        public void cancel() {
            Toast.makeText(getApplicationContext(), "Close - BluetoothSocket", Toast.LENGTH_LONG).show();
            try {
                bluetoothSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myThreadConnectBTDevice !=null) myThreadConnectBTDevice.cancel();
    }

    private class ThreadConnected extends Thread {    // ?????????? - ?????????? ?? ???????????????? ????????????
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;

        public ThreadConnected(BluetoothSocket socket) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            connectedInputStream = in;
            connectedOutputStream = out;
        }

        @Override
        public void run() { // ?????????? ????????????
            while (true) {
                try {
                    byte[] buffer = new byte[1];
                    int bytes = connectedInputStream.read(buffer);
                    String strIncom = new String(buffer, 0, bytes);
                    sb.append(strIncom); // ???????????????? ?????????????? ?? ????????????
                    int endOfLineIndex = sb.indexOf("\r\n"); // ???????????????????? ?????????? ????????????
                    if (endOfLineIndex > 0) {

                        Log.d(TAG, "DATA DETECTED");
                        String sbprint = sb.substring(0, endOfLineIndex);
                        sb.delete(0, sb.length());
                        //Toast.makeText(MainActivity.this, sbprint, Toast.LENGTH_SHORT).show();
                        String[] strMas= sbprint.split(" ");
                        int[] param = new int[strMas.length];

                        for (int i = 0; i < strMas.length; i++) param[i] = Integer.parseInt(strMas[i]);
                        switch (param[0]) {
                            case 1:
                                switch (param[1]) {
                                    case 3:
                                        alarmRecord[param[2]].hrs = param[3];
                                        alarmRecord[param[2]].min = param[4];
                                        if (param[5] == 1) {
                                            alarmRecord[param[2]].state = true;
                                        } else {
                                            alarmRecord[param[2]].state = false;
                                        }

                                        break;
                                    case 4:
                                        dawnTime = param[2];
                                        break;
                                }
                                break;
                        }
                    }

                } catch (IOException e) {
                    break;
                }
            }
        }


        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}