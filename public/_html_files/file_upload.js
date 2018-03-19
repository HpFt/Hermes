const INIT = "/api/upload/init";
const PROGRESS = "/api/upload/progress";
const TOKEN = "token";
const PULL_DELAY = 500;

function log(message) {
    console.log(message)
}

var tokenizer = {
    token: null,
    saveToken: function (jqxhr) {
        this.token = jqxhr.getResponseHeader(TOKEN);
        localStorage.setItem("token", this.token)
    }
};

var uploadingProgress = {
    progressData: null,
    timerId: null,
    start: function (progressData) {
        this.progressData = progressData;
        this.timerId = setInterval(function () {
            if (progressData) {
                uploadingApi.progress(progressData.id, this.stop);
            }
        }, PULL_DELAY);
    },

    stop: function () {
        log("stop");
        clearInterval(this.timerId);
        this.progressData = null
    }
};


var uploadingApi = {

    init: function () {
        $.ajax({
            type: "POST",
            beforeSend: function (request) {
                request.setRequestHeader(TOKEN, tokenizer.token);
            },
            contentType: "application/json",
            dataType: "json",
            url: INIT,
            data: "[{ \"size\":150 }]",
            processData: false,
            success: function (data) {
                uploadingProgress.start(data)
            }
        });
    },

    progress: function (uploading_id, stop) {
        $.ajax({
            type: "GET",
            beforeSend: function (request) {
                request.setRequestHeader(TOKEN, tokenizer.token);
            },
            data: {
                id: uploading_id
            },
            url: PROGRESS,
            success: function (data) {
                log(data);
                if (data) {
                    uploadingProgress.progressData = data
                } else {
                    stop();
                }
            },
            error: function () {
                stop();
            }
        });
    },

    stop: function () {
        uploadingProgress.stop();
    }

};

$(function () {
    $("#upload_button").click(function () {
        uploadingApi.init();
    });
    $("#reset_button").click(function () {
        uploadingApi.stop();
    });
    $(document).ajaxComplete(function (event, xhr) {
        tokenizer.saveToken(xhr);
    })
});