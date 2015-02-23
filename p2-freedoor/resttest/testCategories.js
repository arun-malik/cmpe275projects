/**
 * Created by Sai on 12/6/2014.
 */

var urlOptions = require('./urlOptions'),
    http = require('http');



var testCreateCategory = http.get(urlOptions.categoryGet('GET'), function(res) {
    // Verify status code
    if(res.statusCode !== 200) {
        console.log("GET CATEGORIES: FAILED, EXPECTED 200");
        return;
    }

    try {
        console.log(JSON.parse(res));
    } catch(err) {
        console.log("GET CATEGORIES: FAILED, Invalid JSON")
    }
}).on('error', function (error) {
    console.log("GET CATEGORIES: FAILED, bad request:", error)
});