<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/tags/a4j" prefix="a4j"%>

<style type="text/css">
	#parametrosRelatorio table tr th {
		white-space: nowrap;
	}
	table.tabelaRelatorioBorda th.rightAlign {
		text-align: right;
	}
</style>

<f:view>

     <a4j:keepAlive  beanName="_abstractRelatorioBiblioteca" />

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>

	<c:forEach items="${ _abstractRelatorioBiblioteca.resultadoMap }" var="agrupUsuario" varStatus="i">
	
		<c:set var="totalGeralExemplares" value="0" scope="request" />
		<c:set var="totalGeralFasciculos" value="0" scope="request" />
		
		<hr style="border: solid 1px black; margin-bottom: 0px;" />
		
		<div style="text-align: center; font-weight: bold; background-color: #828282; color: White; padding: 10px 0px;">${ agrupUsuario.key }</div>
		
		<c:if test="${ _abstractRelatorioBiblioteca.tipoDeAcervo == _abstractRelatorioBiblioteca.tipoAcervoTodos ||
					   _abstractRelatorioBiblioteca.tipoDeAcervo == _abstractRelatorioBiblioteca.tipoAcervoTitulos }">
			<%-- quantidade real de Títulos no acervo com a classificação escolhida --%>
			<c:set var="totalTitulos" value="0" scope="request"/>		   
			
			<c:if test="${ not empty agrupUsuario.value[0] }">
				<table class="tabelaRelatorioBorda" style="width: 100%; margin-bottom: 20px;">
					<caption style="font-variant: small-caps;">Quantidade de Títulos Catalogados por Classificação</caption>
					<tr>
						<c:forEach items="${ agrupUsuario.value[0] }" var="linha">
							<th style="width: 8%;text-align: center;">${linha.key}</th>
						</c:forEach>
						<th style="width: 10;text-align: right;">Total</th>
					</tr>
					
					<tr style="background-color: #DEDFE3;">
						<c:forEach items="${ agrupUsuario.value[0] }" var="linha">
							<td style="text-align: right;">${linha.value[0]}</td>
							<c:set var="totalTitulos" value="${totalTitulos + linha.value[0] }" scope="request"/>
						</c:forEach>
						<td style="font-weight: bold;text-align: right;">${totalTitulos}</td>
					</tr>
				</table>
			</c:if>
			
			<c:if test="${ empty agrupUsuario.value[0] }">
				<div style="text-align: center; color: Red; width: 100%; margin-bottom: 20px;">Não foram encontrados títulos deste usuário com as características escolhidas.</div>
			</c:if>
		</c:if>



		<c:if test="${ _abstractRelatorioBiblioteca.tipoDeAcervo == _abstractRelatorioBiblioteca.tipoAcervoTodos ||
					   _abstractRelatorioBiblioteca.tipoDeAcervo == _abstractRelatorioBiblioteca.tipoAcervoMateriais }">
			<table class="tabelaRelatorioBorda" style="width: 100%;">
				<caption style="font-variant: small-caps;"> Quantidade de Materiais Incluídos por Classificação</caption>
			
				<tr><td>
				<c:forEach items="${ agrupUsuario.value[1] }" var="agrup1">
					<table class="tabelaRelatorioBorda" style="margin-bottom: 15px; width: 100%;">
						<caption>Classificação ${_abstractRelatorioBiblioteca.descricaoClassificacaoEscolhida} : ${ agrup1.key }</caption>
						
						<thead>
							<tr style="background-color: #C2C2C2;">
								<th>${"Coleção"}</th>
								<th class="rightAlign" style="width: 20%;">Exemplares</th>
								<th class="rightAlign" style="width: 20%;">Fascículos</th>
								
								<c:set var="totalExemplares" value="0" scope="request"/>
								<c:set var="totalFasciculos" value="0" scope="request"/>
							</tr>
						</thead>
						
						<tbody>
							<c:forEach items="${ agrup1.value }" var="linha" >
								<tr>
									<th>${ linha.key }</th>
									<td class="rightAlign">${ linha.value[0] }</td>
									<td class="rightAlign">${ linha.value[1] }</td>
									
									<c:set var="totalExemplares" value="${totalExemplares + linha.value[0]}" scope="request"/>
									<c:set var="totalFasciculos" value="${totalFasciculos + linha.value[1]}" scope="request"/>
								</tr>
							</c:forEach>
						</tbody>
			
						<tfoot>
							<tr style="background-color: #DEDFE3;">
								<td>Total</td> 
								
								<td class="rightAlign" style="width: 20%;">${ totalExemplares }</td>
								<td class="rightAlign" style="width: 20%;">${ totalFasciculos }</td>
								
								<c:set var="totalGeralExemplares" value="${totalGeralExemplares + totalExemplares}" scope="request"/>
								<c:set var="totalGeralFasciculos" value="${totalGeralFasciculos + totalFasciculos}" scope="request"/>
							</tr>
						</tfoot>
			
					</table>
					
				</c:forEach>
				</td></tr>
			</table>
			
			<c:if test="${ not empty agrupUsuario.value[1] }">
				<table class="tabelaRelatorioBorda" style="width: 100%;">
					<tfoot>
							<tr style="background-color: #DEDFE3;">
								<td>Total Geral</td>
								<td class="rightAlign" style="width: 20%;">${totalGeralExemplares}</td>
								<td class="rightAlign" style="width: 20%;">${totalGeralFasciculos}</td>
							</tr>
					</tfoot>
				</table>
			</c:if>
			
			<c:if test="${ empty agrupUsuario.value[1] }">
				<div style="text-align: center; color: Red">Não foram encontrados materiais deste usuário com as características escolhidas.</div>
			</c:if>
		</c:if>
		
		<c:if test="${ not i.last }">
			<br />
			<br />
			<hr style="border: solid 1px black; margin-bottom: 0px;" />
			<br />
			<br />
		</c:if>
			
	
	</c:forEach>	
	
	<c:if test="${ _abstractRelatorioBiblioteca.tipoDeAcervo == _abstractRelatorioBiblioteca.tipoAcervoTodos ||
				   _abstractRelatorioBiblioteca.tipoDeAcervo == _abstractRelatorioBiblioteca.tipoAcervoMateriais }">
		<c:if test="${ not empty _abstractRelatorioBiblioteca.resultadoMateriaisMap }">
			<p style="margin-top: 15px; margin-bottom: 15px;">
			&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp <strong>Observação:</strong> </>A quantidade de materiais apresentada não considera os materiais removidos ou baixados do acervo.
			</p>
		</c:if>
	</c:if>

	<%--<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"--%>
	
	<%@page import="br.ufrn.arq.util.AmbienteUtils"%>
	</div> <%-- Fim do div relatorio  --%>
		<div class="clear"> </div>
		<br/>
		<div id="relatorio-rodape">
			<p>
				<table width="100%">
					<tr>
						<h:form>
							<td class="voltar" align="left"><h:commandLink id="voltarDocumentoQuitacao" value="Voltar" action="#{_abstractRelatorioBiblioteca.telaPadraoFiltrosRelatoriosBiblioteca}"></h:commandLink></td>
						</h:form>
						<td width="70%"  align="center">${ configSistema['siglaSigaa']} | Copyright &copy; <%= br.ufrn.arq.util.UFRNUtils.getCopyright(2006) %> - ${configSistema['nomeResponsavelInformatica']} - ${configSistema['siglaInstituicao']} - ${ configSistema['telefoneHelpDesk'] } - <%= AmbienteUtils.getNomeServidorComInstancia() %></td>
						<td class="naoImprimir" align="right">
							<a onclick="javascript:window.print();" href="#">Imprimir</a>
						</td>
						<td class="naoImprimir" align="right">
							<a onclick="javascript:window.print();" href="#">							
								<img alt="Imprimir" title="Imprimir" src="/shared/javascript/ext-1.1/docs/resources/print.gif"/>
							</a>
						</td>
					</tr>
				</table>
			</p>
		</div>
	</div>  <%-- Fim do div 'container' --%>

</f:view>