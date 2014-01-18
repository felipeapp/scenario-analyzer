<%@page import="br.ufrn.sigaa.ensino.jsf.DocenteTurmaMBean"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript">
	JAWR.loader.script('/javascript/prototype-1.6.0.3.js');
</script>


<style> .large { width: 40%;}  .medium { width: 30%;}  .small { width: 10%;}  .small2 { width: 5%;} </style>



<a4j:keepAlive beanName="buscaTurmaBean"/>
<a4j:keepAlive beanName="docenteTurmaBean" />

<f:view>
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	<c:if test="${acesso.chefeDepartamento}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<%@include file="/stricto/menu_coordenador.jsp" %>

	<h2> <ufrn:subSistema/> &gt; ${docenteTurmaBean.tituloOperacao } &gt; Definir Docentes</h2>

	<c:set var="turma" value="${docenteTurmaBean.obj}"/>
	<%@include file="/ensino/turma/info_turma.jsp"%>

	<br/>

	<h:form id="formDocentes">
		<table class="formulario" width="90%">
			<caption>Docentes </caption>
			<tr>
				<td>
				<table width="100%">
					<tr>
						<th></th>
						<c:if test="${!docenteTurmaBean.selecionarDocentesPrograma}">
							<th class="required">Docente:</th>
							<td style="text-align: left;">
								<h:inputHidden value="#{docenteTurmaBean.docenteTurma.docente.id}" id="idDocente"/>
								<c:set var="idAjax" value="formDocentes:idDocente"/>
								<c:set var="nomeAjax" value="docenteTurmaBean.docenteTurma.docente.nome"/>
								<c:set var="inativos" value="true"/>
								<%@include file="/WEB-INF/jsp/include/ajax/docente_jsf.jsp" %>
							</td>
						</c:if>
					</tr>
				</table>
				</td>
				<c:if test="${docenteTurmaBean.selecionarDocentesPrograma}">
					<td style="text-align: left;">
						<h:inputHidden value="#{docenteTurmaBean.tipoBuscaDocente}" id="idTipoBusca"/>
						<div id="abas-equipePrograma">
							<c:if test="${ !docenteTurmaBean.lato }">
								<div id="membrosPrograma"  class="aba">
									<table width="100%">
										<c:choose>
											<c:when test="${!acesso.ppg && !docenteTurmaBean.lato }">
												<tr>
													<th class="required">Docente:</th>
													<td style="text-align: left;">
														<h:selectOneMenu  id="docentePrograma" value="#{ docenteTurmaBean.equipe.id }" rendered="#{!acesso.ppg}">
															<f:selectItem itemValue="0" itemLabel="SELECIONE UM DOCENTE DO PROGRAMA"/>
															<f:selectItems value="#{ equipePrograma.docentesProgramaCombo}"/>
														</h:selectOneMenu>
													</td>
												</tr>
											</c:when>
										</c:choose>
										<c:if test="${ acesso.ppg && !docenteTurmaBean.lato }">
											<tr>
												<th class="required">Programa:</th>
												<td style="text-align: left;">
													<h:selectOneMenu value="#{equipePrograma.obj.programa.id}" id="Programa" onchange="submit()"
														valueChangeListener="#{equipePrograma.carregarEquipeDoPrograma}"  rendered="#{acesso.ppg}">
														<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
														<f:selectItems value="#{unidade.allProgramaPosCombo}" />
													</h:selectOneMenu>
												</td>
												</tr>
												<tr>
												<th class="required">Docente:</th>
												<td style="text-align: left;">
													<h:selectOneMenu  id="docentePrograma2" value="#{ docenteTurmaBean.equipe.id }" rendered="#{acesso.ppg}">
														<c:if test="${equipePrograma.obj.programa.id == 0}">
															<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM PROGRAMA --"/>
														</c:if>
														<c:if test="${equipePrograma.obj.programa.id != 0}">
															<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
														</c:if>
														<f:selectItems value="#{ equipePrograma.docentesProgramaObjCombo}"/>
													</h:selectOneMenu>
												</td>
											</tr>
										</c:if>
									</table>		
								</div>
							</c:if>
							<c:if test="${ docenteTurmaBean.lato }">
								<div id="docentesCurso" class="aba">
									<table width="100%">
										<tr>
											<th class="required">Docentes do Curso:</th>
											<td colspan="2" style="text-align: left;">
												<a4j:region id="docentesCurso">
													<td style="text-align: left;">
														<h:selectOneMenu id="docenteCurso" value="#{ docenteTurmaBean.idCorpoDocente }" >
															<f:selectItem itemValue="0" itemLabel="SELECIONE UM DOCENTE DO CURSO"/>
															<f:selectItems value="#{ equipePrograma.docentesProgramaCombo }"/>
														</h:selectOneMenu>
													</td>
												</a4j:region>
											</td>	
										</tr>
									</table>
								</div>
							</c:if>
							<div id="externoPrograma" class="aba">
								<table width="100%">
									<tr>
										<th class="required">Docentes:</th>
										<td colspan="2" style="text-align: left;">
											<a4j:region id="docenteTurma">
												<h:inputText value="#{docenteTurmaBean.docenteTurma.docente.pessoa.nome}" id="nomeDocente" style="width: 400px;"/>
												<rich:suggestionbox width="400" height="100" for="nomeDocente" id="sbDocente"
													minChars="3" nothingLabel="#{servidor.textoSuggestionBox}"
													suggestionAction="#{servidor.autocompleteDocente}" var="_servidor" fetchValue="#{_servidor.nome}"
													onsubmit="$('formDocentes:imgStDocente').style.display='inline';" 
												    oncomplete="$('formDocentes:imgStDocente').style.display='none';">
													<h:column>
														<h:outputText value="#{_servidor.descricaoCompleta}"/>
													</h:column>
												   <a4j:support event="onselect">
														<f:setPropertyActionListener value="#{_servidor.id}" target="#{docenteTurmaBean.docenteTurma.docente.id}" />
												  </a4j:support>								
												</rich:suggestionbox>
												<h:graphicImage id="imgStDocente" style="display:none; overflow: visible;" value="/img/indicator.gif"/>	
											</a4j:region>
										</td>	
									</tr>
								</table>
							</div>
							<div id="docentesExternos" class="aba">
								<table width="100%">
									<tr>
										<th class="required">Docente Externo:</th>
										<td colspan="2" style="text-align: left;">
											<a4j:region id="docenteTurmaExterno">
												<h:inputText value="#{docenteTurmaBean.docenteTurma.docenteExterno.pessoa.nome}" id="nomeDocenteExterno" style="width: 400px;"/>
												<rich:suggestionbox width="400" height="100" for="nomeDocenteExterno" id="sbDocenteExterno"
													minChars="3" 
													suggestionAction="#{docenteExterno.autoCompleteNomeDocenteExterno}" var="_docenteExterno" fetchValue="#{_docenteExterno.nome}"
													onsubmit="$('formDocentes:imgStDocenteExterno').style.display='inline';" 
												    oncomplete="$('formDocentes:imgStDocenteExterno').style.display='none';">
													<h:column>
														<h:outputText value="#{_docenteExterno.nome}"/>
													</h:column>
													<h:column>
														<h:outputText value="#{_docenteExterno.instituicao.nome}"/>
													</h:column>
												   <a4j:support event="onselect">
														<f:setPropertyActionListener value="#{_docenteExterno.id}" target="#{docenteTurmaBean.docenteTurma.docenteExterno.id}" />
												  </a4j:support>								
												</rich:suggestionbox>
												<h:graphicImage id="imgStDocenteExterno" style="display:none; overflow: visible;" value="/img/indicator.gif"/>	
											</a4j:region>
										</td>	
									</tr>
								</table>
							</div>
						</div>					
					</td>
				</c:if>
			</tr>
			
			
			
			<tr>
				<td colspan="2">
					<table>
					<c:if test="${ docenteTurmaBean.obj.disciplina.numMaxDocentes > 1 }">
					<tr>
						<th>Grupo de Docente:</th>
						<td>
							<h:selectOneMenu id="selectGrupo" value="#{ docenteTurmaBean.docenteTurma.grupoDocente }">
								<f:selectItems id="itemsSelectGrupo" value="#{ docenteTurmaBean.gruposDocenteCombo }"/>
							</h:selectOneMenu>
						</td>
					</tr>
					</c:if>
					</table>
				</td>	
			</tr>	
						
			
			<tr>
				<td colspan="2">
					<table>
					<c:if test="${ not docenteTurmaBean.lato }">
						<th></th>
					</c:if>
					<th class="required">Carga Horária:</th>
					<td align="left">
						<h:inputText id="chDedicada" value="#{ docenteTurmaBean.docenteTurma.chDedicadaPeriodo }" size="4"maxlength="4" onkeyup="formatarInteiro(this);"
							rendered="#{not docenteTurmaBean.turmaEad }"/>
						<h:outputText value="h" rendered="#{not docenteTurmaBean.turmaEad }"/>
						<h:outputText value="A carga horária dedicada será cadastrada pela SEDIS" rendered="#{ docenteTurmaBean.turmaEad }"/> 
					</td>
					</table>
				</td>
			</tr>
			<c:if test="${docenteTurmaBean.permiteHorarioDocenteFlexivel && !docenteTurmaBean.turmaEad}">
				<tr>
					<td colspan="2">
						<table>
							<tr>
							<th></th>
							<th class="required">Período:</th>
							<td align="left">
								<t:inputCalendar value="#{docenteTurmaBean.dataInicio}" renderAsPopup="true" size="10" maxlength="10" id="dataInicioHorario"
									popupTodayString="Hoje" renderPopupButtonAsImage="true" onkeypress="formataData(this,event)">
									<f:converter converterId="convertData"/>
								</t:inputCalendar>
								à
								<t:inputCalendar value="#{docenteTurmaBean.dataFim}" renderAsPopup="true" size="10" maxlength="10" id="dataFimHorario"
										popupTodayString="Hoje" renderPopupButtonAsImage="true" onkeypress="formataData(this,event)">
									<f:converter converterId="convertData"/>
								</t:inputCalendar>		 
							</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<table>
							<tr>
							<th></th>
							<th class="required">Horários no Período:</th>
							<td align="left">
								<!-- mapa de horários agrupados por: período, turno (tipo), dia da semana, ordem do horário. -->
								<table width="100%">
									<thead>
										<tr>
											<th style="text-align: center" width="20%">Período</th>
											<th style="text-align: center" width="10%">Domingo</th>
											<th style="text-align: center" width="10%">Segunda</th>
											<th style="text-align: center" width="10%">Terça</th>
											<th style="text-align: center" width="10%">Quarta</th>
											<th style="text-align: center" width="10%">Quinta</th>
											<th style="text-align: center" width="10%">Sexta</th>
											<th style="text-align: center" width="10%">Sábado</th>
										</tr>
									</thead>
									<c:forEach items="#{docenteTurmaBean.mapaHorariosTurma}" var="grupoPeriodo">
										<c:set var="rSpan" value="${fn:length(grupoPeriodo.value)+1}"/>	
										<tr>
											<td width="200" rowspan="${rSpan}" style="text-align: center" >${grupoPeriodo.key }</td>
										</tr>
											<c:forEach items="#{grupoPeriodo.value}" var="grupoTurno">
												<tr>
													<c:forTokens items="1,2,3,4,5,6,7" delims="," var="dia">
														<td>
															<c:forEach items="#{grupoTurno.value}" var="grupoDia">
																<c:choose>
																	<c:when test="${grupoDia.key eq dia }">
																		<c:forEach items="#{grupoDia.value}" var="ordem">
																			<h:selectBooleanCheckbox value="#{ordem.value.selecionado}" id="ordemCheckBox"/>
																			${ordem.value.dia}${ordem.value.horario.turnoChar}${ordem.value.horario.ordem}
																			<br/>
																		</c:forEach>
																	</c:when>
																</c:choose>
															</c:forEach>
														</td>
													</c:forTokens>
												</tr>
											</c:forEach>
									</c:forEach>
								</table>
							</td>
							</tr>
						</table>
					</td>
				</tr>
			</c:if>
			<tr>
				<td colspan="4" align="center" class="caixaCinza">
					<h:commandButton value="Adicionar" actionListener="#{ docenteTurmaBean.adicionarDocenteTurma }" id="btnAdicionarDocente"/>
				</td>
			</tr>
		</table>
	</h:form>
	<table class="formulario" width="90%;">
	<!-- LISTANDO DOCENTES QUANDO NAO EH TURMA DE BLOCO -->
	<c:if test="${ not empty docenteTurmaBean.obj.docentesTurmas }">
		<c:if test="${!docenteTurmaBean.obj.disciplina.bloco}">
			<tr>
				<td colspan="2">
				<div class="infoAltRem" style="width: 100%">
					<img src="/shared/img/delete.gif" style="overflow: visible;"/>: Retirar Docente da Turma 
				</div>
				<table class="subFormulario" style="width: 100%">
				<tr><td align="center">
					
					<c:forEach items="#{docenteTurmaBean.mapaGrupoDocenteDataModel}" var="grupo">
						<h:form id="formListaDocentesGrupo_${grupo.key}" prependId="false">
						<h:dataTable width="100%" var="docenteTurma" value="#{ grupo.value }" styleClass="linhaPar, linhaImpar" columnClasses="large,medium,small,small2" id="datatableDocentesGrupo_${grupo.key}">
							<f:facet name="caption">
								<f:verbatim>
								<c:choose>
									<c:when test="${ fn:length(docenteTurmaBean.mapaGrupoDocenteDataModel) > 1 }">Grupo de Docente 0${grupo.key}</c:when>
									<c:otherwise>Docentes</c:otherwise>
								</c:choose>
								</f:verbatim>
							</f:facet>
							<h:column>
								<f:facet name="header">
									<h:outputText>Docente</h:outputText>
								</f:facet>
								<h:outputText value="#{ docenteTurma.docente.nome }"/>
								<h:outputText value="#{ docenteTurma.docenteExterno.nome }"/>
							</h:column>
							
							<h:column rendered="#{ not docenteTurmaBean.turmaEad }">
								<f:facet name="header">
									<h:outputText>Horário</h:outputText>
								</f:facet>
								<h:outputText value="#{ docenteTurma.descricaoHorario }"/>
							</h:column>
							
							<h:column rendered="#{ not docenteTurmaBean.turmaEad }">
								<f:facet name="header">
									<h:outputText>CH</h:outputText>
								</f:facet>
								<h:inputText id="grupoinputAtualizaChDedicadaPeriodo_${grupo.key}"  maxlength="3" size="3" value="#{ docenteTurma.chDedicadaPeriodo }" onkeyup="formatarInteiro(this);">
									<a4j:support event="onblur" actionListener="#{docenteTurmaBean.atualizaCHDocenteGrupo}" reRender="painelErrosAjax,formListaDocentesGrupo_#{grupo.key}">
										<f:attribute name="grupo" value="#{grupo.key}"/>
										<f:attribute name="docenteTurma" value="#{docenteTurma.id}"/>
									</a4j:support>
								</h:inputText>
						
								<h:outputText value="h " />
							</h:column>
				
							<h:column >
								<div align="right">
								<h:commandLink id="link" actionListener="#{ docenteTurmaBean.removerDocenteTurma }" onclick="#{confirmDelete}">
									<h:graphicImage url="/img/delete.gif" title="Retirar docente da turma"/>
									<f:attribute name="grupo" value="#{grupo.key}"/>
									<f:attribute name="docenteTurma" value="#{docenteTurma.id}"/>
								</h:commandLink>
								</div>
							</h:column>
							
						</h:dataTable>
						</h:form><br/>
					</c:forEach>
				</td></tr>
				</table>
				</td>
			</tr>
		</c:if>
	</c:if>
	<c:if test="${ empty docenteTurmaBean.obj.docentesTurmas}">
		<tr>
			<td colspan="2" style="text-align: center">
				<font color="red"><i><strong>Não há docentes para esta turma.</strong></i></font>
			</td>
		</tr>
	</c:if>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:form id="formBotoes">
					<h:commandButton value="<< Passo Anterior" action="#{ docenteTurmaBean.voltarPassoAnterior }" id="passoAnterior"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ docenteTurmaBean.cancelar }" id="btnCancelar"/>
					<h:commandButton value="Próximo Passo >>" action="#{ docenteTurmaBean.submeterDocentes }" id="btnProximo"/>
				</h:form>
			</td>
		</tr>
	</tfoot>
	</table>
</f:view>
<br/>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	
		
	<script type="text/javascript">
		var Abas = function() {
		
			var setTipoBusca = function(e, aba) {
				var idAba = aba.id;
				var categoria = getEl('formDocentes:idTipoBusca');
				switch(idAba) {
					case 'membrosPrograma': categoria.dom.value = <%= DocenteTurmaMBean.DOCENTE_PROGRAMA %>; break;
					case 'externoPrograma': categoria.dom.value = <%= DocenteTurmaMBean.DOCENTE_EXTERNO_PROGRAMA %>; break;
					case 'docentesExternos':
					case 'docentesTurmaExternos': categoria.dom.value = <%= DocenteTurmaMBean.DOCENTES_EXTERNOS %>; break;
					case 'docentesTurma': categoria.dom.value = <%= DocenteTurmaMBean.DOCENTES_TURMA %>; break;
				}
			};
			return {
			    init : function(){
			    	<c:if test="${!docenteTurmaBean.selecionarDocentesPrograma}">
				        var abas = new YAHOO.ext.TabPanel('abas-docentesTurma');
						abas.on('tabchange', setTipoBusca);
						abas.addTab('docentesTurma', "Docentes");
						abas.addTab('docentesTurmaExternos', "Docentes Externos");
						switch( getEl('formDocentes:idTipoBusca').dom.value ) {
							case ''+<%=1%>:  abas.activate('docentesTurma'); break;
							case ''+<%=2%>:  abas.activate('docentesTurmaExternos'); break;
							default: abas.activate('docentesTurma'); break;
						}
					</c:if>
					<c:if test="${docenteTurmaBean.selecionarDocentesPrograma}">
						var abas = new YAHOO.ext.TabPanel('abas-equipePrograma');
						abas.on('tabchange', setTipoBusca);
						<c:if test="${acesso.algumUsuarioStricto}">
				        abas.addTab('membrosPrograma', "Docentes do Programa")
				        </c:if>
				        <c:if test="${ docenteTurmaBean.lato }">
				        abas.addTab('docentesCurso', "Docentes do Curso");
				        </c:if>
						abas.addTab('externoPrograma', "Docentes Que Não Pertencem ao Programa");
						abas.addTab('docentesExternos', "Docentes Externos");
						switch( getEl('formDocentes:idTipoBusca').dom.value ) {
							case ''+<%=1%>:  abas.activate('membrosPrograma'); break;
							case ''+<%=2%>:  abas.activate('docentesCurso'); break;
							default: abas.activate('membrosPrograma'); break;
						}
					</c:if>
			    }
		    }
		}();
		YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
	</script>

<script type="text/javascript">
	<c:if test="${!acesso.graduacao}">
		buscarDocentePor_${contAjaxDocente}('buscaAjaxDocenteUnidade_${contAjaxDocente}');
	</c:if>
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>