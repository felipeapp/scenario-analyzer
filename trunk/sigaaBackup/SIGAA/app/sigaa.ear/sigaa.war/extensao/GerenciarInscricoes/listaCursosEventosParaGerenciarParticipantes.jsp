<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<a4j:keepAlive beanName="listaAtividadesParticipantesExtensaoMBean" />
	
	<h2><ufrn:subSistema /> &gt; Gerenciar Participantes </h2>
	
	<h:form id="formListaCursosEventoParaGerenciarAparticipantes">
	
		<div class="descricaoOperacao">
				<p> Caro Coordenador, </p>
				<p> Abaixo s�o apresentadas as atividades de extens�o ativas para as quais o(a) senhor(a) � coordenador. </p>
				<p> A partir das a��es abaixo � poss�vel cadastrar a frequ�ncia, emitir certificados entre outras opera��es para os participantes.</p>
				<br/>
				<p><i>Como para se cadastrar em alguma atividade o participante precisa primeiro se cadastrar na atividade principal, o n�mero de 
				participantes na atividade principal inclui obrigatoriamente o n�mero de participantes das mini atividades.</i></p>
				<br/>
				<p>
				<strong style="color: red;">Aten��o</strong><br/> </p>
				<p> Os participantes <strong>N�O</strong> devem ser confundidos com os membros que fazem parte da equipe executora da a��o, estes devem ser cadastrados como membros da equipe! </p>
				<br/>
				<p> <strong>Importante:</strong> Uma atividade de extens�o s� possui participantes quando as inscri��es realizadas s�o aprovadas pelo coordenador ou quando o coordenador cadastra diretamente o participante.</p>
				<p> Para verificar se n�o existe alguma inscri��o pendente de aprova��o utilizando a op��o: <i> <h:commandLink value="Gerenciar Inscri��es" action="#{gerenciarInscricoesCursosEventosExtensaoMBean.listarCursosEventosParaGerenciarInscricao}" /> </i></p>
				
		</div>
		
		
		
		
		<div class="infoAltRem">
			<h:graphicImage value="/img/user.png" height="16" width="16" style="overflow: visible;" />: Gerenciar Participantes
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;" styleClass="noborder" />: Listar Participantes
	   		<h:graphicImage value="/img/extensao/document_edit.png" style="overflow: visible;" styleClass="noborder" />: Exibir Lista de Presen�a
	    	<h:graphicImage value="/img/email_go.png" style="overflow: visible;" styleClass="noborder" />: Enviar Mensagem para Participantes
	   		<br/>
	   		<h:graphicImage value="/img/listar.gif" style="overflow: visible;" styleClass="noborder" />: Listar Informa��es de contato dos Participantes
	    	<h:graphicImage value="/img/porta_arquivos/icones/xls.png" style="overflow: visible;" />: Exportar Informa��es de contato dos Participantes
		</div>
		
		
		<table id="tabelaAtividades"  class="listagem" style="width: 100%;">

				<caption>Lista dos Cursos e Eventos</caption>
				<thead>
					<tr>
						<th width="10%">C�digo </th>
						<th width="40%">T�tulo </th>
						<th width="15%">Tipo </th>
						<th width="10%" style="text-align: center; width: 10%;">Status</th>
						<th style="text-align: right; width: 9%;">Qtd</th>
						<th style="width: 1%;" ></th>
						<th style="width: 1%;" ></th>
						<th style="width: 1%;" ></th>
						<th style="width: 1%;" ></th>
						<th style="width: 1%;" ></th>
						<th style="width: 1%;" ></th>
					</tr>
				</thead>
				<tbody>
					<c:if test="${listaAtividadesParticipantesExtensaoMBean.qtdCursosEventosParaGerenciarParticipantes > 0 }">
						<c:forEach items="#{listaAtividadesParticipantesExtensaoMBean.cursosEventosParaGerenciarParticipantes}" var="cursoEvento" varStatus="count">
							<tr class="${count.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">
								<td>${cursoEvento.codigo}</td>
								<td style="font-weight: bold;">${cursoEvento.titulo}</td>
								<td> ${cursoEvento.tipoAtividadeExtensao.descricao} </td>
								<td style="text-align: center;"> ${cursoEvento.projeto.situacaoProjeto.descricao} </td>
								<td style="text-align:right;">${cursoEvento.numeroParticipantes}</td>
								
								<%-- Op��es para as atividades, tem para as mini atividades l� em baixo --%>
								
								<td style="text-align: center;">
									<h:commandLink id="cmdLinkInscritosAtividade" title="Gerenciar Participantes" action="#{gerenciarParticipantesExtensaoMBean.listarParticipantesAtividade}">
										<f:param name="idAtividadeSelecionada" value="#{cursoEvento.id}" />
										<h:graphicImage url="/img/user.png" />
									</h:commandLink>
								</td>
									
								<td>	
									<h:commandLink action="#{listaAtividadesParticipantesExtensaoMBean.listarParticipantesEmFormatoRelatorio}" style="border: 0;" 
											rendered="#{acesso.coordenadorExtensao}" title="Listar Participantes">
				        				<f:param name="idAtividadeSelecionada" value="#{cursoEvento.id}" />
				        				<h:graphicImage url="/img/view.gif" />
									</h:commandLink>
								</td>
								
								
								<td>	
									<h:commandLink action="#{listaAtividadesParticipantesExtensaoMBean.listarParticipantesEmFormatoListaPresenca}" style="border: 0;" 
											rendered="#{acesso.coordenadorExtensao}" title="Exibir Lista de Presen�a">
				        				<f:param name="idAtividadeSelecionada" value="#{cursoEvento.id}" />
				        				<f:param name="presenca" value="#{true}" />
				        				<h:graphicImage url="/img/extensao/document_edit.png" />
									</h:commandLink>
								</td>
								
								<td>
									<h:commandLink action="#{listaAtividadesParticipantesExtensaoMBean.preNotificarParticipantes}" style="border: 0;" 
											rendered="#{acesso.coordenadorExtensao}" title="Enviar Mensagem para Participantes">
					        			<f:param name="idAtividadeSelecionada" value="#{cursoEvento.id}" />
				    			    	<h:graphicImage url="/img/email_go.png" />
									</h:commandLink>
								</td>
								<td>
									<h:commandLink action="#{listaAtividadesParticipantesExtensaoMBean.listarInformacoesContatosParticipantesEmFormatoRelatorio}" style="border: 0;"
										rendered="#{acesso.coordenadorExtensao}" title="Listar Informa��es de contato dos Participantes">
										<f:param name="idAtividadeSelecionada" value="#{cursoEvento.id}" />
										<f:param name="contato" value="#{true}" />
										<h:graphicImage url="/img/listar.gif" />
									</h:commandLink>
								</td>
								<td>
									<h:commandLink action="#{listaAtividadesParticipantesExtensaoMBean.listarInformacoesContatosParticipantesEmFormatoPlanilha}" style="border 0;" title="Exportar informa��es de contato">
										<f:param name="idAtividadeSelecionada" value="#{cursoEvento.id}" />
										<h:graphicImage value="/img/porta_arquivos/icones/xls.png" />
									</h:commandLink>
								</td>
								
							</tr>
							
							<c:if test="${cursoEvento.qtdSubAtividadesExtensao > 0 }">
								<c:forEach items="#{cursoEvento.subAtividadesExtensao}" var="miniAtividade" varStatus="status">
									<tr class="${count.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">
										<td> </td>
										<td>${miniAtividade.titulo}</td>
										<td>${miniAtividade.tipoSubAtividadeExtensao.descricao}</td>
										<td> </td>
										<td style="text-align:right;">${miniAtividade.numeroParticipantes}</td>
										
										
										<%-- Op��es para as mini atividades, tudo que tem para atividade deve ter para mini atividade --%>
										<td style="text-align: center;">
											<h:commandLink 	id="cmdLinkInscritosMiniAtividade" title="Gerenciar Participantes" action="#{gerenciarParticipantesExtensaoMBean.listarParticipantesAtividade}">
												<f:param name="idSubAtividadeSelecionada" value="#{miniAtividade.id}" />
												<h:graphicImage url="/img/user.png"/>
											</h:commandLink>
										</td>
										
										<td>	
											<h:commandLink action="#{listaAtividadesParticipantesExtensaoMBean.listarParticipantesEmFormatoRelatorio}" style="border: 0;" 
													rendered="#{acesso.coordenadorExtensao}" title="Listar Participantes">
						        				<f:param name="idSubAtividadeSelecionada" value="#{miniAtividade.id}" />
						        				<h:graphicImage url="/img/view.gif" />
											</h:commandLink>
										</td>
										
										
										<td>	
											<h:commandLink action="#{listaAtividadesParticipantesExtensaoMBean.listarParticipantesEmFormatoListaPresenca}" style="border: 0;" 
													rendered="#{acesso.coordenadorExtensao}" title="Exibir Lista de Presen�a">
						        				<f:param name="idSubAtividadeSelecionada" value="#{miniAtividade.id}" />
						        				<f:param name="presenca" value="#{true}" />
						        				<h:graphicImage url="/img/extensao/document_edit.png" />
											</h:commandLink>
										</td>
										
										<td>
											<h:commandLink action="#{listaAtividadesParticipantesExtensaoMBean.preNotificarParticipantes}" style="border: 0;" 
													rendered="#{acesso.coordenadorExtensao}" title="Enviar Mensagem para Participantes">
							        			<f:param name="idSubAtividadeSelecionada" value="#{miniAtividade.id}" />
						    			    	<h:graphicImage url="/img/email_go.png" />
											</h:commandLink>
										</td>
										<td>
											<h:commandLink action="#{listaAtividadesParticipantesExtensaoMBean.listarInformacoesContatosParticipantesEmFormatoRelatorio}" style="border: 0;"
												rendered="#{acesso.coordenadorExtensao}" title="Listar Informa��es de contato dos Participantes">
												<f:param name="idSubAtividadeSelecionada" value="#{miniAtividade.id}" />
												<f:param name="contato" value="#{true}" />
												<h:graphicImage url="/img/listar.gif" />
											</h:commandLink>
										</td>
										<td>
											<h:commandLink action="#{listaAtividadesParticipantesExtensaoMBean.listarInformacoesContatosParticipantesEmFormatoPlanilha}" style="border 0;" title="Exportar informa��es de contato">
												<f:param name="idSubAtividadeSelecionada" value="#{miniAtividade.id}" />
												<h:graphicImage value="/img/porta_arquivos/icones/xls.png" />
											</h:commandLink>
										</td>
										
									</tr>
								</c:forEach>
							</c:if>
							
							
						</c:forEach>
					</c:if>
					<c:if test="${listaAtividadesParticipantesExtensaoMBean.qtdCursosEventosParaGerenciarParticipantes == 0 }">
						<tr>
							<td colspan="11" style="color: red; text-align: center;">N�o existem cursos ou eventos para os quais o senhor seja coordenador </td>
						</tr>
					</c:if>
				</tbody>
			
		</table>			
		
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>