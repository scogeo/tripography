define("app/boot", ["jquery"], function($) {

    // dynamically load script and apply to element that has the data-require-elem attribute.
    $('[data-require-elem]').each(function() {
        var elem = this;
        require([$(this).attr('data-require-elem')], function(callback) {
            if (typeof callback === 'function') {
                callback(elem);
            }
        });
    });

});