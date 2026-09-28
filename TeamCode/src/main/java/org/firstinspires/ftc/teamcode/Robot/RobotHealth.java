package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

/** The minimum positive voltage reported by installed sensors. */
public final class RobotHealth {
    private final HardwareMap map;

    public RobotHealth(HardwareMap map) { this.map = map; }

    public double voltage() {
        double min = Double.POSITIVE_INFINITY;
        for (VoltageSensor sensor : map.voltageSensor) {
            double value = sensor.getVoltage();
            if (value > 0 && value < min) min = value;
        }
        return min == Double.POSITIVE_INFINITY ? Double.NaN : min;
    }

    public boolean shootingVoltageOkay() {
        double value = voltage();
        return !Double.isNaN(value) && value >= RobotConfig.MIN_SHOOT_VOLTAGE;
    }
}
