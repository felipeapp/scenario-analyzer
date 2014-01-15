<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="cursoTecnicoMBean"/>
<h2> <ufrn:subSistema /> > Busca por Qualificação</h2>

<h:form id="form">
	<table class="formulario" style="width: 60%">
	  <caption>Informe os critérios da busca</caption>
 		<tbody>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{qualificacaoTecnicoMBean.filtroCurso}" id="checkCurso" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkCurso" onclick="$('form:checkCurso').checked = !$('form:checkCurso').checked;">Curso:</label></td>
				<td>
					<h:selectOneMenu value="#{qualificacaoTecnicoMBean.obj.cursoTecnico.id}" id="cursoTecnico" onchange="$('form:checkCurso').checked = true;">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{cursoTecnicoMBean.allCursoComModulo}" />
					</h:selectOneMenu>
				</td>				
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{qualificacaoTecnicoMBean.filtroDescricao}" id="checkDescricao" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkDescricao" onclick="$('form:checkDescricao').checked = !$('form:checkDescricao').checked;">Descrição:</label></td>
				<td><h:inputText size="50" maxlength="100" value="#{qualificacaoTecnicoMBean.obj.descricao}" onfocus="$('form:checkDescricao').checked = true;" 
				onkeyup="CAPS(this)"/></td>
			</tr>
	    </tbody>
	    <tfoot>
		    <tr>
				<td colspan="4">
					<h:commandButton value="Buscar" action="#{qualificacaoTecnicoMBean.buscar}" id="buscar" />
					<h:commandButton value="Cancelar" action="#{qualificacaoTecnicoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		    </tr>
		</tfoot>
	</table>
	
	<br /><br />
	
	<div class="infoAltRem">
			<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar 
	        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover 
	        <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar 
	</div>
	
	<table class="visualizacao" width="100%">
		<caption>Qualificações encontradas</caption>
		<thead>
			<tr>
				<th style="text-align: left;">Descrição</th>
				<th style="text-align: left;">Curso Técnico</th>
				<th></th>
				<th></th>
				<th></th>
			</tr>
		</thead>
		<c:forEach var="linha" items="#{qualificacaoTecnicoMBean.all}" varStatus="status">
			<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td style="text-align: left;">${linha.descricao}</td>
				<td style="text-align: left;">${linha.cursoTecnico.nome}</td>
				<td width="20">
					<h:commandLink action="#{qualificacaoTecnicoMBean.atualizar}" >
						<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar"/>
						<f:param name="id" value="#{linha.id}"/>
					</h:commandLink>
				</td>						
				<td width="20">
					<h:commandLink action="#{qualificacaoTecnicoMBean.remover}" onclick="#{confirmDelete}" >
						<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
						<f:param name="id" value="#{linha.id}"/>
					</h:commandLink>
				</td>						
				<td width="20">
					<h:commandLink action="#{qualificacaoTecnicoMBean.view}" >
						<h:graphicImage value="/img/view.gif" style="overflow: visible;" title="Visualizar"/>
						<f:param name="id" value="#{linha.id}"/>
					</h:commandLink>
				</td>						
			</tr>
		</c:forEach>
	</table>
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>