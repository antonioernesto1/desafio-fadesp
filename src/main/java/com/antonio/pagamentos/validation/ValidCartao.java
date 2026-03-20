package com.antonio.pagamentos.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidCartaoValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCartao {
    String message() default "Número do cartão é obrigatório para pagamentos com cartão de crédito ou débito.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
