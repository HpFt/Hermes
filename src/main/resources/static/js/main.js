$(function () {
    $(".upload").click(function () {
        $("#exampleFormControlFile1").submit()
    });
    updateFiles()
});


function upload() {
    console.info("start uploading");
    var files = new FormData();
    jQuery.each(jQuery('#file')[0].files, function (i, file) {
        files.append('file-' + i, file);
    });
    $.ajax({
        url: '/api/upload',
        data: files,
        cache: false,
        contentType: false,
        processData: false,
        method: 'POST',
        type: 'POST', // For jQuery < 1.9
        success: function (data) {
            updateFiles()
        }
    });
}

function copyLink(url) {
    alert(url)
}

function updateFiles() {
    $.ajax({
        url: '/api/file',
        success: function (data) {
            /* Get the files array from the data */
            var files = data;

            console.info(files);

            /* Remove current set of movie template items */
            $("#files-body").empty();

            /* Render the template items for each movie
            and insert the template items into the "movieList" */
            $("#test-markup").tmpl(files).appendTo("#files-body");
        }
    });
}



/*
      <tr class="uploaded">
            <th scope="row"><span class="badge badge-primary">New</span> Новый файл</th>
            <td>94.1 Мб</td>
            <td>2019-01-28</td>
            <td>30 минут</td>
            <td class="action">
                <div class="spinner-border" role="status">
                    <span class="sr-only">Loading...</span>
                </div>
                <button type="button" class="btn btn-primary del">Удалить</button>
            </td>
        </tr>
 */