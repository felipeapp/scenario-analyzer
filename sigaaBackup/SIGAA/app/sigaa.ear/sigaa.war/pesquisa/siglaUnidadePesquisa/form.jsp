<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="siglaUnidadePesquisaMBean" />

<f:view>
	<h2><ufrn:subSistema /> &gt; Configurações das Unidades para o Módulo de Pesquisa</h2>

<div class="descricaoOperacao">

			<p>Esta operação permite configurar as seguinte informações para a unidade desejada: </p>
			<ul>
				<li>
					<b>Sigla</b>: Utilizada para compor o código dos projetos submetidos a partir desta unidade. (Ex.: sigla "A" - PVA5458-2011)<br/>
				</li>
				<li>
			        <b>Unidade de Classificação</b>: A unidade informada nesse campo substituirá a unidade original nas operações de classificação dos docentes
			        para a concessão de cotas. <br/>
			    </li>
				<li>
			        <b>Unidade do CIC</b>: A unidade informada nesse campo substituirá a unidade original nas operações do CIC. <br/>
			    </li>
        	</ul>
</div>

	<h:form id="form">
		<table class="formulario" width="80%">
		<caption>Configurações para Unidades</caption>
			<h:inputHidden value="#{ siglaUnidadePesquisaMBean.obj.id }" />
			
			<tr>
				<th width="20%">Sigla:</th>
				<td>
					<h:inputText id="sigla" value="#{siglaUnidadePesquisaMBean.obj.sigla}" size="1" maxlength="1" onkeyup="CAPS(this)" />
				</td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Unidade:</th>
				<td>
					<h:inputText value="#{siglaUnidadePesquisaMBean.obj.unidade.nome}" id="unidade" style="width: 430px;" />
					<rich:suggestionbox for="unidade" height="100" width="430"  minChars="3" id="suggestion"
					   	suggestionAction="#{unidadeAutoCompleteMBean.autocompleteNomeUnidade}" var="_unidade" 
					   	fetchValue="#{_unidade.codigoNome}">
					 
					      <h:column>
						<h:outputText value="#{_unidade.codigoNome}" /> 
					      </h:column> 
					 
					      <a4j:support event="onselect">
								<f:setPropertyActionListener value="#{_unidade.id}" target="#{siglaUnidadePesquisaMBean.obj.unidade.id}"  />
					      </a4j:support>  
					</rich:suggestionbox>				
				</td>
			</tr>

			<tr>
				<th>Unidade de Classificação:</th>
				<td>
					<h:inputText value="#{siglaUnidadePesquisaMBean.obj.unidadeClassificacao.nome}" id="unidadeClass" style="width: 430px;" />
					 
					<rich:suggestionbox for="unidadeClass" height="100" width="430"  minChars="3" id="suggestionClass"
					   	suggestionAction="#{unidadeAutoCompleteMBean.autocompleteNomeUnidade}" var="_unidadeClass" 
					   	fetchValue="#{_unidadeClass.codigoNome}">
					 
					      <h:column>
						<h:outputText value="#{_unidadeClass.codigoNome}" /> 
					      </h:column> 
					 
					      <a4j:support event="onselect">
						<f:setPropertyActionListener value="#{_unidadeClass.id}" target="#{siglaUnidadePesquisaMBean.obj.unidadeClassificacao.id}"  />
					      </a4j:support>  
					</rich:suggestionbox>				
				</td>
			</tr>

			<tr>
				<th>Unidade de CIC:</th>
				<td>
					<h:inputText value="#{siglaUnidadePesquisaMBean.obj.unidadeCic.nome}" id="unidadeCic" style="width: 430px;" />
					 
					<rich:suggestionbox for="unidadeCic" height="100" width="430"  minChars="3" id="suggestionCic"
					   	suggestionAction="#{unidadeAutoCompleteMBean.autocompleteNomeUnidade}" var="_unidadeCic" 
					   	fetchValue="#{_unidadeCic.codigoNome}">
					 
					      <h:column>
						<h:outputText value="#{_unidadeCic.codigoNome}" /> 
					      </h:column> 
					 
					      <a4j:support event="onselect">
						<f:setPropertyActionListener value="#{_unidadeCic.id}" target="#{siglaUnidadePesquisaMBean.obj.unidadeCic.id}"  />
					      </a4j:support>  
					</rich:suggestionbox>				
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btnGerarNumeracao" action="#{siglaUnidadePesquisaMBean.cadastrar}" value="Cadastrar"/>
						<h:commandButton id="btnCancelar" action="#{siglaUnidadePesquisaMBean.cancelar}" value="Cancelar" immediate="true" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>