/**
 * @file Ficheiro que inclui as funções mais globais de calculos sobre a **stack**
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "compute.h"
#include "computeaux.h"
#include "variables.h"
#include "arrays.h"
#include "logic.h"
#include "arraysoperations.h"
#include "computemath.h"
#include "manipulation.h"

/**
 @file compute.c
 \brief Ficheiro que inclui as funções mais globais de calculos sobre a **stack**
 */

/*!
 * Função que coloca os tipos de duas variáveis do tipo **DATA** iguais e compara os valores das mesmas.
 * @param elem1 Primeiro elemento do tipo **DATA**;
 * @param elem2 Segundo elemento do tipo **DATA**.
 * @returns >0, se o primeiro elemento foi maior do que o segundo;
 * @returns 0, se o primeiro elemento for igual ao segundo;
 * @returns <0, se o primeiro elemento foi meno do que o segundo;
 */
int data_compare (DATA elem1, DATA elem2) {

    assert_type (&elem1, &elem2);

    switch (elem1.type) {

        case LONG:

            if (elem1.LONG > elem2.LONG) return 1;

            if (elem1.LONG < elem2.LONG) return -1;

            return 0;
        
        case DOUBLE: 

            if (elem1.DOUBLE > elem2.DOUBLE) return 1;

            if (elem1.DOUBLE < elem2.DOUBLE) return -1;

            return 0;
        
        case CHAR:

            if (elem1.CHAR > elem2.CHAR) return 1;

            if (elem1.CHAR < elem2.CHAR) return -1;

            return 0;
        
        case STRING:

            return strcmp (elem1.STRING, elem2.STRING);
    }

    return 0; // nunca chega ao final
}

/*!
 * Função que troca a posição de dois elementos de uma **stack** e usada como auxiliar na função bubble_array().
 * @param stk Endereço da **stack** a ser utilizada;
 * @param a índice de uma posição da **stack**;
 * @param b índice de uma outra posição da **stack**.
 */
void swap_stack (STACK *stk, int a, int b) {

    DATA aux = stk->stack[a];
    
    stk->stack[a] = stk->stack[b];

    stk->stack[b] = aux;
}

/*!
 * Função que faz uma travessia de uma **stack** após cada elemento ter sido alterado pelo bloco de instrução na função compute_all_types() e, 
 * quando um elemento é maior (segundo a função data_compare() ) do que o próximo, troca o conteúdo dessa posição com a seguinte na **stack** com os valores 
 * alterados e na **stack** original antes do compute_all_types ser executado a todos os elementos.
 * @param after_compute Endereço da **stack** com todos os elementos da **stack** inicial alterados por um bloco de instrução;
 * @param before_compute Endreço da **stack** original;
 * @param len Última posição dos **arrays** a que ele vai chegar.
 * @returns número de vezes que foi executada a função swap_stack(). 
 */
int bubble_array (STACK *after_compute, STACK *before_compute, int len) {

    int i, r = 0;

    for (i = 0; i < len - 1; i++) 

        if (data_compare (after_compute->stack[i], after_compute->stack[i + 1]) > 0) {
           
            swap_stack (after_compute, i, i+1);

            swap_stack (before_compute, i, i+1);

            r++;
        }
    
    return r;
}

/*!
 * Função que ordena um **array** pelo algoritmo *bubble sort*. Ou seja, aplica repetidamente a função bubble_array() até o **array** estar ordenado 
 * (não fazer *swaps* numa travessia). É declarada uma variável **len** que será passada como input à função bubble_array() e decrementada em cada iteração 
 * pois, com o algoritmo *bubble sort, o mais elemento fica sempre na última posição. 
 * @param after_compute Endereço da **stack** antes de cada elemento ter sofrido alteração.
 * @param before_compute Endereço da **stack** após cada elemento ter sofrido alteração.
 */
void bubble_sort_array (STACK *after_compute, STACK *before_compute) {

    int len = before_compute->n_elems;

    while (bubble_array (after_compute, before_compute, len)) len--;
}

/*!
 * Função que troca a posição de dois caracteres de uma **string** e usada como auxiliar na função bubble_string().
 * @param str **string** com duas posições para serem trocadas;
 * @param a índice de uma posição da **string**;
 * @param b índice de uma outra posição da **string**.
 */
void swap_str (char *str, int a, int b) {

    char aux = str[a];

    str[a] = str[b];

    str[b] = aux;
}
/*!
 * Função que faz uma travessia de uma **stack** após cada elemento ter sido alterado pelo bloco de instrução na função compute_all_types() e, 
 * quando um elemento é maior (segundo a função data_compare() ) do que o próximo, troca o conteúdo dessa posição com a seguinte na **stack** com os valores 
 * alterados e na **string** original antes do compute_all_types ser executado a todos os elementos.
 * @param after_compute Endereço da **stack** com todos os elementos da **stack** inicial alterados por um bloco de instrução;
 * @param str_before **String** para ordenar segundo os elementos da **stack** *after_compute*;
 * @param len Última posição do **arrays** (e da **string**) a que ele vai chegar.
 * @returns número de vezes que foi executada a função swap_stack() e swap_string(). 
 */
int bubble_string (STACK *stk_after_compute, char *str_before, int len) {

    int i, r = 0;

    for (i = 0; i < len - 1; i++)

        if (data_compare (stk_after_compute->stack[i], stk_after_compute->stack[i + 1]) > 0) {

            swap_stack (stk_after_compute, i, i + 1);

            swap_str (str_before, i, i + 1);

            r++;
        }
    
    return r;
}

/*!
 * Função que, tal como a bubble_sort_array, ordena segundo o algoritmo *bubble sort*. Porém esta usa como função auxiliar a função bubble_string().
 * @param after_compute Endereço da **stack** antes de cada elemento ter sofrido alteração.
 * @param str_before **String** com os elementos originais.
 */
void bubble_sort_string (STACK *stk_after_compute, char *str_before) {

    int len = strlen(str_before);

    while (bubble_string (stk_after_compute, str_before, len)) len--;
}

/*!
 * Função responsável por executar um bloco. Ou seja, aplicá-lo à **stack** como se se trasse de um comando.
 * @param stk Endereço da **stack** a ser usada;
 * @param current_pos Endereço do valor do índice atual a ser lido na linha de comando recebida;
 * @param line_aux **String** com todos os caracteres digitados abaixo da primeira linha;
 * @param bg Endereço do valor do índice atual a ser lido na **line_aux**;
 * @param arrays_list Endereço do endereço da lista de **arrays**;
 * @param vars *Array** de **DATAs** com a informação das variáveis;
 * @param current_array Endereço do valor numérico currespondente ao número de **arrays**.
 */
void compute_all_types_execute_block (STACK *stk, int *current_pos, char *line_aux, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array) {
    
    DATA block = POP (stk);

    compute_all_types (stk, block.STRING, line_aux, (int) strlen(block.STRING), bg, arrays_list, vars, current_array);
                
    free (block.STRING);

    (*current_pos)++;
}

/*!
 * Função responsável por aplicar um bloco a todos os elementos de um **array**.
 * @param stk Endereço da **stack** a ser usada;
 * @param current_pos Endereço do valor do índice atual a ser lido na linha de comando recebida;
 * @param line_aux **String** com todos os caracteres digitados abaixo da primeira linha;
 * @param bg Endereço do valor do índice atual a ser lido na **line_aux**;
 * @param arrays_list Endereço do endereço da lista de **arrays**;
 * @param vars *Array** de **DATAs** com a informação das variáveis;
 * @param current_array Endereço do valor numérico currespondente ao número de **arrays**.
 */
void compute_all_types_map_array (STACK *stk, int *current_pos, char *line_aux, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array) {

    DATA block = POP (stk);
    DATA array_flag = POP (stk);

    STACK *stk_array = find_addr_array (*arrays_list, array_flag.LONG);
                
    STACK *new_stack_array = create_stack();

    STACK *stk_aux = create_stack();

    int i, j;

    for (i = 0; i < stk_array->n_elems; i++) {

        PUSH (stk_aux, stk_array->stack[i]);

        compute_all_types (stk_aux, block.STRING, line_aux, (int) strlen(block.STRING), bg, arrays_list, vars, current_array);

        for (j = 0; j < stk_aux->n_elems; j++)

            PUSH (new_stack_array, stk_aux->stack[j]);

        stk_aux->n_elems = 0;
    }

    free (block.STRING);

    free (stk_aux->stack);
    free (stk_aux);

    free (stk_array->stack);
    free (stk_array);

    add_array (stk, arrays_list, new_stack_array, current_array);

    (*current_pos)++;
}

/*!
 * Função responsável por aplicar um bloco a todos os elementos de uma **string**.
 * @param stk Endereço da **stack** a ser usada;
 * @param current_pos Endereço do valor do índice atual a ser lido na linha de comando recebida;
 * @param line_aux **String** com todos os caracteres digitados abaixo da primeira linha;
 * @param bg Endereço do valor do índice atual a ser lido na **line_aux**;
 * @param arrays_list Endereço do endereço da lista de **arrays**;
 * @param vars *Array** de **DATAs** com a informação das variáveis;
 * @param current_array Endereço do valor numérico currespondente ao número de **arrays**.
 */
void compute_all_types_map_string (STACK *stk, int *current_pos, char *line_aux, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array) {

    DATA block = POP (stk);
    DATA str = POP (stk);

    STACK *stk_aux = create_stack();

    int i;

    int len_new_string = strlen (str.STRING) + 1,
        write = 0;

    DATA new_str;
    new_str.type = STRING;
    new_str.is_block = 0;

    new_str.STRING = (char *) malloc (len_new_string * sizeof (char));
                
    block.type = CHAR;

    int r = len_new_string, j = 0;

    for (i = 0; i < r - 1; i++) {
                    
        block.CHAR = str.STRING[i];

        PUSH (stk_aux, block);
                
        compute_all_types (stk_aux, block.STRING, line_aux, (int) strlen(block.STRING), bg, arrays_list, vars, current_array);

        while (j < stk_aux->n_elems) {
                        
            if (write == len_new_string) {
                            
                len_new_string += r;

                new_str.STRING = realloc (new_str.STRING, len_new_string * sizeof (char));
            }
                        
            new_str.STRING[write] = stk_aux->stack[j].CHAR;
                        
            write++; j++;
        }

        stk_aux->n_elems = 0;
        j = 0;
    }

    free (stk_aux->stack);
    free (stk_aux);

    free (str.STRING);
    free (block.STRING);
                
    new_str.STRING[write] = '\0';

    (*current_pos)++;

    PUSH (stk, new_str);
}

/*!
 * Função responsável por aplicar um bloco a dois elementos consecutivos de um **array**, continuamente até sobrar apenas um (aplicado da esquerda para a direita). 
 * @param stk Endereço da **stack** a ser usada;
 * @param current_pos Endereço do valor do índice atual a ser lido na linha de comando recebida;
 * @param line_aux **String** com todos os caracteres digitados abaixo da primeira linha;
 * @param bg Endereço do valor do índice atual a ser lido na **line_aux**;
 * @param arrays_list Endereço do endereço da lista de **arrays**;
 * @param vars *Array** de **DATAs** com a informação das variáveis;
 * @param current_array Endereço do valor numérico currespondente ao número de **arrays**.
 */
void compute_all_types_fold (STACK *stk, int *current_pos, char *line_aux, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array) {

    DATA block = POP (stk);
    DATA arr_flag = POP (stk);

    STACK *old_arr = find_addr_array (*arrays_list, arr_flag.LONG);
    STACK *aux = create_stack();

    PUSH (aux, old_arr->stack[0]);

    int i;

    for (i = 1; i < old_arr->n_elems; i++) {
                    
        PUSH (aux, old_arr->stack[i]);

        compute_all_types (aux, block.STRING, line_aux, (int) strlen(block.STRING), bg, arrays_list, vars, current_array);
    }

    free (old_arr->stack);
    free (old_arr);

    add_array (stk, arrays_list, aux, current_array);

    (*current_pos)++;

}

/*!
 * Função responsável por filtrar um **array** usando um bloco (se o valor final tiver valor lógico falso, 
 * ou seja, valor numérico 0, caractere **\0** ou **string** sem elementos, remove esse elemento do **array**).
 * @param stk Endereço da **stack** a ser usada;
 * @param current_pos Endereço do valor do índice atual a ser lido na linha de comando recebida;
 * @param line_aux **String** com todos os caracteres digitados abaixo da primeira linha;
 * @param bg Endereço do valor do índice atual a ser lido na **line_aux**;
 * @param arrays_list Endereço do endereço da lista de **arrays**;
 * @param vars *Array** de **DATAs** com a informação das variáveis;
 * @param current_array Endereço do valor numérico currespondente ao número de **arrays**.
 */
void compute_all_types_filter_array (STACK *stk, int *current_pos, char *line_aux, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array) {

    DATA block = POP(stk);
    DATA old_arr_elem = POP(stk);

    STACK *stk_aux = create_stack();

    STACK *old_arr = find_addr_array (*arrays_list, old_arr_elem.LONG);

    STACK *new_arr = create_stack();

    int i;

    DATA elem_to_test;

    for (i = 0; i < old_arr->n_elems; i++) {

        PUSH (stk_aux, old_arr->stack[i]);
                    
        compute_all_types (stk_aux, block.STRING, line_aux, (int) strlen(block.STRING), bg, arrays_list, vars, current_array);

        elem_to_test = stk_aux->stack[0];

        if (is_true (elem_to_test)) PUSH (new_arr, old_arr->stack[i]);
                    
        stk_aux->n_elems = 0;
    }

    free (old_arr->stack);
    free (old_arr);

    free (stk_aux->stack);
    free (stk_aux);

    add_array (stk, arrays_list, new_arr, current_array);

    (*current_pos)++;

}

/*!
 * Função responsável por filtrar uma **string** usando um bloco (se o valor final tiver valor lógico falso, 
 * ou seja, valor numérico 0, caractere **\0** ou **string** sem elementos, remove esse elemento da **string**).
 * @param stk Endereço da **stack** a ser usada;
 * @param current_pos Endereço do valor do índice atual a ser lido na linha de comando recebida;
 * @param line_aux **String** com todos os caracteres digitados abaixo da primeira linha;
 * @param bg Endereço do valor do índice atual a ser lido na **line_aux**;
 * @param arrays_list Endereço do endereço da lista de **arrays**;
 * @param vars *Array** de **DATAs** com a informação das variáveis;
 * @param current_array Endereço do valor numérico currespondente ao número de **arrays**.
 */
void compute_all_types_filter_string (STACK *stk, int *current_pos, char *line_aux, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array) {

    DATA block = POP (stk);
    DATA old_str = POP (stk);

    DATA new_str;

    STACK *stk_aux = create_stack();

    int len_old_str = (int) strlen(old_str.STRING);

    new_str.type = STRING;
    new_str.is_block = 0;
    new_str.STRING = (char *) malloc ( (len_old_str + 1) * sizeof (char) );

    int i, write = 0;

    DATA elem_aux;
    elem_aux.type = CHAR;

    for (i = 0; i < len_old_str; i++) {

        elem_aux.CHAR = old_str.STRING[i];

        PUSH (stk_aux, elem_aux);

        compute_all_types (stk_aux, block.STRING, line_aux, (int) strlen(block.STRING), bg, arrays_list, vars, current_array);
                   
        if ( is_true (POP (stk_aux) ) ) {

            new_str.STRING[write] = old_str.STRING[i];

            write++;
        }
    }

    free (block.STRING);
                
    free (old_str.STRING);

    free (stk_aux->stack);
    free (stk_aux);

    new_str.STRING[write] = '\0';

    (*current_pos)++;

    PUSH (stk, new_str);
}

/*!
 * Função responsável por ordenar um **array** por ordem crescente do resultado de aplicar um bloco a um dado elemento.
 * @param stk Endereço da **stack** a ser usada;
 * @param current_pos Endereço do valor do índice atual a ser lido na linha de comando recebida;
 * @param line_aux **String** com todos os caracteres digitados abaixo da primeira linha;
 * @param bg Endereço do valor do índice atual a ser lido na **line_aux**;
 * @param arrays_list Endereço do endereço da lista de **arrays**;
 * @param vars *Array** de **DATAs** com a informação das variáveis;
 * @param current_array Endereço do valor numérico currespondente ao número de **arrays**.
 */
void compute_all_types_sort_on_array (STACK *stk, int *current_pos, char *line_aux, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array) {

    DATA block = POP (stk);
    DATA old_arr_elem = top (stk);

    STACK *old_arr = find_addr_array (*arrays_list, old_arr_elem.LONG);

    STACK *after_compute = create_stack();

    STACK *stk_aux = create_stack();

    int i, num_elems = old_arr->n_elems;

    DATA elem;

    for (i = 0; i < num_elems; i++) {

        elem = old_arr->stack[i];

        if (has_type (elem, STRING)) {
                        
            int j, len = (int) strlen (elem.STRING);

            DATA elem1 = elem;

            elem1.STRING = (char *) malloc ( (len + 1) * sizeof (char));
                        
            for (j = 0; j < len; j++)
                            
                elem1.STRING[j] = elem.STRING[j];

            elem1.STRING[j] = '\0';

            PUSH (stk_aux, elem1);
        }

        else

            PUSH (stk_aux, elem);
                   
        compute_all_types (stk_aux, block.STRING, line_aux, (int) strlen(block.STRING), bg, arrays_list, vars, current_array);
                   
        PUSH (after_compute, stk_aux->stack[0]);

        stk_aux->n_elems = 0;
    }

    free (block.STRING);

    free (stk_aux->stack);
    free (stk_aux);

    bubble_sort_array (after_compute, old_arr);

    free (after_compute->stack);
    free (after_compute);

    (*current_pos)++;
}

/*!
 * Função responsável por ordenar uma **string** por ordem crescente do resultado de aplicar um bloco a um dado caractere.
 * @param stk Endereço da **stack** a ser usada;
 * @param current_pos Endereço do valor do índice atual a ser lido na linha de comando recebida;
 * @param line_aux **String** com todos os caracteres digitados abaixo da primeira linha;
 * @param bg Endereço do valor do índice atual a ser lido na **line_aux**;
 * @param arrays_list Endereço do endereço da lista de **arrays**;
 * @param vars *Array** de **DATAs** com a informação das variáveis;
 * @param current_array Endereço do valor numérico currespondente ao número de **arrays**.
 */
void compute_all_types_sort_on_string (STACK *stk, int *current_pos, char *line_aux, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array) {

    DATA block = POP (stk);
    DATA old_str = POP (stk);

    DATA new_str;

    int len_old_str = (int) strlen(old_str.STRING);

    new_str.type = STRING;
    new_str.is_block = 0;
    new_str.STRING = (char *) malloc ( (len_old_str + 1)  * sizeof(char));

    STACK *stk_after_compute = create_stack();

    STACK *stk_aux = create_stack();

    DATA elem_aux;
                elem_aux.type = CHAR;

    int i;

    for (i = 0; i < len_old_str; i++) {

        elem_aux.CHAR = new_str.STRING[i] = old_str.STRING[i];

        PUSH (stk_aux, elem_aux);

        compute_all_types (stk_aux, block.STRING, line_aux, (int) strlen(block.STRING), bg, arrays_list, vars, current_array);

        PUSH (stk_after_compute, stk_aux->stack[0]);

        stk_aux->n_elems = 0;
    }

    free (block.STRING);

    free (stk_aux->stack);
    free (stk_aux);

    bubble_sort_string (stk_after_compute, new_str.STRING);

    free (stk_after_compute->stack);
    free (stk_after_compute);

    PUSH (stk, new_str);

    (*current_pos)++;
}

/*!
 * Função responsável por executar um bloco continuamente sobre a **stack** até que surja um elemento com o valor lógico falso (remove a condição).
 * @param stk Endereço da **stack** a ser usada;
 * @param current_pos Endereço do valor do índice atual a ser lido na linha de comando recebida;
 * @param line_aux **String** com todos os caracteres digitados abaixo da primeira linha;
 * @param bg Endereço do valor do índice atual a ser lido na **line_aux**;
 * @param arrays_list Endereço do endereço da lista de **arrays**;
 * @param vars *Array** de **DATAs** com a informação das variáveis;
 * @param current_array Endereço do valor numérico currespondente ao número de **arrays**.
 */
void compute_all_types_do_while (STACK *stk, int *current_pos, char *line_aux, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array) {

    DATA block = POP (stk);
    
    DATA aux;
    aux.type = CHAR; // para não interferir no ciclo

    do {

        if (has_type (aux, STRING))

            free (aux.STRING);
        
        else 

            if (has_type (aux, LONG) && aux.is_array) {

                STACK *arr = find_addr_array (*arrays_list, aux.LONG);

                free (arr->stack);
                free (arr);
            }
        
        compute_all_types (stk, block.STRING, line_aux, (int) strlen(block.STRING), bg, arrays_list, vars, current_array);

        aux = POP(stk);
    
    } while (stk->n_elems > 0 && is_true (aux));

    if (has_type (aux, STRING))

            free (aux.STRING);
        
        else 

            if (has_type (aux, LONG) && aux.is_array) {

                STACK *arr = find_addr_array (*arrays_list, aux.LONG);

                free (arr->stack);
                free (arr);
            }

    (*current_pos)++;
}

/*!
 * Função que faz o cáculo correpondente a uma determinada linha de input aplicando todas as operações descritas em todos os ficheiros auxiliares.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param line **string** para operar;
 * @param line_aux linhas escritas abaixo da linha principal
 * @param len_line Comprimento da linha utilizado;
 * @param bg Endereço da atual posição a ser lida na linha auxiliar;
 * @param arrays_list Endereço do endereço da lista ligada de **arrays**;
 * @param vars Lista de **DATAS** com a informação das variáveis;
 * @param current_array Endereço do valor do número de **arrays** já colocados na **stack**.
 * @note Em operações relativas a blocos de instrução, esta função é chamada recursivamente (com funções auxiliares como intermédio).
 */
void compute_all_types (STACK *stk, char *line, char *line_aux, int len_line, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array) {

    int current_pos = 0;

    while (current_pos < len_line) {
    
        while (line[current_pos] == ' ' || line[current_pos] == '\n' || line[current_pos] == '\t') current_pos++;

        switch (operator_blocks (stk, line + current_pos)) {

            case 1:  // ~

                compute_all_types_execute_block (stk, &current_pos, line_aux, bg, arrays_list, vars, current_array);
                
                break;

            case 2:  // % arrays
                
                compute_all_types_map_array (stk, &current_pos, line_aux, bg, arrays_list, vars, current_array);

                break;
    
            case 3:  // % strings
               
                compute_all_types_map_string (stk, &current_pos, line_aux, bg, arrays_list, vars, current_array);

                break;

            case 4:  // fold
               
                compute_all_types_fold (stk, &current_pos, line_aux, bg, arrays_list, vars, current_array);

                break;
            
            case 5:  // , arr
                
                compute_all_types_filter_array (stk, &current_pos, line_aux, bg, arrays_list, vars, current_array);

                break;
            
            case 6:  // , str
                
                compute_all_types_filter_string (stk, &current_pos, line_aux, bg, arrays_list, vars, current_array);

                break;
        
            case 7:  // $ arr
                
                compute_all_types_sort_on_array (stk, &current_pos, line_aux, bg, arrays_list, vars, current_array);
                
                break;

            case 8:  // $ str
                
                compute_all_types_sort_on_string (stk, &current_pos, line_aux, bg, arrays_list, vars, current_array);

                break;
            
            case 9:  // w
                
                compute_all_types_do_while (stk, &current_pos, line_aux, bg, arrays_list, vars, current_array);

                break;

            case 0:  // outras operações

                compute_aux (stk, line, line_aux, len_line, bg, arrays_list, vars, current_array, &current_pos);

                break;
        } 
    }
}

/*!
 * Função que recebe uma linha de input, utiliza a função compute_all_types() para interpretar a linha e imprime o resultado no final da execução.
 * @param line **String** que corresponde à linha lida pela função main().
 */
void compute (char *line) {

    STACK *stk = create_stack(); // stack a ser utilizada

    DATA *vars = create_vars();

    ARRAYSLIST arrays_list = create_arrays();

    int i;

    for (i = 0; line[i] != '\n'; i++);
    
    char *line_aux; // linha auxiliar para usar as próximas linhas
    
    line_aux = line + i + 1;

    line[i] = '\0';

    int bg = 0; // início da linha auxiliar

    long current_array = 0;

    compute_all_types (stk, line, line_aux, i, &bg, &arrays_list, vars, &current_array);

    free (vars);

    print_stack (stk, arrays_list);
    
    free (stk->stack);
    free (stk);

    putchar('\n');
}
