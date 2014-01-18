function new_Ajax() {
  if (window.XMLHttpRequest) {
    return new XMLHttpRequest();
  }
  else if (window.ActiveXObject) {
    return new ActiveXObject("Microsoft.XMLHTTP");
  }
  else {
    return false;
  }
}


/**
 * Extrai o conteudo de um tag XML.
 *
 * @param no_raiz O n? pai do tag que ser? extra?do o conte?do.
 * @param tag O nome do tag a ser procurado.
 * @return O conte?do do tag "tag", que ? filho de "no_raiz".
 */
function conteudo(no_raiz, tag) {
  return no_raiz.getElementsByTagName( tag.toUpperCase() )[0].firstChild.data;
}

/**
 *
 * Funcao chamada para carregar os valores de um select a partir da sele?ao de outro select
 *
 *
 */
function carregarSelect(select, proximoSelectId, url, valorExibir) {

	var proximoSelect = document.getElementById(proximoSelectId);
	var valorSelecao = select.options[select.selectedIndex].value;

	if (valorSelecao == "") {
		// limpando/desabilitando proximo select
		for( var i = 1; i < proximoSelect.length; i++){
      		proximoSelect.options[i] = null;
      	}
	    proximoSelect.disabled="disabled";
	} else {
    	var url = url + "?id=" + valorSelecao;

	    ajax = new_Ajax();
	    ajax.open("GET", url, true);
	    ajax.onreadystatechange = function() {
      		if (ajax.readyState == 4) {
        		if (ajax.status == 200) {
          			var xml = ajax.responseXML;
					var raiz = xml.getElementsByTagName("raiz")[0];
					if( raiz.childNodes.length < 1 )
						return;
          			//limpando proximo select
          			for( var i = 0; i < proximoSelect.length; i++){
          				proximoSelect.options[i+1] = null;
          			}

		          //populando proximo select
					for( var i = 0; i < raiz.childNodes.length; i++){
			          	var filho = raiz.childNodes[i];
			          	var id = conteudo(filho, "ID");
			          	var descricao = conteudo(filho, valorExibir);

			          	//limitando o select para 47 caracteres!
			          	var descricao_cortado = descricao.substr(0,47);
			          	if( descricao.length > 47 )
			          		descricao_cortado = descricao_cortado + '...';

			          	proximoSelect.options[i+1] = new Option(descricao_cortado, id);
          			}
              	proximoSelect.disabled="";
        	}
      	}
    }
  	ajax.send(null);
	}
}

function getContent(url,div,param) {
	var showContent = function(t) {
		$(div).innerHTML = t.responseText;
	};
	var ajax = new Ajax.Request(url,
		{
			method: 'get',
			parameters: param + '&ajaxRequest=true',
			onComplete: showContent
		});
}

function validaProducao(idProducao,value,div) {
	if ( value == '0' ) {
		alert('Selecione a validade');
	} else {
		getContent('/sigaa/ajaxValidacaoProducao',div,'idProducao=' + idProducao + '&validado='+ value)
	}
}
