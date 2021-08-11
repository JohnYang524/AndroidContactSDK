#include <jni.h>
#include <string>

#ifndef CONTACTS_JNI_H_
#define CONTACTS_JNI_H_


namespace contacts {
    
    void onContactUpdated(_jstring *updatedContact_);// Call back to Android SDK to notify server data update
    jstring convertToJString(JNIEnv *env, std::string string);

    namespace jni {
        /* Get version string */
        extern "C" JNIEXPORT jstring JNICALL Java_com_contacts_Contacts_nativeGetVersion(JNIEnv *env,
                                                                                                          jobject jclass);
        /**
         * Android SDK: nativeInit
         * Initialization. Pass in JVM and callback listener.
         * 1. Save JVM instance
         * 2. Create EventListener object and save in vector.
         * */
        extern "C" JNIEXPORT void JNICALL Java_com_contacts_Contacts_nativeAttachListener(JNIEnv *env, jobject jclass, jobject listener);

        /**
         * Android SDK: nativeGetContactList
         * @output jstring a list of all known contacts.
         * */
        extern "C" JNIEXPORT jstring JNICALL Java_com_contacts_Contacts_nativeGetContactList(JNIEnv *env,
                                                                                                         jobject jclass);
        /**
         * Android SDK: nativeGetUpdatedContactListAfter
         * @param timestamp
         * @output jstring: a list of all known contacts updated/created after a certain timestamp
         * */
        extern "C" JNIEXPORT jstring JNICALL Java_com_contacts_Contacts_nativeGetUpdatedContactListAfter(JNIEnv *env,
                                                                                         jobject jclass, jstring timestamp);
        /**
         * Android SDK: nativeGetLastUpdateTime
         * @output jstring: timestamp of last DB server update
         * */
        extern "C" JNIEXPORT jstring JNICALL Java_com_contacts_Contacts_nativeGetLastUpdateTime(JNIEnv *env,
                                                                                             jobject jclass);
        /**
         * Android SDK: nativeAddNewContact
         * @param newContactData
         * @param callback ContactEventListener() -> onNewContactAdded(int responseCode)
         *                 Call back with server response for inserting new contact record
         * */
        extern "C" JNIEXPORT void JNICALL Java_com_contacts_Contacts_nativeAddNewContact(JNIEnv *env,
                                                                                         jobject jclass,
                                                                                         jstring contactData,
                                                                                         jobject listener);
        /**
         * Android SDK: nativeTestCallbackFunc
         * @output jstring: timestamp of last DB server update
         * */
        extern "C" JNIEXPORT void JNICALL Java_com_contacts_Contacts_nativeSimulateContactUpdateEvent(JNIEnv *env, jobject thiz);
    }

}

#endif // CONTACTS_JNI_H_
