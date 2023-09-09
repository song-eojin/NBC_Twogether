const BASE_URL = 'http://localhost:8080'

async function submitEmail() {
  // given
  let email = document.getElementById('email').value
  const request = {
    email: email
  }

  // when
  await fetch('/api/users/send-verification', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  })

  // then
  .then(async res => {
    if (res.status === 200) {
      alert('인증번호가 전송되었습니다.')
    } else {
      let fetched = await res.json()
      alert(fetched.message)
    }
  })
  .catch(err => console.log("error: ", err))
}

async function submitCertificationNumber() {
  // given
  let email = document.getElementById('email').value
  let certificationNumber = document.getElementById('certificationNumber').value
  let params = {
    email: email,
    certificationNumber: certificationNumber
  }

  // when
  let query = Object.keys(params)
  .map(k => encodeURIComponent(k) + '=' + encodeURIComponent(params[k]))
  .join('&')

  let url = BASE_URL + '/api/users/verify?' + query

  await fetch(url, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json'
    }
  })

  // then
  .then(async res => {
    if (res.status === 200) {
      alert('이메일이 인증되었습니다.')
    } else {
      let fetched = await res.json()
      alert(fetched.message)
    }
  })
  .catch(err => console.log("error: ", err))
}

async function signup() {
  // given
  let email = document.getElementById('email').value
  let password = document.getElementById('password').value
  let passwordConfirm = document.getElementById('passwordConfirm').value

  if (password !== passwordConfirm) {
    alert('비밀번호와 비밀번호 확인은 동일한 비밀번호를 입력해야 합니다.')
    return
  }

  const request = {
    email: email,
    password: password
  }

  // when
  const response = await fetch('/api/users/signup', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  })

  // then
  .then(async res => {
    if (res.status === 201) {
      window.location.href = BASE_URL + '/views/login'
    } else {
      let fetchedData = await res.json()
      alert(fetchedData.message)
    }
  })
  .catch(err => console.log("error: ", err))
}