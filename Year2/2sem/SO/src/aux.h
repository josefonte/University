#ifndef AUX_H
#define AUX_H

typedef struct transf{
    char* transf;
    int running;
    int max;
} *TRANSF;

typedef struct request{
    int pid;
    int running;
    int priority;
    int number_arg;
    int max;
    char **args;
} *REQUEST;

TRANSF readConfigFile(int source);
ssize_t readln(int fd, char* line, size_t size);
int parseBegin(char* line, char* op, char* server2client_str);

void mergesortLista(REQUEST lista, int length);
void merge(REQUEST arr, int l, int m, int r);
void mergeSort(REQUEST arr, int l, int r);
void limpaListaTransf(TRANSF lista);

REQUEST init_requestLIST(int max);
int isResquestListEmpty(REQUEST lista, int pos);
int findPOSpid(REQUEST lista, int max, int pid);


int addRequest(TRANSF tracker,REQUEST lista, int pos, char* str);
REQUEST createRequest(char* str);
int findValidRequest(TRANSF transfLIST, REQUEST requestLIST, int pos);
int isRequestValid(TRANSF transfLIST, REQUEST requestLIST, int pos, int tracker[]);
void limpaRequest(REQUEST lista, int pos);
void limpaListaRequest(REQUEST lista, int pos);
void diminuiTracker(TRANSF transfLIST, int tracker[]);
void aumentaTracker(TRANSF transfLIST, int tracker[]);
void getRequestTracker(int tracker[], REQUEST requestLIST, int pos);

int isValidTransf(char* str);
int isValidPrio(char* str);
int isValidReq(char* str);

#endif
