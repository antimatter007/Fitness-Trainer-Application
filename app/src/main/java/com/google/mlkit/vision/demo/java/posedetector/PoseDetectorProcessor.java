/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mlkit.vision.demo.java.posedetector;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.demo.CameraSource;
import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.java.VisionProcessorBase;
import com.google.mlkit.vision.demo.java.posedetector.classification_KNN_RepCounter.PoseClassifierProcessor;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/** A processor to run pose detector. */
public class PoseDetectorProcessor extends VisionProcessorBase<Pose> {

  private static final String TAG = "PoseDetectorProcessor";

  private final PoseDetector detector;

  private final boolean showInFrameLikelihood;
  private final boolean visualizeZ;
  private final boolean rescaleZForVisualization;
  private final boolean runClassification;
  private final boolean isStreamMode;
  private final Context context;
  private final Executor classificationExecutor;
  private final CameraSource cameraSource;

  private PoseClassifierProcessor poseClassifierProcessor;
  private ScreenFaceDistance screenFaceDistance;

  /** Internal class to hold Pose and classification results. */
  protected static class PoseWithClassification {
    private final Pose pose;
    private final List<String> classificationResult;

    public PoseWithClassification(Pose pose, List<String> classificationResult) {
      this.pose = pose;
      this.classificationResult = classificationResult;
    }

    public Pose getPose() {
      return pose;
    }

    public List<String> getClassificationResult() {
      return classificationResult;
    }
  }

  public PoseDetectorProcessor(Context context, PoseDetectorOptionsBase options, boolean showInFrameLikelihood,
      boolean visualizeZ, boolean rescaleZForVisualization, boolean runClassification, boolean isStreamMode, CameraSource cameraSource) {

    super(context);
    this.showInFrameLikelihood = showInFrameLikelihood;
    this.visualizeZ = visualizeZ;
    this.rescaleZForVisualization = rescaleZForVisualization;
    detector = PoseDetection.getClient(options);
    this.runClassification = runClassification;
    this.isStreamMode = isStreamMode;
    this.context = context;
    this.cameraSource = cameraSource;
    classificationExecutor = Executors.newSingleThreadExecutor();
  }

  @Override
  public void stop() {
    super.stop();
    detector.close();
  }

  //---------------Doing Classification on the image and detecting the landmarks----------------------
  @Override
  protected Task<Pose> detectInImage(InputImage image) {

    return detector.process(image);
//        .continueWith(
//            classificationExecutor,
//            task -> {
//              Pose pose = task.getResult();
//              List<String> classificationResult = new ArrayList<>();
//              if (runClassification) {
//                if (poseClassifierProcessor == null) {
//                  poseClassifierProcessor = new PoseClassifierProcessor(context, isStreamMode);
//                }
//                classificationResult = poseClassifierProcessor.getPoseResult(pose);
//              }
//              return new PoseWithClassification(pose, classificationResult);
//            });
  }

  @Override
  protected void onSuccess(@NonNull Pose pose, @NonNull GraphicOverlay graphicOverlay) {

    ScreenFaceDistance screenFaceDistance = new ScreenFaceDistance(graphicOverlay, pose, cameraSource);
    graphicOverlay.add(screenFaceDistance);



//    Task<PoseWithClassification> poseWithClassificationTask = ;
//    poseWithClassificationTask.continueWith(classificationExecutor,
//            task -> {
//              List<String> classificationResult = new ArrayList<>();
//              if (runClassification) {
//                if (poseClassifierProcessor == null) {
//                  poseClassifierProcessor = new PoseClassifierProcessor(context, isStreamMode);
//                }
//                classificationResult = poseClassifierProcessor.getPoseResult(pose);
//              }
//              return new PoseWithClassification(pose, classificationResult);
//            });


//    graphicOverlay.add(
//        new PoseGraphic(
//            graphicOverlay,
//            poseWithClassification.pose,
//            showInFrameLikelihood,
//            visualizeZ,
//            rescaleZForVisualization,
//            poseWithClassification.classificationResult));
//    graphicOverlay.add(
//            new CheckShaking(
//                    graphicOverlay,
//                    poseWithClassification.pose));
//    PoseClassificationProcessor poseClassificationProcessor = new PoseClassificationProcessor(context,showInFrameLikelihood,
//            visualizeZ,rescaleZForVisualization,runClassification,isStreamMode, pose);

  }

  @Override
  protected void onFailure(@NonNull Exception e) {
    Log.e(TAG, "Pose detection failed!", e);
  }


}
