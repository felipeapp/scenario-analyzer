<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2>  <ufrn:subSistema /> &gt; Cataloga��o &gt; Formato do Material do T�tulo</h2>
<f:view>

<h:outputText value="#{catalogacaoMBean.create}" />
<h:form id="form">

<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>

<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>

<%-- Usado quando o usu�rio vai escolher o formato do material dos t�tulos com cataloga��o incompleta.--%>
<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>

<div class="infoAltRem" style="width: 60%">
	<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:Selecionar Formato
</div>


<%-- Usado quando o usu�rio usa a tela de escolher o formato do material vindo da tela de cataloga��es n�o finalizadas --%>
<%-- Precisa atribuir o formato selecionado a esse t�tulo antes de iniciar a cataloga��o--%>
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
	
	<%-- Para otimizar, sen�o ele vai ficar realizando uma nova consulta para cada formato encontrado --%>
	<c:set var="formatosMateriais" value="${catalogacaoMBean.allFormatoMaterialComboBox}" scope="request" />
	
	<c:forEach items="#{formatosMateriais}" var="formato" varStatus="status">
		<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
			<td>${formato.descricaoCompleta}</td>
			<td>
			
				<%-- Come�ando o caso de uso onde digita tudo, ent�o vai para a tela de escolher o formato do material, op��o padr�o, todos os par�metros do request est�o nulos --%>
				
				<c:if test="${ empty  requestScope.escolhendoFormatoMaterialTituloSemFormato 
							&& empty  requestScope.alterandoFormatoMaterialTitulo
							&& empty  requestScope.escolhendoFormatoDeTituloNaoFinalizadoImportacao}">
				
					<h:commandLink styleClass="noborder" title="Selecionar formato do t�tulo" action="#{catalogacaoMBean.submeteFormatoMaterialIniciandoCatalogacao}">
						<f:param name="idFormatoMaterial" value="#{formato.id}"/>
						<h:graphicImage value="/img/seta.gif"/>
					</h:commandLink>
				</c:if>
			
				<%-- 
				 % Aqui entra nos casos onde se chegou na p�gina de cataloga��o e o t�tulo n�o possu�a um formato, 
				 %    seja porque ele foi importado, ou a planilha n�o tinha formato, ou qualquer outro caso 
				 --%>
				<c:if test="${requestScope.escolhendoFormatoMaterialTituloSemFormato == true || requestScope.escolhendoFormatoDeTituloNaoFinalizadoImportacao == true}">
				
					<h:commandLink styleClass="noborder" title="Selecionar formato do t�tulo" action="#{catalogacaoMBean.submeteFormatoMaterialTituloSemFormato}">
						<f:param name="idFormatoMaterial" value="#{formato.id}"/>
						<h:graphicImage value="/img/seta.gif"/>
					</h:commandLink>
				
				</c:if>
				
				<c:if test="${requestScope.alterandoFormatoMaterialTitulo == true}">
					<h:commandLink styleClass="noborder" title="Selecionar formato do t�tulo" action="#{catalogacaoMBean.confirmaAlterarFormatoMaterialCatalogacao}">
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
