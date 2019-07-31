var miskin_fileupload_progressbar_html = '<div class="progress hidden" id="__id__-progress" style="display: none;"><div class="progress-bar progress-bar-success" data-transitiongoal="0" style="width: 0;" aria-valuenow="0"></div></div>';

var miskin_fileupload_download_btn_html = '<a id="__id__-downloadbtn" href="__file_link__" class="btn btn-default btn-sm" style="border-color: #ced4db;color: #495057;"><i class="fa fa-download"></i><span style="border-right: 1px solid #ccc;">&nbsp;</span>&nbsp;<span id="__id__-download-name">__file_name__</span></a>';

var miskin_fileupload_delete_btn_html = '<a id="__id__-deletebtn" class="btn btn-default btn-sm" style="border-color: #ced4db;color: #495057;margin-left: 1px;"><i class="fa fa-times"></i></a>';

var miskin_fileupload_required_class = 'miskin-required';

var miskin_fileupload_file_input_selector = '.miskin-file-input';
var miskin_fileupload_required_fields_for_upload_selector = '.miskin-file-upload-required';

if ($ == undefined) {
    console.log('Jquery is not available. Did you include it?');
}
if ($.fileupload == undefined) {
    console.log('Jquery-fileupload plugin is not available. Did you include it?');
}

$(document).ready(function () {
    miskinInitializeFileFields();
});

function miskinInitializeFileFields() {
    var miskinUploadInputs = $(miskin_fileupload_file_input_selector);
    if(miskinUploadInputs.length<1) {
        return;
    }

    // required json array for jquery-file-upload plugin submit
    var requiredFormSubmitData = {};

    $(miskin_fileupload_required_fields_for_upload_selector).each(function(){
        requiredFormSubmitData[$(this).attr('name')] = $(this).val();
    });

    //requiredFormSubmitData = JSON.stringify(requiredFormSubmitData);



    miskinUploadInputs.each(function(){
        var current_file_input = $(this);
        var elem_id = current_file_input.attr('id');
        var elem_name = current_file_input.attr('name');
        var download_link = current_file_input.attr('data-download-link');
        var download_name = current_file_input.attr('data-download-name');
        var allowed_extensions = current_file_input.attr('data-accept');
        if (allowed_extensions != undefined) {
            allowed_extensions = allowed_extensions.split(',');
        }
        var allowed_max_file_size = current_file_input.attr('data-max-file-size');
        if (allowed_max_file_size != undefined) {
            allowed_max_file_size = parseInt(allowed_max_file_size);
        }
        var is_required = current_file_input.attr('data-parsley-required') == 'true';


        var is_file_uploaded = download_link != undefined;
        if (!is_file_uploaded) {
            // file not uploaded yet
            download_link = '#';
            download_name = '';
        } else {
            // file is uploaded already

        }

        // add delete file button
        current_file_input.after(miskin_fileupload_delete_btn_html.replace('__id__', elem_id));
        var delete_btn = $('#' + elem_id + '-deletebtn');

        // add download button
        current_file_input.after(
            miskin_fileupload_download_btn_html
                .replace('__id__', elem_id)
                .replace('__id__', elem_id)
                .replace('__file_link__', download_link)
                .replace('__file_name__', download_name)
        );

        // add hidden progressbar
        current_file_input.after(miskin_fileupload_progressbar_html.replace('__id__', elem_id));



        if (!is_file_uploaded) {
            // file not uploaded yet
            // hide download and delete buttons
            $('#' + elem_id + '-downloadbtn').hide();
            delete_btn.hide();
        } else {
            // file is uploaded already
            // hide the file input
            current_file_input.hide();
            // remove required attribute if set
            if(is_required) {
                current_file_input.addClass(miskin_fileupload_required_class);
                current_file_input.attr('data-parsley-required', 'false');
                current_file_input.removeAttr('required');
            }
        }

        // add deletebtn click handler
        delete_btn.click(miskinDeleteUploadedFile);





        // initialize jquery-file-upload plugin for this element
        current_file_input.fileupload({
            dataType: 'json',
            formData: requiredFormSubmitData,
            add: function (e, data) {
                var jqInput = $(data.fileInput);
                if(allowed_extensions != undefined) {
                    var ext = '.' + jqInput.val().split('.').pop().toLowerCase();
                    if($.inArray(ext, allowed_extensions) == -1) {
                        alert(
                            'This extension is not allowed. Allowed extensions are: ' + JSON.stringify(allowed_extensions) + '\n\n' +
                            'Неправилный расширения файла. Допустимые расширения: ' + JSON.stringify(allowed_extensions) + '\n\n' +
                            'Faylning formati noto`g`ri. Ruxsat etilgan formatlar: ' + JSON.stringify(allowed_extensions)
                        );
                        return;
                    }
                }
                if(allowed_max_file_size != undefined) {
                    if(
                        jqInput[0].files != undefined &&
                        jqInput[0].files[0] != undefined &&
                        jqInput[0].files[0].size != undefined
                    ) {
                        if (allowed_max_file_size < jqInput[0].files[0].size) {
                            var strMaxSize = (allowed_max_file_size * 1.0 / 1024 / 1024).toFixed(1) + ' Mb';
                            alert(
                                'File size is too big. Allowed max file size is: ' + strMaxSize + '\n\n' +
                                'Размер файла слишком большой. Максимальный допустимый размер файла: ' + strMaxSize + '\n\n' +
                                'Faylning hajmi juda katta. Ruxsat etilgan eng katta hajm: ' + strMaxSize
                            );
                            return;
                        }
                    }
                }
                data.context = data.fileInput;
                data.submit();
                miskinFileUploadStarted($(data.context));
            },
            done: function (e, data) {
                if(data.result.status == 1) {
                    miskinFileUploadDoneSuccessfully($(data.context), data.result.files);
                } else {
                    //TODO: implement error handling
                    alert('error uploading file.');
                }
//                    $.each(data.result.files, function (index, file) {
////                        $('<p/>').text(file.name).appendTo(document.body);
//
//                    });
            },
            progressall: function (e, data) {
                miskinFileUploadProgressUpdated($(e.target), data.total, data.loaded);
            }
        });



    });
}

function miskinDeleteUploadedFile() {
    $(this).addClass('disabled');

    //get target input id
    var input_id = $(this).attr('id');
    input_id = input_id.replace('-deletebtn', '');

    var target_input = $('#' + input_id);

    // hide download and delete buttons
    $('#' + input_id + '-downloadbtn').hide();
    $(this).hide();

    // show the input field
    target_input.show();

    var is_required = target_input.hasClass(miskin_fileupload_required_class);

    // set required attribute if required
    if (is_required) {
        target_input.removeClass(miskin_fileupload_required_class);
        target_input.attr('data-parsley-required', 'true');
        target_input.attr('required', 'required');
    }

    $(this).removeClass('disabled');
}

function miskinFileUploadStarted(current_file_input) {
    var elem_id = current_file_input.attr('id');

    $('#' + elem_id + '-progress').removeClass('hidden').show();

    $('#' + elem_id).hide();
}

function miskinFileUploadProgressUpdated(current_file_input, total, loaded) {
    //console.log('miskinFileUploadProgressUpdated ' + total + ' ' + loaded);
    var elem_id = current_file_input.attr('id');

    var progress = parseInt(loaded * 100 / total, 10);
    //console.log('miskinFileUploadProgressUpdated ' + progress + '%');

    $('#' + elem_id + '-progress').find('.progress-bar')
        .attr('data-transitiongoal', progress)
        .attr('aria-valuenow', progress)
        .css('width', progress + '%');
}

function miskinFileUploadDoneSuccessfully(current_file_input, files) {
    var elem_id = current_file_input.attr('id');
    current_file_input = $('#'+elem_id);
    var is_required = current_file_input.attr('data-parsley-required') == 'true';




    // remove required attribute if set
    if(is_required) {
        current_file_input.addClass(miskin_fileupload_required_class);
        current_file_input.attr('data-parsley-required', 'false');
        current_file_input.removeAttr('required');
    }



    $('#' + elem_id + '-progress').hide();

    $('#' + elem_id + '-download-name').html(files[0].name);
    $('#' + elem_id + '-downloadbtn').attr('href', files[0].link).show();
    $('#' + elem_id + '-deletebtn').show();

    // hide the file input
    current_file_input.hide();
}