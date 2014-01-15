<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/ensino/latosensu/cadastroNoticiaLato.do?dispatch=cancelar">
		<ufrn:subSistema semLink="true"/>
	</html:link> &gt;
	Cadastro de Notícia
</h2>

<html:form action="/ensino/latosensu/cadastroNoticiaLato?dispatch=persist" method="post" focus="obj.titulo" styleId="form">
    <table class="formulario" width="90%">
		<caption>Dados da Notícia</caption>
		<html:hidden property="obj.id" />
		<tbody>
			<tr>
 				<th nowrap="nowrap">Título:</th>
				<td>
					<html:text property="obj.titulo" size="95" />
					<span class="required">&nbsp;</span>
                </td>
            </tr>
			<tr>
 				<th nowrap="nowrap">Texto:</th>
				<td>
					<html:textarea property="obj.texto"  cols="92" rows="10"/>
					<span class="required">&nbsp;</span>
                </td>
            </tr>
            <tr>
 				<th nowrap="nowrap">Data:</th>
				<td>
					<ufrn:calendar property="data" />
					<span class="required">&nbsp;</span>
                </td>
            </tr>
            <tr>
 				<th nowrap="nowrap">Publicada:</th>
				<td>
					<html:radio property="obj.publicada" styleClass="noborder" value="true" />Sim
	   				<html:radio property="obj.publicada" styleClass="noborder" value="false" />Não
	   				<span class="required">&nbsp;</span>
                </td>
            </tr>
        </tbody>
		<tfoot>
   			<tr>
				<td colspan="2">
				<html:submit value="Confirmar" />
                <input value="Cancelar" type="button" onclick="javascript:cancelar('form');"/>
                </td>
			</tr>
		</tfoot>
	</table>
</html:form>

<c:if test="${ param['dispatch'] == 'remove' }">
<script type="text/javascript">
	new Remocao('form');
</script>
</c:if>
<center>
<br>
<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
<br>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>