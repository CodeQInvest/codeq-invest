var ColorScale = (function () {

    var me = {};

    me.createWithDefaultSize = function(uiElementSelector, colorFunction) {
        var width = 100;
        var height = 10;
        var colorScale = d3.select(uiElementSelector).append("svg")
            .attr("width", width)
            .attr("height", height);

        colorScale
            .selectAll("rect")
            .data(d3.range(100))
            .enter()
            .append("rect")
            .attr("x", function (d) {
                return d;
            })
            .attr("y", 1)
            .attr("width", 1)
            .attr("height", height)
            .attr("fill", colorFunction);
    }

    return me;
})();