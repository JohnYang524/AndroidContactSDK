#include "contacts_jni.h"
#include "contacts.h"

#include <codecvt>
#include <locale>

namespace contacts {
namespace jni {

extern "C" JNIEXPORT jstring JNICALL Java_com_contacts_Contacts_00024Companion_nativeGetVersion(JNIEnv *env,
                                                                                                jobject jclass) {
    std::wstring_convert<std::codecvt_utf8_utf16<char16_t>, char16_t> convert;
    std::string sdk_version = contacts::Contacts::getVersion();
    auto sdk_version_str = convert.from_bytes(sdk_version);
    jstring jstr = env->NewString(reinterpret_cast<jchar const *>(sdk_version_str.c_str()),
                                  sdk_version_str.size());

    return jstr;
}

}
}