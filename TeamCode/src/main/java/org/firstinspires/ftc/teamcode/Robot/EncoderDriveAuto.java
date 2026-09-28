package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

/** Example only. Tune encoder geometry and verify all four directions before enabling. */
@Autonomous(name = "Entradox | Encoder Forward (CALIBRATE FIRST)", group = "Entradox")
@Disabled
public final class EncoderDriveAuto extends LinearOpMode {
    private static final double COUNTS_PER_IN = RobotConfig.DRIVE_COUNTS_PER_REV
            / (Math.PI * RobotConfig.DRIVE_WHEEL_DIAMETER_IN);
    private static final double MAX_SECONDS = 4.0;

    @Override
    public void runOpMode() throws InterruptedException {
        DcMotorEx fl = hardwareMap.get(DcMotorEx.class, RobotConfig.FRONT_LEFT);
        DcMotorEx fr = hardwareMap.get(DcMotorEx.class, RobotConfig.FRONT_RIGHT);
        DcMotorEx bl = hardwareMap.get(DcMotorEx.class, RobotConfig.BACK_LEFT);
        DcMotorEx br = hardwareMap.get(DcMotorEx.class, RobotConfig.BACK_RIGHT);
        DcMotorEx[] motors = {fl, fr, bl, br};
        fr.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.REVERSE);
        for (DcMotorEx motor : motors) {
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        telemetry.addLine("DISABLED until counts/inch and motor directions are verified.");
        telemetry.update();
        waitForStart();
        if (isStopRequested()) return;

        int ticks = (int) Math.round(24 * COUNTS_PER_IN);
        ElapsedTime timer = new ElapsedTime();
        try {
            for (DcMotorEx motor : motors) {
                motor.setTargetPosition(motor.getCurrentPosition() + ticks);
                motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motor.setPower(0.25);
            }
            timer.reset();
            while (opModeIsActive() && timer.seconds() < MAX_SECONDS) {
                boolean anyBusy = false;
                for (DcMotorEx motor : motors) anyBusy |= motor.isBusy();
                if (!anyBusy) break;
                telemetry.addData("Elapsed", "%.1f s", timer.seconds());
                telemetry.addData("FL / FR", "%d / %d", fl.getCurrentPosition(), fr.getCurrentPosition());
                telemetry.update();
                idle();
            }
        } finally {
            for (DcMotorEx motor : motors) {
                motor.setPower(0);
                motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
        }
    }
}
