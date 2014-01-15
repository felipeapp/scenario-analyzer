<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<%@taglib tagdir="/WEB-INF/tags" prefix="aval" %>
<f:view>
	<h2>Resultado da Avaliação Institucional do Docente</h2>
	
<div id="parametrosRelatorio">
<table>
	<tr>
		<th>Departamento:</th>
		<td><h:outputText value="#{relatorioAvaliacaoMBean.docente.unidade}"/></td>
	</tr>
	<tr>
		<th>Docente:</th>
		<td><h:outputText value="#{relatorioAvaliacaoMBean.docente.nome}"/></td>
	</tr>
	<tr>
		<th>Ano-Período:</th>
		<td><h:outputText value="#{relatorioAvaliacaoMBean.ano}"/>.<h:outputText value="#{relatorioAvaliacaoMBean.periodo}"/></td>
	</tr>
</table>
</div>
<br/>
	
	<c:set var="docenteAtual" value=""/>
	<c:set var="docenteLoop" value=""/>
	<c:set var="fechaTabela" value="false" />
	<c:forEach items="#{relatorioAvaliacaoMBean.resultadosDocentes}" var="linha">
		<c:set var="docenteLoop" value="${ linha.docenteTurma.docenteNome }"/>
			<c:if test="${docenteLoop != docenteAtual}">
				<c:set var="docenteAtual" value="${ linha.docenteTurma.docenteNome }"/>
				<c:if test="${fechaTabela}">
					</tbody>
					</table>
					<br/>
				</c:if>
				<c:set var="fechaTabela" value="true" />
				<table class="tabelaRelatorioBorda" width="100%">
				<thead>
					<tr>
						<th width="70%">Componente Curricular</th>
						<th width="5%"  style="text-align: right;">Turma</th>
						<th width="5%" style="text-align: right;">Discentes</th>
						<th width="10%" style="text-align: right;">Trancamentos</th>
						<th width="5%" style="text-align: right;">Média Geral</th>
						<th width="5%" style="text-align: right;">DP Geral</th>
					</tr>
				</thead>
				<tbody>
			</c:if>
			<tr>
				<td align="left">${ linha.docenteTurma.turma.disciplina.descricaoResumida }</td>
				<td align="right">${ linha.docenteTurma.turma.codigo }</td>
				<td align="right">${ linha.numDiscentes }</td>
				<td align="right">${ linha.numTrancamentos }</td>
				<c:if test="${not empty linha.mediaGeral}">
					<td align="right"><ufrn:format type="valor" valor="${ linha.mediaGeral }"/></td>
					<td align="right"><ufrn:format type="valor" valor="${ linha.desvioPadraoGeral }"/></td>
				</c:if>
				<c:if test="${empty linha.mediaGeral }">
					<td align="right">N/A</td>
					<td align="right">N/A</td>
				</c:if>
			</tr>
	</c:forEach>
	<c:if test="${fechaTabela}">
		</tbody>
		</table>
	</c:if>
<br/>
<br/>
<%-- Map<Turma, Map<GrupoPerguntas, Map<Pergunta, ItemRelatorioAvaliacaoInstitucionalDocenteTurma>>> --%>
<c:forEach items="#{relatorioAvaliacaoMBean.detalheRespostas}" var="turma">
	<c:if test="${not empty turma.value}">
		<%-- Map<GrupoPerguntas, Map<Pergunta, ItemRelatorioAvaliacaoInstitucionalDocenteTurma>> --%>
		<hr />
		<h4>Turma: ${turma.key.descricaoSemDocente }</h4><br/>
		<c:forEach items="${turma.value}" var="grupoPergunta">
			<%-- Map<Pergunta, ItemRelatorioAvaliacaoInstitucionalDocenteTurma> --%>
			<b>${grupoPergunta.key.titulo}</b>  - ${grupoPergunta.key.descricao }
			<table class="tabelaRelatorioBorda" width="100%">
				<thead>
					<c:forEach items="${grupoPergunta.value}" var="perguntas" varStatus="perguntaStatus" end="0">
						<c:forEach items="${perguntas.value.respostas}" var="item" varStatus="status">
							<c:set var="numRespostas" value="${status.index + 1}" />
						</c:forEach>
					</c:forEach>	
					<tr>
						<th rowspan="2" colspan="2">Perguntas</th>
						<th colspan="${numRespostas}" style="text-align: center">Respostas dos Discentes</th>
						<c:forEach items="${grupoPergunta.value}" var="pergunta" varStatus="perguntaStatus" end="0">
						<c:if test="${pergunta.key.nota}">
						<th rowspan="2" width="40px" style="text-align: center">Média</th>
						<th rowspan="2" width="40px" style="text-align: center">DP</th>
						</c:if>
						<c:if test="${pergunta.key.simNao}">
						<th rowspan="2" width="40px" style="text-align: center">% SIM</th>
						<th rowspan="2" width="40px" style="text-align: center">% NÃO</th>
						</c:if>
						</c:forEach>
					</tr>
					<tr>
						<c:forEach items="${grupoPergunta.value}" var="perguntas" varStatus="perguntaStatus" end="0">
							<c:forEach items="${perguntas.value.respostas}" var="item" varStatus="status">
								<th width="15px;" style="text-align: center">${status.index + 1}</th>
							</c:forEach>
						</c:forEach>	
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${grupoPergunta.value}" var="pergunta" varStatus="perguntaStatus">
					<%-- ItemRelatorioAvaliacaoInstitucionalDocenteTurma --%>
					<tr>
						<td>${perguntaStatus.index + 1}</td>
						<td>${pergunta.key.descricao}</td>
						<c:forEach items="${pergunta.value.respostas}" var="item" varStatus="status">
							<c:if test="${pergunta.key.simNao}">
								<td align="center">
									<c:if test="${empty item.valorSimNao}">N/A</c:if>
									${item.valorSimNao}
								</td>
							</c:if>
							<c:if test="${pergunta.key.nota}">
								<td align="center">
									<c:if test="${empty item.resposta or item.resposta < 0}">N/A</c:if>
									<c:if test="${not empty item.resposta and item.resposta >= 0}">${item.resposta}</c:if>
								</td>
							</c:if>
						</c:forEach>
						<c:if test="${pergunta.key.nota}">
							<td align="center"><ufrn:format type="valor" valor="${pergunta.value.mediaNotas.media}"/></td>
							<td align="center"><ufrn:format type="valor" valor="${pergunta.value.mediaNotas.desvioPadrao}"/></td>
						</c:if>
						<c:if test="${pergunta.key.simNao}">
							<td align="center">
								<c:if test="${empty pergunta.value.percentualSimNao.percentualSim}">N/A</c:if>
								<ufrn:format type="valor" valor="${pergunta.value.percentualSimNao.percentualSim}"/>
							</td>
							<td align="center">
								<c:if test="${empty pergunta.value.percentualSimNao.percentualNao}">N/A</c:if>
								<ufrn:format type="valor" valor="${pergunta.value.percentualSimNao.percentualNao}"/>
							</td>
						</c:if>
					</tr>
					</c:forEach>
				</tbody>
			</table>
			<br/>
		</c:forEach>
	</c:if>
</c:forEach>
<div><b>Legenda:</b></div>
<table>
	<tr>
		<td width="25px"><b>N/A</b></td>
		<td>Item Não Avaliado</td>
	</tr>
	<tr>
		<td width="25px"><b>DP</b></td>
		<td>Desvio Padrão</td>
	</tr>
</table>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>