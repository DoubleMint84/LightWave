package com.m1nt.lightwave;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements ConnectFragment.onSomeEventListener, LampFragment.onLampListener, AlarmsFragment.onAlarmListener {

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
        if (requestCode == REQUEST_ENABLE_BT) { // Если разрешили включить Bluetooth, тогда void setup()
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "BlueTooth включен, нажмите кнопку для открытия списка девайсов", Toast.LENGTH_SHORT).show();
            } else { // Если не разрешили, тогда закрываем приложение
                Toast.makeText(this, "BlueTooth не включён", Toast.LENGTH_SHORT).show();
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
                    selectedFragment = new ConnectFragment();
                    break;
                case R.id.nav_alarms:
                    selectedFragment = new AlarmsFragment();
                    break;
                case R.id.nav_lamp:
                    selectedFragment = new LampFragment();
                    break;
                case R.id.nav_device_settings:
                    selectedFragment = new DeviceSettingsFragment();
                    break;
                case R.id.nav_app_settings:
                    selectedFragment = new AppSettingsFragment();
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
    public void offLed() {
        if (myThreadConnected != null) {
            byte[] bytesToSend = ("$2 0;").getBytes();
            myThreadConnected.write(bytesToSend );
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

    public class ThreadConnectBTdevice extends Thread { // Поток для коннекта с Bluetooth
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
        public void run() { // Коннект
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
            if(success) {  // Если законнектились, тогда открываем панель с кнопками и запускаем поток приёма и отправки данных
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                        //ButPanel.setVisibility(View.VISIBLE); // открываем панель с кнопками
                    }
                });

                myThreadConnected = new ThreadConnected(bluetoothSocket);
                myThreadConnected.start(); // запуск потока приёма и отправки данных
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

    private class ThreadConnected extends Thread {    // Поток - приём и отправка данных
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;
        private String sbprint;
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
        public void run() { // Приём данных
            while (true) {
                try {
                    /*byte[] buffer = new byte[1];
                    int bytes = connectedInputStream.read(buffer);
                    String strIncom = new String(buffer, 0, bytes);
                    sb.append(strIncom); // собираем символы в строку
                    int endOfLineIndex = sb.indexOf("\r\n"); // определяем конец строки
                    if (endOfLineIndex > 0) {
                        sbprint = sb.substring(0, endOfLineIndex);
                        sb.delete(0, sb.length());
                        runOnUiThread(new Runnable() { // Вывод данных

                            @Override
                            public void run() {
                                switch (sbprint) {

                                    case "D10 ON":

                                    case "D12 OFF":

                                    case "D12 ON":

                                    case "D10 OFF":

                                    case "D11 ON":
                                        Toast.makeText(MainActivity.this, sbprint, Toast.LENGTH_SHORT).show();
                                        break;

                                    case "D11 OFF":
                                        Toast.makeText(MainActivity.this, sbprint, Toast.LENGTH_SHORT).show();
                                        break;

                                    case "D13 ON":
                                        Toast.makeText(MainActivity.this, sbprint, Toast.LENGTH_SHORT).show();
                                        break;

                                    case "D13 OFF":
                                        Toast.makeText(MainActivity.this, sbprint, Toast.LENGTH_SHORT).show();
                                        break;

                                    default:
                                        break;
                                }
                            }
                        });
                    }*/
                    byte[] buffer = new byte[1];
                    int bytes = connectedInputStream.read(buffer);
                    String strIncom = new String(buffer, 0, bytes);
                    sb.append(strIncom); // собираем символы в строку
                    int endOfLineIndex = sb.indexOf("\r\n"); // определяем конец строки
                    if (endOfLineIndex > 0) {

                        Log.d(TAG, "DATA DETECTED");
                        sbprint = sb.substring(0, endOfLineIndex);
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