<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2> <ufrn:subSistema /> > Registro Coletivo de ${registroDiplomaColetivo.obj.livroRegistroDiploma.tipoRegistroDescricao } </h2>
	
<div class="descricaoOperacao">Busque uma turma de concluíntes,
	informando o curso e o Ano/Semestre de conclusão. O sistema listará a o
	curso, a data de conclusão, e a quantidade de discentes que concluíram
	na turma.
	</div>
<br/>
<h:form prependId="false">

	<table class="formulario" width="90%">
		<caption>Informe os Parâmetros da Busca por Turma de Conclusão</caption>
		<tr>
			<th width="25%" class="required">Nível de Ensino:</th>
			<td>
				<h:selectOneMenu value="#{registroDiplomaColetivo.obj.livroRegistroDiploma.nivel}"
					onchange="submit()" id="nivelEnsino">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
					<f:selectItems value="#{registroDiplomaColetivo.niveisHabilitadosCombo}" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th width="35%">Curso:</th>
			<td>
				<h:selectOneMenu value="#{registroDiplomaColetivo.obj.curso.id}" id="curso">
					<f:selectItem itemValue="0" itemLabel="-- TODOS --"/>
					<c:if test="${registroDiplomaColetivo.obj.livroRegistroDiploma.graduacao}">
						<f:selectItems value="#{curso.allCursoGraduacaoCombo}"/>
					</c:if>
					<c:if test="${registroDiplomaColetivo.obj.livroRegistroDiploma.latoSensu}">
						<f:selectItems value="#{curso.allCursoEspecializacaoCombo}"/>
					</c:if>
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th class="required">Ano/Semestre de Conclusão:</th>
			<td><h:inputText value="#{registroDiplomaColetivo.ano}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" id="ano"/> -
				<h:inputText value="#{registroDiplomaColetivo.semestre}" size="1" maxlength="1" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" id="semestre"/>
			</td>
		</tr>
		<tfoot>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton action="#{registroDiplomaColetivo.buscarTurma}" value="Buscar Turma de Conclusão" id="buscarTurma"/>
				<h:commandButton action="#{registroDiplomaColetivo.cancelar}" value="Cancelar" onclick="#{confirm}" id="cancelar"/>
			</td>
		</tr>
		</tfoot>
	</table>
<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>
<br>
<c:if test="${not empty registroDiplomaColetivo.turmasEncontradas}">
	<div class="infoAltRem">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar Turma
	</div>
	<br />
	<c:if test="${not acesso.graduacao}">
		<table class="listagem" >
		<caption>Total de Turmas Encontradas: ${fn:length(registroDiplomaColetivo.turmasEncontradas)}</caption>
		<thead>
			<tr>
				<td width="50%">Curso</td>
				<td style="text-align: center;" width="15%">Data de Conclusão</td>
				<td style="text-align: right;" width="10%">Núm. Discentes</td>
				<td width="5%"></td>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{registroDiplomaColetivo.turmasEncontradas}" var="item" varStatus="status">
				<tr class="${status.index % 2 != 0 ? "linhaPar" : "linhaImpar" }">
					<td>
						${item.curso}
						<h:outputText value=" - PÓLO #{item.municipio_polo}" rendered="#{not empty item.municipio_polo}"/>
					</td>
					<td style="text-align: center;"><ufrn:format type="data" valor="${ item.data_colacao_grau}"/></td>
					<td style="text-align: right;">${item.quantidade}</td>
					<td style="text-align: center;">
						<h:commandLink title="Selecionar Turma" style="border: 0;" action="#{registroDiplomaColetivo.registrarTurma}" id="selecionarTurma" >
							<f:param name="dataColacao" value="#{item.data_colacao_grau}" />
							<f:param name="idCurso" value="#{item.id_curso}" />
							<h:graphicImage url="/img/seta.gif" alt="Selecionar Turma" />
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</tbody>
		</table>
	</c:if>
	<c:if test="${acesso.graduacao}">
		<table class="listagem" >
		<caption>Total de Turmas Encontradas: ${fn:length(registroDiplomaColetivo.turmasEncontradas)}</caption>
		<thead>
			<tr>
				<td style="text-align: center;" width="15%">Data de Colação</td>
				<td width="50%">Matriz Curricular</td>
				<td style="text-align: right;" width="10%">Núm. Discentes</td>
				<td style="text-align: center;" width="5%"></td>
			</tr>
		</thead>
		<tbody>
			<c:set var="dataGrupo" value="" />
			<c:set var="cursoGrupo" value="" />
			<c:set var="municipioGrupo" value="" />
			<c:set var="poloGrupo" value="" />
			<c:set var="index" value="0" />
			<c:forEach items="#{registroDiplomaColetivo.turmasEncontradas}" var="item" varStatus="status">
				<c:set var="dataLoop" value="${item.data_colacao_grau}" />
				<c:set var="cursoLoop" value="${item.curso}" />
				<c:set var="municipioLoop" value="${item.municipio}" />
				<c:set var="poloLoop" value="${item.id_polo}" />
				<c:if test="${not (cursoLoop eq cursoGrupo and municipioLoop eq municipioGrupo)}">
					<c:set var="index" value="0" />
					<c:set var="cursoGrupo" value="${cursoLoop}" />
					<c:set var="municipioGrupo" value="${municipioLoop}" />
					<c:set var="dataGrupo" value="" />
					<tr>
						<td colspan="4" class="subFormulario">${ item.curso } - ${ item.sigla } - ${ item.municipio }</td>
					</tr>
				</c:if>
				<c:if test="${dataLoop eq dataGrupo and poloLoop eq poloGrupo}">
					<tr class="${index % 2 != 0 ? "linhaPar" : "linhaImpar" }">
						<td></td>
						<td>
						   	${ item.grau_academico } -  
						   	${item.turno } - ${ item.modalidade }
					   	</td>
						<td style="text-align: right;">${item.quantidade}</td>
						<td></td>
					</tr>
				</c:if>
				<c:if test="${not (dataLoop eq dataGrupo and poloLoop eq poloGrupo)}">
					<c:set var="index" value="${index +1}" />
					<c:set var="dataGrupo" value="${dataLoop}" />
					<c:set var="poloGrupo" value="${poloLoop}" />
					<tr class="${index % 2 != 0 ? "linhaPar" : "linhaImpar" }">
						<td style="text-align: center;"><ufrn:format type="data" valor="${item.data_colacao_grau}" /></td>
						<td>
						   	${ item.grau_academico } -  
						   	${item.turno } - ${ item.modalidade }
						   	<h:outputText value=" - Pólo #{item.municipio_polo}" rendered="#{not empty item.municipio_polo}"/>
					   	</td>
						<td style="text-align: right;">${item.quantidade}</td>
						<td style="text-align: center;">
							<h:commandLink title="Selecionar Turma" style="border: 0;" action="#{registroDiplomaColetivo.registrarTurma}" id="selecionarTurmaLink2">
								<f:param name="dataColacao" value="#{item.data_colacao_grau}" />
								<f:param name="idCurso" value="#{item.id_curso}" />
								<f:param name="idPolo" value="#{item.id_polo}" />
								<h:graphicImage url="/img/seta.gif" alt="Selecionar Turma" />
							</h:commandLink>
						</td>
					</tr>
				</c:if>
			</c:forEach>
		</tbody>
		</table>
	</c:if>
</c:if>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>