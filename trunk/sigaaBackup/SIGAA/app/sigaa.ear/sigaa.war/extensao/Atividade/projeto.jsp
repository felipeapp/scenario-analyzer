<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<h2><ufrn:subSistema /> > Dados Adicionais do Programa</h2>

<div class="descricaoOperacao">
	<table width="100%">
		<tr>
			<td width="50%">
			Nesta tela devem ser informados os dados adicionais de uma Ação.
			</td>
			<td>
				<%@include file="passos_atividade.jsp"%>
			</td>
		</tr>
	</table>
</div>

<h:form id="programa"> 
<table class=formulario width="95%">
	<caption class="listagem">Dados Adicionais do Programa</caption>
	<h:inputHidden value="#{atividadeExtensao.obj.id}"/>
	
	<tr>
		<a4j:region rendered="#{ !atividadeExtensao.obj.projetoAssociado}">
				<tr>
					<td colspan="2">
						<div id="tabs-dados-projeto" class="reduzido">
				
							<div id="resumo" class="aba">
								<i class="required">Resumo do Projeto:</i><br/>
								<h:inputTextarea id="resumo" value="#{atividadeExtensao.obj.projeto.resumo}" rows="10" style="width:99%" />
							</div>
				
							<div id="justificativa" class="aba">
								<i class="required">Justificativa para execução do projeto:</i><br/>
								<div class="descricaoOperacao">Inclua na justificativa os benefícios esperados no processo ensino-aprendizagem dos alunos de graduação e/ou pós-graduação vinculados ao projeto. 
								Explicite também o retorno para os cursos de graduação e/ou pós-graduação e para os professores da ${ configSistema['siglaInstituicao'] } em geral.</div>
								<h:inputTextarea id="justificativa" value="#{atividadeExtensao.obj.justificativa}" rows="10" style="width:99%" />
							</div>

							<div id="fundamentacao" class="aba">
								<i class="required">Metodologia de desenvolvimento do projeto:</i><br/>
								<h:inputTextarea id="fundamentacao" value="#{atividadeExtensao.obj.fundamentacaoTeorica}" rows="10" style="width:99%" />
							</div>

							<div id="metodologia" class="aba">
								<i class="required">Metodologia de desenvolvimento do projeto:</i><br/>
								<h:inputTextarea id="metodologia" value="#{atividadeExtensao.obj.projeto.metodologia}" rows="10" style="width:99%" />
							</div>
				
							<div id="referencias" class="aba">
								<i class="required">Referências:  Ref. Bibliográficas do projeto, etc.</i><br/>
								<h:inputTextarea id="referencias" value="#{atividadeExtensao.obj.projeto.referencias}" rows="10" style="width:99%" />
							</div>

							<div id="objetivosGerais" class="aba">
								<i class="required">Objetivos Gerais.</i><br/>
								<h:inputTextarea id="objetivosGerais" value="#{atividadeExtensao.obj.projeto.objetivos}" rows="10" style="width:99%" />
							</div>

							<div id="resultados" class="aba">
								<i class="required">Resultados Esperados.</i><br/>
								<h:inputTextarea id="resultados" value="#{atividadeExtensao.obj.projeto.resultados}" rows="10" style="width:99%" />
							</div>

						</div>
					</td>
				</tr>
		</a4j:region>
		
		<a4j:region rendered="#{ atividadeExtensao.obj.projetoAssociado }">
			<th width="25%"><b>Resumo do programa:</b></th>
			<td>
				<h:inputTextarea id="resumoAssociado" value="#{atividadeExtensao.obj.resumo}" rows="8" style="width:98%" rendered="#{!atividadeExtensao.obj.projetoAssociado}"/>
				<h:outputText value="#{atividadeExtensao.obj.resumo}" rendered="#{atividadeExtensao.obj.projetoAssociado}"/>
			</td>
		</a4j:region>
		
	</tr>

	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="<< Voltar" action="#{atividadeExtensao.passoAnterior}" />
				<h:commandButton value="Cancelar" action="#{atividadeExtensao.cancelar}"  onclick="#{confirm}" />
				<h:commandButton value="Avançar >>" action="#{projetoExtensao.submeterProjeto }" />
			</td>
		</tr>
	</tfoot>

</table>
</h:form>
</f:view>

<script type="text/javascript">
	var Tabs = {
	    init : function(){
	        var tabs = new YAHOO.ext.TabPanel('tabs-dados-projeto');
	        tabs.addTab('resumo', "Resumo");
	        tabs.addTab('justificativa', "Justificativa");
	        tabs.addTab('fundamentacao', "Fundamentação Teórica");
	        tabs.addTab('metodologia', "Metodologia");
	        tabs.addTab('referencias', "Referências");
	        tabs.addTab('objetivosGerais', "Objetivos Gerais");
	        tabs.addTab('resultados', "Resultados Esperados");
	        tabs.activate('resumo');
	    }
	}
	YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>