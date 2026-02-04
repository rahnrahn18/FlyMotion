#include "motion_estimator.h"
#include <opencv2/video/tracking.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/calib3d.hpp>

namespace flymotion {

MotionEstimator::MotionEstimator() {
    // Constructor
}

MotionEstimator::~MotionEstimator() {
    // Destructor
}

void MotionEstimator::reset() {
    prevFeatures.clear();
    currFeatures.clear();
}

cv::Mat MotionEstimator::estimateMotion(const cv::Mat& prevFrame, const cv::Mat& currFrame) {
    if (prevFrame.empty() || currFrame.empty()) {
        return cv::Mat::eye(2, 3, CV_64F);
    }

    cv::Mat prevGray, currGray;

    // Convert to grayscale if needed
    if (prevFrame.channels() == 3 || prevFrame.channels() == 4) {
        cv::cvtColor(prevFrame, prevGray, cv::COLOR_BGR2GRAY);
    } else {
        prevGray = prevFrame;
    }

    if (currFrame.channels() == 3 || currFrame.channels() == 4) {
        cv::cvtColor(currFrame, currGray, cv::COLOR_BGR2GRAY);
    } else {
        currGray = currFrame;
    }

    // Detect features if we don't have enough from previous frame
    if (prevFeatures.size() < 50) {
        cv::goodFeaturesToTrack(prevGray, prevFeatures, maxCorners, qualityLevel, minDistance);
    }

    if (prevFeatures.empty()) {
        // Fallback: no features, no motion
        return cv::Mat::eye(2, 3, CV_64F);
    }

    // Calculate Optical Flow
    cv::calcOpticalFlowPyrLK(prevGray, currGray, prevFeatures, currFeatures, status, err);

    // Filter good points
    std::vector<cv::Point2f> goodPrev, goodCurr;
    for (size_t i = 0; i < status.size(); i++) {
        if (status[i]) {
            goodPrev.push_back(prevFeatures[i]);
            goodCurr.push_back(currFeatures[i]);
        }
    }

    cv::Mat transform = cv::Mat::eye(2, 3, CV_64F);

    if (goodPrev.size() > 10) { // Require minimum points for stable estimation
        // Estimate Rigid (Rotation + Translation) or Affine transform
        // estimateAffinePartial2D is robust for R+T
        transform = cv::estimateAffinePartial2D(goodPrev, goodCurr);

        if (transform.empty()) {
             transform = cv::Mat::eye(2, 3, CV_64F);
        }
    }

    // Update features for next iteration
    // Use the current good features as the previous features for the next step
    prevFeatures = goodCurr;

    return transform;
}

} // namespace flymotion
