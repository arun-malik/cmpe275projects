/**
 * Created by Sai on 12/6/2014.
 *
 * There are probably more elegant ways of doing this but I'm keeping this simple and quick
 */
module.exports = {
    // Server to test
    server : {
        hostname: '192.168.42.2',
        port: 3000
    },


    /****************
     * Users
     ****************/
    // get users
    users : function (method) { // GET or POST
        return {
            hostname    : this.server.hostname,
            port        : this.server.port,
            path        : '/users',
            method      : method
        };
    },

    // get a url for a specific users
    usersGetUser : function (userId) {
        return {
            hostname    : this.server.hostname,
            port        : this.server.port,
            path        : '/users/'+userId,
            method      : 'GET'
        };
    },




    /****************
     * Category
     ****************/
    // categories
    categoryGet : function(method) {
        return {
            hostname    : this.server.hostname,
            port        : this.server.port,
            path        : '/category',
            method      : method
        };
    },

    // get a url for a specific category
    categoryGetCategory : function (id) {
        return {
            hostname    : this.server.hostname,
            port        : this.server.port,
            path        : '/category/'+id,
            method      : 'GET'
        };
    },




    /****************
     * Product
     ****************/
    // get/post a url for products in a specific category
    productsInACategory : function (method, catId) {
        return {
            hostname    : this.server.hostname,
            port        : this.server.port,
            path        : '/category/'+catId+'/product',
            method      : method
        };
    },

    // get a url for getting a product in a specific category
    specificProductInACategory : function (method, catId, prodId) {
        return {
            hostname    : this.server.hostname,
            port        : this.server.port,
            path        : '/category/'+catId+'/product/'+prodId,
            method      : method
        };
    },



    /****************
     * Offer
     ****************/
    offersForAProduct : function (method, catId, prodId) {
        return {
            hostname    : this.server.hostname,
            port        : this.server.port,
            path        : '/category/'+catId+'/product/'+prodId+'/offer',
            method      : method
        };
    },

    specificOfferForAProduct : function (method, catId, prodId, offerId) {
        return {
            hostname    : this.server.hostname,
            port        : this.server.port,
            path        : '/category/'+catId+'/product/'+prodId+'/offer/'+offerId,
            method      : method
        };
    },



    /****************
     * History
     ****************/
    historyForAProduct : function (method, catId, prodId, offerId) {
        return {
            hostname    : this.server.hostname,
            port        : this.server.port,
            path        : '/category/'+catId+'/product/'+prodId+'/offer/'+offerId+'/history',
            method      : method
        };
    },


    /****************
     * Comment
     ****************/
    commentForAProduct : function (method, catId, prodId, offerId) {
        return {
            hostname    : this.server.hostname,
            port        : this.server.port,
            path        : '/category/'+catId+'/product/'+prodId+'/offer/'+offerId+'/comment',
            method      : method
        };
    }
};