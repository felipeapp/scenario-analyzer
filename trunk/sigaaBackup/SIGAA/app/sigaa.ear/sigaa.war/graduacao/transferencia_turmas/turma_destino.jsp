<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<style>
	tr.componente td{
		background: #C4D2EB;
		font-weight: bold;
		border-bottom: 1px solid #BBB;
		color: #222;
	}
</style>

<f:view>
	<h:messages showDetail="true"/>
	<h2> <ufrn:subSistema /> &gt; Transferência entre Turmas ${transferenciaTurma.descricaoTipo} &gt; Definir Turma de Destino</h2>
	<h:form>
		<table class="visualizacao">
			<caption>Turma de Origem</caption>
			<tbody>
			<tr>
				<th>Turma:</th>
				<td>${transferenciaTurma.turmaOrigem.disciplina.descricaoResumida } - Turma ${transferenciaTurma.turmaOrigem.codigo}</td>
			</tr>
			<tr>
				<th>Docente(s):</th>
				<td>${transferenciaTurma.turmaOrigem.docentesNomes}</td>
			</tr>
			<tr>
				<th>Horário:</th>
				<td> ${transferenciaTurma.turmaOrigem.descricaoHorario}	</td>
			</tr>
			<tr>
				<th>Capacidade da Turma:</th>
				<td> ${transferenciaTurma.turmaOrigem.capacidadeAluno} </td>
			</tr>
			<tr>
				<th>Alunos Matriculados:</th>
				<td> ${transferenciaTurma.turmaOrigem.qtdMatriculados} </td>
			</tr>
			<tr>
				<th>Solicitações:</th>
				<td> ${transferenciaTurma.turmaOrigem.qtdEspera} </td>
			</tr>
			</tbody>
		</table>
	</h:form>
		
	<br />
	<c:if test="${not empty transferenciaTurma.turmasDestino}">
		<br>
		<center>
		<div class="infoAltRem"><h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:
		Selecionar Turma<br />
		</div>
		</center>
		<table class=listagem>
			<caption class="listagem">Selecione a Turma de Destino</caption>
			<thead>
				<tr>
					<td>Turma</td>
					<td>Docentes</td>
					<td style="text-align: center;">Ano/Período</td>
					<td>Horário</td>
					<td style="text-align: right;">Matriculados</td>
					<td style="text-align: right;">Solicitações</td>
					<td style="text-align: right;">Capacidade</td>
					<td></td>
				</tr>
			</thead>
			<h:form>
			
				<tr class="componente">
					<td colspan="8">
						 ${transferenciaTurma.turmaOrigem.disciplina.descricaoResumida}
					</td>
				</tr>
								
				<c:forEach items="#{transferenciaTurma.turmasDestino}" var="item" varStatus="linha">
					<tr class="${ linha.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td> ${item.codigo} </td>
						<td> ${item.docentesNomes} </td>
						<td align="center"> ${item.ano}.${item.periodo}</td>
						<td>${ item.descricaoHorario }</td>
						<td align="right">${ item.qtdMatriculados }</td>
						<td align="right">${ item.qtdEspera }</td>
						<td align="right">${ item.capacidadeAluno }</td>
						<td width="2%">
							<h:commandLink action="#{transferenciaTurma.selecionarTurmaDestino}">
									<h:graphicImage url="/img/seta.gif" alt="Selecionar Turma" title="Selecionar Turma"/>
									<f:param name="id" value="#{item.id}"/>
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</h:form>
		</table>
	</c:if>
	<br>
	<center>
		<h:form>
		<h:commandButton value="<< Voltar" action="#{transferenciaTurma.voltarTurmaOrigem}" />
		<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{transferenciaTurma.cancelar}" />
		</h:form>
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>