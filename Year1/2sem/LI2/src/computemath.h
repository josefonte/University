#ifndef COMPUTEMATH_H
#define COMPUTEMATH_H

#include "stack.h"  

int operator_math(char a);

void compute_math_one_operator (STACK *stk, char token);

void arithmetic_operations (STACK *stk, char token, int type, DATA elem1, DATA elem2);

void compute_math_two_operators (STACK *stk, char token);

void compute_math(STACK *sp, char token);

#endif
