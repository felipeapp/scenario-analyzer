<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2> <ufrn:subSistema /> > 
		<h:outputText value="Cadastrar Docente" rendered="#{solicitacaoDocenteRedeMBean.cadastrar}"/>
		<h:outputText value="Alterar Docente" rendered="#{!solicitacaoDocenteRedeMBean.cadastrar}"/>
	</h2>
	
	<h:form id="formDiscenteStricto">
	${solicitacaoDocenteRedeMBean.verificarOperacao}
	<table class="formulario" style="width: 100%">
		<caption>Dados do Docente</caption>
		<tbody>
			<tr>
				<th style="width: 20%;" class="rotulo"> Nome: </th>
				<td>${solicitacaoDocenteRedeMBean.obj.docente.pessoa.nome}</td>
			</tr>
			<tr>
				<th style="width: 20%;" class="rotulo"> Instituição: </th>
				<td>${solicitacaoDocenteRedeMBean.obj.docente.dadosCurso.campus.instituicao.sigla} - ${solicitacaoDocenteRedeMBean.obj.docente.dadosCurso.campus.sigla}</td>
			</tr>		
			<tr>
				<th class="obrigatorio">Tipo: </th>
				<td>
					<c:if test="${solicitacaoDocenteRedeMBean.cadastrar}">
						<h:selectOneMenu value="#{solicitacaoDocenteRedeMBean.obj.docente.tipo.id}" id="status" style="width: 40%;">
							<f:selectItems value="#{tipoDocenteMBean.allCombo}" id="tipoDocenteCombo"/>
						</h:selectOneMenu>
					</c:if>
					<c:if test="${!solicitacaoDocenteRedeMBean.cadastrar}">
						<h:selectOneMenu value="#{solicitacaoDocenteRedeMBean.obj.tipoRequerido.id}" id="status" style="width: 40%;">
							<f:selectItems value="#{tipoDocenteMBean.allCombo}" id="tipoDocenteCombo"/>
						</h:selectOneMenu>
					</c:if>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Situação: </th>
				<td>
					<c:if test="${solicitacaoDocenteRedeMBean.cadastrar}">
						<h:outputText value="#{solicitacaoDocenteRedeMBean.obj.docente.situacao.descricao}"/>
					</c:if>
					<c:if test="${!solicitacaoDocenteRedeMBean.cadastrar}">
						<h:selectOneMenu value="#{solicitacaoDocenteRedeMBean.obj.situacaoRequerida.id}" id="situacao" style="width: 40%;">
							<f:selectItems value="#{situacaoDocenteRedeMBean.allCombo}" id="situacaoDocenteCombo"/>
						</h:selectOneMenu>
					</c:if>
				</td>
			</tr>		
			<tr>
				<th class="obrigatorio">Justificativa: </th>
				<td>
					<h:inputTextarea id="texto" style="width:90%" value="#{ solicitacaoDocenteRedeMBean.obj.observacao }"/>
				</td>
			</tr>		
		</tbody>
		<tfoot>
			<tr>	
				<td colspan="2">
					<h:commandButton value="#{solicitacaoDocenteRedeMBean.confirmButton }" id="cadastrar" action="#{ solicitacaoDocenteRedeMBean.cadastrarDocente }" rendered="#{solicitacaoDocenteRedeMBean.cadastrar}"/>	
					<h:commandButton value="#{solicitacaoDocenteRedeMBean.confirmButton }" id="alterar" action="#{ solicitacaoDocenteRedeMBean.alterarDocente }"  rendered="#{!solicitacaoDocenteRedeMBean.cadastrar}"/>	
					<h:commandButton value="<< Dados Pessoais" id="DadosPessoais" immediate="true" rendered="#{solicitacaoDocenteRedeMBean.obj.id==0 && solicitacaoDocenteRedeMBean.cadastrar}" action="#{docenteRedeMBean.telaDadosPessoais}" />
					<h:commandButton value="Cancelar" id="Cancelar" immediate="true" action="#{ solicitacaoDocenteRedeMBean.cancelar }" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
	</table>

	</h:form>
</f:view>

	<br>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	</center>
	
<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "620", height : "250", language : "en",
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
	theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>	

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>