/**
 * @file Ficheiro que inclui as funções auxiliares para a interpertação de uma linha
 */

#include <stdlib.h>
#include <string.h>
#include "stack.h"
#include "computemath.h"
#include "commandl.h"
#include "typecasting.h"
#include "manipulation.h"
#include "logic.h"
#include "variables.h"
#include "arrays.h"
#include "arraysoperations.h"

/**
 @file computeaux.c
 \brief Ficheiro que inclui as funções auxiliares para a interpertação de uma linha
 */

/*!
 * Testa se um dado caractere é um operador válido para as operações previstas até ao Guião 2.
 * @param token Caractere a ser testado.
 * @returns 0, se não for um operador válido;
 * @returns 1, se for um operador aritmético;
 * @returns 2, se for o caracter **l**;
 * @returns 3, se for um operador que altera o tipo do elemento;
 * @returns 4, se for um operador que que realiza algum tipo de manipulação na *stack* sem operar com os elementos;
 * @returns 5, se for um operador lógico;
 * @returns 6, se for um operador que realiza operações sobre variáveis.
 */
int operator_unit_type (char token) {
    
    if (operator_math(token)) return 1;

    if (token == 'l') return 2;

    if (operator_change_type(token)) return 3;

    if (operator_manipulation(token)) return 4;

    if (logical_operator(token)) return 5;

    if (is_var(token) || token == ':') return 6;

    return 0;
}

/*!
 * Função responsável por realizar todas as operações sobre tipos básicos definidas.
 *
 * Esta função testa o tipo de operador que vai ser usado e aplica uma das funções gerais de operações básicas descritas noutros ficheiros.
 * @param stk Endereço da *stack* a ser utilizada na função compute();
 * @param token **string** com o(s) caractere(s) lido(s) entre os delimitadores na função compute();
 * @param line_aux **string** que contém todos os caracteres que aparecem após o primeiro '\\n';
 * @param bg Endereço de memória da variável **bg** (declarada na função compute() e usada para determinar a primeira posição que vamos ter em conta na linha auxiliar);
 * @param vars **Array** de **DATAS** que contêm o conteúdo das variáveis;
 * @param arys Endereço do endereço da lista de **arrays**.
 * @param current_array Endereço com o valor do número de **arrays** que já foram colocados na **stack**.
 */
void operate (STACK *stk, char *token, char *line_aux, int *bg, DATA *vars, ARRAYSLIST *arys, long *current_array) {

    switch (operator_unit_type (*token)) {

        case 1: // operator_math
            
            if (operator_math(*token) <= stk->n_elems)
                compute_math (stk, *token);
            
            break;
        
        case 2: // comando l

            execute_L (stk, line_aux, bg);
            
            break;
        
        case 3: // operator_change_type

            type_Casting (stk, *token);
            
            break;
        
        case 4: // operator_manipulation

            if (operator_manipulation(*token) <= stk->n_elems)
                manipulation (stk, *token, arys, current_array);

            break; 
        
        case 5: // logical_operator

            if (logical_operator(*token) <= stk->n_elems)   
                logical_operations(stk, token, *arys);
            
            break;
        
        case 6: // operate_var

            if (!(*token == ':') || stk->n_elems >= 1)
                operate_var(stk, token, vars, arys, current_array);
            
            break;
    }
}

// ~

/*!
 * Função que testa se é para executar um bloco.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token **String** que poderá corresponder a um operador.
 * @returns !0, se for para executar o comando '~';
 * @returns 0, caso contrário.
 */
int is_to_execute_block (STACK *stk, char *token) {

    DATA top_stack = top (stk);

    int cond_1 = token[0] == '~',
        cond_2 = has_type (top_stack, STRING) && top_stack.is_block;

    return cond_1 && cond_2;
}

// % arr

/*!
 * Função que testa se é para executar um bloco sobre todos os elementos de um **array**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token **String** que poderá corresponder a um operador.
 * @returns !0, se for para executar o comando '%' sobre um **array**;
 * @returns 0, caso contrário.
 */
int is_to_execute_in_arr (STACK *stk, char *token) {

    DATA elem2 = top(stk);
    DATA elem1 = stk->stack[stk->n_elems - 2];

    int cond_1 = *token == '%',
        cond_2 = has_type (elem2, STRING) && elem2.is_block,
        cond_3 = has_type (elem1,LONG) && elem1.is_array;
    
    return cond_1 && cond_2 && cond_3;
}


// % str

/*!
 * Função que testa se é para executar um bloco sobre todos os elementos de uma **string**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token **String** que poderá corresponder a um operador.
 * @returns !0, se for para executar o comando '%' sobre uma **string**;
 * @returns 0, caso contrário.
 */
int is_to_execute_in_str (STACK *stk, char *token) {

    DATA elem2 = top(stk);
    DATA elem1 = stk->stack[stk->n_elems - 2];

    int cond_1 = *token == '%',
        cond_2 = has_type (elem2,STRING) && elem2.is_block,
        cond_3 = has_type (elem1, STRING) && !elem1.is_block;
    
    return cond_1 && cond_2 && cond_3;
}

// *

/*!
 * Função que testa se é para executar um bloco continuamente sobre os dois primeiros elementos de um **array**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token **String** que poderá corresponder a um operador.
 * @returns !0, se for para executar o comando '*';
 * @returns 0, caso contrário.
 */
int is_to_fold (STACK *stk, char *token) {

    DATA elem2 = top(stk);
    DATA elem1 = stk->stack[stk->n_elems - 2];

    int cond_1 = *token == '*',
        cond_2 = has_type (elem2, STRING) && elem2.is_block,
        cond_3 = has_type (elem1, LONG) && elem1.is_array;

    return cond_1 && cond_2 && cond_3;
}

// , arr

/*!
 * Função que testa se é para filtrar um **array** usando um bloco (permanencem no **array** os elementos que, após a operação, têm um valor lógico verdadeiro).
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token **String** que poderá corresponder a um operador.
 * @returns !0, se for para executar o comando ',' sobre um **array**;
 * @returns 0, caso contrário.
 */
int is_to_filter_array (STACK *stk, char *token) {

    DATA elem2 = top(stk);
    DATA elem1 = stk->stack[stk->n_elems - 2];

    int cond_1 = *token == ',',
        cond_2 = has_type (elem2, STRING) && elem2.is_block,
        cond_3 = has_type (elem1, LONG) && elem1.is_array;

    return cond_1 && cond_2 && cond_3;
}

// , str

/*!
 * Função que testa se é para filtrar uma **string** usando um bloco (permanencem na **string** os elementos que, após a operação, têm um valor lógico verdadeiro).
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token **String** que poderá corresponder a um operador.
 * @returns !0, se for para executar o comando '%' sobre uma **string**;
 * @returns 0, caso contrário.
 */
int is_to_filter_str (STACK *stk, char *token) {
    
    DATA elem2 = top(stk);
    DATA elem1 = stk->stack[stk->n_elems - 2];

    int cond_1 = *token == ',',
        cond_2 = has_type (elem2, STRING) && elem2.is_block,
        cond_3 = has_type (elem1, STRING) && !elem1.is_block;

    return cond_1 && cond_2 && cond_3;
}

// $ arr

/*!
 * Função que testa se é para ordenar um **array** segundo uma função, no caso, a execução de um bloco sobre cada elemento.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token **String** que poderá corresponder a um operador.
 * @returns !0, se for para executar o comando '$' sobre um **array**;
 * @returns 0, caso contrário.
 */
int is_to_sort_arr (STACK *stk, char *token) {

    DATA elem2 = top(stk);
    DATA elem1 = stk->stack[stk->n_elems - 2];

    int cond_1 = *token == '$',
        cond_2 = has_type (elem2, STRING) && elem2.is_block,
        cond_3 = has_type (elem1, LONG) && elem1.is_array;

    return cond_1 && cond_2 && cond_3;
}

// $ str

/*!
 * Função que testa se é para ordenar uma **string** segundo uma função, no caso, a execução de um bloco sobre cada caractere.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token **String** que poderá corresponder a um operador.
  * @returns !0, se for para executar o comando '$' sobre uma **string**;
 * @returns 0, caso contrário.
 */
int is_to_sort_str (STACK *stk, char *token) {
    
    DATA elem2 = top(stk);
    DATA elem1 = stk->stack[stk->n_elems - 2];

    int cond_1 = *token == '$',
        cond_2 = has_type (elem2, STRING) && elem2.is_block,
        cond_3 = has_type (elem1, STRING) && !elem1.is_block;

    return cond_1 && cond_2 && cond_3;
}

// w

/*!
 * Função que testa se é para executar um bloco enquanto deixar uma condição verdadeira no topo da **stack**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token **String** que poderá corresponder a um operador.
 * @returns !0, se for para executar o comando 'w';
 * @returns 0, caso contrário.
 */
int is_to_operate_w (STACK *stk, char *token) {

    DATA top_stack = top(stk);

    int cond_1 = *token == 'w',
        cond_2 = has_type (top_stack, STRING) && top_stack.is_block;
    
    return cond_1 && cond_2;
}

/*!
 * Função que testa se é para executar alguma operação relativa a blocos de instrução.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token **String** que poderá corresponder a um operador.
 * @returns 1, caso o operador seja '~';
 * @returns 2, caso o operador seja '%' (sobre um **array**);
 * @returns 3, caso o operador seja '%' (sobre uma **string**);
 * @returns 4, caso o operador seja '*';
 * @returns 5, caso o operador seja ',' (sobre um **array**);
 * @returns 6, caso o operador seja ',' (sobre uma **string**);
 * @returns 7, caso o operador seja '$' (sobre um **array**);
 * @returns 8, caso o operador seja '$' (sobre uma **string**);
 * @returns 9, caso o operador seja 'w';
 * @returns 0, se não for para executar qualquer operação sobre blocos.
 */
int operator_blocks (STACK *stk, char *token) {

    if (is_to_execute_block (stk, token)) return 1;

    if (is_to_execute_in_arr (stk, token)) return 2;

    if (is_to_execute_in_str (stk, token)) return 3;

    if (is_to_fold (stk, token)) return 4;

    if (is_to_filter_array (stk, token)) return 5;

    if (is_to_filter_str (stk, token)) return 6;

    if (is_to_sort_arr (stk, token)) return 7;

    if (is_to_sort_str (stk, token)) return 8;

    if (is_to_operate_w (stk, token)) return 9;

    return 0;
}

/*!
 * Função que coloca numa **string** os caracteres correspondentes a um elemento (**long**, **double**, **char** ou **string**), **array** ou **bloco** 
 * e testa se essa **string** corresponde a um **array**, **string**, **bloco** ou operador sobre blocos. Se sim, a função retorna 1. Caso contrário. incrementa 
 * a posição atual da linha e retorna 0 (estes valores determinam, na função compute_unit_types(), se o ciclo deve parar).
 * @param stk Endereço da **stack** a ser utilizada;
 * @param line **String** lida no início da execução;
 * @param token **String** onde será colocado o conteúdo de uma parte da **line**;
 * @param N Número de caracteres a seres lidos da **line**;
 * @param current_pos Endereço do valor da atual posição a ser lida na **line**.
 * @returns 1, Se o **token**, após ter sido completo com o conteúdo, corresponder a uma **string**, **array**, **bloco** ou operador sobre blocos;
 * @returns 0, caso contrário.
 */
int clone_to_token (STACK *stk, char *line, char *token, int N, int *current_pos) {

    int i;

    for (i = 0; i < N; i++)

        token[i] = line[i + *current_pos];
    
    token[i] = '\0';

    if (line[*current_pos] == '[' 
     || line[*current_pos] == '"' 
     || line[*current_pos] == '{' 
     || operator_blocks (stk, token)) return 1;

    (*current_pos) += i + 1;

    return 0;
}

/*!
 * Função responsável pela realização das operações que não envolvem blocos (execeto criar **arrays** ou **strings**), bem como incluir tipos de dados simples à **stack**.
 * @param stk Endereço da **stack** que o programa está a utilizar;
 * @param line **String** lida no início da execução;
 * @param current_pos Endereço do valor da atual posição a ser lida na **line**;
 * @param last_pos_line Última posição a utilizar na **line**
 * @param line_aux linha que contém tudo o que foi escrito após a primeira quebra de linha;
 * @param bg Endereço do valor da atual posição a ser lida na **line_aux**;
 * @param vars **Array** de **DATAS** com a informação do conteúdo das variáveis;
 * @param arys Endereço do endereço da lista ligada de **arrays**;
 * @param current_array Endereço do valor do número de **arrays** que o programa já leu.
 */
void compute_unit_types (STACK *stk, char* line, int *current_pos, int last_pos_line, char *line_aux, int *bg, DATA *vars, ARRAYSLIST *arys, long *current_array) {

    int token_len;
    
    while (*current_pos < last_pos_line) {

        while (line[*current_pos] == ' ' || line[*current_pos] == '\n' || line[*current_pos] == '\t') (*current_pos)++;

        token_len = last_pos_token (line, *current_pos);

        char token[token_len];

        if (clone_to_token (stk, line, token, token_len, current_pos)) break;
    
        if (operator_arrays_or_strings (stk, token)) 
        
            operate_arrays_or_strings (stk, arys, token, current_array); 

        else {

            if (*token == 't' && !token[1]) {

                DATA str;
                str.type = STRING;
                str.is_block = 0;
                str.STRING = (char *) malloc ( (strlen(line_aux) + 1) * sizeof (char));

                int i;

                for (i = 0; i < (int) strlen(line_aux); i++)

                    str.STRING[i] = line_aux[i];
                
                str.STRING[i] = '\0';

                PUSH (stk, str);
            }                

            else {
            
                if (operator_unit_type(*token) && ( !token[1] || (token[0] == 'e' && !token[2]) || (token[0] == ':' && is_var(token[1]) && !token[2]) ) ) 
                    
                    operate (stk, token, line_aux, bg, vars, arys, current_array);
        
                else
                    
                    push_by_type (stk, token);
            }
        }
    }
}

/*!
 * Função que determina o tamanho que terá a **string** com a informação de um bloco.
 * @param line **String** lida no início da execução;
 * @param current_pos Endereço do valor da atual posição a ser lida na **line**.
 * @returns Tamanho que terá a **string** com a informação do bloco.
 */
int len_block (char *line, int *current_pos) {

    int i = 1;

    int c_par_init = 1,
        c_par_end = 0;

    while (c_par_init > c_par_end) {

        i++;

        if (line[i + *current_pos] == '}')

            c_par_end++;
        
        else 

            if (line[i + *current_pos] == '{')

                c_par_init++;
    }

    (*current_pos) += i + 1;

    return i - 1;
}

/*!
 * Função que clona a informação de um bloco, inclui-o na **stack** e incrementa a posição atual da **line**;
 * @param stk Endereço da **stack** a ser utilizada;
 * @param line **String** lida no início da execução;
 * @param current_pos Endereço do valor da atual posição a ser lida na **line**.
 */
void read_block (STACK *stk, char *line, int *current_pos) {

    int len = len_block (line, current_pos);

    DATA elem;
    
    elem.type = STRING;
    elem.STRING = (char *) malloc ((len + 1) * sizeof(char));
    elem.is_block = 1;

    read_string (line, (*current_pos) - len - 1, (*current_pos) - 2, elem.STRING);

    PUSH (stk, elem);
}

/*!
 * Função responsável por adicionar à **stack**: **blocos**, **arrays** e **strings**, bem como executar a função compute_unit_types().
 * @param stk Endereço da **stack** que o programa está a utilizar;
 * @param line **String** lida no início da execução;
 * @param line_aux linha que contém tudo o que foi escrito após a primeira quebra de linha;
 * @param len_line Última posição a utilizar na **line**
 * @param bg Endereço do valor da atual posição a ser lida na **line_aux**;
 * @param arrays_list Endereço do endereço da lista ligada de **arrays**;
 * @param vars **Array** de **DATAS** com a informação do conteúdo das variáveis;
 * @param current_array Endereço do valor do número de **arrays** que o programa já leu;
 * @param current_pos Endereço do valor da atual posição a ser lida na **line**.
 * @note Se for necessário usar uma operação com blocos, é feito um *break* na função e a execução retorna à função compute_all_types().
 */
void compute_aux (STACK *stk, char *line, char *line_aux, int len_line, int *bg, ARRAYSLIST *arrays_list, DATA *vars, long *current_array, int *current_pos) {
    
    while ((*current_pos) < len_line) {

        while (line[*current_pos] == ' ' || line[*current_pos] == '\n' || line[*current_pos] == '\t') (*current_pos)++;
        
        if (operator_blocks (stk, line + *current_pos)) break;
        
        if (line[*current_pos] == '{')

            read_block (stk, line, current_pos);

        else {
            
            if (line[*current_pos] == '[') {

                int len = array_len (line, current_pos);

                char str_to_array[len + 1];

                get_array (line, str_to_array, (*current_pos) - len - 1, (*current_pos) - 2);

                STACK *stk_to_arrays = create_stack();

                int new_current_pos = 0;

                compute_aux (stk_to_arrays, str_to_array, line_aux, len, bg, arrays_list, vars, current_array, &new_current_pos);

                add_array (stk, arrays_list, stk_to_arrays, current_array);
            }
    
            else {

                if (line[*current_pos] == '"') {     

                    int len = len_string(line, current_pos);

                    DATA elem;

                    elem.type = STRING;
                    elem.is_block = 0;

                    elem.STRING = (char *) malloc ((len+1) * sizeof (char));

                    read_string (line, (*current_pos) - len - 1, (*current_pos) - 2, elem.STRING);

                    PUSH (stk, elem);
                }

                else 

                    compute_unit_types (stk, line, current_pos, len_line, line_aux, bg, vars, arrays_list, current_array);
            }
        }
    }
}