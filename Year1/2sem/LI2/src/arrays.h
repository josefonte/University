#ifndef ARRAYS_H
#define ARRAYS_H

#include "stack.h"

void push_by_type (STACK *stk, char *token);

int len_string (char *line, int *current_pos);

void read_string (char *line, int start, int stop, char *string_dest);

int last_pos_token (char *line, int init);

ARRAYSLIST create_arrays ();

int array_len (char *line, int *current_pos);

void get_array (char *line, char *array_dest, int init_pos, int last_pos);

void add_array (STACK *stk, ARRAYSLIST *arys_list, STACK *array_to_include, long *current_array);

#endif