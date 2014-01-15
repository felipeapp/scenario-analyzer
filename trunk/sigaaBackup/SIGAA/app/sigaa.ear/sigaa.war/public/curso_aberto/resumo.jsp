<%@ include file="./include/cabecalho.jsp" %>
<f:view  locale="#{portalPublicoCurso.lc}">
<a4j:keepAlive beanName="consultaPublicaTurmas"/> 
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
<div id="centro">
	<h:form id="formResumoCursoAberto">
		<!-- VOLTAR -->
		<div id="menu-pri">
			<dl>
				<dt>
					<a href="javascript:void(-1);" onclick="history.go(-1);" class="centro">
					VOLTAR PARA A BUSCA
					</a>
				</dt>
			</dl>
		</div>

		<!-- INÍCIO CONTEÚDO -->
		<h4><h:outputText value="#{consultaPublicaTurmas.obj.disciplina.nome}"/> (<h:outputText value="#{consultaPublicaTurmas.obj.disciplina.codigo}"/>)</h4>
	
		<table class="visualizacao">
			<caption>Informações Gerais</caption>
			<tr>
				<th width="15%" align="right">Data da Oferta:</th>
				<td width="35%"	align="left">
					<h:outputText value="#{consultaPublicaTurmas.obj.ano}"/>.<h:outputText value="#{consultaPublicaTurmas.obj.periodo}"/>
				</td>
				<th class="docentes">Docente(s):</th>
			</tr>
			<tr>
				<th width="15%" align="right" valign="top">Ementa:</th>
				<td width="35%" align="left" valign="top">
					<h:outputText value="#{consultaPublicaTurmas.obj.disciplina.detalhes.ementa}"/>
				</td>
				<td class="docentes">
					<c:if test="${not empty consultaPublicaTurmas.obj.docentesTurmas }">
						<c:forEach items="#{consultaPublicaTurmas.obj.docentesTurmas}" var="_docenteTurma" varStatus="status">
							<div class="bloco">
								<div class="foto">
									<img height="85" width="70" src="/sigaa/img/no_picture.png">
								</div>
								<span class="nome">
									<h:outputText value="#{_docenteTurma.docente.pessoa.nome}" rendered="#{_docenteTurma.docenteExterno == null}" />
									<h:outputText value="#{_docenteTurma.docenteExterno.pessoa.nome}" rendered="#{_docenteTurma.docenteExterno != null}" />
								</span>
								<span><a href="../docente/portal.jsf?siape=${_docenteTurma.docente.siape}">Veja o perfil</a></span>
								<span>
								<a href="mailto:${_docenteTurma.docente.pessoa.email}" title="Entre em contato">
									Entre em contato
								</a>
								</span>
							</div>
						</c:forEach>
					</c:if>
				</td>
			</tr>	
		</table>

		<c:choose>	
			<c:when test="${empty consultaPublicaTurmas.conteudoMaterial}" >
				<%@include file="topicos.jsp" %>
			</c:when>
			<c:otherwise>
				<%@include file="material.jsp" %>
			</c:otherwise>
		</c:choose>

		<!--  FIM CONTEÚDO -->

	</h:form>

	<br clear="all"/>
</div> 
</f:view>
<%@ include file="../include/rodape.jsp" %>