#include <Helper_jni.h>
#include <opencv2/core.hpp>
#include <opencv2/objdetect.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <string>
#include <vector>

#include <android/log.h>
#include <opencv2/features2d.hpp>
#include <opencv2/imgproc.hpp>

#define LOG_TAG "FaceDetection/DetectionBasedTracker"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

using namespace std;
using namespace cv;

inline void vector_Rect_to_Mat(vector<Rect>& v_rect, Mat& mat)
{
    mat = Mat(v_rect, true);
}

class CascadeDetectorAdapter: public DetectionBasedTracker::IDetector
{
public:
    CascadeDetectorAdapter(cv::Ptr<cv::CascadeClassifier> detector):
            IDetector(),
            Detector(detector)
    {
        LOGD("CascadeDetectorAdapter::Detect::Detect");
        CV_Assert(detector);
    }

    void detect(const cv::Mat &Image, std::vector<cv::Rect> &objects)
    {
        LOGD("CascadeDetectorAdapter::Detect: begin");
        LOGD("CascadeDetectorAdapter::Detect: scaleFactor=%.2f, minNeighbours=%d, minObjSize=(%dx%d), maxObjSize=(%dx%d)", scaleFactor, minNeighbours, minObjSize.width, minObjSize.height, maxObjSize.width, maxObjSize.height);
        Detector->detectMultiScale(Image, objects, scaleFactor, minNeighbours, 0, minObjSize, maxObjSize);
        LOGD("CascadeDetectorAdapter::Detect: end");
    }

    virtual ~CascadeDetectorAdapter()
    {
        LOGD("CascadeDetectorAdapter::Detect::~Detect");
    }

private:
    CascadeDetectorAdapter();
    cv::Ptr<cv::CascadeClassifier> Detector;
};

struct DetectorAgregator
{
    cv::Ptr<CascadeDetectorAdapter> mainDetector;
    cv::Ptr<CascadeDetectorAdapter> trackingDetector;

    cv::Ptr<DetectionBasedTracker> tracker;
    DetectorAgregator(cv::Ptr<CascadeDetectorAdapter>& _mainDetector, cv::Ptr<CascadeDetectorAdapter>& _trackingDetector):
            mainDetector(_mainDetector),
            trackingDetector(_trackingDetector)
    {
        CV_Assert(_mainDetector);
        CV_Assert(_trackingDetector);

        DetectionBasedTracker::Parameters DetectorParams;
        tracker = makePtr<DetectionBasedTracker>(mainDetector, trackingDetector, DetectorParams);
    }
};

JNIEXPORT jlong JNICALL Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeCreateObject
(JNIEnv * jenv, jclass, jstring jFileName, jint faceSize)
{
    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeCreateObject enter");
    const char* jnamestr = jenv->GetStringUTFChars(jFileName, NULL);
    string stdFileName(jnamestr);
    jlong result = 0;

    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeCreateObject");

    try
    {
        cv::Ptr<CascadeDetectorAdapter> mainDetector = makePtr<CascadeDetectorAdapter>(
            makePtr<CascadeClassifier>(stdFileName));
        cv::Ptr<CascadeDetectorAdapter> trackingDetector = makePtr<CascadeDetectorAdapter>(
            makePtr<CascadeClassifier>(stdFileName));
        result = (jlong)new DetectorAgregator(mainDetector, trackingDetector);
        if (faceSize > 0)
        {
            mainDetector->setMinObjectSize(Size(faceSize, faceSize));
            //trackingDetector->setMinObjectSize(Size(faceSize, faceSize));
        }
    }
    catch(const cv::Exception& e)
    {
        LOGD("nativeCreateObject caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeCreateObject caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of DetectionBasedTracker.nativeCreateObject()");
        return 0;
    }

    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeCreateObject exit");
    return result;
}

JNIEXPORT void JNICALL Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDestroyObject
(JNIEnv * jenv, jclass, jlong thiz)
{
    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDestroyObject");

    try
    {
        if(thiz != 0)
        {
            ((DetectorAgregator*)thiz)->tracker->stop();
            delete (DetectorAgregator*)thiz;
        }
    }
    catch(const cv::Exception& e)
    {
        LOGD("nativeestroyObject caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeDestroyObject caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of DetectionBasedTracker.nativeDestroyObject()");
    }
    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDestroyObject exit");
}

JNIEXPORT void JNICALL Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeStart
(JNIEnv * jenv, jclass, jlong thiz)
{
    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeStart");

    try
    {
        ((DetectorAgregator*)thiz)->tracker->run();
    }
    catch(const cv::Exception& e)
    {
        LOGD("nativeStart caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeStart caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of DetectionBasedTracker.nativeStart()");
    }
    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeStart exit");
}

JNIEXPORT void JNICALL Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeStop
(JNIEnv * jenv, jclass, jlong thiz)
{
    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeStop");

    try
    {
        ((DetectorAgregator*)thiz)->tracker->stop();
    }
    catch(const cv::Exception& e)
    {
        LOGD("nativeStop caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeStop caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of DetectionBasedTracker.nativeStop()");
    }
    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeStop exit");
}

JNIEXPORT void JNICALL Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeSetFaceSize
(JNIEnv * jenv, jclass, jlong thiz, jint faceSize)
{
    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeSetFaceSize -- BEGIN");

    try
    {
        if (faceSize > 0)
        {
            ((DetectorAgregator*)thiz)->mainDetector->setMinObjectSize(Size(faceSize, faceSize));
            //((DetectorAgregator*)thiz)->trackingDetector->setMinObjectSize(Size(faceSize, faceSize));
        }
    }
    catch(const cv::Exception& e)
    {
        LOGD("nativeStop caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeSetFaceSize caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of DetectionBasedTracker.nativeSetFaceSize()");
    }
    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeSetFaceSize -- END");
}


JNIEXPORT void JNICALL Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDetect
(JNIEnv * jenv, jclass, jlong thiz, jlong imageGray, jlong faces)
{
    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDetect");

    try
    {
        vector<Rect> RectFaces;
        ((DetectorAgregator*)thiz)->tracker->process(*((Mat*)imageGray));
        ((DetectorAgregator*)thiz)->tracker->getObjects(RectFaces);
        *((Mat*)faces) = Mat(RectFaces, true);
    }
    catch(const cv::Exception& e)
    {
        LOGD("nativeCreateObject caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeDetect caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code DetectionBasedTracker.nativeDetect()");
    }
    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDetect END");
}

JNIEXPORT void JNICALL Java_com_test_RemapHelper_nativeRemap(JNIEnv*, jclass,jlong srcAddress,jlong map_x_address,jlong map_y_address,jint type)
{
    Mat& src  = *(Mat*)srcAddress;
    Mat& map_x  = *(Mat*)map_x_address;
    Mat& map_y  = *(Mat*)map_y_address;
    switch(type){
        case 11:
            for( int j = 0; j < src.rows; j++ ) {
                for (int i = 0; i < src.cols; i++) {
                    if(j>src.rows/2){
                        map_x.at<float>(j,i) = i ;
                        map_y.at<float>(j,i) = src.rows - j ;
                    }else{
                        map_x.at<float>(j,i) = i ;
                        map_y.at<float>(j,i) = j ;
                    }
                }
            }
            break;
        case 12:
            for( int j = 0; j < src.rows; j++ ) {
                for (int i = 0; i < src.cols; i++) {
                    if(j>src.rows/2){
                        map_x.at<float>(j,i) = i ;
                        map_y.at<float>(j,i) = j -src.rows/2;
                    }else{
                        map_x.at<float>(j,i) = i ;
                        map_y.at<float>(j,i) = src.rows/2-j ;
                    }
                }
            }
            break;
        case 21:
            for( int j = 0; j < src.rows; j++ ) {
                for (int i = 0; i < src.cols; i++) {
                    if(i>src.cols/2){
                        map_x.at<float>(j,i) =src.cols-i ;
                        map_y.at<float>(j,i) = j ;
                    }else{
                        map_x.at<float>(j,i) = i ;
                        map_y.at<float>(j,i) = j ;
                    }
                }
            }
            break;
        case 22:
            for( int j = 0; j < src.rows; j++ ) {
                for (int i = 0; i < src.cols; i++) {
                    if(i>src.cols/2){
                        map_x.at<float>(j,i) = i-src.cols/2;
                        map_y.at<float>(j,i) = j ;
                    }else{
                        map_x.at<float>(j,i) = src.cols/2-i ;
                        map_y.at<float>(j,i) = j ;
                    }
                }
            }
            break;
        case 31:
            for( int j = 0; j < src.rows; j++ ) {
                for (int i = 0; i < src.cols; i++) {
                    if(i>src.cols/2){
                        map_x.at<float>(j,i) = src.cols-i ;
                    }else{
                        map_x.at<float>(j,i) =i ;
                    }
                    if(j>src.rows/2){
                        map_y.at<float>(j,i) = src.rows - j ;
                    }else{
                        map_y.at<float>(j,i) = j ;
                    }
                }
            }
            break;
        case 32:
            for( int j = 0; j < src.rows; j++ ) {
                for (int i = 0; i < src.cols; i++) {
                    if(i>src.cols/2){
                        map_x.at<float>(j,i) = i-src.cols/2 ;
                    }else{
                        map_x.at<float>(j,i) =src.cols/2-i ;
                    }
                    if(j>src.rows/2){
                        map_y.at<float>(j,i) = src.rows - j ;
                    }else{
                        map_y.at<float>(j,i) = j ;
                    }
                }
            }
            break;
        case 33:
            for( int j = 0; j < src.rows; j++ ) {
                for (int i = 0; i < src.cols; i++) {
                    if(i>src.cols/2){
                        map_x.at<float>(j,i) = src.cols-i ;
                    }else{
                        map_x.at<float>(j,i) =i ;
                    }
                    if(j>src.rows/2){
                        map_y.at<float>(j,i) = j-src.rows/2 ;
                    }else{
                        map_y.at<float>(j,i) = src.rows/2-j ;
                    }
                }
            }
            break;
        case 34:
            for( int j = 0; j < src.rows; j++ ) {
                for (int i = 0; i < src.cols; i++) {
                    if(i>src.cols/2){
                        map_x.at<float>(j,i) = i-src.cols/2 ;
                    }else{
                        map_x.at<float>(j,i) =src.cols/2-i ;
                    }
                    if(j>src.rows/2){
                        map_y.at<float>(j,i) = j-src.rows/2 ;
                    }else{
                        map_y.at<float>(j,i) = src.rows/2-j ;
                    }
                }
            }
            break;
    }
}
JNIEXPORT void JNICALL Java_com_test_RemapHelper_nativeRotate(JNIEnv*, jclass,jlong srcAddress,jlong dstAddress,jint angle) {
    Mat& src  = *(Mat*)srcAddress;
    Mat& dst  = *(Mat*)dstAddress;
    float radian = (float) (angle /180.0 * CV_PI);

    //填充图像
    int maxBorder =(int) (max(src.cols, src.rows)* 1.414 ); //即为sqrt(2)*max
    int dx = (maxBorder - src.cols)/2;
    int dy = (maxBorder - src.rows)/2;
    copyMakeBorder(src, dst, dy, dy, dx, dx, BORDER_CONSTANT);

    //旋转
    Point2f center( (float)(dst.cols/2) , (float) (dst.rows/2));
    Mat affine_matrix = getRotationMatrix2D( center, angle, 1.0 );//求得旋转矩阵
    warpAffine(dst, dst, affine_matrix, dst.size());

    //计算图像旋转之后包含图像的最大的矩形
    float sinVal = abs(sin(radian));
    float cosVal = abs(cos(radian));
    Size targetSize( (int)(src.cols * cosVal +src.rows * sinVal),
                     (int)(src.cols * sinVal + src.rows * cosVal) );

    //剪掉多余边框
    int x = (dst.cols - targetSize.width) / 2;
    int y = (dst.rows - targetSize.height) / 2;
    Rect rect(x, y, targetSize.width, targetSize.height);
    dst = Mat(dst,rect);
}
JNIEXPORT void JNICALL Java_com_test_RemapHelper_overlayImage(JNIEnv*, jclass,jlong srcAddress,jlong dstAddress, jint locaton_x,jint location_y)
{
    Mat& first  = *(Mat*)srcAddress;
    Mat& second  = *(Mat*)dstAddress;

    Mat* src  = &first;
    Mat* overlay = &second;
    for (int y = max(location_y, 0); y < src->rows; ++y)
    {
        int fY = y - location_y;

        if (fY >= overlay->rows)
            break;

        for (int x = max(locaton_x, 0); x < src->cols; ++x)
        {
            int fX = x - locaton_x;

            if (fX >= overlay->cols)
                break;

            double opacity = ((double)overlay->data[fY * overlay->step + fX * overlay->channels() + 3]) / 255;

            for (int c = 0; opacity > 0 && c < src->channels(); ++c)
            {
                unsigned char overlayPx = overlay->data[fY * overlay->step + fX * overlay->channels() + c];
                unsigned char srcPx = src->data[y * src->step + x * src->channels() + c];
                src->data[y * src->step + src->channels() * x + c] = srcPx * (1. - opacity) + overlayPx * opacity;
            }
        }
    }
}
