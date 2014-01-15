<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="constanteTraducaoMBean"/>
<h2> <ufrn:subSistema /> &gt; Lista de Constantes para Internacionalização</h2>
	
<h:form id="form">
	<table class="formulario" style="width: 60%">
	  <caption>Critérios de busca</caption>
 		<tbody>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{constanteTraducaoMBean.filtroEntidadeTraducao}" id="checkEntidade" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkEntidade" onclick="$('form:checkEntidade').checked = !$('form:checkEntidade').checked;">Entidade:</label></td>
				<td>
					<h:selectOneMenu id="entidade" value="#{constanteTraducaoMBean.entidadeTraducao.id}" onfocus="$('form:checkEntidade').checked = true;">
						<f:selectItem itemValue="0" itemLabel=" -- SELECIONE -- "  />
						<f:selectItems value="#{entidadeTraducaoMBean.allCombo}"/>
					</h:selectOneMenu>
				</td>	
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{constanteTraducaoMBean.filtroConstante}" id="checkConstante" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkConstante" onclick="$('form:checkConstante').checked = !$('form:checkConstante').checked;">Constante:</label></td>
				<td><h:inputText size="60" maxlength="100" value="#{constanteTraducaoMBean.constante}" onfocus="$('form:checkConstante').checked = true;"/></td>
			</tr>
	    </tbody>
	    <tfoot>
		    <tr>
				<td colspan="4">
					<h:commandButton value="Buscar" action="#{constanteTraducaoMBean.buscar}" id="buscar" />
					<h:commandButton value="Cancelar" action="#{constanteTraducaoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		    </tr>
		</tfoot>
	</table>
	
	<br /><br />
	
	<c:if test="${not empty constanteTraducaoMBean.resultadosBusca}">	
		
		<div class="infoAltRem">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar<br />
		</div>
		
		<table class="listagem">
		  <caption>Constantes para Internacionalização(s) Encontrado(s) (${fn:length(constanteTraducaoMBean.resultadosBusca)})</caption>
			<thead>
				<tr>
					<th width="30%">Entidade de Tradução</th>
					<th width="30%">Constante</th>
					<th width="30%">Valor</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="linha" items="#{constanteTraducaoMBean.resultadosBusca}" varStatus="status">
					<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td> ${ linha.entidade.nome } </td>
						<td> ${ linha.constante } </td>
						<td> ${ linha.valor } </td>
						<td width="2%" align="right">
							<h:commandLink action="#{constanteTraducaoMBean.selecionar}">
								<h:graphicImage value="/img/seta.gif" style="overflow: visible;" title="Selecionar"/>
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