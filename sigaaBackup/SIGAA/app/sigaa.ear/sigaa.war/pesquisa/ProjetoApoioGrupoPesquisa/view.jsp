<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Visualização do Projetos para ação de Apoio a Grupo de Pesquisa </h2>

	<h:form id="form">
		<table class="formulario" width="100%">
		<caption> Identificação </caption>

			<tr>
				<th><b>Título:</b></th>
				<td> 
					<h:outputText id="titulo" value="#{ projetoApoioGrupoPesquisaMBean.obj.projeto.titulo}" />
				</td>
			</tr>

			<tr>
				<th><b>Edital de Pesquisa</b></th>
				<td> 
					<h:outputText id="edital" value="#{ projetoApoioGrupoPesquisaMBean.obj.editalPesquisa.edital.descricao }" />
				</td>
			</tr>
			
			<tr>
				<th width="21%"><b>Grupo de Pesquisa:</b></th>
				<td>
					<h:outputText id="grupoPesquisa" value="#{ projetoApoioGrupoPesquisaMBean.obj.grupoPesquisa.nome}" />
				</td>
			</tr>

			<tr>
				<td class="subFormulario" colspan="2"> Lista do Membros do Grupo de Pesquisa </td>
			</tr>
			<tr>
				<td colspan="2">
				<rich:dataTable id="dtPermanentes" value="#{ projetoApoioGrupoPesquisaMBean.obj.grupoPesquisa.equipesGrupoPesquisaCol }" 
					var="membro" width="100%" styleClass="subFormulario" rowClasses="linhaPar, linhaImpar" binding="#{projetoApoioGrupoPesquisaMBean.membros}">
					<rich:column width="50%">
						<f:facet name="header"><f:verbatim>Pesquisador</f:verbatim></f:facet>
						<h:outputText value="#{membro.pessoa.nome}"/>
					</rich:column>
					<rich:column width="15%">
						<f:facet name="header"><f:verbatim>Categoria</f:verbatim></f:facet>
						<h:outputText value="#{ membro.categoriaString }"/>
					</rich:column>
					<rich:column width="15%">
						<f:facet name="header"><f:verbatim>Classificação</f:verbatim></f:facet>
						<h:outputText value="<font color='red'>" rendered="#{membro.classificacao == 1}"  escape="false"/>
							<h:outputText value="#{membro.classificacaoString}" />
						<h:outputText value="</font>" rendered="#{membro.classificacao == 1}"  escape="false"/>
					</rich:column>
					<rich:column width="15%">
						<f:facet name="header"><f:verbatim>Tipo</f:verbatim></f:facet>
						<h:outputText value="#{ membro.tipoMembroGrupoPesqString }"/>
					</rich:column>
					<rich:column width="5%">
						<center>
							<f:facet name="header"><h:outputText value="Lattes" /></f:facet>
							<a4j:commandLink id="showPanelOn" actionListener="#{projetoApoioGrupoPesquisaMBean.carregarPainel}" 
	               					oncomplete="Richfaces.showModalPanel('painelLattes')" immediate="true" reRender="painelLattes">  
								<h:graphicImage value="/img/prodocente/lattes.gif" style="overflow: visible;" rendered="#{not empty membro.enderecoLattes}" title="Currículo do Pesquisador na Plataforma Lattes"/>
								<h:graphicImage value="/img/prodocente/question.png" style="overflow: visible;" rendered="#{empty membro.enderecoLattes}" title="Endereço do CV não registrado no sistema"/>
						    </a4j:commandLink>
					    </center>
					</rich:column>
				</rich:dataTable>
				</td>
			</tr>

			<tr>
				<td colspan="2" class="subFormulario">Justificativa dos Recursos Solicitados</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<h:outputText value="#{ projetoApoioGrupoPesquisaMBean.obj.projeto.justificativa }" />
				</td>
			</tr>			

			<tr>
				<td colspan="2" class="subFormulario"> Integração do(s) Projetos de Pesquisa </td>
			</tr>
			<tr>
				<td colspan="2">
					<h:outputText value="#{ projetoApoioGrupoPesquisaMBean.obj.integracao }" />
				</td>
			</tr>			

			<tr>
				<td colspan="2">

						<table class="listagem">
						  <caption class="listagem">Lista de Despesas Cadastradas</caption>
							<thead>
								<tr>
									<th>Descrição</th>
									<th>&nbsp;</th>
									<th style="text-align: right"  width="15%">Valor Unitário </th>
									<th style="text-align: right"  width="10%">Quant.</th>
									<th style="text-align: right" width="15%">Valor Total </th>
								</tr>
							</thead>

							<tbody>

								<c:if test="${not empty projetoApoioGrupoPesquisaMBean.tabelaOrcamentaria}">
									
									<c:set value="${projetoApoioGrupoPesquisaMBean.tabelaOrcamentaria}" var="tabelaOrcamentaria" />
									<c:forEach items="#{tabelaOrcamentaria}" var="tabelaOrc">

										<tr  style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
											<td colspan="6">${ tabelaOrc.key.descricao }</td>
										</tr>
											<c:set value="#{tabelaOrc.value.orcamentos}" var="orcas" />
											<c:forEach items="#{orcas}" var="orca" varStatus="status">
												<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
													<td style="padding-left: 20px"> ${orca.discriminacao}</td>
													<td align="right">${orca.materialLicitado ? '(Licitado)' : ''}</td>
													<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${orca.valorUnitario}" type="currency" />  </td>
													<td align="right"> ${orca.quantidade}</td>
													<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${orca.valorTotal}" type="currency"/>  </td>
												</tr>
											</c:forEach>

											<tr  style="background: #EEE; padding: 2px 0 2px 5px;">
												<td colspan="3"><b>SUB-TOTAL (${ tabelaOrc.key.descricao})</b></td>
												<td  align="right"><b>${ tabelaOrc.value.quantidadeTotal }</b></td>
												<td align="right"><b><fmt:formatNumber currencySymbol="R$  " value="${ tabelaOrc.value.valorTotalRubrica }" type="currency"/></b></td>
											</tr>

											<tr>
												<td colspan="6">&nbsp;</td>
											</tr>

									</c:forEach>
								</c:if>

									<c:if test="${empty projetoApoioGrupoPesquisaMBean.tabelaOrcamentaria}">
										<tr><td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font> </td></tr>
									</c:if>

								</tbody>
						</table>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandLink value="<< Voltar" action="#{ projetoApoioGrupoPesquisaMBean.listar }" />
					</td>
				</tr>
			</tfoot>
		</table>
		
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>