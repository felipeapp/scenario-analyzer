<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
table.visualizacao tr td.subFormulario {
	padding: 3px 0 3px 20px;
}
p.corpo {
	padding: 2px 8px 10px;
	line-height: 1.2em;
}
</style>

<f:view>
<h2> <ufrn:subSistema /> &gt; Projeto de Infra-Estrutura em Pesquisa </h2>

<h:form id="formResumo" enctype="multipart/form-data">
<c:set var="projeto" value="${ projetoInfraPesq.obj }" />

	<!-- dados do projeto -->
    <table class="visualizacao" align="center" style="width: 100%">
    <caption>Resumo do Projeto</caption>
	<tbody>
    	<%@include file="/pesquisa/projeto_infraestrutura/include/resumo_projeto.jsp"%>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2" style="text-align: center; background: #C8D5EC; padding: 3px;">
				<a href="javascript:history.back();"> Voltar </a>
			</td>
		</tr>
	</tfoot>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
