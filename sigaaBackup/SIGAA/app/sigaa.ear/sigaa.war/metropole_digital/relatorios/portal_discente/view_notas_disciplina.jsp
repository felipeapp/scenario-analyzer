<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<a4j:keepAlive beanName="lancamentoNotasDisciplina"/>
<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<script>
	// Muda o nome do jQuery para J, evitando conflitos com a Prototype.
	var J = jQuery.noConflict();
</script>

<script type="text/javascript" src="/sigaa/javascript/consolidacao/consolidacao_visualizacao.js"></script>

<f:view>
	<h3>${relatoriosDiscentesIMD.turmaDisciplina.nomeResumido}</h3>
	<h:form>
		<table class="tabelaRelatorioBorda" style="width: 100%">
				<caption>Lançamento de Notas Disciplina</caption>
				
				<thead>
					<tr>
			  			<th style="text-align: left;" rowspan="2">Matrícula</th>
			  			<th style="text-align: left;" rowspan="2">Discente</th>
			  			<th style="text-align: center" colspan="3">Participação</th>
			  			<th style="text-align: center" rowspan="2">AE</th>
			  			<th style="text-align: center" rowspan="2">PE</th>
			  			<th style="text-align: center" rowspan="2">Média da Disciplina</th>
			  			<th style="text-align: center" rowspan="2">CHNF</th>
				  	</tr>
				  	
				  	<tr>
		  				<th style="text-align: center" >PP</th>
		  				<th style="text-align: center">PV</th>
		  				<th style="text-align: center">PT</th>
			  		</tr>
				</thead>
				
			  	<tbody>
				  		<tr style="background-color: ${r.index % 2 == 0 ? 'FFFFFF' : 'F1F1F1'}">
				  			<td >${relatoriosDiscentesIMD.notasDisciplina.discente.matricula}</td>
				  			<td>${relatoriosDiscentesIMD.notasDisciplina.discente.pessoa.nome}</td>
				  							  							  							  								  				 
				  			<c:choose>
				  				<c:when test="${not empty relatoriosDiscentesIMD.notasDisciplina.participacaoPresencial}">
				  					<td style="text-align: center"><fmt:formatNumber value="${relatoriosDiscentesIMD.notasDisciplina.participacaoPresencial}"  pattern="0.0"/></td>
				  				</c:when>
				  				<c:otherwise>
				  					<td style="text-align: center"> --</td>				  				
				  				</c:otherwise>				  			
				  			</c:choose>
				  			
				  			
				  			<c:choose>
				  				<c:when test="${not empty relatoriosDiscentesIMD.notasDisciplina.participacaoVirtual}">
				  					<td style="text-align: center"><fmt:formatNumber value="${relatoriosDiscentesIMD.notasDisciplina.participacaoVirtual}"  pattern="0.0"/></td>	
				  				</c:when>
				  				<c:otherwise>
				  					<td style="text-align: center"> --</td>				  				
				  				</c:otherwise>				  			
				  			</c:choose>
				  			
				  			<c:choose>
				  				<c:when test="${not empty relatoriosDiscentesIMD.notasDisciplina.participacaoTotal.nota}">
				  					<td style="text-align: center"> <fmt:formatNumber value="${relatoriosDiscentesIMD.notasDisciplina.participacaoTotal.nota}"  pattern="0.0"/>  </td>	
				  				</c:when>
				  				<c:otherwise>
				  					<td style="text-align: center"> -- </td>				  				
				  				</c:otherwise>				  			
				  			</c:choose>
				  			
				  			<c:choose>
				  				<c:when test="${not empty relatoriosDiscentesIMD.notasDisciplina.atividadeOnline.nota}">
				  					<td style="text-align: center"> <fmt:formatNumber value="${relatoriosDiscentesIMD.notasDisciplina.atividadeOnline.nota}"  pattern="0.0"/></td>	
				  				</c:when>
				  				<c:otherwise>
				  					<td style="text-align: center"> --</td>				  				
				  				</c:otherwise>				  			
				  			</c:choose>
				  			
				  			
				  			<c:choose>
				  				<c:when test="${not empty relatoriosDiscentesIMD.notasDisciplina.provaEscrita.nota}">
				  						<td style="text-align: center"> <fmt:formatNumber value="${relatoriosDiscentesIMD.notasDisciplina.provaEscrita.nota}"  pattern="0.0"/></td>
				  				</c:when>
				  				<c:otherwise>
				  					<td style="text-align: center"> --</td>				  				
				  				</c:otherwise>				  			
				  			</c:choose>
				  			
				  			<c:choose>
				  				<c:when test="${not empty relatoriosDiscentesIMD.notasDisciplina.participacaoTotal.nota && not empty relatoriosDiscentesIMD.notasDisciplina.atividadeOnline.nota && not empty relatoriosDiscentesIMD.notasDisciplina.provaEscrita.nota}">
				  					<td style="text-align: center"> <fmt:formatNumber value="${(relatoriosDiscentesIMD.notasDisciplina.participacaoTotal.nota + relatoriosDiscentesIMD.notasDisciplina.atividadeOnline.nota +(relatoriosDiscentesIMD.notasDisciplina.provaEscrita.nota*2))/4}"  pattern="0.0"/></td>	
				  				</c:when>
				  				<c:otherwise>				  				
				  					<td style="text-align: center"> --</td>
				  				</c:otherwise>				  			
				  			</c:choose>
				  							  			
				  			<td style="text-align: center">${relatoriosDiscentesIMD.notasDisciplina.chnf}</td>
				  		</tr>
<%-- 				  	</c:forEach> --%>
				  	
				  	
				</tbody>
			</table>
			<div align="center">
				<table style="width: 85%">
					<tr align="center">
						<td style="text-align: right;"><strong>PP:</strong> Participação Presencial</td>		
		  				<td style="text-align: center" colspan="2"><strong>PV:</strong> Participação Virtual</td>
		  				<td style="text-align: left;"><strong>PT:</strong> Participação Total</td>		  				
		  			</tr>
		  			<tr>
		  				<td style="text-align: center"><strong>AE:</strong> Atividades Executadas no Moodle</td>
		  				<td style="text-align: center"><strong>PE:</strong> Prova Escrita</td>
		  				<td style="text-align: center"><strong>Média:</strong> Média na Disciplina</td>
		  				<td style="text-align: center"><strong>CHNF:</strong> Carga Horária não Frequentada</td>
		  			</tr>
				</table>				
			</div>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>