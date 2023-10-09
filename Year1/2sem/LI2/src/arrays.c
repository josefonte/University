/**
 * @file Ficheiro que inclui as funções que realizam operações auxiliares sobre **arrays**
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "stack.h"
#include "arrays.h"

/**
 @file arrays.c
 \brief Ficheiro que inclui as funções que realizam operações auxiliares sobre **arrays**
*/

/*!
 * Função que adiciona um dado elemento no topo da *stack* tendo em conta as suas características.
 * @param stk Endereço da *stack* a ser utilizada;
 * @param token **String** a ser convertida e adicionada no topo da *stack*.
 */
void push_by_type (STACK *stk, char *token) {

    DATA elem;

    char *sobra;
        
    long valint = strtol(token,&sobra, 10);
        
    if (strlen(sobra) == 0) {         // o elemento é do tipo lONG

            elem.type = LONG;
            elem.LONG = valint;
            elem.is_array = 0;
            PUSH(stk, elem);
    }

    else {
            
        double valdoub = strtod(token, &sobra);

        if (strlen(sobra) == 0) {    // o elemento é do tipo DOUBLE

            elem.type = DOUBLE;
            elem.DOUBLE = valdoub;
            PUSH(stk, elem);
        }

        else {
        
            if (token[1] == '\0') { // o elemento é do tipo CHAR

                elem.type = CHAR;
                elem.CHAR = *token;

                PUSH(stk, elem);
            }

            else { // qualquer input válido nunca seguirá para a condição abaixo

                elem.type = STRING;
                elem.is_block = 0;
                elem.STRING = token;

                PUSH(stk, elem);
            }
        }
    } 
}

// STRINGS

/*!
 * Função que retorna o tamanho que terá a **string** usada como *token* (ou seja, quantas posições há até ao próximo espaço, quebra de linha ou *tab*).
 * @param line Linha lida no início da execução;
 * @param current_pos endereço da posição atual na **line**;
 * @returns tamanho que deverá ter a **string**.
 */
int len_string (char *line, int *current_pos) {

    int i;

    for (i = 1; line[i + *current_pos] != '"'; i++);

    (*current_pos) += i + 1; // incrementa a posição a ser lida

    return i - 1;
}

/*!
 * Função que lê uma **string** contida na linha lida.
 * @param line **String** a ser lida;
 * @param start Indica a posição em que a **string** começará a ser lida;
 * @param stop Indica onde a posição final a ser lida;
 * @param string_dest **String** que vai receber o que está contido na *line* entre a posição **start** e **stop**.
 */
void read_string (char *line, int start, int stop, char *string_dest) {

    int i;

    for (i = start; i <= stop; i++)

        string_dest[i - start] = line[i];
    
    string_dest[i - start] = '\0';
}

/*!
 * Função que retorna a última posição na **string** (delimitada por espaços, quebras de linha e *tabs*).
 * @param line Linha lida no início da execução do programa;
 * @param init Índice da posição atual da **string**.
 * @returns número de caracteres entre o *init* e um dos delimitadores.
 */
int last_pos_token (char *line, int init) {

    int i = 0;

    while (line[i + init] != ' ' && line[i + init] != '\n' && line[i + init] != '\t' && line[i + init]) i++;

    return i;
}

// ARRAYS

/// criar a struct

/*!
 * Função que cria uma lista ligada (vazia) de **arrays**.
 * @returns o endereço da lista.
 */
ARRAYSLIST create_arrays () {

    ARRAYSLIST arys = NULL;

    return arys;
}

/// funções auxiliares para ler a string para o array

/*!
 * Função que calcula e retorna o tamanho de um **array**;
 * @param line Linha lida no início da execução (no contexo onde é chamada);
 * @param init índice da posição atual da linha que o programa está a analisar.
 * @returns tamanho que terá o array.
 */
int array_len (char *line, int *current_pos) {

    int len = 0,                             // contador
        par_left = 1,                        // número de [
        par_right = (*line == ']') ? 1 : 0;  // número de ]
    
    while (par_left != par_right) {

        len++;

        if (line[len + *current_pos] == '[') par_left++;

        else if (line[len + *current_pos] == ']') par_right++; 
    }

    (*current_pos) += len + 1;

    return len - 1;
}

/*!
 * Função que cria o **array** com base da linha de input;
 * @param line **String** inserida como input.
 * @param array_dest **Array** a ser criado (ainda como **string**);
 * @param init_pos posição atual da linha;
 * @param last_pos última posição onde é suposto chegar na linha.
 */
void get_array (char *line, char *array_dest, int init_pos, int last_pos) {

    int i;

    for (i = init_pos; i <= last_pos; i++)

        array_dest[i - init_pos] = line[i];
    
    array_dest[i - init_pos] = '\0';
}

// incluir o array na struct 

/*!
 * Função cujo objetivo é inserir um array na lista ligada de **arrays** que o programa utilia durante a execução. 
 * Coloca também uma *flag* (ou *id*) do **array** na *stack* para simbolozar que naquela posição está um **array**.
 * @param stk Endereço da *stack* a ser manipulada;
 * @param arys_list Endereço do endereço da lista ligada de **arrays**;
 * @param array_to_include Endereço da **stack** que o novo **array** representará;
 * @param current_array *ID* do próximo **array** da lista ligada.
 * @note É passado um duplo *pointer* para que este possa ser alterado.
 */
void add_array (STACK *stk, ARRAYSLIST *arys_list, STACK *array_to_include, long *current_array) {

    ARRAY ary = malloc (sizeof (struct array));   // novo array

    ary->array = array_to_include;
    ary->NAME = *current_array;

    DATA array_flag;                     // marcador a adicionar na stack

    array_flag.type = LONG;
    array_flag.is_array = 1;
    array_flag.LONG = *current_array;

    (*current_array)++;

    ARRAYSLIST new_list = malloc (sizeof(struct arrayslist));

    new_list->ary1 = ary;           // colcar o novo array na cabeça da lista
    new_list->rest = *arys_list;    // colpcar o resto no endereço para a próxima célula

    (*arys_list) = new_list;        // trocar o valor do pointer para o endereço da nova lista de arrays

    PUSH (stk, array_flag);         // adicionar a flag do array na stack
}