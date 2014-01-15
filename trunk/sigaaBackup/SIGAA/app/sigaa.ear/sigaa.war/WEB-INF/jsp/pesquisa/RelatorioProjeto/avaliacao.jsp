<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.formulario td.campo {
		padding: 4px 12px;
	}
</style>

<h2 class="tituloPagina">
	<ufrn:subSistema /> &gt;
	Avaliação de Relatórios de Projetos de Pesquisa
</h2>

<html:form action="/pesquisa/avaliarRelatorioProjeto" method="post" focus="obj.parecerConsultor">
<html:hidden property="obj.id"/>

<table class="formulario" width="90%">
<caption> Avaliação de Relatório de Projeto de Pesquisa</caption>
<tbody>
	<tr>
		<td width="25%"> <b>Projeto de Pesquisa:</b> </td>
		<td> ${formRelatorioProjeto.obj.projetoPesquisa.codigo} - ${formRelatorioProjeto.obj.projetoPesquisa.titulo} </td>
	</tr>
	<tr>
		<td colspan="2"> <b>Descrição do Projeto:</b> </td>
	</tr>
	<tr>
		<td colspan="2" class="campo"> ${formRelatorioProjeto.obj.projetoPesquisa.descricao} </td>
	</tr>
	<tr>
		<td colspan="2"> <b>Resumo do Relatório:</b> </td>
	</tr>
	<tr>
		<td colspan="2" class="campo"> ${formRelatorioProjeto.obj.resumo} </td>
	</tr>
	<tr>
		<td colspan="2"> <b>Parecer Anterior:</b> </td>
	</tr>
	<tr>
		<td colspan="2" class="campo"> ${formRelatorioProjeto.parecerAnterior} </td>
	</tr>
	
	<tr>
		<td colspan="2" class="subFormulario"> Parecer <html:img page="/img/required.gif" style="vertical-align: top;"/> </td>
	</tr>
	<tr>
		<td colspan="2" align="center"> <html:textarea property="obj.parecerConsultor" rows="4" style="width: 96%;" /> </td>
	</tr>
</tbody>
<tfoot>
	<tr>
		<td colspan="2">
			<html:button dispatch="aprovar" value="Aprovar"/>
			<html:button dispatch="reprovar" value="Necessita Correção"/>
			<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
		</td>
	</tr>
</tfoot>
</table>

</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>