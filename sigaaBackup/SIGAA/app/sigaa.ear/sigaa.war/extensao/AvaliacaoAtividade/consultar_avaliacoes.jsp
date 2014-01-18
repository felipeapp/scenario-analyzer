<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao"%>

<f:view>
	<h2><ufrn:subSistema /> > Consultar/Remover Avaliações</h2>
	<h:form id="form">

			<table class="formulario" width="90%">
			<caption>Busca por Avaliações de Extensão</caption>
			<tbody>
				<tr>
					<td> <h:selectBooleanCheckbox value="#{avaliacaoAtividade.checkBuscaTitulo}" id="selectBuscaTitulo" styleClass="noborder"/> </td>
			    	<td> <label for="selectBuscaTitulo"> Título da Ação </label> </td>
			    	<td> <h:inputText value="#{avaliacaoAtividade.buscaTituloAtividade}" size="50" onfocus="javascript:$('form:selectBuscaTitulo').checked = true;"/></td>
			    </tr>
		
				<tr>
					<td> <h:selectBooleanCheckbox value="#{avaliacaoAtividade.checkBuscaAnoAtividade}" id="selectBuscaAno" styleClass="noborder"/> </td>
			    	<td> <label for="selectBuscaAno"> Ano da Ação</label> </td>
			    	<td> <h:inputText value="#{avaliacaoAtividade.buscaAnoAtividade}" size="10" onfocus="javascript:$('form:selectBuscaAno').checked = true;"/></td>
			    </tr>
		
				<tr>
					<td> <h:selectBooleanCheckbox value="#{avaliacaoAtividade.checkBuscaTipoAtividade}" id="selectBuscaTipoAtividade" styleClass="noborder"/> </td>
			    	<td> <label for="selectBuscaTipoAtividade"> Tipo da Ação </label> </td>
			    	<td>
			    	
			    	 <h:selectOneMenu value="#{avaliacaoAtividade.buscaTipoAtividade}" onfocus="javascript:$('form:selectBuscaTipoAtividade').checked = true;">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
			    	 	<f:selectItems value="#{tipoAtividadeExtensao.allCombo}" />
			    	 </h:selectOneMenu>	    	 
			    	 </td>
			    </tr>
		
		
				<tr>
					<td> <h:selectBooleanCheckbox value="#{avaliacaoAtividade.checkBuscaAreaCNPq}" id="selectBuscaAreaCNPq" styleClass="noborder"/> </td>
			    	<td> <label for="selectBuscaAreaCNPq"> Área do CNPq </label> </td>
			    	<td>
				    	 <h:selectOneMenu value="#{avaliacaoAtividade.buscaAreaCNPq}" onfocus="javascript:$('form:selectBuscaAreaCNPq').checked = true;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{area.allGrandesAreasCombo}"/>
						</h:selectOneMenu>
			    	 </td>
			    </tr>
		
				<tr>
					<td> <h:selectBooleanCheckbox value="#{avaliacaoAtividade.checkBuscaUnidadeProponente}" id="selectBuscaUnidadeProponente" styleClass="noborder"/> </td>
			    	<td> <label for="selectBuscaUnidadeProponente"> Unidade Proponente </label> </td>
			    	<td>
							<h:selectOneMenu value="#{avaliacaoAtividade.buscaUnidade}"	style="width: 90%" onfocus="javascript:$('form:selectBuscaUnidadeProponente').checked = true;">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
								<f:selectItems value="#{unidade.unidadesProponentesProjetosCombo}"/>
							</h:selectOneMenu>
			    	 </td>
			    </tr>
		
		
				<tr>
					<td> <h:selectBooleanCheckbox value="#{avaliacaoAtividade.checkBuscaAreaTematicaPrincipal}" id="selectBuscaAreaTematicaPrincipal" styleClass="noborder"/>  </td>
			    	<td> <label for="selectBuscaAreaTematicaPrincipal"> Área Temática </label> </td>
			    	<td>
							<h:selectOneMenu value="#{avaliacaoAtividade.buscaAreaTematicaPrincipal}" onfocus="javascript:$('form:selectBuscaAreaTematicaPrincipal').checked = true;">
								<f:selectItem itemValue="0" itemLabel=" -- SELECIONE --"/>
							<f:selectItems value="#{areaTematica.allCombo}"/>
						</h:selectOneMenu>
			    	</td>
			    </tr>
		
		
				<tr>
					<td> <h:selectBooleanCheckbox value="#{avaliacaoAtividade.checkBuscaServidorAtividade}" id="selectBuscaServidor" styleClass="noborder"/>  </td>
					<td>Servidor (Docente na Ação):</td>
					<td>
					<h:inputHidden id="servidor" value="#{avaliacaoAtividade.servidorAtividade.id}"></h:inputHidden>
					<h:inputText id="nome"
						value="#{avaliacaoAtividade.servidorAtividade.pessoa.nome}" size="50" onfocus="javascript:$('form:selectBuscaServidor').checked = true;"/> 
						<ajax:autocomplete
							source="form:nome" target="form:servidor"
							baseUrl="/sigaa/ajaxServidor" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
							parser="new ResponseXmlToHtmlListParser()" /> 
						<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
					</td>
				</tr>



				<tr>
					<td> <h:selectBooleanCheckbox value="#{avaliacaoAtividade.checkBuscaTipoAvaliacao}" id="selectBuscaTipoAvaliacao" styleClass="noborder"/>  </td>
			    	<td> <label for="selectBuscaTipoAvaliacao"> Tipo de Avaliação </label> </td>
			    	<td>
							<h:selectOneMenu value="#{avaliacaoAtividade.buscaTipoAvaliacao}" onfocus="javascript:$('form:selectBuscaTipoAvaliacao').checked = true;">
								<f:selectItem itemValue="0" itemLabel=" -- SELECIONE --"/>
							<f:selectItems value="#{avaliacaoAtividade.allTipoAvaliacaoCombo}"/>
						</h:selectOneMenu>
			    	</td>
			    </tr>


				<tr>
					<td> <h:selectBooleanCheckbox value="#{avaliacaoAtividade.checkBuscaStatusAvaliacao}" id="selectBuscaStatusAvaliacao" styleClass="noborder"/>  </td>
			    	<td> <label for="selectBuscaStatusAvaliacao"> Situação da Avaliação </label> </td>
			    	<td>
							<h:selectOneMenu value="#{avaliacaoAtividade.buscaStatusAvaliacao}" onfocus="javascript:$('form:selectBuscaStatusAvaliacao').checked = true;">
								<f:selectItem itemValue="0" itemLabel=" -- SELECIONE --"/>
							<f:selectItems value="#{avaliacaoAtividade.allStatusAvaliacaoCombo}"/>
						</h:selectOneMenu>
			    	</td>
			    </tr>

				
				
				<tr>
					<td> <h:selectBooleanCheckbox value="#{avaliacaoAtividade.checkBuscaServidorAvaliador}" id="selectBuscaServidorAvaliador" styleClass="noborder"/>  </td>
					<td>Servidor (Avaliador):</td>
					<td>
					<h:inputHidden id="idAvaliador" value="#{avaliacaoAtividade.servidorAvaliador.id}"></h:inputHidden>
					<h:inputText id="nomeAvaliador"
						value="#{avaliacaoAtividade.servidorAvaliador.pessoa.nome}" size="50" onfocus="javascript:$('form:selectBuscaServidorAvaliador').checked = true;"/> 
						<ajax:autocomplete
							source="form:nomeAvaliador" target="form:idAvaliador"
							baseUrl="/sigaa/ajaxServidor" className="autocomplete"
							indicator="indicator2" minimumCharacters="3" parameters="tipo=todos"
							parser="new ResponseXmlToHtmlListParser()" /> 
						<span id="indicator2" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
					</td>
				</tr>
				
				<tr>
					<td> <h:selectBooleanCheckbox value="#{avaliacaoAtividade.checkBuscaEdital}" id="selectBuscaEdital" styleClass="noborder"/> </td>
	    			<td> <label for="edital"> Edital </label> </td>
	    			<td> 
	    				<h:selectOneMenu id="buscaEdital" value="#{avaliacaoAtividade.buscaEdital}" onfocus="javascript:$('form:selectBuscaEdital').checked = true;">
	    					<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
	    	 				<f:selectItems value="#{editalExtensao.allCombo}" />
	    	 		 	</h:selectOneMenu>	    	 
		    	 	</td>
	    		</tr>
				
				
		
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
					<h:commandButton value="Buscar" action="#{ avaliacaoAtividade.localizarAvaliacoes }"/>
					<h:commandButton value="Cancelar" action="#{ avaliacaoAtividade.cancelar }"/>
			    	</td>
			    </tr>
			</tfoot>
		</table>
	
	<br/>

	<c:set var="avaliacoes" value="#{avaliacaoAtividade.avaliacoesLocalizadas}" />
	
	<h:panelGroup rendered="#{not empty avaliacaoAtividade.avaliacoesLocalizadas}" id="legenda">
	
		<div class="infoAltRem">
	   	    <h:graphicImage value="/img/view.gif" style="overflow: visible;" id="legendaView"/>: Visualizar Ação
   	    	<c:if test="${acesso.extensao}"><h:graphicImage value="/img/delete.gif" style="overflow: visible;" id="legendaDelAvaliacao"/>: Remover Avaliação </c:if>
   	    	<h:graphicImage value="/img/extensao/document_chart.png" style="overflow: visible;" id="legendaViewAvaliacao"/>: Visualizar Avaliação
		</div>
	
	 
		<table class="formulario" width="100%">
			<caption class="listagem">Avaliações localizadas (${ fn:length(avaliacoes) })</caption>
		
	
				<thead id="headLista">
		  		   	<tr>
		  		   		<th>Avaliador</th>				  		   		
		  		   		<th>Tipo de Avaliação</th>
		  		   		<th>Situação</th>
		  		   		<th></th>
		  		   		<th></th>
				  	</tr>
				 </thead>
				 <tbody>
								
							<c:set var="AVALIACAO_CANCELADA" value="<%= String.valueOf(StatusAvaliacao.AVALIACAO_CANCELADA) %>" scope="application"/>
							<c:set var="AVALIADO" value="<%= String.valueOf(StatusAvaliacao.AVALIADO) %>" scope="application"/>						
															
															
							<c:set var="idAtual" value="0"/>	 		 	
							<c:forEach items="#{ avaliacoes }" var="avaliacao" varStatus="status">
	
									<c:if test="${ idAtual != avaliacao.atividade.projeto.id }">
										<c:set var="idAtual" value="${ avaliacao.atividade.projeto.id }"/>
										<tr>
												<td colspan="4" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
													<c:out value="${avaliacao.atividade.codigoTitulo}"/>
												</td>
												<td width="2%" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">					
														<h:commandLink id="cmdlnkvisualizar" title="Visualizar Ação" action="#{ atividadeExtensao.view }">
														         <f:param name="id" value="#{avaliacao.atividade.id}"/>
									                   			<h:graphicImage url="/img/view.gif" id="imgViewAcao"/>
														</h:commandLink>
												</td>
										</tr>									
									</c:if>
									
									<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" id="linhaLista">
										<td width="50%">
											<c:if test="${avaliacao.avaliador.nome == ''}"><font color='red'>SEM AVALIADOR DEFINIDO</font></c:if>
										    ${avaliacao.avaliador.nome}
										</td>
										<td>${avaliacao.tipoAvaliacao.descricao}</td>															
										<td>
											<c:set value="${(avaliacao.statusAvaliacao.id == AVALIACAO_CANCELADA) ? 'red':((avaliacao.statusAvaliacao.id == AVALIADO) ?'blue': ((avaliacao.statusAvaliacao.id != AVALIADO) and (avaliacao.statusAvaliacao.id != AVALIACAO_CANCELADA)) ? 'black': '')}" var="cor" />	
											<font color="${cor}">	
												<c:out value="${avaliacao.statusAvaliacao.descricao}"/>
											</font>
										</td>
										<td width="2%">															
									<c:if test="${acesso.extensao}"> <h:commandLink title="Remover Avaliação" action="#{avaliacaoAtividade.inativar}" style="border: 0;" 
												onclick="return confirm('Tem certeza que deseja Remover esta Avaliação?');" id="cmdLnkDelAvaliacao">
											    	<f:param name="id" value="#{avaliacao.id}"/>
											      	<h:graphicImage url="/img/delete.gif"/>
												</h:commandLink></c:if>
										</td>																								
										
										<td width="2%">															
												<h:commandLink title="Visualizar Avaliação" action="#{avaliacaoAtividade.view}" style="border: 0;" id="cmdLnkViewAvaliacao">
											    	<f:param name="idAvaliacao" value="#{avaliacao.id}"/>
											      	<h:graphicImage url="/img/extensao/document_chart.png" id="imgViewAvaliacao" />
												</h:commandLink>
										</td>																								
									</tr>
							</c:forEach>
				</tbody>
			</table>
		</h:panelGroup>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>