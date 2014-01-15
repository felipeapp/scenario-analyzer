<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<f:view>
<f:subview id="menu">
	<%@include file="/portais/docente/menu_docente.jsp" %>
</f:subview>
<h2 class="tituloPagina"> <ufrn:subSistema /> > Cadastrar Notas</h2>




<h:form id="escolher">

<h:messages showDetail="true"/>
<c:set var="turmas" value="#{ consolidarDisciplinaMBean.turmasProfessor }"/>
<input type="hidden" name="gestor" value="${ param['gestor'] }"/>


<c:if test="${ empty turmas }">
	<div style="line-height:200px;text-align:center;font-size:1.3em;font-weight:bold;color: #FF0000;">Todas as turmas estão consolidadas.</div>
	
	<div align="center">
	<h:commandButton action="#{ consolidarTurma.cancelar }" value="Cancelar" onclick="#{confirm}"/>
	</div>
</c:if>

<c:if test="${ not empty turmas }">

<div class="descricaoOperacao">

Selecione uma turma aberta abaixo para modificar as notas.

</div>

<table class="formulario" width="100%">
<caption>Selecione uma Turma</caption>
<tbody>

<!-- Caso seja apenas um professor -->
	<c:forEach var="t" items="#{ turmas}" varStatus="loop">
	<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
		<td>${ t.label }</td>
		<td style="width:20px;">
	 		<h:commandLink action="#{ consolidarDisciplinaMBean.selecionarTurmaDisciplina }">
	 			<h:graphicImage value="/img/seta.gif" alt="Inserir Notas" title="Inserir Notas"/>
	 			<f:param name="id" value="#{ t.value}"/>
	 		</h:commandLink>
		</td>
	</tr>
	</c:forEach>


</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<c:if test="${ consolidarTurma.fromMenuGestor }">
					<h:commandButton action="#{ consolidarTurma.escolherTurma }" value="Inserir Notas >>"/>
				</c:if>
			
				<h:commandButton action="#{ consolidarTurma.cancelar }" value="Cancelar" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>

</table>
</c:if>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
