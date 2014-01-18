<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<f:view>
<a4j:keepAlive beanName="relatoriosCoordenadorTutoresIMD"/>
	
<h:form id="form">
	
	<br />
	<c:if test="${not empty relatoriosCoordenadorTutoresIMD.listaRegistros}">
		
		
	
		<p align="center"><h2 align="center">RELATÓRIO DE ACOMPANHAMENTO DA EXECUÇÃO DA FREQUÊNCIA E NOTAS SEMANAIS</h2></p>
	
		<p align="center">
			<b>
				<html:img page="/img/check.png" style="overflow: visible;"/>: Preenchido OK &nbsp;&nbsp;&nbsp;
				<html:img page="/img/prova_semana.png" style="overflow: visible;"/>: NÃO preenchido &nbsp;&nbsp;&nbsp;
				<html:img page="/img/consolidacao/situacao_rec.png" style="overflow: visible;"/>: Bloqueado
			</b>
		</p>
		<br />	
	
		<table class="listagem" style="width: 100%">
			<thead>
				<tr>
					<th style ="text-align: left;" rowspan="2">Turma</th>
					<th style ="text-align: left;" rowspan="2">Tutor</th>
					
					<th></th>
					<c:forEach var="contador" begin="1" end="${relatoriosCoordenadorTutoresIMD.qtdPeriodosTurmas}" step="1" varStatus ="status">
					        <th style="text-align: center;">P<fmt:formatNumber value="${contador}" pattern="00"/> </th>
					</c:forEach>
					
				</tr>
			</thead>
			
		  	<tbody>
		  	<c:forEach items="#{relatoriosCoordenadorTutoresIMD.listaTutorias}" var="item" varStatus="linha" >
		  	
		  			<c:set var="idTutor"  value="#{item.tutor.id}"/>
		  			
		  			<tr style="background-color: ${linha.count % 2 == 0 ? '#F1F1F1' : '#FFFFFF'}" align="center">
		  				<td style ="text-align: left;" rowspan="4"><h:outputText>${item.turmaEntrada.especializacao.descricao}</h:outputText></td>
		  				<td style ="text-align: left;" rowspan="4"><h:outputText>${item.tutor.pessoa.nome}</h:outputText></td>
		  					
		  				
		  				<tr style="background-color: ${linha.count % 2 == 0 ? '#F1F1F1' : '#FFFFFF'}">
		  					<td style ="text-align: center;"><strong>FREQ</strong></td>
		  					
		  					<c:forEach items="${relatoriosCoordenadorTutoresIMD.listaRegistros}" var="registro" varStatus="row" >
		  						<c:if test="${(item.turmaEntrada.id) == (registro.turmaEntrada.id)}">
		  							<c:if test="${registro.frequenciaExecutada == 1}">
										<td style="text-align: center;" nowrap="nowrap"><h:graphicImage value="/img/check.png" style="overflow: visible;" alt="Preenchido OK" title="Preenchido OK"/></td>
									</c:if>
									<c:if test="${registro.frequenciaExecutada == 0}">
										<td style="text-align: center;" nowrap="nowrap"><h:graphicImage value="/img/prova_semana.png" style="overflow: visible;" alt="NÃO preenchido" title="NÃO preenchido"/></td>
									</c:if>
									<c:if test="${registro.frequenciaExecutada == -1}">
										<td style="text-align: center;" nowrap="nowrap"><h:graphicImage value="/img/consolidacao/situacao_rec.png" style="overflow: visible;" alt="Bloqueado" title="Bloqueado"/></td>
									</c:if>
		  						</c:if>
		  					</c:forEach>
		  					
	  					</tr>
		  					
		  				<tr style="background-color: ${linha.count % 2 == 0 ? '#F1F1F1' : '#FFFFFF'}">
		  					<td style ="text-align: center;"><strong>PP</strong></td>
		  					
		  					<c:forEach items="${relatoriosCoordenadorTutoresIMD.listaRegistros}" var="registro" varStatus="row" >
		  						<c:if test="${(item.turmaEntrada.id) == (registro.turmaEntrada.id)}">
		  							<c:if test="${registro.ppExecutada == 1}">
										<td style="text-align: center;" nowrap="nowrap"><h:graphicImage value="/img/check.png" style="overflow: visible;" alt="Preenchido OK" title="Preenchido OK"/></td>
									</c:if>
									<c:if test="${registro.ppExecutada == 0}">
										<td style="text-align: center;" nowrap="nowrap"><h:graphicImage value="/img/prova_semana.png" style="overflow: visible;" alt="NÃO preenchido" title="NÃO preenchido"/></td>
									</c:if>
									<c:if test="${registro.ppExecutada == -1}">
										<td style="text-align: center;" nowrap="nowrap"><h:graphicImage value="/img/consolidacao/situacao_rec.png" style="overflow: visible;" alt="Bloqueado" title="Bloqueado"/></td>
									</c:if>
		  						</c:if>
		  					</c:forEach>
		  					
	  					</tr>
		  				
		  				<tr style="background-color: ${linha.count % 2 == 0 ? '#F1F1F1' : '#FFFFFF'}">
		  					<td style ="text-align: center;"><strong>PV</strong></td>
		  					
		  					<c:forEach items="${relatoriosCoordenadorTutoresIMD.listaRegistros}" var="registro" varStatus="row" >
		  						<c:if test="${(item.turmaEntrada.id) == (registro.turmaEntrada.id)}">
		  							<c:if test="${registro.pvExecutada == 1}">
										<td style="text-align: center;" nowrap="nowrap"><h:graphicImage value="/img/check.png" style="overflow: visible;" alt="Preenchido OK" title="Preenchido OK"/></td>
									</c:if>
									<c:if test="${registro.pvExecutada == 0}">
										<td style="text-align: center;" nowrap="nowrap"><h:graphicImage value="/img/prova_semana.png" style="overflow: visible;" alt="NÃO preenchido" title="NÃO preenchido"/></td>
									</c:if>
									<c:if test="${registro.pvExecutada == -1}">
										<td style="text-align: center;" nowrap="nowrap"><h:graphicImage value="/img/consolidacao/situacao_rec.png" style="overflow: visible;" alt="Bloqueado" title="Bloqueado"/></td>
									</c:if>
		  						</c:if>
		  					</c:forEach>
		  					
	  					</tr>
	  					
	  					
		  				
		  			</tr>	
		  			
	 			</c:forEach>  
		</table>
		
		<br />
		
		<p align="center">
			<b>FREQ:</b> Frequência &nbsp;&nbsp;&nbsp;
			<b>PP:</b> Participação Presencial &nbsp;&nbsp;&nbsp;
			<b>PV:</b> Participação Virtual
		</p>
		
	</c:if>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>