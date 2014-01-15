<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> > Cadastro de plano de trabalho</h2>
		
		<div class="descricaoOperacao">
			<b>Bem-vindo ao cadastro de planos de trabalho.</b> <br/><br/>
			Para cada projeto integrado listado você pode cadastrar planos de trabalho. 
		</div>
		
		
	<div class="infoAltRem">
	    <h:graphicImage value="/img/view.gif"style="overflow: visible;" styleClass="noborder"/>: Visualizar Ação Associada	    
	    <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;" styleClass="noborder"/>: Cadastrar Novo Plano
	</div>
		
	<c:set var="projetos" value="#{planoTrabalhoProjeto.projetos}"	/>
	<h:form id="form">
	<table class="listagem">
					<caption class="listagem"> Lista de Ações Coordenadas pelo usuário Atual</caption>
			<thead>
				<tr>
						<th width="3%" style="text-align: center;">Ano</th>
						<th>Título</th>
						<th>Situação</th>
						<th></th>
						<th></th>					
				</tr>
			</thead>
			<tbody>
				<c:if test="${empty projetos}">
					<tr>
						<td colspan="4" align="center"><font color="red">Usuário atual não coordena Ações Associadas ativas.</font></td>
					</tr>
				</c:if>
				
				<c:forEach items="#{projetos}" var="item" varStatus="status">
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td width="3%" > <h:outputText  style="text-align: center;" value="#{item.ano}"/></td>
							<td width="70%"><h:outputText value="#{item.titulo}"/></td>
							<td><h:outputText value="#{item.situacaoProjeto.descricao}"/></td>
							<td  width="2%">
									<h:commandLink action="#{projetoBase.view}" style="border: 0;" id="visualizar_projeto" title="Visualizar Ação Associada" >
										<h:graphicImage url="/img/view.gif"/>
									         <f:param name="id" value="#{item.id}"/>								       
									</h:commandLink>
							</td>								
							<td width="2%">	
								<h:commandButton action="#{planoTrabalhoProjeto.novoPlanoTrabalho}" image="/img/adicionar.gif" title="Cadastrar Novo Plano" style="border: 0;" id="cadastrar_novo_plano" >
								        <f:setPropertyActionListener target="#{planoTrabalhoProjeto.idProjeto}" value="#{item.id}"/>
								</h:commandButton>								
							</td>
					</tr>
				</c:forEach>
			</tbody>
	</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>