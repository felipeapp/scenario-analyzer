<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> > Cadastro de plano de trabalho</h2>
		
		<div class="descricaoOperacao">
			<b>Bem-vindo ao cadastro de planos de trabalho.</b> <br/><br/>
			Para cada ação de extensão listada você pode cadastrar planos de trabalho. 
			Se sua ação de extensão não está listada abaixo, verifique se ela possui as seguintes características:<br/>
			
			<ul>
			    <li>Está sob sua coordenação</li>
			    <li>Possui status igual a EM EXECUÇÃO</li>
			</ul>
		</div>
		
		
	<div class="infoAltRem">
	    <h:graphicImage value="/img/view.gif"style="overflow: visible;" styleClass="noborder"/>: Visualizar Ação	    
	    <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;" styleClass="noborder"/>: Cadastrar Novo Plano
	</div>
		
	<c:set var="atividades" value="#{planoTrabalhoExtensao.atividades}"	/>
		
	<h:form id="form">
	<table class="listagem">
					<caption class="listagem"> Lista de Ações Coordenadas pelo usuário Atual</caption>
			<thead>
				<tr>
						<th>Código</th>
						<th>Título</th>
						<th>Situação</th>
						<th></th>
						<th></th>					
						<th></th>					
				</tr>
			</thead>
			<tbody>
				<c:if test="${empty atividades}">
					<tr>
							<td colspan="4" align="center"><font color="red">Usuário atual não coordena Ações de extensão ativas.</font></td>
					</tr>
				</c:if>
				
				<c:forEach items="#{atividades}" var="item" varStatus="status">
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td>${item.codigo}</td>
							<td width="60%">${item.titulo}</td>
							<td>${item.situacaoProjeto.descricao}</td>
							<td  width="2%">
									<h:commandLink action="#{atividadeExtensao.view}" style="border: 0;" id="visualizar_acao" title="Visualizar Ação">
									       <f:param name="id" value="#{item.id}"/>								       
									       <h:graphicImage url="/img/view.gif" />
									</h:commandLink>
							</td>								
							<td width="2%">	
								<h:commandLink action="#{planoTrabalhoExtensao.novoPlanoTrabalho}" title="Cadastrar Novo Plano" 
										style="border: 0;" id="cadastrar_novo_plano" 
										rendered="#{item.aprovadoEmExecucao}">
								        <f:setPropertyActionListener target="#{planoTrabalhoExtensao.idAtividade}" value="#{item.id}"/>
								        <h:graphicImage url="/img/adicionar.gif" />
								</h:commandLink>								
							</td>
					</tr>
				</c:forEach>
			</tbody>
	</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>