requirejs.config({
    paths:{
        highcharts: 'highcharts/highcharts'
    },

    shim: {
        highcharts: {
            exports: "Highcharts"
        }
    }
});
