package com.google.mlkit.vision.demo.java.posedetector;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.Camera;
import android.util.Log;

import com.google.mlkit.vision.demo.CameraSource;
import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.java.posedetector.classification_KNN_RepCounter.PoseClassifierProcessor;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.List;

public class ScreenFaceDistance extends GraphicOverlay.Graphic {

    private final Pose pose;
    private final GraphicOverlay overlay;
    static final int AVERAGE_EYE_DISTANCE =63; // in mm
    float F =1f;    //focal length
    final int IMAGE_WIDTH;
    final int IMAGE_HEIGHT;
    private boolean counterFlag = false;


    //camera sensor dimensions
    float sensorX, sensorY;
    float angleX, angleY;
    private final CameraSource cameraSource;

    private final Paint shakingAlertPaint;
    private static final float POSE_SHAKING_TEXT_SIZE = 60.0f;

    //private PoseClassifierProcessor poseClassifierProcessor;


    public ScreenFaceDistance(GraphicOverlay overlay, Pose pose, CameraSource cameraSource) {
        super(overlay);
        this.pose = pose;
        this.overlay = overlay;
        this.cameraSource = cameraSource;

        IMAGE_WIDTH = overlay.getImageWidth();
        IMAGE_HEIGHT = overlay.getImageHeight();

        shakingAlertPaint = new Paint();
        shakingAlertPaint.setColor(Color.WHITE);
        shakingAlertPaint.setTextSize(POSE_SHAKING_TEXT_SIZE);
        shakingAlertPaint.setShadowLayer(5.0f, 0f, 0f, Color.BLACK);
    }

    @Override
    public void draw(Canvas canvas) {

        List<PoseLandmark> landmarks = pose.getAllPoseLandmarks();
        if (landmarks.isEmpty()) {
            return;
        }

        Camera camera = cameraSource.getCamera();
        if (camera != null){
            Camera.Parameters camper =camera.getParameters();
            F = camper.getFocalLength();
            angleX = camper.getHorizontalViewAngle();
            angleY = camper.getVerticalViewAngle();
            sensorX = (float) (Math.tan(Math.toRadians(angleX / 2)) * 2 * F);
            sensorY = (float) (Math.tan(Math.toRadians(angleY / 2)) * 2 * F);
        }

        PoseLandmark leftEye = pose.getPoseLandmark(PoseLandmark.LEFT_EYE);
        PoseLandmark rightEye = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE);

        PointF leftEyePosition = leftEye.getPosition();
        PointF rightEyePosition = rightEye.getPosition();

        float deltaX = Math.abs(leftEyePosition.x - rightEyePosition.x);
        float deltaY = Math.abs(leftEyePosition.y - rightEyePosition.y);

        float distance;
        if (deltaX >= deltaY) {
            distance = F * (AVERAGE_EYE_DISTANCE / sensorX) * (IMAGE_WIDTH / deltaX);
        } else {
            distance = F * (AVERAGE_EYE_DISTANCE / sensorY) * (IMAGE_HEIGHT / deltaY);
        }

        canvas.drawText(Float.toString(distance),
                60.0f * 0.5f,
                60.0f * 1.5f + 300.0f,
                shakingAlertPaint);

        //@TODO: Make sure to close the counter when a person is not between 1000 to 1500 range.

        if (distance > 500 && distance < 600){
            counterFlag = true;

        }else{
            counterFlag = false;
        }

//        Log.i("COUNTER", Integer.toString(counterFlag));

        //poseClassifierProcessor.setCounterFlag(counterFlag);

    }

    public boolean isCounterFlag() {
        return counterFlag;
    }
}
