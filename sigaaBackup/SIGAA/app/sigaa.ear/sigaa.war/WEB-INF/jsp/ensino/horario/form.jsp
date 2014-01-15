<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<ufrn:subSistema /> &gt; Hor�rios
</h2>

<html:form action="/ensino/cadastroHorario" method="post" focus="inicio" styleId="form">

	<html:hidden property="obj.id" />

	<table class="formulario" width="80%">
    <caption>Dados do Hor�rio</caption>

    <tbody>
    <ufrn:subSistema teste="not lato">
    <tr>
    	<th>Unidade Respons�vel:</th>
        <td colspan="3"><b>${sessionScope.usuario.unidade.gestoraAcademica.nome}</b>
		</td>
	</tr>
	</ufrn:subSistema>
	<tr>
    	<th class="required">Hora In�cio:</th>
        <td>
        	<html:text property="inicio" maxlength="5" size="5" /> (HH:MM)
        </td>
        <th class="required">Turno:</th>
        <td>
        <html:select property="obj.tipo" >
            <html:option value="">-- SELECIONE --</html:option>
            <html:option value="1">MANH�</html:option>
            <html:option value="2">TARDE</html:option>
            <html:option value="3">NOITE</html:option>
        </html:select>
        </td>
	</tr>

	<tr>
    	<th class="required">Hora Fim:</th>
        <td>
        <html:text property="fim" size="5" maxlength="5" styleId="horaFim" /> (HH:MM)
        </td>
        <th class="required">Ordem:</th>
        <td>
        <html:text property="obj.ordem" size="1" maxlength="2" styleId="ordem" />
        <ufrn:help img="/img/ajuda.gif">Se o hor�rio � o primeiro, segundo, terceiro ... do turno especificado</ufrn:help>
        </td>
	</tr>

	<tr>
		<td colspan="4" align="center"><br>
		<table width="90%" class="subFormulario">
			<caption>Ajuda</caption>
			<tr>
			<td width="40"><html:img page="/img/help.png"/> </td>
			<td valign="top" style="text-align: justify">
			Esses hor�rios devem representar todos os hor�rios de aula da gestora acad�mica.<br>
			Por exemplo: se as aulas da gestora acad�mica ocorrerem nos hor�rios: 07:00 �s 10:00,
			10:30 �s 12:00 e 14:00 �s 17:00; cada um desses hor�rios devem ser cadastrados para que
			no cadastramento das turmas de aula possa atribuir seus respectivos hor�rios.
			</td>
			</tr>
		</table>
		</td>
	</tr>
	</tbody>

	<tfoot>
		<tr><td colspan="4">
			<html:button dispatch="persist" value="Confirmar" styleId="botao" />
			<input value="Cancelar" type="button" onclick="javascript:cancelar('form');"/>
    	</td></tr>
	</tfoot>

	</table>

</html:form>

<center id="formRodape">
<br>
<html:img page="/img/required.gif" style="vertical-align: top;"/>
<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
<br><br>
</center>

<c:if test="${ param['dispatch'] == 'remove' }">
<script type="text/javascript">
	new Remocao('form');
</script>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
