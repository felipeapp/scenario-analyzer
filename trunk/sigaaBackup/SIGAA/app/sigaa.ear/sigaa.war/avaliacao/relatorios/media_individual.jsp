<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib tagdir="/WEB-INF/tags" prefix="aval" %>
<f:view>
	<h2>Resultado Analítico da Avaliação Institucional do Docente por Turma</h2>
	
<div id="parametrosRelatorio">
<table>
	<tr>
		<th>Docente:</th>
		<td><h:outputText value="#{relatorioAvaliacaoMBean.docente.nome}"/></td>
	</tr>
	<tr>
		<th>Departamento:</th>
		<td><h:outputText value="#{relatorioAvaliacaoMBean.docente.unidade}"/></td>
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
				<tr style="font-size: 11px">
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
<%-- Map<Turma, Map<GrupoPerguntas, TabelaRespostaResultadoAvaliacao>> --%>
<c:forEach items="#{relatorioAvaliacaoMBean.detalheRespostas}" var="turma">
	<c:if test="${not empty turma.value}">
		<%-- Turma -> Map<GrupoPerguntas, TabelaRespostaResultadoAvaliacao> --%>
		<hr style="color: black; background-color: black;" />
		<h4>Componente: ${turma.key.disciplina.codigo } - ${turma.key.descricaoDisciplina } Turma: ${turma.key.codigo }</h4><br/>
		<c:forEach items="${turma.value}" var="grupoPergunta">
			<%-- GrupoPerguntas -> TabelaRespostaResultadoAvaliacao --%>
			<b>${grupoPergunta.key.titulo}</b>  - ${grupoPergunta.key.descricao }
			<div align="center">
			<table class="tabelaRelatorioBorda" width="100%">
				<thead>
					<c:forEach items="${grupoPergunta.value.mapaRespostas}" var="discentes" varStatus="discenteStatus" end="0">
						<%-- TabelaRespostaResultadoAvaliacao --%>
						<c:forEach items="${discentes.value}" var="respostas" varStatus="respostaStatus" >
							<c:set var="numPerguntas" value="${respostaStatus.index + 1}" />
						</c:forEach>
					</c:forEach>	
					<tr>
						<th width="15%" rowspan="2" style="text-align: center;">Discente<acronym title="Os discentes são ordenados aleatoriamente e o índice serve apenas para visualização">*</acronym></th>
						<th colspan="${numPerguntas}" style="text-align: center">Respostas às Perguntas</th>
					</tr>
					<tr>
						<c:forEach items="${grupoPergunta.value.mapaRespostas}" var="discentes" varStatus="discenteStatus" end="0">
							<%-- Discente -> List<RespostaPergunta> --%>
							<c:forEach items="${discentes.value}" var="respostas" varStatus="respostaStatus">
								<%-- <th>${respostas.pergunta.descricao}</th> --%>
								<c:if test="${respostas.pergunta.simNao}">
									<th style="text-align: center;">
										<acronym title="${respostas.pergunta.descricao}">
											${grupoPergunta.key.id}.${respostaStatus.index + 1}
										</acronym>
									</th>
								</c:if> 
								<c:if test="${respostas.pergunta.nota}">
									<th style="text-align: right;">
										<acronym title="${respostas.pergunta.descricao}">
											${grupoPergunta.key.id}.${respostaStatus.index + 1}
										</acronym>
									</th>
								</c:if> 
							</c:forEach>
						</c:forEach>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${grupoPergunta.value.mapaRespostas}" var="discentes" varStatus="discenteStatus">
					<%-- TabelaRespostaResultadoAvaliacao --%>
					<tr>
						<td align="center">${discenteStatus.index + 1}</td>
						<c:forEach items="${discentes.value}" var="respostas" varStatus="respostaStatus">
							<c:if test="${respostas.pergunta.simNao}">
								<td  align="center">
									<c:if test="${empty respostas.valorSimNao}">N/A</c:if>
									${respostas.valorSimNao}
								</td>
							</c:if>
							<c:if test="${respostas.pergunta.nota}">
								<td  align="right">
									<c:if test="${(empty respostas.resposta) || respostas.resposta < 0}">N/A</c:if>
									<c:if test="${respostas.resposta >= 0}">${respostas.resposta}</c:if>
								</td>
							</c:if>
						</c:forEach>
					</tr>
					</c:forEach>
					</tbody>
					<tfoot>
					<c:if test="${not empty grupoPergunta.value.medias}">
						<tr style="font-weight: bold;">
							<td style="text-align: right;"> Média</td>
							<c:forEach items="${grupoPergunta.value.medias}" var="medias">
								<td align="right">
									<c:if test="${empty medias.media}">N/A</c:if>
									<ufrn:format type="valor" valor="${medias.media}" />
								</td>
							</c:forEach>
						</tr>
						<tr style="font-weight: bold;">
							<td style="text-align: right;">DP</td>
							<c:forEach items="${grupoPergunta.value.medias}" var="medias">
								<td align="right">
									<c:if test="${empty medias.desvioPadrao}">N/A</c:if>
									<ufrn:format type="valor" valor="${medias.desvioPadrao}" />
								</td>
							</c:forEach>
						</tr>
					</c:if>
					<c:if test="${not empty grupoPergunta.value.percentuais}">
						<tr style="font-weight: bold;">
							<td style="text-align: right;"> % SIM</td>
							<c:forEach items="${grupoPergunta.value.percentuais}" var="percentual">
								<td align="center">
									<c:if test="${empty percentual.percentualSim}">N/A</c:if>
									<ufrn:format type="valor" valor="${percentual.percentualSim}" />%
								</td>
							</c:forEach>
						</tr>
						<tr style="font-weight: bold;">
							<td style="text-align: right;"> % NÃO</td>
							<c:forEach items="${grupoPergunta.value.percentuais}" var="percentual">
								<td align="center">
									<c:if test="${empty percentual.percentualNao}">N/A</c:if>
									<ufrn:format type="valor" valor="${percentual.percentualNao}" />%
								</td>
							</c:forEach>
						</tr>
					</c:if>
				</tfoot>
			</table>
			</div>
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
	<c:forEach items="#{relatorioAvaliacaoMBean.detalheRespostas}" var="turma" end="0">
		<c:forEach items="${turma.value}" var="grupoPergunta">
			<tr><td colspan="2"><b>${grupoPergunta.key.titulo}</b> - ${grupoPergunta.key.descricao }</td></tr>
			<c:forEach items="${grupoPergunta.value.mapaRespostas}" var="discentes" varStatus="discenteStatus" end="0">
				<c:forEach items="${discentes.value}" var="respostas" varStatus="respostaStatus">
					<tr>
						<td><b>${grupoPergunta.key.id}.${respostaStatus.index + 1}</b></td>
						<td valign="top">${respostas.pergunta.descricao}</td>
					</tr> 
				</c:forEach>
			</c:forEach>
		</c:forEach>
	</c:forEach>
</table>
<br/>
<div><b>Observações:</b></div>
<table>
	<tr>
		<td width="25px"><b>*</b></td>
		<td>Os discentes são ordenados aleatoriamente e o índice serve apenas para visualização.</td>
	</tr>
</table>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>