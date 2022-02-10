$(document).ready(e => {
    showContentsAt('.');
    $('#btn-upload').click(() => {
        let input = $('#file-path');
        uploadFile(input[0].files[0]);
    });
    $('#btnGo').click(() => showContentsAt($('#path').val()));
    $('input#path').keypress(e => {
        if (e.which == '13')// enter key code
            $('#btnGo').click()
    });
})
const BASE_ADDRESS = getBaseAddress();

function getCompleteUrl(url) {
    return BASE_ADDRESS + url;
}

function getBaseAddress() {
    let adr = window.location.href;
    if (adr.endsWith("/"))
        adr = adr.substr(0, adr.length - 1);
    return adr;
}

function checkResponseError(response, successHandler) {
    if (response.status >= 200 && response.status <= 299) {
        return successHandler(response.data);
    } else {
        throw Error(response.statusText);
    }
}

function postJson(url, data, successProcessor, failProcessor) {
    axios.post(getCompleteUrl(url), data)
        .then(response => checkResponseError(response, data => ifExistsRun(successProcessor, data)))
        .catch(err => ifExistsRunElseAlert(failProcessor, err))
}

function downloadFile(path, progressItem) {
    axios(getCompleteUrl('/files/download'), {
        method: "POST",
        data: {"path": path},
        responseType: 'blob',
        onDownloadProgress: e => {
            if (progressItem)
                progressItem.text(Math.round(e.loaded / e.total * 100) + '%')
        },
    },)
        .then(response => checkResponseError(response, blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.style.display = 'none';
            a.href = url;
            let fileName = path.endsWith("/") ? path.substr(0, path.length - 1) : path;
            a.download = fileName.substr(fileName.lastIndexOf("/") + 1);
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
        }))
        .catch(err => alert(err))
}

function uploadFile(file) {
    const data = new FormData();
    data.append('file', file);
    let progressItem = $('.progress-u');
    // send `POST` request
    axios(getCompleteUrl('/files/upload'), {
        method: 'POST',
        data: data,
        headers: {'Content-Type': 'multipart/form-data'},
        onUploadProgress: e => {
            let prg = Math.round(e.loaded / e.total * 100);
            let msg = prg < 100 ? `Reading file... ${prg}%` : 'Uploading to server. Please wait...';
            progressItem.text(msg);
        },
    })
        .then(res => checkResponseError(res, r => progressItem.text('Successfully done')))
        .catch(err => {
            progressItem.text('');
            alert(err);
        });
}

function ifExistsRun(handler, data) {
    if (handler)
        handler(data)
}

function ifExistsRunElseAlert(handler, data) {
    if (handler)
        handler(data)
    else
        alert(data)
}

function showContentsAt(path) {
    postJson('/files/list', {"path": path}, result => {
        $('#path').val(result.path);
        $('#current-loc').val(result.path);
        fillContainer(result);
    })
}

function fillContainer(fileList) {
    let tblContainer = $('<table class="tbl-file-list"></table>');
    let parent = getParent(fileList.path);
    if (parent != null)
        appendContent(tblContainer, {"name": "../", "type": "dir",},)
    fileList.files.forEach(value => appendContent(tblContainer, value));
    let container = $('#path-content');
    container.empty();
    container.append(tblContainer);
}

function appendContent(container, fileDto) {
    container.append(
        `<tr>
<td><img src="/static/images/${fileDto.type === 'dir' ? 'ic_dir.png' : 'ic_file.png'}" class="icon"/></td>
<td><span class="file" filetype="${fileDto.type}" onclick="onClickFile(this)">${fileDto.name}</span></td>
<td><span class="size">${getHumanReadableSize(fileDto.size)}</span></td>
<td><span class="progress-d"></span></td>
</tr>`)
}

const SIZE_SUFFIXES = ['B', 'KB', 'MB', 'GB', 'TB'];
const A = 1024;

function getHumanReadableSize(size) {
    if (size) {
        let c = 0;
        while (size >= A && c < SIZE_SUFFIXES.length) {
            size /= A;
            c++;
        }
        return (size % 1 !== 0 ? size.toFixed(2) : size) + ' ' + SIZE_SUFFIXES[c];
    } else {
        return ''
    }
}

function getParent(path) {
    if (path.endsWith("/"))
        path = path.substr(0, path.length - 1);
    let index = path.lastIndexOf('/');
    return index >= 0 ? path.substr(0, index + 1) : null;
}

function onClickFile(item) {
    let jqItem = $(item);
    let file = $('#current-loc').val();
    if (!file.endsWith('/'))
        file += '/';
    file = jqItem.text() === '../' ? getParent(file) : file + jqItem.text();
    let type = jqItem.attr('filetype');
    if (type === 'dir') {
        showContentsAt(file)
    } else {
        downloadFile(file, jqItem.parents('tr').find('.progress-d'));
    }
}