#ifndef CONTACTS_H_
#define CONTACTS_H_

#include <string>
#include "contact.h"
#include "EventListener.h"
#include <vector>

namespace contacts {

class Contacts {
 public:

    static std::string getVersion();
    static std::string getContactsList();
    static std::string getUpdatedContactsListAfter(std::string timestamp); // Returns only contact records that got updated after timestamp
    static std::string getLastUpdatedTime(); // last updated time in DB server
    static int addNewContact(std::string newContact); // add new contact, returns server response success/failure
    static void onContactDataUpdated(std::string updatedContact);// Call back to upper SDK for update events
    static std::string getTestDataForCallbackTest();
    static void addEventListener(EventListener *listener);

 private:

    const static std::string kSdkVersion;
    static std::string kContactsList;
    const static std::string kLastUpdateTimestamp;
    static std::string kContactToBeAdded;
    static std::string kCallbackTestData;

};

}

#endif //CONTACTS_H_
