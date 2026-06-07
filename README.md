# poker-star
Multiplayer Poker game using Java Spring Boot and MongoDB

## Introduction

`poker-star` is a multiplayer Texas Hold'em style poker game.
The backend is implemented with Java Spring Boot and persists game state in MongoDB.
The frontend is a React + Vite application providing a simple UI to create games,
join players, place bets and progress hands.

## Quick start

1. Start MongoDB (Docker):

```bash
docker run --name poker-mongo -p 27017:27017 -d mongo:7.0
```

2. Build and run the backend:

```bash
cd /Users/vikrant_goyal/Developer/MyWorkspace/2025/poker-star
sh ./mvnw clean package -DskipTests
sh ./mvnw spring-boot:run
```

The backend will be available at `http://localhost:8080` (API only).

3. Start the frontend (Vite):

```bash
cd /Users/vikrant_goyal/Developer/MyWorkspace/2025/poker-star/frontend
npm install
npm run dev
```

Open the UI at `http://localhost:5173/`.

## Where games are saved

Games are persisted in MongoDB:

- Database: `vpokerdb`
- Collection: `game`

Connection is configured in `src/main/resources/application.properties`:

```
spring.data.mongodb.uri=mongodb://localhost:27017/vpokerdb
```

## Inspect saved games (examples)

Using the Mongo shell inside the container:

```bash
docker exec -it poker-mongo mongosh
use vpokerdb
db.game.find().pretty()
```

Or use MongoDB Compass and connect to `mongodb://localhost:27017`.

## Useful backend API endpoints

- `POST /game/create` — create a new game
- `POST /game/{id}/join` — add a player (JSON body: `id`, `name`, `chips`)
- `POST /game/{id}/deal` — deal cards and start the hand
- `POST /game/{id}/bet` — place a bet (use the frontend DTO shape)
- `GET  /game/{id}/state` — retrieve current game state

Example `curl` to create a game:

```bash
curl -X POST http://localhost:8080/game/create
```

Example `curl` to join:

```bash
curl -X POST http://localhost:8080/game/<GAME_ID>/join \
	-H 'Content-Type: application/json' \
	-d '{"id":"player1","name":"Alice","chips":1000}'
```

## Troubleshooting

- If the backend reports `Connection refused` to MongoDB, ensure the `poker-mongo` container is running and reachable on `localhost:27017`.
- If port `8080` is in use, run the backend on another port: `sh ./mvnw spring-boot:run -Dserver.port=8081`.

