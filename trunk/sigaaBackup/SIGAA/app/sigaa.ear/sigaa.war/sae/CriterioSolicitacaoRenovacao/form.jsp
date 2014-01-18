<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2 class="tituloPagina"> <ufrn:subSistema/> > Critério Renovação Bolsa</h2>
	<h:form id="form">
		<h:inputHidden value="#{criterioSolicitacaoRenovacaoMBean.obj.id}" />
		<table class="formulario" width="100%">
			<caption>Critérios</caption>
		
				<tr>
					<th>Bolsa Auxilio: <span class="obrigatorio"></span> </th>
					<td>
						<h:selectOneMenu value="#{criterioSolicitacaoRenovacaoMBean.obj.tipoBolsaAuxilio.id}">
							<f:selectItem  itemLabel="-- SELECIONE -- " itemValue="0" /> 
							<f:selectItems value="#{tipoBolsaAuxilioMBean.allCombo}" /> 
						</h:selectOneMenu>
						
					</td>
				</tr>

				<tr>
					<th>Situação Auxilio: <span class="obrigatorio"></span> </th>
					<td>
						<h:selectOneMenu value="#{criterioSolicitacaoRenovacaoMBean.obj.situacaoBolsa.id}">
							<f:selectItem  itemLabel="-- SELECIONE -- " itemValue="0" /> 
							<f:selectItems value="#{situacaoBolsaAuxilioMBean.allCombo}" /> 
						</h:selectOneMenu>
						
					</td>
				</tr>

				<tr>
					<th>Tipo Renovação: <span class="obrigatorio"></span> </th>
					<td>
						<h:selectOneMenu value="#{criterioSolicitacaoRenovacaoMBean.obj.tipoRenovacao.id}">
							<f:selectItem  itemLabel="-- SELECIONE -- " itemValue="0" /> 
							<f:selectItems value="#{criterioSolicitacaoRenovacaoMBean.allTipoRenovacao}" /> 
						</h:selectOneMenu>
						
					</td>
				</tr>

			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Cadastrar" action="#{criterioSolicitacaoRenovacaoMBean.cadastrar}" id="submeter" />
						<h:commandButton value="Cancelar" action="#{criterioSolicitacaoRenovacaoMBean.cancelar}" id="cancelarOperacao" onclick="#{confirm}" /> 
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