<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<%@taglib tagdir="/WEB-INF/tags" prefix="aval" %>
<f:view>
	<h2>Resultado Sintético da Avaliação Institucional dos Docentes por Departamento</h2>
	
<div id="parametrosRelatorio">
<table>
	<tr>
		<th>
			Departamento:
		</th>
		<td><h:outputText value="#{relatorioAvaliacaoMBean.unidade}"/></td>
	</tr>
	<tr>
		<th>Ano-Período:</th>
		<td><h:outputText value="#{relatorioAvaliacaoMBean.ano}"/>.<h:outputText value="#{relatorioAvaliacaoMBean.periodo}"/></td>
	</tr>
	<tr>
		<th valign="top">Detalhar as Perguntas:</th>
		<td>
			<c:forEach items="#{relatorioAvaliacaoMBean.legendas}" var="legenda" varStatus="status">
				${legenda.key} - ${legenda.value}<br/>  
			</c:forEach>
		</td>
	</tr>
</table>
</div>
<br/>
	
	<c:set var="docenteAtual" value=""/>
	<c:set var="docenteLoop" value=""/>
	<c:set var="totalDocentes" value="0" />
	<c:set var="totalDiscentes" value="0" />
	<c:set var="totalTrancamentos" value="0" />
	<table class="tabelaRelatorioBorda" width="100%"  style="font-size: 11px;">
	<thead>
		<tr>
			<th rowspan="2" width="35%">Docente</th>
			<th rowspan="2" width="35%">Componente Curricular</th>
			<th rowspan="2" width="5%" style="text-align: left;">Turma</th>
			<th rowspan="2" width="5%" style="text-align: left;">Horário</th>
			<th rowspan="2" width="5%" style="text-align: right;">Discentes</th>
			<th colspan="${fn:length(relatorioAvaliacaoMBean.legendas) + 2}" style="text-align: center">Média das Notas Atribuídas pelos Alunos</th>
		</tr>
		<tr>
			<c:forEach items="#{relatorioAvaliacaoMBean.legendas}" var="legenda" varStatus="status">
				<th style="text-align: right;">${legenda.value}</th>
			</c:forEach>
			<th width="5%" style="text-align: right;">Média Geral</th>
			<th width="5%" style="text-align: right;">Desvio Padrão Geral</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="#{relatorioAvaliacaoMBean.resultadosDocentes}" var="linha">
			<c:set var="docenteLoop" value="${ linha.docenteTurma.docenteNome }"/>
			<c:if test="${docenteLoop != docenteAtual}">
				<c:set var="totalDocentes" value="${totalDocentes + 1}" />
				<c:set var="docenteAtual" value="${ linha.docenteTurma.docenteNome }"/>
			</c:if>
			<tr>
				<c:set var="totalDiscentes" value="${totalDiscentes + linha.numDiscentes}" />
				<td>${ linha.docenteTurma.docenteNome }</td>
				<td style="text-align: left;">${ linha.docenteTurma.turma.disciplina.descricaoResumida }</td>
				<td style="text-align: left;">${ linha.docenteTurma.turma.codigo }</td>
				<td style="text-align: left;">${ linha.docenteTurma.turma.descricaoHorario }</td>
				<td style="text-align: right;">${ linha.numDiscentes }</td>
				<c:forEach items="#{relatorioAvaliacaoMBean.perguntasSelecionadas}" var="idPergunta">
					<aval:mediaNotas somenteMedia="true" resultado="${linha}" idPergunta="${idPergunta}"></aval:mediaNotas>
				</c:forEach>
				<c:if test="${not empty linha.mediaGeral }">
					<td style="text-align: right;"><ufrn:format type="valor" valor="${ linha.mediaGeral }"/></td>
					<td style="text-align: right;"><ufrn:format type="valor" valor="${ linha.desvioPadraoGeral }"/></td>
				</c:if>
				<c:if test="${empty linha.mediaGeral }">
					<td style="text-align: right;">N/A</td>
					<td style="text-align: right;">N/A</td>
				</c:if>
			</tr>
		</c:forEach>
	</tbody>
	</table>
	<br/>
	<table class="tabelaRelatorio" width="40%" style="font-size: 11px;" align="center">
	<caption>Totais do Relatório</caption>
	<tbody>
		<tr>
			<th>Total de Docentes:</th>
			<td style="text-align: right;">${totalDocentes}</td>
		</tr>
		<tr>
			<th>Total de Turmas Avaliadas:</th>
			<td style="text-align: right;">${fn:length(relatorioAvaliacaoMBean.resultadosDocentes)}</td>
		</tr>
		<tr>
			<th>Total de Discentes:</th>
			<td style="text-align: right;">${totalDiscentes}</td>
		</tr>
	</tbody>
	</table>
<br/>
<div><b>Legenda:</b></div>
<table>
	<tr>
		<td width="25px"><b>N/A</b></td>
		<td>Item Não Avaliado</td>
	</tr>
</table>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>