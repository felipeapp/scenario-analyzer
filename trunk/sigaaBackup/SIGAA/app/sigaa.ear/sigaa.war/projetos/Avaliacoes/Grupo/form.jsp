<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<a4j:keepAlive beanName="grupoAvaliacao"></a4j:keepAlive>
	<h2><ufrn:subSistema /> &gt; Grupo de Avaliação</h2>
	<br />

	<table class=formulario width="50%">
		<h:form>
			<caption class="listagem">Cadastro de Grupos de Avaliações</caption>
			<h:inputHidden value="#{grupoAvaliacao.confirmButton}" />
			<h:inputHidden value="#{grupoAvaliacao.obj.id}" />
			<tr>
				<th class="obrigatorio">Descrição:</th>
				<td><h:inputText size="80" maxlength="200"
					readonly="#{grupoAvaliacao.readOnly}"  value="#{grupoAvaliacao.obj.descricao}" /></td>
			</tr>
			
			<tfoot>
				<tr>
					<c:if test="${grupoAvaliacao.confirmButton == 'Alterar' }" >
						<td colspan="2"><h:commandButton value="Alterar" action="#{grupoAvaliacao.alterarGrupo}" />
					</c:if>
					<c:if test="${grupoAvaliacao.confirmButton == 'Cadastrar'}">
						<td colspan="2"><h:commandButton value="Cadastrar" action="#{grupoAvaliacao.cadastrarGrupo}" />
					</c:if>
					 <h:commandButton value="Cancelar" onclick="#{confirm}" action="#{grupoAvaliacao.cancelarGrupo}" />
					 </td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br>
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
