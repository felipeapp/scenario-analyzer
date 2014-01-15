<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.MEMBRO_COMITE_INTEGRADO } %>">

<f:view>
	<a4j:keepAlive beanName="alteracaoProjetoMBean" />
	
	<h2><ufrn:subSistema /> > Gestão de Projetos Vinculados da Ação Acadêmica</h2>
	<br>
	<h:form id="form">

		<table class="formulario" width="100%">
			<caption class="listagem">Dados da Ação</caption>

			<tr>
				<th width="18%">Nº Institucional:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.numeroInstitucional}" />/<h:outputText value="#{alteracaoProjetoMBean.obj.ano}" /></td>
			</tr>

			<tr>
				<th width="18%">Título:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.titulo}" /></td>
			</tr>
			
			<tr>
				<th>Ano:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.ano}" /></td>
			</tr>

			<tr>
				<th>Período:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.dataInicio}" /> até <h:outputText value="#{alteracaoProjetoMBean.obj.dataFim}" /></td>
			</tr>

			<tr>
				<th>Coordenador(a):</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.coordenador.pessoa.nome}" /></td>
			</tr>

			<tr>
				<th>Situação:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.situacaoProjeto.descricao}" /></td>
			</tr>

			<tr>
				<th>Dimensão Acadêmica:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.dimensaoAcademica}" /></td>
			</tr>

			
		</table>
			
			
			<br/>
				<div class="infoAltRem">
    				<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar
    				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" rendered="#{acesso.comissaoIntegrada}"/> <h:outputText rendered="#{acesso.comissaoIntegrada}" value=": Alterar Cadastro" escape="false"/>
    				<h:graphicImage value="/img/alterar_old.gif" style="overflow: visible;" rendered="#{acesso.comissaoIntegrada}"/> <h:outputText rendered="#{acesso.comissaoIntegrada}" value=": Alterar Situação" escape="false"/>
    				<h:graphicImage value="/img/bolsas.png" style="overflow: visible;" rendered="#{acesso.comissaoIntegrada}"/> <h:outputText rendered="#{acesso.comissaoIntegrada}" value=": Alterar Bolsas" escape="false"/>
    				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" rendered="#{acesso.comissaoIntegrada}"/> <h:outputText rendered="#{acesso.comissaoIntegrada}" value=": Remover" escape="false"/>
				</div>
			<br/>
		
		
		<table class="formulario" width="100%">
			<caption class="listagem">Ações Vinculadas</caption>
			
			<tr>
				<td class="subFormulario" colspan="2"><center><b>Projetos de Ensino</b></center></td>
			</tr>
			
			<tr>
				<td colspan="2">
					<t:dataTable id="dtEnsino" value="#{alteracaoProjetoMBean.projetosEnsino}" var="pe"
					 	align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" 
					 	rowIndexVar="index" forceIdIndex="true" rendered="#{not empty alteracaoProjetoMBean.projetosEnsino}">
								<t:column  width="50%">
									<f:facet name="header"><f:verbatim>Tipo de Projeto</f:verbatim></f:facet>
									<h:outputText value="#{pe.tipoProjetoEnsino.descricao}" />
								</t:column>
								
								<t:column>
									<f:facet name="header"><f:verbatim>Data do Cadastro</f:verbatim></f:facet>
									<h:outputText value="#{pe.dataCadastro}" />
								</t:column>
								
								<t:column>
									<f:facet name="header"><f:verbatim>Data do Envio</f:verbatim></f:facet>
									<h:outputText value="#{pe.dataEnvio}" />
								</t:column>
								
								<t:column>
									<f:facet name="header"><f:verbatim>Situação</f:verbatim></f:facet>
									<h:outputText value="#{pe.situacaoProjeto.descricao}" />
								</t:column>

								<t:column>
									<f:facet name="header"><f:verbatim>Bolsas Solicitadas</f:verbatim></f:facet>
									<h:outputText value="#{pe.bolsasSolicitadas}" />
								</t:column>
								
								<t:column>
									<f:facet name="header"><f:verbatim>Bolsas Concedidas</f:verbatim></f:facet>
									<h:outputText value="#{pe.bolsasConcedidas}" />
								</t:column>
								
								<t:column>
									<h:commandLink title="Visualizar" action="#{ projetoMonitoria.view }" immediate="true" id="visualizar_ensino">
							        	<f:param name="id" value="#{pe.id}"/>
				    					<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>
									</h:commandLink>
								</t:column>
								
								<t:column>			
									<h:commandLink title="Alterar Cadastro" action="#{projetoMonitoria.atualizar}" style="border: 0;" id="alterar_cadastro_ensino" >
								    	<f:param name="id" value="#{pe.id}"/>				    	
										<h:graphicImage url="/img/alterar.gif"/>
									</h:commandLink>
								</t:column>
								
								<t:column>
									<h:commandLink title="Alterar Situação do Projeto" action="#{ projetoMonitoria.iniciarAlterarSituacaoProjeto }" immediate="true" id="alterar_situacao_ensino">
							       		<f:param name="id" value="#{pe.id}"/>
				                   		<h:graphicImage url="/img/alterar_old.gif" />
									</h:commandLink>
								</t:column>
								
								<t:column>
									<h:commandLink  title="Alterar Bolsas Concedidas" action="#{ movimentacaoCotasMonitoria.selecionarProjeto }" immediate="true" styleClass="noborder" id="alterar_bolsas_ensino">
									    	<f:param name="id" value="#{pe.id}"/>
									      	<h:graphicImage url="/img/bolsas.png" />
									</h:commandLink>
								</t:column>															
								
				
								<t:column width="2%">	
									<h:commandLink title="Remover" action="#{projetoMonitoria.preRemover}" style="border: 0;" id="remover_ensino">
								    	<f:param name="id" value="#{pe.id}"/>				    	
										<h:graphicImage url="/img/delete.gif" />
									</h:commandLink>
								</t:column>

						</t:dataTable>
				</td>
		</tr>
							
		<c:if test="${empty alteracaoProjetoMBean.projetosEnsino}">
			<tr><td colspan="6" align="center"><font color="red">Não há Projetos de Ensino cadastrados</font> </td></tr>
		</c:if>

<%-- 
			<tr>
				<td class="subFormulario" colspan="2"><center><b>Projeto de Pesquisa</b></center></td>
			</tr>
			
			<tr>
				<td colspan="2">
					<t:dataTable id="dtPesquisa" value="#{alteracaoProjetoMBean.projetosEnsino}" var="pe"
					 	align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" 
					 	rowIndexVar="index" forceIdIndex="true" rendered="#{false}">
								<t:column width="50%">
									<f:facet name="header"><f:verbatim>Tipo de Projeto</f:verbatim></f:facet>
									<h:outputText value="#{pe.tipoProjetoEnsino.descricao}" />
								</t:column>
								
								<t:column>
									<f:facet name="header"><f:verbatim>Data do Cadastro</f:verbatim></f:facet>
									<h:outputText value="#{pe.dataCadastro}" />
								</t:column>
								
								<t:column>
									<f:facet name="header"><f:verbatim>Data do Envio</f:verbatim></f:facet>
									<h:outputText value="#{pe.dataEnvio}" />
								</t:column>
								
								<t:column>
									<f:facet name="header"><f:verbatim>Situação</f:verbatim></f:facet>
									<h:outputText value="#{pe.situacaoProjeto.descricao}" />
								</t:column>

								<t:column>
									<f:facet name="header"><f:verbatim>Bolsas Solicitadas</f:verbatim></f:facet>
									<h:outputText value="#{pe.bolsasSolicitadas}" />
								</t:column>
								
								<t:column>
									<f:facet name="header"><f:verbatim>Bolsas Concedidas</f:verbatim></f:facet>
									<h:outputText value="#{pe.bolsasConcedidas}" />
								</t:column>

						</t:dataTable>
				</td>
		</tr>

		<c:if test="${empty alteracaoProjetoMBean.projetosEnsino}">
			<tr><td colspan="6" align="center"><font color="red">Não há Projetos de Pesquisa cadastrados</font> </td></tr>
		</c:if>
--%>							



		<tr>
			<td class="subFormulario" colspan="2"><center><b>Ações de Extensão</b></center></td>
		</tr>
			
		<tr>
				<td colspan="2">
					<t:dataTable id="dtExtensao" value="#{alteracaoProjetoMBean.acoesExtensao}" var="acao"
					 	align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" 
					 	rowIndexVar="index" forceIdIndex="true" rendered="#{not empty alteracaoProjetoMBean.acoesExtensao}">
								<t:column  width="50%">
									<f:facet name="header"><f:verbatim>Tipo de Ação</f:verbatim></f:facet>
									<h:outputText value="#{acao.tipoAtividadeExtensao.descricao}" />
								</t:column>
								
								<t:column>
									<f:facet name="header"><f:verbatim>Data do Cadastro</f:verbatim></f:facet>
									<h:outputText value="#{acao.dataCadastro}" />
								</t:column>
								
								<t:column>
									<f:facet name="header"><f:verbatim>Data do Envio</f:verbatim></f:facet>
									<h:outputText value="#{acao.dataEnvio}" />
								</t:column>
								
								<t:column>
									<f:facet name="header"><f:verbatim>Situação</f:verbatim></f:facet>
									<h:outputText value="#{acao.situacaoProjeto.descricao}" />
								</t:column>

								<t:column>
									<f:facet name="header"><f:verbatim>Bolsas Solicitadas</f:verbatim></f:facet>
									<h:outputText value="#{acao.bolsasSolicitadas}" />
								</t:column>
								
								<t:column>
									<f:facet name="header"><f:verbatim>Bolsas Concedidas</f:verbatim></f:facet>
									<h:outputText value="#{acao.bolsasConcedidas}" />
								</t:column>

								<t:column>
									<h:commandLink title="Visualizar Ação de Extensão" action="#{ atividadeExtensao.view }" immediate="true" id="visualizar_acao_extensao_">
										        <f:param name="id" value="#{acao.id}"/>
									    		<h:graphicImage url="/img/view.gif" />
									</h:commandLink>
								</t:column>

								<t:column>
									<h:commandLink title="Alterar Cadastro" action="#{ atividadeExtensao.preAtualizar }" immediate="true" id="alterar_cadastro_acao_extensao_">
									        <f:param name="id" value="#{acao.id}"/>
								    		<h:graphicImage url="/img/alterar.gif" />
									</h:commandLink>
								</t:column>
								
								<t:column>
									<h:commandLink title="Alterar Situação" action="#{ atividadeExtensao.iniciarAlterarSituacaoAtividade }" id="alterar_situacao_acao_extensao_">
										        <f:param name="id" value="#{acao.id}"/>
									    		<h:graphicImage url="/img/alterar_old.gif"/>
									</h:commandLink>
								</t:column>

								<t:column>
									<h:commandLink title="Alterar Bolsas Concedidas" action="#{ movimentacaoCotasExtensao.selecionarAtividade }" id="alterar_bolsas_acao_extensao_">
									        <f:param name="id" value="#{acao.id}"/>
									   		<h:graphicImage url="/img/bolsas.png"/>
									</h:commandLink>
								</t:column>
								
								<t:column>
									<h:commandLink title="Remover" action="#{ atividadeExtensao.preRemover }" immediate="true" id="remover_acao_extensao_vinculada_">
									        <f:param name="id" value="#{acao.id}"/>
								    		<h:graphicImage url="/img/delete.gif" />
									</h:commandLink>
								</t:column>
						
								
						</t:dataTable>
				</td>
		</tr>
							
		<c:if test="${empty alteracaoProjetoMBean.acoesExtensao}">
			<tr><td colspan="6" align="center"><font color="red">Não há Ações de Extensão cadastradas</font> </td></tr>
		</c:if>

		<tfoot>
			<tr>
				<td colspan="2">					
					<center>
						<input type="button" value="<< Voltar" onclick="javascript:history.go(-1)" />
						<h:commandButton value="Cancelar" action="#{alteracaoProjetoMBean.cancelar}" onclick="#{confirm}"/>
					</center>
				</td>
			</tr>
		</tfoot>
	</table>
		
</h:form>
</f:view>

</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>