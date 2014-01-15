<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h:form id="form">
	<h2> Lista de Alunos da Turma</h2>

	<table class="visualizacao" >
		<tr>
			<th width="20%"> Turma: </th>
			<td> ${turmaInfantilMBean.obj.disciplina.descricaoResumida} - ${turmaInfantilMBean.obj.codigo} </td>
		</tr>
		<tr>
			<th> Docente(s): </th>
			<td> ${turmaInfantilMBean.obj.docentesNomes} </td>
		</tr>
		<tr>
			<th> Horário: </th>
			<td> ${turmaInfantilMBean.obj.descricaoHorario} </td>
		</tr>
	</table>

	<br />
	
<!-- 	<div class="infoAltRem"> -->
<%-- 	    <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Registro de Evolução da Criança --%>
<!-- 	</div> -->
	
	<br />
	<table class="listagem" id="lista-turmas" style="width: 90%;">
		<caption>${fn:length(turmaInfantilMBean.matriculados) } discentes foram matriculados nessa turma</caption>

		<c:if test="${empty turmaInfantilMBean.matriculados}">
			<tr><td>Nenhum aluno está matriculado nessa turma</td></tr>
		</c:if>
		<c:if test="${not empty turmaInfantilMBean.matriculados}">
			<thead>
				<tr>
					<td width="10%">Matrícula</td>
					<td>Nome</td>
					<td width="13%">Situação</td>
				</tr>
			</thead>
			<c:forEach items="#{turmaInfantilMBean.matriculados}" var="mat" varStatus="s">
				<tbody>
					<tr class="${s.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
						<td> ${mat.discente.matricula } </td>
						<td> ${mat.discente.nome} </td>
						<td> ${mat.situacaoMatricula.descricao} </td>
<!-- 						<td> -->
<%-- 							<h:commandLink title="Registro de Evolução da Criança" action="#{ registroEvolucaoCriancaMBean.selecionarDiscente }" > --%>
<%-- 						        <f:param name="idDiscente" value= "#{mat.discente.id}" /> --%>
<%-- 					    		<h:graphicImage url="/img/seta.gif" /> --%>
<%-- 							</h:commandLink> --%>
<!-- 						</td> -->
					</tr>
				</tbody>
			</c:forEach>
		</c:if>
	</table>
	
	<br/>
	<center>
		<h:form>
			<input type="button" value="<< Selecionar Outra Turma"  onclick="javascript: history.back();" id="voltar" />
		</h:form>
	</center>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
