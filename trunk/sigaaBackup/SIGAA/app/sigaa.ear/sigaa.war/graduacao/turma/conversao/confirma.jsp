<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Converter Turma Regular em Ensino Individual</h2>
	<div class="descricaoOperacao">
		<p>Caro Usuário,</p>
		<p>Confira os dados da turma e confirme a operação. <b>Uma vez convertida a turma, esta não poderá ser revertida em regular novamente.</b></p>
	</div>
	</br>
	<h:form>
	<a4j:keepAlive beanName="converterTurmaRegularIndividualMBean"></a4j:keepAlive>
	<table class="visualizacao" width="90%">
		<caption>Dados da Turma</caption>
		<tr>
			<th width="30%">Componente Curricular:</th>
			<td>
				<h:outputText value="#{converterTurmaRegularIndividualMBean.obj.disciplina.codigo} - #{converterTurmaRegularIndividualMBean.obj.disciplina.nome}"/>
			</td>
		</tr>
		<tr>
			<th> Tipo do Componente: </th>
			<td> ${converterTurmaRegularIndividualMBean.obj.disciplina.tipoComponente.descricao } </td>
		</tr>
		<tr>
			<th>CH / Créditos:</th>
			<td>
			<h:outputText value="#{converterTurmaRegularIndividualMBean.obj.disciplina.chTotal} h / #{converterTurmaRegularIndividualMBean.obj.disciplina.crTotal} crs"/>
			</td>
		</tr>
		<c:if test="${converterTurmaRegularIndividualMBean.obj.curso.id > 0}">
			<tr>
				<th>Curso</th>
				<td>${converterTurmaRegularIndividualMBean.obj.curso.descricao}</td>
			</tr>
		</c:if>
		<tr>
			<th> Tipo da Turma: </th>
			<td> ${converterTurmaRegularIndividualMBean.obj.tipoString} </td>
		</tr>
		<tr>
			<th>Docente(s):</th>
			<td>${converterTurmaRegularIndividualMBean.obj.docentesNomes }</td>
		</tr>
		<tr>
			<th>Código da Turma:</th>
			<td>
				<h:outputText value="#{ converterTurmaRegularIndividualMBean.obj.codigo }"/>
			</td>
		</tr>
		<c:if test="${ !converterTurmaRegularIndividualMBean.obj.distancia }">
		<tr>
			<th>Local:</th>
			<td>
				<h:outputText value="#{ converterTurmaRegularIndividualMBean.obj.local }"/>
			</td>
		</tr>
		<tr>
			<th>Horário:</th>
			<td>
				<h:outputText value="#{ converterTurmaRegularIndividualMBean.obj.descricaoHorario }"/>
			</td>
		</tr>
		</c:if>
		<tr>
			<th>Ano-Período:</th>
			<td>
				<h:outputText value="#{ converterTurmaRegularIndividualMBean.obj.anoPeriodo }"/>
			</td>
		</tr>
		<tr>
			<th>Período de Aulas:</th>
			<td>
			<h:outputText value="#{ converterTurmaRegularIndividualMBean.obj.dataInicio}"/> - <h:outputText value="#{ converterTurmaRegularIndividualMBean.obj.dataFim}"/>
			</td>
		</tr>
		<tr>
			<th>Modalidade:</th>
			<td>
				<c:choose>
					<c:when test="${ !converterTurmaRegularIndividualMBean.obj.distancia }">Presencial</c:when>
					<c:otherwise>A Distância</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<c:if test="${ !converterTurmaRegularIndividualMBean.obj.distancia }">
		<tr>
			<th>Capacidade de Alunos:</th>
			<td>
			<h:outputText value="#{ converterTurmaRegularIndividualMBean.obj.capacidadeAluno }"/>
			</td>
		</tr>
		</c:if>
		<c:if test="${converterTurmaRegularIndividualMBean.obj.totalMatriculados > 0}">
			<tr>
				<th>Total de Matriculados:</th>
				<td>${converterTurmaRegularIndividualMBean.obj.totalMatriculados }</td>
			</tr>
			<tr>
				<th valign="top">Discentes Matriculados:</th>
				<td>
					<c:forEach var="matricula" items="${converterTurmaRegularIndividualMBean.obj.matriculasDisciplina}" varStatus="status">
						${matricula.discente.matricula} - ${matricula.discente.nome} (${matricula.situacaoMatricula.descricao})<br/> 
					</c:forEach>
				</td>	
			</tr>
		</c:if>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Converter Turma" action="#{ converterTurmaRegularIndividualMBean.confirmar }" id="btRemover" onclick="#{alertaDiscentesMatriculados}"/>
					<h:commandButton value="<< Voltar" action="#{ converterTurmaRegularIndividualMBean.listarTurmasComPoucosDiscentes }" id="btVoltar"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ converterTurmaRegularIndividualMBean.cancelar }" id="btCancelar"/>
				</td>
			</tr>
		<tfoot>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

