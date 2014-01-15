<style>
	table.visualizacao tr td.subFormulario {
		padding: 3px 0 3px 20px;
	}
	p.corpo {
		padding: 2px 8px 10px;
		line-height: 1.2em;
	}
</style>
	<tr><td colspan="2" class="subFormulario"> Dados Gerais </td>
	<tr>
		<th> Titulo do Projeto: </th>
		<td> ${projeto.projeto.titulo} </td>
	</tr>
	<tr>
		<th> Tipo do Projeto: </th>
		<td> ${ projeto.tipo == 1 ? "INSTITUCIONAL" : "EXTERNO" } </td>
	</tr>
	<tr>
		<th> Situação do Projeto: </th>
		<td> ${projeto.projeto.situacaoProjeto.descricao} </td>
	</tr>
	<tr>
		<th> Entidade Concedente:	</th>
		<td> ${projeto.concedente.nome} </td>
	</tr>
	<tr>
		<th> Entidade Convenente:	</th>
		<td> ${projeto.convenente.nome} </td>
	</tr>
 	<tr>
		<th> Unidade Executora: </th>
		<td> ${ projeto.executora.nome } </td>
	<tr>
	<tr>
		<th> Unidade Interveniente: </th>
		<td> ${ projeto.interveniente.nome } </td>
	<tr>
	<tr><td colspan="2" class="subFormulario"> Coordenador Geral </td>
	<tr>
		<th> Nome:	</th>
		<td> ${projeto.coordenadorGeral.pessoa.nome} </td>
	</tr>
	<c:if test="${acesso.pesquisa}">
		<tr>
			<th> CPF:	</th>
			<td> <ufrn:format type="cpf_cnpj" name="projetoInfraPesq" property="obj.coordenadorGeral.pessoa.cpf_cnpj" /> </td>
		</tr>
	</c:if>
	<tr>
		<th> Titulação:	</th>
		<td> ${projeto.coordenadorGeral.formacao.denominacao} </td>
	</tr>
	<tr>
		<th> Telefone:	</th>
		<td> ${projeto.coordenadorGeral.pessoa.telefone} </td>
	</tr>
	<tr>
		<th> Email:	</th>
		<td> ${projeto.coordenadorGeral.pessoa.email} </td>
	</tr>
	
	<c:if test="${ projeto.projeto.idArquivo != null && projeto.projeto.idArquivo != 0 }">
		<tr>
			<th> Arquivo do Projeto: </th>
			<td>
			<html:link action="/enviarAquivo?dispatch=enviar&idarquivo=${projetoPesquisaForm.obj.projeto.idArquivo}" target="_blank">
				Visualizar arquivo
			</html:link>
			</td>
		</tr>
		<c:set var="novo" value="Novo "/>
	</c:if>

	<!-- DADOS DO PROJETO -->
	<tr><td colspan="2" class="subFormulario"> Corpo do Projeto </td>

	<tr> <th colspan="2" style="text-align: left;">  <b>Objetivos Gerais</b> </th> </tr>
	<tr>
	
		<td colspan="2"> <p class="corpo"> <ufrn:format type="texto" />${projeto.projeto.objetivos}</p> </td>
	</tr>
		
	<tr> <th colspan="2" style="text-align: left;">  <b>Benefícios esperados aos discentes envolvidos</b> </th> </tr>
	<tr>
		<td colspan="2"> <p class="corpo"> <ufrn:format type="texto"/>${projeto.beneficios} </p> </td>
	</tr>

	<tr><th colspan="2" style="text-align: left;">  <b>Retorno aos cursos e docentes </b> </th></tr>
	<tr>
		<td colspan="2"> <p class="corpo"> <ufrn:format type="texto"/>${projeto.retorno} </p> </td>
	</tr>
