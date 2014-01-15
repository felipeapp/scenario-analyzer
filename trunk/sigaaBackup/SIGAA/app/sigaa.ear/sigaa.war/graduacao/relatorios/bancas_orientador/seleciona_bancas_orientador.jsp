<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2> <ufrn:subSistema/> > Relatório de Bancas por Orientador</h2>
	<h:form id="formConsulta">
	<h:outputText value="#{relatorioBancasOrientador.create}" />
		<table class="formulario" width="60%">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				
			<tr>
				<th class="obrigatorio">Orientador:</th>
				<td>
					<h:inputHidden id="idCoordenador" value="#{relatorioBancasOrientador.membroBanca.id}" /> 
					<h:inputText id="nomeCoordenador" value="#{relatorioBancasOrientador.membroBanca.pessoa.nome}"	size="60" onkeyup="CAPS(this);" /> 
					<ajax:autocomplete
						source="formConsulta:nomeCoordenador" target="formConsulta:idCoordenador"
						baseUrl="/sigaa/ajaxDocente" className="autocomplete"
						indicator="indicatorDocente" minimumCharacters="3"
						parameters="tipo=ufrn,situacao=ativo"
						parser="new ResponseXmlToHtmlListParser()" /> 
					<span id="indicatorDocente" style="display: none;"> 
						<img src="/sigaa/img/indicator.gif" alt="Carregando..." title="Carregando..." /> 
					</span>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Data Inicial:</th>
				<td>
					<t:inputCalendar id="Data_Inicial"
						value="#{ relatorioBancasOrientador.dataInicio }"
						renderAsPopup="true"
						readonly="#{relatorioBancasOrientador.readOnly}"
						disabled="#{relatorioBancasOrientador.readOnly}"
						renderPopupButtonAsImage="true"
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é"
						onkeypress="return(formataData(this,event))" size="10"
						maxlength="10">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th> Data Final: </th>
					<td>
					<t:inputCalendar id="dataFim"
						value="#{ relatorioBancasOrientador.dataFim }"
						renderAsPopup="true"
						readonly="#{relatorioBancasOrientador.readOnly}"
						disabled="#{relatorioBancasOrientador.readOnly}"
						renderPopupButtonAsImage="true"
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é"
						onkeypress="return(formataData(this,event))" size="10"
						maxlength="10">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Gerar Relatório" action="#{relatorioBancasOrientador.gerarRelatorioBancasOrientador}" id="btnGerar" /> 
						<h:commandButton value="Cancelar" action="#{relatorioBancasOrientador.cancelar}" id="cancelar" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
  <br />
  <br />
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>