<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="moduloIMDMBean"/>
<h2> <ufrn:subSistema /> > Listagem dos Módulos</h2>
	
<h:form id="form">
	<table class="formulario" style="width: 60%">
	  <caption>Informe os critérios da busca</caption>
 		<tbody>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{moduloIMDMBean.filtroCodigo}" id="checkCodigo" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkCodigo" onclick="$('form:checkCodigo').checked = !$('form:checkCodigo').checked;">Código:</label></td>
				<td><h:inputText size="10" value="#{moduloIMDMBean.codigo}" onfocus="$('form:checkCodigo').checked = true;" onkeyup="CAPS(this)"/></td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{moduloIMDMBean.filtroNome}" id="checkNome" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkNome" onclick="$('form:checkNome').checked = !$('form:checkNome').checked;">Nome:</label></td>
				<td><h:inputText size="40" value="#{moduloIMDMBean.descricao}" onfocus="$('form:checkNome').checked = true;" onkeyup="CAPS(this)"/></td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{moduloIMDMBean.filtroCurso}" id="checkCurso" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkCurso" onclick="$('form:checkCurso').checked = !$('form:checkCurso').checked;">Curso:</label></td>
				<td>
					<h:selectOneMenu value="#{moduloIMDMBean.idCurso}" onchange="$('form:checkCurso').checked = true;">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{moduloIMDMBean.cursosCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
	    </tbody>
	    <tfoot>
		    <tr>
				<td colspan="4">
					<h:commandButton value="Buscar" action="#{moduloIMDMBean.buscar}" id="buscar" />
					<h:commandButton value="Cancelar" action="#{moduloIMDMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		    </tr>
		</tfoot>
	</table>
	
	<br /><br />

<c:if test="${not empty moduloIMDMBean.listaModulos}">

<div class="infoAltRem">
	<html:img page="/img/view.gif" style="overflow: visible;"/>: Visualizar Módulo
	<html:img page="/img/alterar.gif" style="overflow: visible;"/>: Atualizar Módulo
	<html:img page="/img/delete.gif" style="overflow: visible;"/>: Remover Módulo
</div>
	
	<table class="listagem">
	  <caption>Módulos Encontrados (${ fn:length(moduloIMDMBean.listaModulos) }) </caption>
		<thead>
			<tr>
				<th width="10%" style="text-align: left;">Código</th>
				<th style="text-align: left;">Nome</th>
				<th width="10%" style="text-align: right;">Carga Horária</th>
				<th colspan="3"></th>
			</tr>
		</thead>
		<tbody>
		
		   <c:forEach var="linha" items="#{moduloIMDMBean.listaModulos}" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td style="text-align: left;">${linha.codigo}</td>
					<td style="text-align: left;">${linha.descricao}</td>
					<td style="text-align: right;">${linha.cargaHoraria} h</td>
					<td width="2%" align="right">
						<h:commandLink action="#{moduloIMDMBean.view}" >
							<h:graphicImage value="/img/view.gif" style="overflow: visible;" title="Visualizar Módulo" />  
							<f:param name="id" value="#{linha.id}"/> 
						</h:commandLink>
					</td>					
					<td width="2%" align="right">
						<h:commandLink action="#{moduloIMDMBean.atualizar}" >
							<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Atualizar Módulo" />  
							<f:param name="id" value="#{linha.id}"/> 
						</h:commandLink>
					</td>
					<td width="2%" align="right">
						<h:commandLink action="#{moduloIMDMBean.remover}" >
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover Módulo" />
							<f:param name="id" value="#{linha.id}"/>
						</h:commandLink>
					</td>
				</tr>
		   </c:forEach>
		</tbody>
	</table>
</c:if>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>