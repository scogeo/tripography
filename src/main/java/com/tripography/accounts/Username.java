package com.tripography.accounts;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = {})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@NotEmpty(message = "{com.fitunity.accounts.Username.NotEmpty.message}" )
@Pattern(regexp = "[" + Username.ATOM + "0-9_]*", flags = Pattern.Flag.CASE_INSENSITIVE, message = "{com.fitunity.accounts.Username.Pattern.message}")
@Size(max = 30, message = "{com.fitunity.accounts.Username.Size.message}")
public @interface Username {
    String message() default "{com.fitunity.accounts.Username.message}";

    public static final String ATOM = "a-z";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
