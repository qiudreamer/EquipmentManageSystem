const userAccount = document.getElementById("userAccount")
const userPassword = document.getElementById("userPassword")
checkIfLogin()
function loginUser(event){
    event.preventDefault();
    if (userAccount.value.trim() === "" || userPassword.value.trim() === "") {
        showCustomMessage("用户名和密码不能为空!");
    } else {
        let data = {
            "userAccount": userAccount.value,
            "userPassword": userPassword.value
        }
        console.log(local_href + local_myself_tag + "/login")
        postData(local_href + local_myself_tag + "/login", JSON.stringify(data))
            .then((responseText) => {
                if (responseText['code'] === "no") {
                    showCustomMessage(responseText['reason']);
                } else {
                    let userData = {
                        "userName": responseText['returnData']['userName'],
                        "userAccount": responseText['returnData']['userAccount'],
                        "loginTime": responseText['returnData']['loginTime'],
                    }
                    localStorage.setItem("user", JSON.stringify(userData));
                    showCustomMessage("登陆成功", "yes");
                    setTimeout(() => {
                        window.location.href = local_login_success_page
                    }, 1000);
                }
            })
            .catch((error) => {
                showCustomMessage("请求失败");
            });
    }
}


function checkIfLogin() {
    if (localStorage.getItem("user") !== null) {
        let data = JSON.parse(localStorage.getItem("user"));
        if (data['userAccount'] !== null && data['userName'] !== null) {
            changeLoginTime();
            window.location.href = local_login_success_page
        }
        return true;
    }
    return false;
}

function changeLoginTime() {
    if (localStorage.getItem("user") !== null) {
        let userData = JSON.parse(localStorage.getItem("user"))
        let date = new Date(userData['loginTime']);
        let year = date.getFullYear();
        let month = date.getMonth() + 1;
        let day = date.getDate();

// 获取今天的日期
        let today = new Date();
        let todayYear = today.getFullYear();
        let todayMonth = today.getMonth() + 1; // 同上，月份从0开始
        let todayDay = today.getDate();

// 比较是否是同一天
        if (year !== todayYear || month !== todayMonth || day !== todayDay) {
            let userAccount = userData['userAccount'];
            let data = {
                "userAccount": userAccount,
            }
            postData(local_href + local_myself_tag +"/changeLoginTime", JSON.stringify(data))
                .then((responseData) => {
                    let userData = JSON.parse(localStorage.getItem("user"));
                    // 检查userData是否为null，如果是，则可能是localStorage中没有存储任何数据
                    if (userData) {
                        // 修改loginTime属性，这里以当前时间为例
                        userData.loginTime = responseData['nowTime'];
                        // 将修改后的对象转换为JSON字符串
                        let jsonString = JSON.stringify(userData);
                        // 将JSON字符串存储回localStorage
                        localStorage.setItem("user", jsonString);
                    }
                })
                .catch((error) => {
                    console.log(error);
                });
        }

    }
}