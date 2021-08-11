
#ifndef EventListener_H
#define EventListener_H

#include <jni.h>

/**
 * EventListener class to handle callbacks in case of Contact events
 * */
class EventListener {
public:
    EventListener(jweak pJObject, jmethodID pID);

    jweak mListener=NULL;
    jmethodID mMethodID = NULL;

};


#endif //EventListener_H
