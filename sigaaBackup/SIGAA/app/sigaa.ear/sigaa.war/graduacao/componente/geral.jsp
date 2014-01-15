<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.ensino.dominio.FormaParticipacaoAtividade"%>

<%@page import="br.ufrn.sigaa.ensino.dominio.ComponenteCurricular"%>

<style>
	.ajuda { font-style: italic; }
	
	.inline { display: inline; }
	
	.tableDadosGerais th { width: 60%; }
</style>

<c:set var="AGUARDANDO_CONFIRMACAO" value="<%= ComponenteCurricular.AGUARDANDO_CONFIRMACAO %>" />

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp"%>
	<h2 class="title"><ufrn:subSistema /> &gt; Cadastro de Componente Curricular &gt; Dados Gerais</h2>
	<h:form id="form">
	<table class="formulario" width="100%">
		<caption class="formulario">Dados Gerais do Componente Curricular</caption>
			<tr>
				<td colspan="2">
				<table width="100%" id="tableDadosGerais">
					<tr>
						<th width="295px;"><b>Tipo do Componente:</b></th>
						<td><h:outputText value="#{componenteCurricular.obj.tipoComponente.descricao}" />
					</tr>
					<tr>
						<c:if test="${ componenteCurricular.obj.passivelTipoAtividade && componenteCurricular.obj.tipoAtividade.id > 0 }">
							<tr>
								<th><b>Tipo de ${componenteCurricular.obj.atividade ?
									'Atividade' : 'Disciplina'}:</b></th>
								<td><h:outputText
									value="#{componenteCurricular.obj.tipoAtividade.descricao}" />
								</td>
							</tr>
							<tr>
								<th><b>Forma de Participação:</b></th>
								<td><h:outputText
									value="#{componenteCurricular.obj.formaParticipacao.descricao}" />
								</td>
							</tr>
						</c:if>
					</tr>
					<tr>
						<th width="295px;"><b>Modalidade de Educação:</b></th>
						<td><h:outputText value="#{componenteCurricular.obj.modalidadeEducacao.descricao}" />
					</tr>
					<a4j:region>
					<tr>
						<c:choose>
							<c:when test="${ componenteCurricular.podeAlterarUnidade }">
								<th class="required">
									<c:choose>
										<c:when test="${componenteCurricular.obj.stricto}">Programa:</c:when>
										<c:otherwise>Unidade Responsável:</c:otherwise>
									</c:choose>
								</th>
								<td>
									<h:selectOneMenu id="unidades" onchange="submit();" 
										value="#{componenteCurricular.obj.unidade.id}" 
										valueChangeListener="#{componenteCurricular.selecionarUnidade}">
										<a4j:support event="onchange" reRender="cargasHorarias"/>
										<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
										<f:selectItems value="#{componenteCurricular.possiveisUnidades}" />
									</h:selectOneMenu>
								</td>
							</c:when>
							<c:otherwise>
								<th><b></>Unidade Responsável:</b></th>
								<td>${componenteCurricular.obj.unidade.nomeMunicipio }</td>
							</c:otherwise>
						</c:choose>	
					</tr>
					</a4j:region>
					<c:if test="${componenteCurricular.solicitacaoCadastroComponente || acesso.cdp}">
					<tr>
						<th>Situação do Curso:</th>
						<td nowrap="nowrap" >
							<h:selectOneRadio id="checkCursoAtual_Novo" value="#{componenteCurricular.cursoNovo}" styleClass="noborder" onclick="informarCurso();" >
								<f:selectItem itemValue="false" itemLabel="Curso Existente" />
								<f:selectItem itemValue="true" itemLabel="Curso Novo" />
							</h:selectOneRadio>
						</td>
					</tr>
					<tr>	
						<th valign="top">Curso:</th>
						<td>
							<h:selectOneMenu value="#{componenteCurricular.obj.curso.id}" id="curso_atual" >
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{componenteCurricular.cursoNivel}" />
							</h:selectOneMenu>
							<h:inputText id="curso_novo" value="#{componenteCurricular.obj.cursoNovo}" 
								size="60" maxlength="149" onkeyup="CAPS(this)"/>
							<ufrn:help>Isto não implica que o componente será oferecido exclusivo ao curso. 
								É apenas uma indicação para justificar qual curso foi solicitado o componente curricular. 
							</ufrn:help>
						</td>
					</tr>
					</c:if>
					
						<tr>
							<th class="required">Código: </th>
							<td>
								<h:inputText id="codigo"
									value="#{componenteCurricular.obj.codigo}" size="10" maxlength="#{componenteCurricular.validaCodigo ? 7 : 12}"
									onkeyup="CAPS(this)" readonly="#{ componenteCurricular.readOnly || componenteCurricular.obj.statusInativo != AGUARDANDO_CONFIRMACAO }"
									disabled="#{ componenteCurricular.readOnly || componenteCurricular.obj.statusInativo != AGUARDANDO_CONFIRMACAO }" />
								<c:if test="${not empty componenteCurricular.codigoSugerido}">
									<a href="#"><h:graphicImage onclick="$('form:codigo').value = $('codigoSugerido').innerHTML" value="/img/consolidacao/arrow_left.png" style="overflow: visible;"/></a>
									<span style="color: gray;" id="codigoSugerido">${componenteCurricular.codigoSugerido}</span>
								
									<ufrn:help img="/img/ajuda.gif">O preenchimento automático do código é uma sugestão do sistema por um código disponível para um componente dessa unidade</ufrn:help>
								</c:if> 
							</td>
						</tr>
						<tr>
							<th class="required">Nome:</th>
							<td>
								<h:inputText id="nome"
								value="#{componenteCurricular.obj.detalhes.nome}" size="90"
								maxlength="149" onkeyup="CAPS(this)"
								disabled="#{ (componenteCurricular.obj.graduacao && !acesso.administradorDAE 
									&& componenteCurricular.readOnly) || (componenteCurricular.obj.stricto && !acesso.ppg && (!componenteCurricular.coordenadorOpcaoCadastrarComponenteCurricular) ) }" />
							</td>
						</tr>
						<c:if test="${componenteCurricular.exibeAtivarComponente}">
							<tr>
								<th style="${componenteCurricular.obj.id != 0 ? "" : "font-weight: bold;" }">Ativo:</th>
								<td><c:if test="${componenteCurricular.obj.id != 0}">
									<h:selectOneRadio id="checkAtivo" value="#{componenteCurricular.obj.ativo}" styleClass="noborder" >
										<f:selectItem itemValue="true" itemLabel="Sim"/>
										<f:selectItem itemValue="false" itemLabel="Não"/>
									</h:selectOneRadio>
									</c:if>
									<c:if test="${componenteCurricular.obj.id == 0}">
										Sim
									</c:if>
								</td>
							</tr>
							<tr>
								<th>Excluir da Avaliação Institucional:</th>
								<td>
								<h:selectOneRadio id="checkAvaliacao" value="#{componenteCurricular.obj.excluirAvaliacaoInstitucional}" styleClass="noborder" >
									<f:selectItem itemValue="true" itemLabel="Sim"/>
									<f:selectItem itemValue="false" itemLabel="Não"/>
								</h:selectOneRadio>
								</td>
							</tr>
						</c:if>
				</table>
				</td>					
			</tr>
			
			<c:if test="${componenteCurricular.exibeCargaHorariaTotal}">
			<t:div id="cargasHorarias">
				<tr>
					<td colspan="2" class="subFormulario">
						Carga Horária Total:
						<c:if test="${componenteCurricular.creditosObrigatorios}"><span style="margin-left: -10px;" class="required">&nbsp;</span></c:if>
						<span id="chtotal">&nbsp;${componenteCurricular.obj.detalhes.chTotal}h</span>
					</td>
					
					
				</tr>
				
				<tr>
					<td colspan="3">
						<table width="100%">
							<c:if test="${componenteCurricular.exibeCrTeorico}">
								<tr>
									<th width="290px">Créditos Teóricos:</th>
									<td>
										<a4j:region>
										<h:inputText id="crAula" value="#{componenteCurricular.obj.detalhes.crAula}" 
															size="4" maxlength="4"
															converter="#{ intConverter }"
															onkeyup="calcularCHTeorico(); return formatarInteiro(this);" 
															disabled="#{!componenteCurricular.permiteAlterarCargaHoraria}" >
											<a4j:support event="onblur" reRender="horarioFlexTurma,horarioFlexDocente"/>
										</h:inputText>
										</a4j:region>
										<span id="chteorico">${componenteCurricular.obj.detalhes.chAula}h</span>
									</td>
								</tr>
							</c:if>
							<c:if test="${componenteCurricular.exibeCrPratico}">
								<tr>
									<th width="290px">Créditos Práticos:</th>
									<td>
										<h:inputText id="crLab"	value="#{componenteCurricular.obj.detalhes.crLaboratorio}"
													size="4" maxlength="4"
													converter="#{ intConverter }"
													onkeyup="calcularCHPratico(); return formatarInteiro(this);"
													disabled="#{!componenteCurricular.permiteAlterarCargaHoraria}" />
										<span id="chpratico">${componenteCurricular.obj.detalhes.chLaboratorio}h</span>
									</td>
								</tr>
							</c:if>
							<c:if test="${componenteCurricular.exibeCrEad}">
								<tr>
									<th width="290px">Créditos de Ead:</th>
									<td>
										<h:inputText id="crEad" value="#{componenteCurricular.obj.detalhes.crEad}"
													size="4" maxlength="4"
													converter="#{ intConverter }"
													onkeyup="calcularCHEad(); return formatarInteiro(this);"
													disabled="#{!componenteCurricular.permiteAlterarCargaHoraria}"  />
										<span id="chead">${componenteCurricular.obj.detalhes.chEad}h</span>
									</td>
								</tr>
							</c:if>
							<c:if test="${componenteCurricular.exibeCrEstagio}">
								<tr>
									<th width="290px">Créditos de Estágio:</th>
									<td>${componenteCurricular.obj.detalhes.crEstagio} créditos</td>
								</tr>
							</c:if>
							<c:if test="${componenteCurricular.exibeChTeorico}">
								<tr>
									<th width="290px">Carga Horária Teórica:</th>
									<td>
										<a4j:region>
										<h:inputText id="chAula" value="#{componenteCurricular.obj.detalhes.chAula}" 
													size="4" maxlength="4"
													converter="#{ intConverter }"
													onkeyup="calcularCHTotal(); return formatarInteiro(this);" 
													disabled="#{!componenteCurricular.permiteAlterarCargaHoraria}" >
											<a4j:support event="onblur" reRender="horarioFlexTurma,horarioFlexDocente"/>
										</h:inputText>
										</a4j:region>
										<c:if test="${componenteCurricular.obj.necessitaCargaHoraria}">
											<ufrn:help img="/img/ajuda.gif">
												Necessário informar o valor de pelo menos uma Carga Horária.
											</ufrn:help>
										</c:if>
									</td>
								</tr>
							</c:if>
							<c:if test="${componenteCurricular.exibeChEad}">
								<tr>
									<th width="290px">Carga Horária de Ead:</th>
									<td>
										<h:inputText id="chEad"	value="#{componenteCurricular.obj.detalhes.chEad}"
													size="4" maxlength="4"
													converter="#{ intConverter }"
													onkeyup="calcularCHTotal(); return formatarInteiro(this);"
													disabled="#{!componenteCurricular.permiteAlterarCargaHoraria}" />
									</td> 
								</tr>
							</c:if>
							<c:if test="${componenteCurricular.exibeChPratico}">
								<tr>
									<th width="290px">Carga Horária Prática:</th>
									<td>
										<h:inputText id="chLab"	value="#{componenteCurricular.obj.detalhes.chLaboratorio}"
													size="4" maxlength="4"
													converter="#{ intConverter }"
													onkeyup="calcularCHTotal(); return formatarInteiro(this);"
													disabled="#{!componenteCurricular.permiteAlterarCargaHoraria}" />
									</td> 
								</tr>
							</c:if>
							<c:if test="${componenteCurricular.exibeChNaoAula}">
								<tr>
									<th width="290px">Carga Horária de Não Aula:</th>
									<td>
										<h:inputText id="chNaoAula" value="#{componenteCurricular.obj.detalhes.chNaoAula}"
													size="4" maxlength="4"
													converter="#{ intConverter }"
													onkeyup="calcularCHTotal(); return formatarInteiro(this);"
													disabled="#{!componenteCurricular.permiteAlterarCargaHoraria}" />
									</td> 
								</tr>
							</c:if>
							<c:if test="${componenteCurricular.exibeChDedicadaDocente}">
								<tr>
									
									<c:if test="${componenteCurricular.obj.estagio || componenteCurricular.obj.atividadeColetiva }">
										<th width="290px"><span class="required"/>Carga Horária do Docente:</th>
										<td>
											<h:inputText id="chDedicadaDocente"	value="#{componenteCurricular.obj.detalhes.chDedicadaDocente}" 
											                                    size="4"
											                                    maxlength="4"
											                                    converter="#{ intConverter }"
											                                    onkeyup="return formatarInteiro(this);"/>
										<ufrn:help img="/img/ajuda.gif">
											Necessário informar um valor maior que 0 para atividade selecionada.
										</ufrn:help>
											
										</td>
									</c:if>
									<c:if test="${!componenteCurricular.obj.estagio && !componenteCurricular.obj.atividadeColetiva }">
										<th width="290px">Carga Horária do Docente:</th>
										<td>
											<h:inputText id="chDedicadaDocente"	value="#{componenteCurricular.obj.detalhes.chDedicadaDocente}" 
																				size="4"
																				maxlength="4"
																				converter="#{ intConverter }"
																				onkeyup="return formatarInteiro(this);"/>
										</td>
									</c:if>
								</tr>
							</c:if>
						</table>
					</td>
				</tr>
				</t:div>
			</c:if>
			
			<c:if test="${not componenteCurricular.portalCoordenadorStricto}">
				<tr>
					<td colspan="2">
						<table width="100%" id="tableDadosGerais">
							<caption>Pré-requisitos, Co-Requisitos e Equivalências</caption>

						<tr>
							<td colspan="2">
							<div class="descricaoOperacao">
								<p style="text-align: center; padding: 5px; font-style: italic">
							 	<b>Atenção!</b> Todas as expressões de pré-requisitos,
								co-requisitos e equivalências devem ser cercadas por parênteses.
								<br />
								<b>Exemplo: ( ( DIM0052 ) E ( DIM0301 OU DIM0053 ) ) </b>
								</p>
							</div>
							
							</td>
						</tr>
						<tr>
							<th width="290px">Pré-Requisitos:</th>
							<td><h:inputText id="preReq"
								value="#{componenteCurricular.preRequisitoForm}" size="90" maxlength="800"
								disabled="#{componenteCurricular.portalCoordenadorStricto}" /></td>
						</tr>
						<tr>
							<th width="290px">Co-Requisitos:</th>
							<td><h:inputText id="coReq"
								value="#{componenteCurricular.coRequisitoForm}" size="90" maxlength="800"
								disabled="#{componenteCurricular.portalCoordenadorStricto}" /></td>
						</tr>
						<tr>
							<th width="290px">Equivalências:</th>
							<td><h:inputText id="equivalencia"
								value="#{componenteCurricular.equivalenciaForm}" size="90" maxlength="800"
								disabled="#{componenteCurricular.portalCoordenadorStricto}" /></td>
						</tr>
					</table>
				</td>
			  </tr>
			</c:if>
			<tr>
			  <td colspan="2">
				 <table width="100%" id="tableDadosGerais">
				   <caption>Outras informações</caption>
							<tr>
								<th width="50%" class="rotulo">Permite Criar Turma:</th>
								<td>
									<ufrn:format type="simNao" valor="${componenteCurricular.obj.permiteCriarTurma}"/>
								</td>
							</tr>
							<tr>
								<th width="50%" class="rotulo">Permite CH Compartilhada entre Docentes:</th>
								<td>
									<ufrn:format type="simNao" valor="${componenteCurricular.obj.detalhes.permiteChCompartilhada}"/>
								</td>
							</tr>
							<tr>
								<th  width="50%" class="rotulo"> Permite Turma com Flexibilidade de Horário:</th>
								<td>
									<a4j:outputPanel id="horarioFlexTurma">
										<ufrn:format type="simNao" valor="${componenteCurricular.obj.permiteHorarioFlexivel}"/>
									</a4j:outputPanel>
								</td>
							</tr>
							<tr>
								<th  width="50%" class="rotulo">Horário Flexível do Docente:</th>
								<td>
									<a4j:outputPanel id="horarioFlexDocente">
										<ufrn:format type="simNao" valor="${componenteCurricular.obj.permiteHorarioDocenteFlexivel}"/>
									</a4j:outputPanel>
								</td>
							</tr>
							<tr>
								<th width="50%" class="rotulo">Necessita de Orientador:</th>
								<td>
									<ufrn:format type="simNao" valor="${componenteCurricular.obj.temOrientador}"/>
								</td>
							</tr>
							<tr>
								<th width="50%" class="rotulo">Exige Horário:</th>
								<td>
									<ufrn:format type="simNao" valor="${componenteCurricular.obj.exigeHorarioEmTurmas}"/>
								</td>
							</tr>
							<c:if test="${componenteCurricular.obj.permiteCriarTurma}" >
								<tr>
									<th width="50%">
										Núm. Máximo de Grupos de Docentes na Turma:
									</th>
									<td>
										<h:inputText size="2" maxlength="2" onkeyup="formatarInteiro(this);"  value="#{componenteCurricular.obj.numMaxDocentes}" id="numMaxoDocente"
												converter="#{ intConverter }"/>								
										<ufrn:help>
										Indica o número máximo de Grupos de docentes simultâneos na turma, no qual os grupos terão a carga horária compartilhada.    
										</ufrn:help>								
									</td>
								</tr>
							</c:if>
							<c:if test="${componenteCurricular.exibeMatriculavelOnLine}">
								<tr>
									<th width="50%">
										Matriculável "On-Line":
									</th>
									<td>
										<h:selectOneRadio id="radioMatriculavelOnline" value="#{componenteCurricular.obj.matriculavel}" styleClass="inline" disabled="#{componenteCurricular.portalCoordenadorStricto}" > 
											<f:selectItems value="#{componenteCurricular.simNao}" />
										</h:selectOneRadio>								
										<ufrn:help>
										Desmarque essa opção caso o aluno não possa se matricular nesse componente durante a matrícula online, sendo necessário dirigir-se à coordenação.
										</ufrn:help>
									</td>
								</tr>
							</c:if>
							<c:if test="${componenteCurricular.exibePrecisaNota}">
								<tr>
									<th width="50%">Obrigatoriedade de ${ (sessionScope.nivel == 'S') ? 'Conceito' : 'Nota Final' }:</th>
									<td>
										<h:selectOneRadio id="radioNecessitaMediaFinal" value="#{componenteCurricular.obj.necessitaMediaFinal}" styleClass="inline" disabled="#{componenteCurricular.portalCoordenadorStricto}">
											<f:selectItems value="#{componenteCurricular.simNao}"  />
										</h:selectOneRadio>
										<ufrn:help>
										Desmarque essa opção caso a atividade não precise de média final para sua consolidação ou validação.
										</ufrn:help>
									</td>
								</tr>
							</c:if>					
							<c:if test="${componenteCurricular.exibeTurmaSemSolicitacao}">
								<tr>
									<th width="50%">Pode criar turma sem solicitação:</th>
									<td>
										<h:selectOneRadio id="radioTurmasSemSolicitacao" value="#{componenteCurricular.obj.turmasSemSolicitacao}" styleClass="inline" disabled="#{componenteCurricular.portalCoordenadorStricto}" > 
											<f:selectItems value="#{componenteCurricular.simNao}"  />
										</h:selectOneRadio>
										<ufrn:help>
										Marque esta opção caso seja possível criar turma deste componente sem a necessidade de haver uma solicitação de turma.
										</ufrn:help>
									</td>
								</tr>
							</c:if>
							<c:if test="${componenteCurricular.exibeProibeAproveitamento}">
								<tr>
									<th width="50%">Proíbe Aproveitamento:</th>
									<td>
										<h:selectOneRadio id="radioproibeAproveitamento" styleClass="inline" value="#{componenteCurricular.obj.detalhes.proibeAproveitamento}">
											<f:selectItems value="#{componenteCurricular.simNao}"  />
										</h:selectOneRadio>
										<ufrn:help>
										Marque essa opção caso o componente não puder realizar aproveitamento.
										</ufrn:help>
									</td>
								</tr>
							</c:if>			
							<c:if test="${componenteCurricular.exibeSubTurma}">					
								<tr>
									<th width="50%"> Permitir Criar subturmas desse componente curricular:</th>
									<td>
										<h:selectOneRadio id="radioAceitaSubTurma" value="#{componenteCurricular.obj.aceitaSubturma}" styleClass="inline" > 
											<f:selectItems value="#{componenteCurricular.simNao}"  />
										</h:selectOneRadio>									
										<ufrn:help>
										Marque esta opção caso seja um componente que permita a criação de subturmas.
										</ufrn:help>
									</td>
								</tr>
							</c:if>
							<c:if test="${componenteCurricular.mostrarConteudoVariavel}">
								<tr>
									<th width="50%">Conteúdo Variável:</th>
									<td>
										<h:selectOneRadio  value="#{componenteCurricular.obj.conteudoVariavel}" styleClass="inline" id="checkConteudoVariavel" onclick="submit();"> 
											<f:selectItems value="#{componenteCurricular.simNao}"  />
										</h:selectOneRadio>
										<ufrn:help>
										Um componente curricular com conteúdo variável permite a multiplicidade de matrículas/aproveitamentos em um mesmo ano e período.
										</ufrn:help>
									</td>
								</tr>
							</c:if>
							
							<t:div rendered="#{componenteCurricular.obj.conteudoVariavel}">
							
								<tr>
									<th width="50%">Quantidade máxima de matrículas:</th>
									<td>
										<h:inputText size="2" maxlength="2" onkeyup="formatarInteiro(this);"  value="#{componenteCurricular.obj.qtdMaximaMatriculas}"
											converter="#{ intConverter }"/>								
									
										<ufrn:help>
										Digite a quantidade máxima de matrículas que um discente poderá efetuar neste componente curricular.
										Por exemplo, se este valor for definido com 2, um discente poderá concluir com aprovação o mesmo 
										componente curricular duas vezes. 
										Este valor não é utilizado caso o discente não tenha sido aprovado no componente curricular.
										</ufrn:help>   
									</td>
								</tr>
							
							</t:div>
							<c:if test="${componenteCurricular.exibeQtdAvaliacoes}">							
								<tr>
									<th width="50%">Quantidade de Avaliações:</th>
									<td>
										<h:selectOneMenu id="numunidades" value="#{componenteCurricular.obj.numUnidades}">
											<f:selectItems value="#{componenteCurricular.numUnidadesPossiveis}" />
										</h:selectOneMenu>
									</td>
								</tr>
							</c:if>
							<c:if test="${componenteCurricular.exibeEmenta}">
								<tr>
									<th valign="top" id="campoEmenta" class="required">
										<c:if test="${componenteCurricular.obj.atividade}">
											Descrição:
										</c:if>
										<c:if test="${not componenteCurricular.obj.atividade}">
											Ementa:
										</c:if>
									</th>
									<td colspan="2">
										<h:inputTextarea value="#{ componenteCurricular.obj.detalhes.ementa }" disabled="#{componenteCurricular.obj.bloco}" 
													 style="width: 500px;" id="ementa" cols="85" rows="4"
													 onkeyup="qteCaracteres('form:ementa', 'txtCaracteresDigitados', 2000)" 
													 onblur="qteCaracteres('form:ementa', 'txtCaracteresDigitados', 2000)"/>
										<br/>
										<strong>(<span id="txtCaracteresDigitados">0 digitados</span>/2000 caracteres)</strong>
									</td>
								</tr>					
							</c:if>
							<c:if test="${componenteCurricular.exibeBibliografia}">
								<tr>
									<c:if test="${not componenteCurricular.obj.stricto}">
										<th valign="top" id="campoReferenciaObrigatorio" class="required">Referências:</th>
									</c:if>
									<c:if test="${componenteCurricular.obj.stricto}">
										<th valign="top" id="campoReferencia">Referências:</th>
									</c:if>
									<td>
										<h:inputTextarea id="bibliografia"
											value="#{componenteCurricular.obj.bibliografia}" cols="85"
											rows="4" />
									</td>
								</tr>				
							</c:if>
					</table>
				   </td>
				</tr>
										
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton id="voltar" value="<< Tipo do Componente Curricular" action="#{componenteCurricular.voltarDadosGerais}" />
							<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}" action="#{componenteCurricular.cancelar}" immediate="true" />
							<h:commandButton id="avancar" value="Avançar >>" action="#{componenteCurricular.submeterDadosGerais}" />
						</td>
					</tr>
				</tfoot>
		</table>
	</h:form>
	<br>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
<script type="text/javascript">
	function calcularCHTotal() {
		var total = 0;
		if ($('form:crAula') != null)
			total += $('form:crAula').value * ${componenteCurricular.horasCreditoTeorico};
		if ($('form:crLab') != null)
			total += $('form:crLab').value * ${componenteCurricular.horasCreditoPratico};
		if ($('form:crEad') != null)
			total += $('form:crEad').value * ${componenteCurricular.horasCreditoTeorico}
		if ($('form:chAula') != null && $('form:chAula').value != '')
			total += parseInt($('form:chAula').value, 10); 
		if ($('form:chLab') != null && $('form:chLab').value != '')
			total += parseInt($('form:chLab').value, 10);
		if ($('form:chEad') != null && $('form:chEad').value != '')
			total += parseInt($('form:chEad').value, 10); 
		if ($('form:chNaoAula') != null && $('form:chNaoAula').value != '')
			total += parseInt($('form:chNaoAula').value, 10);
		if (isNaN(total))
			$('chtotal').innerHTML = '0h';
		else
			$('chtotal').innerHTML = total + 'h';
	}
	function calcularCHTeorico() {
		if ($('form:crAula') != null && isNaN($('form:crAula').value))
			$('chteorico').innerHTML = '0h';
		else
			$('chteorico').innerHTML = $('form:crAula').value * ${componenteCurricular.horasCreditoTeorico}+'h';
		calcularCHTotal();
	}

	function calcularCHPratico() {
		if ($('form:crLab') != null && isNaN($('form:crLab').value))
			$('chpratico').innerHTML = '0h';
		else
			$('chpratico').innerHTML = $('form:crLab').value * ${componenteCurricular.horasCreditoPratico}+'h';
		calcularCHTotal();
	}

	function calcularCHEad() {
		if ($('form:crEad') != null && isNaN($('form:crEad').value))
			$('chead').innerHTML = '0h';
		else
			$('chead').innerHTML = $('form:crEad').value * ${componenteCurricular.horasCreditoTeorico}+'h';
		calcularCHTotal();
	}

	if( $('form:checkCursoAtual_Novo:0') != null && $('form:checkCursoAtual_Novo:1') != null ){
		function informarCurso() {
			if ($('form:checkCursoAtual_Novo:0').checked) {
				$('form:curso_atual').style.display = "block";
				$('form:curso_novo').style.display = "none";
			} else if ($('form:checkCursoAtual_Novo:1').checked) {
				$('form:curso_atual').style.display = "none";
				$('form:curso_novo').style.display = "block";
				$('form:curso_atual').value = 0;
			}
		}
		informarCurso();	
	}

	function qteCaracteres(campo, texto, limite) {
		if ($(campo).value.length <= limite) {
			$(texto).innerHTML=$(campo).value.length + ' digitados';
		} else {
			$(campo).value = $(campo).value.substr(0,limite);
			$(texto).innerHTML= limite + ' digitados';
		}
	}
	
	qteCaracteres('form:ementa', 'txtCaracteresDigitados', 2000);
	
	calcularCHTotal();
	calcularCHTeorico();
	calcularCHPratico();
	calcularCHEad();
	
</script>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>