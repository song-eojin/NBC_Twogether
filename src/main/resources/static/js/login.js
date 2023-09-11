const BASE_URL = 'http://localhost:8080'
// const BASE_URL = 'http://52.78.70.219'

async function login() {
    // given
    let email = document.getElementById('email').value
    let password = document.getElementById('password').value
    const request = {
        email: email,
        password: password
    }

    // when
    await fetch('/api/users/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(request)
    })

    // then
    .then(async res => {
        if (res.status === 200) {
            Cookies.set('Authorization', res.headers.get('Authorization'),
                {path: '/'})
            Cookies.set('Refresh-Token', res.headers.get('Refresh-Token'),
                {path: '/'})

            window.location.href = BASE_URL + '/views/workspace'
        } else {
            alert('로그인에 실패했습니다.')
        }
    })
}

function kakao_login() {
    var redirectUri = encodeURIComponent( BASE_URL + '/api/social/kakao/callback');
    var clientId = '52f4db5447641a22ec380d3f92260ce5';

    location.href = 'https://kauth.kakao.com/oauth/authorize?client_id=' + clientId + '&redirect_uri=' + redirectUri + '&response_type=code';
}

function naver_login() {
    var redirectUri = encodeURIComponent( BASE_URL + '/api/social/naver/callback');
    var clientId = 'z4SZ2cx6filEURbIUuq6';

    location.href = 'https://nid.naver.com/oauth2.0/authorize?client_id=' + clientId + '&redirect_uri=' + redirectUri + '&state=test&response_type=code';
}

