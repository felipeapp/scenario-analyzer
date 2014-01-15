<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.pesquisa.form.SubstituicoesBolsistasForm"%>
<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Relatório de Substituição de Bolsistas
</h2>

<html:form action="/pesquisa/relatorioSubstituicoesBolsistas" method="post">

<table class="formulario" width="100%">
    <caption>Informe os critérios de busca</caption>
    <tbody>
	   <tr>
	   		<td width="3%"> </td>
	   		<td class="required"> Período: </td>
	   		<td> <ufrn:calendar property="inicio"/> a <ufrn:calendar property="fim"/> </td>
	   </tr>
		<tr>
			<td> </td>
			<td> <label for="cota">Cota:</label> </td>
			<td>
				<html:select property="cota.id" style="width:50%">
					<html:options collection="cotas" property="id" labelProperty="descricao" />
				</html:select>
			</td>
		</tr>
	   <tr>
	   		<td> </td>
	   		<td> Ordenar por: </td>
	   		<td>
				<html:radio property="ordenacao" value="dataIndicacao" styleId="ordenarPorData"/>
				<label for="ordenarPorData"> Data de indicação e Nome do aluno </label>

				<html:radio property="ordenacao" value="discente" styleId="ordenarPorAluno" />
				<label for="ordenarPorAluno"> Nome do aluno </label>

				<html:radio property="ordenacao" value="orientador" styleId="ordenarPorOrientador" />
				<label for="ordenarPorOrientador"> Nome do orientador </label>
				
				<html:radio property="ordenacao" value="dataOrientador" styleId="ordenarPorDataOrientador" />
				<label for="ordenarPorDataOrientador"> Data e Nome do orientador </label>
	   		</td>
	   </tr>
	   <tr> <td colspan="3" class="subFormulario"> Critérios opcionais </td> </tr>
	   <tr>
			<td> <html:checkbox property="filtros" styleId="tipoBolsa" value="<%="" + SubstituicoesBolsistasForm.FILTRO_TIPO_BOLSA%>" styleClass="noborder"/> </td>
	   		<td> <label for="tipoBolsa"> Tipo de Bolsa: </label> </td>
	   		<td>
				<html:select property="tipoBolsa" style="width: 50%" onchange="$('tipoBolsa').checked = true;">
					<html:options collection="tiposBolsa" property="key" labelProperty="value"/>
				</html:select>
	   		</td>
	   </tr>
		<tr>
			<td> <html:checkbox property="filtros" styleId="orientador" value="<%="" + SubstituicoesBolsistasForm.FILTRO_DOCENTE%>" styleClass="noborder"/> </td>
			<td> <label for="orientador">Orientador:</label> </td>
			<td>
				<c:set var="idAjax" value="orientador.id"/>
				<c:set var="nomeAjax" value="orientador.pessoa.nome"/>
				<c:set var="todosDocentes" value="true"/>
				<%@include file="/WEB-INF/jsp/include/ajax/docente.jsp" %>
                <script type="text/javascript">
					function docenteOnChange() {
						getEl('orientador').dom.checked = true;
					}
				</script>
			</td>
		</tr>
	   <tr>
			<td> <html:checkbox property="filtros" styleId="ativosInativos" value="<%="" + SubstituicoesBolsistasForm.FILTRO_ATIVOS_INATIVOS%>" styleClass="noborder"/> </td>
	   		<td> <label for="ativosInativos"> Status dos Bolsistas: </label> </td>
	   		<td>
				<html:select property="ativos" style="width: 50%" onchange="$('ativosInativos').checked = true;">
					<html:option value="true"> SOMENTE ATIVOS </html:option>
					<html:option value="false"> SOMENTE INATIVOS </html:option>
				</html:select>
	   		</td>
	   </tr>
	</tbody>

	<tfoot>
		<tr>
			<td colspan="3">
				<html:button dispatch="gerar" value="Consultar"/>
				<html:button dispatch="cancelar" value="Cancelar"/>
			</td>
		</tr>
	</tfoot>
</table>

<br/>
<div class="obrigatorio">Campos de preenchimento obrigatório.</div>

</html:form>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>