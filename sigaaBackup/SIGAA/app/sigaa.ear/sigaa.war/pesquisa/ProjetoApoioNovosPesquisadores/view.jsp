<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	tr.negrito th { font-weight: bold; }
</style>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Submissão de Projetos de Novos Pesquisadores </h2>

	<h:form id="form">
		<table class="formulario" width="100%">
		<caption> Projeto de Apoio a Novos Pesquisadores </caption>

			<tr>
				<td colspan="6" class="subFormulario">Identificação</td>
			</tr>
			<tr class="negrito">
				<th>Nome:</th>
				<td colspan="2"> 
					<h:outputText id="nome" value="#{ projetoApoioNovosPesquisadoresMBean.obj.coordenador.nome}" />
				</td>

				<th>CPF:</th>
				<td colspan="2"> 
					<h:outputText id="cpf" value="#{ projetoApoioNovosPesquisadoresMBean.obj.coordenador.pessoa.cpf_cnpj }" /> 
				</td>
			</tr>

			<tr class="negrito">
				<th>RG:</th>
				<td> 
					<h:outputText id="identidade" value="#{ projetoApoioNovosPesquisadoresMBean.obj.coordenador.pessoa.identidade.numero }" /> 
				</td>

				<th>Org. Exp:</th>
				<td> 
					<h:outputText id="orgaoExp" value="#{ projetoApoioNovosPesquisadoresMBean.obj.coordenador.pessoa.identidade.orgaoExpedicao }" /> 
				</td>
			</tr>

			<tr class="negrito">
				<th>Unidade:</th>
				<td> 
					<h:outputText id="unidade" value="#{ projetoApoioNovosPesquisadoresMBean.obj.coordenador.unidade.nome }" /> 
				</td>

				<th>Telefone:</th>
				<td> 
					<h:outputText id="telefone" value="#{ projetoApoioNovosPesquisadoresMBean.obj.coordenador.pessoa.telefone }" /> 
				</td>

				<th>Fax:</th>
				<td> 
					<h:outputText id="fax" value="#{ projetoApoioNovosPesquisadoresMBean.obj.coordenador.pessoa.fax }" /> 
				</td>
			</tr>

			<tr class="negrito">
				<th>E-mail:</th>
				<td> <h:outputText id="email" value="#{ projetoApoioNovosPesquisadoresMBean.obj.coordenador.pessoa.email }" /> </td>

				<th>Lattes:</th>
				<td colspan="3"> 
					<a href="${ projetoApoioNovosPesquisadoresMBean.obj.coordenador.pessoa.perfil.enderecoLattes }" target="_blank">
						${ projetoApoioNovosPesquisadoresMBean.obj.coordenador.pessoa.perfil.enderecoLattes }</a>
				 </td>
			</tr>

			<tr>
				<td colspan="6" class="subFormulario">Grupo de Pesquisa da Instituição</td>
			</tr>

			<tr class="negrito">
				<th>Edital de Pesquisa:</th>
				<td  colspan="5"> 
					<h:outputText id="editalPesquisa" value="#{ projetoApoioNovosPesquisadoresMBean.obj.editalPesquisa.edital.descricao }" />
				</td>
			</tr>

			<tr>
				<td colspan="6" class="subFormulario">Grupo de Pesquisa da Instituição</td>
			</tr>

			<tr class="negrito">
				<th width="21%">Grupo de Pesquisa:</th>
				<td colspan="5">
					<h:outputText id="suggestionNomeGP" value="#{ projetoApoioNovosPesquisadoresMBean.obj.grupoPesquisa.nome}" />
				</td>
			</tr>
	
			<tr>
				<td colspan="6">
					<table class="formulario" width="100%">
						<caption>Informações Projeto</caption>
					
						<tr>
							<td class="subFormulario" colspan="6">Título</td>
						</tr>
						<tr>	
							<td colspan="5"> 
								<h:outputText id="titulo" value="#{ projetoApoioNovosPesquisadoresMBean.obj.projeto.titulo}" />
							</td>
						</tr>

						<tr>
							<td class="subFormulario" colspan="6">Objetivos</td>
						</tr>

						<tr>
							<td colspan="5"> 
								<h:outputText id="objetivo" value="#{ projetoApoioNovosPesquisadoresMBean.obj.projeto.objetivos }" />
							</td>
						</tr>		

						<tr>
							<td class="subFormulario" colspan="6">Metodologia</td>
						</tr>

						<tr>
							<td colspan="5"> 
								<h:outputText id="metodologia" value="#{ projetoApoioNovosPesquisadoresMBean.obj.projeto.metodologia }" />
							</td>
						</tr>		

						<tr>
							<td class="subFormulario" colspan="6">Resultados</td>
						</tr>

						<tr>
							<td colspan="5"> 
								<h:outputText id="resultados" value="#{ projetoApoioNovosPesquisadoresMBean.obj.projeto.resultados }" />
							</td>
						</tr>		

						<tr>
							<td class="subFormulario" colspan="6">Referências do Projeto</td>
						</tr>

						<tr>
							<td colspan="5"> 
								<h:outputText id="referencias" value="#{ projetoApoioNovosPesquisadoresMBean.obj.projeto.referencias }" />
							</td>
						</tr>		

						<tr>
							<td class="subFormulario" colspan="6">Integração</td>
						</tr>

						<tr>
							<td colspan="5"> 
								<h:outputText id="integracao" value="#{ projetoApoioNovosPesquisadoresMBean.obj.integracao }" />
							</td>
						</tr>		
					</table>				
				</td>
			</tr>	

			<tr>
				<td colspan="6">
					<table class="formulario" width="100%">
						<caption>Informações do Cronograma</caption>
							<table id="cronograma" class="listagem" width="100%">
										<thead>
											<tr>
												<th width="30%" rowspan="2"> Atividades </th>
												<c:forEach items="${projetoApoioNovosPesquisadoresMBean.telaCronograma.mesesAno}" var="ano">
												<th colspan="${fn:length(ano.value)}" style="text-align: center" class="inicioAno fimAno">
													${ano.key}
												</th>
												</c:forEach>
											</tr>
											<tr>
												<c:forEach items="${projetoApoioNovosPesquisadoresMBean.telaCronograma.mesesAno}" var="ano">
													<c:forEach items="${ano.value}" var="mes" varStatus="status">
													<c:set var="classeCabecalho" value=""/>
													<c:if test="${status.first}"> <c:set var="classeCabecalho" value="inicioAno"/> </c:if>
													<c:if test="${status.last}"> <c:set var="classeCabecalho" value="fimAno"/> </c:if>
							
													<th class="${classeCabecalho}" style="text-align: center"> ${mes}	</th>
													</c:forEach>
												</c:forEach>
											</tr>
										</thead>
										<tbody>
											<c:set var="numeroAtividades" value="${fn:length(projetoApoioNovosPesquisadoresMBean.telaCronograma.cronogramas)}" />
											<c:set var="valoresCheckboxes" value=",${fn:join(projetoApoioNovosPesquisadoresMBean.telaCronograma.calendario, ',')}" />
											<c:forEach begin="1" end="${numeroAtividades}" varStatus="statusAtividades">
											<tr>
												<th> ${projetoApoioNovosPesquisadoresMBean.telaCronograma.atividade[statusAtividades.index-1]} </th>
												<c:forEach items="${projetoApoioNovosPesquisadoresMBean.telaCronograma.mesesAno}" var="ano" varStatus="statusCheckboxes">
													<c:forEach items="${ano.value}" var="mes">
														<c:set var="valorCheckbox" value=",${statusAtividades.index-1}_${mes}_${ano.key}" />
														<c:set var="classeCelula" value=""/>
														<c:if test="${ fn:contains(valoresCheckboxes, valorCheckbox) }">
															<c:set var="classeCelula" value="selecionado"/>
														</c:if>
														<td align="center" class="${classeCelula}" >
															&nbsp;
														</td>
													</c:forEach>
												</c:forEach>
											</tr>
											</c:forEach>
										</tbody>
							</table>
				</td>
			</tr>

			<tr>
				<td colspan="6" class="subFormulario">Despesas Cadastradas</td>
			</tr>
			<tr>
				<td colspan="6">
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
							<c:if test="${not empty projetoApoioNovosPesquisadoresMBean.tabelaOrcamentaria}">
								
								<c:set value="${projetoApoioNovosPesquisadoresMBean.tabelaOrcamentaria}" var="tabelaOrcamentaria" />
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
		
								<c:if test="${empty projetoApoioNovosPesquisadoresMBean.tabelaOrcamentaria}">
									<tr><td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font> </td></tr>
								</c:if>
		
							</tbody>
					</table>
				</td>
			</tr>
			<a4j:region rendered="#{ !projetoApoioNovosPesquisadoresMBean.obj.visualizar }">
				<tr>
					<td colspan="10">
						<div class="descricaoOperacao" style="width: 75%">
							<p style="line-height: 1.5em; margin: 1em 0.5em; font-size: 1.1em;">
								<h:selectBooleanCheckbox value="#{projetoApoioNovosPesquisadoresMBean.obj.visualizar}" id="concordancia" />
								<h:outputLabel for="concordancia">
								O solicitante declara formalmente que está de acordo com o Termo de
								Adesão e Compromisso e que responde pela veracidade de todas as
								informações contidas no Projeto de Apoio a Novos Pesquisadores implantada no banco de dados
								institucional da ${ configSistema['siglaInstituicao'] }.
								</h:outputLabel>
							</p>
							<p style="text-align: right;">
								<em>(Declaração feita em observância aos artigos 297-299 do Código Penal Brasileiro).</em>
							</p>
						</div>
					</td>
				</tr>
			</a4j:region>
			
			<tfoot>
				<tr>
					<td colspan="6">
						<h:commandButton id="btnCadastrar" action="#{ projetoApoioNovosPesquisadoresMBean.cadastrar }" value="Submeter Projeto" 
							rendered="#{ !projetoApoioNovosPesquisadoresMBean.obj.visualizar }"/>
						<h:commandButton id="btnVoltarDadosGerais" action="#{ projetoApoioNovosPesquisadoresMBean.telaOrcamento }" value="<< Voltar" 
							rendered="#{ !projetoApoioNovosPesquisadoresMBean.obj.visualizar && !projetoApoioNovosPesquisadoresMBean.pesquisa}"/>
						<h:commandButton id="btnVoltarListagem" action="#{ projetoApoioNovosPesquisadoresMBean.listar }" value="<< Voltar" 
							rendered="#{ projetoApoioNovosPesquisadoresMBean.obj.visualizar && !projetoApoioNovosPesquisadoresMBean.pesquisa}"/>
						<h:commandButton id="btnVoltarGestor" action="#{ buscaProjetoApoioNovosPesquisadoresMBean.telaBusca }" value="<< Voltar" 
							rendered="#{ projetoApoioNovosPesquisadoresMBean.pesquisa}"/>
						<h:commandButton id="btnCancelar" action="#{projetoApoioNovosPesquisadoresMBean.cancelar}" value="Cancelar" immediate="true" 
							onclick="#{confirm}" rendered="#{ !projetoApoioNovosPesquisadoresMBean.obj.visualizar }"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>