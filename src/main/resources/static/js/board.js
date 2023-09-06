const BASE_URL = 'http://localhost:8080'

// html 로딩 시 바로 실행되는 로직
$(document).ready(function () {
    let auth = Cookies.get('Authorization') ? Cookies.get('Authorization') : ''
    let refresh = Cookies.get('Refresh-Token') ? Cookies.get('Refresh-Token')
        : ''

    // access 토큰과 refresh 토큰이 모두 존재하지 않을 때 -- 로그아웃
    if (auth === '' && refresh === '') {
        window.location.href = BASE_URL + '/views/login'
    }

    // 본인 정보 불러오기
    getUserInfo()
})

// fetch API 로직
async function getUserInfo() {
    // when
    await fetch('/api/users/info', {
        method: 'GET',
        headers: {
            'Authorization': Cookies.get('Authorization'),
            'Refresh-Token': Cookies.get('Refresh-Token')
        }
    })

    // then
    .then(async res => {
        checkTokenExpired(res)
        refreshToken(res)

        let user = await res.json()
        $('#nickname').text(user['nickname'])
        $('#email').text(user.email)
        $('#role').text(user.role)

        callMyBoard()
    })
}

async function callMyBoard() {
    // given
    let boardId = document.getElementById('boardId').textContent

    // when
    await fetch('/api/boards/' + boardId, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': Cookies.get('Authorization'),
            'Refresh-Token': Cookies.get('Refresh-Token')
        }
    })

    // then
    .then(async res => {
        checkTokenExpired(res)
        refreshToken(res)

        if (res.status !== 200) {
            let error = await res.json()
            alert(error.message)
            return
        }

        var decks = $('#deck-list')
        var archive = $('#archive-container')

        decks.empty()
        archive.empty()
        let board = await res.json()
        console.log(board)

        for (let deck of board['decks']) {
            if (deck['archived']) {
                archive.append(formArchived(deck))
            } else {
                decks.append(formDeck(deck))
                for (let card of deck['cards']) {
                    console.log(card)
                    // todo: 불러온 덱에서 카드 읽어서 나열하기
                    // $('#card-list-' + deck['deckId']).append(formCard(card))
                }
            }
        }
    })
}

async function createWorkspace() {
    // given
    let title = $('#workspace-title').val()
    let description = $('#workspace-description').val()
    const request = {
        title: title,
        icon: description
    }

    // when
    await fetch('/api/workspaces', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': Cookies.get('Authorization'),
            'Refresh-Token': Cookies.get('Refresh-Token')
        },
        body: JSON.stringify(request)
    })

    // then
    .then(res => {
        checkTokenExpired(res)
        refreshToken(res)

        // 생성된 workspace도 노출되도록 하기 위해 함수 호출
        callMyWorkspaces()
    })
}

async function createDeck() {
    // given
    let boardId = document.getElementById('boardId').textContent
    let title = document.getElementById('deck-title-input').value

    // when
    await fetch('/api/boards/' + boardId + '/decks', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': Cookies.get('Authorization'),
            'Refresh-Token': Cookies.get('Refresh-Token')
        },
        body: title
    })

    // then
    .then(async res => {
        checkTokenExpired(res)
        refreshToken(res)

        if (res.status !== 200) {
            let error = await res.json()
            alert(error.message)
            return
        }

        callMyBoard() // board 다시 부르기
    })
}

async function editDeck(deckId) {
    // given
    let title = document.getElementById('edit-deck-title-input-' + deckId).value
    if (title === '' || title === undefined) {
        console.log('title is empty')
        return
    }

    // when
    await fetch('/api/decks/' + deckId, {
        method: 'PUT',
        body: title
    })

    // then
    .then(async res => {
        checkTokenExpired(res)
        refreshToken(res)

        if (res.status !== 200) {
            let error = await res.json()
            alert(error.message)
            return
        }

        const deckTitle = document.getElementById('deck-title-' + deckId)
        deckTitle.innerHTML = title + ' <i class="fas fa-pen">'
        var editTitle = document.getElementById('edit-deck-title-input-' + deckId)
        editTitle.value = null
        toggleEditDeckTitle(deckId)
    })
}

async function deleteDeck(deckId) {
    let check = confirm("해당 덱을 삭제하시겠습니까?")
    if (!check) {
        return
    }

    // when
    await fetch('/api/decks/' + deckId, {
        method: 'DELETE'
    })

    // then
    .then(async res => {
        checkTokenExpired(res)
        refreshToken(res)

        if (res.status !== 200) {
            let error = await res.json()
            alert(error.message)
            return
        }

        $('#archive-deck-' + deckId).remove()
    })
}

async function archiveDeck(dId) {
    let check = confirm("해당 덱을 보관하시겠습니까?")
    if (!check) {
        return
    }

    // when
    await fetch('/api/decks/' + dId + '/archive', {
        method: 'PUT'
    })

    // then
    .then(async res => {
        checkTokenExpired(res)
        refreshToken(res)

        if (res.status !== 200) {
            let error = await res.json()
            alert(error.message)
            return
        }

        callMyBoard()
    })
}

async function restoreDeck(dId) {
    // when
    await fetch('/api/decks/' + dId + '/archive', {
        method: 'PUT'
    })

    // then
    .then(async res => {
        checkTokenExpired(res)
        refreshToken(res)

        if (res.status !== 200) {
            let error = await res.json()
            alert(error.message)
            return
        }

        callMyBoard()
    })
}

async function moveDeck(dId, prevId, nextId) {
    // given
    const request = {
        prevDeckId: prevId,
        nextDeckId: nextId
    }

    // when
    await fetch('/api/decks/' + dId + '/move', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(request)
    })

    // then
    .then(async res => {
        checkTokenExpired(res)
        refreshToken(res)

        if (res.status !== 200) {
            let error = await res.json()
            alert(error['message'])
        }
    })
}

// 순수 javascript 동작
function logout() {
    resetToken()
    window.location.href = BASE_URL + '/views/login'
}

function toggleCreateWorkspace() {
    $('#create-workspace-form').toggle()
}

function formDeck(deck) {
    let deckId = deck['deckId']
    let title = deck['title']

    return `
        <li id="${deckId}" class="deck deck-list-content" draggable="true"
            ondragstart="dragStart(event)">
            <ul class="deck-list-ul">
                <li>
                    <div class="deck-list-header">
                        <p id="deck-title-${deckId}" class="list-header-title" onclick="toggleEditDeckTitle(${deckId})">${title} <i class="fas fa-pen"></i></p>  
                        <p class="list-header-archive" onclick="archiveDeck(${deckId})"><i class="fa fa-archive" aria-hidden="true"></i></p>
                    </div>
                    
                    <div id="edit-deck-title-form-${deckId}" class="edit-deck-title-form" style="display:none">
                        <input id="edit-deck-title-input-${deckId}" type="text" class="edit-deck-title-input" placeholder="새로운 덱 제목을 지어주세요..">
                        <button onclick="editDeck(${deckId})">제출</button>
                        <button onclick="toggleEditDeckTitle(${deckId})">취소</button>
                    </div>
                    
                    <div class="deck-list-add-card-area">
                        <div class="card-list-${deckId}"></div>
                        
                        <!-- todo: 카드 추가 기능 활성화 -->
                        <div class="deck-list-add-card-container">
                            <a id="open-add-cardlist-button-${deckId}" class="open-add-cardlist-button" href="#" aria-label="카드 생성 열기">
                                <i class="fa-solid fa-plus fa-xl"></i>
                                카드 추가
                            </a>
                        </div>
                        
                        <!-- todo: 카드 추가 기능 -->
                        <div id="add-card-name-text-area-form-${deckId}" class="deck-list-add-card-name-text-area">
                            <form class="add-card-name-text-area-form hidden" action="post">
                                <input type="text" name="add-cardlist-input"
                                       class="add-cardlist-input"
                                       placeholder="카드 내용을 입력하세요...">
                                <div class="horizontal-align">
                                    <button type="submit"
                                            class="add-cardlist-submit default-button">카드 추가
                                    </button>
                                    <a class="cancel-button cardlist" href="#"
                                       aria-label="카드 추가 취소">
                                        <i class="fa-solid fa-xmark fa-xl"></i>
                                    </a>
                                </div>
                            </form>
                        </div>
                        
                    </div>
                </li>
            </ul>
        </li>
    `
}

function formArchived(deck) {
    let deckId = deck['deckId']
    let title = deck['title']

    return `
        <li id="archive-deck-${deckId}">
            <span class="archive-item-title">${title}</span>
            <div class="archive-btns">
                <button onclick="restoreDeck(${deckId})">복구</button>
                <button onclick="deleteDeck(${deckId})">삭제</button>
            </div>
        </li>
    `
}

function formCard(card) {
    let cardId = card['id']
    let title = card['title']

}

function toggleEditDeckTitle(deckId) {
    $('#edit-deck-title-form-' + deckId).toggle()
}

function toggleCreateDeckForm() {
    $('#create-deck-form').toggle()
}

function openNav() {
    document.getElementById("mySidenav").style.width = "250px";
}

function closeNav() {
    document.getElementById("mySidenav").style.width = "0";
}

// drag & drop 관련 로직
let draggedIndex = null;

function dragStart(event) {
    const decks = document.querySelectorAll('.deck');
    draggedIndex = Array.from(decks).indexOf(event.target);
}

function allowDrop(event) {
    event.preventDefault();
}

function drop(event) {
    event.preventDefault();

    const deckList = document.getElementById('deck-list');
    if (deckList.contains(event.target)) {
        const dropIndex = Array.from(deckList.children).indexOf(event.target);
        let currentDeck = deckList.children[draggedIndex]
        let targetDeck = event.target

        if (currentDeck.id !== targetDeck.id &&
            currentDeck.classList.contains('deck') &&
            targetDeck.classList.contains('deck')) {
            if (draggedIndex < dropIndex) {
                let nextDeckId = targetDeck.nextElementSibling === null ? 0 : targetDeck.nextElementSibling.id
                moveDeck(currentDeck.id, targetDeck.id, nextDeckId)
                .then(() => deckList.insertBefore(currentDeck, targetDeck.nextElementSibling))
            } else {
                let prevDeckId = targetDeck.previousElementSibling === null ? 0 : targetDeck.previousElementSibling.id
                moveDeck(currentDeck.id, prevDeckId, targetDeck.id)
                .then(() => deckList.insertBefore(currentDeck, targetDeck))
            }
        }
    }

    draggedIndex = null;
}

// token 관련 재생성, 삭제, 만료 로직
function refreshToken(response) {
    let token = response.headers.get('Authorization')
    if (token !== null) {
        resetToken()
        Cookies.set('Authorization', token, {path: '/'})
        Cookies.set('Refresh-Token', response.headers.get('Refresh-Token'),
            {path: '/'})
    }
}

function resetToken() {
    Cookies.remove('Authorization', {path: '/'})
    Cookies.remove('Refresh-Token', {path: '/'})
}

function checkTokenExpired(res) {
    if (res.status === 412) {
        alert('토큰이 만료되었습니다. 다시 로그인해주세요!')
        resetToken()
        window.location.href = BASE_URL + '/views/login'
    }
}