<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
<!--
	h2{
		padding-top: 2cm;
		padding-bottom: 2cm;
		font-size: 1.5em;
		letter-spacing: 0.4em;
		word-spacing: 0.4em;
		text-align: center;
	}
-->
</style>


<h2 style="border-bottom: 0;">
	DECLARA��O
</h2>



<p align="justify" style="font-size: 1.3em; line-height: 1.5em">
Declaramos que o(a) aluno(a) <b>${membro.discente.nome}</b>, mat. ${membro.discente.matricula}, 
atua(ou) como aluno(a) de inicia��o cient�fica, com bolsa <b>${membro.tipoBolsaString}, </b> cota
<b> ${ membro.planoTrabalho.cota.descricao } </b> com vig�ncia de 
<ufrn:format type="data" valor="${ membro.planoTrabalho.cota.inicio }" /> � 
<ufrn:format type="data" valor="${ membro.planoTrabalho.cota.fim }" />, 
no projeto <em>"${membro.planoTrabalho.projetoPesquisa.titulo}"</em>, 
sob a orienta��o do(a) professor(a) ${membro.planoTrabalho.orientador.nome}, 
durante o per�odo de <ufrn:format type="data" name="membro" property="dataInicio"/>
<c:choose>
<c:when test="${membro.dataFim != null}">
	a <ufrn:format type="data" name="membro" property="dataFim"/>.
</c:when>
<c:otherwise>
	at� a presente data.
</c:otherwise>
</c:choose>
</p>

<p align="right" style="font-size: 1.3em; padding-top: 2cm; padding-bottom: 3cm">
	${ configSistema['cidadeInstituicao'] }, <ufrn:format type="dia_mes_ano" name="dataAtual" />.
</p>

<div align="center">_______________________________________________________________________</div>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>