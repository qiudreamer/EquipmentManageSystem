checkIfLoginToPass()

window.addEventListener('DOMContentLoaded', () => {
    document.getElementById("nav-equipment-list").href = navEquipmentList;
    document.getElementById("nav-mine-center").href = navMineCenter;

});
const STATUS_CLASS = {
    borrowed:  'status-borrowed',
    available: 'status-available',
    offline:   'status-offline'
};

const statusMap = {
    borrowed: 'status-borrowed',
    available: 'status-available',
    offline: 'status-offline'
};
const statusNameMap = {
    borrowed: '已借走',
    available: '空闲中',
    offline: '已下架'
};
const overlay = document.getElementById('overlay');
const localStorageUserData = JSON.parse(localStorage.getItem("user"));
const overlayContent = document.getElementById("overlay-content")
const gird = document.getElementById("grid");

const getEquipmentRequest =document.getElementById("get-equipment-request");
const getEquipmentRequestUserName =document.getElementById("get-equipment-request-user-name");
const getEquipmentRequestDeviceName =document.getElementById("get-equipment-request-device-name");
const getEquipmentRequestDeviceCode =document.getElementById("get-equipment-request-device-code");
const getEquipmentRequestReason =document.getElementById("get-equipment-request-reason");
const getEquipmentRequestTextCounter = document.getElementById("get-equipment-request-text-counter");
const getEquipmentRequestOk =document.getElementById("get-equipment-request-ok");

const nervousName = document.getElementById('nervous-name')
const nervousDevice = document.getElementById('nervous-device')
const nervousId = document.getElementById('nervous-id')
const nervousTime = document.getElementById('nervous-time')




// 2. 显示浮窗
const modal = document.getElementById('nervousModal');
// 3. 点击“确定”关闭
const okBtn = document.getElementById('nervous-ok');

const socket = new SockJS(socket_backend);
const stomp = Stomp.over(socket);

stomp.connect({}, (frame) => {
    console.log("Connected: " + frame);

    stomp.subscribe('/topic/status', (msg) => {
        console.log("Received message: " + msg.body);
        const { deviceId, status } = JSON.parse(msg.body);
        const dot = document.querySelector(`[data-id="${deviceId}"] .status-dot`);
        const dotName = document.querySelector(`[data-id="${deviceId}"] .status-dot-name`);
        const overlayContentBox = overlayContent.querySelector(`.detail-item[data-id="${deviceId}"]`)
        if (dot) {
            dot.className = `status-dot ${STATUS_CLASS[status] || 'status-offline'}`;
        }
        if (dotName){
            dotName.textContent = "已借走"
        }
        if (overlayContentBox){
            closeOverlay()
        }

    });


}, (error) => {
    console.error("STOMP error: " + error);
});


const socket_manage = new SockJS(socket_manage_backend);
const stomp_manage = Stomp.over(socket_manage);
stomp_manage.connect({}, (frame) => {
    console.log("Connected: " + frame);

    stomp_manage.subscribe('/topic/ourStatus', (msg) => {
        console.log("Received message: " + msg.body);
        const { equipmentId, onOrOutStatus } = JSON.parse(msg.body);
        const equipment = document.querySelector(`.item[data-id="${equipmentId}"]`);
        if (equipment && onOrOutStatus === "out"){
            const dot = equipment.querySelector('.status-dot.status-available') || equipment.querySelector('.status-dot.status-borrowed');
            if (dot) {
                dot.classList.remove('status-on');
                dot.classList.add('status-offline');
            }
            const dotName = equipment.querySelector('.status-dot-name');
            if (dotName) {
                dotName.textContent = "已下架"
            }
            equipment.classList.add('masking');
        }else if (equipment && onOrOutStatus === "on"){
            equipment.classList.remove('masking');
            const dot = equipment.querySelector('.status-dot.status-offline')
            if (dot) {
                dot.classList.remove('status-offline');
                dot.classList.add('status-on');
            }
            const dotName = equipment.querySelector('.status-dot-name');
            if (dotName) {
                dotName.textContent = "已上架"
            }
        }
    });


    stomp_manage.subscribe('/topic/deleteStatus', (msg) => {
        console.log("Received message: " + msg.body);
        const { equipmentId } = JSON.parse(msg.body);
        const equipment = document.querySelector(`.item-div-box[data-id="${equipmentId}"]`);
        const equipmentDetailBox = document.querySelector(`.detail-item[data-id="${equipmentId}"]`)
        if (equipment){
            console.log(equipment)
            equipment.style.background = "aliceblue"
            equipment.innerHTML = `
                <div class="equipment-delete-box">设备已经被删除了</div>
            `
        }
        console.log(equipmentDetailBox)
        if (equipmentDetailBox){
            closeOverlay();
        }
    });

    stomp_manage.subscribe('/topic/changeStatus', (msg) => {
        console.log("Received message: " + msg.body);
        const { equipmentId,equipmentName,equipmentCode,equipmentTag,equipmentImg } = JSON.parse(msg.body);

        let needChangeItem = document.querySelector(`.item[data-id="${equipmentId}"]`)
        if (needChangeItem){
            let changeItemNameBox = needChangeItem.querySelector(`.equipment-data-name`);
            if (changeItemNameBox){
                changeItemNameBox.textContent = equipmentName;
            }
            let changeItemCodeBox = needChangeItem.querySelector(`.equipment-data-code`);
            if (changeItemCodeBox){
                changeItemCodeBox.textContent = equipmentCode;
            }

            let equipmentTagBox = needChangeItem.querySelector(`.equipment-data-tags`);
            if (equipmentTagBox){
                equipmentTagBox.innerHTML = `
                ${equipmentTag.split(',').map(tag => `<span class="equipment-data-tag">${tag}</span>`).join('')}
            `
            }
            let equipmentImgBox = needChangeItem.querySelector('.equipment-data-img');
            if (equipmentImgBox){
                equipmentImgBox.src = equipmentImg;
            }
        }
    });

    stomp_manage.subscribe('/topic/userDelete', (msg) => {
        console.log("Received message: " + msg.body);
        const { userAccount } = JSON.parse(msg.body);
        if (localStorageUserData['userAccount']  === userAccount){
            // 清除 localStorage 中的 user 内容
            localStorage.removeItem('user');
            // 跳转到指定页面，例如跳转到登录页面
            window.location.href = local_login_log_out_page;
        }

    });
    stomp_manage.subscribe('/topic/borrowStatus', (msg) => {
        console.log("Received message: " + msg.body);
        const { deviceId, status } = JSON.parse(msg.body);
        const dot = document.querySelector(`[data-id="${deviceId}"] .status-dot`);
        const dotName = document.querySelector(`[data-id="${deviceId}"] .status-dot-name`);
        const overlayContentBox = overlayContent.querySelector(`.detail-item[data-id="${deviceId}"]`)
        if (dot) {
            dot.className = `status-dot ${STATUS_CLASS[status] || 'status-offline'}`;
        }
        if (dotName){
            dotName.textContent = "已借走"
        }
        if (overlayContentBox){
            closeOverlay()
        }
    });
}, (error) => {
    console.error("STOMP error: " + error);
});

getAllEquipment();

// 借走设备
function borrowEquipmentRequest(equipmentId){

    let data ={
        "userAccount": localStorageUserData['userAccount'],
        "equipmentId": equipmentId,
        "borrowReason": getEquipmentRequestReason.value
    }
    postData(local_href + local_equipment_tag + "/borrowEquipmentRequest", JSON.stringify(data))
        .then((responseText) => {
            if (responseText['code'] === "yes") {
                getEquipmentRequestOk.disabled = false;
                showCustomMessage("正在申请中!","yes")
                closeOverlay();
                getEquipmentRequest.classList.remove('show');
                getEquipmentRequestReason.value = ""
            } else {
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            console.log(error)
            showCustomMessage("数据请求失败");
        });
}

function getEquipmentBoxRemove(event) {
    if (event.target.id === 'get-equipment-request' || event.target.id === 'get-equipment-request-cancel') {
        event.stopPropagation();
        getEquipmentRequest.classList.remove('show');
    }
}
function showSubmitEquipmentRequestBox(requestEquipmentDeviceName,requestEquipmentCode,requestEquipmentId){
    // 1. 把后端字段填进去（字段名按实际接口调整）
    getEquipmentRequestUserName.textContent = localStorageUserData['userName'];
    getEquipmentRequestDeviceName.textContent = requestEquipmentDeviceName || '--';
    getEquipmentRequestDeviceCode.textContent = requestEquipmentCode || '--';
    getEquipmentRequest.classList.add('show');

    getEquipmentRequestOk.addEventListener('click',function () {
        if (getEquipmentRequestReason.value.trim() !== ""){
            getEquipmentRequestOk.disabled = true;
            borrowEquipmentRequest(requestEquipmentId)
        }else{
            showCustomMessage("请输入借出原因!")
        }

    })
}
function limitReason(el) {
    const max = 100;
    let dialogReasonCount = document.getElementById("dialog-reason-count");
    if (el.value.length > max) el.value = el.value.slice(0, max);
    dialogReasonCount.textContent = el.value.length + '/' + max;
}
function limitDesc(el) {
    const max = 100;
    if (el.value.length > max) el.value = el.value.slice(0, max);
    getEquipmentRequestTextCounter.textContent = el.value.length + '/' + max;
}

function searchEquipment(){
    let selectedValue = document.getElementById("searchType");
    let searchInput = document.getElementById("searchInput");
    let data = {
        "nowPage": 0,
        "needCount": articlePageSize,
        "searchInput":searchInput.value,
        "selectedValue":selectedValue.value
    }

    console.log(data)

    postData(local_href + local_equipment_tag + "/searchEquipment", JSON.stringify(data))
        .then((responseText) => {
            if (responseText['code'] === "yes") {
                gird.innerHTML = "";
                console.log(responseText)
                addEquipmentPageData(responseText)
                addLabelSearchBottom(responseText['returnData']['nowPage'],responseText['returnData']['allPage'],"pager-home", responseText['returnData']['searchName'],responseText['returnData']['searchLabel'])
                showCustomMessage(responseText['reason'],"yes")
            } else {
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            showCustomMessage("数据请求失败");
        });
}

function openOverlay(item) {
    getDetailEquipmentData(item.dataset.id)
}

/* 关闭遮罩 */
function closeOverlay() {
    overlay.classList.remove('show');
    overlay.addEventListener('transitionend', () => {
        overlayContent.innerHTML = ""
        overlay.style.display = 'none';
    }, {once: true});
}

// 上报设备
function EquipmentMalfunction(equipmentId) {
    let confirmDialog = document.getElementById('confirmDialog');
    // 显示对话框
    confirmDialog.style.display = 'block';
    document.getElementById("home-dialog-userAccount").textContent = localStorageUserData['userAccount'];
    document.getElementById("home-dialog-equipmentId").textContent = equipmentId;

    let homeBorrowButton = document.getElementById("home-borrow-button");
    if (homeBorrowButton){
        homeBorrowButton.disabled = true;
    }

    // 显示对话框
    confirmDialog.classList.add('visible');
}
// 清空表单输入内容的函数
function clearFormInputs() {
    // 清空复选框
    let checkboxes = document.querySelectorAll('input[name="reason"]');
    checkboxes.forEach((checkbox) => {
        checkbox.checked = false;
    });

    // 清空详细描述框
    let detailedReasonInput = document.getElementById("home-dialog-reason-input");
    if (detailedReasonInput) {
        detailedReasonInput.value = '';
    }

//   恢复确定按钮的状态:
    document.getElementById('confirmButton').disabled = true

    let dialogReasonCount = document.getElementById("dialog-reason-count");
    dialogReasonCount.textContent = "0/100"
}
function confirmButtonToDialog(){
    let confirmDialog = document.getElementById('confirmDialog');

    let DialogUserAccount = document.getElementById("home-dialog-userAccount");
    let DialogEquipmentId = document.getElementById("home-dialog-equipmentId");
    console.log('设备已标记为不见');
    // 获取所有被选中的复选框
    let selectedReasons = [];
    let checkboxes = document.querySelectorAll('input[name="reason"]:checked');
    checkboxes.forEach((checkbox) => {
        selectedReasons.push(checkbox.value);
    });

    // 获取可选的详细描述
    let detailedReason = document.getElementById("home-dialog-reason-input").value;

    console.log('设备已标记为不见');
    let data = {
        "userAccount": DialogUserAccount.textContent, // 注意：这里应该是 textContent，而不是 value
        "equipmentId": DialogEquipmentId.textContent, // 注意：这里应该是 textContent，而不是 value
        "reasons": selectedReasons, // 添加被选中的复选框值
        "detailedReason": detailedReason // 添加详细描述
    };
    postData(local_href + local_equipment_tag + "/setDialogAboutEquipment", JSON.stringify(data))
        .then((responseText) => {
            if (responseText['code'] === "yes") {
                showCustomMessage(responseText['reason'],"yes")
            } else {
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            showCustomMessage("数据请求失败");
        });

    confirmDialog.classList.remove('visible');

    let homeBorrowButton = document.getElementById("home-borrow-button");
    if (homeBorrowButton){
        homeBorrowButton.disabled = false;
    }
    // 清空复选框和详细描述框
    clearFormInputs();
}
function cancelButtonToDialog(){
    let confirmDialog = document.getElementById('confirmDialog');
    confirmDialog.classList.remove('visible');
    let homeBorrowButton = document.getElementById("home-borrow-button");
    if (homeBorrowButton){
        homeBorrowButton.disabled = false;
    }

    // 清空复选框和详细描述框
    clearFormInputs();
}
// 检查是否有复选框被选中
function checkSelection() {

    let checkboxes = document.querySelectorAll('input[name="reason"]');
    let confirmButton = document.getElementById('confirmButton');
    let isChecked = false;
    checkboxes.forEach((checkbox) => {
        if (checkbox.checked) {
            isChecked = true;
        }
    });
    confirmButton.disabled = !isChecked;
}

// 紧急查看的情况
function homeNervousNeed(equipmentId){
    let data ={
        "equipmentId": equipmentId,
    }
    postData(local_href + local_equipment_tag + "/homeNervousNeed", JSON.stringify(data))
        .then((responseText) => {
            if (responseText['code'] === "yes") {
                homeNervousNeedBox(responseText)
            } else {
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            showCustomMessage("数据请求失败");
        });
}
function homeNervousNeedBox(responseData){
    // 1. 把后端字段填进去（字段名按实际接口调整）
    nervousName.textContent   = responseData['returnData']['userName'] || '--';
    nervousDevice.textContent = responseData['returnData']['deviceName'] || '--';
    nervousId.textContent     = responseData['returnData']['deviceCode'] || '--';
    nervousTime.textContent   = responseData['returnData']['borrowTime'] || '--';

    modal.classList.add('show');
    const close = () => modal.classList.remove('show');
    okBtn.onclick = close;
    modal.onclick = (e) => { if (e.target === modal) close(); };

}
// 添加详情页面
function addDetailEquipmentBox(responseText, needAppendDiv) {
    let cls;
    let clsName;
    if (responseText['returnData']['equipmentOutOrOnStatus'] === "out"){
        cls = "status-offline"
        clsName = "已下架"
    }else {
         cls = statusMap[responseText['returnData']['equipmentStatus']] || 'status-offline';
         clsName = statusNameMap[responseText['returnData']['equipmentStatus']] || '已下架';
    }
    let divData1 = `
        <div class="detail-item" data-id="${responseText['returnData']['equipmentId']}">
            <div class="detail-item-image-box">
              <img src="${responseText['returnData']['equipmentImg']}" alt="${responseText['returnData']['equipmentName']}" class="detail-item-image">
            </div>
 
    <footer>
        <h2 class="detail-item-name">${responseText['returnData']['equipmentName']}</h2>
        <p class="detail-item-tag">设备标签: ${responseText['returnData']['equipmentTag']}</p>
        <p class="detail-item-code">设备编号: ${responseText['returnData']['equipmentCode']}</p>
        <p class="detail-item-desc">设备介绍: ${responseText['returnData']['equipmentDesc']}</p>
        <div class="detail-home-footer-box">
            <span class="detail-status-dot ${cls}"></span>
            <span class="detail-status-dot-name">${clsName}</span>
        </div>`;
            let divData2 = ``;
            if (responseText['returnData']['equipmentOutOrOnStatus'] === "on"){
                if (responseText['returnData']['equipmentStatus'] === "available"){
                    divData2 = `
                   <div class="home-detail-borrow-box">
                        <button class="home-borrow-button" id="home-borrow-button" onclick="showSubmitEquipmentRequestBox('${responseText['returnData']['equipmentName']}','${responseText['returnData']['equipmentCode']}','${responseText['returnData']['equipmentId']}')">借走设备</button>
                         <button class="home-forget-borrow-button" id="home-forget-borrow-button" onclick="EquipmentMalfunction('${responseText['returnData']['equipmentId']}')">设备异常</button>
                    </div>       
                `
                }else if (responseText['returnData']['equipmentStatus'] === "borrowed") {
                    divData2 = `
                <div class="home-detail-borrow-box">
                    <button class="home-nervous-need" id="home-nervous-need"
                            onClick="homeNervousNeed('${responseText['returnData']['equipmentId']}')">紧急查看</button>
                    <button class="home-forget-borrow-button" id="home-forget-borrow-button" onclick="EquipmentMalfunction('${responseText['returnData']['equipmentId']}')">设备异常</button>
                </div>
                        `
                }
            }else{
                divData2 = `
                <div class="home-detail-borrow-box">
                    <button class="home-out-button">设备暂时下架</button>
                </div>
                        `
            }

    let divData3 = `
                 <div id="forget-button-dialog-box">
                            <!-- 弹框 -->
                            <div id="confirmDialog" class="dialog">
                        <div class="dialog-box">
                          <p>账号: </p>
                          <p id="home-dialog-userAccount"></p>
                        </div>
                        <div class="dialog-box">
                          <p>设备Id: </p>
                          <p id="home-dialog-equipmentId"></p>
                        </div>
                    
                        <p>设备的问题</p>
                        <div class="dialog-reasons">
                          <label>
                            <input type="checkbox" name="reason" value="损坏" onchange="checkSelection()"> 设备损坏
                          </label>
                          <label>
                            <input type="checkbox" name="reason" value="丢失" onchange="checkSelection()"> 设备丢失
                          </label>
                        </div>
                        
                        <p>请勾选一个或多个原因。</p>
                        <p class="dialog-tip">可以在个人中心->我的工单中撤销哦</p>
                                    <!-- 添加文本框 -->
                            <div class="dialog-reason-input">
                                <div class="dialog-reason-box">
                                      <p>详细描述问题（可选）：</p>
                                      <p id="dialog-reason-count">0/100</p>
                                </div>

                                <textarea id="home-dialog-reason-input" placeholder="请输入问题的详细描述..." maxlength="100" oninput="limitReason(this)"></textarea>
                            </div>
                        <div class="dialog-buttons">
                          <button id="confirmButton" class="dialog-confirm-button" onclick="confirmButtonToDialog()" disabled>确定</button>
                          <button id="cancelButton" class="dialog-cancel-button" onclick="cancelButtonToDialog()">取消</button>
                        </div>
                      </div>
                </div>
    </footer>
</div>
                    `
    needAppendDiv.innerHTML = divData1 + divData2 + divData3

}

// 获取详情数据
function getDetailEquipmentData(id) {
    console.log(id)
    let data = {
        "equipmentId": id
    }

    postData(local_href + local_equipment_tag + "/getDetailEquipmentData", JSON.stringify(data))
        .then((responseText) => {
            if (responseText['code'] === "yes") {

                // 先 display:flex，再加 show 类触发动画
                overlay.style.display = 'flex';
                overlay.offsetHeight;          // 强制 reflow
                overlay.classList.add('show');
                addDetailEquipmentBox(responseText, overlayContent);
            } else {
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            showCustomMessage("数据请求失败");
        });
}

/* 事件委托：把点击绑定在父容器 gird 上 */
gird.addEventListener('click', e => {
    const item = e.target.closest('.item');
    if (item) openOverlay(item);
});

/* 点击遮罩本身或 × 关闭 */
document.getElementById('overlay').addEventListener('click', e => {
    if (e.target === e.currentTarget || e.target.id === 'overlay-close') {
        closeOverlay();
    }
});

function addEquipmentPageData(responseText) {
    if (responseText['returnData']['deviceList'].length > 0) {

        for (let i = 0; i < responseText['returnData']['deviceList'].length; i++) {
            let cls;
            let clsName;
            if (responseText['returnData']['deviceList'][i]['equipmentOutOrOnStatus'] === "out"){
                cls = "status-offline"
                clsName = "已下架"
            }else {
                cls = statusMap[responseText['returnData']['deviceList'][i]['equipmentStatus']] || 'status-offline';
                clsName = statusNameMap[responseText['returnData']['deviceList'][i]['equipmentStatus']] || '已下架';
            }
            let divDataBox = document.createElement("div");
            divDataBox.className = "item-div-box";
            divDataBox.setAttribute("data-id",responseText['returnData']['deviceList'][i]['equipmentId'])
            let isOut = responseText['returnData']['deviceList'][i]['equipmentOutOrOnStatus'] === "out";
            let divData1
            if (isOut){
                divData1 = `
                 <div class="item masking" data-id = "${responseText['returnData']['deviceList'][i]['equipmentId']}">
                `
            }else{
                divData1 = `
                 <div class="item" data-id = "${responseText['returnData']['deviceList'][i]['equipmentId']}">
                `
            }

            let divData2 = `
                            <img class="equipment-data-img" src="${responseText['returnData']['deviceList'][i]['equipmentImg']}" alt="${responseText['returnData']['deviceList'][i]['equipmentName']}">
                            <footer>
                            <div class="equipment-data-label-box">
                                    <span class="equipment-data-label">设备名称:</span><span class="equipment-data-name">${responseText['returnData']['deviceList'][i]['equipmentName']}</span> 
                            </div>
                            <div class="equipment-data-label-box">
                                    <span class="equipment-data-label">设备编号:</span><span class="equipment-data-code">${responseText['returnData']['deviceList'][i]['equipmentCode']}</span>
                            </div>
                            <div class="equipment-data-label-box">
                                    <span class="equipment-data-label">创建时间:</span><span class="equipment-data-create-time">${responseText['returnData']['deviceList'][i]['equipmentCreateTime']}</span>
                            </div>
                            <div class="equipment-data-label-box">
                                    <span class="equipment-data-label">设备标签:</span><span class="equipment-data-tags">${responseText['returnData']['deviceList'][i]['equipmentTag'].split(',').map(tag => `<span class="equipment-data-tag">${tag}</span>`).join('')}</span>
                            </div>
                            </footer>
                            <div class="equipment-data-label-box-status">
                                <div class="home-footer-box">
                                  <span class="status-dot ${cls}"></span>
                                  <span class="status-dot-name">${clsName}</span>
                                  </div>
                            </div>
                        </div>
                    `
            divDataBox.innerHTML = divData1+divData2
            gird.appendChild(divDataBox);
        }
    }
}

function getAllEquipment() {
    let data = {
        "nowPage": 0,
        "needCount": articlePageSize,
        "userAccount": localStorageUserData['userAccount']
    };
    postData(local_href + local_equipment_tag + "/getAllEquipment", JSON.stringify(data))
        .then((responseText) => {
            if (responseText['code'] === "yes") {
                console.log(responseText)
                addEquipmentPageData(responseText)
                addBottom(responseText['returnData']['nowPage'], responseText['returnData']['allPage'], "equipmentPageData","pager-home")
            }else if (responseText['code'] === "no_user"){
                // 清除 localStorage 中的 user 内容
                localStorage.removeItem('user');
                // 跳转到指定页面，例如跳转到登录页面
                window.location.href = local_login_log_out_page;
            }else {
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            console.log(error)
            showCustomMessage("数据请求失败");
        });
}