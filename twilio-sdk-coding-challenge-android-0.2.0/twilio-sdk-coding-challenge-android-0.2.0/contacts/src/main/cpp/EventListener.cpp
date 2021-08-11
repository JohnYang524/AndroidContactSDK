#include "EventListener.h"

/**
 * EventListener class to handle callbacks in case of Contact events
 * */
EventListener::EventListener(jweak pJObject, jmethodID pID) {
    mListener = pJObject;
    mMethodID = pID;
}
