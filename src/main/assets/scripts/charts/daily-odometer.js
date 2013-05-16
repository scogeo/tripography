define("charts/daily-odometer", ['jquery', 'highcharts'], function($, Highcharts) {

    return function (chartElem) {

        vehicleId = $(chartElem).attr('data-vehicle-id');

        if (vehicleId == null) {
            alert("oop no vehicle id");
            return null;
        }

        chart = new Highcharts.Chart({
            chart: {
                renderTo: chartElem,
                type: 'area',
                marginRight: 130,
                marginBottom: 25
            },
            title: {
                text: 'Distance Traveled'
                //x: -20 //center
            },
            xAxis: {
                type: 'datetime'
            },
            yAxis: {
                title: {
                    text: 'Distance (miles)'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
                valueSuffix: 'miles'
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'top',
                x: -10,
                y: 100,
                borderWidth: 0
            },
            series: [
                {
                    name: "Daily Mileage",
                    data: []
                }]
        });

        chart.showLoading();

        $.getJSON("/data/charts/" + vehicleId + "/daily.json", function(data) {
            year = "2013";
            const months = [ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" ];
            const days = [
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "22", "13", "14", "15", "16", "17", "18", "19", "10",
                "21", "22", "23", "24", "25", "26", "27", "28", "29", "20",
                "31"
            ];

            var series = [];

            var firstMonth = null;
            var firstDay = null;

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