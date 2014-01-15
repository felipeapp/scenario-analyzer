<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina"><ufrn:steps/></h2>

<input type="hidden" id="linkAjuda" value="/manuais/lato/Proposta/2_objetivos_importancia.htm">

<html:form action="/ensino/latosensu/criarCurso" method="post"  >
<table class="formulario" width="90%" >
<caption>Objetivos e Importância do Curso</caption>
	<tr>
		<td>Justificativa/Objetivos do Curso: 
			<html:img page="/img/required.gif" style="vertical-align: center;"/></td>
	</tr>
	<tr>
		<td align="center">
			<html:textarea property="proposta.justificativa"  cols="100" rows="10"/>
		</td>
	</tr>
	<tr>
		<td>Necessidade/Importância do Curso para as IES, Região e Área do Conhecimento:
			<html:img page="/img/required.gif" style="vertical-align: center;"/></td>
	</tr>
	<tr>
		<td align="center">
			<html:textarea property="proposta.importancia"  cols="100" rows="10"/>
		</td>
	</tr>
	<tfoot>
	<tr>
		<td colspan="4">
			<html:button dispatch="gravar" value="Gravar"/>
			<html:button view="dadosGerais" value="<< Voltar" />
			<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
			<html:button dispatch="selecao" value="Avançar >>"/>
		</td>
	</tr>
	</tfoot>
</table>
</html:form>

<center>
	<br />
		<html:img page="/img/required.gif" style="vertical-align: top;"/> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	<br />
</center>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>