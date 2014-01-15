<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="serie"/>
<h2> <ufrn:subSistema /> &gt; Busca de Séries</h2>
	
<h:form id="form">
	<table class="formulario" style="width: 60%">
	  <caption>Critérios de busca</caption>
 		<tbody>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{serie.filtroNumero}" id="checkNumero" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkCodigo" onclick="$('form:checkNumero').checked = !$('form:checkNumero').checked;">Número da Série:</label></td>
				<td><h:inputText size="2" maxlength="1" value="#{serie.obj.numero}" onfocus="$('form:checkNumero').checked = true;" onkeyup="return formatarInteiro(this);"/></td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{serie.filtroDescricao}" id="checkDescricao" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkDescricao" onclick="$('form:checkDescricao').checked = !$('form:checkDescricao').checked;">Descrição:</label></td>
				<td><h:inputText size="50" maxlength="100" value="#{serie.obj.descricao}" onfocus="$('form:checkDescricao').checked = true;" onkeyup="CAPS(this)"/></td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{serie.filtroCurso}" id="checkCurso" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkCurso" onclick="$('form:checkCurso').checked = !$('form:checkCurso').checked;">Curso:</label></td>
				<td>
					<h:selectOneMenu value="#{ serie.obj.cursoMedio.id }" onchange="$('form:checkCurso').checked = true;" style="width: 95%;" id="selectCurso">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{ cursoMedio.allCombo }" />
					</h:selectOneMenu>
				</td>	
			</tr>
	    </tbody>
	    <tfoot>
		    <tr>
				<td colspan="4">
					<h:commandButton value="Buscar" action="#{serie.buscar}" id="buscar" />
					<h:commandButton value="Cancelar" action="#{serie.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		    </tr>
		</tfoot>
	</table>
	
	<br /><br />
	
<c:if test="${not empty serie.listaSeries}">	
	
	<div class="infoAltRem">
		<html:img page="/img/alterar.gif" style="overflow: visible;"/>: Alterar
		<html:img page="/img/delete.gif" style="overflow: visible;"/>: Remover
	</div>
	
	<table class="listagem">
	  <caption>Séries Encontradas (${fn:length(serie.listaSeries)})</caption>
		<thead>
			<tr>
				<th width="30%">Curso</th>
				<th width="10%">Número</th>
				<th width="50%">Descrição</th>
				<th width="10%">Situação</th>
				<th></th>
				<th></th>
			</tr>
		</thead>
		<tbody>
		   <c:forEach var="linha" items="#{serie.listaSeries}" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td>${linha.nomeCurso}</td>
					<td>${linha.numeroSerieOrdinal}</td>
					<td>${linha.descricao}</td>
					<td>${linha.ativoToString}</td>
					<td width="2%" align="right">
						<h:commandLink action="#{serie.atualizar}" >
							<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar" />  
							<f:param name="id" value="#{linha.id}"/> 
						</h:commandLink>
					</td>
					<td width="2%" align="right">
						<h:commandLink action="#{serie.remover}" onclick="#{confirmDelete}">
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
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