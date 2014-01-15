<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib tagdir="/WEB-INF/tags" prefix="aval" %>
<f:view>
	<h2>Relatório de Docentes com Média Baixa por Pergunta</h2>
	
<div id="parametrosRelatorio">
<table>
	<c:if test="${relatorioAvaliacaoMBean.idUnidade > 0}">
		<tr>
			<th>Departamento:</th>
			<td><h:outputText value="#{relatorioAvaliacaoMBean.unidade}"/></td>
		</tr>
	</c:if>
	<tr>
		<th>Ano-Período:</th>
		<td><h:outputText value="#{relatorioAvaliacaoMBean.ano}"/>.<h:outputText value="#{relatorioAvaliacaoMBean.periodo}"/></td>
	</tr>
	<tr>
		<th>Média Mínima:</th>
		<td><h:outputText value="#{relatorioAvaliacaoMBean.mediaMinima}"/></td>
	</tr>
</table>
</div>
<br/>

<c:set var="docenteAnterior" value=""/>
<c:forEach items="#{relatorioAvaliacaoMBean.docentesTurma}" var="docenteTurma" varStatus="status">
	<c:if test="${docenteAnterior != docenteTurma.docenteNome}">
		<c:if test="${status.index > 0}">
			<hr style="color: black; background-color: black;" />
		</c:if>
		<h2>${docenteTurma.docenteNome}</h2>
		<c:set var="docenteAnterior" value="${docenteTurma.docenteNome}"/>
	</c:if>
	<%-- Turma --%>
	<c:forEach items="#{relatorioAvaliacaoMBean.resultadoMediaGeralBaixa}" var="linha">
		<c:if test="${docenteTurma.id == linha.docenteTurma.id}">
			<c:set var="turma" value="${linha.docenteTurma.turma}" />
		</c:if>
	</c:forEach>
	<c:forEach items="#{relatorioAvaliacaoMBean.resultadoMediaNotasBaixa}" var="linha">
		<c:if test="${docenteTurma.id == linha.resultadoAvaliacaoDocente.docenteTurma.id}">
			<c:set var="turma" value="${linha.resultadoAvaliacaoDocente.docenteTurma.turma}" />
		</c:if>
	</c:forEach>
	<%-- Médias por Turma --%>
	<table class="tabelaRelatorioBorda" width="100%">
		<caption>${turma.descricaoSemDocente}</caption>
		<thead>
			<tr style="font-size: 11px">
				<th width="60%" class="caixaCinza" style="text-align: left;"><b>Pergunta/Grupo de Perguntas (Dimensão)</b></th>
				<th width="20%" class="caixaCinza" style="text-align: right;"><b>Média</b></th>
				<th width="20%" class="caixaCinza" style="text-align: right;"><b>Desvio Padrão</b></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{relatorioAvaliacaoMBean.resultadoMediaGeralBaixa}" var="linha">
				<c:if test="${docenteTurma.id == linha.docenteTurma.id}">
					<tr>
						<td align="left">Média Geral</td>
						<td align="right"><ufrn:format type="valor" valor="${ linha.mediaGeral }"/></td>
						<td align="right"><ufrn:format type="valor" valor="${ linha.desvioPadraoGeral }"/></td>
					</tr>
				</c:if>
			</c:forEach>
			<c:forEach items="#{relatorioAvaliacaoMBean.resultadoMediaNotasBaixa}" var="linha">
				<c:if test="${docenteTurma.id == linha.resultadoAvaliacaoDocente.docenteTurma.id}">
					<tr>
						<td align="left">${linha.pergunta.descricao }</td>
						<td align="right"><ufrn:format type="valor" valor="${ linha.media }"/></td>
						<td align="right"><ufrn:format type="valor" valor="${ linha.desvioPadrao }"/></td>
					</tr>
				</c:if>
			</c:forEach>
		</tbody>
	</table>
	<br/>
</c:forEach>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>