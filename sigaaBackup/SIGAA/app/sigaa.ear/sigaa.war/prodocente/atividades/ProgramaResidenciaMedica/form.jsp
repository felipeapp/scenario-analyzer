<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Programa de Residência Médica</h2>

	<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
		<ufrn:link action="/prodocente/atividades/ProgramaResidenciaMedica/lista.jsf" 
			value="Listar Programas de Residências Médicas Cadastrados"/>
	 </div>
    </h:form>

	<a4j:keepAlive beanName="programaResidencia"/>
	<h:form id="form">
	<table class=formulario width="80%">
			<caption class="listagem">Cadastro de Programa de Residência Médica</caption>
			<h:inputHidden value="#{programaResidencia.confirmButton}" />
			<h:inputHidden value="#{programaResidencia.obj.id}" />
			<tr>
				<th class="required">Nome:</th>
				<td>
					<h:inputText value="#{programaResidencia.obj.nome}"  
					   maxlength="50" size="50" readonly="#{programaResidencia.readOnly}" id="nome" />
				 </td>
			</tr>
			<tr>
				<th class="required">Hospital:</th>
				<td>
					<h:selectOneMenu value="#{programaResidencia.obj.hospital.id}" 
						disabled="#{programaResidencia.readOnly}" disabledClass="#{programaResidencia.disableClass}" id="hospital"
						valueChangeListener="#{programaResidencia.changePrograma}" onchange="submit();">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE UM HOSPITAL --- " />
						<f:selectItems value="#{unidade.hospitais}" />
					</h:selectOneMenu>
				 </td>
			</tr>
			<tr>
				<th class="required">Unidade do Programa:</th>
				<td style="width: 50">
					<h:selectOneMenu value="#{programaResidencia.obj.unidadePrograma.id}" id="unidade" immediate="true">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE UM PROGRAMA --- " />
						<f:selectItems value="#{programaResidencia.allProgramas}" />
					</h:selectOneMenu>
				 </td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
					<h:commandButton id="btConfirmarAcao"
						value="#{programaResidencia.confirmButton}" action="#{programaResidencia.cadastrar}" />
					<h:commandButton value="Cancelar" id="btCancelar" action="#{programaResidencia.cancelar}" 
					 		onclick="#{confirm}" immediate="true"/>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>