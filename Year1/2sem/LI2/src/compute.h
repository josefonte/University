#ifndef COMPUTE_H
#define COMPUTE_H

#include "stack.h"

int data_compare (DATA elem1, DATA elem2);

void swap_stack (STACK *stk, int a, int b);

int bubble_array (STACK *after_compute, STACK *before_compute, int len);

void bubble_sort_array (STACK *after_compute, STACK *before_compute);

void swap_str (char *str, int a, int b);

int bubble_string (STACK *stk_after_compute, char *str_before, int len);

void bubble_sort_string (STACK *stk_after_compute, char *str_before);

void compute_all_types_execute_block (STACK *stk, int *current_pos, char *line_aux, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array);

void compute_all_types_map_array (STACK *stk, int *current_pos, char *line_aux, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array);

void compute_all_types_map_string (STACK *stk, int *current_pos, char *line_aux, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array);

void compute_all_types_fold (STACK *stk, int *current_pos, char *line_aux, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array);

void compute_all_types_filter_array (STACK *stk, int *current_pos, char *line_aux, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array);

void compute_all_types_filter_string (STACK *stk, int *current_pos, char *line_aux, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array);

void compute_all_types_sort_on_array (STACK *stk, int *current_pos, char *line_aux, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array);

void compute_all_types_sort_on_string (STACK *stk, int *current_pos, char *line_aux, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array);

void compute_all_types_do_while (STACK *stk, int *current_pos, char *line_aux, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array);

void compute_all_types (STACK *stk, char *line, char *line_aux, int len_line, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array);

void compute (char* line);

#endif
