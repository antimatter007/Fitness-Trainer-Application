package com.google.mlkit.vision.demo.java.posedetector;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.java.VisionProcessorBase;
import com.google.mlkit.vision.demo.java.posedetector.classification_KNN_RepCounter.PoseClassifierProcessor;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PoseClassificationProcessor extends VisionProcessorBase<PoseClassificationProcessor.PoseWithClassification> {

    private final boolean showInFrameLikelihood;
    private final boolean visualizeZ;
    private final boolean rescaleZForVisualization;
    private final boolean runClassification;
    private final boolean isStreamMode;
    private final Context context;
    private final Executor classificationExecutor;
    private Pose pose;

    private PoseClassifierProcessor poseClassifierProcessor;
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

    public PoseClassificationProcessor (Context context, boolean showInFrameLikelihood,
                                        boolean visualizeZ, boolean rescaleZForVisualization, boolean runClassification,
                                        boolean isStreamMode, Pose pose){
        super(context);
        this.showInFrameLikelihood = showInFrameLikelihood;
        this.visualizeZ = visualizeZ;
        this.rescaleZForVisualization = rescaleZForVisualization;
        this.runClassification = runClassification;
        this.isStreamMode = isStreamMode;
        this.context = context;
        this.pose = pose;
        classificationExecutor = Executors.newSingleThreadExecutor();

    }

    @Override
    protected Task<PoseWithClassification> detectInImage(InputImage image) {

        return null;


    }

    @Override
    protected void onSuccess(@NonNull @NotNull PoseWithClassification results, @NonNull @NotNull GraphicOverlay graphicOverlay) {

    }


    @Override
    protected void onFailure(@NonNull @NotNull Exception e) {

    }
}
