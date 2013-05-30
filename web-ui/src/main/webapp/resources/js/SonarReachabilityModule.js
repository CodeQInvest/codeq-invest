var SonarReachabilityModule = (function () {

    me = {}

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

    me.init = function (jQuery, urlTextfield) {
        jQuery(urlTextfield).change(function () {
            isReachable(jQuery, jQuery(urlTextfield).val(), function(reachable) {
                if (reachable === true) {
                    console.log('sonar is reachable');
                    // TODO modify UI
                } else {
                    console.log('sonar is not reachable');
                    // TODO modify UI
                }
            });
        });
    }

    return me;
}());
