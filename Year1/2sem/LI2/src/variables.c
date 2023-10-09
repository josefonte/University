/**
 * @file Ficheiro responsável pelas variáveis do programa.
 */

#include <stdlib.h>
#include <string.h>
#include "stack.h"
#include "variables.h"
#include "arrays.h"

/**
 @file variables.c
 \brief Ficheiro responsável pelas variáveis do programa.
*/

/*!
 * Esta função verifica se o caractere recebido como input é o nome de uma variável.
 * @param token Caractere a ser testado.
 * @returns resultado do teste (se o caractere em ascii pertence a [65..90]).
 */
int is_var (char token) {

    int ascii_token = (int)token; // valor em ascii

    return ascii_token >= 65 && ascii_token <=90;
}

/*!
 * Função que cria a lista das variáveis (algumas com valores já definidos).
 * @return Lista com as 26 variáveis.
 */
DATA *create_vars () {

    DATA *vars = (DATA *) malloc (26 * sizeof (DATA));

    int i;

    for (i = 0; i <= 5; i++) { // A B C D E F

        vars[i].type = LONG;
        vars[i].LONG = 10 + i;  // valores em hexadecimal
    }

    for (i = 23; i < 26; i++) { // X Y Z

        vars[i].type = LONG;
        vars[i].LONG = i - 23;
    }

    vars[13].type = CHAR;   // N
    vars[13].CHAR = '\n';

    vars[18].type = CHAR;   // S
    vars[18].CHAR = ' ';

    return vars;
}

/*!
 * Troca o valor de uma determinada variável.
 * @param stk Endereço da **stack** que o porgrama está a utilizarM
 * @param vars Array de **DATAs** que representam as variáveis;
 * @param name Nome (caractere) da variável a ser trocada;
 * @param arys Endereço do endereço da lista de **arrays**;
 * @param current_array Endereço do valor do número atual de **arrays**.
 */
void change_value_var (STACK *stk, DATA *vars, char name, ARRAYSLIST *arys, long *current_array) {

    DATA new_value = top(stk);
    
    if (has_type (new_value, LONG) && new_value.is_array) {

        STACK *stk_array = create_stack (); // novo array

        STACK *stk_lst_array = find_addr_array (*arys, new_value.LONG); // array a copiar

        int i;

        for (i = 0; i < stk_lst_array->n_elems; i++) // adicionar tds os elementos do antigo array

            PUSH (stk_array, stk_lst_array->stack[i]); 

        add_array (stk, arys, stk_array, current_array); // adicionar o novo array à lista de arrays

        stk->n_elems--; // remover a array flag do novo array do topo da stack

        new_value.LONG = (*current_array) - 1;
        new_value.is_array = 1;
    }

    else {

        if (has_type (new_value, STRING)) {
            
            int i, len = strlen (new_value.STRING) + 1;

            new_value.STRING = (char *) malloc (len * sizeof (char));

            for (i = 0; i < len - 1; i++)

                new_value.STRING[i] = top(stk).STRING[i];

            new_value.STRING[i] = '\0';         
        }
    }

    vars[(int) name - 65] = new_value;
}

/*!
 * Encontra o valor de uma determinada variável.
 * @param stk Endereço da **stack** que o programa está a utilizar;
 * @param arys Endereço do endereço da lista de **arrays**;
 * @param vars **Array** com as variáveis;
 * @param name Nome da variável;
 * @param current_array Endereço do valor do número atual de **arrays**.
 * @returns valor (**DATA**) da variável em questão.
 */
DATA find_value_var (STACK *stk, ARRAYSLIST *arys, DATA *vars, char name, long *current_array) {

    DATA value = vars[(int) name - 65];
    DATA value_aux = vars[(int) name - 65];

    if (has_type (value, STRING)) {

        int i, len = strlen(value.STRING) + 1;

        value.STRING = (char *) malloc (len * sizeof (char));

        for (i = 0; i < len - 1; i++)

            value.STRING[i] = value_aux.STRING[i];
        
        value.STRING[i] = '\0';
    }

    else 

        if (has_type (value, LONG) && value.is_array) {
            
            STACK *stk_array = create_stack(); // novo array

            STACK *stk_lst_array = find_addr_array (*arys, value.LONG); // array a copiar

            int i;

            for (i = 0; i < stk_lst_array->n_elems; i++) // adicionar tds os elementos do antigo array

                PUSH (stk_array, stk_lst_array->stack[i]); 

            add_array (stk, arys, stk_array, current_array); // adicionar o novo array à lista de arrays

            POP (stk); // remover a array flag do novo array do topo da stack

            value.LONG = (*current_array) - 1;
        }

    return value;
}

/*!
 * Esta função é responsável por executar os comandos relativos às variáveis (atribuir valores e adicionar valores de variáveis à *stack*).
 * @param stk Corresponde ao endereço da **stack** que a programa está a utilizar;
 * @param token Corresponde operador (**string**) a ser utilizado;
 * @param arys Endereço do endereço da lista de **arrays**;
 * @param current_array Endereço do valor do número atual de **arrays**.
 */
void operate_var (STACK *stk, char *token, DATA *vars, ARRAYSLIST *arys, long *current_array) {

    if (token[0] == ':') 

        change_value_var (stk, vars, token[1], arys, current_array);

    else 

        PUSH (stk, find_value_var (stk, arys, vars, *token, current_array));
}