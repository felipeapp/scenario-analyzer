
	<div class="descricaoOperacao">
		<b>Tipos de F�rum</b>
		<br />
		<br />

		<b>Cada usu�rio inicia apenas UM NOVO t�pico:</b> 
		cada participante pode abrir apenas um novo t�pico de discuss�o, mas todos podem responder livremente as mensagens, sem limites de quantidades. 
		Este formato � usado, por exemplo, nas atividades em que cada participante apresenta um tema a ser discutido e atua como moderador da discuss�o deste tema.<br/>

		<b>F�rum geral:</b>
		� um f�rum aberto, onde todos os participantes podem iniciar um novo t�pico de discuss�o quando quiserem.<br/>

		<b>F�rum P & R (perguntas e respostas):</b>
		neste f�rum um estudante pode ler as mensagens de outros somente ap�s a publica��o de sua mensagem. 
		Depois disto pode tamb�m responder �s mensagens do grupo. Isto permite que a primeira mensagem de cada estudante seja original e independente.<br/>

		<b>Uma �nica discuss�o simples:</b> 
		este tipo de f�rum possui apenas um t�pico que � criado automaticamente e n�o pode ser exclu�do. Ele � recomendado para organizar discuss�es breves com foco em um tema �nico e preciso.<br/>		
	</div>


	<li>
		<label class="required" for="titulo">T�tulo:<span class="required"></span></label> 
		<h:inputText id="titulo" size="59"	value="#{ forumBean.obj.titulo }" title="T�tulo" />
	</li>
	<li>
		<label class="required" for="descricao">Descri��o:<span class="required"></span></label> 
		<h:inputTextarea value="#{ forumBean.obj.descricao }" cols="20" rows="20" id="descricao" title="Descri��o"/>
	</li>
	<li>
		<label for="monitorarLeitura" class="required">Monitorar Leitura:<span class="required"></span></label>
		<h:selectOneRadio id="monitorarLeitura" value="#{ forumBean.obj.monitorarLeitura }" title="Monitorar Leitura">
			<f:selectItems value="#{ forumBean.simNao }" />
		</h:selectOneRadio>
		<span class="descricao-campo"></span>
	</li>
	<li>
		<label for="tipo" class="required">Tipo de F�rum:<span class="required"></span></label>
		<h:selectOneMenu id="tipo" value="#{ forumBean.obj.tipo.id }" title="Tipo" rendered="#{ forumBean.obj.id == 0}">
			<f:selectItems value="#{ tipoForumBean.allCombo }" />
		</h:selectOneMenu>
		<h:outputText id="txtTipo" value="#{ forumBean.obj.tipo.descricao }" />
		<span class="descricao-campo"></span>
	</li>
	<li>
		<label for="ordem" class="required">Ordem Padr�o:<span class="required"></span></label>
		<h:selectOneMenu id="ordem" value="#{ forumBean.obj.ordenacaoPadrao.id }" title="Ordem">
			<f:selectItems value="#{ ordemMensagensForumBean.allCombo }" />
		</h:selectOneMenu>
		<span class="descricao-campo"></span>
	</li>
	
	<li>
		<label for="arquivo">Arquivo:</label>
		<t:inputFileUpload id="uFile" value="#{ forumBean.obj.arquivo }" storage="file" size="50"/>
	</li>


<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "620", height : "250", language : "pt",
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
	theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>