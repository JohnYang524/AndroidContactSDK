#include "contacts_jni.h"
#include "contacts.h"

#include <codecvt>
#include <locale>

namespace contacts {
    namespace jni {

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

        extern "C" JNIEXPORT jstring JNICALL Java_com_contacts_Contacts_nativeGetLastUpdateTime(JNIEnv *env,
                                                                                             jobject jclass) {
            std::wstring_convert<std::codecvt_utf8_utf16<char16_t>, char16_t> convert;
            std::string lastUpdateTimestamp = contacts::Contacts::getLastUpdatedTime();
            auto last_update_time_str = convert.from_bytes(lastUpdateTimestamp);
            jstring jstr = env->NewString(reinterpret_cast<jchar const *>(last_update_time_str.c_str()),
                                          last_update_time_str.size());

            return jstr;
        }

    }
}