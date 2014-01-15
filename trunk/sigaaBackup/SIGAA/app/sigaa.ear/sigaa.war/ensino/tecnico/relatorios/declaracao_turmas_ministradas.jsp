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
<f:view>
<h2 style="border-bottom: 0;">
	DECLARA��O
</h2>

<h:outputText value="#{declaracaoTecnico.create}"/>
<c:set var="docente_" value="${declaracaoTecnico.docente}"/>

<p align="center" style="text-align: justify; font-size: 1.3em; line-height: 1.5em">
Declaramos que o professor(a) ${docente_.pessoa.nome}, mat. ${docente_.siape}, 
ministra(ou) aulas, entre os per�odos de ${declaracaoTecnico.ano}.${declaracaoTecnico.periodo}
a ${declaracaoTecnico.anoFim}.${declaracaoTecnico.periodoFim}, para a(s) seguinte(s) turma(s):
</p>
<br>
<table class="listagem">
	<thead>
		<tr>
			<th style="text-align: center;">Per�odo</th>
			<th style="text-align: center;">C�digo</th>
			<th>Disciplina</th>
			<th style="text-align: right;">Turma</th>
			<th style="text-align: right;">CH Dedicada</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${declaracaoTecnico.turmas}" var="dt" varStatus="loop">
		<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td style="text-align: center;">${dt.turma.ano}.${dt.turma.periodo}</td>
			<td style="text-align: center;">${dt.turma.disciplina.codigo}</td>
			<td>${dt.turma.disciplina.detalhes.nome}</td>
			<td style="text-align: right;">${dt.turma.codigo}</td>
			<td style="text-align: right;">${dt.chDedicadaPeriodo}</td>
		</tr>
		</c:forEach>
	</tbody>
</table>

<p align="right" style="font-size: 1.3em; padding-top: 2cm; padding-bottom: 3cm">
	${ configSistema['cidadeInstituicao'] }, <ufrn:format type="dia_mes_ano" name="dataAtual" />.
</p>

<div align="center">_______________________________________________________________________</div>
<br>
<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>