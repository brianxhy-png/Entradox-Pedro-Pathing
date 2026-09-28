package org.firstinspires.ftc.teamcode.robot;

/** Replace these placeholders after checking the Driver Station configuration. */
public final class RobotConfig {
    private RobotConfig() {}

    public static final String FRONT_LEFT = "frontLeft";
    public static final String FRONT_RIGHT = "frontRight";
    public static final String BACK_LEFT = "backLeft";
    public static final String BACK_RIGHT = "backRight";
    public static final String INTAKE = "intakeMotor";
    public static final String FLYWHEEL_LEFT = "leftOuttake";
    public static final String FLYWHEEL_RIGHT = "rightOuttake";
    public static final String TURRET = "turretMotor";
    public static final String FEEDER = "gateServo";
    public static final String LAUNCH = "launch";
    public static final String LIMELIGHT = "limelight";

    public static final double DRIVE_DEADZONE = 0.05;
    public static final double SLOW_SCALE = 0.45;
    public static final double INTAKE_POWER = 0.8;
    public static final double FLYWHEEL_TICKS_PER_SECOND = 1200; // TUNE on robot
    public static final double FLYWHEEL_READY_TOLERANCE = 100;   // TUNE
    public static final double TURRET_MANUAL_POWER = 0.35;
    public static final int TURRET_MIN_TICKS = -450;             // TUNE, relative to init
    public static final int TURRET_MAX_TICKS = 450;              // TUNE, relative to init
    public static final double TURRET_KP = 0.018;               // TUNE power/degree
    public static final double TURRET_AIM_DEADBAND_DEG = 1.5;
    public static final double TURRET_MAX_AUTO_POWER = 0.3;
    public static final int LIMELIGHT_PIPELINE = 0;             // Configure on Limelight
    public static final int DESIRED_TAG_ID = -1; // Set a real ID to require a specific fiducial
    public static final double VISION_STALE_MS = 250;
    public static final double FEED_POWER = 0.6; // Verify CRServo direction
    public static final double FEED_SECONDS = 0.35; // Tune for exactly one ball
    public static final double SHOT_COOLDOWN_SECONDS = 0.4;
    public static final double MIN_SHOOT_VOLTAGE = 11.0; // Advisory, not a battery safety guarantee
    public static final double DRIVE_COUNTS_PER_REV = 560; // Confirm gear ratio
    public static final double DRIVE_WHEEL_DIAMETER_IN = 3.94; // Measure
    public static final double AUTO_DRIVE_POWER = 0.25;
    public static final double AUTO_TIMEOUT_SECONDS = 5.0;
}
