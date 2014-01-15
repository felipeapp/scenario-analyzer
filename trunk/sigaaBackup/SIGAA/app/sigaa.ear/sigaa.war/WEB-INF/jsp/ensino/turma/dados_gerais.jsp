<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<h2 class="tituloPagina"><ufrn:steps /></h2>

<script language="javascript">
function postCurso() {
	$('disciplina').value = ${turmaForm.obj.disciplina.id};
}
</script>

<html:form action="/ensino/criarTurma"  method="post" focus="obj.disciplina.nome" onsubmit="return validateTecTurmaForm(this);" styleId="form">

	<table class="formulario" align="center" width="95%">
		<caption>Dados Básicos da Turma</caption>
		<ufrn:subSistema teste="not lato">
		<tr>
			<th valign="top">
				<ufrn:subSistema teste="not infantil">Disciplina:</ufrn:subSistema>
				<ufrn:subSistema teste="infantil">Nível Infantil:</ufrn:subSistema>
			</th>
			<td colspan="2">
				<c:set var="idAjax" value="obj.disciplina.id" />
				<c:set var="nomeAjax" value="obj.disciplina.nome" />
				<c:set var="obrigatorio" value="true" />
				<%@include file="/WEB-INF/jsp/include/ajax/disciplina.jsp"%>
			</td>
		</tr>
		</ufrn:subSistema>
		<ufrn:subSistema teste="lato">
			<c:if test="${acesso.lato}">
								<tr>
					<th class="obrigatorio">Curso:</th>
					<td colspan="2">
						<html:select property="curso.id" styleId="curso" style="width:60%">
							<html:option value=""> -- SELECIONE -- </html:option>
							<html:options collection="cursosLato" property="id" labelProperty="nome" />
						</html:select>
					</td>
				</tr>
					<th class="obrigatorio">Disciplina:</th>
					<td colspan="2">
						<html:select property="obj.disciplina.id" styleId="disciplina" style="width:60%">
							<html:option value=""> -- SELECIONE -- </html:option>
						</html:select>
					</td>
				</tr>
				<ajax:select baseUrl="${pageContext.request.contextPath}/ajaxCursoLato?tipo=disciplina" source="curso"
						target="disciplina" parameters="id={curso}" postFunction="postCurso" executeOnLoad="true" />
			</c:if>
			<c:if test="${acesso.coordenadorCursoLato or acesso.secretarioLato}">
				<html:hidden property="curso.id" value="${ cursoLato.id }" />
				<tr>
					<th>Curso:</th>
					<td colspan="2"> ${ cursoLato.nome } </td>
				</tr>
				<tr>
					<th class="obrigatorio">Disciplina:</th>
					<td colspan="2">
						<html:select property="obj.disciplina.id" styleId="disciplina" style="width:60%">
							<html:option value=""> -- SELECIONE UMA OPÇÃO --  </html:option>
							<html:options collection="disciplinasCurso" property="id" labelProperty="codigoNome" />
						</html:select>
					</td>
				</tr>
			</c:if>
		</ufrn:subSistema>
		<c:if test="${turmaForm.obj.id != 0}">
		<tr>
			<th class="obrigatorio" width="20%">Código da Turma:</th>
			<td colspan="2">
				<html:text property="obj.codigo" size="2" maxlength="2" disabled="${turmaForm.matriculada}" />
			</td>
		</tr>
		</c:if>
		<ufrn:subSistema teste="infantil">
			<tr>
				<th class="obrigatorio">Ano:</th>
				<td><html:text property="obj.ano" size="4" maxlength="4" /> 
				</td>
			</tr>
		</ufrn:subSistema>
		<tr>
			<th class="obrigatorio">Local:</th>
			<td colspan="2">
				<html:text property="obj.local" size="50" maxlength="40" onkeyup="CAPS(this)" />
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">Capacidade de Alunos: </th>
			<td colspan="2">
				<html:text property="obj.capacidadeAluno" size="4" maxlength="4" />
			</td>
		</tr>
		<tr>
			<th>Observação: </th>
			<td colspan="2">
				<html:text property="obj.observacao" size="60" maxlength="60" />
				<ufrn:help img="/img/ajuda.gif">Se preenchido, o conteúdo desse campo será exibido no histórico do aluno </ufrn:help>
			</td>
		</tr>
		<ufrn:subSistema teste="tecnico">
		<c:if test="${usuario.unidade.id != 205}">
			<tr>
				<th>Especialidade da turma de Entrada: </th>
				<td colspan="2">
					<html:select property="obj.especializacao.id">
	              		<html:option value="-1">&gt; Opções</html:option>
	                	<html:options collection="especializacoes" property="id" labelProperty="descricao" />
	            	</html:select>
				</td>
			</tr>
		</c:if>
		</ufrn:subSistema>
		<ufrn:subSistema teste="not infantil">
			<tr>
				<th class="obrigatorio">Ano - Período:</th>
				<td><html:text property="obj.ano" size="4" maxlength="4" styleId="obj.ano" disabled="${turmaForm.obj.qtdMatriculados > 0}"/> -
				<html:text property="obj.periodo" size="1" maxlength="1" styleId="obj.periodo" disabled="${turmaForm.obj.qtdMatriculados > 0}"/> 
			<ufrn:subSistema teste="not lato">
				<html:link href="#" styleClass="mostraPeriodos">
				<html:img page="/img/cal_prefs.png" title="Usar datas da Unidade" />
				</html:link>&nbsp;&nbsp;&nbsp;
				<ajax:callout baseUrl="/sigaa/ajaxTurma" parameters="dispatch=callOutPeriodos" sourceClass="mostraPeriodos" 
						title="Datas da Unidade Responsável" overlib="STICKY,CLOSECLICK,DELAY,250,TIMEOUT,5000,VAUTO,WRAPMAX,240" />
			</ufrn:subSistema>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Data Início:</th>
				<td>
					<ufrn:calendar property="dataInicio" />
				</td>
			</tr>

			<tr>
				<th class="obrigatorio">Data Fim:</th>
				<td>
					<ufrn:calendar property="dataFim" />
				</td>

			</tr>
		</ufrn:subSistema>
 		<c:if test="${turmaForm.obj.id > 0}">
			<tr>
				<th>Situação da Turma:</th>
				<td>
					<html:select property="obj.situacaoTurma.id" disabled="${turmaForm.obj.situacaoTurma.id == 3 ? true : false }">
						<html:options collection="situacoes" property="id" labelProperty="descricao" />
					</html:select>
				</td>
			</tr>
		</c:if>
		<tfoot>
			<tr>
				<td colspan="3">
					<c:if test="${turmaForm.obj.id > 0 }"><html:button dispatch="resumir" value="Confirmar Alteração" /></c:if>
					<input value="Cancelar" type="button" onclick="javascript:cancelar();"/>
					<html:button dispatch="docentes" value="Avançar >>" />
					
				</td>
			</tr>
		</tfoot>
	</table>

</html:form>

<center>
	<br />
	<html:img page="/img/required.gif" style="vertical-align: top;" /> 
	<span class="fontePequena">Campos de preenchimento obrigatório.</span>
</center>

<script type="text/javascript">
<!--
function updateForm(radio) {
	if (radio.value == 'atual') {
		$('obj.ano').value = '${calAtual.ano}';
		$('obj.periodo').value = '${calAtual.periodo}';
		$('dataInicio').value = '${atualInicio}';
		$('dataFim').value = '${atualFim}';
	} else {
		$('obj.ano').value = '${calProx.ano}';
		$('obj.periodo').value = '${calProx.periodo}';
		$('dataInicio').value = '${proxInicio}';
		$('dataFim').value = '${proxFim}';
	}
}

function cancelar() {

	if (confirm('Deseja realmente cancelar a operação?')){
		document.location.href = '<%= request.getContextPath() %>/ensino/criarTurma.do?dispatch=list&voltando=true';
		return true;
	} else 
		return false;
}

//-->
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
