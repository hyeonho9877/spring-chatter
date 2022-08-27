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

    constructor(onlineStatus, email, name, unconfirmed, lastTime, lastMessage) {
        if (onlineStatus === Status.ONLINE) this._onlineStatus = Status.ONLINE
        else this._onlineStatus = Status.OFFLINE
        this._email = email;
        this._name = name;
        this._unconfirmed = unconfirmed;
        this._lastTime = lastTime;
        this._lastMessage = lastMessage;
    }

    get lastTime() {
        return this._lastTime;
    }

    get lastMessage() {
        return this._lastMessage;
    }

    get onlineStatus() {
        return this._onlineStatus
    }


    get email() {
        return this._email;
    }

    get name() {
        return this._name;
    }

    get unconfirmed() {
        return this._unconfirmed;
    }
}

$(function () {
    addDropDownListener()
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
                if (receiver !== body.sender) {
                    notifyMessageReceived(body.sender, body.message)
                    updateChatHistory('RECEIVE', body.sender, body.message, body.timestamp)
                } else {
                    confirm(body.sender, true)
                    updateChatHistory('RECEIVE', receiver, body.message, body.timestamp)
                }
                updateLastMessage(body.sender, body.message)
                updateLastTime(body.sender, body.timestamp.substring(11, 17))
                renderChat()
                scrollToBottom()
            }
        })
    })

    $.ajax({
        type: 'POST',
        url: '/v1/social/following',
        success: (friends, text, request) => {
            let friends_ul = getFriendsListArea();
            friends.forEach(friend => {
                friendsMap.set(friend.email, new Friend(friend.onlineStatus, friend.email, friend.name, friend.unconfirmed, friend.lastChatted, friend.lastMessage))
                friends_ul.innerHTML += addFriendElement(friendsMap.get(friend.email))
            })
            getMessages();
            addSearchListener()

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
    if (connected && validateMessage(message)) {
        let data = {'sender': sender, 'receiver': receiver, 'message': message}
        stomp.send(destination, {}, JSON.stringify(data))
        updateLastMessage(receiver, message)
        updateLastTime(receiver)
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

function addFriendElement(friend) {
    return "<li class='p-2 border-bottom chatter' id='" + friend.email + "'>" +
        "<a href='#!' class='d-flex justify-content-between'>" +
        "<div class='d-flex flex-row'>" +
        "<div>" +
        "<img src='https://mdbcdn.b-cdn.net/img/Photos/new-templates/bootstrap-chat/ava" + count + "-bg.webp' alt='avatar' class='d-flex align-self-center me-3' width='60'>" +
        (friend.onlineStatus === Status.ONLINE ? "<span class='badge bg-success badge-dot' id='status-" + friend.email + "'></span>" : "<span class='badge bg-danger badge-dot' id='status-" + friend.email + "'></span>") +
        "</div>" +
        "<div class='pt-1'>" +
        "<p class='fw-bold mb-0' >" + friend.name + "</p>" +
        "<p class='small text-muted pt-1 fs-6' id='last-message-" + friend.email + "'>" + friend.lastMessage + "</p>" +
        "</div>" +
        "</div>" +
        "<div class='pt-1'>" +
        "<p class='small text-muted mb-1' id='last-time-" + friend.email + "'>" + calDate(friend.lastTime) + "</p>" +
        "<span class='badge bg-danger rounded-pill float-end' id='notify-message-" + friend.email + "'>" + (friend.unconfirmed === 0 ? '' : friend.unconfirmed) + "</span>" +
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
            addChatClickListener()
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

function notifyMessageReceived(id) {
    let userNotification = getNotificationArea(id);
    if (userNotification !== undefined) {
        let innerHTML = userNotification.innerHTML;
        if (innerHTML === "") userNotification.innerHTML = 1;
        else userNotification.innerHTML = (parseInt(innerHTML) + 1).toString();
    }
}

function updateLastMessage(id, message) {
    let recentMessageArea = getRecentMessageArea(id);
    if (recentMessageArea !== undefined) {
        recentMessageArea.innerHTML = message
    }

}

function updateLastTime(id, timestamp) {
    let lastTime = getLastTimeArea(id);

    if (timestamp === undefined) {
        let today = new Date();
        timestamp = padding(today.getHours()) + ":" + padding(today.getMinutes())
    }
    lastTime.innerHTML = timestamp
}

function removeNotification(id) {
    let userNotification = getNotificationArea(id);
    if (userNotification !== undefined) userNotification.innerHTML = '';
}

function confirm(id, reverse) {
    let userNotification = getNotificationArea(id);
    if (userNotification !== undefined) {
        $.ajax({
            type: 'POST',
            url: '/v1/chat/confirm',
            data: {'user': id, 'reverse': reverse},
            success: (messages, text, request) => {
                console.log(messages)
            }
        })
    }
}

function getNotificationArea(id) {
    return document.getElementById('notify-message-' + id);
}

function getRecentMessageArea(id) {
    return document.getElementById('last-message-' + id);
}

function getLastTimeArea(id) {
    return document.getElementById('last-time-' + id);
}

function getFriendsListArea() {
    return document.getElementById('ul-friend')
}

function validateMessage(message) {
    return message.length !== 0
}

function calDate(dateString) {
    let today = new Date();
    if (today.getFullYear() > parseInt(dateString.substring(0, 5))) return dateString.substring(0, 10)
    else if (today.getMonth() === parseInt(dateString.substring(5, 7)) - 1 && today.getDate() === parseInt(dateString.substring(8, 10)))
        return dateString.substring(11, 17)
    else
        return dateString.substring(5, 10);
}

function addSearchListener() {
    let searchInput = document.getElementById('input-search');
    searchInput.addEventListener('keyup', logKey)
}

function addChatClickListener() {
    $('.chatter').on('click', function () {
        receiver = $(this).attr('id');
        confirm(receiver, false)
        removeNotification(receiver)
        renderChat()
        scrollToBottom()
    })
}

function logKey(e) {
    let keyword = e.target.value;
    let friendsListArea = getFriendsListArea();
    receiver = ''
    friendsListArea.innerHTML = ''
    for (let key of friendsMap.keys()) {
        let friend = friendsMap.get(key);
        if (friend.name.includes(keyword)) {
            console.log(friend)
            friendsListArea.innerHTML += addFriendElement(friend)
        }
    }
    addChatClickListener()
}

function addDropDownListener() {
    let notification = document.getElementById('navbarDropdownMenuLink');
    let notificationMenu = document.getElementById('dropdown-menu-notification');

    let avatar = document.getElementById('navbarDropdownMenuAvatar')
    let avatarMenu = document.getElementById('dropdown-menu-avatar')

    addDropDownEventByTarget(notification, notificationMenu, avatar, avatarMenu)
    addDropDownEventByTarget(avatar, avatarMenu, notification, notificationMenu)
}

function addDropDownEventByTarget(target, menu, rival, rivalMenu) {
    target.addEventListener('click', ev => {
        let expanded = target.getAttribute('aria-expanded');
        if (expanded === 'false') {
            if (rival.getAttribute('aria-expanded') === 'true') {
                rival.setAttribute('aria-expanded', 'false')
                rivalMenu.classList.remove('show')
            }
            target.setAttribute('aria-expanded', 'true')
            menu.setAttribute('data-mdb-popper', 'none')
            menu.classList.add('show')
        } else {
            menu.classList.remove('show')
            target.setAttribute('aria-expanded', 'false')
        }
    })
}