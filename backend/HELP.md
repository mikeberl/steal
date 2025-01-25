# Spring Boot Backend API Documentation

Questo progetto è un backend sviluppato in Spring Boot. Fornisce API per la gestione di leghe, classifiche, giocatori e partite. Qui sotto troverai una descrizione dettagliata delle API disponibili, suddivise per gruppo.

## Gruppi di API

### 1. Leagues
**Base URL:** `/api/v1/leagues`

#### API disponibili:
- **GET** `/`
    - TABLE `leagues` -> Returns all leagues

- **GET** `/{id}`
    - TABLE `leagues` -> Return a league with the given id

- **POST** `/`
    - TABLE `players` -> check if player exist and has rights to create a league
    - TABLE `leagues` -> check if name is unique and create the league

- **PUT** `/{id}`
    - TABLE `league` -> if the league exists updates the league infos

- **DELETE** `/{id}`
    - TABLE `players` -> remove leagueId from the player
    - TABLE `matches` -> Delete all the matches of the league
    - TABLE `rankings` -> Delete all the rankings of the league
    - TABLE `leagues` -> Delete the league

---

### 2. Rankings
**Base URL:** `/api/v1/rankings`

#### API disponibili:
- **GET** `/`
    - TABLE `rankings` -> returns all ranking

- **GET** `/{rankingId}`
    - TABLE `rankings` -> returns a ranking with a given id
    - 
- **GET** `/{leagueId}`
    - TABLE `rankings` -> returns all the rankings of a league

- **GET** `/{playedId}`
    - TABLE `rankings` -> returns all the rankings of a player

- **GET** `/{leagueId}/{playerId}`
    - TABLE `rankings` -> returns the ranking of a player in a league

---

### 3. Players
**Base URL:** `/api/v1/players`

#### API disponibili:
- **GET** `/`
    - TABLE `players` -> returns all players

- **GET** `/{id}`
    - TABLE `players` -> returns a player with the given id

- **GET** `/{leagueId}`
    - TABLE `players` -> returns all the players of a league

- **POST** `/`
    - TABLE `players` -> create a new player

- **PUT** `/{id}`
    - TABLE `players` -> Modify the infos of a player

- **DELETE** `/{id}`
    - TABLE `rankings` -> remove the all the rankings of the player
    - TABLE `matches` -> change the list of winners/losers and set id to "canceled player"
    - TABLE `player` -> Delete the player

---

### 4. Matches
**Base URL:** `/api/v1/matches`

#### API disponibili:
- **GET** `/`
    - TABLE `matches` -> returns all games

- **GET** `/{id}`
    - TABLE `matches` -> returns the match with the given id

- **GET** `/{playerId}`
    - TABLE `matches` -> returns all the matches of a player

- **GET** `/{leagueID}`
    - TABLE `matches` -> returns all the matches of a league

- **GET** `/{leagueId}/{playerId}`
    - TABLE `matches` -> returns all the games of a lìplayer in a league

- **POST** `/`
    - TABLE `matches` -> Create a new match
    - TABLE `rankings` -> For every player update the ranking or create a new one

- **PUT** `/{id}`
    - TABLE `match` -> Update match info
    - TABLE `rankings` -> For every player update the ranking by changing the scores of the match

- **DELETE** `/{id}`
    - TABLE `matches` -> Delete the match
    - TABLE `rankings` -> For every player update the ranking by removing the scores of the match

---

## Tecnologie Utilizzate
- **Java 21**
- **Spring Boot** (versione X.X.X)
- **Hibernate**
- **PostgreSQL**

## Configurazione del Progetto
1. Clonare il repository:
   ```bash
   git clone <repository_url>
   ```

2. Configurare il file `application.properties` o `application.yml` con i dettagli del database.

3. Compilare ed eseguire il progetto:
   ```bash
   ./mvnw spring-boot:run
   ```

