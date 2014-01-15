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
Declaramos que o professor(a) ${membro.pessoa.nome}, mat. ${membro.servidor.siape}, 
lotado no ${membro.servidor.unidade.nome}, orienta(ou) alunos de Iniciação Científica, 
conforme quadro abaixo:
</p>
<br>
<table class="listagem">
	<thead>
		<tr>
			<th>Bolsista</th>
			<th>Modalidade</th>
			<th>Período</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${orientandos}" var="membro" varStatus="loop">
		<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td>${membro.discente.pessoa.nome}</td>
			<td>${membro.planoTrabalho.tipoBolsaString}</td>
			<td>
				<ufrn:format type="data" name="membro" property="dataInicio"/>
				<c:choose>
				<c:when test="${membro.dataFim != null}">
					a <ufrn:format type="data" name="membro" property="dataFim"/>.
				</c:when>
				<c:otherwise>
					até a presente data.
				</c:otherwise>
				</c:choose>
			</td>
		</tr>
		</c:forEach>
	</tbody>
</table>

<p align="right" style="font-size: 1.3em; padding-top: 2cm; padding-bottom: 3cm">
	${ configSistema['cidadeInstituicao'] }, <ufrn:format type="dia_mes_ano" name="dataAtual" />.
</p>

<div align="center">_______________________________________________________________________</div>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>