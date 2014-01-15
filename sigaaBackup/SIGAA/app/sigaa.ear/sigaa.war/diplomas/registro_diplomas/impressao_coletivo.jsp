<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2> <ufrn:subSistema /> > Impressão de Diplomas Coletivo</h2>
<h:form prependId="false">

	<table class="formulario" width="100%">
		<caption>Selecione o curso para imprimir os diplomas</caption>
		<tbody>
		<tr>
			<th width="25%" class="required">Nível de Ensino:</th>
			<td>
				<h:selectOneMenu value="#{impressaoDiploma.nivelEnsino}"
					onchange="submit()" id="nivelEnsino">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
					<f:selectItems value="#{registroDiplomaColetivo.niveisHabilitadosCombo}" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th width="35%" class="required">Curso:</th>
			<td>
				<h:selectOneMenu value="#{impressaoDiploma.idCurso}" id="curso">
					<f:selectItem itemValue="0" itemLabel="-- TODOS CURSOS --"/>
					<c:if test="${impressaoDiploma.graduacao}">
						<f:selectItems value="#{curso.allCursoGraduacaoCombo}"/>
					</c:if>
					<c:if test="${impressaoDiploma.latoSensu}">
						<f:selectItems value="#{curso.allCursoEspecializacaoCombo}"/>
					</c:if>
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th class="required">Ano/Semestre de Conclusão:</th>
			<td><h:inputText value="#{impressaoDiploma.ano}" size="4" maxlength="4" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" id="ano"/> -
				<h:inputText value="#{impressaoDiploma.semestre}" size="1" maxlength="1" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" id="semestre"/>
			</td>
		</tr>
		</tbody>
		<tfoot>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton action="#{impressaoDiploma.buscaRegistroDiplomaColetivo}" value="Buscar Registros de Diploma Coletivo" id="buscar"/>
				<h:commandButton action="#{impressaoDiploma.cancelar}" value="Cancelar" onclick="#{confirm}" id="cancelar"/>
			</td>
		</tr>
		</tfoot>
	</table>
<br>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
<br>
<c:if test="${not empty impressaoDiploma.resultadosBusca}">
	<div class="infoAltRem">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Gerar Diplomas
	</div>
	<br />
	<table class="listagem">
	<caption>Total de Registros Encontrados: ${fn:length(impressaoDiploma.resultadosBusca)}</caption>
	<thead>
		<tr>
			<td style="text-align: center;" width="20%">Data de Colação</td>
			<td style="text-align: left;" width="75%">Curso</td>
			<td style="text-align: right;" width="5%">Discentes</td>
			<td width="5%"></td>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="#{impressaoDiploma.resultadosBusca}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td style="text-align: center;"><ufrn:format type="data" valor="${item.dataColacao}" /></td>
				<td style="text-align: left;">${item.curso.descricao}</td>
				<td style="text-align: right;">${fn:length(item.registrosDiplomas)}</td>
				<td>
					<h:commandLink title="Gerar Diplomas" style="border: 0;" action="#{impressaoDiploma.selecionaDiplomaColetivo}" >
						<f:param name="id" value="#{item.id}" />
						<h:graphicImage url="/img/seta.gif" alt="Gerar Diplomas" />
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
	</tbody>
	</table>
</c:if>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>