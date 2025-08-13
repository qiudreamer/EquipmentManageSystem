function postData(strLink, data) {
    return new Promise(function(resolve, reject) {
        var xhr = new XMLHttpRequest();
        xhr.open('POST', strLink, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.onload = function() {
            if (xhr.status >= 200 && xhr.status < 300) {
                var response = JSON.parse(xhr.responseText);
                resolve(response); // 使用resolve将数据传递出去
            } else {
                reject('请求失败'); // 使用reject表示请求失败
            }
        };
        xhr.send(data);
    });
}

function getData(strLink) {
    return new Promise(function(resolve, reject) {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', strLink, true);
        xhr.onload = function() {
            if (xhr.status >= 200 && xhr.status < 300) {
                var response = JSON.parse(xhr.responseText);
                resolve(response); // 使用resolve将数据传递出去
            } else {
                reject('请求失败'); // 使用reject表示请求失败
            }
        };
        xhr.send();
    });
}


