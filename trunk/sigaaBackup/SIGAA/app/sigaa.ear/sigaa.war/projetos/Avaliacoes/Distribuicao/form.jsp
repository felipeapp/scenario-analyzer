<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Distribui��o de projetos para avalia��o</h2>
	<br />
	<table class=formulario width="85%">
		<h:form id="form">
			<caption class="listagem">Cadastro de Distribui��es</caption>
			
			<tr>
				<th class="obrigatorio" width="30%">M�todo de Distribui��o:</th>
				<td>
	    	 		<h:selectOneMenu value="#{distribuicaoProjetoMbean.obj.metodo}" >
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItem itemValue="M" itemLabel="MANUAL" />
						<f:selectItem itemValue="A" itemLabel="AUTOM�TICA" />
 			 		</h:selectOneMenu>
	    	 	</td>
			</tr>

			<tr>
				<th class="obrigatorio">Tipo de Avaliador:</th>
				<td>
	    	 		<h:selectOneMenu value="#{distribuicaoProjetoMbean.obj.tipoAvaliador.id}" >
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{distribuicaoProjetoMbean.allTipoAvaliadorCombo}" />												
 			 		</h:selectOneMenu>
	    	 	</td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Modelo de avalia��o:</th>
				<td>
	    	 		<h:selectOneMenu value="#{distribuicaoProjetoMbean.obj.modeloAvaliacao.id}" onchange="this.form.submit();" 
	    	 			valueChangeListener="#{distribuicaoProjetoMbean.changeModeloAvaliacao}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{modeloAvaliacao.allCombo}" />
 			 		</h:selectOneMenu>
                    <a4j:status>
                       <f:facet name="start">
                         <h:graphicImage value="/img/indicator.gif">Carregando dados do question�rio.</h:graphicImage>
                       </f:facet>
	                </a4j:status>
	    	 	</td>
			</tr>

			<tr>
				<td colspan="2" class="subFormulario">Modelo do Formul�rio de Avalia��o</td>
			</tr>

			<tr>
				<td colspan="2">
				
						<h:dataTable id="questionario" value="#{distribuicaoProjetoMbean.obj.modeloAvaliacao.questionario.itensAvaliacao}" 
								var="item" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" 
								binding="#{avaliacaoAtividade.htmlDataTable}" rendered="#{not empty distribuicaoProjetoMbean.obj.modeloAvaliacao }">
								
							<t:column>
								<f:facet name="header"><h:outputText value="Pergunta" /></f:facet>
								<h:outputText value="#{item.pergunta.descricao}" />
							</t:column>
	
							<t:column>
								<f:facet name="header"><h:outputText value="Nota M�xima" /></f:facet>
								<h:outputText value="#{item.notaMaxima}">
									<f:convertNumber pattern="#0.00" />
								</h:outputText>
							</t:column>
	
							<t:column>
								<f:facet name="header"><h:outputText value="Peso" /></f:facet>
								<h:outputText value="#{item.peso}">
									<f:convertNumber pattern="#0.00" />
								</h:outputText>
							</t:column>
						</h:dataTable>
				
	    	 	</td>
			</tr>

			<tr>
				<td colspan="2" class="subFormulario">Mensagem para Notifica��o dos Avaliadores</td>
			</tr>

			<tr>
				<td colspan="2">
					<h:inputTextarea value="#{distribuicaoProjetoMbean.obj.msgNotificacaoAvaliadores}" style="width:99%" rows="10" id="parecer"/>
	    	 	</td>
			</tr>



			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{distribuicaoProjetoMbean.confirmButton}" action="#{distribuicaoProjetoMbean.cadastrar}" />
						<input type="button" value="<< Voltar" onclick="javascript:history.go(-1)" />
					 	<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{distribuicaoProjetoMbean.cancelar}" />
					 </td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
