var LoadSonarProjectsModule = (function () {

    var me = {};

    var lastSonarServerUrl;

    function loadProjects(jQuery, sonarUrl, loadedProjectsCallback) {
        jQuery.ajax({
            type: 'put',
            url: '/sonar/projects',
            contentType: 'application/json',
            dataType: 'json',
            processData: false,
            data: '{ "url": "' + sonarUrl + '"}'
        }).done(function (data) {
            loadedProjectsCallback(data);
        }).fail(function() {
            loadedProjectsCallback([]);
        })
    }

    function bindOnChange(jQuery, uiElement, getSonarServerUrl, projectsSelect, loadingProjectsDiv, loadedProjectsDiv) {
        jQuery(uiElement).change(function () {
            var currentSonarServer = getSonarServerUrl();
            if (currentSonarServer !== lastSonarServerUrl) {
                lastSonarServerUrl = currentSonarServer;

                jQuery(loadedProjectsDiv).hide();
                jQuery(loadingProjectsDiv).show();

                loadProjects(jQuery, currentSonarServer, function(projects) {
                    if (projects.length > 0) {
                        var projectOptions = "";
                        jQuery.each(projects, function(index, project) {
                            projectOptions += '<option value="' + project.resourceKey + '">' + project.name + '</option>';
                        });
                        jQuery(projectsSelect).empty().append(projectOptions);

                        jQuery(loadingProjectsDiv).hide();
                        jQuery(loadedProjectsDiv).show();
                    } else {
                        jQuery(loadingProjectsDiv).hide();
                        jQuery(loadedProjectsDiv).hide();
                    }
                });
            }
        });
    }

    me.init = function (jQuery, getSonarServerUrl, bindOnElements, projectsSelect, loadingProjectsDiv, loadedProjectsDiv) {
        // bind functionality to given ui elements
        jQuery.each(bindOnElements, function(index, value) {
            bindOnChange(jQuery, value, getSonarServerUrl, projectsSelect, loadingProjectsDiv, loadedProjectsDiv);
        });
    }

    return me;
}());
