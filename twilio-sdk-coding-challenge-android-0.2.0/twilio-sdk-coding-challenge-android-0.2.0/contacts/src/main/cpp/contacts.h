#ifndef CONTACTS_H_
#define CONTACTS_H_

#include <string>
#include "contact.h"

namespace contacts {

class Contacts {
 public:

    static std::string getVersion();
    static std::string getContactsList();
    static std::string getUpdatedContactsListAfter(std::string timestamp); // Returns only contact records that got updated after timestamp
    static std::string getLastUpdatedTime(); // last updated time in DB server

 private:

    const static std::string kSdkVersion;
    const static std::string kContactsList;
    const static std::string kLastUpdateTimestamp;
};

}

#endif //CONTACTS_H_
