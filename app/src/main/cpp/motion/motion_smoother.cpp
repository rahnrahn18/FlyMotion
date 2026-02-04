#include "motion_smoother.h"

namespace flymotion {

MotionSmoother::MotionSmoother(int r) : radius(r), sum(0,0,0) {}

void MotionSmoother::setRadius(int r) {
    radius = r;
}

TrajectoryStep MotionSmoother::smooth(const TrajectoryStep& current) {
    window.push_back(current);
    sum = sum + current;

    if (window.size() > (size_t)(2 * radius + 1)) {
        sum = sum - window.front();
        window.pop_front();
    }

    // If we haven't filled the window yet, just return average of what we have
    // OR return the center element if possible.
    // For simplicity, we return the average of the window.
    return sum / (double)window.size();
}

std::vector<TrajectoryStep> MotionSmoother::smoothPath(const std::vector<TrajectoryStep>& path) {
    std::vector<TrajectoryStep> smoothedPath;
    smoothedPath.resize(path.size());

    for (size_t i = 0; i < path.size(); ++i) {
        double sumX = 0, sumY = 0, sumA = 0;
        int count = 0;

        for (int j = -radius; j <= radius; ++j) {
            if (i + j >= 0 && i + j < path.size()) {
                sumX += path[i + j].x;
                sumY += path[i + j].y;
                sumA += path[i + j].a;
                count++;
            }
        }

        smoothedPath[i] = TrajectoryStep(sumX / count, sumY / count, sumA / count);
    }

    return smoothedPath;
}

} // namespace flymotion
