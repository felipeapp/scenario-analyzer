<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.ensino.form.TurmaForm"%>

<h2 class="tituloPagina">
	<ufrn:subSistema /> &gt; Turmas
</h2>

<html:form action="/ensino/criarTurma" method="get">
      <html:hidden property="buscar" value="true" />
		<table class="formulario" align="center" width="85%">
			<caption class="listagem">Busca por Turma</caption>
			<tr>
				<td colspan="2" align="center">Situação da Turma para Busca:
					<html:select property="obj.situacaoTurma.id">
						<html:options collection="situacoes" property="id" labelProperty="descricao" />
					</html:select>
				</td>
			</tr>
			<ufrn:subSistema teste="not infantil">
			<tr>
				<td>
				<!-- <input type="checkbox" name="buscaDisciplina" value="true" class="noborder" id="buscaDisciplina"/> -->
					<html:checkbox property="filtros" value="<%= "" + TurmaForm.FILTRO_DISCIPLINA %>" styleId="buscaDisciplina" 
							styleClass="noborder" />
					<label for="buscaDisciplina">Disciplina:</label>
				</td>
				<td>
					<c:set var="idAjax" value="disciplina.id"/>
		    		<c:set var="nomeAjax" value="disciplina.nome"/>
					<%@include file="/WEB-INF/jsp/include/ajax/disciplina.jsp" %>
				</td>
			</tr>
			</ufrn:subSistema>
			<ufrn:subSistema teste="infantil">
			<tr>
				<td>
					<input type="radio" name="tipoBusca" value="disciplina" class="noborder" id="buscaDisciplina" />
					<label for="buscaDisciplina">Nível Infantil</label>
				</td>
				<td>
					<html:select property="disciplina.id" onfocus="disciplinaOnFocus()">
						<html:options collection="disciplinas" property="id" labelProperty="nome" />
					</html:select>
				</td>
			</tr>
			</ufrn:subSistema>
			<tr>
				<td>
				<!-- <input type="checkbox" name="buscaDocente" value="true" class="noborder" id="buscaDocente"/> -->
					<html:checkbox property="filtros" value="<%= "" + TurmaForm.FILTRO_DOCENTE %>" styleId="buscaDocente" 
							styleClass="noborder" />
					<label for="buscaDocente">Docente:</label>
				</td>
				<td>
					<c:set var="idAjax" value="docenteTurma.docente.id" />
		    		<c:set var="nomeAjax" value="docenteTurma.docente.pessoa.nome" />
					<%@include file="/WEB-INF/jsp/include/ajax/docente.jsp"%>
				</td>
			</tr>
			<ufrn:subSistema teste="not lato">
			<tr>
				<td> 
					<!-- <input type="checkbox" name="buscaAnoPeriodo" value="true" class="noborder" id="buscaAno"> -->
					<html:checkbox property="filtros" value="<%= "" + TurmaForm.FILTRO_ANO_PERIODO %>" styleId="buscaAno" 
							styleClass="noborder" />
					<label for="buscaAno">Ano-Período</label>
				</td>
				<td> 
					<html:text property="obj.ano" size="5" maxlength="4" onfocus="marcaCheckBox('buscaAno')"></html:text>.
					<html:text property="obj.periodo" size="1" maxlength="1" onfocus="marcaCheckBox('buscaAno')"></html:text> 
				</td>
			</tr>
			</ufrn:subSistema>
			<tfoot>
				<tr>
					<td colspan="2">
						<html:button dispatch="list">Buscar</html:button>
					</td>
				</tr>
			</tfoot>
		</table>
</html:form>

<c:forEach items="${turmaForm.filtros}" var="filtro">
 	<jsp:useBean id="filtro" type="java.lang.Integer" />
 	<c:if test="<%= filtro.intValue() == TurmaForm.FILTRO_DISCIPLINA%>">
 		<script> $('buscaDisciplina').checked = true;</script>
 	</c:if>
 	 <c:if test="<%= filtro.intValue() == TurmaForm.FILTRO_DOCENTE%>">
 		<script> $('buscaDocente').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == TurmaForm.FILTRO_ANO_PERIODO%>">
 		<script> $('buscaAno').checked = true;</script>
 	</c:if>
</c:forEach>

<br />

<ufrn:subSistema teste="infantil">
	<ufrn:table collection="${lista}" properties="disciplina.nome, anoPeriodo, local, situacaoTurma.descricao"
	headers="Nível, Ano/Per., Local, Situação" title="Turmas"
	crud="true" pageSize="20" crudRoles="<%=new int[] {SigaaPapeis.GESTOR_INFANTIL} %>"
	links="src='${ctx}/img/view.gif',?dispatch=view&id={id},Resumo da Turma;"
	linksRoles="<%=new int[][]{null} %>" />
</ufrn:subSistema>

<ufrn:subSistema teste="not infantil">
<ufrn:subSistema teste="not lato">
	<ufrn:table collection="${lista}" properties="descricaoNivelTecnico, codigo, matriculadosCapacidade, descricaoHorario, local, situacaoTurma.descricao"
	headers="Turma, Código, Mat./Cap., Horário, Local, Situação" title="Turmas" sizes="50%,8%,8%,8%,8%,8%,8%"
	crud="true" pageSize="20"
	crudRoles="<%=new int[] {SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_MEDIO} %>"
	links="src='${ctx}/img/view.gif',?dispatch=view&id={id},Resumo da Turma;"
	linksRoles="<%=new int[][]{null} %>" />
</ufrn:subSistema>
</ufrn:subSistema>
<ufrn:subSistema teste="lato">
	<ufrn:table collection="${lista}" properties="descricaoNivelLato, codigo, matriculadosCapacidade, descricaoHorario, local, situacaoTurma.descricao"
	headers="Turma, Código, Mat./Cap., Horário, Local, Situação" title="Turmas" sizes="50%,8%,8%,8%,8%,8%,8%"
	crud="true" pageSize="20"
	crudRoles="<%=new int[] {SigaaPapeis.GESTOR_LATO,SigaaPapeis.COORDENADOR_LATO,SigaaPapeis.SECRETARIA_LATO} %>"
	links="src='${ctx}/img/view.gif',?dispatch=view&id={id},Visualizar Turmas;"
	linksRoles="<%=new int[][]{null} %>" />
</ufrn:subSistema>

<script type="text/javascript">

	function disciplinaOnFocus() {
		marcaCheckBox('buscaDisciplina');
	}
	function docenteOnFocus() {
		marcaCheckBox('buscaDocente');
	}
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
