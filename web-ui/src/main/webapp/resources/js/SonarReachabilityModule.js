var SonarReachabilityModule = (function () {

    var me = {};

    function isReachable(jQuery, sonarUrl, reachableCallback) {
        jQuery.ajax({
            type: 'put',
            url: '/sonar/reachable',
            contentType: 'application/json',
            dataType: 'json',
            processData: false,
            data: '{ "url": "' + sonarUrl + '"}'
        }).done(function (data) {
            reachableCallback(data['ok']);
        }).fail(function() {
            reachableCallback(false);
        })
    }

    function bindOnChange(jQuery, uiElement, getSonarServerUrl, statusDiv) {
        jQuery(uiElement).change(function () {

            // show in progress label
            jQuery(statusDiv).show();
            jQuery(statusDiv + ' .label-info').show();
            jQuery(statusDiv + ' .label-success').hide();
            jQuery(statusDiv + ' .label-important').hide();

            isReachable(jQuery, getSonarServerUrl(), function(reachable) {
                jQuery(statusDiv + ' .label-info').hide();
                if (reachable === true) {
                    jQuery(statusDiv + ' .label-important').hide();
                    jQuery(statusDiv + ' .label-success').show();
                } else {
                    jQuery(statusDiv + ' .label-success').hide();
                    jQuery(statusDiv + ' .label-important').show();
                }
            });
        });
    }

    me.init = function (jQuery, getSonarServerUrl, bindOnElements, statusDiv) {
        // bind functionality to given ui elements
        jQuery.each(bindOnElements, function(index, value) {
            bindOnChange(jQuery, value,getSonarServerUrl, statusDiv);
        });
    }

    return me;
}());
