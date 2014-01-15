<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2>  <ufrn:subSistema /> &gt; Catalogação &gt; Campo Líder</h2>

<style type="text/css">
	.texto{ font-style: italic;}  /* para os texto de ajuda dos campos */	
	
	.alinhamentoDireita{ text-align: right} 
</style>

<div class="descricaoOperacao"> 
  Página de edição do campo 005.
</div>

<f:view>
<h:form>

<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>

<%-- Mander os dados da pesquisa se o usuário clicar no botão voltar --%>
<a4j:keepAlive beanName="catalogaAutoridadesMBean"></a4j:keepAlive>

<%-- Para mandar os dados quando é importado vários título ou autoridades --%>
<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>

<table width="90%" class="formulario">

	<c:if test="${catalogacaoMBean.tipoCatalogacaoBibliografica}">
		<caption>005 ( ${catalogacaoMBean.obj.formatoMaterial.descricaoCompleta} )</caption>
	</c:if>

	<c:if test="${catalogacaoMBean.tipoCatalogacaoAutoridade}">
		<caption>005 Autoridade </caption>
	</c:if>

	<tr>
		<th class="alinhamentoDireita">
		DATA E HORA DA ÚLTIMA INTERVENÇÃO (NR):
		</th>
		<td>
			<h:inputText value="#{catalogacaoMBean.dadosCamposControle}" autocomplete="off" size="60" maxlength="100">
				<f:attribute name="permiteEspacos" value="true"/>
			</h:inputText>				
		</td>
	</tr>

	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="<< Página Catalogação" action="#{catalogacaoMBean.submeterCampo005}">
				 	<f:setPropertyActionListener target="#{catalogacaoMBean.voltarPaginaCatalogacao}" value="true" />
				</h:commandButton>
				
				
				<c:if test="${catalogacaoMBean.adicionandoCamposDeControle == true}">
					<h:commandButton value=" Adicionar Campo >>" action="#{catalogacaoMBean.submeterCampo005}"/>
				</c:if>
				
				<c:if test="${catalogacaoMBean.editandoCamposDeControle == true}">
					<h:commandButton value=" Atualizar Campo >>" action="#{catalogacaoMBean.submeterCampo005}"/>
				</c:if>
				 
			</td>
		</tr>
	</tfoot>

</table>

</h:form>


	<div style="margin-top: 30px">
	
		<rich:simpleTogglePanel id="panelAjudaDoCampo" switchType="ajax" label="Ajuda do campo" height="300px">
                  ${catalogacaoMBean.ajudaCampo}
    	</rich:simpleTogglePanel>
	</div>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>