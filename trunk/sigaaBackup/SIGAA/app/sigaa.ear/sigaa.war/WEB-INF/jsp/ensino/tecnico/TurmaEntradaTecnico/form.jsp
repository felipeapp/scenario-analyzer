<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<h2>
	<html:link action="ensino/tecnico/cadastroCursoTecnico.do?dispatch=cancel">
		<ufrn:subSistema semLink="true"/>
	</html:link> &gt;
	<c:if test="${param.dispatch == 'remove' }">
		Desativar Turma de Entrada
	</c:if>
	<c:if test="${param.dispatch != 'remove' }">
	    ${ (formTurmaEntradaTecnico.obj.id == 0) ? "Cadastro" : "Atualização" } de Turma de Entrada
	</c:if>
</h2>

<div class="descricaoOperacao">
	<p>Uma Turma de Entrada representa um conjunto inicial de alunos que ingressaram num
    	determinado curso e irão cursar turmas das disciplinas de um dos currículos do respectivo curso.</p>
</div>


<html:form action="/ensino/tecnico/cadastroTurmaEntradaTecnico" method="post" focus="obj.cursoTecnico.id" styleId="form">

    <html:hidden property="obj.id" />
    <html:hidden property="obj.unidade.id" value="${ sessionScope.usuario.unidade.id }" />

    <table class="formulario" width="80%">
    <caption>Dados da Turma de Entrada</caption>
    <tbody>
		<tr>
	        <th width="20%" class="obrigatorio">Curso:</th>
	       	<td>
	       		<html:select property="obj.cursoTecnico.id" styleId="cursoId">
	                <html:option value="">-- SELECIONE --</html:option>
	                <html:options collection="cursos" property="id" labelProperty="codigoNome"/>
	            </html:select>
	       	</td>
	    </tr>
		
		<tr>
			<th class="obrigatorio">Data de Entrada:</th>
			<td>
				<ufrn:calendar property="dataEntrada"/>
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">Ano-Período de Referência:</th>
			<td>
				<html:text property="obj.anoReferencia" maxlength="4" size="4" onkeyup="formatarInteiro(this)"/>
				-
				<html:text property="obj.periodoReferencia" maxlength="1" size="1" onkeyup="formatarInteiro(this)"/>
			</td>
		</tr>
		<ufrn:subSistema teste="tecnico">
			<tr>
				<th>Especialização:</th>
				<td>
					<html:select property="obj.especializacao.id">
		                <html:option value="">-- SELECIONE --</html:option>
		                <html:options collection="especializacoes" property="id" labelProperty="descricao"/>
		            </html:select>
				</td>
			</tr>
		</ufrn:subSistema>
		<c:if test="${not formTurmaEntradaTecnico.obj.ativo}">
			<tr>
				<th>Ativo:</th>
				<td>
					<html:checkbox property="obj.ativo" />
				</td>
			</tr>
		</c:if>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2">
				<html:button dispatch="persist">Confirmar</html:button>
				<input type="hidden" name="page" value="${param.page}"/>
				<input type="hidden" name="desativar" value="${param.desativar}"/>
	    		<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
		  	</td>
		</tr>
	</tfoot>

</table>
<br/>
<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>

</html:form>

<c:if test="${ param['dispatch'] == 'remove' }">
<script type="text/javascript">
new Remocao('form');
</script>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>