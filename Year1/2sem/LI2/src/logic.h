
#ifndef LOGIC_H
#define LOGIC_H

#include "stack.h"

int logical_operator (char token);

char operator_e (char *token);

void assert_double (DATA *elem);

void assert_string (DATA *elem);

void assert_type_aux (DATA *elem, TYPE t);

void assert_type (DATA *elem1, DATA *elem2);

int is_true (DATA elem);

void lower (STACK *stk, DATA elem1, DATA elem2);

void higher (STACK *stk, DATA elem1, DATA elem2);

void operate_e (STACK *stk, char *token);

void operate_logic_two_operator_equal (STACK *stk);

void operate_logic_two_operator_less (STACK *stk);

void operate_logic_two_operator_more (STACK *stk);

void operate_logic_two_operator (STACK *stk, char *token);

void logical_operations (STACK *stk, char *token, ARRAYSLIST arys);

#endif
