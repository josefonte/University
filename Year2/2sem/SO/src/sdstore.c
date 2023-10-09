#include <sys/types.h>
#include <sys/wait.h>/* chamadas wait*() e macros relacionadas */
#include <sys/stat.h>
#include <unistd.h> /* chamadas ao sistema: defs e decls essenciais */
#include <fcntl.h> /* O_RDONLY, O_WRONLY, O_CREAT, O_* */
#include "aux.h"
#include <string.h>
#include <stdlib.h>
#include <stdio.h>

#define BUF_SIZE 1024

int main(int argc, char** argv){
    
    pid_t pid_client = getpid();
    char* pipe_name = malloc(10*sizeof(char));
    sprintf(pipe_name,"%d", pid_client);

    if(mkfifo(pipe_name, 0666) == -1){
        perror("failed mkfifo");
    }

    if(argc<2){
        char* error_msg = malloc(sizeof(char)*1024);
        sprintf(error_msg, "INVALID COMMAND: Wrong number of arguments");
        write(2, error_msg,strlen(error_msg));
        
        unlink(pipe_name);
        free(pipe_name);
        free(error_msg);
        return -1;
    }

    int client2server = open("client2server", O_WRONLY);
    if ( client2server== -1){
        perror("No pipe client->server. Run server first");
        
        unlink(pipe_name);
        free(pipe_name);
        return -1;
    }
    
    char *pipe_input= calloc(BUF_SIZE,sizeof(char));
    char *str_pid_client = malloc(10*sizeof(char));
    
    sprintf(str_pid_client,"%d;", pid_client);
    
    strcat(pipe_input,str_pid_client); 


    for(int i=1;i<argc; i++){
        if((isValidReq(argv[i]) && i==1) || isValidTransf(argv[i]) || (i==2 || i==3|| i==4)){
           
            char* str_input = malloc(50*sizeof(char));
            char *str;

            if(i==2 && !isValidPrio(argv[2])) {
                strcat(str_input,"0;");
                str = strdup(argv[2]);
                strcat(str_input,str);
                }
            else  {
                str = strdup(argv[i]);
                strcat(str_input,str);
            }
            
            if(i!=argc-1) {
                strcat(str_input,";"); }

            free(str);
            strcat(pipe_input,str_input);   
            free(str_input); 
        }else{
            char* error_msg = malloc(sizeof(char)*1024);
            sprintf(error_msg, "Argument incorrect or not available (%s) \n", argv[i]);
            write(2, error_msg,strlen(error_msg));
            
            unlink(pipe_name);
            close (client2server);
            free(pipe_name);
            free(pipe_input);
            free(str_pid_client);
            free(error_msg);
            return -1;
        }
    }

    write(client2server,pipe_input,strlen(pipe_input));
    close (client2server);


    int server2client = open(pipe_name, O_RDONLY);

    int helper = open(pipe_name, O_WRONLY);

    char* buffer = malloc(BUF_SIZE*sizeof(char));

    ssize_t bytesread;

    while((bytesread = read(server2client, buffer, BUF_SIZE))>0){ 
        write (1, buffer, bytesread);   
        buffer[9] = '\0';
        if(!strcmp(buffer,"concluded") || !strcmp(argv[1],"status") ) close(helper);
    }

    unlink(pipe_name);
    close (server2client);
    free(pipe_name);
    free(pipe_input);
    free(str_pid_client);
    free(buffer);

    return 0;

      
}