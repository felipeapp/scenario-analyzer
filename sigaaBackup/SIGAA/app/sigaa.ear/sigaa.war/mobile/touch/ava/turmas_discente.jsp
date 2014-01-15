<%@include file="../include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="page-turmas-discente" data-theme="b">
	<h:form id="form-turmas-discente">
		
				<div data-role="header" data-theme="b">
					<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
				</div>
				
				<div data-role="navbar" data-theme="b" data-iconpos="left">
					<ul>
						<li><h:commandLink value="Início" action="#{ portalDiscenteTouch.acessarPortal }" id="lnkInicio"/></li>
						<li><h:commandLink value="Sair" action="#{ loginMobileTouch.logoff }" id="lnkSair" onclick="#{logout }" /></li>
					</ul>
				</div>
		     		
				<div data-role="content">
					<p align="center"><strong>Turmas do Discente</strong></p>
					
					<%@include file="/mobile/touch/include/mensagens.jsp"%>
					
					<div data-role="fieldcontain">
						<c:if test="${ not empty portalDiscenteTouch.turmas }">				
						    <ul data-role="listview" data-inset="true" data-filter="true" data-filter-placeholder="Buscar turmas..." data-filter-theme="b" data-theme="b" data-dividertheme="b">
						    	<c:set var="anoPeriodo" value="0" />
						    	<c:forEach var="t" items="#{ portalDiscenteTouch.turmas }">
						    		<c:if test="${ anoPeriodo != t.anoPeriodo }">
						    			<c:set var="anoPeriodo" value="${t.anoPeriodo}" />
					       				<li data-role="list-divider"><h:outputText value="#{t.anoPeriodo}"/> - <h:outputText value="#{t.disciplina.nivelDesc}"/></li>
						    		</c:if>
					           		<li>
					           			<h:commandLink style="white-space: normal;" action="#{portalDiscenteTouch.entrarTurma}" id="cmdEntrarTurma">
											<f:param value="#{t.id}" name="idTurma"/>
											<f:param value="true" name="msgSwipe"/>
											
											${t.disciplina.codigoNome}
												
											<c:if test="${t.disciplina.permiteHorarioFlexivel}">
												*
												<c:set var="possuiTurmaHorarioFlexivel" value="true" />
											</c:if>
											<br/>
											
											<c:if test="${not empty t.local}">
												<small>Local: ${t.local}</small>
											</c:if>
											<c:if test="${not empty t.local && not empty t.descricaoHorarioSemanaAtual}">
												<small> - </small>
											</c:if>
											<c:if test="${not empty t.descricaoHorarioSemanaAtual}">
												<small>Horário: ${t.descricaoHorarioSemanaAtual}</small>
											</c:if>
				  	           			</h:commandLink>
									</li>
								</c:forEach>
						    </ul>
						</c:if>    
						
						<c:if test="${possuiTurmaHorarioFlexivel}">
				 			<div style="text-align: center;">
				 				* A turma possui horário flexível e o horário exibido é da semana atual.
				 			</div>
				 		</c:if>
						
						<div style="text-align: center;">
							<h:outputText value="Nenhuma turma neste semestre." rendered="#{empty portalDiscenteTouch.turmas}"/>
						</div>
					</div>
					<h:commandLink value="Ver Todas" action="#{ portalDiscenteTouch.listarTodasMinhasTurmas }" id="lnkVerTodas" />
				</div>
					
				<script>
					$("#form-turmas-discente\\:lnkInicio").attr("data-icon", "home");
					$("#form-turmas-discente\\:lnkSair").attr("data-icon", "sair");
					
					$("#form-turmas-discente\\:lnkVerTodas").attr("data-icon", "search");
					$("#form-turmas-discente\\:lnkVerTodas").attr("data-role", "button");
				</script>
				
				<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
	  </h:form>
	  <%@include file="../include/modo_classico.jsp"%>
	</div>
</f:view>
<%@include file="../include/rodape.jsp"%>
			
