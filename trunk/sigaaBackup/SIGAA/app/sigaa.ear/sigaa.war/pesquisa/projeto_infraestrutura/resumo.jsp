<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
table.visualizacao tr td.subFormulario {
	padding: 3px 0 3px 20px;
}
p.corpo {
	padding: 2px 8px 10px;
	line-height: 1.2em;
}

.linhaImpar {
	background-color: #EDF1F8;
}
</style>

<f:view>
<h2> <ufrn:subSistema /> &gt; Projeto de Infra-Estrutura em Pesquisa </h2>

<h:form id="formResumo" enctype="multipart/form-data" prependId="false">
<c:set var="projeto" value="${ projetoInfraPesq.obj }" />

	<!-- dados do projeto -->
    <table class="visualizacao" align="center" style="width: 100%">
    <caption>Resumo do Projeto</caption>
	<tbody>
    	<%@include file="/pesquisa/projeto_infraestrutura/include/resumo_projeto.jsp"%>

 		<c:if test="${not projetoInfraPesq.readOnly}">
			<tr>
				<td colspan="2" class="subFormulario"> Arquivo do Projeto </td>
			</tr>
			<tr>
				<td style="height: 35px;text-align:right"> ${novo} Arquivo: </td>
				<td><t:inputFileUpload id="uFile" value="#{projetoInfraPesq.file}" storage="file" size="70"/> 
					<ufrn:help>Você poderá <i>(opcionalmente)</i> submeter um arquivo contendo os dados do 
								projeto para ser armazenado no sistema.</ufrn:help>
				</td>
			</tr>
		</c:if>

	</tbody>
	<tfoot>
		<tr>
			<td colspan="2" style="text-align: center; background: #C8D5EC; padding: 3px;">
				<h:commandButton value="#{projetoInfraPesq.confirmButton}" action="#{projetoInfraPesq.cadastrar}" id="cadastrar"/>
				<h:commandButton value="Cancelar" action="#{projetoInfraPesq.cancelar}" onclick="#{confirm}" id="cancelar"/>
			<c:if test="${not projetoInfraPesq.readOnly}">
				<h:commandButton value="<< Dados Gerais" action="#{projetoInfraPesq.telaDadosGerais}" id="dadosGerais"/>
				<h:commandButton value="<< Sub-Projetos" action="#{projetoInfraPesq.telaSubProjetos}" id="subProjetos"/>
			</c:if>
			</td>
		</tr>
	</tfoot>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
