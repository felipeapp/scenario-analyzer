<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2> <ufrn:subSistema /> &gt; Declaração de Invenção</h2>

<h:form id="form">
	<h:inputHidden value="#{invencao.confirmButton}" />
	<h:inputHidden value="#{invencao.obj.id}" />

	<table class="formulario" width="100%">
		<caption class="listagem">Resumo da Invenção</caption>
		
		<%@include file="/pesquisa/invencao/include/dados_invencao.jsp"%>
		
											
		<tfoot>
		<tr>
			<td colspan="4">
				<h:commandButton value="Remover" action="#{invencao.remover}" id="btRemover"/> 
				<h:commandButton value="Cancelar" action="#{invencao.cancelar}" onclick="#{confirm}" immediate="true" id="btCancelar"/>
			</td>
		</tr>
		</tfoot>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
