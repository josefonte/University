#ifndef MANIPULATION_H
#define MANIPULATION_H

#include "stack.h"

int operator_manipulation (char token);

void operator_index (STACK* stk, ARRAYSLIST *arys, long *current_array);

void operator_top (STACK* stk, ARRAYSLIST *arys, long *current_array);

void manipulation (STACK* stk, char token, ARRAYSLIST *arys, long *current_array);

#endif