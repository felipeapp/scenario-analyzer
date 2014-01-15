<%@include file="/ava/cabecalho.jsp"%>

<f:view>
	<%@include file="/ava/menu.jsp"%>
	
	<h:form id="form">
	<fieldset>		
			<legend>Buscar Fóruns</legend>
			
			<table class="formulario" width="80%">
				<caption> Busca </caption>
				<tbody>
					<tr>
				    	<td> 
				    		<label for="titulo"> Título do Fórum: </label> 
				    	</td>
				    	<td> 
				    		<h:inputText value="#{ forumParticipanteBean.titulo }" size="90" id="buscaTitulo" />
				    	</td>
				    </tr>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="3">
							<h:commandButton value="Buscar" action="#{ forumParticipanteBean.buscar }" />
							<h:commandButton value="Cancelar" action="#{ forumParticipanteBean.cancelar }" />
				    	</td>
				    </tr>
				</tfoot>
			</table>
			
			<c:set var="foruns" value="#{ forumParticipanteBean.foruns }" />
			
			<c:if test="${ empty foruns }">
				<p class="empty-listing">Nenhum fórum foi encontrado.</p>
			</c:if>
			 <br />
			<c:if test="${ not empty foruns }">
			
				<div class="infoAltRem">
	    			<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar
	   				<h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Participar
				</div>
			
				<table class="listagem">
					<caption class="listagem">Lista de Fóruns </caption>
					<thead>
						<tr>
							<th>Título</th>
							<th>Autor</th>
							<th>Tópicos</th>
							<th>Última Atualização</th>
							<th></th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="n" items="#{ foruns }" varStatus="loop">
							<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">	
								<td class="first">
									<h:commandLink action="#{ forumBean.view }" id="btnViewForum">
										<h:outputText value="#{ n.titulo }" />
										<f:param name="id" value="#{ n.id }" />
									</h:commandLink>
									<br />
									${ sf:nomeResumido(n.descricao) }
								</td>
	
								<td class="width150">
									${ n.usuario.pessoa.nome }
								</td>
								
								<td class="width150">
									${ fn:length(n.topicos) }
								</td>

								<td class="width90">
									<fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${ n.dataCadastro }" />
								</td>
	
								<td class="icon">
									<h:commandLink action="#{ forumBean.view }" id="btnView" title="Visualizar">										
										<f:param name="id" value="#{ n.id }" />
										<h:graphicImage value="/img/view.gif" />
									</h:commandLink>
								</td>
								
								<td class="icon">
									<h:commandLink action="#{ forumParticipanteBean.participar }" id="btnParticiparForum" title="Participar">										
										<f:param name="id" value="#{ n.id }" />
										<h:graphicImage value="/img/seta.gif" />
									</h:commandLink>
								</td>
								
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>
			
	</fieldset>
	</h:form>
</f:view>
<%@include file="/ava/rodape.jsp"%>
	