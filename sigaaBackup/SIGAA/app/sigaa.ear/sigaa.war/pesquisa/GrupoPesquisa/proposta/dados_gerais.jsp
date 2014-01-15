<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:region rendered="#{ propostaGrupoPesquisaMBean.portalDocente }">
	<%@include file="/portais/docente/menu_docente.jsp"%>
</a4j:region>

<h2><ufrn:subSistema /> &gt; Proposta de Criação de Grupos de Pesquisa</h2>

<div class="descricaoOperacao">
	<p>
		Este formulário está em conformidade com a Resolução nº 162/2008-CONSEPE, de 18 de novembro de 2008.
	</p>
</div>
	
<div class="infoAltRem">
	<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" />: Adicionar Linha de Pesquisa
	<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Linha de Pesquisa
</div>
	
<h:form id="form">
	<table class="formulario" width="90%">
        <caption class="listagem">Dados da Proposta</caption>
        <tbody>
        <tr>
        	<td colspan="2" class="subFormulario">1. Caracterização do Grupo</td>
        </tr>
        <a4j:region rendered="#{ propostaGrupoPesquisaMBean.pesquisa }">
	        <tr>
	            <th>Código:</th>
	            <td>
	                <h:inputText id="codigo" value="#{propostaGrupoPesquisaMBean.obj.codigo}" 
	                	size="20" maxlength="20"/>
	            </td>
	        </tr>
        </a4j:region>
        <tr>
            <th class="obrigatorio">Título do Grupo:</th>
            <td>
                <h:inputText id="nome" value="#{propostaGrupoPesquisaMBean.obj.nome}" maxlength="300" size="70"/>
            </td>
        </tr>
		<tr>
			<th class="obrigatorio">Líder:</th>
			<td>
				<a4j:region id="coordenadorReg" rendered="#{ propostaGrupoPesquisaMBean.pesquisa }">
	 				<h:inputText id="coordenador" value="#{propostaGrupoPesquisaMBean.obj.coordenador.pessoa.nome}"
	 					size="70" maxlength="50"/>
	                <rich:suggestionbox id="suggestionCoord"  width="400" height="100" minChars="3" 
					    for="coordenador" suggestionAction="#{propostaGrupoPesquisaMBean.autoCompleteDocentePermanente}" 
					    var="_docente" fetchValue="#{_docente.siapeNome}"
					    onsubmit="$('indicatorCoord').style.display='inline';" 
				      	oncomplete="$('indicatorCoord').style.display='none';">
				      	<h:column>
					      	<h:outputText value="#{_docente.siapeNome}"/>
				      	</h:column>
				      	<a4j:support event="onselect" reRender="dtPermanentes"
					      	actionListener="#{propostaGrupoPesquisaMBean.carregarCoordenador}" >
					      	<f:attribute name="docenteAutoComplete" value="#{_docente}"/>
				      	</a4j:support>
					</rich:suggestionbox>
					<img id="indicatorCoord" src="/sigaa/img/indicator.gif" style="display: none;">
				</a4j:region>
                
				<h:outputText id="nomeCoordenador" value="#{propostaGrupoPesquisaMBean.obj.coordenador.siapeNome}" rendered="#{ !propostaGrupoPesquisaMBean.pesquisa }" />
			</td>
		</tr>
		<tr>
			<th>Vice-Líder:</th>
			<td>
				<a4j:region id="viceCoordenadorReg">
	 				<h:inputText id="viceCoordenador" value="#{propostaGrupoPesquisaMBean.obj.viceCoordenador.pessoa.nome}"
	 					size="70" maxlength="50"/>
	                <rich:suggestionbox id="suggestionViceCoord"  width="400" height="100" minChars="3" 
					    for="viceCoordenador" suggestionAction="#{propostaGrupoPesquisaMBean.autoCompleteDocentePermanente}" 
					    var="_docente" fetchValue="#{_docente.siapeNome}"
					    onsubmit="$('indicatorViceCoord').style.display='inline';" 
				      	oncomplete="$('indicatorViceCoord').style.display='none';">
				      	<h:column>
					      	<h:outputText value="#{_docente.siapeNome}"/>
				      	</h:column>
				      	<a4j:support event="onselect" reRender="dtPermanentes"
					      	actionListener="#{propostaGrupoPesquisaMBean.carregarViceCoordenador}" >
					      	<f:attribute name="docenteAutoComplete" value="#{_docente}"/>
				      	</a4j:support>
					</rich:suggestionbox>
					<img id="indicatorViceCoord" src="/sigaa/img/indicator.gif" style="display: none;">
				</a4j:region>
			</td>
		</tr>
        <tr>
            <th>Área de Conhecimento:</th>
            <td>
               <h:selectOneMenu id="areaCNPQ" valueChangeListener="#{ propostaGrupoPesquisaMBean.changeArea }"
					value="#{ propostaGrupoPesquisaMBean.area.id }" style="width: 70%;">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
					<f:selectItems value="#{ area.allGrandesAreasCombo }"/>
					<a4j:support event="onchange" reRender="subareaCNPQ" />
				</h:selectOneMenu>
            </td>
        </tr>
        <tr>
        	<th class="obrigatorio">Sub-área de Conhecimento:</th>
        	<td>
        	    <h:selectOneMenu id="subareaCNPQ"
					value="#{propostaGrupoPesquisaMBean.obj.areaConhecimentoCnpq.id}" style="width: 70%;">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
					<f:selectItems value="#{propostaGrupoPesquisaMBean.subareas}"/>
				</h:selectOneMenu>
        	</td>
        </tr>
        <tr>
        	<th class="obrigatorio">Linha de Pesquisa:</th>
        	<td> 
        		<h:inputText id="linhaPesquisa" value="#{propostaGrupoPesquisaMBean.linha.nome}" 
        			size="55" maxlength="150" />

				<h:commandLink action="#{ propostaGrupoPesquisaMBean.adicionarLinhaPesquisa }" >
					<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" title="Adicionar Linha de Pesquisa"/>
					<a4j:support reRender="gp" event="onclick" />
				</h:commandLink>
        	</td>
        </tr>

        <c:if test="${ not empty propostaGrupoPesquisaMBean.obj.linhasPesquisaCol }">
	        <tr>
	        	<td colspan="2" class="subFormulario"> Linhas de Pesquisa </td>
	        </tr>
	        <tr>
	        	<td colspan="2">
					<rich:dataTable id="gp" value="#{ propostaGrupoPesquisaMBean.obj.linhasPesquisaCol }" 
						var="linhas" align="center" width="100%" rowClasses="linhaPar, linhaImpar">
	        			<rich:column>
							<f:facet name="header"><f:verbatim>Linha de Pesquisa</f:verbatim></f:facet>
							<h:outputText value="#{linhas.nome}"/>
	        			</rich:column>
						<rich:column width="2%">
							<h:commandLink  title="Remover Linha de Pesquisa" action="#{ propostaGrupoPesquisaMBean.removerLinha }" id="btRemove" onclick="#{confirmDelete}">
						      <f:param name="id" value="#{linhas.id}"/>
						      <h:graphicImage url="/img/delete.gif" />
							</h:commandLink>
						</rich:column>
	        		</rich:dataTable>
	        	</td>
	        </tr>
        </c:if>
        
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton id="btnCancelar" value="Cancelar" onclick="#{confirm}" action="#{propostaGrupoPesquisaMBean.cancelar}" />
					<h:commandButton id="btnSubmeterDadosGerais" value="Avançar >>" action="#{propostaGrupoPesquisaMBean.submeterDadosGerais}" />
				</td>
			</tr>
		</tfoot>
	</table>
</h:form>
<br/>
<div class="obrigatorio">Campos de preenchimento obrigatório.</div>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>