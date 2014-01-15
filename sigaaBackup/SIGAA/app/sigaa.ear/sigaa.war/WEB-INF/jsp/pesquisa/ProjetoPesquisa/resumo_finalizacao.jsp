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

<h2> <ufrn:steps/> &gt; Finalização de Projeto de Pesquisa </h2>

<c:set var="projeto" value="${ projetoPesquisaForm.obj }" />
<html:form action="/pesquisa/projetoPesquisa/criarProjetoPesquisa" method="post">
	<!-- dados do projeto -->
    <table class="visualizacao" align="center" style="width: 100%">
    <caption>Dados do Projeto de Pesquisa</caption>
	<tbody>
    	<%@include file="/WEB-INF/jsp/pesquisa/ProjetoPesquisa/include/resumo_projeto.jsp"%>

	</tbody>
	<tfoot>
		<tr>
			<td colspan="2" style="text-align: center; background: #C8D5EC; padding: 3px;">
	    		<html:button dispatch="finalizar" value="Confirmar Finalização"/>
	    		<html:button dispatch="cancelar" value="Cancelar"/>
			</td>
		</tr>
	</tfoot>
	</table>
</html:form>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
