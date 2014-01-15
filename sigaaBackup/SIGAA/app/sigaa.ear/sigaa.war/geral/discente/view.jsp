<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.formulario th{
		font-weight: bold;
	}
</style>


<f:view>
	<h2> <ufrn:subSistema /> &gt; Dados Pessoais do Discente </h2>

	<%@include file="/geral/discente/dados_pessoais.jsp" %>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>