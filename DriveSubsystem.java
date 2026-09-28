package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

public final class DriveSubsystem {
    private final DcMotorEx fl, fr, bl, br;

    public DriveSubsystem(HardwareMap map) {
        fl = map.get(DcMotorEx.class, RobotConfig.FRONT_LEFT);
        fr = map.get(DcMotorEx.class, RobotConfig.FRONT_RIGHT);
        bl = map.get(DcMotorEx.class, RobotConfig.BACK_LEFT);
        br = map.get(DcMotorEx.class, RobotConfig.BACK_RIGHT);
        fr.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.REVERSE);
        for (DcMotorEx motor : new DcMotorEx[]{fl, fr, bl, br}) {
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    private static double deadzone(double value) {
        return Math.abs(value) < RobotConfig.DRIVE_DEADZONE ? 0 : value;
    }

    /** Robot-centric: forward, right strafe, clockwise turn. Verify motor/strafe signs on blocks. */
    public void drive(double forward, double right, double clockwise, double scale) {
        forward = deadzone(forward);
        right = deadzone(right);
        clockwise = deadzone(clockwise);
        double a = forward + right + clockwise;
        double b = forward - right - clockwise;
        double c = forward - right + clockwise;
        double d = forward + right - clockwise;
        double divisor = Math.max(1, Math.max(Math.max(Math.abs(a), Math.abs(b)),
                                                  Math.max(Math.abs(c), Math.abs(d))));
        scale = Range.clip(scale, 0, 1);
        fl.setPower(scale * a / divisor);
        fr.setPower(scale * b / divisor);
        bl.setPower(scale * c / divisor);
        br.setPower(scale * d / divisor);
    }

    public void stop() { drive(0, 0, 0, 1); }
}
