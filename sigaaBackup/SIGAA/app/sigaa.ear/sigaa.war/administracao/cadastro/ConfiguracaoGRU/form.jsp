<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Cadastro de Configuração de GRU</h2>

	<h:form id="form">
		<a4j:keepAlive beanName="configuracaoGRUMBean" />
		<table class="formulario" width="95%">
			<caption>Dados da Configuração de GRU</caption>
				<tr>
					<th class="obrigatorio">Descrição:</th>
					<td>
						<h:inputText value="#{ configuracaoGRUMBean.obj.descricao }" size="60" maxlength="80" id="descricao"/>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Grupo de Emissão da GRU:</th>
					<td>
						<h:selectOneMenu value="#{ configuracaoGRUMBean.obj.grupoEmissaoGRU.id }" id="grupoEmissaoGRU">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{ grupoEmissaoGRUMBean.grupoEmissaoGRUCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Tipo de Arrecadação:</th>
					<td>
						<h:selectOneMenu value="#{ configuracaoGRUMBean.obj.tipoArrecadacao.id }" id="tipoArrecadacao">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{ configuracaoGRUMBean.tipoArrecadacaoCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Unidade:</th>
					<td>
						<h:inputText value="#{configuracaoGRUMBean.obj.unidade.nome}" id="unidade" style="width: 430px;" />
						<rich:suggestionbox for="unidade" height="100" width="430"  minChars="3" id="suggestion"
						   	suggestionAction="#{unidadeAutoCompleteMBean.autocompleteNomeUnidade}" var="_unidade" 
						   	fetchValue="#{_unidade.codigoNome}">
							<h:column>
								<h:outputText value="#{_unidade.codigoNome}" /> 
							</h:column> 
					     	<a4j:support event="onselect">
								<f:setPropertyActionListener value="#{_unidade.id}" target="#{configuracaoGRUMBean.obj.unidade.id}"  />
					      	</a4j:support>  
						</rich:suggestionbox> 
					</td>
				</tr>
				<tr>
					<th><h:selectBooleanCheckbox value="#{ configuracaoGRUMBean.obj.gruSimples }" id="gruSimples"/></th>
					<td>GRU Simples</td>
				</tr>
				<c:if test="${ configuracaoGRUMBean.obj.id > 0 }">
					<tr>
						<th><h:selectBooleanCheckbox value="#{ configuracaoGRUMBean.obj.ativo }" id="ativo"/></th>
						<td>Ativo</td>
					</tr>
				</c:if>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{configuracaoGRUMBean.confirmButton}" action="#{configuracaoGRUMBean.cadastrar}" id="cadastrar" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{configuracaoGRUMBean.cancelar}" id="cancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br />
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>