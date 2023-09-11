const BASE_URL = 'http://localhost:8080'
// const BASE_URL = 'http://52.78.70.219'

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
async function logout() {
    // when
    await fetch('/api/users/logout', {
        method: 'DELETE',
        headers: {
            'Authorization': Cookies.get('Authorization'),
            'Refresh-Token': Cookies.get('Refresh-Token')
        }
    })

    // then
    .then(async res => {
        checkTokenExpired(res)
        refreshToken(res)

        resetToken()
        window.location.href = BASE_URL + '/views/login'
    })
}

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
// 워크스페이스로 이동
function moveToWorkspace() {
	window.location.href = BASE_URL + '/views/workspace'
}

// Board 관련 로직
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

		var boardTitle =$('#board-title')

		var decks = $('#deck-list')
		var archive = $('#archive-container')

		boardTitle.empty()
		decks.empty()
		archive.empty()
		let board = await res.json()

		for(let boardCollaborator of board['boardCollaborators']) {
			$('#invite-board-collaborator-list').append(formBoardCollaborator(boardId,boardCollaborator))
		}

		boardTitle.append(board['title'])
		for (let deck of board['decks']) {
			if (deck['archived']) {
				archive.append(formArchived(deck))
			} else {
				decks.append(formDeck(deck))
				for (let card of deck['cards']) {
					if (card['archived']) {
						archive.append(formArchivedCard(card))
					} else {
						$('#list-card-list-' + deck['deckId']).append(formCard(card))
					}
				}
			}
		}
	})
}

async function callMyCard(cardId) {
	// when
	await fetch('/api/cards/' + cardId, {
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

		let cardPage = $('#card-page-' + cardId)
		let card = await res.json()

		cardPage.empty()
		cardPage.append(formCardPage(card))
		// 댓글 추가
		let commentList = $('#comment-list-' + cardId)
		for (let comment of card['comments']) {
			commentList.append(formCommentList(comment))
		}
		// 체크리스트 추가
		let checkLists = $('#checkList-list-' + cardId)
		for (let checkList of card['checkLists']) {
			checkLists.append(formCheckList(checkList))
			let checkListItems = $('#checkList-items-' + checkList['clId'])
			for (let chlItem of checkList['chlItems']) {
				checkListItems.append(formCheckListItem(chlItem))
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

function editBoardOnOff() {
	$('#edit-board-form').toggle()
}

function closeEditBoard() {
	$('#edit-board-form').hide()
}

function editBoard() {
	//given
	let boardId = document.getElementById('boardId').textContent

	let title = document.getElementById('board-title-edited').value
	if (title === '') {
		title = null;
	}
	let color = document.getElementById('board-color-edited').value
	if (color === '') {
		color = null;
	}
	let info = document.getElementById('board-info-edited').value
	if (info === '') {
		info = null;
	}

	console.log(title)

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

			closeEditBoard()
			await callMyBoard()
		});
}
function openInviteBoardCollab() {
	closeAllInviteCollaborators()
	$('#invite-board-collaborator').show()
}

function closeAllInviteCollaborators() {
	$('.invite-collaborator').hide()
}

async function inviteBoardCollaborator() {
	// given
	let boardId = document.getElementById('boardId').textContent


	let email = document.getElementById('board-collaborator-email').value
	const request = {
		email: email
	}

	// when
	await fetch('/api/boards/' + boardId + '/invite', {
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
			await callMyBoard()
		})
}

function formBoardCollaborator(boardId,boardCollaborator) {
	let boardColId = boardCollaborator['boardColId']
	let email = boardCollaborator['email']
	let nickname = boardCollaborator['nickname']

	return `
				<li id="board-col-${boardColId}">
				        <div class="board-col-lists">
          <div class="board-col-lists-email">

				<span>${nickname}</span>
				<span id="board-col-email-${boardColId}">${email}</span>
				          </div>
				<button class="col-delete-btn" onclick="deleteBoardCollaborator(${boardId},${boardColId})">추방</button>
				        </div>
				</li>
				`
}

function deleteBoard() {
	let boardId = document.getElementById('boardId').textContent

	let check = confirm("보드를 삭제하시겠습니까?")
	if (!check) {
		return
	}

	// when
	fetch(`/api/boards/${boardId}`, {
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

			moveToWorkspace()
		})
}

function deleteBoardCollaborator(boardId,boardColId) {
	//given
	let boardColEmail = document.getElementById('board-col-email-' + boardColId).innerHTML

	const request = {
		email: boardColEmail
	}

	// when
	fetch('/api/boards/' + boardId + '/invite', {
		method: "DELETE",
		headers: {
			"Content-Type": "application/json",
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
		}

		$('#board-col-' + boardColId).remove()
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

		await callMyBoard() // board 다시 부르기
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
		var editTitle = document.getElementById(
			'edit-deck-title-input-' + deckId)
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

		callMyBoard() // board 다시 부르기
	})
}

async function createCard(deckId) {
	// given
	let title = document.getElementById('card-title-input-' + deckId).value
	if (title === '') {
		title = '카드 제목'
	}

	// when
	await fetch('/api/decks/' + deckId + '/cards', {
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

		callMyBoard()
	})
}

async function editCardTitleB(cardId, newTitle) {
	// given
	let title = newTitle
	if (title === '') {
		title = null;
	}
	let content = null;
	const request = {
		title: title,
		content: content
	};

	// when
	fetch('/api/cards/' + cardId, {
		method: "PATCH",
		headers: {
			"Content-Type": "application/json",
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
		}
		callMyBoard()
	})
}

async function editCardTitleC(cardId, newTitle) {
	// given
	let title = newTitle
	if (title === '') {
		title = null;
	}
	let content = null;
	const request = {
		title: title,
		content: content
	};

	// when
	fetch('/api/cards/' + cardId, {
		method: "PATCH",
		headers: {
			"Content-Type": "application/json",
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
		}
		callMyCard(cardId)
	})
}

async function editCardContent(cardId, newContent) {
	// given
	let title = null;
	let content = newContent
	if (content === '') {
		content = '카드 설명을 입력해주세요'
	}
	const request = {
		title: title,
		content: content
	};

	// when
	fetch('/api/cards/' + cardId, {
		method: "PATCH",
		headers: {
			"Content-Type": "application/json",
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
		}
		callMyCard(cardId)
	})
}

async function submitComment(cardId) {
	// given
	let boardId = document.getElementById('boardId').textContent
	let newComment = document.getElementById('comment-input-' + cardId).value
	if (newComment === '') {
		return
	}
	const request = {
		content: newComment
	};

	// when
	fetch('/api/boards/' + boardId + '/cards/' + cardId + '/comments', {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
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
		}

		callMyCard(cardId).then()
	})
}

async function editComment(cardId, commentId, newComment) {
	// given
	let content = newComment
	if (content === '') {
		content = null
	}
	const request = {
		content: content
	};

	// when
	fetch('/api/comments/' + commentId, {
		method: "PUT",
		headers: {
			"Content-Type": "application/json",
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
		}
		callMyCard(cardId);
	})
}

async function deleteComment(cardId, commentId) {
	let check = confirm("해당 댓글을 삭제하시겠습니까?")
	if (!check) {
		return
	}

	fetch('/api/comments/' + commentId, {
		method: "DELETE",
		headers: {
			"Content-Type": "application/json",
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
		}

		callMyCard(cardId).then()
	})
}

async function addCheckList(cardId) {
	// given
	let title = 'CheckList'

	const request = {
		title: title
	};

	// when
	fetch('/api/cards/' + cardId + '/checklists', {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
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
		}

		callMyCard(cardId).then()
	})
}

async function editCheckListTitle(cardId, chlId, newTitle) {
	// given
	let title = newTitle
	if (title === '') {
		title = null
	}
	const request = {
		title: title
	};

	// when
	fetch('/api/checklists/' + chlId, {
		method: "PUT",
		headers: {
			"Content-Type": "application/json",
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
		}
		callMyCard(cardId);
	})
}

async function addCheckListItem(cardId, checkListId) {
	// given
	let content = document.getElementById('checkList-item-input-' + checkListId).value

	// when
	fetch('/api/checklists/' + checkListId + '/chlItems', {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
			'Authorization': Cookies.get('Authorization'),
			'Refresh-Token': Cookies.get('Refresh-Token')
		},
		body: content,
	})

	// then
	.then(async res => {
		checkTokenExpired(res)
		refreshToken(res)

		if (res.status !== 200) {
			let error = await res.json()
			alert(error['message'])
		}

		callMyCard(cardId).then()
	})
}

async function checkItem(cardId, chlItemId) {
	// when
	fetch('/api/chlItems/' + chlItemId + '/isChecked', {
		method: "PATCH",
		headers: {
			"Content-Type": "application/json",
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
		}

		callMyCard(cardId).then()
	})
}

async function editChlItem(cardId, chlItemId, newContent) {
	// given
	let content = newContent
	if (content === '') {
		content = null
	}

	// when
	fetch('/api/chlItems/' + chlItemId + '/content', {
		method: "PATCH",
		headers: {
			"Content-Type": "application/json",
			'Authorization': Cookies.get('Authorization'),
			'Refresh-Token': Cookies.get('Refresh-Token')
		},
		body: content,
	})

	// then
	.then(async res => {
		checkTokenExpired(res)
		refreshToken(res)

		if (res.status !== 200) {
			let error = await res.json()
			alert(error['message'])
		}
		callMyCard(cardId);
	})
}

async function deleteCheckList(cardId, checkListId) {
	// when
	fetch('/api/checklists/' + checkListId, {
		method: "DELETE",
		headers: {
			"Content-Type": "application/json",
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
		}

		callMyCard(cardId).then()
	})
}

async function deleteCheckListItem(cardId, chlItemId) {
	// when
	fetch('/api/chlItems/' + chlItemId, {
		method: "DELETE",
		headers: {
			"Content-Type": "application/json",
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
		}

		callMyCard(cardId).then()
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

async function moveCard(targetDeckId, draggedCardId, prevCardId, nextCardId) {
	// given
	const request = {
		prevCardId: prevCardId,
		nextCardId: nextCardId,
		deckId: targetDeckId
	}

	// when
	await fetch('/api/cards/' + draggedCardId + '/move', {
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

async function archiveCard(cardId) {
	let check = confirm("해당 카드를 보관하시겠습니까?")
	if (!check) {
		return
	}

	// when
	await fetch('/api/cards/' + cardId + '/archive', {
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

async function restoreCard(cardId) {
	// when
	await fetch('/api/cards/' + cardId + '/archive', {
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

		callMyBoard() // board 다시 부르기
	})
}

async function deleteCard(cardId) {
	let check = confirm("해당 카드를 삭제하시겠습니까?")
	if (!check) {
		return
	}

	// when
	await fetch('/api/cards/' + cardId, {
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

		$('#archive-card-' + cardId).remove()
	})
}

// 순수 javascript 동작
function moveToWorkspace() {
	window.location.href = BASE_URL + '/views/workspace'
}

function toggleCreateWorkspace() {
	$('#create-workspace-form').toggle()
}

function createCardOnOff() {
	$('#create-card-form').toggle()
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
                        <div id="deck-title-${deckId}" class="list-header-title" onclick="toggleEditDeckTitle(${deckId})">${title} <i class="fas fa-pen"></i></div>  
                        <div class="list-header-archive" onclick="archiveDeck(${deckId})"><i class="fa fa-archive" aria-hidden="true"></i></div>
                    </div>
                    
                    <div id="edit-deck-title-form-${deckId}" class="edit-deck-title-form" style="display:none">
                        <input id="edit-deck-title-input-${deckId}" type="text" class="edit-deck-title-input" placeholder="새로운 덱 제목을 지어주세요..">
                        <button onclick="editDeck(${deckId})">제출</button>
                        <button onclick="toggleEditDeckTitle(${deckId})">취소</button>
                    </div>
                    
                    <div class="deck-list-add-card-area">
                        <div class="cards" id="cards-${deckId}">
                            <ul class="list-card-list" id="list-card-list-${deckId}"></ul>
                        </div>
                        
                        <!-- todo: 카드 추가 기능 활성화 -->
                        <div class="deck-list-add-card-container" id="add-card-button-${deckId}" onclick="toggleCreateCard(${deckId})">
                            <a id="open-add-cardlist-button-${deckId}" class="open-add-cardlist-button" aria-label="카드 생성 열기">
                                <i class="fa-solid fa-plus fa-xl"></i>
                                카드 추가
                            </a>
                        </div>
                        
                        <!-- todo: 카드 추가 기능 -->
                        <div id="add-card-name-text-area-form-${deckId}" class="deck-list-add-card-name-text-area" hidden>
                            <div class="add-card-name-text-area-form">
                                <input type="text" name="add-cardlist-input"
                                       class="add-cardlist-input"
                                       id="card-title-input-${deckId}"
                                       placeholder="카드 내용을 입력하세요...">
                                <div class="horizontal-align">
                                    <button type="submit"
                                            class="add-cardlist-submit default-button"
                                            onclick="createCard(${deckId})">카드 추가
                                    </button>
                                    <a class="cancel-button cardlist"
                                       aria-label="카드 추가 취소"  onclick="toggleCreateCard(${deckId})">
                                        <i class="fa-solid fa-xmark fa-xl"></i>
                                    </a>
                                </div>
                            </div>
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

function formArchivedCard(card) {
	let cardId = card['id']
	let title = card['title']

	return `
        <li id="archive-card-${cardId}">
            <span class="archive-item-title">${title}</span>
            <div class="archive-btns">
                <button onclick="restoreCard(${cardId})">복구</button>
                <button onclick="deleteCard(${cardId})">삭제</button>
            </div>
        </li>
    `
}

function formCard(card) {
	let cardId = card['id']
	let title = card['title']

	return`
			<li class="card-list" id="card-list-${cardId}" onclick="callMyCard(${cardId})">
				<div class="card-list-title" id="card-list-title-${cardId}">
			    	<span onclick="editTitle(${cardId}, event)">
						${title}
					</span>
			    </div>
			</li>
			<div id="card-page-${cardId}"></div>
	`
}

function formCardPage(card) {
	let cardId = card['id']
	let title = card['title']
	let dueDate = card['dueDate']
	let content = card['content']
	let attachment = card['attachment']
	let cardLabels = card['cardLabels']
	let collaborators = card['cardCollaborators']

	return `
			<div id="card-page-wrapper" class="card-page-wrapper">
				<div id="card-page-${cardId}" class="card-page">
					<button id="close-card" onclick="closeCard(${cardId})"><i class="fa-solid fa-xmark fa-xl"></i></button>
				    <div class="card-header">
				    	<i class="fa-regular fa-note-sticky"></i>
				        <div class="card-page-title" id="card-page-title-${cardId}" onclick="editTitleInCP(${cardId})">
				            ${title}
				        </div>
				    </div>
				    <div class="card-main">
				        <h2 style="display: none">카드 작업자</h2>
				        <!--카드 작업자 추가 기능 및 정렬-->
				        <h2 style="display: none">라벨</h2>
				        <!--트렐로 참조-->
				        <h2 style="display: none">마감일</h2>
				        <!--달력 처럼 보여줄지 그냥 YYYY.MM.dd 꼴로 보여줄지 논의-->
				        <div class="card-description">
				        	<div class="card-description-header">
				    		<i class="fa-regular fa-pen-to-square"></i>
				        	<h2>카드 설명</h2>
                        	</div>
                        	<div class="card-description-content">
				        	<div class="card-page-content" id="card-page-content-${cardId}" onclick="editContentInCP(${cardId})"> <!--설명 수정 메서드 추가-->
				            ${content}
				        	</div>
				        	</div>
				        </div>
				        <h2 style="display: none">첨부 파일</h2>
				        <!--첨부파일이 없으면 파일을 올릴 수 있도록 드래그 할 수 있는 공간이 있고, 있다면 파일 형식에 따라 보여주기-->

				        <div class="checkList-list" id="checkList-list-${cardId}"></div>
				        
				        	<div class="card-comment">
    								<div class="card-comment-header">
    									<i class="fa-solid fa-comments fa-sm"></i>
				        			<h2>댓글</h2>
				        		</div>
				        		<div class="card-comment-content">
				        			<div class="comment-input">
				        				<span id="nickname"><!--현재 사용자의 닉네임과 아이콘 표시--></span>
				            		<input type="text" id="comment-input-${cardId}" placeholder="댓글 작성...">
				            		<button onclick="submitComment(${cardId})">제출</button>
				        			</div>
				        		</div>	
				        		<div class="comment" id="comment-list-${cardId}"></div>
					</div>
					<div class="card-sidebar">
						<button class="sidebar-button" id="sidebar-button-members" style="display: none">Members</button>
						<button class="sidebar-button" id="sidebar-button-labels" style="display: none">Labels</button>
						<button class="sidebar-button" id="sidebar-button-checklist" onclick="addCheckList(${cardId})">Checklist</button>
						<button class="sidebar-button" id="sidebar-button-duedate" style="display: none">Due Date</button>
						<button class="sidebar-button" id="sidebar-button-attachment" style="display: none">Attachment</button>
						<button class="sidebar-button" id="sidebar-button-archive" onclick="archiveCard(${cardId})">Archive</button>
					</div>
				</div>
			</div>
	`
}

function formCommentList(comment) {
	let cardId = comment['cardId']
	let commentId = comment['commentId']
	let writer = comment['writer']
	let icon = comment['icon']
	let content = comment['content']

	return`
		<div class="comment-item" id="comment-${commentId}">
			<img src=${icon} alt=${writer}>
			<div class="comment-content" id="comment-content-${commentId}" onclick="editCommentInCP(${cardId}, ${commentId})">${content}</div>
			<i class="fa-solid fa-circle-xmark cp-delete-button delete-comment" onclick="deleteComment(${cardId}, ${commentId})"></button>
		<div>
	`
}

function formCheckList(checkList) {
	let cardId = checkList['cardId']
	let checkListId = checkList['clId']
	let title = checkList['title']

	return`
		<div class="checkList" id="checkList-${checkListId}">
			<div class="card-checklist-header">
				<i class="fa-regular fa-square-check"></i>
				<div class="checkList-title" id="checkList-title-${checkListId}" onclick="editCheckListTitleInCP(${cardId}, ${checkListId})">${title}</div>
				<i class="fa-solid fa-circle-xmark cp-delete-button" onclick="deleteCheckList(${cardId}, ${checkListId})"></i>
			</div>
			<div class="checkList-item-input">
				<input type="text" id="checkList-item-input-${checkListId}" placeholder="체크박스 작성...">
				<button onclick="addCheckListItem(${cardId}, ${checkListId})">생성</button>
			</div>
			<div class="checkList-items" id="checkList-items-${checkListId}"></div>
		</div>
	`
}

function formCheckListItem(chlItem) {
	let cardId = chlItem['cardId']
	let chlItemId = chlItem['chlItemId']
	let content = chlItem['content']
	let checked = chlItem['checked']

	let checkbox = ``
	if (checked) {
		checkbox = `<input type="checkbox" id="checkbox-${chlItemId}" onclick="checkItem(${cardId}, ${chlItemId})" checked>`
	} else {
		checkbox = `<input type="checkbox" id="checkbox-${chlItemId}" onclick="checkItem(${cardId}, ${chlItemId})">`
	}

	return`
		<div class="checkList-item" id="checkList-item-${chlItemId}">
			${checkbox}
			<!--label로 설정하면 클릭 시 체크박스만 체크되고 content 수정이 안되는 문제가 있어 p로 변경하였습니다.-->
			<div class="checkbox-title" id="checkbox-title-${chlItemId}" onclick="editChlItemInCP(${cardId}, ${chlItemId})">${content}</div>
			<i class="fa-solid fa-circle-xmark cp-delete-button" onclick="deleteCheckListItem(${cardId}, ${chlItemId})"></i>
		</div>
	`
}

function closeCard(cardId) {
	let cardPage = document.getElementById('card-page-' + cardId)
	while (cardPage.firstChild) {
		cardPage.removeChild(cardPage.firstChild)
	}
	callMyBoard()
}

// 보드 페이지에서 카드 제목 수정하기
function editTitle(cardId, event) {
	event.stopPropagation();
	// 클릭한 제목 요소 가져오기
	const titleElement = document.getElementById(`card-list-title-${cardId}`);

	// 현재 제목 내용 가져오기
	const currentTitle = titleElement.innerText;

	// 수정 가능한 input 요소 생성
	const inputElement = document.createElement("input");
	inputElement.value = currentTitle;

	// 제목을 input 요소로 교체
	titleElement.innerHTML = "";
	titleElement.appendChild(inputElement);

	// input 요소에 포커스 설정
	inputElement.focus();

	inputElement.addEventListener("keydown", (event) => {
		if (event.key === "Enter") {
			event.preventDefault();
			let newTitle = inputElement.value;
			if (newTitle === '') {
				newTitle = currentTitle;
			}
			titleElement.innerHTML = newTitle;
			editCardTitleB(cardId, newTitle);
		}
	});

	// input 요소에서 포커스가 해제되면 수정 완료 처리
	inputElement.addEventListener("blur", () => {
		let newTitle = inputElement.value;
		if (newTitle === '') {
			newTitle = currentTitle;
		}
		titleElement.innerHTML = newTitle;
		editCardTitleB(cardId, newTitle);
	});
}

// 카드 페이지에서 카드 제목 수정하기
function editTitleInCP(cardId) {
	// 클릭한 제목 요소 가져오기
	const titleElement = document.getElementById(`card-page-title-${cardId}`);

	// 현재 제목 내용 가져오기
	const currentTitle = titleElement.innerText;

	// 수정 가능한 input 요소 생성
	const inputElement = document.createElement("input");
	inputElement.value = currentTitle;

	// 제목을 input 요소로 교체
	titleElement.innerHTML = "";
	titleElement.appendChild(inputElement);

	// input 요소에 포커스 설정
	inputElement.focus();

	inputElement.addEventListener("keydown", (event) => {
		if (event.key === "Enter") {
			event.preventDefault();
			const newTitle = inputElement.value;
			titleElement.innerHTML = newTitle;
			editCardTitleC(cardId, newTitle);
		}
	});

	// input 요소에서 포커스가 해제되면 수정 완료 처리
	inputElement.addEventListener("blur", () => {
		const newTitle = inputElement.value;
		titleElement.innerHTML = newTitle;
		editCardTitleC(cardId, newTitle);
	});
}

function editContentInCP(cardId) {
	// 클릭한 제목 요소 가져오기
	const contentElement = document.getElementById(`card-page-content-${cardId}`);

	// 현재 제목 내용 가져오기
	const currentContent = contentElement.innerText;

	// 수정 가능한 input 요소 생성
	const inputElement = document.createElement("input");
	inputElement.value = currentContent;

	// 제목을 input 요소로 교체
	contentElement.innerHTML = "";
	contentElement.appendChild(inputElement);

	// input 요소에 포커스 설정
	inputElement.focus();

	inputElement.addEventListener("keydown", (event) => {
		if (event.key === "Enter") {
			event.preventDefault();
			const newContent = inputElement.value;
			contentElement.innerHTML += newContent;
			editCardContent(cardId, newContent);
		}
	});

	// input 요소에서 포커스가 해제되면 수정 완료 처리
	inputElement.addEventListener("blur", () => {
		const newContent = inputElement.value;
		contentElement.innerHTML += newContent;
		editCardContent(cardId, newContent);
	});
}

function editCommentInCP(cardId, commentId) {
	// 클릭한 댓글 요소 가져오기
	const contentElement = document.getElementById(`comment-content-${commentId}`);

	// 현재 댓글 내용 가져오기
	const currentContent = contentElement.innerText;

	// 수정 가능한 input 요소 생성
	const inputElement = document.createElement("input");
	inputElement.value = currentContent;

	// 댓글을 input 요소로 교체
	contentElement.innerHTML = "";
	contentElement.appendChild(inputElement);

	// input 요소에 포커스 설정
	inputElement.focus();

	inputElement.addEventListener("keydown", (event) => {
		if (event.key === "Enter") {
			event.preventDefault();
			const newComment = inputElement.value;
			contentElement.innerHTML = newComment;
			editComment(cardId, commentId, newComment);
		}
	});

	// input 요소에서 포커스가 해제되면 수정 완료 처리
	inputElement.addEventListener("blur", () => {
		const newComment = inputElement.value;
		contentElement.innerHTML = newComment;
		editComment(cardId, commentId, newComment);
	});
}

function editCheckListTitleInCP(cardId, chlId) {
	// 클릭한 제목 요소 가져오기
	const titleElement = document.getElementById(`checkList-title-${chlId}`);

	// 현재 제목 내용 가져오기
	const currentTitle = titleElement.innerText;

	// 수정 가능한 input 요소 생성
	const inputElement = document.createElement("input");
	inputElement.value = currentTitle;

	// 제목을 input 요소로 교체
	titleElement.innerHTML = "";
	titleElement.appendChild(inputElement);

	// input 요소에 포커스 설정
	inputElement.focus();

	inputElement.addEventListener("keydown", (event) => {
		if (event.key === "Enter") {
			event.preventDefault();
			const newTitle = inputElement.value;
			titleElement.innerHTML = newTitle;
			editCheckListTitle(cardId, chlId, newTitle);
		}
	});

	// input 요소에서 포커스가 해제되면 수정 완료 처리
	inputElement.addEventListener("blur", () => {
		const newTitle = inputElement.value;
		titleElement.innerHTML = newTitle;
		editCheckListTitle(cardId, chlId, newTitle);
	});
}

function editChlItemInCP(cardId, chlItemId) {
	// 클릭한 제목 요소 가져오기
	const titleElement = document.getElementById(`checkbox-title-${chlItemId}`);

	// 현재 제목 내용 가져오기
	const currentTitle = titleElement.innerText;

	// 수정 가능한 input 요소 생성
	const inputElement = document.createElement("input");
	inputElement.value = currentTitle;

	// 제목을 input 요소로 교체
	titleElement.innerHTML = "";
	titleElement.appendChild(inputElement);

	// input 요소에 포커스 설정
	inputElement.focus();

	inputElement.addEventListener("keydown", (event) => {
		if (event.key === "Enter") {
			event.preventDefault();
			const newTitle = inputElement.value;
			titleElement.innerHTML = newTitle;
			editChlItem(cardId, chlItemId, newTitle);
		}
	});

	// input 요소에서 포커스가 해제되면 수정 완료 처리
	inputElement.addEventListener("blur", () => {
		const newTitle = inputElement.value;
		titleElement.innerHTML = newTitle;
		editChlItem(cardId, chlItemId, newTitle);
	});
}

function toggleEditDeckTitle(deckId) {
    $('#edit-deck-title-form-' + deckId).toggle()
}

function toggleCreateDeckForm() {
	$('.add-decklist-button').toggle();
    $('#create-deck-form').toggle();
}

function toggleCreateCard(deckId) {
	$('#add-card-name-text-area-form-' + deckId).toggle();
	$('#add-card-button-' + deckId).toggle();
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