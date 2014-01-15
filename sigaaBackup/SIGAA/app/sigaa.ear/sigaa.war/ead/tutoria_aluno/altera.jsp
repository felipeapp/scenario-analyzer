<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Cadastro de Tutoria de Alunos</h2>

	<h:messages showDetail="true"></h:messages>
	<br>
	<table class="formulario" width="90%">
		<h:form id="formulario">
			<h:outputText value="#{tutoriaAluno.create}" />
			<h:outputText value="#{tutoriaAluno.carregarPossiveisTutores }"></h:outputText>
			<caption class="listagem">Cadastro de Tutorias de Aluno</caption>
			<tr>
				<th>Curso:</th>
				<td><h:outputText value="#{tutoriaAluno.obj.aluno.curso.descricao}" /></td>
			</tr>
			<tr>
				<th>Pólo:</th>
				<td><h:outputText value="#{tutoriaAluno.obj.aluno.polo.cidade.nome}" /></td>
			</tr>
			<tr>
				<th>Aluno:</th>
				<td><h:outputText value="#{tutoriaAluno.obj.aluno.nome}"/></td>
			</tr>
			<tr>
				<th>Tutor:</th>
				<td><h:selectOneMenu value="#{tutoriaAluno.obj.tutor.id}" id="tutor" disabled="#{tutoriaAluno.readOnly }">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{tutoriaAluno.possiveisTutores}" />
				</h:selectOneMenu> <span class="required">&nbsp;</span></td>
			</tr>
			<tr>
				<th>Data Inicial: </th>
				<td><t:inputCalendar value="#{tutoriaAluno.obj.inicio}" size="10" maxlength="10" disabled="#{tutoriaAluno.readOnly }"
					renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" >
						<f:converter converterId="convertData"/>	
					</t:inputCalendar>
					<span class="required">&nbsp;</span></td>
			</tr>
			<tr>
				<th>Data Final: </th>
				<td><t:inputCalendar value="#{tutoriaAluno.obj.fim}" size="10" maxlength="10" disabled="#{tutoriaAluno.readOnly }"
					renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy">
						<f:converter converterId="convertData"/>	
					</t:inputCalendar>
			    </td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<c:if test="${!tutoriaAluno.remover}">
							<h:commandButton value="#{tutoriaAluno.confirmButton}" action="#{tutoriaAluno.alterar}" /> 
						</c:if>
						<c:if test="${tutoriaAluno.remover}">
							<h:commandButton value="#{tutoriaAluno.confirmButton}" onclick="if (!confirm(\"Tem certeza que deseja remover esta tutoria?\")) return false;" action="#{tutoriaAluno.alterar}" /> 
						</c:if>
						<input value="<< Voltar" type="button" onClick="history.go(-1)"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{tutoriaAluno.cancelar}" />
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
