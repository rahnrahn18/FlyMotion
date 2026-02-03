#include "video_stabilizer.h"
#include <cmath>

namespace flymotion {

VideoStabilizer::VideoStabilizer() : currentPos(0,0,0) {
}

VideoStabilizer::~VideoStabilizer() {
}

void VideoStabilizer::reset() {
    motionEstimator.reset();
    motionTransforms.clear();
    trajectory.clear();
    smoothedTrajectory.clear();
    currentPos = TrajectoryStep(0, 0, 0);
    lastFrame.release();
}

cv::Mat VideoStabilizer::feedFrame(const cv::Mat& frame) {
    if (frame.empty()) return cv::Mat::eye(2, 3, CV_64F);

    cv::Mat transform;

    if (lastFrame.empty()) {
        transform = cv::Mat::eye(2, 3, CV_64F);
    } else {
        transform = motionEstimator.estimateMotion(lastFrame, frame);
    }

    frame.copyTo(lastFrame);

    // Extract dx, dy, da from transform
    // [ cos(a) -sin(a) x ]
    // [ sin(a)  cos(a) y ]
    double dx = transform.at<double>(0, 2);
    double dy = transform.at<double>(1, 2);
    double da = atan2(transform.at<double>(1, 0), transform.at<double>(0, 0));

    // Store relative motion
    motionTransforms.push_back(transform);

    // Update cumulative trajectory
    currentPos.x += dx;
    currentPos.y += dy;
    currentPos.a += da;

    trajectory.push_back(currentPos);

    return transform;
}

void VideoStabilizer::smoothTrajectory(int radius) {
    motionSmoother.setRadius(radius);
    smoothedTrajectory = motionSmoother.smoothPath(trajectory);
}

cv::Mat VideoStabilizer::getTransform(int index) {
    if (index < 0 || index >= trajectory.size() || index >= smoothedTrajectory.size()) {
        return cv::Mat::eye(2, 3, CV_64F);
    }

    TrajectoryStep original = trajectory[index];
    TrajectoryStep smoothed = smoothedTrajectory[index];

    double diffX = smoothed.x - original.x;
    double diffY = smoothed.y - original.y;
    double diffA = smoothed.a - original.a;

    double dx = diffX;
    double dy = diffY;
    double da = diffA;

    // Construct the warp matrix to bring original to smoothed
    cv::Mat T = cv::Mat::zeros(2, 3, CV_64F);
    T.at<double>(0, 0) = cos(da);
    T.at<double>(0, 1) = -sin(da);
    T.at<double>(1, 0) = sin(da);
    T.at<double>(1, 1) = cos(da);
    T.at<double>(0, 2) = dx;
    T.at<double>(1, 2) = dy;

    return T;
}

cv::Mat VideoStabilizer::stabilizeFrame(const cv::Mat& frame, int index) {
    cv::Mat T = getTransform(index);
    cv::Mat stabilized;
    cv::warpAffine(frame, stabilized, T, frame.size());
    return stabilized;
}

size_t VideoStabilizer::getFrameCount() const {
    return trajectory.size();
}

} // namespace flymotion
