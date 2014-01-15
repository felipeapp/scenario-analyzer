<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> &gt; Gerenciar participantes de ações de extensãoa </h2>
	<h:outputText value="#{participanteAcaoExtensao.create}" />
	<h:outputText value="#{atividadeExtensao.create}" />		
		
	
	<div class="descricaoOperacao">
		<font color="red">Atenção</font><br />
		Os participantes NÃO devem ser confundidos com os membros que fazem parte da equipe executora da ação, 
		estes devem ser cadastrados como membros da equipe!<br />		
		Utilize esta tela para registrar os dados de todos os participantes, ou seja, o público alvo atingido com a execução da ação.<br />
		Por exemplo: lista de alunos que participarão do curso, todas as pessoas beneficiadas com o projeto, etc.		
	</div>	
		
	<div class="infoAltRem">	
		<h:graphicImage value="/img/monitoria/user1_add.png" style="overflow: visible;" styleClass="noborder" />: Cadastrar Participante    
	    <h:graphicImage value="/img/seta.gif" style="overflow: visible;" styleClass="noborder" />
	    	: Gerenciar Participantes/Emitir Certificados e Declarações
	     <br/>
	    <h:graphicImage value="/img/view.gif" style="overflow: visible;" styleClass="noborder" />: Listar Participantes
	    <h:graphicImage value="/img/extensao/document_edit.png" style="overflow: visible;" styleClass="noborder" />: Exibir Lista de Presença
	    <h:graphicImage value="/img/email_go.png" style="overflow: visible;" styleClass="noborder" />: Enviar Mensagem para Participantes
	    <h:graphicImage value="/img/listar.gif" style="overflow: visible;" styleClass="noborder" />: Listar Informações de contato dos Participantes 
	    <h:graphicImage value="/img/porta_arquivos/icones/xls.png" style="overflow: visible;" />: Exportar Informações de contato dos Participantes
	</div>
		
	<c:set var="atividades" value="#{participanteAcaoExtensao.atividadesCoordenador}" />
	<c:set var="subAtividades" value="#{participanteAcaoExtensao.subAtividadesCoordenador}" />
	<c:set var="atividadesGenrenciador" value="#{participanteAcaoExtensao.atividadesGenrenciador}" />
		
	<h:form>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	<table class="listagem">
		<caption class="listagem"> Lista de Ações ativas Coordenadas pelo usuário Atual (${fn:length(participanteAcaoExtensao.atividadesCoordenador)})</caption>
		<thead>
			<tr>
				<th width="12%">Código</th>
				<th width="50%">Título</th>
				<th>Situação da Ação</th>
				<th></th>
				<th></th>
				<th></th>
				<th></th>
				<th></th>
				<th></th>							
				<th></th>
			</tr>
		</thead>
		
		<tbody>
			<c:choose>
				<c:when test="${empty atividades}">
					<tr>
						<td colspan="4" align="center"><font color="red">Usuário atual não coordena ações de extensão ativas.</font></td>
					</tr>
				</c:when>
			
				<c:otherwise>
					<c:forEach items="#{atividades}" var="item" varStatus="status">
           				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td>${item.codigo}</td>
							<td>${item.titulo}</td>						
							<td>${item.projeto.situacaoProjeto.descricao}</td>
							
							<td width="2%">
									<h:commandLink action="#{participanteAcaoExtensao.iniciarCadastroParticipante}" style="border: 0;" 
											rendered="#{item.valida}" title="Cadastrar Participante">
					        			<f:param name="id" value="#{item.id}" />
				    			    	<h:graphicImage url="/img/monitoria/user1_add.png" />
									</h:commandLink>
							</td>

							<td width="2%">
									<h:commandLink action="#{participanteAcaoExtensao.iniciaGerenciarParticipantes}" style="border: 0;" 
											rendered="#{item.valida}" title="Gerenciar Participantes/Emitir Certificados e Declarações">
					        			<f:param name="id" value="#{item.id}" />
				    			    	<h:graphicImage url="/img/seta.gif" />
									</h:commandLink>
							</td>
							
							<td width="2%">	
									<h:commandLink action="#{participanteAcaoExtensao.listarParticipantes}" style="border: 0;" 
											rendered="#{item.valida}" title="Listar Participantes">
				        				<f:param name="id" value="#{item.id}" />
				        				<h:graphicImage url="/img/view.gif" />
									</h:commandLink>
							</td>
							
							
							<td width="2%">	
									<h:commandLink action="#{participanteAcaoExtensao.listarParticipantes}" style="border: 0;" 
											rendered="#{item.valida}" title="Exibir Lista de Presença">
				        				<f:param name="id" value="#{item.id}" />
				        				<f:param name="presenca" value="#{true}" />
				        				<h:graphicImage url="/img/extensao/document_edit.png" />
									</h:commandLink>
							</td>
							
							<td width="2%">
									<h:commandLink action="#{participanteAcaoExtensao.iniciarNotificacaoParticipantes}" style="border: 0;" 
											rendered="#{item.valida}" title="Enviar Mensagem para Participantes">
					        			<f:param name="id" value="#{item.id}" />
				    			    	<h:graphicImage url="/img/email_go.png" />
									</h:commandLink>
							</td>
							<td width="2%">
								<h:commandLink action="#{participanteAcaoExtensao.listarParticipantes}" style="border: 0;"
									rendered="#{item.valida}" title="Listar Informações de contato dos Participantes">
									<f:param name="id" value="#{item.id}" />
									<f:param name="contato" value="#{true}" />
									<h:graphicImage url="/img/listar.gif" />
								</h:commandLink>
							</td>
							<td width="2%">
								<h:commandLink action="#{participanteAcaoExtensao.exportarPlanilhaContatos}" style="border 0;" title="Exportar informações de contato">
									<f:param name="id" value="#{item.id}" />
									<h:graphicImage value="/img/porta_arquivos/icones/xls.png" />
								</h:commandLink>
							</td>
						</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</tbody>
	</table>
	
	<c:if test="${not empty subAtividades }">
	
	<br/><br/><br/><br/>
	
	<table class="listagem">
		<caption class="listagem"> Lista de Mini Ações ativas Coordenadas pelo usuário Atual (${fn:length(subAtividades)})</caption>
		<thead>
			<tr>
				<th width="16%">Código(Atividade Pai)</th>
				<th width="50%">Título Mini Ação</th>
				<th>Situação da Ação</th>
				<th></th>				
			</tr>
		</thead>
		
		<tbody>
			<c:choose>
				<c:when test="${empty subAtividades}">
					<tr>
						<td colspan="4" align="center"><font color="red">Usuário atual não coordena mini Ações de extensão ativas.</font></td>
					</tr>
				</c:when>
			
				<c:otherwise>
					<c:forEach items="#{subAtividades}" var="item" varStatus="status">
           				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td>${item.codigo}</td>
							<td>${item.subAtividade.titulo}</td>						
							<td>${item.projeto.situacaoProjeto.descricao}</td>

							<td width="2%">
									<h:commandLink action="#{participanteAcaoExtensao.iniciaGerenciarParticipantesSubAtividades}" style="border: 0;" 
											rendered="#{item.valida}" title="Gerenciar Participantes/Emitir Certificados e Declarações">
					        			<f:param name="id" value="#{item.subAtividade.id}" />
				    			    	<h:graphicImage url="/img/seta.gif" />
									</h:commandLink>
							</td>
							
						</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</tbody>
	</table>
	</c:if>
	
	<c:if test="${not empty atividadesGenrenciador }">
	
	<br/><br/><br/><br/>
	
	<table class="listagem">
		<caption class="listagem"> Lista de Ações ativas que participo como gerenciador de participantes (${fn:length(atividadesGenrenciador)})</caption>
		<thead>
			<tr>
				<th width="16%">Código</th>
				<th width="50%">Título</th>
				<th>Situação da Ação</th>
				<th></th>				
			</tr>
		</thead>
		
		<tbody>
			<c:choose>
				<c:when test="${empty atividadesGenrenciador}">
					<tr>
						<td colspan="4" align="center"><font color="red">Usuário atual não coordena mini Ações de extensão ativas.</font></td>
					</tr>
				</c:when>
			
				<c:otherwise>
					<c:forEach items="#{atividadesGenrenciador}" var="item" varStatus="status">
           				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td>${item.codigo}</td>
							<td>${item.titulo}</td>						
							<td>${item.projeto.situacaoProjeto.descricao}</td>

							<td width="2%">
									<h:commandLink action="#{participanteAcaoExtensao.iniciaGerenciarParticipantes}" style="border: 0;" 
											rendered="#{item.valida}" title="Gerenciar Participantes/Emitir Certificados e Declarações">
					        			<f:param name="id" value="#{item.id}" />
				    			    	<h:graphicImage url="/img/seta.gif" />
									</h:commandLink>
							</td>
							
						</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</tbody>
	</table>
	
	</c:if>
	
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>