// 提示函数(message是信息内容,imageType是提醒类型)
function showCustomMessage(message, imageType) {
    // 创建提示容器
    let sample = document.getElementById("container");
    if (!sample) {
        var container = document.createElement("div");
        container.id = "container";
        container.style.position = "fixed";
        container.style.top = "10%"; // 初始位置在屏幕上方
        container.style.left = "50%";
        container.style.transform = "translate(-50%, 0)";
        container.style.backgroundColor = "rgba(0, 0, 0, 0.8)";
        container.style.color = "white";
        container.style.padding = "20px";
        container.style.textAlign = "center";
        container.style.borderRadius = "10px";
        container.style.boxShadow = "0 0 10px rgba(0, 0, 0, 0.5)";
        container.style.zIndex = "20000";
        container.style.transition = "top 0.3s ease-in-out"; // 添加过渡效果

        // 创建提示图片
        var image = document.createElement("img");
        image.style.maxWidth = "25%";
        if (imageType === "yes") {
            image.src = "/source/picture/true.png";
        } else {
            image.src = "/source/picture/false.png";
        }
        container.appendChild(image);
        // 创建提示文本
        let text = document.createElement("p");
        text.innerHTML = message;
        container.appendChild(text);

        // 将提示容器添加到 body 中，并在下一个渲染周期中启动动画
        setTimeout(function () {
            container.style.top = "30%"; // 显示时向下滑动
        }, 0);
        document.body.appendChild(container);

        // 3秒后移除提示容器
        setTimeout(function () {
            container.style.top = "0%"; // 隐藏时向上滑动
            setTimeout(function () {
                document.body.removeChild(container);
            }, 300);
        }, 1000);
    }
}