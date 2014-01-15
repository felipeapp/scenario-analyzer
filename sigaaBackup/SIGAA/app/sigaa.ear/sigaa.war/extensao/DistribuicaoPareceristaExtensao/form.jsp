<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript">
	JAWR.loader.script('/javascript/jquery/jquery.js');
	JAWR.loader.script('/javascript/jquery.tablesorter.min.js');
</script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<%@page import="br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAvaliacao"%>

<f:view>
	<h2><ufrn:subSistema /> > Distribuição de Ações de Extensão para Avaliadores Ad Hoc</h2>
	<br>
	
	<h:form id="form">
	  <table class="formulario" width="100%">
		<caption class="listagem">Distribuir Ações de Extensão</caption>
			<tr>			
				<th width="25%" class="rotulo">Título da Ação:</th>
				<td><h:outputText value="#{distribuicaoExtensao.obj.atividade.titulo}" /></td>
			</tr>
			<tr>
				<th width="25%" class="rotulo"> Área Temática: </th>
				<td><h:outputText value="#{distribuicaoExtensao.obj.atividade.areaTematicaPrincipal.descricao}" /></td>
			</tr>
			<tr>
				<th width="25%" class="rotulo"> Unidade Proponente: </th>
				<td><h:outputText value="#{distribuicaoExtensao.obj.atividade.unidade.nome}" /></td>
			</tr>
			<tr>
				<th width="25%" class="rotulo"> Outras Unidades Envolvidas: </th>
				<td>				
				  <c:if test="${not empty distribuicaoExtensao.obj.atividade.unidadesProponentes}">		
						<t:dataTable value="#{distribuicaoExtensao.obj.atividade.unidadesProponentes}" var="unidadeP" align="center" width="100%" id="autoriz">											
							<t:column><h:outputText value="#{unidadeP.unidade.nome}"/></t:column>				
						</t:dataTable>
					</c:if>
				</td>
			</tr>
			<tr>			
				<td colspan="2">

					<div class="infoAltRem">
					    <img src="${ ctx }/img/delete.gif" style="overflow: visible;" />: Remover Avaliador
					</div>					
				
					<table class="subFormulario" width="100%" id="avalidoresDaAcao">
						<caption>Lista de avaliadores ad hoc avaliando esta ação</caption>
						<thead id="headLista">
				  		   	<tr>
				  		   		<td>Avaliador(a)</td>
				  		   		<td>Área Temática</td>
				  		   		<td>Situação</td>
				  		   		<td>&nbsp;</td>
						  	</tr>
						  </thead>
						  <tbody>
								<c:set var="AVALIACAO_ACAO_PARECERISTA" value="<%= String.valueOf(TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA) %>" scope="application"/>
								<c:set var="AGUARDANDO_AVALIACAO" value="<%= String.valueOf(StatusAvaliacao.AGUARDANDO_AVALIACAO) %>" scope="application"/>
								<c:set var="AVALIACAO_CANCELADA" value="<%= String.valueOf(StatusAvaliacao.AVALIACAO_CANCELADA) %>" scope="application"/>
																
								<c:set value="#{distribuicaoExtensao.obj.atividade.avaliacoes}" var="avaliacoesJaDistribuidas" />								
								<c:if test="${not empty avaliacoesJaDistribuidas}">
									<c:forEach items="#{avaliacoesJaDistribuidas}" var="avaliacao" varStatus="status">
												<c:if test="${(avaliacao.tipoAvaliacao.id eq AVALIACAO_ACAO_PARECERISTA) and (avaliacao.statusAvaliacao.id ne AVALIACAO_CANCELADA)}">												
														<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" id="linhaLista">
															<td><c:out value="${avaliacao.avaliadorAtividadeExtensao.servidor.nome}"/></td>
															<td><c:out value="${avaliacao.avaliadorAtividadeExtensao.areaTematica.descricao}" /></td>															
															<td><c:out value="${avaliacao.statusAvaliacao.descricao}" /></td>
															<td>
																<h:commandLink action="#{distribuicaoExtensao.removerAvaliacaoParecerista}" id="btRemover" title="Remover Avaliador">
																	<h:graphicImage value="/img/delete.gif" />
																	<f:param name="idAvaliacao" value="#{ avaliacao.id }" />
																	<f:param name="idAvaliador" value="#{ avaliacao.avaliadorAtividadeExtensao.id }" />
																</h:commandLink>
															</td>
														</tr>
												</c:if>
									</c:forEach>
								</c:if>
								<c:if test="${empty avaliacoesJaDistribuidas}">
										<tr>
											<td colspan="4" align="center"><font color="red">Não há avaliadores ad hoc avaliando esta ação de extensão.</font></td>
										</tr>									
								</c:if>								
						  </tbody>
					</table>
				</td>
			</tr>
			</table>
			
			<div class="infoAltRem">
			    <img src="${ ctx  }/img/adicionar.gif" style="overflow: visible;" />: Incluir Avaliador
			</div>					
	
			<h:inputHidden value="#{distribuicaoExtensao.obj.atividade.id}" />
			<h:inputHidden value="#{distribuicaoExtensao.obj.atividade.unidade.id}" />
			
 			    <table width="100%" class="listagem tablesorter" id="listagem">
				<caption>Avaliadores Ad Hoc Disponíveis </caption>
		  		<thead id="headLista">
		  		   	<tr>
		  		   		<th>Avaliador(a)</th>
		  		   		<th>Área Temática</th>				  		   		
		  		   		<th>&nbsp;</th>
				  	</tr>
			    </thead>
				<tbody>															  
			  		<c:set var="avaliadoresDisponiveis" value="#{distribuicaoExtensao.obj.avaliadoresPossiveis}"/>												  
					<c:forEach items="#{ avaliadoresDisponiveis }" var="avaliador" varStatus="status">
						<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" id="linhaLista">
							<td><c:out value="${avaliador.servidor.pessoa.nome}"/></td>															
							<td><c:out value="${avaliador.areaTematica.descricao}" /></td>																												
							<td width="2%">								
								<h:commandLink action="#{distribuicaoExtensao.adicionarAvaliacaoParecerista}" id="btAdicionar" title="Incluir Avaliador">
									<h:graphicImage value="/img/adicionar.gif" />
									<f:param name="idAvaliador" value="#{ avaliador.id }" />				
								</h:commandLink>	
							</td>
						</tr>
					</c:forEach>
					
					<c:if test="${empty avaliadoresDisponiveis}">
						<tr><td colspan="4"><center><font color="red">Não há Avaliadores disponíveis habilitados para avaliar esta ação de extensão.</font></center></td></tr>
					</c:if>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="4" align="center">	
							<h:commandButton action="#{distribuicaoExtensao.distribuir}" value="Confirmar Distribuição" id="confirmarDistribuicao"/>
							<h:commandButton  action="#{distribuicaoExtensao.distribuirOutraAtividadeParecerista}" value="Cancelar" id="cancelar" onclick="#{confirm}"/>						
						</td>		
					</tr>
				</tfoot>
		  </table>
		  <rich:jQuery selector="#listagem" query="tablesorter( {headers: {2: { sorter: false } } });" timing="onload" />
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>