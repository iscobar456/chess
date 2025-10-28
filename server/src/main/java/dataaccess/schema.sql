CREATE TABLE users (
    username VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    PRIMARY KEY (username)
);

CREATE TABLE auths (
    authtoken VARCHAR(255),
    username VARCHAR(255),
    PRIMARY KEY (authtoken),
    FOREIGN KEY (username) REFERENCES users(username)
);

CREATE TABLE games (
    id INT AUTO_INCREMENT,
    whiteusername VARCHAR(255),
    blackusername VARCHAR(255),
    gamename VARCHAR(255),
    game MEDIUMTEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (whiteusername) REFERENCES users(username),
    FOREIGN KEY (blackusername) REFERENCES users(username)
);