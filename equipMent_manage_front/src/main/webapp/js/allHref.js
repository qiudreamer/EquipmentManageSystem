// 本地部署
const local_manage_backend_href = "http://localhost:9999";
const local_manage_front_href = "http://localhost:9998";
const local_backend_href = "http://localhost:7777";

const local_href = local_manage_backend_href+ "/equipmentmanagement";
const local_login_success_page = local_manage_front_href+"/home";
const local_login_log_out_page = local_manage_front_href+"/";

const local_equipment_tag = "/equipment"
const local_steward_tag = "/steward";
const local_order_tag = "/profile";

const socket_manage_backend = local_manage_backend_href+"/equipmentmanagement/ws";
const socket_backend = local_backend_href+"/equipment/ws";

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


function killHaveLoginAdmin(reason){
    showCustomMessage(reason)
    // 清除 localStorage 中的 user 内容
    localStorage.removeItem('user');
    // 跳转到指定页面，例如跳转到登录页面
    window.location.href = local_login_log_out_page;
}

// 设备借还页面的分页长度
const articlePageSize = 10;
// 设备申请中的分页长度
const checkEquipmentPageSize = 8;
// 用户管理的分页长度
const userPageSize = 10
// 个人中心中工单详情的分页长度
const workOrderPageSize = 12;
