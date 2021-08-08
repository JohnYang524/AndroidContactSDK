#include "contacts.h"

namespace contacts {

    const std::string Contacts::kSdkVersion = "0.1.0";

    /*
     * This value should be retrieved from server, hardcoding the value here for testing
     */
    const std::string Contacts::kLastUpdateTimestamp = "1";

    /*
     * You may store all of a users contacts in memory and use this list to seed the users's initial
     * contact list.
     */
    const std::string Contacts::kContactsList = "[{\"id\" : \"100\",\"firstName\" : \"Alexander\",\"lastName\" : \"Bell\",\"phoneNumber\" : \"+16170000001\"},"
                                                "{\"id\" : \"101\",\"firstName\" : \"Thomas\",\"lastName\" : \"Watson\",\"phoneNumber\" : \"+16170000002\"},"
                                                "{\"id\" : \"102\",\"firstName\" : \"Elisha\",\"lastName\" : \"Gray\",\"phoneNumber\" : \"+18476003599\"},"
                                                "{\"id\" : \"103\",\"firstName\" : \"Antonio\",\"lastName\" : \"Meucci\",\"phoneNumber\" : \"+17188763245\"},"
                                                "{\"id\" : \"104\",\"firstName\" : \"Guglielmo\",\"lastName\" : \"Marconi\",\"phoneNumber\" : \"+39051203222\"},"
                                                "{\"id\" : \"105\",\"firstName\" : \"Samuel\",\"lastName\" : \"Morse\",\"phoneNumber\" : \"+16172419876\"},"
                                                "{\"id\" : \"106\",\"firstName\" : \"Tim\",\"lastName\" : \"Berners-Lee\",\"phoneNumber\" : \"+44204549898\"},"
                                                "{\"id\" : \"107\",\"firstName\" : \"John\",\"lastName\" : \"Baird\",\"phoneNumber\" : \"+4408458591006\"},"
                                                "{\"id\" : \"108\",\"firstName\" : \"Thomas\",\"lastName\" : \"Edison\",\"phoneNumber\" : \"+19086575678\"}]";

    std::string contacts::Contacts::getVersion() {
        return kSdkVersion;
    }

    std::string contacts::Contacts::getContactsList() {
        return kContactsList;
    }

    std::string contacts::Contacts::getLastUpdatedTime() {
        return kLastUpdateTimestamp;
    }
}
