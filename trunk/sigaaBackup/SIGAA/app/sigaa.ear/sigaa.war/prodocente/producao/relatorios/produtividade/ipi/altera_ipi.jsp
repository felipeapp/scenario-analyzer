<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> > Alteração Ipi</h2>
<style>
table th { font-weight: bold !important; }
</style>
	<h:form id="form">
	<c:set var="dirBase" value="/prodocente/producao/" scope="session"/>
	

	<h:outputText value="#{classificacaoRelatorio.create}" />
	<h:inputHidden value="#{classificacaoRelatorio.emissaoRelatorio.id}" />
  	<input type="hidden" name="id" id="id" value="${classificacaoRelatorio.emissaoRelatorio.classificacaoRelatorio.id}"/>
  	
		<table align="center" class="formulario" width="80%">
			<caption>Alterar índice de Produtividade Individual</caption>
			<tr>
				<th>Docente:</th>
				<td><h:outputText value="#{classificacaoRelatorio.emissaoRelatorio.servidor.nome}" /></td>
			</tr>
			<tr>
				<th>Departamento:</th>
				<td><h:outputText value="#{classificacaoRelatorio.emissaoRelatorio.servidor.unidade.siglaNome}" /></td>
			</tr>
			<tr>
				<th>IPI Original:</th>
				<td>
				   <c:if test="${empty classificacaoRelatorio.emissaoRelatorio.ipiOriginal or classificacaoRelatorio.emissaoRelatorio.ipiOriginal <=0}">
				   	<h:outputText value="#{classificacaoRelatorio.emissaoRelatorio.ipi}" />
				   </c:if>
				   <c:if test="${ not empty classificacaoRelatorio.emissaoRelatorio.ipiOriginal and classificacaoRelatorio.emissaoRelatorio.ipiOriginal >0}">
				      <h:outputText value="#{classificacaoRelatorio.emissaoRelatorio.ipiOriginal}" />
				   </c:if>
				</td>
			</tr>
			<tr>
				<th>Novo IPI:</th>
				<td> 
					<h:inputText size="8"  value="#{classificacaoRelatorio.emissaoRelatorio.ipi}" onkeypress="return ApenasNumeros(event);" />
				</td>
			</tr>
			<tr>
				<td class="subFormulario" colspan="2" style="text-align: center">Observações</td>
			</tr>
			<tr>
				<td colspan="2">
					<h:inputTextarea rows="15" value="#{classificacaoRelatorio.emissaoRelatorio.motivoAlteracaoIPI}" style="width: 95%; margin: 5px 10px;"/>
				</td>
			</tr>
			<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton id="gerarRelatorio" value="Cadastrar" action="#{classificacaoRelatorio.alterarIPI}" />
					<h:commandButton id="voltar" value="<< Voltar" action="#{classificacaoRelatorio.redirectPreAlterarIPI}" />
					<h:commandButton id="cancelar" value="Cancelar" action="#{classificacaoRelatorio.consultar}" />
				</td>
			</tr>
			</tfoot>
		</table>
	</h:form>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>