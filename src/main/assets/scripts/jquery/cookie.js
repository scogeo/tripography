/*jshint eqnull:true */
/*!
 * jQuery Cookie Plugin v1.2
 * https://github.com/carhartl/jquery-cookie
 *
 * Copyright 2011, Klaus Hartl
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.opensource.org/licenses/GPL-2.0
 */
define("jquery/cookie", ["jquery"], function($) {
    (function ($, document, undefined) {

        var pluses = /\+/g;

        function raw(s) {
            return s;
        }

        function decoded(s) {
            return decodeURIComponent(s.replace(pluses, ' '));
        }

        var config = $.cookie = function (key, value, options) {

            // write
            if (value !== undefined) {
                options = $.extend({}, config.defaults, options);

                if (value === null) {
                    options.expires = -1;
                }

                if (typeof options.expires === 'number') {
                    var days = options.expires, t = options.expires = new Date();
                    t.setDate(t.getDate() + days);
                }

                value = config.json ? JSON.stringify(value) : String(value);

                return (document.cookie = [
                    encodeURIComponent(key), '=', config.raw ? value : encodeURIComponent(value),
                    options.expires ? '; expires=' + options.expires.toUTCString() : '', // use expires attribute, max-age is not supported by IE
                    options.path    ? '; path=' + options.path : '',
                    options.domain  ? '; domain=' + options.domain : '',
                    options.secure  ? '; secure' : ''
                ].join(''));
            }

            // read
            var decode = config.raw ? raw : decoded;
            var cookies = document.cookie.split('; ');
            for (var i = 0, parts; (parts = cookies[i] && cookies[i].split('=')); i++) {
                if (decode(parts.shift()) === key) {
                    var cookie = decode(parts.join('='));
                    return config.json ? JSON.parse(cookie) : cookie;
                }
            }

            return null;
        };

        config.defaults = {};

        $.removeCookie = function (key, options) {
            if ($.cookie(key, options) !== null) {
                $.cookie(key, null, options);
                return true;
            }
            return false;
        };

    })(jQuery, document);
});