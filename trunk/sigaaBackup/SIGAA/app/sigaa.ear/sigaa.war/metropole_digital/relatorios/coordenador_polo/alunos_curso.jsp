<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>

<h2> LISTAGEM GERAL DOS ALUNOS CADASTRADOS</h2>


	<c:set var="total" value="0"/>
	<c:set var="idPoloAntigo" value="0"/>
	<c:set var="idTurmaAntiga" value="0"/>
	<c:set var="alterouPolo" value="0"/>
	
	<c:forEach items="#{relatoriosCoordenadorPoloIMD.listaGeralDiscentes}" var="linha"  varStatus="status">
	
		<c:if test="${(linha.turmaEntradaTecnico.opcaoPoloGrupo.polo.id) != (idPoloAntigo)}">
			<c:if test="${(total) > 0}">
				</table>
				<br />
				<c:set var="alterouPolo"  value="1"/>
			</c:if>
				
			<h2><u>PÓLO: ${linha.turmaEntradaTecnico.opcaoPoloGrupo.polo.cidade.nome} - ${linha.turmaEntradaTecnico.opcaoPoloGrupo.polo.cidade.unidadeFederativa.sigla}</u></h2>
				
		</c:if>
		
		
		<c:if test="${(linha.turmaEntradaTecnico.id) != (idTurmaAntiga)}">
			<c:if test="${(total > 0) && (alterouPolo == 0)}">
				</table>
				<br />
			</c:if>
			
			<h2>TURMA: ${linha.turmaEntradaTecnico.especializacao.descricao}</h2>
			
			<table cellspacing="1" width="100%" style="font-size: 10px;">
				<tr class="header"> 
					<td style="text-align: center; width: 10%;"><b>Matrícula</b></td> 	
					<td  style="text-align: left; width: 60%;"><b>Nome</b></td>
					<td style="text-align: center; width: 15%;"><b>CPF</b></td>
					<td style="text-align: center; width: 15%;"><b>Situação</b></td>		
				</tr>
		</c:if>
		
		<tr class="componentes" style="background-color: ${status.count % 2 == 0 ? '#F1F1F1' : '#FFFFFF'}">
			<td style="text-align: center;"> ${linha.discente.matricula }</td>
			<td style="text-align: left;"> ${linha.discente.pessoa.nome}</td>
			<td style="text-align: center;" width="15%"><ufrn:format type="cpf_cnpj" valor="${linha.discente.pessoa.cpf_cnpj}" /> </td>
			
			<c:if test="${(linha.discente.status) == 1}">
				<td style="text-align: center;">ATIVO</td>
			</c:if>
			<c:if test="${(linha.discente.status) == 2}">
				<td style="text-align: center;">CADASTRADO</td>
			</c:if>
			<c:if test="${(linha.discente.status) == 3}">
				<td style="text-align: center;">CONCLUIDO</td>
			</c:if>
			<c:if test="${(linha.discente.status) == 4}">
				<td style="text-align: center;">AFASTADO</td>
			</c:if>
			<c:if test="${(linha.discente.status) == 5}">
				<td style="text-align: center;">TRANCADO</td>
			</c:if>
			<c:if test="${(linha.discente.status) == 6}">
				<td style="text-align: center;">CANCELADO</td>
			</c:if>
			<c:if test="${(linha.discente.status) == 7}">
				<td style="text-align: center;">JUBILADO</td>
			</c:if>
			<c:if test="${(linha.discente.status) == 8}">
				<td style="text-align: center;">FORMANDO</td>
			</c:if>
			<c:if test="${(linha.discente.status) == 9}">
				<td style="text-align: center;">GRADUANDO</td>
			</c:if>
			<c:if test="${(linha.discente.status) == 10}">
				<td style="text-align: center;">EXCLUIDO</td>
			</c:if>
			<c:if test="${(linha.discente.status) == 11}">
				<td style="text-align: center;">EM_HOMOLOGACAO</td>
			</c:if>
			
			
			<c:set var="total"  value="${total + 1}"/>
			<c:set var="idPoloAntigo"  value="${linha.turmaEntradaTecnico.opcaoPoloGrupo.polo.id}"/>
			<c:set var="idTurmaAntiga"  value="${linha.turmaEntradaTecnico.id}"/>
			<c:set var="alterouPolo"  value="0"/>
		</tr>
			
		
	</c:forEach>
	
	</table>
	<br />

	<table width="100%">
		<tr align="center">
			<td colspan="3"><b>Total: ${total} registros.</b></td>
		</tr>
	</table>
	
</table>
<br />

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>