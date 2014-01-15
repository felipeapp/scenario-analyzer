<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript">
<!--
	function limitText(limitField, limitCount, limitNum) {
	    if (limitField.value.length > limitNum) {
	        limitField.value = limitField.value.substring(0, limitNum);
	    } else {
	        $(limitCount).value = limitNum - limitField.value.length;
	    }
	}
	
//-->
</script>

<f:view>
	<h2><ufrn:subSistema /> &gt; Avaliação de Projeto de Apoio a Novos Pesquisadores </h2>

	<h:form id="form">
		<table class="formulario" width="100%">
		<caption> Projeto de Apoio a Novos Pesquisadores </caption>

			<tr>
				<td colspan="6" class="subFormulario">Identificação</td>
			</tr>
			<tr class="negrito">
				<th>Nome:</th>
				<td colspan="2"> 
					<h:outputText id="nome" value="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.projeto.coordenador.nome}" />
				</td>

				<th>CPF:</th>
				<td colspan="2"> 
					<h:outputText id="cpf" value="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.projeto.coordenador.pessoa.cpf_cnpj }" /> 
				</td>
			</tr>

			<tr class="negrito">
				<th>RG:</th>
				<td> 
					<h:outputText id="identidade" value="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.projeto.coordenador.pessoa.identidade.numero }" /> 
				</td>

				<th>Org. Exp:</th>
				<td> 
					<h:outputText id="orgaoExp" value="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.projeto.coordenador.pessoa.identidade.orgaoExpedicao }" /> 
				</td>
			</tr>

			<tr class="negrito">
				<th>Unidade:</th>
				<td> 
					<h:outputText id="unidade" value="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.projeto.coordenador.unidade.nome }" /> 
				</td>

				<th>Telefone:</th>
				<td> 
					<h:outputText id="telefone" value="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.projeto.coordenador.pessoa.telefone }" /> 
				</td>

				<th>Fax:</th>
				<td> 
					<h:outputText id="fax" value="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.projeto.coordenador.pessoa.fax }" /> 
				</td>
			</tr>

			<tr class="negrito">
				<th>E-mail:</th>
				<td> <h:outputText id="email" value="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.projeto.coordenador.pessoa.email }" /> </td>

				<th>Lattes:</th>
				<td colspan="3"> 
					<a href="${ avaliacaoProjetoApoioNovosPesquisadoresMBean.projeto.coordenador.pessoa.perfil.enderecoLattes }" target="_blank">
						${ avaliacaoProjetoApoioNovosPesquisadoresMBean.projeto.coordenador.pessoa.perfil.enderecoLattes }</a>
				 </td>
			</tr>

			<tr>
				<td colspan="6" class="subFormulario">Edital</td>
			</tr>

			<tr class="negrito">
				<th>Edital de Pesquisa:</th>
				<td  colspan="5"> 
					<h:outputText id="editalPesquisa" value="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.projeto.editalPesquisa.edital.descricao }" />
				</td>
			</tr>

			<tr>
				<td colspan="6" class="subFormulario">Grupo de Pesquisa da Instituição</td>
			</tr>

			<tr class="negrito">
				<th width="21%">Grupo de Pesquisa:</th>
				<td colspan="5">
					<h:outputText id="suggestionNomeGP" value="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.projeto.grupoPesquisa.nome}" />
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
								<h:outputText id="titulo" value="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.projeto.projeto.titulo}" />
							</td>
						</tr>

						<tr>
							<td class="subFormulario" colspan="6">Objetivos</td>
						</tr>

						<tr>
							<td colspan="5"> 
								<h:outputText id="objetivo" value="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.projeto.projeto.objetivos }" />
							</td>
						</tr>		

						<tr>
							<td class="subFormulario" colspan="6">Metodologia</td>
						</tr>

						<tr>
							<td colspan="5"> 
								<h:outputText id="metodologia" value="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.projeto.projeto.metodologia }" />
							</td>
						</tr>		

						<tr>
							<td class="subFormulario" colspan="6">Resultados</td>
						</tr>

						<tr>
							<td colspan="5"> 
								<h:outputText id="resultados" value="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.projeto.projeto.resultados }" />
							</td>
						</tr>		

						<tr>
							<td class="subFormulario" colspan="6">Referências do Projeto</td>
						</tr>

						<tr>
							<td colspan="5"> 
								<h:outputText id="referencias" value="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.projeto.projeto.referencias }" />
							</td>
						</tr>		

						<tr>
							<td class="subFormulario" colspan="6">Integração</td>
						</tr>

						<tr>
							<td colspan="5"> 
								<h:outputText id="integracao" value="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.projeto.integracao }" />
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
												<c:forEach items="${avaliacaoProjetoApoioNovosPesquisadoresMBean.telaCronograma.mesesAno}" var="ano">
												<th colspan="${fn:length(ano.value)}" style="text-align: center" class="inicioAno fimAno">
													${ano.key}
												</th>
												</c:forEach>
											</tr>
											<tr>
												<c:forEach items="${avaliacaoProjetoApoioNovosPesquisadoresMBean.telaCronograma.mesesAno}" var="ano">
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
											<c:set var="numeroAtividades" value="${fn:length(avaliacaoProjetoApoioNovosPesquisadoresMBean.telaCronograma.cronogramas)}" />
											<c:set var="valoresCheckboxes" value=",${fn:join(avaliacaoProjetoApoioNovosPesquisadoresMBean.telaCronograma.calendario, ',')}" />
											<c:forEach begin="1" end="${numeroAtividades}" varStatus="statusAtividades">
											<tr>
												<th> ${avaliacaoProjetoApoioNovosPesquisadoresMBean.telaCronograma.atividade[statusAtividades.index-1]} </th>
												<c:forEach items="${avaliacaoProjetoApoioNovosPesquisadoresMBean.telaCronograma.mesesAno}" var="ano" varStatus="statusCheckboxes">
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
							<c:if test="${not empty avaliacaoProjetoApoioNovosPesquisadoresMBean.tabelaOrcamentaria}">
								
								<c:set value="${avaliacaoProjetoApoioNovosPesquisadoresMBean.tabelaOrcamentaria}" var="tabelaOrcamentaria" />
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
		
								<c:if test="${empty avaliacaoProjetoApoioNovosPesquisadoresMBean.tabelaOrcamentaria}">
									<tr><td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font> </td></tr>
								</c:if>
		
							</tbody>
					</table>
				</td>
			</tr>
			
		</table>
		
		<br/>
		
		<table class="formulario" width="100%">
			<caption>Avaliações Realizadas</caption>	
			<thead>
				<tr>
					<th>Avaliador</th>
					<th>Data</th>
					<th>Parecer</th>
				</tr>
			</thead>

			<c:forEach items="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.avaliacoes }" var="aval" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td> ${ aval.avaliador.nome } </td>
					<td> <ufrn:format type="datahora" valor="${ aval.dataAvaliacao }"/> </td>
					<td> ${ aval.parecer } </td>
				</tr>				
			</c:forEach>
		</table>
		
		<br/>
		
		<table class="formulario" width="100%">
			<caption>Avaliar Projeto</caption>	
			<tr>
				<td>
					<br/>
					<h:outputText styleClass="obrigatorio" value="Parecer:" /> 
					<br/>
					<h:inputTextarea id="parecer" value="#{avaliacaoProjetoApoioNovosPesquisadoresMBean.obj.parecer}" rows="10" style="width:99%" 
					readonly="#{avaliacaoProjetoApoioNovosPesquisadoresMBean.readOnly}" onkeydown="limitText(this, countParecer, 2000);" onkeyup="limitText(this, countParecer, 2000);"/>
					<center>
					     Você pode digitar <input readonly type="text" id="countParecer" size="3" value="${2000 - fn:length(avaliacaoProjetoApoioNovosPesquisadoresMBean.obj.parecer) < 0 ? 0 : 2000 - fn:length(avaliacaoProjetoApoioNovosPesquisadoresMBean.obj.parecer)}"> caracteres.
					</center>
					
				</td>
			</tr>
			<tr><td colspan="2"></td></tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btAvaliarProjeto" value="Avaliar Projeto"action="#{avaliacaoProjetoApoioNovosPesquisadoresMBean.avaliar}" />
						<h:commandButton id="btVoltar" value="<< Voltar"action="#{avaliacaoProjetoApoioNovosPesquisadoresMBean.listarProjetos}"/>
						<h:commandButton id="btCancelar" value="Cancelar" action="#{avaliacaoProjetoApoioNovosPesquisadoresMBean.cancelar}" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>