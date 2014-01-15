<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h:form id="formAtividade">
	
			<h2> <ufrn:subSistema /> &gt; Lista de Notificações de Invenção</h2>
			<h:outputText value="#{invencao.create}"/>
			
			<table class="formulario" style="width:80%;">
				<caption>Informe os critérios de busca</caption>
				<tbody>
				
					<tr>
						<td>
							<h:selectBooleanCheckbox id="checkUsuario" value="#{invencao.buscaUsuario}" styleClass="noborder"/>
						</td>
						<th style="text-align: left"> <label for="checkUsuario"
							onclick="javascript:$('formAtividade:checkUsuario').checked = true;">Usuário:</label></th>
						<td>
							<h:inputText id="nomeUsuario" value="#{invencao.loginUsuario}" size="60" maxlength="60"
								onfocus="getEl('form:checkUsuario').dom.checked = true; " />
							<ajax:autocomplete
							source="formAtividade:nomeUsuario" target="formAtividade:buscaUsuario"
							baseUrl="/sigaa/ajaxServidor" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
							parser="new ResponseXmlToHtmlListParser()" />
							<span id="indicator" style="display:none;"> <img src="/sigaa/img/indicator.gif" /> </span>
						</td>
					</tr>
				
					<tr>
						<td>
							<h:selectBooleanCheckbox id="checkSituacao" value="#{invencao.buscaSituacao}" styleClass="noborder"/>
						</td>
						<th style="text-align: left"> <label for="checkSituacao"
						onclick="javascript:$('formAtividade:checkSituacao').checked = true;">Situação:</label></th>
						<td> 
							<h:selectOneMenu id="statusInvencao" value="#{invencao.obj.status}" onfocus="getEl('form:checkSituacao').dom.checked = true;">
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
								<f:selectItems value="#{invencao.situacaoCombo}"/>
							</h:selectOneMenu>
						</td>
					</tr>
					
					<tr>
						<td>
							<h:selectBooleanCheckbox id="checkData" value="#{invencao.buscaData}" styleClass="noborder"/>
						</td>
						<th style="text-align: left"><label for="checkData"
						onclick="javascript:$('formAtividade:checkData').checked = true;">Data:</label>
						</th>
						<td colspan="2">
							 <t:inputCalendar id="Dia" value="#{invencao.obj.criadoEm}" size="10" maxlength="10"
							 	onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy">
								<f:converter converterId="convertData"/>
							</t:inputCalendar>
						</td>
					</tr>
					
				</tbody>
				
				<tfoot>
					<tr>
						<td colspan="3">
							<h:commandButton id="buscar" value="Buscar" action="#{invencao.buscar}" />
							<h:commandButton id="cancelar" value="Cancelar" action="#{invencao.cancelar}" onclick="#{confirm}"/>
						</td>
					</tr>
				</tfoot>
				
			</table>
			<br/>
		
			<div class="infoAltRem">
				<h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar Informações da Invenção
				<h:graphicImage value="/img/view2.gif"style="overflow: visible;"/>: Visualizar Comprovante 
				<h:graphicImage value="/img/avaliar.gif"style="overflow: visible;"/>: Emitir parecer sobre a invenção <br />
				<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Informações da Invenção
			    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Excluir Notificação de Invenção
			</div>
			<br/>
			
			<t:dataTable id="datatableInvencoes" value="#{invencao.resultadosBusca}" var="inv" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
				<f:facet name="caption">
					<h:outputText value="Lista das Invenções" />
				</f:facet>
		
				<t:column>
					<f:facet name="header">
						<f:verbatim>Código</f:verbatim>
					</f:facet>
					<h:outputText value="#{inv.codigo}" />
				</t:column>
		
				<t:column>
					<f:facet name="header">
						<f:verbatim>Tipo</f:verbatim>
					</f:facet>
					<h:outputText value="#{inv.tipo.descricao}" escape="false"/>
					<h:outputText value=" (#{inv.descricaoCategoriaPatente})" rendered="#{inv.tipo.id == 1}"/>
				</t:column>


				<t:column>
					<f:facet name="header">
						<f:verbatim>Situação</f:verbatim>
					</f:facet>
					<h:outputText value="#{inv.statusString}" />
				</t:column>
				
				<t:column>
					<f:facet name="header"><f:verbatim>Usuário</f:verbatim></f:facet>
					<h:outputText value="#{inv.criadoPor.usuario.login}" />
				</t:column>
				
				<t:column>
					<f:facet name="header"><f:verbatim><center>Data</center></f:verbatim></f:facet>
					<div style="text-align:center">
					<h:outputText value="#{inv.criadoEm}">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</h:outputText>
					</div>
				</t:column>
				
				<t:column>
					<f:facet name="header">
						<f:verbatim>Ativa</f:verbatim>
					</f:facet>
					<h:outputText value="#{inv.ativo? 'Sim' : 'Não'}" />
				</t:column>
		
				<t:column width="2%" styleClass="centerAlign">
					<h:commandLink title="Visualizar Informações da Invenção" action="#{ invencao.view }" immediate="true">
				        <f:param name="idInvencao" value="#{inv.id}"/>
				        <f:param name="idView" value="true"/>
			    		<h:graphicImage url="/img/view.gif" />
					</h:commandLink>
				</t:column>
				
				<t:column width="2%" styleClass="centerAlign">
					<h:commandLink title="Visualizar Comprovante" action="#{ invencao.verComprovante }" immediate="true" rendered="#{inv.submetida}">
				        <f:param name="idInvencao" value="#{inv.id}"/>
			    		<h:graphicImage url="/img/view2.gif" />
					</h:commandLink>
				</t:column>
				
				<t:column width="2%" styleClass="centerAlign">
					<h:commandLink title="Emitir Parecer sobre a Invenção" action="#{ parecerInvencaoBean.popularParecer }" immediate="true">
				        <f:param name="idInvencao" value="#{inv.id}"/>
			    		<h:graphicImage url="/img/avaliar.gif" />
					</h:commandLink>
				</t:column>
		
				<t:column width="2%" styleClass="centerAlign">
					<h:commandLink title="Alterar Informações da Invenção" action="#{ invencao.continuarCadastro }" immediate="true">
				        <f:param name="idInvencao" value="#{inv.id}"/>
			    		<h:graphicImage url="/img/alterar.gif" />
					</h:commandLink>
				</t:column>
		
				<t:column width="2%" styleClass="centerAlign">
					<h:commandLink title="Excluir Notificação de Invenção" action="#{ invencao.preRemover }" immediate="true">
				        <f:param name="idInvencao" value="#{inv.id}"/>
			    		<h:graphicImage url="/img/delete.gif" />
					</h:commandLink>
				</t:column>
		
			</t:dataTable>
			
		<c:if test="${(empty invencao.resultadosBusca)}">
			<center><font color='red'>Não há invenções registradas na base de dados.</font></center>
		</c:if>
			
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>