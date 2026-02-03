#ifndef FLYMOTION_VIDEO_STABILIZER_H
#define FLYMOTION_VIDEO_STABILIZER_H

#include <opencv2/opencv.hpp>
#include <vector>
#include "../motion/motion_estimator.h"
#include "../motion/motion_smoother.h"
#include "../motion/trajectory.h"

namespace flymotion {

class VideoStabilizer {
public:
    VideoStabilizer();
    ~VideoStabilizer();

    void reset();

    // Step 1: Feed frame to calculate motion.
    // Returns the calculated affine transform for this step (relative to prev).
    // This is useful for debugging or live feedback.
    cv::Mat feedFrame(const cv::Mat& frame);

    // Step 2: After all frames fed, smooth the trajectory.
    void smoothTrajectory(int radius);

    // Step 3: Get the stabilization transform for a specific frame index.
    // This transform should be applied to the original frame to stabilize it.
    cv::Mat getTransform(int index);

    // Helper: Apply transform to frame with border handling
    cv::Mat stabilizeFrame(const cv::Mat& frame, int index);

    // Progress info
    size_t getFrameCount() const;

private:
    MotionEstimator motionEstimator;
    MotionSmoother motionSmoother;

    std::vector<cv::Mat> motionTransforms; // Relative motion between frames
    std::vector<TrajectoryStep> trajectory; // Cumulative path
    std::vector<TrajectoryStep> smoothedTrajectory; // Smoothed path

    cv::Mat lastFrame;
    TrajectoryStep currentPos;
};

} // namespace flymotion

#endif //FLYMOTION_VIDEO_STABILIZER_H
