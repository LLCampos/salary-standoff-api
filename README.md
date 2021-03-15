# Salary Standoff API

Back-end of a tool that checks if a candidate's salary expectation is compatible with a company's budget, without leaking any numbers to any of the parties.

Front-end is available [here](https://github.com/LLCampos/salary-standoff-ui).

## Running
You can run the microservice with `sbt run`. By default it listens to port number 8080, you can change
this in the `application.conf`.

## Acknowledgements
* This was build on top of https://github.com/jaspervz/todo-http4s-doobie
