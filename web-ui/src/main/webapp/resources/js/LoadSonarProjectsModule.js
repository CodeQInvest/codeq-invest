var LoadSonarProjectsModule = (function () {

    var me = {};

    var lastSonarServer;
    var loadedProjects = 0;

    function loadProjects(jQuery, connectionSettings, loadedProjectsCallback) {
        jQuery.ajax({
            type: 'put',
            url: '/sonar/projects',
            contentType: 'application/json',
            dataType: 'json',
            processData: false,
            data: '{'
                + '"url": "' + connectionSettings.url + '",'
                + '"username": "' + connectionSettings.username +'",'
                + '"password": "' + connectionSettings.password +'"'
                + '}'
        }).done(function (data) {
            loadedProjectsCallback(data);
        }).fail(function() {
            loadedProjectsCallback([]);
        })
    }

    function bindOnChange(jQuery, uiElement, getSonarConnectionSettings, projectsSelect, loadingProjectsDiv, loadedProjectsDiv, retrievedSonarProjects) {
        jQuery(uiElement).change(function () {
            var currentSonarServer = getSonarConnectionSettings();
            if (currentSonarServer !== lastSonarServer) {
                lastSonarServer = currentSonarServer;

                if (lastSonarServer === undefined || lastSonarServer.url !== currentSonarServer.url || loadedProjects === 0) {
                    jQuery(loadedProjectsDiv).hide();
                    jQuery(loadingProjectsDiv).show();
                }

                loadProjects(jQuery, currentSonarServer, function(projects) {
                    loadedProjects = projects.length;
                    if (projects.length > 0) {
                        var projectOptions = '';
                        jQuery.each(projects, function(index, project) {
                            projectOptions += '<option value="' + project.resourceKey + '">' + project.name + '</option>';
                        });

                        if (projectOptions !== jQuery(projectsSelect).html()) {
                            jQuery(retrievedSonarProjects).val(JSON.stringify(projects));
                            jQuery(projectsSelect).empty().append(projectOptions);
                            jQuery(loadingProjectsDiv).hide();
                            jQuery(loadedProjectsDiv).show();
                        }
                    } else {
                        jQuery(retrievedSonarProjects).val('');
                        jQuery(projectsSelect).empty();
                        jQuery(loadingProjectsDiv).hide();
                        jQuery(loadedProjectsDiv).hide();
                    }
                });
            }
        });
    }

    me.init = function (jQuery, getSonarConnectionSettings, bindOnElements, projectsSelect, loadingProjectsDiv, loadedProjectsDiv, retrievedSonarProjects) {
        // bind functionality to given ui elements
        jQuery.each(bindOnElements, function(index, value) {
            bindOnChange(jQuery, value, getSonarConnectionSettings, projectsSelect, loadingProjectsDiv, loadedProjectsDiv, retrievedSonarProjects);
        });
    }

    return me;
}());
