<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>
<h2><ufrn:subSistema /> > Lista de Planos de Trabalho </h2>

	
	<c:set var="planos" value="#{planoTrabalhoProjeto.planosCoordenadorLogado}"	/>
	
<h:form>

	<div class="infoAltRem">
			<html:img page="/img/email_go.png" style="overflow: visible;"/>: Enviar Mensagem
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar
		    <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar
			<h:graphicImage value="/img/extensao/user1_delete.png" style="overflow: visible;"/>: Finalizar Discente		    
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
	</div>

		<table class=listagem>
				<caption class="listagem"> Lista de Planos de Trabalho de Ações Coordenadas pelo Usuário Atual</caption>
				<thead>
						<tr>
							<th>Discente</th>
							<th>Vínculo</th>
							<th>Situação</th>
							<th><p align="center">Início do Plano</p></th>
							<th><p align="center">Fim do Plano</p></th>							
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						
						<c:set var="projeto" value=""/>
						<c:forEach items="#{planos}" var="item"varStatus="status">
						
						
							<c:if test="${ projeto != item.projeto.id }">
								<c:set var="projeto" value="${ item.projeto.id }"/>
								<tr>
										<td colspan="10" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
											${ item.projeto.anoTitulo }
										</td>
								</tr>
							</c:if>
		
						
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td>
										<h:outputText value="#{item.discenteProjeto.discente.nome}" rendered="#{not empty item.discenteProjeto}"/> 
										<h:outputText value="<font color=red><i> DISCENTE NÃO DEFINIDO </i></font>" rendered="#{empty item.discenteProjeto.discente.nome}" escape="false" />
									</td>																		
									<td><h:outputText value="#{item.discenteProjeto.tipoVinculo.descricao}" rendered="#{not empty item.discenteProjeto}"/></td>
									<td><h:outputText value="#{item.discenteProjeto.situacaoDiscenteProjeto.descricao}" rendered="#{not empty item.discenteProjeto}"/></td>
									<td><p align="center"><fmt:formatDate value="${item.dataInicio}" pattern="dd/MM/yyyy" /></p></td>
									<td><p align="center"><fmt:formatDate value="${item.dataFim}" pattern="dd/MM/yyyy" /></p></td>
									<td  width="2%">
										<h:commandLink action="#{avisoProjeto.enviarMensagem}" style="border: 0;" title="Enviar Mensagem" rendered="#{not empty item.discenteProjeto.discente.nome}">
									       <f:param name="idDiscente" value="#{item.discenteProjeto.discente.id}"/>
							               <h:graphicImage url="/img/email_go.png" />
										</h:commandLink>
									</td>
									<td  width="2%">
										<h:commandLink action="#{planoTrabalhoProjeto.view}" style="border: 0;" title="Visualizar">
									       <f:param name="idPlano" value="#{item.id}"/>
							               <h:graphicImage url="/img/view.gif" />
										</h:commandLink>
									</td>
									
									<td  width="2%">
										<h:commandLink action="#{planoTrabalhoProjeto.atualizar}" style="border: 0;" title="Alterar" rendered="#{(not empty item.projeto) }">
									       <f:param name="id" value="#{item.id}"/>
							               <h:graphicImage url="/img/alterar.gif" />
										</h:commandLink>
									</td>
									
									<td width="2%">
										<h:commandLink action="#{planoTrabalhoProjeto.iniciarFinalizarDiscente}" style="border: 0;" id="finalizar" rendered="#{not empty item.discenteProjeto.discente.nome}">
										      <f:param name="id" value="#{item.id}"/>
								              <h:graphicImage url="/img/extensao/user1_delete.png" title="Finalizar Discente"/>
										</h:commandLink>
									</td>
									
									<td  width="2%">
										<h:commandLink action="#{planoTrabalhoProjeto.preRemoverPlanoTrabalho}" style="border: 0;" title="Remover">
									       <f:param name="id" value="#{item.id}"/>
							               <h:graphicImage url="/img/delete.gif" />
										</h:commandLink>
									</td>									
							</tr>
			 		   </c:forEach>
			 		   
			 		   <c:if test="${empty planos}" >
			 		   		<tr><td colspan="9" align="center"><font color="red">Não há planos de trabalhos cadastrados para o usuário atual!</font></td></tr>
			 		   </c:if>
			 		   
					</tbody>	
					
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>