<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<div id="parametrosRelatorio">
	<h3 align="center">Relat�rio de Turmas</h3>
	<br>
	<table>
		<c:if test="${ buscaTurmaBean.filtroNivel }">
			<tr>
				<td>
					<b>N�vel:</b>
					<h:outputText value="#{buscaTurmaBean.nivelDescricao}"/>
				</td>
			</tr>
		</c:if>
		<c:if test="${ buscaTurmaBean.filtroAnoPeriodo }">
			<tr>
				<td>
					<b>Ano-Per�odo:</b>
					<h:outputText value="#{buscaTurmaBean.anoTurma}"/>.<h:outputText value="#{buscaTurmaBean.periodoTurma}"/>
				</td>
			</tr>
		</c:if>
		<c:if test="${ buscaTurmaBean.filtroUnidade }">
			<tr>
				<td> 
					<b>Unidade:</b>
					<h:outputText value="#{buscaTurmaBean.unidade.nome}"/>
				</td>
			</tr>
		</c:if>
		<c:if test="${ buscaTurmaBean.filtroCodigo }">
			<tr>
				<td>
					<b>C�digo do componente:</b>
					<h:outputText value="#{buscaTurmaBean.codigoDisciplina}" />
				</td>
			</tr>
		</c:if>
		<c:if test="${ buscaTurmaBean.filtroCodigoTurma }">
			<tr>
				<td>
					<b>C�digo da turma:</b>
					<h:outputText value="#{buscaTurmaBean.codigoTurma}"/>
				</td>
			</tr>
		</c:if>
		<c:if test="${ buscaTurmaBean.filtroLocal }">
			<tr>
				<td>
					<b>Local:</b>
					<h:outputText value="#{buscaTurmaBean.local}" />
				</td>
			</tr>
		</c:if>
		<c:if test="${ buscaTurmaBean.filtroHorario }">
			<tr>
				<td>
					<b>Hor�rio:</b>
					<h:outputText value="#{buscaTurmaBean.turmaHorario}" />
				</td>
			</tr>
		</c:if>
		<c:if test="${ buscaTurmaBean.filtroDisciplina }">
			<tr>
				<td>
					<b>Nome do componente:</b>
					<h:outputText value="#{buscaTurmaBean.nomeDisciplina}" />
				</td>
			</tr>
		</c:if>
		<c:if test="${ buscaTurmaBean.filtroDocente }">
			<tr>
				<td>
					<b>Nome do docente:</b>
					<h:outputText value="#{buscaTurmaBean.nomeDocente}" />
				</td>
			</tr>
		</c:if>
		<c:if test="${ buscaTurmaBean.filtroCurso }">		
			<tr>
				<td>
					<b>Ofertadas ao curso:</b>
					<h:outputText value="#{ buscaTurmaBean.curso.descricao }"/>
				</td>
			</tr>
		</c:if>
		<c:if test="${ buscaTurmaBean.filtroPolo }">
			<tr>
				<td>
					<b>P�lo:</b>
					<h:outputText value="#{ buscaTurmaBean.polo.cidade.nome}"/>
				</td>
			</tr>
		</c:if>
		<c:if test="${ buscaTurmaBean.filtroSituacao }">
			<tr>
				<td>
					<b>Situa��o:</b>
					<h:outputText value="#{buscaTurmaBean.descricaoSituacaoTurma}" />
				</td>
			</tr>
		</c:if>
		<c:if test="${ buscaTurmaBean.filtroTipo }">
			<tr>
			<td>
				<b>Tipo:</b>
				<c:choose>
					<c:when test="${buscaTurmaBean.tipoTurma == 1}">Regular</c:when>
					<c:when test="${buscaTurmaBean.tipoTurma == 2}">F�rias</c:when>
					<c:when test="${buscaTurmaBean.tipoTurma == 3}">Ensino Individual</c:when>
					<c:otherwise>Todos</c:otherwise>
				</c:choose>
			</td>
			</tr>
		</c:if>
		<c:if test="${ buscaTurmaBean.filtroConvenio }">
			<tr>
				<th colspan="2">Somente turmas vinculadas ao Conv�nio Prob�sica</th>
			</tr>
		</c:if>
	</table>
</div>
<br/>
	<c:set value="${buscaTurmaBean.resultadosBusca}" var="resultado" />
	<c:if test="${empty resultado}">
		<h3>Nenhuma turma foi encontrada com os crit�rios utilizados</h3>
	</c:if>
	<c:if test="${not empty resultado}">
	<table id="lista-turmas" class="tabelaRelatorioBorda">
		<tbody>
			<c:set var="disciplinaAtual" value="0" />
			<c:forEach items="${resultado}" var="t" varStatus="s">
				<c:if test="${ disciplinaAtual != t.disciplina.id}">
					<c:set var="disciplinaAtual" value="${t.disciplina.id}" />	
					<tr class="linhaAcima"><td colspan="11" >
						<h4 >${t.disciplina.descricaoResumida}</h4>
					</td></tr>						
						<tr class="caixaCinza" style="font-weight: bold;">
							<td width="12%">Ano-Per.</td>
							<td>N�vel</td>
							<td>C�digo</td>
							<td>Docente(s)</td>
							<td>Situa��o</td>
							<td>Hor�rio</td>
							<td>Local</td>
							<td>Mat./Cap.</td>
						</tr>
				</c:if>
					<tr  class="bordaBottonRelatorio" style="font-size: xx-small; vertical-align: top;">
						<td style="text-align:center; width: 5%;">${t.ano}.${t.periodo}</td>
						<td width="8%">${t.disciplina.nivelDesc}</td>
						<td width="10%">
							Turma ${t.codigo}
							<c:if test="${not empty t.especializacao}">
								(${t.especializacao.descricao})
							</c:if>
						</td>
						<td>${empty t.docentesNomesCh ? "A DEFINIR" : t.docentesNomesCh}</td>
						<td width="8%">${t.situacaoTurma.descricao}</td>
						<td width="8%">${t.descricaoHorario}</td>					
						<td width="10%">${t.polo == null ? t.local : t.polo.cidade.nomeUF}</td>
						<td width="10%" align="right">${t.qtdMatriculados}/${t.capacidadeAluno} alunos</td>
				</tr>
		</c:forEach>
		</tbody>
	</table>
	</c:if>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
