#ifndef FLYMOTION_MOTION_SMOOTHER_H
#define FLYMOTION_MOTION_SMOOTHER_H

#include "trajectory.h"
#include <vector>
#include <deque>

namespace flymotion {

class MotionSmoother {
public:
    MotionSmoother(int radius = 30);

    // Adds a trajectory point and returns the smoothed point for the (current - radius) frame.
    // NOTE: This introduces latency = radius.
    TrajectoryStep smooth(const TrajectoryStep& current);

    // Offline smoothing: Smooths the entire path at once
    std::vector<TrajectoryStep> smoothPath(const std::vector<TrajectoryStep>& path);

    void setRadius(int r);

private:
    int radius;
    std::deque<TrajectoryStep> window;
    TrajectoryStep sum;
};

} // namespace flymotion

#endif //FLYMOTION_MOTION_SMOOTHER_H
