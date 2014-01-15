<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<ufrn:subSistema /> &gt; Horários
</h2>

<html:form action="/ensino/cadastroHorario" method="post" focus="inicio" styleId="form">

	<html:hidden property="obj.id" />

	<table class="formulario" width="80%">
    <caption>Dados do Horário</caption>

    <tbody>
    <ufrn:subSistema teste="not lato">
    <tr>
    	<th>Unidade Responsável:</th>
        <td colspan="3"><b>${sessionScope.usuario.unidade.gestoraAcademica.nome}</b>
		</td>
	</tr>
	</ufrn:subSistema>
	<tr>
    	<th class="required">Hora Início:</th>
        <td>
        	<html:text property="inicio" maxlength="5" size="5" /> (HH:MM)
        </td>
        <th class="required">Turno:</th>
        <td>
        <html:select property="obj.tipo" >
            <html:option value="">-- SELECIONE --</html:option>
            <html:option value="1">MANHÃ</html:option>
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
        <ufrn:help img="/img/ajuda.gif">Se o horário é o primeiro, segundo, terceiro ... do turno especificado</ufrn:help>
        </td>
	</tr>

	<tr>
		<td colspan="4" align="center"><br>
		<table width="90%" class="subFormulario">
			<caption>Ajuda</caption>
			<tr>
			<td width="40"><html:img page="/img/help.png"/> </td>
			<td valign="top" style="text-align: justify">
			Esses horários devem representar todos os horários de aula da gestora acadêmica.<br>
			Por exemplo: se as aulas da gestora acadêmica ocorrerem nos horários: 07:00 às 10:00,
			10:30 às 12:00 e 14:00 às 17:00; cada um desses horários devem ser cadastrados para que
			no cadastramento das turmas de aula possa atribuir seus respectivos horários.
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
<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
<br><br>
</center>

<c:if test="${ param['dispatch'] == 'remove' }">
<script type="text/javascript">
	new Remocao('form');
</script>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
