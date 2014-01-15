<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.MEMBRO_COMITE_INTEGRADO } %>">

<f:view>
	<a4j:keepAlive beanName="alteracaoProjetoMBean" />
	
	<h2><ufrn:subSistema /> > Conceder Recursos à Ação Acadêmica</h2>
	<br>
	<h:form id="form">

		<table class="formulario" width="100%">
			<caption class="listagem">Definição dos Recursos da Ação</caption>

			<tr>
				<th width="22%" class="rotulo">Nº Institucional:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.numeroInstitucional}" />/<h:outputText value="#{alteracaoProjetoMBean.obj.ano}" /></td>
			</tr>

			<tr>
				<th class="rotulo">Ano:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.ano}" /></td>
			</tr>
			
			<tr>
				<th class="rotulo">Título:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.titulo}" /></td>
			</tr>
			
			<tr>
				<th class="rotulo">Unidade:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.unidade.nome}" /></td>
			</tr>

			<tr>
				<th class="rotulo">Área do CNPq:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.areaConhecimentoCnpq.nome}" /></td>
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
				<th class="rotulo">Classificação:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.classificacao}" /></td>
			</tr>
			
			<tr>
				<th class="rotulo">Média:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.media}" /></td>
			</tr>


			<tr>
				<th class="rotulo">Bolsas Solicitadas:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.bolsasSolicitadas}" /></td>
			</tr>
			
			<tr>
				<th class="rotulo">Situação:</th>
				<td>
					<h:selectOneMenu value="#{alteracaoProjetoMBean.obj.situacaoProjeto.id}" id="selectSituacao">
						<f:selectItems value="#{tipoSituacaoProjeto.situacoesAcoesAssociadasAnalisandoProposta}" />
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<th class="rotulo">Bolsas Concedidas:</th>
				<td>
					<h:inputText value="#{alteracaoProjetoMBean.obj.bolsasConcedidas}" maxlength="3" size="5" id="bolsasConcedidas" onkeyup="return formatarInteiro(this)"/>
				</td>
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

								<t:column>
									<f:facet name="header"><f:verbatim>Financ. Solicitado</f:verbatim></f:facet>
									<f:verbatim>R$ </f:verbatim>
									<h:outputText value="#{consolidacao.fundo}"  id="fundoSolicitado" >
										<f:convertNumber pattern="#,##0.00"/>
									</h:outputText>
								</t:column>

								<t:column>
									<f:facet name="header"><f:verbatim>Financ. Concedido</f:verbatim></f:facet>
									<f:verbatim>R$ </f:verbatim>
									<h:inputText value="#{consolidacao.fundoConcedido}"  id="fundoConcedido" size="14" onkeypress="return(formataValor(this, event, 2))">
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
					<h:commandButton value="Confirmar" action="#{alteracaoProjetoMBean.concederRecursos}" rendered="#{acesso.comissaoIntegrada}" />
					<input type="button" value="<< Voltar" onclick="javascript:history.go(-1)" /> 
					<h:commandButton value="Cancelar" action="#{alteracaoProjetoMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
				</td>
			</tr>
		</tfoot>
	</table>
		
</h:form>
</f:view>

</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>