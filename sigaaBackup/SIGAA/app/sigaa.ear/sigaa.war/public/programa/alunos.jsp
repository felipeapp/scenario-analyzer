<%-- Cabeçalho --%>
<%@ include file="include/cabecalho.jsp" %>

<f:view locale="#{portalPublicoPrograma.lc}">
<a4j:keepAlive beanName="portalPublicoPrograma"/>
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>

<%-- topo --%>
<%@ include file="include/programa.jsp" %>
<%-- menu flutuante --%>
<%@ include file="include/menu.jsp" %>

	 
<div id="conteudo">
<%-- INÍCIO CONTEÚDO --%>
	
	<div class="titulo">
		<h:outputText value="#{idioma.alunosAtivos}"/>
	</div>
	
	<c:set var="nivelUltimo" value=""/>
	<c:set var="alunosAt" value="#{portalPublicoPrograma.alunosAtivos}"/>
	<c:set var="areaUltima"  value=""/>
	 <c:set var="tipoCursoUltimo"  value=""/>

	<c:if test="${not empty alunosAt}">

		<%-- CONTA O NÚMERO DE ALUNOS EM CADA NÍVEL --%>
		<c:set var="contMestrado" value="0"/>
		<c:set var="contDoutorado" value="0"/>
		<c:set var="contMestradoProfissional" value="0"/>
		<c:forEach items="#{alunosAt}" var="aluno" varStatus="status">
			<c:if test="${alunoUltimo!=aluno.discente.nome}">	
				<c:if test="${aluno.discente.curso.mestradoProfissional}">	
					<c:set var="contMestradoProfissional" value="${contMestradoProfissional+1}"/>				
				</c:if>
				<c:if test="${aluno.discente.curso.mestrado}">	
					<c:set var="contMestrado" value="${contMestrado+1}"/>				
				</c:if>
				<c:if test="${aluno.discente.doutorado}">	
					<c:set var="contDoutorado" value="${contDoutorado+1}"/>				
				</c:if>
			</c:if>
			 <c:set var="alunoUltimo"  value="${aluno.discente.nome}"/>
		</c:forEach>
		
		<%-- LISTAGEM DOS ALUNOS AGRUPADOS PELO NÍVEL DO CURSO --%>
		<c:set var="cont" value="0"/>
		<c:forEach items="#{alunosAt}" var="aluno" varStatus="status">

			<c:if test="${nivelUltimo!=aluno.discente.nivel || ( nivelUltimo!=aluno.discente.nivel && aluno.discente.curso.tipoCursoStricto.descricao != tipoCursoUltimo ) }">	

					<c:if test="${!status.first}">
									</td>
								</tr>
							</tbody>
						</table>
						</div>
						<br clear="all"/>	
					</c:if>	
							
					<div id="listagem_tabela">	
						
						<div id="group_lt">
							<c:choose>
								<c:when test="${aluno.discente.curso.mestradoProfissional}">
									${idioma.mestradoProfissional} (${contMestradoProfissional})
								</c:when>
								<c:when test="${aluno.discente.curso.mestrado}">
									${idioma.mestrado} (${contMestrado})
								</c:when>
								<c:otherwise>
									${idioma.doutorado} (${contDoutorado})
								</c:otherwise>
							</c:choose>
						</div>
						
						<table id="table_lt">
							<tbody>	
							<tr class="campos">
								<td class="direita" width="80px"><h:outputText value="#{idioma.matricula}"/></th>
								<td width="835px"><h:outputText value="#{idioma.aluno}"/>/<h:outputText value="#{idioma.orientador}"/></th>
							</tr>
							
			</c:if>
		
			<c:choose>
				<c:when test="${alunoUltimo != aluno.discente.nome}">
						
					<c:set var="cont" value="${cont+1}"/>
					<c:if test="${!status.first && nivelUltimo==aluno.discente.nivel}">
							</td>
						</tr>
					</c:if>
							
					<tr  class="${cont % 2 == 0 ? '' : 'linha_impar' }">
						<td class="direita" width="80px">${aluno.discente.matricula}</td>
						<td width="835px">${aluno.discente.nome}<br/>
							<c:if test="${empty aluno.fim && (empty aluno.cancelado || !aluno.cancelado)}">
								<strong><h:outputText value="#{idioma.orientador}"/>:</strong> &nbsp;
							</c:if> 
				</c:when>	
				<c:otherwise>
							<c:if test="${empty aluno.fim && (empty aluno.cancelado || !aluno.cancelado)}">
								, &nbsp;
							</c:if>
				</c:otherwise>
			</c:choose>
			
			<c:if test="${empty aluno.fim && (empty aluno.cancelado || !aluno.cancelado)}">
			
				<c:if test="${not empty aluno.docenteExterno && not empty aluno.docenteExterno.pessoa.nome}">
					${aluno.docenteExterno.pessoa.nome} (${aluno.tipoOrientacaoString})	
				</c:if>	
			
				<c:if test="${not empty aluno.servidor && not empty aluno.servidor.pessoa.nome}">
				
					<a class="cor" href="${ctx}/public/docente/portal.jsf?siape=${aluno.servidor.siape}" target="_blank" 
					title="Clique aqui para acessar a página pública deste docente">
						${aluno.servidor.pessoa.nome}(${aluno.tipoOrientacaoString})
					</a>
					
				</c:if>
				
			</c:if>
	
			 <c:set var="nivelUltimo"  value="${aluno.discente.nivel}"/>
			 <c:set var="tipoCursoUltimo"  value="${aluno.discente.curso.tipoCursoStricto.descricao}"/>
			 <c:set var="alunoUltimo"  value="${aluno.discente.nome}"/>
					 
		</c:forEach>
			
					</td>
				</tbody>
			</table>
		</div>	
	</c:if>
	
	<c:if test="${empty alunosAt}">
		<p class="vazio"><h:outputText value="#{idioma.vazio}"/></p>
	</c:if>

<%--  FIM CONTEÚDO  --%>	
</div>	

</f:view>
<%-- Rodapé --%>
<%@ include file="./include/rodape.jsp" %>