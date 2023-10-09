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

void exitServer(int signal){

    int client2server = open("client2server", O_WRONLY);
    if(client2server == -1) {
        perror("Pipe not open correctly: ");
    }

    write(client2server, "0000;exit", 11);  
    close(client2server);
}


int exec_command(char* transf_folder,char* arg){

    int exec_ret = 0;

    char* transf = strdup(arg);
    char* path = malloc(100*sizeof(char));
    sprintf(path, "%s/%s", transf_folder,transf);

    exec_ret = execl(path, transf,NULL);

    free(transf);
    free(path);

    return exec_ret;
}


int executar_pedido(REQUEST lista, int pos, int pipe_fd, char* transf_folder){  
    //------------------------------------------------------
    int input_file = open(lista[pos].args[2], O_RDONLY);
    if(input_file == -1) {
        perror("Input File not open correctly: ");
        write(pipe_fd,"concluded - Input File not open correctly",11);
        return(-1); 
    }

    //------------------------------------------------------
    int output_file = open(lista[pos].args[3], O_TRUNC | O_CREAT | O_WRONLY, 0666);
    if(output_file == -1) {
        perror("Output file not open/create correctly:");
        write(pipe_fd,"concluded - Output file not open/create correctly",11);
        return(-1); 
    }

    //------------------------------------------------------

    int n_com =  lista[pos].number_arg - 4;
            
    if(n_com==1){
        
        int fd[2];
        int status;
        
            
            if(pipe(fd)!=0){
                perror("pipe falhou");
                return -1;
            }
            switch(fork()){
                case -1:
                    perror("fork falhou");
                    return -1;
                case 0:
                    dup2(input_file, 0);
                    close(fd[0]);
                    dup2(output_file, 1);
                    close(fd[1]);
                    
                    exec_command(transf_folder,lista[pos].args[4]);

                    _exit(0);
                default:
                    wait(&status);
            }
    }
    else{

        int pipes[n_com-1][2];
        int status[n_com];

        for(int c=0; c<n_com; c++){
            if(c==0){ //primeiro pipe 
                
                if(pipe(pipes[c])!=0){
                    perror("pipe falhou");
                    return -1;
                }
                switch(fork()){
                    case -1:
                        perror("fork falhou");
                        return -1;
                    case 0:
                        dup2(input_file, 0);
                        close(pipes[c][0]);
                        dup2(pipes[c][1], 1);
                        close(pipes[c][1]);

                        exec_command(transf_folder,lista[pos].args[c+4]);

                        _exit(0);
                    default:
                        close(pipes[c][1]);
                }

            }else if(c==n_com-1){
                //ultimo processo
                switch(fork()){
                    case -1:
                        perror("fork falhou");
                        return -1;
                    case 0:
                        dup2(output_file, 1);
                        dup2(pipes[c-1][0], 0);
                        close(pipes[c-1][0]);

                        exec_command(transf_folder,lista[pos].args[c+4]);

                        _exit(0);
                    default:
                        close(pipes[c-1][1]);
                }
            }else{ 
                //processos-intermédios
                if(pipe(pipes[c]) !=0){
                    perror("pipe");
                    return -1;
                }
                switch(fork()){
                    case -1:
                        perror("fork");
                        return -1;
                        break;
                    case 0:
                        close(pipes[c][0]);
                        dup2(pipes[c][1],1);
                        close(pipes[c][1]);
                        dup2(pipes[c-1][0],0);
                        close(pipes[c-1][0]);

                        exec_command(transf_folder,lista[pos].args[c+4]);

                        _exit(0);
                    default:
                        close(pipes[c][1]);
                        close(pipes[c-1][0]);

                }
            }   
        }

        for(int i=0; i<n_com; i++){
            wait(&status[i]);
        }
    }

    off_t input_size = lseek(input_file, 0, SEEK_END);
    off_t output_size = lseek(output_file, 0, SEEK_END);

    char *str_pipe = malloc(60*sizeof(char));
    sprintf(str_pipe, "concluded (bytes-input:%ld, bytes-output:%ld)\n", input_size, output_size);
    write(pipe_fd ,str_pipe, strlen(str_pipe));
    free(str_pipe);
}

//write(1, "\n", 2);
// char *str_s;
//sprintf(str_s,"%d",);
//write(1, buffer, bytes_read);
//write(1, "\n", 2);
//char *str_s = malloc (BUF_SIZE*sizeof(char));
//sprintf(str_s,"%d",);
//write(1, str_s, strlen(str_s));
//free(str_s);

int main(int argc, char** argv){

    if(argc<3) {
        perror("Low number of arguments\n");
        return -1;
    }
    


    int config_fd = open(argv[1], O_RDONLY);
    if(config_fd == -1) {
        perror("Config File not open correctly: ");
        unlink("client2server");
        return -1;
    }

    char* transf_folder = strdup(argv[2]);

    unlink("client2server");
    int client2server = open("client2server", O_RDONLY);
    
    if (client2server == -1){
        if(mkfifo("client2server", 0666) == -1){
            perror("failed mkfifo");
            return -1;
        }
        client2server = open("client2server", O_RDONLY);
    }

    signal(SIGTERM, exitServer);


    ssize_t bytes_read;
    int exit = 0, 
        pos_list = 0,
        max=2,
        del=-1,
        server2client ;


    TRANSF tracker = readConfigFile(config_fd);
    REQUEST lista = init_requestLIST(max);


    char *buffer= calloc(BUF_SIZE,sizeof(char));
    while(!exit){
        while((bytes_read = read(client2server,buffer, BUF_SIZE))>0 && !exit){
           
            //leitura do buffer
            buffer[bytes_read] = '\0';
            char *command = strdup(buffer);
        
            char *op = malloc(15*sizeof(char));
            char *server2client_str = malloc(15*sizeof(char));
            parseBegin(buffer,op, server2client_str);
            


            // se for para sair executa o sair
            if(!strcmp(op,"exit")){
                exit=1;

                while (isResquestListEmpty(lista,pos_list));

            }else if(!strcmp(op,"termina-pedido")){
                int pos = findPOSpid(lista, pos_list, atoi(server2client_str));

                int rqt[7] = {0};
                getRequestTracker(rqt,lista,pos);
                diminuiTracker(tracker, rqt);
                limpaRequest(lista, pos);
                del = findValidRequest(tracker,lista,pos_list);
                

                if(del!=-1) {
                    char *pid_s2c = malloc (100*sizeof(char));
                    sprintf(pid_s2c,"%d",lista[del].pid);
                    server2client = open(pid_s2c,O_WRONLY);   
                    free(pid_s2c);

                    goto pedido;
                }
                            
            }
            else{
                // se não for para sair executa o status ou o proc-file

                //abre o descritor para o server->client
                server2client = open(server2client_str, O_WRONLY);
                
                if(server2client == -1) {
                    perror("pipe server->client not open correctly: ");
                    exit=1;
                }

        
                // se operação for 'status'
                if(!strcmp(op,"status") && !exit){ 
                    int pid = fork();
                    if (pid==0){

                        int i;
                        for(i=0; i<pos_list; i++){
                            if(lista[i].pid!=0){ 
                                char* str = malloc(BUF_SIZE*sizeof(char*));
                                sprintf(str, "task #%d : ", i);
                                for (int j=0; j<lista[i].number_arg; j++){
                                    strcat(str, lista[i].args[j]);
                                    strcat(str, " ");
                                }
                                write(server2client, str, strlen(str));
                                write(server2client, "\n", 2);
                                free(str);
                            }
                        }   
                        for (int j=0; j<7; j++){
                            char *str2 = malloc(BUF_SIZE*sizeof(char*));
                            sprintf(str2 ,"tranfs: %s: %d/%d (running/max)\n",tracker[j].transf,tracker[j].running,tracker[j].max);
                            write(server2client, str2, strlen(str2));
                            free(str2);
                        }

                        free(transf_folder);
                        free(buffer);
                        free(op);    
                        free(command);
                        free(server2client_str);
                        close(server2client);
                        close(client2server);
                        limpaListaRequest(lista, pos_list);
                        limpaListaTransf(tracker);

                        _exit(0);
                    }

                }// se operação for 'proc-file'
                else if(!strcmp(op,"proc-file") && !exit) { 
                    
                    write(server2client, "pending\n", 9);
                    
                    int pos;
                    if(pos_list<max) {
                        pos = addRequest(tracker,lista,pos_list,command);    
                        if(!(pos<pos_list))pos_list++;     
                    }
                    else {
                        max*=2;
                        lista = realloc(lista, max * sizeof(struct request));
                        pos = addRequest(tracker,lista,pos_list,command);  
                        if(!(pos<pos_list))pos_list++;   
                    }

                    if (pos==-1){
                        write(server2client, "concluded : Pedido incompativel com configuração",51);
                    }else{
                        //int pid = lista[pos].pid; 
                        int requestTracker[7] = {0};
                        getRequestTracker(requestTracker,lista,pos);
                        del = isRequestValid(tracker,lista,pos, requestTracker);     



                        if(del!=-1){
                            pedido:
                            write(server2client, "processing\n",12);    
                            lista[del].running = 1;
                            int rqt[7] = {0};
                            getRequestTracker(rqt,lista,del);
                            aumentaTracker(tracker,rqt);

                            int pid = fork();
                            if(pid==0){
                                close(client2server);
                                client2server = open("client2server", O_WRONLY);
                                executar_pedido(lista, del, server2client, transf_folder);

                                char *str_s = malloc (BUF_SIZE*sizeof(char));
                                sprintf(str_s,"%d;termina-pedido",lista[del].pid);
                                write(client2server, str_s, strlen(str_s));
                                free(str_s);

                                free(transf_folder);
                                free(buffer);
                                free(op);    
                                free(command);
                                free(server2client_str);
                                close(server2client);
                                close(client2server);
                                limpaListaRequest(lista, pos_list);
                                limpaListaTransf(tracker);
                                _exit(0);
                            }
                        }

                    }

                }

            } 
                free(op);
                free(command);
                close(server2client);
                free(server2client_str);
        }
    }   
    
    free(buffer);
    free(transf_folder);
    limpaListaRequest(lista, pos_list);
    limpaListaTransf(tracker);
    close(client2server);
    unlink("client2server");

    return 0;

}