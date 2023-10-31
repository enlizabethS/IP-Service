# IP Scanner Application

The application scans a range of IP addresses and displays all found domain names present in the SSL certificates of those IP addresses, if available.

## Requirements

- Java 8
- Web Request Framework: Apache Http Client
- Web Interface Framework: Javalin
- Maven
- Prohibition of using Spring Framework
- Frontend to be developed in either pure HTML&CSS or Bootstrap 5. React, Angular, etc., are not allowed.

## Functionality

- Accepts an input range of IP addresses in the format, e.g., 51.38.24.0/24 (51.38.24.0-51.38.24.255).
- Specifies the number of threads to be used for scanning.
- The application evenly distributes IP addresses across threads for scanning.
- Obtains SSL certificates for each IP address, if available, and searches for any domains within the certificates.
- Saves all discovered domains to a text file.
- The program contains a web interface that handles all interactions between the user and the application.

## Technologies and Frameworks

- **Java 8:** The programming language used for the application.
- **Apache Http Client:** Framework for handling web requests.
- **Javalin:** Framework for developing the web interface.
- **Maven:** Build automation and project management tool.
