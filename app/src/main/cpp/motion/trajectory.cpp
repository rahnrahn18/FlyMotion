#include "trajectory.h"

namespace flymotion {

TrajectoryStep::TrajectoryStep() : x(0), y(0), a(0) {}

TrajectoryStep::TrajectoryStep(double _x, double _y, double _a) : x(_x), y(_y), a(_a) {}

TrajectoryStep TrajectoryStep::operator+(const TrajectoryStep& other) const {
    return TrajectoryStep(x + other.x, y + other.y, a + other.a);
}

TrajectoryStep TrajectoryStep::operator-(const TrajectoryStep& other) const {
    return TrajectoryStep(x - other.x, y - other.y, a - other.a);
}

TrajectoryStep TrajectoryStep::operator*(double val) const {
    return TrajectoryStep(x * val, y * val, a * val);
}

TrajectoryStep TrajectoryStep::operator/(double val) const {
    return TrajectoryStep(x / val, y / val, a / val);
}

} // namespace flymotion
