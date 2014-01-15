<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>
<h2><ufrn:subSistema /> > Planos de Trabalho da Ação de Extensão Selecionada</h2>

<h:outputText value="#{planoTrabalhoExtensao.create}"/>
	
	<div class="descricaoOperacao">
		<b>Tipos de Vínculo</b><br/>
		<ul>
			<li>Bolsista FAEx:</b> bolsista mantido com recursos concedidos pelo FAEx.</li>
			<li>Bolsista Externo:</b> bolsista mantido com recursos de outros orgãos. CNPq, Petrobrás, Ministério da Saúde, etc.</li>
			<li>Voluntário:</b> são membros da equipe da ação de extensão que não são remunerados.</li>
			<li>Atividade Curricular:</b> são discentes não remunerados que fazem parte da equipe da ação de extensão.</li>
		<ul>				
	</div>
	
	<c:set var="planos" value="#{planoTrabalhoExtensao.planosCoordenadorLogado}"	/>
	
<h:form>

	<div class="infoAltRem">
			<html:img page="/img/email_go.png" style="overflow: visible;"/>: Enviar Mensagem
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar
		    <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Plano
			<h:graphicImage value="/img/extensao/user1_delete.png" style="overflow: visible;"/>: Finalizar Discente		    
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Plano
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
						
						<c:set var="atividade" value=""/>
						<c:forEach items="#{planos}" var="item"varStatus="status">
						
						
							<c:if test="${ atividade != item.atividade.id }">
								<c:set var="atividade" value="${ item.atividade.id }"/>
								<tr>
										<td colspan="10" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
											${ item.atividade.anoTitulo }
										</td>
								</tr>
							</c:if>
		
						
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td>
										<h:outputText value="#{item.discenteExtensao.discente.nome}" rendered="#{not empty item.discenteExtensao}"/> 
										<h:outputText value="<font color=red><i> DISCENTE NÃO DEFINIDO </i></font>" rendered="#{empty item.discenteExtensao.discente.nome}" escape="false" />
									</td>																		
									<td><h:outputText value="#{item.discenteExtensao.tipoVinculo.descricao}" rendered="#{not empty item.discenteExtensao}"/></td>
									<td><h:outputText value="#{item.discenteExtensao.situacaoDiscenteExtensao.descricao}" rendered="#{not empty item.discenteExtensao}"/></td>
									<td><p align="center"><fmt:formatDate value="${item.dataInicio}" pattern="dd/MM/yyyy" /></p></td>
									<td><p align="center"><fmt:formatDate value="${item.dataFim}" pattern="dd/MM/yyyy" /></p></td>
									<td  width="2%">
										<h:commandLink action="#{avisoProjeto.enviarMensagem}" style="border: 0;" title="Enviar Mensagem" rendered="#{not empty item.discenteExtensao.discente.nome && item.enviado}">
									       <f:param name="idDiscente" value="#{item.discenteExtensao.discente.id}"/>
							               <h:graphicImage url="/img/email_go.png" />
										</h:commandLink>
									</td>
									<td  width="2%">
										<h:commandLink action="#{planoTrabalhoExtensao.view}" style="border: 0;" title="Visualizar" immediate="true">
									       <f:param name="id" value="#{item.id}"/>
							               <h:graphicImage url="/img/view.gif" />
										</h:commandLink>
									</td>
									<td  width="2%">
										<h:commandLink action="#{planoTrabalhoExtensao.atualizar}" style="border: 0;" title="Alterar" rendered="#{(!item.enviado)}">
									       <f:param name="id" value="#{item.id}"/>
							               <h:graphicImage url="/img/alterar.gif" />
										</h:commandLink>
									</td>
									
									<td width="2%">
										<h:commandLink action="#{planoTrabalhoExtensao.iniciarFinalizarDiscente}" style="border: 0;" id="finalizar" 
											rendered="#{not empty item.discenteExtensao.discente.nome && item.enviado}" title="Finalizar Discente">
										      <f:param name="idDiscenteExtensao" value="#{item.discenteExtensao.id}"/>
								              <h:graphicImage url="/img/extensao/user1_delete.png" title="Finalizar Discente"/>
										</h:commandLink>
									</td>
									
									<td  width="2%">
										<h:commandLink action="#{planoTrabalhoExtensao.preRemoverPlanoTrabalho}" style="border: 0;" title="Remover Plano">
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