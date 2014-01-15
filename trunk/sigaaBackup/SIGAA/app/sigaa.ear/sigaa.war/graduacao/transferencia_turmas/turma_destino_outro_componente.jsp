<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<f:view>

<style>
	tr.componente td{
		background: #C4D2EB;
		font-weight: bold;
		border-bottom: 1px solid #BBB;
		color: #222;
	}
</style>

	<h2> <ufrn:subSistema /> &gt; Transferência entre Turmas ${transferenciaTurma.descricaoTipo}  &gt; Definir Turma de Destino Utilizando Componente Curricular Diferente </h2>

	<h:form id="buscaTurma">
		<table class="visualizacao" style="width: 95%;">
			<caption>Turma de Origem</caption>
			<tr>
				<th>Turma:</th>
				<td>${transferenciaTurma.turmaOrigem.disciplina.descricaoResumida } - Turma ${transferenciaTurma.turmaOrigem.codigo}</td>
			</tr>
			<tr>
				<th>Docente(s):</th>
				<td>${transferenciaTurma.turmaOrigem.docentesNomes}</td>
			</tr>
			<tr>
				<th>Horário:</th>
				<td> ${transferenciaTurma.turmaOrigem.descricaoHorario}	</td>
			</tr>
			<tr>
				<th>Capacidade da Turma:</th>
				<td> ${transferenciaTurma.turmaOrigem.capacidadeAluno} </td>
			</tr>
			<tr>
				<th>Alunos Matriculados:</th>
				<td> ${transferenciaTurma.turmaOrigem.qtdMatriculados} </td>
			</tr>
			<tr>
				<th>Solicitações:</th>
				<td> ${transferenciaTurma.turmaOrigem.qtdEspera} </td>
			</tr>
	
		</table>


	<table class="formulario" width="95%">
		<caption class="formulario">Buscar Turma de Destino</caption>
		<tbody>
			<c:if test="${!transferenciaTurma.menuTecnico && !transferenciaTurma.latoSensu}">
				<tr>
					<c:choose>
						<c:when test="${transferenciaTurma.unidadeRestrita}">
							<th width="25%" class="rotulo">Ano-Período:</th>
							<td>
								<h:outputText value="#{transferenciaTurma.ano}" />.<h:outputText value="#{transferenciaTurma.periodo}" />
							</td>
						</c:when>
						<c:otherwise>
							<th width="25%">Ano-Período:</th>
							<td>
								<a4j:region>
									<h:inputText value="#{transferenciaTurma.ano}" size="4" maxlength="4" 
										id="inputAno" converter="#{ intConverter }" 
										valueChangeListener="#{ transferenciaTurma.changeAnoPeriodo }"
										disabled="#{acesso.chefeDepartamento or acesso.secretarioDepartamento}" > 
										<a4j:support event="onkeyup" oncomplete="populaAnoPeriodo();return formatarInteiro(this);" reRender="departamentos,disciplinas" />
									</h:inputText>.	
								</a4j:region>	
								<a4j:region>
									<h:inputText value="#{transferenciaTurma.periodo}" size="1" maxlength="1" 
										id="inputPeriodo" converter="#{ intConverter }" 
										valueChangeListener="#{ transferenciaTurma.changeAnoPeriodo }"
										disabled="#{acesso.chefeDepartamento or acesso.secretarioDepartamento}">
										<a4j:support event="onkeyup" oncomplete="populaAnoPeriodo();return formatarInteiro(this);" reRender="departamentos,disciplinas" />
									</h:inputText>
								</a4j:region>	
							</td>
						</c:otherwise>	
					</c:choose>
				</tr>
				
				<%-- Seleção de Centro e Departamento --%>
				<c:if test="${not transferenciaTurma.unidadeRestrita}">
					<tr>
						<th width="25%">Centro:</th>
						<td>
							<a4j:region>
								<h:selectOneMenu id="centros" style="width: 80%" value="#{transferenciaTurma.unidade.id}"
									valueChangeListener="#{ transferenciaTurma.changeCentro }" disabled="#{acesso.chefeDepartamento or acesso.secretarioDepartamento}">
									<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
									<f:selectItems value="#{unidade.centrosEspecificasEscolas}" />
									<a4j:support event="onchange" reRender="departamentos, disciplinas"/>
								</h:selectOneMenu>
					            <a4j:status>
					                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
					            </a4j:status>
				            </a4j:region>					
						</td>
					</tr>
					<tr>
						<th>Unidade:</th>
						<td>
							<a4j:region>
							<h:inputHidden id="paramAno" value="#{transferenciaTurma.ano}"/>
							<h:inputHidden id="paramPeriodo" value="#{transferenciaTurma.periodo}"/>
							<h:selectOneMenu id="departamentos" style="width: 80%" 
								value="#{unidade.obj.id}" 
								valueChangeListener="#{ transferenciaTurma.buscarComponentes }" 
								disabled="#{acesso.chefeDepartamento or acesso.secretarioDepartamento or empty unidade.unidades}">
								<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
								<f:selectItems value="#{unidade.unidades}" />
								<a4j:support event="onchange" reRender="disciplinas"/>
							</h:selectOneMenu>
				            <a4j:status>
					                <f:facet name="start"><h:graphicImage value="/img/indicator.gif"/></f:facet>
					            </a4j:status>
				            </a4j:region>							
						</td>
					</tr>
				</c:if>
			</c:if>


			<tr>
				<th width="25%" class="obrigatorio">Componente Curricular:</th>
				<td>
					<h:selectOneMenu id="disciplinas" style="width: 95%" 
						value="#{transferenciaTurma.turmaDestino.disciplina.id}" 
						disabled="#{empty transferenciaTurma.componentes and not transferenciaTurma.unidadeRestrita}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{transferenciaTurma.componentes}" />
					</h:selectOneMenu>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Buscar" actionListener="#{transferenciaTurma.buscarTurmasDestino}" id="btnBuscar"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{transferenciaTurma.cancelar}" id="btnCancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>
	
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>

	</h:form>

	<c:if test="${not empty transferenciaTurma.turmasDestino}">
		<br>
		<div class="infoAltRem">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar Turma
		</div>

		<h:form id="listaTurmas">
		<table class=listagem>
			<caption class="listagem">Lista de Turmas Encontradas</caption>
			<thead>
			<tr>
				<td>Turma</td>
				<td>Docentes</td>
				<td style="text-align: center;">Ano/Período</td>
				<td width="10%">Horário</td>
				<td style="text-align: right;">Matriculados</td>
				<td style="text-align: right;">Solicitações</td>
				<td style="text-align: right;">Capacidade</td>
				<td colspan="2"></td>
			</tr>
			</thead>

			<c:set var="componente" />
			<c:forEach items="#{transferenciaTurma.turmasDestino}" var="item" varStatus="linha">

				<c:if test="${item.disciplina.id != componente}">
					<c:set var="componente" value="${item.disciplina.id}"/>
					<tr class="componente">
						<td colspan="9">
							 ${item.disciplina.descricaoResumida}
						</td>
					</tr>
				</c:if>

				<tr class="${linha.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td> ${item.codigo} </td>
					<td> ${item.docentesNomes} </td>
					<td align="center"> ${item.ano}.${item.periodo}</td>
					<td> ${ item.descricaoHorario }</td>
					<td align="right"> ${ item.qtdMatriculados } </td>
					<td align="right"> ${ item.qtdEspera } </td>
					<td align="right"> ${ item.capacidadeAluno } </td>
					<td width="2%">
						<h:commandLink id="selecionarTurma" action="#{transferenciaTurma.selecionarTurmaDestino}">
							<h:graphicImage url="/img/seta.gif" alt="Selecionar Turma" title="Selecionar Turma"/>
							<f:param name="id" value="#{item.id}"/>
						</h:commandLink>
					</td>	
				</tr>
			</c:forEach>
		</table>
		</h:form>
	</c:if>
	<br>
	<center>
		<h:form>
			<h:commandButton value="<< Voltar" action="#{transferenciaTurma.voltarTurmaOrigem}" />
			<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{transferenciaTurma.cancelar}" />
		</h:form>
	</center>
	
<script type="text/javascript">
	function populaAnoPeriodo() {
		if ($('buscaTurma:inputAno') != null)
			$('buscaTurma:paramAno').value = $('buscaTurma:inputAno').value;
		if ($('buscaTurma:inputPeriodo') != null)
			$('buscaTurma:paramPeriodo').value = $('buscaTurma:inputPeriodo').value;
	}
	populaAnoPeriodo();
</script>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>