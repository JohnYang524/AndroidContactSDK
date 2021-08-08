#include "contact.h"
#include <utility>

namespace contacts {

Contact::Contact(std::string id,
                 std::string first_name,
                 std::string last_name,
                 std::string phone_number)
    : id_(std::move(id)),
      first_name_(std::move(first_name)),
      last_name_(std::move(last_name)),
      phone_number_(std::move(phone_number)) {}

}
