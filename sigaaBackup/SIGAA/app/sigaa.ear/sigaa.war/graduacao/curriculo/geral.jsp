<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<style>
		input.numerico { text-align: right; }
		span.info {color: #555; font-size: 0.95em;}
	</style>

	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:messages showDetail="true"></h:messages>
	<h:outputText value="#{curriculo.create}"></h:outputText>
	<h2 class="title"><ufrn:subSistema /> &gt; Estrutura Curricular  &gt; Dados Gerais</h2>

	<h:form id="formulario">

		<table class="formulario" width="100%" style="margin-left: 0;">
			<caption class="formulario">Dados do Currículo</caption>
			<tr>
				<th class="required" width="17%">Código: </th>
				<td>
					<h:inputText size="7" maxlength="7" value="#{curriculo.obj.codigo}" id="codigo" disabled="#{curriculo.obj.id > 0}"/>
				</td>
			</tr>
			<c:if test="${not curriculo.graduacao}">
				<tr>
					<th class="required"> Programa:</th>
					<td>
						<h:selectOneMenu value="#{ curriculo.obj.curso.unidade.id }" id="selectPrograma"
							valueChangeListener="#{curriculo.selecionarPrograma}" >
							<a4j:support event="onchange" reRender="selectCurso" />
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
							
							<c:if test="${ acesso.ppg and curriculo.portalPpg }">
								<f:selectItems value="#{ unidade.allProgramaPosCombo }"/>
							</c:if>
							
							<c:if test="${ curriculo.portalComplexoHospitalar }">
								<f:selectItems value="#{ unidade.allProgramaResidenciaCombo }"/>
							</c:if>
							
							
							<c:if test="${ curriculo.portalCoordenadorStricto }">
								<f:selectItems value="#{ curso.allProgramasAcesso }"/>
							</c:if>
							
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>			
			<tr>
				<th class="required">Curso:</th>
				<td>
					<h:selectOneMenu id="selectCurso" style="width: 98%;" value="#{curriculo.obj.curso.id }" 
							valueChangeListener="#{curriculo.carregarMatrizesParametrosByCurso }">
						<a4j:support event="onchange" reRender="selectMatriz,minmax" />
						<a4j:status>
							<f:facet name="start" >
								<h:graphicImage value="/img/indicator.gif" />
							</f:facet>
						</a4j:status>
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{curriculo.cursosCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<c:if test="${curriculo.graduacao}">
				<tr>
					<th class="required">Matriz Curricular:</th>
					<td>
						<h:selectOneMenu id="selectMatriz" value="#{curriculo.obj.matriz.id }" style="width: 98%;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{curriculo.possiveisMatrizes}" />
						</h:selectOneMenu>	
					</td>
				</tr>
				</c:if>

				<tr>
					<th class="required">Ano-Período de Implantação:</th>
					<td>
						<h:inputText size="4" maxlength="4" value="#{curriculo.obj.anoEntradaVigor}" onkeyup="formatarInteiro(this);" id="inputAnoEntradaVigor"/>
						 <b>.</b> <h:inputText	size="1" maxlength="1" value="#{curriculo.obj.periodoEntradaVigor}" onkeyup="formatarInteiro(this);" id="inputPeriodoEntradaVigor"/>
					</td>
				</tr>
				
			<c:if test="${curriculo.residencia}">
				<tr>
					<th>Carga Horária Optativa Mínima:</th>
					<td>
						<c:if test="${curriculo.podeAlterarChEObrigatorias}">
							<h:inputText size="4" maxlength="4" value="#{curriculo.obj.chOptativasMinima}" onkeyup="formatarInteiro(this);"  styleClass="numerico" id="inputChOptativasMinima" />
						</c:if>
						<c:if test="${!curriculo.podeAlterarChEObrigatorias}">
							<h:outputText value="#{curriculo.obj.chOptativasMinima}" id="outputChoptativaMinima"/>
						</c:if>
						 horas
					</td>
				</tr>
				<tr>
					<th> 
						Carga Horária Por Período Letivo Mínima:
					 </th>
					<td width="16%">
						<h:inputText size="4" maxlength="4" value="#{curriculo.obj.chMinimaSemestre}"  
						onkeyup="formatarInteiro(this);" styleClass="numerico" id="inputChMinimaSemestre"/> horas
					</td>
				</tr>
			</c:if>
			
			<c:if test="${!curriculo.residencia}">
				<tr>
					<th class="required">Carga Horária Optativa Mínima:</th>
					<td>
						<c:if test="${curriculo.podeAlterarChEObrigatorias}">
							<h:inputText size="4" maxlength="4" value="#{curriculo.obj.chOptativasMinima}" converter="#{ intConverter }"  onkeyup="formatarInteiro(this);"  styleClass="numerico" id="inputChOptativasMinima" />
						</c:if>
						<c:if test="${!curriculo.podeAlterarChEObrigatorias}">
							<h:outputText value="#{curriculo.obj.chOptativasMinima}" id="outputChoptativaMinima"/>
						</c:if>
						 horas
					</td>
				</tr>
				<tr>
					<th class="required"> 
						Carga Horária Por Período Letivo Mínima:
					 </th>
					<td width="16%">
						<h:inputText size="4" maxlength="4" value="#{curriculo.obj.chMinimaSemestre}"  
						onkeyup="formatarInteiro(this);" styleClass="numerico" id="inputChMinimaSemestre"/> horas
					</td>
				</tr>
			</c:if>
			<c:if test="${curriculo.graduacao}">
				<tr>
					<th class="required"> 
						Carga Horária de Componentes Eletivos Máxima:
					 </th>
					<td width="16%">
						<h:inputText size="4" maxlength="4" value="#{curriculo.obj.maxEletivos}"  
						converter="#{ intConverter }" onkeyup="formatarInteiro(this);" styleClass="numerico" id="inputChMaxCompEletivos"/> horas 
						<h:outputText id="minmax" value="(#{curriculo.minComponenteEletivo} - #{curriculo.maxComponenteEletivo})"/> 
					</td>
				</tr>			
			</c:if>
			<c:if test="${!curriculo.desabilitaAproveitamento}">
				<tr>
					<th>Aproveitar Componentes de Outro Currículo</th>
					<td><h:selectBooleanCheckbox id="aproveitar" value="#{curriculo.aproveitarComponentes}"/>
					<ufrn:help img="/img/ajuda.gif">
					ATENÇÃO! Ao marcar essa opção o usuário terá apenas uma chance no próximo passo
					para aproveitar os componentes
					de qualquer currículo já cadastrado.
					</ufrn:help>
					</td>
				</tr>
			</c:if>
			<tr>	
				<td colspan="2" class="subFormulario">Créditos Por Período Letivo:</td>
			</tr>
			
			<c:if test="${!curriculo.residencia}">
				<tr>
					<th class="required">Mínimo: </th>
					<td width="25%">
						<h:inputText size="4" maxlength="4" value="#{curriculo.obj.crMinimoSemestre}" converter="#{ intConverter }" onkeyup="formatarInteiro(this);" styleClass="numerico" id="inputCrMinimoSemestre"/> 
					</td>
				</tr>
				<tr>
					<th class="required">Médio: </th>
					<td width="25%">
						<h:inputText size="4" maxlength="4" value="#{curriculo.obj.crIdealSemestre}" converter="#{ intConverter }"  onkeyup="formatarInteiro(this);" styleClass="numerico" id="inputCrIdealSemestre"/> 
					</td>
				</tr>
				<tr>
					<th class="required">Máximo: </th>
					<td width="25%">
						<h:inputText size="4" maxlength="4" value="#{curriculo.obj.crMaximoSemestre}" converter="#{ intConverter }" onkeyup="formatarInteiro(this);" styleClass="numerico" id="inputCrMaximoSemestre"/> 
					</td>
				</tr>
			</c:if>
			
			<c:if test="${curriculo.residencia}">
				<tr>
					<th>Mínimo: </th>
					<td width="25%">
						<h:inputText size="4" maxlength="4" value="#{curriculo.obj.crMinimoSemestre}" onkeyup="formatarInteiro(this);" styleClass="numerico" id="inputCrMinimoSemestre"/> 
					</td>
				</tr>
				<tr>
					<th>Médio: </th>
					<td width="25%">
						<h:inputText size="4" maxlength="4" value="#{curriculo.obj.crIdealSemestre}"  onkeyup="formatarInteiro(this);" styleClass="numerico" id="inputCrIdealSemestre"/> 
					</td>
				</tr>
				<tr>
					<th>Máximo: </th>
					<td width="25%">
						<h:inputText size="4" maxlength="4" value="#{curriculo.obj.crMaximoSemestre}" onkeyup="formatarInteiro(this);" styleClass="numerico" id="inputCrMaximoSemestre"/> 
					</td>
				</tr>
			</c:if>
			
			<tr>
				<td colspan="2" class="subFormulario">
					Prazo Para Conclusão ${curriculo.graduacao? '(em semestres)' : '(em meses)'}
				</td>
			</tr>
			<tr>
				<th class="required">Mínimo:</th>
				<td width="25%">
					<h:inputText size="4" maxlength="4" value="#{curriculo.obj.semestreConclusaoMinimo}" 
					onkeyup="formatarInteiro(this);" styleClass="numerico" id="inputSemestreConclusaoMinimo"/> 
				</td>
			</tr>
			<tr>
				<th class="required">
					${curriculo.graduacao? 'Médio:' : 'Regulamentar:'}
				</th>
				<td width="25%">
					<h:inputText size="4" maxlength="4" value="#{curriculo.obj.semestreConclusaoIdeal}" 
					onkeyup="formatarInteiro(this);" styleClass="numerico" id="inputSemestreConclusaoIdeal"/> 
				</td>
			</tr>
			<tr>
				<th class="required">Máximo:</th>
				<td width="25%">
					<h:inputText size="4" maxlength="4" value="#{curriculo.obj.semestreConclusaoMaximo}" 
					onkeyup="formatarInteiro(this);"  styleClass="numerico" id="inputSemestreConclusaoMaximo"/> 
				</td>
			</tr>
			
			<c:if test="${curriculo.obj.id  == 0}">
			<tr>
				<th class="required">Ativo:</th>
				<td width="25%">
					<h:selectOneRadio value="#{curriculo.obj.ativo}" id="inputAtivo">
						<f:selectItems value="#{curriculo.simNao}"/> 
					</h:selectOneRadio>
				</td>
			</tr>
			</c:if>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}" action="#{curriculo.cancelarOperacao}" /> 
						<h:commandButton id="avancar" value="Próximo Passo >>" action="#{curriculo.submeterDadosGerais}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

	<script type="text/javascript">$('formulario:codigo').focus();</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
