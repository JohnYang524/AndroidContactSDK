#include "contacts.h"

namespace contacts {

    const std::string Contacts::kSdkVersion = "0.1.0";

    /*
     * This value should be retrieved from server, hardcoding the value here for testing
     */
    const std::string Contacts::kLastUpdateTimestamp = "1";

    /*
     * Original test data
     */
    std::string Contacts::kContactsList = "[{\"id\" : \"100\",\"firstName\" : \"Alexander\",\"lastName\" : \"Bell\",\"phoneNumber\" : \"+16170000001\"},"
                                                "{\"id\" : \"101\",\"firstName\" : \"Thomas\",\"lastName\" : \"Watson\",\"phoneNumber\" : \"+16170000002\"},"
                                                "{\"id\" : \"102\",\"firstName\" : \"Elisha\",\"lastName\" : \"Gray\",\"phoneNumber\" : \"+18476003599\"},"
                                                "{\"id\" : \"103\",\"firstName\" : \"Antonio\",\"lastName\" : \"Meucci\",\"phoneNumber\" : \"+17188763245\"},"
                                                "{\"id\" : \"104\",\"firstName\" : \"Guglielmo\",\"lastName\" : \"Marconi\",\"phoneNumber\" : \"+39051203222\"},"
                                                "{\"id\" : \"105\",\"firstName\" : \"Samuel\",\"lastName\" : \"Morse\",\"phoneNumber\" : \"+16172419876\"},"
                                                "{\"id\" : \"106\",\"firstName\" : \"Tim\",\"lastName\" : \"Berners-Lee\",\"phoneNumber\" : \"+44204549898\"},"
                                                "{\"id\" : \"107\",\"firstName\" : \"John\",\"lastName\" : \"Baird\",\"phoneNumber\" : \"+4408458591006\"},"
                                                "{\"id\" : \"108\",\"firstName\" : \"Thomas\",\"lastName\" : \"Edison\",\"phoneNumber\" : \"+19086575678\"}]";
    /**
     * New contact record to be used for testing adding contacts
     */
    std::string Contacts::kContactToBeAdded = "[{\"id\" : \"100\",\"firstName\" : \"Alexander\",\"lastName\" : \"Bell\",\"phoneNumber\" : \"+16170000001\"},"
                                              "{\"id\" : \"101\",\"firstName\" : \"Thomas\",\"lastName\" : \"Watson\",\"phoneNumber\" : \"+16170000002\"},"
                                              "{\"id\" : \"102\",\"firstName\" : \"Elisha\",\"lastName\" : \"Gray\",\"phoneNumber\" : \"+18476003599\"},"
                                              "{\"id\" : \"103\",\"firstName\" : \"Antonio\",\"lastName\" : \"Meucci\",\"phoneNumber\" : \"+17188763245\"},"
                                              "{\"id\" : \"104\",\"firstName\" : \"Guglielmo\",\"lastName\" : \"Marconi\",\"phoneNumber\" : \"+39051203222\"},"
                                              "{\"id\" : \"105\",\"firstName\" : \"Samuel\",\"lastName\" : \"Morse\",\"phoneNumber\" : \"+16172419876\"},"
                                              "{\"id\" : \"106\",\"firstName\" : \"Tim\",\"lastName\" : \"Berners-Lee\",\"phoneNumber\" : \"+44204549898\"},"
                                              "{\"id\" : \"107\",\"firstName\" : \"John\",\"lastName\" : \"Baird\",\"phoneNumber\" : \"+4408458591006\"},"
                                              "{\"id\" : \"108\",\"firstName\" : \"Thomas\",\"lastName\" : \"Edison\",\"phoneNumber\" : \"+19086575678\"},"
                                              "{\"id\" : \"109\",\"firstName\" : \"Jack\",\"lastName\" : \"Smith\",\"phoneNumber\" : \"+18888888888\"}]";

    /**
     * Test data for testing callback function when update event is triggered
     */
    std::string Contacts::kCallbackTestData = "[{\"id\" : \"100\",\"firstName\" : \"UPDATED_FirstName\",\"lastName\" : \"UPDATED_LastName\",\"phoneNumber\" : \"+19999999999\"}]";

    std::vector<EventListener *> kEventListeners; // Save all listeners to call back to.

    std::string contacts::Contacts::getVersion() {
        return kSdkVersion;
    }

    std::string contacts::Contacts::getContactsList() {
        return kContactsList;
    }

    std::string contacts::Contacts::getUpdatedContactsListAfter(std::string timestamp) {
        // Make server calls to return list of data updated after timestamp
        return kContactsList;
    }

    std::string contacts::Contacts::getLastUpdatedTime() {
        return kLastUpdateTimestamp;
    }

    int contacts::Contacts::addNewContact(std::string newContact) {
        // TODO: make web server calls to add new contact record
        // kContactsList = kContactsList + kContactToBeAdded;
        kContactsList = kContactToBeAdded; // For testing purposes
        return 200; // always return 200 for testing purposes
    }

    std::string contacts::Contacts::getTestDataForCallbackTest() {
        return kCallbackTestData;
    }

    void contacts::Contacts::addEventListener(EventListener *listener) {
        kEventListeners.push_back(listener);
    }

    /**
     * In the event of Contact data update, notify all EventListeners
     * Test methods written in contacts_jni.cpp
     */
    void contacts::Contacts::onContactDataUpdated(std::string updatedContact){
        if (!kEventListeners.empty()) {
            for (EventListener *listener : kEventListeners) {
                // TODO: update all Event Listeners from here
                // env->CallVoidMethod(listener->mListener,
                //                     listener->mMethodID, updatedContact);
            }
        }
    }
}
