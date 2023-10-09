# Projeto-SO 💻⚡

No âmbito da cadeira de Sistemas Operativos, o grupo 98 desenvolveu um programa em C que pretende implementar um serviço que permitisse aos utilizadores armazenar uma cópia dos seus ficheiros de forma segura e eficiente, poupando espaço de disco.

Para tal o serviço disponibilizará funcionalidades de compressão/descompressão e cifragem/decifragem dos ficheiros a serem armazenados. As 7 transformações disponíveis são:
   - __bcompress__ / __bdecompress__. Comprime / descomprime dados com o formato bzip2.
   - __gcompress__ / __gdecompress__. Comprime / descomprime dados com o formato gzip.
   - __encrypt__ / __decrypt__. Cifra / decifra dados.
   - __nop__. Copia dados sem realizar qualquer transformação.

Como funcionalidades básicas o serviço deve permitir a submissão de pedidos para processar e
armazenar novos ficheiros bem como recuperar o conteúdo original de ficheiros guardados
previamente. É possível ainda consultar as tarefas de processamento de ficheiros a serem efetuadas
num dado momento.

Como funcionalidades avançadas o serviço deve permitir obter estatísticas sobre o tamanho do
documento de input e de output, implementar a prioridade de pedidos e fechar o servidor
graciosamente com o sinal SIGTERM.


---------------

Para compilar o programa usa-se o _Makefile_:
 
 `$ make` : para compilar
 
 `$ make clean` : para limpar
 
 ---------------
  
Para correr o programa abre-se dois terminais, um do servidor e outro do cliente.

O servidor deve ser executado primeiro com o seguinte formato.
 
 `$ ./sdstored <server-configfile> <pasta de exectuaveis das transformações>`
 
 Neste caso este ficheiro e diretoria já existem por isso é só correr o comando:
  
  ``` $ ./sdstored src/config.txt bin/ ```
  
---------------
  
 Para executar os clientes/pedidos (podem ser executados vários ao mesmo tempo) corre-se o comando com os formatos: 
 
`./sdstore status` : para saber o estado do servidor.

`./sdstore proc-file priority input-filename output-filename transf1 transf2 ...` : para executar transformações no input-file.

Um exemplo de uma execução de transformações é:

`./sdstore proc-file 1 file1.txt file2 nop bcompress gcompress e encrypt`

E para retornar o ficheiro de output à sua forma original faz-se as operações contrárias:

```./sdstore proc-file 1 file2 file3 decrypt gdecompress bdecompress```

---------------

Projeto SO | 2ºano | 2ºSemestre | Universidade do Minho 2021/2022
