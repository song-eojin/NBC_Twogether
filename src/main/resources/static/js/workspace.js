const BASE_URL = 'http://localhost:8080'
// const BASE_URL = 'http://www.twogetherwork.com'

// html 로딩 시 바로 실행되는 로직
$(document).ready(function () {

    let auth = Cookies.get('Authorization') ? Cookies.get('Authorization') : ''
    let refresh = Cookies.get('Refresh-Token') ? Cookies.get('Refresh-Token')
        : ''

    // access 토큰과 refresh 토큰이 모두 존재하지 않을 때 -- 로그아웃
    if (auth === '' && refresh === '') {
        window.location.href = BASE_URL + '/views/login'
    }

    // 헤더 : 사진 클릭 이벤트 핸들러 추가
    $('#header-profileImage-container').click(function () {
        if ($('#userProfile-panel').is(':visible')) {
            $('#userProfile-panel').hide();
        } else {
            $('#userProfile-panel').show();
        }
    });

    // 헤더 : 알림 버튼 클릭 이벤트 핸들러 추가
    $('#alarm-button').click(function () {
        if ($('#alarm-panel').is(':visible')) {
            $('#alarm-panel').hide();
        } else {
            $('#alarm-panel').show();
        }
        callMyAlarms()
    })

    // 개인 프로필 창 : 사진 클릭 이벤트 핸들러 추가
    $('.close-userProfile-panel').click(function () {
        $('#userProfile-panel').hide();
    });

    // 개인 프로필 창 : 사용자 정보 수정 버튼 클릭 이벤트 핸들러 추가
    $('#change-userInfo-btn').click(function () {
        const oldNickname = $('#nickname').text()
        const oldIntroduction = $('#introduction').text();

        document.getElementById('edit-nick-input').value = oldNickname;
        document.getElementById('edit-intro-input').value = oldIntroduction;

        $('#nickname, #introduction, #change-userInfo-btn, #change-userImage-btn').hide();
        $('#edit-nick-input, #edit-intro-input, #save-edit-userInfo-btn, #cancel-userInfo-btn').show();
    });

    // 개인 프로필 창 : 사용자 이미지 수정 관련 이벤트 핸들러 추가
    $('#change-userImage-btn').click(function () {
        $('#profileImage-btns').show();
        $('#change-userImage-btn, #change-userInfo-btn').hide();
    });
    $('#cancel-profileImage-btn').click(function () {
        $('#profileImage-btns').hide();
        $('#change-userImage-btn, #change-userInfo-btn').show();
    })

    // 본인 정보 불러오기
    callMyUserInfo()
    callMyAlarms()
})

function callMyWorkspaces() {

    // before
    $('#my-workspaces').empty()

    // when
    fetch('/api/workspaces', {
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

        let workspaces = await res.json()

        for (let workspace of workspaces['workspaces']) {
            let wId = workspace['workspaceId']
            $('#my-workspaces').append(formMyWorkspace(workspace))
            $('#nav-workspaces').append(formNavMyWorkspace(workspace))

            for (let board of workspace['boards']) {
                $('#workspace-board-list-' + wId).append(formMyBoard(board))
            }

            for (let collaborator of workspace['wpCollaborators']) {
                console.log(collaborator)
                $('#invite-wp-col-list-' + wId).append(
                    formCollaborator(wId, collaborator))
            }
            $(`#workspace-board-list-${wId}`).append(formCreateBoard(wId))
        }
        // 공통 초대 요청란 닫기
        closeAllInviteCollaborators()
    })
}

function callColWorkspaces() {

    $('#col-workspaces').empty()

    // when
    fetch('/api/workspaces/invite', {
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

        let workspaces = await res.json()

        for (let workspace of workspaces['workspaces']) {
            let wId = workspace['workspaceId']
            $('#col-workspaces').append(formColWorkspace(workspace))
            for (let board of workspace['boards']) {
                $('#workspace-board-list-' + wId).append(formColBoard(board))
            }
        }

        // 공통 초대 요청란 닫기
        closeAllInviteCollaborators()
    })
}

// 워크스페이스로 이동
function moveToWorkspace() {
    window.location.reload();
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

function editWorkspace(wId) {

    // given
    let title = $('#workspace-title-edited-' + wId).val()
    if (title === '') {
        title = null;
    }
    let description = $('#workspace-description-edited-' + wId).val()
    if (description === '') {
        description = null;
    }
    const request = {
        title: title,
        icon: description
    }

    // when
    fetch('/api/workspaces/' + wId, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': Cookies.get('Authorization'),
            'Refresh-Token': Cookies.get('Refresh-Token')
        },
        body: JSON.stringify(request),
    })

    // then
    .then(async res => {
        checkTokenExpired(res)
        refreshToken(res)

        if (res.status !== 200) {
            let error = await res.json()
            alert(error['message'])
            return
        }
        // 생성된 workspace도 노출되도록 하기 위해 함수 호출
        callMyWorkspaces()
    });
}

async function createBoard(wId) {

    // given
    let title = document.getElementById('board-title-' + wId).value
    let color = document.getElementById('board-color-' + wId).value
    let info = document.getElementById('board-info-' + wId).value

    const request = {
        title: title,
        color: color,
        info: info
    }

    // when
    await fetch('/api/workspaces/' + wId + '/boards', {
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

        createBoardOnOff(wId)
        // 생성된 workspace도 노출되도록 하기 위해 함수 호출
        callMyWorkspaces()
    })
}

function deleteWorkspace(wId) {

    let check = confirm("워크스페이스를 삭제하시겠습니까?")
    if (!check) {
        return
    }

    // when
    fetch(`/api/workspaces/${wId}`, {
        method: 'DELETE',
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
            alert(error['message'])
            return
        }
        // 생성된 workspace도 노출되도록 하기 위해 함수 호출
        callMyWorkspaces()
    })
}

function deleteBoard(bId) {
    let check = confirm("보드를 삭제하시겠습니까?")
    if (!check) {
        return
    }
    // when
    fetch(`/api/boards/${bId}`, {
        method: 'DELETE',
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
            alert(error['message'])
            return
        }
        // 생성된 workspace도 노출되도록 하기 위해 함수 호출
        callMyWorkspaces()
    })
}

async function inviteWpCollaborator(wId) {
    // given
    let email = document.getElementById('wp-col-email-' + wId).value
    const request = {
        email: email
    }

    // when
    await fetch('/api/workspaces/' + wId + '/invite', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': Cookies.get('Authorization'),
            'Refresh-Token': Cookies.get('Refresh-Token')
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

        closeAllInviteCollaborators()
    })
}

async function cancelWpCollaborator(wId, colId) {
    // given
    let colEmail = document.getElementById('col-email-' + colId).innerHTML
    const request = {
        email: colEmail
    }

    // when
    await fetch('/api/workspaces/' + wId + '/invite', {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': Cookies.get('Authorization'),
            'Refresh-Token': Cookies.get('Refresh-Token')
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

        $('#wp-col-' + colId).remove()
    })
}

function moveToBoard(bId) {
    window.location.href = BASE_URL + '/views/boards/' + bId
}

function createWorkspaceOnOff() {
    $('#create-workspace-form').toggle()
}

function editWorkspaceOnOff(wId) {
    $('#edit-workspace-form-' + wId).toggle()
}

function createBoardOnOff(wId) {
    $('#create-board-btn-' + wId).toggle()
    $('#create-board-form-' + wId).toggle()
}

function editBoardOnOff(boardId) {
    $('#edit-board-form-' + boardId).toggle()
}

function formMyWorkspace(workspace) {
    let title = workspace['title']
    let introduction = workspace['icon']
    let wId = workspace['workspaceId']

    return `
    <div id="workspace-${wId}" class="workspace">
        <div>
          <header>
            <div class="workspace-control">
                <div class="workspace-control-header">
                    <div class="workspace-control-title-des">

            <h2>${title}</h2>
              <h3>${introduction}</h3>
                    </div>
                    <div class="workspace-control-btns">
                        <div class="edit-workspace-control">
              <button class="" onclick="editWorkspaceOnOff(${wId})"><i class="fas fa-pen"></i></button>
                            <div class="edit-workspace-content" id="edit-workspace-form-${wId}" style="display:none">
                                <div class="edit-workspace-content-title">
                  <label for="workspace-title-edited-${wId}">타이틀</label>
                  <input type="text" id="workspace-title-edited-${wId}"/>                 
                                </div>
                                <div class="edit-workspace-content-description">
                  <label for="workspace-description-edited-${wId}">짧은 설명</label>
                  <input type="text" id="workspace-description-edited-${wId}"/>
                                </div>
                                <div>
                <button class="edit-workspace-content-submit" id="edit-workspace-btn" onclick="editWorkspace(${wId})">수정</button>
                                </div>
                            </div>
                        </div>
                        <div>
              <button onclick="openInviteWpCollaborator(${wId})"><i class="fas fa-person"></i></button>
                        </div>
                        <div>
              <button onclick="deleteWorkspace(${wId})"><i class="fas fa-trash"></i></button>
                        </div>
                    </div>
              
                </div>
            </div>
              
              
              
            <div class="invite-wp-col-bg">
                <div id="invite-wp-col-${wId}" class="invite-collaborator">

                    <div class="invite-wp-col-header">
                <h2>워크스페이스에 초대하세요.</h2>
                <a class="close-button-invite-wp-col" onclick="closeAllInviteCollaborators()">
                <i class="fa-solid fa-xmark fa-xl"></i>
                </a>
                    </div>
                    <div class="wp-col-email">
                    <input type="text" id="wp-col-email-${wId}"
                    class="invite-wp-col-input"
                    placeholder="초대할 이메일을 입력하세요..."/>
                      <button class="invite-wp-col-btn" onclick="inviteWpCollaborator(${wId})">초대</button>
                    </div>
                    <hr>
                    <div class="wp-col-list">
                      <ul id="invite-wp-col-list-${wId}"></ul>

                    </div>
              </div>
                </div>
          </header>
          <hr>
                <div>
                    <div id="workspace-board-list-${wId}" class="workspace-board-list"></div>
                </div>
            </div>
        </div>       `
}

function formNavMyWorkspace(workspace) {
    let title = workspace['title']
    let wId = workspace['workspaceId']

    return `
    <li id="workspace-${wId}">
      <div class="wp-lists"
        <span>${title}</span>
      </div>
    </li>
    `

}

function formNavColWorkspace(workspace) {
    let title = workspace['title']
    let wId = workspace['workspaceId']

    return `
    <li id="workspace-${wId}">
      <div class="col-wp-lists"
        <span>${title}</span>
      </div>
    </li>
    `
}

function formMyBoard(board) {
    let boardId = board['boardId']
    let title = board['title']
    let color = board['color']

    return `
    <div id="board-${boardId}" class="board" >
      <div class="move-to-board" onclick="moveToBoard(${boardId})">
      <h3>${title}</h3>
      </div>
      <div id="board-${boardId}-btns" class="board-btns">
        <button id=delete-board-btn onclick="deleteBoard(${boardId})"
        ><i class="fa-solid fa-trash"></i></button>
      </div>
    </div>
    
  `
}

function formCollaborator(wId, collaborator) {
    let colId = collaborator['wpColId']
    let email = collaborator['email']
    let nickname = collaborator['nickname']

    return `
      <li id="wp-col-${colId}">
        <div class="wp-col-lists">
          <div class="wp-col-lists-email">
          <span>${nickname}</span>
          <span id="col-email-${colId}">${email}</span>
          </div>
          <button class="col-delete-btn" onclick="cancelWpCollaborator(${wId}, ${colId})">추방</button>
        </div>
      </li>
  `
}

function formCreateBoard(wId) {
    return `
        <div id="create-board-btn-${wId}" class="create-board-btn board" onclick="createBoardOnOff(${wId})">보드 생성</div>
        <div id="create-board-form-${wId}" class="create-board-form board">
          <div class="create-board-content">
            <div>
              <label for="board-title-${wId}">보드 이름</label>
              <input type="text" id="board-title-${wId}"/>
            </div>
            <div>
              <label for="board-color-${wId}">보드 색상</label>
              <input type="text" id="board-color-${wId}"/>
            </div>
            <div>
              <label for="board-info-${wId}">보드 정보</label>
              <input type="text" id="board-info-${wId}"/>
            </div>
            <button onclick="createBoard(${wId})">생성</button>
            <button onclick="createBoardOnOff(${wId})">취소</button>
          </div>
       </div>
       `
}

function formColWorkspace(workspace) {
    let title = workspace['title']
    let introduction = workspace['icon']
    let wId = workspace['workspaceId']

    return `
       <div id="workspace-${wId}" class="col-workspace">
          <header>
            <h2>${title}</h2>
            <h3>${introduction}</h3>
          </header>
          <div>
            <div id="workspace-board-list-${wId}" class="workspace-board-list"></div>
          </div>
       </div>
       `
}

function formColBoard(board) {
    let boardId = board['boardId']
    let title = board['title']
    let color = board['color']

    return `
    <div id="board-${boardId}" class="board" onclick="moveToBoard(${boardId})">
    <div class="move-to-board">
      <h3>${title}</h3>
     </div>
    </div>
    `
}

function openInviteWpCollaborator(wId) {
    closeAllInviteCollaborators()
    $('#invite-wp-col-' + wId).show()
}

function openInviteBoardCollab(bId) {
    closeAllInviteCollaborators()
    $('#invite-board-collaborator-' + bId).show()
}

function closeAllInviteCollaborators() {
    $('.invite-collaborator').hide()
}