# Millennium Falcon
## Samuele Di Tullio sditullio@tutanota.com

For reference https://github.com/lioncowlionant/developer-test 

- To build the project, run: 
    - *mvn clean verify*


- To launch the application, run: 

    - *mvn spring-boot:run -Dspring-boot.run.arguments=[PATH_TO_INITIALIZATION_FILE]*
 
    Or, to use the file already present in the application path, run:

  - *mvn spring-boot:run -Dspring-boot.run.arguments=./millennium-falcon.json*


- The Backend will be serving request at the following URL: http://localhost:8080/c3po/intercept
(Accept empire.json, that is a JSON object containing the empire intereceptions, as described in the specification Readme)


- To launch the GUI, visit: http://localhost:8080

---
TODO:
- CLI implementation
- Render API call result in html
- Add algorithm to stop in one planet in order to avoid Bounty Hunters
