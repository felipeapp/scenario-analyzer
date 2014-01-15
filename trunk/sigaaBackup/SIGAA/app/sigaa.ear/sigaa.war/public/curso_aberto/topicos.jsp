	<h2>
		Material do Curso
	</h2>

	<c:forEach var="item"  items="#{ consultaPublicaTurmas.topicosAulas }" varStatus="status">
	<c:if test="${item.visivel}">
	<c:set var="marginLeft" value="${ 10 + (20*item.nivel)}" />
	<div class="topico-aula"  style="margin-left: ${marginLeft}px; ${!item.visivel ?  "background-color: #F0F0F0;" : "" }">
		
		<h3>
		${item.topicoPai.id}
			<h:outputText escape="false" value="#{ item.descricao }"/>
			(<fmt:formatDate value="${ item.data }" pattern="dd/MM/yyyy"/> - <fmt:formatDate value="${ item.fim }" pattern="dd/MM/yyyy"/>)
		</h3>
		<ul>
			<li><h:outputText escape="false" value="#{ item.conteudo }"/></li>
		</ul>
		
		<c:set var="_tipoMaterial" value="0"/>
		<c:forEach var="material" items="#{ item.materiais }" varStatus="i">
			<c:choose>
			
				<%-- REFERÊNCIAS--%>
				<c:when test="${ material.tipoArquivo }">
					<c:if test="${!i.first && _tipoMaterial!='1'}">
						</ul><ul class="materiais">
					</c:if>
					<li>
						<a href="${ctx}/verProducao?idProducao=${ material.arquivo.idArquivo }&key=${ sf:generateArquivoKey(material.arquivo.idArquivo)}" 
							 target="_blank" title="${ sf:escapeHtml(material.descricao) }">
							<img src="${ ctx }/${ material.icone }" title="${ sf:escapeHtml(material.descricao) }"	alt="${ sf:escapeHtml(material.descricao) }"> 
							${ material.nome }
						 </a>
					</li>
					 <c:set var="_tipoMaterial" value="1"/>
				</c:when>
				
				<%-- ANEXOS --%>	
				<c:when test="${ material.tipoIndicacao }">
					<c:if test="${!i.first && _tipoMaterial!='2'}">
						</ul><ul class="materiais">
					</c:if>
					<li>
						<h:commandLink action="#{ indicacaoReferencia.mostrar }" 
							rendered="#{!material.site}">
							<h:graphicImage value="#{ material.icone }"/>
							<h:outputText value=" #{ material.nome }"/>
							<f:param name="id" value="#{ material.id }"/>
						</h:commandLink>
						
						<c:if test="${material.site}">
							<a href="${material.url}" target="_blank">
								<img src="/sigaa/img/portal_turma/site_add.png">
								<h:outputText value=" #{ material.nome }"/>
							</a>
						</c:if>
						
						<i><small>	(${material.tipoDesc})</small></i>
					</li>	
					 <c:set var="_tipoMaterial" value="2"/>
				 </c:when>
							
				<%-- CONTEÚDOS --%>
				<c:when test="${ material.tipoConteudo }">
					<c:if test="${!i.first && tipoMaterial!='3'}">
						</ul><ul class="materiais">
					</c:if>
					<li>
						<h:commandLink action="#{ consultaPublicaTurmas.mostrarMaterial }">
							<h:graphicImage value="#{ material.icone }"/>
							<h:outputText value=" #{ material.nome }"/>
							<f:param name="mid" value="#{material.id}"/>
						</h:commandLink>
					</li>
					<c:set var="_tipoMaterial" value="3"/>
				</c:when>
				
			</c:choose>	
		</c:forEach>
		
	</div>
	</c:if>				

	</c:forEach>
	
	<br clear="all"/>
