<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> &gt; Projeto de Pesquisa </h2>



<c:set var="projeto" value="${ projetoPesquisaForm.obj }" />
<h3 class="tituloTabela">Dados do Projeto de Pesquisa</h3>
<table class="visualizacao" align="center" style="width: 100%">
    <tbody>
		<%@include file="/WEB-INF/jsp/pesquisa/ProjetoPesquisa/include/resumo_projeto.jsp"%>
	</tbody>
</table>

<br />
<div class="voltar" style="text-align: center;">
	<a href="javascript: history.go(-1);"> Voltar </a>
</div>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>