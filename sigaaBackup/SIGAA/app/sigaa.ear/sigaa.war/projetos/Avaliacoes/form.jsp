<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<script type="text/javascript" src="/sigaa/javascript/consolidacao/consolidacao_visualizacao.js"></script>

<script type="text/javascript">
<!--
	function limitText(limitField, limitCount, limitNum) {
	    if (limitField.value.length > limitNum) {
	        limitField.value = limitField.value.substring(0, limitNum);
	    } else {
	        $(limitCount).value = limitNum - limitField.value.length;
	    }
	}
	
//-->
</script>


<f:view>
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	<h2><ufrn:subSistema /> > Avaliacação do Projeto</h2>
	<br>
	
	<h:form id="form">
	<div class="descricaoOperacao">
		<p>Senhor(a) Avaliador(a),</p>  
		<p>
	   		Através desta página você poderá avaliar um projeto atribuindo notas a itens pertencentes a determinadas categorias.
	   		Observe que cada item possui uma nota máxima que pode ser atribuída a ele. A nota total da avaliação, que corresponde ao somatório
	   		das notas dos itens, não poderá ultrapassar um valor máximo, que é 10. 
	   	</p>	   		   
	   
	</div>

	
	<table class="formulario" width="99%">
		<caption>Resumo do projeto</caption>
		<%@include file="/projetos/ProjetoBase/dados_projeto.jsp"%>
	</table>



	<table class="formulario" width="99%">
		<caption>Solicitação de Reconsideração</caption>
		<tr>
			<td>
				<h:dataTable id="reconsideracao" value="#{avaliacaoProjetoBean.obj.projeto.solicitacoesReconsideracao}" 
									var="reconsideracao" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
									
						<t:column>
							<f:facet name="header"><h:outputText value="Justificativa da Coordenação do Projeto" /></f:facet>
							<h:outputText value="#{reconsideracao.justificativa}" id="justificativa" />
						</t:column>
						
						<t:column>
							<f:facet name="header"><h:outputText value="Parecer do Comitê Interno" /></f:facet>
							<h:outputText value="#{reconsideracao.parecer}" id="parecer" />
						</t:column>
						
				</h:dataTable>
			</td>
		</tr>
		
		
		
	</table>


	<br/>
		<%-- <a4j:outputPanel id="tabelaNotas"> --%>
		<table class="formulario" width="100%">
			<caption>Avaliar Projeto</caption>				
			<tr>
				<td colspan="2">						
						<h:dataTable id="avaliacao" value="#{avaliacaoProjetoBean.obj.notas}" 
								var="nota" width="100%" 
								styleClass="listagem" rowClasses="linhaPar, linhaImpar" 
								binding="#{avaliacaoProjetoBean.uiData}">
								
							<f:facet name="caption">
								<h:outputText value="#{avaliacaoProjetoBean.obj.distribuicao.modeloAvaliacao.questionario.descricao}" id="descricao" />
							</f:facet>
								
							<t:column>
								<f:facet name="header"><h:outputText value="Descrição do Item Avaliado" /></f:facet>
								<h:outputText value="#{nota.itemAvaliacao.pergunta.descricao}" id="pergunta"/>
							</t:column>
	
	
							<t:column>
								<f:facet name="header"><h:outputText value="Nota" id="outNota"/></f:facet>
								<h:inputText value="#{nota.nota}" size="5" maxlength="5" style="text-align: right;" 
									binding="#{avaliacaoProjetoBean.htmlNota}" id="notaPergunta">
									<f:convertNumber pattern="#0.0" type="number" maxFractionDigits="2"/>
									<a4j:support immediate="true" action="#{avaliacaoProjetoBean.atualizarMedia}" event="onchange" reRender="mediaFinal,notaPergunta" />
								</h:inputText>
							</t:column>
		
							<t:column>
								<f:facet name="header"><h:outputText value="Máximo" /></f:facet>
								<p align="right">
									<h:outputText value="#{nota.itemAvaliacao.notaMaxima}" style="text-align: right;" id="maximo">
										<f:convertNumber pattern="#0.0" />
									</h:outputText>
								</p>
							</t:column>
	
							<t:column>
								<f:facet name="header"><h:outputText value="Peso" /></f:facet>
								<p align="right">
									<h:outputText value="#{nota.itemAvaliacao.peso}" style="text-align: right;" id="peso">
										<f:convertNumber pattern="#0.0" />
									</h:outputText>
								</p>
							</t:column>
							
						</h:dataTable>
				</td>
			</tr>
	
			<tr bgcolor="#C8D5EC">				
				<td colspan="2" align="center">
					<strong>Total: 
						<h:outputText id="mediaFinal" value="#{avaliacaoProjetoBean.obj.nota}">
		                	<f:convertNumber pattern="#0.0" />
		                </h:outputText>			
					</strong>			
				</td>
			</tr>
	
			<tr>
				<td>
					<br/>
					<h:outputText styleClass="obrigatorio" value="Parecer:" /> 
					<br/>
					<h:inputTextarea id="parecer" value="#{avaliacaoProjetoBean.obj.parecer}" rows="10" style="width:99%" 
					readonly="#{avaliacaoProjetoBean.readOnly}" onkeydown="limitText(this, countParecer, 2000);" onkeyup="limitText(this, countParecer, 2000);"/>
					<center>
					     Você pode digitar <input readonly type="text" id="countParecer" size="3" value="${2000 - fn:length(avaliacaoProjetoBean.obj.parecer) < 0 ? 0 : 2000 - fn:length(avaliacaoProjetoBean.obj.parecer)}"> caracteres.
					</center>
					
				</td>
			</tr>
			<tr><td colspan="2"></td></tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btAvaliarProjeto" value="Avaliar Projeto"action="#{avaliacaoProjetoBean.avaliar}" />
						<h:commandButton id="btVoltar" value="<< Voltar"action="#{avaliacaoProjetoBean.listarAvaliacoes}"/>
						<h:commandButton id="btCancelar" value="Cancelar" action="#{avaliacaoProjetoBean.cancelar}" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
	</table>
  <%-- </a4j:outputPanel> --%>
	
</h:form>
	<br />
	<center><h:graphicImage url="/img/required.gif"
		style="vertical-align: top;" /> <span class="fontePequena">
	Campos de preenchimento obrigatório. </span></center>
	<br />
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>