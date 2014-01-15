<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<style>
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.inscritos td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	table thead tr th.inscricao,th#dataAgendada,th#dataNascimento{text-align:center;}
	table thead tr th.paramDtAgendada{text-align:center;}
	tr td.orgao{width: 50px}
</style>

<f:view>
<a4j:keepAlive beanName="relatorioInscricaoSelecao"></a4j:keepAlive>
	<h2>
		Relatório de Inscritos
	</h2>
	
	<table>
		<tr>
			<th align="left"><b>Período das Inscrições:</b></th>
			<td align="left">
				<ufrn:format type="data" 
					valor="${relatorioInscricaoSelecao.obj.processoSeletivo.inicioInscricoes}"/>
				 até
				<ufrn:format type="data" 
					valor="${relatorioInscricaoSelecao.obj.processoSeletivo.fimInscricoes}"/>
			</td>
		</tr>
		<c:if test="${not empty relatorioInscricaoSelecao.obj.dataAgendada}">
			<tr>	
				<td align="left">
					<b>Data de Agendada:</b>				
				</td>
				<td align="left">
				<ufrn:format type="data" 
					valor="${relatorioInscricaoSelecao.obj.dataAgendada}"/>
				</td>
			</tr>
		</c:if>
	</table>
	<hr>
	
	<table class="tabelaRelatorioBorda" width="100%">
	<thead>
		<tr>
			<th align="left">Insc.</th>
			<th align="left" class="cpf">CPF</th>
			<th>Nome</th>
			
			<!-- SOMENTE QUANDO FOR RELATÓRIO DE INSCRITOS DEFERIDOS  -->
			<c:if test="${relatorioInscricaoSelecao.relInscritosDeferidos}">
				<th align="center" id="dataNascimento"> Data Nascimento</th>
				<th align="left" class="orgao">RG/Orgão Expedidor</th>
				<th align="left" >Língua</th>
			</c:if>
			
			<!-- SOMENTE QUANDO FOR RELATÓRIO DE AGENDAMENTO  -->
			<c:if test="${relatorioInscricaoSelecao.relInscritosAgendados}">	
				<th>Matriz Curricular</th>
				<th  align="center" width="105px" id="dataAgendada">Cidade</th>
			</c:if>
		</tr>
	</thead>
	
	<tbody>
	<c:forEach items="${relatorioInscricaoSelecao.inscritos}" var="linha">
		<tr class="inscritos">
			<td> ${linha.numeroInscricao}</td>
			<td align="left">
				<ufrn:format type="cpf_cnpj"	valor="${linha.pessoaInscricao.cpf}" />
			</td>
			<td> ${linha.pessoaInscricao.nome}</td>
			
			<!-- SOMENTE QUANDO FOR RELATÓRIO DE INSCRITOS DEFERIDOS  -->
			<c:if test="${relatorioInscricaoSelecao.relInscritosDeferidos}">
				<td align="center">
					<ufrn:format type="data" valor="${linha.pessoaInscricao.dataNascimento}"/>
				</td>
				<td align="left"  class="orgao">
					<ufrn:format type="texto"	valor="${linha.pessoaInscricao.identidade.numero}" />/
					<ufrn:format type="texto"	valor="${linha.pessoaInscricao.identidade.orgaoExpedicao}" />
				</td>
				<td align="left"> ${linha.alternativa.alternativa}</td>
			</c:if>
			
			<!-- SOMENTE QUANDO FOR RELATÓRIO DE AGENDAMENTO  -->
			<c:if test="${relatorioInscricaoSelecao.relInscritosAgendados}">
				<td class="curso"> 
					<c:choose>
						<c:when test="${empty linha.processoSeletivo.matrizCurricular}">
							${linha.processoSeletivo.curso.descricao}
						</c:when>
						<c:otherwise>
							${linha.processoSeletivo.matrizCurricular.descricao}
						</c:otherwise>
					</c:choose>
				</td>
				<td align="center" > 
					${linha.processoSeletivo.matrizCurricular.curso.municipio.nome}
				</td>
			</c:if>
		</tr>
	</c:forEach>
	</tbody>
	
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
