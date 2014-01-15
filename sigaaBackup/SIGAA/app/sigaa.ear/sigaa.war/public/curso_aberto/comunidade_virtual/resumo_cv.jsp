<%@ include file="../include/cabecalho.jsp" %>
<f:view  locale="#{portalPublicoCurso.lc}">

<a4j:keepAlive beanName="consultaPublicaTurmas"/> 

<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
<div id="centro">
	<h:form id="formResumoCV">
		<!-- VOLTAR -->
		<div id="menu-pri">
			<dl>
				<dt>
					<h:commandLink styleClass="centro" value="VOLTAR PARA A BUSCA" action="#{consultaPublicaTurmas.buscarCursosAbertos}" >
					</h:commandLink>
				</dt>
			</dl>
		</div>

		<!-- INÍCIO CONTEÚDO -->
		<table class="visualizacao">
			<caption>Informações Gerais</caption>
			<tr>
				<th>
					<h2>
						Comunidade:
					</h2>
				</th>
				<td style="font-family: Verdana,sans-serif;font-weight:bold; text-transform: uppercase;">
					<h:outputText value="#{consultaPublicaTurmas.comunidadeVirtual.nome}"/> 
				</td>
			</tr>
			<tr>
				<th>
					<h2>
						Descrição:
					</h2>
				</th>
				<td style="font-family: Verdana,sans-serif;font-weight:bold; text-transform: uppercase;">
					<h:outputText value="#{consultaPublicaTurmas.comunidadeVirtual.descricao}"/>
				</td>
			</tr>	
		</table>
		
		<br clear="all">
		<c:choose>	
			<c:when test="${empty consultaPublicaTurmas.conteudoMaterial}" >
				<%@include file="topicos_cv.jsp" %>
			</c:when>
			<c:otherwise>
				<%@include file="material_cv.jsp" %>
			</c:otherwise>
		</c:choose>	

	</h:form>

	<br clear="all"/>
</div> 
</f:view>
<%@ include file="/public/include/rodape.jsp" %>