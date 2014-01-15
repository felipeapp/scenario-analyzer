<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>
<f:view>

<a4j:keepAlive beanName="movimentacaoCotasMonitoria"/>

<h2><ufrn:subSistema /> >  Movimentação de Cotas Entre Projetos</h2>
<h:form id="form" >

	<table class="formulario" width="100%">
				<caption>Dados do Edital</caption>		
				
				<tr>		
					<th width="25%">Edital:</th>
					<td><b>
						 <h:outputText value="#{movimentacaoCotasMonitoria.obj.projetoEnsino.editalMonitoria.numeroEdital}"/> 
						 (<h:outputText value="#{movimentacaoCotasMonitoria.obj.projetoEnsino.editalMonitoria.descricao}"/>) 
						 </b>
					 </td>
				</tr>
				
				<tr>		
					<th>Número de bolsas do edital:</th>
					<td>
						<b><h:outputText value="#{movimentacaoCotasMonitoria.obj.projetoEnsino.editalMonitoria.numeroBolsas}"/> </b>
					</td>
				</tr>
				
				<tr>		
					<th>Total de Bolsistas Ativos: </th>
					<td>
						<b><h:outputText value="#{movimentacaoCotasMonitoria.qtdBolsistas}"/></b>
					</td>
				</tr>	
				
				<tr>		
					<th> Total de Voluntários Ativos: </th>
					<td>					
						<b><h:outputText value="#{movimentacaoCotasMonitoria.qtdVoluntarios}"/></b>
					</td>
				</tr>																

				<tr>		
					<th>Quantidade de Bolsas Livres nos Projetos:</th>
					<td>
						<font color="red"><b><h:outputText value="#{movimentacaoCotasMonitoria.obj.projetoEnsino.editalMonitoria.numeroBolsas - movimentacaoCotasMonitoria.qtdBolsistas}"/></b></font>
					</td>
				</tr>
	</table>
<br/>

	<table class="formulario" width="100%">
		<caption>Movimentar Cotas</caption>
	
		<tbody>
			<tr>
				<td>			
					<table class="subFormulario" width="100%">
						<caption>Movimentação</caption>		
				

						<tr>
							<th width="25%" class="required"> Operação: </th>
							<td> 
									<h:selectOneRadio id="tipo_movimentacao" value="#{movimentacaoCotasMonitoria.obj.tipoMovimentacao}" >
										<f:selectItem itemValue="I" itemLabel="INCLUIR" />
										<f:selectItem itemValue="R" itemLabel="REMOVER" />
									</h:selectOneRadio>
							 </td>
						</tr>				

						<tr>
							<th width="25%" class="required"> Tipo de Cota: </th>
							<td> 
									<h:selectOneMenu id="tipo_cota" value="#{movimentacaoCotasMonitoria.obj.tipoCota.id}" >
										<f:selectItems value="#{tipoVinculoDiscenteBean.remuneradosAtivosEnsinoCombo }"/>
										<f:selectItems value="#{tipoVinculoDiscenteBean.naoRemuneradosAtivosEnsinoCombo }"/>
									</h:selectOneMenu>
							 </td>
						</tr>				


						<tr>
							<th width="25%" class="required"> Nº de Cotas: </th>
							<td> <h:inputText id="numeroCotas" value="#{movimentacaoCotasMonitoria.obj.qtdCotas}" size="3" onkeyup="formatarInteiro(this)" maxlength="4"/>								 
							</td>
						</tr>				
		
						<tr>
							<th class="required"> Observações: </th>
							<td> 
								<h:inputText id="observacoes" value="#{movimentacaoCotasMonitoria.obj.observacao }"  size="60" maxlength="250"/> 
		   					   <ufrn:help img="/img/ajuda.gif">Motivo da movimentação das cotas.</ufrn:help> 
							</td>
						</tr>
		
						<tr>
							<th> Máximo de Voluntários para Remover: </th>
							<td> <b><h:outputText value="#{movimentacaoCotasMonitoria.obj.projetoEnsino.bolsasNaoRemuneradas  - movimentacaoCotasMonitoria.totalVoluntariosProjeto }" /></b> </td>
						</tr>
						
						<tr>
							<th> Máximo de Bolsas para Remover: </th>
							<td> <b><h:outputText value="#{movimentacaoCotasMonitoria.obj.projetoEnsino.bolsasConcedidas  - movimentacaoCotasMonitoria.totalBolsistasProjeto }" /></b> </td>
						</tr>

					</table>
				</td>
			</tr>
			
			<tr><td>
				<table class="subFormulario" width="100%">
					<caption>Dados do Projeto</caption>

						<tr>
							<th width="25%">Título do Projeto:</th>
							<td>
								<b><h:outputText value="#{movimentacaoCotasMonitoria.obj.projetoEnsino.anoTitulo}"/></b>
							</td>
						</tr>

						<tr>
							<th width="25%">Coordenação:</th>
							<td>
								<b><h:outputText value="#{movimentacaoCotasMonitoria.obj.projetoEnsino.coordenacao.pessoa.nome}"/></b>
							</td>
						</tr>
				
						<tr>
							<th>Bolsas Solicitadas:</th>
							<td>
							 	<b><h:outputText value="#{movimentacaoCotasMonitoria.obj.projetoEnsino.bolsasSolicitadas}"/></b>
							 </td>
						</tr>
						
						<tr>		
							<th>Bolsas Concedidas:</th>
							<td>
							 	<b><h:outputText value="#{movimentacaoCotasMonitoria.obj.projetoEnsino.bolsasConcedidas}"/></b>
							</td>
						</tr>
			
			
						<tr>		
							<th>
								<c:set var="NAO_RECOMENDADO" value="<%= String.valueOf(TipoSituacaoProjeto.MON_NAO_RECOMENDADO) %>" scope="application"/>				
								<c:set var="NAO_AUTORIZADO" value="<%= String.valueOf(TipoSituacaoProjeto.MON_NAO_AUTORIZADO_PELOS_DEPARTAMENTOS_ENVOLVIDOS) %>" scope="application"/>								
								 Bolsas Não Remuneradas:
							</th>	
							<td>
								 <c:if test="${(movimentacaoCotasMonitoria.obj.projetoEnsino.situacaoProjeto.id == NAO_RECOMENDADO) or (movimentacaoCotasMonitoria.obj.projetoEnsino.situacaoProjeto.id == NAO_AUTORIAZADO)}">0</c:if>
								 <c:if test="${(movimentacaoCotasMonitoria.obj.projetoEnsino.situacaoProjeto.id != NAO_RECOMENDADO) and (movimentacaoCotasMonitoria.obj.projetoEnsino.situacaoProjeto.id != NAO_AUTORIAZADO)}">
								 	<b><h:outputText value="#{movimentacaoCotasMonitoria.obj.projetoEnsino.bolsasSolicitadas - movimentacaoCotasMonitoria.obj.projetoEnsino.bolsasConcedidas}"/> </b>
								 </c:if>
							</td>
						</tr>
			
					
						<tr>
							<th>E-Mail do Projeto:</th>
							<td>
							 	<b><h:outputText value="#{movimentacaoCotasMonitoria.obj.projetoEnsino.email}"/></b>
							 </td>
						</tr>
					
						<tr>		
							<th>Centro: </th>
							<td>
								<b><h:outputText value="#{movimentacaoCotasMonitoria.obj.projetoEnsino.unidade}"/></b>
							</td>
						</tr>
					
						<tr>
							<th>Situação: </th>
							<td>
								<b><h:outputText value="#{movimentacaoCotasMonitoria.obj.projetoEnsino.situacaoProjeto.descricao}"/></b>
							</td>
						</tr>
						
						
						<c:set var="discentes"  value="${movimentacaoCotasMonitoria.obj.projetoEnsino.discentesConsolidados}" />
						
						<tr>
							
							<td colspan="2" align="center">
								<hr/>
								<b> Discentes Envolvidos no Projeto </b>
								<hr/>
							</td>
						</tr>
									
						<tr>
							<td colspan="2">
			
								<c:if test="${empty discentes}">
									<font color="red">Não há monitores neste projeto</font>
								</c:if>				
								
								<c:if test="${not empty discentes}">
									
									<t:dataTable value="#{movimentacaoCotasMonitoria.obj.projetoEnsino.discentesMonitoria}" var="dm" width="100%">
											<t:column>
												<f:facet name="header" >
													<f:verbatim>Matrícula</f:verbatim>
												</f:facet>
												<h:outputText value="#{dm.discente.matricula}"  />
											</t:column>	

											<t:column>
												<f:facet name="header" >
													<f:verbatim>Nome</f:verbatim>
												</f:facet>
												<h:outputText value="#{dm.discente.nome}"  />
											</t:column>	

											<t:column>
												<f:facet name="header">
													<f:verbatim>Início</f:verbatim>
												</f:facet>
												<h:outputText value="#{dm.dataInicio}" />
											</t:column>	

											<t:column>
												<f:facet name="header">
													<f:verbatim>Fim</f:verbatim>
												</f:facet>
												<h:outputText value="#{dm.dataFim}" />
											</t:column>	
			
											<t:column>
												<f:facet name="header">
													<f:verbatim>Tipo Vínculo</f:verbatim>
												</f:facet>
												<h:outputText value="#{dm.tipoVinculo.descricao}" />
											</t:column>	
			
											<t:column>
												<f:facet name="header">
													<f:verbatim>Situação</f:verbatim>
												</f:facet>
												<h:outputText value="#{dm.situacaoDiscenteMonitoria.descricao}"  />
											</t:column>									
									</t:dataTable>
										
								</c:if>
			
							</td>
						</tr>			
			
				</table>
			</td></tr>
			
			
		</tbody>

	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton action="#{movimentacaoCotasMonitoria.cadastrar}" value="Movimentar Cotas" id="btnCadastrar"/>
				<h:commandButton action="#{movimentacaoCotasMonitoria.cancelar}" value="Cancelar" id="btnCancelar" immediate="true"/>
			</td>
		</tr>
	</tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>