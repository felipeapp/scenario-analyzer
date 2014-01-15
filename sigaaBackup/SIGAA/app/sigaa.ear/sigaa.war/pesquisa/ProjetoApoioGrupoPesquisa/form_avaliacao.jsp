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
	<h2><ufrn:subSistema /> &gt; Avaliação de Projeto de Apoio a Grupo de Pesquisa </h2>

	<h:form id="form">
		<table class="formulario" width="100%">
		<caption> Identificação </caption>

			<tr>
				<th><b>Título:</b></th>
				<td> 
					<h:outputText id="titulo" value="#{ avaliacaoProjetoApoioGrupoPesquisaMBean.projeto.projeto.titulo}" />
				</td>
			</tr>

			<tr>
				<th><b>Edital de Pesquisa</b></th>
				<td> 
					<h:outputText id="edital" value="#{ avaliacaoProjetoApoioGrupoPesquisaMBean.projeto.editalPesquisa.edital.descricao }" />
				</td>
			</tr>
			
			<tr>
				<th width="21%"><b>Grupo de Pesquisa:</b></th>
				<td>
					<h:outputText id="grupoPesquisa" value="#{ avaliacaoProjetoApoioGrupoPesquisaMBean.projeto.grupoPesquisa.nome}" />
				</td>
			</tr>

			<tr>
				<td colspan="2" class="subFormulario">Justificativa dos Recursos Solicitados</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<h:outputText value="#{ avaliacaoProjetoApoioGrupoPesquisaMBean.projeto.projeto.justificativa }" />
				</td>
			</tr>			

			<tr>
				<td colspan="2" class="subFormulario"> Integração do(s) Projetos de Pesquisa </td>
			</tr>
			<tr>
				<td colspan="2">
					<h:outputText value="#{ avaliacaoProjetoApoioGrupoPesquisaMBean.projeto.integracao }" />
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

								<c:if test="${not empty avaliacaoProjetoApoioGrupoPesquisaMBean.tabelaOrcamentaria}">
									
									<c:set value="${avaliacaoProjetoApoioGrupoPesquisaMBean.tabelaOrcamentaria}" var="tabelaOrcamentaria" />
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

									<c:if test="${empty avaliacaoProjetoApoioGrupoPesquisaMBean.tabelaOrcamentaria}">
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

			<c:forEach items="#{ avaliacaoProjetoApoioGrupoPesquisaMBean.avaliacoes }" var="aval" varStatus="status">
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
					<h:inputTextarea id="parecer" value="#{avaliacaoProjetoApoioGrupoPesquisaMBean.obj.parecer}" rows="10" style="width:99%" 
					readonly="#{avaliacaoProjetoApoioGrupoPesquisaMBean.readOnly}" onkeydown="limitText(this, countParecer, 2000);" onkeyup="limitText(this, countParecer, 2000);"/>
					<center>
					     Você pode digitar <input readonly type="text" id="countParecer" size="3" value="${2000 - fn:length(avaliacaoProjetoApoioGrupoPesquisaMBean.obj.parecer) < 0 ? 0 : 2000 - fn:length(avaliacaoProjetoApoioGrupoPesquisaMBean.obj.parecer)}"> caracteres.
					</center>
					
				</td>
			</tr>
			<tr><td colspan="2"></td></tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btAvaliarProjeto" value="Avaliar Projeto"action="#{avaliacaoProjetoApoioGrupoPesquisaMBean.avaliar}" />
						<h:commandButton id="btVoltar" value="<< Voltar"action="#{avaliacaoProjetoApoioGrupoPesquisaMBean.listarProjetos}"/>
						<h:commandButton id="btCancelar" value="Cancelar" action="#{avaliacaoProjetoApoioGrupoPesquisaMBean.cancelar}" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>