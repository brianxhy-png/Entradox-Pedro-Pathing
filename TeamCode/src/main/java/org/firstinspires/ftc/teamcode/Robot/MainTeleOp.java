package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Entradox | Main TeleOp", group = "Entradox")
public final class MainTeleOp extends OpMode {
    private DriveSubsystem drive;
    private IntakeSubsystem intake;
    private FlywheelSubsystem flywheel;
    private TurretSubsystem turret;
    private VisionSubsystem vision;
    private FeederSubsystem feeder;
    private RobotHealth health;
    private final EdgeButton intakeButton = new EdgeButton();
    private final EdgeButton flywheelButton = new EdgeButton();
    private final EdgeButton shotButton = new EdgeButton();
    private final ElapsedTime matchClock = new ElapsedTime();
    private String shotStatus = "idle";

    @Override
    public void init() {
        drive = new DriveSubsystem(hardwareMap);
        intake = new IntakeSubsystem(hardwareMap);
        flywheel = new FlywheelSubsystem(hardwareMap);
        turret = new TurretSubsystem(hardwareMap);
        health = new RobotHealth(hardwareMap);
        try {
            feeder = new FeederSubsystem(hardwareMap);
        } catch (IllegalArgumentException ex) {
            telemetry.addData("Feeder", "Unavailable: %s", ex.getMessage());
        }
        // Limelight is optional for manual driving; missing device leaves auto-aim unavailable.
        try {
            vision = new VisionSubsystem(hardwareMap);
        } catch (IllegalArgumentException ex) {
            telemetry.addData("Vision", "Unavailable: %s", ex.getMessage());
        }
        telemetry.addLine("Check turret centered before INIT; test motor directions with wheels raised.");
        telemetry.update();
    }

    @Override
    public void start() { matchClock.reset(); }

    @Override
    public void loop() {
        drive.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x,
                gamepad1.right_stick_x,
                gamepad1.right_bumper ? RobotConfig.SLOW_SCALE : 1);

        if (intakeButton.rising(gamepad2.right_bumper)) intake.toggle();
        intake.update(gamepad2.right_trigger > 0.2);

        if (flywheelButton.rising(gamepad2.left_bumper)) flywheel.toggle();
        flywheel.update();

        if (feeder != null) {
            feeder.update();
            if (shotButton.rising(gamepad2.x)) {
                if (!health.shootingVoltageOkay()) shotStatus = "blocked: low/unknown voltage";
                else if (feeder.requestShot(flywheel.isReady())) shotStatus = "feeding";
                else shotStatus = "blocked: not ready or cooldown";
            }
        }

        boolean aiming = gamepad2.a && vision != null;
        Double error = aiming ? vision.horizontalErrorDegrees() : null;
        if (aiming) {
            if (error != null) turret.aimAtError(error);
            else turret.stop(); // Never continue turning on lost or stale vision.
        } else {
            turret.setPower(-gamepad2.left_stick_x * RobotConfig.TURRET_MANUAL_POWER);
        }

        telemetry.addData("Intake", intake.isEnabled());
        telemetry.addData("Flywheel", flywheel.isEnabled());
        telemetry.addData("Flywheel ready (tune first)", flywheel.isReady());
        telemetry.addData("Turret ticks from init", turret.relativeTicks());
        telemetry.addData("Vision tx", error == null ? "unavailable" : error);
        telemetry.addData("Match time", "%.1f s", matchClock.seconds());
        telemetry.addData("Battery", "%.2f V", health.voltage());
        telemetry.addData("Shot", feeder == null ? "no feeder" : shotStatus);
        telemetry.addLine("GP1 sticks drive; RB slow | GP2 RB intake, RT reverse, LB flywheel");
        telemetry.addLine("GP2 left stick X turret, hold A for Limelight assist, X fire when ready");
        telemetry.update();
    }

    @Override
    public void stop() {
        if (drive != null) drive.stop();
        if (intake != null) intake.stop();
        if (flywheel != null) flywheel.stop();
        if (turret != null) turret.stop();
        if (feeder != null) feeder.stop();
        if (vision != null) vision.stop();
    }
}
