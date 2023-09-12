function callMyAlarms() {

    $('#alarm_list').empty();

    fetch('/api/alarms', {
        method: 'GET',
        headers: {
            'Authorization': Cookies.get('Authorization'),
            'Refresh-Token': Cookies.get('Refresh-Token')
        }
    })

    .then(async res => {
        checkTokenExpired(res)
        refreshToken(res)

        let alarmsResponse = await res.json();
        let alarms = alarmsResponse['alarms'];
        let isUnreadAlarmExist = false;

        for (let alarmId in alarms) {
            if (alarms.hasOwnProperty(alarmId)) {
                let alarm = alarms[alarmId];
                $('#alarm-list').append(formAlarm(alarm));
                $('#alarm-default-msg').hide();

                // 여기서 알림 객체마다 이벤트 리스너를 추가
                $('#read-alarm-btn-' + alarmId).click(function () {
                    readAlarm(alarmId);
                });
            }

            for (const alarm of alarms) {
                if (!alarm['isRead']) {
                    isUnreadAlarmExist = true;
                    break;
                }
            }

            if (isUnreadAlarmExist) {
                // 하나라도 읽지 않은 알림이 있을 때, 형광 초록색으로 변환
                $('#alarm-badge').css('background-color', 'limegreen');
            } else {
                // 모든 알림이 읽힌 경우, 현재 색상으로 유지
                // 이때, 현재 색상은 CSS에 지정된 색상일 것입니다.
                $('#alarm-badge').css('background-color', '');
            }
        }
    })
}

function deleteAlarm(aId) {

    // 삭제 여부 확인
    const confirmation = confirm("알림을 삭제하시겠습니까?");

    if (confirmation) {

        // 사용자가 확인을 선택한 경우에만 알람 삭제
        fetch('/api/alarms/' + aId, {
            method: 'DELETE',
            headers: {
                'Authorization': Cookies.get('Authorization'),
                'Refresh-Token': Cookies.get('Refresh-Token')
            }
        })
        .then(async res => {
            checkTokenExpired(res);
            refreshToken(res);

            alert('알림이 삭제되었습니다.');

            $('#alarm-list').empty();
            callMyAlarms();
        })
        .catch(error => {
            console.error('알림 삭제 실패:', error);
        });
    }
}

function readAlarm(aId) {

    fetch('/api/alarms/' + aId, {
        method: 'PUT',
        headers: {
            'Authorization': Cookies.get('Authorization'),
            'Refresh-Token': Cookies.get('Refresh-Token')
        }
    })
    .then(async res => {
        checkTokenExpired(res)
        refreshToken(res)

        console.log('알림 읽음');
        alert('알림 읽음');

        $('#alarm-list').empty();
        callMyAlarms();
    })
    .catch(error => {
        console.error('알림 읽기 실패 :', error);
    });
}

function formAlarm(alarm) {
    let aId = alarm['id']
    let title = alarm['title'];
    let content = alarm['content']
    let isRead = alarm['isRead'];

    let readClass = isRead ? 'read' : '';

    return `
       <li id="alarm-${aId}" class="${readClass}">
          <div>
            <div id="alarm-title">${title}</div>
            <div id="alarm-content">${content}</div>
          </div>
          <button id="read-alarm-btn-${aId}" class="alarm-panel-btn" onclick="readAlarm(${aId})">Read</button>
          <button id="delete-alarm-btn-${aId}" class="alarm-panel-btn" onclick="deleteAlarm(${aId})">Delete</button>
       </li>
        `
}