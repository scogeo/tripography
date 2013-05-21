define("charts/daily-histogram", ['jquery', 'highcharts'], function($, Highcharts) {

    return function (chartElem) {

        var vehicleId = $(chartElem).attr('data-vehicle-id');

        if (vehicleId == null) {
            alert("oop no vehicle id");
            return null;
        }

        var chart = new Highcharts.Chart({
            chart: {
                renderTo: chartElem,
                type: 'column',
                marginBottom: 25
            },
            title: {
                text: 'Distance (Histogram)'
                //x: -20 //center
            },
            xAxis: {
                type: 'linear'
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
                valueSuffix: 'miles'

            },
            legend: {
                enabled: false
            },
            plotOptions: {
                column: {
                    pointPadding: 0,
                    borderWidth: 0
                }
            },
            series: [
                {
                    showInLegend: false,
                    //name: "Daily Mileage",
                    data: []
                }]
        });

        $(chartElem).data("chart", chart);

        chart.showLoading();

        $.getJSON("/data/charts/" + vehicleId + "/daily-histogram.json", function(data) {

            var series = [ [ 0, 3], [1, 5], [20, 3]];

            chart.series[0].setData(series);
            chart.hideLoading();
        });

    }


});
