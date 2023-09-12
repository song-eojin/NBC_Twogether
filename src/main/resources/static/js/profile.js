function logout() {
    resetToken()
    window.location.href = BASE_URL + '/views/login'
}

async function callMyUserInfo() {

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
        $('#introduction').text(user.introduction)
        $('#role').text(user.role) // 필요한지?

        let imageURL = user.icon;
        $('#header-profileImage').attr('src', imageURL);
        $('#panel-profileImage').attr('src', imageURL);

        // 본인이 생성한 workspace 불러오기
        callMyWorkspaces()
        // 본인이 초대된 workspace 불러오기
        callColWorkspaces()
    })
}

function editUserInfo() {
    const oldNickname = $('#nickname').text();
    const oldIntroduction = $('#introduction').text();

    const newNickname = $('#edit-nick-input').val();
    const newIntroduction = $('#edit-intro-input').val();

    if (!newNickname && !newIntroduction) {
        // 두 입력 필드가 모두 비어 있으면 아무 작업도 수행하지 않음
        closeEditUserInfoForm();
        return;
    }

    // 수정된 필드만 업데이트할 객체 생성
    const request = {
        nickname: newNickname || oldNickname,
        introduction: newIntroduction || oldIntroduction,
    };

    fetch('/api/users/info', {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': Cookies.get('Authorization'),
            'Refresh-Token': Cookies.get('Refresh-Token')
        },
        body: JSON.stringify(request),
    })
    .then(async res => {
        checkTokenExpired(res);
        refreshToken(res);

        if (res.status === 200) {
            // 업데이트 성공 시 UI 업데이트
            $('#nickname').text(request.nickname);
            $('#introduction').text(request.introduction);

            // 수정 폼 닫기
            closeEditUserInfoForm();
        } else {
            let error = await res.json();
            alert(error.message);
        }
    });
}

function editProfileImage() {
    const fileInput = document.getElementById('upload-profileImage-input');
    const selectedFile = fileInput.files[0];

    if (!selectedFile) {
        alert('파일을 선택해주세요.');
        return;
    }

    // FormData를 사용하여 파일 전송 준비
    const formData = new FormData();
    formData.append('multipartFile', selectedFile);

    fetch(BASE_URL + '/api/users/icon', {
        method: 'PUT',
        headers: {
            'Authorization': Cookies.get('Authorization'),
            'Refresh-Token': Cookies.get('Refresh-Token'),
        },
        body: formData,
    })
    .then(async (res) => {
        checkTokenExpired(res);

        if (res.status === 200) {
            alert('프로필 이미지가 업데이트되었습니다.');
            getProfileImage()
        } else {
            const errorData = await res.json();
            alert(errorData.message);
        }
    })
    .catch((error) => {
        console.error('프로필 이미지 업데이트 실패:', error);
    });
}

function getProfileImage() {

    // when
    fetch('/api/users/info', {
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
        let imageURL = user.icon;
        $('#header-profileImage').attr('src', imageURL);
        $('#panel-profileImage').attr('src', imageURL);
        $('#profileImage-btns').hide();
        $('#change-userImage-btn, #change-userInfo-btn').show();
        closeEditUserInfoForm()
    })
}

function defaultProfileImage() {

    // when
    fetch('/api/users/default', {
        method: 'PUT',
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
        let imageURL = user.icon;
        $('#header-profileImage').attr('src', imageURL);
        $('#panel-profileImage').attr('src', imageURL);
        $('#profileImage-btns').hide();
        getProfileImage();
    })
}

function closeEditUserInfoForm() {
    $('#edit-nick-input, #edit-intro-input').val('');
    $('#edit-nick-input, #edit-intro-input, #save-edit-userInfo-btn, #cancel-userInfo-btn').hide();
    $('#nickname, #introduction, #change-userInfo-btn, #change-userImage-btn').show();
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