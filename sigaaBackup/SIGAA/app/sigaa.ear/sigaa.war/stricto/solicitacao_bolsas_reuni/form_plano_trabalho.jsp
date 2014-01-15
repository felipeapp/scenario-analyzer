<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/prototype-1.6.0.3.js" ></script>

<style type="text/css">
	.dr-table, .dr-table-cell { border: 0; }
	.comboCurso { width: 90%; }
	
	.radios { width: 70%; margin: 0 auto; } 
	.radios input { vertical-align: middle; } 
	
	span.info { color: #555; font-size: 0.9em;  }
	
	#descricaoLinhasAcao { margin-left: 30px; }
	#descricaoLinhasAcao dt { margin: 5px 0; text-decoration: underline;}
</style>

<f:view>
	<a4j:keepAlive beanName="solicitacaoBolsasReuniBean" />
	<a4j:keepAlive beanName="planoTrabalhoReuniBean" />
		
	<h2> <ufrn:subSistema /> &gt; Solicitação de Bolsas REUNI de Assistência ao Ensino</h2>
	
	<div class="descricaoOperacao">
		<p>
			<b> Caro Coordenador, </b>
		</p> 	
		<p>
			Abaixo está disponível o formulário para cadastro de um plano de trabalho de bolsista, 
			com seus campos definidos de acordo com o edital de concessão publicado.
		</p>
		<p>
			Para cada plano de trabalho é necessário selecionar uma das duas linhas de ação disponíveis:
		</p>
		<dl id="descricaoLinhasAcao">
			<dt> Linha de Ação 1</dt>
			<dd> 
				Visa à melhoria da qualidade de ensino de graduação através da atuação de pós-graduandos,
				prioritariamente, em componentes curriculares com elevado índice de retenção.
			</dd>
			<dt> Linha de Ação 2</dt>
			<dd> 
				Visa à implantação de novas metodologias de ensino-aprendizagem no Bacharelado em 
				Ciências e Tecnologia (BC&T) através da atuação de pós-graduandos nas áreas relacionadas no edital.
			</dd>
		</dl>
	</div>
	<h:form id="form">
	
		${planoTrabalhoReuniBean.clear}
	
		<table class="formulario" style="width: 100%">
			<caption>Dados do Plano de Trabalho</caption>
			<tr>
				<th> Edital: </th>
				<td>
					<c:choose>
						<c:when test="${not empty planoTrabalhoReuniBean.obj.solicitacao.edital.idArquivoEdital}">
							<a href="${ctx}/verArquivo?idArquivo=${planoTrabalhoReuniBean.obj.solicitacao.edital.idArquivoEdital}&key=${ sf:generateArquivoKey(planoTrabalhoReuniBean.obj.solicitacao.edital.idArquivoEdital) }" target="_blank" id="planTrabArqEdital">
								${ planoTrabalhoReuniBean.obj.solicitacao.edital }
							</a> 
						</c:when>
						<c:otherwise>
							${ planoTrabalhoReuniBean.obj.solicitacao.edital }
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			
			<tr>
				<th> Programa de Pós-Graduação: </th>
				<td> <h:outputText value="#{ planoTrabalhoReuniBean.obj.solicitacao.programa }"/> </td>
			</tr>			
			
			<tr>
				<th width="35%" class="obrigatorio"> Nível do Discente do Plano: </th>
				<td>
	 				<h:selectOneMenu value="#{planoTrabalhoReuniBean.obj.nivel}" style="width: 50%;" id="nivelEnsino">
	 					<f:selectItem itemValue="0" itemLabel="Selecione o nível de ensino..."/>
	 					<f:selectItems value="#{nivelEnsino.strictoCombo}"/>
	 				</h:selectOneMenu>
				</td>
			</tr>			
			
			<tr>
				<th width="35%" class="obrigatorio"> Número de Alunos de Graduação Beneficiados: </th>
				<td>
					<h:inputText value="#{planoTrabalhoReuniBean.obj.numeroAlunosGraduacaoBeneficiados}" size="5" maxlength="5" id="alunosBenef"
						onkeyup="return formatarInteiro(this);" style="text-align: right;"/>
					<span class="info"> (número estimado) </span>
				</td>
			</tr>
						
			<tr>
				<td colspan="2" class="subFormulario" style="background: #DFD; border-color: #ACA;">
					<h:selectOneRadio value="#{planoTrabalhoReuniBean.obj.linhaAcao}" id="radioLinhaAcao" styleClass="radios" 
						valueChangeListener="#{planoTrabalhoReuniBean.alterarLinhaAcao}">
						<f:selectItem itemValue="1" itemLabel="Linha de Ação 1"/>
						<f:selectItem itemValue="2" itemLabel="Linha de Ação 2"/>
						<a4j:support event="onchange" reRender="painelLinhaAcao" /> 
					</h:selectOneRadio>
				</td>
			</tr>

			<tr>
				<td colspan="2" style="padding:0;">
					<a4j:outputPanel id="painelLinhaAcao">
						<a4j:outputPanel id="painelLinhaAcao1" rendered="#{planoTrabalhoReuniBean.obj.linhaAcao == 1}">
						 	<table width="100%">
								<a4j:region>
						 		<tr>
						 			<th width="35%"> Componente Curricular: </th>
						 			<td>
						 				<h:selectOneMenu value="#{planoTrabalhoReuniBean.obj.componenteCurricular.id}" style="width: 95%;" 
						 					valueChangeListener="#{planoTrabalhoReuniBean.alterarComponentePrioritario}" id="componenteCurricularPrioritario">
						 					<f:selectItem itemValue="0" itemLabel="Selecione um dos componentes curriculares com alta taxa de retenção..."/>
						 					<f:selectItems value="#{planoTrabalhoReuniBean.componentesPrioritariosCombo}"/>
						 					<f:selectItem itemValue="-1" itemLabel="Informar um componente curricular que não está na lista..."/>
						 					<a4j:support event="onchange" reRender="painelLinhaAcao1" />
						 				</h:selectOneMenu>
						 			</td>
						 		</tr>
						 		<tr> <td colspan="2"> 
						 		<a4j:outputPanel id="painelOutroComponente" rendered="#{planoTrabalhoReuniBean.obj.componenteCurricular.id  == -1}">
						 		<table width="100%">
						 		<tr>
						 			<th width="35%"> Outro Componente: </th>
						 			<td>
						 				<a4j:region>
						 				<h:inputText value="#{planoTrabalhoReuniBean.outroComponenteCurricular.nome}" id="nomeComponente" style="width: 440px;"/> 
										<rich:suggestionbox width="400" height="120" for="nomeComponente" 
											minChars="6" frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="200" 
											suggestionAction="#{componenteCurricular.autocompleteGraduacao}" var="_componente" fetchValue="#{_componente.nome}">
											<h:column>
												<h:outputText value="#{_componente.codigo}"/>
											</h:column>
											<h:column>
												<h:outputText value="#{_componente.nome}"/>
											</h:column>
											<h:column>
												<h:outputText value="#{_componente.unidade.sigla}"/>
											</h:column>
											<a4j:support event="onselect" actionListener="#{planoTrabalhoReuniBean.selecionarOutroComponente}">
												<f:attribute name="componente" value="#{_componente}"/>
											</a4j:support>
										</rich:suggestionbox>	
									
										<rich:spacer width="10"/>
							            <a4j:status>
							                <f:facet name="start">&nbsp;<h:graphicImage  value="/img/indicator.gif"/></f:facet>
							            </a4j:status>
							            </a4j:region>
						 			</td>
						 		</tr>
						 		<tr>
						 			<th> Justificativa: </th>
						 			<td> 
										<h:inputTextarea value="#{planoTrabalhoReuniBean.obj.justificativaComponenteCurricular}" id="justificativaOutroComponente" style="width: 95%;"/>
									</td>
						 		</tr>
						 		</table>
						 		</a4j:outputPanel>
						 		</td></tr>
						 		</a4j:region>
						 		
						 		<a4j:region>
						 		<tr>
						 			<td colspan="2" class="subFormulario"> Docentes Responsáveis Pelo Componente Curricular </td>
						 		</tr>
								<tr>
									<td colspan="2" style="line-height: 30px; padding-left: 5%;">
										<a4j:outputPanel id="inputDocente">
											Docente:
											
											<rich:spacer width="10"/>
											<h:inputText value="#{planoTrabalhoReuniBean.docente.pessoa.nome}" id="nomeDocente" style="width: 600px;"/>
											<rich:suggestionbox width="600" height="100" for="nomeDocente" 
												minChars="5" frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="200" 
												suggestionAction="#{servidor.autocompleteDocente}" var="_servidor" fetchValue="#{_servidor.nome}">
												<h:column>
													<h:outputText value="#{_servidor.siape}"/>
												</h:column>
												<h:column>
													<h:outputText value="#{_servidor.nome}"/>
												</h:column>
												<a4j:support event="onselect" actionListener="#{planoTrabalhoReuniBean.adicionarDocente}" reRender="docentesResponsaveis, inputDocente" focus="nomeDocente">
													<f:attribute name="docente" value="#{_servidor}"/>
												</a4j:support>
											</rich:suggestionbox>
											
											<rich:spacer width="10"/>
								            <a4j:status>
								                <f:facet name="start">&nbsp;<h:graphicImage  value="/img/indicator.gif"/></f:facet>
								            </a4j:status>
							            </a4j:outputPanel>					
									</td>
								</tr>
								<tr>
									<td colspan="2">
									<a4j:outputPanel id="docentesResponsaveis">
									<c:if test="${not empty planoTrabalhoReuniBean.obj.docentes}">						
										<div class="infoAltRem">
											<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
											Remover Docente
										</div>
									</c:if>									
									<t:dataTable value="#{planoTrabalhoReuniBean.obj.docentes}" var="_docente" style="width: 90%;" 
										styleClass="listagem" rowClasses="linhaPar, linhaImpar" rendered="#{not empty planoTrabalhoReuniBean.obj.docentes}">
					
										<t:column>
											<f:facet name="header"><f:verbatim>Docente</f:verbatim></f:facet>
											<h:outputText value="#{_docente.nome}"/>
										</t:column>
										<t:column>
											<f:facet name="header"><f:verbatim>Unidade</f:verbatim></f:facet>
											<h:outputText value="#{_docente.unidade.sigla}"/>
										</t:column>
					
										<t:column width="5%" styleClass="centerAlign">
											<a4j:commandButton image="/img/delete.gif" actionListener="#{planoTrabalhoReuniBean.removerDocente}" id="removDocente"
												title="Remover docente da lista"	reRender="docentesResponsaveis">
													<f:attribute name="docente" value="#{_docente}"/>
											</a4j:commandButton>
										</t:column>					
									</t:dataTable>
									</a4j:outputPanel>
									</td>			
								</tr>
								</a4j:region>						 			
								<tr>
									<td colspan="2" class="subFormulario"> Curso(s) de Graduação Envolvido(s) </td>
								</tr>			
								<a4j:region>
								<tr>
									<td colspan="2" style="line-height: 30px; padding-left: 5%;">
										Curso:
										<h:selectOneMenu value="#{planoTrabalhoReuniBean.curso.id}" style="width: 70%" id="cursoCombo">
											<f:selectItem itemValue="0" itemLabel="Selecione um curso de graduação..."/>
											<f:selectItems value="#{curso.allCursosGraduacaoPresenciaisCombo}"/>
										</h:selectOneMenu>
										
										<rich:spacer width="10"/>
										<a4j:commandButton value="Adicionar Curso" actionListener="#{planoTrabalhoReuniBean.adicionarCurso}" reRender="cursosEnvolvidos, cursoCombo" id="addCurso"/>
										
										<rich:spacer width="10"/>
							            <a4j:status>
							                <f:facet name="start">&nbsp;<h:graphicImage  value="/img/indicator.gif"/></f:facet>
							            </a4j:status>					
									</td>
								</tr>
								<tr>
									<td colspan="2">
									<a4j:outputPanel id="cursosEnvolvidos">
									<c:if test="${not empty planoTrabalhoReuniBean.obj.cursos}">						
										<div class="infoAltRem">
											<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
											Remover Curso
										</div>
									</c:if>											
									<t:dataTable value="#{planoTrabalhoReuniBean.obj.cursos}" var="_curso" style="width: 90%;" 
										styleClass="listagem" rowClasses="linhaPar, linhaImpar" rendered="#{not empty planoTrabalhoReuniBean.obj.cursos}">
					
										<t:column>
											<f:facet name="header"><f:verbatim>Cursos selecionados</f:verbatim></f:facet>
											<h:outputText value="#{_curso.descricao}"/>
										</t:column>
					
										<t:column width="5%" styleClass="centerAlign">
											<a4j:commandButton image="/img/delete.gif" actionListener="#{planoTrabalhoReuniBean.removerCurso}" 
												title="Remover curso da lista"	reRender="cursosEnvolvidos">
													<f:attribute name="curso" value="#{_curso}"/>
											</a4j:commandButton>
										</t:column>					
									</t:dataTable>
									</a4j:outputPanel>
									</td>			
								</tr>
								</a4j:region>						 			
						 	</table>
						</a4j:outputPanel>
						<a4j:outputPanel id="painelLinhaAcao2" rendered="#{planoTrabalhoReuniBean.obj.linhaAcao == 2}">
						 	<table width="100%">
						 		<tr>
						 			<th width="35%"> Área de Ensino em Ciências e Tecnologia: </th>
						 			<td> 
						 				<h:selectOneMenu value="#{planoTrabalhoReuniBean.obj.areaConhecimento.id}" style="width: 90%;" id="areaEnsinoCeT" title="Área de Ensino em Ciências e Tecnologia">
						 					<f:selectItem itemValue="0" itemLabel="Selecione uma área de atuação..."/>
						 					<f:selectItems value="#{areaConhecimentoCienciasTecnologiaBean.allAtivosCombo}"/>
						 				</h:selectOneMenu> 
						 			</td>
						 		</tr>
						 	</table>
						</a4j:outputPanel>
					</a4j:outputPanel>
				</td>
			</tr>			
			
			<tr>
				<td colspan="2" class="subFormulario"> Objetivos <span class="obrigatorio"></span></td>
			</tr>
			<tr>
				<td colspan="2">
					<h:inputTextarea value="#{planoTrabalhoReuniBean.obj.objetivos}" rows="10" style="width: 95%; margin: 5px 15px;" id="objetivos"/>
				</td>
			</tr>
			
			<tr>
				<td colspan="2" class="subFormulario"> Forma(s) de atuação do bolsista <span class="obrigatorio"></span></td>
			</tr>
			<tr>
				<td colspan="2">
					 <rich:dataGrid value="#{planoTrabalhoReuniBean.formasAtuacao}" var="_formaAtuacao" columns="2" style="width: 95%; margin: 0 auto;" id="dbgrid">
						<h:selectBooleanCheckbox value="#{_formaAtuacao.selecionada}" styleClass="noborder" id="formaAtuacao" style="vertical-align: middle;"  />
						<h:outputLabel for="formaAtuacao" value="#{_formaAtuacao.descricao}" />
						
					 </rich:dataGrid>
					<rich:spacer width="21" />
					<h:selectBooleanCheckbox value="#{planoTrabalhoReuniBean.opcaoOutrasFormasAtuacao}" styleClass="noborder" id="opcaoOutrasFormasAtuacao"  style="vertical-align: middle;"  />
					<h:outputLabel for="opcaoOutrasFormasAtuacao" value="Outras Formas de Atuação: "/>
					<h:inputText value="#{planoTrabalhoReuniBean.obj.outrasFormasAtuacao}" style="width: 72%;" id="outrasFormasAtuacaoo"/>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{planoTrabalhoReuniBean.confirmButton}" action="#{planoTrabalhoReuniBean.cadastrar}" id="cadastrar"/> 
						<h:commandButton value="Cancelar" action="#{planoTrabalhoReuniBean.cancelar}" immediate="true" onclick="#{confirm}" id="cancelar"/>
					</td> 
				</tr>
			</tfoot>
			
		</table>
	</h:form>

	<br/>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Todos os campos são de preenchimento obrigatório. </span> 
		<br><br>
	</center>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	