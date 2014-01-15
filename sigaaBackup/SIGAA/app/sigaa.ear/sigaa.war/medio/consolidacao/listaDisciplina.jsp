<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="consolidarDisciplinaMBean"/>
<h2> <ufrn:subSistema /> &gt; Consolidar Disciplina</h2>

<h:form id="form" prependId="false">
	
	<div class="descricaoOperacao" style="width: 90%">
		Selecione uma das Disciplinas listadas abaixo, para modificar suas notas e efetuar a consolidação.
	</div>
	<table class="visualizacao" style="width: 60%">
		<caption>Dados da Turma</caption>
		<tr>
			<th>Ano:</th>
			<td>${consolidarDisciplinaMBean.turmaSerie.ano}</td>
		</tr>
		<tr>
			<th>Série:</th>
			<td>${consolidarDisciplinaMBean.turmaSerie.serie.descricaoCompleta}</td>
		</tr>
		<tr>
			<th>Turma:</th>
			<td>${consolidarDisciplinaMBean.turmaSerie.dependencia ?'Dependência':consolidarDisciplinaMBean.turmaSerie.nome}</td>
		</tr>
	</table>
	<br/>
	<div class="infoAltRem" style="width:90%">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar Disciplina
	</div>
	<table class="formulario" style="width: 90%">
		<caption> Disciplinas Ativas</caption>
			<thead>
				<tr>
					<th width="10%">Disciplinas</th>
					<th width="30%">Docente(s)</th>
					<th width="20%">Situação</th>
					<th width="7%">Horário</th>
					<th width="7%">Local</th>
					<th width="7%">Mat./Cap.</th>
					<th width="5%" align="right"></th>
				</tr>
			</thead>
			<tbody>
			<c:if test="${not empty consolidarDisciplinaMBean.turmaSerie.disciplinas}">
			<c:forEach var="linha" items="#{consolidarDisciplinaMBean.turmaSerie.disciplinas}" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" >
					<td>${linha.turma.disciplina.nome} </td>
					<td id="colDocente">${empty linha.turma.docentesNomesCh ? linha.turma.situacaoTurma.descricao : linha.turma.docentesNomesCh}</td>
					<td>${linha.turma.situacaoTurma.descricao} </td>
					<c:set var="posDescricaoHorario" value="${fn:indexOf(linha.turma.descricaoHorario,' ')}"/>
					<td id="colHorario">${linha.turma.descricaoHorario}</td>
					<td>${linha.turma.local}</td>
					<td style="text-align: right;"> ${linha.turma.qtdMatriculados}/${linha.turma.capacidadeAluno} alunos</td>
					<td width="2%" align="right">
						<h:commandLink action="#{consolidarDisciplinaMBean.selecionarDisciplina}" >
							<h:graphicImage value="/img/seta.gif" style="overflow: visible;" title="Selecionar Disciplina" />  
							<f:param name="id" value="#{linha.id}"/> 
							<f:param name="pageBack" value="consolidar" />
						</h:commandLink>
					</td>
				</tr>
							
			</c:forEach>
			</c:if>
			<c:if test="${empty consolidarDisciplinaMBean.turmaSerie.disciplinas}">
				<tr><td colspan="7">Não há disciplinas vinculadas a esta turma.</td></tr>
			</c:if>
		</tbody>
		<tfoot>
	    <tr>
			<td colspan="7">
				<h:commandButton value="<< Voltar" action="#{consolidarDisciplinaMBean.telaSelecaoTurma}" id="voltar"/>
				<h:commandButton value="Cancelar" action="#{consolidarDisciplinaMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
			</td>
	    </tr>
		</tfoot>
	</table>	
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>