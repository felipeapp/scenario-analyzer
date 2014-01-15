<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
<!--
table.subFormulario th{
	font-weight: bold;
}
-->
</style>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2 class="title"> <ufrn:subSistema /> &gt; Retificação de Aproveitamento e Consolidação de Turmas > Confirmação</h2>

	<h:messages showDetail="true"></h:messages>
		<c:set value="#{retificacaoMatricula.discenteEscolhido}" var="discente" />
		<%@ include file="/graduacao/info_discente.jsp"%>
<table class="formulario" width="90%">
	<tr>
		<th width="20%"><b>Componente C.:</b></th>
		<td> ${retificacaoMatricula.matriculaOriginal.componenteDescricaoResumida} </td>
	</tr>
	<c:if test="${!retificacaoMatricula.matriculaOriginal.aproveitado}">
	<tr>
		<th><b> Turma:</b> </th>
		<td> ${retificacaoMatricula.matriculaOriginal.turma.codigo} (${retificacaoMatricula.matriculaOriginal.turma.anoPeriodo})</td>
	</tr>
	<tr>
		<th><b> Docente(s):</b> </th>
		<td> ${retificacaoMatricula.matriculaOriginal.turma.docentesNomes} </td>
	</tr>
	</c:if>
</table>
<br />
	<table class="formulario" width="90%">
		<h:form id="formulario">
			<h:outputText value="#{retificacaoMatricula.create}" />
			<caption class="listagem">Retificação de Matrícula</caption>
			<tr>
				<td colspan="2">
				<table class="subFormulario" width="100%">
					<caption>Dados Consolidados</caption>
					<tr>
						<c:if test="${retificacaoMatricula.conceito}">
							<th width="15%">Conceito: </th>
							<td width="15%"><h:outputText value="#{retificacaoMatricula.matriculaOriginal.conceitoChar}" /></td>
						</c:if>
						
						<c:if test="${retificacaoMatricula.nota}">
							<th width="15%">Média Final: </th>
							<td width="15%"><h:outputText value="#{retificacaoMatricula.matriculaOriginal.mediaFinal}" /></td>
						</c:if>
						
						<th width="10%">Faltas: </th>
						<td width="15%"><h:outputText value="#{retificacaoMatricula.matriculaOriginal.numeroFaltas}" /></td>
						<th width="15%">Situação:</th>
						<td>${retificacaoMatricula.matriculaOriginal.situacaoMatricula.descricao}</td>

						<c:if test="${retificacaoMatricula.matriculaOriginal.aproveitado}">
							<th width="15%">Ano-Período:</th>
							<td>${retificacaoMatricula.matriculaOriginal.anoPeriodo}</td>
						</c:if>
					</tr>

				</table>
				</td>
			</tr>
			<tr>
				<td colspan="2">
				<table class="subFormulario" width="100%">
					<caption>Novos Dados da Consolidação da Turma</caption>
					<tr>
						<c:if test="${retificacaoMatricula.conceito}">
							<th width="15%">Conceito: </th>
							<td width="15%"><h:outputText value="#{retificacaoMatricula.matriculaModificada.conceitoChar}" /></td>
						</c:if>
						
						<c:if test="${retificacaoMatricula.nota}">
							<th width="15%">Média Final: </th>
							<td width="15%"><h:outputText value="#{retificacaoMatricula.matriculaModificada.mediaFinal}" /></td>
						</c:if>
						
						<th width="10%">Faltas: </th>
						<td width="15%"><h:outputText value="#{retificacaoMatricula.matriculaModificada.numeroFaltas}" /></td>
						<th width="15%">Situação:</th>
						<td>${retificacaoMatricula.matriculaModificada.situacaoMatricula.descricao}</td>
						
						<c:if test="${retificacaoMatricula.matriculaOriginal.aproveitado}">
							<th width="15%">Ano-Período:</th>
							<td>${retificacaoMatricula.matriculaModificada.anoPeriodo}</td>
						</c:if>
					</tr>

				</table>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="4"><h:commandButton value="Confirmar Retificação"
						action="#{retificacaoMatricula.processarRetificacao}" /> <h:commandButton value="Cancelar" onclick="#{confirm}"
						action="#{retificacaoMatricula.cancelar}" />
						<h:commandButton value="<< Alterar Novos Dados"
						action="#{retificacaoMatricula.telaMatriculaEscolhida}" />
						</td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
