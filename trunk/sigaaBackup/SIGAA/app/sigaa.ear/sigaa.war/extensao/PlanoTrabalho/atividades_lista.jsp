<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> > Cadastro de plano de trabalho</h2>
		
		<div class="descricaoOperacao">
			<b>Bem-vindo ao cadastro de planos de trabalho.</b> <br/><br/>
			Para cada a��o de extens�o listada voc� pode cadastrar planos de trabalho. 
			Se sua a��o de extens�o n�o est� listada abaixo, verifique se ela possui as seguintes caracter�sticas:<br/>
			
			<ul>
			    <li>Est� sob sua coordena��o</li>
			    <li>Possui status igual a EM EXECU��O</li>
			</ul>
		</div>
		
		
	<div class="infoAltRem">
	    <h:graphicImage value="/img/view.gif"style="overflow: visible;" styleClass="noborder"/>: Visualizar A��o	    
	    <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;" styleClass="noborder"/>: Cadastrar Novo Plano
	</div>
		
	<c:set var="atividades" value="#{planoTrabalhoExtensao.atividades}"	/>
		
	<h:form id="form">
	<table class="listagem">
					<caption class="listagem"> Lista de A��es Coordenadas pelo usu�rio Atual</caption>
			<thead>
				<tr>
						<th>C�digo</th>
						<th>T�tulo</th>
						<th>Situa��o</th>
						<th></th>
						<th></th>					
						<th></th>					
				</tr>
			</thead>
			<tbody>
				<c:if test="${empty atividades}">
					<tr>
							<td colspan="4" align="center"><font color="red">Usu�rio atual n�o coordena A��es de extens�o ativas.</font></td>
					</tr>
				</c:if>
				
				<c:forEach items="#{atividades}" var="item" varStatus="status">
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td>${item.codigo}</td>
							<td width="60%">${item.titulo}</td>
							<td>${item.situacaoProjeto.descricao}</td>
							<td  width="2%">
									<h:commandLink action="#{atividadeExtensao.view}" style="border: 0;" id="visualizar_acao" title="Visualizar A��o">
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