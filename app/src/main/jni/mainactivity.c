#include <jni.h>
#include <stdio.h>
#include <android/log.h>
#include "libmp3lame/lame.h"
#define LOG_TAG "lame"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
int flage=0;
JNIEXPORT void JNICALL
Java_beyondboy_scau_com_jniproject_MainActivity_convertmp3(JNIEnv *env, jobject instance,
                                                           jstring war_, jstring mp3_)
{
    //转换文件路径
    const char *war = (*env)->GetStringUTFChars(env, war_, 0);
    const char *mp3 = (*env)->GetStringUTFChars(env, mp3_, 0);
    FILE * fwav=fopen(war,"rb");
    FILE * fmp3=fopen(mp3,"wb");
    short int wav_buffer[8192*2];
    unsigned char mp3_buffer[8192];
    //初始化编码器
    lame_t  lame=lame_init();
    //设置lame mp3编码的采样率
    lame_set_in_samplerate(lame,441000);
    //设置双声道
    lame_set_num_channels(lame,2);
    // 3. 设置MP3的编码方式
    lame_set_VBR(lame, vbr_default);
    lame_init_params(lame);
    LOGI("lame init finish");
    int read ; int write; //代表读了多少个次 和写了多少次
    int total=0; // 当前读的wav文件的byte数目
    do
    {
        LOGI("start converting");
        if (flage==404)
        {
           /* lame_close(lame);
            fclose(fwav);
            fclose(fmp3);*/
            LOGD("跳到这里");
            publishJavaProgress(env,instance,0);
            break;
        }
        read=fread(wav_buffer, sizeof(short int)*2,8192,fwav);
        total+=read*sizeof(short int)*2;
        publishJavaProgress(env,instance,total);
        LOGI("converting ........%d",total);
        if (read!=0)
        {
            write=lame_encode_buffer_interleaved(lame,wav_buffer,read,mp3_buffer,8192);
            //把转化后的mp3数据写到文件里
            fwrite(mp3_buffer,sizeof(unsigned char),write,fmp3);
        }
        else if(read==0)
        {
            //刷新缓存
            lame_encode_flush(lame,mp3_buffer,8192);
            //publishJavaProgress(env,instance,0);
        }
    }while(read!=0);
    lame_close(lame);
    fclose(fwav);
    fclose(fmp3);
    //total=0;
    (*env)->ReleaseStringUTFChars(env, war_, war);
    (*env)->ReleaseStringUTFChars(env, mp3_, mp3);
    LOGI("convert finish");
}
// 定义静态变量可以起到缓存作用，减少时间的消耗
static jmethodID method_id=NULL;
void publishJavaProgress(JNIEnv *env,jobject jobject1,jint propress)
{
    /*//找到java的class
    jclass clazz=(*env)->FindClass(env,"beyondboy/scau/com/jniproject/MainActivity");
    if (clazz == 0)
    {
		LOGI("can't find clazz");
	}
    if (method_id==NULL)
    {*/
        /*//2 找到class 里面的方法定义
        method_id=(*env)->GetMethodID(env,clazz,"setConvertProgress","(I)V");
        // 再次判断是否找到该类的str字段
        if (method_id ==NULL)
        {
            LOGI("can't find methodid");
        }*/
 //  }
    //3.利用静态缓存ID,调用方法
    (*env)->CallVoidMethod(env,jobject1,method_id,propress);
}
JNIEXPORT void JNICALL
Java_beyondboy_scau_com_jniproject_MainActivity_stop(JNIEnv *env, jobject instance)
{
    flage=404;
}
JNIEXPORT jstring JNICALL
Java_beyondboy_scau_com_jniproject_MainActivity_getLameVersion(JNIEnv *env, jobject instance)
{
    return (*env)->NewStringUTF(env,get_lame_version());
}
JNIEXPORT void JNICALL
Java_beyondboy_scau_com_jniproject_MainActivity_initIDs(JNIEnv *env, jclass type)
{
    //获取方法ID
    method_id=(*env)->GetMethodID(env,type,"setConvertProgress","(I)V");
}

JNIEXPORT void JNICALL
Java_beyondboy_scau_com_jniproject_MainActivity_start(JNIEnv *env, jobject instance)
{
    flage=0;
}