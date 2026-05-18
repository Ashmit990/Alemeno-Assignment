package com.alemenomarkerapp;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.Arguments;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import org.opencv.core.CvType;
import org.opencv.core.Core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MarkerDetectionModule extends ReactContextBaseJavaModule {
    static {
        System.loadLibrary("opencv_java4");
    }

    public MarkerDetectionModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "MarkerDetectionModule";
    }

    @ReactMethod
    public void processFrame(String imageUri, Promise promise) {
        try {
            String path = imageUri.replace("file://", "");
            Mat src = Imgcodecs.imread(path);
            if (src.empty()) {
                promise.reject("LOAD_ERROR", "Could not load image");
                return;
            }

            Mat gray = new Mat();
            Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.GaussianBlur(gray, gray, new Size(5, 5), 0);
            Mat edges = new Mat();
            Imgproc.Canny(gray, edges, 75, 200);

            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            MatOfPoint2f bestApprox = null;
            double maxArea = 0;

            for (MatOfPoint contour : contours) {
                MatOfPoint2f c2f = new MatOfPoint2f(contour.toArray());
                double peri = Imgproc.arcLength(c2f, true);
                MatOfPoint2f approx = new MatOfPoint2f();
                Imgproc.approxPolyDP(c2f, approx, 0.02 * peri, true);

                if (approx.total() == 4) {
                    double area = Imgproc.contourArea(contour);
                    if (area > 1000 && area > maxArea) {
                        maxArea = area;
                        bestApprox = approx;
                    }
                }
            }

            if (bestApprox != null) {
                // Perspective Transform & Crop to 300x300
                Point[] pts = bestApprox.toArray();
                // Simple ordering: top-left, top-right, bottom-right, bottom-left
                // Note: Real robustness requires sorting points by sum and diff
                
                Mat dst = new Mat(300, 300, src.type());
                MatOfPoint2f srcPts = new MatOfPoint2f(pts);
                MatOfPoint2f dstPts = new MatOfPoint2f(
                    new Point(0, 0),
                    new Point(300, 0),
                    new Point(300, 300),
                    new Point(0, 300)
                );

                Mat M = Imgproc.getPerspectiveTransform(srcPts, dstPts);
                Imgproc.warpPerspective(src, dst, M, new Size(300, 300));

                String outPath = getReactApplicationContext().getCacheDir() + "/marker_" + System.currentTimeMillis() + ".jpg";
                Imgcodecs.imwrite(outPath, dst);
                
                promise.resolve("file://" + outPath);
            } else {
                promise.resolve(null);
            }

        } catch (Exception e) {
            promise.reject("PROCESSS_ERROR", e.getMessage());
        }
    }
}
