	<%@ include file="./include/cabecalho.jsp" %>
	<f:view  locale="#{portalPublicoDepartamento.lc}">
	<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
	<div id="colEsq">
		<%@ include file="./include/menu.jsp" %>
	</div>
	<div id="colDir">
		<%@ include file="./include/departamento.jsp" %>
		<div id="colDirCorpo">
		<!--  INÍCIO CONTEÚDO -->
		<div id="professores">
		<div id="titulo">
			<h1>Corpo Docente</h1>
		</div>
		<c:set var="docentess" value="#{portalPublicoDepartamento.docentes}"/>
		<c:set var="situacaoAnterior" value=""/>
		<c:set var="cargoAnterior" value=""/>
		<c:set var="contador" value="0"/>
		
		<c:if test="${not empty docentess}">
			<c:forEach var="docente" items="#{docentess}" varStatus="status">
					<c:if test="${docente.situacaoServidor.id != situacaoAnterior}">
						<c:if test="${!status.first}">
							<br clear="all"/>
						</c:if>
						<h2 style="line-height: 25px;">
							<c:choose>
								<c:when test="${docente.visitante}">
									Visitante
								</c:when>
								<c:when test="${docente.substituto}">
									Substituto
								</c:when>
								<c:otherwise>
								${docente.situacaoServidor.descricao}
								</c:otherwise>
							</c:choose>
						</h2>
						<c:set var="contador" value="0"/>
					</c:if>
				
				<table align="left" >
					<tr>
						<td class="foto">
							<c:choose>
								<c:when test="${not empty docente.idFoto && docente.idFoto>0}" >
									<img src="${ctx}/verFoto?idFoto=${docente.idFoto}&key=${ sf:generateArquivoKey(docente.idFoto) }" width="70" height="85"/>
								</c:when>
								<c:otherwise>
									<img src="${ctx}/img/no_picture.png" width="70" height="85"	/>
								</c:otherwise>
							</c:choose>&nbsp;
						</td>
						<td  class="descricao">
							
							<span class="nome">
							${docente.pessoa.nome} 	
								<c:if test="${not empty docente.formacao.tipoDescricaoTitulo}">
									(${docente.formacao.tipoDescricaoTitulo})
								</c:if>		
							</span>
							
							<span class="departamento">
								<c:choose>
									<c:when test="${empty docente.pessoa.perfil.formacao}">
										Formação não informada.
									</c:when>
									<c:otherwise>
										${docente.pessoa.perfil.formacaoResumida}								
									</c:otherwise>
								</c:choose>
							</span>
							<br clear="all"/>
							<c:if test="${not empty docente.pessoa.perfil.enderecoLattes}">
								<span class="enderecoLattes">
								<a href="${docente.pessoa.perfil.enderecoLattes}" target="_blank">
								 Currículo Lattes 
								</a>&nbsp;
								</span> 
							</c:if>
							<span class="pagina">
								<a title="Clique aqui para acessar a página pública deste docente" 
								href="${ctx}/public/docente/portal.jsf?siape=${docente.siape}" target="_blank"> 
								Ver página 
								</a>
							</span>
							
						</td>
					</tr>
			</table>
			<c:if test="${(docente.cargo.id == cargoAnterior && contador% 2 != 0)}">
				<br clear="all"/>
				
			</c:if>
			<c:set var="contador" value="${contador+1}"/>
			<c:set var="cargoAnterior" value="${docente.cargo.id}"/>
			<c:set var="situacaoAnterior" value="${docente.situacaoServidor.id}"/>
			</c:forEach>
		</c:if>
		<br clear="all">
		
		</div>
		<table class="listagem" width="100%">
		<tfoot>
				<td colspan="5"><b>${fn:length(docentess)} Docente(s) </b></td>
		</tfoot>
		</table>
		<!--  FIM CONTEÚDO  -->	
	</div>
	</f:view>
	<%@ include file="../include/rodape.jsp" %>