<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<a4j:keepAlive beanName="tutoriaIMD" />
	<script type="text/javascript">
function mascara(o,f){
    obj = o
    fun = f
    setTimeout("gerarmascara()",1)
}

function gerarmascara(){
    obj.value=fun(obj.value)
}

function masknumeros(texto){
    texto = texto.replace(/\D/g,"")
    return texto
}
</script>
	<c:choose>
		<c:when test="${tutoriaIMD.cadastro}">
			<h2>
				<ufrn:subSistema />
				> Cadastro de Turmas
			</h2>
		</c:when>
		<c:otherwise>
			<h2>
				<ufrn:subSistema />
				> Altera��o de Turmas
			</h2>
		</c:otherwise>
	</c:choose>

	<h:form id="form">
		<table class="formulario" style="width: 100%">
			<caption>Dados da Turma</caption>
			<h:inputHidden value="#{tutoriaIMD.obj.id}" />
			<h:inputHidden value="#{tutoriaIMD.modulo.id}" />
			<tbody>

				<c:choose>
					<c:when test="${tutoriaIMD.cadastro}">
						<!-- DADOS GERAIS -->
						<!-- Cursos do IMD -->
						<tr>
							<th width="25%" class="obrigatorio">Curso:</th>
							<td><h:selectOneMenu value="#{tutoriaIMD.curso.id}"
									valueChangeListener="#{tutoriaIMD.carregarModulos}" id="curso"
									required="true" disabled="#{!tutoriaIMD.cadastro}">
									<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
									<f:selectItems value="#{tutoriaIMD.cursosCombo}" />
									<a4j:support event="onchange"
										reRender="modulo, cronograma, disciplinas" />
								</h:selectOneMenu></td>
						</tr>

						<!--Lista de M�dulos -->
						<tr>
							<th class="obrigatorio">M�dulo:</th>
							<td><h:selectOneMenu value="#{tutoriaIMD.modulo.id}"
									valueChangeListener="#{tutoriaIMD.carregarCronogramas}"
									id="modulo" required="true" disabled="#{!tutoriaIMD.cadastro}">
									<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
									<f:selectItems value="#{tutoriaIMD.modulosCombo}" />
									<a4j:support event="onchange"
										reRender="cronograma, disciplinas" />
								</h:selectOneMenu></td>
						</tr>

						<!-- Cronograma de Execu��o -->
						<tr>
							<th class="obrigatorio">Cronograma Execu��o:</th>
							<td><h:selectOneMenu value="#{tutoriaIMD.idCronograma}"
									id="cronograma"
									valueChangeListener="#{tutoriaIMD.vincularDisciplinas}"
									required="true" disabled="#{!tutoriaIMD.cadastro}">
									<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
									<f:selectItems value="#{tutoriaIMD.cronogramasCombo}" />
									<a4j:support event="onchange" reRender="disciplinas" />
								</h:selectOneMenu></td>
						</tr>
					</c:when>
					<c:otherwise>
						<!-- DADOS GERAIS -->
						<!-- Cursos do IMD -->
						<tr>
							<th width="25%" class="rotulo">Curso:</th>
							<td><h:outputText
									value="#{tutoriaIMD.cronograma.curso.descricao}" /></td>
						</tr>

						<!--Lista de M�dulos -->
						<tr>
							<th class="rotulo">M�dulo:</th>
							<td><h:outputText value="#{tutoriaIMD.modulo.descricao}" /></td>
						</tr>

						<!-- Cronograma de Execu��o -->
						<tr>
							<th class="rotulo">Cronograma Execu��o:</th>
							<td><h:outputText value="#{tutoriaIMD.cronograma.descricao}" /></td>
						</tr>

					</c:otherwise>
				</c:choose>



				<!-- Op��o Polo/Grupo -->
				<tr>
					<th class="obrigatorio">Polo/Grupo:</th>
					<td><h:selectOneMenu value="#{tutoriaIMD.opcaoPoloGrupo.id}"
							id="opcaoPoloGrupo" required="true">
							<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
							<f:selectItems value="#{tutoriaIMD.opcaoPoloGrupoCombo}" />
						</h:selectOneMenu></td>
				</tr>

				<!-- Nome da Turma -->
				<tr>
					<th class="obrigatorio">Nome da Turma:</th>
					<td><h:inputText
							value="#{tutoriaIMD.obj.turmaEntrada.especializacao.descricao}"
							size="10" maxlength="10" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Hor�rio:</th>
					<td><h:inputText
							value="#{tutoriaIMD.obj.turmaEntrada.dadosTurmaIMD.horario}"
							size="30" maxlength="30" /> <ufrn:help>Informe uma express�o para o hor�rio como, por exemplo, 
						246M12 ou 35T34. A express�o do hor�rio poder� conter mais de um 
						intervalo como, por exemplo, 246M12 35T34. </ufrn:help></td>
				</tr>

				<tr>
					<th>Local:</th>
					<td><h:inputText
							value="#{tutoriaIMD.obj.turmaEntrada.dadosTurmaIMD.local}"
							size="30" maxlength="30" /></td>
				</tr>

				<tr>
					<th>Capacidade:</th>
					<td><h:inputText
							value="#{tutoriaIMD.obj.turmaEntrada.capacidade}" size="4"
							maxlength="2" onkeypress="return mascara(this,masknumeros);" /></td>
				</tr>
				<tr>
					<td colspan="2"><a4j:outputPanel id="disciplinas">
							<table class="subFormulario" style="width: 100%">
								<c:if test="${!empty tutoriaIMD.listaDisciplinas}">
									<caption>Disciplinas vinculadas
										(${fn:length(tutoriaIMD.listaDisciplinas)})</caption>

									<thead>
										<tr>
											<th style="text-align: left;">C�digo</th>
											<th style="text-align: left;">Disciplina</th>
											<th style="text-align: left;">Carga Hor�ria</th>

										</tr>
									</thead>

									<tbody>
										<c:forEach var="linha" items="#{tutoriaIMD.listaDisciplinas}"
											varStatus="status">
											<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
												<td width="15%" style="text-align: left;">${linha.disciplina.codigo}</td>
												<td width="15%" style="text-align: left;">${linha.disciplina.nome}</td>
												<td width="15%" style="text-align: left;">${linha.disciplina.chTotal}</td>
											</tr>
										</c:forEach>
									</tbody>
								</c:if>
							</table>
						</a4j:outputPanel></td>
				</tr>

			</tbody>
			<tfoot>
				<tr>
					<td colspan="2"><c:choose>
							<c:when test="${tutoriaIMD.cadastro}">
								<h:commandButton value="Cadastrar"
									action="#{tutoriaIMD.cadastrar}" id="cadastrar" />
									<h:commandButton value="Cancelar"
									action="#{tutoriaIMD.cancelar}" onclick="#{confirm}"
									id="cancelar" />
							</c:when>
							<c:otherwise>
								<h:commandButton value="Alterar" action="#{tutoriaIMD.alterar}"
									id="alterar" />
								<h:commandButton value="Cancelar"
									action="#{tutoriaIMD.cancelarAlteracao}" onclick="#{confirm}"
									id="cancelar" />
							</c:otherwise>
						</c:choose> </td>
				</tr>
			</tfoot>
		</table>



		<br />
		<center>
			<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
			<span class="fontePequena"> Campos de preenchimento
				obrigat�rio. </span>
		</center>

	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>