<%@include file="/ava/cabecalho.jsp"%>

<f:view>
	<%@include file="/ava/menu.jsp"%>
	
	<h:form id="form">
		<c:set var="foruns" value="#{ forumBean.foruns }" />
	
		<fieldset>		
			<legend>Fóruns Gerais</legend>
			 
			<c:if test="${ empty foruns }">
				<p class="empty-listing">Nenhum item foi encontrado.</p>
			</c:if>
			 
			<c:if test="${ not empty foruns }">
				<table class="listing">
					<thead>
						<tr>
							<th>Título</th>
							<th>Tipo</th>
							<th>Tópicos</th>
							<th>Autor(a)</th>
							<th>Criado em</th>
							<c:if test="${ permissaoAva.permissaoUsuario.forum }">
								<th></th>
							</c:if>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="n" items="#{ foruns }" varStatus="loop">
							<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">	
								<td class="first">
									<h:commandLink action="#{ forumBean.view }" id="btnViewForum">
										<h:outputText value="#{ n.titulo }" />
										<f:setPropertyActionListener target="#{ forumBean.obj.id }" value="#{ n.id }" />
									</h:commandLink>
									<br/>
									${ sf:nomeResumido(n.descricao) }
								</td>

								<td class="width150">
										<h:outputText value="#{ n.tipo.descricao }" />
								</td>
	
								<td class="width150">
									<h:commandLink action="#{ forumBean.view }" id="btnViewTopicos">
										<h:outputText value="#{ n.totalTopicos }" />
										<f:setPropertyActionListener target="#{ forumBean.obj.id }" value="#{ n.id }" />
									</h:commandLink>
								</td>
	
								<td class="width150">
									${ n.usuario.pessoa.nome }
								</td>
								
								<td class="width90">
									<fmt:formatDate pattern="dd/MM/yyyy" value="${ n.dataCadastro }" />
								</td>
	
								<td class="icon">
									<h:commandLink action="#{ forumBean.preRemover }" id="btnPreRemoverForum"
										rendered="#{ forumBean.usuarioLogado.id == n.usuario.id }" title="Remover Fórum">										
										<f:setPropertyActionListener value="#{ n.id }" target="#{ forumBean.obj.id }"/>
										<h:graphicImage value="/ava/img/bin.png" />
									</h:commandLink>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>
			
			<div class="botoes">
				<div class="form-actions">
					<%-- h:commandButton action="#{ forumBean.preCadastrar }" value="Cadastrar Fórum" id="btnNovo"/ --%>
					<%-- h:commandButton action="#{ forumParticipanteBean.iniciarBusca }" value="Buscar" id="btnBuscar"/ --%> 
				</div>
				<div class="other-actions">
					<h:commandButton action="#{ forumBean.cancelar }" value="Voltar" id="btnCancelar"/> 
				</div>
			</div>
			
		</fieldset>
	</h:form>
</f:view>
<%@include file="/ava/rodape.jsp"%>
