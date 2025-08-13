function addBottom(nowPage, allPage, tyData, adderId) {
    let bottomPage = document.getElementById(adderId);
    bottomPage.innerHTML = "";
    if (allPage > 0) {
        createPageDiv("首页", 0, bottomPage, tyData);
        if (nowPage === 1 || nowPage === 0) {
            for (let i = nowPage; i <= allPage && i <= 3; i++) {
                createPageDiv(i, i - 1, bottomPage, tyData);
            }
        } else if (nowPage === allPage) {
            if (allPage - 2 >= 1) {
                createPageDiv(allPage - 2, allPage - 3, bottomPage, tyData);
            }
            if (allPage - 1 >= 1) {
                createPageDiv(allPage - 1, allPage - 2, bottomPage, tyData);
            }
            if (allPage >= 1) {
                createPageDiv(allPage, allPage - 1, bottomPage, tyData);
            }

        } else {
            createPageDiv(nowPage - 1, nowPage - 2, bottomPage, tyData);
            createPageDiv(nowPage, nowPage - 1, bottomPage, tyData);
            createPageDiv(nowPage + 1, nowPage, bottomPage, tyData);
        }
        createPageDiv("尾页", allPage - 1, bottomPage, tyData);
    }
}

function createPageDiv(text, href, fatherDiv, type) {

    let pageDiv = document.createElement("div");
    pageDiv.classList = "bottom-div"
    pageDiv.addEventListener("click", (event) => {
        event.preventDefault();
        getPageData(href, type)
    });
    pageDiv.textContent = text
    fatherDiv.appendChild(pageDiv);
}

function getPageData(nowPage, type) {
    if (type === "equipmentPageData") {
        let data = {
            "nowPage": nowPage,
            "needCount": articlePageSize
        }
        console.log(data)
        postData(local_href + local_equipment_tag + "/getAllEquipment", JSON.stringify(data))
            .then((responseText) => {
                if (responseText['code'] === "yes") {
                    gird.innerHTML = "";
                    addEquipmentPageData(responseText)
                    addBottom(responseText['returnData']['nowPage'], responseText['returnData']['allPage'], responseText['returnData']['tyData'], "pager-home")
                } else {
                    showCustomMessage(responseText['reason'])
                }
            })
            .catch((error) => {
                showCustomMessage("数据请求失败");
            });
    } else if (type === "userPageData") {
        let data = {
            "userAccount": localStorageUserData['userAccount'],
            "nowPage": nowPage,
            "needCount": userPageSize,
        }
        postData(local_href + local_steward_tag + "/getAllUser", JSON.stringify(data))
            .then((responseText) => {
                console.log(responseText)
                if (responseText['code'] === "yes") {
                    grid.innerHTML = "";
                    addUserPageData(responseText)
                    addBottom(responseText['returnData']['nowPage'], responseText['returnData']['allPage'], responseText['returnData']['tyData'], "pager-home")
                } else {
                    showCustomMessage(responseText['reason'])
                }
            })
            .catch((error) => {
                console.log(error)
                showCustomMessage("数据请求失败");
            });
    } else if (type === "orderPageData") {
        let data = {
            "nowPage": nowPage,
            "needCount": workOrderPageSize,
        }
        postData(local_href + local_order_tag + "/getAllWorkOrder", JSON.stringify(data))
            .then((responseText) => {
                console.log(responseText)
                if (responseText['code'] === "yes") {
                    grid.innerHTML = "";
                    addBorrowAndOrderPageDataProfile(responseText)
                    addBottom(responseText['returnData']['nowPage'], responseText['returnData']['allPage'], "orderPageData", "pager-home")
                } else {
                    showCustomMessage(responseText['reason'])
                }
            })
            .catch((error) => {
                console.log(error)
                showCustomMessage("数据请求失败");
            });
    }
}


function addLabelSearchBottom(nowPage, allPage, adderId, searchName, searchLabel) {
    let bottomPage = document.getElementById(adderId);
    bottomPage.innerHTML = "";
    if (allPage > 0) {
        createLabelPageDiv("首页", 0, bottomPage, searchName, searchLabel);
        if (nowPage === 1 || nowPage === 0) {
            for (let i = nowPage; i <= allPage && i <= 3; i++) {
                createLabelPageDiv(i, i - 1, bottomPage, searchName, searchLabel);
            }
        } else if (nowPage === allPage) {
            if (allPage - 2 >= 1) {
                createLabelPageDiv(allPage - 2, allPage - 3, bottomPage, searchName, searchLabel);
            }
            if (allPage - 1 >= 1) {
                createLabelPageDiv(allPage - 1, allPage - 2, bottomPage, searchName, searchLabel);
            }
            if (allPage >= 1) {
                createLabelPageDiv(allPage, allPage - 1, bottomPage, searchName, searchLabel);
            }

        } else {
            createLabelPageDiv(nowPage - 1, nowPage - 2, bottomPage, searchName, searchLabel);
            createLabelPageDiv(nowPage, nowPage - 1, bottomPage, searchName, searchLabel);
            createLabelPageDiv(nowPage + 1, nowPage, bottomPage, searchName, searchLabel);
        }
        createLabelPageDiv("尾页", allPage - 1, bottomPage, searchName, searchLabel);
    }
}

function createLabelPageDiv(text, href, fatherDiv, searchName, searchLabel) {
    let pageDiv = document.createElement("div");
    pageDiv.classList = "bottom-div"
    pageDiv.addEventListener("click", (event) => {
        event.preventDefault();
        getAllLabelPageData(href, searchName, searchLabel)
    });
    pageDiv.textContent = text
    fatherDiv.appendChild(pageDiv);
}

function getAllLabelPageData(nowPage, searchName, searchLabel) {
    let data = {
        "nowPage": nowPage,
        "needCount": articlePageSize,
        "searchInput": searchName,
        "selectedValue": searchLabel
    }
    console.log(data)
    postData(local_href + local_equipment_tag + "/searchEquipment", JSON.stringify(data))
        .then((responseText) => {
            if (responseText['code'] === "yes") {
                gird.innerHTML = "";
                console.log(responseText)
                addEquipmentPageData(responseText)
                addLabelSearchBottom(responseText['returnData']['nowPage'], responseText['returnData']['allPage'], "pager-home", responseText['returnData']['searchName'], responseText['returnData']['searchLabel'])
            } else {
                showCustomMessage(responseText['reason'])
            }
        })
        .catch((error) => {
            showCustomMessage("数据请求失败");
        });
}
