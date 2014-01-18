if ( !ativo ) {
	ativo = Ext.get('p-academico');
}

Ext.onReady(function(){

    var menu = Ext.get('modulos');
    var box = Ext.get('opcoes-modulo');

    ativo.show();
	Ext.get('l-' +  ativo.id.substring(2)).radioClass('item-over');

    menu.on('mousedown', function(e, t){
        if((t = e.getTarget('.item:not(.item-over)')) && e.getTarget('.item:not(.inativo)')){
            Ext.fly(t).removeClass('item-inativo-over');
            Ext.fly(t).radioClass('item-over');

			var atual = Ext.get('p-' +  t.id.substring(2));
			if (atual && atual != ativo) {
				if (ativo) {
		            ativo.fadeOut( {duration:.4});
		        }
	            atual.slideIn('l', {duration:.7});
	            ativo = atual;
			}
        }
    });

	menu.on('mouseover', function(e, t){
        if((t = e.getTarget('.item:not(.item-over)')) && e.getTarget('.item:not(.inativo)')){
            Ext.fly(t).addClass('item-inativo-over');
        }
    });

    menu.on('mouseout', function(e, t){
        if((t = e.getTarget('.item:not(.item-over)')) && !e.within(t, true)){
            Ext.fly(t).removeClass('item-inativo-over');
        }
    });

    box.on('mouseover', function(e, t){
        if(t = e.getTarget('div.opcao')){
            Ext.fly(t).up('dt').addClass('opcao-over');
        }
    });
    box.on('mouseout', function(e, t){
        if(t = e.getTarget('div.opcao')){
            Ext.fly(t).up('dt').removeClass('opcao-over');
        }
    });

    box.on('mousedown', function(e, t){
        if( t = e.getTarget('div.opcao') ){
	      var a = Ext.fly(t).down('a');
	      document.location = a.dom;
        }
    });

});