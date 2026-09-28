package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

/** A valid configured pipeline is required; tx alone does not identify a desired game target. */
public final class VisionSubsystem {
    private final Limelight3A camera;

    public VisionSubsystem(HardwareMap map) {
        camera = map.get(Limelight3A.class, RobotConfig.LIMELIGHT);
        camera.pipelineSwitch(RobotConfig.LIMELIGHT_PIPELINE);
        camera.start();
    }

    public Double horizontalErrorDegrees() {
        LLResult result = camera.getLatestResult();
        if (result == null || !result.isValid()
                || result.getStaleness() > RobotConfig.VISION_STALE_MS) {
            return null;
        }
        if (RobotConfig.DESIRED_TAG_ID > 0) {
            for (LLResultTypes.FiducialResult tag : result.getFiducialResults()) {
                if (tag.getFiducialId() == RobotConfig.DESIRED_TAG_ID) {
                    return tag.getTargetXDegrees();
                }
            }
            return null;
        }
        return result.getTx();
    }

    public void stop() { camera.stop(); }
}
