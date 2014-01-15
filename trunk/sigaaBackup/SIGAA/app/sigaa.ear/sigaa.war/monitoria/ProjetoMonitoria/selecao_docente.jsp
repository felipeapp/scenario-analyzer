<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>	
	<h2><ufrn:subSistema /> > Docentes de Monitoria</h2>
	
	<div class="descricaoOperacao">
		<table width="100%">
			<tr>
				<td><font color="red" size="2">Atenção:</font> Selecione os componentes curriculares que serão associados ao docente (orientador).<br/>
				Somente docentes do quadro permanente da ${ configSistema['siglaInstituicao'] } podem ser adicionados ao projeto.</td>
				<td><%@include file="passos_projeto.jsp"%></td>
			</tr>
		</table>
	</div>
	
	
	<h:form id="formIncluirDocentes">

		<table class="formulario" width="100%" id="formulario">
			<caption class="listagem">Seleção de Docentes</caption>
			
			<c:if test="${!projetoMonitoria.obj.projetoAssociado}">
				<tr>
					<th class="required" style="text-align:left;width:10px">Docente:</th>
					<td>
					<h:inputHidden id="id" value="#{projetoMonitoria.equipeDocente.servidor.id}"></h:inputHidden>
					<h:inputText id="nome"	value="#{projetoMonitoria.equipeDocente.servidor.pessoa.nome}" size="70" />
	
					 <ajax:autocomplete
						source="formIncluirDocentes:nome" target="formIncluirDocentes:id"
						baseUrl="/sigaa/ajaxDocente" className="autocomplete"
						indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn,situacao=ativo"
						parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
						style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
						<ufrn:help img="/img/ajuda.gif">Apenas os docentes do Quadro Permanente da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>
					</td>
				</tr>
			</c:if>	
			
			<c:if test="${projetoMonitoria.obj.projetoAssociado}">		
				<tr>
					<td colspan="2" class="subFormulario">Membros da Equipe do Projeto</td>
				</tr>
				<tr>
					<td colspan="2">
						<table class="subFormulario" width="100%">
							<thead>
								<tr><td>Lista de Possíveis Orientadores</td></tr>
							</thead>
								<tr>
									<td>
										<h:selectOneRadio  value="#{projetoMonitoria.equipeDocente.servidor.id}" styleClass="noborder" id="docenteSelecionado" layout="pageDirection">
											<f:selectItems value="#{projetoMonitoria.selectItemsPossiveisOrientadores}"/>
										</h:selectOneRadio>
									</td>
								</tr>
						</table>
					</td>
				</tr>
			</c:if>
			
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">
							<tr>
								<td>
									<t:dataTable value="#{projetoMonitoria.obj.componentesCurriculares}" var="comp" align="center" width="100%" id="compo" styleClass="listagem"  >
										<t:column>
											<f:facet name="header"><f:verbatim>Lista de Componentes Curriculares do Projeto</f:verbatim></f:facet>
											<h:selectBooleanCheckbox value="#{comp.selecionado}" styleClass="noborder" id="compCurricular"/>
											<h:outputText value="#{comp.disciplina.codigoNome}" />
										</t:column>
									</t:dataTable>
								</td>
							</tr>
					</table>
				</td>
			</tr>
			
			<tr>
				<td colspan="2" align="center">
					<input type="hidden" name="idServidor" id="idServidor" />
					<h:commandButton	action="#{projetoMonitoria.adicionaDocente}" value="Adicionar Docente ao Projeto"/>
				</td>
			</tr>
			
			<tr>
				<td colspan="2" align="left">
					<div class="infoAltRem">
					   	<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover<br/>
					</div>
					
					<c:if test="${not empty projetoMonitoria.obj.componentesCurriculares}">
						<t:dataTable id="tableCompCurricular" value="#{projetoMonitoria.obj.componentesCurriculares}" var="compCurricular" width="100%" styleClass="subformulario">
							<f:facet name="header">
								<h:outputText value="Orientadores" />
							</f:facet>
							
							<t:column styleClass="leftAlign">
								<t:dataTable id="tableDocentesComp" value="#{compCurricular.docentesComponentes}" var="docentesComponentes" width="100%" styleClass="subformulario">
									<f:facet name="header">
										<h:outputText value="Lista de Orientadores e seus Componentes Curriculares" />
									</f:facet>
									
									<t:column styleClass="leftAlign" width="50%">
										<f:facet name="header">
											<f:verbatim>Orientador(a)</f:verbatim>
										</f:facet>
										<t:outputText value="#{docentesComponentes.equipeDocente.servidor.siapeNome}" />
									</t:column>
									
									<t:column styleClass="leftAlign" >
										<f:facet name="header">
											<f:verbatim>Componente Curricular Relacionado</f:verbatim>
										</f:facet>
										<t:outputText value="#{docentesComponentes.componenteCurricularMonitoria.disciplina.codigoNome}" />
									</t:column>
									
									<t:column width="2%">
										<h:commandLink action="#{projetoMonitoria.removeDocenteComponente}">
									    	<f:param name="idDisciplina" value="#{compCurricular.disciplina.id}"/>				    	
									    	<f:param name="idDocente" value="#{docentesComponentes.equipeDocente.servidor.id}"/>																	
											<h:graphicImage url="/img/delete.gif" title="Remover"/>
										</h:commandLink>
									</t:column>			
								</t:dataTable>
							</t:column>
							
						</t:dataTable>
					</c:if>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Gravar Proposta" action="#{ projetoMonitoria.cadastrarParcialEquipeDocente}" id="btGravar"  title="Gravar Proposta para Continuar Depois."/>
						<h:commandButton value="<< Voltar" action="#{projetoMonitoria.passoAnterior}" id="btComp"/>								
						<h:commandButton value="Cancelar" action="#{projetoMonitoria.cancelar}" id="btCancel" onclick="#{confirm}"/>							
						<h:commandButton value="Avançar >>" action="#{projetoMonitoria.submeterDocentes}" id="btcood"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
	</h:form>

<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;" id="obrigatorio"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>