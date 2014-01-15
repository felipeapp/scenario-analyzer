<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>
<h2><ufrn:subSistema /> > Alterar Planos de Trabalho</h2>

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
	
<h:form>

	<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Plano de Trabalho		    		    
		    <h:graphicImage value="/img/email_go.png" style="overflow: visible;"/>: Enviar Email
		    <h:graphicImage value="/img/pesquisa/indicar_bolsista.gif" style="overflow: visible;"/>: Indicar/Substituir Discente
	</div>

		<table class=listagem>
				<caption class="listagem"> Lista de Planos de Trabalho de Ações Coordenadas pelo Usuário Atual</caption>
				<thead>
						<tr>
							<th>Discente</th>
							<th>Vínculo</th>
							<th>Situação</th>
							<th>Período</th>
							<th></th>							
							<th></th>							
							<th></th>
						</tr>
					</thead>
					<tbody>
						
						<c:set var="atividade" value=""/>
						<c:forEach items="#{planoTrabalhoExtensao.planosCoordenadorLogado}" var="item" varStatus="status">						
						
							<c:if test="${ atividade != item.atividade.id }">
								<c:set var="atividade" value="${ item.atividade.id }"/>
								<tr>
									<td colspan="8" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
										${ item.atividade.anoTitulo }
									</td>
								</tr>
							</c:if>		
						
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td>${(fn:length(item.discenteExtensao.discente.nome) <= 0)?'<font color=red><i> DISCENTE NÃO DEFINIDO </i></font>': item.discenteExtensao.discente.nome}</td>
									<td>${item.discenteExtensao.tipoVinculo.descricao}</td>
									<td>${item.discenteExtensao.situacaoDiscenteExtensao.descricao}</td>
									<td><fmt:formatDate value="${item.dataInicio}" pattern="dd/MM/yyyy" /> a <fmt:formatDate value="${item.dataFim}" pattern="dd/MM/yyyy" /></td>
									<td  width="2%">								               
										<h:commandLink action="#{planoTrabalhoExtensao.view}" id="lnkViewPlano" immediate="true">
										   <f:param name="id" value="#{item.id}"/>
								           <t:graphicImage value="/img/view.gif" style="border: 0;" title="Visualizar Plano de Trabalho"/>
										</h:commandLink>
									</td>

									<td width="3%">
										<c:if test="${(fn:length(item.discenteExtensao.discente.nome) > 0)}">
											<h:commandLink action="#{ planoTrabalhoExtensao.preEnviarEmailIndicacao }" >
												<h:graphicImage value="/img/email_go.png" style="overflow: visible;" title="Enviar Email"/>
												<f:param name="id" value="#{ item.discenteExtensao.id }" />
											</h:commandLink>
										</c:if>
									</td>

									<td width="2%">
										<h:commandLink action="#{planoTrabalhoExtensao.iniciarIndicarDiscente}" id="lnkIndicarDiscente" rendered="#{!item.atividade.finalizada && item.enviado}">
									       <f:param name="id" value="#{item.id}"/>
							               <t:graphicImage value="/img/pesquisa/indicar_bolsista.gif" title="Indicar/Substituir Discente" style="border: 0;"/>
										</h:commandLink>
									</td>		
							</tr>
			 		   </c:forEach>
			 		   
			 		   <c:if test="${empty planoTrabalhoExtensao.planosCoordenadorLogado}" >
			 		   		<tr><td colspan="5" align="center"><font color="red">Não há planos de trabalhos cadastrados!</font></td></tr>
			 		   </c:if>
			 		   
					</tbody>	
					
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>