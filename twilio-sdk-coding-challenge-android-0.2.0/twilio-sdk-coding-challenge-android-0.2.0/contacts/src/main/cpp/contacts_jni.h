#include <jni.h>

#ifndef CONTACTS_JNI_H_
#define CONTACTS_JNI_H_


namespace contacts {
namespace jni {

extern "C" JNIEXPORT jstring JNICALL Java_com_contacts_Contacts_00024Companion_nativeGetVersion(JNIEnv *env,
                                                                                                jobject jclass);

}
}

#endif // CONTACTS_JNI_H_
