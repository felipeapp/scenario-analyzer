<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Coordenadores de um Pólo > Cadastrar</h2>
	<br>
	<h:form id="form">
	<h:messages showDetail="true"/>
	<h:inputHidden value="#{coordenacaoPolo.confirmButton}"/>
	<h:inputHidden value="#{coordenacaoPolo.obj.id}"/>

	<table class="formulario" width="100%">
	<caption class="listagem"> Cadastrar Coordenador de Pólo </caption>

	<tr>
		<th> Data Início: </th>
		<td> <t:inputCalendar id="calendario"
						value="#{ coordenacaoPolo.obj.inicio}"
						renderAsPopup="true" renderPopupButtonAsImage="true" size="12"
						maxlength="10" onkeypress="return(formataData(this, event))"
						disabled="#{coordenacaoPolo.readOnly}">
						<f:convertDateTime pattern="dd/MM/yyyy" />
					</t:inputCalendar>
		</td>
	</tr>
	<tr>
		<th> Pólo: </th>
		<td> 
			<h:selectOneMenu value="#{ coordenacaoPolo.obj.polo.id }" id="polo" disabled="#{coordenacaoPolo.readOnly}">
				<f:selectItems value="#{ coordenacaoPolo.allPolos }"/>	
			</h:selectOneMenu>
		</td>
	</tr>
	
	<tr>
		<th class="rotulo">Pessoa: </th>
		<td>
			<h:outputText value="#{coordenacaoPolo.obj.pessoa.nome}"/>
			<h:inputHidden value="#{coordenacaoPolo.obj.pessoa.nome}"/>
			<h:inputHidden value="#{coordenacaoPolo.obj.pessoa.id}"/>
		</td>
	</tr>
	
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="#{coordenacaoPolo.confirmButton}" action="#{coordenacaoPolo.cadastrar}"/>
				<h:commandButton value="Cancelar" action="#{coordenacaoPolo.cancelar}" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>
	</h:form>

	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
