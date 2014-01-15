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
	<c:choose>
		<c:when test="${acesso.chefeDepartamento}">
			<%@include file="/portais/docente/menu_docente.jsp" %>
		</c:when>
		<c:otherwise>
			<%@include file="/graduacao/menu_coordenador.jsp" %>
		</c:otherwise>
	</c:choose>
	
	<h2> <ufrn:subSistema/> &gt; ${docenteTurmaBean.tituloOperacao } &gt; Definir Docentes</h2>

	<c:set var="turma" value="${docenteTurmaBean.obj}"/>
	<%@include file="/ensino/turma/info_turma.jsp"%>

	<br/>
	<h:form id="formDocentes">
		<table class="formulario" width="90%">
			<caption>Docentes </caption>
			<tr>
				<th class="required" width="15%">Docente:</th>
				<td style="text-align: left;">
					<h:inputHidden value="#{docenteTurmaBean.docenteTurma.docente.id}" id="idDocente"/>
					<c:set var="idAjax" value="formDocentes:idDocente"/>
					<c:set var="nomeAjax" value="docenteTurmaBean.docenteTurma.docente.nome"/>
					<c:set var="inativos" value="true"/>
					<%@include file="/WEB-INF/jsp/include/ajax/docente_jsf.jsp" %>
				</td>
			</tr>
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
			<tr>
				<th class="required">Carga Horária:</th>
				<td align="left">
					<input type="hidden" id="chComponente" value="${ docenteTurmaBean.obj.chTotalTurma }"/>
					<h:inputText id="porcentagemCh" value="#{ docenteTurmaBean.docenteTurma.porcentagemChDedicada }" size="4"maxlength="3"
						title="Carga Horária" 
						onkeyup="formatarInteiro(this);calcularChDocente(this);" rendered="#{not docenteTurmaBean.turmaEad }"/>
					<h:outputText value="%" rendered="#{not docenteTurmaBean.turmaEad }"/>
					<span id="chDocente"></span>

					<h:outputText value="A carga horária dedicada será cadastrada pela SEDIS" rendered="#{ docenteTurmaBean.turmaEad }"/> 
				</td>
			</tr>
			<c:if test="${docenteTurmaBean.permiteHorarioDocenteFlexivel && !docenteTurmaBean.turmaEad}">
				<tr>
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
				<tr>
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
			</c:if>
			<tfoot>
			<tr>
				<td colspan="4" align="center" class="caixaCinza">
					<h:commandButton value="Adicionar" actionListener="#{ docenteTurmaBean.adicionarDocenteTurma }" id="btnAdicionarDocente"/>
				</td>
			</tr>
			</tfoot>
		</table>
	</h:form>

	<table width="90%" class="formulario" id="tabela">
		<tr><td>
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
					
					<h:inputText id="grupoinputAtualizaChDedicadaPeriodo_${grupo.key}"  maxlength="3" size="3" value="#{ docenteTurma.chDedicadaPeriodo }" rendered="#{!docenteTurmaBean.turmaGraduacao}" onkeyup="formatarInteiro(this);">
						<a4j:support event="onblur" actionListener="#{docenteTurmaBean.atualizaCHDocenteGrupo}" reRender="painelErrosAjax">
							<f:attribute name="grupo" value="#{grupo.key}"/>
							<f:attribute name="docenteTurma" value="#{docenteTurma.id}"/>
						</a4j:support>
					</h:inputText>
							
					<h:inputText id="grupoinputAtualizaPorcentagemCHPeriodo_${grupo.key}"  maxlength="3" size="3" value="#{ docenteTurma.porcentagemChDedicada }" rendered="#{docenteTurmaBean.turmaGraduacao}" onkeyup="formatarInteiro(this);">
						<a4j:support event="onblur" actionListener="#{docenteTurmaBean.atualizaCHDocenteGrupo}" reRender="painelErrosAjax,formListaDocentesGrupo_#{grupo.key} ">
							<f:attribute name="grupo" value="#{grupo.key}"/>
							<f:attribute name="docenteTurma" value="#{docenteTurma.id}"/>
						</a4j:support>
					</h:inputText>
					
					<h:outputText value="% " rendered="#{docenteTurmaBean.turmaGraduacao}"/>
					<h:outputText value="h " rendered="#{!docenteTurmaBean.turmaGraduacao}"/>
					
					<h:outputText value="#{ docenteTurma.chDedicadaPeriodo }" rendered="#{docenteTurmaBean.turmaGraduacao && !docenteTurmaBean.turmaEad}" />
					<h:outputText value="h " rendered="#{docenteTurmaBean.turmaGraduacao && !docenteTurmaBean.turmaEad && (docenteTurma.chDedicadaPeriodo != null) }"/>
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
	<table width="90%" class="formulario">
		<tfoot>
		<tr>
			<td>
				<h:form>
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
	
		// Muda o nome do jQuery para J, evitando conflitos com a Prototype.
		var J = jQuery.noConflict();
	
		var chComponente = null;
		if (getEl('chComponente') != null)
			chComponente = getEl('chComponente').dom.value;

		function calcularChDocente (porcentagemElem) {

			var porcentagem = porcentagemElem.value;
			var chDocenteElem = document.getElementById("chDocente");
			var chDocente = (int) (chComponente * porcentagem / 100);

			var chDocenteTexto = " " + chDocente + "h";
			if (isNaN(chDocente))
				chDocenteTexto = "";
			
			chDocenteElem.innerHTML = chDocenteTexto;
			
		}	

		J(document).ready(function () {
			calcularChDocente(document.getElementById("formDocentes:porcentagemCh"));
		});
	</script>
		
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