	<table class="formAva">	
		<tr>
			<th id="nomeTh" class="required">Nome ou Título:</th>
			<td>
				<h:inputText value="#{indicacaoReferencia.object.descricao}" size="59" maxlength="255" id="nome"/>
			</td>
		</tr>
		<tr>

				<th id="tipoTh" class="required"> Tipo:</th>
				<td>
					<h:selectOneMenu id="tipo" value="#{indicacaoReferencia.object.tipo}" onclick="exibirPesquisa(this, 'formReferencia\\\:tipo');">
						<f:selectItem itemLabel="Livro" itemValue="L"/>
						<f:selectItem itemLabel="Artigo" itemValue="A"/>
						<f:selectItem itemLabel="Revista" itemValue="R"/>
						<f:selectItem itemLabel="Site" itemValue="S"/>
						<f:selectItem itemLabel="Outros" itemValue="O"/>
					</h:selectOneMenu>
				</td>
		</tr>	
		<tr id="linhaPesquisarTitulo">
			<td colspan="2">																											
				<span class="botao-pequeno" style="margin-left:19%;">			
					<h:commandLink style="background-image: url(/sigaa/img/buscar.gif);"  action="#{indicacaoReferencia.pesquisarNoAcervo}" value="Pesquisar no Acervo" />						
				</span>	
				<ufrn:help>Caso deseje selecionar um livro do acervo da biblioteca. Se o livro for selecionado serão exibidos seus dados,
							além das bibliotecas da instituição em que o livro se encontra e se ele está disponível ou emprestado.</ufrn:help>				
			</td>									
		</tr>
		<c:if test="${ indicacaoReferencia.object.livro && not empty indicacaoReferencia.object.tituloCatalografico }">
			<tr id="dadosLivro">
				<td colspan="2">
				<table class="formulario" style="width: 72%;margin-left:16%;">
					<caption>Dados do Livro</caption>
					<tBody>
					<tr><td><span>Título: </span></td><td><h:outputText value="#{indicacaoReferencia.titulo.titulo}" /></td></tr>
					<tr><td><span>Autor: </span></td><td><h:outputText value="#{indicacaoReferencia.titulo.autor}" /> </td></tr>
					<tr><td><span>Editora: </span></td><td><h:outputText value="#{indicacaoReferencia.titulo.editora}" /></td></tr>
					<tr><td><span>Ano: </span></td><td><h:outputText value="#{indicacaoReferencia.titulo.ano}" /> </td></tr>
					<tr><td><span>Edição: </span></td><td><h:outputText value="#{indicacaoReferencia.titulo.edicao}" /> </td></tr>
					</tBody>
				</table>
				</td>				
			</tr>
		</c:if>
		<tr id="urlTh">
			<th class="required">Endereço (URL):</th>
			<td>
				<h:inputText value="#{indicacaoReferencia.object.url}" size="59" maxlength="255" id="url"/>
			</td>
		</tr>
		<tr>
			<th id="topicoTh">Tópico de Aula: </th>
			<td>
				<h:selectOneMenu value="#{indicacaoReferencia.object.aula.id}" id="topico">
					<f:selectItem itemValue="0" itemLabel=""/>
					<f:selectItems value="#{topicoAula.comboIdentado}" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th>Detalhes</th>
			<td>
				<h:inputTextarea cols="70" rows="5" value="#{indicacaoReferencia.object.detalhes}"/>
			</td>
		</tr>
		
		<c:if test="${ not empty turmaVirtual.turmasSemestrePermissaoDocente && indicacaoReferencia.object.id == 0 }">	
		<tr>
			<th class="required">Criar em:</th>
			<td>
				<t:selectManyCheckbox value="#{ indicacaoReferencia.cadastrarEm }" layout="pageDirection">
					<t:selectItems var="ts" itemLabel="#{ ts.descricaoSemDocente }" itemValue="#{ ts.id }" value="#{ turmaVirtual.turmasSemestrePermissaoDocente }"/>
				</t:selectManyCheckbox>
			</td>
		</tr>
		</c:if>
		
		<tr>
			<th id="notificacaoTh">Notificação: </th>
			<td>
				<h:selectBooleanCheckbox id="notificacao" value="#{ indicacaoReferencia.object.notificarAlunos }" />
				<span class="texto-ajuda">(Notificar os alunos por e-mail)</span> 
			</td>
		</tr>
	</table>
	
<script type="text/javascript">

function exibirPesquisa(select, idSelect) {

	var auxTipo = "${indicacaoReferencia.object.tipo}";
	var divPesquisa = jQuery("#linhaPesquisarTitulo");
	var dadosLivro = jQuery("#dadosLivro");
	var isSite = jQuery("#urlTh");
	
	if (select == null) {
		select = jQuery("#"+idSelect);
		select.val(auxTipo);
	} else
		auxTipo = select.value;
	
	 if (auxTipo == "L") {
		divPesquisa.show();
		if ( dadosLivro != null ) dadosLivro.show();
	 } else {
		divPesquisa.hide(); 
		if ( dadosLivro != null ) dadosLivro.hide();
	 }

	 if (auxTipo == "S")
		 isSite.show();
	 else
		 isSite.hide();
}
exibirPesquisa(null, 'formAva\\:tipo');
</script>

<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
<c:if test="${turmaVirtual.acessoMobile == false}">
	tinyMCE.init({
		mode : "textareas", theme : "advanced", width : "460", height : "250", language : "pt",
		theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
		theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
		theme_advanced_buttons3 : "",
		plugins : "searchreplace,contextmenu,advimage",
		theme_advanced_toolbar_location : "top",
		theme_advanced_toolbar_align : "left"
	});
</c:if>
</script>
