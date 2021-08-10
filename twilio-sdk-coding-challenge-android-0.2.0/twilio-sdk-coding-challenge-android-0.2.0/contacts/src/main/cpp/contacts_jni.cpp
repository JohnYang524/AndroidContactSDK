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
                // Fail to get JVM
                // TODO: notify Java for init failure
            }
            mListener = listener;
        }

        extern "C" JNIEXPORT jstring JNICALL Java_com_contacts_Contacts_nativeGetVersion(JNIEnv *env, jobject jclass) {

            std::string sdk_version = contacts::Contacts::getVersion();
            return convertToJString(env, sdk_version);
        }

        extern "C" JNIEXPORT jstring JNICALL Java_com_contacts_Contacts_nativeGetContactList(JNIEnv *env,
                                                                                                        jobject jclass) {

            std::string contactList = contacts::Contacts::getContactsList();
            return convertToJString(env, contactList);
        }

        extern "C" JNIEXPORT jstring JNICALL Java_com_contacts_Contacts_nativeGetUpdatedContactListAfter(JNIEnv *env,
                                                                                             jobject jclass, jstring timestamp) {
            // Convert jstring to char*
            const char *nativeTimestamp = env->GetStringUTFChars(timestamp, 0);
            std::string contactList = contacts::Contacts::getUpdatedContactsListAfter(nativeTimestamp);
            return convertToJString(env, contactList);
        }

        extern "C" JNIEXPORT jstring JNICALL Java_com_contacts_Contacts_nativeGetLastUpdateTime(JNIEnv *env,
                                                                                             jobject jclass) {

            std::string contactList = contacts::Contacts::getLastUpdatedTime();
            return convertToJString(env, contactList);
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

        extern "C" JNIEXPORT void JNICALL Java_com_contacts_Contacts_nativeTestCallbackFunc(JNIEnv *env) {
            jstring testData = convertToJString(env, contacts::Contacts::getTestDataForCallbackTest());
            onContactUpdated(testData); // Verify Java listener gets notified
        }

    }

    // Util method to convert std::string to jstring
    jstring convertToJString(JNIEnv *env, std::string string) {
        std::wstring_convert<std::codecvt_utf8_utf16<char16_t>, char16_t> convert;
        auto str = convert.from_bytes(string);
        jstring jstr = env->NewString(reinterpret_cast<jchar const *>(str.c_str()),
                                      str.size());
        return jstr;
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