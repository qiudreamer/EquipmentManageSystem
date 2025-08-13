function initSocketData() {
    const socket_manage = new SockJS(socket_manage_backend);
    const stomp_manage = Stomp.over(socket_manage);
    stomp_manage.connect({}, (frame) => {
        console.log("Connected: " + frame);
        stomp_manage.subscribe('/topic/stewardStatus', (msg) => {
            console.log("Received message: " + msg.body);
            const {userAccount} = JSON.parse(msg.body);
            if (localStorageUserData['userAccount'] === userAccount) {
                // 清除 localStorage 中的 user 内容
                localStorage.removeItem('user');
                // 跳转到指定页面，例如跳转到登录页面
                window.location.href = local_login_log_out_page;
            }
        });

    }, (error) => {
        console.error("STOMP error: " + error);
    });


    const socket = new SockJS(socket_backend);
    const stomp = Stomp.over(socket);
    stomp.connect({}, (frame) => {
        console.log("Connected: " + frame);
        stomp.subscribe('/topic/stewardStatus', (msg) => {
            console.log("Received message: " + msg.body);
            const {userAccount} = JSON.parse(msg.body);
            if (localStorageUserData['userAccount'] === userAccount) {
                // 清除 localStorage 中的 user 内容
                localStorage.removeItem('user');
                // 跳转到指定页面，例如跳转到登录页面
                window.location.href = local_login_log_out_page;
            }
        });

    }, (error) => {
        console.error("STOMP error: " + error);
    });
}
