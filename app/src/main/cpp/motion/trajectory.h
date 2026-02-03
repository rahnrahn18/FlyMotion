#ifndef FLYMOTION_TRAJECTORY_H
#define FLYMOTION_TRAJECTORY_H

namespace flymotion {

struct TrajectoryStep {
    double x;
    double y;
    double a; // Angle in radians

    TrajectoryStep();
    TrajectoryStep(double _x, double _y, double _a);

    TrajectoryStep operator+(const TrajectoryStep& other) const;
    TrajectoryStep operator-(const TrajectoryStep& other) const;
    TrajectoryStep operator*(double val) const;
    TrajectoryStep operator/(double val) const;
};

} // namespace flymotion

#endif //FLYMOTION_TRAJECTORY_H
