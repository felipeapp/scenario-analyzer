<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript">
	function fechar(){
		window.close();
	}
</script>

<f:view>

	<h:form id="form">
	
	<table class="formulario" style="width: 65%;">
		<caption>Questionário Sócio Econômico</caption>
			<tbody>
				<tr>
					<td style="text-align: left;"><%@include file="/geral/questionario/_respostas.jsp" %></td>
				</tr>
				<tr>
					<td class="subFormulario">Itens do conforto familiar</td>
				</tr>
				
				<tr>
					<td>
						<t:newspaperTable value="#{adesaoCadastroUnico.lista}" var="confortoFamiliar" newspaperColumns="2" 
									newspaperOrientation="horizontal" rowClasses="linhaPar, linhaImpar"  width="100%">					
					
							<t:column style="text-align: right;">
			                      <h:outputText value="#{confortoFamiliar.item.item}" />
		                    </t:column>
		                    
		                    <t:column>
		                    	<h:outputText value="#{confortoFamiliar.quantidade}"  style="font-style:italic" rendered="#{confortoFamiliar.quantidade > 0}"/>
		                    	<h:outputText value="Nenhum"  style="font-style:italic" rendered="#{confortoFamiliar.quantidade == 0}"/>
		                    </t:column>
		                </t:newspaperTable>
					</td>				
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td>
						<c:if test="${adesaoCadastroUnico.exibirBotaoVoltar}">
							<input type="button" value="<< Voltar" onclick="javascript:history.go(-1)"/>
						</c:if>
						<c:if test="${!adesaoCadastroUnico.exibirBotaoVoltar}">
							<h:commandButton value="Fechar" onclick="javascript:fechar();" />
						</c:if>
					</td>
				</tr>			
			</tfoot>
	</table>
	
	</h:form>
	
	
</f:view>

<c:set var="hideSubsistema" value="true" />

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>