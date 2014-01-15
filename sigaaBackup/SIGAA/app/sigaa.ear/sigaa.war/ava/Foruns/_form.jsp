
	<div class="descricaoOperacao">
		<b>Tipos de Fórum</b>
		<br />
		<br />

		<b>Cada usuário inicia apenas UM NOVO tópico:</b> 
		cada participante pode abrir apenas um novo tópico de discussão, mas todos podem responder livremente as mensagens, sem limites de quantidades. 
		Este formato é usado, por exemplo, nas atividades em que cada participante apresenta um tema a ser discutido e atua como moderador da discussão deste tema.<br/>

		<b>Fórum geral:</b>
		é um fórum aberto, onde todos os participantes podem iniciar um novo tópico de discussão quando quiserem.<br/>

		<b>Fórum P & R (perguntas e respostas):</b>
		neste fórum um estudante pode ler as mensagens de outros somente após a publicação de sua mensagem. 
		Depois disto pode também responder às mensagens do grupo. Isto permite que a primeira mensagem de cada estudante seja original e independente.<br/>

		<b>Uma única discussão simples:</b> 
		este tipo de fórum possui apenas um tópico que é criado automaticamente e não pode ser excluído. Ele é recomendado para organizar discussões breves com foco em um tema único e preciso.<br/>		
	</div>


	<li>
		<label class="required" for="titulo">Título:<span class="required"></span></label> 
		<h:inputText id="titulo" size="59"	value="#{ forumBean.obj.titulo }" title="Título" />
	</li>
	<li>
		<label class="required" for="descricao">Descrição:<span class="required"></span></label> 
		<h:inputTextarea value="#{ forumBean.obj.descricao }" cols="20" rows="20" id="descricao" title="Descrição"/>
	</li>
	<li>
		<label for="monitorarLeitura" class="required">Monitorar Leitura:<span class="required"></span></label>
		<h:selectOneRadio id="monitorarLeitura" value="#{ forumBean.obj.monitorarLeitura }" title="Monitorar Leitura">
			<f:selectItems value="#{ forumBean.simNao }" />
		</h:selectOneRadio>
		<span class="descricao-campo"></span>
	</li>
	<li>
		<label for="tipo" class="required">Tipo de Fórum:<span class="required"></span></label>
		<h:selectOneMenu id="tipo" value="#{ forumBean.obj.tipo.id }" title="Tipo" rendered="#{ forumBean.obj.id == 0}">
			<f:selectItems value="#{ tipoForumBean.allCombo }" />
		</h:selectOneMenu>
		<h:outputText id="txtTipo" value="#{ forumBean.obj.tipo.descricao }" />
		<span class="descricao-campo"></span>
	</li>
	<li>
		<label for="ordem" class="required">Ordem Padrão:<span class="required"></span></label>
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