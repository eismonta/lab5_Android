package com.example.lab5;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.util.UnstableApi;

@UnstableApi
public class LevelFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private TextView tvPitch, tvRoll;
    private EditText etTargetAngle;
    private View lineHorizon, rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_level, container, false);

        rootView = v.findViewById(R.id.levelRootView);
        tvPitch = v.findViewById(R.id.tvPitch);
        tvRoll = v.findViewById(R.id.tvRoll);
        etTargetAngle = v.findViewById(R.id.etTargetAngle);
        lineHorizon = v.findViewById(R.id.lineHorizon);

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        return v;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double pitch = Math.toDegrees(Math.atan2(y, z));
            double roll = Math.toDegrees(Math.atan2(-x, Math.sqrt(y * y + z * z)));

            updateUI(pitch, roll);
        }
    }

    private void updateUI(double pitch, double roll) {
        tvPitch.setText(String.format("Нахил: %.1f°", pitch));
        tvRoll.setText(String.format("Поворот: %.1f°", roll));
        lineHorizon.setRotation((float) -roll);

        // Отримання цільового кута
        double target = 0;
        String input = etTargetAngle.getText().toString();
        if (!input.isEmpty()) {
            try {
                target = Double.parseDouble(input);
            } catch (NumberFormatException e) {
                target = 0;
            }
        }

        if (Math.abs(Math.abs(roll) - Math.abs(target)) < 1.5) {
            rootView.setBackgroundColor(0xFFC8E6C9);
        } else {
            rootView.setBackgroundColor(0xFFFFFFFF);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}