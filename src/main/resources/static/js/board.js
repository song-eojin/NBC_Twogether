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
			$('list-card-list-' + deck['deckId']).empty()
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

function editCardTitle(cardId, newTitle) {
	// given
	let title = newTitle
	if (title === '') {
		title = '카드 타이틀을 입력해주세요';
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
	})
}

function editCardContent(cardId, newContent) {
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
function logout() {
	resetToken()
	window.location.href = BASE_URL + '/views/login'
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
                        <p id="deck-title-${deckId}" class="list-header-title" onclick="toggleEditDeckTitle(${deckId})">${title} <i class="fas fa-pen"></i></p>  
                        <p class="list-header-archive" onclick="archiveDeck(${deckId})"><i class="fa fa-archive" aria-hidden="true"></i></p>
                    </div>
                    
                    <div id="edit-deck-title-form-${deckId}" class="edit-deck-title-form" style="display:none">
                        <input id="edit-deck-title-input-${deckId}" type="text" class="edit-deck-title-input" placeholder="새로운 덱 제목을 지어주세요..">
                        <button onclick="editDeck(${deckId})">제출</button>
                        <button onclick="toggleEditDeckTitle(${deckId})">취소</button>
                    </div>
                    
                    <div class="deck-list-add-card-area">
                        <div class="card-list-${deckId}" id="card-list-${deckId}">
                            <ul class="list-card-list" id="list-card-list-${deckId}"></ul>
                        </div>
                        
                        <!-- todo: 카드 추가 기능 활성화 -->
                        <div class="deck-list-add-card-container">
                            <a id="open-add-cardlist-button-${deckId}" class="open-add-cardlist-button" aria-label="카드 생성 열기">
                                <i class="fa-solid fa-plus fa-xl"></i>
                                카드 추가
                            </a>
                        </div>
                        
                        <!-- todo: 카드 추가 기능 -->
                        <div id="add-card-name-text-area-form-${deckId}" class="deck-list-add-card-name-text-area">
                            <div class="add-card-name-text-area-form hidden">
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
                                       aria-label="카드 추가 취소">
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
	let dueDate = card['dueDate']
	let content = card['content']
	let attachment = card['attachment']
	let cardLabels = card['cardLabels']
	let checkLists = card['checkLists']
	let comments = card['comments']
	let collaborators = card['cardCollaborators']

	return`
			<li class="card-list" id="card-list-${cardId}" onclick="openCardPage(${cardId})">
			    <span>
			        <p class="card-list-title" id="card-list-title-${cardId}" onclick="editTitle(${cardId}, event)">
			            ${title}
			        </p>
			    </span>
			</li>
			<div id="card-page-wrapper-${cardId}" class="card-page-wrapper">
				<div id="card-page-${cardId}" class="card-page">
					<button id="close-card" onclick="closeCard(${cardId})">닫기</button>
				    <div class="card-header">
				        <p class="card-page-title" id="card-page-title-${cardId}" onclick="editTitleInCP(${cardId})">
				            ${title}
				        </p>
				    </div>
				    <div class="card-main">
				        <h2 style="display: none">카드 작업자</h2>
				        <!--카드 작업자 추가 기능 및 정렬-->
				        <h2 style="display: none">라벨</h2>
				        <!--트렐로 참조-->
				        <h2 style="display: none">마감일</h2>
				        <!--달력 처럼 보여줄지 그냥 YYYY.MM.dd 꼴로 보여줄지 논의-->
				        <h2>카드 설명</h2>
				        <p class="card-page-content" id="card-page-content-${cardId}" onclick="editContentInCP(${cardId})"> <!--설명 수정 메서드 추가-->
				            ${content}
				        </p>
				        <h2 style="display: none">첨부 파일</h2>
				        <!--첨부파일이 없으면 파일을 올릴 수 있도록 드래그 할 수 있는 공간이 있고, 있다면 파일 형식에 따라 보여주기-->
				        <h2 style="display: none">체크리스트</h2>
				        <!--체크리스트 내 체크된 아이템 개수/체크리스트 내 아이템 개수로 달성도 표시-->
				        <h2 style="display: none">댓글</h2>
				        <!--본인 프로필 이미지와 댓글 입력창-->
				        <!--댓글 쓴 유저의 프로필 이미지와 카드 댓글 목록-->
					</div>
					<div class="card-sidebar">
						<button class="sidebar-button" id="sidebar-button-members" style="display: none">Members</button>
						<button class="sidebar-button" id="sidebar-button-labels" style="display: none">Labels</button>
						<button class="sidebar-button" id="sidebar-button-checklist" style="display: none">Checklist</button>
						<button class="sidebar-button" id="sidebar-button-duedate" style="display: none">Due Date</button>
						<button class="sidebar-button" id="sidebar-button-attachment" style="display: none">Attachment</button>
						<button class="sidebar-button" id="sidebar-button-archive" onclick="archiveCard(${cardId})">Archive</button>
					</div>
				</div>
			</div>
	`
}

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
			const newTitle = inputElement.value;
			titleElement.innerHTML = newTitle;
			editCardTitle(cardId, newTitle);
		}
	});

	// input 요소에서 포커스가 해제되면 수정 완료 처리
	inputElement.addEventListener("blur", () => {
		const newTitle = inputElement.value;
		titleElement.innerHTML = newTitle;
		editCardTitle(cardId, newTitle);
	});
}

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
			editCardTitle(cardId, newTitle);
		}
	});

	// input 요소에서 포커스가 해제되면 수정 완료 처리
	inputElement.addEventListener("blur", () => {
		const newTitle = inputElement.value;
		titleElement.innerHTML = newTitle;
		editCardTitle(cardId, newTitle);
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
			contentElement.innerHTML = newContent;
			editCardContent(cardId, newContent);
		}
	});

	// input 요소에서 포커스가 해제되면 수정 완료 처리
	inputElement.addEventListener("blur", () => {
		const newContent = inputElement.value;
		contentElement.innerHTML = newContent;
		editCardContent(cardId, newContent);
	});
}

function openCardPage(cardId) {
	// 팝업 창을 보이도록 설정
	var cardPageWrapper = document.getElementById("card-page-wrapper-" + cardId);
	var cardPage = document.getElementById("card-page-" + cardId);
	cardPageWrapper.style.display = "block";
	cardPage.style.display = "block";
}

function closeCard(cardId) {
	// 팝업 창을 숨김
	var cardPageWrapper = document.getElementById("card-page-wrapper-" + cardId);
	var cardPage = document.querySelector(".card-page");
	cardPageWrapper.style.display = "none";
	cardPage.style.display = "none";

	callMyBoard().then()
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
				let nextDeckId = targetDeck.nextElementSibling === null ? 0
					: targetDeck.nextElementSibling.id
				moveDeck(currentDeck.id, targetDeck.id, nextDeckId)
				.then(() => deckList.insertBefore(currentDeck,
					targetDeck.nextElementSibling))
			} else {
				let prevDeckId = targetDeck.previousElementSibling === null ? 0
					: targetDeck.previousElementSibling.id
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