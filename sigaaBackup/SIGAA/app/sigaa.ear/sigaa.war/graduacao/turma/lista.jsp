<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	table.listagem tr.unidade td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:messages showDetail="true"></h:messages>
	<h2> <ufrn:subSistema /> &gt; Lista de Turmas</h2>

	<h:form id="busca">
		<table class="formulario" width="50%">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				<c:if test="${ acesso.dae }">
				<tr>
					<th> Departamento: </th>
					<td>
						<h:selectOneMenu id="departamento" value="#{turmaGraduacaoBean.obj.disciplina.unidade.id}">
							<f:selectItem itemValue="0" itemLabel="TODOS" />
							<f:selectItems value="#{unidade.allDepartamentoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				</c:if>

				<tr>
					<th> <label>Ano-Período:</label> </th>
					<td>
						<h:inputText value="#{turmaGraduacaoBean.obj.ano}" size="5" id="ano" />
						- <h:inputText value="#{turmaGraduacaoBean.obj.periodo}" size="2" id="periodo" />
						<span class="required">&nbsp;</span>
					</td>
				</tr>
				<tr>
					<th><label>Situação:</label></th>
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
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{turmaGraduacaoBean.buscar}" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{turmaGraduacaoBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<c:if test="${not empty turmaGraduacaoBean.resultadosBusca}">
		<br>
		<center>
		<div class="infoAltRem"><h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />:
		Alterar dados da Turma<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />:
		Remover Turma<br />
		</div>
		</center>
		<table class=listagem>
			<caption class="listagem">Lista de Turmas de Graduação Encontradas</caption>
			<thead>
				<tr>
					<td>Turma</td>
					<td>Situação</td>
					<td></td>
					<td></td>
				</tr>
			</thead>
			<c:set var="idUnid" value="-1"/>
			<c:forEach items="${turmaGraduacaoBean.resultadosBusca}" var="item" varStatus="loop">
			
				<c:if test="${idUnid != item.disciplina.unidade.id}">
					<c:set var="idUnid" value="${item.disciplina.unidade.id}"/>
					<tr class="unidade">
						<td colspan="4">
							${ item.disciplina.unidade.siglaNome}
						</td>
					</tr>
				</c:if>
			
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td>${ item.anoPeriodo } - ${ item.disciplina.codigoNome } - Turma ${item.codigo} </td>
					<td>${ item.situacaoTurma.descricao }</td>
					<h:form>
						<td width=20><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
							image="/img/alterar.gif" styleClass="noborder" value="Alterar"
							action="#{turmaGraduacaoBean.atualizar}" /></td>
					</h:form>
					<h:form>
						<td width=25><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
							image="/img/delete.gif" styleClass="noborder" alt="Remover" action="#{turmaGraduacaoBean.preRemover}" /></td>
					</h:form>
				</tr>
			</c:forEach>
		</table>
	</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>