<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	table.tabelaRelatorioBorda{border-bottom: 0px !important;}
	tr.curso td {border-bottom: 1px solid #555;  }
	tr.titulo td {border-bottom: 2.5px solid #555}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #000; }
	tr.total td {padding: 5px 2px 2px; border: 1px solid #000; font-weight: bold;}
	.center{text-align: center !important;}
	.right{text-align: right !important;}
	td.espaco{padding: 12px;border:0 !important;padding: 0px !important;}
	td.espaco table{margin: 0px !important;}
</style>
<f:view>

		<table width="100%" style="font-size: 11px;">
			<caption><td align="center"><b>RELATÓRIO DE TURMAS NÃO CONSOLIDADAS</b></td></b>
		</table>
		<br />
		
		<table width="100%">
	  	  <tr>		
			<td width="16%"><b>Ano.Período:</b></td> 
			<td>
				<h:outputText value="#{relatorioTurma.ano}"/>.<h:outputText value="#{relatorioTurma.periodo}"/>
			</td>
			<tr class="curso"> 
				<td><b>Incluir Ead:</b></td>
				<td>
					<h:outputText value="#{relatorioTurma.ead ? 'Sim':'Não'}"/>
				</td>
			</tr>
		</table>
		<br />
	
	    <c:set var="_departamento" />
	    <c:set var="_totalMatriculados" value="0"/>
	    <c:set var="_total" value="0"/>
		<c:set var="_totalTurmas" value="0"/>
	
		<c:forEach items="#{relatorioTurma.listaTurma}" var="linha" varStatus="indice">
			
				<c:set var="departamentoAtual" value="${linha.departamento}"/>
			 
			  	<c:if test="${_departamento != departamentoAtual}">
					<table width="100%" class="tabelaRelatorioBorda">
						<thead>
							<tr>
								<th colspan="10" style="font-size: 11px;">Departamento: ${linha.departamento}</th>
							</tr>
							<tr>
								<th class="center" width="12%">Código</th>
								<th align="left"> Disciplina</th>
								<th class="right" width="10%">Turma</th>
								<th class="right" width="13%">Matriculados</th>
							</tr>
						</thead>
						<tbody>
					<c:set var="_departamento" value="${departamentoAtual}"/>
				</c:if>
				
					<tr >
						<td class="center"> ${linha.codigo}</td>
						<td align="left"> ${linha.disciplina}</td>
						<td class="right"> ${linha.turma}</td>
						<td class="right"> ${linha.matriculados}</td>
						<c:set var="_totalMatriculados" value="${_totalMatriculados + linha.matriculados}"/>
					</tr>
					
				<c:set var="_totalTurmas" value="${_totalTurmas + 1}"/>
				<c:set var="proximo" value="${relatorioTurma.listaTurma[indice.index+1].departamento}" ></c:set>
										
				<c:if test="${departamentoAtual != proximo}">
						</tbody>
					</table>
					<table width="100%" class="tabelaRelatorioBorda">
						<tfoot>
								<tr>
									<td align="right"  class="total">Total de Turmas:</td>
									<td align="right" width="5%" class="total">${_totalTurmas}</td>
									<td align="right"  class="total">Total de Matriculados:</td>
									<td align="right" width="5%" class="total">${_totalMatriculados}</td>
								</tr>	
						</tfoot>
					</table>
					<br/>
						<c:set var="_total" value="${_total + _totalTurmas}"/>
						<c:set var="_totalMatriculados" value="0"/>
						<c:set var="_totalTurmas" value="0"/>
				</c:if>
				
		</c:forEach>
		
		<table width="100%" class="tabelaRelatorioBorda">
			<tfoot>
				<tr>
					<td class="total" align="right">Total Geral de Turmas:</td>
					<td class="total" width="5%">${_total}</td>
				</tr>	
			</tfoot>
		</table>
		<br />
		
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>