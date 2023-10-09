#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <math.h>
#include "struct.h"



unsigned long hash(KEY k) 
{
  unsigned long hash = 5381;
  int c;
  while ((c = *(k++)))
      hash = ((hash << 5) + hash) + c;
  return hash;
}


/**
 * \brief Função initUsers
 * 
 * É a função reponsável por inicializar a tabela de users
 * @param table tabela de users
 */
void initUsers (USERS table)
{
	int i;
	for (i=0; i<MAX_USERS; table[i++].state = 0);
}


/**
 * \brief Função add_users
 * 
 * É a função reponsável por adicionar os users à tabela de hash
 * @param i id a adicionar
 * @param log login do user
 * @param t type do user
 * @param follower_list string que tem a lista dos seguidores
 * @param following_list string que tem a lista dos utilizadores que um user segue
 * @param table tabela de users
 * @param followers quantidade de seguidores
 * @param following qunatidade de pessoas que segue
 */
int add_users (KEY i,USERS table)
{
	unsigned long p = hash (i)%MAX_USERS;
	while (table[p].state && strcmp (table[p].id,i))
	    p = (p+1)%MAX_USERS;

	if (table[p].state) return 1;
	table[p].id=i;
	table[p].state = 1;
return 0; 
}


int searchID (KEY i,USERS table)
{
	unsigned long p = hash (i)%MAX_USERS;
	while (table[p].state && strcmp (table[p].id,i))
	    p = (p+1)%MAX_USERS;
	if (table[p].state) return 1;
	return 0;
}


/**
 * \brief Função printUSERS
 * 
 * É a função reponsável por imprimir os users 
 * @param t tabela de users
 */
void printUSERS(USERS t)
{
  int i;
  for (i=0; i<MAX_USERS; i++) if (t[i].state) 
  {
    printf("##### ID:%s\n\n ",t[i].id) ; 
  }
}

/**
 * \brief Função initREPOS
 * 
 * É a função reponsável por iniciar o parametro da taebla de hash "state" a 0 
 * @param repo_table (Tabela de repositórios
 * 
 */
void initREPOS (REPOS repo_table) 
{
	int i;
	for (i=0; i<MAX_REPOS; repo_table[i++].state = 0);
}


/**
 * \brief Função addREPOS
 * 
 * É a função reponsável por adicionar um repositório à tabela de hash
 * @param i (KEY que se pretende procurar)
 * @param repo_table (Tabela Repos)
 * @param owner (dono do repositório)
 * @param lingua (Linguagem do repositório
 * @param date (Updated at daquele repositório
 * @param descricao (descrição do repositório)
 * 
 */
int addREPOS (KEY i, REPOS repo_table)
{
	unsigned long p = hash (i)%MAX_REPOS;
	while (repo_table[p].state && strcmp (repo_table[p].id,i))
	    p = (p+1)%MAX_REPOS;
	if (repo_table[p].state) return 1;
	repo_table[p].id=i;
	repo_table[p].state = 1;
	return 0;
}


int searchREPOS (KEY i,REPOS repo_table)
{
	unsigned long p = hash (i)%MAX_REPOS;
	while (repo_table[p].state && strcmp (repo_table[p].id,i))
	    p = (p+1)%MAX_REPOS;
	if (repo_table[p].state) return 1;
	return 0;
}

void initCOMMITS (COMMITS com_table) 
{
	int i;
	for (i=0; i<MAX_COMMITS; com_table[i++].state = 0) com_table[i++].count = 0;
}


int addCOMMITS (KEY i, COMMITS com_table)
{
	unsigned long p = hash (i)%MAX_COMMITS;
	while (com_table[p].state && strcmp(com_table[p].id,i))
	   p = (p+1)%MAX_COMMITS;
	if (com_table[p].state) 
	{
    com_table[p].count++;
    return 1;
 	}
	com_table[p].id=i;
	com_table[p].state = 1;
	com_table[p].count = 1;
	 return 0;
}

int searchCOMMITS (KEY i , COMMITS com_table) 
{
    unsigned long p = hash (i)%MAX_REPOS;
    while (com_table[p].state && strcmp (com_table[p].id,i))
	    p = (p+1)%MAX_REPOS;
	if (com_table[p].state) return 1;
	return 0;
}