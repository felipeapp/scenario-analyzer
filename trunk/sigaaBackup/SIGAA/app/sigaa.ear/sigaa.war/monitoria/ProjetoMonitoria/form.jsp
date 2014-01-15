<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.TipoProjetoEnsino"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h:messages showDetail="true" showSummary="true" />
	<h2><ufrn:subSistema /> > Cadastro de Projeto de Ensino</h2>
	
	<div class="descricaoOperacao">
		<table width="100%">
			<tr>
				<td>Nesta tela devem ser informados os dados gerais do Projeto.</td>
				<td><%@include file="passos_projeto.jsp"%></td>
			</tr>
		</table>
	</div>
	
	
	<h:form enctype="multipart/form-data"  id="form">
	<h:outputText  value="#{projetoMonitoria.create}"/>
	<h:outputText  value="#{unidade.create}"/>
	<h:inputHidden value="#{projetoMonitoria.confirmButton}"/>
	<h:inputHidden value="#{projetoMonitoria.obj.id}"/>
	
	<c:set var="PROJETO_MONITORIA" 		value="<%= String.valueOf(TipoProjetoEnsino.PROJETO_DE_MONITORIA) %>" 	scope="application"/>
	<c:set var="PROJETO_PAMQEG" 		value="<%= String.valueOf(TipoProjetoEnsino.PROJETO_PAMQEG) %>" 		scope="application"/>	
	<c:set var="AMBOS_PROJETOS" 		value="<%= String.valueOf(TipoProjetoEnsino.AMBOS_MONITORIA_PAMQEG) %>" 		scope="application"/>	


	<table class="formulario" width="100%" cellpadding="3" id="tabela1">
	<caption class="formulario"> Solicitar Cadastro de Projeto de Ensino </caption>
	<tr>
		<td>
			<table class="subFormulario" width="100%">
				<caption>Dados Gerais</caption>
				<tr>
					<th width="20%" class="required"> Título do Projeto: </th>
					<td>
						<h:inputText id="tituloProjeto" value="#{projetoMonitoria.obj.titulo}" rendered="#{!projetoMonitoria.readOnly && !projetoMonitoria.obj.projetoAssociado}" size="70" maxlength="400"/>
						<b><h:outputText id="_tituloProjeto" value="#{projetoMonitoria.obj.titulo}" rendered="#{projetoMonitoria.readOnly || projetoMonitoria.obj.projetoAssociado}"/></b>
					</td>
				</tr>
				
				<tr>
	                <th class="required"> Edital: </th>
	                <td>
                      <a4j:region> 
                      
	                        <h:selectOneMenu id="edital" value="#{projetoMonitoria.obj.editalMonitoria.id}" rendered="#{projetoMonitoria.editalDesteProjetoAindaAberto}">
	                            <f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
	                            <f:selectItems value="#{projetoMonitoria.editaisCombo}"/>
	                            <a4j:support event="onchange" reRender="anoReferencia, periodoProjeto" action="#{projetoMonitoria.changeEdital}"/>
	                        </h:selectOneMenu>
		                    <h:outputText value="#{projetoMonitoria.obj.editalMonitoria.descricao}" rendered="#{!projetoMonitoria.editalDesteProjetoAindaAberto}"/>

                        
                        <a4j:status>
                           <f:facet name="start">
                               <h:graphicImage value="/img/indicator.gif">Carregando dados do edital </h:graphicImage>
                           </f:facet>
		                </a4j:status>
                      </a4j:region>
	                </td>
	            </tr>

				<tr>
					<th><b>Período:</b></th>
					<td>
					   <h:panelGroup id="periodoProjeto">
							<h:outputText id="dataInicio" value="#{projetoMonitoria.obj.projeto.dataInicio}" /> 
							<h:outputText id="ate" rendered="#{not empty projetoMonitoria.obj.projeto.dataFim}" value=" até "/> 
							<h:outputText id="dataFim" value="#{projetoMonitoria.obj.projeto.dataFim}" />
						</h:panelGroup>
					</td>						
				</tr>

				<tr>
					<th><b>Ano de Referência:</b></th>
					<td>
						<h:outputText id="anoReferencia" value="#{projetoMonitoria.obj.ano}"/>
					</td>
				</tr>

				<c:if test="${!projetoMonitoria.obj.projetoPAMQEG}">
					<tr>
						<th class="required"> Bolsas Solicitadas:</th>
						<td>
							<h:inputText id="bolsasSolicitadas" value="#{projetoMonitoria.obj.bolsasSolicitadas}" readonly="#{projetoMonitoria.readOnly}" maxlength="3" size="3" onkeyup="formatarInteiro(this)"/>
						</td>
					</tr>
				</c:if>

				<tr>
						<td colspan="2">
							<div id="tabs-monitoria">
	
								<div id="resumo" class="aba" >
									<br><i>
									Resumo do Projeto:</i><span class="required"></span><br/>
									<div class="descricaoOperacao">O resumo deve contemplar de forma sucinta os objetivos, metodologia e resultados esperados, de modo a oferecer um panorama geral da proposta.</div>
									<h:inputTextarea id="resumo" value="#{projetoMonitoria.obj.resumo}" rows="10" style="width: 99%" rendered="#{!projetoMonitoria.readOnly && !projetoMonitoria.obj.projetoAssociado}" />
									<h:outputText id="_resumo" value="#{projetoMonitoria.obj.resumo}" rendered="#{projetoMonitoria.readOnly || projetoMonitoria.obj.projetoAssociado}" />
								</div>
								
								<div id="justificativa" class="aba" >
									<br/><i>Justificativa e Diagnóstico para Execução do Projeto:</i><span class="required">&nbsp;</span><br/>
									<div class="descricaoOperacao">Destaque a relevância e o porquê da necessidade de desenvolver tal projeto. Deixe claras as razões e explicite dados/diagnósticos que ressaltem tal necessidade, 
									principalmente aquelas direcionadas aos componentes curriculares que serão contemplados na proposta.</div>
									<h:inputTextarea id="justificativa" value="#{projetoMonitoria.obj.justificativa}" rows="10" style="width:99%" rendered="#{!projetoMonitoria.readOnly && !projetoMonitoria.obj.projetoAssociado}"/>
									<h:outputText id="_justificativa" value="#{projetoMonitoria.obj.justificativa}"  rendered="#{projetoMonitoria.readOnly || projetoMonitoria.obj.projetoAssociado}"/>									
								</div>
															
								<div id="objetivos" class="aba">
									<br/><i>Objetivos (geral e específico):</i><span class="required"></span><br/>
									<div class="descricaoOperacao">A definição dos objetivos determina o que se quer atingir com a realização da proposta, que meta se quer alcançar.</div>
									<h:inputTextarea id="objetivos"  value="#{projetoMonitoria.obj.objetivos}" rows="10" style="width: 99%" rendered="#{!projetoMonitoria.readOnly && !projetoMonitoria.obj.projetoAssociado}"/>
									<h:outputText id="_objetivos"  value="#{projetoMonitoria.obj.objetivos}" rendered="#{projetoMonitoria.readOnly || projetoMonitoria.obj.projetoAssociado}"/>									
								</div>
	
								<div id="metodologia"  class="aba"> <!-- o campo de metodologia antes armazenava dados de avaliação -->
									<br/><i>Metodologia de Desenvolvimento do Projeto:</i><span class="required"></span><br/>
									<div class="descricaoOperacao">Explique as ações que serão desenvolvidas no projeto, o método (caminho) adotado para a sua execução.</div>
									<h:inputTextarea  id="metodologia" value="#{projetoMonitoria.obj.metodologia}" rows="10" style="width: 99%" rendered="#{!projetoMonitoria.readOnly && !projetoMonitoria.obj.projetoAssociado}"/>
									<h:outputText  id="_metodologia" value="#{projetoMonitoria.obj.metodologia}" rendered="#{projetoMonitoria.readOnly || projetoMonitoria.obj.projetoAssociado}"/>
								</div>
								
								<div id="resultados" class="aba" >
									<br><i>Resultados Esperados e Impactos:</i><span class="required"></span><br/>
									<div class="descricaoOperacao">Destaque os benefícios esperados para os componentes curriculares envolvidos, para os docentes, para os alunos monitores e para aqueles atendidos pelo projeto de ensino.</div>
									<h:inputTextarea id="resultados"  value="#{projetoMonitoria.obj.resultados}" rows="10" style="width: 99%" rendered="#{!projetoMonitoria.readOnly && !projetoMonitoria.obj.projetoAssociado}"/>
									<h:outputText id="_resultados"  value="#{projetoMonitoria.obj.resultados}" rendered="#{projetoMonitoria.readOnly || projetoMonitoria.obj.projetoAssociado}"/>								
								</div>
								
								<div id="produtos" class="aba" >
									<br><i>Produtos que resultam da execução do projeto:</i><span class="required"></span><br/>
									<div class="descricaoOperacao">Explicite aqui o(s) produto(s) resultante(s) do projeto de ensino desenvolvido especificando o formato (materiais didáticos e instrucionais, aplicativos, projetos técnicos,
									patentes, processos, técnicas, elaboração de produtos midiáticos, editoria, softwares, propostas de intervenção clínica, projetos de aplicação técnica, propostas de extensão tecnológica em empresas, projetos 
									de inovação tecnológica, protocolo experimental, produção artística, artigo acadêmico, outros) e o(s) seu(s) objetivo(s).</div>
									<h:inputTextarea id="produtos"  value="#{projetoMonitoria.obj.produto}" rows="10" style="width: 99%" rendered="#{!projetoMonitoria.readOnly && !projetoMonitoria.obj.projetoAssociado}"/>
									<h:outputText id="_produtos"  value="#{projetoMonitoria.obj.produto}" rendered="#{projetoMonitoria.readOnly || projetoMonitoria.obj.projetoAssociado}"/>								
								</div>
																
								<div id="avaliacao"  class="aba">
									<br/><i>Avaliação do Desenvolvimento do Projeto:</i><span class="required"></span><br/>
									<div class="descricaoOperacao">Mencione os instrumentos e os métodos que serão utilizados para avaliar o alcance dos objetivos do projeto de ensino. </div>
									<h:inputTextarea  id="avaliacao" value="#{projetoMonitoria.obj.avaliacao}" rows="10" style="width: 99%" readonly="#{projetoMonitoria.readOnly}" />
								</div>
	
								<div id="processo_seletivo" class="aba" >
									<br><i>Processo Seletivo:</i><span class="required"></span><br/>
									<div class="descricaoOperacao">Descreva como será realizado o processo seletivo do monitor.</div>
									<h:inputTextarea id="processo_seletivo"  value="#{projetoMonitoria.obj.processoSeletivo}" rows="10" style="width: 99%" rendered="#{!projetoMonitoria.readOnly && !projetoMonitoria.obj.projetoAssociado}"/>
									<h:outputText id="_processo_seletivo"  value="#{projetoMonitoria.obj.processoSeletivo}" rendered="#{projetoMonitoria.readOnly || projetoMonitoria.obj.projetoAssociado}"/>								
								</div>
	
								<div id="referencias" class="aba" >
									<br/><i>Referências:</i><span class="required"></span><br/>
									<div class="descricaoOperacao">Explicite apenas as referências mencionadas na proposta. </div>
									<h:inputTextarea  id="referencias" value="#{projetoMonitoria.obj.referencias}" rows="10" style="width: 99%" rendered="#{!projetoMonitoria.readOnly && !projetoMonitoria.obj.projetoAssociado}"/>
									<h:outputText  id="_referencias" value="#{projetoMonitoria.obj.referencias}" rendered="#{projetoMonitoria.readOnly || projetoMonitoria.obj.projetoAssociado}"/>
								</div>
	
							</div>
						</td>
				</tr>
				
			</table>
		</td>
	</tr>

	<tfoot>
		<tr>
			<td colspan="2">

				<c:if test="${projetoMonitoria.confirmButton != 'Remover'}">
					<h:commandButton value="Gravar Proposta" action="#{ projetoMonitoria.cadastrarParcialDadosGerais }" title="Gravar Proposta para Continuar Depois." id="btGravar" />
				</c:if>
				
				<h:commandButton value="<< Voltar" action="#{ projetoMonitoria.passoAnterior }" id="btVoltar" />					
				<h:commandButton value="Cancelar" action="#{projetoMonitoria.cancelar}" onclick="#{confirm}" id="btCancelar" immediate="true" />
				
				<c:if test="${projetoMonitoria.confirmButton != 'Remover'}">
					<h:commandButton value="Avançar >>" action="#{ projetoMonitoria.submeterDadosGerais }" id="btSubmeterDadosGerais" />						
				</c:if>
			</td>
		</tr>
	</tfoot>
	</h:form>

	</table>

<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>

</f:view>

<script type="text/javascript">
	var Tabs = {
	    init : function(){
	        var tabs = new YAHOO.ext.TabPanel('tabs-monitoria');
	        tabs.addTab('resumo', "Resumo");
	        tabs.addTab('justificativa', "Justificativa e Diagnóstico");
	        tabs.addTab('objetivos', "Objetivos");
	        tabs.addTab('metodologia', "Metodologia");
	        tabs.addTab('resultados', "Resultados");
	        tabs.addTab('produtos', "Produtos");
	        tabs.addTab('avaliacao', "Avaliação");
	        tabs.addTab('processo_seletivo', "Processo Seletivo");
	        tabs.addTab('referencias', "Referências");
	        tabs.activate('resumo');
	    }
	}
	YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);

</script>
<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "100%", height : "200", language : "pt",
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image,fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons2 : "",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>