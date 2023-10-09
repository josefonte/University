/**
 * @file Ficheiro principal do programa que engloba todos os restantes para interpretar a linha a ser lida
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "compute.h"

/*!
 * Constante **SIZE** que corresponde ao valor numérico 10240
 */
#define SIZE   10240

/*!
 * Função responsável por ler a linha que será interpretada pela função compute().
 * @param line **String** que receberá o input.
 */
void make_line (char *line) {

    char *r = (char *) malloc ( SIZE * sizeof (char) );

    while (line[strlen(line) - 1] != EOF && r != NULL) 
        
        r = fgets (line + strlen(line), SIZE, stdin);
    
    free (r);
}

/*!
 * Função principal do programa que:
 * + Declara uma **string** com uma capacidade de 1024 (*line*);
 * + Usa a função make_line() para ler o comando do utilizador e armazenar na **string**;
 * + Usa a função compute() para realizar todas as operações sobre a **string** lida e imprimir o resultado da **stack** no final da execução.
 */
int main() {

    char line[SIZE];     // linha usada para escrever o comando

    make_line(line);     // função para permitir escrever o comando na linha declarada acima

    compute(line);       // interpreta a linha e imprime o resultado

    return 0;
}