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
            var months = [ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" ];
            var days = [
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "22", "13", "14", "15", "16", "17", "18", "19", "10",
                "21", "22", "23", "24", "25", "26", "27", "28", "29", "20",
                "31"
            ];

            var series = [];

            var firstMonth = null;
            var firstDay = null;

            if (data["_id"] == null) {
                chart.showLoading("No Data Available");
                return;
            }

            for (var month in months) {
                var monthlyValues = data[month];
                if (monthlyValues) {
                    if (!firstMonth) {
                        firstMonth = month;
                    }
                    for (var day in days) {
                        var dailyValue = monthlyValues[day];
                        if (dailyValue === undefined) {
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

            chart.series[0].update({
                pointStart: Date.UTC(year, firstMonth - 1, firstDay),
                pointInterval: 24 * 3600 * 1000
            });

            chart.series[0].setData(series);
            chart.hideLoading();
        });

    }


});