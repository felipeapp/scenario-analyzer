<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style type="text/css">
	table.tabelaRelatorioBorda tr.biblioteca td{
		background: #EEEEEE;
		font-weight: bold;
		padding-left: 20px;
	}
</style>

<f:view>

	<a4j:keepAlive beanName="interrupcaoBibliotecaMBean"></a4j:keepAlive>

	<h:form>

		<table class="visualizacao" style="margin-bottom: 30px; margin-right: auto;  margin-left: auto; width:100%;">
			<tr>
				<th style="width: 30%;">Biblioteca:</th>
				<td>
					<h:outputText value="#{interrupcaoBibliotecaMBean.descricaoBibliotecaHistorico}"/>
				</td>
			</tr>
			<tr>
				<th>Data Inicial:</th>
				<td><ufrn:format type="data" valor="${interrupcaoBibliotecaMBean.dataInicio}" /></td>
			</tr>
			<tr>
				<th>Data Final:</th>
				<td><ufrn:format type="data" valor="${interrupcaoBibliotecaMBean.dataFim}" /></td>
			</tr>
		</table>

		<table class="tabelaRelatorioBorda" style="width:100%;">
			<caption>Histórico de Interrupções (${fn:length(interrupcaoBibliotecaMBean.interrupcoesCadastradas)})</caption>
			<c:if test="${ empty interrupcaoBibliotecaMBean.interrupcoesCadastradas }">
				<tr>
					<td><p style="text-align:center;color:red;">Não Existem interrupções Cadastradas para nenhuma Biblioteca.</p></td>
				</tr>
			</c:if>

			<c:if test="${ not empty interrupcaoBibliotecaMBean.interrupcoesCadastradas }">
				<thead>
					<tr>
						<th style="text-align:center;width:120px;">Data</th>
						<th>Motivo</th>
						<th>Cadastrada por:</th>
					</tr>
				</thead>
				<tbody>
					<c:set var="idFiltroBibliotecaInterrupcao" scope="request" value="-1" />
					<c:forEach var="dadosInterrupcao" items="#{interrupcaoBibliotecaMBean.interrupcoesCadastradas}" varStatus="status">
						
						<c:if test="${ idFiltroBibliotecaInterrupcao != dadosInterrupcao.idBiblioteca}">
							<c:set var="idFiltroBibliotecaInterrupcao" scope="request" value="${dadosInterrupcao.idBiblioteca}" />
							<tr class="biblioteca">
								<td colspan="3">${dadosInterrupcao.descricaoBiblioteca}</td>
							</tr>
						</c:if>
						
						<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td style="text-align:center;">
								<ufrn:format type="data" valor="${dadosInterrupcao.dataInterrupcao}" />
							</td>
							<td style="width: 50%">${dadosInterrupcao.motivoInterrupcao}</td>
							<td>${dadosInterrupcao.criadoPor}</td>
						</tr>
					</c:forEach>
				</tbody>
			</c:if>
			
		</table>
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>