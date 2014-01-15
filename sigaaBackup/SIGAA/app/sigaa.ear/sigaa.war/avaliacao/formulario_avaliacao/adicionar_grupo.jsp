<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	
	<h2 class="title"><ufrn:subSistema /> &gt; Formulário de Avaliação Institucional &gt; Adicionar Grupo de Perguntas</h2>

	<h:form id="form">
	
	<table class="formulario" width="80%">
		<caption>Informe os Dados para o Grupo de Perguntas</caption>
		<tbody>
			<tr>
				<th class="required" width="35%">Descrição da Dimensão:</th>
				<td><h:inputText value="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.grupo.descricao }" id="descricao" size="80" maxlength="180"/></td>
			</tr>
			<tr>
				<th><h:selectBooleanCheckbox value="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.grupo.avaliaTurmas }" id="avaliaTurmas"/></th>
				<td>As perguntas desta dimensão avaliam turmas</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="#{cadastrarFormularioAvaliacaoInstitucionalMBean.confirmButton}" id="adicionar" action="#{cadastrarFormularioAvaliacaoInstitucionalMBean.adicionaGrupo}" />
					<h:commandButton value="<< Voltar" id="voltar" action="#{cadastrarFormularioAvaliacaoInstitucionalMBean.formCadastro}" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" id="cancelar" action="#{cadastrarFormularioAvaliacaoInstitucionalMBean.cancelar}" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
	<br />
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		<br />
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
