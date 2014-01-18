/*
 * Ext JS Library 1.1
 * Copyright(c) 2006-2007, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://www.extjs.com/license
 */


Ext.data.Tree=function(_1){this.nodeHash={};this.root=null;if(_1){this.setRootNode(_1);}this.addEvents({"append":true,"remove":true,"move":true,"insert":true,"beforeappend":true,"beforeremove":true,"beforemove":true,"beforeinsert":true});Ext.data.Tree.superclass.constructor.call(this);};Ext.extend(Ext.data.Tree,Ext.util.Observable,{pathSeparator:"/",proxyNodeEvent:function(){return this.fireEvent.apply(this,arguments);},getRootNode:function(){return this.root;},setRootNode:function(_2){this.root=_2;_2.ownerTree=this;_2.isRoot=true;this.registerNode(_2);return _2;},getNodeById:function(id){return this.nodeHash[id];},registerNode:function(_4){this.nodeHash[_4.id]=_4;},unregisterNode:function(_5){delete this.nodeHash[_5.id];},toString:function(){return"[Tree"+(this.id?" "+this.id:"")+"]";}});Ext.data.Node=function(_6){this.attributes=_6||{};this.leaf=this.attributes.leaf;this.id=this.attributes.id;if(!this.id){this.id=Ext.id(null,"ynode-");this.attributes.id=this.id;}this.childNodes=[];if(!this.childNodes.indexOf){this.childNodes.indexOf=function(o){for(var i=0,_9=this.length;i<_9;i++){if(this[i]==o){return i;}}return-1;};}this.parentNode=null;this.firstChild=null;this.lastChild=null;this.previousSibling=null;this.nextSibling=null;this.addEvents({"append":true,"remove":true,"move":true,"insert":true,"beforeappend":true,"beforeremove":true,"beforemove":true,"beforeinsert":true});this.listeners=this.attributes.listeners;Ext.data.Node.superclass.constructor.call(this);};Ext.extend(Ext.data.Node,Ext.util.Observable,{fireEvent:function(_a){if(Ext.data.Node.superclass.fireEvent.apply(this,arguments)===false){return false;}var ot=this.getOwnerTree();if(ot){if(ot.proxyNodeEvent.apply(ot,arguments)===false){return false;}}return true;},isLeaf:function(){return this.leaf===true;},setFirstChild:function(_c){this.firstChild=_c;},setLastChild:function(_d){this.lastChild=_d;},isLast:function(){return(!this.parentNode?true:this.parentNode.lastChild==this);},isFirst:function(){return(!this.parentNode?true:this.parentNode.firstChild==this);},hasChildNodes:function(){return!this.isLeaf()&&this.childNodes.length>0;},appendChild:function(_e){var _f=false;if(_e instanceof Array){_f=_e;}else{if(arguments.length>1){_f=arguments;}}if(_f){for(var i=0,len=_f.length;i<len;i++){this.appendChild(_f[i]);}}else{if(this.fireEvent("beforeappend",this.ownerTree,this,_e)===false){return false;}var _12=this.childNodes.length;var _13=_e.parentNode;if(_13){if(_e.fireEvent("beforemove",_e.getOwnerTree(),_e,_13,this,_12)===false){return false;}_13.removeChild(_e);}_12=this.childNodes.length;if(_12==0){this.setFirstChild(_e);}this.childNodes.push(_e);_e.parentNode=this;var ps=this.childNodes[_12-1];if(ps){_e.previousSibling=ps;ps.nextSibling=_e;}else{_e.previousSibling=null;}_e.nextSibling=null;this.setLastChild(_e);_e.setOwnerTree(this.getOwnerTree());this.fireEvent("append",this.ownerTree,this,_e,_12);if(_13){_e.fireEvent("move",this.ownerTree,_e,_13,this,_12);}return _e;}},removeChild:function(_15){var _16=this.childNodes.indexOf(_15);if(_16==-1){return false;}if(this.fireEvent("beforeremove",this.ownerTree,this,_15)===false){return false;}this.childNodes.splice(_16,1);if(_15.previousSibling){_15.previousSibling.nextSibling=_15.nextSibling;}if(_15.nextSibling){_15.nextSibling.previousSibling=_15.previousSibling;}if(this.firstChild==_15){this.setFirstChild(_15.nextSibling);}if(this.lastChild==_15){this.setLastChild(_15.previousSibling);}_15.setOwnerTree(null);_15.parentNode=null;_15.previousSibling=null;_15.nextSibling=null;this.fireEvent("remove",this.ownerTree,this,_15);return _15;},insertBefore:function(_17,_18){if(!_18){return this.appendChild(_17);}if(_17==_18){return false;}if(this.fireEvent("beforeinsert",this.ownerTree,this,_17,_18)===false){return false;}var _19=this.childNodes.indexOf(_18);var _1a=_17.parentNode;var _1b=_19;if(_1a==this&&this.childNodes.indexOf(_17)<_19){_1b--;}if(_1a){if(_17.fireEvent("beforemove",_17.getOwnerTree(),_17,_1a,this,_19,_18)===false){return false;}_1a.removeChild(_17);}if(_1b==0){this.setFirstChild(_17);}this.childNodes.splice(_1b,0,_17);_17.parentNode=this;var ps=this.childNodes[_1b-1];if(ps){_17.previousSibling=ps;ps.nextSibling=_17;}else{_17.previousSibling=null;}_17.nextSibling=_18;_18.previousSibling=_17;_17.setOwnerTree(this.getOwnerTree());this.fireEvent("insert",this.ownerTree,this,_17,_18);if(_1a){_17.fireEvent("move",this.ownerTree,_17,_1a,this,_1b,_18);}return _17;},item:function(_1d){return this.childNodes[_1d];},replaceChild:function(_1e,_1f){this.insertBefore(_1e,_1f);this.removeChild(_1f);return _1f;},indexOf:function(_20){return this.childNodes.indexOf(_20);},getOwnerTree:function(){if(!this.ownerTree){var p=this;while(p){if(p.ownerTree){this.ownerTree=p.ownerTree;break;}p=p.parentNode;}}return this.ownerTree;},getDepth:function(){var _22=0;var p=this;while(p.parentNode){++_22;p=p.parentNode;}return _22;},setOwnerTree:function(_24){if(_24!=this.ownerTree){if(this.ownerTree){this.ownerTree.unregisterNode(this);}this.ownerTree=_24;var cs=this.childNodes;for(var i=0,len=cs.length;i<len;i++){cs[i].setOwnerTree(_24);}if(_24){_24.registerNode(this);}}},getPath:function(_28){_28=_28||"id";var p=this.parentNode;var b=[this.attributes[_28]];while(p){b.unshift(p.attributes[_28]);p=p.parentNode;}var sep=this.getOwnerTree().pathSeparator;return sep+b.join(sep);},bubble:function(fn,_2d,_2e){var p=this;while(p){if(fn.call(_2d||p,_2e||p)===false){break;}p=p.parentNode;}},cascade:function(fn,_31,_32){if(fn.call(_31||this,_32||this)!==false){var cs=this.childNodes;for(var i=0,len=cs.length;i<len;i++){cs[i].cascade(fn,_31,_32);}}},eachChild:function(fn,_37,_38){var cs=this.childNodes;for(var i=0,len=cs.length;i<len;i++){if(fn.call(_37||this,_38||cs[i])===false){break;}}},findChild:function(_3c,_3d){var cs=this.childNodes;for(var i=0,len=cs.length;i<len;i++){if(cs[i].attributes[_3c]==_3d){return cs[i];}}return null;},findChildBy:function(fn,_42){var cs=this.childNodes;for(var i=0,len=cs.length;i<len;i++){if(fn.call(_42||cs[i],cs[i])===true){return cs[i];}}return null;},sort:function(fn,_47){var cs=this.childNodes;var len=cs.length;if(len>0){var _4a=_47?function(){fn.apply(_47,arguments);}:fn;cs.sort(_4a);for(var i=0;i<len;i++){var n=cs[i];n.previousSibling=cs[i-1];n.nextSibling=cs[i+1];if(i==0){this.setFirstChild(n);}if(i==len-1){this.setLastChild(n);}}}},contains:function(_4d){return _4d.isAncestor(this);},isAncestor:function(_4e){var p=this.parentNode;while(p){if(p==_4e){return true;}p=p.parentNode;}return false;},toString:function(){return"[Node"+(this.id?" "+this.id:"")+"]";}});