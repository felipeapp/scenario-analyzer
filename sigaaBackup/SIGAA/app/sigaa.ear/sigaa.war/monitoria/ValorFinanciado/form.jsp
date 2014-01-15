<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria"%>

<f:view>
	<h2><ufrn:subSistema /> > Projeto de Ensino</h2>

<h:form>
	<a4j:keepAlive beanName="valorFinanciadoProjetoMBean" />
	
	<c:set var="ASSUMIU_MONITORIA" 	value="<%= String.valueOf(SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA) %>" 	scope="application"/>
	<c:set var="CONVOCADO" 	value="<%= String.valueOf(SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA) %>" scope="application"/>
	
	<table class="listagem" width="100%">
		<caption class="listagem"> Dados do Projeto de Ensino </caption>

			<tr>
				<th width="25%"><b>Título do Projeto:</b></th>
				<td><h:outputText value="#{valorFinanciadoProjetoMBean.obj.titulo}"/></td>
			</tr>

			<tr>
				<th><b> Tipo de Projeto: </b></th>
				<td><h:outputText value="#{valorFinanciadoProjetoMBean.obj.tipoProjetoEnsino.descricao}"/></td>
			</tr>

			<tr>
				<th><b> Ano de Referência: </b></th>
				<td><h:outputText value="#{valorFinanciadoProjetoMBean.obj.ano}"/> </td>
			</tr>

			<tr>
				<th><b> Data de Inicio: </b></th>
				<td><h:outputText value="#{valorFinanciadoProjetoMBean.obj.projeto.dataInicio}"/> </td>
			</tr>

			<tr>
				<th><b> Data de Fim: </b></th>
				<td><h:outputText value="#{valorFinanciadoProjetoMBean.obj.projeto.dataFim}"/> </td>
			</tr>
			
			<tr>
				<th><b> Valor do Financiamento: </b></th>
				<td> R$
					<h:inputText id="valor" value="#{valorFinanciadoProjetoMBean.obj.valorFinanciamento}" size="9"  
						maxlength="12" style="text-align: right" onkeydown="return(formataValor(this, event, 2))" 
						onblur="if(this.value==''){ this.value = '0,00';}" title="valor do Financiamento" alt="Valor do Financiamento">
						<f:convertNumber pattern="#,##0.00" />
					</h:inputText>
				</td>
			</tr>

			<tr>
				<th><b> Coordenador(a): </b></th>
				<td><h:outputText value="#{valorFinanciadoProjetoMBean.obj.projeto.coordenador.pessoa.nome}"/> </td>
			</tr>
			
			<tr>
				<th><b> E-Mail do Projeto: </b></th>
				<td><h:outputText value="#{valorFinanciadoProjetoMBean.obj.projeto.coordenador.pessoa.email}"/> </td>
			</tr>
		
			<c:if test="${ empty valorFinanciadoProjetoMBean.obj.editalMonitoria }">
				<tr>		
					<th><b> Edital: </b></th>
					<td><h:outputText value="#{valorFinanciadoProjetoMBean.obj.editalMonitoria}"/></td>
				</tr>
			</c:if>				
			
			<tr>		
				<th><b> Centro: </b></th>
				<td><h:outputText value="#{valorFinanciadoProjetoMBean.obj.unidade}"/> </td>
			</tr>
			
			<tr>
				<th><b> Situação: </b></th>
				<td><h:outputText value="#{valorFinanciadoProjetoMBean.obj.situacaoProjeto.descricao}"/></td>
			</tr>

			<tr>
				<td style="padding-top: 10px;"></td>
			</tr>			
			
			<tr>
				<td colspan="2" class="subFormulario">Docentes Envolvidos no Projeto</td>
			</tr>

			<tr>
				<td colspan="2">
					<t:dataTable value="#{valorFinanciadoProjetoMBean.obj.equipeDocentes}" var="docente" width="100%" >	
						<t:column>
							<h:outputText value="<font color='#eee'>" rendered="#{! docente.ativo}" escape="false"/>
								<f:facet name="header">
									<f:verbatim>Docente</f:verbatim>
								</f:facet>
								<h:outputText value="#{docente.servidor.siapeNome}" />
						</t:column>
						<t:column>
							<f:facet name="header">
								<f:verbatim>Vínculo</f:verbatim>
							</f:facet>
							<h:outputText value="<font color='red'>COORDENADOR(A) </font>" rendered="#{docente.coordenador}" escape="false" />
							<h:outputText value="ORIENTADOR(A)" rendered="#{!docente.coordenador}" escape="false"/>
						</t:column>
						<t:column style="text-align: center;">
								<f:facet name="header">
									<f:verbatim><center>Data Início</center></f:verbatim>
								</f:facet>
								<h:outputText value="#{docente.dataEntradaProjeto}" />
						</t:column>										
						<t:column style="text-align: center;">
								<f:facet name="header">
									<f:verbatim><center>Data Fim</center></f:verbatim>
								</f:facet>
								<h:outputText value="#{docente.dataSaidaProjeto}" rendered="#{docente.dataSaidaProjeto != null}"/>
								<h:outputText value="#{valorFinanciadoProjetoMBean.obj.projeto.dataFim}" rendered="#{docente.dataSaidaProjeto == null}"/>
						</t:column>
					</t:dataTable>					
				</td>
			</tr>
					
			<tr>
				<td style="padding-top: 10px;"></td>
			</tr>			
			
			<tr>
				<td colspan="2" class="subFormulario">Discentes Envolvidos no Projeto</td>
			</tr>

			<tr>
				<td colspan="2">
					<t:dataTable value="#{valorFinanciadoProjetoMBean.obj.discentesMonitoria}" var="discente" width="100%" >	
						<t:column>
								<f:facet name="header">
									<f:verbatim>Discente</f:verbatim>
								</f:facet>
								<h:outputText value="#{discente.discente.discente.matriculaNome}" />
						</t:column>
						<t:column>
								<f:facet name="header">
									<f:verbatim>Vínculo</f:verbatim>
								</f:facet>
								<h:outputText value="#{discente.tipoVinculo.descricao}" />
						</t:column>
						<t:column style="text-align: center;" width="15%">
								<f:facet name="header">
									<f:verbatim><center>Data Início</center></f:verbatim>
								</f:facet>
								<h:outputText value="#{discente.dataInicio}" />
						</t:column>
						<t:column style="text-align: center;" width="15%">
								<f:facet name="header">
									<f:verbatim><center>Data Fim &nbsp;&nbsp;&nbsp; </center></f:verbatim>
								</f:facet>
								<h:outputText value="#{discente.dataFim}" /> 
						</t:column>									
					</t:dataTable>					
				</td>
			</tr>						
	
			<tr>
				<td style="padding-top: 10px;"></td>
			</tr>			
					
			<tr>
				<td colspan="2" class="subFormulario">Orçamento Detalhado</td>
			</tr>
			
			<c:if test="${not empty valorFinanciadoProjetoMBean.obj.orcamentosDetalhados}">
				<tr>
					<td colspan="2">
						<table class="listagem" width="99%">
							<thead>
							<tr>
								<th>Descrição</th>
								<th style="text-align: right"  width="15%">Licitado</th>
								<th style="text-align: right"  width="15%">Valor Unitário </th>
								<th style="text-align: right"  width="10%">Quant.</th>
								<th style="text-align: right" width="15%">Valor Total </th>
							</tr>
							</thead>

							<tbody>

								<c:if test="${not empty valorFinanciadoProjetoMBean.tabelaOrcamentaria}">
								
									<c:set value="${valorFinanciadoProjetoMBean.tabelaOrcamentaria}" var="tabelaOrcamentaria" />
									<c:forEach items="#{tabelaOrcamentaria}" var="tabelaOrc">
											
											<tr  style="background: #EFF0FF; font-weight: bold; padding: 2px 0 2px 5px;">
												<td colspan="5">${ tabelaOrc.key.descricao }</td>
											</tr>
													<c:set value="#{tabelaOrc.value.orcamentos}" var="orcamentos" />
													<c:forEach items="#{orcamentos}" var="orcamento" varStatus="status">
														<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
															<td style="padding-left: 20px"> ${orcamento.discriminacao}</td>
															<td style="text-align:right">${orcamento.materialLicitado ? 'Sim' : 'Não'}</td>
															<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${orcamento.valorUnitario}" type="currency" />  </td>
															<td align="right">${orcamento.quantidade}</td>
															<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${orcamento.valorTotal}" type="currency"/>  </td>
														</tr>
													</c:forEach>

											<tr  style="background: #EEE; padding: 2px 0 2px 5px;">
												<td colspan="3">SUB-TOTAL (${ tabelaOrc.key.descricao})</td>
												<td  align="right">${ tabelaOrc.value.quantidadeTotal }</td>
												<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${ tabelaOrc.value.valorTotalRubrica }" type="currency"/></td>
											</tr>

											<tr>
												<td colspan="5">&nbsp;</td>
											</tr>

									</c:forEach>
								</c:if>

									<c:if test="${empty valorFinanciadoProjetoMBean.obj.orcamentosDetalhados}">
										<tr><td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font> </td></tr>
									</c:if>

							</tbody>
						</table>
					</td>
				</tr>	
			</c:if>		

			<tfoot>
				<tr>
					<td colspan="2">
						<center>
							<h:commandButton value="Cadastrar" action="#{ valorFinanciadoProjetoMBean.cadastrar }" id="btVoltar" />					
							<h:commandButton value="Cancelar" action="#{ valorFinanciadoProjetoMBean.cancelar }" onclick="#{ confirm }" id="btCancelar" immediate="true" />
						</center>
					</td>
				</tr>
			</tfoot>
							
	</table>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>