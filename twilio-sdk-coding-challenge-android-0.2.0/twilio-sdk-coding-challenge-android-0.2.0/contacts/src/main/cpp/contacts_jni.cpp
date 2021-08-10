#include "contacts_jni.h"
#include "contacts.h"

#include <codecvt>
#include <locale>

namespace contacts {
    static JavaVM *cachedJVM; // Cache a JavaVM instance here to make callback with. Do not cache JNIEnv as it changes with different active thread.
    static jobject mListener;// Save listener to call back.

    namespace jni {

        extern "C" JNIEXPORT void JNICALL Java_com_contacts_Contacts_nativeInit(JNIEnv *env, jobject listener)
        {
            int status = env->GetJavaVM(&cachedJVM);
            if(status != 0) {
                // Fail to get JVM, TODO: implement some sort of callback to java with failure message
            }
            mListener = listener;
        }

        extern "C" JNIEXPORT jstring JNICALL Java_com_contacts_Contacts_nativeGetVersion(JNIEnv *env,
                                                                                                              jobject jclass) {
            std::wstring_convert<std::codecvt_utf8_utf16<char16_t>, char16_t> convert;
            std::string sdk_version = contacts::Contacts::getVersion();
            auto sdk_version_str = convert.from_bytes(sdk_version);
            jstring jstr = env->NewString(reinterpret_cast<jchar const *>(sdk_version_str.c_str()),
                                          sdk_version_str.size());

            return jstr;
        }

        extern "C" JNIEXPORT jstring JNICALL Java_com_contacts_Contacts_nativeGetContactList(JNIEnv *env,
                                                                                                        jobject jclass) {
            std::wstring_convert<std::codecvt_utf8_utf16<char16_t>, char16_t> convert;
            std::string contactList = contacts::Contacts::getContactsList();
            auto contact_list_str = convert.from_bytes(contactList);
            jstring jstr = env->NewString(reinterpret_cast<jchar const *>(contact_list_str.c_str()),
                                          contact_list_str.size());

            return jstr;
        }

        extern "C" JNIEXPORT jstring JNICALL Java_com_contacts_Contacts_nativeGetUpdatedContactListAfter(JNIEnv *env,
                                                                                             jobject jclass, jstring timestamp) {
            std::wstring_convert<std::codecvt_utf8_utf16<char16_t>, char16_t> convert;
            std::string contactList = contacts::Contacts::getContactsList();
            auto contact_list_str = convert.from_bytes(contactList);
            jstring jstr = env->NewString(reinterpret_cast<jchar const *>(contact_list_str.c_str()),
                                          contact_list_str.size());

            return jstr;
        }

        extern "C" JNIEXPORT jstring JNICALL Java_com_contacts_Contacts_nativeGetLastUpdateTime(JNIEnv *env,
                                                                                             jobject jclass) {
            std::wstring_convert<std::codecvt_utf8_utf16<char16_t>, char16_t> convert;
            std::string lastUpdateTimestamp = contacts::Contacts::getLastUpdatedTime();
            auto last_update_time_str = convert.from_bytes(lastUpdateTimestamp);
            jstring jstr = env->NewString(reinterpret_cast<jchar const *>(last_update_time_str.c_str()),
                                          last_update_time_str.size());

            return jstr;
        }

        extern "C" JNIEXPORT void JNICALL Java_com_contacts_Contacts_nativeAddNewContact(JNIEnv *env, jobject jthis, jstring contactData,
                                                                                         jobject listener) {
            jclass callbackClass = env->GetObjectClass(listener);

            if (callbackClass != nullptr) {
                jmethodID jmethodId = env->GetMethodID(callbackClass, "onNewContactAdded", "(I)V");
                if (jmethodId != nullptr) {
                    // Convert jstring to char*
                    const char *nativeContactData = env->GetStringUTFChars(contactData, 0);
                    int addContactServerResponse =  contacts::Contacts::addNewContact(nativeContactData);
                    env->CallVoidMethod(listener, jmethodId, addContactServerResponse);
                }

            }
        }

    }

    // Call back to java listener when server data is updated
    void onContactUpdated(_jstring *updatedContact_) {
        if (cachedJVM != nullptr && mListener != nullptr) {
            JNIEnv *env; // Get current JNIEvn from JVM
            cachedJVM->AttachCurrentThread(&env, NULL);

            jclass callbackClass = env->GetObjectClass(mListener);
            if (callbackClass != nullptr) {
                jmethodID jmethodId = env->GetMethodID(callbackClass, "onContactUpdated", "([Ljava/lang/String;)V");
                if (jmethodId != nullptr) {
                    // Convert jstring to char*
                    const char *nativeContactData = env->GetStringUTFChars(updatedContact_, 0);
                    env->CallVoidMethod(mListener, jmethodId, nativeContactData);
                }
            }

        }


    }
}