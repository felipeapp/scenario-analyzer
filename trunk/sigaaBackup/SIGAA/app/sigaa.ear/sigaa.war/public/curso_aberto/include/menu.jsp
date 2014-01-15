<h:form id="menuForm">
	<div id="menu-pri">
		<dl>
			<c:set var="ultCentro" value=""/>
			
		
			<c:forEach var="_centrosMenu" items="#{consultaPublicaTurmas.centrosDeptos}" varStatus="loop">
			
				<c:choose>
					<c:when test="${not empty _centrosMenu.colectionGeral}">
						<dt>
							<a href="#"
							 class="centro" onclick="esconderTodos();mostrarAtual(this);" >
							 ${_centrosMenu.nome}
							 </a>
						
						</dt>
						
						<dd id="${_centrosMenu.id}">
							<ul style="position: relative;">
		
							<c:forEach var="_deptosMenu" items="#{_centrosMenu.colectionGeral}" varStatus="loop2">
				
								<li style="position: relative;">	
									<h:commandLink title="Turmas do Departamento" 
									action="#{consultaPublicaTurmas.buscarCursosAbertos}" styleClass="departamento" >
										${fn:replace(_deptosMenu.nome, 'DEPARTAMENTO', 'DEP.')}
										<f:setPropertyActionListener value="#{_deptosMenu.id}" target="#{consultaPublicaTurmas.idDepto}" />
										<f:setPropertyActionListener value="#{_centrosMenu.id}" target="#{consultaPublicaTurmas.idCentro}" />
									</h:commandLink>
								</li>
								
							</c:forEach>
							
							</ul>
						</dd>
					</c:when>
					<c:otherwise>
						<dt>
							<h:commandLink styleClass="centro"  
								action="#{consultaPublicaTurmas.buscarCursosAbertos}" >
								<f:setPropertyActionListener value="#{_centrosMenu.id}" target="#{consultaPublicaTurmas.idCentro}" />
							 ${_centrosMenu.nome}
							</h:commandLink>
						</dt>
					</c:otherwise>
				</c:choose>
			</c:forEach>

		</dl>		
	</div>

	<div id="home-link">
		<h:commandLink title="Ir para o Portal Público do SIGAA"
			action="#{portalPublicoCurso.cancelar}">
			<h:outputText	value="#{idioma.irMenuPrincipal}" />
		</h:commandLink>
	</div>
	<br clear="all"/>
</h:form>

<rich:jQuery selector="#menu-pri dd" query="hide()" timing="onload" />
<c:if test="${not empty consultaPublicaTurmas.idCentro}">
<rich:jQuery selector="#menu-pri ##{consultaPublicaTurmas.idCentro}" query="show()" timing="onload" />
</c:if>
<rich:jQuery name="esconderTodos" selector="#menu-pri dd:visible" query="slideUp('slow')" timing="onJScall" />
<rich:jQuery name="mostrarAtual" selector="#menu-pri dt a" query="parent().next().slideDown('slow')" timing="onJScall" />