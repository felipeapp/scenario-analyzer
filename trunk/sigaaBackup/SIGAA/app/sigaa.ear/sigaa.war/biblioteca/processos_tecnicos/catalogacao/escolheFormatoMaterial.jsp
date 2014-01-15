<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2>  <ufrn:subSistema /> &gt; Catalogação &gt; Formato do Material do Título</h2>
<f:view>

<h:outputText value="#{catalogacaoMBean.create}" />
<h:form id="form">

<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>

<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>

<%-- Usado quando o usuário vai escolher o formato do material dos títulos com catalogação incompleta.--%>
<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>

<div class="infoAltRem" style="width: 60%">
	<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:Selecionar Formato
</div>


<%-- Usado quando o usuário usa a tela de escolher o formato do material vindo da tela de catalogações não finalizadas --%>
<%-- Precisa atribuir o formato selecionado a esse título antes de iniciar a catalogação--%>
<c:if test="${requestScope.escolhendoFormatoDeTituloNaoFinalizado == true}">
	<t:saveState value="#{buscaCatalogacoesIncompletasMBean.tituloSelecionado}"></t:saveState>
</c:if>


<table class=formulario width="60%">
	<caption>Formato do Material</caption>
	
	<thead>
		<tr>
			<td>Formato do Material</td>
			<td width="1%"></td>
		</tr>
	</thead>
	
	<%-- Para otimizar, senão ele vai ficar realizando uma nova consulta para cada formato encontrado --%>
	<c:set var="formatosMateriais" value="${catalogacaoMBean.allFormatoMaterialComboBox}" scope="request" />
	
	<c:forEach items="#{formatosMateriais}" var="formato" varStatus="status">
		<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
			<td>${formato.descricaoCompleta}</td>
			<td>
			
				<%-- Começando o caso de uso onde digita tudo, então vai para a tela de escolher o formato do material, opção padrão, todos os parâmetros do request estão nulos --%>
				
				<c:if test="${ empty  requestScope.escolhendoFormatoMaterialTituloSemFormato 
							&& empty  requestScope.alterandoFormatoMaterialTitulo
							&& empty  requestScope.escolhendoFormatoDeTituloNaoFinalizadoImportacao}">
				
					<h:commandLink styleClass="noborder" title="Selecionar formato do título" action="#{catalogacaoMBean.submeteFormatoMaterialIniciandoCatalogacao}">
						<f:param name="idFormatoMaterial" value="#{formato.id}"/>
						<h:graphicImage value="/img/seta.gif"/>
					</h:commandLink>
				</c:if>
			
				<%-- 
				 % Aqui entra nos casos onde se chegou na página de catalogação e o título não possuía um formato, 
				 %    seja porque ele foi importado, ou a planilha não tinha formato, ou qualquer outro caso 
				 --%>
				<c:if test="${requestScope.escolhendoFormatoMaterialTituloSemFormato == true || requestScope.escolhendoFormatoDeTituloNaoFinalizadoImportacao == true}">
				
					<h:commandLink styleClass="noborder" title="Selecionar formato do título" action="#{catalogacaoMBean.submeteFormatoMaterialTituloSemFormato}">
						<f:param name="idFormatoMaterial" value="#{formato.id}"/>
						<h:graphicImage value="/img/seta.gif"/>
					</h:commandLink>
				
				</c:if>
				
				<c:if test="${requestScope.alterandoFormatoMaterialTitulo == true}">
					<h:commandLink styleClass="noborder" title="Selecionar formato do título" action="#{catalogacaoMBean.confirmaAlterarFormatoMaterialCatalogacao}">
						<f:param name="idNovoFormatoMaterial" value="#{formato.id}"/>
						<h:graphicImage value="/img/seta.gif"/>
					</h:commandLink>
				</c:if>
				
				
			</td>
		</tr>
	</c:forEach>
	
	<tfoot>
		<tr>
			<td colspan="2">
				<c:if test="${requestScope.escolhendoFormatoDeTituloNaoFinalizado == null 
				        && requestScope.escolhendoFormatoDeTituloNaoFinalizadoImportacao == null
				        && requestScope.alterandoFormatoMaterialTitulo == null}">
					<h:commandButton value="<< Voltar" action="#{pesquisaTituloCatalograficoMBean.telaPesquisaTitulo}" />
				</c:if>
				
				<c:if test="${requestScope.escolhendoFormatoDeTituloNaoFinalizado == true}">
					<h:commandButton value="<< Voltar" action="#{buscaCatalogacoesIncompletasMBean.iniciarBuscaTitulosIncompletos}" />
				</c:if>
				
				<c:if test="${requestScope.escolhendoFormatoDeTituloNaoFinalizadoImportacao == true}">
					<h:commandButton value="<< Voltar" action="#{buscaCatalogacoesIncompletasMBean.iniciarBuscaTitulosIncompletosImportacao}" />
				</c:if>
				
				<c:if test="${requestScope.alterandoFormatoMaterialTitulo == true}">
					<h:commandButton value="<< Voltar" action="#{catalogacaoMBean.telaDadosTituloCatalografico}" />
				</c:if>
				
				
				<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true"  action="#{catalogacaoMBean.cancelar}"/>
			</td>
		</tr>
	</tfoot>
	
</table>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
