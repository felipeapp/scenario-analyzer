<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2>  <ufrn:subSistema /> &gt; Catalogação &gt; Campo Líder</h2>

<c:set var="confirmVoltar" value="if (!confirm('Ao voltar para a página anterior os dados digitados nessa página serão perdidos?')) return false" scope="request" />

<style type="text/css">
	.texto{ font-style: italic;}  /* para os texto de ajuda dos campos */
	
	.alinhamentoDireita{ text-align: right}
		 
</style>



<div class="descricaoOperacao"> 
  Página de edição do campo Líder. Ele é obrigatório em todos os Títulos.
</div>

<f:view>
<h:outputText value="#{catalogacaoMBean.create}"/> 
<h:form>

<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>

<%-- Mander os dados da pesquisa se o usuário clicar no botão voltar --%>
<a4j:keepAlive beanName="catalogaAutoridadesMBean"></a4j:keepAlive>

<%-- Para mandar os dados quando é importado vários título ou autoridades --%>
<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>

<c:if test="${fn:length(catalogacaoMBean.valoresImparLider) > 0 || fn:length(catalogacaoMBean.valoresParLider) > 0 }">

	<table width="80%" class="formulario">
	
		<c:if test="${catalogacaoMBean.tipoCatalogacaoBibliografica}">
			<caption>Líder( ${catalogacaoMBean.obj.formatoMaterial.descricaoCompleta} )</caption>
		</c:if>
	
		<c:if test="${catalogacaoMBean.tipoCatalogacaoAutoridade}">
			<caption>Líder Autoridade </caption>
		</c:if>
	
		<tr>
		
			<%-- 1ª colula da tabela somente as posicoes impares da lista --%>
			<td>
				<t:dataTable value="#{catalogacaoMBean.valoresImparLider}"  var="valorLiderImpar" rowIndexVar="indice" columnClasses="alinhamentoDireita">
					<h:column>
						<h:outputText value="#{valorLiderImpar.descricao}"/> ( <h:outputText value="#{valorLiderImpar.posicao}"/> ):
					</h:column>
					<h:column>
						<h:inputText size="5" maxlength="1" value="#{valorLiderImpar.valorPadrao}" autocomplete="off">
							<f:attribute name="permiteEspacos" value="true"/>
						</h:inputText>
					</h:column>
				</t:dataTable>
			</td>
			
			<%-- 2ª colula da tabela somente as posicoes pares da lista --%>
			<td>
				<t:dataTable value="#{catalogacaoMBean.valoresParLider}"  var="valorLiderPar" rowIndexVar="indice" columnClasses="alinhamentoDireita">
					<h:column>
						<h:outputText value="#{valorLiderPar.descricao}"/> ( <h:outputText value="#{valorLiderPar.posicao}"/> ) :
					</h:column>
					<h:column>
						<h:inputText size="5" maxlength="1" value="#{valorLiderPar.valorPadrao}" autocomplete="off">
							<f:attribute name="permiteEspacos" value="true"/>
						</h:inputText>
					</h:column>
				</t:dataTable>
			</td> 		
			
		</tr>
	
		<tfoot>
			<tr>
				<td colspan="2">
				
					<c:if test="${catalogacaoMBean.editandoCamposDeControle == false 
									&& catalogacaoMBean.adicionandoCamposDeControle == false
										&& catalogacaoMBean.tipoCatalogacaoBibliografica }">
						<h:commandButton value="<< Voltar" action="#{catalogacaoMBean.telaEscolheFormatoMaterial}" onclick="#{confirmVoltar}"/>
					</c:if>
				
					<c:if test="${catalogacaoMBean.editandoCamposDeControle == false 
									&& catalogacaoMBean.adicionandoCamposDeControle == false
										&& catalogacaoMBean.tipoCatalogacaoAutoridade }">
						<h:commandButton value="<< Voltar" action="#{catalogaAutoridadesMBean.iniciar}" />
					</c:if>
					
					<c:if test="${catalogacaoMBean.editandoCamposDeControle == false && catalogacaoMBean.adicionandoCamposDeControle == false }">
						<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true" action="#{catalogacaoMBean.cancelar}" />
						<h:commandButton value="Próximo >>" action="#{catalogacaoMBean.submeterCampoLider}"/>
					</c:if>
					
					<c:if test="${catalogacaoMBean.editandoCamposDeControle == true || catalogacaoMBean.adicionandoCamposDeControle == true}">
						<h:commandButton value="<< Página Catalogação" action="#{catalogacaoMBean.submeterCampoLider}">
						 	<f:setPropertyActionListener target="#{catalogacaoMBean.voltarPaginaCatalogacao}" value="true" />
						</h:commandButton>
					</c:if>
					
					<c:if test="${catalogacaoMBean.adicionandoCamposDeControle == true}">
						<h:commandButton value=" Adicionar Campo >>" action="#{catalogacaoMBean.submeterCampoLider}"/>
					</c:if>
					
					<c:if test="${catalogacaoMBean.editandoCamposDeControle == true}">
						<h:commandButton value=" Atualizar Campo >>" action="#{catalogacaoMBean.submeterCampoLider}"/>
					</c:if>
					 
				</td>
			</tr>
		</tfoot>
	
	</table>


	<div style="margin-top: 30px">
	
		<rich:simpleTogglePanel id="panelAjudaDoCampo" switchType="ajax" label="Ajuda do campo" height="300px">
                  ${catalogacaoMBean.ajudaCampo}
    	</rich:simpleTogglePanel>
	</div>
	
</c:if>

</h:form>


<c:if test="${fn:length(catalogacaoMBean.valoresImparLider) == 0 && fn:length(catalogacaoMBean.valoresParLider) == 0 }">
	<table width="90%" class="formulario">
		<tr>
			<td style="color: red; text-align: center">
				<c:out value="Você não utilizou a maneira correta para chegar a esta página." />
			</td>
		</tr>
		<tr style="text-align: center">
			<td> <a href="javascript:history.go(-1);"> << Voltar </a>   </td>
		</tr>
	</table>
</c:if>
	
	
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>