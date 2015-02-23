To prep db:
<ol>
<li>make sure database username and pw match that in lib/user.js</li>
<li>execute `create database cmpe275project2;`</li>
</ol>

To run app:
<ol>
<li>npm install</li>
<li>npm start</li>
</ol>

To use circuit breaker:<br>
`TOOBUSY=true MAXLAG=70 npm start`<br>
Note: MAXLAG is optional. Must be at least 10 if set. Defaults to 70 ms.

Sample files of interest:
<ul>
<li>lib/db.js: db from orm module. Contains db connection info.</li>
<li>lib/user.js: User schema.</li>
<li>routes/users.js: User route.</li>
<li>app.js: If you want to do define a new route file, be sure to requre the file here and app.use it</li>
</ul>
