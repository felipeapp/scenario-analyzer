<%@include file="/public/include/cabecalho.jsp" %>

<style>
	strong {
		font-weight: bold;
	}
</style>

<f:view>
	<a4j:keepAlive beanName="relatorioNovasAquisicoesMBean" />
	
	<h2>
		 Informativo De Novas Aquisições
	</h2>
	
	<h:form>
		<table id="tabelaFiltros" width="90%" style="margin: 0px auto;">
			<tbody>
				<tr>
					<th width="20%" style="font-weight: bold; white-space: nowrap; vertical-align: top;">
						Biblioteca(s):
					</th>
			
					<td>
						<c:forEach items="${relatorioNovasAquisicoesMBean.nomeBibliotecasSelecionadas}" var="nome">
							${nome} <br/>
						</c:forEach>
					</td>
				</tr>
	
				<tr>
					<th width="20%" style="font-weight: bold;">
						Área de Conhecimento:
					</th>
			
					<td>
						${relatorioNovasAquisicoesMBean.nomeAreaCNPQSelecionada}
					</td>
				</tr>
	
				<tr>
					<th width="20%" style="font-weight: bold;">
						Período:
					</th>
					<td>
						<table>
							<tr>
								<td>
									<ufrn:format type="data" valor="${relatorioNovasAquisicoesMBean.inicioPeriodo}"></ufrn:format>
								</td>
								<td>a</td>
								<td>
									<ufrn:format type="data" valor="${relatorioNovasAquisicoesMBean.fimPeriodo}"></ufrn:format>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</tbody>
		</table>
		
		<hr style="width: 90%; margin: 5px auto;" />
		
		<a href="#" name="topo"></a>
		
		<table id="tabelaResultado" class="formulario" width="90%" style="margin-bottom: 20px">
			<caption>Novas Aquisições das Bibliotecas (${fn:length(relatorioNovasAquisicoesMBean.registros)}) </caption>
			<tbody>
				<c:set var="metadeRegistros" scope="page" value="${fn:length(relatorioNovasAquisicoesMBean.registros) % 2 == 0 ? (fn:length(relatorioNovasAquisicoesMBean.registros) / 2) - 1 : fn:length(relatorioNovasAquisicoesMBean.registros) / 2}" />
				<c:forEach var="registro" items="${relatorioNovasAquisicoesMBean.registros}" varStatus="row" step="2">
					<tr>
						<td style="width: 50%; vertical-align: top; background-color: ${(row.index / 2) % 2 == 0 ? '#EBEDEF' : 'White'}">${registro}</td>
						<td style="width: 50%; vertical-align: top; background-color: ${(row.index / 2) % 2 == 0 ? 'White' : '#EBEDEF'}">${relatorioNovasAquisicoesMBean.registros[row.index + 1]}</td>
					</tr>
				</c:forEach>
			</tbody>
	
			<tfoot>
				<tr>
					<td colspan="7">		
						<h:commandLink value="<< Voltar à tela de busca" action="#{relatorioNovasAquisicoesMBean.voltar}" />
					</td>
				</tr>
			</tfoot>
		</table>
		
		<div style="width: 90%; margin: 0px auto; text-align: right;">
			<a href="#topo" style="text-decoration: underline;">Voltar ao topo</a>
		</div>
		
		<%@include file="/public/include/voltar.jsp"%>
	</h:form>
</f:view>

<%@include file="/public/include/rodape.jsp" %>