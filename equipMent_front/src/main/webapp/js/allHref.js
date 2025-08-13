const basic_front_href = "http://localhost:7778"
const basic_backend_href = "http://localhost:7777"
const basic_manage_backend_href = "http://localhost:9999"

const local_href = basic_backend_href+"/equipment";
const local_login_success_page = basic_front_href+"/home";
const local_login_log_out_page = basic_front_href+ "/";
const local_myself_tag = "/auth";
const local_equipment_tag = "/equipment"
const local_profile_tag = "/profile";

const navEquipmentList = "/home"
const navMineCenter = "/profile"

const socket_backend = basic_backend_href+"/equipment/ws";
const socket_manage_backend = basic_manage_backend_href+"/equipmentmanagement/ws";

function checkIfLoginToPass() {
    if (localStorage.getItem("user") !== null) {
        let data = JSON.parse(localStorage.getItem("user"));
        if (data['userAccount'] === null || data['userName'] === null) {
            window.location.href = local_login_log_out_page
        }
    }else{
        window.location.href = local_login_log_out_page
    }
}

// 设备借还页面的分页长度
const articlePageSize = 10;
// 设备申请中的分页长度
const checkEquipmentPageSize = 8;
// 个人中心中待还设备的分页长度
const waitNeedReturnPageSize = 8;
// 个人中心中工单详情的分页长度
const workOrderPageSize = 8;
