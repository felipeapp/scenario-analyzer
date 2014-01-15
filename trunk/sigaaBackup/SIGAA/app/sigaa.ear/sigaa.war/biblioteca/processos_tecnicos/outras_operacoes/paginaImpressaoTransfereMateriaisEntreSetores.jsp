<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

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

	<a4j:keepAlive beanName="transferirMateriaisEntreSetoresBibliotecaMBean" />
	
	<table class="tabelaRelatorioBorda" width="100%">

		<caption> Materiais transferidos ( ${transferirMateriaisEntreSetoresBibliotecaMBean.qtdMateriaisTransferidos} )</caption>	
		
		<thead>
			<th style="text-align: left" width="15%">Código de Barras</th>
			<th style="text-align: left" width="55%">Referência</th>
			<th style="text-align: left" width="15%">Situação Anterior</th>
			<th style="text-align: left" width="15%">Nova Situação</th>
		</thead>
		
		<c:forEach items="#{transferirMateriaisEntreSetoresBibliotecaMBean.materiaisImpressao}" var="seq" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
				<c:if test="${seq.material != null}" >   
					<td>${seq.material.codigoBarras}</td>
					<td>${seq.material.informacao}</td>
					<td> ${seq.material.situacao.descricao} </td>
					<td> ${transferirMateriaisEntreSetoresBibliotecaMBean.situacaoSelecionada.descricao} </td>
				</c:if>
				<c:if test="${seq.material == null}">
					<td> <h:outputText value="#{seq.primeiroCodigoBarras}"/> até <h:outputText value="#{seq.ultimoCodigoBarras}"/></td>
					<td>Vários títulos</td>
					<td> ${seq.situacaoAtual} </td>
					<td> ${transferirMateriaisEntreSetoresBibliotecaMBean.situacaoSelecionada.descricao} </td>
				</c:if>
			</tr>
		</c:forEach>
		
	</table>
	
	<div style="margin-top: 15px; margin-bottom: 15px; margin-left: auto; margin-right: auto; text-align: center;">
		<strong>Transferência Realizada por: </strong> ${usuario.nome}
	</div>
	
	<h:form id="formImpressao">
		<div style="text-align: center; width: 100%;">
			<table style="margin-left: auto; margin-right: auto; text-align: center;">
				
				<tr style="height: 30px;">
					<td class="naoImprimir">
						<h:commandLink id="linkImprimirRelatorioTransferencia" value="Gerar em PDF" actionListener="#{transferirMateriaisEntreSetoresBibliotecaMBean.visualizarRelatorio}" />
					</td>
				</tr>
				
				<tr style="height: 30px;">
					<td class="naoImprimir">
						<h:commandLink id="linkVoltarTelaBusca" value="<< Realizar Nova Transferência" action="#{transferirMateriaisEntreSetoresBibliotecaMBean.telaBusca}" />
					</td>
				</tr>
				
			</table>
		</div>
		
	</h:form> 
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>