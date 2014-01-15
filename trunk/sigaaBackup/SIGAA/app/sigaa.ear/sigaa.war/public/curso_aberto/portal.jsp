<%@ include file="./include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/erros.jsp"%>
<style>
	table.listagem td{border:0px;padding:10px;}
	table.listagem td.turma{padding:5px 10px 5px;}
	table.listagem td.topico{padding:0px 10px 10px;border-bottom:1px solid #EEEEEE; }
	table.listagem td.turma a{text-decoration: underline;vertical-align: middle;}
</style>	

<f:view>
<div id="colEsq">
	<%@ include file="./include/menu.jsp" %>
</div>
<div id="colDir">
	<div id="colDirCorpo">
	<!-- INÍCIO CONTEÚDO -->
	<div class="descricaoOperacao" style="margin:auto 40px;text-align:justify;">
	<p>Através desta página você poderá consultar as <b>Turmas</b> oferecidas pelos seus respectivos departamentos ou <b>Comunidades Virtuais</b> 
	  que tenham sido criadas e publicadas de forma pública. 
	</p>
	<br/>
	<p>
	 Por favor selecione/preencha uma ou mais opções abaixo, clique em buscar, ou se preferir
	 clique em um dos centros ao lado e selecione um departamento.
	<p>
	
	</div>	
	<br clear="all"/>
	<br clear="all"/>
	
	<h:form id="formPrincipal">
		<table class="formulario" width="90%">
			<caption>Informe os critérios de consulta</caption>
			<tbody>
				
				<tr>
					<th>Buscar por: </th>
					<td align="left" colspan="2">
						<h:selectOneRadio value="#{consultaPublicaTurmas.buscaTurmas}" id="tipoBusca" style="border-style:none">
							<f:selectItem itemValue="true" itemLabel="Turmas" />
							<f:selectItem itemValue="false" itemLabel="Comunidades Virtuais"/>
						 	<a4j:support reRender="formPrincipal" event="onclick"></a4j:support>
						</h:selectOneRadio>
					</td>
				</tr>
				 
				<tr>
					<th>Palavra-chave: </th>
					<td align="left" colspan="2">
						<h:inputText  id="palavraChave" 
						value="#{consultaPublicaTurmas.palavraChave}" 
						maxlength="200"  styleClass="filtroBusca" />
						
					</td>
				</tr>
		
				<tr>
					<th>Centros: </th>
					<td align="left" colspan="2">
						<h:selectOneMenu id="idCentro" styleClass="filtroBusca" value="#{consultaPublicaTurmas.idCentro}"
						valueChangeListener="#{unidade.changeCentro}" disabled="#{!consultaPublicaTurmas.buscaTurmas}">
							<f:selectItem itemLabel=" -- TODOS -- " itemValue="0"/>
							<f:selectItems value="#{unidade.allCentrosEscolasCombo}"/>
							<a4j:support event="onchange" reRender="formPrincipal"/>
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<th>Departamentos:</th>
					<td align="left" >
						<h:selectOneMenu id="idDepartamento" styleClass="filtroBusca" value="#{consultaPublicaTurmas.idDepto}"
							disabled="#{!consultaPublicaTurmas.buscaTurmas}">
							<f:selectItem itemLabel=" -- TODOS -- " itemValue="0"/>
							<f:selectItems value="#{unidade.unidades}"/>
						</h:selectOneMenu>
					</td>
					<td>
					 <a4j:status>
			                <f:facet name="start"><h:graphicImage value="/img/indicator.gif"/></f:facet>
			           </a4j:status>
					</td>
				</tr>


			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton id="buscarTurmas" value="Buscar" action="#{consultaPublicaTurmas.buscarCursosAbertos}" />&nbsp;
						<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}"  action="#{portalPublicoCurso.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>

	<c:if test="${not empty consultaPublicaTurmas.turmas}">
				<br clear="all"/>
				
				<div id="turmasAbertas">
					<table class="listagem" width="95%">
						<caption>
							<c:choose>
								<c:when test="${consultaPublicaTurmas.buscaTurmas && consultaPublicaTurmas.idCentro==0 && consultaPublicaTurmas.idDepto==0 && empty consultaPublicaTurmas.palavraChave}">
									Últimas Turmas Publicadas
								</c:when>
								<c:otherwise>
									${fn:length(consultaPublicaTurmas.turmas)} Turma(s) encontrada(s)
								</c:otherwise>
							</c:choose>
						</caption>
						
						<tbody>
							<c:set var="palavraChaveNegrito" value="<b>${consultaPublicaTurmas.palavraChave}</b>"/>
							
							<c:forEach var="_turmaAberta" items="#{consultaPublicaTurmas.turmas}" varStatus="status">

								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td class="turma" colspan="4">
									${_turmaAberta.disciplina.codigo}

										<h:commandLink action="#{consultaPublicaTurmas.detalhesTopicoAula}" >
											${sf:marcaTexto(consultaPublicaTurmas.palavraChave,_turmaAberta.disciplina.nome)}
											<f:param name="tid" value="#{ _turmaAberta.id }" />
										</h:commandLink>	 
		
										<%--
										<a href="./turma_resumo.jsf?tid=${_turmaAberta.id}
											&idCentro=${consultaPublicaTurmas.idCentro}">
											
										</a> 
										--%>
									</td>
								</tr>
								
								<c:set var="_topico" value="${sf:marcaTexto(consultaPublicaTurmas.palavraChave,_turmaAberta.observacao)}"/>
									<c:if test="${not empty _topico && not empty consultaPublicaTurmas.palavraChave}">
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td align="justify" colspan="4" class="topico">
										${fn: substring(_topico,0,200)}...										
									</td>
								</tr>
								</c:if>
							</c:forEach>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="4" align="center"> 
									<b>${fn:length(consultaPublicaTurmas.turmas)} Turma(s) encontrada(s) </b>
								</td>
							</tr>
						</tfoot>
				</table>
			</div>	
		</c:if>
		
		<c:if test="${not empty consultaPublicaTurmas.listaComunidadesAbertaPortalPublico}">
			<br clear="all"/>
				
				<div id="turmasAbertas">
					<table class="listagem" width="95%">
						<caption>${fn:length(consultaPublicaTurmas.listaComunidadesAbertaPortalPublico)} Comunidade(s) Virtual(is) encontrada(s)</caption>
						<tbody>
							<c:set var="palavraChaveNegrito" value="<b>${consultaPublicaTurmas.palavraChave}</b>"/>
							
							<c:forEach var="_cv" items="#{consultaPublicaTurmas.listaComunidadesAbertaPortalPublico}" varStatus="status">

								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td class="turma" colspan="4">

										<h:commandLink action="#{consultaPublicaTurmas.visualizarComundiadeVirtual}" >
											${sf:marcaTexto(consultaPublicaTurmas.palavraChave,_cv.nome)}
											<f:param name="idComunidade" value="#{ _cv.id }" />
										</h:commandLink>	
									</td>
								</tr>
	
									<c:set var="_topico" value="${sf:marcaTexto(consultaPublicaTurmas.palavraChave,_cv.descricao)}"/>
									<c:if test="${not empty _topico && not empty consultaPublicaTurmas.palavraChave}">
										<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
											<td align="justify" colspan="4" class="topico">
												${fn: substring(_topico,0,200)}...										
											</td>
										</tr>
									</c:if>
							</c:forEach>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="4" align="center"> 
									<b> ${fn:length(consultaPublicaTurmas.listaComunidadesAbertaPortalPublico)} Comunidade(s) Virtual(is) encontrada(s) </b>
								</td>
							</tr>
						</tfoot>
				</table>
			</div>	
		</c:if>

		</h:form>
	</div>
</div> 
</f:view>
<%@ include file="../include/rodape.jsp" %>