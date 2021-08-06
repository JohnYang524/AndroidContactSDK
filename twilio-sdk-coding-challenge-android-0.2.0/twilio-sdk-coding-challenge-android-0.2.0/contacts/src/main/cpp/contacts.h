#ifndef CONTACTS_H_
#define CONTACTS_H_

#include <string>
#include "contact.h"

namespace contacts {

class Contacts {
 public:

    static std::string getVersion();

 private:

    const static std::string kSdkVersion;
    const static std::string kContactsList;
};

}

#endif //CONTACTS_H_
