<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema/> &gt; Relatório de Integralização de Currículo</h2>

<table class="visualizacao" >
	<tr>
		<th width="20%"> Matrícula: </th>
		<td colspan="3"> ${relatorioIntegralizacaoCurriculoMBean.obj.matricula } </td>
	</tr>
	<tr>
		<th> Discente: </th>
		<td colspan="3"> ${relatorioIntegralizacaoCurriculoMBean.obj.pessoa.nome } </td>
	</tr>
	<tr>
		<th> Curso: </th>
		<td colspan="3">${relatorioIntegralizacaoCurriculoMBean.obj.matrizCurricular.descricao}</td>
	</tr>
	<tr>
		<th> Status: </th>
		<td width="25%"> ${relatorioIntegralizacaoCurriculoMBean.obj.statusString } </td>
		<td width="8%"> <b>Tipo:</b> </td>
		<td> ${relatorioIntegralizacaoCurriculoMBean.obj.tipoString } </td>
	</tr>
	<tr>
		<th><strong>Período Atual:</strong></th>
		<td>${ relatorioIntegralizacaoCurriculoMBean.obj.periodoAtual }</td>
		<td nowrap="nowrap"><strong>Índice de Integralização:</strong> </td>
		<td>${ relatorioIntegralizacaoCurriculoMBean.indiceStr }</td>
	</tr>
</table>
<br/>

<table class="listagem">
<caption>Estrutura Curricular</caption>
<thead><tr><th style="text-align: center;">Código</th><th>Componente</th><th>Cursado</th></tr></thead>
<c:set var="periodoAtual" value="0"/>
<c:forEach var="c" items="${ relatorioIntegralizacaoCurriculoMBean.componentes }" varStatus="loop">
<c:if test="${ periodoAtual != c.semestreOferta }">
<tr><td colspan="3" align="center"><strong>${ c.semestreOferta }<sup>o</sup> Período</strong></td></tr>
<c:set var="periodoAtual" value="${ c.semestreOferta }"/>
</c:if>
<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
<td width="70" align="center">${ c.componente.codigo }</td><td>${ c.componente.nome }</td>
<td width="50" align="center">
<c:if test="${ c.selecionado }"><img src="/sigaa/avaliacao/tick.png"/></c:if>
<c:if test="${ !c.selecionado }"><img src="/sigaa/avaliacao/cross.png"/></c:if>
</td>
</tr>
</c:forEach>
</table>

<br/>
	<div align="center">	
		<a href="javascript:history.go(-1);"> << Voltar</a>
	</div>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
