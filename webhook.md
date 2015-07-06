MMX Hackathon - Server Side Enchancement
========================================
Hacked by: Rahul Phadnis & Sujay Datar

Add support for WebHook based integration to magnet message server.
-------------------------------------------------------------------

Web hooks provide a mechanism for integrating different systems using HTTP. Webhooks are typically used for
for event notifications.

Magnet Message has been enhanced to support web hooks for the following events:

- Message with specific meta key value pair
- User creation

An HTTP POST request is sent to the registered target URL when these events occur.




Database changes:

- Add a new table for registering Web Hooks.

REST API:

- Creating new web hooks
- Retrieving a web hook using its id

# Demo
For demonstration, we setup the hacked magnet message server. We then defined a hook for getting notified when a message with specific meta key value pair is 
processed by the server.



