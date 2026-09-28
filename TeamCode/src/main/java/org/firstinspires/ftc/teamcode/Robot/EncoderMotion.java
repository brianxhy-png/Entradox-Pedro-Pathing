package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

/** Relative mecanum encoder steps. Counts/strafe slip need robot calibration. */
public final class EncoderMotion {
    private final LinearOpMode opMode;
    private final DcMotorEx[] motors;

    public EncoderMotion(LinearOpMode opMode) {
        this.opMode = opMode;
        motors = new DcMotorEx[]{
                opMode.hardwareMap.get(DcMotorEx.class, RobotConfig.FRONT_LEFT),
                opMode.hardwareMap.get(DcMotorEx.class, RobotConfig.FRONT_RIGHT),
                opMode.hardwareMap.get(DcMotorEx.class, RobotConfig.BACK_LEFT),
                opMode.hardwareMap.get(DcMotorEx.class, RobotConfig.BACK_RIGHT)
        };
        motors[1].setDirection(DcMotor.Direction.REVERSE);
        motors[3].setDirection(DcMotor.Direction.REVERSE);
        for (DcMotorEx motor : motors) {
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public boolean move(double forwardIn, double rightIn, double turnEquivalentIn) {
        return move(forwardIn, rightIn, turnEquivalentIn, RobotConfig.AUTO_TIMEOUT_SECONDS);
    }

    public boolean move(double forwardIn, double rightIn, double turnEquivalentIn, double timeoutSeconds) {
        if (!opMode.opModeIsActive()) return false;
        double ticksPerIn = RobotConfig.DRIVE_COUNTS_PER_REV
                / (Math.PI * RobotConfig.DRIVE_WHEEL_DIAMETER_IN);
        double[] inches = {
                forwardIn + rightIn + turnEquivalentIn,
                forwardIn - rightIn - turnEquivalentIn,
                forwardIn - rightIn + turnEquivalentIn,
                forwardIn + rightIn - turnEquivalentIn
        };
        for (int i = 0; i < motors.length; i++) {
            motors[i].setTargetPosition(motors[i].getCurrentPosition()
                    + (int) Math.round(inches[i] * ticksPerIn));
            motors[i].setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motors[i].setPower(RobotConfig.AUTO_DRIVE_POWER);
        }
        ElapsedTime timer = new ElapsedTime();
        boolean reached = false;
        try {
            while (opMode.opModeIsActive() && timer.seconds() < timeoutSeconds) {
                boolean anyBusy = false;
                for (DcMotorEx motor : motors) anyBusy |= motor.isBusy();
                if (!anyBusy) {
                    reached = true;
                    break;
                }
                opMode.telemetry.addData("Motion", "F %.1f R %.1f T %.1f", forwardIn, rightIn, turnEquivalentIn);
                opMode.telemetry.addData("Timeout", "%.1f / %.1f", timer.seconds(), timeoutSeconds);
                opMode.telemetry.update();
                opMode.idle();
            }
        } finally {
            stop();
            for (DcMotorEx motor : motors) motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        return reached && opMode.opModeIsActive();
    }

    public void stop() {
        for (DcMotorEx motor : motors) motor.setPower(0);
    }
}
