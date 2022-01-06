#include <unistd.h>
#include <stdio.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <string.h>

#define PORT 8080
#define MAXBUFFERSIZE 1024 // 10 bit buffer

int main(int argc, char const *argv[]){
    int server_fd, new_socket, valread;
    struct sockaddr_in address;
    int opt = 1;
    int addrlen = sizeof(address);
    char buffer[MAXBUFFERSIZE] = {0};
    char *ok = "OK"; // OK
    char *nok = "NOK"; // Not OK

    int healthcheck = 0;
    while(1){
        // Creating socket file descriptor
        if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0){
            perror("socket failed");
            exit(EXIT_FAILURE);
        }
        
        // Forcefully attaching socket to the port 8080
        if (setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR | SO_REUSEPORT, &opt, sizeof(opt))){
            perror("setsockopt");
            exit(EXIT_FAILURE);
        }
        address.sin_family = AF_INET;
        address.sin_addr.s_addr = INADDR_ANY;
        address.sin_port = htons(PORT);
        // Forcefully attaching socket to the port 8080
        if (bind(server_fd, (struct sockaddr *)&address, sizeof(address))<0){
            perror("bind failed");
            exit(EXIT_FAILURE);
        }
        if (listen(server_fd, 3) < 0){
            perror("listen");
            exit(EXIT_FAILURE);
        }
        if ((new_socket = accept(server_fd, (struct sockaddr *)&address, (socklen_t*)&addrlen))<0){
            perror("accept");
            exit(EXIT_FAILURE);
        }
        valread = read( new_socket , buffer, 1024);
        printf("%s\n",buffer );

        if(*buffer == 'N'){ // NO
            healthcheck = 1; // There's a problem with user
            close(server_fd);
        }
        if(*buffer == 'Y'){ // YES
            healthcheck = 0; // User is OK
            close(server_fd);
        }

        if(*buffer == 'R'){
            if(healthcheck == 0){ // User is ok
                send(new_socket , ok , strlen(ok) , 0);
                close(server_fd);
            }
            if(healthcheck == 1){ // User is not ok
                send(new_socket , nok , strlen(nok) , 0);
                close(server_fd);
            }
        }
	else{
	    close(server_fd);
	}
    }
    return 0;
}
