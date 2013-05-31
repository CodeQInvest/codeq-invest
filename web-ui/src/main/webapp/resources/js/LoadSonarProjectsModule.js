var LoadSonarProjectsModule = (function () {

    var me = {};

    var attr = {};

    var lastSonarServer;
    var loadedProjects = 0;

    function retrieveProjectsFromSonar(jQuery, connectionSettings, loadedProjectsCallback) {
        jQuery.ajax({
            type: 'put',
            url: '/sonar/projects',
            contentType: 'application/json',
            dataType: 'json',
            processData: false,
            data: JSON.stringify(connectionSettings)
        }).done(function (data) {
            loadedProjectsCallback(data);
        }).fail(function() {
            loadedProjectsCallback([]);
        })
    }

    me.loadProjects = function() {
        var currentSonarServer = attr.getSonarConnectionSettings();
        if (currentSonarServer !== lastSonarServer) {
            lastSonarServer = currentSonarServer;

            if (lastSonarServer === undefined
                || lastSonarServer.url !== currentSonarServer.url
                || loadedProjects === 0) {

                attr.jQuery(attr.loadedProjectsDiv).hide();
                attr.jQuery(attr.loadingProjectsDiv).show();
            }

            retrieveProjectsFromSonar(attr.jQuery, currentSonarServer, function(projects) {
                loadedProjects = projects.length;
                if (projects.length > 0) {
                    var projectOptions = '';
                    jQuery.each(projects, function(index, project) {
                        projectOptions += '<option value="' + project.resourceKey + '">' + project.name + '</option>';
                    });

                    if (projectOptions !== attr.jQuery(attr.projectsSelect).html()) {
                        attr.jQuery(attr.retrievedSonarProjects).val(JSON.stringify(projects));
                        attr.jQuery(attr.projectsSelect).empty().append(projectOptions);
                        attr.jQuery(attr.loadingProjectsDiv).hide();
                        attr.jQuery(attr.loadedProjectsDiv).show();
                    }
                } else {
                    attr.jQuery(attr.retrievedSonarProjects).val('');
                    attr.jQuery(attr.projectsSelect).empty();
                    attr.jQuery(attr.loadingProjectsDiv).hide();
                    attr.jQuery(attr.loadedProjectsDiv).hide();
                }
            });
        }
    }

    me.init = function (jQuery, getSonarConnectionSettings, bindOnElements, projectsSelect, loadingProjectsDiv, loadedProjectsDiv, retrievedSonarProjects) {
        attr.jQuery = jQuery;
        attr.getSonarConnectionSettings = getSonarConnectionSettings,
        attr.projectsSelect = projectsSelect;
        attr.loadingProjectsDiv = loadingProjectsDiv;
        attr.loadedProjectsDiv = loadedProjectsDiv;
        attr.retrievedSonarProjects = retrievedSonarProjects;

        // bind functionality to given ui elements
        jQuery.each(bindOnElements, function(index, value) {
            jQuery(value).change(function () {
                me.loadProjects();
            });
        });
    }

    return me;
}());
