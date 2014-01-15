	<%@ include file="./include/cabecalho.jsp" %>
	<f:view  locale="#{portalPublicoDepartamento.lc}">
	<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
	<div id="colEsq">
		<%@ include file="./include/menu.jsp" %>
	</div>
	<div id="colDir">
	<h:form id="formComponenteDepartamento">	
		<%@ include file="./include/departamento.jsp" %>
		<div id="colDirCorpo">
		<!--  INÍCIO CONTEÚDO -->
	<div id="disciplinas-docente">
		<div id="titulo">
			<h1>Componentes Curriculares</h1>
		</div>
		<br/>
	
			<c:set var="componentesPG" value="#{portalPublicoDepartamento.componentesCurriculares}" />
			<c:choose>
			 	<c:when test="${not empty componentesPG}">
			 	<table class="listagem">
					<tfoot >
						<td colspan="4">
						<h:graphicImage url="/img/view.gif" width="12px" height="12px" />
						<b>: Visualizar Detalhes do Componente Curricular</b>
						</td>
					</tfoot>
				</table>
						<c:set var="tipo" value=""/>
						<c:forEach var="componente" items="#{portalPublicoDepartamento.componentesCurriculares}" varStatus="loop">
							<c:if test="${tipo != componente.tipoComponente.descricao}">
								<c:set var="tipo" value="${componente.tipoComponente.descricao}"/>
								<c:if test="${not loop.first}">
									</tbody>
									</table>
								</c:if>
								
								<h2>${componente.tipoComponente.descricao}</h2>
								<br clear="all">
								<table class="listagem">
									<thead>
										<th class="codigo"> Código </th>
										<th class="nome">Nome</th>
										<th class="tipo">Tipo da Atividade</th>
										<th class="ch" align="left"> CH </th>
										<th class="ver">Ver</th>
									</thead>
									<tbody>	
							</c:if>
							<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
								<td class="cod"> ${componente.codigo} </td>
								<td class="nome"> ${componente.nome} </td>
								<td > ${componente.tipoAtividade.descricao}</td>
								<td class="ch"> 	${componente.detalhes.chTotal}h </td>
								<td class="ver">
									<h:commandLink title="Visualizar Detalhes do Componente Curricular" action="#{componenteCurricular.detalharComponente}">								
										<f:param name="id" value="#{componente.id}"/>
										<f:param name="publico" value="#{componenteCurricular.consultaPublica}"/>							
										<h:graphicImage url="/img/view.gif"/><br clear="all"/>
									</h:commandLink>
								</td>
							</tr>
						</c:forEach>
						
						<table class="listagem" >	
						<tfoot>
							<td colspan="3"><b>${fn:length(componentesPG)} Componente(s) encontrado(s) </b></td>
						</tfoot>
						</table>
			</c:when>
			<c:otherwise>
				<p class="vazio">
					Nenhuma turma encontrada
				</p>	
			</c:otherwise>	
			</c:choose>

		</div>
		<!--  FIM CONTEÚDO  -->	
		</div>
	</h:form>	
	</div>
	</f:view>
	<%@ include file="../include/rodape.jsp" %>	