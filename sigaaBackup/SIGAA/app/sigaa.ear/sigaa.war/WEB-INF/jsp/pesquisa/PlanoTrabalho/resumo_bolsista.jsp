<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2> <ufrn:subSistema /> &gt; Resumo da Indicação/Substituição de Bolsista </h2>

<table class="formulario">
<tr><td>
	<table class="subformulario" width="100%">
	<caption>Dados do Orientador</caption>
	
	<c:if test="${acesso.pesquisa}">
	  <tr>
	    <td><b>Nome</b></td>
	    <td><b>CPF</b></td>
	  </tr>
	</c:if>

	<c:if test="${not acesso.pesquisa}">
	  <tr>
	    <td colspan="2"><b>Nome</b></td>
	  </tr>
	</c:if>
	
	<c:if test="${acesso.pesquisa}">
	  <tr>
	    <td> ${bolsista.planoTrabalho.orientador.pessoa.nome} </td>
	    <td> <ufrn:format type="cpf_cnpj" valor="${bolsista.planoTrabalho.orientador.pessoa.cpf_cnpj}" /> </td>
	  </tr>
	</c:if>

	<c:if test="${not acesso.pesquisa}">
	    <td colspan="2"> ${bolsista.planoTrabalho.orientador.pessoa.nome} </td>
	</c:if>

	  <tr>
	    <td> <b>Departamento</b> </td>
	    <td> <b>Fone/Ramal</b> </td>
	  </tr>
	  <tr>
	    <td> ${bolsista.planoTrabalho.orientador.unidade.nome} </td>
	    <td> ${orientador.ramal} </td>
	  </tr>
	  <tr>
	    <td> <b>E-mail</b> </td>
	    <td> <b>Código do Projeto</b> </td>
	  </tr>
	  <tr>
	    <td> ${orientador.email} </td>
	    <td> ${bolsista.planoTrabalho.projetoPesquisa.codigo} </td>
	  </tr>
	</table>
</td></tr>

<c:if test="${ bolsista.bolsistaAnterior != null }">
<tr><td>
	<table class="subformulario" width="100%">
	<caption>Dados do Bolsista Substituído <em>(finalizado em: <ufrn:format type="data" valor="${bolsista.bolsistaAnterior.dataFinalizacao}" />)</em></caption>
	  <tr>
	    <td><b>Nome</b></td>
	  </tr>
	  <tr>
	    <td colspan="1"> ${bolsista.bolsistaAnterior.discente.pessoa.nome} </td>
	  </tr>
	  <c:if test="${acesso.pesquisa}">
		  <tr>
		    <td> <b>CPF</b></td>
		    <td> <b>Motivo</b> </td>
		  </tr>
		  <tr>
		    <td> <ufrn:format type="cpf_cnpj" valor="${bolsista.bolsistaAnterior.discente.pessoa.cpf_cnpj}" /> </td>
		    <td> ${bolsista.bolsistaAnterior.motivoSubstituicao} </td>
		  </tr>
	  </c:if>
	  
	  <c:if test="${not acesso.pesquisa}">
		  <tr>
		    <td colspan="2"> <b>Motivo</b> </td>
		  </tr>
		  <tr>
		    <td colspan="2"> ${bolsista.bolsistaAnterior.motivoSubstituicao} </td>
		  </tr>	  
	  </c:if>	  
	  
	</table>
</td></tr>
</c:if>

<tr><td>
	<table class="subformulario" width="100%">
	<caption>Dados do Novo Bolsista <em>(indicado em: <ufrn:format type="data" valor="${bolsista.dataIndicacao}" />)</em></caption>
	  <tr>
	    <td colspan="4"><b>Título do Projeto</b></td>
	  </tr>
	  <tr>
	    <td colspan="4">${bolsista.planoTrabalho.projetoPesquisa.titulo}</td>
	  </tr>
	  <tr>
	    <td colspan="2"><b>Palavras-Chave</b></td>
	    <td colspan="2"><b>Área de Conhecimento</b></td>
	  </tr>
	  <tr>
	    <td colspan="2">${bolsista.planoTrabalho.projetoPesquisa.palavrasChave}</td>
	    <td colspan="2">${bolsista.planoTrabalho.projetoPesquisa.areaConhecimentoCnpq.nome}</td>
	  </tr>
	  <tr>
	    <td><b>Nome</b></td>
	    <td><b>Data de Nascimento</b></td>
	    <td><b>Sexo</b></td>
	  </tr>
	  <tr>
	    <td> ${bolsista.discente.pessoa.nome} </td>
	    <td> <ufrn:format type="data" valor="${bolsista.discente.pessoa.dataNascimento}" /> </td>
	    <td> ${bolsista.discente.pessoa.sexo} </td>
	  </tr>
	  <c:if test="${acesso.pesquisa}">
		  <tr>
		  	<td> <b>CPF</b></td>
		  	<td> <b>RG</b> </td>
		  	<td> <b>Órgão Emissor</b> </td>
		  	<td> <b>Data Emissão</b> </td>
		  </tr>
		  <tr>
		    <td> <ufrn:format type="cpf_cnpj" valor="${bolsista.discente.pessoa.cpf_cnpj}" /> </td>
		    <td> ${bolsista.discente.pessoa.identidade.numero} </td>
		    <td> ${bolsista.discente.pessoa.identidade.orgaoExpedicao} </td>
		    <td> <ufrn:format type="data" valor="${bolsista.discente.pessoa.identidade.dataExpedicao}" /> </td>
		  </tr>
	  </c:if>
	  <tr>
	    <td> <b>Número de Matrícula</b> </td>
	    <td> <b>Curso</b> </td>
	    <td> <b>Centro Acadêmico</b> </td>
	  </tr>
	  <tr>
	    <td> ${bolsista.discente.matricula} </td>
	    <td> ${bolsista.discente.curso.descricao} </td>
	    <td> ${bolsista.discente.curso.unidade.nome} </td>
	  </tr>
	  <tr>
	    <td> <b>Nacionalidade</b> </td>
	    <td> <b>País de Nascimento</b> </td>
	    <td> <b>Passaporte</b> </td>
	  </tr>
	  <tr>
	    <td> ${bolsista.discente.pessoa.pais.nacionalidade} </td>
	    <td> ${bolsista.discente.pessoa.pais.nome} </td>
	    <td> ${bolsista.discente.pessoa.passaporte} </td>
	  </tr>
	  <tr>
	  	<td> <b>Endereço</b> </td>
	    <td> <b>Número</b> </td>
	    <td> <b>Complemento</b> </td>
	    <td> <b>Bairro</b> </td>
	  </tr>
	  <tr>
	  	<td> ${bolsista.discente.pessoa.enderecoContato.logradouro} </td>
	    <td> ${bolsista.discente.pessoa.enderecoContato.numero} </td>  
	    <td> ${bolsista.discente.pessoa.enderecoContato.complemento} </td>
	    <td> ${bolsista.discente.pessoa.enderecoContato.bairro} </td>
	  </tr>
	  <tr>
	    <td> <b>CEP</b> </td>
	    <td> <b>Cidade</b> </td>
	    <td> <b>UF</b> </td>
	  </tr>
	  <tr>
	    <td> ${bolsista.discente.pessoa.enderecoContato.cep} </td>
	    <td> ${bolsista.discente.pessoa.enderecoContato.municipio.nome} </td>
	    <td> ${bolsista.discente.pessoa.enderecoContato.unidadeFederativa.sigla} </td>
	  </tr>
	  <tr>
	  	<td> <b>DDD</b> </td>
	  	<td> <b>Fone</b> </td>
	  	<td> <b>E-mail</b> </td>
	  	<td> <b>Celular</b> </td>
	  <tr>
	  <tr>
	  	<td> ${bolsista.discente.pessoa.codigoAreaNacionalTelefoneFixo} </td>
	    <td> ${bolsista.discente.pessoa.telefone} </td>
	    <td> ${bolsista.discente.pessoa.email} </td>
	    <td> ${bolsista.discente.pessoa.celular} </td>
	  </tr>
	  <tr>
	  	<td> <b>Nome do Banco</b> </td>
	  	<td> <b>Número da Agência</b> </td>
	  	<td> <b>Número da Conta</b> </td>
	  	<td> <b>Número da Operação</b> </td>
	  </tr>
	  <tr>
	    <td> ${bolsista.discente.pessoa.contaBancaria.banco.denominacao} </td>
	    <td> ${bolsista.discente.pessoa.contaBancaria.agencia} </td>
	    <td> ${bolsista.discente.pessoa.contaBancaria.numero} </td>
	    <td> ${bolsista.discente.pessoa.contaBancaria.operacao} </td>
	  </tr>
	  <tr>
	  	<td> <b>Data de Ingresso</b> </td>
	  	<td> <b>Cota</b> </td>
	  	<td> <b>Modalidade</b> </td>
	  </tr>
	  <tr>
	    <td> ${bolsista.dataInicio} </td>
	    <td> ${bolsista.planoTrabalho.cota.descricao} </td>
	    <td> ${bolsista.planoTrabalho.tipoBolsaString} </td>
	  </tr>
	</table>
</td></tr>
</table>
<br/>
<center>

<c:if test="${ param.dispatch != 'resumo' }">
	<html:link action="/pesquisa/indicarBolsista?dispatch=popular">Indicar/Substituir outro bolsista</html:link>
</c:if>
</center>


<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>