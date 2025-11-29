# Secure Distributed Real-Time Chat Server

A high-performance, scalable real-time chat application built with Java 21 and Spring Boot. Designed to support horizontal scaling using Redis Pub/Sub and secure WebSocket connections via JWT.

## Key Features

- **Real-time Communication:** Full-duplex communication using WebSocket (STOMP protocol).

- **Horizontal Scaling:** Distributed architecture using Redis Pub/Sub to broadcast messages across multiple server instances.

- **Persistence:** Chat history (Public & Private) stored in MongoDB for high write throughput.

- **Zero-Trust Security:** WebSocket connections secured via JWT (JSON Web Token) and Spring Security 6.

- **Private Messaging:** 1-on-1 secure messaging with history retrieval.

- **Distributed Presence:** Real-time online user tracking using Redis Sets.

## Tech Stack

- **Core:** Java 21, Spring Boot 3

- **Communication:** WebSocket, STOMP

- **Database:** MongoDB (Chat Logs & Users)

- **Message Broker:** Redis (Pub/Sub & Presence State)

- **Security:** Spring Security, JJWT (Jackson)

## Architecture

1. The system uses a Broker-based architecture. When a user sends a message, the server:

2. Validates the JWT.

3. Persists the message to MongoDB.

4. Publishes the event to a Redis Topic.

5. All server instances subscribed to the topic receive the event.

6. Each instance delivers the message to the locally connected target user(s).

## Getting Started

### Prerequisites

- Java 21

- Docker & Docker Compose

- Maven

### 1. Clone the repository

> git clone [https://github.com/pedrozaz/realtime-chat.git](https://github.com/pedrozaz/realtime-chat.git)
> 
> cd realtime-chat



### 2. Configure Environment

Create a `.env` file in the root directory:

> JWT_SECRET=YourSuperSecretKeyBase64EncodedStringHere
> 
> MONGO_ROOT_USER=admin
> 
> MONGO_ROOT_PASSWORD=password



### 3. Start Infrastructure

Run MongoDB and Redis using Docker Compose:

> docker-compose up -d



### 4. Run Application

> mvn spring-boot:run



The server will start on `http://localhost:8080`.

## Usage

1. **Access the Client:** Open `http://localhost:8080` in your browser.

2. **Register/Login:** Use the built-in form to create an account.

3. **Chat:**
   - Messages sent in "Public Room" are broadcast to everyone.
   - Click on a user in the "Online Users" list to start a private chat.

## API Endpoints

| Method| Endpoint| Description |
|:-------- |:--------:| --------:|
| POST | /auth/register | Register new user {username, password} |
| POST | /auth/login | Authenticate and retrieve JWT |
| GET | /messages | Fetch chat history (Optional query: ?recipient=username) |
| WS | /ws | WebSocket Handshake Endpoint |

