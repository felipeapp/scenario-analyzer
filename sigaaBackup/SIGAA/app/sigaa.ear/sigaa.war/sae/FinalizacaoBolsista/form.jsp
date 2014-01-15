<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<h2 class="tituloPagina"> <ufrn:subSistema/> > Finalização dos Bolsistas </h2>
	
	<h:form id="form">
		<table class="formulario" width="40%">
			<caption>Finalizar Bolsistas</caption>
			<tr>
				<th class="obrigatorio">Ano-Período:</th>
				<td>
					<h:inputText value="#{finalizarBolsaAuxilioMBean.ano}" maxlength="4" size="4" onkeyup="return formatarInteiro(this);"/> - 
					<h:inputText value="#{finalizarBolsaAuxilioMBean.periodo}" maxlength="1" size="2" onkeyup="return formatarInteiro(this);"/>
				</td>
			</tr>
			
			<tr>
				<th>Tipo Bolsa:</th>
				<td>
					<h:selectOneMenu value="#{ finalizarBolsaAuxilioMBean.obj.bolsaAuxilio.tipoBolsaAuxilio.id }" id="idTipoBolsa" onchange="$('checkTipoBolsa').checked = true;">
						<f:selectItem itemLabel="-- SELECIONE -- " itemValue="0"/>
						<f:selectItems value="#{relatorioAcompanhamentoBolsas.allComboBolsaAuxilio}"/>
					</h:selectOneMenu>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="4" align="center">
						<h:commandButton value="Buscar" action="#{finalizarBolsaAuxilioMBean.buscar}" id="submeter" />
						<h:commandButton value="Cancelar" action="#{finalizarBolsaAuxilioMBean.cancelar}" id="cancelarOperacao" onclick="#{confirm}" /> 
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>
	
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
</center>
	

</f:view>


<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>