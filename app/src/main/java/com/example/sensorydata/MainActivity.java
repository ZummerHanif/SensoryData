package com.example.sensorydata;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private TextView textViewAccelerometer, textViewGyroscope, textViewMagneticField;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor, gyroscopeSensor, magneticFieldSensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewAccelerometer = findViewById(R.id.textViewAccelerometer);
        textViewGyroscope = findViewById(R.id.textViewGyroscope);
        textViewMagneticField = findViewById(R.id.textViewMagneticField);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        registerSensorListeners();
    }

    private void registerSensorListeners() {
        if (accelerometerSensor != null) {
            sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            textViewAccelerometer.setText("Accelerometer Not Available");
        }

        if (gyroscopeSensor != null) {
            sensorManager.registerListener(gyroscopeListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            textViewGyroscope.setText("Gyroscope Not Available");
        }

        if (magneticFieldSensor != null) {
            sensorManager.registerListener(magneticFieldListener, magneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            textViewMagneticField.setText("Magnetic Field Sensor Not Available");
        }
    }

    private final SensorEventListener accelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            textViewAccelerometer.setText("Accelerometer\nX: " + x + "\nY: " + y + "\nZ: " + z);

            // Send data to the server
            sendDataToServer("Accelerometer\nX: " + x + "\nY: " + y + "\nZ: " + z);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private final SensorEventListener gyroscopeListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            textViewGyroscope.setText("Gyroscope\nX: " + x + "\nY: " + y + "\nZ: " + z);

            // Send data to the server
            sendDataToServer("Gyroscope\nX: " + x + "\nY: " + y + "\nZ: " + z);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private final SensorEventListener magneticFieldListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            textViewMagneticField.setText("Magnetic Field\nX: " + x + "\nY: " + y + "\nZ: " + z);

            // Send data to the server
            sendDataToServer("Magnetic Field\nX: " + x + "\nY: " + y + "\nZ: " + z);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void sendDataToServer(String data) {
        new SendDataToServerTask().execute(data);
    }

    private static class SendDataToServerTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                String serverIp = "192.168.0.120"; // Replace with your laptop's IP address
                int serverPort = 12345; // Use the same port as the server

                Socket socket = new Socket(serverIp, serverPort);

                OutputStream outputStream = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(outputStream, true);
                writer.println(params[0]);

                writer.close();
                socket.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
                // Handle UnknownHostException (e.g., incorrect IP address)
            } catch (IOException e) {
                e.printStackTrace();
                // Handle IOException (e.g., network error)
            }
            return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister sensor listeners to save power when the activity is paused
        sensorManager.unregisterListener(accelerometerListener);
        sensorManager.unregisterListener(gyroscopeListener);
        sensorManager.unregisterListener(magneticFieldListener);
    }
}
