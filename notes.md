### Services to implement
1. LoginResult register(RegisterRequest)
2. authToken login(loginRequest)
3. void logout(authToken)
4. validateToken(authToken)
5. Game[] getGames()
6. createGame(gameName)
7. joinGame(gameID, playerColor)
7. void clear()

### DataTypes to implement
1. RegisterRequest (username, password, email)
2. LoginResult (username, authtoken)
2. LoginRequest (username, password)
3. Game