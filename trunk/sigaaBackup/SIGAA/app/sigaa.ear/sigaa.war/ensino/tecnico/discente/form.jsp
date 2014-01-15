<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<f:view>
	<h2> <ufrn:subSistema /> > ${discenteTecnico.obj.id > 0 ? 'Atualização' : 'Cadastro'} de Aluno</h2>
	
	<c:if test="${discenteTecnico.obj.id > 0}">
		<c:set value="#{discente}" var="discenteMBean" />
		<c:set value="#{discenteTecnico.obj}" var="discente" />
		<%@ include file="/graduacao/info_discente.jsp"%>
		<c:set value="#{discenteMBean}" var="discente" />
	</c:if>
	
	<h:form id="formDiscenteTecnico">
	<h:inputHidden value="#{discenteTecnico.obj.id}"/>
	<table class="formulario" style="width: 100%">
		<caption>Dados do Discente</caption>
		<tbody>
			<tr>
				<th><b>Unidade Responsável:</b></th>
				<td >
					${discenteTecnico.obj.gestoraAcademica.nome} 
				</td>
			</tr>
			<tr>
				<th style="width: 20%;"><b> Nome:</b> </th>
				<td>${discenteTecnico.obj.nome}</td>
			</tr>
			
			<c:if test="${not empty discenteTecnico.obj.processoSeletivo && not empty discenteTecnico.obj.processoSeletivo.editalProcessoSeletivo}">
				<tr>
					<th><b>Processo Seletivo:</b></th>
					<td>
						<h:outputText value="#{discenteTecnico.obj.processoSeletivo.editalProcessoSeletivo.nome}"/>
					</td>
				</tr>	
			</c:if>
			
			<c:if test="${discenteTecnico.discenteAntigo}">
				<tr>
					<th class="required"> Matrícula: </th>
					<td>
						<h:inputHidden value="#{discenteTecnico.discenteAntigo}" />
						<h:inputText id="matricula" size="10" maxlength="10" value="#{ discenteTecnico.obj.matricula }"
						  onkeyup="formatarInteiro(this);" rendered="#{discenteTecnico.discenteAntigo}" />
					</td>
				</tr>
				<tr>
					<th class="required">Ano-Período Inicial: </th>
					<td>
						<c:if test="${ discenteTecnico.obj.id == 0 }">
							<h:inputText id="ano" size="4" onkeyup="formatarInteiro(this);" maxlength="4" 
							value="#{ discenteTecnico.obj.anoIngresso }" /> -
							<h:inputText id="periodo" size="1" onkeyup="formatarInteiro(this);" maxlength="1" 
							value="#{ discenteTecnico.obj.periodoIngresso }" />
						</c:if>
						<c:if test="${ discenteTecnico.obj.id > 0}">
							<h:outputText id="anoTexto" value="#{ discenteTecnico.obj.anoIngresso }"/> -
							<h:outputText id="periodoTexto" value="#{ discenteTecnico.obj.periodoIngresso }"/>
						</c:if>					
					</td>
				</tr>
	
				<tr>
					<th class="required">Status: </th>
					<td>
						<h:selectOneMenu value="#{discenteTecnico.obj.status}" id="status" style="width: 40%;">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"  />
							<f:selectItems value="#{discenteTecnico.statusCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
		
			<tr>
					<c:choose>
						<c:when test="${discenteTecnico.obj.hasVariosCursos}">
							<th class="obrigatorio">
								<h:outputText value="Curso:" />
							</th> 
							<td>
								<a4j:region>
									<h:selectOneMenu id="curso" 
										value="#{discenteTecnico.obj.estruturaCurricularTecnica.cursoTecnico.id}" 
									 	style="width: 40%" valueChangeListener="#{discenteTecnico.carregarCurriculosTurmasEntrada}" >
										<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
										<f:selectItems value="#{discenteTecnico.cursosCombo}" />
										<a4j:support event="onchange" reRender="curriculo,turmaEntrada" />
									</h:selectOneMenu>
									<a4j:status>
							                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
						            </a4j:status>
								</a4j:region>
							</td>
						</c:when>
						<c:otherwise>
							<th>
								<h:outputText value="Curso:" style="font-weight: bold;"/>
							</th>
							<td> 
								${discenteTecnico.obj.estruturaCurricularTecnica.cursoTecnico.nome}
							</td>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>

			<tr id="trCurriculo">
				<th class="required">Currículo:</th>
				<td>
					<a4j:region>
					<h:selectOneMenu id="curriculo" value="#{discenteTecnico.obj.estruturaCurricularTecnica.id}" >
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{discenteTecnico.curriculosCombo}" />
					</h:selectOneMenu>
					<a4j:status>
				                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
			            </a4j:status>
					</a4j:region>
				</td>
			</tr>	
			
			<tr>
				<th class="required">Turma de Entrada:</th>
				<td colspan="4">
					<h:selectOneMenu id="turmaEntrada" value="#{discenteTecnico.obj.turmaEntradaTecnico.id}" >
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{discenteTecnico.turmasCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>	
			
			<tr>
				<th>Forma de Ingresso:</th>
				<td>
					<h:selectOneMenu value="#{discenteTecnico.obj.formaIngresso.id}" id="formaIngresso" style="width: 40%" >
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"  />
						<f:selectItems value="#{discenteTecnico.formasIngressoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th>Regime do Aluno:</th>
				<td>
					<h:selectOneMenu value="#{discenteTecnico.obj.tipoRegimeAluno.id}" id="regimeAluno" style="width: 40%" >
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"  />
						<f:selectItems value="#{tipoRegimeAluno.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>	
			
			<tr>
				<th>Concluiu ensino médio?</th>
				<td>
					<h:selectOneRadio id="Bolsista" value="#{discenteTecnico.obj.concluiuEnsinoMedio}">
						<f:selectItem itemLabel="Sim" itemValue="true"/>
						<f:selectItem itemLabel="Não" itemValue="false"/>
					</h:selectOneRadio>
				</td>
			</tr>	
			
			<tr>
				<th valign="top">Observação:</th>
				<td colspan="4">
				<h:inputTextarea value="#{discenteTecnico.obj.observacao}" cols="60" rows="8" />
				</td>
			</tr>		
		</tbody>
		<tfoot>
			<tr>
			<td colspan="2">
				<c:if test="${discenteTecnico.obj.id <= 0}">
					<h:commandButton value="<< Dados Pessoais" id="DadosPessoais" action="#{discenteTecnico.telaDadosPessoais}"/>
				</c:if>
				<h:commandButton value="Cancelar" id="Cancelar" action="#{ discenteTecnico.cancelar }" onclick="#{confirm}"/>
				<h:commandButton value="Próximo Passo >>" id="Proximo" action="#{ discenteTecnico.submeterDadosDiscente }"/>
			</td>
			</tr>
		</tfoot>
	</table>

	</h:form>
</f:view>

	<br>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	</center>


<script>
function exibirCurriculo(sel){
	var val = sel.options[sel.selectedIndex].value;
		if (val != "1") {
			$('spanInicio').style.display='none';
		} else {
			$('spanInicio').style.display='inline';
		}
} 
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>