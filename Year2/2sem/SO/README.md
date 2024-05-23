# OS Project 💻⚡
## Grade: 16.0

As part of the Operating Systems course, group 98 developed a program in C aimed at implementing a service that allows users to securely and efficiently store copies of their files, saving disk space.

To achieve this, the service offers functionalities for compressing/decompressing and encrypting/decrypting the files to be stored. The 7 available transformations are:
- __bcompress__ / __bdecompress__: Compresses / decompresses data in bzip2 format.
- __gcompress__ / __gdecompress__: Compresses / decompresses data in gzip format.
- __encrypt__ / __decrypt__: Encrypts / decrypts data.
- __nop__: Copies data without any transformation.

The basic functionalities of the service include submitting requests to process and store new files, as well as retrieving the original content of previously stored files. It is also possible to check the processing tasks being performed at any given moment.

Advanced functionalities of the service include obtaining statistics on the input and output file sizes, implementing request priorities, and gracefully shutting down the server with the SIGTERM signal.

---------------

To compile the program, use the _Makefile_:

`$ make`: to compile

`$ make clean`: to clean

---------------

To run the program, open two terminals, one for the server and one for the client.

The server should be executed first with the following format:

`$ ./sdstored <server-configfile> <transformations executables directory>`

In this case, the file and directory already exist, so simply run the command:

`$ ./sdstored src/config.txt bin/`

---------------

To execute the clients/requests (multiple can be executed simultaneously), use the following commands:

`./sdstore status`: to check the server status.

`./sdstore proc-file priority input-filename output-filename transf1 transf2 ...`: to execute transformations on the input file.

An example of executing transformations is:

`./sdstore proc-file 1 file1.txt file2 nop bcompress gcompress encrypt`

And to return the output file to its original form, perform the opposite operations:

`./sdstore proc-file 1 file2 file3 decrypt gdecompress bdecompress`

---------------

Operating Systems Project | 2nd Year | 2nd Semester | University of Minho | Academic Year 2021/2022
