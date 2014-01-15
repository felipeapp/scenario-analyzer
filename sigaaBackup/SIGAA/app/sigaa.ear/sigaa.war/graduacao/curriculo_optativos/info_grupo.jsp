
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> &gt; Grupo de Optativas &gt; Criar Novo Grupo </h2>

<f:view>

<h:form>


<table class="formulario" width="100%">
	<caption>Dados do Grupo</caption>
	<tr>
		<th><strong>Descrição:</strong></th>
		<td><h:outputText value="#{grupoOptativasMBean.obj.descricao}" /></td>
	</tr>
	<tr>
		<th width="250"><strong>Componente Curricular Associado:</strong></th>
		<td><h:outputText value="#{grupoOptativasMBean.obj.componente.descricaoResumida}" /></td>
	</tr>
	<tr>
		<th><strong>CH Mínima:</strong></th>
		<td><h:outputText value="#{grupoOptativasMBean.obj.chMinima}" /></td>
	</tr>
	<tr>
		<th><strong>CH Total:</strong></th>
		<td><h:outputText value="#{grupoOptativasMBean.obj.chTotal}" /></td>
	</tr>
	<tr>
		<td colspan="2">
			<table class="subFormulario" width="100%">
			<caption>Componentes do Grupo</caption>
			<thead>
			<tr><th>Componente</th><th style="text-align: right;">Carga Horária</th></tr>
			</thead>
			<c:forEach var="cc" items="#{ grupoOptativasMBean.obj.componentes }">
			<tr><td>${ cc.componente.codigoNome }</td><td align="right">${ cc.componente.detalhes.chTotal }h</td></tr>
			</c:forEach>
			</table>
		</td>
	</tr>
	<tfoot>
	<tr><td colspan="2">
		<h:commandButton value="<< Voltar" action="#{ grupoOptativasMBean.telaGrupos }" id="back"/>
		<h:commandButton value="Cancelar" action="#{ grupoOptativasMBean.cancelar }" onclick="if (!confirm('Deseja realmente cancelar a operação?')) { return false; }" id="cancelarOperacao"/>
	</td></tr>
	</tfoot>
</table>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>