<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<f:view>
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	<h2><ufrn:subSistema /> > Avaliaca��o da A��o de Extens�o</h2>
	<br>
	
	<h:form id="formAvaliacaoAtividade">
	<div class="descricaoOperacao">
		<p>Senhor(a) Avaliador(a),</p>  
		<p>
	   		Atrav�s desta p�gina voc� poder� avaliar uma a��o de extens�o atribuindo notas a itens pertencentes a determinadas categorias.
	   		Observe que cada item possui uma nota m�xima que pode ser atribu�da a ele. A nota total da avalia��o, que corresponde ao somat�rio
	   		das notas dos itens, n�o poder� ultrapassar um valor m�ximo, que � 10.
	   	</p>	   		   
	   
	</div>

	<%@include file="/extensao/Atividade/include/dados_avaliar_atividade.jsp"%>
		
 	<br/> 

	
	<a4j:outputPanel id="tabelaNotas">
		<table class="formulario" width="100%">
			<caption>Avaliar Projeto de Extens�o</caption>				
			<tr>
				<td colspan="2">						
						<h:dataTable id="_avaliacao" value="#{avaliacaoAtividade.obj.notasItem}" 
								var="nota" width="100%" 
								styleClass="listagem" rowClasses="linhaPar, linhaImpar" 
								binding="#{avaliacaoAtividade.htmlDataTable}">
								
							<t:column>
								<f:facet name="header"><h:outputText value="Descri��o do Item Avaliado" /></f:facet>
								<h:outputText value="#{nota.itemAvaliacao.descricao}" />
							</t:column>
	
							<t:column>
								<f:facet name="header"><h:outputText value="Nota" /></f:facet>
								<h:inputText value="#{nota.nota}" size="5" maxlength="5" style="text-align: right;" 
									binding="#{avaliacaoAtividade.htmlNota}" id="notaPergunta">
									<f:convertNumber pattern="#0.00" type="number" maxFractionDigits="2"/>
									<a4j:support immediate="true" action="#{avaliacaoAtividade.atualizarMedia}"	event="onchange" reRender="mediaFinal,notaPergunta" />
								</h:inputText>
							</t:column>
	
							<t:column>
								<f:facet name="header"><h:outputText value="M�ximo" /></f:facet>
								<h:outputText value="#{nota.itemAvaliacao.notaMaxima}" style="text-align: right;">
									<f:convertNumber pattern="#0.00" />
								</h:outputText>
							</t:column>
	
							<t:column>
								<f:facet name="header"><h:outputText value="Peso" /></f:facet>
								<h:outputText value="#{nota.itemAvaliacao.peso}" style="text-align: right;">
									<f:convertNumber pattern="#0.00" />
								</h:outputText>
							</t:column>
						</h:dataTable>
				</td>
			</tr>
	
			<tr bgcolor="#C8D5EC">				
				<td colspan="2" align="center">
					<strong>Total: 
						<h:outputText id="mediaFinal" value="#{avaliacaoAtividade.obj.nota}">
		                	<f:convertNumber pattern="#0.00" />
		                </h:outputText>			
					</strong>			
				</td>
			</tr>
	
			<tr>
				<td>
					<br />  
					Parecer: 
					<h:selectOneRadio id="rbParecer" value="#{avaliacaoAtividade.obj.parecer.id}">
						<f:selectItem itemValue="1" itemLabel="Aprovar proposta" />
						<f:selectItem itemValue="3" itemLabel="N�O aprovar proposta" />
					</h:selectOneRadio>
				</td>
			</tr>	

			<tr>
				<td>
					<br />
					Justificativa: <br/>
					<h:inputTextarea value="#{avaliacaoAtividade.obj.justificativa}" style="width:98%" rows="4" id="parecer"/>
				</td>
			</tr>
			<tr><td colspan="2"></td></tr>
			<tfoot>
				<tr>
					<td colspan="2">
					<h:commandButton id="btAvaliarProjeto" value="Avaliar Projeto"action="#{avaliacaoAtividade.avaliarPareceristaAdHoc}" />
					<h:commandButton id="btCancelar" value="Cancelar" action="#{avaliacaoAtividade.cancelar}" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
	</table>
  </a4j:outputPanel>
	
</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>