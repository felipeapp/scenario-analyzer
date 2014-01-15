<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<c:if test="${sessionScope.acesso.chefeDepartamento }">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>
	<h2 class="title"><ufrn:subSistema /> &gt; Programa de Componentes Curriculares &gt; Dados</h2>

	<table class="visualizacao">
		<tr>
			<th width="30%">Componente Curricular:</th>
			<td>${programaComponente.obj.componenteCurricular.codigo} -	${programaComponente.obj.componenteCurricular.nome}</td>
		</tr>
		<tr>
			<th>Cr�ditos:</th>
			<td>${programaComponente.obj.componenteCurricular.crTotal} cr�ditos</td>
		</tr>
		<tr>
			<th>Carga Hor�ria:</th>
			<td>${programaComponente.obj.componenteCurricular.chTotal} horas</td>
		</tr>
		<tr>
			<th>Unidade Respons�vel:</th>
			<td>${programaComponente.obj.componenteCurricular.unidade.nome}</td>
		</tr>
		<tr>
			<th>Tipo do Componente:</th>
			<td>${programaComponente.obj.componenteCurricular.tipoComponente.descricao}</td>
		</tr>
		<tr>
			<th>Ementa:</th>
			<td>${programaComponente.obj.componenteCurricular.detalhes.ementa}</td>
		</tr>
	</table>
	
	<div class="descricaoOperacao">
		<p>Caro Usu�rio,</p>
		<p>Estas informa��es ser�o disponibilizadas publicamente e � de sua responsabilidade caso o aluno 
			utilize-as para processo de aproveitamento.</p>
	</div>
	
	<br />
	<h:form id="form">
		<table class="formulario" width="680px">
			<caption>Dados do Programa</caption>
			<tr>
				<th width="30%" class="obrigatorio">Ano-Per�odo:</th>
				<td>
					<h:inputText value="#{programaComponente.obj.ano}" disabled="true" maxlength="4" size="4" id="ano" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
					-
					<h:inputText value="#{programaComponente.obj.periodo}" disabled="true" maxlength="1" size="1" id="periodo" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
				</td>
			</tr>
			<tr>
				<th width="30%" class="obrigatorio">Quantidade de Avalia��es:</th>
				<td><h:selectOneMenu value="#{programaComponente.obj.numUnidades}" id="qtdAvaliacoes">
						<f:selectItems value="#{programaComponente.qtdAvaliacoes}" />
					</h:selectOneMenu> 
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<c:set value="true" var="editar" />
					<%@include file="/graduacao/componente_programa/programa_dados.jsp" %>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{programaComponente.confirmButton}" action="#{programaComponente.cadastrar}" 
								id="${programaComponente.confirmButton}" />
						<h:commandButton value="<< Selecionar Outro Componente"	action="#{programaComponente.telaBuscaComponentes}" 
								id="selecionarOutro" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{programaComponente.cancelar}" id="cancelar" /> 
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br />
	<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>
	
<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('abas-objetivo');
        abas.addTab('objetivo-selecao', "Objetivos");
        abas.addTab('conteudo-selecao', "Conte�do");
        abas.addTab('habilidade-selecao', "Compet�ncias e Habilidades");
        abas.activate('objetivo-selecao');
    }
};
YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
</script>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>