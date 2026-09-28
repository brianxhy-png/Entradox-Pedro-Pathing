package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public final class FlywheelSubsystem {
    private final DcMotorEx left, right;
    private boolean enabled;

    public FlywheelSubsystem(HardwareMap map) {
        left = map.get(DcMotorEx.class, RobotConfig.FLYWHEEL_LEFT);
        right = map.get(DcMotorEx.class, RobotConfig.FLYWHEEL_RIGHT);
        // Verify the wheel directions in a safe test before firing.
        right.setDirection(DcMotor.Direction.REVERSE);
        for (DcMotorEx motor : new DcMotorEx[]{left, right}) {
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
    }

    public void toggle() { enabled = !enabled; }
    public boolean isEnabled() { return enabled; }

    public void update() {
        double target = enabled ? RobotConfig.FLYWHEEL_TICKS_PER_SECOND : 0;
        left.setVelocity(target);
        right.setVelocity(target);
    }

    public boolean isReady() {
        double goal = RobotConfig.FLYWHEEL_TICKS_PER_SECOND;
        return enabled && Math.abs(left.getVelocity() - goal) <= RobotConfig.FLYWHEEL_READY_TOLERANCE
                       && Math.abs(right.getVelocity() - goal) <= RobotConfig.FLYWHEEL_READY_TOLERANCE;
    }

    public void stop() {
        enabled = false;
        left.setPower(0);
        right.setPower(0);
    }
}
