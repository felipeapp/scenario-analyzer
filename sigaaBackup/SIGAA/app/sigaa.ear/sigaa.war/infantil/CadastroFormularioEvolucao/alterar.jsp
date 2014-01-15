<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2> <ufrn:subSistema /> &gt; Formulário de Evolução</h2>

<h:form id="form">
	<h:inputHidden value="#{cadastroFormularioEvolucaoMBean.confirmButton}" />
	<h:inputHidden value="#{cadastroFormularioEvolucaoMBean.obj.id}" />

	<table class="formulario" width="100%">
		<caption class="listagem">Resumo do Formulário de Evolução</caption>
		
		<%@include file="/infantil/CadastroFormularioEvolucao/include/dados_formulario.jsp"%>
											
		<tfoot>
		<tr>
			<td colspan="4">
				<h:commandButton value="Alterar" action="#{cadastroFormularioEvolucaoMBean.alterar}" id="alterarForm"/> 
				<h:commandButton value="Cancelar" action="#{cadastroFormularioEvolucaoMBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar"/>
			</td>
		</tr>
		</tfoot>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
