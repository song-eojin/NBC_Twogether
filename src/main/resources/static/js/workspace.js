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

    // 본인이 생성한 workspace 불러오기
    callMyWorkspaces()
    // 본인이 초대된 workspace 불러오기
    callColWorkspaces()
  })
}

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
      for (let board of workspace['boards']) {
        $('#workspace-board-list-' + wId).append(formMyBoard(board))
      }
      for (let collaborator of workspace['wpCollaborators']) {
        console.log(collaborator)
      }
      $(`#workspace-board-list-${wId}`).append(formCreateBoard(wId))
    }

    // 공통 초대 요청란 닫기
    closeAllInviteCollaborators()
  })
}

function callColWorkspaces() {
  // when
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

function editBoard(boardId) {
  // given
  let title = $('#board-title-edited-' + boardId).val()
  if (title === '') {
    title = null;
  }
  let color = $('#board-color-edited-' + boardId).val()
  if (color === '') {
    color = null;
  }
  let info = $('#board-info-edited-' + boardId).val()
  if (info === '') {
    color = null;
  }
  const request = {
    title: title,
    color: color,
    info: info
  }

  // when
  fetch('/api/boards/' + boardId, {
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
  let email = document.getElementById('wp-collaborator-email-' + wId).value
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

async function inviteBoardCollaborator(bId) {
  // given
  let email = document.getElementById('board-collaborator-email-' + bId).value
  const request = {
    email: email
  }

  // when
  await fetch('/api/boards/' + bId + '/invite', {
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

function moveToBoard(bId) {
  window.location.href = BASE_URL + '/views/boards/' + bId
}

// 순수 javascript 동작
function logout() {
  resetToken()
  window.location.href = BASE_URL + '/views/login'
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
          <header>
            <h2>${title}</h2>
            <h3>${introduction}</h3>
            <div class="workspace-control-btns">
              <button onclick="editWorkspaceOnOff(${wId})"><i class="fas fa-pen"></i></button>
              
              <div id="edit-workspace-form-${wId}" style="display:none">
                <div>
                  <label for="workspace-title-edited-${wId}">Title</label>
                  <input type="text" id="workspace-title-edited-${wId}"/>
                </div>
                <div>
                  <label for="workspace-description-edited-${wId}">Description</label>
                  <input type="text" id="workspace-description-edited-${wId}"/>
                </div>
                <button id="edit-workspace-btn" onclick="editWorkspace(${wId})">Edit</button>
              </div>
    
              <button onclick="openInviteWpCollaborator(${wId})"><i class="fas fa-person"></i></button>
              <button onclick="deleteWorkspace(${wId})"><i class="fas fa-trash"></i></button>
              <div id="invite-wp-collaborator-${wId}" class="invite-collaborator">
                <h2>Invite collaborator to Workspace(<em>${title}</em>)</h2>
                  <div>
                    <label for="wp-collaborator-email-${wId}">Col Email</label>
                    <input type="text" id="wp-collaborator-email-${wId}"/>
                  </div>
                  <button onclick="inviteWpCollaborator(${wId})">Invite</button>
                  <button onclick="closeAllInviteCollaborators()">Cancel</button>
                  <ul id="invite-wp-collaborator-list-${wId}"></ul>
              </div>
            </div>
          </header>
          <div>
            <div id="workspace-board-list-${wId}" class="workspace-board-list"></div>
          </div>
        </div>
        `
}

function formMyBoard(board) {
  let boardId = board['boardId']
  let title = board['title']
  let color = board['color']

  return `
    <div id="board-${boardId}" class="board">
      <h3 onclick="moveToBoard(${boardId})">${title}</h3>
      <div id="board-${boardId}-btns" class="board-btns">
        <button onclick="editBoardOnOff(${boardId})"><i class="fa-regular fa-pen-to-square"></i></button>
        <div id="edit-board-form-${boardId}" style="display:none">
          <div>
            <label for="board-title-edited-${boardId}">보드 이름</label>
            <input type="text" id="board-title-edited-${boardId}"/>
          </div>
          <div>
            <label for="board-color-edited-${boardId}">보드 색상</label>
            <input type="text" id="board-color-edited-${boardId}"/>
          </div>
          <div>
            <label for="board-info-edited-${boardId}">보드 정보</label>
            <input type="text" id="board-info-edited-${boardId}"/>
          </div>
          <button id="edit-board-btn" onclick="editBoard(${boardId})">Edit</button>
        </div>
        <button onclick="openInviteBoardCollab(${boardId})"><i class="fa-solid fa-person"></i></button>
        <button onclick="deleteBoard(${boardId})"><i class="fa-solid fa-trash"></i></button>
        <div id="invite-board-collaborator-${boardId}" class="invite-collaborator">
          <h2>Invite collaborator to Board(<em>${title}</em>)</h2>
            <div>
              <label for="board-collaborator-email-${boardId}">Col Email</label>
              <input type="text" id="board-collaborator-email-${boardId}"/>
            </div>
            <button onclick="inviteBoardCollaborator(${boardId})">Invite</button>
            <button onclick="closeAllInviteCollaborators()">Cancel</button>
            <ul id="invite-board-collaborator-list-${boardId}"></ul>
        </div>
      </div>
    </div>
  `
}

function formCreateBoard(wId) {
  return `
        <div id="create-board-btn-${wId}" class="create-borad-btn board" onclick="createBoardOnOff(${wId})">Create New Board</div>
        <div id="create-board-form-${wId}" class="create-board-form board">
          <div class="create-borad-content">
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
       <div id="workspace-${wId}" class="workspace">
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
      <h3>${title}</h3>
    </div>
  `
}

function openInviteWpCollaborator(wId) {
  closeAllInviteCollaborators()
  $('#invite-wp-collaborator-' + wId).show()
}

function openInviteBoardCollab(bId) {
  closeAllInviteCollaborators()
  $('#invite-board-collaborator-' + bId).show()
}

function closeAllInviteCollaborators() {
  $('.invite-collaborator').hide()
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