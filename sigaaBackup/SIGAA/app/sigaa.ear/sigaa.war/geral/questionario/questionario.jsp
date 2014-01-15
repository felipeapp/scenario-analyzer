<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<link href="/sigaa/css/ensino/questionarios.css" rel="stylesheet" type="text/css" />

<f:view>
	
	<h2 class="title">  <ufrn:subSistema/> &gt; Questionário &gt; Visualização</h2>

	<h:form id="formQuestionario">
	<div class="infoAltRem">
		<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/><h:commandLink value="Nova Pergunta" action="#{questionarioBean.iniciarAdicionarPergunta}" />
		<h:graphicImage value="/img/prodocente/cima.gif"style="overflow: visible;"/>
        / <h:graphicImage value="/img/prodocente/baixo.gif" style="overflow: visible; margin-left: 0.3em;"/>
		: Mover pergunta para cima ou para baixo
        <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar pergunta
        <h:graphicImage value="/img/garbage.png" style="overflow: visible;"/>: Remover pergunta
	</div>

	
	<table class="formulario" width="100%">
		<caption>Resumo do Questionário</caption>
		<tr>
			<th width="20%"><b>Tipo de Questionário:</b></th>
			<td>${questionarioBean.obj.tipo}</td>
		</tr>
		<tr>
			<th><b>Título:</b></th>
			<td>${questionarioBean.obj.titulo}</td>
		</tr>
		
		<c:if test="${questionarioBean.obj.necessarioPeriodoPublicacao}">
		<tr>
			<th><b>Disponível de:</b></th>
			<td> 
				<ufrn:format type="data" valor="${questionarioBean.obj.inicio}" /> 
				a <ufrn:format type="data" valor="${questionarioBean.obj.fim}" />
			</td>
		</tr>
		</c:if>	

		<tr><td colspan="2">
			<rich:dataTable var="pergunta" value="#{questionarioBean.modelPerguntas}" width="100%" styleClass="listagem" 
				id="dataTableQuestionario" rowKeyVar="row">
			
				 <f:facet name="header">
	                 <rich:columnGroup>
	                     <rich:column colspan="2">
	                         <h:outputText value="Questionário" />
	                     </rich:column>
	                 </rich:columnGroup>
                 </f:facet>
			
				<rich:column rendered="#{pergunta.ativo}">
				
					<h:outputText value="#{row + 1}. #{pergunta.pergunta}" styleClass="pergunta" /> 
					
					<h:panelGroup rendered="#{pergunta.vf}">
						<rich:panel>
							<h:outputText value="#{pergunta.gabaritoVf == null ? 'Verdadeiro / Falso' : (pergunta.gabaritoVf ? 'Verdadeiro' : 'Falso?')}"/>
						</rich:panel>
					</h:panelGroup>
					
					<h:panelGroup rendered="#{pergunta.dissertativa}">
						<rich:panel>
							<h:outputText value="#{empty pergunta.gabaritoDissertativa ? 'Resposta Dissertativa' : pergunta.gabaritoDissertativa}"/>
						</rich:panel>
					</h:panelGroup>
					
					<h:panelGroup rendered="#{pergunta.numerica}">
						<rich:panel>
							<h:outputText value="#{empty pergunta.gabaritoNumerica ? 'Resposta Numérica' : pergunta.gabaritoNumerica}"/>
						</rich:panel>
					</h:panelGroup>
					
					<h:panelGroup rendered="#{pergunta.arquivo}">
						<rich:panel>
							<h:outputText value="Arquivo Anexo"/>
						</rich:panel>
					</h:panelGroup>
				
				</rich:column>
				
				<rich:column width="65px" style="vertical-align: top; padding-top: 12px;" rendered="#{pergunta.ativo}">
					<a4j:commandButton id="cima" image="/img/prodocente/cima.gif" title="Mover para cima" 
						actionListener="#{questionarioBean.movePerguntaCima}" 
						reRender="dataTableQuestionario" styleClass="noborder"/>
						
					<a4j:commandButton id="baixo" image="/img/prodocente/baixo.gif" title="Mover para baixo" 
						actionListener="#{questionarioBean.movePerguntaBaixo}" 
						reRender="dataTableQuestionario"  styleClass="noborder"/>
						
					<h:commandButton id="alterarItem" image="/img/alterar.gif" title="Alterar Pergunta" 
						action="#{questionarioBean.alterarPergunta}" styleClass="noborder"/>
						
					<a4j:commandButton id="removerItem" image="/img/garbage.png" title="Remover Pergunta" 
						actionListener="#{questionarioBean.removerPergunta}" onclick="if (!confirm('Confirma a remoção desta informação?')) return false;"
						reRender="dataTableQuestionario" styleClass="noborder"/>
				</rich:column>
						
				
				<rich:subTable var="alternativa" value="#{pergunta.alternativas}" rendered="#{pergunta.ativo && (pergunta.unicaEscolha || pergunta.unicaEscolhaAlternativaPeso)}" rowClasses="alternativa" >
					<rich:column styleClass="alternativa" rendered="#{alternativa.ativo}">
						<span class="radio <h:outputText value="#{alternativa.gabarito ? 'marcado' : ''}"/>">
						<h:outputText value="#{alternativa.letraAlternativa} #{alternativa}"/>
						</span>
					</rich:column>
                </rich:subTable>
			
			
				<rich:subTable var="alternativa" value="#{pergunta.alternativas}" rendered="#{pergunta.ativo && pergunta.multiplaEscolha}"  rowClasses="alternativa">
					<rich:column styleClass="alternativa" rendered="#{alternativa.ativo}">
						<span class="checkbox <h:outputText value="#{alternativa.gabarito ? 'marcado' : ''}"/>">
							<h:outputText value="#{alternativa.letraAlternativa} #{alternativa}"/>
						</span>
					</rich:column>
                </rich:subTable>
			
			</rich:dataTable>
		
		</td></tr>
		
		<tfoot>
			<tr><td colspan="2">
				<h:commandButton value="#{questionarioBean.confirmButton}" action="#{questionarioBean.cadastrar}" id="btnCadastrar"/>
				<h:commandButton value="<< Editar Dados Gerais" action="#{questionarioBean.telaDadosGerais}" id="btnDadosGerais"/>
				<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{questionarioBean.cancelar}" id="btnCancelar"/>
			</td></tr>
		</tfoot>
	
	</table>
	
	</h:form>
	
	<br>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
