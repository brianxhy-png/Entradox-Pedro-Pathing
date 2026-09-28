package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public final class IntakeSubsystem {
    private final DcMotor motor;
    private boolean enabled;

    public IntakeSubsystem(HardwareMap map) {
        motor = map.get(DcMotor.class, RobotConfig.INTAKE);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void toggle() { enabled = !enabled; }
    public boolean isEnabled() { return enabled; }

    public void update(boolean reverse) {
        motor.setPower(reverse ? -RobotConfig.INTAKE_POWER :
                       enabled ? RobotConfig.INTAKE_POWER : 0);
    }

    public void stop() {
        enabled = false;
        motor.setPower(0);
    }
}
