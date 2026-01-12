# chat-messaging

# done
* clients connect via websocket to chat servers
* clients can self identify, register, fetch all users, create chat, send message to chat, acknowledge messages, fetch all pending messages
* chat servers can be scaled to many because routing within instances is handled via redis pub sub
* source of truth is still persistent database with acid
* messages and inbox is written in single transaction
* messages are deleted when ack is received from all receivers

# todo
* fetch all chats
* extend create new chat response to use redis pub sub
* client side reconciliation every x seconds for all messages
* object storage for images (messages are currently text only)
* least connection based routing to chat servers, right now there is client side direct connection to chat server
* scheduled job to auto delete messages undelivered for a month
* can change arch to server topics instead of user topics, user key tells which chat server in redis
* dynamic scaling, how will system handle when we dynamically add a new chat server