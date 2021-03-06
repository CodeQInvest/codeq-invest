var SonarReachabilityModule = (function () {

    var me = {};

    function isReachable(jQuery, connectionSettings, reachableCallback) {
        jQuery.ajax({
            type: 'put',
            url: '/sonar/reachable',
            contentType: 'application/json',
            dataType: 'json',
            processData: false,
            data: JSON.stringify(connectionSettings)
        }).done(function (data) {
            reachableCallback(data['ok']);
        }).fail(function() {
            reachableCallback(false);
        })
    }

    function bindOnChange(jQuery, uiElement, getSonarConnectionSettings, statusDiv) {
        jQuery(uiElement).change(function () {

            // show in progress label
            jQuery(statusDiv).show();
            jQuery(statusDiv + ' .label-info').show();
            jQuery(statusDiv + ' .label-success').hide();
            jQuery(statusDiv + ' .label-important').hide();

            isReachable(jQuery, getSonarConnectionSettings(), function(reachable) {
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

    me.init = function (jQuery, getSonarConnectionSettings, bindOnElements, statusDiv) {
        // bind functionality to given ui elements
        jQuery.each(bindOnElements, function(index, value) {
            bindOnChange(jQuery, value, getSonarConnectionSettings, statusDiv);
        });
    }

    return me;
}());
