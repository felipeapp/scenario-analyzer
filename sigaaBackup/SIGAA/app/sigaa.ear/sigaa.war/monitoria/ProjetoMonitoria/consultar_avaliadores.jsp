<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao"%>

<f:view>


	<h:outputText value="#{projetoMonitoria.create}"/>
	<h:outputText value="#{avalProjetoMonitoria.create}"/>
	
	<h:messages/>
	<h2><ufrn:subSistema /> > Consultar Avaliadores do Projeto</h2>
	<br>

 	<h:form id="formBusca">

	<table class="formulario" width="100%">
	<caption>Busca por Projetos</caption>
	<tbody>
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{projetoMonitoria.checkBuscaTitulo}" id="selectBuscaTitulo"/>
			</td>
	    	<td> <label for="nomeProjeto"> Titulo do Projeto </label> </td>
	    	<td> <h:inputText value="#{projetoMonitoria.buscaNomeProjeto}" size="70" onchange="javascript:$('formBusca:selectBuscaTitulo').checked = true;"/></td>
	    </tr>

	     <tr>
			<td>
			<h:selectBooleanCheckbox value="#{projetoMonitoria.checkBuscaEdital}" id="selectBuscaEdital"/>
			</td>
	    	<td> <label for="nomeProjeto"> Edital do Projeto: </label> </td>
	    	<td>
	    	<h:selectOneMenu onchange="javascript:$('formBusca:selectBuscaEdital').checked = true;" value="#{projetoMonitoria.edital.id}">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM EDITAL --"  />
								<f:selectItems value="#{editalMonitoria.allCombo}"/>
							</h:selectOneMenu>
	    </tr>

		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{projetoMonitoria.checkBuscaSituacao}"  id="selectBuscaSituacao" />
			</td>
	    	<td> <label for="situacaoProjeto"> Situação do Projeto </label> </td>
	    	<td>

	    	 <h:selectOneMenu value="#{projetoMonitoria.buscaSituacaoProjeto.id}" style="width: 300px"  onchange="javascript:$('formBusca:selectBuscaSituacao').checked = true;">
				<f:selectItems value="#{projetoMonitoria.tipoSituacaoProjetoCombo}"/>
 			 </h:selectOneMenu>

	    	 </td>
	    </tr>

	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
			<h:commandButton value="Buscar" action="#{ projetoMonitoria.iniciarLocalizacao }"/>
			<h:commandButton value="Cancelar" action="#{ projetoMonitoria.cancelar }"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>
	</h:form>

<br/>

	<center>
	<h:form>
		<table class="formulario" width="100%">
			<caption class="listagem">Projetos de Monitoria Encontrados</caption>
	
			<tr>			
				<td>
				
				 	<h:selectOneListbox value="#{projetoMonitoria.obj.id}"
						valueChangeListener="#{projetoMonitoria.changeProjetoMonitoria}" onchange="submit()" 
						size="10" style="width: 100%">	
						<f:selectItems value="#{projetoMonitoria.projetosLocalizadosCombo}"/>
					</h:selectOneListbox>
				
				</td>
			</tr>
			
		</table>
	</h:form>
	</center>
	<br>
	
	
	<div class="infoAltRem">
   	    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Avaliação
	</div>
	
	<h:form>
	<table class="subFormulario" width="100%" id="avalidoresDoProjeto">
			<caption>Lista de Avaliadores do Projeto</caption>
				<thead id="headLista">
		  		   	<tr>
		  		   		<td>Avaliador(a)</td>
		  		   		<td>Depto.</td>				  		   		
		  		   		<td>Nota</td>				  		   				  		   		
		  		   		<td>Tipo Avaliação</td>
		  		   		<td>Situação</td>
		  		   		<td></td>		  		   		
				  	</tr>
				  </thead>

					<c:set var="avaliacoes" value="#{projetoMonitoria.obj.avaliacoes}" />
					
					<c:if test="${not empty avaliacoes}">
					
						<c:set var="AVALIACAO_CANCELADA" value="<%= String.valueOf(StatusAvaliacao.AVALIACAO_CANCELADA) %>" scope="application"/>
						<c:set var="AVALIADO" value="<%= String.valueOf(StatusAvaliacao.AVALIADO) %>" scope="application"/>												
						
						<c:forEach items="#{avaliacoes}" var="avaliacao" varStatus="status">
						
											<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" id="linhaLista">
											
												<td><c:out value="${avaliacao.avaliador.servidor.nome}"/></td>
												<td><c:out value="${avaliacao.avaliador.servidor.unidade.sigla}" /></td>															
												<td><c:out value="${avaliacao.notaAvaliacao}" /></td>																											
												<td><c:out value="${avaliacao.tipoAvaliacao.descricao}" /> <c:out value="${avaliacao.avaliacaoPrograd ? ' (AVALIACAO FINAL)' : ''}" /></td>
												<td>
													<c:out value="${(avaliacao.statusAvaliacao.id == AVALIACAO_CANCELADA) ?'<font color=red>':''}" escapeXml="false" />	
													<c:out value="${(avaliacao.statusAvaliacao.id == AVALIADO) ?'<font color=blue>':''}" escapeXml="false" />	
													<c:out value="${((avaliacao.statusAvaliacao.id != AVALIADO) and (avaliacao.statusAvaliacao.id != AVALIACAO_CANCELADA)) ?'<font color=black>':''}" escapeXml="false" />	
														<c:out value="${avaliacao.statusAvaliacao.descricao}" />
													</font>
												</td>
												<td>															
												   <c:if test="${not empty avaliacao.dataAvaliacao}">																
															<h:commandLink  title="Ver Avaliacao" action="#{avalProjetoMonitoria.view}" style="border: 0;">
															   	<f:param name="idAvaliacao" value="#{avaliacao.id}"/>				    	
																<h:graphicImage url="/img/view.gif"/>
															</h:commandLink>
													</c:if>				
												</td>


											</tr>
								
						</c:forEach>
					</c:if>
					<c:if test="${(not empty projetoMonitoria.projetosLocalizadosCombo) and (empty projetoMonitoria.obj.avaliacoes)}">
							<tr>
								<td colspan="4" align="center"><font color="red">Atualmente, não há avaliadores para este projeto.</font></td>
							</tr>									
					</c:if>
								
					<c:if test="${empty projetoMonitoria.projetosLocalizadosCombo}">
						<tr>
							<td colspan="4" align="center"><font color="red">Nenhum projeto selecionado!</font></td>
						</tr>									
					</c:if>
		</table>
		
</h:form>
	</tbody>
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>