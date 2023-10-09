#include <sys/types.h>
#include <unistd.h> /* chamadas ao sistema: defs e decls essenciais */
#include <fcntl.h> /* O_RDONLY, O_WRONLY, O_CREAT, O_* */
#include <sys/wait.h>/* chamadas wait*() e macros relacionadas */
#include <sys/types.h>
#include <sys/stat.h>
#include "aux.h"
#include <string.h>
#include <stdlib.h>
#include <stdio.h>




int parseBegin(char* line, char* op, char* server2client_str){
    
    
    char* token;

    int i=0;

    char* command = strdup(line);
    char *guide = command;

    while ((token = strsep(&guide, ";"))!= NULL && i!=2){
        if(i==0) {
            strcpy(server2client_str,token);
        }
        else if(i==1) {
           strcpy(op,token);
        }
        i++;
    }

    free(command);
    return i;
}

ssize_t readln(int fd, char* line, size_t size){

    ssize_t i=0;

    while (i<size-1){
        ssize_t scanner = read(fd, &line[i], 1);
        if (scanner<1) break;
        if(line[i++] == '\n') break;
    }

    line[i] = '\0';

    return i;
}



TRANSF readConfigFile(int source){
    
    TRANSF lista = malloc(7*sizeof(struct transf));
    

    int BUFFER_SIZE = 100;
    char* buffer = malloc (sizeof(char)* BUFFER_SIZE);
    char* buf_cop;

    ssize_t scanner;
    int i = 0,
        j = 0;
    
    while ((scanner = readln(source,buffer, BUFFER_SIZE))>0){
        
        buf_cop = strdup(buffer);
        char *guide = buf_cop;
        char *token;
        lista[i].running = 0;

        while ((token = strsep(&guide, " "))!= NULL){
            if(!j){
                lista[i].transf  = strdup(token);  
                j=1;
            }
            else  {
                lista[i].max = atoi(token);  
                j=0;
            }
        }
        i++;
        free(buf_cop);
    }  

    free(buffer);
    close(source);

    return lista;
}


void mergesortLista(REQUEST lista, int length) {
    mergeSort(lista, 0, length - 1);
}

void mergeSort(REQUEST lista, int l, int r){
    if (l < r) {
       
        int m = l + (r - l) / 2;
  
        mergeSort(lista, l, m);
        mergeSort(lista, m + 1, r);
  
        merge(lista, l, m, r);
    }
}
void merge(REQUEST lista, int l, int m, int r)
{
    int i, j, k;
    int n1 = m - l + 1;
    int n2 = r - m;
  
    REQUEST L = init_requestLIST(n1); 
    REQUEST R = init_requestLIST(n2);
  
    for (i = 0; i < n1; i++)
        L[i] = lista[l + i];
    for (j = 0; j < n2; j++)
        R[j] = lista[m + 1 + j];
  
    
    i = 0; 
    j = 0; 
    k = l; 
    while (i < n1 && j < n2) {
        if (L[i].priority > R[j].priority) {
            lista[k] = L[i];
            i++;
        }
        else {
            lista[k] = R[j];
            j++;
        }
        k++;
    }
  
    while (i < n1) {
        lista[k] = L[i];
        i++;
        k++;
    }
  
    
    while (j < n2) {
        lista[k] = R[j];
        j++;
        k++;
    }
    free(L), free(R);
}
  

void limpaRequest(REQUEST lista, int pos){
    
    for(int i=0; i<lista[pos].number_arg; i++){
        free(lista[pos].args[i]);
    }
   
    lista[pos].number_arg = 0;
    lista[pos].max = 0;
    lista[pos].pid = 0;
    lista[pos].priority = 0;
}

void limpaListaRequest(REQUEST lista, int max){
    for(int j=0; j<max; j++){
        limpaRequest(lista, j);
        free(lista[j].args);
    }

    free(lista);
}
void limpaListaTransf(TRANSF lista){
    for(int pos=0; pos<7; pos++)
        free(lista[pos].transf);

    free(lista);
}


REQUEST init_requestLIST(int max){
    malloc(max*sizeof(struct request));
}

int findPOSpid(REQUEST lista, int max, int pid){
    for(int i=0; i<max; i++){
        if(lista[i].pid == pid) return i;
    }
    return -1;
}


int addRequest(TRANSF tracker, REQUEST lista, int pos, char* line){
    int pos_max = pos;
    for(int i = 0; i<pos; i++){
        if (lista[i].pid == 0) {
            pos = i;
            break;
        } 
    }
    char* comm = strdup(line);
    char *guide = comm;
    char *token;
    int i=-1;

    lista[pos].running = 0;
    lista[pos].pid = 0;
    lista[pos].priority = 0;
    lista[pos].number_arg = 0;
    lista[pos].max = 8;
    lista[pos].args = (char **) malloc(lista[pos].max *sizeof(char*));

    while ((token = strsep(&guide, ";"))!= NULL){ 

        if (lista[pos].number_arg>=lista[pos].max){
            lista[pos].max *= 2;
            lista[pos].args = realloc(lista[pos].args, lista[pos].max  * sizeof(64 * sizeof(char)));
        } 

        if(i==-1) lista[pos].pid = atoi(token);
        
        else {
            if(i==1) lista[pos].priority = atoi(token);
            lista[pos].args[i] = malloc(64 * sizeof(char));
            strcpy(lista[pos].args[i],token);
            lista[pos].number_arg++;
        }
        i++;
    }
    free(comm);

    int reqtracker[7] = {0};
    getRequestTracker(reqtracker, lista, pos);

    int valid = 0;
    for(int j = 0; j<7; j++) {
        if( reqtracker[j] > tracker[j].max){ 
            valid=-1;
            break;
        }
    }

    if(valid==-1) {
        limpaRequest(lista, pos);
        pos=-1;
    }

    
    int pid = lista[pos].pid;
    
    //mergesortLista(lista,pos_list);

    pos=-1;

    for(int j=0; j<pos_max+1; j++){
        char *str_s = malloc (1024*sizeof(char));
        if(lista[j].pid == pid) return j;
    }

    return pos;
}


int findValidRequest(TRANSF transfLIST, REQUEST requestLIST, int pos) {
    int valid = -1;


    for(int i = 0; i<pos ; i++) {
        if (requestLIST[i].pid != 0 && requestLIST[i].running == 0){
            int reqTracker[7] = {0};
            

            getRequestTracker(reqTracker, requestLIST, i);
            
            
            int valid = isRequestValid(transfLIST, requestLIST, i, reqTracker);
            
            

            if (valid!=-1) return valid;
        }
    }

    return valid;
}

int isRequestValid(TRANSF transfLIST, REQUEST requestLIST, int pos, int tracker[]) {
    
    int valid = pos;


    if (requestLIST[pos].pid != 0 && requestLIST[pos].running == 0) {
        for(int j = 0; j<7; j++) {
            
            if(transfLIST[j].running + tracker[j] > transfLIST[j].max){ 
                valid=-1;
                break;
            }
        }
    }
    
    return valid;
}

void getRequestTracker(int tracker[], REQUEST requestLIST, int pos){

    if (requestLIST[pos].pid != 0) {
        for(int j = 4; j<requestLIST[pos].number_arg; j++) {
           
            if(!strcmp(requestLIST[pos].args[j], "nop")) {
                tracker[0]++;
            }
            else if(!strcmp(requestLIST[pos].args[j], "bcompress")) {
                tracker[1]++;
            }
            else if(!strcmp(requestLIST[pos].args[j], "bdecompress")) {
                tracker[2]++;
            }
            else if(!strcmp(requestLIST[pos].args[j], "gcompress")) {
                tracker[3]++;
            }
            else if(!strcmp(requestLIST[pos].args[j], "gdecompress")) {
                tracker[4]++;
            }
            else if(!strcmp(requestLIST[pos].args[j], "encrypt")) {
                tracker[5]++;
            }
            else if(!strcmp(requestLIST[pos].args[j], "decrypt")) {
                tracker[6]++;
            }
        } 
    }

}


void aumentaTracker(TRANSF transfLIST, int tracker[]) {    
    for(int j = 0; j<7 ; j++) {
        transfLIST[j].running += tracker[j]; 
    }
}


void diminuiTracker(TRANSF transfLIST, int tracker[]){
    for(int j = 0; j<7 ; j++) {
        transfLIST[j].running -= tracker[j]; 
    }
}


int isResquestListEmpty(REQUEST lista, int pos){
    for(int i = 0 ; i<pos; i++){
        if(lista[i].pid != 0 ) return 1;
    } 
    return 0;
}

int isValidTransf(char* str){
    if(!strcmp(str,"nop") || 
       !strcmp(str,"bcompress") || 
       !strcmp(str,"bdecompress") || 
       !strcmp(str,"gcompress") || 
       !strcmp(str,"gdecompress") || 
       !strcmp(str,"encrypt") || 
       !strcmp(str,"decrypt")) return 1;
    else return 0;
}

int isValidPrio(char* str){
    if(!strcmp(str,"0") ||
       !strcmp(str,"1") ||
       !strcmp(str,"2") ||
       !strcmp(str,"3") ||
       !strcmp(str,"4") ||
       !strcmp(str,"5")) return 1;
    else return 0;
}

int isValidReq(char* str){
    if(!strcmp(str,"status") ||
        !strcmp(str,"exit") ||
       !strcmp(str,"proc-file")) return 1;
    else return 0;
}


