<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Gerencia da Presença dos Avaliadores do CIC</h2>
<h:outputText value="#{avaliadorCIC.create}" />
	<a4j:keepAlive beanName="avaliadorCIC"/>

	<div class="descricaoOperacao" align="center"> 
		Gerência das presenças para os avaliadores do Congresso de Iniciação Científica.
	</div>
	
	<h:form id="busca">
	<table class="formulario" width="70%">
		<caption>Busca por Avaliadores do CIC</caption>
		<tbody>
			<tr>
				<td></td>
				<td align="right" class="obrigatorio">Congresso: &nbsp;&nbsp;</td>
				<td>
					<h:selectOneMenu id="selectCongresso" value="#{avaliadorCIC.obj.congresso.id}">
						<f:selectItems value="#{avaliadorCIC.allCongressosCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{avaliadorCIC.filtroArea}" id="checkArea" styleClass="noborder"/> </td>
				<td width="22%"  align="right"><label for="checkArea" onclick="$('busca:checkArea').checked = !$('busca:checkArea').checked;">Área de Conhecimento:</label></td>
				<td>
					<h:selectOneMenu id="selectAreaConhecimentoCnpq"
						value="#{avaliadorCIC.obj.area.id}" style="width: 70%;"
						onfocus="$('busca:checkArea').checked = true;">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{area.allGrandesAreasCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td width="3%"></td>
				<td width="22%" align="right"><label for="checkTipo" onclick="$('busca:checkTipo').checked = !$('busca:checkTipo').checked;">Tipo:</label></td>
				<td> 
					<h:selectBooleanCheckbox id="checkAvaliadorResumo" value="#{avaliadorCIC.obj.avaliadorResumo}" 
						onclick="$('busca:checkTipo').checked = $('busca:checkAvaliadorResumo').checked || $('busca:checkAvaliadorApresentacao').checked;"/> 
					Avaliador de Resumo
					&nbsp;&nbsp;&nbsp;&nbsp;
				 	<h:selectBooleanCheckbox id="checkAvaliadorApresentacao" value="#{avaliadorCIC.obj.avaliadorApresentacao}" 
				 		onclick="$('busca:checkTipo').checked = $('busca:checkAvaliadorResumo').checked || $('busca:checkAvaliadorApresentacao').checked;"/> 
				 	Avaliador de Apresentação 
				</td>
					<td>
					<ufrn:help img="/img/ajuda.gif">Filtro utilizado para exibição apenas dos 
					docentes que se enquadram no tipo selecionado.</ufrn:help>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="4">
				<h:commandButton value="Buscar" action="#{avaliadorCIC.buscarAvaliador}" /> 
				<h:commandButton value="Cancelar" action="#{avaliadorCIC.cancelar}" onclick="#{confirm}" /></td>
			</tr>
		</tfoot>
	</table>
	<br />
	
	<c:if test="${not empty avaliadorCIC.avaliadoresCIC}">
		<table class=listagem>
			<caption class="listagem">Lista de Avaliadores Encontrados (${fn:length(avaliadorCIC.avaliadoresCIC)})</caption>
			<thead>
				<tr>
					<td width="5%"><h:selectBooleanCheckbox value="false" onclick="checkAll(this)"/></td>
					<td>Docente/Discente</td>
				</tr>
			</thead>
			<c:forEach items="#{avaliadorCIC.avaliadoresCIC}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td><h:selectBooleanCheckbox value="#{item.presenca}" styleClass="check" /></td> 
					<td>
						<h:outputText value="#{ item.docente.pessoa.nome  }" rendered="#{ item.docente.pessoa.nome !=null }" />
						<h:outputText value="#{ item.discente.pessoa.nome  }" rendered="#{ item.docente.pessoa.nome ==null && item.discente.pessoa.nome != null }" />
					</td>
				</tr>
			</c:forEach>
		</table>
	
		<table class="formulario" width="100%">
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton value="Cadastrar Presenças" action="#{avaliadorCIC.cadastrarPresenca}" /> 
				</tr>
			</tfoot>
		</table>
		
		</c:if>	
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<script type="text/javascript">
function checkAll(elem) {
	$A(document.getElementsByClassName('check')).each(function(e) {
		e.checked = elem.checked;
	});
}
</script>