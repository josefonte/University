#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <ctype.h>
#include "struct.h"



void verifica1(USERS tabelauser) {
    int collumn =0,row=0;
    char *linecopy,*line,*token,*author_id,*commiter_id;
    line=malloc(MAX_SIZE);

    FILE *commits = fopen("./entradapt2/commits-g2.csv","r") ;
    FILE *commits_ok = fopen ("./saidapt2/commits-mid.csv","w") ; 

while (fgets(line,MAX_SIZE, commits))  {
        collumn =0;
    
        linecopy = strdup(line);
        if (row ==0) {
            fputs(line,commits_ok);
        }
        if (row != 0) 
        {
            while (token = strsep (&linecopy,";"))
            {
                if (collumn ==1) 
                {
                author_id = strdup(token);
                }
                if (collumn ==2) 
                {
                commiter_id = strdup(token);
                }
                collumn++;

            }
            if ((searchID (author_id ,tabelauser)==1)&&(searchID (commiter_id ,tabelauser)==1) )
            {
                fputs (line , commits_ok);
            }         
        }
        row++;
    }
    fclose (commits);
    fclose(commits_ok);
}



void verifica2(USERS tabelauser) {
    int collumn =0,row=0;
    char *linecopy,*line,*token,*owner_id;
    line=malloc(MAX_SIZE);

    FILE *repos = fopen("./entradapt2/repos-g2.csv","r") ;
    FILE *repos_ok = fopen ("./saidapt2/repos-mid.csv","w") ; 

while (fgets(line,MAX_SIZE, repos))  {
        collumn =0;
    
        linecopy = strdup(line);
        if (row ==0) {
            fputs(line,repos_ok);
        }
        if (row != 0) 
        {
            while (token = strsep (&linecopy,";"))
            {
                if (collumn ==1) 
                {
                owner_id = strdup(token);
                }
                collumn++;

            }
            if ((searchID (owner_id ,tabelauser)==1) )
            {
                fputs (line , repos_ok);
            }         
        }
        row++;
    }
    fclose (repos);
    fclose(repos_ok);
}

void verifica3(REPOS tabelarepos) {
    int collumn =0,row=0;
    char *linecopy,*line,*token,*repo_id;
    line=malloc(MAX_SIZE);

    FILE *commits = fopen("./saidapt2/commits-mid.csv","r") ;
    FILE *commits_final = fopen ("./saidapt2/commits-final.csv","w"); 

    while (fgets(line,MAX_SIZE, commits))  
    {
        collumn =0;
    
        linecopy = strdup(line);
        if (row ==0) 
        {
            fputs(line,commits_final);
        }
        if (row != 0) 
        {
            while (token = strsep (&linecopy,";"))
            {
                if (collumn ==0) 
                {
                repo_id = strdup(token);
                }
                collumn++;
            }
            if ((searchREPOS (repo_id ,tabelarepos)==1) )
            {
                fputs (line , commits_final);
            }   
        }
        row++;
    }
    fclose (commits);
    fclose(commits_final);
}


void verifica4 (COMMITS tabelacommit) 
{
    int collumn =0,row=0;
    char *linecopy,*line,*token,*repo_id;
    line=malloc(MAX_SIZE);

    FILE *repos = fopen("./saidapt2/repos-mid.csv","r") ;
    FILE *repos_final = fopen ("./saidapt2/repos-final.csv","w"); 


    while (fgets(line,MAX_SIZE, repos))  
    {
        collumn =0;
    
        linecopy = strdup(line);
        if (row ==0) 
        {
            fputs(line,repos_final);
        }
        if (row != 0) 
        {
            while (token = strsep (&linecopy,";"))
            {
                if (collumn ==0) 
                {
                repo_id = strdup(token);
                }
                collumn++;
            }
            if ((searchCOMMITS (repo_id ,tabelacommit)==1) )
            {
                fputs (line , repos_final);
            }   
        }
        row++;
    }
    fclose (repos);
    fclose (repos_final);

}


int main () 
{
    char *linecopy,*line,*token,*user_id,*filepath,*repo_id;
    int collumn ,owner_id,followers,following,row=0,conta=0,contarepos=0,contacommit=0;

    line=malloc(MAX_SIZE);

    USERS tabelauser=malloc(MAX_USERS*sizeof(struct users));
   
    initUsers(tabelauser); 

    
    FILE *users = fopen("./entradapt2/users-g2.csv", "r");  

    while (fgets(line,MAX_SIZE, users)) 
    {
        collumn =0;
        linecopy = strdup(line);
        if (row != 0) 
        {
            while (token = strsep (&linecopy,";"))
            {
                if (collumn ==0) user_id = strdup(token);
                collumn++;

            }
        add_users(user_id,tabelauser);
        conta++;
        }
        row++;
    }
   // printf("conta %d\n",conta); 
    fclose(users);
    
    verifica1 (tabelauser);
    verifica2 (tabelauser);




REPOS tabelarepos=malloc(MAX_REPOS*sizeof(struct repos));
   
    initREPOS(tabelarepos); 

    
    FILE *reposmid = fopen("./saidapt2/repos-mid.csv", "r");  
    row=0;
    while (fgets(line,MAX_SIZE, reposmid)) 
    {
        collumn =0;
        linecopy = strdup(line);
        if (row != 0) 
        {
              while (token = strsep (&linecopy,";"))
            {
                if (collumn ==0) repo_id = strdup(token);
                collumn++;
            }
        addREPOS(repo_id,tabelarepos);
        contarepos++;
        }
        row++;
    }
    fclose(reposmid);
//printf (" conta repos %d\n ",contarepos);


    verifica3 (tabelarepos);


    COMMITS tabelacommit=malloc(MAX_COMMITS*sizeof(struct commits));
    FILE *commits_final = fopen("./saidapt2/commits-final.csv", "r");

    row=0;
    while (fgets(line,MAX_SIZE, commits_final)) 
    {
        collumn =0;
        linecopy = strdup(line);
        if (row!= 0) 
        {
            while (token = strsep (&linecopy,";"))
            {
                if (collumn ==0) repo_id = strdup(token);
                collumn++;
            }
        addCOMMITS(repo_id,tabelacommit);
        contacommit++;
        }
        row++;
    }
    fclose(commits_final);
    
    verifica4(tabelacommit);

free(tabelauser);
free(tabelacommit);
free(tabelarepos);
}
