/*
 * Ext JS Library 1.1
 * Copyright(c) 2006-2007, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://www.extjs.com/license
 */


Ext.state.Provider=function(){this.addEvents({"statechange":true});this.state={};Ext.state.Provider.superclass.constructor.call(this);};Ext.extend(Ext.state.Provider,Ext.util.Observable,{get:function(_1,_2){return typeof this.state[_1]=="undefined"?_2:this.state[_1];},clear:function(_3){delete this.state[_3];this.fireEvent("statechange",this,_3,null);},set:function(_4,_5){this.state[_4]=_5;this.fireEvent("statechange",this,_4,_5);},decodeValue:function(_6){var re=/^(a|n|d|b|s|o)\:(.*)$/;var _8=re.exec(unescape(_6));if(!_8||!_8[1]){return;}var _9=_8[1];var v=_8[2];switch(_9){case"n":return parseFloat(v);case"d":return new Date(Date.parse(v));case"b":return(v=="1");case"a":var _b=[];var _c=v.split("^");for(var i=0,_e=_c.length;i<_e;i++){_b.push(this.decodeValue(_c[i]));}return _b;case"o":var _b={};var _c=v.split("^");for(var i=0,_e=_c.length;i<_e;i++){var kv=_c[i].split("=");_b[kv[0]]=this.decodeValue(kv[1]);}return _b;default:return v;}},encodeValue:function(v){var enc;if(typeof v=="number"){enc="n:"+v;}else{if(typeof v=="boolean"){enc="b:"+(v?"1":"0");}else{if(v instanceof Date){enc="d:"+v.toGMTString();}else{if(v instanceof Array){var _12="";for(var i=0,len=v.length;i<len;i++){_12+=this.encodeValue(v[i]);if(i!=len-1){_12+="^";}}enc="a:"+_12;}else{if(typeof v=="object"){var _12="";for(var key in v){if(typeof v[key]!="function"){_12+=key+"="+this.encodeValue(v[key])+"^";}}enc="o:"+_12.substring(0,_12.length-1);}else{enc="s:"+v;}}}}}return escape(enc);}});Ext.state.Manager=function(){var _16=new Ext.state.Provider();return{setProvider:function(_17){_16=_17;},get:function(key,_19){return _16.get(key,_19);},set:function(key,_1b){_16.set(key,_1b);},clear:function(key){_16.clear(key);},getProvider:function(){return _16;}};}();Ext.state.CookieProvider=function(_1d){Ext.state.CookieProvider.superclass.constructor.call(this);this.path="/";this.expires=new Date(new Date().getTime()+(1000*60*60*24*7));this.domain=null;this.secure=false;Ext.apply(this,_1d);this.state=this.readCookies();};Ext.extend(Ext.state.CookieProvider,Ext.state.Provider,{set:function(_1e,_1f){if(typeof _1f=="undefined"||_1f===null){this.clear(_1e);return;}this.setCookie(_1e,_1f);Ext.state.CookieProvider.superclass.set.call(this,_1e,_1f);},clear:function(_20){this.clearCookie(_20);Ext.state.CookieProvider.superclass.clear.call(this,_20);},readCookies:function(){var _21={};var c=document.cookie+";";var re=/\s?(.*?)=(.*?);/g;var _24;while((_24=re.exec(c))!=null){var _25=_24[1];var _26=_24[2];if(_25&&_25.substring(0,3)=="ys-"){_21[_25.substr(3)]=this.decodeValue(_26);}}return _21;},setCookie:function(_27,_28){document.cookie="ys-"+_27+"="+this.encodeValue(_28)+((this.expires==null)?"":("; expires="+this.expires.toGMTString()))+((this.path==null)?"":("; path="+this.path))+((this.domain==null)?"":("; domain="+this.domain))+((this.secure==true)?"; secure":"");},clearCookie:function(_29){document.cookie="ys-"+_29+"=null; expires=Thu, 01-Jan-70 00:00:01 GMT"+((this.path==null)?"":("; path="+this.path))+((this.domain==null)?"":("; domain="+this.domain))+((this.secure==true)?"; secure":"");}});