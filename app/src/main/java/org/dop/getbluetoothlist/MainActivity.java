package org.dop.getbluetoothlist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 2;
    private static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 100;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 3;
    private RecyclerView rcList;
    private ListAdapter adapter;
    private List<Item> items = new ArrayList<>();
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private TextView bluetoothConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rcList = findViewById(R.id.rc_view_blt_paired);
        bluetoothConnected = findViewById(R.id.blt_connected_devices);
        items = new ArrayList<>();

        rcList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListAdapter(items, getApplicationContext());
        rcList.setAdapter(adapter);

        checkBluetoothPermission();

    }

    public void checkBluetoothPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    10);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
            checkBluetoothEnable();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, REQUEST_BLUETOOTH_PERMISSION);
        }
    }

    @SuppressLint("MissingPermission")
    public void checkBluetoothEnable() {
        bluetoothManager = getSystemService(BluetoothManager.class);
        if (Build.VERSION.SDK_INT >= 31) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Device does not support bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                processPairedDevices(pairedDevices);

                IntentFilter filter = new IntentFilter();
                filter.addAction(BluetoothDevice.ACTION_FOUND);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                registerReceiver(receiver, filter);
                bluetoothAdapter.startDiscovery();
            }
        }
    }

    public void processPairedDevices(Set<BluetoothDevice> pairedDevices) {

        if (pairedDevices.size() > 0) {
            items.clear();
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                    String deviceName = device.getName();
                    if (deviceName != null) {
                        String deviceAddress = device.getAddress(); // MAC address
                        Item bluetooth = new Item(deviceName, deviceAddress);

                        items.add(bluetooth);
                    }
                } else {
                    // Xử lý khi quyền BLUETOOTH không được cấp
                    Toast.makeText(this, "Bluetooth permission is not granted", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public void processConnectedDevice() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
            if (!bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.startDiscovery();
            }
        }

    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            String action = intent.getAction();
            Toast.makeText(context, "huhu", Toast.LENGTH_SHORT).show();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                StringBuilder bluetooth = new StringBuilder();
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                    String deviceName = device.getName();
                    if(deviceName != null){
                        String deviceAddress = device.getAddress();
                        bluetooth.append("\n Bluetooth name:"+deviceName);
                        bluetooth.append("\n Bluetooth address"+ deviceAddress);
                        Toast.makeText(context, "heheh", Toast.LENGTH_LONG).show();
                        bluetoothConnected.setText(bluetooth.toString());
                    }

                }
            }
        }
    };
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_BLUETOOTH_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                checkBluetoothEnable();
            }
            else{
                Toast.makeText(this, "Bluetooth permissions should be allowed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                checkBluetoothEnable();
            } else {
                Toast.makeText(this, "Device does not support bluetooth", Toast.LENGTH_SHORT).show();
            }
        }
    }
}