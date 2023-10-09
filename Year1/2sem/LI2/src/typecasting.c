/**
 * @file Ficheiro que inclui as funções que realizam a conversão de tipos 
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "stack.h"
#include "typecasting.h"

/**
 @file typecasting.c
 \brief Ficheiro que inclui as funções que realizam a conversão de tipos 
*/

/*!
 * Esta função testa se o caractere recebido com input é um dos operadores que as funções operator_i(), operator_f(), operator_c(), operator_s() utiliza.
 * @param token Caractere que será testado.
 * @returns 0 Se não for um operador usado nas várias funções;
 * @returns 1 Se for um operador e precisar de, pelo menos um elemento na *stack*.
 */
int operator_change_type (char token) {
      
    char operators_to_change[4] = "ifcs";
    
    int i, r = 0;

    for(i = 0; i<4; i++)                             
        if (token == operators_to_change[i]) {                  //verifica se o token é um dos operadores
            r = 1;
            i = 3;
        }
    
    return r;
}

/*!
 * Função responsável por executar os comandos **i**.
 * @param stk Corresponde ao endereço da *stack* que a programa está a utilizar.
 * ### Descrição
 * Esta função converte o topo da *stack* para um LONG.
 */
void operator_i (STACK *stk) {

    DATA elem = POP(stk);

    switch (elem.type) {
                                                               //verificar qual o operador e trocar o tipo
        case LONG:
            break;

        case DOUBLE:
            elem.LONG = (long) elem.DOUBLE;
            elem.is_array = 0;
            break;

        case CHAR: 
            elem.LONG = (long) elem.CHAR;
            elem.is_array = 0;
            break;

        case STRING:
            elem.LONG = atol(elem.STRING);
            elem.is_array = 0;
            free (elem.STRING);
            break;        
    }

    elem.type = LONG;
    elem.is_array = 0;
    PUSH(stk, elem);
}

/*!
 * Função responsável por executar os comandos **f**.
 * @param stk Corresponde ao endereço da *stack* que a programa está a utilizar.
 * ### Descrição
 * Esta função converte o topo da *stack* para um **DOUBLE**.
 */
void operator_f (STACK *stk) {

    DATA elem = POP(stk);

    switch (elem.type) {

        case DOUBLE:
            break;

        case LONG:
            elem.DOUBLE = (double) elem.LONG;
            break;

        case CHAR:
            elem.DOUBLE = (double) elem.CHAR;
            break;

        case STRING:
            elem.DOUBLE = atof(elem.STRING);
            free (elem.STRING);
            break;        
    }
    
    elem.type = DOUBLE;    
    PUSH(stk, elem);   
}

/*!
 * Função responsável por executar os comandos **c**.
 * @param stk Corresponde ao endereço da *stack* que a programa está a utilizar.
 * ### Descrição
 * Esta função converte o topo da *stack* para um **CHAR**.
 */
void operator_c (STACK *stk) {

    DATA elem = POP(stk);

    switch (elem.type) {

        case CHAR:
            break;

        case DOUBLE:
            elem.CHAR = (char) elem.DOUBLE;
            break;

        case LONG: 
            elem.CHAR = (char) elem.LONG;
            break;

        case STRING:
            elem.CHAR = (*elem.STRING);
            free (elem.STRING);
            break;        
    }

    elem.type = CHAR;           
    PUSH(stk, elem);
}

/*!
 * Função responsável por executar os comandos **s**.
 * @param stk Corresponde ao endereço da *stack* que a programa está a utilizar.
 * ### Descrição
 * Esta função converte o topo da *stack* para uma **STRING**.
 */
void operator_s (STACK *stk) {

    if (!(has_type (top(stk), STRING))) {

        DATA elem = POP(stk);

        elem.STRING = (char *) malloc (20 * sizeof (char));

        switch (elem.type) {                            

            case STRING:
                break;

            case LONG:
                sprintf(elem.STRING, "%ld", elem.LONG);
                break;

            case DOUBLE:
                sprintf(elem.STRING, "%lf", elem.DOUBLE);
                break;

            case CHAR: 
                *elem.STRING = elem.CHAR;
                break;      
        }
    
        elem.type = STRING;
        elem.is_block = 0;
    
        PUSH(stk, elem);
    }
}

/*!
 * Função testa qual é o operador em questão e executa.
 * @param stk Corresponde ao endereço da *stack* que a programa está a utilizar;
 * @param token Caracter a ser testado.
 * ### Descrição  
 * Esta determina qual é o operador em questão e executa conforme o operador utilizado.
 */
void type_Casting (STACK *stk, char token) {

    switch (token) {                        // verifica qual o operador        

        case 'i':                           // caso seja 'i', executa a função operator_i
            operator_i (stk);   
            break;

        case 'f':                           // caso seja 'f', executa a função operator_f
            operator_f (stk);
            break;

        case 'c':                           // caso seja 'c', executa a função operator_c
            operator_c (stk);
            break;

        case 's':                           // caso seja 's', executa a função operator_s
            operator_s (stk);
            break;            
    }
}
