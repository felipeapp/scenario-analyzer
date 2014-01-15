<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<style>
<!--

/* Paineis de opçoes */
.titulo {
	background: #EFF3FA;
}
.subtitulo{
	text-align:center; 
	background:#EDF1F8; 
	color:#333366; 
	font-variant:small-caps;
	font-weight:bold;
	letter-spacing:1px;
	margin:1px 0; 
	border-collapse:collapse; 
	border-spacing:2px;
	font-size:1em; 
	font-family:Verdana,sans-serif; 
	font-size: 12px
}

-->
</style>

<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAvaliacao"%>

<f:view>
	<h2>Visualização da Avaliação</h2>

<h:form id="formVisualizacaoAvaliacao">

	<c:set var="AVALIACAO_AD_HOC" 	value="<%= String.valueOf(TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA) %>" 		scope="application"/>


	<h3 class="tituloTabelaRelatorio"> DADOS DA AVALIAÇÃO </h3>
	<table class="tabelaRelatorio" width="100%">
	<tbody>

		<tr>
			<th> Código:</th>
			<td> <h:outputText value="#{avaliacaoAtividade.avaliacaoSelecionada.atividade.codigo}"/> </td>
		</tr>
		
		<tr>
			<th width="23%">Título:</th>
			<td> <h:outputText value="#{avaliacaoAtividade.avaliacaoSelecionada.atividade.titulo}"/> </td>
		</tr>

		<tr>
			<th> Ano:  </th>
			<td> <h:outputText value="#{avaliacaoAtividade.avaliacaoSelecionada.atividade.ano}"/> </td>
		</tr>

		<tr>
			<th> Financiamento:  </th>
			<td> <h:outputText value="#{avaliacaoAtividade.avaliacaoSelecionada.atividade.fonteFinanciamentoString}"/> </td>
		</tr>
	
		<tr>
			<th> Tipo de Ação:  </th>
			<td> <h:outputText value="#{avaliacaoAtividade.avaliacaoSelecionada.atividade.tipoAtividadeExtensao.descricao}"/> </td>
		</tr>
	
		<tr>
			<th> Tipo da Avaliação:  </th>
			<td> <h:outputText value="#{avaliacaoAtividade.avaliacaoSelecionada.tipoAvaliacao.descricao}"/> </td>
		</tr>

		<tr>
			<th> Avaliador(a):  </th>
			<td>
				<c:if test="${(acesso.extensao) or (avaliacaoAtividade.avaliacaoSelecionada.avaliador.id == avaliacaoAtividade.usuarioLogado.servidor.id)}"> 
					<h:outputText value="#{avaliacaoAtividade.avaliacaoSelecionada.avaliadorAtividadeExtensao.servidor.pessoa.nome}" rendered="#{not empty avaliacaoAtividade.avaliacaoSelecionada.avaliadorAtividadeExtensao}"/>
					<h:outputText value="#{avaliacaoAtividade.avaliacaoSelecionada.membroComissao.servidor.pessoa.nome}" rendered="#{not empty avaliacaoAtividade.avaliacaoSelecionada.membroComissao}"/>
				</c:if>
				<c:if test="${(not acesso.extensao) and (not (avaliacaoAtividade.avaliacaoSelecionada.avaliador.id == avaliacaoAtividade.usuarioLogado.servidor.id))}">
				 <font color=red>[Visualização não autorizada]</font>
				</c:if>									
			</td>
		</tr>

		<tr>
			<th> Situação:  </th>
			<td> <h:outputText value="#{avaliacaoAtividade.avaliacaoSelecionada.statusAvaliacao.descricao}"/> </td>
		</tr>
		
		<tr>
			<th> Data da Avaliação:  </th>
			<td> <fmt:formatDate value="${avaliacaoAtividade.avaliacaoSelecionada.dataAvaliacao}" pattern="dd/MM/yyy HH:mm:ss"/> </td>
		</tr>
		
		
		<tr>
			<th> Parecer:  </th>
			<td> <h:outputText value="#{avaliacaoAtividade.avaliacaoSelecionada.parecer.descricao}"/> </td>
		</tr>
		
		<c:if	test="${avaliacaoAtividade.avaliacaoSelecionada.tipoAvaliacao.id == AVALIACAO_AD_HOC}">
						
			<tr>
				<th> Nota:  </th>
				<td>
					<c:if test="${(acesso.extensao) or (avaliacaoAtividade.avaliacaoSelecionada.avaliador.id == avaliacaoAtividade.usuarioLogado.servidor.id)}">
						<h:outputText value="#{avaliacaoAtividade.avaliacaoSelecionada.nota}"/>
					</c:if>
					<c:if test="${(not acesso.extensao) and (not (avaliacaoAtividade.avaliacaoSelecionada.avaliador.id == avaliacaoAtividade.usuarioLogado.servidor.id))}">
						 <font color=red>[Visualização não autorizada]</font>
					</c:if>									
				</td>
			</tr>
		</c:if>

		<tr>
			<th> Justificativa:  </th>
			<td>
				<h:outputText value="#{avaliacaoAtividade.avaliacaoSelecionada.justificativa}"/>
				<br />
				
			</td> 
		</tr>
		
		
		<c:if	test="${avaliacaoAtividade.avaliacaoSelecionada.tipoAvaliacao.id == AVALIACAO_AD_HOC}">
		<tr>
				<td colspan="2">
				<h3 class="subtitulo">Itens de Avaliação</h3>
				
					<table width="100%" class="subFormulario">

							
							
						<thead>							
							<tr>
								<td width="50%">Descrição do Item Avaliado</td>
								<td width="10%" >Nota</td>
								<td width="10%" >Máximo</td>
								<td width="10%" >Peso</td>
								<td width="30%" style="text-align: center;">Considerado na Avaliação</td>								
							</tr>
							
						</thead>
					
						<c:forEach items="${ avaliacaoAtividade.notasItem }" var="nota" varStatus="status">
							<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td>${ nota.itemAvaliacao.descricao }</td>
								<td><fmt:formatNumber pattern="#0.00" value="${ nota.nota }"/></td>
								<td><fmt:formatNumber pattern="#0.00" value="${ nota.itemAvaliacao.notaMaxima }"/></td>
								<td><fmt:formatNumber pattern="#0.00" value="${ nota.itemAvaliacao.peso }"/></td>
								<td style="text-align: center;">
									<c:if test="${ !nota.desconsiderar }" > SIM </c:if>
									<c:if test="${ nota.desconsiderar }" > NÃO </c:if>
								</td>				
							</tr>
						</c:forEach>
	
					</table>
					<br/>&nbsp;
				</td>
			</tr>
		</c:if>

		
		<c:if	test="${avaliacaoAtividade.avaliacaoSelecionada.tipoAvaliacao.id != AVALIACAO_AD_HOC}">
			<tr>
				<th> Bolsas Propostas:  </th>
				<td> <h:outputText value="#{avaliacaoAtividade.avaliacaoSelecionada.bolsasPropostas}"/> </td>
			</tr>
		</c:if>
		
		
		
		<c:if	test="${avaliacaoAtividade.avaliacaoSelecionada.tipoAvaliacao.id != AVALIACAO_AD_HOC}">
		<tr>
			<th valign="top">Orçamento Proposto: </th>
			<td>


							<t:dataTable id="dt"
								value="#{avaliacaoAtividade.avaliacaoSelecionada.orcamentoProposto}"
								var="proposto" align="center" width="95%" styleClass="listagem"
								rowClasses="linhaPar, linhaImpar" rowIndexVar="index"
								forceIdIndex="true">
								<t:column>
									<f:facet name="header">
										<f:verbatim>Descrição</f:verbatim>
									</f:facet>
									<h:outputText value="#{proposto.elementoDespesa.descricao}" />
								</t:column>

								<t:column>
									<f:facet name="header">
										<f:verbatim>Valor Proposto</f:verbatim>
									</f:facet>
									<f:verbatim>R$ </f:verbatim>
									<h:outputText value="#{proposto.valorProposto}"
										id="valorProposto" style="text-align: right">
										<f:convertNumber pattern="#,##0.00" />
									</h:outputText>
								</t:column>
							</t:dataTable>

					<c:if	test="${empty avaliacaoAtividade.avaliacaoSelecionada.orcamentoProposto}">
						<center><font color="red">Recursos não solicitados</font></center>	
					</c:if>
					 
				</td>
		</tr>
		</c:if>
		
	</tbody>
</table>


</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>