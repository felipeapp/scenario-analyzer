<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	tr.negrito th { font-weight: bold; }
</style>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Submissão de Projetos de Novos Pesquisadores </h2>

	<%@include file="_operacao.jsp"%>

	<h:form id="form">
		<table class="formulario" width="100%">
		<caption> Projeto de Apoio a Novos Pesquisadores </caption>

			<tr>
				<td colspan="6" class="subFormulario">Identificação</td>
			</tr>
			<tr class="negrito">
				<th>Nome:</th>
				<td colspan="3"> 
					<h:outputText id="nome" value="#{ projetoApoioNovosPesquisadoresMBean.obj.coordenador.nome}" />
				</td>

				<th>CPF:</th>
				<td> <h:outputText id="cpf" value="#{ projetoApoioNovosPesquisadoresMBean.obj.coordenador.pessoa.cpf_cnpj }" /> </td>
			</tr>

			<tr class="negrito">
				<th>RG:</th>
				<td> <h:outputText id="identidade" value="#{ projetoApoioNovosPesquisadoresMBean.obj.coordenador.pessoa.identidade.numero }" /> </td>

				<th>Org. Exp:</th>
				<td> <h:outputText id="orgaoExp" value="#{ projetoApoioNovosPesquisadoresMBean.obj.coordenador.pessoa.identidade.orgaoExpedicao }" /> </td>
			</tr>

			<tr class="negrito">
				<th>Unidade:</th>
				<td> <h:outputText id="unidade" value="#{ projetoApoioNovosPesquisadoresMBean.obj.coordenador.unidade.nome }" /> </td>

				<th>Telefone:</th>
				<td> <h:outputText id="telefone" value="#{ projetoApoioNovosPesquisadoresMBean.obj.coordenador.pessoa.telefone }" /> </td>

				<th>Fax:</th>
				<td> <h:outputText id="fax" value="#{ projetoApoioNovosPesquisadoresMBean.obj.coordenador.pessoa.fax }" /> </td>
			</tr>

			<tr class="negrito">
				<th>E-mail:</th>
				<td> <h:outputText id="email" value="#{ projetoApoioNovosPesquisadoresMBean.obj.coordenador.pessoa.email }" /> </td>

				<th>Lattes:</th>
				<td colspan="3"> 
					<a href="${ projetoApoioNovosPesquisadoresMBean.obj.coordenador.pessoa.perfil.enderecoLattes }" target="_blank">
						${ projetoApoioNovosPesquisadoresMBean.obj.coordenador.pessoa.perfil.enderecoLattes }</a>
				 </td>
			</tr>

			<tr>
				<td style="padding-top: 10px;"></td>
			</tr>

			<tr>
				<td colspan="6" class="subFormulario">Edital de Pesquisa</td>
			</tr>

			<tr>
				<th>Edital:</th>
				<td  colspan="5"> 
					<h:outputText value="#{projetoApoioNovosPesquisadoresMBean.obj.editalPesquisa.edital.descricao}" />
				</td>
			</tr>

			<tr>
				<td style="padding-top: 10px;"></td>
			</tr>

			<tr>
				<td colspan="6" class="subFormulario">Grupo de Pesquisa da Instituição</td>
			</tr>

			<tr>
				<th width="21%">Grupo de Pesquisa:</th>
				<td colspan="6">
					<h:inputText id="suggestionNomeGP" value="#{ projetoApoioNovosPesquisadoresMBean.obj.grupoPesquisa.nome}" size="62"/>
					<rich:suggestionbox for="suggestionNomeGP" width="450" height="100" minChars="3" id="nomeGrupoPesquisa" 
						suggestionAction="#{ grupoPesquisa.autocompleteGrupoPesquisa }" var="_grupoPesquisa" fetchValue="#{_grupoPesquisa.nome}">
							 
						<h:column>
							<h:outputText value="#{_grupoPesquisa.nome}" />
						</h:column>
					 
				        <a4j:support event="onselect" reRender="coordenador" immediate="true" >
							<f:setPropertyActionListener value="#{ _grupoPesquisa.id }" target="#{ projetoApoioNovosPesquisadoresMBean.obj.grupoPesquisa.id }" />
					    </a4j:support>
					</rich:suggestionbox>
				</td>
			</tr>
			
			<tr>
				<td style="padding-top: 10px;"></td>
			</tr>
					
			<tr>
				<td colspan="6" class="subFormulario">Informações Projeto</td>
			</tr>

			<tr>
				<th class="obrigatorio">Título</th>
				<td  colspan="6"> 
					<h:inputText id="titulo" value="#{ projetoApoioNovosPesquisadoresMBean.obj.projeto.titulo}" size="62" maxlength="400"/>
				</td>
			</tr>

			<tr>
				<td style="padding-top: 10px;"></td>
			</tr>

			<tr>
				<td colspan="6">
					<div id="abas-descricao">
			
						<div id="objetivo" class="aba">
							<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />&nbsp;<i> Objetivo do Projeto de novos Pesquisadores </i><br /><br />
							<h:inputTextarea id="objetivos" value="#{ projetoApoioNovosPesquisadoresMBean.obj.projeto.objetivos }" style="width: 95%" rows="10" />
						</div>
	
						<div id="metodologia" class="aba">
							<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />&nbsp;
							<i> Metodologia do Trabalho </i><br /><br />
							<h:inputTextarea id="integracao" value="#{ projetoApoioNovosPesquisadoresMBean.obj.projeto.metodologia }" style="width: 95%" rows="10" />
						</div>

						<div id="resultado" class="aba">
							<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />&nbsp;
							<i> Resultado Esperado para o projeto </i><br /><br />
							<h:inputTextarea id="resultados" value="#{ projetoApoioNovosPesquisadoresMBean.obj.projeto.resultados }" style="width: 95%" rows="10" />
						</div>

						<div id="referencia" class="aba">
							<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />&nbsp;
							<i> Referências do Projeto </i><br /><br />
							<h:inputTextarea id="referencias" value="#{ projetoApoioNovosPesquisadoresMBean.obj.projeto.referencias }" style="width: 95%" rows="10" />
						</div>

						<div id="integracao" class="aba">
							<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />&nbsp;
							<i> Descrever se o projeto ora apresentado se integra com as atividades de ensino e/ou extensão desenvolvidas na Instituição </i><br /><br />
							<h:inputTextarea id="integracoes" value="#{ projetoApoioNovosPesquisadoresMBean.obj.integracao }" style="width: 95%" rows="10" />
						</div>

					</div>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="6">
						<h:commandButton id="btnCancelar" action="#{projetoApoioNovosPesquisadoresMBean.cancelar}" value="Cancelar" immediate="true" onclick="#{confirm}"/>
						<h:commandButton id="btnGerarNumeracao" action="#{ projetoApoioNovosPesquisadoresMBean.submeterDadosGerais }" value="Avançar >>"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
		<br />
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
		
	</h:form>
	
</f:view>

<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('abas-descricao');
        abas.addTab('objetivo', "Objetivo");
        abas.addTab('metodologia', "Metodologia");
        abas.addTab('resultado', "Resultados");
        abas.addTab('referencia', "Referência");
        abas.addTab('integracao', "Integração");
        abas.activate('objetivo');
    }
};

YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
YAHOO.ext.EventManager.onDocumentReady(Abas2.init, Abas2, true);
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>