function addHeader(needActivity) {
    mainMenuHeaderBox.innerHTML = `<div class="nav-toggle" id="nav-toggle-id" onclick="openMenu()">
    <span></span>
    <span></span>
    <span></span>
</div>
<div class="nav-toggle-overlay" id="the-overlay" onclick="closeMenu()"></div>
<ul class="nav-menu">
    <li class="menu_li menu_need_link menu_li_open ${needActivity === 'home' ? 'active' : ''}"><a href="/home" class="menu_a">设备管理</a></li>
    <li class="menu_li menu_need_link menu_li_open ${needActivity === 'check' ? 'active' : ''}"><a href="/check" class="menu_a">审核管理</a></li>
    <li class="menu_li menu_need_link menu_li_open ${needActivity === 'user' ? 'active' : ''}"><a href="/user" class="menu_a">用户管理</a></li>
    <li class="menu_li menu_need_link menu_li_open ${needActivity === 'order' ? 'active' : ''}"><a href="/order" class="menu_a">工单管理</a></li>
    <div class="welcome menu_li_open">欢迎：用户名</div>
    <li class="menu_li menu_li_open"><div class="menu_a" onclick="logOutUser()">登出</div></li>
</ul>`
}

function openMenu() {
    document.querySelector(".nav-menu").classList.toggle("open");
    document.getElementById("the-overlay").classList.toggle("visible"); // 显示/隐藏暗层
}

function closeMenu() {
    document.querySelector(".nav-menu").classList.remove("open");
    document.getElementById("the-overlay").classList.remove("visible"); // 隐藏暗层
}

function logOutUser() {
    // 清除 localStorage 中的 user 内容
    localStorage.removeItem('user');
    // 跳转到指定页面，例如跳转到登录页面
    window.location.href = local_login_log_out_page;
}