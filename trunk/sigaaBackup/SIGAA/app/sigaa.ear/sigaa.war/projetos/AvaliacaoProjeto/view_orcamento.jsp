<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.FuncaoMembro"%>
<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<f:view>
	<h2>Visualização do Orçamento de Ação Acadêmica Integrada</h2>

<h:form id="form">

	<c:set var="COORDENADOR" 		value="<%= String.valueOf(FuncaoMembro.COORDENADOR) %>" 	scope="application"/>	
	<c:set var="EM_EXECUCAO" 		value="<%= String.valueOf(TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO) %>" 		scope="application"/>

	<h3 class="tituloTabelaRelatorio"> DADOS DA AÇÃO </h3>
	<table class="tabelaRelatorio" width="100%">
	<tbody>
		
		<%-- DADOS GERAIS, DE TODOS OS TIPOS DE AÇÂO --%>
		<tr>
			<th width="23%"><b> Nº Institucional: </b> </th>
			<td><h:outputText value="#{projetoBase.projeto.numeroInstitucional}/#{projetoBase.projeto.ano}"/></td>
		</tr>

		<tr>
			<th width="23%"><b> Título: </b> </th>
			<td><h:outputText value="#{projetoBase.projeto.titulo}"/></td>
		</tr>


		<tr>
			<th width="23%"><b> Dimensão Acadêmica: </b> </th>
			<td><h:outputText value="#{projetoBase.projeto.dimensaoAcademica}"/></td>
		</tr>

	
		<tr>
			<th width="23%"><b> Situação: </b> </th>
			<td>
				<font color="${(projetoBase.projeto.situacaoProjeto.id eq EM_EXECUCAO) ? 'black':'red'}">
					<h:outputText value="#{projetoBase.projeto.situacaoProjeto.descricao}"/>
				</font>
			</td>
		</tr>
	
		<tr>
			<th><b>Financiamento:</b></th>
			<td>
				<h:outputText value="#{projetoBase.projeto.fonteFinanciamentoString}"/>
			</td>
		</tr>
		
		
		<!-- ORÇAMENTO DETALHADO -->
		<c:if test="${not empty projetoBase.projeto.orcamento}">
			<tr>
				<td colspan="3">
							<h3 class="subtitulo">Orçamento Detalhado</h3>			
							<table class="listagem">
								<thead>
								<tr>
									<th>Descrição</th>
									<th style="text-align: right"  width="15%">Valor Unitário </th>
									<th style="text-align: right"  width="10%">Quant.</th>
									<th style="text-align: right" width="15%">Valor Total </th>
								</tr>
								</thead>

								<tbody>

									<c:if test="${not empty projetoBase.tabelaOrcamentaria}">
									
										<c:set value="${projetoBase.tabelaOrcamentaria}" var="tabelaOrcamentaria" />
										<c:forEach items="#{tabelaOrcamentaria}" var="tabelaOrc">
												
												<tr  style="background: #EFF0FF; font-weight: bold; padding: 2px 0 2px 5px;">
													<td colspan="5">${ tabelaOrc.key.descricao }</td>
												</tr>
														<c:set value="#{tabelaOrc.value.orcamentos}" var="orcamentos" />
														<c:forEach items="#{orcamentos}" var="orcamento" varStatus="status">
															<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
																<td style="padding-left: 20px"> ${orcamento.discriminacao}</td>
																<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${orcamento.valorUnitario}" type="currency" />  </td>
																<td align="right">${orcamento.quantidade}</td>
																<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${orcamento.valorTotal}" type="currency"/>  </td>
															</tr>
														</c:forEach>

												<tr  style="background: #EEE; padding: 2px 0 2px 5px;">
													<td colspan="2">SUB-TOTAL (${ tabelaOrc.key.descricao})</td>
													<td  align="right">${ tabelaOrc.value.quantidadeTotal }</td>
													<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${ tabelaOrc.value.valorTotalRubrica }" type="currency"/></td>
												</tr>

												<tr>
													<td colspan="5">&nbsp;</td>
												</tr>

										</c:forEach>
									</c:if>
									<c:if test="${empty projetoBase.projeto.orcamento}">
										<tr><td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font> </td></tr>
									</c:if>

								</tbody>
						</table>
				
				</td>
			</tr>	
		</c:if>




		
		
		
		
		
		
		
		
		
		
		<!-- ORÇAMENTO EM_EXECUCAO  -->
		<c:if test="${not empty projetoBase.projeto.orcamentoConsolidado}">
			<tr>
				<td colspan="2">
				
						<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Resumo do Orçamento</h3>

							<t:dataTable id="dt_aprovado" value="#{projetoBase.projeto.orcamentoConsolidado}" var="consolidacao"
							 align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" rowIndexVar="index" forceIdIndex="true">
										<t:column>
											<f:facet name="header"><f:verbatim>Descrição</f:verbatim></f:facet>
											<h:outputText value="#{consolidacao.elementoDespesa.descricao}" />
										</t:column>

										<t:column>
											<f:facet name="header"><f:verbatim>Valor Solicitado</f:verbatim></f:facet>
											<h:outputText value="#{consolidacao.fundo}"><f:convertNumber pattern="R$ #,##0.00"/></h:outputText>
										</t:column>

										<t:column>
											<f:facet name="header"><f:verbatim>Valor Aprovado</f:verbatim></f:facet>
											<h:outputText value="#{consolidacao.fundoConcedido}"><f:convertNumber pattern="R$ #,##0.00"/></h:outputText>
										</t:column>
						</t:dataTable>
				</td>
			</tr>	
		</c:if>
	
	</tbody>
</table>


</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>