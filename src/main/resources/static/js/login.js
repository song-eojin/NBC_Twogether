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
      // Cookie
      let token = res.headers.get('Authorization')
      console.log(token)
      document.cookie = 'Authorization' + '=' + res.headers.get('Authorization') + '; path=/'

      token = res.headers.get('Refresh-Token')
      if (token !== null) {
        document.cookie = 'Refresh-Token' + '=' + token + '; path=/'
      }

      window.location.href = BASE_URL + '/views/workspace'
    } else {
      alert('로그인에 실패했습니다.')
    }
  })
}
