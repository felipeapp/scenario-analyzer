<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
<html:link action="/ensino/cadastroAfastamentoAluno?dispatch=cancel" >
	<ufrn:subSistema semLink="true" />
</html:link> &gt;
<c:choose><c:when test="${ param.dispatch == 'desativar' }">
	Desativar Afastamento
</c:when><c:otherwise>
	${(formAfastamentoAluno.obj.id > 0)?'Alterar Registro de':'Registrar'} Afastamento
</c:otherwise></c:choose>

</h2>

<html:form action="/ensino/cadastroAfastamentoAluno" focus="obj.discente.pessoa.nome" styleId="form">
	<html:hidden property="obj.id" />
	<html:hidden property="obj.ativo" />
	<html:hidden property="obj.usuarioCadastro.id" value="${sessionScope.usuario.id}"/>
	<html:hidden property="tipoLista"/>
    <table class="formulario" width="95%">
       <caption>Dados do Afastamento</caption>

       <tbody>

       <tr>
            <th> Nome do Aluno:</th>
            <td>
                <c:set var="idAjax" value="obj.discente.id"/>
                <c:set var="nomeAjax" value="obj.discente.pessoa.nome"/>
                <c:set var="obrigatorio" value="true"/>
                <%@include file="/WEB-INF/jsp/include/ajax/discente.jsp" %>
            </td>
        </tr>
		<tr>
			<th>Tipo de Afastamento:</th>
			<td>
				<html:select property="obj.tipoMovimentacaoAluno.id"  styleId="tipoAfastamentoAluno" onblur="verificaApostilamento()" onchange="verificaApostilamento()">
                	<html:option value="">--SELECIONE--</html:option>
                	<html:options collection="tipoAfastamentoAlunoTecs" property="id" labelProperty="descricao"/>
                </html:select>
                <span class="required">&nbsp;</span><br/>
			</td>
		</tr>
		<%--
		<div id="verApostilamento">
		<tr>
			<th>Apostilamento:
				</th>
			<td>
				<html:radio property="obj.apostilamento" value="true"  label="Sim"/>
          		<html:radio property="obj.apostilamento" value="false" label="Não"/>
			</td>
		</tr>
					</div>
		--%>

		<tr>
			<th>Data de Afastamento:</th>
			<td>
				<ufrn:calendar property="dataAfastamento"/>
               	<span class="required">&nbsp;</span><br/>
			</td>
		</tr>

		<tfoot>
   		<tr>
   			<td colspan="2">
   				<html:button value="Confirmar" dispatch="persist" />
   				<input value="Cancelar" type="button" onclick="javascript:cancelar('form');"/>
   			</td>
   		</tr>
   		</tfoot>
   	</table>
</html:form>
<br>
<center>
<html:img page="/img/required.gif" style="vertical-align: top;" />
<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>
<br><br>
<%--
<script type="text/javascript">
<!--
	verificaApostilamento();
	function verificaApostilamento() {
		var selectAfastamentoAluno = document.getElementById('tipoAfastamentoAluno');
		if (selectAfastamentoAluno.options[selectAfastamentoAluno.selectedIndex].text == "Formado / Concluído") {
			$('verApostilamento').style.display = "";
		} else {
			$('verApostilamento').style.display = "none";
		}
	}

-->
</script>
--%>
<c:if test="${ param['dispatch'] == 'desativar' }">
<script type="text/javascript">
	new Remocao('form');
</script>
</c:if>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>