/**
 * @file Ficheiro principal do programa que engloba todos os restantes para interpretar a linha a ser lida
 */

#ifndef STACK_H
#define STACK_H

/**
 * \file stack.h
 * \brief Ficheiro principal do programa que engloba todos os restantes para interpretar a linha a ser lida
 */


/**
 * Definição dos vários tipos de elementos que podem ser incluídos à *stack* (**LONG**, **DOUBLE**, **CHAR** e **STRING**).
 */
typedef enum {
    /** elementos do tipo **long** */
    LONG = 1,
    /** elementos do tipo **double** */
    DOUBLE = 2,
    /** elementos do tipo **char** */
    CHAR = 4,
    /** elementos do tipo **string** */
    STRING = 8
} TYPE;

/**
 * *Struct* que corresponde ao tipo de dados com que estamos a trabalhar (cujo *type* é um dos quatro possíveis e pode variar).
 * @note a variável **is_array** determina se um elemento do tipo **LONG** é um ID de um **array**.
 * @note a variável **is_block** determina se uma **string** representa um bloco. 
 */
typedef struct data {
    /** tipo de dados */
    TYPE type;
    /** conteúdo do tipo **long** */
    long LONG;
    /** conteúdo do tipo **double** */       
    double DOUBLE;
    /** conteúdo do tipo **char** */
    char CHAR;
    /** conteúdo do tipo **string** */
    char* STRING;

    /** determina se se trata de um **array** */
    int is_array;
    /** determina se se trata de um bloco */
    int is_block;

} DATA;

/**
 * *Struct* que contém um *array* de **DATA**'s (*stack*) e variáveis do tipo **int** para o tamanho da *stack* (**size**) e para o número de elementos (**n_elems**).  
 */ 
typedef struct stack {
    /** **Array** de **DATAs** */
    DATA *stack;
    /** Tamanho disponível na **stack** */
    int size;          //tamanho disponível
    /** Número de elementos na **stack** */
    int n_elems;

} STACK;

/**
 * *Struct* que contém um *array* de **DATA**'s (*stack*) e variáveis do tipo **int** para o tamanho da *stack* (**size**) e para o número de elementos (**n_elems**).  
 */ 
typedef struct array {
    /** Nome/ID de um **array** */
    long NAME;
    /** Endereço de uma **satck** com a informação de um **array** */
    STACK *array;

} * ARRAY;

/**
 * Lista ligada de elementos de *structs* **array**.
 */
typedef struct arrayslist {
    /** Primeiro **array** da lista (último a entrar) */
    ARRAY ary1;
    /** *Pointer* para a próxima célula da lista */
    struct arrayslist * rest;

} * ARRAYSLIST;

STACK *create_stack ();

int has_type (DATA elem, int type);

void PUSH (STACK *stk, DATA elem);

DATA POP (STACK *stk);

DATA top (STACK *stk);

int is_empty (STACK *stk);

STACK *find_addr_array (ARRAYSLIST arys, long nm);

void print_stack (STACK *stk, ARRAYSLIST arys);

#endif