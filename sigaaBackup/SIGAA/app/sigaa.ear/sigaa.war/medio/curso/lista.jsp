<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="cursoMedio"/>
<h2> <ufrn:subSistema /> &gt; Busca de Cursos</h2>
	
<h:form id="form">
	<table class="formulario" style="width: 60%">
	  <caption>Critérios de busca</caption>
 		<tbody>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{cursoMedio.filtroCodigo}" id="checkCodigo" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkCodigo" onclick="$('form:checkCodigo').checked = !$('form:checkCodigo').checked;">Cód. da ${ configSistema['siglaInstituicao'] }:</label></td>
				<td><h:inputText size="10" maxlength="20" value="#{cursoMedio.obj.codigo}" onfocus="$('form:checkCodigo').checked = true;" onkeyup="CAPS(this)"/></td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{cursoMedio.filtroNome}" id="checkNome" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkNome" onclick="$('form:checkNome').checked = !$('form:checkNome').checked;">Nome:</label></td>
				<td><h:inputText size="50" maxlength="100" value="#{cursoMedio.obj.nome}" onfocus="$('form:checkNome').checked = true;" onkeyup="CAPS(this)"/></td>
			</tr>
	    </tbody>
	    <tfoot>
		    <tr>
				<td colspan="4">
					<h:commandButton value="Buscar" action="#{cursoMedio.buscar}" id="buscar" />
					<h:commandButton value="Cancelar" action="#{cursoMedio.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		    </tr>
		</tfoot>
	</table>
	
	<br /><br />
	
<c:if test="${not empty cursoMedio.listaCursos}">	
	
<div class="infoAltRem">
		<html:img page="/img/view.gif" style="overflow: visible;"/>: Visualizar Curso
	<c:if test="${not acesso.pedagogico }">
		<html:img page="/img/alterar.gif" style="overflow: visible;"/>: Alterar Curso
		<html:img page="/img/delete.gif" style="overflow: visible;"/>: Remover Curso
	</c:if>
</div>
	
	<table class="listagem">
	  <caption>Cursos Encontrados (${fn:length(cursoMedio.listaCursos)})</caption>
		<thead>
			<tr>
				<th width="10%">Cód. da ${ configSistema['siglaInstituicao'] }</th>
				<th width="70%">Nome</th>
				<th width="70%">Situação</th>
				<th></th>
				<th></th>
				<th></th>
			</tr>
		</thead>
		<tbody>
		   <c:forEach var="linha" items="#{cursoMedio.listaCursos}" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td>${linha.codigo}</td>
					<td>${linha.nome}</td>
					<td>${linha.ativoToString}</td>
					<td width="2%" align="right">
						<h:commandLink action="#{cursoMedio.view}" >
							<h:graphicImage value="/img/view.gif" style="overflow: visible;" alt="Alterar" title="Visualizar Curso" />  
							<f:param name="id" value="#{linha.id}"/> 
						</h:commandLink>
					</td>
					<c:if test="${not acesso.pedagogico }">
						<td width="2%" align="right">
							<h:commandLink action="#{cursoMedio.atualizar}" >
								<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" alt="Alterar" title="Alterar Curso" />  
								<f:param name="id" value="#{linha.id}"/> 
							</h:commandLink>
						</td>
						<td width="2%" align="right">
							<h:commandLink action="#{cursoMedio.remover}" onclick="#{confirmDelete}">
								<h:graphicImage value="/img/delete.gif" style="overflow: visible;" alt="Remover" title="Remover Curso"/>
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