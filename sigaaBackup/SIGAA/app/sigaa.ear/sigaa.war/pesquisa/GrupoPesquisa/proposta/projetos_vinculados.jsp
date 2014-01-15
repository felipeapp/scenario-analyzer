<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:region rendered="#{ propostaGrupoPesquisaMBean.portalDocente }">
<%@include file="/portais/docente/menu_docente.jsp"%>
</a4j:region>

<h2><ufrn:subSistema /> &gt; Proposta de Criação de Grupos de Pesquisa</h2>

<div class="descricaoOperacao">
	<p>
		As vinculações realizadas nessa tela irá gerar impacto nos projetos envolvidos. Modificando o Grupo de Pesquisa e a Linha de Pesquisa do Projeto informado.  
	</p>
</div>

<h:form id="form">
	<table class="formulario" width="90%">
        <caption class="listagem">Dados da Proposta</caption>
        <tbody>
        <tr>
        	<th class="obrigatorio">Projeto de Pesquisa:</th>
        	<td> 
				<a4j:region id="projetoPesquisa">
	 				<h:inputText id="projPesquisa" value="#{ propostaGrupoPesquisaMBean.obj.projPesquisa.titulo }"
	 					size="90" maxlength="150"/>
	                <rich:suggestionbox id="suggestionProjPesquisa"  width="650" height="200" minChars="3" 
					    for="projPesquisa" suggestionAction="#{ propostaGrupoPesquisaMBean.autoCompleteProjetoPesquisa }" 
					    var="_proj" fetchValue="#{ _proj.titulo }"
					    onsubmit="$('indicatorProjPesquisa').style.display='inline';" 
				      	oncomplete="$('indicatorProjPesquisa').style.display='none';">
				      	<h:column>
					      	<h:outputText value="#{ _proj.codigoTitulo }"/>
				      	</h:column>
				      	<a4j:support event="onselect">
					      	<f:setPropertyActionListener value="#{ _proj }" target="#{ propostaGrupoPesquisaMBean.obj.projPesquisa }" />
				      	</a4j:support>
					</rich:suggestionbox>
					<img id="indicatorProjPesquisa" src="/sigaa/img/indicator.gif" style="display: none;">
				</a4j:region>
        	</td>
        </tr>

        <tr>
        	<th class="obrigatorio">Linha de Pesquisa:</th>
        	<td> 
        	    <h:selectOneMenu id="linhaPesq"
					value="#{ propostaGrupoPesquisaMBean.linha.id }" style="width: 80%;">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
					<f:selectItems value="#{propostaGrupoPesquisaMBean.obj.linhasPesquisaCombo}"/>
				</h:selectOneMenu>
        	</td>
        </tr>

		<tr>
			<td colspan="2" class="subFormulario" style="text-align: center;">
				<h:commandButton id="btnAdicionar" value="Adicionar" action="#{ propostaGrupoPesquisaMBean.adicionarProjeto }" />
			</td>
		</tr>

        <tr>
        	<td colspan="2" class="subFormulario"> Linhas de Pesquisa e Projeto Vínculados </td>
        </tr>
        <tr>
        	<td colspan="2">
				<rich:dataTable id="gp" value="#{ propostaGrupoPesquisaMBean.obj.linhasPesquisaCol }" 
					var="linhas" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
        			<rich:column styleClass="subFormulario" colspan="2">
							<f:facet name="header"><f:verbatim>Linha de Pesquisa</f:verbatim></f:facet>
							<h:outputText value="#{ linhas.nome }"/>

							<rich:dataTable id="projetos" value="#{ linhas.projetosPesquisaCol }" 
								var="_proj" align="center" width="100%" styleClass="subFormulario">
			        			<rich:column width="95%">
			        				<f:facet name="header"><f:verbatim>Projeto de Pesquisa</f:verbatim></f:facet>
									<h:outputText value="#{ _proj.codigoTitulo }"/>
			        			</rich:column>

			        			<rich:column>
									<h:commandLink action="#{ propostaGrupoPesquisaMBean.removerProjeto }" onclick="#{confirmDelete}" >
										<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
										<f:param name="id" value="#{linhas.id}"/>
										<f:param name="idProj" value="#{_proj.id}"/>
									</h:commandLink>
			        			</rich:column>
			        			
			        		</rich:dataTable>
						
        			</rich:column>
        		</rich:dataTable>
        	</td>
        </tr>
        
		<tfoot>
			<tr>
				<td colspan="2" style="text-align: center;">
					<h:commandButton id="btnVoltar" value="<< Voltar" action="#{ propostaGrupoPesquisaMBean.telaTermoConcordancia }" />
					<h:commandButton id="btnCancelar" value="Cancelar" onclick="#{ confirm }" action="#{ propostaGrupoPesquisaMBean.cancelar }" />
					<h:commandButton id="btnSubmeterProjetos" value="Avançar >>" action="#{ propostaGrupoPesquisaMBean.submeterProjetosVinculados }" />
				</td>
			</tr>
		</tfoot>
	</table>
</h:form>
<br/>
<div class="obrigatorio">Campos de preenchimento obrigatório.</div>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>