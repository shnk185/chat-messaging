# Chat Messaging Service

A WebSocket-based chat platform. Clients connect to a chat server over
WebSocket, self identify, register users, create chats, send messages,
acknowledge delivery, and fetch pending messages. Chat servers are
horizontally scalable: cross instance routing is handled via Redis pub/sub,
with MySQL as the persistent source of truth.

## Stack

- Java 17, Spring Boot 4
- MySQL 8
- Redis 7

## Setup instructions 
```
git clone <this-repo>
cd chat-messaging
docker compose up -d

# to run 2 instances:
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8080

# open in browser:
src/textwebsocket1.html (port 8080)
src/textwebsocket2.html (port 8081)
```
sample commands: ```src/args.txt```


## Protocol summary

| Direction | `commandType` | `command` payload | Description |
|---|---|---|---|
| C → S | `CLIENT_SELF_IDENTIFY` | `{ userId }` | Bind connection to user, subscribe this server to `topic_user_{userId}` |
| S → C | `SERVER_SELF_IDENTIFY_RESPONSE` | `{ userId }` | Confirm identification |
| C → S | `CLIENT_REGISTER_USER` | `{ userName }` | Create user |
| S → C | `SERVER_REGISTER_USER_RESPONSE` | `{ user }` | Returns created user |
| C → S | `CLIENT_FETCH_ALL_USERS` | `{ userId }` | List all users |
| S → C | `SERVER_FETCH_ALL_USERS_RESPONSE` | `{ users[] }` | Full user list |
| C → S | `CLIENT_CREATE_CHAT` | `{ chatName, userIds[] }` | Create chat + chat members |
| S → C | `SERVER_CREATE_CHAT_RESPONSE` | `{ chatDto }` | Returns chat id, name, members |
| C → S | `CLIENT_SEND_MESSAGE_TO_CHAT` | `{ chatId, content }` | Persist message + fan out to receivers |
| S → C | `SERVER_SEND_MESSAGE_TO_CHAT_RESPONSE` | `{ message }` | Ack to sender with message id |
| S → C | `SERVER_PUSH_MESSAGE_TO_CHAT` | `{ incomingMessage }` | Live push to each receiver (`messageId, content, chatId, senderId, receiverId`) |
| C → S | `CLIENT_ACKNOWLEDGE_MESSAGE_RECEIVED` | `{ messageId, chatId }` | Mark inbox row delivered; deletes message once all receivers ack |
| S → C | `SERVER_ACKNOWLEDGE_MESSAGE_RECEIVED_RESPONSE` | `{ messageId, chatId, receiverId }` | Confirm ack |
| C → S | `CLIENT_FETCH_ALL_PENDING_MESSAGES` | `{ userId }` | Pull undelivered inbox grouped by chat |
| S → C | `SERVER_FETCH_ALL_PENDING_MESSAGES_RESPONSE` | `{ chatId -> incomingMessage[] }` | Offline / missed messages |
| Internal | `INTERNAL_SEND_MESSAGE_TO_CHAT` | `{ messageId, content, chatId, senderId, receiverId }` | Redis pub/sub frame on `topic_user_{receiverId}` for receivers connected to another instance |

Typical flow:
```
connect ws://localhost:8080/ws
→ CLIENT_REGISTER_USER → SERVER_REGISTER_USER_RESPONSE (get userId)
→ CLIENT_SELF_IDENTIFY → SERVER_SELF_IDENTIFY_RESPONSE
→ CLIENT_CREATE_CHAT → SERVER_CREATE_CHAT_RESPONSE (get chatId)
→ CLIENT_SEND_MESSAGE_TO_CHAT → SERVER_PUSH_MESSAGE_TO_CHAT (receivers)
→ CLIENT_ACKNOWLEDGE_MESSAGE_RECEIVED → message deleted when all receivers ack
→ CLIENT_FETCH_ALL_PENDING_MESSAGES (on reconnect to catch up)
```

## Design decisions

* MySQL is the source of truth, Redis is used to route to app instance user is connected to: Message and inbox
rows are written in a single transaction, one message row plus one inbox row
per receiver (excluding the sender).

* Per user Redis topics: Each server subscribes
to `topic_user_{userId}` when a connection self identifies and unsubscribes
on disconnect. Sending publishes only to topics of receivers not connected
locally.

* Local first delivery with remote fallback: The sending instance scans its
own sessions and pushes directly to locally connected receivers, then
publishes to redis for remaining users. The instance holding that user's connection receives the Redis
message and
pushes it down the right WebSocket.

* Inbox + delete on full acks: Each receiver
gets an inbox row (delivered=false). acknowledging flips it
to delivered and deletes the message row once no undelivered inbox rows
remain for that message