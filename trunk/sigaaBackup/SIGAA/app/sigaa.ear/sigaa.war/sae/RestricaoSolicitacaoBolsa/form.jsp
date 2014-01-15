<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<h2 class="title"><ufrn:subSistema /> > Restrição Acumulo de Bolsas </h2>
	
	<div class="descricaoOperacao">
		<p>
			O formulário abaixo serve para cadastrar as restrições para o acumulo de bolsas por parte dos discentes, por exemplo: <br />
		</p>
		<p>
			Se um bolsista pode ou não ser contemplado em mais de uma bolsa, ou se um mesmo discente pode solicitar mais de uma bolsa.
		</p>
	</div>
	
	<h:form id="form">
		<table width="100%" class="formulario">
			<caption> Cadastro de Restrição Solicitação de Bolsa</caption>
			 <tr>
			 	<th width="35%" class="obrigatorio">Tipo da Bolsa:</th>
				<td>			 
					<h:selectOneMenu value="#{restricaoSolicitacaoBolsaAuxilioMBean.obj.tipoBolsaAuxilio.id}" id="tipoBolsa"
							valueChangeListener="#{restricaoSolicitacaoBolsaAuxilioMBean.carregarRestricoes}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItems value="#{calendarioBolsaAuxilioMBean.tiposBolsaAuxilio}" />
						<a4j:support event="onchange" reRender="form" />
					</h:selectOneMenu>
				</td>
			 </tr>					
			 
			 <tr>
			 	<td colspan="2" class="subFormulario">
			 		Restrição Solicitação e Validação da Bolsa		
			 	</td>
			 </tr>
			 
			 <tr>
			 	<th class="obrigatorio">Tipo da Bolsa Restrição:</th>
				<td>			 
					<h:selectOneMenu value="#{restricaoSolicitacaoBolsaAuxilioMBean.restricao.tipoBolsaAuxilio.id}" id="tipoBolsaRestricao">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItems value="#{calendarioBolsaAuxilioMBean.tiposBolsaAuxilio}" />
					</h:selectOneMenu>
				</td>
			 </tr>	
			 <tr>
				<th class="obrigatorio">Situação da Bolsa:</th>
				<td>
					<h:selectOneMenu id="situacaoBolsa" immediate="true" value="#{restricaoSolicitacaoBolsaAuxilioMBean.restricao.situacao.id}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItems value="#{situacaoBolsaAuxilioMBean.allCombo}" /> 
					</h:selectOneMenu>									
				</td>
			 </tr>
			 <tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton value="Adicionar" id="adicionar" action="#{restricaoSolicitacaoBolsaAuxilioMBean.adicionarRestricaoBolsa}">
							<a4j:support reRender="tipoBolsaRestricao, situacaoBolsa" />
						</h:commandButton> 
					</td>
				</tr>
			 </tfoot>
		</table>									

	<a4j:region rendered="#{ not empty restricaoSolicitacaoBolsaAuxilioMBean.obj.restricao }">
		
			<br />
		
			<table width="100%" class="formulario" border="1">
					
					<caption>Restrições já cadastradas (${ fn:length(restricaoSolicitacaoBolsaAuxilioMBean.obj.restricao) })</caption>
					
					<tr>
						<td>
							<t:dataTable value="#{restricaoSolicitacaoBolsaAuxilioMBean.lista}" var="linha" align="center" width="100%" 
								styleClass="listagem" rowClasses="linhaPar, linhaImpar" id="tbCalendario">
								
								<t:column>
									<f:facet name="header"><f:verbatim>Tipo Bolsa</f:verbatim></f:facet>
									<h:outputText value="#{linha.bolsasAuxilioRestricao.denominacao}" />
								</t:column>
				
								<t:column>
									<f:facet name="header"><f:verbatim>Situação Bolsa</f:verbatim></f:facet>
									<h:outputText value="#{linha.situacao.denominacao}" />
								</t:column>										
								
							    <h:column>
									<h:commandLink action="#{restricaoSolicitacaoBolsaAuxilioMBean.removerRestricao}"
										onclick=" if(!confirm('Deseja realmente remover esse registro ?')) { return false; }">
										<h:graphicImage value="/img/delete.gif" alt="Remover registro"/>
									</h:commandLink>
			 				    </h:column>
				
							</t:dataTable>
						</td>
					</tr>
			</table>
		
		</a4j:region>

		<table width="100%" class="formulario">
			<tfoot>
				<tr>
					<td>
						<h:commandButton value="#{restricaoSolicitacaoBolsaAuxilioMBean.confirmButton}" id="confirmar" action="#{restricaoSolicitacaoBolsaAuxilioMBean.cadastrar}" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{restricaoSolicitacaoBolsaAuxilioMBean.cancelar}" id="cancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<center>
		<br/>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena">Campos de preenchimento obrigatório. </span> 
	</center>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>