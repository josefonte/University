/**
 * @file Ficheiro que inclui as funções que realizam operações de lógica  
 */

#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include "arrays.h"
#include "stack.h"
#include "logic.h"

/**
 @file logic.c
 \brief Ficheiro que inclui as funções que realizam operações de lógica 
*/

/*!
 * Esta função testa se o caractere recebido como input é um dos operadores que a função manipulation() utiliza.
 * @param token Caractere a ser testado.
 * @returns 0 Se não for um operador usado na função logic_operations();
 * @returns 1 Se for um operador e precisar de, pelo menos, um elemento na *stack*;
 * @returns 2 Se for um operador e precisar de, pelo menos, dois elemento na *stack*;
 * @returns 3 Se for um operador e precisar de, pelo menos, trêS elemento na *stack*;
 */
int logical_operator (char token) {

    char logical_operators[6] = "!=<>e?";

    int i, r = 0;

    for (i = 0; i < 6; i++) 

        if (token == logical_operators[i]) {

            if (!i) r = 1;             // se for o operador !

            else r = (i == 5) ? 3 : 2; // se for o operador ? será 3, caso contrário será 2
        }
    
    return r;
}

/*!
 * Retorna o segundo caractere da **string** recebida como input, ou seja, no contexto onde é inserida retorna o caractere após o 'e'.
 * @param token **String** que corresponde uma parte da linha lida.
 * @returns segunda posição da **string**.
 */
char operator_e (char *token) {
    return token[1];             //variável
}

/*!
 * Função que muda o tipo de um **DATA** para **double**.
 * @param elem Endereço do elemento do tipo **DATA**.
 */
void assert_double (DATA *elem) {
    
    switch (elem->type) {

        case DOUBLE:
            break;

        case LONG:
            elem->DOUBLE = (double) elem->LONG;
            break;

        case CHAR:
            elem->DOUBLE = (double) elem->CHAR;
            break;

        case STRING:
            elem->DOUBLE = atof(elem->STRING);
            free (elem->STRING);
            break;        
    }
    
    elem->type = DOUBLE;  
}

/*!
 * Função que muda o tipo de um **DATA** para **double**.
 * @param elem Endereço do elemento do tipo **DATA**.
 */
void assert_string (DATA *elem) {

    switch (elem->type) {                            

        case STRING:
            break;

        case LONG:
            elem->STRING = (char *) malloc (20 * sizeof (char));
            sprintf(elem->STRING, "%ld", elem->LONG);
            break;

        case DOUBLE:
            elem->STRING = (char *) malloc (20 * sizeof (char));
            sprintf(elem->STRING, "%lf", elem->DOUBLE);
            break;

        case CHAR:
            elem->STRING = (char *) malloc (2 * sizeof (char));
            elem->STRING[0] = elem->CHAR;
            elem->STRING[1] = '\0';
            break;      
    }
    
    elem->type = STRING;
    elem->is_block = 0;
}

/*!
 * Esta função testa se o elemento do tipo DATA para o qual aponta o *pointer* recebido é do tipo desejado. Se não for, converte-o.
 * @param elem Endereço que aponta para um elemento do tipo **DATA**;
 * @param t **TYPE** desejado;
 * @note Caso o **TYPE** desejado seja **double**, utiliza a função assert_double(). Caso o **TYPE** desejado seja **string**, utiliza a função assert_string().
 */
void assert_type_aux (DATA *elem, TYPE t) {

    switch (t) {

        case DOUBLE:

            assert_double (elem);
            break;

        case STRING:

            assert_string (elem);
            break;
        
        default:

            break;
    }
}

/*!
 * Esta função testa se dois elementos DATA recebidos são do mesmo tipo e, se não forem, converte-os de modo a reduzir ao mínimo as perdas de informação 
 * e continuarem coerentes no contexto onde são inseridos.
 * @param elem1 Endereço do primeiro elemento a ser testado;
 * @param elem2 Endereço do segundo elemento a ser testado.
 * @note Caso são sejam do mesmo tipo, utiliza a função auxiliar assert_type_aux(), que assegura a conversão de tipos.
 */
void assert_type (DATA *elem1, DATA *elem2) {

    if (elem1->type != elem2->type) { // testa se tẽm tipos diferens

        if ( has_type (*elem1, DOUBLE) || (has_type (*elem1, LONG) && !elem1->is_array) 
          || has_type (*elem2, DOUBLE) || (has_type (*elem2, LONG) && !elem2->is_array)) { // testa se pelo menos um é um valor numérico

            assert_type_aux (elem1, DOUBLE);
            assert_type_aux (elem2, DOUBLE);
        }

        else { // se não for, ou seja, restam os tipos CHAR e STRING, coloca os dois como STRING

            assert_type_aux (elem1, STRING);
            assert_type_aux (elem2, STRING);
        }
    }
}

/*!
 * Função que testa se um dado **DATA** tem o valor lógico verdadeiro.
 * @param elem **DATA** a ser testado.
 * @returns valor lógico do **DATA**.
 */
int is_true (DATA elem) {

    switch (elem.type) {

        case LONG:

            return elem.LONG;
        
        case DOUBLE:

            return elem.DOUBLE;
        
        case CHAR:

            return elem.CHAR != '\0';
        
        case STRING:

            return elem.STRING != NULL;
    }
        
    return 0; // nunca chega aqui;
}

/*!
 * Função responsável por colocar o menor entre dois elementos do tipo **DATA** na **stack**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param elem1 Primeiro elemento.
 * @param elem2 Segundo elemento;
 */
void lower (STACK *stk, DATA elem1, DATA elem2) {

    assert_type (&elem1, &elem2);

    switch (elem1.type) {

    case LONG:

        PUSH (stk, (elem1.LONG < elem2.LONG) ? elem1 : elem2);
        
        break;
            
    case DOUBLE:

        PUSH (stk, (elem1.DOUBLE < elem2.DOUBLE) ? elem1 : elem2);
        
        break;

    case CHAR:

        PUSH (stk, (elem1.CHAR < elem2.CHAR) ? elem1 : elem2);
        
        break;

    case STRING:

        PUSH (stk, ( strcmp (elem1.STRING, elem2.STRING) < 0) ? elem1 : elem2);             // coloca a menor string na stack
        
        free ( ( strcmp (elem1.STRING, elem2.STRING) < 0) ? elem2.STRING : elem1.STRING );  // liberta o espaço alocado na heap da maior string
        
        break;
    }
}

/*!
 * Função responsável por colocar o maior entre dois elementos do tipo **DATA** na **stack**.
 * @param stk Endereço da **stack** a ser utilizada;
 * @param elem1 Primeiro elemento.
 * @param elem2 Segundo elemento;
 */
void higher (STACK *stk, DATA elem1, DATA elem2) {

    assert_type (&elem1, &elem2);

    switch (elem1.type) {

         case LONG:

            PUSH (stk, (elem1.LONG > elem2.LONG) ? elem1 : elem2);
            
            break;
            
        case DOUBLE:

            PUSH (stk, (elem1.DOUBLE > elem2.DOUBLE) ? elem1 : elem2);
            
            break;

         case CHAR:

            PUSH (stk, (elem1.CHAR > elem2.CHAR) ? elem1 : elem2);
            
            break;

         case STRING:

            PUSH (stk, ( strcmp (elem1.STRING, elem2.STRING) > 0) ? elem1 : elem2);              // coloca a menor string na stack
            
            free ( ( strcmp (elem1.STRING, elem2.STRING) > 0) ? elem2.STRING : elem1.STRING );   // liberta o espaço alocado na heap da maior string
            
            break;
    }
}

/*!
 * Função responsável por executar os comandos lógicos: e&, e|, e< e e>. 
 * @param stk Corresponde ao endereço da *stack* que a programa está a utilizar;
 * @param token **STRIN** que corresponde a um operador.
 * ### Descrição
 * Retira dois elementos do topo da *stack* para a operação lógica. Antes de cada operação assegura que ambos os **DATA** são do mesmo tipo.   
 * Se o operador em questão for:
 * - e& : Se o primeiro elemento for logicamente verdadeiro, coloca o segundo elemento na *stack*, caso seja logicamente falso, coloca o primeiro elemento na *stack*; 
 * - e| : Se o primeiro elemento for logicamente verdadeiro, coloca o primeiro elemento na *stack*, caso seja logicamente falso, coloca o segundo elemento na *stack*;
 * - e< : Coloca o menor dos dois valores na stack;
 * - e> : Coloca o maior dos dois valores na stack;
 */
void operate_e (STACK *stk, char *token) {

    DATA elem2 = POP(stk);
    DATA elem1 = POP(stk);

    switch (operator_e(token)) {

        case '&':        

            if ( is_true (elem1) ) PUSH(stk, elem2);

            else PUSH(stk, elem1);

            break;
        
        case '|':

            if ( is_true (elem1) ) PUSH(stk, elem1);

            else PUSH(stk, elem2);

            break;
        
        case '<':

            if (!has_type (elem2, LONG) || !elem2.is_array)
            
                lower (stk, elem1, elem2);

            break;            
        
        case '>':
        
            if (!has_type (elem2, LONG) || !elem2.is_array)

                higher (stk, elem1, elem2);

            break;
    }
}

/*!
 * Função responsável por testar a equivalência dos elementos no topo da **stack**.
 * @param stk Corresponde ao endereço da *stack* que a programa está a utilizar.
 */
void operate_logic_two_operator_equal (STACK *stk) {

    DATA elem2 = POP(stk);
    DATA elem1 = POP(stk);

    if (!has_type (elem2, LONG) || !elem2.is_array) {
        
        assert_type (&elem1, &elem2);

        switch (elem1.type) {

            case LONG:

                elem1.LONG = (elem1.LONG == elem2.LONG) ? 1 : 0;

                break;

            case DOUBLE:

                elem1.LONG = (elem1.DOUBLE == elem2.DOUBLE) ? 1 : 0;

                break;

            case CHAR:

                elem1.LONG = ((int)elem1.CHAR == (int)elem2.CHAR) ? 1 : 0;

                break;

            case STRING:

                elem1.LONG = (strcmp (elem1.STRING, elem2.STRING) ) ? 0 : 1;

                free (elem1.STRING);
                free (elem2.STRING);

                break;
            }

        elem1.type = LONG;
        elem1.is_array = 0;

        PUSH(stk, elem1);
    }
}

/*!
 * Função responsável por testar se o primeiro elemento (penúltimo a entrar) é menor do que o segundo (último a entrar).
 * @param stk Corresponde ao endereço da *stack* que a programa está a utilizar.
 */
void operate_logic_two_operator_less (STACK *stk) {

    DATA elem2 = POP(stk);
    DATA elem1 = POP(stk);
    
    if (!has_type (elem2, LONG) || !elem2.is_array) {
        
        assert_type (&elem1, &elem2);

        switch (elem1.type) {

            case LONG:

                elem1.LONG = (elem1.LONG < elem2.LONG) ? 1 : 0;

                break;

            case DOUBLE:

                elem1.LONG = (elem1.DOUBLE < elem2.DOUBLE) ? 1 : 0;

                break;

            case CHAR:

                elem1.LONG = ((int)elem1.CHAR < (int)elem2.CHAR) ? 1 : 0;

                break;

            case STRING:

                elem1.LONG = (strcmp (elem1.STRING, elem2.STRING) < 0 ) ? 1 : 0;

                free (elem1.STRING);
                free (elem2.STRING);

                break;
            }

        elem1.type = LONG;
        elem1.is_array = 0;

        PUSH(stk, elem1);
    }
}

/*!
 * Função responsável por testar se o primeiro elemento (penúltimo a entrar) é maior do que o segundo (último a entrar).
 * @param stk Corresponde ao endereço da *stack* que a programa está a utilizar.
 */
void operate_logic_two_operator_more (STACK *stk) {

    DATA elem2 = POP(stk);
    DATA elem1 = POP(stk);
    
    if (!has_type (elem2, LONG) || !elem2.is_array) {
        
        assert_type (&elem1, &elem2);

        switch (elem1.type) {

            case LONG:

                elem1.LONG = (elem1.LONG > elem2.LONG) ? 1 : 0;

                break;

            case DOUBLE:

                elem1.LONG = (elem1.DOUBLE > elem2.DOUBLE) ? 1 : 0;

                break;

            case CHAR:

                elem1.LONG = ((int)elem1.CHAR > (int)elem2.CHAR) ? 1 : 0;

                break;

            case STRING:

                elem1.LONG = (strcmp (elem1.STRING, elem2.STRING) > 0 ) ? 1 : 0;

                free (elem1.STRING);
                free (elem2.STRING);

                break;
            }

        elem1.type = LONG;
        elem1.is_array = 0;

        PUSH(stk, elem1);
    }
}

/*!
 * Função responsável por executar os comandos lógicos: e(...), =, < e > (comandos que necessitam de dois operandos).
 * @param stk Corresponde ao endereço da *stack* que a programa está a utilizar;
 * @param token **String** que corresponde a um operador.
 * ### Descrição
 * Retira dois elementos do topo da stack para a operação lógica. Caso seja uma operação do tipo "e_" chama a função auxiliar operator_e(). 
 * Antes de cada operação assegura que ambos os elementos do tipo **DATA** são do mesmo tipo. 
 * Testa qual é o operador em questão e caso seja:
 * - = : Testa se os dois elementos são iguais, definindo o valor do elem1 com o resultado (1-True, 0-False);
 * - < : Testa se elem1 é menor do que elem2, definindo o valor do elem1 com o resultado (1-True, 0-False); 
 * - > : Testa se elem1 é maior do que elem2, definindo o valor do elem1 com o resultado (1-True, 0-False); 
 */
void operate_logic_two_operator (STACK *stk, char *token) {

    if (*token == 'e') operate_e (stk, token);  // operações do tipo e_

    else {

        switch (*token) {

            case '=': // elem1 == elem2

                operate_logic_two_operator_equal (stk);

                break;
            
            case '<':  // elem1 < elem2

                operate_logic_two_operator_less (stk);

                break;
            
            case '>':  // elem1 > elem2

                operate_logic_two_operator_more (stk);

                break;
        } 
    }
}

/*!
 * Função principal responsável por executar os comandos lógicos.
 * @param stk Corresponde ao endereço da *stack* que a programa está a utilizar;
 * @param token *String* que corresponde a um operador;
 * @param arys Endereço da lista ligada de *arrays*.
 * ### Descrição
 * Testa qual é o operador em questão e caso seja:
 * - ! : Troca o valor lógico do topo da *stack*;
 * - ? : Impõe uma expressão do tipo If-Then-Else nos 3 elementos no topo da *stack*;
 * - outros operadores (e&, e|, e<, e>, =, <, >): chama a função operate_logic_two_operator() que realiza todas as outras operações.
 */
void logical_operations (STACK *stk, char *token, ARRAYSLIST arys) {

    switch (logical_operator (*token)) {

        case 1: {  // operador !

            DATA elem = POP(stk);

            switch (elem.type) {

                case LONG:
                    
                    elem.LONG = (elem.LONG) ? 0 : 1;

                    break;
                
                case DOUBLE:

                    elem.LONG = (elem.DOUBLE) ? 0 : 1;

                    break;
                
                case CHAR:

                    elem.LONG = (elem.CHAR) ? 0 : 1;

                    break;
                
                case STRING:

                    elem.LONG = (elem.STRING != NULL) ? 0 : 1;

                    free (elem.STRING);

                    break;
            }

            elem.type = LONG;
            elem.is_array = 0;

            PUSH (stk, elem);

            break;
        }
        
        case 2:

            operate_logic_two_operator(stk, token);
            
            break;
        
        case 3: {   //operador ?

            DATA elem3 = POP(stk);
            DATA elem2 = POP(stk);
            DATA elem1 = POP(stk);

            if (has_type (elem1, LONG) && elem1.is_array) {                   // se for um array testa se tem elementos

                STACK *stk_array = find_addr_array (arys, elem1.LONG);
                
                elem1.LONG =stk_array->n_elems;

                free (stk_array->stack);
                free (stk_array);
            }

            if ( is_true (elem1) ) // se for um valor numérico testa se é diferente de 0

                PUSH (stk, elem2);
            
            else 

                PUSH(stk, elem3);
            
            break;
        }
    }
}
