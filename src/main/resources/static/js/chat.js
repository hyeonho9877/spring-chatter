let connected = false
let receiver = ''
let message = ''
let sender = ''
let socket = new SockJS('/ws');
let stomp = Stomp.over(socket);
let count = 1;
const destination = '/pub/hello'
let messageListArea
let messageList = new Map();
let friendsMap = new Map()
const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']

const Status = {
    ONLINE: "ONLINE",
    OFFLINE: "OFFLINE"
}

class Friend {

    constructor(onlineStatus) {
        if (onlineStatus === Status.ONLINE) this.onlineStatus = Status.ONLINE
        else this.onlineStatus = Status.OFFLINE
    }

    get getStatus() {
        return this.onlineStatus
    }

    set setStatus(status) {
        this.onlineStatus = status
    }
}

$(function () {
    messageListArea = document.getElementById('list-messages')
    stomp.connect({}, frame => {
        connected = true
        sender = frame.headers['user-name']
        stomp.subscribe('/queue/' + frame.headers['user-name'], response => {
            let body = JSON.parse(response.body);
            if (body.type === Status.ONLINE || body.type === Status.OFFLINE) {
                let user = body.user;
                let userStatus = document.getElementById('status-' + user);
                let classes = userStatus.classList;
                if (body.type === Status.ONLINE) {
                    if (classes.contains('bg-danger')) {
                        classes.remove('bg-danger')
                        classes.add('bg-success')
                    }
                } else {
                    if (classes.contains('bg-success')) {
                        classes.remove('bg-success')
                        classes.add('bg-danger')
                    }
                }
            } else {
                updateChatHistory('RECEIVE', receiver, body.message, body.timestamp)
                renderChat()
                scrollToBottom()
            }
        })
    })

    $.ajax({
        type: 'POST',
        url: '/v1/social/following',
        success: (friends, text, request) => {
            let friends_ul = document.getElementById('ul-friend');
            friends.forEach(friend => {
                friendsMap.set(friend.email, new Friend(friend.onlineStatus))
                friends_ul.innerHTML += addFriendElement(friend.name, friend.email, count++, friendsMap.get(friend.email).getStatus)
            })
            getMessages();
        },
        error: (err, text, request) => {
            console.log(err);
        }
    })
    let target = document.getElementById('input-message');

    target.addEventListener('keyup', e => {
        let key = e.code
        if (key === 'Enter') {
            if (connected && receiver !== '') {
                send(target.value)
                target.value = ''
            }
        }
    })
})

function send(message) {
    if (connected) {
        let data = {'sender': sender, 'receiver': receiver, 'message': message}
        stomp.send(destination, {}, JSON.stringify(data))
        updateChatHistory('SEND', receiver, message)
        renderChat()
        scrollToBottom()
    }
}

function addReceivedMessage(message, date) {
    let dateString = date.getHours() + ':' + date.getMinutes() + ' | ' + months[date.getMonth()] + ' ' + date.getDate();
    return "<div class='d-flex flex-row justify-content-start'>" +
        "<img src='https://mdbcdn.b-cdn.net/img/Photos/new-templates/bootstrap-chat/ava6-bg.webp' alt='avatar 1' style='width: 45px; height: 100%;'>" +
        "<div>" +
        "<p class='small p-2 ms-3 mb-1 rounded-3 fs-4' style='background-color: #f5f6f7;'>" + message + "</p>" +
        "<p class='small ms-3 mb-3 rounded-3 text-muted float-end'>" + dateString + "</p>" +
        "</div>" +
        "</div>"
}

function addSentMessage(message, date) {
    if (date === undefined) date = new Date();
    let dateString = padding(date.getHours()) + ':' + padding(date.getMinutes()) + ' | ' + months[date.getMonth()] + ' ' + padding(date.getDate());
    return "<div class='d-flex flex-row justify-content-end'>" +
        "<div>" +
        "<p class='small p-2 me-3 mb-1 text-white rounded-3 bg-primary fs-4'>" + message + "</p>" +
        "<p class='small me-3 mb-3 rounded-3 text-muted'>" + dateString + "</p>" +
        "</div>" +
        "<img src='https://mdbcdn.b-cdn.net/img/Photos/new-templates/bootstrap-chat/ava1-bg.webp' alt='avatar 1' style='width: 45px; height: 100%;'>" +
        "</div>"
}

function addFriendElement(name, id, count, status) {
    return "<li class='p-2 border-bottom chatter' id='" + id + "'>" +
        "<a href='#!' class='d-flex justify-content-between'>" +
        "<div class='d-flex flex-row'>" +
        "<div>" +
        "<img src='https://mdbcdn.b-cdn.net/img/Photos/new-templates/bootstrap-chat/ava" + count + "-bg.webp' alt='avatar' class='d-flex align-self-center me-3' width='60'>" +
        (status === Status.ONLINE ? "<span class='badge bg-success badge-dot' id='status-" + id + "'></span>" : "<span class='badge bg-danger badge-dot' id='status-" + id + "'></span>") +
        "</div>" +
        "<div class='pt-1'>" +
        "<p class='fw-bold mb-0' >" + name + "</p>" +
        "<p class='small text-muted'>Hello, Are you there?</p>" +
        "</div>" +
        "</div>" +
        "<div class='pt-1'>" +
        "<p class='small text-muted mb-1'>Just now</p>" +
        "<span class='badge bg-danger rounded-pill float-end'>3</span>" +
        "</div>" +
        "</a>" +
        "</li>"
}

function parseDate(dateString) {
    if (dateString === undefined) return undefined
    let dateAndTime = dateString.split(' ');
    let date = dateAndTime[0].split('-');
    let time = dateAndTime[1].split(':');
    return new Date(date[0], date[1] - 1, date[2], time[0], time[1]);
}

function padding(value) {
    if (value < 10) return '0' + value;
    else return value;
}

function getMessages() {
    $.ajax({
        type: 'POST',
        url: '/v1/chat/get',
        success: (messages, text, request) => {
            for (let f of friendsMap.keys()) {
                let userMessage = messages[f];
                if (userMessage !== undefined)
                    userMessage.forEach(m => updateChatHistory(m['type'], f, m['message'], m['timestamp']))
            }
            $('.chatter').on('click', function () {
                receiver = $(this).attr('id');
                renderChat()
                scrollToBottom()
            })
        }
    })
}

function scrollToBottom() {
    messageListArea.scrollTo(0, messageListArea.scrollHeight)
}

function renderChat() {
    let history = messageList.get(receiver);
    if (history === undefined) messageListArea.innerHTML = ''
    else messageListArea.innerHTML = history
}

function updateChatHistory(type, receiver, message, timestamp) {
    if (type === 'SEND') {
        if (messageList.has(receiver)) messageList.set(receiver, messageList.get(receiver) + addSentMessage(message, parseDate(timestamp)))
        else messageList.set(receiver, addSentMessage(message, parseDate(timestamp)))
    } else {
        if (messageList.has(receiver)) messageList.set(receiver, messageList.get(receiver) + addReceivedMessage(message, parseDate(timestamp)))
        else messageList.set(receiver, addReceivedMessage(message, parseDate(timestamp)))
    }
}