<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<h2 class="tituloPagina"><ufrn:steps /></h2>

<html:form  action="/pessoa/wizard"  method="post" >
<html:hidden property="pessoa.id" />

	<table class="formulario" width="99%">
	<caption>Dados da Pessoa Jurídica</caption>

	<tbody>
		<tr>
		<th width="140">CNPJ:</th>
		<td colspan="3">
		<ufrn:format type="cpf_cnpj" valor="${pj.pessoa.cpf_cnpj }" />
		</td>
		</tr>

		<tr>
		<th>Nome Fantasia:</th>
		<td colspan="3">
		${pj.nomeFantasia }
		</td>
		</tr>

		<tr>
		<th>E-mail:</th>
		<td colspan="3">${pj.pessoa.email }</td>
		</tr>

		<tr>
		<th>
		Razão Social:
		</th>
		<td colspan="3">
		${pj.razaoSocial}
		</td>
		</tr>

		<tr>
		<th>
		Nome do Dirigente:
		</th>
		<td colspan="3">
		${pj.nomeDirigente}
		</td>
		</tr>

		<tr>
		<th>
		Cargo do Dirigente:
		</th>
		<td colspan="3">
		${pj.cargoDirigente}
		</td>
		</tr>

		<tr>
		<th>
		Atividade Fim:
		</th>
		<td colspan="3">
		${pj.atividadeFim}
		</td>
		</tr>

		<tr>
		<th>
		Esfera Administrativa:
		</th>
		<td colspan="3">
		${pj.tipoEsferaAdministrativa.descricao}
           &nbsp;&nbsp;&nbsp;
		Outras:
		${pj.esferaAdministrativaOutra}
		</td>
		</tr>

		<tr>
		<th>
		Empresa Privada:
		</th>
		<td width="150">
		<ufrn:format type="bool_sn" valor="${pj.privada}" />
		</td>
		<th width="150">
		Fins lucrativos:
		</th>
		<td>
		<ufrn:format type="bool_sn" valor="${pj.finsLucrativos}" />
		</td>
		</tr>

		<tr>
		<th>
		Registro no CNSS:
		</th>
		<td>
		${pj.registroCnss}
		</td>
		<th>
		N.&deg; Declaração Federal:
		</th>
		<td>
		${pj.numDeclaracaoFederal}
		</td>
		</tr>

		<tr>
		<th>
		N.&deg; Declaração Estadual:
		</th>
		<td>
		${pj.numDeclaracaoEstadual}
		</td>
		<th>
		N.&deg; Declaração Municipal:
		</th>
		<td>
		${pj.numDeclaracaoMunicipal}
		</td>
		</tr>

	<tr>
	<td colspan="4">
		<table width="100%" class="subFormulario">
		<caption>Informações Para Contato</caption>
		<tr>
		<th>Logradouro:</th>
		<td colspan="3">
		${pj.pessoa.enderecoContato.tipoLogradouro.descricao}
		${pj.pessoa.enderecoContato.logradouro}
		</td>
		<th>
		N.&deg;:
		</th>
		<td>
		${pj.pessoa.enderecoContato.numero}
		</td>
		</tr>

		<tr>
		<th>Bairro:</th>
		<td>
		${pj.pessoa.enderecoContato.bairro}
		</td>
		<th>Complemento:</th>
		<td>
		${pj.pessoa.enderecoContato.complemento}
		</td>
		<th>CEP:</th>
		<td>
		${pj.pessoa.enderecoContato.cep}
		</td>
		</tr>

		<tr>
		<th>UF:</th>
		<td>
		${pj.pessoa.enderecoContato.unidadeFederativa.descricao}
		</td>
		<th>Município:</th>
		<td colspan="4">
		${pj.pessoa.enderecoContato.municipio.nome}
		</td>
		</tr>

		<tr>
		<th>Tel. Fixo:</th>
		<td>
		(${pj.pessoa.codigoAreaNacionalTelefoneFixo})
		${pj.pessoa.telefone }
		</td>
		<th>Tel. Celular:</th>
		<td colspan="4">
		(${pj.pessoa.codigoAreaNacionalTelefoneCelular})
		${pj.pessoa.celular }
		</td>
		</tr>
	</td>
	</tr>

	</tbody>

	<tfoot>
		<tr>
		<td colspan="6">
			<html:button dispatch="persist">Confirmar</html:button>
			<html:button dispatch="cancelar">Cancelar</html:button>
		</td>
		</tr>
	</tfoot>

	</table>

</html:form>

<br><br>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>