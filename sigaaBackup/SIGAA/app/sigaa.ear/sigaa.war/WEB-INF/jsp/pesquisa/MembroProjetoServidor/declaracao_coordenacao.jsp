<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
<!--
	h2{
		padding-top: 2cm;
		padding-bottom: 2cm;
		font-size: 1.5em;
		text-align: center;
		letter-spacing: 0.4em;
		word-spacing: 0.4em;
	}
-->
</style>

<h2 style="border-bottom: 0;">
	DECLARAÇÃO
</h2>

<p align="center" style="text-align: justify; font-size: 1.3em; line-height: 1.5em">
Declaramos que o professor(a) <b>${membro.pessoa.nome}</b>, mat. ${membro.servidor.siape}, 
lotado no ${membro.servidor.unidade.nome}, atuou como ${membro.funcaoMembro.descricao} da Pesquisa "<em>${membro.projeto.titulo}</em>", 
no período de <ufrn:format type="data" name="membro" property="dataInicio"/>
<c:choose>
<c:when test="${membro.dataFim != null && membro.dataFim <= dataAtual}">
	a <ufrn:format type="data" name="membro" property="dataFim"/>.
</c:when>
<c:otherwise>
	até a presente data.
</c:otherwise>
</c:choose>
</p>

<p align="right" style="font-size: 1.3em; padding-top: 2cm; padding-bottom: 3cm">
	${ configSistema['cidadeInstituicao'] }, <ufrn:format type="dia_mes_ano" name="dataAtual" />.
</p>

<div align="center">___________________________________________________</div>
<br/><br/><br/><br/>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>