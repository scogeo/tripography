define("forms/validate", ["jquery"], function($) {
    (function($) {

        var Validator = function() {

            var rules = {
                notEmpty : {
                    validate : function(value, context, params) {
                        if (value) {
                            return true;
                        }
                        return false;
                    },
                    message : "This field is required"
                }
            }
        }

        var ValidatedForm = function(form) {
            var fields = [];
            console.log("Building a from for " + form);

            form.find("input[data-validation]").each(function() {
                var field = $(this);
                console.log('found a field ' + field);
                fields.push(new ValidatedField(field));
            });

            this.fields = fields;
        }

        ValidatedForm.prototype = {
            validate : function() {
                console.log("Validating a form");
                for(field in this.fields) {
                    this.fields[field].validate();
                }
            },
            isValid : function() {
                for(field in this.fields) {
                    if(!this.fields[field].valid) {
                        this.fields[field].field.focus();
                        return false;
                    }
                }
                return true;
            }
        }

        var ValidatedField = function(field) {
            this.field = field;
            this.valid = false;

            // parse validate field
            var ruleList = field.attr("data-validation");

            // Match patterns like
            // a, b, c
            // or
            // a(13, 12), b("hello"), c("jdjfdie")
            // or
            // a b c
            // if rule contains arguments, they are parsed as if they were contained in a JSON array

            var rulePattern = /[a-z]+(\(([^\)]*)\))?/gi;

            var rule;
            while ((rule = rulePattern.exec(ruleList)) != null) {
                console.log("found a rule " + rule[0]);
                var args;
                if (rule[2] !== undefined) {
                    var args = $.parseJSON("[" + rule[2] + "]");
                    console.log("found arguments " +  rule[2]);
                }
            }


            //this.attach("change");
        }

        ValidatedField.prototype = {

            validate : function() {
                console.log("Calling validate on " + this.field);
            }
        }

        $.extend($.fn, {

            validate : function(options) {
                var validator = $(this).data("validator");
                validator && validator.validate();
                return this;
            },

            installValidation : function(options) {
                var settings = {
                    validateAttribute : "data-validate"
                };

                // adjust the settings
                if (options) {
                    $.extend(settings, options);
                }

                $(this).data("validator", new ValidatedForm($(this)));

            }
        });

    })(jQuery);
});