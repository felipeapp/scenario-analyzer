<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>

	<h2><ufrn:subSistema /> > Selecionar Projeto para Cadastrar Prova Seletiva</h2>

	<div class="infoAltRem">
		<c:if test="${acesso.coordenadorMonitoria}">
	     	<h:graphicImage value="/img/monitoria/document_new.png" style="overflow: visible;" />: Nova Prova	     	
	     	<h:graphicImage value="/img/alterar.gif"style="overflow: visible;" />: Alterar Prova
		    <h:graphicImage value="/img/monitoria/form_green.png"style="overflow: visible;"/>: Listar Inscritos		    
	     	<br />
		    <h:graphicImage value="/img/monitoria/document_chart.png"style="overflow: visible;"/>: Visualizar Resultado							
		    <h:graphicImage value="/img/delete.gif"style="overflow: visible;" />: Apagar Prova	
	     	<h:graphicImage value="/img/seta.gif"style="overflow: visible;" />: Cadastrar Resultado da Prova
	    </c:if>
	</div>


<h:form id="form">

	<table class="listagem">
		<caption class="listagem">Projetos de Ensino Coordenados pelo usuário atual </caption>
		<c:set value="#{provaSelecao.projetos}" var="projetosUsuarioLogado"/>

		<c:forEach items="#{projetosUsuarioLogado}" var="projeto" varStatus="status">
			<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
				<td>${projeto.anoTitulo}</td>
				<td width="5%">
					<h:commandLink title="Nova Prova" action="#{provaSelecao.novaProvaSeletiva}">
					     <f:param name="idProjeto" value="#{projeto.id}"/>
					     <h:graphicImage url="/img/monitoria/document_new.png" />
					</h:commandLink>
				</td>
			</tr>
		
			<tr>
				<td colspan="2">
					<table class="listagem">
						<thead>
							<tr>
								<th>Data Prova</th>
								<th>Inscrições até</th>				
								<th>Título</th>
								<th>VR</th>
								<th>VNR</th>
								<th></th>				
								<th></th>
								<th></th>
								<th></th>				
								<th></th>				
								<th></th>
							</tr>
						</thead>
				
						<c:forEach items="#{projeto.provasSelecao}" var="prova" varStatus="status">
						   						   
					            <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						            <td><fmt:formatDate pattern="dd/MM/yyyy" value="${prova.dataProva}"/></td>
						            <td><fmt:formatDate pattern="dd/MM/yyyy" value="${prova.dataLimiteIncricao}"/></td>
					            	<td>${prova.titulo}</td>					            
					                <td>${prova.vagasRemuneradas}</td>
					                <td>${prova.vagasNaoRemuneradas}</td>
	
									<td width="2%">
												<h:commandLink  title="Alterar Prova" action="#{provaSelecao.atualizar}" style="border: 0;" 
													rendered="#{prova.permitidoAlterar}">
													      <f:param name="id" value="#{prova.id}"/>
													      <h:graphicImage url="/img/alterar.gif" />
												</h:commandLink>
									</td>
									<td  width="2%">			
												<h:commandLink  title="Listar Inscritos" action="#{provaSelecao.visualizarCandidatos}" style="border: 0;">
													      <f:param name="id" value="#{prova.id}"/>
													      <h:graphicImage url="/img/monitoria/form_green.png" />
												</h:commandLink>
									</td>
									<td width="2%">			
												<h:commandLink title="Visualizar Resultado"  action="#{ provaSelecao.visualizarResultados }">
													      <f:param name="id" value="#{prova.id}"/>
													      <h:graphicImage url="/img/monitoria/document_chart.png" />
												</h:commandLink>
									</td>
									<td width="2%">			
												<h:commandLink action="#{provaSelecao.preRemover}" style="border: 0;" title="Apagar Prova"
												 rendered="#{(empty prova.discentesInscritos) and (prova.permitidoAlterar)}" 
												 onclick="return confirm('Atenção! Deseja realmente remover esta prova da lista?');">
												      <f:param name="id" value="#{prova.id}"/>
												      <h:graphicImage url="/img/delete.gif" />
												</h:commandLink>
									</td>
									<td width="2%">
												<h:commandLink title="Cadastrar Resultado da Prova" action="#{discenteMonitoria.iniciarCadatroResultadoProva}" 
												style="border: 0;" rendered="#{prova.permitidoAlterar && (empty prova.resultadoSelecao)}">
												      <f:param name="idProva" value="#{prova.id}"/>
												      <h:graphicImage url="/img/seta.gif" />
												</h:commandLink>
									</td>
									<td width="2%">
												<h:commandLink title="Enviar Email" action="#{discenteMonitoria.iniciarEnvioEmail}" 
												style="border: 0;">
												      <f:param name="idProva" value="#{prova.id}"/>
												      <h:graphicImage url="/img/email_go.png" />
												</h:commandLink>
									</td>									
								</tr>
								
						</c:forEach>
				
						<c:if test="${empty projeto.provasSelecao}">
							<tr>
								<td colspan="10"><center><font color="red">Não há Provas de Seleção cadastradas para este projeto<br/></font> </center></td>
							</tr>
						</c:if>
				
					</table>
				
				</td>
			</tr>
			
			<tr><td colspan="2"><br/></td></tr>
			
		</c:forEach>

		<c:if test="${empty projetosUsuarioLogado}">
			<tr>
				<td colspan="4"><center><font color="red">Usuário não coordena de Projetos de Ensino ativos<br/></font> </center></td>
			</tr>
		</c:if>

	</table>
	</h:form>
	<div>[<b>VR</b> = Vagas Remuneradas  <b>VNR</b> = Vagas Não Remuneradas] </div>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>