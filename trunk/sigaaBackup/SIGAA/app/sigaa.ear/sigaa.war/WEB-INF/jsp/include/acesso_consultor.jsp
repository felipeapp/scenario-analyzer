<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	.codigoInvalido {
		font-size: 1.2em;
		font-weight: bold;
		color: #F00;
		text-align: center;
		margin: 100px 0;
	}

	.avaliacaoJustificada {
		font-size: 1.4em;
		font-weight: bold;
		color: #006E3A;
		text-align: center;
		margin: 100px 0;
	}

	td.confirmacao{
		background-color: #EFF3FA;
		font-variant: small-caps;
		color: #003395;
		font-weight: bold;
	}

	#justificativa td{
		padding-left: 25px;
	}

	#justificativa label{
		display: block;
		line-height: 2em;
		text-align: center;
	}

</style>

<script>
	confirm = true;
	function showJustificativa(confirm) {
		confirm = $('confirmacao').checked;
		if(confirm){
			$('justificativa').hide();
		}else{
			$('justificativa').show();
		}
	}
	$('justificativa').hide();
</script>

<h2> Acesso a Consultores Externos </h2>

<form action="${ctx}/acessoConsultor" method="post" name="form">

<c:if test="${not empty codigoInvalido }">
	<div class="codigoInvalido">
		${ codigoInvalido }
	</div>
</c:if>

<c:if test="${not empty avaliacaoJustificada }">
	<div class="avaliacaoJustificada">
		Sua justificativa foi cadastrada com sucesso! <br /> <br />
		Obrigado por sua colabora��o!
	</div>
</c:if>

<div class="descricaoOperacao">
	<p><center><strong>ATEN��O!</strong></center></p>
	<p>O sistema diferencia letras mai�sculas de min�sculas na senha, 
		portanto ela deve ser digitada da mesma maneira que est� no e-mail autom�tico.
		Caso tenha dificuldade, recomendamos utilizar o recurso de "copiar e colar". </p>
</div>

<c:if test="${empty codigoInvalido and empty avaliacaoJustificada}">
	<br/>
	<table class="formulario" style="width: 70%;">
		<caption> Caro consultor, confirme sua identifica��o </caption>
		<tr>
			<th width="40%">
				<label for="codigo"> Identifica��o: </label>
			</th>
			<td>
				<input type="hidden" name="codigo" value="${codigo}"/>
				<b>${codigo}</b>
			</td>
		</tr>
		<tr>
			<th>
				<label for="codigo"> Senha: </label>
			</th>
			<td>
				<input type="password" name="senha"/>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="confirmacao">
				<input type="radio" name="confirmacao" id="confirmacao" onclick="showJustificativa()" value="true" checked="checked"/>
				<label for="confirmacao"> Irei realizar a avalia��o dos projetos a mim destinados </label>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="confirmacao">
				<input type="radio" name="confirmacao" id="desistencia" onclick="showJustificativa()" value="false"/>
				<label for="desistencia"> N�o poderei realizar a avalia��o dos projetos a mim destinados </label>
			</td>
		</tr>
		<tbody id="justificativa">
		<tr>
			<td> Selecione uma justificativa: </td>
			<td>
				<select name="motivo" style="width: 80%">
					<option> Em viagem </option>
					<option> Doen�a </option>
					<option> Tempo escasso </option>
					<option> Conflito de interesses </option>
					<option> N�o ser da �rea dos projetos </option>
				</select> <br/>
			</td>
		</tr>
		<tr>
			<td> Outra espec�fica: </td>
			<td>
				<textarea name="justificativa" rows="2" style="width: 95%">${justificativa}</textarea>
			</td>
		</tr>
		</tbody>
		<tfoot>
		<tr>
			<td colspan="2">
				<input type="submit" value="Confirmar" />
			</td>
		</tr>
		</tfoot>
	</table>

	</form>

	<br />
	${login}
</c:if>

	<script>
	function showJustificativa(confirm) {
		confirm = $('confirmacao').checked;
		if(confirm){
			$('justificativa').hide();
		}else{
			$('justificativa').show();
		}
	}
	$('justificativa').hide();
	</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>