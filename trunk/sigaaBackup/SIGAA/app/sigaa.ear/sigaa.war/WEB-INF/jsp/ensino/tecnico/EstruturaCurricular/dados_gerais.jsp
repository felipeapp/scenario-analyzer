<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina"><ufrn:steps /></h2>

<html:form action="/ensino/tecnico/estruturaCurricular/wizard"
	method="post" focus="obj.codigo">
	<html:hidden property="obj.id" />
	<table class="formulario" width="700">
		<caption>Dados Gerais da Estrutura Curricular</caption>
		<tbody>
			<tr>
				<th class="required">Código da Estrutura:</th>
				<td colspan="5"><html:text property="obj.codigo" maxlength="4"
					size="4" update="false" /></td>
			</tr>

			<tr>
				<th class="required">Curso:</th>
				<td colspan="5"><html:select property="obj.cursoTecnico.id">
					<html:option value="">-- SELECIONE --</html:option>
					<html:options collection="cursos" property="id"
						labelProperty="codigoNome" />
				</html:select></td>
			</tr>

			<tr>
				<th>Prazos de Conclusão:</th>
				<td width="120">( <html:select property="obj.unidadeTempo.id"
					onchange="atribuiTipoUnidadeTempo()" styleId="tipoUnidadeTempo">
					<html:option value="">--</html:option>
					<html:options collection="unidadesTempo" property="id"
						labelProperty="descricao" />
				</html:select> )</td>
				<th>Mínimo:</th>
				<td><html:text property="obj.prazoMinConclusao" maxlength="3"
					size="3" /> <span id="prazoMin"></span></td>
				<th>Máximo:</th>
				<td><html:text property="obj.prazoMaxConclusao" maxlength="3"
					size="3" /> <span id="prazoMax"></span></td>
			</tr>

			<tr>
				<th class="required">Carga Horária:</th>
				<td colspan="5"><html:text property="obj.cargaHoraria"
					maxlength="5" size="5" /> <ufrn:help img="/img/ajuda.gif">Essa C.H. deve ser compatível com a soma das C.H. <br>
			dos módulos a serem adicionados nessa estrutura curricular.
			</ufrn:help></td>
			</tr>

			<tr>
				<th class="required">Ano - Período de <br>
				Entrada em Vigor:</th>
				<td><html:text property="obj.anoEntradaVigor" maxlength="4"
					size="4" /> - <html:text
					property="obj.periodoEntradaVigor" maxlength="1" size="1" />
				</td>
				<th>Ativa:</th>
				<td colspan="3"><html:radio property="obj.ativa" value="true"
					styleClass="noborder">Sim</html:radio> <html:radio
					property="obj.ativa" value="false" styleClass="noborder">Não</html:radio>
				</td>
			</tr>

			<tr>
				<th>Turno:</th>
				<td colspan="5"><html:select property="obj.turno.id">
					<html:option value="">-- SELECIONE --</html:option>
					<html:options collection="turnos" property="id"
						labelProperty="descricao" />
				</html:select></td>
			</tr>

		</tbody>

		<tfoot>
			<tr>
				<td colspan="6"><html:button dispatch="cancelar">Cancelar</html:button>
				<html:button dispatch="submeterDadosGerais">Próximo &gt;&gt;</html:button>
				<c:if test="${estruturaCurTecnicoForm.obj.id > 0 }">
					<br>
					<br>
					<html:button dispatch="resumir" value="Confirmar Alteração" />
				</c:if></td>
			</tr>
		</tfoot>

	</table>
</html:form>
<script type="text/javascript">
<!--
	atribuiTipoUnidadeTempo();
	function atribuiTipoUnidadeTempo() {
		var escolhido = $('tipoUnidadeTempo').options[$('tipoUnidadeTempo').selectedIndex];
		if (escolhido.text == "Mês") {
			$('prazoMax').innerHTML = escolhido.text.toLowerCase() + "(es)";
			$('prazoMin').innerHTML = escolhido.text.toLowerCase() + "(es)";
		} else {
			$('prazoMax').innerHTML = escolhido.text.toLowerCase() + "(s)";
			$('prazoMin').innerHTML = escolhido.text.toLowerCase() + "(s)";
		}
	}
-->
</script>

<center><br>
<html:img page="/img/required.gif" style="vertical-align: top;" /> <span
	class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
<br>
</center>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>