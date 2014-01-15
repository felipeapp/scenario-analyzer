<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2><ufrn:subSistema /> > Remover Projeto</h2>
<h:messages showDetail="true" showSummary="true"/>

<h:form id="form">

<table class=formulario width="100%">
	<caption class="listagem">Dados da Ação Acadêmica Associada</caption>
	
	<%@include file="/projetos/ProjetoBase/dados_projeto.jsp"%>
	
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton value="Confirmar Remoção" action="#{projetoBase.removerCiepe}" id="btConfirmarRemocao"/>
			<h:commandButton value="Cancelar" action="#{projetoBase.cancelar}" onclick="#{confirm}" id="btCancelar"/>
		</td>
	</tr>
	</tfoot>
</table>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>