const BASE_URL = 'http://localhost:8080'

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
      // Cookie todo: AWS 배포 후 에 Cookie 저장 방식 정상 동작 여부에 따라 Cookie 설정 변경하기
      // e.g. document.cookie = 'Authorization' + '=' + res.headers.get('Authorization') + '; path=/'
      Cookies.set('Authorization', res.headers.get('Authorization'), { path: '/' })
      Cookies.set('Refresh-Token', res.headers.get('Refresh-Token'), { path: '/' })

      window.location.href = BASE_URL + '/views/workspace'
    } else {
      alert('로그인에 실패했습니다.')
    }
  })
}
