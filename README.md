# CodeKataBattle

## RASD

Link to Google Docs: [https://docs.google.com/document/d/10vZeTFIDdeDSkQcuF5zIVIuLRGAfrsiJ/edit?usp=sharing&ouid=104616082306459956243&rtpof=true&sd=true](https://docs.google.com/document/d/10vZeTFIDdeDSkQcuF5zIVIuLRGAfrsiJ/edit?usp=sharing&ouid=104616082306459956243&rtpof=true&sd=true) <br/>
The link has "Editor" permissions and SHOULD allow for the viewing of the contributions history.

## DD

Link to Google Docs: [https://docs.google.com/document/d/1qk5tuozckmCNdFvtgnDkPkqSirofi-9G/edit?usp=sharing&ouid=104616082306459956243&rtpof=true&sd=true](https://docs.google.com/document/d/1qk5tuozckmCNdFvtgnDkPkqSirofi-9G/edit?usp=sharing&ouid=104616082306459956243&rtpof=true&sd=true)

## Additional resources

### UI Design

This is the link to a PDF export from Figma of all the designed frames.
[https://drive.google.com/file/d/1xVp7ZCkC3g13-1L6ZIunNzYIuDC6_irG/view?usp=sharing](https://drive.google.com/file/d/1xVp7ZCkC3g13-1L6ZIunNzYIuDC6_irG/view?usp=sharing)

## ITD

Link to Google Docs: [https://docs.google.com/document/d/11cRw45dfITrOJOHw46YCNCGwLU6H1duW/edit?usp=sharing&ouid=104616082306459956243&rtpof=true&sd=true](https://docs.google.com/document/d/11cRw45dfITrOJOHw46YCNCGwLU6H1duW/edit?usp=sharing&ouid=104616082306459956243&rtpof=true&sd=true)

## Deployment Instructions and Tips

We recommend [Render](https://render.com) as a cloud platform for deploying the servers. The microservice server can be uploaded along with its database (as it is also run in a container). 

Please make sure that the jars are correctly built before pushing to the deployment repository and that the /target folders are **not** in the gitignore. Also make sure that **only one jar** is present in each /target folders to avoid that the Dockerfile and docker-compose scripts run the wrong jar. We say this because the main application server is packaged as a [shaded fat jar](https://imagej.net/develop/uber-jars#:~:text=An%20uber%2DJAR%E2%80%94also%20known,needing%20any%20other%20Java%20code.) (this is because we needed the JUnit TestEngine to run the automatic evaluation) along with it other jars. 

The correct one should be called something like *CKBApplicationServer-0.0.1-SNAPSHOT-shaded.jar*, in any case the correct jar is the one that ends in *-shaded.jar*

## Authentication microservice installation instructions

For local running and testing, you need docker installed on your computer.
Run the following command in the AuthenticationService directory inside the docker folder, so AuthenticationService>docker.
Also create, in that folder, the directory database>data to memorize the information of the database in that folder:

```
docker-compuse up --build
```

This command will create the container with the server and the integrated database. The server wil remain listening on port 8081 ready to receive requests from CKBApplication

## GitHub Action

This is the github action that group leaders must insert into their repositories to send updates to Code Kata Battle's API. The API KEY is generated at group creation and is only visible to the leader.

```yml
on: [push]
jobs:
  callCKBAPI:
    runs-on: ubuntu-latest
    steps:
    - name: Call CKB API
      uses: dkershner6/post-api-call-action@v2
      with:
        url: ${{ secrets.API_URL }}/github/push
        data: "{\"command\": \"publish\"}"
        headers: "{\"Authorization\": \"Bearer ${{ secrets.API_KEY }}\"}"
```

## Group members

- [Michele Bersani](https://github.com/MikiBersa)
- [Paolo Chiappini](https://github.com/paolo-chiappini)
- [Andrea Fraschini](https://github.com/Andrea01Fraschini)
