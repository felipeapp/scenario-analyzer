<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Definir Coordenador de Tutoria</h2>
	<br>
	<h:form id="form">
	<h:messages showDetail="true"/>
	<h:inputHidden value="#{coordenacaoTutoria.confirmButton}"/>
	<h:inputHidden value="#{coordenacaoTutoria.obj.id}"/>

	<table class="formulario" width="100%">
	<caption class="listagem"> Cadastrar Coordenador de Tutoria </caption>

	<tr>
		<th> Data Início: </th>
		<td> <t:inputCalendar
						value="#{ coordenacaoTutoria.obj.dataInicio}"
						renderAsPopup="true" renderPopupButtonAsImage="true" size="12"
						maxlength="10" onkeypress="return(formataData(this, event))">
						<f:convertDateTime pattern="dd/MM/yyyy" />
					</t:inputCalendar>
		</td>
	</tr>	
	<tr>
		<th>Pessoa: </th>
		<td>
			<h:outputText value="#{coordenacaoTutoria.obj.pessoa.nome}"/>
			<h:inputHidden value="#{coordenacaoTutoria.obj.pessoa.id}"/>
			<h:inputHidden value="#{coordenacaoTutoria.obj.pessoa.nome}"/>
			<h:inputHidden value="#{coordenacaoTutoria.confirmado}"/>
		</td>
	</tr>
	
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="#{coordenacaoTutoria.confirmButton}" action="#{coordenacaoTutoria.confirmar}"/>
				<h:commandButton value="Cancelar" action="#{coordenacaoTutoria.cancelar}"/>
			</td>
		</tr>
	</tfoot>
	</h:form>

	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
