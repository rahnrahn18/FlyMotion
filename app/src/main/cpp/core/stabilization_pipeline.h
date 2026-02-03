#ifndef FLYMOTION_STABILIZATION_PIPELINE_H
#define FLYMOTION_STABILIZATION_PIPELINE_H

#include "video_stabilizer.h"
#include <memory>

namespace flymotion {

class StabilizationPipeline {
public:
    StabilizationPipeline();
    ~StabilizationPipeline();

    void init();
    void reset();

    // Pass 1: Analysis
    void pushFrameForAnalysis(const cv::Mat& frame);
    void finalizeAnalysis(int smoothnessRadius);

    // Pass 2: Rendering / Export
    cv::Mat getStabilizedFrame(const cv::Mat& frame, int index);

    bool isReady() const;
    int getFrameCount() const;

private:
    std::unique_ptr<VideoStabilizer> stabilizer;
    bool ready;
};

} // namespace flymotion

#endif //FLYMOTION_STABILIZATION_PIPELINE_H
