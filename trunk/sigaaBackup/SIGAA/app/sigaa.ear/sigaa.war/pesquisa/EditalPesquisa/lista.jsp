<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h:form id="formEditais">
	
		<h2> <ufrn:subSistema /> &gt; Editais Cadastrados</h2>
		<h:outputText value="#{editalPesquisaMBean.create}"/>
	
		<table class="formulario" width="30%">
			<caption>Buscar Edital de Pesquisa</caption>
				<tr>
					<td width="2%">
						<h:selectBooleanCheckbox value="#{editalPesquisaMBean.checkTipoEdital}" styleClass="noborder" id="checkTipoEd" />
					</td>
					<th style="text-align: left" width="130px"> 
					<label for="checkTipoEd" onclick="$('formEditais:checkTipoEd').checked = !$('formEditais:checkTipoEd').checked;">
					 Tipo Edital:</label></th>
					<td>
						<h:selectOneMenu value="#{editalPesquisaMBean.buscaTipoBolsa}" id="tiposEdital" 
						onchange="getEl('formEditais:checkTipoEd').dom.checked = true;">
							<f:selectItems value="#{editalPesquisaMBean.tiposEdital}"/>
						</h:selectOneMenu>
					</td>
				</tr>

		        <tr>
					<td width="2%">
						<h:selectBooleanCheckbox value="#{editalPesquisaMBean.checkAno}" styleClass="noborder" id="checkAnoEdital" />
					</td>
					<th style="text-align: left" width="130px"> 
					<label for="checkTipoEd" onclick="$('formEditais:checkAnoEdital').checked = !$('formEditais:checkAnoEdital').checked;">
					 Ano:</label></th>
		        	<td>
		            	<h:inputText id="ano" size="5" maxlength="4" value="#{editalPesquisaMBean.ano}" onkeyup="formatarInteiro(this)" 
		            		onchange="getEl('formEditais:checkAnoEdital').dom.checked = true;" />
		            </td>
		        </tr>

        	<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton id="buscar" value="Buscar" action="#{editalPesquisaMBean.buscar}" />
					 	<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}" action="#{editalPesquisaMBean.cancelar}" />
					 </td>
				</tr>
			</tfoot>

		</table>
	
		<br />
		
		<c:if test="${ not empty editalPesquisaMBean.resultadosBusca }">
		
		<div class="infoAltRem">
			<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
			<h:commandLink id="cadastrarEditar" action="#{editalPesquisaMBean.preCadastrar}" value="#{editalPesquisaMBean.confirmButton}"/>&nbsp;
			<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Edital&nbsp;
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Edital&nbsp;
			<h:graphicImage value="/img/seta.gif" 	style="overflow: visible;"/>: Enviar Arquivo do Edital&nbsp;
			<img src="/shared/img/icones/download.png" style="overflow: visible;"/>: Visualizar Arquivo do Edital&nbsp;
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Edital &nbsp;
		</div>
		
		<h:dataTable id="datatableEditais" value="#{editalPesquisaMBean.resultadosBusca}" var="editalPesquisa" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
			
			<f:facet name="caption">
				<h:outputText value="Lista de Editais de Pesquisa" />
			</f:facet>
	
			<t:column>
				<f:facet name="header">
					<f:verbatim>Descrição</f:verbatim>
				</f:facet>
				<h:outputText value="#{editalPesquisa.edital.descricao}" />
			</t:column>
			
			<t:column>
				<f:facet name="header">
					<f:verbatim>Cota</f:verbatim>
				</f:facet>
				<h:outputText value="#{editalPesquisa.cota.descricao}" />
			</t:column>
			
			<t:column>
				<f:facet name="header">
					<f:verbatim><center>Início Submissão</center></f:verbatim>
				</f:facet>
				<div style="text-align:center">
					<h:outputText value="#{editalPesquisa.edital.inicioSubmissao}">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</h:outputText>
				</div>
			</t:column>
			
			<t:column>
				<f:facet name="header">
					<f:verbatim><center>Fim Submissão</center></f:verbatim>
				</f:facet>
				<div style="text-align:center">
					<h:outputText value="#{editalPesquisa.edital.fimSubmissao}">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</h:outputText>
				</div>
			</t:column>
			
			<t:column>
				<f:facet name="header">
					<f:verbatim>Titulação mínima</f:verbatim>
				</f:facet>
				<h:outputText value="#{editalPesquisa.titulacaoMinimaCotasDescricao}" />
			</t:column>
			
			<t:column width="15" styleClass="centerAlign">
				<h:commandLink id="visualizar" title="Visualizar Edital" action="#{ editalPesquisaMBean.view }" immediate="true">
			        <f:param name="id" value="#{editalPesquisa.id}"/>
		    		<h:graphicImage url="/img/view.gif" />
				</h:commandLink>
			</t:column>
	
			<t:column width="15" styleClass="centerAlign">
				<h:commandLink id="alterar" title="Alterar Edital" action="#{ editalPesquisaMBean.alterar }" immediate="true">
			        <f:param name="id" value="#{editalPesquisa.id}"/>
		    		<h:graphicImage url="/img/alterar.gif" />
				</h:commandLink>
			</t:column>
			
			<t:column width="15" styleClass="centerAlign">
				<h:commandLink id="enviarArquivo" title="Enviar Arquivo do Edital" action="#{ editalPesquisaMBean.preEnviarAqruivo }" immediate="true">
			        <f:param name="id" value="#{editalPesquisa.id}"/>
		    		<h:graphicImage url="/img/seta.gif" />
				</h:commandLink>
			</t:column>
			
			<t:column width="15" styleClass="centerAlign">
				<h:commandLink id="visualizarArquivo" title="Visualizar Arquivo do Edital" action="#{ editalMBean.viewArquivo }" immediate="true">
			        <f:param name="id" value="#{editalPesquisa.edital.id}"/>
		    		<img src="/shared/img/icones/download.png" border="0" alt="Visualizar Arquivo do Edital" title="Visualizar Arquivo do Edital" />
				</h:commandLink>
			</t:column>
	
			<t:column width="15" styleClass="centerAlign">
				<h:commandLink id="remover" title="Remover Edital" action="#{ editalPesquisaMBean.preRemover }" immediate="true">
			        <f:param name="id" value="#{editalPesquisa.id}"/>
		    		<h:graphicImage url="/img/delete.gif" />
				</h:commandLink>
			</t:column>
	
		</h:dataTable>
		
		</c:if>
			
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>