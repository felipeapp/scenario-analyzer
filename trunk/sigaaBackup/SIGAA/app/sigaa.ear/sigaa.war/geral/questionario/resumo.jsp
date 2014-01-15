<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link href="/sigaa/css/ensino/questionarios.css" rel="stylesheet" type="text/css" />

<c:set var="confirmRemover" value="return confirm('Tem certeza que deseja remover este questionário?');" scope="request"/>

<f:view>
<h2> Questionário > Dados do Questionário </h2>


<%@include file="/geral/questionario/_dados_gerais.jsp" %>
<%@include file="/geral/questionario/_perguntas.jsp" %>

<br />
<div class="voltar" style="text-align: center;">
	<a href="javascript: history.go(-1);"> Voltar </a>
</div>	

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>