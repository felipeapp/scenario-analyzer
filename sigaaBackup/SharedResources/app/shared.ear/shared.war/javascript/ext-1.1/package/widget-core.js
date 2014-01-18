/*
 * Ext JS Library 1.1
 * Copyright(c) 2006-2007, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://www.extjs.com/license
 */


Ext.ComponentMgr=function(){var _1=new Ext.util.MixedCollection();return{register:function(c){_1.add(c);},unregister:function(c){_1.remove(c);},get:function(id){return _1.get(id);},onAvailable:function(id,fn,_7){_1.on("add",function(_8,o){if(o.id==id){fn.call(_7||o,o);_1.un("add",fn,_7);}});}};}();Ext.Component=function(_a){_a=_a||{};if(_a.tagName||_a.dom||typeof _a=="string"){_a={el:_a,id:_a.id||_a};}this.initialConfig=_a;Ext.apply(this,_a);this.addEvents({disable:true,enable:true,beforeshow:true,show:true,beforehide:true,hide:true,beforerender:true,render:true,beforedestroy:true,destroy:true});if(!this.id){this.id="ext-comp-"+(++Ext.Component.AUTO_ID);}Ext.ComponentMgr.register(this);Ext.Component.superclass.constructor.call(this);this.initComponent();if(this.renderTo){this.render(this.renderTo);delete this.renderTo;}};Ext.Component.AUTO_ID=1000;Ext.extend(Ext.Component,Ext.util.Observable,{hidden:false,disabled:false,rendered:false,disabledClass:"x-item-disabled",allowDomMove:true,hideMode:"display",ctype:"Ext.Component",actionMode:"el",getActionEl:function(){return this[this.actionMode];},initComponent:Ext.emptyFn,render:function(_b,_c){if(!this.rendered&&this.fireEvent("beforerender",this)!==false){if(!_b&&this.el){this.el=Ext.get(this.el);_b=this.el.dom.parentNode;this.allowDomMove=false;}this.container=Ext.get(_b);this.rendered=true;if(_c!==undefined){if(typeof _c=="number"){_c=this.container.dom.childNodes[_c];}else{_c=Ext.getDom(_c);}}this.onRender(this.container,_c||null);if(this.cls){this.el.addClass(this.cls);delete this.cls;}if(this.style){this.el.applyStyles(this.style);delete this.style;}this.fireEvent("render",this);this.afterRender(this.container);if(this.hidden){this.hide();}if(this.disabled){this.disable();}}return this;},onRender:function(ct,_e){if(this.el){this.el=Ext.get(this.el);if(this.allowDomMove!==false){ct.dom.insertBefore(this.el.dom,_e);}}},getAutoCreate:function(){var _f=typeof this.autoCreate=="object"?this.autoCreate:Ext.apply({},this.defaultAutoCreate);if(this.id&&!_f.id){_f.id=this.id;}return _f;},afterRender:Ext.emptyFn,destroy:function(){if(this.fireEvent("beforedestroy",this)!==false){this.purgeListeners();this.beforeDestroy();if(this.rendered){this.el.removeAllListeners();this.el.remove();if(this.actionMode=="container"){this.container.remove();}}this.onDestroy();Ext.ComponentMgr.unregister(this);this.fireEvent("destroy",this);}},beforeDestroy:function(){},onDestroy:function(){},getEl:function(){return this.el;},getId:function(){return this.id;},focus:function(_10){if(this.rendered){this.el.focus();if(_10===true){this.el.dom.select();}}return this;},blur:function(){if(this.rendered){this.el.blur();}return this;},disable:function(){if(this.rendered){this.onDisable();}this.disabled=true;this.fireEvent("disable",this);return this;},onDisable:function(){this.getActionEl().addClass(this.disabledClass);this.el.dom.disabled=true;},enable:function(){if(this.rendered){this.onEnable();}this.disabled=false;this.fireEvent("enable",this);return this;},onEnable:function(){this.getActionEl().removeClass(this.disabledClass);this.el.dom.disabled=false;},setDisabled:function(_11){this[_11?"disable":"enable"]();},show:function(){if(this.fireEvent("beforeshow",this)!==false){this.hidden=false;if(this.rendered){this.onShow();}this.fireEvent("show",this);}return this;},onShow:function(){var ae=this.getActionEl();if(this.hideMode=="visibility"){ae.dom.style.visibility="visible";}else{if(this.hideMode=="offsets"){ae.removeClass("x-hidden");}else{ae.dom.style.display="";}}},hide:function(){if(this.fireEvent("beforehide",this)!==false){this.hidden=true;if(this.rendered){this.onHide();}this.fireEvent("hide",this);}return this;},onHide:function(){var ae=this.getActionEl();if(this.hideMode=="visibility"){ae.dom.style.visibility="hidden";}else{if(this.hideMode=="offsets"){ae.addClass("x-hidden");}else{ae.dom.style.display="none";}}},setVisible:function(_14){if(_14){this.show();}else{this.hide();}return this;},isVisible:function(){return this.getActionEl().isVisible();},cloneConfig:function(_15){_15=_15||{};var id=_15.id||Ext.id();var cfg=Ext.applyIf(_15,this.initialConfig);cfg.id=id;return new this.constructor(cfg);}});

Ext.BoxComponent=function(_1){Ext.BoxComponent.superclass.constructor.call(this,_1);this.addEvents({resize:true,move:true});};Ext.extend(Ext.BoxComponent,Ext.Component,{boxReady:false,deferHeight:false,setSize:function(w,h){if(typeof w=="object"){h=w.height;w=w.width;}if(!this.boxReady){this.width=w;this.height=h;return this;}if(this.lastSize&&this.lastSize.width==w&&this.lastSize.height==h){return this;}this.lastSize={width:w,height:h};var _4=this.adjustSize(w,h);var aw=_4.width,ah=_4.height;if(aw!==undefined||ah!==undefined){var rz=this.getResizeEl();if(!this.deferHeight&&aw!==undefined&&ah!==undefined){rz.setSize(aw,ah);}else{if(!this.deferHeight&&ah!==undefined){rz.setHeight(ah);}else{if(aw!==undefined){rz.setWidth(aw);}}}this.onResize(aw,ah,w,h);this.fireEvent("resize",this,aw,ah,w,h);}return this;},getSize:function(){return this.el.getSize();},getPosition:function(_8){if(_8===true){return[this.el.getLeft(true),this.el.getTop(true)];}return this.xy||this.el.getXY();},getBox:function(_9){var s=this.el.getSize();if(_9){s.x=this.el.getLeft(true);s.y=this.el.getTop(true);}else{var xy=this.xy||this.el.getXY();s.x=xy[0];s.y=xy[1];}return s;},updateBox:function(_c){this.setSize(_c.width,_c.height);this.setPagePosition(_c.x,_c.y);return this;},getResizeEl:function(){return this.resizeEl||this.el;},getPositionEl:function(){return this.positionEl||this.el;},setPosition:function(x,y){this.x=x;this.y=y;if(!this.boxReady){return this;}var _f=this.adjustPosition(x,y);var ax=_f.x,ay=_f.y;var el=this.getPositionEl();if(ax!==undefined||ay!==undefined){if(ax!==undefined&&ay!==undefined){el.setLeftTop(ax,ay);}else{if(ax!==undefined){el.setLeft(ax);}else{if(ay!==undefined){el.setTop(ay);}}}this.onPosition(ax,ay);this.fireEvent("move",this,ax,ay);}return this;},setPagePosition:function(x,y){this.pageX=x;this.pageY=y;if(!this.boxReady){return;}if(x===undefined||y===undefined){return;}var p=this.el.translatePoints(x,y);this.setPosition(p.left,p.top);return this;},onRender:function(ct,_17){Ext.BoxComponent.superclass.onRender.call(this,ct,_17);if(this.resizeEl){this.resizeEl=Ext.get(this.resizeEl);}if(this.positionEl){this.positionEl=Ext.get(this.positionEl);}},afterRender:function(){Ext.BoxComponent.superclass.afterRender.call(this);this.boxReady=true;this.setSize(this.width,this.height);if(this.x||this.y){this.setPosition(this.x,this.y);}if(this.pageX||this.pageY){this.setPagePosition(this.pageX,this.pageY);}},syncSize:function(){this.setSize(this.el.getWidth(),this.el.getHeight());return this;},onResize:function(_18,_19,_1a,_1b){},onPosition:function(x,y){},adjustSize:function(w,h){if(this.autoWidth){w="auto";}if(this.autoHeight){h="auto";}return{width:w,height:h};},adjustPosition:function(x,y){return{x:x,y:y};}});

(function(){Ext.Layer=function(_1,_2){_1=_1||{};var dh=Ext.DomHelper;var cp=_1.parentEl,_5=cp?Ext.getDom(cp):document.body;if(_2){this.dom=Ext.getDom(_2);}if(!this.dom){var o=_1.dh||{tag:"div",cls:"x-layer"};this.dom=dh.append(_5,o);}if(_1.cls){this.addClass(_1.cls);}this.constrain=_1.constrain!==false;this.visibilityMode=Ext.Element.VISIBILITY;if(_1.id){this.id=this.dom.id=_1.id;}else{this.id=Ext.id(this.dom);}this.zindex=_1.zindex||this.getZIndex();this.position("absolute",this.zindex);if(_1.shadow){this.shadowOffset=_1.shadowOffset||4;this.shadow=new Ext.Shadow({offset:this.shadowOffset,mode:_1.shadow});}else{this.shadowOffset=0;}this.useShim=_1.shim!==false&&Ext.useShims;this.useDisplay=_1.useDisplay;this.hide();};var _7=Ext.Element.prototype;var _8=[];Ext.extend(Ext.Layer,Ext.Element,{getZIndex:function(){return this.zindex||parseInt(this.getStyle("z-index"),10)||11000;},getShim:function(){if(!this.useShim){return null;}if(this.shim){return this.shim;}var _9=_8.shift();if(!_9){_9=this.createShim();_9.enableDisplayMode("block");_9.dom.style.display="none";_9.dom.style.visibility="visible";}var pn=this.dom.parentNode;if(_9.dom.parentNode!=pn){pn.insertBefore(_9.dom,this.dom);}_9.setStyle("z-index",this.getZIndex()-2);this.shim=_9;return _9;},hideShim:function(){if(this.shim){this.shim.setDisplayed(false);_8.push(this.shim);delete this.shim;}},disableShadow:function(){if(this.shadow){this.shadowDisabled=true;this.shadow.hide();this.lastShadowOffset=this.shadowOffset;this.shadowOffset=0;}},enableShadow:function(_b){if(this.shadow){this.shadowDisabled=false;this.shadowOffset=this.lastShadowOffset;delete this.lastShadowOffset;if(_b){this.sync(true);}}},sync:function(_c){var sw=this.shadow;if(!this.updating&&this.isVisible()&&(sw||this.useShim)){var sh=this.getShim();var w=this.getWidth(),h=this.getHeight();var l=this.getLeft(true),t=this.getTop(true);if(sw&&!this.shadowDisabled){if(_c&&!sw.isVisible()){sw.show(this);}else{sw.realign(l,t,w,h);}if(sh){if(_c){sh.show();}var a=sw.adjusts,s=sh.dom.style;s.left=(Math.min(l,l+a.l))+"px";s.top=(Math.min(t,t+a.t))+"px";s.width=(w+a.w)+"px";s.height=(h+a.h)+"px";}}else{if(sh){if(_c){sh.show();}sh.setSize(w,h);sh.setLeftTop(l,t);}}}},destroy:function(){this.hideShim();if(this.shadow){this.shadow.hide();}this.removeAllListeners();var pn=this.dom.parentNode;if(pn){pn.removeChild(this.dom);}Ext.Element.uncache(this.id);},remove:function(){this.destroy();},beginUpdate:function(){this.updating=true;},endUpdate:function(){this.updating=false;this.sync(true);},hideUnders:function(_16){if(this.shadow){this.shadow.hide();}this.hideShim();},constrainXY:function(){if(this.constrain){var vw=Ext.lib.Dom.getViewWidth(),vh=Ext.lib.Dom.getViewHeight();var s=Ext.get(document).getScroll();var xy=this.getXY();var x=xy[0],y=xy[1];var w=this.dom.offsetWidth+this.shadowOffset,h=this.dom.offsetHeight+this.shadowOffset;var _1f=false;if((x+w)>vw+s.left){x=vw-w-this.shadowOffset;_1f=true;}if((y+h)>vh+s.top){y=vh-h-this.shadowOffset;_1f=true;}if(x<s.left){x=s.left;_1f=true;}if(y<s.top){y=s.top;_1f=true;}if(_1f){if(this.avoidY){var ay=this.avoidY;if(y<=ay&&(y+h)>=ay){y=ay-h-5;}}xy=[x,y];this.storeXY(xy);_7.setXY.call(this,xy);this.sync();}}},isVisible:function(){return this.visible;},showAction:function(){this.visible=true;if(this.useDisplay===true){this.setDisplayed("");}else{if(this.lastXY){_7.setXY.call(this,this.lastXY);}else{if(this.lastLT){_7.setLeftTop.call(this,this.lastLT[0],this.lastLT[1]);}}}},hideAction:function(){this.visible=false;if(this.useDisplay===true){this.setDisplayed(false);}else{this.setLeftTop(-10000,-10000);}},setVisible:function(v,a,d,c,e){if(v){this.showAction();}if(a&&v){var cb=function(){this.sync(true);if(c){c();}}.createDelegate(this);_7.setVisible.call(this,true,true,d,cb,e);}else{if(!v){this.hideUnders(true);}var cb=c;if(a){cb=function(){this.hideAction();if(c){c();}}.createDelegate(this);}_7.setVisible.call(this,v,a,d,cb,e);if(v){this.sync(true);}else{if(!a){this.hideAction();}}}},storeXY:function(xy){delete this.lastLT;this.lastXY=xy;},storeLeftTop:function(_28,top){delete this.lastXY;this.lastLT=[_28,top];},beforeFx:function(){this.beforeAction();return Ext.Layer.superclass.beforeFx.apply(this,arguments);},afterFx:function(){Ext.Layer.superclass.afterFx.apply(this,arguments);this.sync(this.isVisible());},beforeAction:function(){if(!this.updating&&this.shadow){this.shadow.hide();}},setLeft:function(_2a){this.storeLeftTop(_2a,this.getTop(true));_7.setLeft.apply(this,arguments);this.sync();},setTop:function(top){this.storeLeftTop(this.getLeft(true),top);_7.setTop.apply(this,arguments);this.sync();},setLeftTop:function(_2c,top){this.storeLeftTop(_2c,top);_7.setLeftTop.apply(this,arguments);this.sync();},setXY:function(xy,a,d,c,e){this.fixDisplay();this.beforeAction();this.storeXY(xy);var cb=this.createCB(c);_7.setXY.call(this,xy,a,d,cb,e);if(!a){cb();}},createCB:function(c){var el=this;return function(){el.constrainXY();el.sync(true);if(c){c();}};},setX:function(x,a,d,c,e){this.setXY([x,this.getY()],a,d,c,e);},setY:function(y,a,d,c,e){this.setXY([this.getX(),y],a,d,c,e);},setSize:function(w,h,a,d,c,e){this.beforeAction();var cb=this.createCB(c);_7.setSize.call(this,w,h,a,d,cb,e);if(!a){cb();}},setWidth:function(w,a,d,c,e){this.beforeAction();var cb=this.createCB(c);_7.setWidth.call(this,w,a,d,cb,e);if(!a){cb();}},setHeight:function(h,a,d,c,e){this.beforeAction();var cb=this.createCB(c);_7.setHeight.call(this,h,a,d,cb,e);if(!a){cb();}},setBounds:function(x,y,w,h,a,d,c,e){this.beforeAction();var cb=this.createCB(c);if(!a){this.storeXY([x,y]);_7.setXY.call(this,[x,y]);_7.setSize.call(this,w,h,a,d,cb,e);cb();}else{_7.setBounds.call(this,x,y,w,h,a,d,cb,e);}return this;},setZIndex:function(_5c){this.zindex=_5c;this.setStyle("z-index",_5c+2);if(this.shadow){this.shadow.setZIndex(_5c+1);}if(this.shim){this.shim.setStyle("z-index",_5c);}}});})();

Ext.Shadow=function(_1){Ext.apply(this,_1);if(typeof this.mode!="string"){this.mode=this.defaultMode;}var o=this.offset,a={h:0};var _4=Math.floor(this.offset/2);switch(this.mode.toLowerCase()){case"drop":a.w=0;a.l=a.t=o;a.t-=1;if(Ext.isIE){a.l-=this.offset+_4;a.t-=this.offset+_4;a.w-=_4;a.h-=_4;a.t+=1;}break;case"sides":a.w=(o*2);a.l=-o;a.t=o-1;if(Ext.isIE){a.l-=(this.offset-_4);a.t-=this.offset+_4;a.l+=1;a.w-=(this.offset-_4)*2;a.w-=_4+1;a.h-=1;}break;case"frame":a.w=a.h=(o*2);a.l=a.t=-o;a.t+=1;a.h-=2;if(Ext.isIE){a.l-=(this.offset-_4);a.t-=(this.offset-_4);a.l+=1;a.w-=(this.offset+_4+1);a.h-=(this.offset+_4);a.h+=1;}break;}this.adjusts=a;};Ext.Shadow.prototype={offset:4,defaultMode:"drop",show:function(_5){_5=Ext.get(_5);if(!this.el){this.el=Ext.Shadow.Pool.pull();if(this.el.dom.nextSibling!=_5.dom){this.el.insertBefore(_5);}}this.el.setStyle("z-index",this.zIndex||parseInt(_5.getStyle("z-index"),10)-1);if(Ext.isIE){this.el.dom.style.filter="progid:DXImageTransform.Microsoft.alpha(opacity=50) progid:DXImageTransform.Microsoft.Blur(pixelradius="+(this.offset)+")";}this.realign(_5.getLeft(true),_5.getTop(true),_5.getWidth(),_5.getHeight());this.el.dom.style.display="block";},isVisible:function(){return this.el?true:false;},realign:function(l,t,w,h){if(!this.el){return;}var a=this.adjusts,d=this.el.dom,s=d.style;var _d=0;s.left=(l+a.l)+"px";s.top=(t+a.t)+"px";var sw=(w+a.w),sh=(h+a.h),sws=sw+"px",shs=sh+"px";if(s.width!=sws||s.height!=shs){s.width=sws;s.height=shs;if(!Ext.isIE){var cn=d.childNodes;var sww=Math.max(0,(sw-12))+"px";cn[0].childNodes[1].style.width=sww;cn[1].childNodes[1].style.width=sww;cn[2].childNodes[1].style.width=sww;cn[1].style.height=Math.max(0,(sh-12))+"px";}}},hide:function(){if(this.el){this.el.dom.style.display="none";Ext.Shadow.Pool.push(this.el);delete this.el;}},setZIndex:function(z){this.zIndex=z;if(this.el){this.el.setStyle("z-index",z);}}};Ext.Shadow.Pool=function(){var p=[];var _16=Ext.isIE?"<div class=\"x-ie-shadow\"></div>":"<div class=\"x-shadow\"><div class=\"xst\"><div class=\"xstl\"></div><div class=\"xstc\"></div><div class=\"xstr\"></div></div><div class=\"xsc\"><div class=\"xsml\"></div><div class=\"xsmc\"></div><div class=\"xsmr\"></div></div><div class=\"xsb\"><div class=\"xsbl\"></div><div class=\"xsbc\"></div><div class=\"xsbr\"></div></div></div>";return{pull:function(){var sh=p.shift();if(!sh){sh=Ext.get(Ext.DomHelper.insertHtml("beforeBegin",document.body.firstChild,_16));sh.autoBoxAdjust=false;}return sh;},push:function(sh){p.push(sh);}};}();
