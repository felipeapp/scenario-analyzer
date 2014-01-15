<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<h2 class="tituloPagina">
	<html:link action="ensino/ead/cadastroVeiculacoesCurso?dispatch=cancelar">
		<ufrn:subSistema semLink="true"/>
	</html:link>
	&gt; Veiculações de Curso de Ensino à distância
</h2>

<html:form styleId="formVeiculacaoCurso" action="ensino/ead/cadastroVeiculacoesCurso?dispatch=list">
<table class="formulario" width="90%">
		<caption>Dados do Curso</caption>

		<tbody>
			<tr>

				<td></td>
				<th nowrap="nowrap">Curso:</th>
				<td><b>${curso.unidade.nome} - ${curso.descricao}</b></td>
				<td></td>
			</tr>
			<tr>
				<td></td>
				<th nowrap="nowrap">Veiculações:</th>
				<td><html:select property="obj.tipoVeiculacaoEad.id"
							styleId="tipoVeiculacaoEad">
	                <html:option value=""> Opções </html:option>
	                <html:options collection="listaTipoVeiculacoes" property="id" labelProperty="descricao" />
                </html:select></td>
                <td><html:button value="Adicionar" onclick="adicionarVeiculacao()"/></td>
			</tr>
		</tbody>
	</table>
</html:form>

<br><br>

<ufrn:table collection="${listaVeiculacoesCurso}"
	properties="tipoVeiculacaoEad.descricao"
	headers="Tipo de Veiculação"
	title="Veiculação do Curso ${curso.descricao }" crud="false"
	links="src='${ctx}/img/delete.gif',?id={tipoVeiculacaoEad.id}&dispatch=removerVeiculacoes"
	linksRoles="<%=new int[][] {new int[] {SigaaPapeis.GESTOR_TECNICO}} %>"/>

<br>
<br>
<form>
<center>
	<input type="button" value="Confirmar Cadastro Veiculações" onclick="javascript:document.location.href='cadastroVeiculacoesCurso.do?dispatch=persist'">
	&nbsp;
	&nbsp;
	<input type="button" value="Cancelar" onclick="javascript:document.location.href='cadastroVeiculacoesCurso.do?dispatch=cancel'">
</center>
</form>
<br><br>
<script type="text/javascript">
<!--
function adicionarVeiculacao(){
	var id_tipoveiculacao = document.forms[0].tipoVeiculacaoEad.value;
	if(id_tipoveiculacao > 0){
		var action = "cadastroVeiculacoesCurso.do?dispatch=adicionarVeiculacoes&id="
		document.location.href = action.concat(id_tipoveiculacao);
	}else {
	alert("Selecione algum tipo de veiculação");
	}
}
//-->
</script>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
