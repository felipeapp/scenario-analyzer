<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2>   <ufrn:subSistema /> &gt; Cataloga��o &gt; 008 - Campos Fixos de Dados</h2>


<style type="text/css">
	.texto{ font-style: italic;}  /* para os texto de ajuda dos campos */	
	
	.alinhamentoDireita{ text-align: right}
	 
</style>

<div class="descricaoOperacao"> 
    P�gina de edi��o do campo cuja etiqueta � a 008. Esse campo � obrigrat�rio 
em todos os T�tulos Catalogr�ficos.
</div>

<f:view>
<h:form>

<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>

<%-- Mander os dados da pesquisa se o usu�rio clicar no bot�o voltar --%>
<a4j:keepAlive beanName="catalogaAutoridadesMBean"></a4j:keepAlive>

<%-- Para mandar os dados quando � importado v�rios t�tulo ou autoridades --%>
<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>

<c:if test="${fn:length(catalogacaoMBean.valoresImpar008) > 0 || fn:length(catalogacaoMBean.valoresPar008) > 0 }">

	<table width="90%" class="formulario">
		
		<c:if test="${catalogacaoMBean.tipoCatalogacaoBibliografica}">
			<caption>008( ${catalogacaoMBean.obj.formatoMaterial.descricaoCompleta} )</caption>
		</c:if>
	
		<c:if test="${catalogacaoMBean.tipoCatalogacaoAutoridade}">
			<caption>008 Autoridade </caption>
		</c:if>
	
		<tr>
	
				<%-- 1� colula da tabela somente as posicoes impares da lista --%>
			<td>
			
				<t:dataTable value="#{catalogacaoMBean.valoresImpar008}"  var="valor008Impar" rowIndexVar="indice" columnClasses="alinhamentoDireita">
					<h:column>
						<h:outputText value="#{valor008Impar.descricao}"/> ( <h:outputText value="#{valor008Impar.posicao}"/> ):
					</h:column>
					<h:column>
						<h:inputText size="5" maxlength="#{valor008Impar.maxLength}" value="#{valor008Impar.valorPadrao}" autocomplete="off">
							<f:attribute name="permiteEspacos" value="true"/>
						</h:inputText>
					</h:column>
				</t:dataTable>
			</td>
			
			<%-- 2� colula da tabela somente as posicoes pares da lista --%>
			<td>
			
			
				<t:dataTable value="#{catalogacaoMBean.valoresPar008}"  var="valor008Par" rowIndexVar="indice" columnClasses="alinhamentoDireita">
					<h:column>
						<h:outputText value="#{valor008Par.descricao}"/>  ( <h:outputText value="#{valor008Par.posicao}"/> ):
					</h:column>
					<h:column>
						<h:inputText size="5" maxlength="#{valor008Par.maxLength}" value="#{valor008Par.valorPadrao}" autocomplete="off">
							<f:attribute name="permiteEspacos" value="true"/>
						</h:inputText>
					</h:column>
				</t:dataTable>
			</td>
			
		</tr>
			
		<tfoot>
			<tr>
				<td colspan="3">
				
					<c:if test="${catalogacaoMBean.editandoCamposDeControle == false 
									&& catalogacaoMBean.adicionandoCamposDeControle == false }">
						<h:commandButton value="<< Voltar" action="#{catalogacaoMBean.voltarPaginaLider}" />
					</c:if>
				
					<c:if test="${catalogacaoMBean.editandoCamposDeControle == false && catalogacaoMBean.adicionandoCamposDeControle == false }">
						<%-- <h:commandButton value="<< Anterior" action="#{catalogacaoMBean.telaCampoControleLider}" >
							<f:setPropertyActionListener target="#{catalogacaoMBean.voltandoPaginaLider}" value="true" />
						</h:commandButton> --%>
						<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true" action="#{catalogacaoMBean.cancelar}" />
						<h:commandButton value="Pr�ximo >>" action="#{catalogacaoMBean.submeterCampo008}"/>
					</c:if>
					
					<c:if test="${catalogacaoMBean.editandoCamposDeControle == true || catalogacaoMBean.adicionandoCamposDeControle == true}">
						<h:commandButton value="<< P�gina Cataloga��o" action="#{catalogacaoMBean.submeterCampo008}">
						 	<f:setPropertyActionListener target="#{catalogacaoMBean.voltarPaginaCatalogacao}" value="true" />
						</h:commandButton>
					</c:if>
					
					<c:if test="${catalogacaoMBean.adicionandoCamposDeControle == true}">
						<h:commandButton value=" Adicionar Campo >>" action="#{catalogacaoMBean.submeterCampo008}"/>
					</c:if>
					
					<c:if test="${catalogacaoMBean.editandoCamposDeControle == true}">
						<h:commandButton value=" Atualizar Campo >>" action="#{catalogacaoMBean.submeterCampo008}"/>
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

	




<c:if test="${fn:length(catalogacaoMBean.valoresImpar008) == 0 && fn:length(catalogacaoMBean.valoresPar008) == 0 }">
	<table width="90%" class="formulario">
		<tr>
			<td style="color: red; text-align: center">
				<c:out value="Voc� n�o utilizou a maneira correta para chegar a esta p�gina." />
			</td>
		</tr>
		<tr style="text-align: center">
			<td> <a href="javascript:history.go(-1);"> << Voltar </a>   </td>
		</tr>
	</table>
</c:if>




</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>