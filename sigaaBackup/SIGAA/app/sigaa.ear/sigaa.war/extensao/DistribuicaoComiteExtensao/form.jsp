<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript">
	JAWR.loader.script('/javascript/jquery/jquery.js');
	JAWR.loader.script('/javascript/jquery.tablesorter.min.js');
</script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<%@page import="br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAvaliacao"%>

<f:view>
	<h2><ufrn:subSistema /> > Distribuição de Ações de Extensão para Membros do Comitê</h2>

	<h:form id="form">
	  <table class="formulario" width="100%">
		<caption class="listagem">Distribuir Ações de Extensão</caption>
			
			<tr>			
				<th width="25%"> <b>Título da Ação: </b></th>
				<td>
					<h:outputText value="#{distribuicaoExtensao.obj.atividade.titulo}" />
				</td>
			</tr>
			<tr>
				<th width="25%"><b> Área Temática: </b></th>
				<td>
					<h:outputText value="#{distribuicaoExtensao.obj.atividade.areaTematicaPrincipal.descricao}" />
				</td>
			</tr>
			<tr>
				<th width="25%"><b> Unidade Proponente: </b></th>
				<td>
					<h:outputText value="#{distribuicaoExtensao.obj.atividade.unidade.nome}" />
				</td>
			</tr>		
			<tr>
				<th width="25%"><b> Outras Unidades Envolvidas: </b></th>
				<td>				
				  <c:if test="${not empty distribuicaoExtensao.obj.atividade.unidadesProponentes}">		
						<t:dataTable value="#{distribuicaoExtensao.obj.atividade.unidadesProponentes}" var="unidadeP" align="center" width="100%" id="autoriz">											
							<t:column>
								<h:outputText value="<b>#{unidadeP.unidade.nome}</b>" escape="false"/>
							</t:column>				
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
						<caption>Lista de Membros do Comitê avaliando esta ação</caption>
						<thead id="headLista">
				  		   	<tr>
				  		   		<td>Avaliador(a)</td>
				  		   		<td>Departamento</td>				  		   		
				  		   		<td>Situação</td>
				  		   		<td>&nbsp;</td>
						  	</tr>
						  </thead>

								<c:set var="AVALIACAO_ACAO_COMITE" value="<%= String.valueOf(TipoAvaliacao.AVALIACAO_ACAO_COMITE)  %>" scope="application"/>
								<c:set var="AGUARDANDO_AVALIACAO"  value="<%= String.valueOf(StatusAvaliacao.AGUARDANDO_AVALIACAO) %>" scope="application"/>
								<c:set var="AVALIACAO_CANCELADA"   value="<%= String.valueOf(StatusAvaliacao.AVALIACAO_CANCELADA)  %>" scope="application"/>
																
								<c:set value="#{distribuicaoExtensao.obj.atividade.avaliacoes}" var="avaliacoes" />								
								<c:if test="${not empty avaliacoes}">
									<c:forEach items="#{avaliacoes}" var="avaliacao" varStatus="status">
										<c:if test="${(avaliacao.tipoAvaliacao.id eq AVALIACAO_ACAO_COMITE) and (avaliacao.statusAvaliacao.id ne AVALIACAO_CANCELADA)}">												
											<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" id="linhaLista">
												<td><c:out value="${avaliacao.membroComissao.servidor.nome}"/></td>
												<td><c:out value="${avaliacao.membroComissao.servidor.unidade.nome}" /></td>															
												<td><c:out value="${avaliacao.statusAvaliacao.descricao}" /></td>
												<td>
													<a4j:commandLink action="#{distribuicaoExtensao.removerAvaliacaoComite}" id="btRemover" title="Remover Avaliador">
														<h:graphicImage value="/img/delete.gif" />
														<f:param name="idAvaliacao" value="#{ avaliacao.id }" />
														<f:param name="idMembroComite" value="#{ avaliacao.membroComissao.id }" />														
													</a4j:commandLink>
												</td>
											</tr>
										</c:if>
									</c:forEach>
								</c:if>
								<c:if test="${empty avaliacoes}">
									<tr>
										<td colspan="4" align="center"><font color="red">Não há avaliadores avaliando esta ação de extensão.</font></td>
									</tr>									
								</c:if>
					</table>					
				</td>
			</tr>
		</table>

		<div class="infoAltRem">
			<img src="${ ctx  }/img/adicionar.gif" style="overflow: visible;" />: Incluir Avaliador
		</div>
		
		<h:inputHidden value="#{distribuicaoExtensao.obj.atividade.id}" />
		<h:inputHidden value="#{distribuicaoExtensao.obj.atividade.unidade.id}" />
							
	  	<table width="100%"  class="listagem tablesorter subFormulario" id="listagem">
			<caption>Membros do Comitê Disponíveis </caption>
	  		<thead id="headLista">
	  		   	<tr>
	  		   		<th>Membros do Comitê</th>
	  		   		<th>Departamento</th>				  		   		
	  		   		<th>&nbsp;</th>
			  	</tr>
		    </thead>
	  		<tbody>
		  		<c:set var="avaliadoresDisponiveis" value="#{distribuicaoExtensao.obj.membrosComitePossiveis}"/>
		  
				<c:forEach items="#{ avaliadoresDisponiveis }" var="membroComite" varStatus="status">
					<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" id="linhaLista">
						<td><c:out value="${membroComite.servidor.pessoa.nome}"/></td>															
						<td><c:out value="${membroComite.servidor.unidade.nome}" /></td>																												
						<td>
							<h:commandLink action="#{distribuicaoExtensao.adicionarAvaliacaoComite}" id="btAdicionar" title="Incluir Avaliador">
								<h:graphicImage value="/img/adicionar.gif" />
								<f:param name="idMembroComite" value="#{ membroComite.id }" />				
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
				
				<c:if test="${empty avaliadoresDisponiveis}">
					<tr><td colspan="2"><center><font color="red">Não há Avaliadores disponíveis habilitados para avaliar esta ação de extensão.</font></center></td></tr>
				</c:if>														
	  		</tbody>
			<tfoot>
				<tr>
					<td colspan="4" align="center">	
						<h:commandButton action="#{distribuicaoExtensao.distribuir}" value="Confirmar Distribuição" id="btDistribuir"/>
						<h:commandButton action="#{distribuicaoExtensao.distribuirOutraAtividadeComite}" value="Cancelar" onclick="#{confirm}" id="btCancelar"/>						
					</td>		
				</tr>
			</tfoot>
		</table>
		<rich:jQuery selector="#listagem" query="tablesorter( {headers: {2: { sorter: false } } });" timing="onload" />
  </h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>