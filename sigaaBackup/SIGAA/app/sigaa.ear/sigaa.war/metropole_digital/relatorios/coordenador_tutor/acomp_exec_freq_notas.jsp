<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="relatoriosCoordenadorTutoresIMD"/>
<h2> <ufrn:subSistema /> > Execução da Frequência e Notas Semanais</h2>
	
<h:form id="form">
	
	<br />
	<c:if test="${not empty relatoriosCoordenadorTutoresIMD.listaRegistros}">
		
		<div class="infoAltRem">
			<html:img page="/img/email_go.png" style="overflow: visible;"/>: Enviar Mensagem
			<html:img page="/img/check.png" style="overflow: visible;"/>: Preenchido OK
			<html:img page="/img/prova_semana.png" style="overflow: visible;"/>: NÃO preenchido
			<html:img page="/img/consolidacao/situacao_rec.png" style="overflow: visible;"/>: Bloqueado
		</div>
		
		
	
		<table class="listagem" style="width: 100%">
			<thead>
				<tr>
					<th style ="text-align: left;" rowspan="2">Turma</th>
					<th style ="text-align: left;" rowspan="2" ></th>
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
		  				<td style="text-align: left;" nowrap="nowrap" rowspan="4">
							
							<h:commandLink action="#{relatoriosCoordenadorTutoresIMD.enviarMensagem}">
								<h:graphicImage value="/img/email_go.png" style="overflow: visible;" title="Enviar Mensagem" alt="Enviar Mensagem"/>
								<f:param name="id" value="#{item.tutor.id}"/>								
							</h:commandLink>	  					
		  					
	  					</td>	
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
		<div class="infoAltRem">
			FREQ: Frequência &nbsp;&nbsp;&nbsp;
			PP: Participação Presencial &nbsp;&nbsp;&nbsp;
			PV: Participação Virtual
		</div>
		
		<br />
		<p align="center">
			<h:commandLink action="#{ relatoriosCoordenadorTutoresIMD.listagemExecucaoFrequenciaNotasImprimir }" title="Imprimir">
		 		<h:graphicImage value="/img/consolidacao/printer.png"/>
			</h:commandLink>
			<br />
			<h:commandLink action="#{relatoriosCoordenadorTutoresIMD.listagemExecucaoFrequenciaNotasImprimir}" value="Imprimir"/>
		</p>
	</c:if>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>