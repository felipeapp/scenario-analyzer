<%@include file="/public/include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	table.listagem{width: 90%;}
	table.listagem caption{text-align: left;}
	table.listagem caption img{position:relative;float:left;padding:0px 3px;}
	table.listagem caption a.nomeDepartamento{position:relative;color:#FFF;display:block;float:left;}
	table.listagem caption a.nomeCentro{display:block;width:90%;float:left;color:#FFF;}
	table.listagem caption a.iconeCentro{display:block;width:22px;float:right;}
</style>

<h2> Centros/Unidades Especializadas da ${ configSistema['siglaInstituicao'] } </h2>

<f:view locale="#{portalPublicoCentro.lc}">
	
	<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
	
	<h:outputText value="#{portalPublicoCentro.create}"/>
	<c:set var="centros" value="${portalPublicoCentro.centros}" />
	
	<br clear="all"/>
	<table  class="listagem">
	<tr >
		<td class="legenda">
			<h:graphicImage url="#{ctx}/public/images/link.png" width="15px" height="15px" /><b>: Visualizar Página do Centro/Unidade Especializada</b>
			<h:graphicImage url="#{ctx}/img/view.gif" width="15px" height="15px" /><b>: Visualizar Portal Público</b>
		</td>	
	</tr>
	</table>
	<br clear="all"/>
	<c:set var="ultMunicipio" value=""/>
	<h:form id="formListagemCentros">
		<c:set var="contCentro" value="0"/>
		<c:forEach var="c" items="${centros}" varStatus="status">
			
			<%-- Início nome do Centro e localização --%>
			<c:if test="${ultCentro != c.nomeCentro}">
				
				<c:if test="${!status.first}">
				</tbody>
				</table>
					<br clear="all"/>
				</c:if>
			
				<table class="listagem">
					<thead>
						<caption>
							<a  href="#${c.nomeCentro}" id="${c.nomeCentro}" class="nomeCentro">
							&nbsp;${c.nomeCentro} - ${c.siglaCentro}
							</a>
							<a  class="iconeCentro" href="portal.jsf?lc=${portalPublicoCentro.lc}&id=${c.idCentro}" title="Visualizar Portal Público">
								<h:graphicImage url="#{ctx}/img/view.gif"/>
							</a>
							<c:if test="${not empty c.siteExtra}">
								<a  class="iconeCentro" href="${c.siteExtra}" title="Visualizar Página do Centro">
									<h:graphicImage url="#{ctx}/public/images/link.png"/>
								</a>
							</c:if>
						<br clear="all"/>
						</caption>
						<tr>
							<td class="subListagem" colspan="2">
								LOCALIZADO NO MUNICÍPIO DE 
								${c.municipioCentro}, 
								ENGLOBA OS SEGUINTES DEPARTAMENTOS:
							</td>
						</tr>
					</thead>
					
					<tbody>
					<c:set var="contCentro" value="${contCentro+1}"/>
				
			</c:if>
			<%-- Fim nome do Centro e localização -- %>
			
			<%-- Início nome do Departamento --%>
			<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td width="98%">&nbsp;&rsaquo;
					<a class="nomeDepartamento" href="/sigaa/public/departamento/portal.jsf?lc=${portalPublicoCentro.lc}
					&id=${c.idDepartamento}" style="link">${c.nomeDepartamento}</a>
				</td>
				<td class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<a class="nomeDepartamento" title="Clique aqui para acessar a página pública deste departamento" 
					href="/sigaa/public/departamento/portal.jsf?id=${c.idDepartamento}">
						<h:graphicImage url="/img/view.gif"/>
					</a>
				</td>
			</tr>
				
				<c:if test="${status.last}">
					</table>
					<br clear="all"/>
				</c:if>
			<c:set var="ultCentro" value="${c.nomeCentro}"/>
			<%-- Fim nome do Departamento --%>
				
		</c:forEach>

		<c:set var="contUnidade" value="0"/>
		<c:forEach var="u" items="${portalPublicoCentro.unidadesEspecializadas}" varStatus="status2">
			
			<c:if test="${ultUnidade != u.nomeUnidade}">
				
				<c:if test="${!status2.first}">
					</tbody>
					</table>
						<br clear="all"/>
				</c:if>

				<table class="listagem unidade">
					<thead>
						<caption>
							<a href="#${u.nomeUnidade}" id="${u.nomeUnidade}" class="nomeCentro">&nbsp;${u.nomeUnidade} - ${u.siglaUnidade}</a>
							<c:if test="${not empty u.siteExtra}">
								<a  class="iconeCentro" href="${u.siteExtra}" target="_blank" title="Visualiza Página da Unidade Especializada">
									<h:graphicImage url="#{ctx}/public/images/link.png"/>
								</a>
							</c:if>
						<br clear="all"/>
						</caption>
						<tr>
							<td class="subListagem" colspan="2">
								LOCALIZADO NO MUNICÍPIO DE 
								${u.municipioUnidade}, 
								ENGLOBA OS SEGUINTES DEPARTAMENTOS:
							</td>
						</tr>
					</thead>
					
					<tbody>
					<c:set var="contUnidade" value="${contUnidade+1}"/>
				
			</c:if>
			
			<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td width="98%">&nbsp;&rsaquo;
					<a class="nomeDepartamento" href="/sigaa/public/departamento/portal.jsf?lc=${portalPublicoCentro.lc}
					&id=${u.idUnidade}" style="link">DEPARTAMENTO DO(A) ${u.nomeUnidade}</a>
				</td>
				<td class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<a class="nomeDepartamento" title="Visualizar Portal Público" 
					href="/sigaa/public/departamento/portal.jsf?id=${u.idUnidade}">
						<h:graphicImage url="/img/view.gif"/>
					</a>
				</td>
			</tr>
			<c:set var="ultUnidade" value="${c.nomeUnidade}"/>

				
		</c:forEach>
		
	</h:form>
			
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3" align="center"> <b>${contCentro+contUnidade} Centros/Unidades Especializadas encontradas </b></td>
		</tr>
	</tfoot>
	</table>
	
	<br>
	<center>
		<a href="javascript: history.go(-1);"> << Voltar </a>
	</center>	
		
</f:view>
<%@include file="/public/include/rodape.jsp" %>