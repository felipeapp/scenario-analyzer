<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.componentes td {padding: 4px 2px 2px ; }
	tr.componentes1 td {padding: 1px 10px 2px ; border-bottom: 1px dashed #888;}
	tr.curso td {padding: 0px 0 0; border-bottom: 1px solid #555; }
</style>
<f:view>
	<h2> Listagem dos Alunos da Turma</h2>

	<br />
	<table width="100%">
		<tr>
			<td width="14%"><b></>Componente:</b></td><td>${buscaTurmaBean.turmaEscolhida.disciplina.descricao}</td>
		</tr>
		<tr>
			<td><b>Turma:</b></td><td>${buscaTurmaBean.turmaEscolhida.codigo}</td>
		</tr>
		<tr>
			<td><b>Docente(s):</b></td><td>${buscaTurmaBean.turmaEscolhida.docentesNomes}</td>
		</tr>
		<tr class="curso">
			<td><b>Horário:</b></td><td>${buscaTurmaBean.turmaEscolhida.descricaoHorario}</td>
		</tr>
	</table>
	<br />
	
	<table cellspacing="1" width="100%" style="font-size: 10px;">
	  <c:if test="${empty buscaTurmaBean.matriculados}">
		<tr><td>Nenhum aluno está matriculado nessa turma</td></tr>
	  </c:if>
	   <c:forEach items="${buscaTurmaBean.matriculados}" var="mat" varStatus="s">
		<c:if test="${not empty buscaTurmaBean.matriculados}">
				<tr class="componentes1">
					<td colspan="5"></td>
				</tr>
				<tr class="componentes">
					<td colspan="2"><b>Matrícula:</b> ${mat.discente.matricula }</td>
					<td align="left" colspan="2"><b>Nome:</b> ${mat.discente.nome}</td>
					<td align="left"><b>Sexo:</b> ${mat.discente.pessoa.sexo }</td>
				</tr>
		
				<tr class="componentes">
					<td colspan="2"><b>Naturalidade:</b> ${mat.discente.pessoa.municipio.nome }</td>
					<td><b>Nacionalidade:</b> ${mat.discente.pessoa.pais.nome }</td>
					<td colspan="2"><b>Fone:</b> ${mat.discente.pessoa.telefone}</td>
				</tr>
				
				<tr class="componentes">
					<td><b>RG:</b> ${mat.discente.pessoa.identidade.numero} ${mat.discente.pessoa.identidade.orgaoExpedicao}-${mat.discente.pessoa.identidade.unidadeFederativa.sigla}</td>
					<td><b>Data de Nasc.</b> <ufrn:format type="data" valor="${mat.discente.pessoa.dataNascimento}"/></td>
					<td><b>CPF:</b> <ufrn:format type="cpf_cnpj" valor="${mat.discente.pessoa.cpf_cnpj}"/>  </td>
					<td colspan="2"><b>Celular:</b> ${mat.discente.pessoa.celular}</td>
				</tr>

				<tr class="componentes">
					<td colspan="6"><b>Endereço:</b>
						${mat.discente.pessoa.enderecoContato.tipoLogradouro.descricao} ${mat.discente.pessoa.enderecoContato.logradouro}, ${mat.discente.pessoa.enderecoContato.numero}
						- ${mat.discente.pessoa.enderecoContato.bairro}
						- CEP: ${mat.discente.pessoa.enderecoContato.cep} - ${mat.discente.pessoa.enderecoContato.municipio.nome}/${mat.discente.pessoa.enderecoContato.unidadeFederativa.sigla}
					</td>
				</tr>
				<tr>
					<td colspan="6" align="left"><b>Pai:</b> ${mat.discente.pessoa.nomePai }</td>
				</tr>
				<tr>
					<td colspan="6" align="left"><b>Mãe:</b> ${mat.discente.pessoa.nomeMae}</td>
				</tr>
				<tr class="componentes">
					<td><b>Conta nº:</b> ${mat.discente.pessoa.contaBancaria.numero} </td>
					<td colspan="2"><b>Banco:</b> ${mat.discente.pessoa.contaBancaria.banco.denominacao}	</td>
					<td><b>Agência:</b> ${mat.discente.pessoa.contaBancaria.agencia} </td>
				</tr>
 		  </c:if>
		</c:forEach>
	</table>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>