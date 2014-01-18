<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<f:view>
	<h:form styleClass="formulario">
		<c:if test="${(empty lancamentoNotasSemanais.listaDiscentesTurma) && (empty lancamentoNotasSemanais.listaPeriodosTurma) && (empty lancamentoNotasSemanais.tabelaAcompanhamento) }">
			<center><i> Nenhum discente e/ou período encontrado.</i></center>
		</c:if>
		
		<c:if test="${(not empty lancamentoNotasSemanais.listaDiscentesTurma) && (not empty lancamentoNotasSemanais.listaPeriodosTurma) && (not empty lancamentoNotasSemanais.tabelaAcompanhamento) }">
			
			<p align="center"><h2 align="center">TURMA: ${lancamentoNotasSemanais.turmaEntradaSelecionada.anoReferencia}.${lancamentoNotasSemanais.turmaEntradaSelecionada.periodoReferencia} - ${lancamentoNotasSemanais.turmaEntradaSelecionada.especializacao.descricao} - ${lancamentoNotasSemanais.turmaEntradaSelecionada.cursoTecnico.nome}
			<br />OPÇÃO PÓLO GRUPO: ${lancamentoNotasSemanais.turmaEntradaSelecionada.opcaoPoloGrupo.descricao}</h2></p>
				
			
			<table class="listagem" style="width: 100%">
				<thead>
					<tr>
						<th style ="text-align: left;" rowspan="2">Matrícula</th>
						<th style ="text-align: left;width: 7%;" rowspan="2">Discente</th>
						<th style ="text-align: left;width: 7%;text-align: center;" rowspan="2">Notas</th>
						<c:forEach items="${lancamentoNotasSemanais.tabelaAcompanhamento[0]}"  var="coluna" varStatus="c">
							<th style="text-align: center;">P<fmt:formatNumber value="${c.count}" pattern="00"/> </th>	
						</c:forEach>
					</tr>
				</thead>
				
			  	<tbody>
			  	<c:forEach items="${lancamentoNotasSemanais.tabelaAcompanhamento}" var="linhas" varStatus="l" >
			  	
			  		<tr style="background-color: ${l.count % 2 == 0 ? '#F1F1F1' : '#FFFFFF'}" align="right">
			  			<td style ="text-align: left;" rowspan="4">${lancamentoNotasSemanais.listaDiscentesTurma[l.count-1].discente.matricula}</td>
			  			<td style ="text-align: left;" rowspan="4">${lancamentoNotasSemanais.listaDiscentesTurma[l.count-1].discente.nome}</td>
			  			<tr style="background-color: ${l.count % 2 == 0 ? '#F1F1F1' : '#FFFFFF'}">
			  			<td style ="text-align: center;"><strong>PP</strong></td>
			  			<c:forEach items="${linhas}" var="coluna" varStatus="c">
			  				<c:choose>
								<c:when test="${empty coluna.participacaoPresencial}">
									<td style="text-align: right;" nowrap="nowrap">--</td>
								</c:when>
								
								<c:otherwise>
									<td style="text-align: right;" nowrap="nowrap"><fmt:formatNumber value="${coluna.participacaoPresencial}" pattern="0.0"/>  </td>
								</c:otherwise>
							</c:choose>
			  			
				  		</c:forEach>
				  		</tr>
				  		<tr style="background-color: ${l.count % 2 == 0 ? '#F1F1F1' : '#FFFFFF'}">
				  		<td style ="text-align: center;"><strong>PV</strong></td>
			  			<c:forEach items="${linhas}" var="coluna" varStatus="c">
			  			
			  				<c:choose>
								<c:when test="${empty coluna.participacaoVirtual}">
									<td style="text-align: right;" nowrap="nowrap">--</td>
								</c:when>
								<c:otherwise>
									<td style="text-align: right;" nowrap="nowrap"><fmt:formatNumber value="${coluna.participacaoVirtual}" pattern="0.0"/> </td>
								</c:otherwise>
							</c:choose>
				  		</c:forEach>
				  		</tr>
				  		<tr style="border-bottom-style: solid;border-bottom: 1;	background-color: ${l.count % 2 == 0 ? '#F1F1F1' : '#FFFFFF'}">
				  		<td style ="text-align: center;"><strong>PT	</strong></td>
			  			<c:forEach items="${linhas}" var="coluna" varStatus="c">
			  				<c:choose>
								<c:when test="${(empty coluna.participacaoPresencial || empty coluna.participacaoVirtual)}">
									<td style="text-align: right;" nowrap="nowrap"><strong>--</strong></td>
								</c:when>
								
								<c:otherwise>
									<td style="text-align: right;" nowrap="nowrap"><strong><fmt:formatNumber value="${((coluna.participacaoPresencial * 3) +(coluna.participacaoVirtual*7))/10}" pattern="0.0"/> </strong> </td>
								</c:otherwise>
							</c:choose>
				  		</c:forEach>
				  		</tr>
			  		</tr>						  			
	  			</c:forEach>  
			</table>
			
			<div align="center">
				<table style="margin-top: 10;  text-align: center; width: 50%" >
					<tr><td><strong>PP</strong> Participação Presencial</td>		
		  			<td><strong>PV</strong> Participação Virtual</td>
		  			<td><strong>PT</strong> Participação Total</td></tr>
				</table>
			</div>
		</c:if>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>