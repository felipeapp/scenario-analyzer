<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<f:view>
	<a4j:keepAlive beanName="alteracaoProjetoMBean" />
	
	<h2><ufrn:subSistema /> > Alterar Orçamento Concedido da Ação Acadêmica</h2>
	<br>
	<h:form id="form">

		<table class="formulario" width="100%">
			<caption class="listagem">Alterar Orçamento</caption>

			<tr>
				<th width="18%" class="rotulo">Nº Institucional:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.numeroInstitucional}" />/<h:outputText value="#{alteracaoProjetoMBean.obj.ano}" /></td>
			</tr>

			<tr>
				<th width="18%" class="rotulo">Título:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.titulo}" /></td>
			</tr>
			
			<tr>
				<th class="rotulo">Ano:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.ano}" /></td>
			</tr>

			<tr>
				<th class="rotulo">Período:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.dataInicio}" /> até <h:outputText value="#{alteracaoProjetoMBean.obj.dataFim}" /></td>
			</tr>


			<tr>
				<th class="rotulo">Coordenador(a):</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.coordenador.pessoa.nome}" /></td>
			</tr>

			<tr>
				<th class="rotulo">Situação:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.situacaoProjeto.descricao}" /></td>
			</tr>
			
			<tr>
				<td class="subFormulario" colspan="2"><b>Orçamento</b></td>
			</tr>
			
			<tr>
				<td colspan="2">
					<t:dataTable id="dt" value="#{alteracaoProjetoMBean.obj.orcamentoConsolidado}" var="consolidacao"
					 	align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" 
					 	rowIndexVar="index" forceIdIndex="true" rendered="#{not empty alteracaoProjetoMBean.obj.orcamentoConsolidado}">
								<t:column>
									<f:facet name="header"><f:verbatim>Descrição</f:verbatim></f:facet>
									<h:outputText value="#{consolidacao.elementoDespesa.descricao}" />
								</t:column>

								<t:column style="text-align: right">
									<f:facet name="header"><f:verbatim>Financ. Solicitado</f:verbatim></f:facet>
									<f:verbatim>R$ </f:verbatim>
									<h:outputText value="#{consolidacao.fundo}"  id="fundoSolicitado" >
										<f:convertNumber pattern="#,##0.00"/>
									</h:outputText>
								</t:column>

								<t:column style="text-align: right">
									<f:facet name="header"><f:verbatim>Financ. Concedido</f:verbatim></f:facet>
									<f:verbatim>R$ </f:verbatim>
									<h:inputText value="#{consolidacao.fundoConcedido}"  id="fundoConcedido" size="12" onkeypress="return(formataValor(this, event, 2))">
											<f:convertNumber pattern="#,##0.00"/>
									</h:inputText>															
								</t:column>
						</t:dataTable>
				</td>
		</tr>
							
		<c:if test="${empty alteracaoProjetoMBean.obj.orcamentoConsolidado}">
			<tr><td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font> </td></tr>
		</c:if>

		<tfoot>
			<tr>
				<td colspan="2">					
					<h:commandButton value="Confirmar Alteração" action="#{alteracaoProjetoMBean.alterarOrcamento}" rendered="#{acesso.comissaoIntegrada || acesso.extensao}" /> 
					<h:commandButton value="Cancelar" action="#{alteracaoProjetoMBean.cancelar}" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
	</table>
		
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>