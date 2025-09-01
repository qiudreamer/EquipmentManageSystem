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
    if (type === "equipmentPageData"){
        let data= {
            "checkRootAccount": localStorageUserData['userAccount'],
            "nowPage": nowPage,
            "needCount": articlePageSize
        }
        console.log(data)
        postData(local_href + local_equipment_tag + "/getAllEquipment", JSON.stringify(data))
            .then((responseText) => {
                if (responseText['code'] === "yes") {
                    gird.innerHTML = "";
                    addEquipmentPageData(responseText)
                    addBottom(responseText['returnData']['nowPage'],responseText['returnData']['allPage'],responseText['returnData']['tyData'],"pager-home")
                } else {
                    showCustomMessage(responseText['reason'])
                }
            })
            .catch((error) => {
                showCustomMessage("数据请求失败");
            });
    }else if (type === "borrowCheckPageData"){
        let data = {
            "checkRootAccount": localStorageUserData['userAccount'],
            "nowPage": nowPage,
            "needCount": checkEquipmentPageSize
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
    }else if (type === "userPageData"){
        let data ={
            "checkRootAccount": localStorageUserData['userAccount'],
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
    }else if (type === "orderPageData"){
        let data ={
            "checkRootAccount": localStorageUserData['userAccount'],
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






function addLabelSearchBottom(nowPage, allPage, adderId, searchName, searchLabel, searchType) {
    let bottomPage = document.getElementById(adderId);
    bottomPage.innerHTML = "";
    if (allPage > 0) {
        createLabelPageDiv("首页", 0, bottomPage, searchName, searchLabel, searchType);
        if (nowPage === 1 || nowPage === 0) {
            for (let i = nowPage; i <= allPage && i <= 3; i++) {
                createLabelPageDiv(i, i - 1, bottomPage, searchName, searchLabel, searchType);
            }
        } else if (nowPage === allPage) {
            if (allPage - 2 >= 1) {
                createLabelPageDiv(allPage - 2, allPage - 3, bottomPage, searchName, searchLabel, searchType);
            }
            if (allPage - 1 >= 1) {
                createLabelPageDiv(allPage - 1, allPage - 2, bottomPage, searchName, searchLabel, searchType);
            }
            if (allPage >= 1) {
                createLabelPageDiv(allPage, allPage - 1, bottomPage, searchName, searchLabel, searchType);
            }

        } else {
            createLabelPageDiv(nowPage - 1, nowPage - 2, bottomPage, searchName, searchLabel, searchType);
            createLabelPageDiv(nowPage, nowPage - 1, bottomPage, searchName, searchLabel, searchType);
            createLabelPageDiv(nowPage + 1, nowPage, bottomPage, searchName, searchLabel, searchType);
        }
        createLabelPageDiv("尾页", allPage - 1, bottomPage, searchName, searchLabel, searchType);
    }
}

function createLabelPageDiv(text, href, fatherDiv, searchName, searchLabel, searchType) {
    let pageDiv = document.createElement("div");
    pageDiv.classList = "bottom-div"
    pageDiv.addEventListener("click", (event) => {
        event.preventDefault();
        getAllLabelPageData(href, searchName, searchLabel, searchType)
    });
    pageDiv.textContent = text
    fatherDiv.appendChild(pageDiv);
}

function getAllLabelPageData(nowPage, searchName, searchLabel, searchType) {
    if (searchType === "equipment"){
        let data= {
            "checkRootAccount": localStorageUserData['userAccount'],
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
                    if (responseText['returnData']['ifHaveSearch'] === "yes"){
                        console.log("equipment_have_search_yes")
                        addLabelSearchBottom(responseText['returnData']['nowPage'],responseText['returnData']['allPage'],"pager-home", responseText['returnData']['searchName'],responseText['returnData']['searchLabel'], "equipment")
                    }else{
                        console.log("equipment_have_search_no")
                        addBottom(responseText['returnData']['nowPage'],responseText['returnData']['allPage'],responseText['returnData']['tyData'],"pager-home")
                    }
                } else {
                    showCustomMessage(responseText['reason'])
                }
            })
            .catch((error) => {
                console.log(error)
                showCustomMessage("数据请求失败");
            });
    }
    else if (searchType === "check"){
        let data= {
            "checkRootAccount": localStorageUserData['userAccount'],
            "nowPage": nowPage,
            "needCount": checkEquipmentPageSize,
            "searchInput": searchName,
            "selectedValue": searchLabel
        }
        console.log(data)
        postData(local_href + local_equipment_tag + "/searchCheck", JSON.stringify(data))
            .then((responseText) => {
                if (responseText['code'] === "yes") {
                    mineCheckEquipmentPager.innerHTML = "";
                    console.log(responseText)
                    addCheckEquipmentProfile(responseText)
                    if (responseText['returnData']['ifHaveSearch'] === "yes"){
                        console.log("check_have_search_yes")
                        addLabelSearchBottom(responseText['returnData']['nowPage'],responseText['returnData']['allPage'],"pager-home", responseText['returnData']['searchName'],responseText['returnData']['searchLabel'], "check")
                    }else{
                        console.log("check_have_search_no")
                        addBottom(responseText['returnData']['nowPage'],responseText['returnData']['allPage'],responseText['returnData']['tyData'],"pager-home")
                    }
                } else {
                    showCustomMessage(responseText['reason'])
                }
            })
            .catch((error) => {
                console.log(error)
                showCustomMessage("数据请求失败");
            });
    }
    else if (searchType === "user"){
        let data= {
            "nowPage": nowPage,
            "needCount": userPageSize,
            "searchInput":searchName,
            "selectedValue":searchLabel,
            "checkUserAccount": localStorageUserData['userAccount']
        }
        console.log(data)
        postData(local_href + local_steward_tag + "/searchUser", JSON.stringify(data))
            .then((responseText) => {
                console.log(responseText)
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
                console.log(error)
                showCustomMessage("数据请求失败");
            });
    }
    else if (searchType === "order"){

        console.log(searchLabel)
        let labelJson = JSON.parse(searchLabel);

        let data= {
            "nowPage": nowPage,
            "needCount": workOrderPageSize,
            "searchInput":searchName,
            "searchType":labelJson['searchType'],
            "searchOrderType": labelJson['searchOrderType']
        }

        console.log(data)

        postData(local_href + local_order_tag + "/searchOrder", JSON.stringify(data))
            .then((responseText) => {
                console.log(responseText)
                if (responseText['code'] === "yes") {
                    grid.innerHTML = "";
                    addBorrowAndOrderPageDataProfile(responseText)
                    if (responseText['returnData']['ifHaveSearch'] === "yes"){
                        console.log("equipment_have_search_yes")
                        addLabelSearchBottom(responseText['returnData']['nowPage'],responseText['returnData']['allPage'],"pager-home", responseText['returnData']['searchName'],responseText['returnData']['searchLabel'], "order")
                    }else{
                        console.log("equipment_have_search_no")
                        addBottom(responseText['returnData']['nowPage'],responseText['returnData']['allPage'],responseText['returnData']['tyData'],"pager-home")
                    }
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
