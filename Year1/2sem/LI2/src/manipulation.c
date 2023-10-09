/**
 * @file Ficheiro que inclui as funções que realizam manipulações na ordem dos elementos da *stack*, retiram elemetos e incluem no topo alguns que já existem 
 */

#include <stdlib.h>
#include <assert.h>
#include <string.h>
#include "stack.h"
#include "manipulation.h"
#include "arrays.h"

/**
 @file manipulation.c
 \brief Ficheiro que inclui as funções que realizam manipulações na ordem dos elementos da *stack*, retiram elemetos e incluem no topo alguns que já existem 
*/

/*!
 * Esta função testa se o caractere recebido com input é um dos operadores que a função manipulation() utiliza.
 * @param token Caractere que será testado
 * @returns 0 Se não for um operador usado na função manipulation();
 * @returns 1 Se for um operador e precisar de, pelo menos um elemento na *stack*;
 * @returns 2 Se for um operador e precisar de, pelo menos dois elemento na *stack*;
 * @returns 3 Se for um operador e precisar de, pelo menos trêS elemento na *stack*;
 * @note Caso seja o operador *$* o número de elementos na *stack* varia consoante o valor do último elemento pelo que, foi considerado que devia returnar 1 caso
 * esse caractere aparecesse;
 */
int operator_manipulation (char token) {
    int i, r = 0;

    char operators[5] = ";$_\\@";

    for (i = 0; i<5; i++)
        if (token == operators[i]) {
            if (i<3) r = 1;
            else r = (i == 3) ? 2 : 3;
            i = 4;
        }
    
    return r;
}

/*!
 * Esta é uma função auxiliar da manipulation responsável pelo operador '$', que retira o topo da *stack* e usa o valor numérico nesse elemento como índice para elemento da *stack* que será copiado para o topo.
 * @param stk Corresponde à *stack* que a programa está a utilizar;
 * @param arys Endereço do endereço da lista de **arrays**;
 * @param current_array Endereço do valor numérico currespondente ao número de **arrays**.
 */

void operator_index (STACK* stk, ARRAYSLIST *arys, long *current_array){

    DATA aux = POP(stk);
                    
    assert(has_type(aux, LONG)); 
            
    int indice = stk->n_elems - aux.LONG - 1;
    
    aux = stk->stack[indice];
    
    if (has_type (aux, STRING)) {
       
        DATA aux2 = aux;
        
        int i, len = (int) strlen(aux2.STRING);
        
        aux.STRING = (char *) malloc ( (len + 1) * sizeof (char));
        
        for (i = 0; i < len; i++)
            
            aux.STRING[i] = aux2.STRING[i];
        
        aux.STRING[i] = '\0';
        
        PUSH (stk, aux);
    }
    else {
        
        if (has_type (aux, LONG) && aux.is_array) {
            
            STACK *new_arr = create_stack();
            
            STACK *old_arr = find_addr_array (*arys, aux.LONG);
            
            int i, len = old_arr->n_elems;
            
            for (i = 0; i < len; i++)
               
                PUSH (new_arr, old_arr->stack[i]);
            
            add_array (stk, arys, new_arr, current_array);
        }
        else 
            PUSH (stk, aux);
    }
}

/*!
 * Esta é uma função auxiliar da manipulation responsável pelo operador '_', que duplica o topo da *stack* (acrescenta o elemento que estava o topo, novamente).
 * @param stk Corresponde à *stack* que a programa está a utilizar;
 * @param arys Endereço do endereço da lista de **arrays**;
 * @param current_array Endereço do valor numérico currespondente ao número de **arrays**.
 */

void operator_top (STACK* stk, ARRAYSLIST *arys, long *current_array){

    DATA aux = top (stk);
        
    if (has_type (aux, STRING)) {
    
        int i, len = strlen(aux.STRING);
    
        aux.STRING = (char *) malloc (len * sizeof(char));
    
        for (i = 0; i < len; i++)
    
            aux.STRING[i] = top(stk).STRING[i];
        
        aux.STRING[i] = '\0';
        
        PUSH(stk, aux);
    }
    else {
    
        if (has_type (aux, LONG) && aux.is_array) {
    
            STACK *array_to_copy = find_addr_array (*arys, aux.LONG);
    
            STACK *new_array = create_stack();
    
            int i, len = array_to_copy->n_elems;
    
            for (i = 0; i < len; i++)
    
                PUSH (new_array, array_to_copy->stack[i]);
            
            add_array (stk, arys, new_array, current_array);
        }
        
        else 
    
            PUSH (stk, aux);
    }
}        

/*!
 * Função responsável por executar os comandos: ; $ \\ @;
 * @param stk Corresponde à *stack* que a programa está a utilizar;
 * @param token Caracter que corresponde a um operador;
 * @param arys Endereço do endereço da lista de **arrays**;
 * @param current_array Endereço do valor numérico currespondente ao número de **arrays**.
 * ### Descrição
 * Testa qual é o operador em questão e caso seja:
 * - ; : Retira o topo da *stack*;
 * - $ : Retira o topo da *stack* e usa o valor numérico nesse elemento como índice para elemento da *stack* que será copiado para o topo;
 * - _ : Duplica o topo da *stack* (acrescenta o elemento que estava o topo, novamente);
 * - \ : Troca as posições do elemento no topo da *stack* e no elemento imediatamente abaixo desse;
 * - @ : "Roda" os três primeiros elementos da *stack* (a contar do topo).
 */

void manipulation (STACK* stk, char token, ARRAYSLIST *arys, long *current_array) {

    DATA aux;

    switch (token) {

        case ';':
            aux = POP(stk);

            if (has_type (aux, STRING))

                free (aux.STRING);
            
            break;

        case '$': 
            
            operator_index(stk, arys, current_array);      
            
            break;          
       
        case '_':

            operator_top(stk, arys, current_array);

            break;

        case '\\':
            aux = top (stk);
            stk->stack[stk->n_elems-1] = stk->stack[stk->n_elems-2];
            stk->stack[stk->n_elems-2] = aux;

            break;
        
        case '@':
            aux = stk->stack[stk->n_elems-3];
            stk->stack[stk->n_elems-3] = stk->stack[stk->n_elems-2];
            stk->stack[stk-> n_elems-2] = stk->stack[stk->n_elems-1];
            stk->stack[stk->n_elems-1] = aux;
    }
}
