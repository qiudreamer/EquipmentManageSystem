checkIfLoginToPass()

const localStorageUserData = JSON.parse(localStorage.getItem("user"));
const mineCheckEquipmentPager = document.getElementById("grid")
const checkOverlayContent = document.getElementById("check-overlay-content")
const checkOverlay = document.getElementById("check-overlay")


function getAllCheckEquipment(){
    let data = {
        "nowPage": 0,
        "needCount": checkEquipmentPageSize,
        "checkRootAccount": localStorageUserData['userAccount']
    }
    postData(local_href + local_equipment_tag + "/getAllCheckEquipment", JSON.stringify(data))
        .then((responseText) => {
            console.log(responseText)
            if (responseText['code'] === "yes") {
                addCheckEquipmentProfile(responseText)
                addBottom(responseText['returnData']['nowPage'], responseText['returnData']['allPage'], responseText['returnData']['tyData'], "pager-home")
            }else if (responseText['code'] === "kill"){
                killHaveLoginAdmin(responseText['reason'])
            }else {
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            showCustomMessage("数据请求失败");
        });
}


function addCheckEquipmentProfile(responseText) {
    if (responseText['returnData']['checkEquipmentList'].length > 0) {
        mineCheckEquipmentPager.innerHTML = ""
        for (let i = 0; i < responseText['returnData']['checkEquipmentList'].length; i++) {
            let divDataBox = document.createElement("div");
            divDataBox.className = "check-div-box"
            divDataBox.innerHTML = `
                        <div class="item">
                            <img class="item-profile-equipment-img" src="${responseText['returnData']['checkEquipmentList'][i]['equipmentImg']}" alt="${responseText['returnData']['checkEquipmentList'][i]['equipmentName']}">
                            <footer>
                            <div class="equipment-data-label-box">
                                    <span class="equipment-data-label">设备名称:</span>
                                    <span class="item-profile-equipment-name">${responseText['returnData']['checkEquipmentList'][i]['equipmentName']}</span>
                            </div>
                            <div class="equipment-data-label-box">
                                    <span class="equipment-data-label">设备编号:</span>
                                    <span class="item-profile-equipment-code">${responseText['returnData']['checkEquipmentList'][i]['equipmentCode']}</span>
                            </div>
                            <div class="equipment-data-label-box">
                                    <span class="equipment-data-label">申请时间:</span>
                                    <span class="item-profile-equipment-tag">${responseText['returnData']['checkEquipmentList'][i]['equipmentCheckTime']}</span>
                            </div>
                            </footer>
                        </div>
                    `
            mineCheckEquipmentPager.appendChild(divDataBox);
            divDataBox.addEventListener('click',function (){
                getDetailCheckData(responseText['returnData']['checkEquipmentList'][i]);
            })
        }

    } else {
        mineCheckEquipmentPager.innerHTML = "当前还没有待审核的设备"
    }
}

function getDetailCheckData(responseText){
    // 先 display:flex，再加 show 类触发动画
    checkOverlay.style.display = 'flex';
    checkOverlay.offsetHeight;          // 强制 reflow
    checkOverlay.classList.add('show');
    addDetailCheckEquipmentBox(responseText, checkOverlayContent);
}


// 添加详情页面
function addDetailCheckEquipmentBox(responseText, needAppendDiv) {
    needAppendDiv.innerHTML= `
        <div class="detail-item">
            <div class="detail-item-image-box">
              <img src="${responseText['equipmentImg']}" alt="${responseText['equipmentName']}" class="detail-item-image">
            </div>
 
            <footer>
                <p class="detail-item-code">设备名称: ${responseText['equipmentName']}</p>
                <p class="detail-item-code">设备编号: ${responseText['equipmentCode']}</p>
                <p class="detail-item-desc">申请原因: ${responseText['equipmentReason']}</p>
                <p class="detail-item-desc">申请时间: ${responseText['equipmentCheckTime']}</p>
                <div class="home-detail-borrow-box">
                     <button class="home-agree-button" onclick="agreeEquipmentCheck('${responseText['checkId']}')">同意申请</button>
                     <button class="home-revoke-button" onclick="revokeEquipmentCheck('${responseText['checkId']}')">驳回申请</button>
                     <button class="home-cancel-button" onclick="cancelEquipmentCheckBox()">取消查看</button>
                </div>
            </footer>
        </div>
    `
}

/* 点击遮罩本身或 × 关闭 */
document.getElementById('check-overlay').addEventListener('click', e => {
    if (e.target === e.currentTarget) {
        cancelEquipmentCheckBox();
    }
});

function agreeEquipmentCheck(checkId){
    let data = {
        "nowPage": 0 ,
        "needCount": checkEquipmentPageSize,
        "checkId": checkId,
    }
    postData(local_href + local_equipment_tag + "/agreeEquipmentCheck", JSON.stringify(data))
        .then((responseText) => {
            if (responseText['code'] === "yes") {
                addCheckEquipmentProfile(responseText)
                addBottom(responseText['returnData']['nowPage'], responseText['returnData']['allPage'], responseText['returnData']['tyData'], "pager-home")
                showCustomMessage(responseText['reason'],"yes")
            } else {
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            showCustomMessage("数据请求失败");
        });
    cancelEquipmentCheckBox();
}

function revokeEquipmentCheck(checkId){
    let data = {
        "nowPage": 0 ,
        "needCount": checkEquipmentPageSize,
        "checkId": checkId,
    }
    postData(local_href + local_equipment_tag + "/revokeEquipmentCheck", JSON.stringify(data))
        .then((responseText) => {
            console.log(responseText)
            if (responseText['code'] === "yes") {
                addCheckEquipmentProfile(responseText)
                addBottom(responseText['returnData']['nowPage'], responseText['returnData']['allPage'], responseText['returnData']['tyData'], "pager-home")
                showCustomMessage(responseText['reason'],"yes")
            } else {
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            console.log(error)
            showCustomMessage("数据请求失败");
        });
    cancelEquipmentCheckBox();
}

function cancelEquipmentCheckBox() {
    checkOverlay.classList.remove('show');
    checkOverlay.addEventListener('transitionend', () => {
        checkOverlayContent.innerHTML = ""
        checkOverlay.style.display = 'none';
    }, {once: true});
}
