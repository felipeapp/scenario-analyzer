<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2>   <ufrn:subSistema /> &gt; Cataloga��o &gt; 006 - Campos Fixos de Dados</h2>


<style type="text/css">
	.texto{ font-style: italic;}  /* para os texto de ajuda dos campos */	 
	
	.alinhamentoDireita{ text-align: right}
	
</style>

<div class="descricaoOperacao"> 
    P�gina de edi��o do campo cuja etiqueta � a 006. Esse campo � obrigrat�rio 
em todos os T�tulos Catalogr�ficos.
</div>

<f:view>
<h:form>

<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>

<%-- Para mandar os dados quando � importado v�rios t�tulo ou autoridades --%>
<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>

<c:if test="${fn:length(catalogacaoMBean.valoresImpar006) > 0 || fn:length(catalogacaoMBean.valoresPar006) > 0 }">

	<table width="90%" class="formulario">
	
		<c:if test="${catalogacaoMBean.tipoCatalogacaoBibliografica}">
			<caption>006 ( ${catalogacaoMBean.obj.formatoMaterial.descricaoCompleta} )</caption>
		</c:if>
		
		<tr>
	
			<%-- 1� colula da tabela somente as posicoes impares da lista --%>
			<td>
				<t:dataTable value="#{catalogacaoMBean.valoresImpar006}"  var="valor006Impar" rowIndexVar="indice" columnClasses="alinhamentoDireita">
					<h:column>
						<h:outputText value="#{valor006Impar.descricao}"/>  ( <h:outputText value="#{valor006Impar.posicao}"/> ):
					</h:column>
					<h:column>
						<h:inputText size="5" maxlength="#{valor006Impar.maxLength}" value="#{valor006Impar.valorPadrao}" autocomplete="off">
							<f:attribute name="permiteEspacos" value="true"/>
						</h:inputText>
					</h:column>
				</t:dataTable>
			</td>
			
			<%-- 2� colula da tabela somente as posicoes pares da lista --%>
			<td>
				<t:dataTable value="#{catalogacaoMBean.valoresPar006}"  var="valor006Par" rowIndexVar="indice" columnClasses="alinhamentoDireita">
					<h:column>
						<h:outputText value="#{valor006Par.descricao}"/>  ( <h:outputText value="#{valor006Par.posicao}"/> ):
					</h:column>
					<h:column>
						<h:inputText size="5" maxlength="#{valor006Par.maxLength}" value="#{valor006Par.valorPadrao}" autocomplete="off">
							<f:attribute name="permiteEspacos" value="true"/>
						</h:inputText>
					</h:column>
				</t:dataTable>
			</td>
			
		</tr>
			
		<tfoot>
			<tr>
				<td colspan="3">
					
					<h:commandButton value="<< P�gina Cataloga��o" action="#{catalogacaoMBean.submeterCampo006}">
					 	<f:setPropertyActionListener target="#{catalogacaoMBean.voltarPaginaCatalogacao}" value="true" />
					</h:commandButton>
					
					
					<c:if test="${catalogacaoMBean.adicionandoCamposDeControle == true}">
						<h:commandButton value=" Adicionar Campo >>" action="#{catalogacaoMBean.submeterCampo006}"/>
					</c:if>
					
					<c:if test="${catalogacaoMBean.editandoCamposDeControle == true}">
						<h:commandButton value=" Atualizar Campo >>" action="#{catalogacaoMBean.submeterCampo006}"/>
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

<c:if test="${fn:length(catalogacaoMBean.valoresImpar006) == 0 && fn:length(catalogacaoMBean.valoresPar006) == 0 }">
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