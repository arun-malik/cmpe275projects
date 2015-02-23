var express = require('express');
var logger = require('morgan');
var bodyParser = require('body-parser');

var routes = require('./routes/index');
var users = require('./routes/users');
var category = require('./routes/category');

var app = express();

// circuit breaker
if(process.env.TOOBUSY === "true"){
  console.log('enabling toobusy');
  var toobusy = require('toobusy');

  // MAXLAG must be a number. Optional. 
  // Default = 70 b/c toobusy repo says that is the value used if it is not set explicitly.
  var maxLag = +process.env.MAXLAG || 70;
  console.log('setting maxLag to ' + maxLag + ' ms');
  toobusy.maxLag(maxLag);

  // add toobusy middleware
  app.use(function(req, res, next) {
    if (toobusy()) {
      res.status(503).json({err: "I'm busy right now, sorry."});
    } else {
      next();
    } 
  });
}

app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded());

app.use('/', routes);
app.use('/users', users);
app.use('/category', category);

/// catch 404 and forward to error handler
app.use(function(req, res, next) {
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});

// development error handler
// will print stacktrace
app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.json({
        message: err.message,
        err: err
    });
});

module.exports = app;
