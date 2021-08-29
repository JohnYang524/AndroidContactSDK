# AndroidContactSDK
Contact SDK for Android


What is Contact SDK?

This project is a minimalistic implementation of a cross-platform mobile SDK to manage “Twilio Contacts”. We have a cloud-based distributed system to manage phone contacts - first name, last name, and phone number - and this project servers as the mobile client libraries to access those contacts in a cross-platform way. 
On the client-side, we have a common C++ library that will be used by both the iOS and Android SDKs, this project is scoped to the Android platform, with 3 major components:
1. C++ Contacts SDK
2. Android Contacts SDK - This SDK use the C++ SDK
3. Sample Android application that demonstrates the SDK capabilities. 
		

What I have done:
APIs:
I’ve updated the SDK to provide APIs for the followings:
1.	List contacts - return a list of known contacts
2.	Get list all of user’s contacts that were updated/created after a certain timestamp
3.	Add a new contact – asynchronously notify the caller of success/failure
4.	Notify user of an updated contact event.
5.	Get timestamp of last DB server data update.  
