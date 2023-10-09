#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <math.h>

#define LINE_SIZE_MAX 5000000


int verifyInt (char *token)
{
  int j, result=-1,a,c;

  for (a = 0; token[a] == ' '; a++);

  for (j = a;(isdigit(token[j]) && token[j]);j++);

  for(c=j;token[c] == ' '; c++);

  if ((token[c+1]!='\n'&& token[c] != '\0' && token[c]!='\n') || j == a) result = -1;
  else result = atoi(token);

  return result;
}

int verify_type (char*token)
{
  if( strcmp(token,"User")==0 || strcmp(token,"Organization")==0 || strcmp(token,"Bot")==0) return 1;
  else return 0;
}


int verify_lists (char *line, int y)
{
  char *linecopy=strdup(line);
  int tam = strlen(linecopy), nelems = 0;

  if (line[0]=='[' && line[tam-1]==']')
  {
    linecopy[tam-1]='\0';
    linecopy++;

    char *token=strsep(&linecopy, ",");
    while (linecopy) 
      {
        if (!verifyInt(token)) return 0;
        token=strsep(&linecopy, ",");
        nelems++;
      }
  }
  else return 0;
  return (nelems==0 || nelems+1 == y);
}



int verifyBool (char*token)
{
  if( strcmp(token,"False")==0 || strcmp(token,"True")==0) return 1;
  else return 0;
}



int verify_date (char*token)
{
  int year=-1,month=-1,day=-1,hour=-1,min=-1,sec=-1;

  if (strlen(token)<19 || token[19]!='\0') return 0;

  sscanf(token,"%d-%d-%d %d:%d:%d",&year ,&month ,&day ,&hour ,&min ,&sec);

  if(year < 2005 || year > 2021)return 0;
  if(month < 1 || month > 12) return 0;
  if(day < 1 || day > 31) return 0;
  if(day == 31 && ((month < 8 && month%2==0)||(month >= 8 && month%2==1)))return 0;
  if(day == 30 && month == 2) return 0;
  if(day == 29 && month == 2 && (year%4 != 0 || (year%4 == 0 &&  year%10 == 0)) ) return 0;
  if (year==2005&&month<4) return 0;
  if (year==2005&&month==4 && day<7) return 0;
  if(0 > hour || hour > 23) return 0;
  if(0 > min || min > 59) return 0;
  if(0 > sec || sec > 59) return 0;

  return 1;
}


int checkUsers(char line[])
{
    char *linecopy=strdup(line),*copy=linecopy;
    char *token=strsep(&linecopy, ";");
    int j, i;


    for(i=0; i<10; i++)
    {
        if(i==1)
        {
          if (strlen(token)==0) return 0;
        }
        else if(i==2)
        {
          if (!verify_type(token)) return 0;
        }
        else if(i==3)
        {
          if (!verify_date(token)) return 0;
        }
        else if(i==5 || i==7)
        {
          if (!verify_lists(token,j)) return 0;
        }
        else
        {
          if ((j=verifyInt(token))==-1) return 0;
        }


        if (i<8) token=strsep(&linecopy, ";");
        if (i==8) token=linecopy;
    }
    free(copy);
    return 1;
}


int checkCommits (char line[])
{
    char *linecopy=strdup(line),*copy=linecopy;
    char *token=strsep(&linecopy, ";");
    int i,ctr=1;


    for(i=0; i<5; i++)
    {
        if(i==3)
        {
          if (!verify_date(token)) return 0;
        }
        else if(i==4)
        {
        }
        else
        {
          if ((verifyInt(token))==-1) return 0;
        }

        if (i<3) token=strsep(&linecopy, ";");
        if (i==3) token=linecopy;
    }
    free(copy);
    return 1;
}


int checkRepos(char *line)
{
    char *linecopy=strdup(line);
    char *token=strsep(&linecopy, ";");
    int i;

    for(i=0; i<14; i++)
    {
    
        if(i==2)
        {
          if (strlen(token)==0) return 0;
        }
        else if(i==3)
        {
          if (strlen(token)==0) return 0;
        }
        else if(i==6)
        {
          if (strlen(token)==0) return 0;
        }
        else if(i==7)
        {
          if (strlen(token)==0) return 0;
        }
        else if(i==4)
        {
          if (!verifyBool(token)) return 0;
        }
        else if(i==5)
        {
        }
        else if(i==8)
        {
          if (!verify_date(token)) return 0;
        }     
        else if(i==9)
        {
          if (!verify_date(token)) return 0;
        }
        else
        {
          if ((verifyInt(token))==-1) return 0;
        }

        if (i<12) token=strsep(&linecopy, ";");
        if (i==12) token=linecopy;
    }
    return 1;
}


int main ()
{
  FILE *users;
  FILE *users_ok;
  char *line = malloc(LINE_SIZE_MAX);

  users = fopen("entradas/users.csv", "r");
  users_ok= fopen("saidas/users-ok.csv", "w");

  fgets(line,LINE_SIZE_MAX,users);
  fprintf(users_ok, "%s", line);
    while (fgets(line,LINE_SIZE_MAX,users))
    {
      if (checkUsers(line)) fprintf(users_ok, "%s", line);
    }
  fclose(users);
  fclose(users_ok);

  

  FILE *repos;
  FILE *repos_ok;

  repos = fopen("entradas/repos.csv", "r");
  repos_ok= fopen("saidas/repos-ok.csv", "w");

  fgets(line,LINE_SIZE_MAX,repos);
  fprintf(repos_ok, "%s", line);
    while (fgets(line,LINE_SIZE_MAX,repos))
    {
      if (checkRepos(line)) fprintf(repos_ok, "%s", line);
    }
  fclose(repos);
  fclose(repos_ok);


  FILE *commits;
  FILE *commits_ok;

  commits = fopen("entradas/commits.csv", "r");
  commits_ok= fopen("saidas/commits-ok.csv", "w");

  fgets(line,LINE_SIZE_MAX,commits);
  fprintf(commits_ok, "%s", line);
    while (fgets(line,LINE_SIZE_MAX,commits))
    {
      if (checkCommits(line)) fprintf(commits_ok, "%s", line);
    }
  fclose(commits);
  fclose(commits_ok);


  return 0;
}
