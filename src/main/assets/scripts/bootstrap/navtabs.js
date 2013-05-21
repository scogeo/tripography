define("bootstrap/navtabs", ['jquery'], function($) {
    return function(ulNav) {
        $(ulNav).find('a').on('shown', function (e) {
            var chartElem = $($(e.target).attr('href'));
            var chart = $(chartElem).data("chart");
            if (chart) {
                chart.setSize($(chartElem).width(), $(chartElem).height(), false);
                //chart.redraw();
            }
        });
    }
});