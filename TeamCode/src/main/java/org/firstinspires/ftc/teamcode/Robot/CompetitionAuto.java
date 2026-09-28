package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Autonomous framework, disabled until paths, aim, and hardware are verified.
 * Field coordinates and scoring geometry are intentionally not invented.
 */
@Autonomous(name = "Entradox | Selectable Auto (CALIBRATE FIRST)", group = "Entradox")
@Disabled
public final class CompetitionAuto extends LinearOpMode {
    private enum Alliance { RED, BLUE }
    private enum Start { AUDIENCE, FAR }
    private enum Plan { PARK, ONE_PRELOAD, TWO_PRELOAD }

    // PLACEHOLDERS: measure from the actual BIOBUZZ starting locations and safe lanes.
    private static final double AUDIENCE_APPROACH_IN = 0;
    private static final double FAR_APPROACH_IN = 0;
    private static final double PARK_FORWARD_IN = 0;
    private static final double PARK_STRAFE_IN = 0;

    private Alliance alliance = Alliance.RED;
    private Start start = Start.AUDIENCE;
    private Plan plan = Plan.PARK;

    @Override
    public void runOpMode() throws InterruptedException {
        EncoderMotion motion = new EncoderMotion(this);
        FlywheelSubsystem flywheel = null;
        FeederSubsystem feeder = null;
        TurretSubsystem turret = null;
        VisionSubsystem vision = null;
        RobotHealth health = new RobotHealth(hardwareMap);

        boolean previousUp = false, previousDown = false, previousLeft = false, previousRight = false;
        while (opModeInInit()) {
            if (gamepad1.dpad_up && !previousUp) alliance = Alliance.RED;
            if (gamepad1.dpad_down && !previousDown) alliance = Alliance.BLUE;
            if (gamepad1.dpad_left && !previousLeft) start = Start.AUDIENCE;
            if (gamepad1.dpad_right && !previousRight) start = Start.FAR;
            previousUp = gamepad1.dpad_up;
            previousDown = gamepad1.dpad_down;
            previousLeft = gamepad1.dpad_left;
            previousRight = gamepad1.dpad_right;
            if (gamepad1.a) plan = Plan.PARK;
            if (gamepad1.b) plan = Plan.ONE_PRELOAD;
            if (gamepad1.x) plan = Plan.TWO_PRELOAD;
            telemetry.addData("Alliance", alliance);
            telemetry.addData("Start", start);
            telemetry.addData("Plan", plan);
            telemetry.addLine("Dpad up/down alliance; left/right start; A/B/X plan");
            telemetry.addLine("DISABLED: field distances are zero placeholders. Measure before enabling.");
            telemetry.update();
            sleep(50);
        }
        if (isStopRequested()) return;

        try {
            if (plan != Plan.PARK) {
                flywheel = new FlywheelSubsystem(hardwareMap);
                feeder = new FeederSubsystem(hardwareMap);
                turret = new TurretSubsystem(hardwareMap);
                vision = new VisionSubsystem(hardwareMap);
            }
            double side = alliance == Alliance.RED ? 1 : -1;
            double approach = start == Start.AUDIENCE ? AUDIENCE_APPROACH_IN : FAR_APPROACH_IN;
            if (approach != 0 && !motion.move(approach, 0, 0)) return;

            if (plan != Plan.PARK) {
                flywheel.toggle();
                int count = plan == Plan.TWO_PRELOAD ? 2 : 1;
                for (int i = 0; i < count && opModeIsActive(); i++) {
                    if (!aimUntilCentered(turret, vision, 2.0)) return;
                    if (!waitUntilReady(flywheel, health, 3.0)) return;
                    if (!feeder.requestShot(flywheel.isReady())) return;
                    while (opModeIsActive() && feeder.isFeeding()) {
                        feeder.update();
                        idle();
                    }
                    // Let cooldown expire before a second shot.
                    sleep(500);
                }
            }
            if (PARK_FORWARD_IN != 0 || PARK_STRAFE_IN != 0) {
                motion.move(PARK_FORWARD_IN, side * PARK_STRAFE_IN, 0);
            }
        } finally {
            motion.stop();
            if (feeder != null) feeder.stop();
            if (flywheel != null) flywheel.stop();
            if (turret != null) turret.stop();
            if (vision != null) vision.stop();
        }
    }

    private boolean aimUntilCentered(TurretSubsystem turret, VisionSubsystem vision, double seconds) {
        ElapsedTime timer = new ElapsedTime();
        while (opModeIsActive() && timer.seconds() < seconds) {
            Double tx = vision.horizontalErrorDegrees();
            if (tx == null) {
                turret.stop();
                return false;
            }
            if (Math.abs(tx) <= RobotConfig.TURRET_AIM_DEADBAND_DEG) {
                turret.stop();
                return true;
            }
            turret.aimAtError(tx);
            idle();
        }
        turret.stop();
        return false;
    }

    private boolean waitUntilReady(FlywheelSubsystem flywheel, RobotHealth health, double seconds) {
        ElapsedTime timer = new ElapsedTime();
        while (opModeIsActive() && timer.seconds() < seconds) {
            flywheel.update();
            if (!health.shootingVoltageOkay()) return false;
            if (flywheel.isReady()) return true;
            idle();
        }
        return false;
    }
}
