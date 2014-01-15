<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h2> <ufrn:subSistema /> > Cadastro de Aluno de Lato</h2>
	
	<c:if test="${discenteLato.obj.id > 0}">
		<c:set value="#{discente}" var="discenteMBean" />
		<c:set value="#{discenteLato.obj}" var="discente" />
		<%@ include file="/graduacao/info_discente.jsp"%>
		<c:set value="#{discenteMBean}" var="discente" />
	</c:if>
	
	<h:form id="formDiscenteLato">

	<table class="formulario" style="width: 100%">
		<caption>Dados do Discente</caption>
		<tbody>

			<tr>
				<th style="width: 20%;"><b> Nome: </b></th>
				<td>${discenteLato.obj.nome}</td>
			</tr>
			
			<c:if test="${discenteLato.discenteAntigo}">
				<tr>
					<th class="required"> Matrícula: </th>
					<td>
						<h:inputText id="matricula" size="10" maxlength="10" value="#{ discenteLato.obj.matricula }"  onkeyup="formatarInteiro(this);" rendered="#{discenteStricto.discenteAntigo}" />
					</td>
				</tr>
				<tr>
					<th class="required">Status: </th>
					<td>
						<h:selectOneMenu value="#{discenteLato.obj.status}" id="status" style="width: 40%;">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"  />
							<f:selectItems value="#{discente.statusCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			
		
			<tr>
				<th class="required">Curso:</th>
				<td>				
					<a4j:region>
						<h:selectOneMenu id="curso" 
							value="#{discenteLato.obj.turmaEntrada.cursoLato.id}" 
						 	style="width: 75%" valueChangeListener="#{discenteLato.carregarTurmas}" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{discenteLato.cursosCombo}" />
							<a4j:support event="onchange" reRender="formDiscenteLato" />
						</h:selectOneMenu>
						<a4j:status>
				                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
			            </a4j:status>
					</a4j:region>				
				</td>
			</tr>

			<tr>
				<th class="required">Processo Seletivo:</th>
				<td>
					<h:selectOneMenu value="#{discenteLato.obj.processoSeletivo.id}" id="processoSeletivo" style="width: 40%;">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"  />
						<f:selectItems value="#{discenteLato.processosSeletivosCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>	

			<tr>
				<th class="obrigatorio">Turma de Entrada:</th>
				<td colspan="4">
					<h:selectOneMenu id="turmaEntrada" value="#{discenteLato.obj.turmaEntrada.id}" >
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{discenteLato.turmasCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>	
			
			<tr>
				<th>Forma de Ingresso:</th>
				<td>
					<h:selectOneMenu id="formaIngresso" value="#{discenteLato.obj.formaIngresso.id}" >
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{discenteLato.formasIngressoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>		
				<th>Procedência do Aluno:</th>
				<td>
					<h:selectOneMenu id="procedencia" value="#{discenteLato.obj.tipoProcedenciaAluno.id}" >
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{discenteLato.tiposProcedenciaCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th class="required">Ano - Período <br>de Ingresso:</th>
				<td>
					<h:inputText id="ano" size="4" maxlength="4" value="#{ discenteLato.obj.anoIngresso }"  onkeyup="formatarInteiro(this);"  />
					 - <h:inputText id="periodo" size="1" maxlength="1" value="#{ discenteLato.obj.periodoIngresso }"  onkeyup="formatarInteiro(this);"  />
				</td>
			</tr>
			
			<tr>
				<th valign="top">Observação:</th>
				<td colspan="4">
				<h:inputTextarea id="observacao" value="#{discenteLato.obj.observacao}" cols="60" rows="8" />
				</td>
			</tr>		
		</tbody>
		<tfoot>
			<tr>
			<td colspan="2">
				<h:commandButton value="<< Dados Pessoais" id="DadosPessoais" action="#{discenteLato.telaDadosPessoais}"/>
				<h:commandButton value="Cancelar" id="Cancelar" action="#{ discenteLato.cancelar }" onclick="#{confirm}"/>
				<h:commandButton value="Próximo >>" id="Proximo" action="#{ discenteLato.submeterDadosDiscente }"/>
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