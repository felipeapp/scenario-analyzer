<%@ include file="./include/cabecalho.jsp" %>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view locale="#{portalPublicoCurso.lc}">
	<a4j:keepAlive beanName="portalPublicoCurso"/>
	<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
<div id="colEsq">
	<%@ include file="./include/menu.jsp" %>
</div>

<div id="colDir">
	<%@ include file="./include/curso.jsp" %>

	<div id="colDirCorpo">
		<!--  Início do Conteúdo -->	
		<div id="titulo">
			<h1 class="estagios"><h:outputText value="#{idioma.estagios}"/></h1>
		</div>		
		<div class="descricaoOperacao">
			<p>Nesta página visualizar as vagas de estágio disponíveis para o seu curso.</p>
		</div>				
		<h:form id="form">				
			<c:set var="ofertas" value="#{portalPublicoCurso.ofertasEstagio}" />
			<c:if test="${not empty ofertas}">	
				<div class="legenda">
					<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Oferta de Estágio
				</div>
										
				<table class="listagem" style="width: 100%">
					<caption class="listagem"><h:outputText value="#{idioma.vagasEstagioEncontradas}"/> (${fn:length(ofertas)})</caption>
					<thead>
						<tr>
							<th style="width: 30%;"><h:outputText value="#{idioma.titulo}"/></th>
							<th style="text-align: right;"><h:outputText value="#{idioma.vagas}"/></th>
							<th style="text-align: right;"><h:outputText value="#{idioma.valorBolsa}"/></th>
							<th style="text-align: center;"><h:outputText value="#{idioma.fimInscricoes}"/></th>
							<th colspan="1"></th>
						</tr>
					</thead>
					<c:set var="idConcedente" value="0"/> 
					<c:forEach items="#{ofertas}" var="oferta" varStatus="loop">	
						<c:if test="${idConcedente != oferta.concedente.pessoa.id}">
							<tr>
								<td colspan="5" class="subFormulario">Concedente: ${oferta.concedente.pessoa.nome}</td>
							</tr>
							<c:set var="idConcedente" value="${oferta.concedente.pessoa.id}"/> 		
						</c:if>	
						<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td>${oferta.titulo}</td>
							<td style="text-align: right;">${oferta.numeroVagas}</td>
							<td style="text-align: right;"><ufrn:format type="moeda" valor="${oferta.valorBolsa}"></ufrn:format></td>
							<td style="text-align: center;"><ufrn:format type="data" valor="${oferta.dataFimPublicacao}"></ufrn:format></td>
							<td style="width:5px;">
								<h:commandLink action="#{consultaPublicaCursos.detalhesOfertaEstagio}" title="Visualizar Oferta de Estágio">
									<h:graphicImage value="/img/view.gif"/>
									<f:param name="id" value="#{oferta.id}" />
								</h:commandLink>
							</td>
						</tr>					
					</c:forEach>
				</table>					
			</c:if>		
			
			<c:if test="${empty ofertas}">
				<table class="listagem">
					<caption class="listagem">Vagas de Estágio</caption>
					<tr>
						<td style="text-align:center;"><i>Não foi encontrada nenhuma Vaga de Estágio para este Curso.</i></td>
					</tr>
				</table>			
			</c:if>			
		</h:form>			
		<!--  FIM CONTEÚDO  -->	
	</div>
</div>	
</f:view>
<%@ include file="./include/rodape.jsp" %>