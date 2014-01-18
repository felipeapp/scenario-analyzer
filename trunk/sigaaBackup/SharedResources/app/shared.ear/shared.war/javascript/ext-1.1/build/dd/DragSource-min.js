/*
 * Ext JS Library 1.1
 * Copyright(c) 2006-2007, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://www.extjs.com/license
 */


Ext.dd.DragSource=function(el,_2){this.el=Ext.get(el);this.dragData={};Ext.apply(this,_2);if(!this.proxy){this.proxy=new Ext.dd.StatusProxy();}Ext.dd.DragSource.superclass.constructor.call(this,this.el.dom,this.ddGroup||this.group,{dragElId:this.proxy.id,resizeFrame:false,isTarget:false,scroll:this.scroll===true});this.dragging=false;};Ext.extend(Ext.dd.DragSource,Ext.dd.DDProxy,{dropAllowed:"x-dd-drop-ok",dropNotAllowed:"x-dd-drop-nodrop",getDragData:function(e){return this.dragData;},onDragEnter:function(e,id){var _6=Ext.dd.DragDropMgr.getDDById(id);this.cachedTarget=_6;if(this.beforeDragEnter(_6,e,id)!==false){if(_6.isNotifyTarget){var _7=_6.notifyEnter(this,e,this.dragData);this.proxy.setStatus(_7);}else{this.proxy.setStatus(this.dropAllowed);}if(this.afterDragEnter){this.afterDragEnter(_6,e,id);}}},beforeDragEnter:function(_8,e,id){return true;},alignElWithMouse:function(){Ext.dd.DragSource.superclass.alignElWithMouse.apply(this,arguments);this.proxy.sync();},onDragOver:function(e,id){var _d=this.cachedTarget||Ext.dd.DragDropMgr.getDDById(id);if(this.beforeDragOver(_d,e,id)!==false){if(_d.isNotifyTarget){var _e=_d.notifyOver(this,e,this.dragData);this.proxy.setStatus(_e);}if(this.afterDragOver){this.afterDragOver(_d,e,id);}}},beforeDragOver:function(_f,e,id){return true;},onDragOut:function(e,id){var _14=this.cachedTarget||Ext.dd.DragDropMgr.getDDById(id);if(this.beforeDragOut(_14,e,id)!==false){if(_14.isNotifyTarget){_14.notifyOut(this,e,this.dragData);}this.proxy.reset();if(this.afterDragOut){this.afterDragOut(_14,e,id);}}this.cachedTarget=null;},beforeDragOut:function(_15,e,id){return true;},onDragDrop:function(e,id){var _1a=this.cachedTarget||Ext.dd.DragDropMgr.getDDById(id);if(this.beforeDragDrop(_1a,e,id)!==false){if(_1a.isNotifyTarget){if(_1a.notifyDrop(this,e,this.dragData)){this.onValidDrop(_1a,e,id);}else{this.onInvalidDrop(_1a,e,id);}}else{this.onValidDrop(_1a,e,id);}if(this.afterDragDrop){this.afterDragDrop(_1a,e,id);}}},beforeDragDrop:function(_1b,e,id){return true;},onValidDrop:function(_1e,e,id){this.hideProxy();if(this.afterValidDrop){this.afterValidDrop(_1e,e,id);}},getRepairXY:function(e,_22){return this.el.getXY();},onInvalidDrop:function(_23,e,id){this.beforeInvalidDrop(_23,e,id);if(this.cachedTarget){if(this.cachedTarget.isNotifyTarget){this.cachedTarget.notifyOut(this,e,this.dragData);}this.cacheTarget=null;}this.proxy.repair(this.getRepairXY(e,this.dragData),this.afterRepair,this);if(this.afterInvalidDrop){this.afterInvalidDrop(e,id);}},afterRepair:function(){if(Ext.enableFx){this.el.highlight(this.hlColor||"c3daf9");}this.dragging=false;},beforeInvalidDrop:function(_26,e,id){return true;},handleMouseDown:function(e){if(this.dragging){return;}var _2a=this.getDragData(e);if(_2a&&this.onBeforeDrag(_2a,e)!==false){this.dragData=_2a;this.proxy.stop();Ext.dd.DragSource.superclass.handleMouseDown.apply(this,arguments);}},onBeforeDrag:function(_2b,e){return true;},onStartDrag:Ext.emptyFn,startDrag:function(x,y){this.proxy.reset();this.dragging=true;this.proxy.update("");this.onInitDrag(x,y);this.proxy.show();},onInitDrag:function(x,y){var _31=this.el.dom.cloneNode(true);_31.id=Ext.id();this.proxy.update(_31);this.onStartDrag(x,y);return true;},getProxy:function(){return this.proxy;},hideProxy:function(){this.proxy.hide();this.proxy.reset(true);this.dragging=false;},triggerCacheRefresh:function(){Ext.dd.DDM.refreshCache(this.groups);},b4EndDrag:function(e){},endDrag:function(e){this.onEndDrag(this.dragData,e);},onEndDrag:function(_34,e){},autoOffset:function(x,y){this.setDelta(-12,-20);}});