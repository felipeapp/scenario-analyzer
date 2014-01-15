<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
table.subFormulario td {text-align: left;}

table.subFormulario  tr.turmas td{
	background: #C4D2EB;
	padding-left: 10px;
	font-weight: bold;
}

table.subFormulario tr th{
	font-weight: bold; 
}

</style>

<f:view>
<a4j:keepAlive beanName="ajustarTurmaMBean"></a4j:keepAlive>
	<c:if test="${acesso.chefeDepartamento}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>
	<c:if test="${acesso.coordenadorCursoGrad or acesso.coordenacaoProbasica}">
		<%@include file="/graduacao/menu_coordenador.jsp" %>
	</c:if>
	<h2><ufrn:subSistema/> > Ajustar Turma</h2>

<h:form id="formAjustesTurma">

	<table class="formulario" width="90%">
	<caption>Dados da Turma</caption>
	<tr><td>
		<table class="subFormulario" width="100%" style="">
		<caption>Dados Básicos</caption>
			<tr>
				<th width="30%">Componente Curricular:</th>
				<td>
					<h:outputText value="#{ajustarTurmaMBean.obj.disciplina.codigo} - #{ajustarTurmaMBean.obj.disciplina.nome}"/>
				</td>
			</tr>
			<tr>
				<th> Tipo do Componente: </th>
				<td> ${ajustarTurmaMBean.obj.disciplina.tipoComponente.descricao } </td>
			</tr>
			<tr>
				<th>CH / Créditos:</th>
				<td>
				<h:outputText value="#{ajustarTurmaMBean.obj.disciplina.chTotal} h / #{ajustarTurmaMBean.obj.disciplina.crTotal} crs"/>
				</td>
			</tr>
			<c:if test="${ajustarTurmaMBean.obj.curso.id > 0}">
				<tr>
					<th>Curso</th>
					<td>${ajustarTurmaMBean.obj.curso.descricao}</td>
				</tr>
			</c:if>
			<tr>
				<th>Docente(s):</th>
				<td>
					<c:forEach items="#{ajustarTurmaMBean.obj.docentesNomesHorarios}" var="nomeHorario">
						${nomeHorario}<br/>
					</c:forEach>
				</td>
			</tr>
			<tr>
				<th>Código da Turma:</th>
				<td>
					<c:if test="${ajustarTurmaMBean.obj.turmaAgrupadora.id > 0}">
						Será atribuído automaticamente.
					</c:if>
					<c:if test="${ajustarTurmaMBean.obj.turmaAgrupadora == null || ajustarTurmaMBean.obj.turmaAgrupadora.id == 0}">
						<c:if test="${not empty ajustarTurmaMBean.obj.codigo}">
							<h:outputText value="#{ ajustarTurmaMBean.obj.codigo }"/>
						</c:if>
						<c:if test="${empty ajustarTurmaMBean.obj.codigo}">
							Será atribuído automaticamente
						</c:if>
					</c:if>
				</td>
			</tr>
			<c:if test="${ !ajustarTurmaMBean.obj.distancia }">
			<tr>
				<th class="required">Local:</th>
				<td>
					<h:inputText id="local" value="#{ ajustarTurmaMBean.obj.local }" size="50"/>
				</td>
			</tr>
			<tr>
				<th>Horário:</th>
				<td>
					<h:outputText value="#{ ajustarTurmaMBean.obj.descricaoHorario }"/>
				</td>
			</tr>
			</c:if>
			<tr>
				<th>Ano-Período:</th>
				<td>
					<h:outputText value="#{ ajustarTurmaMBean.obj.anoPeriodo }"/>
				</td>
			</tr>
			<tr>
				<th>Período de Aulas:</th>
				<td>
				<h:outputText value="#{ ajustarTurmaMBean.obj.dataInicio}"/> - <h:outputText value="#{ ajustarTurmaMBean.obj.dataFim}"/>
				</td>
			</tr>
			<tr>
				<th>Modalidade:</th>
				<td>
					<c:choose>
						<c:when test="${ !ajustarTurmaMBean.obj.distancia }">Presencial</c:when>
						<c:otherwise>A Distância</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<c:if test="${ !ajustarTurmaMBean.obj.distancia }">
			<tr>
				<th>Capacidade de Alunos Atual:</th>
				<td>
				<h:outputText value="#{ ajustarTurmaMBean.obj.capacidadeAluno }"/>
				</td>
			</tr>
			<tr>
				<th>Total de Matriculados:</th>
				<td>${ajustarTurmaMBean.obj.totalMatriculados }</td>
			</tr>
			<tr>
				<th>Nº de Vagas Atual:</th>
				<td>
				<h:outputText value="#{ ajustarTurmaMBean.obj.capacidadeAluno - ajustarTurmaMBean.obj.totalMatriculados}"/>
				</td>
			</tr>
			<tr>
				<th class="required">
					Nº de Vagas a Adicionar:
				</th>
				<td>
					<table>
						<tr>
							<td>
								<h:inputText id="novasVagas" value="#{ ajustarTurmaMBean.novasVagas }" 
									size="3" maxlength="3" onkeyup="formatarInteiro(this);"/>
							</td>
							<td>
								<ufrn:help>
									Através desta operação só é permitido aumentar o número de vagas, nunca diminuir.
								</ufrn:help>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			</c:if>
			<tr>
				<th>Total de Solicitações de Matrícula:</th>
				<td>${ajustarTurmaMBean.obj.totalSolicitacoes}</td>
			</tr>
		</table>
	</td></tr>
	<c:if test="${ !(ajustarTurmaMBean.obj.distancia) }">
		<tr><td>
		<table class="subFormulario" width="100%">
		<caption>Reservas</caption>
			<c:if test="${ empty ajustarTurmaMBean.obj.reservas }">
				<tr><td>
					<center><font color="red"><i><strong>Não há reservas para esta turma</strong></i></font></center>
				</td></tr>
			</c:if>
			<c:if test="${ not empty ajustarTurmaMBean.obj.reservas }">
				<thead>
				<tr>
					<td>Matriz Curricular</td>
					<td>Vagas Reservadas</td>
				</tr>
				</thead>
				<c:forEach  items="${ajustarTurmaMBean.obj.reservas }" var="reserva" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td>${ reserva.descricao }</td>
						<td width="20%" style="text-align: right">${reserva.vagasReservadas }</td>
					</tr>
				</c:forEach>
			</c:if>
		</table>
		</td></tr>
	</c:if>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="Confirmar Ajustes" action="#{ ajustarTurmaMBean.confirmarAjustes }" id="btConfirmarAjustes"/>
				<h:commandButton value="<< Selecionar Outra Turma" action="#{ buscaTurmaBean.telaBuscaGeral }" id="voltarAlteraTurma"/>
				<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ ajustarTurmaMBean.cancelar }" id="btCancelar"/>
			</td>
		</tr>
	</tfoot>

	</table>
	<br/>
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>

	
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
