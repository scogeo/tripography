define("charts/daily-histogram", ['jquery', 'highcharts'], function($, Highcharts) {

    return function (chartElem) {

        var chart = new Highcharts.Chart({
            chart: {
                renderTo: chartElem,
                type: 'column',
                marginBottom: 40,
                marginRight: 30,
                borderRadius: 0
            },
            title: {
                text: 'Daily Driving Distance'
            },
            subtitle: {
                text: 'Distribution'
            },
            credits: {
                text: "tripography.com"
            },
            xAxis: {
                allowDecimals: false,
                type: 'linear',
                min: 0,
                max: 200,
                title: {
                    text: 'Distance (miles)'
                }
            },
            yAxis: {
                allowDecimals: false,
                title: {
                    text: '# of Days'
                },
                min: 0,
                minRange: 1,
                minTickInterval: 0,
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
                formatter: function() {

                    var days = this.y == 1 ? ' day' : ' days';

                    var result = '';

                    if (this.x >= 200) {
                        result += 'Distance: greater than <b>' + this.x + '</b> miles.';
                    }
                    else {
                        result += 'Distance: between <b>' + this.x + '</b> and <b>' + (this.x + 1) + '</b> miles.';
                    }
                    result += '<br/>Days Driven: <b>' + this.y + '</b>';

                    return result;
                }

            },
            legend: {
                enabled: false
            },
            plotOptions: {
                column: {
                    pointPadding: 0,
                    groupPadding: 0,
                    borderWidth: 0
                }
            },
            series: [
                {
                    showInLegend: false,
                    name: "Count",
                    data: []
                }]
        });

        $(chartElem).data("chart", chart);
        $(window).resize(function() {
            $(chartElem).data("chart").setSize($(chartElem).width(), $(chartElem).height(), false);
        });
        chart.showLoading();

        var vehicleId = $(chartElem).attr('data-vehicle-id');

        if (vehicleId == null) {
            chart.showLoading("No Data Available");
            return;
        }

        $.getJSON("/data/charts/" + vehicleId + "/daily-histogram.json", function(data) {

            if (data["_id"] == null) {
                chart.showLoading("No Data Available");
                return;
            }

            var bins = data["b"];
            var series = [];
            var maxBins = 200;

            if (bins) {
                for (var i = 0; i <= maxBins; i++) {
                    var value = parseInt(bins[i]);
                    if (value) {
                        series.push(i, value);
                    }
                }
            }

            chart.series[0].setData(series);
            chart.hideLoading();
        });

    }


});
