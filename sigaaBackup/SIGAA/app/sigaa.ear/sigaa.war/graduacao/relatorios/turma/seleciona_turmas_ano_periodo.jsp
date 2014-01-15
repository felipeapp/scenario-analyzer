<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> > Relatório de Turmas</h2>
	<h:form prependId="false">
		<table class="formulario" width="50%">
			<caption class="formulario">Parâmetros da Busca</caption>
			<tbody>
				<tr>
					<th class="required">Ano-Período:</th>
					<td>
						<h:inputText value="#{turmaGraduacaoBean.obj.ano}" size="4" maxlength="4" id="ano" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
						- <h:inputText value="#{turmaGraduacaoBean.obj.periodo}" size="1" maxlength="1" id="periodo" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
					</td>
				</tr>
				<tr>
					<th>Departamento: </th>
					<td>
						<h:selectOneMenu id="unidades" style="width: 300px"	value="#{turmaGraduacaoBean.obj.disciplina.unidade.id}">
							<f:selectItem itemValue="0" itemLabel="TODOS" />
							<f:selectItems value="#{unidade.allDepartamentoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>Status:</th>
					<td>
						<h:selectOneMenu id="status" value="#{turmaGraduacaoBean.obj.situacaoTurma.id}">
							<f:selectItem itemValue="0" itemLabel="TODOS" />
							<f:selectItems value="#{situacaoTurma.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton value="Buscar" action="#{turmaGraduacaoBean.buscar}" id="btnBuscar" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{turmaGraduacaoBean.cancelar}" id="cancelar"/></td>
				</tr>
			</tfoot>
		</table>

	</h:form>
<br/>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
<br/>
<c:if test="${not empty turmaGraduacaoBean.resultadosBusca}">
	<table class="listagem">
		<caption class="listagem">Resultado da Busca</caption>
		<thead>
			<tr>
				<td style="text-align: center;">Ano-Período</td>
				<td>Componente Curricular</td>
				<td style="text-align: center;">Cód. da Turma</td>
				<td>Situação</td>
			</tr>
		</thead>
		<c:set var="idUnid" value="-1"/>
		<c:forEach items="${turmaGraduacaoBean.resultadosBusca}" var="item" >
			<c:if test="${idUnid != item.disciplina.unidade.id}">
				<c:set var="idUnid" value="${item.disciplina.unidade.id}"/>
				<c:set var="loop" value="0"/>
				<tr class="unidade">
					<td colspan="4" class="subFormulario">
						${ item.disciplina.unidade.siglaNome}
					</td>
				</tr>
			</c:if>
			<tr class="${ loop % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<td style="text-align: center;">${ item.anoPeriodo }</td>
				<td>${ item.disciplina.codigoNome }</td>
				<td style="text-align: center;">${item.codigo} </td>
				<td>${ item.situacaoTurma.descricao }</td>
			</tr>
			<c:set var="loop" value="${loop + 1}"/>
		</c:forEach>
	</table>
</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>