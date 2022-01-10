*webserv.c*: Code to be compiled and run in the server. It opens a TCP socket listening on 8080 port. It accepts requests with a specific character indicating the purpose of the request.
```
$ make
$ ./webserv&
```