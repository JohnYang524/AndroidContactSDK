#include "contacts.h"

namespace contacts {

    const std::string Contacts::kSdkVersion = "0.1.0";

    /*
     * You may store all of a users contacts in memory and use this list to seed the users's initial
     * contact list.
     */
    const std::string Contacts::kContactsList = "[{\"firstName\" : \"Alexander\",\"lastName\" : \"Bell\",\"phoneNumber\" : \"+16170000001\"},"
                                                "{\"firstName\" : \"Thomas\",\"lastName\" : \"Watson\",\"phoneNumber\" : \"+16170000002\"},"
                                                "{\"firstName\" : \"Elisha\",\"lastName\" : \"Gray\",\"phoneNumber\" : \"+18476003599\"},"
                                                "{\"firstName\" : \"Antonio\",\"lastName\" : \"Meucci\",\"phoneNumber\" : \"+17188763245\"},"
                                                "{\"firstName\" : \"Guglielmo\",\"lastName\" : \"Marconi\",\"phoneNumber\" : \"+39051203222\"},"
                                                "{\"firstName\" : \"Samuel\",\"lastName\" : \"Morse\",\"phoneNumber\" : \"+16172419876\"},"
                                                "{\"firstName\" : \"Tim\",\"lastName\" : \"Berners-Lee\",\"phoneNumber\" : \"+44204549898\"},"
                                                "{\"firstName\" : \"John\",\"lastName\" : \"Baird\",\"phoneNumber\" : \"+4408458591006\"},"
                                                "{\"firstName\" : \"Thomas\",\"lastName\" : \"Edison\",\"phoneNumber\" : \"+19086575678\"}]";

    std::string contacts::Contacts::getVersion() {
        return kSdkVersion;
    }

    std::string contacts::Contacts::getContactsList() {
        return kContactsList;
    }

}
