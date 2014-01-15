<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
<ufrn:steps/>
</h2>

<html:form action="/ensino/criarTurma" method="post" focus="docenteTurma.docente.pessoa.nome" onsubmit="return validateTecDocenteTurmaForm(this);" >
<table class="formulario" width="95%">
	<caption class="listagem">Turma</caption>
	<tr>
	<th style="font-weight: bold;">
	<ufrn:subSistema teste="not infantil">Disciplina:</ufrn:subSistema>
	<ufrn:subSistema teste="infantil">Nível Infantil:</ufrn:subSistema>
	</th>
	<td colspan="5">
	${turmaForm.obj.disciplina.nome}
	</td>
	</tr>

	<tr>
	<th>
	<b>Código:</b>
	</th>
	<td>
	${turmaForm.obj.codigo}
	</td>
	<ufrn:subSistema teste="not lato">
	<th>
	<b>Ano-Período:</b>
	</th>
	<td>
	${turmaForm.obj.ano}-${turmaForm.obj.periodo}
	</td>
	</ufrn:subSistema>
	<th>
	<b>Carga Horária da Turma:</b>
	</th>
	<td>
	${turmaForm.obj.disciplina.chTotal} h
	</td>
	</tr>

	<tr>
	<td colspan="6" align="center">
	<br>

	<table class="subFormulario" width="95%">
	<caption class="listagem">Adicione Docentes à essa Turma</caption>
	<tr>
		<th valign="top">Docente:</th>
		<td>
			<c:set var="idAjax" value="docenteTurma.docente.id"/>
			<c:set var="nomeAjax" value="docenteTurma.docente.pessoa.nome"/>
			<c:if test="${acesso.tecnico}">
				<c:set var="buscaInativos" value="true"/>
			</c:if>
			<c:if test="${acesso.lato or acesso.coordenadorCursoLato or acesso.secretarioLato}">
				<c:set var="externoLato" value="true"/>
			</c:if>
			<%@include file="/WEB-INF/jsp/include/ajax/docente.jsp" %>
		</td>
	</tr>
	<tr>
		<th>
			CH Dedicada à Turma:
		</th>
		<td align="left">
			<html:text property="docenteTurma.chDedicadaPeriodo" size="4" value="${turmaForm.obj.disciplina.chTotal}" />
		</td>
	</tr>
	<tfoot>
		<tr>
		<td colspan="2">
		<html:button dispatch="adicionarTurmaDocente" value="Adicionar Docente"/>
		</td>
		</tr>

	</tfoot>
	</table>
	</td>
	</tr>

	<tr>
	<td colspan="6" align="center">
	<c:if test="${not empty turmaForm.obj.docentesTurmas}">
	<br>
	    <table width="95%">
		<caption >Docentes Adicionados</caption>
	        <thead>
		        <td>Nome</td>
		        <td width="10%">CH Ded.</td>
		        <td width="5%"> </tD>
	        </thead>
	        <tbody>

			<html:hidden property="docenteId" styleId="docenteId" />

	        <c:forEach items="${turmaForm.obj.docentesTurmas}" var="docenteTurma" varStatus="status">
	            <tr>

	                    <td>${docenteTurma.docenteDescricao}</td>
	                    <td>${docenteTurma.chDedicadaPeriodo} h</td>
	                    <td>
	                        <html:link action="/ensino/criarTurma?dispatch=removerTurmaDocente&posLista=${status.index}">
                       			<img src="<%= request.getContextPath() %>/img/delete.gif" alt="Remover Professor desta Turma" title="Remover" border="0"/>
	                       </html:link>
	                    </td>
	            </tr>
	        </c:forEach>
	    </table>
	</c:if>
	<br><br>
	</td>
	</tr>

	<tfoot>
	<tr>
	<td colspan="6">
	<html:button view="dadosGerais" value="<< Dados Básicos da Turma"/>
	<html:button dispatch="cancelar" value="Cancelar"/>
	<ufrn:subSistema teste="not infantil">
		<html:button dispatch="horarios" value="Definir Horários >>"/>
	</ufrn:subSistema>
	<ufrn:subSistema teste="infantil">
			<html:button dispatch="resumo">Confirmar Turma >></html:button>
			 <html:button dispatch="cancelar" value="Cancelar"/>
	</ufrn:subSistema>

	</td>
	</tr>
	</tfoot>

</table>
</html:form>
<br>
<center>
	<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>

<br><br>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
