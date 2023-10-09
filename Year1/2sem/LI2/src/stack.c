/**
 * @file Ficheiro que inclui as funções mais elementares ao nível da *stack*
 */

#include <stdio.h>
#include <stdlib.h> 
#include <string.h>
#include "stack.h"

/**
 * @file stack.c
 * \brief Ficheiro que inclui as funções mais elementares ao nível da *stack*
 */

/*!
 * Função que cria a *stack* a ser usada aquando da realização da função **compute** definida no ficheiro **main.c**.
 * @returns *Stack* com 0 elementos e uma capacidade para 100 elementos.
 * @note O tamanho da *stack* pode variar no decorrer da execução do programa, caso seja necessário alocar mais espaço.
 */
STACK *create_stack () {
    STACK *stk = (STACK *) malloc(sizeof(STACK));
    stk->n_elems = 0;
    stk->size = 100;
    stk->stack = (DATA *) malloc (stk->size * sizeof(DATA));
    return stk; 
}

/*!
 * Testa se o tipo de um determinado elemento da *stack* é o mesmo do tipo fornecido no segundo argumento.
 * @param elem do tipo DATA cujo **elem.type** será testado;
 * @param type que queremos comparar ao anterior.
 * @returns 0, se forem diferentes;
 * @returns !0, caso contrário.
 */
int has_type (DATA elem, int type) {
    return (elem.type & type) != 0;
}

/*!
 * Função que coloca o elemento que conta segundo argumento na *stack* (primeiro argumento) e incrementa o número de elementos.
 * @param stk que corresponde ao endereço da *stack* a ser utilizada;
 * @param elem do tipo **DATA** que será adicionado ao topo da *stack*.
 * @note Caso não haja espaço suficiente na *stack*, a função é também responsável por realocar mais espaço na memória.
 */
void PUSH (STACK *stk, DATA elem) {
    if (stk->size == stk->n_elems) {
        stk->size += 100;
        stk->stack = (DATA *) realloc (stk->stack, stk->size * sizeof(DATA));
    }
    stk->stack[stk->n_elems] = elem;
    stk->n_elems++;
}

/*!
 * Função que remove o elemento no topo da *stack* e o devolve como argumento.
 * @param stk que correponde ao endereço da *stack* em questão.
 * @returns elemento do tipo **DATA** que foi removido.
 */
DATA POP (STACK *stk) {
    stk->n_elems--;
    return stk->stack[stk->n_elems];
}

/*!
 * Revela o topo da *stack*.
 * @param stk que corresponde ao endereço de memória da *stack* a ser utilizada.
 * @return elemento que está no topo da *stack*. 
 */
DATA top (STACK *stk) {
    return stk->stack[stk->n_elems-1];
}

/*!
 * Testa se a *stack* está vazia.
 * @param stk que correponde ao endereço de memória da *stack* a ser utilizada.
 * @return 0 se a *stack* não estiver vazia;
 * @return !0, caso contrário.
 */
int is_empty (STACK *stk) {
    return stk->n_elems == 0;
}

/*!
 * Função que retorna o endreço do conteúdo de um *array** (uma **stack**).
 * @param arys Endereço da lista de **arrays**;
 * @param nm Nome/ID de um **array**.
 * @returns Endereço da **stack** correspondente.
 */
STACK *find_addr_array (ARRAYSLIST arys, long nm) {

    while ( arys->ary1->NAME != nm ) arys = arys->rest;

    return arys->ary1->array;
}

/*!
 * Função utilizada no final da função **compute** do ficheiro **main.c** para imprimir a *stack* final (sem espaços e com os dígitos não significativos dos elementos do tipo **double** ocultos).
 * @param stk O endereço de memória da **stack** a ser utilizada;
 * @param arys Endereço da lista de **arrays**.
 */
void print_stack (STACK *stk, ARRAYSLIST arys) {

    int i;
    
    for (i = 0; i < stk->n_elems; i++) {
        
        DATA elem = stk->stack[i];
        
        TYPE type = elem.type;
        
        switch (type) {
            
            case LONG:
                if (elem.is_array)

                    print_stack (find_addr_array (arys, elem.LONG), arys);

                else 
                    
                    printf("%ld", elem.LONG);
                
                break;

            case DOUBLE: 
                printf("%g", elem.DOUBLE);
                break;
            
            case CHAR:
                
                printf("%c", elem.CHAR);
                
                break;
            
            case STRING:
                if (elem.is_block) putchar('{');

                printf("%s", elem.STRING);

                if (elem.is_block) putchar('}');
        }
    }
}
