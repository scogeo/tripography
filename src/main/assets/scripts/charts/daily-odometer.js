define("charts/daily-odometer", ['jquery', 'highcharts'], function($, Highcharts) {

    return function (chartElem) {

        var chart = new Highcharts.Chart({
            chart: {
                renderTo: chartElem,
                type: 'area',
                marginRight: 30,
                marginBottom: 50,
                borderRadius: 0
            },
            credits: {
                text: "tripography.com"
            },
            title: {
                text: 'Distance Driven'
            },
            subtitle: {
                text: 'Per Day'
            },
            xAxis: {
                type: 'datetime',
                minTickInterval: 24 * 3600 * 1000,
                offset: 0,
                title: {
                    text: 'Date'
                }
            },
            yAxis: {
                title: {
                    text: 'Distance (miles)'
                },
                min: 0,

                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
                valueSuffix: ' miles'

            },
            legend: {
                enabled: false
            },
            series: [
                {
                    name: "Distance",
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

        $.getJSON("/data/charts/" + vehicleId + "/daily.json", function(data) {
            var year = "2013";

            var series = [];

            var firstMonth = null;
            var firstDay = null;

            if (data["_id"] == null) {
                chart.showLoading("No Data Available");
                return;
            }

            for (var month = 1; month <= 12; month++) {
                var monthlyValues = data[month];
                if (monthlyValues) {
                    if (!firstMonth) {
                        firstMonth = month;
                    }
                    for (var day = 1; day <= 31; day++) {
                        var dailyValue = monthlyValues[day];
                        if (dailyValue === undefined) {
                            if (firstDay) {
                                series.push(null);
                            }
                            continue;
                        }
                        if (dailyValue >= 0.0) {
                            if (!firstDay) {
                                firstDay = day;
                            }
                            series.push(dailyValue);
                        }
                        else {
                            if (firstDay) {
                                // first day defined
                                series.push(null);
                            }
                        }

                    }
                }
            }

            for (var i = series.length - 1; i >= 0; i--) {
                if (series[i] != null) {
                    break;
                }
            }

            series = series.slice(0, i + 1);

            chart.series[0].update({
                pointStart: Date.UTC(year, firstMonth - 1, firstDay),
                pointInterval: 24 * 3600 * 1000
            });

            chart.series[0].setData(series);
            chart.hideLoading();
        });

    }


});