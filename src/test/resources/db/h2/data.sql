-- users 테이블 생성
CREATE TABLE IF NOT EXISTS users (
    id LONG PRIMARY KEY,
    email VARCHAR(100),
    password VARCHAR(100),
    introduction VARCHAR(100),
    nickname VARCHAR(100),
    role VARCHAR(50)
);

-- 더미 사용자 데이터 삽입
INSERT INTO users (id, email, introduction, nickname, password, role) VALUES
(1, 'user1@mail.com', 'tester', 'tester', 'test1234!', 'USER'),
(2, 'user2@mail.com', 'tester', 'tester', 'test1234!', 'USER'),
(3, 'user3@mail.com', 'tester', 'tester', 'test1234!', 'USER');

-- workspace 테이블 생성
CREATE TABLE IF NOT EXISTS workspace (
    id LONG PRIMARY KEY,
    title VARCHAR(50),
    icon VARCHAR(50),
    user_id LONG,
    created_at datetime,
    modified_at datetime,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 더미 workspace 데이터 삽입
INSERT INTO workspace (id, title, icon, user_id, created_at, modified_at) VALUES
(1, 'Workspace 1', 'test', 1, '2023-01-01 00:00:00', '2023-01-01 00:00:00'),
(2, 'Workspace 2', 'test', 1, '2023-01-01 00:00:00', '2023-01-01 00:00:00'),
(3, 'Workspace 1', 'test', 2, '2023-01-01 00:00:00', '2023-01-01 00:00:00');

-- board 테이블 생성
CREATE TABLE IF NOT EXISTS board (
    id LONG PRIMARY KEY,
    title VARCHAR(50),
    color VARCHAR(50),
    info VARCHAR(50),
    workspace_id LONG,
    user_id LONG,
    created_at datetime,
    modified_at datetime,
    FOREIGN KEY (workspace_id) REFERENCES workspace(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 더미 board 데이터 삽입
INSERT INTO board (id, title, color, info, user_id, workspace_id, created_at, modified_at) VALUES
(1, 'Board 1', 'black', 'test', 1, 1, '2023-01-01 00:00:00', '2023-01-01 00:00:00'),
(2, 'Board 2', 'white', 'test', 1, 1, '2023-01-01 00:00:00', '2023-01-01 00:00:00'),
(3, 'Board 1', 'black', 'test', 1, 2, '2023-01-01 00:00:00', '2023-01-01 00:00:00');

-- deck 테이블 생성
CREATE TABLE IF NOT EXISTS deck (
    id LONG PRIMARY KEY,
    title VARCHAR(50),
    position float,
    is_archived bit,
    board_id LONG,
    created_at datetime,
    modified_at datetime,
    FOREIGN KEY (board_id) REFERENCES board(id)
);

-- 더미 deck 데이터 삽입
INSERT INTO deck (id, title, position, is_archived, board_id, created_at, modified_at) VALUES
(1, 'Deck 1', 128, false, 1, '2023-01-01 00:00:00', '2023-01-01 00:00:00'),
(2, 'Deck 2', 256, false, 1, '2023-01-01 00:00:00', '2023-01-01 00:00:00'),
(3, 'Deck 3', 384, false, 1, '2023-01-01 00:00:00', '2023-01-01 00:00:00'),
(4, 'Deck 3', 512, true, 1, '2023-01-01 00:00:00', '2023-01-01 00:00:00');

-- card 테이블 생성
CREATE TABLE IF NOT EXISTS card (
    id LONG PRIMARY KEY,
    title VARCHAR(50),
    content VARCHAR(50),
    attachment VARCHAR(50),
    position float,
    is_archived bit,
    due_date datetime,
    deck_id LONG,
    created_at datetime,
    modified_at datetime,
    FOREIGN KEY (deck_id) REFERENCES deck(id)
);

-- 더미 card 데이터 삽입
INSERT INTO card (id, title, content, attachment, position, is_archived, due_date, deck_id, created_at, modified_at) VALUES
(1, 'Card 1-1', 'test', null, 128, false, null, 1, '2023-01-01 00:00:00', '2023-01-01 00:00:00'),
(2, 'Card 1-2', 'test', null, 256, false, null, 1, '2023-01-01 00:00:00', '2023-01-01 00:00:00'),
(3, 'Card 1-3', 'test', null, 384, false, null, 1, '2023-01-01 00:00:00', '2023-01-01 00:00:00'),
(4, 'Card 2-1', 'test', null, 512, false, null, 2, '2023-01-01 00:00:00', '2023-01-01 00:00:00'),
(5, 'Card 2-2', 'test', null, 640, false, null, 2, '2023-01-01 00:00:00', '2023-01-01 00:00:00'),
(6, 'Card 3-1', 'test', null, 768, true, null, 3, '2023-01-01 00:00:00', '2023-01-01 00:00:00');
