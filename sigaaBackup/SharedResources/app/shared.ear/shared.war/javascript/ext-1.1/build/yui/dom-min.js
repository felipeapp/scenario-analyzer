/*
 * Ext JS Library 1.1
 * Copyright(c) 2006-2007, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://www.extjs.com/license
 */


(function(){var Y=YAHOO.util,_2,_3,_4=0,_5={};var ua=navigator.userAgent.toLowerCase(),_7=(ua.indexOf("opera")>-1),_8=(ua.indexOf("safari")>-1),_9=(!_7&&!_8&&ua.indexOf("gecko")>-1),_a=(!_7&&ua.indexOf("msie")>-1);var _b={HYPHEN:/(-[a-z])/i};var _c=function(_d){if(!_b.HYPHEN.test(_d)){return _d;}if(_5[_d]){return _5[_d];}while(_b.HYPHEN.exec(_d)){_d=_d.replace(RegExp.$1,RegExp.$1.substr(1).toUpperCase());}_5[_d]=_d;return _d;};if(document.defaultView&&document.defaultView.getComputedStyle){_2=function(el,_f){var _10=null;var _11=document.defaultView.getComputedStyle(el,"");if(_11){_10=_11[_c(_f)];}return el.style[_f]||_10;};}else{if(document.documentElement.currentStyle&&_a){_2=function(el,_13){switch(_c(_13)){case"opacity":var val=100;try{val=el.filters["DXImageTransform.Microsoft.Alpha"].opacity;}catch(e){try{val=el.filters("alpha").opacity;}catch(e){}}return val/100;break;default:var _15=el.currentStyle?el.currentStyle[_13]:null;return(el.style[_13]||_15);}};}else{_2=function(el,_17){return el.style[_17];};}}if(_a){_3=function(el,_19,val){switch(_19){case"opacity":if(typeof el.style.filter=="string"){el.style.filter="alpha(opacity="+val*100+")";if(!el.currentStyle||!el.currentStyle.hasLayout){el.style.zoom=1;}}break;default:el.style[_19]=val;}};}else{_3=function(el,_1c,val){el.style[_1c]=val;};}YAHOO.util.Dom={get:function(el){if(!el){return null;}if(typeof el!="string"&&!(el instanceof Array)){return el;}if(typeof el=="string"){return document.getElementById(el);}else{var _1f=[];for(var i=0,len=el.length;i<len;++i){_1f[_1f.length]=Y.Dom.get(el[i]);}return _1f;}return null;},getStyle:function(el,_23){_23=_c(_23);var f=function(_25){return _2(_25,_23);};return Y.Dom.batch(el,f,Y.Dom,true);},setStyle:function(el,_27,val){_27=_c(_27);var f=function(_2a){_3(_2a,_27,val);};Y.Dom.batch(el,f,Y.Dom,true);},getXY:function(el){var f=function(el){if(el.parentNode===null||el.offsetParent===null||this.getStyle(el,"display")=="none"){return false;}var _2e=null;var pos=[];var box;if(el.getBoundingClientRect){box=el.getBoundingClientRect();var doc=document;if(!this.inDocument(el)&&parent.document!=document){doc=parent.document;if(!this.isAncestor(doc.documentElement,el)){return false;}}var _32=Math.max(doc.documentElement.scrollTop,doc.body.scrollTop);var _33=Math.max(doc.documentElement.scrollLeft,doc.body.scrollLeft);return[box.left+_33,box.top+_32];}else{pos=[el.offsetLeft,el.offsetTop];_2e=el.offsetParent;if(_2e!=el){while(_2e){pos[0]+=_2e.offsetLeft;pos[1]+=_2e.offsetTop;_2e=_2e.offsetParent;}}if(_8&&this.getStyle(el,"position")=="absolute"){pos[0]-=document.body.offsetLeft;pos[1]-=document.body.offsetTop;}}if(el.parentNode){_2e=el.parentNode;}else{_2e=null;}while(_2e&&_2e.tagName.toUpperCase()!="BODY"&&_2e.tagName.toUpperCase()!="HTML"){if(Y.Dom.getStyle(_2e,"display")!="inline"){pos[0]-=_2e.scrollLeft;pos[1]-=_2e.scrollTop;}if(_2e.parentNode){_2e=_2e.parentNode;}else{_2e=null;}}return pos;};return Y.Dom.batch(el,f,Y.Dom,true);},getX:function(el){var f=function(el){return Y.Dom.getXY(el)[0];};return Y.Dom.batch(el,f,Y.Dom,true);},getY:function(el){var f=function(el){return Y.Dom.getXY(el)[1];};return Y.Dom.batch(el,f,Y.Dom,true);},setXY:function(el,pos,_3c){var f=function(el){var _3f=this.getStyle(el,"position");if(_3f=="static"){this.setStyle(el,"position","relative");_3f="relative";}var _40=this.getXY(el);if(_40===false){return false;}var _41=[parseInt(this.getStyle(el,"left"),10),parseInt(this.getStyle(el,"top"),10)];if(isNaN(_41[0])){_41[0]=(_3f=="relative")?0:el.offsetLeft;}if(isNaN(_41[1])){_41[1]=(_3f=="relative")?0:el.offsetTop;}if(pos[0]!==null){el.style.left=pos[0]-_40[0]+_41[0]+"px";}if(pos[1]!==null){el.style.top=pos[1]-_40[1]+_41[1]+"px";}if(!_3c){var _42=this.getXY(el);if((pos[0]!==null&&_42[0]!=pos[0])||(pos[1]!==null&&_42[1]!=pos[1])){this.setXY(el,pos,true);}}};Y.Dom.batch(el,f,Y.Dom,true);},setX:function(el,x){Y.Dom.setXY(el,[x,null]);},setY:function(el,y){Y.Dom.setXY(el,[null,y]);},getRegion:function(el){var f=function(el){var _4a=new Y.Region.getRegion(el);return _4a;};return Y.Dom.batch(el,f,Y.Dom,true);},getClientWidth:function(){return Y.Dom.getViewportWidth();},getClientHeight:function(){return Y.Dom.getViewportHeight();},getElementsByClassName:function(_4b,tag,_4d){var _4e=function(el){return Y.Dom.hasClass(el,_4b);};return Y.Dom.getElementsBy(_4e,tag,_4d);},hasClass:function(el,_51){var re=new RegExp("(?:^|\\s+)"+_51+"(?:\\s+|$)");var f=function(el){return re.test(el["className"]);};return Y.Dom.batch(el,f,Y.Dom,true);},addClass:function(el,_56){var f=function(el){if(this.hasClass(el,_56)){return;}el["className"]=[el["className"],_56].join(" ");};Y.Dom.batch(el,f,Y.Dom,true);},removeClass:function(el,_5a){var re=new RegExp("(?:^|\\s+)"+_5a+"(?:\\s+|$)","g");var f=function(el){if(!this.hasClass(el,_5a)){return;}var c=el["className"];el["className"]=c.replace(re," ");if(this.hasClass(el,_5a)){this.removeClass(el,_5a);}};Y.Dom.batch(el,f,Y.Dom,true);},replaceClass:function(el,_60,_61){if(_60===_61){return false;}var re=new RegExp("(?:^|\\s+)"+_60+"(?:\\s+|$)","g");var f=function(el){if(!this.hasClass(el,_60)){this.addClass(el,_61);return;}el["className"]=el["className"].replace(re," "+_61+" ");if(this.hasClass(el,_60)){this.replaceClass(el,_60,_61);}};Y.Dom.batch(el,f,Y.Dom,true);},generateId:function(el,_66){_66=_66||"yui-gen";el=el||{};var f=function(el){if(el){el=Y.Dom.get(el);}else{el={};}if(!el.id){el.id=_66+_4++;}return el.id;};return Y.Dom.batch(el,f,Y.Dom,true);},isAncestor:function(_69,_6a){_69=Y.Dom.get(_69);if(!_69||!_6a){return false;}var f=function(_6c){if(_69.contains&&!_8){return _69.contains(_6c);}else{if(_69.compareDocumentPosition){return!!(_69.compareDocumentPosition(_6c)&16);}else{var _6d=_6c.parentNode;while(_6d){if(_6d==_69){return true;}else{if(!_6d.tagName||_6d.tagName.toUpperCase()=="HTML"){return false;}}_6d=_6d.parentNode;}return false;}}};return Y.Dom.batch(_6a,f,Y.Dom,true);},inDocument:function(el){var f=function(el){return this.isAncestor(document.documentElement,el);};return Y.Dom.batch(el,f,Y.Dom,true);},getElementsBy:function(_71,tag,_73){tag=tag||"*";var _74=[];if(_73){_73=Y.Dom.get(_73);if(!_73){return _74;}}else{_73=document;}var _75=_73.getElementsByTagName(tag);if(!_75.length&&(tag=="*"&&_73.all)){_75=_73.all;}for(var i=0,len=_75.length;i<len;++i){if(_71(_75[i])){_74[_74.length]=_75[i];}}return _74;},batch:function(el,_79,o,_7b){var id=el;el=Y.Dom.get(el);var _7d=(_7b)?o:window;if(!el||el.tagName||!el.length){if(!el){return false;}return _79.call(_7d,el,o);}var _7e=[];for(var i=0,len=el.length;i<len;++i){if(!el[i]){id=el[i];}_7e[_7e.length]=_79.call(_7d,el[i],o);}return _7e;},getDocumentHeight:function(){var _81=(document.compatMode!="CSS1Compat")?document.body.scrollHeight:document.documentElement.scrollHeight;var h=Math.max(_81,Y.Dom.getViewportHeight());return h;},getDocumentWidth:function(){var _83=(document.compatMode!="CSS1Compat")?document.body.scrollWidth:document.documentElement.scrollWidth;var w=Math.max(_83,Y.Dom.getViewportWidth());return w;},getViewportHeight:function(){var _85=self.innerHeight;var _86=document.compatMode;if((_86||_a)&&!_7){_85=(_86=="CSS1Compat")?document.documentElement.clientHeight:document.body.clientHeight;}return _85;},getViewportWidth:function(){var _87=self.innerWidth;var _88=document.compatMode;if(_88||_a){_87=(_88=="CSS1Compat")?document.documentElement.clientWidth:document.body.clientWidth;}return _87;}};})();YAHOO.util.Region=function(t,r,b,l){this.top=t;this[1]=t;this.right=r;this.bottom=b;this.left=l;this[0]=l;};YAHOO.util.Region.prototype.contains=function(_8d){return(_8d.left>=this.left&&_8d.right<=this.right&&_8d.top>=this.top&&_8d.bottom<=this.bottom);};YAHOO.util.Region.prototype.getArea=function(){return((this.bottom-this.top)*(this.right-this.left));};YAHOO.util.Region.prototype.intersect=function(_8e){var t=Math.max(this.top,_8e.top);var r=Math.min(this.right,_8e.right);var b=Math.min(this.bottom,_8e.bottom);var l=Math.max(this.left,_8e.left);if(b>=t&&r>=l){return new YAHOO.util.Region(t,r,b,l);}else{return null;}};YAHOO.util.Region.prototype.union=function(_93){var t=Math.min(this.top,_93.top);var r=Math.max(this.right,_93.right);var b=Math.max(this.bottom,_93.bottom);var l=Math.min(this.left,_93.left);return new YAHOO.util.Region(t,r,b,l);};YAHOO.util.Region.prototype.toString=function(){return("Region {"+"top: "+this.top+", right: "+this.right+", bottom: "+this.bottom+", left: "+this.left+"}");};YAHOO.util.Region.getRegion=function(el){var p=YAHOO.util.Dom.getXY(el);var t=p[1];var r=p[0]+el.offsetWidth;var b=p[1]+el.offsetHeight;var l=p[0];return new YAHOO.util.Region(t,r,b,l);};YAHOO.util.Point=function(x,y){if(x instanceof Array){y=x[1];x=x[0];}this.x=this.right=this.left=this[0]=x;this.y=this.top=this.bottom=this[1]=y;};YAHOO.util.Point.prototype=new YAHOO.util.Region();YAHOO.register("dom",YAHOO.util.Dom,{version:"2.2.0",build:"127"});