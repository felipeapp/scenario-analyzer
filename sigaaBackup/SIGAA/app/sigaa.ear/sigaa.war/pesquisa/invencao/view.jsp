<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

	<h2>Visualiza��o da Inven��o</h2>
	<br>

	<table class="tabelaRelatorio" width="100%">
	<caption>Dados da Inven��o</caption>

		<tbody>
	
			<%@include file="/pesquisa/invencao/include/dados_invencao.jsp"%>
		
		</tbody>
	
	</table>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
