<%-- Cabeçalho --%>
<%@ include file="include/cabecalho.jsp" %>

<f:view locale="#{portalPublicoPrograma.lc}">
<a4j:keepAlive beanName="portalPublicoCurso"/>
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>

<%-- topo --%>
<%@ include file="include/curso.jsp" %>
<%-- menu flutuante --%>
<%@ include file="include/menu.jsp" %>
	 
<%-- conteudo --%>
<div id="conteudo">
	
	<div class="titulo"><h:outputText value="#{idioma.calendario}"/></div>

			
			
			<c:set var="primeiroPeriodo" value="#{portalPublicoCurso.calendarioPrimeiroPeriodo}" />
			<c:set var="segundoPeriodo" value="#{portalPublicoCurso.calendarioSegundoPeriodo}" />
			<c:if test="${not empty primeiroPeriodo}">
			<div id="listagem_tabela">
				<div id="group_lt">
					${primeiroPeriodo.ano}.1
				</div>	
				<table class="table_lt">
					<tbody>
					<c:if test="${not empty primeiroPeriodo.inicioMatriculaOnline}">
							<tr>
							<td>
							<b><ufrn:format type="data" valor="${primeiroPeriodo.inicioMatriculaOnline}"/> à <ufrn:format type="data" valor="${primeiroPeriodo.fimMatriculaOnline}"/></b><br clear="all"/>
								&rarr;&nbsp;<h:outputText value="#{idioma.matricular}"/> ${primeiroPeriodo.ano}.1.
							</td>
							</tr>
						</c:if>
					<c:if test="${not empty primeiroPeriodo.inicioReMatricula}">
							<tr>
							<td>
							<b><ufrn:format type="data" valor="${primeiroPeriodo.inicioReMatricula}"/> à <ufrn:format type="data" valor="${primeiroPeriodo.fimReMatricula}"/></b><br clear="all"/>
								&rarr;&nbsp;<h:outputText value="#{idioma.rematricular}"/> ${primeiroPeriodo.ano}.1.
							</td>
							</tr>
						</c:if>
						<c:if test="${not empty primeiroPeriodo.inicioPeriodoLetivo}">
							<tr>
							<td>
								<b>	<ufrn:format type="data" valor="${primeiroPeriodo.inicioPeriodoLetivo}"/> </b><br clear="all"/>
								&rarr;&nbsp;<h:outputText value="#{idioma.inicioPeriodoLetivo}"/> ${primeiroPeriodo.ano}.1.
							</td>
							</tr>
						</c:if>
						<c:if test="${not empty primeiroPeriodo.inicioTrancamentoTurma}">
						<tr>
							<td>
								<b>
								<ufrn:format type="data" valor="${primeiroPeriodo.inicioTrancamentoTurma}"/></b><br clear="all"/>
								&rarr;&nbsp;<h:outputText value="#{idioma.inicioPeriodoTrancamento}"/> ${primeiroPeriodo.ano}.1.
							</td>
							</tr>
						</c:if>
						<c:if test="${not empty primeiroPeriodo.fimTrancamentoTurma}">
							<tr>
							<td>
								<b><ufrn:format type="data" valor="${primeiroPeriodo.fimTrancamentoTurma}"/> </b><br clear="all"/>
								&rarr;&nbsp;<h:outputText value="#{idioma.terminoPeriodoTrancamento}"/> ${primeiroPeriodo.ano}.1.
							</td>
							</tr>
						</c:if>
						<c:if test="${not empty primeiroPeriodo.fimPeriodoLetivo}">
							<tr>
							<td>
								<b>	<ufrn:format type="data" valor="${primeiroPeriodo.fimPeriodoLetivo}"/></b><br clear="all"/>
								&rarr;&nbsp;<h:outputText value="#{idioma.terminoPeriodoLetivo}"/> ${primeiroPeriodo.ano}.1.
							</td>
							</tr>
						</c:if>
						</tbody>	
						</table>
				</div>
			</c:if>			
			<c:if test="${not empty segundoPeriodo}">
			
				<div id="listagem_tabela">
					<div id="group_lt">
						${segundoPeriodo.ano}.2
					</div>	
					<table class="table_lt">
					<tbody>
					<c:if test="${not empty segundoPeriodo.inicioMatriculaOnline}">
							
							<tr>
							<td>
							<b><ufrn:format type="data" valor="${segundoPeriodo.inicioMatriculaOnline}"/> à <ufrn:format type="data" valor="${segundoPeriodo.fimMatriculaOnline}"/></b><br clear="all"/>
								&rarr;&nbsp;<h:outputText value="#{idioma.matricular}"/> ${segundoPeriodo.ano}.2.
							</td>
							</tr>
						</c:if>
					<c:if test="${not empty segundoPeriodo.inicioReMatricula}">
						<tr>
							<td>
							<b><ufrn:format type="data" valor="${segundoPeriodo.inicioReMatricula}"/> à <ufrn:format type="data" valor="${segundoPeriodo.fimReMatricula}"/></b><br clear="all"/>
								&rarr;&nbsp;<h:outputText value="#{idioma.rematricular}"/> ${segundoPeriodo.ano}.2.
							</td>
						</tr>
					</c:if>
					<c:if test="${not empty segundoPeriodo.inicioPeriodoLetivo}">
						<tr>
							<td>
								<b><ufrn:format type="data" valor="${segundoPeriodo.inicioPeriodoLetivo}"/> </b><br clear="all"/>
								&rarr;&nbsp;<h:outputText value="#{idioma.inicioPeriodoLetivo}"/> ${segundoPeriodo.ano}.2.
							</td>
						</tr>
					</c:if>
					<c:if test="${not empty segundoPeriodo.inicioTrancamentoTurma}">
						<tr>
							<td>
								<b>
								<ufrn:format type="data" valor="${segundoPeriodo.inicioTrancamentoTurma}"/></b><br clear="all"/>
								&rarr;&nbsp;<h:outputText value="#{idioma.inicioPeriodoTrancamento}"/> ${segundoPeriodo.ano}.2.
							</td>
						</tr>
					</c:if>
					<c:if test="${not empty segundoPeriodo.fimTrancamentoTurma}">
						<tr>
							<td>
								<b><ufrn:format type="data" valor="${segundoPeriodo.fimTrancamentoTurma}"/> </b><br clear="all"/>
								&rarr;&nbsp;<h:outputText value="#{idioma.terminoPeriodoTrancamento }"/> ${segundoPeriodo.ano}.2.
							</td>
						</tr>
					</c:if>
					<c:if test="${not empty segundoPeriodo.fimPeriodoLetivo}">
						<tr>
						<td>
						<b>	<ufrn:format type="data" valor="${segundoPeriodo.fimPeriodoLetivo}"/></b><br clear="all"/>
								&rarr;&nbsp;<h:outputText value="#{idioma.terminoPeriodoLetivo }"/> ${segundoPeriodo.ano}.2.
						</td>
						</tr>
					</c:if>
					</tbody>
					</table>
			</div>
			</c:if>	
				
</div>
		
<!--  FIM CONTEÚDO  -->	
</f:view>
<%@ include file="./include/rodape.jsp" %>