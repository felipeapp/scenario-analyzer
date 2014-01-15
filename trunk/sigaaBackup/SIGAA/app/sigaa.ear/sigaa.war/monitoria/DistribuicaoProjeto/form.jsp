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
	<h2><ufrn:subSistema /> > Distribuição de Projetos de Ensino para Membros da Comissão</h2>
	<br>

	<table class="visualizacao" width="100%">
		<caption class="listagem">Distribuir Projetos</caption>
	
			
			<tr>			
				<th width="25%"> Título do Projeto: </th>
				<td>
					<h:outputText value="#{distribuicaoProjeto.obj.projetoEnsino.titulo}" />
				</td>
			</tr>

			

			<tr>
				<th width="25%"> Edital: </th>
				<td>
					<h:outputText value="#{distribuicaoProjeto.obj.projetoEnsino.editalMonitoria.descricao}" />
				</td>
			</tr>

			<tr>
				<th width="25%"> Centro: </th>
				<td>
					<h:outputText value="#{distribuicaoProjeto.obj.projetoEnsino.unidade.nome}" />
				</td>
			</tr>

			
			<tr>
				<th width="25%"> Departamentos Envolvidos: </th>
				<td style="font-weight: normal;">				
				  <c:if test="${not empty distribuicaoProjeto.obj.projetoEnsino.autorizacoesProjeto}">		
						<t:dataTable value="#{distribuicaoProjeto.obj.projetoEnsino.autorizacoesProjeto}" var="auto"  width="100%" id="autoriz">											
									<t:column >
										<h:outputText value="#{auto.unidade.nome}" escape="false"/>
									</t:column>				
						</t:dataTable>
					</c:if>
				</td>
		  </tr>
			
		
  		  <tr>			
				<td colspan="2">
				
				<h:form id="formAvaliadoresDeFato">
				
					<input type="hidden" name="idMembroComissao" id="idMembroComissao" value="0"/>
				
					<table class="subFormulario" width="100%" id="avalidoresDoProjeto">
						<caption>Lista de Avaliadores do Projeto</caption>
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
																

								<c:if test="${not empty distribuicaoProjeto.obj.projetoEnsino.avaliacoes}">
									<c:forEach items="${distribuicaoProjeto.obj.projetoEnsino.avaliacoes}" var="avaliacao" varStatus="status">
									
												<c:if test="${(avaliacao.tipoAvaliacao.id eq AVALIACAO_DE_PROJETO) and (avaliacao.statusAvaliacao.id ne AVALIACAO_CANCELADA)}">												

														<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" id="linhaLista">
														
															<td><c:out value="${avaliacao.avaliador.servidor.nome}"/></td>
															<td><c:out value="${avaliacao.avaliador.servidor.unidade.nome}" /></td>															
															<td><c:out value="${avaliacao.statusAvaliacao.descricao}" /></td>

															<td>
																<input type="hidden" class="avaliador" value="${ avaliacao.avaliador.id }"/>
																<h:commandButton action="#{distribuicaoProjeto.removerAvaliacao}" image="/img/delete.gif" value="Excluir" onclick="setarMembroComissao(event);"/>
															</td>
														</tr>
											
												</c:if>

									</c:forEach>
								</c:if>
								<c:if test="${empty distribuicaoProjeto.obj.projetoEnsino.avaliacoes}">
										<tr>
											<td colspan="4" align="center"><font color="red">Atualmente, não há membros da comissão de monitoria avaliando este projeto.</font></td>
										</tr>									
								</c:if>
								
					</table>
					
				</h:form>
					
				</td>
			</tr>
	


			<tr>
				<td colspan="2">
					<div class="infoAltRem">
					    <img src="${ ctx }/img/delete.gif" style="overflow: visible;" />: Excluir da lista de avaliadores projeto
   					    <img src="${ ctx  }/img/adicionar.gif" style="overflow: visible;" />: Incluir na lista de avaliadores projeto
					</div>
				</td>
			</tr>


	
			<h:inputHidden value="#{distribuicaoProjeto.obj.projetoEnsino.id}" />
			<h:inputHidden value="#{distribuicaoProjeto.obj.projetoEnsino.unidade.id}" />
	
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
											  
													<c:forEach items="${ distribuicaoProjeto.membrosComissaoDisponiveis }" var="mComissao" varStatus="status">
													
														<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" id="linhaLista">

															<td><c:out value="${mComissao.servidor.pessoa.nome}"/></td>															
															<td><c:out value="${mComissao.servidor.unidade.nome}" /></td>																												
															<td>
																	<input type="hidden" class="membro" value="${mComissao.id}"/>
																	<h:commandButton action="#{distribuicaoProjeto.adicionarAvaliacao}" image="/img/adicionar.gif" onclick="setarMembroComissaoPossivel(event);"/>
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
					<td colspan="2" align="center">	
						<h:commandButton action="#{distribuicaoProjeto.distribuir}" value="Confirmar Distribuição"/>
						<h:commandButton  action="#{distribuicaoProjeto.cancelar}" value="Cancelar" onclick="return(confirm('Deseja cancelar a operação? Todos os dados digitatos não salvos serão perdidos!'))"/>						
						<h:commandButton  action="#{distribuicaoProjeto.distribuirOutroProjeto}" value="Distribuir Outro Projeto"/>						
					</td>		
				</tr>
			</tfoot>
			                                                                                                                                                                                                                                                                                                                                                       
	</h:form>

   </table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>