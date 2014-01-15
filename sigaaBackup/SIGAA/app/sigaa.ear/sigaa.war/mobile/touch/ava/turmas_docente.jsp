<%@include file="../include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="page-turmas-docente" data-theme="b">
		<h:form id="form-turmas-docente">
	
			<div data-role="header" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink value="Início" action="#{ portalDocenteTouch.acessarPortal }" id="lnkInicio"/></li>
					<li><h:commandLink value="Sair" action="#{ loginMobileTouch.logoff }" id="lnkSair" onclick="#{logout }" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><strong>Turmas do Docente</strong></p>
				
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
				
				<div data-role="fieldcontain">
					<c:if test="${ not empty portalDocenteTouch.turmas }">	
					    <ul data-role="listview" data-inset="true" data-filter="true" data-filter-placeholder="Buscar turmas..." data-filter-theme="b" data-theme="b" data-dividertheme="b">
					    	<c:set var="anoPeriodo" value="0" />
					    	<c:forEach var="t" items="#{portalDocenteTouch.turmas}">
					    		<c:if test="${ anoPeriodo != t.anoPeriodo }">
					    			<c:set var="anoPeriodo" value="${t.anoPeriodo}" />
				       				<li data-role="list-divider"><h:outputText value="#{t.anoPeriodo}"/> - <h:outputText value="#{t.disciplina.nivelDesc}"/></li>
					    		</c:if>
				           		<li>
			  	           			<h:commandLink style="white-space: normal;" action="#{portalDocenteTouch.entrarTurma}" id="cmdEntrarTurma">
										<f:param value="#{t.id}" name="idTurma"/>
										<f:param value="true" name="msgSwipe"/>
										
										${t.disciplina.codigoNome}
										<c:if test="${t.disciplina.permiteHorarioFlexivel}">
											<h:outputText value="*" />
											<c:set var="possuiTurmaHorarioFlexivel" value="true" />
										</c:if>
										<br/>
										
										<c:choose>
											<c:when test="${not empty t.subturmas}">
												<c:forEach items="#{t.subturmas}" var="subTurma">
													<c:set var="localTurma" value="" />
													
													<c:if test="${ empty subTurma.polo }">
														<c:set var="localTurma" value="${subTurma.local}" />
													</c:if>
													
													<c:if test="${ not empty subTurma.polo }">
														<c:set var="localTurma" value="${ subTurma.polo.descricao }" />
													</c:if>
										
													<c:if test="${not empty localTurma}">
											 			<small>T${subTurma.codigo } - Local: ${localTurma}</small>
													</c:if>
													
													<c:if test="${not empty subTurma.descricaoHorarioSemanaAtual}">
														<small>- Horário: ${subTurma.descricaoHorarioSemanaAtual}</small>
													</c:if>
												</c:forEach>
											</c:when>
											<c:otherwise>
												<c:set var="localTurma" value="" />
											
												<c:if test="${ empty t.polo }">
													<c:set var="localTurma" value="${t.local}" />
												</c:if>
											
												<c:if test="${ not empty t.polo }">
													<c:set var="localTurma" value="${ t.polo.descricao }" />
												</c:if>
										
												<c:if test="${not empty localTurma}">
													<small>Local: ${localTurma}</small>
												</c:if>
												
												<c:if test="${not empty t.descricaoHorarioSemanaAtual}">
													<small>- Horário: ${t.descricaoHorarioSemanaAtual}</small>
												</c:if>
											</c:otherwise>
										</c:choose>
										
										<!-- <br/>
										<small>
											${t.descricaoHorario} - ${t.local}
										</small> -->
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
				    	<h:outputText value="Nenhuma turma neste semestre." rendered="#{empty portalDocenteTouch.turmas}"/>
				    </div>
				    
				 </div>
				 
				 <h:commandLink value="Ver Todas" action="#{ portalDocenteTouch.listarTodasMinhasTurmas }" id="lnkVerTodas" />
			</div>
			
			<script>
				$("#form-turmas-docente\\:lnkVerTodas").attr("data-icon", "search");
				$("#form-turmas-docente\\:lnkVerTodas").attr("data-role", "button");
				
				$("#form-turmas-docente\\:lnkSair").attr("data-icon", "sair");
				$("#form-turmas-docente\\:lnkInicio").attr("data-icon", "home");
			</script>
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
	  	</h:form>
		<%@include file="../include/modo_classico.jsp"%>
 	 </div>
   </f:view>
<%@include file="../include/rodape.jsp"%>
			
