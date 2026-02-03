#include "stabilization_pipeline.h"

namespace flymotion {

StabilizationPipeline::StabilizationPipeline() : ready(false) {
    stabilizer = std::make_unique<VideoStabilizer>();
}

StabilizationPipeline::~StabilizationPipeline() {
}

void StabilizationPipeline::init() {
    reset();
}

void StabilizationPipeline::reset() {
    stabilizer->reset();
    ready = false;
}

void StabilizationPipeline::pushFrameForAnalysis(const cv::Mat& frame) {
    stabilizer->feedFrame(frame);
}

void StabilizationPipeline::finalizeAnalysis(int smoothnessRadius) {
    stabilizer->smoothTrajectory(smoothnessRadius);
    ready = true;
}

cv::Mat StabilizationPipeline::getStabilizedFrame(const cv::Mat& frame, int index) {
    if (!ready) {
        return frame.clone();
    }
    return stabilizer->stabilizeFrame(frame, index);
}

bool StabilizationPipeline::isReady() const {
    return ready;
}

int StabilizationPipeline::getFrameCount() const {
    return (int)stabilizer->getFrameCount();
}

} // namespace flymotion
