const BASE_URL = 'http://localhost:8080'

// html 로딩 시 바로 실행되는 로직
$(document).ready(function () {
    let auth = Cookies.get('Authorization') ? Cookies.get('Authorization') : ''
    let refresh = Cookies.get('Refresh-Token') ? Cookies.get('Refresh-Token') : ''

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

        $("#deck-list").empty()
        let board = await res.json()
        console.log(board)

        for (let deck of board['decks']) {
            if(deck['archived']) continue

            $('#deck-list').append(formDeck(deck))
            for (let card of deck['cards']) {
                console.log(card)
                // todo: 불러온 덱에서 카드 읽어서 나열하기
                // $('#card-list-' + deck['deckId']).append(formCard(card))
            }
        }

        closeAllListHeaderBtns()
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

async function archiveDeck(dId) {
    let check = confirm("해당 덱을 보관하시겠습니까?")
    if(!check) return

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


// 순수 javascript 동작
function logout() {
    resetToken()
    window.location.href = BASE_URL + '/views/login'
}

function createWorkspaceOnOff() {
    $('#create-workspace-form').toggle()
}

function formDeck(deck) {
    let deckId = deck['deckId']
    let title = deck['title']

    return `
        <div id="deck-${deckId}" class="deck-list-content">
            <ul class="deck-list-ul">
                <li id="1">
                    <div class="deck-list-header">
                        <p class="list-header-title">${title}</p>
                        <a class="list-header-3dot" aria-label="덱 메뉴 생성" onclick="openListHeaderBtns(${deckId})">
                            <i class="fa-solid fa-ellipsis fa-xl"></i></a>
                        <div id="list-header-btns-${deckId}" class="list-header-btns">
                            <div class="list-header-options">수정</div>
                            <div class="list-header-options" onclick="archiveDeck(${deckId})">보관</div>
                        </div>
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
        </div>
    `
}

function formCard(card) {
    let cardId = card['id']
    let title = card['title']

}

function openListHeaderBtns(deckId) {
    closeAllListHeaderBtns()
    let listHeaderBtns = document.getElementById('list-header-btns-' + deckId)
    listHeaderBtns.show()
}

function closeAllListHeaderBtns() {
    const listHeaderBtns = document.getElementsByClassName('list-header-btns')
    listHeaderBtns.hide()
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