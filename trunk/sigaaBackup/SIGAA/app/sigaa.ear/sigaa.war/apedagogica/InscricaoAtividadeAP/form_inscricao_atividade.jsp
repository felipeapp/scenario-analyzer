<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<script>
	// Muda o nome do jQuery para J, evitando conflitos com a Prototype.
	var J = jQuery.noConflict();
</script>

<style>
	.footer{text-align: center;}
	.colNome{width: 25%;text-align: left !important;}
	.colPeriodo{text-align: center !important;width: 13%}
	.colCH,.colVagas{text-align: right !important;width: 10%;padding-right:5px;}
	.colInstrutores{width: 25%;padding-left: 5px; }
	.colIcone{width: 1%;text-align: center !important;}
</style>
<f:view>
	<a4j:keepAlive beanName="inscricaoAtividadeAP"></a4j:keepAlive>
	<h2 class="title"><ufrn:subSistema />  > Inscrição para Participação em Atividade > Seleção das Atividades</h2>

	<div class="descricaoOperacao">
		<p>Caro Usuário,</p>
		<p>A listagem abaixo exibe todas as atividades abertas de atualização pedagógica do grupo selecionado.</p>
		<p>Por favor selecione as atividades que deseja participar e pressione "Inscrever em Atividades".</p>
	</div>

	<h:form id="forminscricaoAtividadeAP" >

		
		<center>
			<div class="infoAltRem">
			    <img src="/shared/img/icones/download.png" style="overflow: visible;"/>: Visualizar Programa
			    <h:graphicImage url="/img/view.gif" />: Detalhes da Atividade
			</div>
		</center>
		<br/>
		<%-- VISUALIZAÇÃO DO GRUPO DE ATIVIDADES --%>
		<table class="visualizacao" width="99%">
			<caption>Dados do Grupo de Atividade</caption>
			<tbody>
				<c:if test="${acesso.programaAtualizacaoPedagogica}">
				<tr>
					<th style="width:20%">
						<%-- Docente ou Técnico Administrativo --%>
						<h:outputText value="#{inscricaoAtividadeAP.obj.docente.categoria.descricao}"/>:
					</th>
					<td width="60%"><h:outputText value="#{inscricaoAtividadeAP.obj.docente.pessoa.nome}"/></td>
				</tr>
				</c:if>
				<tr>
					<th >Grupo:</th>
					<td><h:outputText value="#{inscricaoAtividadeAP.grupoSelecionado.denominacao}"/></td>
				</tr>	
				<tr>
					<th>Período:</th>
					<td>
						<h:outputText value="#{inscricaoAtividadeAP.grupoSelecionado.inicio}"/>
						a
						<h:outputText value="#{inscricaoAtividadeAP.grupoSelecionado.fim}"/>
					</td>
				</tr>
				<tr>
					<td colspan="2" class="subListagem">Atividades Abertas do Grupo</td>
				</tr>
				<tr>
					<td colspan="2">
		<%-- FORMULÁRIO DE CADASTRO DAS ATIVIDADES --%>
		

				<t:dataTable value="#{inscricaoAtividadeAP.grupoSelecionado.atividades}" rendered="#{not empty inscricaoAtividadeAP.grupoSelecionado.atividades}"
					 var="_a" styleClass="listagem"  width="80%"  footerClass="footer" 
					 columnClasses="colIcone,colNome,colPeriodo,colPeriodo,colCH,colVagas,colInstrutores" rowClasses="linhaPar, linhaImpar">
							
							<t:column styleClass="colIcone">
								<f:facet name="header">
									<f:verbatim>&nbsp;</f:verbatim>
								</f:facet>
								<h:selectBooleanCheckbox  value="#{_a.selecionada}"/>
							</t:column>	
							
							<t:column headerstyleClass="colNome" styleClass="colNome">
								<f:facet name="header">
									<f:verbatim>Nome</f:verbatim>
								</f:facet>
								<h:outputText value="#{_a.nome}"/>
							</t:column>
									
							<t:column headerstyleClass="colPeriodo" styleClass="colPeriodo">
								<f:facet name="header">
									<f:verbatim>Período</f:verbatim>
								</f:facet>
								<h:outputText value="#{_a.descricaoPeriodo}"/> 
							</t:column>
							
							<t:column headerstyleClass="colPeriodo" styleClass="colPeriodo">
								<f:facet name="header">
									<f:verbatim>Horário</f:verbatim>
								</f:facet>
								<h:outputText value="#{_a.horarioInicio} às #{_a.horarioFim}" rendered="#{not empty _a.horarioInicio}"/>
								<h:outputText value="Não informado" rendered="#{empty _a.horarioInicio}"/>
							</t:column>
								
							<t:column styleClass="colVagas" headerstyleClass="colVagas">
								<f:facet name="header">
									<f:verbatim>Nº de Vagas</f:verbatim>
								</f:facet>
								<h:outputText value="#{_a.numVagas}" rendered="#{not empty _a.numVagas}"/>
								<h:outputText value="Não informado" rendered="#{empty _a.numVagas}"/>
							</t:column>
							
							<t:column styleClass="colIcone" headerstyleClass="colIcone">
								<f:facet name="header">
									<f:verbatim>&nbsp;</f:verbatim>
								</f:facet>
								<h:outputText escape="false" value="
								<a href='/sigaa/verProducao?idProducao=#{_a.idArquivo}&key=#{ sf:generateArquivoKey(_a.idArquivo) }' 
									title='Visualizar Programa' target='_blank'>
									 <img src='/shared/img/icones/download.png'/>
								</a>"  rendered="#{_a.idArquivo > 0 }"/>
							</t:column>
		
							<t:column styleClass="colIcone" headerstyleClass="colIcone">
								<f:facet name="header">
									<f:verbatim>&nbsp;</f:verbatim>
								</f:facet>
								<h:commandLink styleClass="noborder" title="Detalhes da Atividade" id="visualizarRegistro"
									action="#{inscricaoAtividadeAP.viewAtividade}">
									<h:graphicImage url="/img/view.gif" />
									<f:param name="id" value="#{_a.id}" />
								</h:commandLink>
							</t:column>
						
				</t:dataTable>
				</td>	
			</tr>
			<tr>
			<td colspan="2" class="subListagem">Solicitação de Recursos Especiais</td>
			</tr>
			<tr>
				<th width="50%">Necessita de algum apoio/recurso para a participação no evento?</th>
				<td>
					<h:panelGroup>		
					<h:selectOneRadio value="#{inscricaoAtividadeAP.obj.solicitacaoRecursoNEE }" valueChangeListener="#{inscricaoAtividadeAP.carregarRecursosNee}" styleClass="noborder">
						<f:selectItems value="#{ inscricaoAtividadeAP.simNao }"/>
						<a4j:support event="onclick" onsubmit="true" reRender="recursosNEE" />
					</h:selectOneRadio>
					</h:panelGroup>
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<a4j:outputPanel id="recursosNEE">					
					<t:dataTable id="tabRecurso" value="#{inscricaoAtividadeAP.recursosParaSelecionar}" rendered="#{inscricaoAtividadeAP.obj.solicitacaoRecursoNEE}"
						 var="recurso" styleClass="listagem"  width="80%"  footerClass="footer" 
						 columnClasses="colIcone" rowClasses="linhaPar, linhaImpar">
						 
						 	<t:column styleClass="colIcone">
								<h:selectBooleanCheckbox  rendered="#{!recurso.outros}" value="#{recurso.selecionado}"/>
								<h:selectBooleanCheckbox  styleClass="checkOutros" rendered="#{recurso.outros}" value="#{recurso.selecionado}"/>
							</t:column>	
							
							<t:column headerstyleClass="colNome" styleClass="colNome">
								<h:outputText value="#{recurso.descricao}"/>
								&nbsp;
								<h:inputText value="#{recurso.complemento}" onclick="marcarCheck();" rendered="#{recurso.permiteComplemento}"/>
							</t:column>
							
					</t:dataTable>	 	
					</a4j:outputPanel>					
							
				</td>
			</tr>		
			
			</tbody>
			
			<tfoot>
				<tr>
					<td align="center" colspan="2">
						<h:commandButton value="#{inscricaoAtividadeAP.confirmButton}" 
							action="#{inscricaoAtividadeAP.cadastrar}"	title="Inscrever"/>
						<h:commandButton value="<< Voltar" action="#{inscricaoAtividadeAP.preCadastrar}" 
							 immediate="true" title="Voltar"/>
						<h:commandButton value="Cancelar" action="#{inscricaoAtividadeAP.cancelar}"
							 immediate="true" title="Cancelar" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
	</h:form>
	
</f:view>
<script>

function marcarCheck (){
	var check = J('.checkOutros')[0][0];
	check.checked = !check.checked;
}
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
