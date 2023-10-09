/**
 * @file Ficheiro que contém as funções necessárias para operarar sobre **arrays**
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "stack.h"
#include "arrays.h"
#include "arraysoperations.h"

/**
 @file arraysoperations.c
 \brief Ficheiro que contém as funções necessárias para operarar sobre **arrays**
*/


// ~

/*!
 * Esta função é responsável por executar o comando '~' sobre **arrays**.
 * @param stk Endereço da *stack* a ser utilizada;
 * @param arys Endereço da lista ligada de **arrays**.
 */

void put_array_in_stack (STACK *stk, ARRAYSLIST *arys) {

    DATA flag = POP (stk);        // remover a flag

    STACK *array_stack = find_addr_array (*arys, flag.LONG);  // encontrar a stack corresponde ao array

    int i;

    for (i = 0; i < array_stack->n_elems; i++)     // adiciona todos os elementos do array à stack inicial

        PUSH (stk, array_stack->stack[i]);

    free (array_stack->stack);  // liberta o espaço alocado para o array
    free (array_stack);
}

/*!
 * Função que testa se é para executar o comando '~'.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token Caractere a ser testado.
 * @returns !0, se o topo da **stack** for um **array** e se o *token* for o operador '~';
 * @returns 0, caso contrário.
 */
int is_to_put (STACK *stk, char token) {

    return ( has_type (top(stk), LONG) && top(stk).is_array && token == '~');
}

// + 

/*!
 * Esta função é responsável por concatenar dois **arrays**.
 * @param stk Corresponde ao endereço da **stack**;
 * @param arys Endereço da lista de **arrays** a ser utilizada.
 */
void concat_array_to_array (STACK *stk, ARRAYSLIST arys) {

    DATA array2_flag = POP(stk);
    DATA array1_flag = top(stk);

    STACK *stk_to_add = find_addr_array (arys, array2_flag.LONG);     // stack do segundo array
    STACK *stk_to_recive = find_addr_array (arys, array1_flag.LONG);  // stack do primeiro array

    int i;

    for (i = 0; i < stk_to_add->n_elems; i++)

        PUSH (stk_to_recive, stk_to_add->stack[i]);
    
    free (stk_to_add->stack); // liberta o espaço na heap ocupado pela stack do segundo array
    free (stk_to_add);
}

/*!
 * Função responsável por concatenar um **array** com um dos quatro tipos elementares (**LONG**, **DOUBLE**, **CHAR** ou **STRING**), por esta ordem.
 * @param stk Corresponde ao endereço da **stack**;
 * @param arys Endereço da lista de **arrays** a ser utilizada.
 */
void concat_array_to_elem (STACK *stk, ARRAYSLIST arys) {

    DATA elem = POP(stk); // remove o elemento da stack principal

    PUSH ( find_addr_array(arys, (top(stk)).LONG), elem);  // coloca o elemento no array
}

/*!
 * Função responsável por concatenar um dos quatro tipos elementares (**LONG**, **DOUBLE**, **CHAR** ou **STRING**) com um **array**, por esta ordem.
 * @param stk Corresponde ao endereço da **stack**;
 * @param arys Endereço da lista de **arrays** a ser utilizada.
 */
void concat_elem_to_array (STACK *stk, ARRAYSLIST arys) {

    DATA array_flag = POP (stk);
    DATA elem = POP (stk);

    STACK *stk_array = find_addr_array (arys, array_flag.LONG);

    stk_array->n_elems++;

    if (stk_array->size == stk_array->n_elems) {
        stk_array->size += 100;
        stk_array->stack = (DATA *) realloc (stk_array->stack, stk_array->size * sizeof(DATA));
    }

    int i;

    for (i = stk_array->n_elems - 1; i > 0; i--)

        stk_array->stack[i] = stk_array->stack[i - 1]; // "empurar" os elementos para a direita
    
    stk_array->stack[0] = elem; // colocar os elemento no início do array

    PUSH (stk, array_flag);
}

/*!
 * Função que usa as três funções responsáveis para concatenar **arrays** com **arrays** ou com outros tipos de dados como auxiliares para realizar todas as operações de concatenação
 * que envolvem **arrays**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço da lista de **arrays**.
 */
void concat_arrays (STACK *stk, ARRAYSLIST arys) {

    DATA elem2 = top(stk);
    DATA elem1 = stk->stack[stk->n_elems - 2];

    if (has_type (elem1, LONG) && elem1.is_array) { // penult elem é array

        if (has_type (elem2, LONG) && elem2.is_array) // ult também

            concat_array_to_array (stk, arys);
        
        else

            concat_array_to_elem (stk, arys); // ult não é
    }
    
    else // apenas o topo é um array

        concat_elem_to_array (stk, arys);
}

/*!
 * Função que concatena duas **strings**.
 * @param stk Endereço da **stack** a ser utilizada.
 */
void concat_string_to_string (STACK *stk) {

    DATA str2 = POP (stk);
    DATA str1 = POP(stk);

    int write = strlen(str1.STRING); // tamanho da string que irá receber

    int i, len = strlen(str2.STRING); // tamanho da string que irá fornecer

    str1.STRING = realloc (str1.STRING, (write + len + 1) * sizeof (char));

    for (i = 0; i < len; i++)

        str1.STRING[i + write] = str2.STRING[i];
    
    str1.STRING[i + write] = '\0';

    free (str2.STRING); // liberta a segunda string

    PUSH (stk, str1);
}

/*!
 * Função que coloca um elemento no final de uma **string**.
 * @param stk Endereço da **stack** a ser utilizada.
 */
void concat_string_to_unit_type (STACK *stk) {

    DATA elem = POP(stk);
    DATA str = top(stk);

    int len_str = strlen(str.STRING);

    str.STRING = realloc (str.STRING, (len_str + 2) * sizeof(char));

    switch (elem.type) {

        case CHAR:
            break;

        case DOUBLE:
            elem.CHAR = (char) elem.DOUBLE;
            break;

        case LONG: 
            elem.CHAR = (char) elem.LONG;
            break;

        case STRING: // nunca chega a este caso
            elem.CHAR = (*elem.STRING);
            free (elem.STRING);
            break;        
    }

    str.STRING[len_str] = elem.CHAR;
    str.STRING[len_str + 1] = '\0';
}

/*!
 * Função que coloca um elemento no início de uma **string**.
 * @param stk Endereço da **stack** a ser utilizada.
 */
void concat_unit_type_to_string (STACK *stk) {

    DATA str = POP (stk);
    DATA elem = POP (stk);

    int len_str = strlen(str.STRING);

    str.STRING = realloc (str.STRING, (len_str + 1) * sizeof(char));
    
    switch (elem.type) {

        case CHAR:
            break;

        case DOUBLE:
            elem.CHAR = (char) elem.DOUBLE;
            break;

        case LONG: 
            elem.CHAR = (char) elem.LONG;
            break;

        case STRING: // nuca chega a este caso
            elem.CHAR = (*elem.STRING);
            free (elem.STRING);
            break;        
    }

    int i;

    for (i = len_str; i > 0; i--)    // "empurar" os caracteres da string para a direita

        str.STRING[i] = str.STRING[i - 1];
    
    str.STRING[i] = elem.CHAR;

    PUSH (stk, str);
}

/*!
 * Função que usa a três funções que concatenam **strings** a **strings** ou tipo elementares.
 * @param stk Endereço da **stack** a ser utilizada.
 */
void concat_strings (STACK *stk) {

    DATA elem2 = top(stk);
    DATA elem1 = stk->stack[stk->n_elems - 2];

    if (has_type (elem1, STRING)) { // o penult é STRING

        if (has_type (elem2, STRING)) // o ult é STRING

            concat_string_to_string (stk);
        
        else // o ult não é STRING

            concat_string_to_unit_type (stk);
    }

    else // apenas o ult é STRING

        concat_unit_type_to_string (stk);
}

/*!
 * Função final que concatena **arrays** ou **strings** usando como auxiares as funções concat_strings() e concat_arrays().
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço da lista de **arrays**.
 */
void concat (STACK *stk, ARRAYSLIST arys) {

    DATA elem2 = top(stk);
    DATA elem1 = stk->stack[stk->n_elems - 2];

    if ( (has_type (elem1, LONG) && elem1.is_array) || (has_type (elem2, LONG) && elem2.is_array) ) // testa se pelo menos um dos elementos é um array

        concat_arrays (stk, arys);
    
    else 

        concat_strings (stk);
    
}

/*!
 * Função que testa se é para realizar a operação de concatenação.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token Caractere a ser testado.
 * @returns !0, se for para concatenar **strings** ou **arrays**;
 * @returns 0, caso contrário.
 */
int is_to_concat (STACK *stk, char token) {

    DATA elem2 = top(stk);
    DATA elem1 = stk->stack[stk->n_elems - 2];

    int cond_1 = token == '+',                                 // o token é o operador usado para concatenar
        cond_2 = has_type (elem1, STRING) && !elem1.is_block,  // o primeiro elemento é uma string
        cond_3 = has_type (elem2, STRING) && !elem2.is_block,  // o segundo elemento é uma string
        cond_4 = has_type (elem1, LONG) && elem1.is_array,     // o primeiro elemento é um array
        cond_5 = has_type (elem2, LONG) && elem2.is_array;     // o segundo elemento é um array

    return cond_1 && (cond_2 || cond_3 || cond_4 || cond_5);
}

// *

/*!
 * Função que replica um **array** **n** vezes (e coloca tudo no primeiro) (**n** recebido como input).
 * @param stk Endereço da stack a ser utilizada;
 * @param arys Endereço da lista de **arrays**;
 * @param n_times Número de vezes que o **array** será replicado.
 */
void concat_arrays_n_times (STACK *stk, ARRAYSLIST arys, int n_times) {

    DATA array_flag = top(stk);

    STACK *stk_array = find_addr_array (arys, array_flag.LONG); // stack com os elementos do array

    int n_elems_init = stk_array->n_elems; // número de elementos iniciais

    int i, j;

    for (i = 1; i < n_times; i++) // percorrer (n - 1) vezes

        for (j = 0; j < n_elems_init; j++)  // colocar todos os elementos do array inicial

            PUSH (stk_array, stk_array->stack[j]);
}

/*!
 * Função que clona uma **string** **n** vezes (e coloca tudo no final da mesma).
 * @param stk Endereço da **stack** a ser utilizada;
 * @param n_times Número de vezes que a **string** será replicada.
 */
void concat_strings_n_times (STACK *stk, int n_times) {

    DATA str = top (stk);

    int len_init = strlen(str.STRING);

    str.STRING = realloc (str.STRING, (len_init * n_times + 1) * sizeof(char)); // realocar espaço suficiente para a string que irá resultar (e o \0)

    int i, j, write = len_init;

    for (i = 1; i < n_times; i++) // realizar a ação (n - 1) vezes

        for (j = 0; j < len_init; j++) // colocar todos os elementos da string inicial

            str.STRING[write++] = str.STRING[j];
    
    str.STRING[write] = '\0';
}

/*!
 * Função que usa as funções concat_strings_n_times() e concat_arrays_n_times() como auxiliares para realizar todas as operações em que
 * é necessário replicar um **array** ou **string** múltiplas vezes.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço da lista de **arrays**.
 */
void concat_n_times (STACK *stk, ARRAYSLIST arys) {

    DATA n_times = POP (stk);   // número de vezes a ser replicado

    if ( has_type(n_times, DOUBLE) ) {         // e for do tipo DOUBLE, o n_times.LONG (que será passado como argumento nas funções abaixo) 
                                               // passa a ter o valor do double (sem a parte decimal)
        n_times.LONG = (long) n_times.DOUBLE;
        n_times.type = LONG;
    }

    if ( has_type (top(stk), STRING) ) // testa se o topo é uma string

        concat_strings_n_times (stk, n_times.LONG);
    
    else 

        concat_arrays_n_times (stk, arys, n_times.LONG);
}

/*!
 * Função que testa se é para concatenar um **array** ou **string** múltiplas vezes.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token Caractere que será testado.
 * @returns !0, se for para utilizar o operador '*' sobre **arrays** ou **strings**;
 * @returns 0, caso contrário.
 */
int is_to_concat_n_times (STACK *stk, char token) {

    DATA elem2 = top (stk);
    DATA elem1 = stk->stack[stk->n_elems - 2];

    int cond_1 = token == '*',                                                                                     // o operador tem que ser o '*'
        cond_2 = ( has_type (elem2, LONG) && !elem2.is_array ) || has_type (elem2, DOUBLE),                        // o elemento do topo tem de ser um LONG (não flag) ou DOUBLE
        cond_3 = (has_type (elem1, STRING) && !elem1.is_block) ||  ( has_type (elem1, LONG) && elem1.is_array );   // o penult elemento tem de ser uma STRING ou ARRAY

    return cond_1 && cond_2 && cond_3;
}

// ,

/*!
 * Função que remove do topo da **stack** um **array** e coloca o número de elementos que o **array** tinha.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço da lista de **arrays**.
 */
void arrays_length (STACK *stk, ARRAYSLIST arys) {

    DATA array_flag = POP(stk);

    STACK *stk_array = find_addr_array (arys, array_flag.LONG);

    array_flag.LONG = stk_array->n_elems;

    array_flag.is_array = 0;

    PUSH (stk, array_flag);

    free (stk_array->stack);
    free (stk_array);
}

/*!
 * Função que remove do topo da **stack** uma **strinf** e coloca o número de elementos que a **string** tinha.
 * @param stk Endereço da **stack** a ser utilizada.
 */
void string_length (STACK *stk) {

    DATA str = POP(stk);

    str.LONG = (int) strlen (str.STRING);

    free (str.STRING);

    str.type = LONG;

    str.is_array = 0;

    PUSH (stk, str);
}

/*!
 * Função que usa as funções arrays_length() e string_length() para calcular o tamanho de um **array** ou uma **string** no topo da **stack**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço da lista de **arrays**.
 */
void operate_comma_length (STACK *stk, ARRAYSLIST arys) {

    if ( has_type (top (stk), STRING) ) // testa se é uma string

        string_length (stk);
    
    else 

        arrays_length (stk, arys);
}

/*!
 * Função que cria um *range* (**array** com os valores inteiros de 0 a n-1, sendo **n** o valor no topo da **stack**).
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço do endereço da lista de **arrays**;
 * @param current_array Endereço do valor inteiro do atual **array** (usado para as *flags*).
 */
void operate_comma_range (STACK *stk, ARRAYSLIST *arys, long *current_array) {

    DATA num = POP (stk);

    if( has_type (num, DOUBLE) ) {

        num.LONG = (long) num.DOUBLE;
        num.type = LONG;
    }

    STACK *stk_array = create_stack();

    long aux = num.LONG, i;

    for (i = 0; i < aux; i++) { // colocar os valores de 0 a n-1 no array

        num.LONG = i;
        
        PUSH(stk_array, num);
    }

   add_array (stk, arys, stk_array, current_array); // adicionar o array à lista de arrays
}

/*!
 * Função que usa as funções operate_comma_length() e operate_comma_range() para utilizar o operador ','.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço do endereço da lista de **arrays**;
 * @param current_array Endereço do valor inteiro do atual **array**.
 */
void operate_comma (STACK *stk, ARRAYSLIST *arys, long *current_array) {

    DATA elem_top = top (stk);

    if (has_type(elem_top, STRING) || (has_type(elem_top, LONG) && elem_top.is_array ))  // testa se o topo é uma string ou um array

         operate_comma_length (stk, *arys);

    else
         operate_comma_range(stk, arys, current_array);

}

/*!
 * Função que testa se o operador ',' é para ser executado.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token Caractere a ser testado.
 * @returns !0, se for para usar o operador ',';
 * @returns 0, caso contrário.
 */
int is_to_operate_comma (STACK *stk, char token) {

    DATA top_elem = top(stk);

    int cond_1 = token == ',',
        cond_2 = ( has_type (top_elem, LONG) && !top_elem.is_array) || has_type (top_elem, DOUBLE),                         // range
        cond_3 = (has_type (top_elem, STRING) && !top_elem.is_block) || ( has_type (top_elem, LONG) && top_elem.is_array ); // length

    return cond_1 && (cond_2 || cond_3);  
}

/// < >

// <

/*!
 * Função que "quebra" um **array** e fica apenas com os **n** primeiros elementos.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço da lista de **arrays**. 
 */
void n_elems_init_array (STACK *stk, ARRAYSLIST arys) {

    DATA n = POP (stk);

    if ( has_type (n, DOUBLE) ) 

        n.LONG = (long) n.DOUBLE;

    STACK *stk_array = find_addr_array (arys, top(stk).LONG);

    stk_array->n_elems = n.LONG;
}

/*!
 * Função que "quebra" uma **string** e fica apenas com os **n** primeiros caracteres.
 * @param stk Endereço da **stack** a ser utilizada.
 */
void n_chars_init_string (STACK *stk) {

    DATA n = POP (stk);

    if ( has_type (n, DOUBLE) ) 

        n.LONG = (long) n.DOUBLE;

    stk->stack[stk->n_elems - 1].STRING[n.LONG] = '\0';
}

/*!
 * Função que utiliza as funções n_elems_init_array() e n_chars_init_string() 
 * para manter apenas os **n** primeiros elementos de um **array** ou **string**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço da lista de **arrays**.
 */
void n_inits (STACK *stk, ARRAYSLIST arys) {

    if ( has_type (stk->stack[stk->n_elems - 2], STRING) )  // testa se o elemento abaixo do topo é uma string

        n_chars_init_string (stk);
    
    else

        n_elems_init_array (stk, arys);
}

// >

/*!
 * Função que "quebra" um **array** e fica apenas com os **n** últimos elementos.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço da lista de **arrays**. 
 */
void n_elems_lst_array (STACK *stk, ARRAYSLIST arys) {

    DATA n = POP (stk);

    if ( has_type (n, DOUBLE) ) 

        n.LONG = (long) n.DOUBLE;

    STACK *stk_array = find_addr_array (arys, top(stk).LONG);

    int i;

    for (i = 0; i < n.LONG; i++) // "empurar" para a esquerda os elementos

        stk_array->stack[i] = stk_array->stack[i + stk_array->n_elems - n.LONG];

    stk_array->n_elems = n.LONG;
}

/*!
 * Função que "quebra" uma **string** e fica apenas com os **n** últimos caracteres.
 * @param stk Endereço da **stack** a ser utilizada.
 */
void n_chars_lst_string (STACK *stk) {

    DATA n = POP (stk);
    DATA str = top (stk);

    if ( has_type (n, DOUBLE) ) 

        n.LONG = (long) n.DOUBLE;

    int i;

    for (i = 0; i < n.LONG; i++)

        str.STRING[i] = str.STRING[ i + strlen(str.STRING) - n.LONG ];
    
    str.STRING[i] = '\0';
}

/*!
 * Função que utiliza as funções n_elems_lst_array() e n_chars_lst_string() 
 * para manter apenas os **n** últimos elementos de um **array** ou **string**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço da lista de **arrays**.
 */
void n_lst (STACK *stk, ARRAYSLIST arys) {

    if ( has_type (stk->stack[stk->n_elems - 2], STRING)) // testa se o elemento abaixo do topo é uma string

        n_chars_lst_string (stk);
    
    else 

        n_elems_lst_array (stk, arys);
}

/*!
 * Função que utiliza as funções n_inits() e n_lst() como auxiliares para manter os **n** primeiros ou **n** últimos elementos de um **array** ou **string**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço da lista de **arrays**;
 * @param token Caractere a ser testado.
 */
void n_elems_init_or_lst (STACK *stk, ARRAYSLIST arys, char token) {

    if (token == '<')  // testa se é para ficar com os n primeiros

        n_inits (stk, arys);
    
    else 

        n_lst (stk, arys);
}

/*!
 * Função que testa se é para "cortar" um **array** ou uma **string**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token Caractere a ser testado.
 * @returns !0, se for para executar a operação;
 * @returns 0, caso contrário.
 */
int is_to_cut_array_or_str (STACK *stk, char token) {

    DATA elem2 = top(stk);
    DATA elem1 = stk->stack[stk->n_elems - 2];

    int cond_1 = token == '<' || token == '>',                                             // token têm que ser < ou >
        cond_2 = (has_type (elem2, LONG) && !elem2.is_array) || has_type (elem2, DOUBLE),  // o topo da stack tem que ser um long ou um double
        cond_3 = has_type (elem1, STRING) && !elem1.is_block,                              // o elemento abaixo do topo tem que ser uma string
        cond_4 = has_type (elem1, LONG) && elem1.is_array;                                 // ou um array

    return cond_1 && cond_2 && (cond_3 || cond_4);
}

/// ( )

// (

/*!
 * Função que remove o primeiro elemento de um **array** (primeiro a entrar) e o coloca na **stack**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço da lista de **arrays**.
 */
void remove_and_push_fst_array (STACK *stk, ARRAYSLIST arys) {

    DATA array_flag = top (stk);

    STACK *stk_array = find_addr_array (arys, array_flag.LONG);
    STACK **stk_double_pointer = &stk_array;                     // para poder alterar a stk->stack

    PUSH (stk, stk_array->stack[0]);

    ((*stk_double_pointer)->stack)++;
}

/*!
 * Função que remove o primeiro caractere de uma **string** e o coloca na **stack**.
 * @param stk Endereço da **stack** a ser utilizada.
 */
void remove_and_push_fst_string (STACK *stk) {

    DATA str = top(stk);

    DATA elem;
    elem.type = CHAR;
    elem.CHAR = str.STRING[0];

    int i, len = strlen(str.STRING);

    for (i = 0; i < len - 1; i++)

        str.STRING[i] = str.STRING[i + 1];  
    
    str.STRING[i] = '\0';

    PUSH (stk, elem);
}

/*!
 * Função que usa as funções remove_and_push_fst_array() e remove_and_push_fst_string() para remover e adicionar
 * o primeiro elemento/caractere do/a array/string na **stack**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço da lista de **arrays**.
 */
void remove_and_push_fst (STACK *stk, ARRAYSLIST arys) {

    if ( has_type (top (stk), STRING) ) // testa se o topo é uma string

        remove_and_push_fst_string (stk);
    
    else 

        remove_and_push_fst_array (stk, arys);
}

// )

/*!
 * Função que remove o último elemento de um **array** (último a entrar) e o coloca na **stack**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço da lista de **arrays**.
 */
void remove_and_push_lst_array (STACK *stk, ARRAYSLIST arys) {

    DATA array_flag = top (stk);

    STACK *stk_array = find_addr_array (arys, array_flag.LONG);

    array_flag = POP (stk_array); // subtitui a array_flag pelo último elemento no array

    PUSH (stk, array_flag); // acrescenta o elemento à stack
}

/*!
 * Função que remove o último caractere de uma **string** e o coloca na **stack**.
 * @param stk Endereço da **stack** a ser utilizada.
 */
void remove_and_push_lst_string (STACK *stk) {

    DATA str = top (stk);

    DATA elem;

    elem.type = CHAR;
    elem.CHAR = str.STRING[strlen(str.STRING) - 1];

    str.STRING[strlen(str.STRING) - 1] = '\0';

    PUSH (stk, elem);
}

/*!
 * Função que usa as funções remove_and_push_lst_array() e remove_and_push_lst_string() para remover e adicionar
 * o último elemento/caractere do/a array/string na **stack**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço da lista de **arrays**.
 */
void remove_and_push_lst (STACK *stk, ARRAYSLIST arys) {

    if ( has_type (top (stk), STRING) )

        remove_and_push_lst_string (stk);
    
    else 

        remove_and_push_lst_array (stk, arys);
}

/*!
 * Função utilizada para remover um elemento de um **array** ou de uma **string** e adicionar na **stack**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço da lista de **arrays**;
 * @param token Caractere que corresponde a um operador (uma vez que entrar nesta função significa que as condições na função is_to_remove_and_push() foram satisfeitas).
 */
void remove_and_push (STACK *stk, ARRAYSLIST arys, char token) {

    if (token == '(')                      // testa se é para remover do início

        remove_and_push_fst (stk, arys);
    
    else 

        remove_and_push_lst (stk, arys);
}

/*!
 * Função que testa se é para utilizar a função remove_and_push() sobre a **stack**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token Caractere a ser testado.
 * @returns !0, se for para executar o operador '(' ou ')' a um **array** ou **string**;
 * @returns 0, caso contrário.
 */
int is_to_remove_and_push (STACK *stk, char token) {

    DATA top_stack = top (stk);

    int cond_1 = token == ')' || token == '(',                         // o caractere é ( ou )
        cond_2 = has_type (top_stack, STRING) && !top_stack.is_block,  // o topo da stack é uma string
        cond_3 = has_type (top_stack, LONG) && top_stack.is_array;     // o topo da stack é um array
    
    return cond_1 && (cond_2 || cond_3);
}

// =

/*!
 * Função que remove um **array** do topo da **stack** e um valor numérico e adiciona o valor que está na posição **n** (definida pelo valor numérico) do **array** na **stack**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço do endereço da lista de **arrays** a ser utilizada.
 */
void ind_value_array (STACK *stk, ARRAYSLIST *arys) {

    DATA ind = POP (stk);
    DATA array_flag = POP (stk);

    if ( has_type (ind, DOUBLE) ) 

        ind.LONG = (long) DOUBLE;

    STACK *stk_array = find_addr_array (*arys, array_flag.LONG);

    ind = stk_array->stack[ind.LONG];

    free (stk_array->stack);
    free (stk_array);

    PUSH (stk, ind);
}

/*!
 * Função que remove uma **string** do topo da **stack** e um valor numérico e adiciona o valor que está na posição **n** (definida pelo valor numérico) da **string** na **stack**.
 * @param stk Endereço da **stack** a ser utilizada.
 */
void ind_value_string (STACK *stk) {

    DATA ind = POP (stk);
    DATA str = POP (stk);

    if (has_type (ind, DOUBLE))

        ind.LONG = (long) DOUBLE;

    ind.CHAR = str.STRING[ind.LONG];       

    free (str.STRING);

    ind.type = CHAR;

    PUSH (stk, ind);
}

/*!
 * Função que utiliza as funções ind_value_array() e ind_value_string() como auxiliares para utilizar o operador '=' sobre **arrays** ou **strings**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço do endereço da lista de **arrays** a ser utilizada.
 */
void ind_value (STACK *stk, ARRAYSLIST *arys) {

    if (has_type (stk->stack[stk->n_elems - 2], STRING))  // testa se o elemento abaixo do topo é uma string

        ind_value_string (stk);
    
    else 

        ind_value_array (stk, arys);
}

/*!
 * Testa se é para utilizar o operador '=' sobre **arrays** ou **strings** (e um elemento com um valor numérico).
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token Caractere a ser testado.
 * @returns !0, se for para "ir buscar" o elemento no índice **n** de um **array** ou **string**;
 * @returns 0, caso contrário.
 */
int is_to_take_ind (STACK *stk, char token) {

    DATA elem2 = top (stk);
    DATA elem1 = stk->stack[stk->n_elems - 2];

    int cond_1 = token == '=',
        cond_2 = has_type (elem2, DOUBLE) || ( has_type (elem2, LONG) && !elem2.is_array),
        cond_3 = has_type (elem1, STRING) && !elem1.is_block,
        cond_4 = has_type (elem1, LONG) && elem1.is_array;

    return cond_1 && cond_2 && (cond_3 || cond_4);
}

// #

/*!
 * Função que encontra primeira ocorrência de uma **substring** numa **string** e devolve o índice onde começa.
 * @param stk Endereço da **stack** a ser utilizada.
 * @returns índide da posição da **string** onde começa a **substring**, se encontrar. Se não encontrar, retorna o valor -1.
 */
void find_ind (STACK *stk) {

    DATA sub_str = POP (stk);
    DATA str = POP (stk);

    if (has_type (sub_str, CHAR)) {

        sub_str.type = STRING;
        sub_str.STRING = (char *) malloc (2 * sizeof (char));
        sub_str.STRING[0] = sub_str.CHAR;
        sub_str.STRING[1] = '\0';
    }

    int sub_str_len = (int) strlen (sub_str.STRING),
        str_len = (int) strlen (str.STRING);

    int i, j, r = -1;

    for (i = 0; i <= str_len - sub_str_len; i++)   // percorre a string principal até encontrar a ocorrência da subtring 
                                                   // ou até número de elementos restantes ser maior ou igual do que o tamanho da substring

        for (j = 0; j < sub_str_len; j++) {        // percorre a substring

            if (str.STRING[i + j] != sub_str.STRING[j])  

                j = sub_str_len;   // se um dos elementos não coincidir, o ciclo for acaba (o mais interior) 
            
            else 
                
                if (j == sub_str_len - 1) {  // se já estiver na última posição e não entrou na condição acima significa que foi encontrada a posição onde começa a ocorrência da subtring, 
                                             // pelo que o ciclo for (mais externo) deve acabar
                    r = i;

                    i = str_len - sub_str_len;
                }
        }
    
    free (str.STRING);
    free (sub_str.STRING);

    str.type = LONG;
    str.LONG = (long) r;
    str.is_array = 0;

    PUSH (stk, str);
}

/*!
 * Testa se é para utilizar o operador '#' sobre duas **strings** ou um caractere e uma **string**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token Caractere a ser testado.
 * @returns !0, se for para executar o comando '#';
 * @returns 0, caso contrário.
 */
int is_to_find_ind (STACK *stk, char token) {

    DATA elem2 = top(stk);
    DATA elem1 = stk->stack[stk->n_elems - 2];

    int cond_1 = token == '#',                                                             // o caractere é o '#'
        cond_2 = (has_type (elem2, STRING) && !elem2.is_block) || has_type (elem2, CHAR),  // o topo da stack é uma string ou um char
        cond_3 = has_type (elem1, STRING) && !elem1.is_block;                              // o elemento abaixo do topo é uma string
    
    return cond_1 && cond_2 && cond_3;
}

// /

/*!
 * Função que testa se uma **substring** está a ocorrer numa **string** na posição **bg** fornecida como input.
 * @param str **String** principal;
 * @param bg Posição inicial da **string** principal para testar;
 * @param sub_str **Substring** que poderá estar na **string** principal.
 * @returns 1, se a **substring** ocorrer na **string** na posição **bg**;
 * @returns 0, caso contrário.
 */
int is_to_stop (char *str, int bg, char *sub_str) {

    int i, 
        r = 1,
        len = strlen(sub_str);

    if ((int) strlen (str) - bg < len) return 0;  // tamanho restante da string menor do que o tamanho da substring

    for (i = 0; i < len; i++)

        if (str[i + bg] != sub_str[i]) {

            r = 0;

            i = len - 1;
        }
    
    return r;
}

/*!
 * Função que coloca o conteúdo de uma zona de uma **string** numa **subtring** usado a função is_to_stop() para testes.
 * @param str **String** que fornece caracteres;
 * @param sub_str **Substring** de teste;
 * @param str_dest **String** que recebe caracteres da **string**;
 * @param current_pos Endereço da posição onde começará a ser lida a **string**.
 */
void separate_aux (char *str, char *sub_str, char *str_dest, int *current_pos) {

    int w = 0;

    while (!is_to_stop (str, *current_pos, sub_str) && str[(*current_pos)] != '\0') {

        str_dest[w] = str[(*current_pos)];

        (*current_pos)++; w++;
    }

    str_dest[w] = '\0';

    (*current_pos) += strlen (sub_str);
}

/*!
 * Função que separa uma **string** em **subtrings** para um **array** e adiciona o **array** à **stack** e à lista de **arrays**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço do endereço da lista de **arrays**;
 * @param current_array Endereço do número de **arrays** adicionados desde a execução.
 */
void separate_in_sub_str (STACK *stk, ARRAYSLIST *arys, long *current_array) {

    DATA sub_str = POP (stk);
    DATA str = POP (stk);

    if (has_type (sub_str, CHAR)) {

        sub_str.STRING = (char *) malloc (2 * sizeof (char));
        sub_str.STRING[0] = sub_str.CHAR;
        sub_str.STRING[1] = '\0';
    } 

    STACK *stk_array = create_stack();

    int current_pos = 0, 
        len = strlen(str.STRING) + 1;

    DATA elem_to_push;

    elem_to_push.type = STRING;
    elem_to_push.is_block = 0;

    while (current_pos < len - 1) {
        
        elem_to_push.STRING = (char *) malloc ( len * sizeof (char) );

        separate_aux (str.STRING, sub_str.STRING, elem_to_push.STRING, &current_pos);

        PUSH (stk_array, elem_to_push);
    }

    add_array (stk, arys, stk_array, current_array);

    free (sub_str.STRING);
    free (str.STRING);
}

/*!
 * Função que testa se é para aplicar o operador que separa uma **string** em **substrings**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token Caractere a ser testado.
 * @returns !0, se for para aplicar a operação;
 * @returns 0, caso contrário.
 */
int is_to_separate (STACK *stk, char token) {

    DATA elem2 = top (stk);
    DATA elem1 = stk->stack[stk->n_elems - 2];

    int cond_1 = token == '/',                                   
        cond_2 = has_type (elem1, STRING) && !elem1.is_block,  // o elemento abaixo do topo é uma string
        cond_3 = has_type (elem2, CHAR),                       // o elemento do topo é um caractere
        cond_4 = has_type (elem2, STRING) && !elem2.is_block;  // o elemento do topo é string 
    
    return cond_1 && cond_2 && (cond_3 || cond_4);
}

// S/

/*!
 * Função que coloca numa **string** de destino todos os caracteres da **string** principal até chegar a um caracter de espaço.
 * @param str **String** que fornece caracteres;
 * @param str_dest **String** que recebe caracteres da **string**;
 * @param current_pos Endereço da posição onde começará a ser lida a **string**.
 */
void separate_aux_spaces (char *str, char *str_dest, int *current_pos) {
    
    int w = 0;

    while (str[(*current_pos)] != '\0' && str[(*current_pos)] != ' ' && str[(*current_pos)] != '\n') {

        str_dest[w] = str[*current_pos];

        (*current_pos)++; w++;
    }

    str_dest[w] = '\0';
}

/*!
 * Função que separa uma **string** nos caracteres de espaço e coloca as **substrings** num **array**. Após isso, adiciona o **array** à **stack** e à lista de **arrays**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço do endereço da lista de **arrays**;
 * @param current_array Endereço do número de **arrays** adicionados desde a execução.
 */
void separate_by_space (STACK *stk, ARRAYSLIST *arys, long *current_array) {

    DATA str = POP (stk);

    STACK *stk_array = create_stack();

    int current_pos = 0;

    while (current_pos < (int) strlen(str.STRING)) {

        DATA elem_to_push;

        elem_to_push.type = STRING;
        elem_to_push.is_block = 0;
        elem_to_push.STRING = (char *) malloc ( (strlen(str.STRING) + 1 ) * sizeof (char) );

        separate_aux_spaces (str.STRING, elem_to_push.STRING, &current_pos);

        while (str.STRING[current_pos] == ' ' || str.STRING[current_pos] == '\n') current_pos++;

        PUSH (stk_array, elem_to_push);
    }

    free (str.STRING);

    add_array (stk, arys, stk_array, current_array);
}

/*!
 * Função que testa se é para separar uma **string** por espaços.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token **string** a ser testada.
 * @returns !0, se for para aplicar a operação em questão;
 * @returns 0, caso contrário. 
 */
int is_to_separate_by_spaces (STACK *stk, char *token) {

    DATA top_stack = top(stk);

    int cond_1 = token[0] == 'S' && token[1] == '/' && !token[2],
        cond_2 = has_type (top_stack, STRING) && !top_stack.is_block;
    
    return cond_1 && cond_2;
}

// N/

/*!
 * Função que separa uma **string** nos caracteres de quebra de linha e coloca as **substrings** num **array**. Após isso, adiciona o **array** à **stack** e à lista de **arrays**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço do endereço da lista de **arrays**;
 * @param current_array Endereço do número de **arrays** adicionados desde a execução.
 */
void separate_by_n (STACK *stk, ARRAYSLIST *arys, long *current_array) {

    DATA str = POP (stk);

    STACK *stk_array = create_stack();

    int current_pos = 0;

    while (current_pos < (int) strlen(str.STRING)) {

        DATA elem_to_push;

        elem_to_push.type = STRING;
        elem_to_push.is_block = 0;
        elem_to_push.STRING = (char *) malloc ( strlen(str.STRING) * sizeof (char) );

        separate_aux (str.STRING, "\n", elem_to_push.STRING, &current_pos);

        PUSH (stk_array, elem_to_push);
    }

    free (str.STRING);

    add_array (stk, arys, stk_array, current_array);
}

/*!
 * Função que testa se é para separar uma **string** por quebras de linha.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token **string** a ser testada.
 * @returns !0, se for para aplicar a operação em questão;
 * @returns 0, caso contrário. 
 */
int is_to_separate_by_n (STACK *stk, char *token) {

    DATA top_stack = top(stk);

    int cond_1 = token[0] == 'N' && token[1] == '/' && !token[2],
        cond_2 = has_type (top_stack, STRING) && !top_stack.is_block;
    
    return cond_1 && cond_2;
}

/*!
 * Função que imprime o topo da **stack**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço da lista de **arrays**.
 */
void command_p (STACK *stk, ARRAYSLIST arys) {

    DATA top_stack = top (stk);

    switch (top_stack.type) {
            
        case LONG:
            
            if (top_stack.is_array)

                print_stack (find_addr_array (arys, top_stack.LONG), arys);

            else 
                    
                printf("%ld", top_stack.LONG);
                
            break;

        case DOUBLE: 
                
            printf("%g", top_stack.DOUBLE);
                
            break;
            
        case CHAR:
                
            printf("%c", top_stack.CHAR);
                
            break;
            
        case STRING:

            if (top_stack.is_block) putchar('{');
                
            printf("%s", top_stack.STRING);

            if (top_stack.is_block) putchar('}');
                
            break;
    }
    putchar ('\n');
}

/*!
 * Função que testa, a partir do estado da **stack** e de uma dada **string** (*token*), se é para realizar uma operação sobre **arrays** ou **strings**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param token **String** que poderá corresponder a um operador.
 * @return 1 se corresponder ao operador '~';
 * @return 2 se corresponder ao operador '+';
 * @return 3 se corresponder ao operador '*';
 * @return 4 se corresponder ao operador ',';
 * @return 5 se corresponder ao operador '<' ou '>';
 * @return 6 se corresponder ao operador '(' ou ')';
 * @return 7 se corresponder ao operador '=';
 * @return 8 se corresponder ao operador '#';
 * @return 9 se corresponder ao operador '/';
 * @return 10 se corresponder ao operador "S/";
 * @return 11 se corresponder ao operador "N/";
 * @return 12 se corresponder ao operador 'p';
 * @return 0 se não for para realizar uma das operações descritas acima.
 */
int operator_arrays_or_strings (STACK *stk, char *token) {

    if       (is_to_put (stk, *token))         return 1;    // ~


    if      (is_to_concat (stk, *token))       return 2;    // +


    if  (is_to_concat_n_times (stk, *token))   return 3;    // *


    if   (is_to_operate_comma (stk, *token))   return 4;    // ,


    if  (is_to_cut_array_or_str (stk, *token)) return 5;    // < >


    if  (is_to_remove_and_push (stk, *token))  return 6;    // ( )


    if     (is_to_take_ind (stk, *token))      return 7;    // =


    if     (is_to_find_ind (stk, *token))      return 8;    // #


    if     (is_to_separate (stk, *token))      return 9;    // /


    if (is_to_separate_by_spaces (stk, token)) return 10;   // S/


    if   (is_to_separate_by_n (stk, token))    return 11;   // N/


    if      (*token == 'p' && !token[1])       return 12;   // p


    return 0;
}

/*!
 * Função que realiza todas as operações sobre **arrays** e **strings** usando todas as restantes como auxiliares.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param arys Endereço do endereço da lista de **arrays**;
 * @param token **String** que poderá corresponder a um operador;
 * @param current_array Endereço no número atual de **arrays**.
 */
void operate_arrays_or_strings (STACK *stk, ARRAYSLIST *arys, char *token, long *current_array) {

    switch (operator_arrays_or_strings (stk, token)) {

        case 1:

            put_array_in_stack (stk, arys);
            break;
        
        case 2:

            concat (stk, *arys);
            break;
        
        case 3:

            concat_n_times (stk, *arys);
            break;
        
        case 4:

            operate_comma (stk, arys, current_array);
            break;
        
        case 5:

            n_elems_init_or_lst (stk, *arys, *token);
            break;
        
        case 6:

            remove_and_push (stk, *arys, *token);
            break;
        
        case 7:

            ind_value (stk, arys);
            break;
        
        case 8:

            find_ind (stk);
            break;
        
        case 9:

            separate_in_sub_str (stk, arys, current_array);
            break;

        case 10:

            separate_by_space (stk, arys, current_array);
            break;
        
        case 11:

            separate_by_n (stk, arys, current_array);
            break;
        
        case 12:

            command_p (stk, *arys);
            break;
    }
}