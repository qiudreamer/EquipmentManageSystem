checkIfLoginToPass()

const localStorageUserData = JSON.parse(localStorage.getItem("user"));
const passwordDisplay = document.getElementById("password-display");
const passwordOverlay = document.getElementById("password-overlay");
const passwordModal = document.getElementById("password-modal");

const grid = document.getElementById("grid");

const newUserOverlay = document.getElementById("new-user-overlay");
const newUserName = document.getElementById("user-name");
const newUserAccount = document.getElementById("user-account");
const newUserPassword = document.getElementById("user-password");


// 点击覆盖层时关闭覆盖层
passwordOverlay.addEventListener("click", (event) => {
    if (event.target.id === "password-overlay") {
        passwordOverlay.style.display = "none";
    }
});

// 阻止事件冒泡到 order-container
passwordModal.addEventListener("click", (event) => {
    event.stopPropagation();
});

function closePasswordOverlay(event) {
    if (event.target.id === "close-btn") {
        passwordOverlay.style.display = "none";
    }
}

// 显示新建用户弹窗
function showNewUserModal() {
    newUserOverlay.style.display = "flex";
}

// 存储新用户信息
function confirmButton(){
    if (newUserName.value.trim() === ""){
        newUserName.value = ""
        showCustomMessage("用户名不能为空!")
        return;
    }
    if (newUserAccount.value.trim() === ""){
        newUserAccount.value = ""
        showCustomMessage("账号不能为空!")
        return;
    }
    if (newUserPassword.value.trim() === ""){
        newUserPassword.value = ""
        showCustomMessage("密码不能为空!")
        return;
    }
    let data= {
        "nowPage": 0,
        "needCount": userPageSize,
        "userName":newUserName.value,
        "userAccount":newUserAccount.value,
        "userPassword":newUserPassword.value,
        "rootUserAccount":localStorageUserData['userAccount']
    }
    postData(local_href + local_steward_tag + "/submitNewUserData", JSON.stringify(data))
        .then((responseText) => {
            if (responseText['code'] === "yes") {
                newUserName.value = ""
                newUserAccount.value = ""
                newUserPassword.value = ""

                grid.innerHTML = "";
                console.log(responseText)
                addUserPageData(responseText)
                addBottom(responseText['returnData']['nowPage'], responseText['returnData']['allPage'], "userPageData","pager-home")
                showCustomMessage(responseText['reason'],"yes")

                // 关闭弹窗
                newUserOverlay.style.display = "none";
            } else {
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            showCustomMessage("数据请求失败");
            console.log(error)
        });

}

// 取消新建用户
function cancelButtonSubmit(){{
    newUserOverlay.style.display = "none";
}}

function generateAccountAndPasswordSubmit(){
    if (newUserName.value.trim() === ""){
        showCustomMessage("用户名不能为空!")
        newUserName.value = ""
        return;
    }
    let data ={
        "userName": newUserName.value.toLowerCase().replace(/\s+/g, "")
    }
    console.log(data)
    postData(local_href + local_steward_tag + "/getPinYin", JSON.stringify(data))
        .then((responseText) => {
            if (responseText['code'] === "yes") {
                newUserName.value = newUserName.value.toLowerCase().replace(/\s+/g, "")
                newUserAccount.value = responseText['returnData']['userAccount']
                newUserPassword.value = responseText['returnData']['userPassword']
                console.log(responseText)
            } else {
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            showCustomMessage("数据请求失败");
            console.log(error)
        });
}


// 点击覆盖层时关闭弹窗
newUserOverlay.addEventListener("click", (event) => {
    if (event.target.id === "new-user-overlay") {
        newUserOverlay.style.display = "none";
    }
});



function searchUser(){

    let selectedValue = document.getElementById("searchType");
    let searchInput = document.getElementById("searchInput");

    searchInput.value = searchInput.value.replace(/\s+/g, "")

    let data = {
        "nowPage": 0,
        "needCount": userPageSize,
        "searchInput":searchInput.value,
        "selectedValue":selectedValue.value,
        "checkUserAccount": localStorageUserData['userAccount']
    }
    postData(local_href + local_steward_tag + "/searchUser", JSON.stringify(data))
        .then((responseText) => {
            if (responseText['code'] === "yes") {
                grid.innerHTML = "";
                console.log(responseText)
                addUserPageData(responseText)
                if (responseText['returnData']['ifHaveSearch'] === "yes"){
                    console.log("equipment_have_search_yes")
                    addLabelSearchBottom(responseText['returnData']['nowPage'],responseText['returnData']['allPage'],"pager-home", responseText['returnData']['searchName'],responseText['returnData']['searchLabel'], "user")
                }else{
                    console.log("equipment_have_search_no")
                    addBottom(responseText['returnData']['nowPage'],responseText['returnData']['allPage'],responseText['returnData']['tyData'],"pager-home")
                }
            } else {
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            showCustomMessage("数据请求失败");
            console.log(error)
        });

}


function addUserPageData(responseText){
    grid.innerHTML = `
            <div class="user-row header">
            <span class="col">用户账号</span>
            <span class="col">用户名称</span>
            <span class="col time-title">登录时间</span>
            <span class="col action">操作</span>
        </div>
    `
    if (responseText['returnData']['userList'].length === 0){
        let needAddNoneDiv = document.createElement("div");
        needAddNoneDiv.classList = "need-add-none-box";
        needAddNoneDiv.textContent = "当前网站还未有用户账号"
        grid.appendChild(needAddNoneDiv)
    }else{
        for (let i=0;i<responseText['returnData']['userList'].length;i++){
            let needDiv = document.createElement("div");
            needDiv.classList = "user-row";
            needDiv.setAttribute("data-id",responseText['returnData']['userList'][i]['userAccount']);
            let divBox1 = `
                <span class="col account">${responseText['returnData']['userList'][i]['userAccount']}</span>
                <span class="col name">${responseText['returnData']['userList'][i]['userName']}</span>
                <span class="col time">${responseText['returnData']['userList'][i]['loginTime'] === 'none' ? '未登录过' : responseText['returnData']['userList'][i]['loginTime'] }</span>
                <span class="col action">
                <button class="btn btn-sm btn-pwd"  title="查看密码" onclick="viewPwd('${responseText['returnData']['userList'][i]['userAccount']}')">密码</button>
                <button class="btn btn-sm btn-del"  title="删除用户"  onclick="deleteUser('${responseText['returnData']['userList'][i]['userAccount']}')">删除</button>
        `;
            let divBox2= ``;
            if (responseText['returnData']['rootType'] === "yes"){
                if (responseText['returnData']['userList'][i]['rootType'] === "yes"){
                    needDiv.classList = "user-row admin-out";
                    divBox2 = `
                <button class="btn btn-sm btn-admin btn-admin-out" title="撤销管理员" onclick="revokeAdmin('${responseText['returnData']['userList'][i]['userAccount']}')">撤销管理员</button>
            `
                }else{
                    divBox2 = `
                <button class="btn btn-sm btn-admin" title="添加管理员"  onclick="setAdmin('${responseText['returnData']['userList'][i]['userAccount']}')">设为管理员</button>
            `
                }

            }

            let divBox3 = `
                </span>
            
        `;
            needDiv.innerHTML = divBox1+divBox2+divBox3;
            grid.appendChild(needDiv)
        }
    }


}
function getAllUser(){
    let data={
        "checkRootAccount": localStorageUserData['userAccount'],
        "nowPage":0,
        "needCount":userPageSize
    }
    postData(local_href + local_steward_tag + "/getAllUser", JSON.stringify(data))
        .then((responseText) => {
            console.log(responseText)
            if (responseText['code'] === "yes") {
                grid.innerHTML = "";
                addUserPageData(responseText)
                addBottom(responseText['returnData']['nowPage'], responseText['returnData']['allPage'], "userPageData","pager-home")
            } else if (responseText['code'] === "kill"){
                killHaveLoginAdmin(responseText['reason'])
            }else{
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            console.log(error)
            showCustomMessage("数据请求失败");
        });
}

function removeAdminStyle(userAccount){
    console.log(userAccount)
    let rowUserAccount = document.querySelector(`.user-row[data-id="${userAccount}"]`);
    if (rowUserAccount){
        rowUserAccount.classList.remove("admin-out");
        let rowUserButton = rowUserAccount.querySelector('.btn-admin');
        if (rowUserButton){
            rowUserButton.classList.remove("btn-admin-out");
            rowUserButton.textContent = "设为管理员";
            rowUserButton.onclick = function () {
                setAdmin(userAccount)
            };
        }
    }
}

function addAdminStyle(userAccount){
    let rowUserAccount = document.querySelector(`.user-row[data-id="${userAccount}"]`);
    if (rowUserAccount){
        rowUserAccount.classList.add("admin-out");
        let rowUserButton = rowUserAccount.querySelector('.btn-admin');
        if (rowUserButton){
            rowUserButton.classList.add("btn-admin-out");
            rowUserButton.textContent = "撤销管理员";
            rowUserButton.onclick = function () {
                revokeAdmin(userAccount)
            };
        }
    }
}

function setAdmin(acc){
    /* 设为管理员 */
    let data = {
        "userAccount":localStorageUserData['userAccount'],
        "setAdminAccount":acc,
    }
    postData(local_href + local_steward_tag + "/setAdmin", JSON.stringify(data))
        .then((responseText) => {
            if (responseText['code'] === "yes") {
                addAdminStyle(responseText['returnData']['userAccount']);
                showCustomMessage(responseText['reason'],"yes")
            } else {
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            console.log(error)
            showCustomMessage("数据请求失败");
        });
}


function revokeAdmin(acc){
    let data = {
        "userAccount":localStorageUserData['userAccount'],
        "setAdminAccount":acc,
    }
    postData(local_href + local_steward_tag + "/revokeAdmin", JSON.stringify(data))
        .then((responseText) => {
            if (responseText['code'] === "yes") {
                console.log(responseText)
                removeAdminStyle(responseText['returnData']['userAccount']);
                showCustomMessage(responseText['reason'],"yes")
            } else {
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            console.log(error)
            showCustomMessage("数据请求失败");
        });

}

function viewPwd(acc){
    let data = {
        "userAccount":localStorageUserData['userAccount'],
        "getPasswordAccount":acc,
    }
    postData(local_href + local_steward_tag + "/viewPwd", JSON.stringify(data))
        .then((responseText) => {
            if (responseText['code'] === "yes") {
                passwordDisplay.textContent = responseText['returnData']['password'];
                passwordOverlay.style.display = "flex";
                console.log(responseText)
            } else {
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            console.log(error)
            showCustomMessage("数据请求失败");
        });
    /* 弹出密码 */
}


function delUser(acc){

    /* 打开确认弹窗 */
    let data = {
        "userAccount":localStorageUserData['userAccount'],
        "getPasswordAccount":acc,
    }
    console.log(data)
    postData(local_href + local_steward_tag + "/delUser", JSON.stringify(data))
        .then((responseText) => {
            if (responseText['code'] === "yes") {
                console.log(responseText)
                let needDeleteUserRow = document.querySelector(`.user-row[data-id="${acc}"]`)
                if (needDeleteUserRow){
                    needDeleteUserRow.remove()
                }
                showCustomMessage(responseText['reason'],"yes");
            } else {
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            console.log(error)
            showCustomMessage("数据请求失败");
        });
}


async function deleteUser(userAccount){
    const ok = await confirmDelete();
    console.log(ok)
    if(ok){
        // 真正删除逻辑，调用你自己的方法
        delUser(userAccount);   // ← 替换为你的删除接口
    }
}

/* 打开弹窗并返回 Promise（true=确认，false=取消）*/
function confirmDelete(){
    return new Promise(resolve=>{
        const overlay=document.getElementById('dlgOverlay');
        overlay.classList.add('show');

        const clean=()=>overlay.classList.remove('show');

        /* 关闭并 resolve(false) */
        const close=()=>{clean(); resolve(false);};

        /* 点击确定 */
        document.getElementById('dlgConfirm').onclick=()=>{
            clean(); resolve(true);
        };

        /* 点击取消 */
        document.getElementById('dlgCancel').onclick=close;

        /* 点击遮罩 */
        overlay.onclick=e=>{
            if(e.target===overlay) close();
        };
    });
}
