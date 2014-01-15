<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
<html:link action="/ensino/cadastroAfastamentoAluno?dispatch=cancel" >
	<ufrn:subSistema semLink="true" />
</html:link>
&gt; Registrar Retorno do Aluno
</h2>
<style>
	table.formulario td { font-weight: bold; }
</style>

<html:form action="/ensino/cadastroAfastamentoAluno" focus="dataRetorno">
	<html:hidden property="obj.id" />
	<html:hidden property="obj.usuarioRetorno.id" value="${sessionScope.usuario.id}"/>
	<html:hidden property="tipoLista" />
	<input name="cadastroRetorno" type="hidden" value="true" />

    <table class="formulario" width="80%">
       <caption>Dados do Afastamento</caption>

       <tbody>

        <tr>
            <th>Nome do Aluno: </th>
            <td>
            ${ formAfastamentoAluno.obj.discente.matricula } -
            ${ formAfastamentoAluno.obj.discente.pessoa.nome }
            </td>
        </tr>
		<tr>
			<th>Tipo de Afastamento: </th>
			<td>${ formAfastamentoAluno.obj.tipoMovimentacaoAluno.descricao }</td>
		</tr>
		<tr>
			<th>Data do Afastamento:</th>
			<td>
				<ufrn:format type="data" valor="${formAfastamentoAluno.obj.dataOcorrencia}"/>
			</td>
		</tr>
		<tr>
			<th>Data do Retorno:</th>
			<td>
				<ufrn:calendar property="dataRetorno"/>
			</td>
		</tr>
		<tfoot>
   		<tr>
   			<td colspan="2">
   				<html:button value="Confirmar" dispatch="cadastrarRetorno" />
   				<html:button value="Cancelar" dispatch="cancel" />
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
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>