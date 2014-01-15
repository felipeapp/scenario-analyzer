<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="traducaoElementoMBean"/>
<h2> <ufrn:subSistema /> &gt; Listagem de ${traducaoElementoMBean.tituloOperacao}</h2>
	
<h:form id="form">
	<table class="formulario" style="width: 60%">
	  <caption>Critérios de busca</caption>
 		<tbody>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{traducaoElementoMBean.filtroBusca}" id="checkNome" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkNome" onclick="$('form:checkNome').checked = !$('form:checkNome').checked;">Nome:</label></td>
				<td><h:inputText size="60" maxlength="100" value="#{traducaoElementoMBean.campoBusca}" onfocus="$('form:checkNome').checked = true;"/></td>
			</tr>
	    </tbody>
	    <tfoot>
		    <tr>
				<td colspan="4">
					<h:commandButton value="Buscar" action="#{traducaoElementoMBean.buscar}" id="buscar" />
					<h:commandButton value="Cancelar" action="#{traducaoElementoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		    </tr>
		</tfoot>
	</table>
	
	<br /><br />
	
	<c:if test="${not empty traducaoElementoMBean.resultadosBusca}">	
		
		<div class="infoAltRem">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar<br />
		</div>
		
		<table class="listagem">
		  <caption>${traducaoElementoMBean.tituloOperacao}(s) Encontrado(s) (${fn:length(traducaoElementoMBean.resultadosBusca)})</caption>
			<thead>
				<tr>
					<th width="90%">Nome</th>
					<th width="5%"><h:outputLabel value="Ativo" rendered="#{traducaoElementoMBean.hasCampoAtivo}" /></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="linha" items="#{traducaoElementoMBean.resultadosBusca}" varStatus="status">
					<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td> 
							${ traducaoElementoMBean.atributoDescritivo eq 'nome' ? linha.nome : 
							   traducaoElementoMBean.atributoDescritivo eq 'denominacao' ? linha.denominacao :
							   traducaoElementoMBean.atributoDescritivo eq 'observacao' ? linha.observacao :
							   linha.descricao }
						</td>
						<td><ufrn:format type="simnao" valor="${traducaoElementoMBean.hasCampoAtivo ? linha.ativo : null}" /></td>
						<td width="2%" align="right">
							<h:commandLink action="#{traducaoElementoMBean.selecionar}">
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