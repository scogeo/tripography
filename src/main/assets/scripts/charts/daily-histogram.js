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
                marginBottom: 40,
                marginRight: 30,
                borderRadius: 0
            },
            title: {
                text: 'Distance (Distribution)'
            },
            credits: {
                enabled: false
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
                    text: 'Days'
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
                    //name: "Daily Mileage",
                    data: []
                },
                {
                    name:'Curve',
                    type:'spline',
                    visible:true,
                    data: []
                    //color: 'rgba(204,204,255,.85)'
                }]
        });

        $(chartElem).data("chart", chart);
        $(chartElem).resize(function() {
            var chart = $(chartElem).data("chart");
            chart.setSize($(chartElem).width(), $(chartElem).height(), false);
        });
        chart.showLoading();

        $.getJSON("/data/charts/" + vehicleId + "/daily-histogram.json", function(data) {

            var bins = data["b"];

            var series = [];

            if (bins) {
                for (var distance in bins) {
                    series.push([parseInt(distance), parseInt(bins[distance])]);
                }
            }

            chart.series[0].setData(series);
            //chart.series[1].setData(series);
            chart.hideLoading();
        });

    }


});
