REST API ReadMe
===========

Things to note:
----
- Code is not completed
- Main class ("RestApiEngine") is the package "edu.sjsu.cmpe275.p1test.restapi" 
- The pojo package has a simpele object for storage and room for changes without affect the webservices
- The services package contains a simple data store (simple hashmap in memory, not good but effective)
- The ws service contains a few controllers:
   - "Index" -> just says Hi for any page
   - "SimpleUsersRestAPI" -> REST API for users (base url: "/api/users"): GET "/" for list of users and GET "/{user}" for list of files for that user
   - "SimpleFilesRestApi" -> REST API for files (base url: "/api/images"): POST to "/" to Create, GET/PUT/DELETE to "/{uuid}" for the approriate task


```
This was some really quick coding!!!
```
