checkIfLoginToPass()

const STATUS_CLASS = {
    borrowed:  'status-borrowed',
    available: 'status-available',
    offline:   'status-offline'
};

const statusNameMap = {
    borrowed: '已借走',
    available: '空闲中',
    offline: '已下架'
};

const localStorageUserData = JSON.parse(localStorage.getItem("user"));
const overlayContent = document.getElementById("overlay-content")
const gird = document.getElementById("grid");


function searchEquipment(){
    let selectedValue = document.getElementById("searchType");
    let searchInput = document.getElementById("searchInput");
    searchInput.value = searchInput.value.trim()
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
                addLabelSearchBottom(responseText['returnData']['nowPage'],responseText['returnData']['allPage'],"pager-home", responseText['returnData']['searchName'],responseText['returnData']['searchLabel'],"equipment")
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

/* ========= 图片相关 ========= */
function previewImg(input) {
    const file = input.files[0];
    if (!file) return;

    // 10 MB = 10 * 1024 * 1024 B
    if (file.size > 10 * 1024 * 1024) {
        alert('图片大小不能超过 10 MB');
        input.value = '';          // 清空已选文件
        return;
    }

    const reader = new FileReader();
    reader.onload = e => {
        const img = input.parentElement.querySelector('img');
        img.src = e.target.result;
        img.style.display = 'block';
        input.parentElement.querySelector('.close').style.display = 'block';
        input.parentElement.querySelector('.placeholder').style.display = 'none';
    };
    reader.readAsDataURL(file);
}

function removeImg(btn) {
    const box = btn.parentElement;
    box.querySelector('img').style.display = 'none';
    box.querySelector('.close').style.display = 'none';
    box.querySelector('.placeholder').style.display = 'block';
    box.querySelector('input[type=file]').value = '';
}

/* ========= Tag 相关 ========= */
/* 生成标签 */
function addTag() {
    const input   = document.getElementById('tagInput');
    const display = document.getElementById('tagDisplay');
    const text    = input.value.trim();
    if (!text) return;

    // 新增：限制标签长度 ≤ 5
    if (text.length > 5) {
        showCustomMessage('单个标签最多 5 个字');
        return;
    }

    if (display.children.length >= 3) {
        showCustomMessage('最多只能添加 3 个标签');
        return;
    }

    const tag = document.createElement('span');
    tag.className = 'tag-item';
    tag.innerHTML = `<span class="collect-tag-text">${text}</span><span class="remove" onclick="this.parentElement.remove()">×</span>`;
    display.appendChild(tag);

    input.value = '';
}

function collectSubmitTags(){
    return Array.from(document.getElementById("tagDisplay").querySelectorAll(`.collect-tag-text`))
        .map(node => node.textContent.trim())
        .join(',');
}
/* 收集标签（提交时调用） */
function collectTags() {
    return Array.from(document.getElementById("edit-tagDisplay").querySelectorAll(`.collect-tag-text`))
        .map(node => node.textContent.trim())
        .join(',');
}

function limitDesc(el) {
    const max = 100;
    if (el.value.length > max) el.value = el.value.slice(0, max);
    el.nextElementSibling.textContent = el.value.length + '/' + max;
}

// 把 <input type=file> 选择的图片转成 base64（不含 data:image/xxx;base64, 前缀）
function fileToBase64(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload  = () => resolve(reader.result.split(',')[1]); // 去掉前缀
        reader.onerror = err => reject(err);
    });
}
/* ========= 提交 ========= */
async function submitNewEquipmentData() {
    let tags = collectSubmitTags();
    console.log(tags);
    let equipmentName = document.getElementById('new-equipment-name').value.trim();
    let equipmentCode = document.getElementById('new-equipment-code').value.trim();
    let equipmentDesc = document.getElementById('new-equipment-desc').value.trim();
    let equipmentImg;
    // 2. 图片 → Base64
    const imgFile = document.querySelector('.img-box input[type=file]').files[0];
    if (imgFile) {
        try {
            equipmentImg = await fileToBase64(imgFile);
        } catch (e) {
            showCustomMessage("图片读取失败")
            return;
        }
    } else {
        equipmentImg = '';
    }
    if (equipmentName === ""){
        showCustomMessage("设备名称不能为空!")
    }else if (equipmentName.length > 20){
        showCustomMessage("设备名称不能超过20字!")
    }else if (equipmentCode === ""){
        showCustomMessage("设备编号不能为空!")
    }else if (equipmentCode.length > 20){
        showCustomMessage("设备编号不能超过20字!")
    }else if (equipmentImg === ""){
        showCustomMessage("设备图片不能为空!")
    }else if(equipmentDesc === ""){
        showCustomMessage("设备简介不能为空!")
    }else if (tags.length === 0){
        showCustomMessage("设备标签至少选一个!")
    }else{
        let newEquipmentSubmitButton = document.getElementById("new-equipment-submit-button");
        newEquipmentSubmitButton.disabled = true;
        // 1. 收集文本字段
        let data = {
            "equipmentName": equipmentName,
            "equipmentCode": equipmentCode,
            "equipmentDesc": equipmentDesc,
            "equipmentTag": tags,
            "equipmentImg": equipmentImg,
            "needCount": articlePageSize,
            "nowPage": 0
        };

        console.log(data)

        postData(local_href + local_equipment_tag + "/submitNewEquipmentData", JSON.stringify(data))
            .then((responseText) => {
                if (responseText['code'] === "yes") {
                    newEquipmentSubmitButton.disabled = false;
                    gird.innerHTML = "";
                    addEquipmentPageData(responseText)
                    addBottom(responseText['returnData']['nowPage'], responseText['returnData']['allPage'], "equipmentPageData","pager-home")
                    showCustomMessage(responseText['reason'],"yes")
                    closeEquipmentDialog()
                } else {
                    showCustomMessage(responseText['reason'])
                }
            })
            .catch((error) => {
                showCustomMessage("数据请求失败");
            });
    }

}
/* 关闭整个弹窗（含遮罩） */
function closeEquipmentDialog() {
    document.getElementById("equipment-mask").remove();
}
function addNewEquipment(){
    let mask = document.createElement("div");
    mask.className = "equipment-mask";
    mask.id = "equipment-mask";

    let dlg = document.createElement("div");
    dlg.className = "equipment-dialog";
    dlg.innerHTML = `
        <h2>新建设备</h2>
            <label>设备名称</label>
            <input name="name" id="new-equipment-name" required maxlength="50"/>

            <label>设备编号</label>
            <input name="code" id="new-equipment-code" required maxlength="50"/>

            <label>设备图片（1张）</label>
            <div class="img-box">
                <img id="new-equipment-img"/>
                <input type="file" accept="image/*" onchange="previewImg(this)"/>
                <span class="placeholder">点击上传图片</span>
                <span class="close" onclick="removeImg(this)">×</span>
            </div>

            <label>设备介绍（≤100字）</label>
            <textarea name="desc" id="new-equipment-desc" maxlength="100" oninput="limitDesc(this)"></textarea>
            <div class="counter">0/100</div>

            <label>设备标签（最多3个）</label>
            <div class="tag-control">
                <div class="tag-input-line">
                    <input id="tagInput" placeholder="请输入标签"/>
                    <button type="button" class="btn-add" onclick="addTag()">添加</button>
                </div>
            
                <!-- 生成的标签会插到这里 -->
                <div class="tag-display" id="tagDisplay"></div>
            </div>
            <div class="btn-group">
                <button type="submit" id="new-equipment-submit-button" class="btn btn-confirm" onclick="submitNewEquipmentData()">提交</button>
                <button type="button" class="btn btn-cancel" onclick="closeEquipmentDialog()">取消</button>
            </div>
    `;

    mask.appendChild(dlg);
    document.body.appendChild(mask);

}

/* 统一标签添加：type='new'|'edit' */
function addTagChange(type, preText = '') {
    const input   = document.getElementById(type === 'edit' ? 'edit-tagInput' : 'tagInput');
    const display = document.getElementById(type === 'edit' ? 'edit-tagDisplay' : 'tagDisplay');
    const text    = preText || input.value.trim();
    if (!text) return;

    // 新增：限制标签长度 ≤ 5
    if (text.length > 5) {
        showCustomMessage('单个标签最多 5 个字');
        return;
    }

    if (display.children.length >= 3) {
        showCustomMessage('最多只能添加 3 个标签');
        return;
    }
    const tag = document.createElement('span');
    tag.className = 'tag-item';
    tag.innerHTML = `<span class="collect-tag-text">${text}</span><span class="remove" onclick="this.parentElement.remove()">×</span>`;
    display.appendChild(tag);
    input.value = '';
}

function openEditEquipmentDialog(eq) {
    console.log(eq)
    console.log(eq['equipmentId'])
    // 如果之前存在就移除
    document.getElementById('equipment-mask')?.remove();

    const mask = document.createElement('div');
    mask.className = 'equipment-mask';
    mask.id = 'equipment-mask';


    const dlg = document.createElement('div');
    dlg.className = 'equipment-dialog edit-dialog';   // 额外 class 方便微调
    dlg.innerHTML = `
        <h2>修改设备</h2>

        <input type="hidden" id="edit-equipment-id" value="${eq.equipmentId}"/>

        <label>设备名称</label>
        <input id="edit-equipment-name" maxlength="50" value="${eq.equipmentName}"/>

        <label>设备编号</label>
        <input id="edit-equipment-code" maxlength="50" value="${eq.equipmentCode}"/>

        <label>设备图片（1张）</label>
        <div class="img-box">
            <img id="edit-equipment-img" src="${eq.equipmentImg}" style="display:block"/>
            <input type="file" accept="image/*" onchange="previewImg(this)"/>
            <span class="placeholder" style="display:none">点击上传图片</span>
            <span class="close" style="display:block" onclick="removeImg(this)">×</span>
        </div>

        <label>设备介绍（≤100字）</label>
        <textarea id="edit-equipment-desc" maxlength="100" oninput="limitDesc(this)">${eq.equipmentDesc}</textarea>
        <div class="counter">${eq['equipmentDesc'].length}/100</div>

        <label>设备标签（最多3个）</label>
        <div class="tag-control">
            <div class="tag-input-line">
                <input id="edit-tagInput" placeholder="请输入标签"/>
                <button type="button" class="btn-add" onclick="addTagChange('edit')">添加</button>
            </div>
            <div class="tag-display" id="edit-tagDisplay"></div>
        </div>


        <div class="btn-group">
            <button type="button" class="btn btn-cancel" onclick="closeEquipmentDialog()">取消</button>
            <button type="button" class="btn btn-confirm" onclick="submitEditEquipmentData()">保存修改</button>
        </div>
    `;

    mask.appendChild(dlg);
    document.body.appendChild(mask);

    // 预填标签
    console.log(eq.equipmentTag)
    const tagArr = eq.equipmentTag.split(',').filter(t => t);
    tagArr.forEach(t => addTagChange('edit', t));
}


/* 收集字段并提交 */
async function submitEditEquipmentData() {
    const id        = document.getElementById('edit-equipment-id').value;
    const name      = document.getElementById('edit-equipment-name').value.trim();
    const code      = document.getElementById('edit-equipment-code').value.trim();
    const desc      = document.getElementById('edit-equipment-desc').value.trim();
    const tags      = collectTags('edit');
    let imgBase64   = '';

    const fileInput = document.querySelector('.edit-dialog input[type=file]').files[0];
    if (fileInput) {
        try {
            imgBase64 = await fileToBase64(fileInput);
        } catch (e) {
            showCustomMessage("图片读取失败")
            return;
        }
    }else {
        // 未重新上传，保留原图
        imgBase64 = document.getElementById('edit-equipment-img').src;
    }

    if (name === ""){
        showCustomMessage("设备名称不能为空!")
    }else if (name.length > 20){
        showCustomMessage("设备名称不能超过20字!")
    }else if (code === ""){
        showCustomMessage("设备编号不能为空!")
    }else if (code.length > 20){
        showCustomMessage("设备编号不能超过20字!")
    }else if (imgBase64 === ""){
        showCustomMessage("设备图片不能为空!")
    }else if(desc === ""){
        showCustomMessage("设备简介不能为空!")
    }else if (tags.length === 0){
        showCustomMessage("设备标签至少选一个!")
    }else{
        const payload = {
            equipmentId: id,
            equipmentName: name,
            equipmentCode: code,
            equipmentDesc: desc,
            equipmentImg: imgBase64,
            equipmentTag: tags
        };

        postData(local_href + local_equipment_tag + "/submitEditEquipmentData", JSON.stringify(payload))
            .then(res => {
                if (res.code === 'yes') {
                    changeHaveEquipment(id,name,code,tags,res['returnData']['newImgSrc']);
                    closeOverlay()
                    closeEquipmentDialog();
                    showCustomMessage(res.reason, 'yes');
                } else {
                    showCustomMessage(res.reason);
                }
            })
            .catch(() => showCustomMessage("submitEditEquipmentData方法请求失败"));
    }


}

function changeHaveEquipment(equipmentId,equipmentName,equipmentCode,equipmentTag,equipmentImg){
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
}

function outEquipment(equipmentId){
    let data = {
        "equipmentId": equipmentId,
        "userAccount": localStorageUserData['userAccount'],
    }
    console.log(data)
    postData(local_href + local_equipment_tag + "/outEquipment", JSON.stringify(data))
        .then((responseText) => {
            if (responseText['code'] === "yes") {
                closeOverlay()
                let checkBox = document.querySelector(`.item[data-id="${equipmentId}"]`);
                console.log(checkBox)
                if (checkBox){
                    let checkBox2 = checkBox.querySelector(".equipment-on");
                    console.log(checkBox2)
                    if (checkBox2){
                        checkBox2.classList.remove("equipment-on")
                        checkBox2.classList.add("equipment-out")
                        checkBox2.textContent = "下架中";
                    }
                }
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

function onEquipment(equipmentId){
    let data = {
        "equipmentId": equipmentId,
        "userAccount": localStorageUserData['userAccount'],
    }
    console.log(data)
    postData(local_href + local_equipment_tag + "/onEquipment", JSON.stringify(data))
        .then((responseText) => {
            if (responseText['code'] === "yes") {
                closeOverlay()
                let checkBox = document.querySelector(`.item[data-id="${equipmentId}"]`);
                console.log(checkBox)
                if (checkBox){
                    let checkBox2 = checkBox.querySelector(".equipment-out");
                    console.log(checkBox2)
                    if (checkBox2){
                        checkBox2.classList.remove("equipment-out")
                        checkBox2.classList.add("equipment-on")

                        checkBox2.textContent = "已上架";
                    }
                }

                showCustomMessage(responseText['reason'],"yes")
            } else {
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            showCustomMessage("数据请求失败");
        });
}

/* 关闭遮罩 */
function closeOverlay() {
    const overlay = document.getElementById('overlay');
    overlay.classList.remove('show');
    overlay.addEventListener('transitionend', () => {
        overlayContent.innerHTML = ""
        overlay.style.display = 'none';
    }, {once: true});
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
async function deleteEquipment(equipmentId){
    const ok = await confirmDelete();
    console.log(ok)
    if(ok){
        // 真正删除逻辑，调用你自己的方法
        doDelete(equipmentId);   // ← 替换为你的删除接口
    }
}
/* 示例真正删除方法 */
function doDelete(equipmentId){
    let data = {
        "equipmentId": equipmentId,
        "userAccount": localStorageUserData['userAccount'],
        "needCount": articlePageSize,
        "nowPage": 0
    }

    postData(local_href + local_equipment_tag + "/doDeleteEquipment", JSON.stringify(data))
        .then((responseText) => {
            if (responseText['code'] === "yes") {
                gird.innerHTML = "";
                addEquipmentPageData(responseText)
                addBottom(responseText['returnData']['nowPage'], responseText['returnData']['allPage'], "equipmentPageData","pager-home")
                showCustomMessage(responseText['reason'],"yes")
                closeOverlay()
            } else {
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            showCustomMessage("数据请求失败");
        });
}

// 添加详情页面
function addDetailEquipmentBox(responseText, needAppendDiv) {
    let clsName = statusNameMap[responseText['returnData']['equipmentStatus']] || '已下架';
     let dataDiv1= `
<div class="detail-item">
    <div class="detail-item-image-box">
      <img src="${responseText['returnData']['equipmentImg']}" alt="${responseText['returnData']['equipmentName']}" class="detail-item-image">
    </div>
 
    <footer>
        <h2 class="detail-item-name">${responseText['returnData']['equipmentName']}</h2>
        <p class="detail-item-tag">设备Id: ${responseText['returnData']['equipmentId']}</p>
        <p class="detail-item-tag">设备标签: ${responseText['returnData']['equipmentTag']}</p>
        <p class="detail-item-code">设备编号: ${responseText['returnData']['equipmentCode']}</p>
        <p class="detail-item-desc">设备介绍: ${responseText['returnData']['equipmentDesc']}</p>
        <p class="detail-item-tag">设备状态: ${clsName}</p>
        <p class="detail-item-tag">设备标签: ${responseText['returnData']['equipmentTag']}</p>
        <p class="detail-item-tag">创建时间: ${responseText['returnData']['equipmentCreateTime']}</p>
        <p class="detail-item-tag">上架状态: ${responseText['returnData']['equipmentOutOrOnStatus']}</p>
        <div class="home-detail-borrow-box">
            <button class="home-borrow-button" id="home-borrow-button" onclick='openEditEquipmentDialog(${JSON.stringify(responseText.returnData).replace(/"/g, "&quot;")})'>修改数据</button>
            <button class="home-borrow-button" id="home-forget-borrow-button" onclick="closeOverlay()">取消查看</button>
            `;
         let dataDiv2;
             if(responseText['returnData']['equipmentOutOrOnStatus'] === "out"){
                 dataDiv2 = `
                    <button class="home-nervous-need" id="home-on-button" onclick="onEquipment('${responseText['returnData']['equipmentId']}')">上架设备</button>
                `;
             }else{
                 dataDiv2 = `
                    <button class="home-forget-borrow-button" id="home-out-button" onclick="outEquipment('${responseText['returnData']['equipmentId']}')">下架设备</button>
                `;
             }
         let dataDiv3 = `
            <button class="home-forget-borrow-button" id="home-forget-borrow-button" onclick="deleteEquipment('${responseText['returnData']['equipmentId']}')">删除设备</button>
        </div>       
    </footer>
            `;
    needAppendDiv.innerHTML = dataDiv1 + dataDiv2 +dataDiv3
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
                const overlay = document.getElementById('overlay');
                const content = document.getElementById('overlay-content');
                // 先 display:flex，再加 show 类触发动画
                overlay.style.display = 'flex';
                overlay.offsetHeight;          // 强制 reflow
                overlay.classList.add('show');
                addDetailEquipmentBox(responseText, content);
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
            let divDataBox = document.createElement("div");
            let divData = `
                        <div class="item" data-id = "${responseText['returnData']['deviceList'][i]['equipmentId']}">
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
                                     <div class="equipment-data-label-box">
                            `
                    let divData2
                    if((responseText['returnData']['deviceList'][i]['equipmentOutOrOnStatus']) === "out"){
                        divData2 = `<span class="equipment-data-label">上架状态:</span><span class="equipment-data-tags equipment-out">下架中</span>`
                    }
                    else{
                        divData2 = `<span class="equipment-data-label">上架状态:</span><span class="equipment-data-tags equipment-on">已上架</span>`
                    }
                    let divData3 = `
                            </div>
                            </footer>
                        </div>
                    `
            divDataBox.innerHTML = divData+divData2+divData3
            gird.appendChild(divDataBox);
        }
    }
}

function getAllEquipment() {
    let data = {
        "nowPage": 0,
        "needCount": articlePageSize,
        "checkRootAccount":localStorageUserData['userAccount']
    };
    postData(local_href + local_equipment_tag + "/getAllEquipment", JSON.stringify(data))
        .then((responseText) => {
            if (responseText['code'] === "yes") {
                addEquipmentPageData(responseText)
                addBottom(responseText['returnData']['nowPage'], responseText['returnData']['allPage'], "equipmentPageData","pager-home")
            }else if (responseText['code'] === "kill"){
                killHaveLoginAdmin(responseText['reason'])
            }else {
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            console.log(error)
            showCustomMessage("数据请求失败");
        });
}