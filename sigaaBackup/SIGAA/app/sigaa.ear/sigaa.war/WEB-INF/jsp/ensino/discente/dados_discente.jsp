<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<script>
	<ufrn:subSistema teste="tecnico,medio">
		var selTurmaEntrada;
	
		function postCurriculo() {
			$('cursoId').value = ${discenteForm.discente.cursoTecnico.id};
			$('turmaEntradaId').value = ${discenteForm.discente.turmaEntradaTecnico.id};
			if (${discenteForm.discente.turmaEntradaTecnico.id} != "") {
				selTurmaEntrada.execute();
			}
		}
		
		function postTurmaEntrada() {
			$('turmaEntradaId').value = ${discenteForm.discente.turmaEntradaTecnico.id};
		}
	</ufrn:subSistema>
</script>

<h2><ufrn:steps /></h2>


<html:form action="/ensino/discente/wizard"  method="post">
	<html:hidden property="discente.id" />
	<table class="formulario" width="750">
	<caption>Dados do Aluno: ${discenteForm.discente.pessoa.nome}</caption>

	<tbody>

		<tr>
			<th>Unidade Responsável:</th>
			<td colspan="3">
			<html:hidden property="discente.gestoraAcademica.id"/>
			<b>${discenteForm.discente.gestoraAcademica.nome }</b> 
			</td>
		</tr>
		
		<c:if test="${sessionScope.discenteAntigo}">
			<tr>
				<th class="required">Matrícula:</th>
				<td colspan="3">
					<html:text property="discente.matricula" size="10" maxlength="10" onkeyup="formatarInteiro(this);"/> 
				</td>
			</tr>
			<tr>
				<th class="required"> Ano-Período Inicial: </th>
				<td>
					<html:text property="discente.anoIngresso" size="4" maxlength="4" onkeyup="formatarInteiro(this);"/> -
					<html:text property="discente.periodoIngresso" size="1" maxlength="1" onkeyup="formatarInteiro(this);"/>
				</td>
			</tr>
			<tr>
				<th class="required"> Status:</th>
				<td>
					<html:select property="discente.status">
						<html:option value="">-- SELECIONE --</html:option>
						<html:options collection="statusDiscente" property="id" labelProperty="descricao"/>
					</html:select>
				</td>
			</tr>
		</c:if>

		<tr>
			<th class="required">Curso:</th>
			<td colspan="4">
			<ufrn:subSistema teste="tecnico,medio">
				<html:select property="discente.estruturaCurricularTecnica.cursoTecnico.id" styleId="cursoId">
					<html:option value="">-- SELECIONE --</html:option>
					<html:options collection="cursosTecnicos" property="id" labelProperty="codigoNome"/>
				</html:select>
			</ufrn:subSistema>
			<ufrn:subSistema teste="portalCoordenadorLato">
				<ufrn:checkRole papeis="<%=new int[] { SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO }%>">
					${ discenteForm.discente.turmaEntrada.cursoLato.nome }
				</ufrn:checkRole>
			</ufrn:subSistema>
			<ufrn:subSistema teste="lato">
				<ufrn:checkRole papel="<%= SigaaPapeis.GESTOR_LATO %>">
					<html:select property="discente.turmaEntrada.cursoLato.id" styleId="cursoId">
						<html:option value="">-- SELECIONE --</html:option>
						<html:options collection="cursosLato" property="id" labelProperty="nome"/>
					</html:select>
				</ufrn:checkRole>
			</ufrn:subSistema>
			</td>
		</tr>
		<ufrn:subSistema teste="tecnico">
			<tr>
				<th class="required">Currículo</th>
				<td>
					<html:select property="discente.estruturaCurricularTecnica.id" styleId="curriculoId">
		                <html:option value="">-- SELECIONE --</html:option>
		                <html:options collection="curriculos" property="id" labelProperty="descricao"/>
		            </html:select>
		            <ajax:select baseUrl="${applicationScope.contexto}/ajaxEstruturaCurricularTecnica"
							parameters="cursoId={cursoId}"  executeOnLoad="true"
							source="cursoId" target="curriculoId" postFunction="postCurriculo"/>
				</td>
			</tr>
		</ufrn:subSistema>
		
		<tr>
			<th class="required">Turma de Entrada:</th>
			<td colspan="4">
			<ufrn:subSistema teste="tecnico,medio">
				<html:select property="discente.turmaEntradaTecnico.id" styleId="turmaEntradaId">
					<html:option value="">-- SELECIONE --</html:option>
					<html:options collection="turmasEntrada" property="id" labelProperty="descricao"/>
				</html:select>
				<ajax:select baseUrl="${applicationScope.contexto}/ajaxTurmaEntradaTecnico"
					var="selTurmaEntrada"
					parameters="cursoId={cursoId}" 
					source="cursoId" target="turmaEntradaId" postFunction="postTurmaEntrada"
					defaultOptions="${ discenteForm.discente.turmaEntradaTecnico.id}"  />
			</ufrn:subSistema>
			<ufrn:subSistema teste="lato, portalCoordenadorLato">
				<html:select property="discente.turmaEntrada.id" styleId="turmaEntradaId">
					<html:option value="">-- SELECIONE --</html:option>
					<html:options collection="turmasEntrada" property="id" labelProperty="descricao"/>
				</html:select>
				<ajax:select baseUrl="${applicationScope.contexto}/ajaxTurmaEntradaTecnico"
					parameters="cursoId={cursoId}"  executeOnLoad="true"
					source="cursoId" target="turmaEntradaId" />
			</ufrn:subSistema>
			</td>
		</tr>

		<tr>
			<ufrn:subSistema teste="tecnico">
				<th>Forma de <br>Ingresso:</th>
				<td>
				<html:select property="discente.formaIngresso.id" >
				<html:options collection="formasIngresso" property="id" labelProperty="descricao"/>
				</html:select>
				</td>
				<th>Regime do Aluno:</th>
				<td>
				<html:select property="discente.tipoRegimeAluno.id" >
				<html:options collection="tiposRegimeAluno" property="id" labelProperty="descricao"/>
				</html:select>
				</td>
			</ufrn:subSistema>
			<ufrn:subSistema teste="lato">
				<th>Forma de <br>Ingresso:</th>
				<td>
				<html:select property="discente.formaIngresso.id" >
				<html:options collection="formasIngresso" property="id" labelProperty="descricao"/>
				</html:select>
				</td>
				<th>Procedência do Aluno:</th>
				<td>
				<html:select property="discente.tipoProcedenciaAluno.id" >
				<html:options collection="tiposProcedenciaAluno" property="id" labelProperty="descricao"/>
				</html:select>
				</td>
			</ufrn:subSistema>
		</tr>


		<tr>
			<ufrn:subSistema teste="not tecnico">
				<th>Ano - Período <br>de Ingresso:</th>
				<td>
				<html:text property="discente.anoIngresso" maxlength="4" size="4" /> -
				<html:text property="discente.periodoIngresso" maxlength="1" size="1" />
				<span class="required">&nbsp;</span>
				</td>
			</ufrn:subSistema>
			<ufrn:subSistema teste="tecnico">
				<th>Concluiu o Ensino Médio?</th>
				<td>
				<html:radio property="discente.concluiuEnsinoMedio" value="true" styleClass="noborder">Sim</html:radio>
				<html:radio property="discente.concluiuEnsinoMedio" value="false" styleClass="noborder">Não</html:radio>
				</td>
			</ufrn:subSistema>
		</tr>

		<tr>
			<th valign="top">Observação:</th>
			<td colspan="4">
			<ufrn:textarea property="discente.observacao" maxlength="255" cols="60" rows="8" />
			</td>
		</tr>
	</tbody>

	<tfoot>
		<tr>
			<td colspan="5">
			<html:button view="dadosPessoais">&lt;&lt; Dados Pessoais</html:button>
			<html:button dispatch="cancelar" cancelar="true">Cancelar</html:button>
			<html:button dispatch="submeterDadosDiscente">Próximo &gt;&gt;</html:button>
			</td>
		</tr>
	</tfoot>
	</table>

</html:form>

<br/>
<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>