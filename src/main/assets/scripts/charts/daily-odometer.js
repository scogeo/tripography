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
                    marker: {
                        enabled: false
                    },
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

            var startDate = data.startDate;

            if (startDate === undefined) {
                chart.showLoading("No Data Available");
                return;
            }

            chart.series[0].update({
                pointStart: Date.UTC(startDate.year, startDate.month - 1, startDate.day),
                pointInterval: 24 * 3600 * 1000
            });

            chart.series[0].setData(data.values);
            chart.hideLoading();
        });

    }


});