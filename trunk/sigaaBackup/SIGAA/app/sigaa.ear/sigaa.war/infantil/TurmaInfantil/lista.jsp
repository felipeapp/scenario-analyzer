<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<a4j:keepAlive beanName="turmaInfantilMBean" />
<f:view>

	<h:form id="form">
	
			<h2> <ufrn:subSistema /> &gt; Lista de Turmas</h2>
		
			<table class="formulario" style="width:80%;">
				<caption>Informe os critérios de busca</caption>
				<tbody>
					<tr>
						<td width="2%">
							<h:selectBooleanCheckbox value="#{turmaInfantilMBean.buscaAno}" styleClass="noborder" id="checkAno"/>
						</td>
						<th style="text-align: left"> <label for="checkAno" onclick="$('form:checkAno').checked = !$('form:checkAno').checked;">Ano:</label></th>
						<td> 
							<h:inputText value="#{turmaInfantilMBean.ano}" size="4" id="anoTurma" maxlength="4"
								onfocus="getEl('form:checkAno').dom.checked = true;" 
								onkeyup="return formatarInteiro(this);"/>
						</td>
					</tr>
					
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{turmaInfantilMBean.buscaNivel}" styleClass="noborder" id="checkNivel"/>
						</td>
						<th style="text-align: left"> <label for="checkNivel" onclick="$('form:checkNivel').checked = !$('form:checkNivel').checked;">Turma:</label></th>
						<td> 
							<h:selectOneMenu id="nivelTurma" value="#{turmaInfantilMBean.nivel}" onfocus="getEl('form:checkNivel').dom.checked = true;">
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
								<f:selectItems value="#{turmaInfantilMBean.niveisCombo}"/>
							</h:selectOneMenu>
						</td>
					</tr>
						
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{turmaInfantilMBean.buscaCodigo}" styleClass="noborder" id="checkCodigo"/>
						</td>
						<th style="text-align: left"> <label for="checkCodigo" onclick="$('form:checkCodigo').checked = !$('form:checkCodigo').checked;">Código:</label></th>
						<td> <h:inputText value="#{turmaInfantilMBean.codigo}" size="2" maxlength="1" id="codigoTurma" onfocus="getEl('form:checkCodigo').dom.checked = true;"/> </td>
					</tr>
					
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{turmaInfantilMBean.buscaProfessor}" styleClass="noborder" id="checkProfessor"/>
						</td>
						<th style="text-align: left"> <label for="checkProfessor" onclick="$('form:checkProfessor').checked = !$('form:checkProfessor').checked;">Professor(a):</label></th>
						<td> <h:inputText value="#{turmaInfantilMBean.docenteTurmaInfantil.docente.pessoa.nome}" size="60" maxlength="60" id="nomeProfessor" onfocus="getEl('form:checkProfessor').dom.checked = true;"/> </td>
					</tr>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="3">
							<h:commandButton action="#{turmaInfantilMBean.buscar}" value="Buscar" id="buscar"/>
							<h:commandButton id="cancelar" action="#{turmaInfantilMBean.cancelar}" value="Cancelar" onclick="#{confirm}"/>
						</td>
					</tr>
				</tfoot>
			</table>
			<br/>
			<div class="infoAltRem">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;" rendered="#{ turmaInfantilMBean.buscaFichaEvolucao }"/>
					<h:commandLink action="#{turmaInfantilMBean.iniciar}" value="Cadastrar Nova Turma" rendered="#{ turmaInfantilMBean.buscaFichaEvolucao }"/>
				<h:graphicImage value="/img/alterar.gif"style="overflow: visible;" rendered="#{ turmaInfantilMBean.buscaFichaEvolucao }"/>
				<h:outputText value=": Alterar Turma" rendered="#{ turmaInfantilMBean.buscaFichaEvolucao }" />
			    <h:graphicImage value="/img/delete.gif" style="overflow: visible;" rendered="#{ turmaInfantilMBean.buscaFichaEvolucao }"/>
			    <h:outputText value=": Remover Turma" rendered="#{ turmaInfantilMBean.buscaFichaEvolucao }" />
			    <h:graphicImage value="/img/extensao/user1_view.png" style="overflow: visible;" rendered="#{ turmaInfantilMBean.buscaFichaEvolucao }"/> 
			    <h:outputText value=": Listar Alunos" rendered="#{ turmaInfantilMBean.buscaFichaEvolucao }" />
			    <c:if test="${ turmaInfantilMBean.buscaFichaEvolucao }"><br /></c:if>
			    <h:graphicImage value="/img/printer_ok.png" width="16px;" style="overflow: visible;" rendered="#{ turmaInfantilMBean.buscaFichaEvolucao }"/> 
			    <h:outputText value=": Listar Alunos para Impressão" rendered="#{ turmaInfantilMBean.buscaFichaEvolucao }" />
			    <h:graphicImage value="/img/listar.gif" style="overflow: visible;"/>
			    <h:outputText value=": Formulário da Turma" />
			</div>
			<br/>
			<c:if test="${not empty turmaInfantilMBean.resultadosBusca}">
				<table class="listagem">
					<caption>Turmas Encontradas (${fn:length(turmaInfantilMBean.resultadosBusca)})</caption>
					<thead>
						<tr>
							<th>Turma</th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
						</tr>
					</thead>
					<c:forEach items="#{turmaInfantilMBean.resultadosBusca}" var="turma" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td>${ turma.descricaoTurmaInfantil }</td>
							<td>
								<h:commandLink title="Alterar Turma" action="#{ turmaInfantilMBean.atualizar }" rendered="#{ turmaInfantilMBean.buscaFichaEvolucao }">
							        <f:param name="id" value="#{turma.id}"/>
						    		<h:graphicImage url="/img/alterar.gif" />
								</h:commandLink>
							</td>
							<td>
								<h:commandLink title="Remover Turma" action="#{ turmaInfantilMBean.preRemover }" rendered="#{ turmaInfantilMBean.buscaFichaEvolucao }">
							        <f:param name="id" value="#{turma.id}"/>
						    		<h:graphicImage url="/img/delete.gif" />
								</h:commandLink>
							</td>
							<td>
								<h:commandLink action="#{turmaInfantilMBean.listarAlunos}" title="Listar Alunos" rendered="#{ turmaInfantilMBean.buscaFichaEvolucao }">
									<f:param name="id" value="#{turma.id}" />
									<h:graphicImage url="/img/extensao/user1_view.png" />
								</h:commandLink>
							</td>
							<td>
								<h:commandLink action="#{turmaInfantilMBean.listarAlunosImpressao}" title="Listar Alunos para Impressão" rendered="#{ turmaInfantilMBean.buscaFichaEvolucao }">
									<f:param name="id" value="#{turma.id}" />
									<h:graphicImage url="/img/printer_ok.png" width="17px;" />
								</h:commandLink>
							</td>
							<td>
								<h:commandLink title="Formulário da Turma" action="#{ formularioEvolucaoCriancaMBean.iniciarFormularioTurma }" >
							        <f:param name="idTurma" value="#{turma.id}"/>
							        <f:param name="idComponente" value="#{turma.disciplina.id}"/>
						    		<h:graphicImage url="/img/listar.gif" />
								</h:commandLink>
							</td>
						</tr>
					</c:forEach>
				</table>
			</c:if>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>