<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Convênio</h2>
	<h:form id="form">
	
		<h:inputHidden value="#{convenioBiblioteca.obj.id}" />
	
		<table class="formulario">
			<caption> Cadastro de Convenios</caption>
			<tr>
				<th class="obrigatorio">Nome:</th>
				<td><h:inputText value="#{convenioBiblioteca.obj.nome}" size="50" maxlength="200" readonly="#{convenioBiblioteca.readOnly}" disabled="#{convenioBiblioteca.readOnly}" /></td>
			</tr>
			<tr>
				<th class="obrigatorio">Início:</th>
				<td>
					<t:inputCalendar id="Inicio" value="#{convenioBiblioteca.obj.dataInicio}" renderAsPopup="true" readonly="#{convenioBiblioteca.readOnly}" disabled="#{convenioBiblioteca.readOnly}"  renderPopupButtonAsImage="true" onkeypress="return(formataData(this,event))" size="10" maxlength="10" popupDateFormat="dd/MM/yyyy" /> 
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Fim:</th>
				<td>
					<t:inputCalendar id="Fim" value="#{convenioBiblioteca.obj.dataFim}" renderAsPopup="true" readonly="#{convenioBiblioteca.readOnly}" disabled="#{convenioBiblioteca.readOnly}"  renderPopupButtonAsImage="true" onkeypress="return formataData(this,event)" size="10" maxlength="10" popupDateFormat="dd/MM/yyyy" /> 
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:inputHidden value="#{convenioBiblioteca.obj.id}"/>
						<h:commandButton value="#{convenioBiblioteca.confirmButton}" action="#{convenioBiblioteca.cadastrar}"/>
						<h:commandButton value="<< Voltar" action="#{convenioBiblioteca.listar}" immediate="true"/>
						<h:commandButton value="Cancelar" action="#{convenioBiblioteca.cancelar}" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
