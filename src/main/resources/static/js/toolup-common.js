/* global Intl */
var ToolUpCommon = function(){
    var that = this;
    this.currentJWT = null;

    const debug = true;

    this.debug = function(){
        if(debug){
            console.log(arguments);
        }
    };
    
    this.prettyTime = function(d){
        const ye = new Intl.DateTimeFormat('en', { hour: 'numeric', minute: 'numeric', second: 'numeric' })
                .format(Date.parse(d));
        return ye;
    };


    this.assertCorrectStatus = function(requestUrl, status){
        if(status === 200 || status === 202) return;
        let e = null;
        let typeE = null;
        e = status;
        if(status >= 500){
            if(status === 500) typeE = "internal server error.";
            if(status === 503) typeE = "Service unavailable.";
            e += " - server-side Bug : ";
        }else if(status >= 400){

            if(status === 400) typeE = "Bad request.";
            if(status === 401) typeE = "Unauthorized.";
            if(status === 403) typeE = "Forbidden.";
            if(status === 404) typeE = "Not found.";
            e += " - client-side Bug : ";
        }

        if(typeE) e += typeE;
        if(e){
            that.displayError(e + " on " + requestUrl);
            throw e;
        }
    };

    this.isJSON = function(text){
        try{
            JSON.parse(text);
            return true;
        }catch(ex){
            return false;
        }
    };
    
    this.secureXhrWrapper = async function(requestUrl, httpMethod, requestbody, responseType, modalId){
        try{
            that.clearError();
            that.showWaitModal(modalId);
            let result = await that.secureXhr(requestUrl, httpMethod, requestbody, responseType, that.currentJWT);
            if(result.e) throw result.e;
            
            return result;
        }catch (e){
            let errorJson = null;
            try{ 
                errorJson = JSON.parse(result.response);
            }catch(ex2){
                errorJson = null;
            }
            that.displayError(e + (errorJson ? " : " + errorJson.message : ""), "errorDiv");
            return {status:null, response:null, e:e};
        }finally{            
            that.hideWaitModal(modalId);
        }
    };
    
    this.secureXhr = async function(requestUrl, httpMethod, requestbody, responseType, bearer, refreshBearer){
        return new Promise((resolve, reject) => async function(){
            that.debug("secureXhr", requestUrl);
            try{
                resolve(await that.xhrCall(requestUrl, httpMethod, requestbody, responseType, bearer, refreshBearer));
            }catch(e){
                reject(e);
            }
        }());
    };
    
    this.xhrCall = async function(requestUrl, httpMethod, requestbody, responseType, bearer, dontRetry403){
        that.debug("xhrCall", requestUrl, httpMethod, requestbody, responseType, bearer, dontRetry403);
        return new Promise((resolve, reject) => async function(){
            let xhr = new XMLHttpRequest();
            xhr.onreadystatechange = async function() {
                if (this.readyState !== 4) return;
                try{
                     let resp;
                     
                     if(responseType === 'blob')
                         resp = this.response;
                     else
                         resp = this.responseText;
                     that.debug("xhrCall ", requestUrl + " => ",this.status, " Retry403 ? " + !dontRetry403);
                     if(this.status === 403 && !dontRetry403){
                        let bearer = await that.refreshToken();
                        that.debug("xhrCall -> JWT ", bearer);
                        resolve(await that.xhrCall(requestUrl, httpMethod, requestbody, responseType, bearer, true));
                        return;
                     }

                     that.assertCorrectStatus(requestUrl, this.status);

                     resolve({status:this.status, response:resp, e:null});
                 }catch(e){
                     reject(e);
                     return;
                 }
            };

            if(responseType) xhr.responseType = responseType;
            if(bearer) xhr.setRequestHeader("Authorization", "Bearer " + bearer);	
            xhr.open(httpMethod, requestUrl, true);

            xhr.setRequestHeader('Content-Type', 'application/json');
            xhr.setRequestHeader('Accept', '*/*'); // accept all 

            if(requestbody) xhr.send(requestbody);
            else xhr.send();
        }());
    };

    this.refreshToken = async function(){	
        return new Promise((resolve, reject) => async function(){
            const url = "/generate-jwt";
            var xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function(){
                if (this.readyState !== 4) return;
                try{
                    that.debug("refreshToken response", this.status);
                    //if (this.readyState !== 4) return;
                    if(this.status !== 200){
                        reject(url + " => " + this.status);
                        return;
                    }
                    that.currentJWT = this.responseText;
                    resolve(that.currentJWT);
                }catch(e){
                    reject(e);
                }
            };
            
            xhr.open("GET", url, true);
            xhr.setRequestHeader('Accept', '*/*'); // accept all 
            xhr.send();
            
        }());
    };

    this.setIntervalAndExecute = function(fn, t) {
        fn();
        return(setInterval(fn, t));
    };



    this.enableBackToTop = function (){
        jQuery(document).ready(function(){
            jQuery(window).scroll(function () {
                if (jQuery(this).scrollTop() > 50) {
                    jQuery('#back-to-top').fadeIn();
                } else {
                    jQuery('#back-to-top').fadeOut();
                }
            });
            // Back to top : scroll body to 0px on click
            jQuery('#back-to-top').click(function () {
                jQuery('body,html').animate({
                    scrollTop: 0
                }, 400);
                return false;
            });
        });
    };

    this.enableSidebarCollapse = function (){
        jQuery(document).ready(function () {
                jQuery('#sidebarCollapse').on('click', function () {
                        jQuery('#sidebar').toggleClass('active');
                        jQuery(this).toggleClass('active');
                });
        });
    };

    this.enableIffytip = function (){
        jQuery(document).ready(function () {
            jQuery(document).on('mouseenter', ".iffyTip", function () {
                var $this = jQuery(this);
                if (this.offsetWidth < this.scrollWidth && !$this.attr('title')) {
                    $this.tooltip({
                        title: $this.text(),
                        placement: "top"
                    });
                    $this.tooltip('show');
                }
            });
        });
    };


    this.topHideWaitModal = false;
    this.showWaitModal = function(id) {
        let finalId = id ? id : '#wait-modal';
        that.topHideWaitModal = false;
        jQuery(finalId).on('shown.bs.modal', e => {
            if (that.topHideWaitModal)
                jQuery(finalId).modal('hide');
        });
        jQuery(finalId).modal();
    };

    this.hideWaitModal = function(id) {
        that.topHideWaitModal = true;
        jQuery(id ? id : '#wait-modal').modal('hide');

        //fix chrome & ffox bug with boostrap
        //cf. : https://stackoverflow.com/questions/11519660/twitter-bootstrap-modal-backdrop-doesnt-disappear
        jQuery('body').removeClass('modal-open');
        jQuery('.modal-backdrop').remove();
    };

    this.clearError = function(elemId){
        let elem = document.getElementById(elemId ? elemId : "errorDiv");
        elem.innerHTML = "";
    };

    this.displayError = function(e, elemId){
        let elem = document.getElementById(elemId ? elemId : "errorDiv");
        if(!elem) that.debug("displayError", e, e.stack, elemId);
        elem.innerHTML = "<p class='text-danger'>" + e + "</p><br>";
    };

    this.appendError = function(e, elemId){
        let elem = document.getElementById(elemId ? elemId : "errorDiv");
        if(!elem) that.debug("displayError", e, e.stack, elemId);
        elem.innerHTML += "<p class='text-danger'>" + e + "</p><br>";
    };

    this.escapeHtml = function(unsafe) {
        return unsafe
             .replace(/&/g, "&amp;")
             .replace(/</g, "&lt;")
             .replace(/>/g, "&gt;")
             .replace(/"/g, "&quot;")
             .replace(/'/g, "&#039;");
    };

    this.getActiveElemVal = function(selectName) {
        let comboElem = document.getElementById(selectName);
        if (!comboElem.selectedOptions || comboElem.selectedOptions.length === 0)
            return null;
        return comboElem.selectedOptions[0].value;
    };


    this.getQueryParam = function(p, defaultValue){
        if(!p) return null;
        const queryString = window.location.search;
        const urlParams = new URLSearchParams(queryString);
        let result = urlParams.get(p);
        if(defaultValue === null) return result;
        if(result === null) return defaultValue;
        return result;
    };


    this.createChoice = function(c, v, id, selected){
        var res = document.createElement("option");
        res.innerHTML = c;
        if(id !== null) res.setAttribute("id", id);
        if(!v && !c)
            res.setAttribute("value", "");
        else
            res.setAttribute("value", !v ? c : v);
        if(selected)
            res.setAttribute("selected", true);
        return res;
    };

    this.isNull = function(a){
        return !a || "undefined" === a;
    };
};