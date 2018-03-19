$(document).ready(function () {

    alert('e')

    $("#btnSubmit").click(function (event) {

        alert('test');

        var media = $('input[name^="media"]');
        var form_data = new FormData(media);

        /* First build a FormData of files to be sent out to the server-side */
        $.each(media[0].files, function (i, file) {
            form_data.append(i, file);
        });

        /* Now send the gathered files data to our backend server */
        $.ajax({
            type: 'POST',
            cache: false,
            processData: false,
            contentType: false,
            data: form_data,
            url: '/api/file/save',

            success: function (data) {
                $("#result").replaceWith(data)
            }
        });
    });

});