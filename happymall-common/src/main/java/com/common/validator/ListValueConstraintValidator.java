package com.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Yilong
 * @date 2022-10-03 12:43 a.m.
 * @description
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {

    private Set<Integer> values = new HashSet<Integer>();

    /**
     * Initialize method
     * @param constraintAnnotation
     */
    @Override
    public void initialize(ListValue constraintAnnotation) {

        int[] vals = constraintAnnotation.vals();
        for (int val : vals){
            values.add(val);
        }
    }

    /**
     *
     * @param integer Value need to valid
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        return values.contains(integer);
    }
}
