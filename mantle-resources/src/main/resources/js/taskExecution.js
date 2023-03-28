/*
* For usage and bookmarklet installation, see https://confluence.corp.about-inc.com/display/TECH/Globe+Task+Execution+Bookmarklet
*/
(function($, Mntl) {
    var $container = $('<div id="mntltaskexecution"></div>');
    var width = window.innerWidth - 30;
    var maxHeight = window.innerHeight - 35;
    var $body = $('body');
    var $close = $('<div class="close">&times;</div>');
    var script = document.createElement('script');
    var $style;
    var resourceUrl = Mntl.domUtilities.getResourceRootUrl();
    var staticPath = Mntl.utilities.getStaticPath();

    $style = $('<link rel="stylesheet" href="' + resourceUrl + staticPath + '/css/taskExecution.min.css" type="text/css" />');

    script.type = 'text/javascript';
    // Update d3 as versions come out.
    script.src = 'https://cdnjs.cloudflare.com/ajax/libs/d3/5.14.2/d3.min.js';

    function cleanData(data) {
        var cleanedData = [];
        var startOfExecutions = data[0].startTime;

        // Filter out tasks with both start and end times of 0
        data.forEach(function(task) {
            task.startTime = task.startTime - startOfExecutions;
            task.endTime = task.endTime - startOfExecutions;
            task.duration = task.endTime - task.startTime;
            cleanedData.push(task);
        });

        return cleanedData;
    }

    function sortAndFilterData(data) {
        data = data.filter(function(task) {
            return task.endTime > 0 && task.startTime > 0;
        });

        data.sort(function(a, b) {
            // Compare start times
            if (a.startTime < b.startTime) {
                return -1;
            }
            if (a.startTime > b.startTime) {
                return 1;
            }
            // Start times are equal, compare end times
            if (a.endTime < b.endTime) {
                return -1;
            }
            if (a.endTime > b.endTime) {
                return 1;
            }

            // Start and end times are equal
            return 0;
        });

        return data;
    }

    function graphData(data) {
        var taskEndTime;
        var maxData;
        var svgHeight = (data.length * 30) + 70;
        var svgWidth = width - 80;
        var numTicks = 6;
        var barHeight = 30;
        var leftMargin = 60;
        var lineHeight;
        var chart;
        var tooltip;
        var xScale;
        var innerChart;
        var yAxis;

        taskEndTime = d3.max(data, function(d) {
            return d.endTime;
        });

        maxData = taskEndTime + 20;
        lineHeight = data.length * barHeight;

        // Main svg wrapper
        chart = d3.select('#mntltaskexecution')
            .append('svg')
            .attr('class', 'chart')
            .attr('width', svgWidth)
            .attr('height', svgHeight);

        tooltip = d3.select('#mntltaskexecution')
            .append('div')
            .attr('class', 'perf-tooltip')
            .style('opacity', 0);

        xScale = d3
            .scaleLinear()
            .domain([0, maxData])
            .range(['5px', svgWidth - maxData + 'px']);

        // Chart title
        chart.append('g')
            .attr('transform', 'translate(' + (svgWidth * 0.33) + ',30)')
            .attr('class', 'chart-title')
            .append('text')
            .text('Globe Task Execution Timing (' + taskEndTime + 'ms)');

        // Inner content of chart
        innerChart = chart.append('g')
            .attr('transform', 'translate(' + leftMargin + ',30)')
            .attr('class', 'main-graph');

        // data bars for tasks
        innerChart.append('g')
            .attr('transform', 'translate(' + leftMargin + ',30)')
            .selectAll('rect')
            .data(data)
            .enter()
            .append('rect')
            .attr('class', function(d) {
                var className = 'taskbar';

                if (d.exception) {
                    className += ' exception';
                }

                return className;
            })
            .attr('x', function(d) {
                return xScale(d.endTime - d.duration);
            })
            .attr('y', function(d, i) {
                return i * barHeight;
            })
            .attr('height', barHeight)
            .attr('width', 0)
            .on('mouseover', function(d, i) {
                var tooltipHtml;
                var overlayScrollOffset;
                var bodyScrollOffset;
                var tipX;
                var tipY;

                d3.select(this).style('opacity', 0.2);
                tooltip.transition()
                    .duration(200)
                    .style('opacity', 0.9);
                tooltipHtml = '<strong>' + d.id + '</strong> (' + d.duration + 'ms)<br />'
                    + 'Start: ' + d.startTime + '<br />End: ' + d.endTime
                    + '<br />' + d.uniqueId;
                if (d.exception) {
                    tooltipHtml += '<br />Exception: ' + d.exception;
                }

                overlayScrollOffset = $container.scrollTop();
                bodyScrollOffset = $('body').scrollTop();
                tipX = i > data.length - 5 ? d3.event.pageX - 150 : d3.event.pageX;
                tipY = i > data.length - 5 ? d3.event.pageY + overlayScrollOffset - bodyScrollOffset - 200 : d3.event.pageY + overlayScrollOffset - bodyScrollOffset - 30;

                tooltip.html(tooltipHtml)
                    .style('left', (tipX) + 'px')
                    .style('top', (tipY) + 'px');
            })
            .on('mouseout', function() {
                d3.selectAll('.taskbar')
                    .style('opacity', 1);
                tooltip.transition()
                    .duration(500)
                    .style('opacity', 0);
            })
            .transition()
            .duration(1000)
            .attr('width', function(d) {
                return xScale(d.duration);
            });


        // Titles for each task-bar
        innerChart.append('g')
            .attr('transform', 'translate(' + leftMargin + ',30)')
            .selectAll('text')
            .data(data)
            .enter()
            .append('text')
            .attr('class', 'bar')
            .attr('x', function(d) {
                return xScale(d.endTime);
            })
            .attr('y', function(d, i) {
                return (i * barHeight) + (barHeight * 0.5);
            })
            .attr('dx', 5)
            .attr('dy', '0')
            .attr('text-anchor', 'start')
            .text(function(d) {
                return (d.id + '(' + d.duration + 'ms)');
            });

        // Vertical lines to mark time along X axis
        innerChart.append('g')
            .attr('transform', 'translate(' + leftMargin + ',30)')
            .selectAll('line')
            .data(xScale.ticks(numTicks))
            .enter()
            .append('line')
            .attr('class', 'xAxisLine')
            .attr('x1', xScale)
            .attr('x2', xScale)
            .attr('y1', 0)
            .attr('y2', 0)
            .transition()
            .duration(1500)
            .attr('y2', lineHeight);

        // Text for vertical lines marking time along X axis
        innerChart.append('g')
            .attr('transform', 'translate(' + leftMargin + ',30)')
            .selectAll('.rule')
            .data(xScale.ticks(numTicks))
            .enter()
            .append('text')
            .attr('class', 'rule')
            .attr('x', xScale)
            .attr('y', 0)
            .attr('dy', -3)
            .attr('text-anchor', 'middle')
            .text(String);

        // Y axis container
        yAxis = innerChart.append('g')
            .attr('class', 'yAxis');

        // Y axis line
        yAxis.append('line')
            .attr('class', 'yAxisLine')
            .attr('x1', leftMargin)
            .attr('x2', leftMargin)
            .attr('y1', 30)
            .attr('y2', 30 + lineHeight);

        // Y axis labels
        yAxis.selectAll('text')
            .data(data)
            .enter()
            .append('text')
            .attr('x', leftMargin - 10)
            .attr('y', function(d, i) {
                return (i * barHeight) + (barHeight * 0.8);
            })
            .attr('dy', '20')
            .attr('text-anchor', 'end')
            .text(function(d) {
                return (d.id);
            });
    }

    function getData(url) {
        $.ajax({
            url: resourceUrl + '/debug/modeltimings.json?url=' + url,
            dataType: 'json',
            xhrFields: { withCredentials: true }
        })
            .done(function(data) {
                data = sortAndFilterData(data);
                data = cleanData(data);
                graphData(data);
            })
            .fail(function() {
                $container.html('There was an error getting the data.');
            });
    }

    // Append container
    $body.append($style);
    $container.append($close);
    $container.css({
        width: width,
        maxHeight: maxHeight
    });
    $body.append($container);

    // Get task data once d3 is loaded
    script.onload = function() {
        getData(location.href);
    };

    document.body.appendChild(script);

    // Allow for removal
    $close.on('click', function() {
        $container.remove();
    });
}(window.jQuery || {}, window.Mntl || {}));
