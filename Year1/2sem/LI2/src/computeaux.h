#ifndef COMPUTEAUX_H
#define COMPUTEAUX_H

int operator_unit_type (char token);

void operate (STACK *stk, char *token, char *line_aux, int *bg, DATA *vars, ARRAYSLIST *arys, long *current_array);

int is_to_execute_block (STACK *stk, char *token);

int is_to_execute_in_arr (STACK *stk, char *token);

int is_to_execute_in_str (STACK *stk, char *token);

int is_to_fold (STACK *stk, char *token);

int is_to_filter_array (STACK *stk, char *token);

int is_to_filter_str (STACK *stk, char *token);

int is_to_sort_arr (STACK *stk, char *token);

int is_to_sort_str (STACK *stk, char *token);

int is_to_operate_w (STACK *stk, char *token);

int operator_blocks (STACK *stk, char *token);

int clone_to_token (STACK *stk, char *line, char *token, int N, int *current_pos);

void compute_unit_types (STACK *stk, char* line, int *current_pos, int last_pos_line, char *line_aux, int *bg, DATA *vars, ARRAYSLIST *arys, long *current_array);

int len_block (char *line, int *current_pos);

void read_block (STACK *stk, char *line, int *current_pos);

void compute_aux (STACK *stk, char *line, char *line_aux, int len_line, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array, int *current_pos);

#endif