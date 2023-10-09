/**
 * @file Ficheiro que inclui a função que executa o comando 'l'
 */
 
#include <stdlib.h>
#include <string.h>
#include "stack.h"
#include "commandl.h"

/**
 @file commandl.c
 \brief Ficheiro que inclui a função que executa o comando 'l'
 */

/*!
 * A função, a partir do **bg** (índice do inicio da linha a ser lida), calcula o **end** (índice do próximo "\n") e cria um **DATA** auxliar com a **string**
 * que se encontra entre esses dois índices. \n \n
 * De seguida, envia um **DATA** (declarado como **elem**) com essa **string** e com *elem.type = STRING*. 
 * @param stk endereço da *stack* a ser manipulada; 
 * @param line_aux **string** que contém todos os caracteres após o primeiro "\n";
 * @param bg índice do inicio da próxima linha.
 */ 
void execute_L (STACK* stk, char* line_aux, int* bg) {

    int end; 

    for (end = (*bg); line_aux[end] != '\n'; end++);
     
    DATA elem;

    char *aux = line_aux + (*bg);
    aux[end-(*bg)] = '\0';

    (*bg) = end + 1;

    int k;

    elem.type = STRING;
    elem.is_block = 0;
    elem.STRING = (char *) malloc ((strlen(aux) + 1) * sizeof(char));

    for (k = 0; k < (int) strlen(aux); k++)

        elem.STRING[k] = aux[k];
    
    elem.STRING[k] = '\0';

    PUSH (stk, elem);
}