// funcao utilizada para habilitar a visualizacao dos detalhes de um item em especifico //

function toggleLinha(objLinha, objImagem){
	objLinha.toggle("slow");
	var iBaixo = "/sigaa/img/biblioteca/baixo.gif";
	var iCima  = "/sigaa/img/biblioteca/cima.gif";
	
	if (objImagem.attr("src") == iBaixo)
		objImagem.attr("src", iCima);
	else
		objImagem.attr("src", iBaixo);
}

function habilitarDetalhesItem(idLinha, idObjeto){
	var objLinha = jQuery("#linha_"+idLinha);
	var objImagem = jQuery("#imagem_"+idLinha);
	
	if (objLinha.html() == ""){
		jQuery.get(
				"/sigaa/biblioteca/processos_tecnicos/pesquisas_acervo/detalhesItemCatalografico.jsf",
				{idItem : idObjeto},
				function (r){
					objLinha.html(r);
					toggleLinha(objLinha, objImagem);
				}
		);
		// TODO adicionar "loading"
	} else
		toggleLinha(objLinha, objImagem);
}