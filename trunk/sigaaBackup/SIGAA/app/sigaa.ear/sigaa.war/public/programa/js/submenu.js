/*
* Funcao para o submenu
*/

var Submenu = function(){
	this.initialize.apply(this, arguments);
};

Submenu.prototype = {
	
	initialize: function(container, options){
		this.options = options;
		this.container = $(container);
		this.elements = this.container.find(this.options.elementsSelector);
		this.attachElementsEvent();
	},
	
	attachElementsEvent: function(){
		this.elements.bind('mouseenter', {instance: this}, this.mouseEnterEventHandler);
		this.elements.bind('mouseleave', {instance: this}, this.mouseLeaveEventHandler);
	},
	
	mouseEnterEventHandler: function(e){
		$(this).find(e.data.instance.options.submenuSelector).show();
	},
	
	mouseLeaveEventHandler: function(e){
		$(this).find(e.data.instance.options.submenuSelector).hide();
	}
	
};
