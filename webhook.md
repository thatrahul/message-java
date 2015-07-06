MMX Hackathon - Server Side Enchancement
========================================
Hacked by: Rahul Phadnis & Sujay Datar

Add support for Webhook based integration to magnet message server.
-------------------------------------------------------------------

Webhooks provide a mechanism for integrating different systems using HTTP. Webhooks are typically used for
for event notifications.

Magnet Message has been enhanced to support webhooks for the following events:

- Message with specific meta key value pair
- User creation

An HTTP POST request is sent to the registered target URL when these events occur.

In the future we can add support for events such as:
- Push message acknowledged
- MMX Message acknowledged
- Message timed out
and more....

## Payload Details
* Message with specific meta key value pair
        
    {
        "recipientUsernames":["rahuldemo"],
        "deviceId":"3992B549-50F5-48BC-8527-A86E291B8CED",
        "content":"I need help with stuff in Aisle 10",
        "metadata":
            {   "mtype":"support",
                "date":"20150701"
            }
    }
    

## Database changes:

- Add a new table for registering Web Hooks.

REST API:

- Creating new web hooks
- Retrieving a web hook using its id

## Demo
For demonstration, we setup the hacked magnet message server. We then defined a hook for getting notified when a message with specific meta key value pair is 
processed by the server. Blowfish server was used for providing the notification controller. In the controller we just echoed the message to a log file.



