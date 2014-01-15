<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/ensino/latosensu/cadastroBolsistaLato.do?dispatch=cancelar">
		<ufrn:subSistema semLink="true"/>
	</html:link> &gt;
	Cadastro de Bolsista
</h2>

<html:form action="/ensino/latosensu/cadastroBolsistaLato?dispatch=persist" method="post" focus="obj.discenteLato.pessoa.nome" styleId="form">
    <table class="formulario" width="82%">
		<caption>Bolsista de Lato Sensu</caption>
		<html:hidden property="obj.id" />
		<tbody>
			<tr>
 				<th nowrap="nowrap">Aluno:</th>
				<td>
					<c:set var="idAjax" value="obj.discenteLato.id"/>
	                <c:set var="nomeAjax" value="obj.discenteLato.pessoa.nome"/>
	                <c:set var="obrigatorio" value="true" />
	                <c:set var="nivel" value="L" />
	                <%@include file="/WEB-INF/jsp/include/ajax/discente.jsp" %>
                </td>
            </tr>
            <tr>
 				<th nowrap="nowrap">Entidade Financiadora:</th>
				<td>
					<html:select property="obj.entidadeFinanciadora.id">
						<html:option value="">-- SELECIONE --</html:option>
		            	<html:options collection="entidadesFinanciadoras" property="id" labelProperty="nome"/>
					</html:select>
					<span class="required">&nbsp;</span>
                </td>
            </tr>
            <tr>
 				<th nowrap="nowrap">Percentual:</th>
				<td>
					<html:text property="percentual" size="5" onkeydown="return(formataValor(this, event, 2))"/>
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