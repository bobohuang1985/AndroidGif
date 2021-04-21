//// Created by succlz123 on 17-9-5.//#include <jni.h>#include <string>#include <android/bitmap.h>#include <cstring>#include <cstdint>#include "../cpp2/GifEncoder.h"#include "../cpp2/Logger.h"#include "../cpp2/ThreadPool.h"#ifdef __cplusplusextern "C" {#endif#define RGB565_R(p) ((((p) & 0xF800) >> 11) << 3)#define RGB565_G(p) ((((p) & 0x7E0)  >> 5)  << 2)#define RGB565_B(p) (((p)  & 0x1F)          << 3)#define MAKE_BGR(b, g, r) (((b) << 16) | ((g) << 8) | (r))JNIEXPORT jlong JNICALLJava_com_bilibili_burstlinker_BurstLinker_jniInit(JNIEnv *env, jobject, jstring path, jint width,                                                  jint height, jint loopCount, jint threadCount) {    const char *pathStr = env->GetStringUTFChars(path, nullptr);    if (pathStr == nullptr) {        return 0;    }    auto *gifEncoder = new blk::GifEncoder();    bool success = gifEncoder->init(pathStr, (uint16_t) width, (uint16_t) height,                                    (uint32_t) loopCount, (uint32_t) threadCount);    env->ReleaseStringUTFChars(path, pathStr);    if (success) {        return (jlong) gifEncoder;    } else {        delete gifEncoder;        return 0;    }}JNIEXPORT void JNICALLJava_com_bilibili_burstlinker_BurstLinker_jniDebugLog(JNIEnv *env, jobject, jlong handle,                                                      jboolean debug) {    auto *gifEncoder = (blk::GifEncoder *) handle;    gifEncoder->debugLog = debug;}JNIEXPORT jstring JNICALLJava_com_bilibili_burstlinker_BurstLinker_jniConnect(JNIEnv *env, jobject, jlong handle,                                                     jint quantizerType,                                                     jint ditherType, jint ignoreTranslucency,                                                     jint left, jint top, jint delay,                                                     jstring rsCacheDir,                                                     jobject jBitmap) {    if (jBitmap == nullptr) {        return env->NewStringUTF("jBitmap is null");    }    auto *gifEncoder = (blk::GifEncoder *) handle;    AndroidBitmapInfo androidBitmapInfo;    if (AndroidBitmap_getInfo(env, jBitmap, &androidBitmapInfo) < 0) {        return env->NewStringUTF("call AndroidBitmap_getInfo failed");    }    void *src = nullptr;    if (AndroidBitmap_lockPixels(env, jBitmap, &src) < 0) {        return env->NewStringUTF("call AndroidBitmap_lockPixels failed");    }    char *rsCacheDirStr = nullptr;    if (rsCacheDir != nullptr) {        const char *tmp = env->GetStringUTFChars(rsCacheDir, nullptr);        rsCacheDirStr = new char[strlen(tmp)];        strcpy(rsCacheDirStr, tmp);        gifEncoder->rsCacheDir = rsCacheDirStr;        env->ReleaseStringUTFChars(rsCacheDir, tmp);    }    uint16_t width = gifEncoder->screenWidth;    uint16_t height = gifEncoder->screenHeight;    uint32_t imageSize = width * height;    std::vector<uint32_t> dst(imageSize);    int8_t enableTransparency = 0;    bool validFormat = true;    if (androidBitmapInfo.format == ANDROID_BITMAP_FORMAT_RGB_565) {        for (int k = 0; k < imageSize; ++k) {            uint16_t v = *(((uint16_t *) src) + k);            dst[k] = static_cast<uint32_t>(MAKE_BGR(RGB565_B(v), RGB565_G(v), RGB565_R(v)));        }    } else if (androidBitmapInfo.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {        memcpy((void *) &dst[0], src, imageSize * 4);        enableTransparency = 1;    } else {        validFormat = false;    }    AndroidBitmap_unlockPixels(env, jBitmap);    if (!validFormat) {        return env->NewStringUTF("bitmap's format is't RGB_565 or RGBA_8888");    }    int32_t transparencyOption = (ignoreTranslucency << 8) | enableTransparency;    std::vector<uint8_t> out;    gifEncoder->addImage(dst, static_cast<uint32_t>(delay),                         static_cast<blk::QuantizerType>(quantizerType),                         static_cast<blk::DitherType>(ditherType), transparencyOption,                         (uint16_t) left, (uint16_t) top, out);    if (out.empty()) {        return env->NewStringUTF("gifEncoder add image out arrays is empty");    }    gifEncoder->flush(out);    return nullptr;}JNIEXPORT jstring JNICALLJava_com_bilibili_burstlinker_BurstLinker_jniConnectArray(JNIEnv *env, jobject, jlong handle,                                                          jint quantizerType,                                                          jint ditherType, jint ignoreTranslucency,                                                          jint left, jint top, jint delay,                                                          jstring rsCacheDir,                                                          jobjectArray jBitmapArray) {    auto *gifEncoder = (blk::GifEncoder *) handle;    char *rsCacheDirStr = nullptr;    if (rsCacheDir != nullptr) {        const char *tmp = env->GetStringUTFChars(rsCacheDir, nullptr);        rsCacheDirStr = new char[strlen(tmp)];        strcpy(rsCacheDirStr, tmp);        gifEncoder->rsCacheDir = rsCacheDirStr;        env->ReleaseStringUTFChars(rsCacheDir, tmp);    }    std::vector<std::future<std::vector<uint8_t >>> tasks;    jsize count = env->GetArrayLength(jBitmapArray);    for (int i = 0; i < count; i++) {        jobject jBitmap = env->GetObjectArrayElement(jBitmapArray, i);        if (jBitmap == nullptr) {            return env->NewStringUTF("jBitmap is null");        }        AndroidBitmapInfo androidBitmapInfo;        if (AndroidBitmap_getInfo(env, jBitmap, &androidBitmapInfo) < 0) {            env->DeleteLocalRef(jBitmap);            return env->NewStringUTF("call AndroidBitmap_getInfo failed");        }        void *src = nullptr;        if (AndroidBitmap_lockPixels(env, jBitmap, &src) < 0) {            env->DeleteLocalRef(jBitmap);            return env->NewStringUTF("call AndroidBitmap_lockPixels failed");        }        uint16_t width = gifEncoder->screenWidth;        uint16_t height = gifEncoder->screenHeight;        uint32_t imageSize = width * height;        std::vector<uint32_t> dst(imageSize);        int8_t enableTransparency = 0;        bool validFormat = true;        if (androidBitmapInfo.format == ANDROID_BITMAP_FORMAT_RGB_565) {            for (int k = 0; k < imageSize; ++k) {                uint16_t v = *(((uint16_t *) src) + k);                dst[k] = static_cast<uint32_t>(MAKE_BGR(RGB565_B(v), RGB565_G(v), RGB565_R(v)));            }        } else if (androidBitmapInfo.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {            memcpy((void *) &dst[0], src, imageSize * 4);            enableTransparency = 1;        } else {            validFormat = false;        }        AndroidBitmap_unlockPixels(env, jBitmap);        env->DeleteLocalRef(jBitmap);        if (!validFormat) {            return env->NewStringUTF("bitmap's format is't RGB_565 or RGBA_8888");        }        int32_t transparencyOption = (ignoreTranslucency << 8) | enableTransparency;        auto result = gifEncoder->threadPool->enqueue([=]() {            std::vector<uint8_t> out;            gifEncoder->addImage(dst, static_cast<uint32_t>(delay),                                 static_cast<blk::QuantizerType>(quantizerType),                                 static_cast<blk::DitherType>(ditherType),                                 transparencyOption,                                 (uint16_t) left, (uint16_t) top, out);            return out;        });        tasks.emplace_back(std::move(result));    }    for (auto &task : tasks) {        std::vector<uint8_t> out = task.get();        if (out.empty()) {            return env->NewStringUTF("gifEncoder add image out arrays is empty");        }        gifEncoder->flush(out);    }    return nullptr;}JNIEXPORT void JNICALLJava_com_bilibili_burstlinker_BurstLinker_jniRelease(JNIEnv *env, jobject, jlong handle) {    auto *gifEncoder = (blk::GifEncoder *) handle;    if (gifEncoder != nullptr) {        gifEncoder->finishEncoding();        delete gifEncoder;    }}#ifdef __cplusplus}#endif