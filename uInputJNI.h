/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class uInputJNI */

#ifndef _Included_uInputJNI
#define _Included_uInputJNI
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     uInputJNI
 * Method:    setup_uinput_device
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_uInputJNI_setup_1uinput_1device
  (JNIEnv *, jobject);

/*
 * Class:     uInputJNI
 * Method:    trigger_single_key_click
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_uInputJNI_trigger_1single_1key_1click
  (JNIEnv *, jobject, jint);

/*
 * Class:     uInputJNI
 * Method:    trigger_axis_X_event
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_uInputJNI_trigger_1axis_1X_1event
  (JNIEnv *, jobject, jint);

/*
 * Class:     uInputJNI
 * Method:    trigger_axis_Y_event
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_uInputJNI_trigger_1axis_1Y_1event
  (JNIEnv *, jobject, jint);

/*
 * Class:     uInputJNI
 * Method:    close_device
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_uInputJNI_close_1device
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
