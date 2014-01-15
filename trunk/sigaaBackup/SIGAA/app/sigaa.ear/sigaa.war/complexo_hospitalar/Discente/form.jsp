<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	
	<a4j:keepAlive beanName="discenteResidenciaMedica"></a4j:keepAlive>
	
	<h2> <ufrn:subSistema /> > Cadastro de Aluno Residente </h2>
	
	<c:if test="${discenteResidenciaMedica.obj.id > 0}">
		<c:set value="#{discente}" var="discenteMBean" />
		<c:set value="#{discenteResidenciaMedica.obj}" var="discente" />
		<%@ include file="/graduacao/info_discente.jsp"%>
	<c:set value="#{discenteMBean}" var="discente" />
	</c:if>
	
	<h:form id="formDiscenteResidenciaMedica">
		<h:inputHidden value="#{discenteResidenciaMedica.obj.id}"/>
		<table class="formulario" style="width: 90%">
			<caption>Dados do Discente</caption>
			<tbody>
				<tr>
					<th style="width: 28%;" class="required">Ano-Semestre Inicial: </th>
					<td>
						<h:inputText id="ano" size="4" onkeyup="formatarInteiro(this);" maxlength="4" value="#{ discenteResidenciaMedica.obj.anoIngresso }" readonly="#{discenteStricto.blockAnoSemestre}" disabled="#{discenteStricto.blockAnoSemestre}" /> -
						<h:inputText id="periodo" size="1" onkeyup="formatarInteiro(this);" maxlength="1" value="#{ discenteResidenciaMedica.obj.periodoIngresso }"  readonly="#{discenteStricto.blockAnoSemestre}" disabled="#{discenteStricto.blockAnoSemestre}"/>
					</td>
				</tr>
				
				<tr>
					<th class="required">Mês de Entrada: </th>
					<td>
						<h:selectOneMenu id="mesEntrada" value="#{ discenteResidenciaMedica.obj.mesEntrada }" style="width: 40%" >
							<f:selectItem itemValue="" itemLabel="--> SELECIONE <--"  />
							<f:selectItems value="#{discenteResidenciaMedica.meses}"/>
						</h:selectOneMenu>
					</td>
				</tr>
	
				<tr>
					<th class="required">Local de Graduação:</th>
					<td>
						<h:selectOneMenu value="#{discenteResidenciaMedica.obj.localGraduacao.id}" id="formaIngresso" style="width: 90%" >
							<f:selectItem itemLabel="--> SELECIONE <--" itemValue="0"  />
							<f:selectItems value="#{discenteResidenciaMedica.localGraduacao}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<th class="required">Programa:</th>
					<td>
						<h:selectOneMenu value="#{discenteResidenciaMedica.obj.gestoraAcademica.id}" id="Programa" style="width: 90%" 
								valueChangeListener="#{curriculo.selecionarPrograma}">
							<a4j:support event="onchange" reRender="curso" />
							<f:selectItem itemLabel="--> SELECIONE <--" itemValue="0" />
							<f:selectItems value="#{discenteResidenciaMedica.unidades}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<th class="required">Curso:</th>
					<td>
						<h:selectOneMenu value="#{discenteResidenciaMedica.obj.discente.curso.id}" id="curso" style="width: 90%" 
								valueChangeListener="#{curriculo.carregarCurriculos}">
							<a4j:support event="onchange" reRender="curriculo" />
							<f:selectItem itemLabel="--> SELECIONE <--" itemValue="0" />
							<f:selectItems value="#{curriculo.cursosCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<th class="required">Currículo:</th>
					<td>
						<h:selectOneMenu value="#{discenteResidenciaMedica.obj.discente.curriculo.id}" id="curriculo" style="width: 90%" >
							<f:selectItem itemLabel="--> SELECIONE <--" itemValue="0" />
							<f:selectItems value="#{curriculo.curriculosBase}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<th class="required">Nº do Registro no Conselho Profissional:</th>
					<td>
						<h:inputText value="#{discenteResidenciaMedica.obj.crm}" id="crm" size="11" maxlength="10" onkeyup="return formatarInteiro(this);" />
					</td>
				</tr>
				
				<tr>
					<th class="required">Nível de entrada:</th>
					<td>
						<h:selectOneMenu value="#{discenteResidenciaMedica.obj.nivelEntradaResidente}" id="nivelEntrada">
							<f:selectItem itemLabel="--> SELECIONE <--" itemValue="0" />
							<f:selectItem itemLabel="1º Ano (R1)" itemValue="1" />
							<f:selectItem itemLabel="2º Ano (R2)" itemValue="2" />
							<f:selectItem itemLabel="3º Ano (R3)" itemValue="3" />
							<f:selectItem itemLabel="4º Ano (R4)" itemValue="4" />
							<f:selectItem itemLabel="5º Ano (R5)" itemValue="5" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
				
			<tfoot>
				<tr>
					<td colspan="2">
						<c:choose>
							<c:when test="${discenteResidenciaMedica.obj.id != 0}">
								<h:commandButton value="Confirmar Alteração" id="Cadastrar" action="#{ discenteResidenciaMedica.cadastrar }"/>
								<h:commandButton value="<< Voltar" id="voltar" action="#{ discenteResidenciaMedica.voltar }"/>
								<h:commandButton value="Cancelar" id="Cancelar" action="#{ discenteResidenciaMedica.cancelar }" onclick="#{confirm}" immediate="true"/>
							</c:when>
							<c:otherwise>
								<h:commandButton value="Confirmar" id="Cadastrar" action="#{ discenteResidenciaMedica.cadastrar }"/>
								<h:commandButton value="<< Voltar" id="voltar" action="#{ discenteResidenciaMedica.voltar }"/>
								<h:commandButton value="Cancelar" id="Cancelar" action="#{ discenteResidenciaMedica.cancelar }" onclick="#{confirm}" immediate="true"/>
							</c:otherwise>
						</c:choose>
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

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>