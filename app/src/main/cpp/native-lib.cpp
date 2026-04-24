#include <jni.h>
#include <string>
#include <vector>
#include <android/log.h>
#include "llama.h"
#include "common.h"

#define TAG "OllamaCCP_Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

static llama_model* g_model = nullptr;
static llama_context* g_context = nullptr;

extern "C" JNIEXPORT jboolean JNICALL
Java_com_ollama_ccp_core_LLMEngine_loadModel(JNIEnv* env, jobject thiz, jstring modelPath) {
    const char* path = env->GetStringUTFChars(modelPath, nullptr);

    llama_backend_init();

    llama_model_params model_params = llama_model_default_params();
    g_model = llama_model_load_from_file(path, model_params);

    env->ReleaseStringUTFChars(modelPath, path);

    if (!g_model) {
        LOGE("Failed to load model");
        return JNI_FALSE;
    }

    llama_context_params ctx_params = llama_context_default_params();
    ctx_params.n_ctx = 2048;
    ctx_params.n_threads = 4;

    g_context = llama_init_from_model(g_model, ctx_params);
    if (!g_context) {
        LOGE("Failed to create context");
        llama_model_free(g_model);
        g_model = nullptr;
        return JNI_FALSE;
    }

    LOGI("Model loaded successfully");
    return JNI_TRUE;
}

extern "C" JNIEXPORT void JNICALL
Java_com_ollama_ccp_core_LLMEngine_unloadModel(JNIEnv* env, jobject thiz) {
    if (g_context) {
        llama_free(g_context);
        g_context = nullptr;
    }
    if (g_model) {
        llama_model_free(g_model);
        g_model = nullptr;
    }
    llama_backend_free();
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_ollama_ccp_core_LLMEngine_generate(JNIEnv* env, jobject thiz, jstring prompt) {
    if (!g_context) return env->NewStringUTF("Error: Model not loaded");

    const char* p_prompt = env->GetStringUTFChars(prompt, nullptr);

    // Minimal generation logic for verification
    // In a real app, this would be a loop with streaming callbacks

    std::string result = "Native echo: ";
    result += p_prompt;

    env->ReleaseStringUTFChars(prompt, p_prompt);
    return env->NewStringUTF(result.c_str());
}
