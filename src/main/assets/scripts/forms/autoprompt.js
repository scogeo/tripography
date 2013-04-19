define("forms/autoprompt", ['jquery'], function($) {

    return function (formElem) {

        var settings = {
            // CSS class used for the span element
            promptClass : 'prompt',
            // CSS class added to the span element when it has focus, can be used to highlight/lighten text
            promptFocusClass : 'focused'
        };

        function showOrHidePrompt(prompt, input) {
            var isVisible = prompt.data('inputprompt-visible');
            if (input.val().length === 0) {
                if (!isVisible) {
                    prompt.show();
                    prompt.data('inputprompt-visible', true);
                }
            }
            else {
                if (isVisible) {
                    prompt.hide();
                    prompt.data('inputprompt-visible', false);
                }
            }
        }

        function createPrompt(input) {

            // create a span
            var prompt = $('<span class="' + settings.promptClass + '"/>');
            prompt.append(input.attr('data-prompt'));
            prompt.data("inputprompt-visible", false);

            input.after(prompt);

            input.bind('keyup', function() {
                showOrHidePrompt(prompt, $(this));
            });

            // keypress needed to quickly hide the prompt
            input.bind('keydown', function(event) {
                if (event.keyCode > 46) {
                    var isVisible = prompt.data('inputprompt-visible');
                    if (isVisible) {
                        prompt.hide();
                        prompt.data('inputprompt-visible', false);
                    }
                }
            });

            $(prompt).click(function() {
                showOrHidePrompt($(this), input);
                input.focus();
            });

            // show the prompt now if needed
            showOrHidePrompt(prompt, input);
        }

        $(formElem).each(function() {
            $(formElem).find('div.prompting-input input[type=text][data-prompt],input[type=password][data-prompt]').each(function() {
                createPrompt($(this));
            });
        });

    };

});