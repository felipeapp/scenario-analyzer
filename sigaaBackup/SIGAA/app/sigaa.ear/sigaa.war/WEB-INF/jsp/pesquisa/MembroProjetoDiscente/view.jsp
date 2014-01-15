<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Dados do Bolsista
</h2>

<html:form action="/pesquisa/buscarMembroProjetoDiscente" method="post">
<html:hidden property="obj.id"/>
<html:hidden property="obj.discente.pessoa.id"/>
<table class="formulario" style="width: 90%">
	<caption> Dados do Bolsista </caption>
	<tr>
		<td>Nome: </td>
		<td colspan="3"> ${membroProjetoDiscenteForm.obj.discente.pessoa.nome} </td>
		<td>Login: </td>
		<td>${usuarioBolsista.login} </td>
	</tr>
	<tr>
		<td> Data de Nascimento:</td>
		<td> <ufrn:format type="data" name="membroProjetoDiscenteForm" property="obj.discente.pessoa.dataNascimento" /></td>
		<td> Sexo: </td>
		<td colspan="3"> ${membroProjetoDiscenteForm.obj.discente.pessoa.sexo} </td>
	</tr>
	<tr> <td class="subFormulario" colspan="6"> Dados Pessoais  </td></tr>
	<c:if test="${acesso.pesquisa}">
		<tr>
			<td> CPF: </td>
			<td> <ufrn:format type="cpf_cnpj" name="membroProjetoDiscenteForm" property="obj.discente.pessoa.cpf_cnpj" /></td>
			
			<td> Identidade: </td>
			<td colspan="3">
				${membroProjetoDiscenteForm.obj.discente.pessoa.identidade}
				<c:if test="${!empty membroProjetoDiscenteForm.obj.discente.pessoa.identidade.dataExpedicao}">
					(expedida em <ufrn:format type="date" name="membroProjetoDiscenteForm" property="obj.discente.pessoa.identidade.dataExpedicao"/>)
				</c:if>
			</td>
		</tr>
	</c:if>
	<tr>
		<td width="20%"> Nacionalidade: </td>
		<td> ${membroProjetoDiscenteForm.obj.discente.pessoa.municipio.unidadeFederativa.pais.nacionalidade} </td>
		<td> País de Nascimento:</td>
		<td> ${membroProjetoDiscenteForm.obj.discente.pessoa.municipio.unidadeFederativa.pais.nome} </td>
		<td> Passaporte: </td>
		<td> ${membroProjetoDiscenteForm.obj.discente.pessoa.passaporte} </td>
	</tr>
	<tr>
		<td> E-mail: </td>
		<td colspan="5"> ${membroProjetoDiscenteForm.obj.discente.pessoa.email} </td>
	</tr>
	<tr>
		<td> Telefone Fixo: </td>
		<td> ${membroProjetoDiscenteForm.obj.discente.pessoa.telefone} </td>
		<td> Telefone Celular: </td>
		<td colspan="3"> ${membroProjetoDiscenteForm.obj.discente.pessoa.celular} </td>
	</tr>
	<tr>
		<td class="subFormulario" colspan="6"> Dados Bancários </td>
	</tr>
	<tr>
		<td> Banco: </td>
		<td>
			<ufrn:checkNotRole papel="<%= SigaaPapeis.GESTOR_PESQUISA%>">
				${membroProjetoDiscenteForm.obj.discente.pessoa.contaBancaria.banco.codigo} - ${membroProjetoDiscenteForm.obj.discente.pessoa.contaBancaria.banco.denominacao}
			</ufrn:checkNotRole>
			<ufrn:checkRole papel="<%= SigaaPapeis.GESTOR_PESQUISA%>">
				<html:select property="obj.discente.pessoa.contaBancaria.banco.id">
					<html:options collection="bancos" property="id" labelProperty="codigoNome" />
				</html:select>
			</ufrn:checkRole>
		</td>
		<td> Operação: </td>
		<td>
			<ufrn:checkNotRole papel="<%= SigaaPapeis.GESTOR_PESQUISA%>">
				${membroProjetoDiscenteForm.obj.discente.pessoa.contaBancaria.operacao}
			</ufrn:checkNotRole>
			<ufrn:checkRole papel="<%= SigaaPapeis.GESTOR_PESQUISA%>">
				<html:text property="obj.discente.pessoa.contaBancaria.operacao" size="10"/>
			</ufrn:checkRole>
		</td>
	</tr>
	<tr>
		<td class="obrigatorio"> Conta: </td>
		<td>
		<ufrn:checkNotRole papel="<%= SigaaPapeis.GESTOR_PESQUISA%>">
			${membroProjetoDiscenteForm.obj.discente.pessoa.contaBancaria.numero}
		</ufrn:checkNotRole>
		<ufrn:checkRole papel="<%= SigaaPapeis.GESTOR_PESQUISA%>">
			<html:text property="obj.discente.pessoa.contaBancaria.numero" size="10"/>
		</ufrn:checkRole>
		</td>
		<td class="obrigatorio"> Agência: </td>
		<td>
		<ufrn:checkNotRole papel="<%= SigaaPapeis.GESTOR_PESQUISA%>">
			${membroProjetoDiscenteForm.obj.discente.pessoa.contaBancaria.agencia}
		</ufrn:checkNotRole>
		<ufrn:checkRole papel="<%= SigaaPapeis.GESTOR_PESQUISA%>">
			<html:text property="obj.discente.pessoa.contaBancaria.agencia" size="10"/>
		</ufrn:checkRole>
		</td>
	</tr>
	<tr>
		<td> Tipo Conta: </td>
		<td>
			${ membroProjetoDiscenteForm.obj.descricaoTipoConta }		
		</td>
	</tr>
	<tr> <td class="subFormulario" colspan="6"> Endereço Residencial </td></tr>
	<tr>
		<td> Endereço: </td>
		<td colspan="5"> ${membroProjetoDiscenteForm.obj.discente.pessoa.enderecoContato}</td>
	</tr>
	<tr>
		<td> Bairro: </td>
		<td> ${membroProjetoDiscenteForm.obj.discente.pessoa.enderecoContato.bairro} </td>
		<td> Cidade: </td>
		<td> ${membroProjetoDiscenteForm.obj.discente.pessoa.enderecoContato.municipio}</td>
		<td> Estado: </td>
		<td>${membroProjetoDiscenteForm.obj.discente.pessoa.enderecoContato.unidadeFederativa.sigla}</td>
	</tr>
	<tr>
		<td> CEP: </td>
		<td  colspan="5">${membroProjetoDiscenteForm.obj.discente.pessoa.enderecoContato.cep}</td>
	</tr>
	<tr> <td class="subFormulario" colspan="6"> Informações Acadêmicas  </td></tr>
	<tr>
		<td> Matrícula: </td>
		<td colspan="5"> ${membroProjetoDiscenteForm.obj.discente.matricula }</td>
	</tr>
	<tr>
		<td> Curso: </td>
		<td  colspan="5"> ${membroProjetoDiscenteForm.obj.discente.curso }</td>
	</tr>
	
	<tr>
		<td> </td>
		<td> </td>
	</tr>
	<tr>
		<td> </td>
		<td> </td>
	</tr>
	<tr> <td class="subFormulario" colspan="6"> Dados da Bolsa  </td></tr>
	<tr>
		<td> Projeto de Pesquisa: </td>
		<td colspan="5"> ${membroProjetoDiscenteForm.obj.planoTrabalho.projetoPesquisa.codigo} </td>
	</tr>
	<tr>
		<td> Plano de Trabalho: </td>
		<td colspan="5"> ${membroProjetoDiscenteForm.obj.planoTrabalho.titulo != null ? membroProjetoDiscenteForm.obj.planoTrabalho.titulo : "<i> Título não disponível </i>"} </td>
	</tr>
	<tr>
		<td> Orientador: </td>
		<td>
			${membroProjetoDiscenteForm.obj.planoTrabalho.orientador.pessoa.nome }
			<c:if test="${acesso.pesquisa}">
				(<ufrn:format type="cpf_cnpj" name="membroProjetoDiscenteForm" property="obj.planoTrabalho.orientador.pessoa.cpf_cnpj" />)
			</c:if>
		</td>
		<td> Departamento: </td>
		<td colspan="3"> ${membroProjetoDiscenteForm.obj.planoTrabalho.orientador.unidade.sigla } </td>
	</tr>
	<c:if test="${acesso.pesquisa}">
		<tr>
			<td> Email: </td>
			<td> ${membroProjetoDiscenteForm.obj.planoTrabalho.orientador.pessoa.email } </td>
			<td> Telefone: </td>
			<td colspan="3"> ${membroProjetoDiscenteForm.obj.planoTrabalho.orientador.pessoa.telefone } </td>
		</tr>
	</c:if>
	<c:if test="${not acesso.pesquisa}">
		<tr>
			<td> Email: </td>
			<td colspan="5"> ${membroProjetoDiscenteForm.obj.planoTrabalho.orientador.pessoa.email } </td>
		</tr>
	</c:if>	
	<tr>
		<td> Modalidade da bolsa: </td>
		<td colspan="5"> ${membroProjetoDiscenteForm.obj.planoTrabalho.tipoBolsaString} </td>
	</tr>
	<tr>
		<td> Cota: </td>
		<td colspan="5"> ${membroProjetoDiscenteForm.obj.planoTrabalho.cota} </td>
	</tr>
	<tr>
		<td class="obrigatorio"> Período da Bolsa: </td>
		<td colspan="5">
			<ufrn:checkRole papel="<%= SigaaPapeis.GESTOR_PESQUISA%>">
				<ufrn:calendar property="dataInicio"/> a
				<ufrn:calendar property="dataFim"/>
			</ufrn:checkRole>
			<ufrn:checkNotRole papel="<%= SigaaPapeis.GESTOR_PESQUISA%>">
				<ufrn:format type="data" name="membroProjetoDiscenteForm" property="obj.dataInicio"/> a
				<ufrn:format type="data" name="membroProjetoDiscenteForm" property="obj.dataFim"/>
			</ufrn:checkNotRole>
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="6">
			<html:button onclick="javascript:history.go(-1)" value="<< Voltar"/>
			<ufrn:checkRole papel="<%= SigaaPapeis.GESTOR_PESQUISA%>">
				<html:button dispatch="persist" value="Confirmar Alterações"/>
			</ufrn:checkRole>
			</td>
		</tr>
	</tfoot>
</table>
</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>