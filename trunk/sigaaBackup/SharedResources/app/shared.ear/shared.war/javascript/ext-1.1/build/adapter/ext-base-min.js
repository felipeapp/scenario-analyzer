/*
 * Ext JS Library 1.1
 * Copyright(c) 2006-2007, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://www.extjs.com/license
 */


(function(){var _1;Ext.lib.Dom={getViewWidth:function(_2){return _2?this.getDocumentWidth():this.getViewportWidth();},getViewHeight:function(_3){return _3?this.getDocumentHeight():this.getViewportHeight();},getDocumentHeight:function(){var _4=(document.compatMode!="CSS1Compat")?document.body.scrollHeight:document.documentElement.scrollHeight;return Math.max(_4,this.getViewportHeight());},getDocumentWidth:function(){var _5=(document.compatMode!="CSS1Compat")?document.body.scrollWidth:document.documentElement.scrollWidth;return Math.max(_5,this.getViewportWidth());},getViewportHeight:function(){var _6=self.innerHeight;var _7=document.compatMode;if((_7||Ext.isIE)&&!Ext.isOpera){_6=(_7=="CSS1Compat")?document.documentElement.clientHeight:document.body.clientHeight;}return _6;},getViewportWidth:function(){var _8=self.innerWidth;var _9=document.compatMode;if(_9||Ext.isIE){_8=(_9=="CSS1Compat")?document.documentElement.clientWidth:document.body.clientWidth;}return _8;},isAncestor:function(p,c){p=Ext.getDom(p);c=Ext.getDom(c);if(!p||!c){return false;}if(p.contains&&!Ext.isSafari){return p.contains(c);}else{if(p.compareDocumentPosition){return!!(p.compareDocumentPosition(c)&16);}else{var _c=c.parentNode;while(_c){if(_c==p){return true;}else{if(!_c.tagName||_c.tagName.toUpperCase()=="HTML"){return false;}}_c=_c.parentNode;}return false;}}},getRegion:function(el){return Ext.lib.Region.getRegion(el);},getY:function(el){return this.getXY(el)[1];},getX:function(el){return this.getXY(el)[0];},getXY:function(el){var p,pe,b,_14,bd=document.body;el=Ext.getDom(el);if(el.getBoundingClientRect){b=el.getBoundingClientRect();_14=fly(document).getScroll();return[b.left+_14.left,b.top+_14.top];}var x=0,y=0;p=el;var _18=fly(el).getStyle("position")=="absolute";while(p){x+=p.offsetLeft;y+=p.offsetTop;if(!_18&&fly(p).getStyle("position")=="absolute"){_18=true;}if(Ext.isGecko){pe=fly(p);var bt=parseInt(pe.getStyle("borderTopWidth"),10)||0;var bl=parseInt(pe.getStyle("borderLeftWidth"),10)||0;x+=bl;y+=bt;if(p!=el&&pe.getStyle("overflow")!="visible"){x+=bl;y+=bt;}}p=p.offsetParent;}if(Ext.isSafari&&_18){x-=bd.offsetLeft;y-=bd.offsetTop;}if(Ext.isGecko&&!_18){var dbd=fly(bd);x+=parseInt(dbd.getStyle("borderLeftWidth"),10)||0;y+=parseInt(dbd.getStyle("borderTopWidth"),10)||0;}p=el.parentNode;while(p&&p!=bd){if(!(Ext.isOpera&&p.tagName!="TR"&&fly(p).getStyle("display")!="inline")){x-=p.scrollLeft;y-=p.scrollTop;}p=p.parentNode;}return[x,y];},setXY:function(el,xy){el=Ext.fly(el,"_setXY");el.position();var pts=el.translatePoints(xy);if(xy[0]!==false){el.dom.style.left=pts.left+"px";}if(xy[1]!==false){el.dom.style.top=pts.top+"px";}},setX:function(el,x){this.setXY(el,[x,false]);},setY:function(el,y){this.setXY(el,[false,y]);}};Ext.lib.Event=function(){var _23=false;var _24=[];var _25=[];var _26=0;var _27=[];var _28=0;var _29=null;return{POLL_RETRYS:200,POLL_INTERVAL:20,EL:0,TYPE:1,FN:2,WFN:3,OBJ:3,ADJ_SCOPE:4,_interval:null,startInterval:function(){if(!this._interval){var _2a=this;var _2b=function(){_2a._tryPreloadAttach();};this._interval=setInterval(_2b,this.POLL_INTERVAL);}},onAvailable:function(_2c,_2d,_2e,_2f){_27.push({id:_2c,fn:_2d,obj:_2e,override:_2f,checkReady:false});_26=this.POLL_RETRYS;this.startInterval();},addListener:function(el,_31,fn){el=Ext.getDom(el);if(!el||!fn){return false;}if("unload"==_31){_25[_25.length]=[el,_31,fn];return true;}var _33=function(e){return fn(Ext.lib.Event.getEvent(e));};var li=[el,_31,fn,_33];var _36=_24.length;_24[_36]=li;this.doAdd(el,_31,_33,false);return true;},removeListener:function(el,_38,fn){var i,len;el=Ext.getDom(el);if(!fn){return this.purgeElement(el,false,_38);}if("unload"==_38){for(i=0,len=_25.length;i<len;i++){var li=_25[i];if(li&&li[0]==el&&li[1]==_38&&li[2]==fn){_25.splice(i,1);return true;}}return false;}var _3d=null;var _3e=arguments[3];if("undefined"==typeof _3e){_3e=this._getCacheIndex(el,_38,fn);}if(_3e>=0){_3d=_24[_3e];}if(!el||!_3d){return false;}this.doRemove(el,_38,_3d[this.WFN],false);delete _24[_3e][this.WFN];delete _24[_3e][this.FN];_24.splice(_3e,1);return true;},getTarget:function(ev,_40){ev=ev.browserEvent||ev;var t=ev.target||ev.srcElement;return this.resolveTextNode(t);},resolveTextNode:function(_42){if(Ext.isSafari&&_42&&3==_42.nodeType){return _42.parentNode;}else{return _42;}},getPageX:function(ev){ev=ev.browserEvent||ev;var x=ev.pageX;if(!x&&0!==x){x=ev.clientX||0;if(Ext.isIE){x+=this.getScroll()[1];}}return x;},getPageY:function(ev){ev=ev.browserEvent||ev;var y=ev.pageY;if(!y&&0!==y){y=ev.clientY||0;if(Ext.isIE){y+=this.getScroll()[0];}}return y;},getXY:function(ev){ev=ev.browserEvent||ev;return[this.getPageX(ev),this.getPageY(ev)];},getRelatedTarget:function(ev){ev=ev.browserEvent||ev;var t=ev.relatedTarget;if(!t){if(ev.type=="mouseout"){t=ev.toElement;}else{if(ev.type=="mouseover"){t=ev.fromElement;}}}return this.resolveTextNode(t);},getTime:function(ev){ev=ev.browserEvent||ev;if(!ev.time){var t=new Date().getTime();try{ev.time=t;}catch(ex){this.lastError=ex;return t;}}return ev.time;},stopEvent:function(ev){this.stopPropagation(ev);this.preventDefault(ev);},stopPropagation:function(ev){ev=ev.browserEvent||ev;if(ev.stopPropagation){ev.stopPropagation();}else{ev.cancelBubble=true;}},preventDefault:function(ev){ev=ev.browserEvent||ev;if(ev.preventDefault){ev.preventDefault();}else{ev.returnValue=false;}},getEvent:function(e){var ev=e||window.event;if(!ev){var c=this.getEvent.caller;while(c){ev=c.arguments[0];if(ev&&Event==ev.constructor){break;}c=c.caller;}}return ev;},getCharCode:function(ev){ev=ev.browserEvent||ev;return ev.charCode||ev.keyCode||0;},_getCacheIndex:function(el,_54,fn){for(var i=0,len=_24.length;i<len;++i){var li=_24[i];if(li&&li[this.FN]==fn&&li[this.EL]==el&&li[this.TYPE]==_54){return i;}}return-1;},elCache:{},getEl:function(id){return document.getElementById(id);},clearCache:function(){},_load:function(e){_23=true;var EU=Ext.lib.Event;if(Ext.isIE){EU.doRemove(window,"load",EU._load);}},_tryPreloadAttach:function(){if(this.locked){return false;}this.locked=true;var _5c=!_23;if(!_5c){_5c=(_26>0);}var _5d=[];for(var i=0,len=_27.length;i<len;++i){var _60=_27[i];if(_60){var el=this.getEl(_60.id);if(el){if(!_60.checkReady||_23||el.nextSibling||(document&&document.body)){var _62=el;if(_60.override){if(_60.override===true){_62=_60.obj;}else{_62=_60.override;}}_60.fn.call(_62,_60.obj);_27[i]=null;}}else{_5d.push(_60);}}}_26=(_5d.length===0)?0:_26-1;if(_5c){this.startInterval();}else{clearInterval(this._interval);this._interval=null;}this.locked=false;return true;},purgeElement:function(el,_64,_65){var _66=this.getListeners(el,_65);if(_66){for(var i=0,len=_66.length;i<len;++i){var l=_66[i];this.removeListener(el,l.type,l.fn);}}if(_64&&el&&el.childNodes){for(i=0,len=el.childNodes.length;i<len;++i){this.purgeElement(el.childNodes[i],_64,_65);}}},getListeners:function(el,_6b){var _6c=[],_6d;if(!_6b){_6d=[_24,_25];}else{if(_6b=="unload"){_6d=[_25];}else{_6d=[_24];}}for(var j=0;j<_6d.length;++j){var _6f=_6d[j];if(_6f&&_6f.length>0){for(var i=0,len=_6f.length;i<len;++i){var l=_6f[i];if(l&&l[this.EL]===el&&(!_6b||_6b===l[this.TYPE])){_6c.push({type:l[this.TYPE],fn:l[this.FN],obj:l[this.OBJ],adjust:l[this.ADJ_SCOPE],index:i});}}}}return(_6c.length)?_6c:null;},_unload:function(e){var EU=Ext.lib.Event,i,j,l,len,_79;for(i=0,len=_25.length;i<len;++i){l=_25[i];if(l){var _7a=window;if(l[EU.ADJ_SCOPE]){if(l[EU.ADJ_SCOPE]===true){_7a=l[EU.OBJ];}else{_7a=l[EU.ADJ_SCOPE];}}l[EU.FN].call(_7a,EU.getEvent(e),l[EU.OBJ]);_25[i]=null;l=null;_7a=null;}}_25=null;if(_24&&_24.length>0){j=_24.length;while(j){_79=j-1;l=_24[_79];if(l){EU.removeListener(l[EU.EL],l[EU.TYPE],l[EU.FN],_79);}j=j-1;}l=null;EU.clearCache();}EU.doRemove(window,"unload",EU._unload);},getScroll:function(){var dd=document.documentElement,db=document.body;if(dd&&(dd.scrollTop||dd.scrollLeft)){return[dd.scrollTop,dd.scrollLeft];}else{if(db){return[db.scrollTop,db.scrollLeft];}else{return[0,0];}}},doAdd:function(){if(window.addEventListener){return function(el,_7e,fn,_80){el.addEventListener(_7e,fn,(_80));};}else{if(window.attachEvent){return function(el,_82,fn,_84){el.attachEvent("on"+_82,fn);};}else{return function(){};}}}(),doRemove:function(){if(window.removeEventListener){return function(el,_86,fn,_88){el.removeEventListener(_86,fn,(_88));};}else{if(window.detachEvent){return function(el,_8a,fn){el.detachEvent("on"+_8a,fn);};}else{return function(){};}}}()};}();var E=Ext.lib.Event;E.on=E.addListener;E.un=E.removeListener;if(document&&document.body){E._load();}else{E.doAdd(window,"load",E._load);}E.doAdd(window,"unload",E._unload);E._tryPreloadAttach();Ext.lib.Ajax={request:function(_8d,uri,cb,_90,_91){if(_91){var hs=_91.headers;if(hs){for(var h in hs){if(hs.hasOwnProperty(h)){this.initHeader(h,hs[h],false);}}}if(_91.xmlData){this.initHeader("Content-Type","text/xml",false);_8d="POST";_90=_91.xmlData;}}return this.asyncRequest(_8d,uri,cb,_90);},serializeForm:function(_94){if(typeof _94=="string"){_94=(document.getElementById(_94)||document.forms[_94]);}var el,_96,val,_98,_99="",_9a=false;for(var i=0;i<_94.elements.length;i++){el=_94.elements[i];_98=_94.elements[i].disabled;_96=_94.elements[i].name;val=_94.elements[i].value;if(!_98&&_96){switch(el.type){case"select-one":case"select-multiple":for(var j=0;j<el.options.length;j++){if(el.options[j].selected){if(Ext.isIE){_99+=encodeURIComponent(_96)+"="+encodeURIComponent(el.options[j].attributes["value"].specified?el.options[j].value:el.options[j].text)+"&";}else{_99+=encodeURIComponent(_96)+"="+encodeURIComponent(el.options[j].hasAttribute("value")?el.options[j].value:el.options[j].text)+"&";}}}break;case"radio":case"checkbox":if(el.checked){_99+=encodeURIComponent(_96)+"="+encodeURIComponent(val)+"&";}break;case"file":case undefined:case"reset":case"button":break;case"submit":if(_9a==false){_99+=encodeURIComponent(_96)+"="+encodeURIComponent(val)+"&";_9a=true;}break;default:_99+=encodeURIComponent(_96)+"="+encodeURIComponent(val)+"&";break;}}}_99=_99.substr(0,_99.length-1);return _99;},headers:{},hasHeaders:false,useDefaultHeader:true,defaultPostHeader:"application/x-www-form-urlencoded",useDefaultXhrHeader:true,defaultXhrHeader:"XMLHttpRequest",hasDefaultHeaders:true,defaultHeaders:{},poll:{},timeout:{},pollInterval:50,transactionId:0,setProgId:function(id){this.activeX.unshift(id);},setDefaultPostHeader:function(b){this.useDefaultHeader=b;},setDefaultXhrHeader:function(b){this.useDefaultXhrHeader=b;},setPollingInterval:function(i){if(typeof i=="number"&&isFinite(i)){this.pollInterval=i;}},createXhrObject:function(_a1){var obj,_a3;try{_a3=new XMLHttpRequest();obj={conn:_a3,tId:_a1};}catch(e){for(var i=0;i<this.activeX.length;++i){try{_a3=new ActiveXObject(this.activeX[i]);obj={conn:_a3,tId:_a1};break;}catch(e){}}}finally{return obj;}},getConnectionObject:function(){var o;var tId=this.transactionId;try{o=this.createXhrObject(tId);if(o){this.transactionId++;}}catch(e){}finally{return o;}},asyncRequest:function(_a7,uri,_a9,_aa){var o=this.getConnectionObject();if(!o){return null;}else{o.conn.open(_a7,uri,true);if(this.useDefaultXhrHeader){if(!this.defaultHeaders["X-Requested-With"]){this.initHeader("X-Requested-With",this.defaultXhrHeader,true);}}if(_aa&&this.useDefaultHeader){this.initHeader("Content-Type",this.defaultPostHeader);}if(this.hasDefaultHeaders||this.hasHeaders){this.setHeader(o);}this.handleReadyState(o,_a9);o.conn.send(_aa||null);return o;}},handleReadyState:function(o,_ad){var _ae=this;if(_ad&&_ad.timeout){this.timeout[o.tId]=window.setTimeout(function(){_ae.abort(o,_ad,true);},_ad.timeout);}this.poll[o.tId]=window.setInterval(function(){if(o.conn&&o.conn.readyState==4){window.clearInterval(_ae.poll[o.tId]);delete _ae.poll[o.tId];if(_ad&&_ad.timeout){window.clearTimeout(_ae.timeout[o.tId]);delete _ae.timeout[o.tId];}_ae.handleTransactionResponse(o,_ad);}},this.pollInterval);},handleTransactionResponse:function(o,_b0,_b1){if(!_b0){this.releaseObject(o);return;}var _b2,_b3;try{if(o.conn.status!==undefined&&o.conn.status!=0){_b2=o.conn.status;}else{_b2=13030;}}catch(e){_b2=13030;}if(_b2>=200&&_b2<300){_b3=this.createResponseObject(o,_b0.argument);if(_b0.success){if(!_b0.scope){_b0.success(_b3);}else{_b0.success.apply(_b0.scope,[_b3]);}}}else{switch(_b2){case 12002:case 12029:case 12030:case 12031:case 12152:case 13030:_b3=this.createExceptionObject(o.tId,_b0.argument,(_b1?_b1:false));if(_b0.failure){if(!_b0.scope){_b0.failure(_b3);}else{_b0.failure.apply(_b0.scope,[_b3]);}}break;default:_b3=this.createResponseObject(o,_b0.argument);if(_b0.failure){if(!_b0.scope){_b0.failure(_b3);}else{_b0.failure.apply(_b0.scope,[_b3]);}}}}this.releaseObject(o);_b3=null;},createResponseObject:function(o,_b5){var obj={};var _b7={};try{var _b8=o.conn.getAllResponseHeaders();var _b9=_b8.split("\n");for(var i=0;i<_b9.length;i++){var _bb=_b9[i].indexOf(":");if(_bb!=-1){_b7[_b9[i].substring(0,_bb)]=_b9[i].substring(_bb+2);}}}catch(e){}obj.tId=o.tId;obj.status=o.conn.status;obj.statusText=o.conn.statusText;obj.getResponseHeader=_b7;obj.getAllResponseHeaders=_b8;obj.responseText=o.conn.responseText;obj.responseXML=o.conn.responseXML;if(typeof _b5!==undefined){obj.argument=_b5;}return obj;},createExceptionObject:function(tId,_bd,_be){var _bf=0;var _c0="communication failure";var _c1=-1;var _c2="transaction aborted";var obj={};obj.tId=tId;if(_be){obj.status=_c1;obj.statusText=_c2;}else{obj.status=_bf;obj.statusText=_c0;}if(_bd){obj.argument=_bd;}return obj;},initHeader:function(_c4,_c5,_c6){var _c7=(_c6)?this.defaultHeaders:this.headers;if(_c7[_c4]===undefined){_c7[_c4]=_c5;}else{_c7[_c4]=_c5+","+_c7[_c4];}if(_c6){this.hasDefaultHeaders=true;}else{this.hasHeaders=true;}},setHeader:function(o){if(this.hasDefaultHeaders){for(var _c9 in this.defaultHeaders){if(this.defaultHeaders.hasOwnProperty(_c9)){o.conn.setRequestHeader(_c9,this.defaultHeaders[_c9]);}}}if(this.hasHeaders){for(var _c9 in this.headers){if(this.headers.hasOwnProperty(_c9)){o.conn.setRequestHeader(_c9,this.headers[_c9]);}}this.headers={};this.hasHeaders=false;}},resetDefaultHeaders:function(){delete this.defaultHeaders;this.defaultHeaders={};this.hasDefaultHeaders=false;},abort:function(o,_cb,_cc){if(this.isCallInProgress(o)){o.conn.abort();window.clearInterval(this.poll[o.tId]);delete this.poll[o.tId];if(_cc){delete this.timeout[o.tId];}this.handleTransactionResponse(o,_cb,true);return true;}else{return false;}},isCallInProgress:function(o){if(o&&o.conn){return o.conn.readyState!=4&&o.conn.readyState!=0;}else{return false;}},releaseObject:function(o){o.conn=null;o=null;},activeX:["MSXML2.XMLHTTP.3.0","MSXML2.XMLHTTP","Microsoft.XMLHTTP"]};Ext.lib.Region=function(t,r,b,l){this.top=t;this[1]=t;this.right=r;this.bottom=b;this.left=l;this[0]=l;};Ext.lib.Region.prototype={contains:function(_d3){return(_d3.left>=this.left&&_d3.right<=this.right&&_d3.top>=this.top&&_d3.bottom<=this.bottom);},getArea:function(){return((this.bottom-this.top)*(this.right-this.left));},intersect:function(_d4){var t=Math.max(this.top,_d4.top);var r=Math.min(this.right,_d4.right);var b=Math.min(this.bottom,_d4.bottom);var l=Math.max(this.left,_d4.left);if(b>=t&&r>=l){return new Ext.lib.Region(t,r,b,l);}else{return null;}},union:function(_d9){var t=Math.min(this.top,_d9.top);var r=Math.max(this.right,_d9.right);var b=Math.max(this.bottom,_d9.bottom);var l=Math.min(this.left,_d9.left);return new Ext.lib.Region(t,r,b,l);},adjust:function(t,l,b,r){this.top+=t;this.left+=l;this.right+=r;this.bottom+=b;return this;}};Ext.lib.Region.getRegion=function(el){var p=Ext.lib.Dom.getXY(el);var t=p[1];var r=p[0]+el.offsetWidth;var b=p[1]+el.offsetHeight;var l=p[0];return new Ext.lib.Region(t,r,b,l);};Ext.lib.Point=function(x,y){if(x instanceof Array){y=x[1];x=x[0];}this.x=this.right=this.left=this[0]=x;this.y=this.top=this.bottom=this[1]=y;};Ext.lib.Point.prototype=new Ext.lib.Region();Ext.lib.Anim={scroll:function(el,_eb,_ec,_ed,cb,_ef){this.run(el,_eb,_ec,_ed,cb,_ef,Ext.lib.Scroll);},motion:function(el,_f1,_f2,_f3,cb,_f5){this.run(el,_f1,_f2,_f3,cb,_f5,Ext.lib.Motion);},color:function(el,_f7,_f8,_f9,cb,_fb){this.run(el,_f7,_f8,_f9,cb,_fb,Ext.lib.ColorAnim);},run:function(el,_fd,_fe,_ff,cb,_101,type){type=type||Ext.lib.AnimBase;if(typeof _ff=="string"){_ff=Ext.lib.Easing[_ff];}var anim=new type(el,_fd,_fe,_ff);anim.animateX(function(){Ext.callback(cb,_101);});return anim;}};function fly(el){if(!_1){_1=new Ext.Element.Flyweight();}_1.dom=el;return _1;}if(Ext.isIE){function fnCleanUp(){var p=Function.prototype;delete p.createSequence;delete p.defer;delete p.createDelegate;delete p.createCallback;delete p.createInterceptor;window.detachEvent("onunload",fnCleanUp);}window.attachEvent("onunload",fnCleanUp);}Ext.lib.AnimBase=function(el,_107,_108,_109){if(el){this.init(el,_107,_108,_109);}};Ext.lib.AnimBase.prototype={toString:function(){var el=this.getEl();var id=el.id||el.tagName;return("Anim "+id);},patterns:{noNegatives:/width|height|opacity|padding/i,offsetAttribute:/^((width|height)|(top|left))$/,defaultUnit:/width|height|top$|bottom$|left$|right$/i,offsetUnit:/\d+(em|%|en|ex|pt|in|cm|mm|pc)$/i},doMethod:function(attr,_10d,end){return this.method(this.currentFrame,_10d,end-_10d,this.totalFrames);},setAttribute:function(attr,val,unit){if(this.patterns.noNegatives.test(attr)){val=(val>0)?val:0;}Ext.fly(this.getEl(),"_anim").setStyle(attr,val+unit);},getAttribute:function(attr){var el=this.getEl();var val=fly(el).getStyle(attr);if(val!=="auto"&&!this.patterns.offsetUnit.test(val)){return parseFloat(val);}var a=this.patterns.offsetAttribute.exec(attr)||[];var pos=!!(a[3]);var box=!!(a[2]);if(box||(fly(el).getStyle("position")=="absolute"&&pos)){val=el["offset"+a[0].charAt(0).toUpperCase()+a[0].substr(1)];}else{val=0;}return val;},getDefaultUnit:function(attr){if(this.patterns.defaultUnit.test(attr)){return"px";}return"";},animateX:function(_119,_11a){var f=function(){this.onComplete.removeListener(f);if(typeof _119=="function"){_119.call(_11a||this,this);}};this.onComplete.addListener(f,this);this.animate();},setRuntimeAttribute:function(attr){var _11d;var end;var _11f=this.attributes;this.runtimeAttributes[attr]={};var _120=function(prop){return(typeof prop!=="undefined");};if(!_120(_11f[attr]["to"])&&!_120(_11f[attr]["by"])){return false;}_11d=(_120(_11f[attr]["from"]))?_11f[attr]["from"]:this.getAttribute(attr);if(_120(_11f[attr]["to"])){end=_11f[attr]["to"];}else{if(_120(_11f[attr]["by"])){if(_11d.constructor==Array){end=[];for(var i=0,len=_11d.length;i<len;++i){end[i]=_11d[i]+_11f[attr]["by"][i];}}else{end=_11d+_11f[attr]["by"];}}}this.runtimeAttributes[attr].start=_11d;this.runtimeAttributes[attr].end=end;this.runtimeAttributes[attr].unit=(_120(_11f[attr].unit))?_11f[attr]["unit"]:this.getDefaultUnit(attr);},init:function(el,_125,_126,_127){var _128=false;var _129=null;var _12a=0;el=Ext.getDom(el);this.attributes=_125||{};this.duration=_126||1;this.method=_127||Ext.lib.Easing.easeNone;this.useSeconds=true;this.currentFrame=0;this.totalFrames=Ext.lib.AnimMgr.fps;this.getEl=function(){return el;};this.isAnimated=function(){return _128;};this.getStartTime=function(){return _129;};this.runtimeAttributes={};this.animate=function(){if(this.isAnimated()){return false;}this.currentFrame=0;this.totalFrames=(this.useSeconds)?Math.ceil(Ext.lib.AnimMgr.fps*this.duration):this.duration;Ext.lib.AnimMgr.registerElement(this);};this.stop=function(_12b){if(_12b){this.currentFrame=this.totalFrames;this._onTween.fire();}Ext.lib.AnimMgr.stop(this);};var _12c=function(){this.onStart.fire();this.runtimeAttributes={};for(var attr in this.attributes){this.setRuntimeAttribute(attr);}_128=true;_12a=0;_129=new Date();};var _12e=function(){var data={duration:new Date()-this.getStartTime(),currentFrame:this.currentFrame};data.toString=function(){return("duration: "+data.duration+", currentFrame: "+data.currentFrame);};this.onTween.fire(data);var _130=this.runtimeAttributes;for(var attr in _130){this.setAttribute(attr,this.doMethod(attr,_130[attr].start,_130[attr].end),_130[attr].unit);}_12a+=1;};var _132=function(){var _133=(new Date()-_129)/1000;var data={duration:_133,frames:_12a,fps:_12a/_133};data.toString=function(){return("duration: "+data.duration+", frames: "+data.frames+", fps: "+data.fps);};_128=false;_12a=0;this.onComplete.fire(data);};this._onStart=new Ext.util.Event(this);this.onStart=new Ext.util.Event(this);this.onTween=new Ext.util.Event(this);this._onTween=new Ext.util.Event(this);this.onComplete=new Ext.util.Event(this);this._onComplete=new Ext.util.Event(this);this._onStart.addListener(_12c);this._onTween.addListener(_12e);this._onComplete.addListener(_132);}};Ext.lib.AnimMgr=new function(){var _135=null;var _136=[];var _137=0;this.fps=1000;this.delay=1;this.registerElement=function(_138){_136[_136.length]=_138;_137+=1;_138._onStart.fire();this.start();};this.unRegister=function(_139,_13a){_139._onComplete.fire();_13a=_13a||_13b(_139);if(_13a!=-1){_136.splice(_13a,1);}_137-=1;if(_137<=0){this.stop();}};this.start=function(){if(_135===null){_135=setInterval(this.run,this.delay);}};this.stop=function(_13c){if(!_13c){clearInterval(_135);for(var i=0,len=_136.length;i<len;++i){if(_136[0].isAnimated()){this.unRegister(_136[0],0);}}_136=[];_135=null;_137=0;}else{this.unRegister(_13c);}};this.run=function(){for(var i=0,len=_136.length;i<len;++i){var _141=_136[i];if(!_141||!_141.isAnimated()){continue;}if(_141.currentFrame<_141.totalFrames||_141.totalFrames===null){_141.currentFrame+=1;if(_141.useSeconds){_142(_141);}_141._onTween.fire();}else{Ext.lib.AnimMgr.stop(_141,i);}}};var _13b=function(anim){for(var i=0,len=_136.length;i<len;++i){if(_136[i]==anim){return i;}}return-1;};var _142=function(_146){var _147=_146.totalFrames;var _148=_146.currentFrame;var _149=(_146.currentFrame*_146.duration*1000/_146.totalFrames);var _14a=(new Date()-_146.getStartTime());var _14b=0;if(_14a<_146.duration*1000){_14b=Math.round((_14a/_149-1)*_146.currentFrame);}else{_14b=_147-(_148+1);}if(_14b>0&&isFinite(_14b)){if(_146.currentFrame+_14b>=_147){_14b=_147-(_148+1);}_146.currentFrame+=_14b;}};};Ext.lib.Bezier=new function(){this.getPosition=function(_14c,t){var n=_14c.length;var tmp=[];for(var i=0;i<n;++i){tmp[i]=[_14c[i][0],_14c[i][1]];}for(var j=1;j<n;++j){for(i=0;i<n-j;++i){tmp[i][0]=(1-t)*tmp[i][0]+t*tmp[parseInt(i+1,10)][0];tmp[i][1]=(1-t)*tmp[i][1]+t*tmp[parseInt(i+1,10)][1];}}return[tmp[0][0],tmp[0][1]];};};(function(){Ext.lib.ColorAnim=function(el,_153,_154,_155){Ext.lib.ColorAnim.superclass.constructor.call(this,el,_153,_154,_155);};Ext.extend(Ext.lib.ColorAnim,Ext.lib.AnimBase);var Y=Ext.lib;var _157=Y.ColorAnim.superclass;var _158=Y.ColorAnim.prototype;_158.toString=function(){var el=this.getEl();var id=el.id||el.tagName;return("ColorAnim "+id);};_158.patterns.color=/color$/i;_158.patterns.rgb=/^rgb\(([0-9]+)\s*,\s*([0-9]+)\s*,\s*([0-9]+)\)$/i;_158.patterns.hex=/^#?([0-9A-F]{2})([0-9A-F]{2})([0-9A-F]{2})$/i;_158.patterns.hex3=/^#?([0-9A-F]{1})([0-9A-F]{1})([0-9A-F]{1})$/i;_158.patterns.transparent=/^transparent|rgba\(0, 0, 0, 0\)$/;_158.parseColor=function(s){if(s.length==3){return s;}var c=this.patterns.hex.exec(s);if(c&&c.length==4){return[parseInt(c[1],16),parseInt(c[2],16),parseInt(c[3],16)];}c=this.patterns.rgb.exec(s);if(c&&c.length==4){return[parseInt(c[1],10),parseInt(c[2],10),parseInt(c[3],10)];}c=this.patterns.hex3.exec(s);if(c&&c.length==4){return[parseInt(c[1]+c[1],16),parseInt(c[2]+c[2],16),parseInt(c[3]+c[3],16)];}return null;};_158.getAttribute=function(attr){var el=this.getEl();if(this.patterns.color.test(attr)){var val=fly(el).getStyle(attr);if(this.patterns.transparent.test(val)){var _160=el.parentNode;val=fly(_160).getStyle(attr);while(_160&&this.patterns.transparent.test(val)){_160=_160.parentNode;val=fly(_160).getStyle(attr);if(_160.tagName.toUpperCase()=="HTML"){val="#fff";}}}}else{val=_157.getAttribute.call(this,attr);}return val;};_158.doMethod=function(attr,_162,end){var val;if(this.patterns.color.test(attr)){val=[];for(var i=0,len=_162.length;i<len;++i){val[i]=_157.doMethod.call(this,attr,_162[i],end[i]);}val="rgb("+Math.floor(val[0])+","+Math.floor(val[1])+","+Math.floor(val[2])+")";}else{val=_157.doMethod.call(this,attr,_162,end);}return val;};_158.setRuntimeAttribute=function(attr){_157.setRuntimeAttribute.call(this,attr);if(this.patterns.color.test(attr)){var _168=this.attributes;var _169=this.parseColor(this.runtimeAttributes[attr].start);var end=this.parseColor(this.runtimeAttributes[attr].end);if(typeof _168[attr]["to"]==="undefined"&&typeof _168[attr]["by"]!=="undefined"){end=this.parseColor(_168[attr].by);for(var i=0,len=_169.length;i<len;++i){end[i]=_169[i]+end[i];}}this.runtimeAttributes[attr].start=_169;this.runtimeAttributes[attr].end=end;}};})();Ext.lib.Easing={easeNone:function(t,b,c,d){return c*t/d+b;},easeIn:function(t,b,c,d){return c*(t/=d)*t+b;},easeOut:function(t,b,c,d){return-c*(t/=d)*(t-2)+b;},easeBoth:function(t,b,c,d){if((t/=d/2)<1){return c/2*t*t+b;}return-c/2*((--t)*(t-2)-1)+b;},easeInStrong:function(t,b,c,d){return c*(t/=d)*t*t*t+b;},easeOutStrong:function(t,b,c,d){return-c*((t=t/d-1)*t*t*t-1)+b;},easeBothStrong:function(t,b,c,d){if((t/=d/2)<1){return c/2*t*t*t*t+b;}return-c/2*((t-=2)*t*t*t-2)+b;},elasticIn:function(t,b,c,d,a,p){if(t==0){return b;}if((t/=d)==1){return b+c;}if(!p){p=d*0.3;}if(!a||a<Math.abs(c)){a=c;var s=p/4;}else{var s=p/(2*Math.PI)*Math.asin(c/a);}return-(a*Math.pow(2,10*(t-=1))*Math.sin((t*d-s)*(2*Math.PI)/p))+b;},elasticOut:function(t,b,c,d,a,p){if(t==0){return b;}if((t/=d)==1){return b+c;}if(!p){p=d*0.3;}if(!a||a<Math.abs(c)){a=c;var s=p/4;}else{var s=p/(2*Math.PI)*Math.asin(c/a);}return a*Math.pow(2,-10*t)*Math.sin((t*d-s)*(2*Math.PI)/p)+c+b;},elasticBoth:function(t,b,c,d,a,p){if(t==0){return b;}if((t/=d/2)==2){return b+c;}if(!p){p=d*(0.3*1.5);}if(!a||a<Math.abs(c)){a=c;var s=p/4;}else{var s=p/(2*Math.PI)*Math.asin(c/a);}if(t<1){return-0.5*(a*Math.pow(2,10*(t-=1))*Math.sin((t*d-s)*(2*Math.PI)/p))+b;}return a*Math.pow(2,-10*(t-=1))*Math.sin((t*d-s)*(2*Math.PI)/p)*0.5+c+b;},backIn:function(t,b,c,d,s){if(typeof s=="undefined"){s=1.70158;}return c*(t/=d)*t*((s+1)*t-s)+b;},backOut:function(t,b,c,d,s){if(typeof s=="undefined"){s=1.70158;}return c*((t=t/d-1)*t*((s+1)*t+s)+1)+b;},backBoth:function(t,b,c,d,s){if(typeof s=="undefined"){s=1.70158;}if((t/=d/2)<1){return c/2*(t*t*(((s*=(1.525))+1)*t-s))+b;}return c/2*((t-=2)*t*(((s*=(1.525))+1)*t+s)+2)+b;},bounceIn:function(t,b,c,d){return c-Ext.lib.Easing.bounceOut(d-t,0,c,d)+b;},bounceOut:function(t,b,c,d){if((t/=d)<(1/2.75)){return c*(7.5625*t*t)+b;}else{if(t<(2/2.75)){return c*(7.5625*(t-=(1.5/2.75))*t+0.75)+b;}else{if(t<(2.5/2.75)){return c*(7.5625*(t-=(2.25/2.75))*t+0.9375)+b;}}}return c*(7.5625*(t-=(2.625/2.75))*t+0.984375)+b;},bounceBoth:function(t,b,c,d){if(t<d/2){return Ext.lib.Easing.bounceIn(t*2,0,c,d)*0.5+b;}return Ext.lib.Easing.bounceOut(t*2-d,0,c,d)*0.5+c*0.5+b;}};(function(){Ext.lib.Motion=function(el,_1ba,_1bb,_1bc){if(el){Ext.lib.Motion.superclass.constructor.call(this,el,_1ba,_1bb,_1bc);}};Ext.extend(Ext.lib.Motion,Ext.lib.ColorAnim);var Y=Ext.lib;var _1be=Y.Motion.superclass;var _1bf=Y.Motion.prototype;_1bf.toString=function(){var el=this.getEl();var id=el.id||el.tagName;return("Motion "+id);};_1bf.patterns.points=/^points$/i;_1bf.setAttribute=function(attr,val,unit){if(this.patterns.points.test(attr)){unit=unit||"px";_1be.setAttribute.call(this,"left",val[0],unit);_1be.setAttribute.call(this,"top",val[1],unit);}else{_1be.setAttribute.call(this,attr,val,unit);}};_1bf.getAttribute=function(attr){if(this.patterns.points.test(attr)){var val=[_1be.getAttribute.call(this,"left"),_1be.getAttribute.call(this,"top")];}else{val=_1be.getAttribute.call(this,attr);}return val;};_1bf.doMethod=function(attr,_1c8,end){var val=null;if(this.patterns.points.test(attr)){var t=this.method(this.currentFrame,0,100,this.totalFrames)/100;val=Y.Bezier.getPosition(this.runtimeAttributes[attr],t);}else{val=_1be.doMethod.call(this,attr,_1c8,end);}return val;};_1bf.setRuntimeAttribute=function(attr){if(this.patterns.points.test(attr)){var el=this.getEl();var _1ce=this.attributes;var _1cf;var _1d0=_1ce["points"]["control"]||[];var end;var i,len;if(_1d0.length>0&&!(_1d0[0]instanceof Array)){_1d0=[_1d0];}else{var tmp=[];for(i=0,len=_1d0.length;i<len;++i){tmp[i]=_1d0[i];}_1d0=tmp;}Ext.fly(el).position();if(_1d5(_1ce["points"]["from"])){Ext.lib.Dom.setXY(el,_1ce["points"]["from"]);}else{Ext.lib.Dom.setXY(el,Ext.lib.Dom.getXY(el));}_1cf=this.getAttribute("points");if(_1d5(_1ce["points"]["to"])){end=_1d6.call(this,_1ce["points"]["to"],_1cf);var _1d7=Ext.lib.Dom.getXY(this.getEl());for(i=0,len=_1d0.length;i<len;++i){_1d0[i]=_1d6.call(this,_1d0[i],_1cf);}}else{if(_1d5(_1ce["points"]["by"])){end=[_1cf[0]+_1ce["points"]["by"][0],_1cf[1]+_1ce["points"]["by"][1]];for(i=0,len=_1d0.length;i<len;++i){_1d0[i]=[_1cf[0]+_1d0[i][0],_1cf[1]+_1d0[i][1]];}}}this.runtimeAttributes[attr]=[_1cf];if(_1d0.length>0){this.runtimeAttributes[attr]=this.runtimeAttributes[attr].concat(_1d0);}this.runtimeAttributes[attr][this.runtimeAttributes[attr].length]=end;}else{_1be.setRuntimeAttribute.call(this,attr);}};var _1d6=function(val,_1d9){var _1da=Ext.lib.Dom.getXY(this.getEl());val=[val[0]-_1da[0]+_1d9[0],val[1]-_1da[1]+_1d9[1]];return val;};var _1d5=function(prop){return(typeof prop!=="undefined");};})();(function(){Ext.lib.Scroll=function(el,_1dd,_1de,_1df){if(el){Ext.lib.Scroll.superclass.constructor.call(this,el,_1dd,_1de,_1df);}};Ext.extend(Ext.lib.Scroll,Ext.lib.ColorAnim);var Y=Ext.lib;var _1e1=Y.Scroll.superclass;var _1e2=Y.Scroll.prototype;_1e2.toString=function(){var el=this.getEl();var id=el.id||el.tagName;return("Scroll "+id);};_1e2.doMethod=function(attr,_1e6,end){var val=null;if(attr=="scroll"){val=[this.method(this.currentFrame,_1e6[0],end[0]-_1e6[0],this.totalFrames),this.method(this.currentFrame,_1e6[1],end[1]-_1e6[1],this.totalFrames)];}else{val=_1e1.doMethod.call(this,attr,_1e6,end);}return val;};_1e2.getAttribute=function(attr){var val=null;var el=this.getEl();if(attr=="scroll"){val=[el.scrollLeft,el.scrollTop];}else{val=_1e1.getAttribute.call(this,attr);}return val;};_1e2.setAttribute=function(attr,val,unit){var el=this.getEl();if(attr=="scroll"){el.scrollLeft=val[0];el.scrollTop=val[1];}else{_1e1.setAttribute.call(this,attr,val,unit);}};})();})();