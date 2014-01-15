<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<f:view>

	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	
	<style>
		.etiqueta {
			width: 20%;
		}
		
		.indicador {
			width: 10%;
		}
		
		.acao {
			width: 2px;
		}
	</style>


	<h2><ufrn:subSistema /> > Cadastrar Planilha ${planilhaMBean.descricaoTipoPlanilha}</h2>
	<br>
	
	<h:form id="formularioPlanilha">
	
	
		<a4j:keepAlive beanName="planilhaMBean"></a4j:keepAlive>
	
	
		<div class="descricaoOperacao" style="width:85%">
			<p>Caro Usuário,</p>
			<p>Nesta tela deve-se digitar todos os dados MARC utilizados em um determinado tipo de catalogação.</p>
			<p>É preciso informar:
				 <ol>
				 	<li>O nome que identifica o tipo da planilha</li>
				 	<li>Formato de Material do MARC</li>
				 	<li>Rótulo dos Campos de Controle</li>
				 	<li>Todas as posições dos dados dos Campos de Controle</li>
				 	<li>Rótulo dos Campos de Dados</li>
				 	<li>Indicadores dos Campos de Dados</li>
				 	<li>Sub Campos dos Campos de Dados separados por espaço.</li>
				 </ol>
			</p>
		
			<br/>
			<p> <strong>Exemplo de Preenchimento:</strong> </p>
			
			<div style="font-family: monospace; margin-left: 10%; margin-right: 10%; font-size: 16px; width: 80%;">
				<br/>
				<span style="font-family: Verdana,sans-serif; font-size: 70%;">Campo de Controle:&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp </span> LDR	00000nam&nbspa2200000&nbspu&nbsp4500
				<br/><br/><br/>
				<span style="font-family: Verdana,sans-serif; font-size: 70%;">Campo de Dados:&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp </span>  245&nbsp&nbsp&nbsp1&nbsp&nbsp&nbsp1&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbspa&nbspb&nbspc&nbspd&nbsp
			</div>
			
		</div>

	
		<div class="infoAltRem" style="width:85%;">
			<img src="${ctx}/img/delete.gif">: Remover Campo
		</div>
	
	
		<table class="formulario" width="85%">
			<caption> ${planilhaMBean.confirmButton} Planilha</caption>

			<tr>
				<th class="obrigatorio">Nome:</th>
				<td><h:inputText value="#{planilhaMBean.obj.nome}" size="100" maxlength="100"/></td>
			</tr>
			<tr>
				<th>Descrição:</th>
				<td> <h:inputTextarea value="#{planilhaMBean.obj.descricao}" cols="100" rows="5" /></td>
			</tr>
			
			<c:if test="${planilhaMBean.obj.tipo == planilhaMBean.bibliografica}">
				<tr>
					<th class="obrigatorio">Formato:</th>
					<td><h:selectOneMenu id="formato"
						value="#{planilhaMBean.obj.idFormato}" readonly="#{planilhaMBean.readOnly}">
						<f:selectItem itemValue="-1" itemLabel="--- SELECIONE ---" />
						<f:selectItems value="#{planilhaMBean.formatosTituloCatalografico}" />
					</h:selectOneMenu></td>
				</tr>
			</c:if>
	
			<%-- Parte que adiciona os campos de controle --%>
			<tr>
				<td colspan="2">
				<table width="100%" class="subFormulario">
					<caption>Campos de Controle</caption>

					<thead>
						<tr>
							<a4j:region>
								<td>Rótulo: 
									<h:inputText id="tagEtiquetaBuscadaControle" value="#{planilhaMBean.tagControle}" size="5" maxlength="3" readonly="#{planilhaMBean.readOnly}" onkeyup="CAPS(this)" />
										
									<ajax:autocomplete source="formularioPlanilha:tagEtiquetaBuscadaControle"
											parameters="tipoEtiqueta=${planilhaMBean.obj.tipo},tipoCampo=C"
											target=""
											baseUrl="/sigaa/buscaEtiquetaAjax"
											className="autocomplete" minimumCharacters="1"
											parser="new ResponseXmlToHtmlListParser()" />
	
									Dado: 
									<h:inputText id="inputDadosControle" value="#{planilhaMBean.dadoControle}" readonly="#{planilhaMBean.readOnly}" size="60" maxlength="100">
										<f:attribute name="permiteEspacos" value="true"/>
									</h:inputText>
									
									<a4j:commandButton value="Adicionar Campo de Controle" actionListener="#{planilhaMBean.adicionarCampoControle}"
													reRender="camposControle,formularioPlanilha:mensagemErro" disabled="#{planilhaMBean.readOnly}" /> 
									<a4j:status>
										<f:facet name="start">
											<h:graphicImage value="/img/indicator.gif" />
										</f:facet>
									</a4j:status>
								
								</td>
							</a4j:region>
						</tr>
					</thead>
					<tr>
						<td><t:dataTable var="c" columnClasses="etiqueta, dado, acao"
							value="#{planilhaMBean.dmCamposControle}" id="camposControle"
							width="100%">

							<t:column>
								<f:facet name="header">
									<h:outputText value="Rótulo" />
								</f:facet>
								<h:outputText value="#{c.tagEtiqueta}" />
							</t:column>

							<t:column>
								<f:facet name="header">
									<h:outputText value="Dado" />
								</f:facet>
								<h:outputText value="#{c.dadoParaExibicao}" />
							</t:column>

							<t:column width="20">
								<f:facet name="header">
									<h:outputText value="" />
								</f:facet>
								<a4j:commandLink title="Remover"
									onclick="if (!confirm('Tem certeza que deseja remover este campo?')) return false;"
									actionListener="#{planilhaMBean.removerCampoControle}"
									reRender="camposControle">
									<h:graphicImage url="/img/delete.gif" />
								</a4j:commandLink>
							</t:column>
						</t:dataTable></td>
					</tr>
				</table>
				</td>
			</tr>
	
	
	
	
	
			<%-- Parte que adiciona os campos de dados  --%>
			<tr>
				<td colspan="2">
				<table width="100%" class="subFormulario">
					<caption>Campos de Dados</caption>

					<thead>
						<tr>
							<a4j:region>
								<td>Rótulo: <h:inputText
									id="tagEtiquetaBuscadaDados" value="#{planilhaMBean.tagDados}" size="5"
									maxlength="3" readonly="#{planilhaMBean.readOnly}"  onkeyup="CAPS(this)"/>
									
									<ajax:autocomplete
										source="formularioPlanilha:tagEtiquetaBuscadaDados" target=""
										parameters="tipoEtiqueta=${planilhaMBean.obj.tipo},tipoCampo=D"
										baseUrl="/sigaa/buscaEtiquetaAjax"
										className="autocomplete" minimumCharacters="1"
										parser="new ResponseXmlToHtmlListParser()" />
									
									Indicadores: 
										<h:inputText id="i1" value="#{planilhaMBean.indicador1}" readonly="#{planilhaMBean.readOnly}" size="1" maxlength="1" >
											<f:attribute name="permiteEspacos" value="true"/>
										</h:inputText> 
										<h:inputText id="i2" value="#{planilhaMBean.indicador2}" readonly="#{planilhaMBean.readOnly}" size="1" maxlength="1" >
											<f:attribute name="permiteEspacos" value="true"/>
										</h:inputText>
								
									Sub Campos: 
										<h:inputText value="#{planilhaMBean.subCampos}" readonly="#{planilhaMBean.readOnly}" >
											<f:attribute name="permiteEspacos" value="true"/>
										</h:inputText>
										
										<a4j:commandButton value="Adicionar Campo de Dados" actionListener="#{planilhaMBean.adicionarCampoDados}"
												reRender="camposDados,formularioPlanilha:mensagemErro" disabled="#{planilhaMBean.readOnly}" /> 
										<ufrn:help>Informe os sub campos do campo de dados separados por espaço. Exemplo: "a b c d e", criará um campo na planilha com cinco sub campos.</ufrn:help>
									<a4j:status>
										<f:facet name="start">
											<h:graphicImage value="/img/indicator.gif" />
										</f:facet>
									</a4j:status>
									
								</td>
							</a4j:region>
						</tr>
					</thead>
					
					<tr>
						<td>
							<t:dataTable var="d" columnClasses="etiqueta, indicador, indicador, dado, acao"
										value="#{planilhaMBean.dmCamposDados}" id="camposDados" width="100%">

							<t:column>
								<f:facet name="header">
									<h:outputText value="Rótulo" />
								</f:facet>
								<h:outputText value="#{d.tagEtiqueta}" />
							</t:column>
							<t:column>
								<f:facet name="header">
									<h:outputText value="Indicador 1" />
								</f:facet>
								<h:outputText value="#{d.indicador1}" />
							</t:column>
							<t:column>
								<f:facet name="header">
									<h:outputText value="Indicador 2" />
								</f:facet>
								<h:outputText value="#{d.indicador2}" />
							</t:column>
							<t:column>
								<f:facet name="header">
									<h:outputText value="Sub-Campos" />
								</f:facet>
								<h:outputText value="#{d.subCampos}" />
							</t:column>

							<t:column width="20">
								<f:facet name="header">
									<h:outputText value="" />
								</f:facet>
								<a4j:commandLink title="Remover"
									onclick="if (!confirm('Tem certeza que deseja remover este campo?')) return false;"
									actionListener="#{planilhaMBean.removerCampoDados}"
									reRender="camposDados">
									<h:graphicImage url="/img/delete.gif" />
								</a4j:commandLink>
							</t:column>
							
						</t:dataTable>
						
						</td>
					</tr>
				</table>
				</td>
			</tr>
				
			<tfoot>
				<tr>
					<td colspan="2" align="center">
					
						<h:commandButton value="#{planilhaMBean.confirmButton}" action="#{planilhaMBean.cadastrar}" id="acao" /> 
						
						<h:commandButton value="Cancelar" action="#{planilhaMBean.voltar}" onclick="#{confirm}" immediate="true" id="cancelar" />
						
					</td>
				</tr>
			</tfoot>
			
		</table>
			
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
			
	</h:form>
	
</f:view>




<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>