<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
    <a4j:keepAlive beanName="relatoriosPlanejamento"/>
    
<style>
	.siglas { 
		border:1px black solid;
		text-indent: 5px;  
		font-weight: bold;
		background: #ccc;		
	}
	
	.modalidade { 
		border:1px black solid;
		text-indent: 5px;  
		font-weight: bold;
		background: #dedfe3;	
	}	
	
	.aluno a {
	  text-decoration: underline;
	  color: #000000;
	  font-size: 11px;
	  font-weight: normal;
	}

    .linhaPar td { background-color: #f0f0F0; text-align: left;}
    .linhaImpar td { background-color: #fff; text-align: left;}	
	
</style>
    
	<h2>${relatoriosPlanejamento.tituloRelatorio}</h2>
	
	<div id="parametrosRelatorio">
	<table>
		<tr>
			<th>Unidade:</th>
			<c:if test="${empty relatoriosPlanejamento.unidade.nome}">
				<td>TODOS</td>			
			</c:if>
			<c:if test="${!empty relatoriosPlanejamento.unidade.nome}">
				<td><h:outputText value="#{relatoriosPlanejamento.unidade.nome}"/></td>
			</c:if>			
		</tr>
		<tr>
			<th>Ano:</th>
			<td><h:outputText value="#{relatoriosPlanejamento.ano}"/></td>
		</tr>
	</table>
	</div>
	<br/>
	
	<table class="tabelaRelatorioBorda" width="100%">
		<thead>
		    <tr>
		    	<th>Matrícula</th>
		    	<th width="300">Nome</th>
		    	<th style="text-align: center;">Sexo</th>
		    	<th>Status</th>
		    	<th width="60" style="text-align: center;">Semestre Saída</th>
		    	<th width="80" style="text-align: center;">Data Nascimento</th>
		    </tr>
		</thead>
		
		<c:set var="sigla" value=""/>
		<c:set var="idCurso" value="0"/>
		<c:set var="nomeCurso" value="0"/>
		
		<c:set var="totalSigla" value="0"/>
		<c:set var="totalCurso" value="0"/>
		<c:set var="totalGeral" value="0"/>
		
		<c:set var="modalidade" value=""/>
		<c:set var="totalModalidade" value="0"/>
		
		<c:set var="imprimiuTotal" value="false"/>
		<h:form>
	    <tbody>
		 	<c:forEach items="#{relatoriosPlanejamento.lista}"  var="linha" varStatus="loop">
		 		<c:if test="${loop.first}">
					<c:set var="modalidade" value="${linha.me_modalidade}"/> 		
		 		</c:if>
				 	
		 		<c:if test="${sigla != linha.sigla}">
		 			<c:if test="${!loop.first}">
			 			<c:if test="${idCurso != linha.id_matriz_curricular}">
							<tr>
								<th class="modalidade" colspan="6" style="text-align: center;"><b>TOTAL ${nomeCurso} (${totalCurso})</b></th>
							</tr>
							<c:set var="totalCurso" value="0"/>			
							<c:set var="imprimiuTotal" value="true"/>							
				 		</c:if>			 		
						<tr>
							<th class="siglas" colspan="6" style="text-align: center;"><b>TOTAL ${sigla} (${totalSigla})</b></th>
						</tr>
						<tr><td colspan="6"><br/></td></tr>
			 			<c:if test="${modalidade != linha.me_modalidade}">
							<tr>
								<th class="modalidade" colspan="6" style="text-align: center;"><b>TOTAL ${modalidade} (${totalModalidade})</b></th>
							</tr>
							<tr><td  colspan="6"><br/></td></tr>	 			
			 				<c:set var="totalModalidade" value="0"/>
			 				<c:set var="modalidade" value="${linha.me_modalidade}"/> 
			 			</c:if>							 									
						<c:set var="totalSigla" value="0"/>
			 		</c:if>		 		
					<tr>
						<th class="siglas" colspan="6" style="text-align: center;"><b>${linha.sigla}</b></th>
					</tr>			 		    					
		 			<c:set var="sigla" value="${linha.sigla}"/>
		 		</c:if>
	 			<c:set var="totalModalidade" value="${totalModalidade + 1}"/>		 		
	 			<c:if test="${idCurso != linha.id_matriz_curricular}">
		 			<c:if test="${!loop.first and !imprimiuTotal}">
						<tr>
							<th class="modalidade" colspan="6" style="text-align: center;"><b>TOTAL ${nomeCurso} (${totalCurso})</b></th>
						</tr>
						<tr><td  colspan="6"><br/></td></tr>
						<c:set var="totalCurso" value="0"/>			 			
			 		</c:if>	 			
					<tr>
						<th class="modalidade" colspan="6" style="text-align: center;"><b>${linha.nome} - ${linha.modalidade} - ${linha.turno} ( ${linha.me_modalidade} )</b></th>
					</tr>	
					<c:set var="imprimiuTotal" value="false"/>		
					<c:set var="idCurso" value="${linha.id_matriz_curricular}"/>
					<c:set var="nomeCurso" value="${linha.nome} - ${linha.modalidade} - ${linha.turno}"/> 	 		    
	 			</c:if>		
		 		<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${linha.matricula}</td>		 		
					<td class="aluno">
					    <h:commandLink action="#{relatoriosPlanejamento.gerarHistorico}" title="Clique para Visualizar o Histórico">
					    	<h:outputText value="#{linha.discente_nome}"/>
						    <h:outputText rendered="#{not empty linha.municipio_polo}" value=" - PÓLO #{linha.municipio_polo}"/>
					    	<f:param name="id" value="#{linha.id_discente}"/>
					    </h:commandLink>									
					</td>
					<td style="text-align: center;">${linha.sexo}</td>
					<td>${linha.status}</td>
					<td style="text-align: center;">${linha.semestre_saida}</td>
					<td style="text-align: center;">
						<h:outputText value="#{linha.data_nascimento}">
							<f:convertDateTime pattern="dd/MM/yyyy"  />
						</h:outputText> 					
					</td>
				</tr>		
				<c:set var="totalSigla" value="${totalSigla + 1}"/>
				<c:set var="totalCurso" value="${totalCurso + 1}"/>		
				<c:set var="totalGeral" value="${totalGeral + 1}"/>
		 	</c:forEach>
			<tr>
				<th class="modalidade" colspan="6" style="text-align: center;"><b>TOTAL ${nomeCurso} (${totalCurso})</b></th>
			</tr>				
			<tr>
				<th class="siglas" colspan="6" style="text-align: center;"><b>TOTAL ${sigla} (${totalSigla})</b></th>
			</tr>	
			<tr><td  colspan="6"><br/></td></tr>			
			<tr>
				<th class="modalidade" colspan="6" style="text-align: center;"><b>TOTAL ${modalidade} (${totalModalidade})</b></th>
			</tr>
			<tr><td  colspan="6"><br/></td></tr>
			<tr>
				<th class="siglas" colspan="6" style="text-align: center;"><b>TOTAL GERAL (${totalGeral})</b></th>
			</tr>
		 </tbody>		
	</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>