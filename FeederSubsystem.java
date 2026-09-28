package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

/** Nonblocking single pulse with cooldown. Use only after verifying servo direction and timing. */
public final class FeederSubsystem {
    private final CRServo feeder;
    private final ElapsedTime clock = new ElapsedTime();
    private double feedEnd = -1;
    private double nextAllowed = 0;

    public FeederSubsystem(HardwareMap map) {
        feeder = map.get(CRServo.class, RobotConfig.FEEDER);
        feeder.setPower(0);
    }

    public boolean requestShot(boolean flywheelReady) {
        if (!flywheelReady || isFeeding() || clock.seconds() < nextAllowed) return false;
        feedEnd = clock.seconds() + RobotConfig.FEED_SECONDS;
        nextAllowed = feedEnd + RobotConfig.SHOT_COOLDOWN_SECONDS;
        feeder.setPower(RobotConfig.FEED_POWER);
        return true;
    }

    public void update() {
        if (feedEnd >= 0 && clock.seconds() >= feedEnd) {
            feeder.setPower(0);
            feedEnd = -1;
        }
    }

    public boolean isFeeding() { return feedEnd >= 0; }

    public void stop() {
        feeder.setPower(0);
        feedEnd = -1;
    }
}
