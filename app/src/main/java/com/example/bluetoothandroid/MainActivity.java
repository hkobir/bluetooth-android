package com.example.bluetoothandroid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private Button btOn, btOff, devices, discoverBt;
    private BluetoothAdapter bluetoothAdapter;
    private int REQUEST_CODE = 100;
    private Intent enableBtEntent;
    private ListView deviceLV;
    private boolean isDevices = false;
    ArrayList<String> discoverDevices;
    ArrayAdapter<String> arrayAdapter;
    private IntentFilter foundFilter;
    BroadcastReceiver myReceiver;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btOn = findViewById(R.id.btOn);
        btOff = findViewById(R.id.btOff);
        devices = findViewById(R.id.deviceListBt);
        discoverBt = findViewById(R.id.discoverDBt);
        progressBar = findViewById(R.id.progressB);
        deviceLV = findViewById(R.id.deviceLV);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        enableBtEntent = new Intent((BluetoothAdapter.ACTION_REQUEST_ENABLE));
        discoverDevices = new ArrayList<>();
        foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        btOn.setOnClickListener(v -> {
            enaBleBluetoothRequest();
        });
        btOff.setOnClickListener(v -> {
            if (bluetoothAdapter.enable()) {
                bluetoothAdapter.disable();
                isDevices = false;
            }
        });
        devices.setOnClickListener(v -> {
            isDevices = true;
            if (!bluetoothAdapter.enable())
                enaBleBluetoothRequest();
            else
                showDevices();
        });
        discoverBt.setOnClickListener(v -> {
            isDevices = true;
            if (!bluetoothAdapter.enable())
                enaBleBluetoothRequest();
            else {
                progressBar.setVisibility(View.VISIBLE);
                startScanDevices();
            }
        });

        registerReceiver(myReceiver, foundFilter);
        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    discoverDevices.add(device.getName());
                    arrayAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }
            }
        };
    }

    private void startScanDevices() {
        bluetoothAdapter.startDiscovery();
        arrayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, discoverDevices);
        deviceLV.setAdapter(arrayAdapter);
        deviceLV.setAdapter(arrayAdapter);
    }

    public void showDevices() {
        Set<BluetoothDevice> bluetoothDevices = bluetoothAdapter.getBondedDevices();
        String[] names = new String[bluetoothDevices.size()];
        int index = 0;
        if (bluetoothDevices.size() > 0)
            for (BluetoothDevice device : bluetoothDevices) {
                names[index] = device.getName();
                index++;
            }
        arrayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, names);
        deviceLV.setAdapter(arrayAdapter);
    }

    public void enaBleBluetoothRequest() {
        if (bluetoothAdapter == null) {
            Toast.makeText(MainActivity.this, "Not support!", Toast.LENGTH_SHORT).show();
        } else {
            if (!bluetoothAdapter.enable()) {
                startActivityForResult(enableBtEntent, REQUEST_CODE);
            } else {
                Toast.makeText(MainActivity.this, "Already enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (isDevices)
                    showDevices();
                Toast.makeText(MainActivity.this, "bluetooth enabled",
                        Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED)
                Toast.makeText(MainActivity.this, "bluetooth canceled",
                        Toast.LENGTH_SHORT).show();

        }
    }
}