#ifndef FLYMOTION_MOTION_ESTIMATOR_H
#define FLYMOTION_MOTION_ESTIMATOR_H

#include <opencv2/opencv.hpp>
#include <vector>

namespace flymotion {

class MotionEstimator {
public:
    MotionEstimator();
    ~MotionEstimator();

    // Estimates motion transform (Affine 2x3) between prevFrame and currFrame
    cv::Mat estimateMotion(const cv::Mat& prevFrame, const cv::Mat& currFrame);

    void reset();

private:
    std::vector<cv::Point2f> prevFeatures;
    std::vector<cv::Point2f> currFeatures;
    std::vector<uchar> status;
    std::vector<float> err;

    // Config
    int maxCorners = 200;
    double qualityLevel = 0.01;
    double minDistance = 30;
};

} // namespace flymotion

#endif //FLYMOTION_MOTION_ESTIMATOR_H
