<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	.descricaoOperacao{
		font-size: 1.2em;
	}

	h3, h4 {
		font-variant: small-caps;
		text-align: center;
		margin: 2px 0 20px;
	}	
	
	h4 { margin: 15px 0 20px; }
	
	.descricaoOperacao p { text-align: justify; } 
	
	.codVer{text-align: center;display: block;position: relative;width: 100%;}
	
	.maiuscula{text-transform: capitalize;}	
</style>

<f:view>
	<h:form id="formComprovanteInscricaoImpressao">
	<h2>${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.editalProcessoSeletivo.nome)}</h2>
	<h3 class="maiuscula">Comprovante de Inscrição - Número ${inscricaoSelecao.obj.numeroInscricao} </h3>

	
	<c:if test="${inscricaoSelecao.obj.processoSeletivo.possuiAgendamento || not empty inscricaoSelecao.obj.processoSeletivo.editalProcessoSeletivo.orientacoesInscritos}">
	<div class="descricaoOperacao">
		<%-- USO EXCLUSIVO DA GRADUAÇÃO - TRANSFERÊNCIA VOLUNTÁRIA --%>
		<c:if test="${not empty inscricaoSelecao.obj.processoSeletivo.matrizCurricular && inscricaoSelecao.obj.processoSeletivo.possuiAgendamento}">
		<p>
			<b>O inscrito deverá comparecer no município  de 
			${inscricaoSelecao.obj.processoSeletivo.matrizCurricular.curso.municipio.nome}, data 	
			<ufrn:format type="data" valor="${inscricaoSelecao.obj.agenda.dataAgenda}" />
			, para entrega dos documentos, conforme descrito no edital.</b> 
		</p>	
		</c:if>

		<c:set var="orientacoes" value="${inscricaoSelecao.obj.processoSeletivo.editalProcessoSeletivo.orientacoesInscritos}" />
		<c:if test="${not empty orientacoes}">
			<h4> Orientações Importantes</h4>
			${orientacoes}
		</c:if>
	</div>
	</c:if>
	
	<table class="tabelaRelatorio" width="100%">
		<caption>Dados do Processo Seletivo</caption>
		<body>
			<tr>
				<th width="120px">${inscricaoSelecao.obj.pessoaInscricao.estrangeiro?'Passaporte:':'CPF:'}</th>
				<td colspan="3">
					<c:choose>
						<c:when test="${inscricaoSelecao.obj.pessoaInscricao.estrangeiro}">
							${inscricaoSelecao.obj.pessoaInscricao.passaporte}
						</c:when>
						<c:otherwise>
							<ufrn:format type="cpf_cnpj" valor="${inscricaoSelecao.obj.pessoaInscricao.cpf}"/>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>

			<tr>
				<th	>Nome:</th>
				<td class="maiuscula" colspan="3">${fn:toLowerCase(inscricaoSelecao.obj.pessoaInscricao.nome)}</td>
			</tr>

			
				<c:choose>
					<c:when test="${not empty inscricaoSelecao.obj.processoSeletivo.curso}">
					<tr>	
						<th>Curso:</th>
						<td class="maiuscula" colspan="3">
							${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.curso.nomeCompleto)}
						</td>
					</tr>
					<tr>	
						<th width="140px">Nível de Ensino:</th>
						<td class="maiuscula" colspan="3">
							${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.curso.nivelDescricao)}
						</td>
					</tr>	
					</c:when>
					<c:otherwise>
					<tr>
						<th>Curso:</th>
						<td class="maiuscula">
							${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.matrizCurricular.curso.descricao)}
						</td>
						<th width="60px">Cidade:</th>
						<td class="maiuscula">
							${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.matrizCurricular.curso.municipio.nome)}
						</td>
					</tr>
					<tr>		
						<th>Modalidade:</th>
						<td class="maiuscula">
							<c:choose>
								<c:when test="${not empty inscricaoSelecao.obj.processoSeletivo.matrizCurricular.grauAcademico.descricao}">
									${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.matrizCurricular.grauAcademico.descricao)}
								</c:when>
								<c:otherwise>
									${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.matrizCurricular.grauAcademico.titulo)}
								</c:otherwise>
							</c:choose>		<caption></caption>
						</td>
						<th>Turno:</th>
						<td>
							${inscricaoSelecao.obj.processoSeletivo.matrizCurricular.turno.descricao}
						</td>
					</tr>
					</c:otherwise>
				</c:choose>	
					<tr>			
						<th>Data da Inscrição:</th>
						<td colspan="3">
							<fmt:formatDate value="${inscricaoSelecao.obj.dataInscricao}"/>
						</td>
					</tr>	
					<tr>	
					<c:choose>	
						<c:when test="${not empty inscricaoSelecao.obj.processoSeletivo.matrizCurricular && inscricaoSelecao.obj.processoSeletivo.possuiAgendamento}">
							<th>Data Agendada:</th>
							<td colspan="3"><ufrn:format type="data" valor="${inscricaoSelecao.obj.agenda.dataAgenda}" /></td>
						</c:when>
						<c:otherwise>
							<td></td>
							<td colspan="3"></td>
						</c:otherwise>
					</c:choose>
					</tr>
			</tbody>
		</table>
			
		<%-- USO EXCLUSIVO DA GRADUAÇÃO - TRANSFERÊNCIA VOLUNTÁRIA --%>
		<c:if test="${not empty inscricaoSelecao.obj.processoSeletivo.matrizCurricular}">
			<table class="subTabelaRelatorio">
				<caption>Documentação (Para uso exclusivo do ${ configSistema['siglaUnidadeGestoraGraduacao'] })</caption>
				<tr>
					<td colspan="6">
					<%@include file="/public/processo_seletivo/documentacao.jsp" %>
					</td>
				</tr>
			</table>
		</c:if>
			
		<div class="descricaoOperacao">
			<center>Código Verificador: ${inscricaoSelecao.obj.codigoHash}</center>
		</div>
		
	</h:form>
</f:view>
<script>//print();</script>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>