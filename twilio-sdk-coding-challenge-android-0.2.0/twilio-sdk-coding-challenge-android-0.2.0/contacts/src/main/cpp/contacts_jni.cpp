#include "contacts_jni.h"
#include "contacts.h"
#include <android/log.h>
#include "EventListener.h"
#include <vector>

#include <codecvt>
#include <locale>

namespace contacts {

    static JavaVM *cachedJVM; // Cache a JavaVM instance here to make callback with. Do not cache JNIEnv as it changes with different active thread.
    std::vector<EventListener *> eventListeners; // Save all listeners to call back to.

    namespace jni {

        extern "C" JNIEXPORT void JNICALL Java_com_contacts_Contacts_nativeAttachListener(JNIEnv *env, jobject jclazz, jobject listener)
        {
            // Save JVM instance
            int status = env->GetJavaVM(&cachedJVM);
            if(status != 0) {
                // Fail to get JVM
                // TODO: notify Java for init failure
            }
            // Create EventListener object and save in EventListener vector
            jweak mListener = env->NewWeakGlobalRef(listener);
            jclass clazz = env->GetObjectClass(listener);
            jmethodID mMethodID = env->GetMethodID(clazz, "onContactUpdated", "(Ljava/lang/String;)V");

            EventListener *eventListener = new EventListener(mListener, mMethodID);
            eventListeners.push_back(eventListener);

            // Add to eventListener vector in contacts.cpp
            contacts::Contacts::addEventListener(eventListener);
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

        extern "C" JNIEXPORT void JNICALL Java_com_contacts_Contacts_nativeSimulateContactUpdateEvent(JNIEnv *env,
                                                                                            jobject thiz) {
            //__android_log_write(ANDROID_LOG_VERBOSE, "JNIClass", "nativeTestCallbackFunc");
            jstring testData = convertToJString(env, contacts::Contacts::getTestDataForCallbackTest());
            onContactUpdated(testData); // Verify Java listeners get notified
        }

    }

    /**
     * Util method to convert std::string to jstring
     * */
    jstring convertToJString(JNIEnv *env, std::string string) {
        std::wstring_convert<std::codecvt_utf8_utf16<char16_t>, char16_t> convert;
        auto str = convert.from_bytes(string);
        jstring jstr = env->NewString(reinterpret_cast<jchar const *>(str.c_str()),
                                      str.size());
        return jstr;
    }

    /**
     * Call back to java listener when server data is updated
     * Step1: Get current JNIEvn from JVM
     * Step2: Notify all listeners stored in vector, even though in our test case there is only 1 listener.
     * Step3: Detach current thread
     * */
    //
    void onContactUpdated(_jstring *updatedContact_) {
        //__android_log_write(ANDROID_LOG_VERBOSE, "JNIClass", "onContactUpdated");
        if (cachedJVM != nullptr) {
            JNIEnv *env; // Get current JNIEvn from JVM

            JavaVMAttachArgs args;
            args.version = JNI_VERSION_1_6; // set JNI version
            args.name = NULL; // We might want to give the java thread a name
            args.group = NULL; // we might want to assign the java thread to a ThreadGroup

            int getEnvStat = cachedJVM->GetEnv((void **)&env, JNI_VERSION_1_6);
            if (getEnvStat == JNI_EDETACHED) {
                if (cachedJVM->AttachCurrentThread(&env, &args) != 0) {
                    //__android_log_write(ANDROID_LOG_VERBOSE, "JNIClass", "Failed to attach.");
                    return;
                }
            } else if (getEnvStat == JNI_OK) {
                //__android_log_write(ANDROID_LOG_VERBOSE, "JNIClass", "Already attached.");
            } else if (getEnvStat == JNI_EVERSION) {
            }

            // Notify all listeners, even though in our test case there is only 1 listener.
            if (!eventListeners.empty()) {
                for (EventListener *listener : eventListeners) {
                    env->CallVoidMethod(listener->mListener,
                                        listener->mMethodID, updatedContact_);
                }
            }

            if (env->ExceptionCheck()) {
                env->ExceptionDescribe();
            }
            // Detach
            if (getEnvStat == JNI_EDETACHED) {
                cachedJVM->DetachCurrentThread();
            }
        }

    }
}