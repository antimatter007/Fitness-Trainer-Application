package com.google.mlkit.vision.demo.java.posedetector;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;

import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.ArrayList;
import java.util.List;

public class CheckShaking extends GraphicOverlay.Graphic {

    private final Pose pose;
    private final Paint shakingAlertPaint;
    private static final float POSE_SHAKING_TEXT_SIZE = 60.0f;
    private static List<PoseLandmark> landmarks;
    private static int unstable = 0;
    private static int stable = 0;


    public CheckShaking(GraphicOverlay overlay, Pose pose) {
        super(overlay);
        this.pose = pose;

        shakingAlertPaint = new Paint();
        shakingAlertPaint.setColor(Color.WHITE);
        shakingAlertPaint.setTextSize(POSE_SHAKING_TEXT_SIZE);
        shakingAlertPaint.setShadowLayer(5.0f, 0f, 0f, Color.BLACK);

        landmarks = pose.getAllPoseLandmarks();

    }

    @Override
    public void draw(Canvas canvas) {


        if (landmarks.isEmpty()){
            return;
        }else{

            List<PoseLandmark> prevLandmarks = new ArrayList<>(landmarks);
            landmarks = pose.getAllPoseLandmarks();

            if (shakingCheck(landmarks, prevLandmarks)){
                unstable += 1;
                stable = 0;
            }else {
                unstable -= 0.5;
                stable += 1;
            }
            if (stable > 10){
                unstable = 0;
            }
            if (unstable > 8){
                canvas.drawText("Garbage",
                        60.0f * 0.5f,
                        60.0f * 1.5f + 180.0f,
                        shakingAlertPaint);
            }else {
                canvas.drawText("Real",
                        60.0f * 0.5f,
                        60.0f * 1.5f + 180.0f,
                        shakingAlertPaint);
            }

        }

    }

    double distance(int a, int b){
        float valueX = (landmarks.get(a).getPosition().x - landmarks.get(b).getPosition().x);
        float valueY = (landmarks.get(a).getPosition().y - landmarks.get(b).getPosition().y);
        return Math.sqrt((Math.pow(valueX,2) + Math.pow(valueY,2)));
    }

    boolean checkBetween (float top, float bottom, float left, float right, int point){
        if ((landmarks.get(point).getPosition().x > left) && (landmarks.get(point).getPosition().x < right)
        && (landmarks.get(point).getPosition().y > top) && (landmarks.get(point).getPosition().y < bottom)){
            return true;
        }
        return false;
    }

    boolean shakingCheck(List<PoseLandmark> currentLandmarks, List<PoseLandmark> previousLandmarks){
        double xVar = 0;
        double yVar = 0;
        int numberVisible = 0;
        float yAvgShoulderTop,yAvgShoulderBottom,xAvgShoulderLeft,xAvgShoulderRight;
        yAvgShoulderTop = currentLandmarks.get(12).getPosition().y + currentLandmarks.get(11).getPosition().y;
        yAvgShoulderBottom = currentLandmarks.get(24).getPosition().y + currentLandmarks.get(23).getPosition().y;
        xAvgShoulderLeft = currentLandmarks.get(12).getPosition().x + currentLandmarks.get(24).getPosition().x;
        xAvgShoulderRight = currentLandmarks.get(11).getPosition().x + currentLandmarks.get(23).getPosition().x;

        if (checkBetween(yAvgShoulderTop,yAvgShoulderBottom,xAvgShoulderLeft,xAvgShoulderRight,0)){
            return true;
        }

        int i = 0;
        try {
            for(PoseLandmark current: currentLandmarks){
                if (current.getInFrameLikelihood() > 0.9){
                    numberVisible += 1;
                }
            }
            for (PoseLandmark current: currentLandmarks){
                if (current.getInFrameLikelihood() > 0.9){
                    xVar = Math.min(Math.abs(current.getPosition().x - previousLandmarks.get(i).getPosition().x),
                            0.5 / numberVisible);
                    yVar = Math.min(Math.abs(current.getPosition().y - previousLandmarks.get(i).getPosition().y),
                            0.5 / numberVisible);
                }
                i++;
            }
        }catch (Exception e){
            Log.i("TAG", e.getStackTrace().toString());
        }
        xVar = xVar / distance(2,5);
        yVar = yVar / distance(2,5);

//        Toast.makeText(getApplicationContext(), " Values" + numberVisible, Toast.LENGTH_SHORT).show();//("VALUE", "Values:" + xVar + " " + yVar + " " + xVar * yVar + " " + numberVisible);

        if ((xVar * yVar > 0.1 && numberVisible >= 14) || (xVar * yVar > 0.01 && numberVisible < 14)){
            return true;
        }
        return false;
    }
}
