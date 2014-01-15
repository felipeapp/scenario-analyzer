<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<link rel="stylesheet" media="all" href="/sigaa/css/atestado_matricula.css" type="text/css" />

<f:view>
<h3>Requerimento</h3>
<h:outputText value="#{ atestadoMatricula.create }"/>
<h3>${fn:toUpperCase(requerimento.obj.tipo.descricao)}</h3>

<h2>Código: ${requerimento.obj.codigoProcesso}</h2>

<table id="identificacao">

	<tr>
		<td width="15%"><strong>Matrícula:</strong></td>
		<td width="45%"> ${ requerimento.obj.discente.matricula }</td>
		<td width="10%"><strong>Vínculo:</strong></td>
		<td>${requerimento.obj.discente.tipoString }</td>
	</tr>
	<tr>
		<td><strong>Nome:</strong></td>
		<td colspan="3">${ requerimento.obj.discente.pessoa.nome }</td>
	</tr>
	
	<tr>
		<td><strong>CPF:</strong></td>
		<td colspan="3">${requerimento.obj.discente.pessoa.cpf_cnpj}</td>
	</tr>

	<tr>
		<td><strong>Curso:</strong></td>
		<td>${ requerimento.obj.discente.curso.descricao } - ${ requerimento.obj.discente.matrizCurricular.turno.sigla }</td>
		<td><strong>Cidade:</strong></td>
		<td>${ requerimento.obj.discente.curso.municipio.nome }</td>
	</tr>
	
	<tr>
		<td><strong>Endereço:</strong></td>
		<td>${requerimento.obj.discente.pessoa.enderecoContato.descricao}</td>
		<td><strong>Bairro:</strong></td>
		<td>${requerimento.obj.discente.pessoa.enderecoContato.bairro}</td>	
	</tr>	

	<tr>
		<td><strong>Telefone:</strong></td>
			<td>${requerimento.obj.discente.pessoa.telefone}</td>
	</tr>								
							
	<tr>
		<td></td>
		<td>${requerimento.obj.discente.pessoa.celular}</td>
	</tr>															
							
	<tr>
		<td><strong>Email:</strong></td>
		<td>
			<c:choose>
	 			<c:when test="${requerimento.obj.discente.pessoa.email != null}">
					${fn:toUpperCase(requerimento.obj.discente.pessoa.email)} 
				</c:when>
									
				<c:otherwise>
					<i>Não possui email cadastrado.</i> 
				</c:otherwise>
									
			</c:choose>						
		</td>
	</tr>						
		
	<tr>
		<td><strong>Status:</strong></td>
		<td colspan="3">${fn:toUpperCase(requerimento.obj.status.descricao)}</td>
	</tr>

	<tr>
		<td><strong>Data:</strong></td>
		<td colspan="3"><fmt:formatDate value="${requerimento.obj.dataAtualizado}" pattern="dd/MM/yyyy 'às' HH:mm:ss" /></td>
	</tr>
	
</table>

<br />
<c:if test="${requerimento.obj.tipo.id == 4}">
	<h4>Ano-Periodo Base:</h4>
	
	<table id="matriculas" cellspacing="0">
		<tr>
			<td>${requerimento.obj.anoBase}.${requerimento.obj.periodoBase}</td>
		</tr>
	</table>

	<h4>Quantidade de Semestres:</h4>
	
	<table id="matriculas" cellspacing="0">
		<tr>
			<td>${requerimento.obj.trancarQtdSemestres}</td>
		</tr>
	</table>
</c:if>

<h4>Solicitação</h4>

<table id="matriculas" cellspacing="0">
	<tr>
		<td>${requerimento.obj.solicitacao}</td>
	</tr>
</table>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>