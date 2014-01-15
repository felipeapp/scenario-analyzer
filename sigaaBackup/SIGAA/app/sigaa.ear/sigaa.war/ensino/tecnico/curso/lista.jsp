<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="cursoTecnicoMBean"/>
<h2> <ufrn:subSistema /> &gt; Busca de Cursos</h2>
	
<h:form id="form">
	<table class="formulario" style="width: 60%">
	  <caption>Critérios de busca</caption>
 		<tbody>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{cursoTecnicoMBean.filtroCodigo}" id="checkCodigo" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkCodigo" onclick="$('form:checkCodigo').checked = !$('form:checkCodigo').checked;">Cód. da ${ configSistema['siglaInstituicao'] }:</label></td>
				<td><h:inputText size="10" maxlength="20" value="#{cursoTecnicoMBean.obj.codigo}" onfocus="$('form:checkCodigo').checked = true;" onkeyup="CAPS(this)"/></td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{cursoTecnicoMBean.filtroNome}" id="checkNome" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkNome" onclick="$('form:checkNome').checked = !$('form:checkNome').checked;">Nome:</label></td>
				<td><h:inputText size="50" maxlength="100" value="#{cursoTecnicoMBean.obj.nome}" onfocus="$('form:checkNome').checked = true;" onkeyup="CAPS(this)"/></td>
			</tr>
	    </tbody>
	    <tfoot>
		    <tr>
				<td colspan="4">
					<h:commandButton value="Buscar" action="#{cursoTecnicoMBean.buscar}" id="buscar" />
					<h:commandButton value="Cancelar" action="#{cursoTecnicoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		    </tr>
		</tfoot>
	</table>
	
	<br /><br />
	
<c:if test="${not empty cursoTecnicoMBean.listaCursos}">	
	
<div class="infoAltRem">
		<html:img page="/img/view.gif" style="overflow: visible;"/>: Visualizar
		<c:if test="${not acesso.pedagogico}">
			<html:img page="/img/alterar.gif" style="overflow: visible;"/>: Atualizar
			<html:img page="/img/delete.gif" style="overflow: visible;"/>: Remover
		</c:if>
</div>
	
	<table class="listagem">
	  <caption>Cursos Encontrados (${fn:length(cursoTecnicoMBean.listaCursos)})</caption>
		<thead>
			<tr>
				<th width="10%">Cód. da ${ configSistema['siglaInstituicao'] }</th>
				<th width="70%">Nome</th>
				<th></th>
				<c:if test="${not acesso.pedagogico}">
				<th></th>
				<th></th>
				</c:if>
			</tr>
		</thead>
		<tbody>
		   <c:forEach var="linha" items="#{cursoTecnicoMBean.listaCursos}" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td>${linha.codigo}</td>
					<td>${linha.nome}</td>
					<td width="2%">
						<h:commandLink action="#{cursoTecnicoMBean.view}">
							<h:graphicImage value="/img/view.gif" style="overflow: visible;" title="Visualizar" />
							<f:param name="id" value="#{linha.id}"/>
						</h:commandLink>
					</td>
					<c:if test="${not acesso.pedagogico}">
					<td width="2%" align="right">
						<h:commandLink action="#{cursoTecnicoMBean.atualizar}" >
							<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Atualizar" />  
							<f:param name="id" value="#{linha.id}"/> 
						</h:commandLink>
					</td>
					<td width="2%" align="right">
						<h:commandLink action="#{cursoTecnicoMBean.remover}" onclick="#{confirmDelete}">
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
							<f:param name="id" value="#{linha.id}"/>
						</h:commandLink>
					</td>
					</c:if>
				</tr>
		   </c:forEach>
		</tbody>
	</table>
</c:if>	
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>