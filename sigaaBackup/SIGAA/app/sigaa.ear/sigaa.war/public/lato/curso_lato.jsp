<%@include file="/public/include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/erros.jsp"%>
<h2> <a href="/sigaa/public"/>SIGAA - </a> Especialização em ${lato.curso.descricao} </h2>


<f:view>
	<%--

	 --%>
	<table class="formulario" width="100%">
	<caption>Características do Curso</caption>
		<tr>
			<td width="100"> <b>Carga Horária:</b> </td>
			<td> ${lato.curso.cargaHoraria} horas/aula</td>
			<td>
			<h:form>
			<td align="right" rowspan="4">
				<img src="/sigaa/img/lato/pre_inscricao.jpg"><br>
				<h:inputHidden value="#{lato.curso.id}"/>
				<h:commandLink value="Faça sua Pré-Inscrição" action="#{lato.preInscricao}"/>
			</td>
			</h:form>
			</td>
		</tr>

		<tr>
			<td nowrap="nowrap"> <b>Período de Realização:</b> </td>
			<td colspan="2">
				<fmt:formatDate var="dataInicio" value="${lato.curso.dataInicio}" pattern='dd/MM/yyyy' />
				<c:out value="${dataInicio}"/> -
				<fmt:formatDate var="dataFim" value="${lato.curso.dataFim}" pattern='dd/MM/yyyy' />
				<c:out value="${dataFim}"/>
			</td>
		</tr>
		<tr>
			<td nowrap="nowrap"> <b>Período de Inscrição:</b> </td>
			<td colspan="2">
				<fmt:formatDate var="dataIniSelecao" value="${lato.curso.propostaCurso.inicioInscSelecao}" pattern='dd/MM/yyyy' />
				<c:out value="${dataIniSelecao}"/> -
				<fmt:formatDate var="dataFimSelecao" value="${lato.curso.propostaCurso.fimInscSelecao}" pattern='dd/MM/yyyy' />
				<c:out value="${dataFimSelecao}"/>
			</td>
		</tr>
		<tr>
			<td nowrap="nowrap"> <b>Requisitos:</b> </td>
			<td>
			${lato.curso.propostaCurso.requisitos}
			</td>
		</tr>
		<tr>
			<td nowrap="nowrap"> <b>Contato:</b> </td>
			<td>
			${lato.curso.propostaCurso.contatos}
			</td>
		</tr>
		<tr>
			<td nowrap="nowrap"> <b>Situação da Proposta:</b> </td>
			<td>
			${lato.curso.propostaCurso.situacaoProposta.descricao}
			</td>
		</tr>
		<tr>
			<td  colspan="3"> <h2>Objetivos</h2>
			${lato.curso.propostaCurso.justificativa}
			</td>
		</tr>
		<tr>
			<td colspan="4">
			<h2> Necessidade/Importância </h2>
			${lato.curso.propostaCurso.importancia} </td>
		</tr>


		<tr>
			<td colspan="4">
			<table class="formulario" width="100%">
				<caption>Disciplinas</caption>
				<thead>
					<td width="400">Nome</td>
					<td>Carga Horária</td>
				</thead>
				<tbody>
				<c:forEach items="${lato.curso.disciplinas}" var="disciplina">
					<tr>
					<td>
						${disciplina.nome}<br>
						${disciplina.ementa}
					</td>
					<td>${disciplina.chTotal}</td>
					</tr>
				</c:forEach>
				</tbody>
				</table>
			</td>
		</tr>
	</table>
	<br>
	<center>
	<a href="/sigaa/public"/>Voltar </a>
	</center>

</f:view>
<%@include file="/public/include/rodape.jsp" %>