<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<%@page import="br.ufrn.sigaa.monitoria.dominio.TipoAvaliacaoMonitoria"%>
<%@page import="br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao"%>

<script type="text/javascript">

function setarMembroComissao(evt) {
	var obj = evt.target ? evt.target : evt.srcElement;
	var hidden = getEl(obj.parentNode).getChildrenByClassName('avaliador')[0];
	getEl('idMembroComissao').dom.value = hidden.dom.value;
}


function setarMembroComissaoPossivel(evt) {
	var obj = evt.target ? evt.target : evt.srcElement;
	var hidden = getEl(obj.parentNode).getChildrenByClassName('membro')[0];
	getEl('idMembro').dom.value = hidden.dom.value;
}

</script>

<f:view>
	<h2><ufrn:subSistema /> > Distribuição de Relatórios de Projetos de Ensino para Membros da Comissão</h2>
	<br>

	<table class="formulario" width="100%">
		<caption class="listagem">Distribuir Relatórios de Projetos</caption>
	
			<c:choose>
				<c:when test="${ empty distribuicaoRelatorio.relatoriosProjeto }">
					<tr>			
						<th width="20%"> Título do Projeto: </th>
						<td>
							<b><h:outputText value="#{distribuicaoRelatorio.obj.relatorio.projetoEnsino.titulo}" /></b>
						</td>
					</tr>
		
					<tr>			
						<th width="20%"> Tipo de Relatório: </th>
						<td>
							<b><h:outputText value="#{distribuicaoRelatorio.obj.relatorio.tipoRelatorio.descricao}" /></b>
						</td>
					</tr>
		
					<tr>
						<th> Centro: </th>
						<td>
							<b><h:outputText value="#{distribuicaoRelatorio.obj.relatorio.projetoEnsino.unidade.nome}" /></b>
						</td>
					</tr>
		
					<tr>
						<th> Ano: </th>
						<td>
							<b><h:outputText value="#{distribuicaoRelatorio.obj.relatorio.projetoEnsino.ano}" /></b>
						</td>
					</tr>
				</c:when>
				<c:otherwise>
					<thead id="headLista">
						<tr>
							<th> Título do Projeto </th>
							<th> Tipo de Relatório </th>
							<th> Centro </th>
							<th> Ano </th>
						</tr>
					</thead>
					<c:forEach var="linha" items="${ distribuicaoRelatorio.relatoriosProjeto }">
							<tr>
								<td> ${ linha.projetoEnsino.titulo } </td>
								<td> ${ linha.tipoRelatorio.descricao } </td>
								<td> ${ linha.projetoEnsino.unidade.nome } </td>
								<td> ${ linha.projetoEnsino.ano } </td>
							</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
			
			<tr>			
				<td colspan="4">
				
				<h:form id="formAvaliadoresDeFato">
				
					<input type="hidden" name="idMembroComissao" id="idMembroComissao" value="0"/>
				
					<table class="subFormulario" width="100%" id="avalidoresDoProjeto">
						<caption>Lista de Avaliadores do Relatório</caption>
						<thead id="headLista">
				  		   	<tr>
				  		   		<td>Avaliador(a)</td>
				  		   		<td>Departamento</td>				  		   		
				  		   		<td>Situação</td>
				  		   		<td>&nbsp;</td>
						  	</tr>
						  </thead>

								<c:set var="AVALIACAO_DE_PROJETO" value="<%= String.valueOf(TipoAvaliacaoMonitoria.AVALIACAO_PROJETO_ENSINO) %>" scope="application"/>
								<c:set var="AGUARDANDO_AVALIACAO" value="<%= String.valueOf(StatusAvaliacao.AGUARDANDO_AVALIACAO) %>" scope="application"/>
								<c:set var="AVALIACAO_CANCELADA" value="<%= String.valueOf(StatusAvaliacao.AVALIACAO_CANCELADA) %>" scope="application"/>
																

								<c:if test="${not empty distribuicaoRelatorio.obj.relatorio.avaliacoes}">
									<c:forEach items="${distribuicaoRelatorio.obj.relatorio.avaliacoes}" var="avaliacao" varStatus="status">
									
												<c:if test="${avaliacao.statusAvaliacao.id ne AVALIACAO_CANCELADA}">												

														<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" id="linhaLista">
														
															<td><c:out value="${avaliacao.avaliador.servidor.nome}"/></td>
															<td><c:out value="${avaliacao.avaliador.servidor.unidade.nome}" /></td>															
															<td><c:out value="${avaliacao.statusAvaliacao.descricao}" /></td>

															<td>
																<c:if test="${avaliacao.statusAvaliacao.id eq AGUARDANDO_AVALIACAO}">
																		<input type="hidden" class="avaliador" value="${ avaliacao.avaliador.id }"/>
																		<h:commandButton action="#{distribuicaoRelatorio.removerAvaliacao}" image="/img/delete.gif" value="Excluir" onclick="setarMembroComissao(event);"/>
																</c:if>
															</td>
														</tr>
											
												</c:if>

									</c:forEach>
								</c:if>
								<c:if test="${empty distribuicaoRelatorio.obj.relatorio.avaliacoes}">
										<tr>
											<td colspan="4" align="center"><font color="red">Atualmente, não há membros da comissão de monitoria avaliando este realatório.</font></td>
										</tr>									
								</c:if>
								
					</table>
					
				</h:form>
					
				</td>
			</tr>
	


			<tr>
				<td colspan="4">
					<div class="infoAltRem">
					    <img src="${ ctx }/img/delete.gif" style="overflow: visible;" />: Excluir da lista de avaliadores do relatório
   					    <img src="${ ctx  }/img/adicionar.gif" style="overflow: visible;" />: Incluir na lista de avaliadores do relatório
					</div>
					
					<hr/>
					<br/>
					
				</td>
			</tr>
	
			<h:inputHidden value="#{distribuicaoRelatorio.obj.relatorio.id}" />
			<h:inputHidden value="#{distribuicaoRelatorio.obj.relatorio.projetoEnsino.unidade.id}" />
	
			<tr>			
				<td colspan="4">
								<h:form id="formAvaliadoresPossiveis">
								<input type="hidden" name="idMembro" id="idMembro" value="0"/>

								<table class="subFormulario" width="100%" id="listaMembroComissaoPossiveis">
									<caption>Avaliadores Disponíveis - Membros da Comissão de Monitoria</caption>
									<tr>
										<td>
											  <table width="100%">
											  		<thead id="headLista">
											  		   	<tr>
											  		   		<td>Avaliador(a)</td>
											  		   		<td>Departamento</td>				  		   		
											  		   		<td>&nbsp;</td>
													  	</tr>
												     </thead>
											  
													<c:forEach items="${ distribuicaoRelatorio.membrosComissaoDisponiveis }" var="mComissao" varStatus="status">
													
														<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" id="linhaLista">

															<td><c:out value="${mComissao.servidor.pessoa.nome}"/></td>															
															<td><c:out value="${mComissao.servidor.unidade.nome}" /></td>																												
															<td>
																	<input type="hidden" class="membro" value="${mComissao.id}"/>
																	<h:commandButton action="#{distribuicaoRelatorio.adicionarAvaliacao}" image="/img/adicionar.gif" onclick="setarMembroComissaoPossivel(event);"/>
															</td>
														</tr>
	
													</c:forEach>
											  </table>
									 	</td>
									 </tr>
							  </table>
							  
							</h:form>							  
					</td>
				</tr>

	<h:form>

			<tfoot>
				<tr>
					<td colspan="4" align="center">	
						<h:commandButton action="#{distribuicaoRelatorio.distribuir}" value="Confirmar Distribuição"/>
						<h:commandButton  action="#{distribuicaoRelatorio.cancelar}" value="Cancelar"/>						
						<h:commandButton  action="#{distribuicaoRelatorio.distribuirOutroRelatorio}" value="Distribuir Outro Relatório"/>						
					</td>		
				</tr>
			</tfoot>
			                                                                                                                                                                                                                                                                                                                                                       
	</h:form>

   </table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>