<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="consolidacaoTurmaInfantilMBean"/>
<h:form id="form">
	<h2> <ufrn:subSistema /> &gt; Lista de Alunos da Turma</h2>
	<table class="visualizacao" >
		<tr>
			<th width="20%"> Turma: </th>
			<td> ${consolidacaoTurmaInfantilMBean.turma.descricaoResumida} - ${consolidacaoTurmaInfantilMBean.turma.codigo} </td>
		</tr>
		<tr>
			<th> Docente(s): </th>
			<td> ${consolidacaoTurmaInfantilMBean.turma.docentesNomes} </td>
		</tr>
		<tr>
			<th> Horário: </th>
			<td> ${consolidacaoTurmaInfantilMBean.turma.descricaoHorario} </td>
		</tr>
	</table>

	<br />
	<table class="listagem" id="lista-turmas" style="width: 90%;">
		<caption>${fn:length(consolidacaoTurmaInfantilMBean.matriculados) } discentes foram matriculados nessa turma</caption>

		<c:if test="${empty consolidacaoTurmaInfantilMBean.matriculados}">
			<tr><td>Nenhum aluno está matriculado nessa turma</td></tr>
		</c:if>
		<c:if test="${not empty consolidacaoTurmaInfantilMBean.matriculados}">
			<thead>
				<tr>
					<td width="5%"><h:selectBooleanCheckbox value="false" onclick="checkAll(this)"/></td>
					<td width="10%">Matrícula</td>
					<td>Nome</td>
					<td width="13%">Situação</td>
				</tr>
			</thead>
			<c:forEach items="#{consolidacaoTurmaInfantilMBean.matriculados}" var="mat" varStatus="s">
				<tbody>
					<tr class="${s.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
						<td><h:selectBooleanCheckbox value="#{mat.discente.selecionado}" styleClass="check" /></td>
						<td> ${mat.discente.matricula } </td>
						<td> ${mat.discente.nome} </td>
						<td> ${mat.situacaoMatricula.descricao} </td>
					</tr>
				</tbody>
			</c:forEach>
			<tfoot>
			<tr>
				<td colspan="4" align="center">
					<h:commandButton value="Consolidar" action="#{consolidacaoTurmaInfantilMBean.consolidar}"/>
					<input type="button" value="<<Voltar"  onclick="javascript: history.back();" id="voltar" />
					<h:commandButton value="Cancelar" action="#{consolidacaoTurmaInfantilMBean.cancelar}" onclick="#{confirm}" immediate="true"  />
				 </td>
			</tr>
			</tfoot>
		</c:if>
	</table>
	
	<br/>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<script type="text/javascript">
function checkAll(elem) {
	$A(document.getElementsByClassName('check')).each(function(e) {
		e.checked = elem.checked;
	});
}
</script>