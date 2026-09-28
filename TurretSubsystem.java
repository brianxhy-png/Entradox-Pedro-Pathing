package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

public final class TurretSubsystem {
    private final DcMotorEx motor;
    private final int zero;

    public TurretSubsystem(HardwareMap map) {
        motor = map.get(DcMotorEx.class, RobotConfig.TURRET);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        // Relative software zero only. Place turret at a known safe center before INIT.
        zero = motor.getCurrentPosition();
    }

    public int relativeTicks() { return motor.getCurrentPosition() - zero; }

    public void setPower(double power) {
        power = Range.clip(power, -RobotConfig.TURRET_MANUAL_POWER,
                                 RobotConfig.TURRET_MANUAL_POWER);
        int position = relativeTicks();
        if ((position <= RobotConfig.TURRET_MIN_TICKS && power < 0)
                || (position >= RobotConfig.TURRET_MAX_TICKS && power > 0)) {
            power = 0;
        }
        motor.setPower(power);
    }

    /** tx is Limelight's horizontal target error; sign needs verification on the real turret. */
    public void aimAtError(double txDegrees) {
        if (Math.abs(txDegrees) <= RobotConfig.TURRET_AIM_DEADBAND_DEG) {
            stop();
            return;
        }
        double power = Range.clip(txDegrees * RobotConfig.TURRET_KP,
                -RobotConfig.TURRET_MAX_AUTO_POWER, RobotConfig.TURRET_MAX_AUTO_POWER);
        setPower(power);
    }

    public void stop() { motor.setPower(0); }
}
