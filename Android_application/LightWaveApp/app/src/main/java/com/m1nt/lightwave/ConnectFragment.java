package com.m1nt.lightwave;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Set;
import static android.R.layout.simple_list_item_1;

public class ConnectFragment extends Fragment {
    private TextView textInfo;
    ListView listViewPairedDevice;
    Button bSetup;
    ArrayList<String> pairedDeviceArrayList;
    ArrayAdapter pairedDeviceAdapter;

    public interface onSomeEventListener {
        public void someEvent(BluetoothDevice device);
    }

    onSomeEventListener someEventListener;

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            someEventListener = (onSomeEventListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_connect, container, false);
        textInfo = v.findViewById(R.id.textInfoPhone);
        bSetup = v.findViewById(R.id.butSetup);
        bSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> pairedDevices = MainActivity.bluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    pairedDeviceArrayList = new ArrayList<>();
                    for (BluetoothDevice device : pairedDevices) {
                        pairedDeviceArrayList.add(device.getName() + "\n" + device.getAddress());
                    }
                    pairedDeviceAdapter = new ArrayAdapter<>(inflater.getContext(), simple_list_item_1, pairedDeviceArrayList);
                    listViewPairedDevice.setAdapter(pairedDeviceAdapter);
                    listViewPairedDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            listViewPairedDevice.setEnabled(false);
                            String itemValue = (String) listViewPairedDevice.getItemAtPosition(position);
                            String MAC = itemValue.substring(itemValue.length() - 17);
                            BluetoothDevice device2 = MainActivity.bluetoothAdapter.getRemoteDevice(MAC);
                            someEventListener.someEvent(device2);
                            //MainActivity.myThreadConnectBTDevice.setSocket(device2);
                            //MainActivity.myThreadConnectBTDevice.start();
                        }
                    });
                }
            }
        });
        textInfo.setText(String.format("Это устройство: %s", MainActivity.bluetoothAdapter.getName() + " " + MainActivity.bluetoothAdapter.getAddress()));
        listViewPairedDevice = (ListView) v.findViewById(R.id.listDevices);
        return v;
    }

}