<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.pesquisa.form.ItemAvaliacaoForm"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt; Consultor Interno/Externo</h2>

<script type="text/javascript">
	function checkNome(bool) {
		document.getElementById('buscaNome').checked = true;
	}
</script>


<html:form action="/pesquisa/cadastroConsultor?dispatch=list" method="post" focus="obj.servidor.pessoa.nome">
<table class="formulario" width="50%">
	<caption>Busca por Consultores</caption>
	<tbody>
		<tr>
			<td><html:radio property="tipoBusca" value="2" styleClass="noborder" styleId="buscaNome" /></td>
    		<td><label for="nome">Nome:</label></td>
    		<td><html:text property="obj.servidor.pessoa.nome" size="50" 
    					onfocus="checkNome();" styleId="nome"/></td>
    	</tr>
    	<tr>
    		<td><html:radio property="tipoBusca" value="1" styleClass="noborder" styleId="todos"/></td>
    		<td colspan="2"><label for="todos">Todos</label></td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<html:hidden property="buscar" value="true"/>
				<html:submit><fmt:message key="botao.buscar" /></html:submit>
				<ufrn:button action="/verMenuPesquisa" confirma="true" value="Cancelar" />
    		</td>
    	</tr>
	</tfoot>
</table>
</html:form>

<br />

<center>
	<div class="infoAltRem">
			<html:img page="/img/alterar.gif" style="overflow: visible;"/>: Alterar
			<html:img page="/img/delete.gif" style="overflow: visible;"/>: Remover
			<html:img page="/img/pesquisa/certificado.png" style="overflow: visible;"/>: Certificado
			<html:img page="/img/comprovante.png" style="overflow: visible;"/>: Ver Credenciais
	</div>
</center>

<ufrn:table collection="${lista}" properties="nome, email, areaConhecimentoCnpq.nome, tipo"
		headers="Nome, E-mail, Área de Conhecimento, Tipo"
		title="Consultores Cadastrados" crud="false"
		links=" src='${ctx}/img/alterar.gif',?dispatch=edit&id={id},Alterar;
				src='${ctx}/img/delete.gif',?dispatch=remove&id={id},Remover;
				src='${ctx}/img/pesquisa/certificado.png',emissaoCertificadoConsultor.do?dispatch=emitir&obj.id={id}&propesq=true,Certificado;
				src='${ctx}/img/comprovante.png',?dispatch=viewCredenciais&id={id},Ver Credenciais"

		linksRoles="<%=new int[][] {
				   new int[] {SigaaPapeis.GESTOR_PESQUISA},
				   new int[] {SigaaPapeis.GESTOR_PESQUISA},
				   new int[] {SigaaPapeis.GESTOR_PESQUISA},
				   new int[] {SigaaPapeis.ADMINISTRADOR_PESQUISA}}%>"/>

<script type="text/javascript">
	checkNome();
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>