#ifndef ARRAYSOPERATIONS_H
#define ARRAYSOPERATIONS_H

void put_array_in_stack (STACK *stk, ARRAYSLIST *arys);

int is_to_put (STACK *stk, char token);

void concat_array_to_array (STACK *stk, ARRAYSLIST arys);

void concat_array_to_elem (STACK *stk, ARRAYSLIST arys);

void concat_elem_to_array (STACK *stk, ARRAYSLIST arys);

void concat_arrays (STACK *stk, ARRAYSLIST arys);

void concat_string_to_string (STACK *stk);

void concat_string_to_unit_type (STACK *stk);

void concat_unit_type_to_string (STACK *stk);

void concat_strings (STACK *stk);

void concat (STACK *stk, ARRAYSLIST arys);

int is_to_concat (STACK *stk, char token);

void concat_arrays_n_times (STACK *stk, ARRAYSLIST arys, int n_times);

void concat_strings_n_times (STACK *stk, int n_times);

void concat_n_times (STACK *stk, ARRAYSLIST arys);

int is_to_concat_n_times (STACK *stk, char token);

void arrays_length (STACK *stk, ARRAYSLIST arys);

void string_length (STACK *stk);

void operate_comma_length (STACK *stk, ARRAYSLIST arys);

void operate_comma_range (STACK *stk, ARRAYSLIST *arys, long *current_array);

void operate_comma (STACK *stk, ARRAYSLIST *arys, long *current_array);

int is_to_operate_comma (STACK *stk, char token);

void n_elems_init_array (STACK *stk, ARRAYSLIST arys);

void n_chars_init_string (STACK *stk);

void n_inits (STACK *stk, ARRAYSLIST arys);

void n_elems_lst_array (STACK *stk, ARRAYSLIST arys);

void n_chars_lst_string (STACK *stk);

void n_lst (STACK *stk, ARRAYSLIST arys);

void n_elems_init_or_lst (STACK *stk, ARRAYSLIST arys, char token);

int is_to_cut_array_or_str (STACK *stk, char token);

void remove_and_push_fst_array (STACK *stk, ARRAYSLIST arys);

void remove_and_push_fst_string (STACK *stk);

void remove_and_push_fst (STACK *stk, ARRAYSLIST arys);

void remove_and_push_lst_array (STACK *stk, ARRAYSLIST arys);

void remove_and_push_lst_string (STACK *stk);

void remove_and_push_lst (STACK *stk, ARRAYSLIST arys);

void remove_and_push (STACK *stk, ARRAYSLIST arys, char token);

int is_to_remove_and_push (STACK *stk, char token);

void ind_value_array (STACK *stk, ARRAYSLIST *arys);

void ind_value_string (STACK *stk);

void ind_value (STACK *stk, ARRAYSLIST *arys);

int is_to_take_ind (STACK *stk, char token);

void find_ind (STACK *stk);

int is_to_find_ind (STACK *stk, char token);

int is_to_stop (char *str, int bg, char *sub_str);

void separate_aux (char *str, char *sub_str, char *str_dest, int *current_pos);

void separate_in_sub_str (STACK *stk, ARRAYSLIST *arys, long *current_array);

int is_to_separate (STACK *stk, char token);

void separate_by_space (STACK *stk, ARRAYSLIST *arys, long *current_array);

int is_to_separate_by_spaces (STACK *stk, char *token);

void separate_by_n (STACK *stk, ARRAYSLIST *arys, long *current_array);

int is_to_separate_by_n (STACK *stk, char *token);

void command_p (STACK *stk, ARRAYSLIST arys);

int operator_arrays_or_strings (STACK *stk, char *token);

void operate_arrays_or_strings (STACK *stk, ARRAYSLIST *arys, char *token, long *current_array);

#endif