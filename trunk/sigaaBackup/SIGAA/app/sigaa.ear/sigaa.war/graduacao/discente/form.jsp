<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h:messages />
	<h2><ufrn:subSistema /> >  Cadastro de Aluno </h2>

	<h:form id="formDiscenteGraduacao" enctype="multipart/form-data">
	<h:inputHidden value="#{discenteGraduacao.obj.id}"/>
	<table class="formulario" style="width: 100%;">
		<caption>Dados do Discente</caption>
		<tbody>
			<tr>
				<th width="18%"> Nome: </th>
				<td> ${discenteGraduacao.obj.nome}</td>
			</tr>
			
			<c:if test="${discenteGraduacao.discenteAntigo}">
			<tr>
				<th class="required"> Matrícula: </th>
				<td>
					<h:inputText id="matricula" size="10" maxlength="10" value="#{ discenteGraduacao.obj.matricula }" rendered="#{discenteGraduacao.discenteAntigo}" >
						<a4j:support actionListener="#{discenteGraduacao.numeroMatriculaListener}" event="onchange" reRender="formDiscenteGraduacao"/>
					</h:inputText>
				</td>
			</tr>
			</c:if>
			
			<tr>
				<th class="required"> Ano-Período Inicial: </th>
				<td>
					<h:inputText id="ano" size="4" maxlength="4" value="#{ discenteGraduacao.obj.anoIngresso }" readonly="#{discenteGraduacao.blockAnoSemestre}" /> -
					<h:inputText id="periodo" size="1" maxlength="1" value="#{ discenteGraduacao.obj.periodoIngresso }"  readonly="#{discenteGraduacao.blockAnoSemestre}" />
				</td>
			</tr>

			<tr>
				<th class="required"> Tipo:</th>
				<td>
					<h:selectOneMenu value="#{discenteGraduacao.obj.tipo}" id="tipo"
						onchange="submit()" valueChangeListener="#{discenteGraduacao.filtraFormaEntrada}" style="width: 40%;" >
						<f:selectItem itemLabel="--> SELECIONE <--" itemValue="0"  />
						<f:selectItems value="#{discente.tipoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required"> Status:</th>
				
				<td>
					<h:selectOneMenu value="#{discenteGraduacao.obj.status}" id="status" style="width: 40%;" 
						rendered="#{!discenteGraduacao.blockStatus}" onchange="submit()" >
						<f:selectItem itemLabel="--> SELECIONE <--" itemValue="0"  />
						<f:selectItems value="#{discente.statusCombo}" />
					</h:selectOneMenu>
					<h:outputText value="#{discenteGraduacao.obj.statusString}" rendered="#{discenteGraduacao.blockStatus}"/>
				</td>
			</tr>
			<c:if test="${discenteGraduacao.obj.concluido}">
				<tr>
					<th class="required"> Data da Colação de Grau:</th>
					
					<td>
						<t:inputCalendar id="dataColacaoGrau" renderAsPopup="true" renderPopupButtonAsImage="true" 
						size="10" maxlength="10" onkeypress="return formataData(this,event)" 
						value="#{discenteGraduacao.obj.dataColacaoGrau}" />
					</td>
				</tr>
			</c:if>
			<tr>
				<th class="required"> Forma de Ingresso:</th>
				<td>
					<h:selectOneMenu value="#{discenteGraduacao.obj.formaIngresso.id}" id="formaIngresso" style="width: 40%;" >
						<f:selectItem itemLabel="--> SELECIONE <--" itemValue="0"  />
						<f:selectItems value="#{discenteGraduacao.formasIngressoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<th class="required"> Perfil Inicial: </th>
				<td>
					<h:inputText value="#{discenteGraduacao.obj.perfilInicial}" size="2"  id="inicial" disabled="true"/>
				</td>
			</tr>
			
			
		<c:if test="${discenteGraduacao.obj.regular}">
			<tr>
				<th class="required">Curso:</th>
				<td>
					<h:selectOneMenu id="curso" onchange="submit()" value="#{discenteGraduacao.obj.curso.id }"
						valueChangeListener="#{discenteGraduacao.selecionaCurso}" rendered="#{discenteGraduacao.obj.regular}" style="width: 97%;" >
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{cursoGrad.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th class="required">Matriz Curricular:</th>
				<td>
					<h:selectOneMenu id="matriz" value="#{discenteGraduacao.obj.matrizCurricular.id }" rendered="#{discenteGraduacao.obj.regular}" style="width: 97%;">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{curriculo.possiveisMatrizes}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<c:if test="${discenteGraduacao.obj.curso.ADistancia}">
			<tr>
				<th class="required">Pólo:</th>
				<td>
					<h:selectOneMenu id="polo" value="#{discenteGraduacao.obj.polo.id}" style="width: 50%;" >
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{discenteGraduacao.polosCursoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			</c:if>
		</c:if>
		<c:if test="${discenteGraduacao.importaHistorico}">
			<tr>
				<c:if test="${discenteGraduacao.obj.concluido}">
					<th class="required"> Arquivo do Histórico Digitalizado: </th>
				</c:if>
				<c:if test="${not discenteGraduacao.obj.concluido}">
					<th> Arquivo do Histórico Digitalizado: </th>
				</c:if>
				<td><t:inputFileUpload id="historico" immediate="true" value="#{ discenteGraduacao.historico }" /></td>
			</tr>
		</c:if>
		<c:if test="${not discenteGraduacao.importaHistorico}">
			<tr>
				<th> Arquivo do Histórico Digitalizado: </th>
				<td>O discente possui matrículas em componentes curriculares e não é possível importar o histórico digitalizado</td>
			</tr>
		</c:if>
		</tbody>
		<tfoot>
			<tr><td colspan="2">
				<h:commandButton value="<< Dados Pessoais" action="#{ dadosPessoais.voltarDadosPessoais}" id="btnVoltar"/>
				<h:commandButton value="Cancelar" action="#{ discenteGraduacao.cancelar }" onclick="#{confirm}" id="btnCancelar"/>
				<h:commandButton value="Confirmar" action="#{ discenteGraduacao.cadastrar }" id="btnCadastrar"/>
			</td></tr>
		</tfoot>
	</table>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>