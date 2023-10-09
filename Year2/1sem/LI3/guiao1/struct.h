#define MAX_USERS 6000000
#define MAX_REPOS 20000000
#define MAX_COMMITS 20000000
#define MAX_SIZE 200000

typedef char *KEY;

typedef struct users
{
	KEY id;
    char state;
} * USERS;


typedef struct repos 
{
	KEY id; 
	char state; 
} * REPOS;

typedef struct commits 
{
	KEY id;
	char state; // 0 = free; 1 = used;
	int count;
} * COMMITS;



void printUSERS(USERS t);
int searchID (KEY i,USERS table);
int add_users (KEY i,USERS table);
unsigned long hash(KEY k) ;
void initUsers (USERS table);
void initREPOS (REPOS repo_table) ;
int addREPOS (KEY i, REPOS repo_table);
int searchREPOS (KEY i,REPOS repo_table);
int addCOMMITS (KEY i, COMMITS com_table);
void initCOMMITS (COMMITS com_table) ;
int searchCOMMITS (KEY i , COMMITS com_table) ;