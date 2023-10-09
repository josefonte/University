#ifndef VARIABLES_H
#define VARIABLES_H

int is_var (char token);

DATA *create_vars();

void change_value_var (STACK *stk, DATA *vars, char name, ARRAYSLIST *arys, long *current_array);

DATA find_value_var (STACK *stk, ARRAYSLIST *arys, DATA *vars, char name, long *current_array);

void operate_var (STACK *stk, char *token, DATA *vars, ARRAYSLIST *arys, long *current_array);

#endif