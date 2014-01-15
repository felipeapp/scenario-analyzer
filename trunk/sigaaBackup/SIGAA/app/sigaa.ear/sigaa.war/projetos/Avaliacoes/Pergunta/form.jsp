<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> &gt; Pergunta Avaliação</h2>
	<a4j:keepAlive beanName="grupoAvaliacao" />
	<br />

	<table class=formulario width="50%">
		<h:form>
			<caption class="listagem">Cadastro de Pergunta de Avaliação</caption>
			<h:inputHidden value="#{grupoAvaliacao.confirmButton}" />
			
			<tr>
				<th class="obrigatorio">Descrição:</th>
				<td><h:inputText size="80" maxlength="200"
					readonly="#{grupoAvaliacao.readOnly}"  value="#{grupoAvaliacao.pergunta.descricao}" /></td>
			</tr>
			
			<tfoot>
				<tr>
					<c:if test="${grupoAvaliacao.confirmButton == 'Alterar' }" >
						<td colspan="2"><h:commandButton value="Alterar" action="#{grupoAvaliacao.alterarPergunta}" />
					</c:if>
					<c:if test="${grupoAvaliacao.confirmButton == 'Cadastrar'}">
						<td colspan="2"><h:commandButton value="Cadastrar" action="#{grupoAvaliacao.cadastrarPergunta}" />
					</c:if>
					 <h:commandButton value="Cancelar" onclick="#{confirm}" action="#{grupoAvaliacao.cancelarPergunta}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br>
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
