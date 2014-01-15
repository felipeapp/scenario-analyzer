<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
#abas-descricao .aba {
	padding: 10px;
}

p.ajuda {
	text-align: center;
	margin: 5px 90px;
	color: #083772;
	font-style: italic;
}
div#msgAddProcessos{
	background:#FFF none repeat scroll 0 0;
	border:1px solid #FFDFDF;
	color:#FF1111;
	line-height:1.2em;
	padding:10px;
	width:95%;
}
</style>

<f:view>
<h2><ufrn:subSistema /> > Informações do ${cursoEventoExtensao.obj.tipoAtividadeExtensao.descricao }</h2>

<div class="descricaoOperacao">
	<table width="100%">
		<tr>
			<td width="50%">
			Nesta tela devem ser informados os dados adicionais de uma Ação.
			</td>
			<td>
				<%@include file="passos_atividade.jsp"%>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<p><strong>OBSERVAÇÃO:</strong> Os dados informados só são cadastrados na base de dados quando clica-se em "Avançar >>".</p> 
			</td>
		</tr>
	</table>
</div>

<h:form id="formCursoEvento">

<table class=formulario width="95%">
	<caption class="listagem">Informe os dados complementares do ${cursoEventoExtensao.obj.tipoAtividadeExtensao.descricao}</caption>

	<c:if test="${cursoEventoExtensao.curso}">
		<tr>
			<th  class="required"> Modalidade do Curso:</th>
			<td colspan="3">
				<h:selectOneMenu id="modalidadeEducacao"
					value="#{cursoEventoExtensao.obj.cursoEventoExtensao.modalidadeEducacao.id}"
					readonly="#{cursoEventoExtensao.readOnly}" style="width: 70%;">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE A MODALIDADE DO CURSO --"/>
					<f:selectItems value="#{modalidadeEducacao.allCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th  class="required"> Tipo do Curso:</th>
			<td colspan="3">
				<h:selectOneMenu id="tipoCurso"
					value="#{cursoEventoExtensao.obj.cursoEventoExtensao.tipoCursoEvento.id}"
					readonly="#{cursoEventoExtensao.readOnly}" style="width: 70%;">

					<f:selectItem itemValue="0" itemLabel="-- SELECIONE A CLASSIFICAÇÃO DO CURSO --"/>
					<f:selectItems value="#{tipoCursoEventoExtensao.allCursoCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>
	</c:if>

	<c:if test="${cursoEventoExtensao.evento}">
		<tr>
			<th  class="required"> Tipo do Evento:</th>
			<td colspan="3">
				<h:selectOneMenu id="tipoEvento"
					value="#{cursoEventoExtensao.obj.cursoEventoExtensao.tipoCursoEvento.id}"
					readonly="#{atividadeExtensao.readOnly}" style="width: 70%;">

					<f:selectItem itemValue="0" itemLabel="-- SELECIONE O TIPO DO EVENTO --"/>
					<f:selectItems value="#{tipoCursoEventoExtensao.allEventoCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>
	</c:if>

	<tr>
		<th width="35%"  class="required"> Carga Horária:</th>
		<td colspan="3">
			<h:inputText id="cargaHoraria" value="#{cursoEventoExtensao.obj.cursoEventoExtensao.cargaHoraria}" size="4" maxlength="4"
				readonly="#{cursoEventoExtensao.readOnly}" style="text-align: right" onkeyup="formatarInteiro(this)" />
			horas
		</td>
	</tr>

	<tr>
		<th  class="required"> Previsão de Nº de Vagas Oferecidas:</th>
		<td colspan="3">
			<h:inputText id="numeroVagas" value="#{cursoEventoExtensao.obj.cursoEventoExtensao.numeroVagas}" size="4" maxlength="4"
			 	readonly="#{cursoEventoExtensao.readOnly}" style="text-align: right" onkeyup="formatarInteiro(this)"/>
			vagas
		</td>
	</tr>

	<tr>
		<td colspan="4" class="subFormulario">Outras Informações</td>
	</tr>
	<tr>
		<td colspan="6">
		<div id="abas-descricao">
			<div id="descricao-resumo" class="aba">
				<i class="required">Utilize o espaço abaixo para colocar o resumo.</i><br/>
				<h:inputTextarea id="resumo" value="#{cursoEventoExtensao.obj.cursoEventoExtensao.resumo}" rows="8" style="width:97%;"/>
			</div>
			<div id="descricao-programacao" class="aba">
				<i class="required">Utilize o espaço abaixo para colocar a programação.</i><br/>
				<h:inputTextarea id="programacao" value="#{cursoEventoExtensao.obj.cursoEventoExtensao.programacao}" rows="8" style="width:97%;"/>
			</div>
			<div id="objetivosGerais" class="aba">
				<i class="required">Objetivos Gerais.</i><br/>
				<h:inputTextarea id="objetivosGerais" value="#{cursoEventoExtensao.obj.projeto.objetivos}" rows="10" style="width:99%" />
			</div>
			<div id="resultados" class="aba">
				<i class="required">Resultados Esperados.</i><br/>
				<h:inputTextarea id="resultados" value="#{cursoEventoExtensao.obj.projeto.resultados}" rows="10" style="width:99%" />
			</div>
		</div>
		</td>
	</tr>

	<tfoot>
	<tr>
		<td colspan="4">
			<h:commandButton value="<< Voltar" action="#{atividadeExtensao.passoAnterior}" id="btVoltar"/>
			<h:commandButton value="Cancelar" action="#{atividadeExtensao.cancelar}" id="btCancelar" onclick="#{confirm}"/>
			<h:commandButton value="Avançar >>" action="#{cursoEventoExtensao.submeterDadosCursoEvento}" id="btAvancar"/>
		</td>
	</tr>
	</tfoot>
</table>
</h:form>
	<br />
	<center><h:graphicImage url="/img/required.gif"
		style="vertical-align: top;" /> <span class="fontePequena">
	Campos de preenchimento obrigatório. </span></center>
	<br />
</f:view>

<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script>
	var Abas = {
		    init : function(){
		        var abas = new YAHOO.ext.TabPanel('abas-descricao');
		        abas.addTab('descricao-resumo', "Resumo");
		        abas.addTab('descricao-programacao', "Programação");
		        abas.addTab('objetivosGerais', "Objetivos Gerais");
		        abas.addTab('resultados', "Resultados Esperados");
			    abas.activate('descricao-resumo');
		    }
		};
		YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);

	tinyMCE.init({
		mode : "textareas", theme : "advanced", width : "100%", height : "250", language : "pt",
		theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
		theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
		theme_advanced_buttons3 : "",
		plugins : "searchreplace,contextmenu,advimage",
		theme_advanced_toolbar_location : "top",
		theme_advanced_toolbar_align : "left"
	});

</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>