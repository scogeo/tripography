define("forms/cookie-input", ["jquery", "jquery/cookie"], function($) {
   return function(input) {
       $(input).prop('checked', $.cookie($(input).data('cookie')) === 'true');
       $(input).change(function() {
           $.cookie($(input).data('cookie'), $(this).is(':checked') ? 'true' : 'false', { expires: 365, path: '/' });
       });
   }
});