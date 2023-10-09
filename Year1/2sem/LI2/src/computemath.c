/**
 * @file Ficheiro que inclui as funções que operam sobre a *stack* quando surgem operadores matemáticos 
 */

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "computemath.h"
#include "stack.h"

/**
 @file computemath.c
 \brief Ficheiro que inclui as funções que operam sobre a *stack* quando surgem operadores matemáticos 
*/

/*!
* A função retorna o número de argumentos que o operador recebe. No caso de não ser um operador matemático, devolve 0.
* @param token que representa um caractere da **string**.
* @returns 0 se não for operador matemático; 
* @returns 1 se for um operador matemático que utiliza um operando; 
* @returns 2 se for um operador matemático que utiliza dois operandos.
*/ 
int operator_math(char a) {
    char operators[12] = "()~+-*/%#&|^"; 
    int i, r = 0;

    for (i = 0; i<12; i++)
        if (a == operators[i]) {
            r = (i<3) ? 1 : 2;
            i = 12;
        }

    return r;  
}

/*!
 * Função que realiza operações que apenas necessitam de uma argumento.
 * @param *stk Endereço da *stack* que está a ser utilizada; 
 * @param token Caractere que correponde a um operador.
 */
void compute_math_one_operator (STACK *stk, char token) {

    DATA elem = POP(stk);

    switch (token) {

        case '(':
            if (elem.type == LONG) elem.LONG--;
            else {
                if (elem.type == DOUBLE) elem.DOUBLE--;
                else elem.CHAR--;
            }

            PUSH(stk, elem);
            break;

        case ')':
            if (elem.type == LONG) elem.LONG++;
            else {
                if (elem.type == DOUBLE) elem.DOUBLE++;
                else elem.CHAR++;
            }
                    
            PUSH(stk, elem);
            break;

        case '~':
            elem.LONG = ~elem.LONG;
            PUSH(stk, elem);
    }
}

/*!
 * Função que realiza as operações de soma, subtração, multiplicação e divisão.
 * @param stk Endereço da *stack* que está a ser utilizada; 
 * @param token Caractere que correponde a um operador.
 * @param type Tipe de dados do **elem1** e **elem2**;
 * @param elem1 Primeiro elemento do tipo **DATA**;
 * @param elem2 Primeiro elemento do tipo **DATA**.
 */
void arithmetic_operations (STACK *stk, char token, int type, DATA elem1, DATA elem2) {
    
    switch (token) {

        case '+':
      
            if (type == 2) {
                elem1.DOUBLE += elem2.DOUBLE; 
                PUSH(stk, elem1);
            } 
            else {
                elem1.LONG += elem2.LONG; 
                PUSH(stk, elem1);
            }

            break;

        case '-':
      
            if (type == 2) {
                elem1.DOUBLE -= elem2.DOUBLE; 
                PUSH(stk, elem1); 
            }
            else {
                elem1.LONG -= elem2.LONG; 
                PUSH(stk, elem1);
            }

            break;

        case '*':
      
            if (type == 2) {
                elem1.DOUBLE *= elem2.DOUBLE; 
                PUSH(stk, elem1); 
            }
            else {
                elem1.LONG *= elem2.LONG; 
                PUSH(stk, elem1);
            }

            break;
            

        case '/':
      
            if (type == 2) {
                elem1.DOUBLE /= elem2.DOUBLE; 
                PUSH(stk, elem1); 
            }
            else {
                elem1.LONG /= elem2.LONG; 
                PUSH(stk, elem1);
            }

            break;
    }
}

/*!
 * Função que realiza operações sobre a *stack* que necessitam de dois operandos.
 * @param stk Endereço da *stack* que está a ser utilizada; 
 * @param token Caractere que correponde a um operador.
 */
void compute_math_two_operators (STACK *stk, char token) {

    DATA elem2 = POP(stk);
    DATA elem1 = POP(stk);

    int type = 1;
                
    if ((elem1.type == DOUBLE) || (elem2.type == DOUBLE)) {  // Se um dos elementos for do tipo **DOUBLE**, então ambos têm que o ser 
        
        if (elem1.type == LONG) {
            elem1.DOUBLE = (double) elem1.LONG;
            elem1.type = DOUBLE;
        }

        else 
            if (elem2.type == LONG) {
                elem2.DOUBLE = (double) elem2.LONG;
                elem2.type = DOUBLE;
            }
        
        type = 2;
    } 

    if (token=='+'|| token=='-'|| token=='*'|| token=='/') 
        arithmetic_operations(stk,token, type, elem1, elem2);
    
    else{ 
        
        switch (token) {

            case '%':
                elem1.LONG %= elem2.LONG;
                PUSH(stk, elem1);
                break;

            case '#':
                if (type == 2) {
                    elem1.DOUBLE = pow (elem1.DOUBLE,elem2.DOUBLE); 
                    PUSH(stk, elem1); 
                }
                else {
                    elem1.LONG = pow (elem1.LONG,elem2.LONG);
                    PUSH(stk, elem1);
                }

                break;
    
            case '&':
                elem1.LONG &= elem2.LONG;
                PUSH(stk, elem1); 
                break;

            case '|':
                elem1.LONG |= elem2.LONG;
                PUSH(stk, elem1);
                break;

            case '^':
                elem1.LONG ^= elem2.LONG;
                PUSH(stk, elem1);
        }
    }
}

/*!
* Função responsável por todos os cálculos entre um ou mais elementos do topo da *stack*.
* @param stk que corresponde à *stack* a ser utilizada;
* @param token (caractere) que corresponde a um operador matemático. 
* ##Funcionamento
* Utiliza a função operator_math() para calcular o número de argumentos que o operador utiliza.
* ### Caso seja um argumento:
* Operadores de incrementação/decrementação e de *not* (*bitwise*). 
* - retira o último valor da *stack*, usando POP(sp);
* - aplica a operação e dá *push* para o topo da *stack*. 
* ### Caso sejam dois argumentos:
* Operadores soma, subtração, multiplicação, divisão, módulo, exponenciação e algumas operações *bitwise* (*AND*, *OR* e *XOR*).
* - retira os dois últimos valores da *stack*, usando a função POP(sp) duas vezes.
* - aplica a operação entre os dois argumentos e dá *push* para o topo da *stack*.
* @note As funções auxiliares desta função (compute_math_one_operator() e compute_math_two_operators()) foram adicionas e foi alterado o modo de funcionamento, aquando do Guião 2, para efetuar cálculos com **doubles** e com **chars**.
*/
void compute_math(STACK *stk, char token) {

     if (operator_math(token) == 1) 

        compute_math_one_operator (stk, token);

    else 

        compute_math_two_operators (stk, token);  
}      
