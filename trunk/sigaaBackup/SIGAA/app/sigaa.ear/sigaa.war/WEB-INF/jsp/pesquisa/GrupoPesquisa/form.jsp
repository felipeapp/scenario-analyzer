<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Cadastro de Grupos de Pesquisa
</h2>

<html:form action="/pesquisa/cadastroGrupoPesquisa?dispatch=persist" method="post" focus="obj.codigo"  styleId="form">
	<html:hidden property="obj.id" />
	<input type="hidden" name="desativar" value="true" />
	<table class="formulario" width="75%">
        <caption>Dados do Grupo de Pesquisa</caption>
        <tbody>
        <tr>
            <th class="required">Código:</th>
            <td>
                <html:text property="obj.codigo" size="11"/>
            </td>
        </tr>
        <tr>
            <th class="required">Nome:</th>
            <td>
                <html:text property="obj.nome" size="80"/>
            </td>
        </tr>
		<tr>
			<th>Coordenador:</th>
			<td>
				<c:set var="idAjax" value="obj.coordenador.id"/>
				<c:set var="nomeAjax" value="obj.coordenador.nome"/>
				<c:set var="obrigatorio" value="true"/>
				<c:set var="somenteInternos" value="true"/>
				<%@include file="/WEB-INF/jsp/include/ajax/docente.jsp" %>
			</td>
		</tr>
        <tr>
            <th class="required">Home Page:</th>
            <td>
                <html:text property="obj.homePage" size="80"/>
            </td>
        </tr>
        <tr>
            <th class="required">E-Mail:</th>
            <td>
                <html:text property="obj.email" size="80"/>
            </td>
        </tr>
        <tr>
            <th class="required" nowrap="nowrap">Área Conhecimento:</th>
            <td>
               <html:select property="obj.areaConhecimentoCnpq.id">
	               	<html:options collection="areas" property="id" labelProperty="nome"/>
               </html:select>
            </td>
        </tr>


		<tfoot>
			<tr><td colspan="2">
				<html:submit>Confirmar</html:submit>
		    	<input value="Cancelar" type="button" onclick="javascript:cancelar('form');"/>
	    	</td></tr>
		</tfoot>
	</table>
</html:form>

<c:if test="${ param['dispatch'] == 'remove' }">
<script type="text/javascript">
	new Remocao('form');
</script>
</c:if>
<br>
<center>
<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>
<br>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>