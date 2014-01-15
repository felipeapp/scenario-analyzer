<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<style type="text/css">
	table.tabelaRelatorioBorda tr.biblioteca td{
		font-weight: bold;
		padding-left: 20px;
		padding-top: 10px;
		font-variant: small-caps;
		border: none; 
	}
</style>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<table class="tabelaRelatorioBorda" width="100%">


		<c:set var="idFiltroBibliotecaMateriaisBaixados" scope="request" value="-1" />

		<c:forEach var="resultado" items="#{_abstractRelatorioBiblioteca.dadosRelatorio}">
			
			<c:if test="${ idFiltroBibliotecaMateriaisBaixados != resultado.idBiblioteca}">
				<c:set var="idFiltroBibliotecaMateriaisBaixados" scope="request" value="${resultado.idBiblioteca}" />
				<tr class="biblioteca">
					<td colspan="4">${resultado.descricaoBiblioteca}</td>
				</tr>
				
				<tr style="background-color: #DEDFE3">
					<th style="width: 10%">Código de Barras / Tombamento</th>
					<th style="width: 60%">Título / Motivo da Baixa</th>  
					<th style="width: 20%">Baixado Por</th>  
					<th style="width: 10%; text-align: center;">Data/Hora da baixa</th>  
				</tr>
				
			</c:if>
			
			<tr>
				<td style="width: 15%">
					${resultado.codigoBarras}
					<c:choose>
						<c:when  test="${ empty resultado.numeroPatrimonio }">
							(Sem&nbsp;tombamento)
						</c:when>
						<c:when test="${ resultado.numeroPatrimonio != resultado.codigoBarras }">
							/ ${resultado.numeroPatrimonio}
						</c:when>
					</c:choose>
				</td>
				<td style="width: 50%">
					
					<strong>Título: </strong>${resultado.informacoesTitulo} 
					
					<br/>
					
					<c:if test="${not empty resultado.classificacao1 && classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao1}"> 
						<strong>${classificacaoBibliograficaMBean.descricaoClassificacao1}</strong>: <h:outputText value="#{resultado.classificacao1}"/> 
					</c:if>
					<c:if test="${not empty resultado.classificacao2 && classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao2}"> 
						<strong>${classificacaoBibliograficaMBean.descricaoClassificacao2}</strong>: <h:outputText value="#{resultado.classificacao2}"/> 
					</c:if>
					<c:if test="${not empty resultado.classificacao3 && classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao3}"> 
						<strong>${classificacaoBibliograficaMBean.descricaoClassificacao3}</strong>: <h:outputText value="#{resultado.classificacao3}"/> 
					</c:if>
					
					<br/>
					
					<c:if test="${not empty resultado.colecao}"> 
						<strong>Coleção</strong>: <h:outputText value="#{resultado.colecao}"/> 
					</c:if>
					<br/>
					<c:if test="${not empty resultado.tipoDeMaterial}"> 
						<strong>Tipo de Material</strong>: <h:outputText value="#{resultado.tipoDeMaterial}"/>
					</c:if>
					
					<p>
					<br/>
					<span style="font-weight: bold;">Motivo:</span> 
					<c:if test="${not empty resultado.motivoBaixa}"> 
						<span style="font-style: italic;"> ${resultado.motivoBaixa} </span> 
					</c:if>
					<c:if test="${empty resultado.motivoBaixa}"> 
						<span style="font-style: italic;"> Não Informado </span> 
					</c:if>
					</p>
				</td>
				<td>${resultado.nomeUsuarioBaixa}</td>
				<td style="text-align: center;"><ufrn:format type="dataHora" valor="${resultado.dataBaixa}"></ufrn:format> </td>
			</tr>
			
		</c:forEach>
		
	</table>
	
	<table class="tabelaRelatorioBorda" style="width: 100%; margin-top: 10px">
		<tr>
			<th colspan="4" style="text-align: center;">
				Total de Materiais Baixados:  <h:outputText value="#{ fn:length(_abstractRelatorioBiblioteca.dadosRelatorio) }" />
				Total de Títulos: <h:outputText value="#{ _abstractRelatorioBiblioteca.totalTitulos }" />
			</th>
		</tr>
	</table>	
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>