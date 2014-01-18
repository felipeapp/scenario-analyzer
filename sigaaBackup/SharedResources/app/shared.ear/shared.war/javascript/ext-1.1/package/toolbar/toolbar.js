/*
 * Ext JS Library 1.1
 * Copyright(c) 2006-2007, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://www.extjs.com/license
 */


Ext.Toolbar=function(_1,_2,_3){if(_1 instanceof Array){_2=_1;_3=_2;_1=null;}Ext.apply(this,_3);this.buttons=_2;if(_1){this.render(_1);}};Ext.Toolbar.prototype={render:function(ct){this.el=Ext.get(ct);if(this.cls){this.el.addClass(this.cls);}this.el.update("<div class=\"x-toolbar x-small-editor\"><table cellspacing=\"0\"><tr></tr></table></div>");this.tr=this.el.child("tr",true);var _5=0;this.items=new Ext.util.MixedCollection(false,function(o){return o.id||("item"+(++_5));});if(this.buttons){this.add.apply(this,this.buttons);delete this.buttons;}},add:function(){var a=arguments,l=a.length;for(var i=0;i<l;i++){var el=a[i];if(el.applyTo){this.addField(el);}else{if(el.render){this.addItem(el);}else{if(typeof el=="string"){if(el=="separator"||el=="-"){this.addSeparator();}else{if(el==" "){this.addSpacer();}else{if(el=="->"){this.addFill();}else{this.addText(el);}}}}else{if(el.tagName){this.addElement(el);}else{if(typeof el=="object"){this.addButton(el);}}}}}}},getEl:function(){return this.el;},addSeparator:function(){return this.addItem(new Ext.Toolbar.Separator());},addSpacer:function(){return this.addItem(new Ext.Toolbar.Spacer());},addFill:function(){return this.addItem(new Ext.Toolbar.Fill());},addElement:function(el){return this.addItem(new Ext.Toolbar.Item(el));},addItem:function(_c){var td=this.nextBlock();_c.render(td);this.items.add(_c);return _c;},addButton:function(_e){if(_e instanceof Array){var _f=[];for(var i=0,len=_e.length;i<len;i++){_f.push(this.addButton(_e[i]));}return _f;}var b=_e;if(!(_e instanceof Ext.Toolbar.Button)){b=_e.split?new Ext.Toolbar.SplitButton(_e):new Ext.Toolbar.Button(_e);}var td=this.nextBlock();b.render(td);this.items.add(b);return b;},addText:function(_14){return this.addItem(new Ext.Toolbar.TextItem(_14));},insertButton:function(_15,_16){if(_16 instanceof Array){var _17=[];for(var i=0,len=_16.length;i<len;i++){_17.push(this.insertButton(_15+i,_16[i]));}return _17;}if(!(_16 instanceof Ext.Toolbar.Button)){_16=new Ext.Toolbar.Button(_16);}var td=document.createElement("td");this.tr.insertBefore(td,this.tr.childNodes[_15]);_16.render(td);this.items.insert(_15,_16);return _16;},addDom:function(_1b,_1c){var td=this.nextBlock();Ext.DomHelper.overwrite(td,_1b);var ti=new Ext.Toolbar.Item(td.firstChild);ti.render(td);this.items.add(ti);return ti;},addField:function(_1f){var td=this.nextBlock();_1f.render(td);var ti=new Ext.Toolbar.Item(td.firstChild);ti.render(td);this.items.add(ti);return ti;},nextBlock:function(){var td=document.createElement("td");this.tr.appendChild(td);return td;},destroy:function(){if(this.items){Ext.destroy.apply(Ext,this.items.items);}Ext.Element.uncache(this.el,this.tr);}};Ext.Toolbar.Item=function(el){this.el=Ext.getDom(el);this.id=Ext.id(this.el);this.hidden=false;};Ext.Toolbar.Item.prototype={getEl:function(){return this.el;},render:function(td){this.td=td;td.appendChild(this.el);},destroy:function(){this.td.parentNode.removeChild(this.td);},show:function(){this.hidden=false;this.td.style.display="";},hide:function(){this.hidden=true;this.td.style.display="none";},setVisible:function(_25){if(_25){this.show();}else{this.hide();}},focus:function(){Ext.fly(this.el).focus();},disable:function(){Ext.fly(this.td).addClass("x-item-disabled");this.disabled=true;this.el.disabled=true;},enable:function(){Ext.fly(this.td).removeClass("x-item-disabled");this.disabled=false;this.el.disabled=false;}};Ext.Toolbar.Separator=function(){var s=document.createElement("span");s.className="ytb-sep";Ext.Toolbar.Separator.superclass.constructor.call(this,s);};Ext.extend(Ext.Toolbar.Separator,Ext.Toolbar.Item,{enable:Ext.emptyFn,disable:Ext.emptyFn,focus:Ext.emptyFn});Ext.Toolbar.Spacer=function(){var s=document.createElement("div");s.className="ytb-spacer";Ext.Toolbar.Spacer.superclass.constructor.call(this,s);};Ext.extend(Ext.Toolbar.Spacer,Ext.Toolbar.Item,{enable:Ext.emptyFn,disable:Ext.emptyFn,focus:Ext.emptyFn});Ext.Toolbar.Fill=Ext.extend(Ext.Toolbar.Spacer,{render:function(td){td.style.width="100%";Ext.Toolbar.Fill.superclass.render.call(this,td);}});Ext.Toolbar.TextItem=function(_29){var s=document.createElement("span");s.className="ytb-text";s.innerHTML=_29;Ext.Toolbar.TextItem.superclass.constructor.call(this,s);};Ext.extend(Ext.Toolbar.TextItem,Ext.Toolbar.Item,{enable:Ext.emptyFn,disable:Ext.emptyFn,focus:Ext.emptyFn});Ext.Toolbar.Button=function(_2b){Ext.Toolbar.Button.superclass.constructor.call(this,null,_2b);};Ext.extend(Ext.Toolbar.Button,Ext.Button,{render:function(td){this.td=td;Ext.Toolbar.Button.superclass.render.call(this,td);},destroy:function(){Ext.Toolbar.Button.superclass.destroy.call(this);this.td.parentNode.removeChild(this.td);},show:function(){this.hidden=false;this.td.style.display="";},hide:function(){this.hidden=true;this.td.style.display="none";},disable:function(){Ext.fly(this.td).addClass("x-item-disabled");this.disabled=true;},enable:function(){Ext.fly(this.td).removeClass("x-item-disabled");this.disabled=false;}});Ext.ToolbarButton=Ext.Toolbar.Button;Ext.Toolbar.SplitButton=function(_2d){Ext.Toolbar.SplitButton.superclass.constructor.call(this,null,_2d);};Ext.extend(Ext.Toolbar.SplitButton,Ext.SplitButton,{render:function(td){this.td=td;Ext.Toolbar.SplitButton.superclass.render.call(this,td);},destroy:function(){Ext.Toolbar.SplitButton.superclass.destroy.call(this);this.td.parentNode.removeChild(this.td);},show:function(){this.hidden=false;this.td.style.display="";},hide:function(){this.hidden=true;this.td.style.display="none";}});Ext.Toolbar.MenuButton=Ext.Toolbar.SplitButton;

Ext.PagingToolbar=function(el,ds,_3){Ext.PagingToolbar.superclass.constructor.call(this,el,null,_3);this.ds=ds;this.cursor=0;this.renderButtons(this.el);this.bind(ds);};Ext.extend(Ext.PagingToolbar,Ext.Toolbar,{pageSize:20,displayMsg:"Displaying {0} - {1} of {2}",emptyMsg:"No data to display",beforePageText:"Page",afterPageText:"of {0}",firstText:"First Page",prevText:"Previous Page",nextText:"Next Page",lastText:"Last Page",refreshText:"Refresh",renderButtons:function(el){Ext.PagingToolbar.superclass.render.call(this,el);this.first=this.addButton({tooltip:this.firstText,cls:"x-btn-icon x-grid-page-first",disabled:true,handler:this.onClick.createDelegate(this,["first"])});this.prev=this.addButton({tooltip:this.prevText,cls:"x-btn-icon x-grid-page-prev",disabled:true,handler:this.onClick.createDelegate(this,["prev"])});this.addSeparator();this.add(this.beforePageText);this.field=Ext.get(this.addDom({tag:"input",type:"text",size:"3",value:"1",cls:"x-grid-page-number"}).el);this.field.on("keydown",this.onPagingKeydown,this);this.field.on("focus",function(){this.dom.select();});this.afterTextEl=this.addText(String.format(this.afterPageText,1));this.field.setHeight(18);this.addSeparator();this.next=this.addButton({tooltip:this.nextText,cls:"x-btn-icon x-grid-page-next",disabled:true,handler:this.onClick.createDelegate(this,["next"])});this.last=this.addButton({tooltip:this.lastText,cls:"x-btn-icon x-grid-page-last",disabled:true,handler:this.onClick.createDelegate(this,["last"])});this.addSeparator();this.loading=this.addButton({tooltip:this.refreshText,cls:"x-btn-icon x-grid-loading",handler:this.onClick.createDelegate(this,["refresh"])});if(this.displayInfo){this.displayEl=Ext.fly(this.el.dom.firstChild).createChild({cls:"x-paging-info"});}},updateInfo:function(){if(this.displayEl){var _5=this.ds.getCount();var _6=_5==0?this.emptyMsg:String.format(this.displayMsg,this.cursor+1,this.cursor+_5,this.ds.getTotalCount());this.displayEl.update(_6);}},onLoad:function(ds,r,o){this.cursor=o.params?o.params.start:0;var d=this.getPageData(),ap=d.activePage,ps=d.pages;this.afterTextEl.el.innerHTML=String.format(this.afterPageText,d.pages);this.field.dom.value=ap;this.first.setDisabled(ap==1);this.prev.setDisabled(ap==1);this.next.setDisabled(ap==ps);this.last.setDisabled(ap==ps);this.loading.enable();this.updateInfo();},getPageData:function(){var _d=this.ds.getTotalCount();return{total:_d,activePage:Math.ceil((this.cursor+this.pageSize)/this.pageSize),pages:_d<this.pageSize?1:Math.ceil(_d/this.pageSize)};},onLoadError:function(){this.loading.enable();},onPagingKeydown:function(e){var k=e.getKey();var d=this.getPageData();if(k==e.RETURN){var v=this.field.dom.value,_12;if(!v||isNaN(_12=parseInt(v,10))){this.field.dom.value=d.activePage;return;}_12=Math.min(Math.max(1,_12),d.pages)-1;this.ds.load({params:{start:_12*this.pageSize,limit:this.pageSize}});e.stopEvent();}else{if(k==e.HOME||(k==e.UP&&e.ctrlKey)||(k==e.PAGEUP&&e.ctrlKey)||(k==e.RIGHT&&e.ctrlKey)||k==e.END||(k==e.DOWN&&e.ctrlKey)||(k==e.LEFT&&e.ctrlKey)||(k==e.PAGEDOWN&&e.ctrlKey)){var _12=(k==e.HOME||(k==e.DOWN&&e.ctrlKey)||(k==e.LEFT&&e.ctrlKey)||(k==e.PAGEDOWN&&e.ctrlKey))?1:d.pages;this.field.dom.value=_12;this.ds.load({params:{start:(_12-1)*this.pageSize,limit:this.pageSize}});e.stopEvent();}else{if(k==e.UP||k==e.RIGHT||k==e.PAGEUP||k==e.DOWN||k==e.LEFT||k==e.PAGEDOWN){var v=this.field.dom.value,_12;var _13=(e.shiftKey)?10:1;if(k==e.DOWN||k==e.LEFT||k==e.PAGEDOWN){_13*=-1;}if(!v||isNaN(_12=parseInt(v,10))){this.field.dom.value=d.activePage;return;}else{if(parseInt(v,10)+_13>=1&parseInt(v,10)+_13<=d.pages){this.field.dom.value=parseInt(v,10)+_13;_12=Math.min(Math.max(1,_12+_13),d.pages)-1;this.ds.load({params:{start:_12*this.pageSize,limit:this.pageSize}});}}e.stopEvent();}}}},beforeLoad:function(){if(this.loading){this.loading.disable();}},onClick:function(_14){var ds=this.ds;switch(_14){case"first":ds.load({params:{start:0,limit:this.pageSize}});break;case"prev":ds.load({params:{start:Math.max(0,this.cursor-this.pageSize),limit:this.pageSize}});break;case"next":ds.load({params:{start:this.cursor+this.pageSize,limit:this.pageSize}});break;case"last":var _16=ds.getTotalCount();var _17=_16%this.pageSize;var _18=_17?(_16-_17):_16-this.pageSize;ds.load({params:{start:_18,limit:this.pageSize}});break;case"refresh":ds.load({params:{start:this.cursor,limit:this.pageSize}});break;}},unbind:function(ds){ds.un("beforeload",this.beforeLoad,this);ds.un("load",this.onLoad,this);ds.un("loadexception",this.onLoadError,this);this.ds=undefined;},bind:function(ds){ds.on("beforeload",this.beforeLoad,this);ds.on("load",this.onLoad,this);ds.on("loadexception",this.onLoadError,this);this.ds=ds;}});
