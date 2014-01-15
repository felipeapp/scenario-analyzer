<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="tipoParticipacaoAcaoExtensao"></a4j:keepAlive>
	<h2><ufrn:subSistema /> > Tipo Participação Ação de Extensão </h2>
	<h:form id="form">
		<table class="formulario" width="80%">
			<caption>Cadastro de Tipo Participação Ação de Extensão</caption>
			<tr>
				<th class="obrigatorio">Tipo de Atividade de Extensão:</th>
				<td><h:selectOneMenu
					value="#{ tipoParticipacaoAcaoExtensao.obj.tipoAcaoExtensao.id }"
					readonly="#{tipoParticipacaoAcaoExtensao.readOnly}"
					disabled="#{tipoParticipacaoAcaoExtensao.readOnly}" id="tipoParticipacao">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
					<f:selectItems value="#{ tipoParticipacaoAcaoExtensao.allTiposAtividades }" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="obrigatorio">Descrição:</th>
				<td><h:inputText value="#{ tipoParticipacaoAcaoExtensao.obj.descricao }"
					readonly="#{tipoParticipacaoAcaoExtensao.readOnly}"
					disabled="#{tipoParticipacaoAcaoExtensao.readOnly}" maxlength="80" id="descricao"/></td>
			</tr>
			<tfoot>
				<tr>
				<td colspan="2">
					<h:inputHidden value="#{tipoParticipacaoAcaoExtensao.obj.id}" /> 
					<h:commandButton value="#{tipoParticipacaoAcaoExtensao.confirmButton}" action="#{tipoParticipacaoAcaoExtensao.cadastrar}" /> 
					<h:commandButton value="<< Voltar" action="#{tipoParticipacaoAcaoExtensao.listar}" immediate="true" rendered="#{tipoParticipacaoAcaoExtensao.listagem}" />
					<h:commandButton value="Cancelar" action="#{tipoParticipacaoAcaoExtensao.cancelar}" onclick="#{confirm}" immediate="true" />
				</td>
				</tr>
			</tfoot>
		</table>
		<br />
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
