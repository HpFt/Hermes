//******************************************************************************************************
//	Name: ubr_file_upload.js
//	Revision: 4.0
//	Date: 9:45 PM November 23, 2009
//	Link: http://uber-uploader.sourceforge.net
//	Developer Peter Schmandra
//
//	Copyright (C) 2009  Peter Schmandra
//
//	This file is part of Uber-Uploader.
//
//	Uber-Uploader is free software: you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation, either version 3 of the License, or
//	(at your option) any later version.
//
//	Uber-Uploader is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//	GNU General Public License for more details.
//
//	You should have received a copy of the GNU General Public License
//	along with Uber-Uploader. If not, see http://www.gnu.org/licenses/.
//
//***************************************************************************************************************
/*
var cyr=new Array("jo","zh","i'","ch","sh","xh","je","ju","ja","a","b","v","g","d","e","z","i","k","l","m","n","o","p","r","s","t","u","f","x","c","'","y","`","j","h", "_");
var lat=new Array("�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�"," ");
 
var cyrcap=new Array("JO","Jo","ZH","Zh","I'","Ch","CH","Sh","SH","Xh","XH","Je","JE","Ju","JU","Ja","JA","A","B","V","G","D","E","Z","I","K","L","M","N","      O","P","R","S","T","U","F","X","C","Y","J","H");
var latcap=new Array("�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�      ","�","�","�","�","�","�","�");
//function translate(tex)
{
var buf=tex;
var i;
for (i=0;i<latcap.length;i++)
        {
        buf=replace(buf,latcap[i],cyrcap[i],1,0);
        }
 
for (i=0;i<lat.length;i++)
        {
        buf=replace(buf,lat[i],cyr[i],1,0);
        }
 
tex=buf;
return tex;
}
 
function replace(target,oldTerm,newTerm,caseSens,wordOnly) {
 
        var work = target;
        var ind = 0;
        var next = 0;
 
        if (!caseSens) {
          oldTerm = oldTerm.toLowerCase();
          work = target.toLowerCase();
        }
 
        while ((ind = work.indexOf(oldTerm,next)) >= 0) {
          if (wordOnly) {
            var before = ind - 1;
            var after = ind + oldTerm.length;
            if (!(space(work.charAt(before)) && space(work.charAt(after)))) {
              next = ind + oldTerm.length;
              continue;
            }
          }
          target = target.substring(0,ind) + newTerm +
          target.substring(ind+oldTerm.length,target.length);
          work = work.substring(0,ind) + newTerm +
          work.substring(ind+oldTerm.length,work.length);
          next = ind + newTerm.length;
          if (next >= work.length) { break; }
        }
 
        return target;
 
}

*/

var UberUpload = {
    seconds: 0,
    minutes: 0,
    hours: 0,
    start_time: 0,
    upload_id: null,
    total_upload_size: 0,
    total_kbytes: 0,
    toggle_upload_stats: 0,
    file_label_highlight_on: '#b45931',
    file_label_highlight_off: '#F9F9F9',
    CPB_loop: false,
    CPB_width: 0,
    CPB_bytes: 0,
    CPB_time_width: 500,
    CPB_time_bytes: 15,
    CPB_hold: true,
    CPB_byte_timer: null,
    CPB_status_timer: null,
    BPB_width_inc: 0,
    BPB_width_new: 0,
    BPB_width_old: 0,
    BPB_timer: null,
    UP_timer: null,
    progress_data: null,
    path_to_link_script: null,
    path_to_set_progress_script: null,
    path_to_get_progress_script: null,
    path_to_upload_script: null,
    check_allow_extensions_on_client: null,
    check_disallow_extensions_on_client: null,
    allow_extensions: null,
    disallow_extensions: null,
    check_file_name_format: null,
    check_file_name_regex: null,
    check_file_name_error_message: null,
    max_file_name_chars: null,
    min_file_name_chars: null,
    check_null_file_count: null,
    check_duplicate_file_count: null,
    max_upload_slots: null,
    cedric_progress_bar: null,
    cedric_hold_to_sync: null,
    bucket_progress_bar: null,
    progress_bar_width: null,
    block_ui_enabled: null,
    show_percent_complete: null,
    show_files_uploaded: null,
    show_current_position: null,
    show_current_file: null,
    show_elapsed_time: null,
    show_est_time_left: null,
    show_est_speed: null,
    getFileName: function (slot_value) {

        var index_of_last_slash = slot_value.lastIndexOf("\\");

        if (index_of_last_slash < 1) {
            index_of_last_slash = slot_value.lastIndexOf("/");
        }

        var file_name = slot_value.slice(index_of_last_slash + 1, slot_value.length);

        return file_name;
    },

    getFileExtension: function (slot_value) {
        var file_extension = slot_value.substring(slot_value.lastIndexOf(".") + 1, slot_value.length).toLowerCase();

        return file_extension;
    },

    highlightFileLabel: function (file_label, color) {
        JQ("#" + file_label).css({background: color});
    },

    clearFileLabels: function () {
        JQ("#ubr_upload_form").find(":file").each(function () {
            UberUpload.highlightFileLabel(JQ(this).attr("id") + "_label", UberUpload.file_label_highlight_off);
        });
    },

    // Check the file format before uploading
    checkFileNameFormat: function () {
        if (!UberUpload.check_file_name_format) {
            return false;
        }

        var found_error = false;

        JQ("#ubr_upload_form").find(":file").each(function () {
            if (JQ(this).val() !== "") {
                var file_name = UberUpload.getFileName(JQ(this).val());
                if (file_name.length > UberUpload.max_file_name_chars) {
                    UberUpload.highlightFileLabel(JQ(this).attr("id") + "_label", UberUpload.file_label_highlight_on);
                    UberUpload.showAlert("������, ��� ����� �� ����� ��������� ����� " + UberUpload.max_file_name_chars + " ��������.", 500, 85, UberUpload.block_ui_enabled);
                    found_error = true;
                }

                if (file_name.length < UberUpload.min_file_name_chars) {
                    UberUpload.highlightFileLabel(JQ(this).attr("id") + "_label", UberUpload.file_label_highlight_on);
                    UberUpload.showAlert("������, ��� ����� �� ����� ���� ����� " + UberUpload.min_file_name_chars + " ��������.", 500, 85, UberUpload.block_ui_enabled);
                    found_error = true;
                }

                if (!UberUpload.check_file_name_regex.test(file_name)) {
                    UberUpload.highlightFileLabel(JQ(this).attr("id") + "_label", UberUpload.file_label_highlight_on);
                    UberUpload.showAlert(UberUpload.check_file_name_error_message, 500, 85, UberUpload.block_ui_enabled);
                    found_error = true;
                }
            }
        });

        return found_error;
    },

    // Check for legal file extentions
    checkAllowFileExtensions: function () {
        if (!UberUpload.check_allow_extensions_on_client) {
            return false;
        }

        var found_error = false;

        JQ("#ubr_upload_form").find(":file").each(function () {
            if (JQ(this).val() !== "") {
                var file_extension = UberUpload.getFileExtension(UberUpload.getFileName(JQ(this).val()));

                if (!file_extension.match(UberUpload.allow_extensions)) {
                    UberUpload.highlightFileLabel(JQ(this).attr("id") + "_label", UberUpload.file_label_highlight_on);
                    UberUpload.showAlert('��������, �� �������� ������ � ����������� "' + file_extension + '" ���������.', 500, 85, UberUpload.block_ui_enabled);
                    found_error = true;
                }
            }
        });

        return found_error;
    },

    // Check for illegal file extentions
    checkDisallowFileExtensions: function () {
        if (!UberUpload.check_disallow_extensions_on_client) {
            return false;
        }

        var found_error = false;

        JQ("#ubr_upload_form").find(":file").each(function () {
            if (JQ(this).val() !== "") {
                var file_extension = UberUpload.getFileExtension(UberUpload.getFileName(JQ(this).val()));

                if (file_extension.match(UberUpload.disallow_extensions)) {
                    UberUpload.highlightFileLabel(JQ(this).attr("id") + "_label", UberUpload.file_label_highlight_on);
                    UberUpload.showAlert('��������, �� �������� ����� � ����������� "' + file_extension + '" �� ���������.', 500, 85, UberUpload.block_ui_enabled);
                    found_error = true;
                }
            }
        });

        return found_error;
    },

    // Make sure the user selected at least one file
    checkNullFileCount: function () {
        if (!UberUpload.check_null_file_count) {
            return false;
        }

        var found_file = false;

        JQ("#ubr_upload_form").find(':file').each(function () {
            if (JQ(this).val() !== "") {
                found_file = true;
            }
        });

        if (!found_file) {
            UberUpload.showAlert("���������� �������� ���� ��� ��������.", 400, 80, UberUpload.block_ui_enabled);
            return true;
        }
        else {
            return false;
        }
    },
    showHelpMess: function () {
        UberUpload.showAlert('<table align = "left" border = "0"><tr><td><b><ol><li>������ ����� ��������� �� ������� 30 ����.<li>�������� ������������� �������� �� ����� 5-�� ������.<li>������ ������� ����� ��  ������ ��������� 1,5 ��.<li>� ��������� � ����������� ���������� �� ������ hermes-admin@hermitage.ru.</ol><br><br> <span style = "color:#FF0000; font-size:20px">�����:</span> �� ������� ������� ������� ����� ������ <span style = "color:#FF0000;">������</span> �� ������ �������� ����������.</td></tr></table>', 800, 200, UberUpload.block_ui_enabled);
    },
    // Make sure the user is not uploading duplicate files
    checkDuplicateFileCount: function () {
        if (!UberUpload.check_duplicate_file_count) {
            return false;
        }

        var found_duplicate = false;
        var file_count = 0;
        var file_name_array = [];

        JQ("#ubr_upload_form").find(":file").each(function () {
            if (JQ(this).val() !== "") {
                var obj = {};
                obj.file_name = UberUpload.getFileName(JQ(this).val());
                obj.label_name = JQ(this).attr("id") + "_label";
                file_name_array[file_count] = obj;
                file_count++;
            }
        });

        for (var i = 0; i < file_name_array.length; i++) {
            var obj_1 = file_name_array[i];

            for (var j = 0; j < file_name_array.length; j++) {
                var obj_2 = file_name_array[j];

                if (obj_1.file_name === obj_2.file_name && obj_1.label_name !== obj_2.label_name) {
                    found_duplicate = true;
                    UberUpload.highlightFileLabel(obj_1.label_name, UberUpload.file_label_highlight_on);
                    UberUpload.highlightFileLabel(obj_2.label_name, UberUpload.file_label_highlight_on);
                }
            }
        }

        if (found_duplicate) {
            UberUpload.showAlert("�� ��������� ��������� 2 ��� ����� ���������� �����.", 500, 80, UberUpload.block_ui_enabled);
            return true;
        }
        else {
            return false;
        }
    },

    showAlert: function (alert_message, alert_width, alert_height, block_ui_enabled) {
        if (!block_ui_enabled) {
            alert(alert_message);
        }
        else {
            alert_message = "<br>" + alert_message + "<br><br><input style='width:75px;' type='button' id='ok_btn' name='ok' value='�������' onClick='JQ.unblockUI();'>";

            JQ.blockUI({
                message: alert_message,
                css: {
                    width: alert_width + 'px',
                    height: alert_height + 'px',
                    top: (JQ(window).height() / 3) - (alert_height / 2) + 'px',
                    left: (JQ(window).width() / 2) - (alert_width / 2) + 'px',
                    textAlign: 'center',
                    cursor: 'default',
                    backgroundColor: '#EDEDED',
                    borderColor: '#D9D9D9',
                    color: 'black',
                    font: '14px Arial',
                    fontWeight: 'bold',
                    padding: '2px',
                    opacity: '1',
                    '-webkit-border-radius': '2px',
                    '-moz-border-radius': '2px'
                },
                overlayCSS: {
                    cursor: 'default',
                    applyPlatformOpacityRules: true
                }
            });
        }
    },

    showCGIOutput: function (CGI_message, reset_page) {
        UberUpload.showAlert(CGI_message, 400, 80, UberUpload.block_ui_enabled);
        if (reset_page) {
            UberUpload.resetFileUploadPage();
        }
    },

    showDebugMessage: function (message) {
        JQ("#ubr_debug").append(message + "<br>");
    },

    showAlertMessage: function (message) {
        JQ("#ubr_alert").html(message);
    },

    redirectAfterUpload: function (redirect_url, embedded_upload_results) {
        redirect_url = decodeURIComponent(redirect_url);

        if (embedded_upload_results) {
            JQ('#upload_container').load(redirect_url);
            UberUpload.showEmbeddedUploadResults();
        }
        else {
            self.location.href = redirect_url;
        }
    },

    showEmbeddedUploadResults: function () {
        UberUpload.stopDataLoop();
        UberUpload.resetProgressBar();

        JQ("#ubr_alert").html("");
        JQ("#upload_container").show();
        JQ("#reset_button").val("�������� �����");
        JQ(".upfile_ultimo").remove();
        JQ(".upfile").remove();
        JQ(".upfile_label").remove();
        JQ("#upload_button").show();
        JQ("#upload_slots_container").hide();

        UberUpload.addUploadSlot();
    },

    stopDataLoop: function () {
        UberUpload.CPB_loop = false;
        clearInterval(UberUpload.UP_timer);
        clearInterval(UberUpload.BPB_timer);

        if (UberUpload.cedric_progress_bar) {
            if (UberUpload.show_current_position) {
                clearTimeout(UberUpload.CPB_byte_timer);
            }
            clearTimeout(UberUpload.CPB_status_timer);
        }
    },

    // Reset the progress bar
    resetProgressBar: function () {
        JQ("#progress_bar_container").hide();
        JQ("#upload_stats_container").hide();

        UberUpload.seconds = 0;
        UberUpload.minutes = 0;
        UberUpload.hours = 0;
        UberUpload.start_time = 0;
        UberUpload.upload_id = '';
        UberUpload.progress_data = '';
        UberUpload.total_upload_size = 0;
        UberUpload.total_kbytes = 0;
        UberUpload.toggle_upload_stats = 1;
        UberUpload.CPB_loop = false;
        UberUpload.CPB_width = 0;
        UberUpload.CPB_bytes = 0;
        UberUpload.CPB_hold = true;
        UberUpload.BPB_width_inc = 0;
        UberUpload.BPB_width_new = 0;
        UberUpload.BPB_width_old = 0;

        JQ("#progress_bar").css("width", "0px");


        if (UberUpload.show_percent_complete) {
            JQ("#percent_complete").html("0%");
        }
        if (UberUpload.show_files_uploaded) {
            JQ("#files_uploaded").html("0");
        }
        if (UberUpload.show_files_uploaded) {
            JQ("#total_uploads").html("0");
        }
        if (UberUpload.show_current_position) {
            JQ("#current_position").html("0");
        }
        if (UberUpload.show_current_position) {
            JQ("#total_kbytes").html("0");
        }
        if (UberUpload.show_elapsed_time) {
            JQ("#elapsed_time").html("00:00:00");
        }
        if (UberUpload.show_est_time_left) {
            JQ("#est_time_left").html("00:00:00");
        }
        if (UberUpload.show_est_speed) {
            JQ("#est_speed").html("0");
        }
    },

    resetUploadDiv: function () {
        JQ("#upload_container").hide();
        JQ("#upload_container").html("");
    },

    // Initialize the file upload page
    resetFileUploadPage: function () {
        UberUpload.stopDataLoop();
        UberUpload.resetProgressBar();
        UberUpload.resetUploadDiv();

        JQ("#ubr_alert").html("");
        JQ("#reset_button").val("��������");
        JQ(".upfile_ultimo").remove();
        JQ(".upfile").remove();
        JQ(".upfile_label").remove();
        JQ("#upload_button").show();
        JQ("#upload_slots_container").hide();
        JQ("#upload_form_values_container").show();

        UberUpload.addUploadSlot();
    },

    // Link the upload
    linkUpload: function () {
        if (UberUpload.check_file_name_format || UberUpload.check_allow_extensions_on_client || UberUpload.check_disallow_extensions_on_client || UberUpload.check_duplicate_file_count) {
            UberUpload.clearFileLabels();
        }
        if (UberUpload.checkFileNameFormat()) {
            return false;
        }
        if (UberUpload.checkAllowFileExtensions()) {
            return false;
        }
        if (UberUpload.checkDisallowFileExtensions()) {
            return false;
        }
        if (UberUpload.checkNullFileCount()) {
            return false;
        }
        if (UberUpload.checkDuplicateFileCount()) {
            return false;
        }

        JQ("#upload_button").hide();

        if (UberUpload.show_files_uploaded) {
            JQ("#total_uploads").html(JQ(".upfile").length - 1);
        }

        var form_data = JQ("#ubr_upload_form").serialize();
        var file_data = UberUpload.serializeFileNames();
        var data = form_data + "&" + file_data;

        JQ.post(UberUpload.path_to_link_script, data, function () {
        }, "script");

        return false;
    },

    // Add upload file names to serialized data
    // Based on jQuery.serializeAnything by Bramus! (Bram Van Damme)
    serializeFileNames: function () {
        var toReturn = [];

        JQ("#ubr_upload_form").find(":file").each(function () {
            if (JQ(this).val() !== "") {
                var file_name = UberUpload.getFileName(JQ(this).val());
                toReturn.push("upload_file[]" + "=" + encodeURIComponent(file_name));
            }
        });

        return toReturn.join("&").replace(/%20/g, "+");
    },

    // Initialize progress bar
    initializeProgressBar: function (upload_id, debug_ajax) {
        if (debug_ajax) {
            UberUpload.showDebugMessage("Initializing Progress Bar: " + UberUpload.path_to_set_progress_script + '?upload_id=' + upload_id);
        }

        var data = "upload_id=" + upload_id;
        JQ.get(UberUpload.path_to_set_progress_script, data, function () {
        }, "script");
    },

    //Submit the upload form
    startUpload: function (upload_id, debug_upload, debug_ajax) {
        UberUpload.resetUploadDiv();

        var iframe_name = "upload_iframe_" + upload_id;

        if (debug_ajax) {
            UberUpload.showDebugMessage("Submitting Upload: " + UberUpload.path_to_upload_script + "?upload_id=" + upload_id);
        }

        JQ("#upload_container").html("<iframe name='" + iframe_name + "' frameborder='0' width='780' height='200' scrolling='auto'></iframe>");
        JQ("#ubr_upload_form").attr("target", iframe_name);
        JQ("#ubr_upload_form").attr("action", UberUpload.path_to_upload_script + "?upload_id=" + upload_id);
        JQ("#upload_slots_container").fadeOut("fast");
        JQ("#upload_form_values_container").fadeOut("fast");
        JQ(".upfile_ultimo").fadeOut("fast");
        JQ("#ubr_upload_form").submit();
        JQ("#reset_button").val("���������� ��������");
        if (!debug_upload) {
            UberUpload.initializeProgressBar(upload_id, debug_ajax);
        }
        else {
            UberUpload.showAlertMessage("��������� ������ ���������, ���������� ���������...");
        }
    },

    // Stop the upload
    stopUpload: function () {
        try {
            document.execCommand("Stop");
        }
        catch (e) {
        }
        try {
            window.stop();
        }
        catch (e) {
        }

        JQ("#upload_slots_container").fadeIn("fast");
        JQ("#upload_form_values_container").fadeIn("fast");
        JQ("#upload_button").show();
        JQ("#reset_button").val("��������");
    },

    // Get the progress of the upload
    getProgressStatus: function () {
        if (UberUpload.CPB_loop) {
            JQ.get(UberUpload.path_to_get_progress_script, UberUpload.progress_data, function () {
            }, "script");
        }
    },

    // Make the progress bar smooth
    smoothCedricStatus: function () {
        if (UberUpload.CPB_width < UberUpload.progress_bar_width && !UberUpload.CPB_hold) {
            UberUpload.CPB_width++;
            JQ("#progress_bar").css("width", UberUpload.CPB_width + "px");
        }

        if (UberUpload.CPB_loop) {
            clearTimeout(UberUpload.CPB_status_timer);
            UberUpload.CPB_status_timer = setTimeout("UberUpload.smoothCedricStatus()", UberUpload.CPB_time_width);
        }
    },

    // Make the bytes uploaded smooth
    smoothCedricBytes: function () {
        if (UberUpload.CPB_bytes < UberUpload.total_kbytes && !UberUpload.CPB_hold) {
            UberUpload.CPB_bytes++;
            JQ("#current_position").html(UberUpload.CPB_bytes);
        }

        if (UberUpload.CPB_loop) {
            clearTimeout(UberUpload.CPB_byte_timer);
            UberUpload.CPB_byte_timer = setTimeout("UberUpload.smoothCedricBytes()", UberUpload.CPB_time_bytes);
        }
    },

    //Start the progress bar
    startProgressBar: function (upload_id, upload_size, start_time) {
        UberUpload.upload_id = upload_id;
        UberUpload.total_upload_size = upload_size;
        UberUpload.start_time = start_time;
        UberUpload.progress_data = "upload_id=" + UberUpload.upload_id + "&start_time=" + UberUpload.start_time + "&total_upload_size=" + UberUpload.total_upload_size;
        UberUpload.total_kbytes = Math.round(UberUpload.total_upload_size / 1024);
        UberUpload.CPB_loop = true;

        JQ("#progress_bar_container").fadeIn("fast");
        UberUpload.showAlertMessage("���� �������� ������");
        JQ("#upload_stats_container").slideDown("fast");
        if (UberUpload.show_current_position) {
            JQ("#total_kbytes").html(UberUpload.total_kbytes + " ");
        }
        if (UberUpload.show_elapsed_time) {
            UberUpload.UP_timer = setInterval("UberUpload.getElapsedTime()", 1000);
        }

        UberUpload.getProgressStatus();

        if (UberUpload.cedric_progress_bar) {
            if (UberUpload.show_current_position) {
                UberUpload.smoothCedricBytes();
            }
            UberUpload.smoothCedricStatus();
        }
    },

    // Calculate and display upload information*****************************************************************************************************************************************************
    setProgressStatus: function (total_bytes_read, files_uploaded, current_file, bytes_read, lapsed_time) {
        var byte_speed = 0;
        var time_remaining = 0;

        if (lapsed_time > 0) {
            byte_speed = total_bytes_read / lapsed_time;
        }
        if (byte_speed > 0) {
            time_remaining = Math.round((UberUpload.total_upload_size - total_bytes_read) / byte_speed);
        }

        if (UberUpload.cedric_progress_bar === 1) {
            if (byte_speed !== 0) {
                var temp_CPB_time_width = Math.round(UberUpload.total_upload_size * 1000 / (byte_speed * UberUpload.progress_bar_width));
                var temp_CPB_time_bytes = Math.round(1024000 / byte_speed);

                if (temp_CPB_time_width < 5001) {
                    UberUpload.CPB_time_width = temp_CPB_time_width;
                }
                if (temp_CPB_time_bytes < 5001) {
                    UberUpload.CPB_time_bytes = temp_CPB_time_bytes;
                }
            }
            else {
                UberUpload.CPB_time_width = 500;
                UberUpload.CPB_time_bytes = 15;
            }
        }

        // Calculate percent_complete finished
        var percent_complete = Math.floor(100 * parseInt(total_bytes_read, 10) / parseInt(UberUpload.total_upload_size, 10));

        if (percent_complete === Infinity) {
            percent_complete = 0;
        }

        var progress_bar_status = Math.floor(UberUpload.progress_bar_width * (parseInt(total_bytes_read, 10) / parseInt(UberUpload.total_upload_size, 10)));

        // Calculate time remaining
        var remaining_sec = (time_remaining % 60);
        var remaining_min = (((time_remaining - remaining_sec) % 3600) / 60);
        var remaining_hours = ((((time_remaining - remaining_sec) - (remaining_min * 60)) % 86400) / 3600);

        if (remaining_sec < 10) {
            remaining_sec = "0" + remaining_sec;
        }
        if (remaining_min < 10) {
            remaining_min = "0" + remaining_min;
        }
        if (remaining_hours < 10) {
            remaining_hours = "0" + remaining_hours;
        }

        var est_time_left = remaining_hours + ":" + remaining_min + ":" + remaining_sec;
        var est_speed = Math.round(byte_speed / 1024);
        var current_position = Math.round(total_bytes_read / 1024);

        if (UberUpload.cedric_progress_bar === 1) {
            if (UberUpload.cedric_hold_to_sync) {
                if (progress_bar_status < UberUpload.CPB_width) {
                    UberUpload.CPB_hold = true;
                }
                else {
                    UberUpload.CPB_hold = false;
                    UberUpload.CPB_width = progress_bar_status;
                    UberUpload.CPB_bytes = current_position;
                }
            }
            else {
                UberUpload.CPB_hold = false;
                UberUpload.CPB_width = progress_bar_status;
                UberUpload.CPB_bytes = current_position;
            }

            JQ("#progress_bar").css("width", progress_bar_status + "px");
        }
        else if (UberUpload.bucket_progress_bar === 1) {
            UberUpload.BPB_width_old = UberUpload.BPB_width_new;
            UberUpload.BPB_width_new = progress_bar_status;

            if ((UberUpload.BPB_width_inc < UberUpload.BPB_width_old) && (UberUpload.BPB_width_new > UberUpload.BPB_width_old)) {
                UberUpload.BPB_width_inc = UberUpload.BPB_width_old;
            }

            clearInterval(UberUpload.BPB_timer);
            UberUpload.BPB_timer = setInterval("UberUpload.incrementProgressBar()", 10);
        }
        else {
            JQ("#progress_bar").css("width", progress_bar_status + "px");
        }

        if (UberUpload.show_current_position) {
            JQ("#current_position").html(current_position);
        }
        if (UberUpload.show_current_file) {
            JQ("#current_file").html(current_file);
        }
        if (UberUpload.show_percent_complete) {
            JQ("#percent_complete").html(percent_complete + "%");
        }
        if (UberUpload.show_files_uploaded) {
            if (files_uploaded > 0) {
                JQ("#files_uploaded").html(files_uploaded);
            }
        }
        if (UberUpload.show_est_time_left) {
            JQ("#est_time_left").html(est_time_left);
        }
        if (UberUpload.show_est_speed) {
            JQ("#est_speed").html(est_speed);
        }
    },

    incrementProgressBar: function () {
        if (UberUpload.BPB_width_inc < UberUpload.BPB_width_new) {
            UberUpload.BPB_width_inc++;
            JQ("#progress_bar").css("width", UberUpload.BPB_width_inc + "px");
        }
    },

    // Calculate the time spent uploading
    getElapsedTime: function () {
        UberUpload.seconds++;

        if (UberUpload.seconds === 60) {
            UberUpload.seconds = 0;
            UberUpload.minutes++;
        }

        if (UberUpload.minutes === 60) {
            UberUpload.minutes = 0;
            UberUpload.hours++;
        }

        var hr = "" + ((UberUpload.hours < 10) ? "0" : "") + UberUpload.hours;
        var min = "" + ((UberUpload.minutes < 10) ? "0" : "") + UberUpload.minutes;
        var sec = "" + ((UberUpload.seconds < 10) ? "0" : "") + UberUpload.seconds;

        JQ("#elapsed_time").html(hr + ":" + min + ":" + sec);
    },

    // Add one upload slot
    addUploadSlot: function () {
        if (JQ(".upfile_ultimo").val() !== "") {
            if (JQ(".upfile").length < UberUpload.max_upload_slots + 1) {
                if (JQ(".upfile").length > 0) {
                    JQ(".upfile_ultimo").hide();
                    JQ("#upload_slots_container").show();
                    JQ("#upload_slots_container").append('<div class=" upfile_label " id="' + JQ(".upfile_ultimo").attr("id") + '_label"><span class="upfile_name">' + UberUpload.getFileName(JQ(".upfile_ultimo").val()) + '</span><span class="upfile_remove" title="������� �� ������" onClick="UberUpload.deleteUploadSlot(\'' + JQ(".upfile_ultimo").attr("id") + '\')"><!--[x]--></span></div>');
                    //JQ("#upload_slots_container").append('<div class="upfile_label" id="' + JQ(".upfile_ultimo").attr("id") +'_label"><span class="upfile_name">' + UberUpload.getFileName(JQ(".upfile_ultimo").val()) + '</span><span class="upfile_remove" title="Remove File" onClick="UberUpload.deleteUploadSlot(\'' + JQ(".upfile_ultimo").attr("id") + '\')"></span></div>');
                }
                var id = new Date().getTime();

                JQ(".upfile_ultimo").removeClass("upfile_ultimo");
                JQ("#file_picker_container").prepend('<input type="file" class="upfile upfile_ultimo ' + getBrowser() + '" name="upfile_' + id + '" id="upfile_' + id + '" size="35" value="">');
                JQ("#upfile_" + id).bind("keypress", function (e) {
                    var code = (e.keyCode ? e.keyCode : e.which);
                    if (code === 13) {
                        return false;
                    }
                });
                JQ("#upfile_" + id).bind("change", function (e) {
                    UberUpload.addUploadSlot();
                });

                if (JQ(".upfile").length > UberUpload.max_upload_slots) {
                    JQ(".upfile_ultimo").fadeOut("fast");
                }
            }
        }
    },

    deleteUploadSlot: function (id) {
        JQ("#" + id).remove();
        JQ("#" + id + '_label').remove();

        if (JQ(".upfile").length <= UberUpload.max_upload_slots) {
            JQ(".upfile_ultimo").fadeIn("fast");
        }
        if (JQ(".upfile").length === 1) {
            JQ("#upload_slots_container").hide();
        }
    }


};

function getBrowser() { // ���������� ������� � �� �������� ����������

    uaVers = '' // uaVers ����� �� ������������ � �����, ��� ���, ���������� ���������� ����������������, ����� �� ���� ������
    if (window.navigator.userAgent.indexOf("Chrome") >= 0) {
        ua = 'upfile_ultimo';
        uaVers = window.navigator.userAgent.substr(window.navigator.userAgent.indexOf("Opera") + 6, 4);
    }
    else if (window.navigator.userAgent.indexOf("Gecko") >= 0) // (Mozilla, Netscape, FireFox)
    {        // �� ������ ����� ���������, ��� ��� �� ���������� � ������ ������� �������� ����� ����� � ������ � ���� �� ����������� ���������
        ua = 'Firefox';
        uaVers = window.navigator.userAgent.substr(window.navigator.userAgent.indexOf("Gecko") + 6, 8) + ' (' + window.navigator.userAgent.substr(8, 3) + ')';
    }
    else if (window.navigator.userAgent.indexOf("MSIE") >= 0) {
        ua = 'IE';
        uaVers = window.navigator.userAgent.substr(window.navigator.userAgent.indexOf("MSIE") + 5, 3);
    }
    else if (window.navigator.userAgent.indexOf("Opera") >= 0) {
        ua = 'Firefox';
        uaVers = window.navigator.userAgent.substr(window.navigator.userAgent.indexOf("Opera") + 5, 3);
    }
    else
        ua = 'Firefox'; // ����������� ��� ��������
    return ua;
};